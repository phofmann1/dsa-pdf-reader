package de.pho.dsapdfreader;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.pho.dsapdfreader.markdown.RawExtractor;
import de.pho.dsapdfreader.markdown.RawImageExtractor;
import de.pho.dsapdfreader.markdown.RawPageData;
import de.pho.dsapdfreader.markdown.RawRectExtractor;
import de.pho.dsapdfreader.markdown.TextInterpreter;

/**
 * Pipeline:
 *   PDF → [Stufe 0: Rohdaten]  → export/markdown/raw/  (JSON, einmalig, nur wenn PDF neuer)
 *       → [Stufe 1: Text]      → export/markdown/text/  (Markdown, iterierbar auf Rohdaten)
 *       → [Stufe 2: Bilder]    → export/markdown/images/ (separat, bereits erledigt)
 *
 * Modus wird ueber EXTRACTION_MODE gesteuert.
 */
public class DsaMarkdownReaderMain
{
    private static final Logger LOGGER = LogManager.getLogger();

    // --- Konfiguration ---
    private static final String[] PDF_ROOTS = {
        "D:/Daten/Dropbox/pdf.library/RPG/DSA 5",
        "D:/Daten/OneDrive/pdf.library/RPG/DSA 5 - SL"
    };
    private static final String EXPORT_BASE = "export/markdown";

    // Zu verarbeitende Unterverzeichnisse (null = alles)
    //private static final String[] SCAN_SUBDIRS = null;

    private static final String[] SCAN_SUBDIRS = {
        //"04 - Regionen",
        //"04 - Regionen\\Das Wustenreich (111)",
        "01 - Regeln"
    };

    // Was soll laufen?
    private static final boolean RUN_RAW_EXTRACTION = false;    // Stufe 0: PDF → Rohdaten JSON
    private static final boolean EXTRACT_TEXT = true;            // Stufe 0: Text-Rohdaten extrahieren
    private static final boolean EXTRACT_IMAGES = false;          // Stufe 0: Bild-Rohdaten extrahieren
    private static final boolean EXTRACT_RECTS = true;           // Stufe 0: Vektor-Rechtecke extrahieren
    private static final boolean RUN_HEADINGS_ONLY = true;       // Stufe 1: Headings + Body → _headings.md
    private static final boolean HEADINGS_INCLUDE_BODY = true;   // Stufe 1b: Body-Text mit ausgeben
    private static final boolean INCLUDE_IMAGES = false;          // Stufe 1: Bilder als ![image]() einbinden
    private static final boolean INCLUDE_ORNAMENTS = false;       // Stufe 1: Ornament-Zeichen mit <span> ausgeben
    private static final boolean RUN_VAULT_GENERATION = false;    // Stufe 3: Markdown → Obsidian Vault

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static
    {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static void main(String[] args) throws Exception
    {
        LOGGER.info("-----------------------------------------------------------");
        LOGGER.info("started");
        LOGGER.info("-----------------------------------------------------------");
        Path exportBase = Paths.get(EXPORT_BASE);

        // PDFs sammeln
        List<Path[]> pdfFilesWithRoot = new ArrayList<>();
        for (String rootStr : PDF_ROOTS)
        {
            Path rootPath = Paths.get(rootStr);
            if (SCAN_SUBDIRS != null)
            {
                for (String subdir : SCAN_SUBDIRS)
                {
                    for (Path pdf : findAllPdfs(rootPath.resolve(subdir)))
                    {
                        pdfFilesWithRoot.add(new Path[]{pdf, rootPath});
                    }
                }
            }
            else
            {
                LOGGER.info("Scanne: {}", rootPath);
                for (Path pdf : findAllPdfs(rootPath))
                {
                    pdfFilesWithRoot.add(new Path[]{pdf, rootPath});
                }
            }
        }
        LOGGER.info("{} PDF-Dateien gefunden", pdfFilesWithRoot.size());

        TextInterpreter interpreter = new TextInterpreter();
        interpreter.setIncludeBodyText(HEADINGS_INCLUDE_BODY);
        interpreter.setIncludeImages(INCLUDE_IMAGES);
        interpreter.setIncludeOrnaments(INCLUDE_ORNAMENTS);
        int rawExtracted = 0;
        int textInterpreted = 0;
        int skippedRaw = 0;
        int skippedText = 0;
        int failed = 0;

        for (Path[] entry : pdfFilesWithRoot)
        {
            Path pdfFile = entry[0];
            Path rootPath = entry[1];
            Path relativePath = rootPath.relativize(pdfFile);
            String pdfBaseName = pdfFile.getFileName().toString().replaceAll("(?i)\\.pdf$", "");

            Path rawDir = exportBase.resolve("raw").resolve(relativePath.getParent() != null
                ? relativePath.getParent().resolve(pdfBaseName)
                : Paths.get(pdfBaseName));
            Path textDir = exportBase.resolve("text").resolve(relativePath.getParent() != null
                ? relativePath.getParent().resolve(pdfBaseName)
                : Paths.get(pdfBaseName));

            try
            {
                // --- Stufe 0: Rohdaten ---
                if (RUN_RAW_EXTRACTION)
                {
                    if (isRawUpToDate(rawDir, pdfFile))
                    {
                        skippedRaw++;
                    }
                    else
                    {
                        LOGGER.info("--- RAW: {} ---", relativePath);
                        extractRaw(pdfFile, rawDir);
                        rawExtracted++;
                    }
                }

                // --- Stufe 1: Headings + Body → _headings.md ---
                if (RUN_HEADINGS_ONLY)
                {
                    LOGGER.info("--- HEADINGS: {} ---", relativePath);
                    extractHeadingsOnly(rawDir, textDir, relativePath.toString(), interpreter);
                    textInterpreted++;
                }
            }
            catch (Exception e)
            {
                LOGGER.error("Fehler bei {}: {}", pdfFile.getFileName(), e.getMessage(), e);
                failed++;
            }
        }

        LOGGER.info("=== Stufen 0-1 fertig ===");
        LOGGER.info("  Rohdaten: {} extrahiert, {} übersprungen", rawExtracted, skippedRaw);
        LOGGER.info("  Text: {} interpretiert, {} übersprungen", textInterpreted, skippedText);
        if (failed > 0) LOGGER.warn("  {} fehlgeschlagen", failed);

        // Stufe 3: Vault-Generierung
        if (RUN_VAULT_GENERATION)
        {
            LOGGER.info("=== Stufe 3: Obsidian Vault Generierung ===");
            de.pho.dsapdfreader.obsidian.ObsidianVaultGenerator.main(args);
        }
        LOGGER.info("-----------------------------------------------------------");
        LOGGER.info("started");
        LOGGER.info("-----------------------------------------------------------");
    }

    // --- Stufe 0: Rohdaten-Extraktion ---

    private static void extractRaw(Path pdfFile, Path rawDir) throws IOException
    {
        Files.createDirectories(rawDir);

        PDFParser parser = new PDFParser(new RandomAccessFile(pdfFile.toFile(), "r"));
        parser.parse();
        PDDocument document = new PDDocument(parser.getDocument());

        try
        {
            List<RawPageData> pages = new ArrayList<>();

            // Text-Rohdaten extrahieren
            if (EXTRACT_TEXT)
            {
                RawExtractor extractor = new RawExtractor();
                pages = extractor.extract(document);
                LOGGER.info("  {} Seiten (Text)", pages.size());
            }
            else
            {
                // Leere Seitenstruktur anlegen damit Bilder zugeordnet werden koennen
                for (int i = 1; i <= document.getNumberOfPages(); i++)
                {
                    PDPage pdPage = document.getPage(i - 1);
                    pages.add(new RawPageData(i,
                        pdPage.getMediaBox().getWidth(),
                        pdPage.getMediaBox().getHeight(),
                        new ArrayList<>()));
                }
                LOGGER.info("  {} Seiten (nur Struktur, kein Text)", pages.size());
            }

            // Bild-Rohdaten extrahieren
            if (EXTRACT_IMAGES)
            {
                String pdfBaseName = pdfFile.getFileName().toString().replaceAll("(?i)\\.pdf$", "");
                Path imageDir = rawDir.resolve("images");
                RawImageExtractor imageExtractor = new RawImageExtractor(imageDir, pdfBaseName);
                java.util.Map<Integer, List<RawPageData.RawImage>> imageMap = imageExtractor.extract(document);

                int totalImages = 0;
                for (RawPageData page : pages)
                {
                    List<RawPageData.RawImage> imgs = imageMap.get(page.pageNumber);
                    page.images = imgs != null ? imgs : new ArrayList<>();
                    totalImages += page.images.size();
                }
                LOGGER.info("  {} Bilder extrahiert", totalImages);
            }

            // Vektor-Rechtecke extrahieren
            if (EXTRACT_RECTS)
            {
                RawRectExtractor rectExtractor = new RawRectExtractor(document.getPage(0));
                java.util.Map<Integer, List<RawPageData.RawRect>> rectMap = rectExtractor.extract(document);

                int totalRects = 0;
                for (RawPageData page : pages)
                {
                    List<RawPageData.RawRect> rcs = rectMap.get(page.pageNumber);
                    page.rects = rcs != null ? rcs : new ArrayList<>();
                    totalRects += page.rects.size();
                }
                LOGGER.info("  {} Rechtecke extrahiert", totalRects);
            }

            // Jede Seite als eigene JSON-Datei
            for (RawPageData page : pages)
            {
                Path pageFile = rawDir.resolve(String.format("page_%03d.json", page.pageNumber));
                MAPPER.writeValue(pageFile.toFile(), page);
            }

            // Timestamp-Marker
            Path markerFile = rawDir.resolve("_extracted.marker");
            Files.writeString(markerFile,
                "source=" + pdfFile.toAbsolutePath() + "\n" +
                    "timestamp=" + Files.getLastModifiedTime(pdfFile) + "\n" +
                    "pages=" + pages.size() + "\n",
                StandardCharsets.UTF_8);
        }
        finally
        {
            document.close();
            parser.getDocument().close();
        }
    }

    // --- Stufe 1: Headings + Body ---

    private static void extractHeadingsOnly(Path rawDir, Path textDir, String sourcePath,
                                             TextInterpreter interpreter) throws IOException
    {
        Files.createDirectories(textDir);

        List<Path> rawFiles;
        try (Stream<Path> walk = Files.list(rawDir))
        {
            rawFiles = walk
                .filter(p -> p.getFileName().toString().matches("page_\\d+\\.json"))
                .sorted()
                .toList();
        }

        StringBuilder headings = new StringBuilder();
        headings.append("# ").append(sourcePath).append("\n\n");

        boolean previousPageWasTable = false;

        for (Path rawFile : rawFiles)
        {
            RawPageData page = MAPPER.readValue(rawFile.toFile(), RawPageData.class);
            String pageHeadings = interpreter.interpretPageTree(page);

            if (pageHeadings.isBlank()) continue;

            boolean isTablePage = pageHeadings.contains("|---|---|");

            if (isTablePage && previousPageWasTable)
            {
                // Continuation of a table page - strip FIRST table header only
                // so the main table continues seamlessly, but box sub-tables keep theirs
                int headerIdx = pageHeadings.indexOf("| | |\n|---|---|\n");
                if (headerIdx >= 0)
                {
                    pageHeadings = pageHeadings.substring(0, headerIdx)
                        + pageHeadings.substring(headerIdx + "| | |\n|---|---|\n".length());
                }
                // Remove trailing blank lines from previous page to avoid table break
                while (headings.length() > 0 && headings.charAt(headings.length() - 1) == '\n'
                    && headings.length() > 1 && headings.charAt(headings.length() - 2) == '\n')
                {
                    headings.setLength(headings.length() - 1);
                }
            }
            else
            {
                headings.append("<!-- Seite ").append(page.pageNumber).append(" -->\n");
            }

            headings.append(pageHeadings);
            previousPageWasTable = isTablePage;
        }

        Path headingsFile = textDir.resolve("_headings.md");
        Files.writeString(headingsFile, headings.toString(), StandardCharsets.UTF_8);

        LOGGER.info("  Überschriften -> {}", headingsFile);
    }

    // --- Caching/Timestamps ---

    /**
     * Prueft ob die Rohdaten aktuell sind (neuer als das PDF).
     */
    private static boolean isRawUpToDate(Path rawDir, Path pdfFile) throws IOException
    {
        Path markerFile = rawDir.resolve("_extracted.marker");
        if (!Files.exists(markerFile)) return false;

        String marker = Files.readString(markerFile, StandardCharsets.UTF_8);
        FileTime pdfTime = Files.getLastModifiedTime(pdfFile);
        return marker.contains("timestamp=" + pdfTime);
    }

    // --- Hilfsmethoden ---

    private static List<Path> findAllPdfs(Path directory) throws IOException
    {
        List<Path> result = new ArrayList<>();
        if (!Files.exists(directory))
        {
            LOGGER.warn("Verzeichnis existiert nicht: {}", directory);
            return result;
        }
        try (Stream<Path> walk = Files.walk(directory))
        {
            walk.filter(Files::isRegularFile)
                .filter(p -> p.toString().toLowerCase().endsWith(".pdf"))
                .sorted()
                .forEach(result::add);
        }
        return result;
    }
}
