package de.pho.dsapdfreader.obsidian;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.markdown.RawPageData;

/**
 * Stufe 3 der Pipeline: Erzeugt einen Obsidian-Vault aus _headings.md.
 *
 * Struktur:
 *   - Pro Publikation ein Ordner unter VAULT_BASE/ (bereinigter Buchtitel)
 *   - FolderNote pro Buch enthält nur das Titelbild
 *   - Bilder unter _Quellen/Bilder/{Buchtitel}/ — nur Originale, keine Duplikate
 *   - Originale Bilder werden in den Artikeln eingebettet (nach Seite)
 *   - Headings hierarchisch als Ordner/Notizen
 */
public class ObsidianVaultGenerator
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String TEXT_BASE = "export/markdown/text";
    private static final String IMAGE_BASE = "export/markdown/images";
    private static final String RAW_BASE = "export/markdown/raw";
    private static final String VAULT_BASE = "D:/Daten/Obsidian/Aventurien/Regionen";
    private static final String IMAGE_VAULT_FOLDER = "_Quellen/Bilder";

    private static final String[] SCAN_SUBDIRS = {
        "04 - Regionen"
    };

    private static final String[] SKIP_HEADINGS = {
        "IMPRESSUM", "INHALTSVERZEICHNIS", "INDEX", "MYSTERIA & ARCANA"
    };

    public static void main(String[] args) throws Exception
    {
        Path textBase = Paths.get(TEXT_BASE);

        for (String subdir : SCAN_SUBDIRS)
        {
            Path scanDir = textBase.resolve(subdir);
            if (!Files.exists(scanDir))
            {
                LOGGER.warn("Verzeichnis existiert nicht: {}", scanDir);
                continue;
            }

            List<Path> bookDirs = new ArrayList<>();
            try (Stream<Path> walk = Files.walk(scanDir, 3))
            {
                walk.filter(p -> p.getFileName().toString().equals("_headings.md"))
                    .map(Path::getParent)
                    .forEach(bookDirs::add);
            }

            for (Path bookDir : bookDirs)
            {
                // Bereinigten Buchtitel aus dem Verzeichnisnamen extrahieren
                String cleanBookName = extractCleanBookName(bookDir);

                LOGGER.info("=== Vault: {} ===", cleanBookName);

                Path headingsFile = bookDir.resolve("_headings.md");
                Path vaultDir = Paths.get(VAULT_BASE).resolve(sanitizeFilename(cleanBookName));

                // Bilder sammeln (Original + Duplikat-Zuordnung)
                ImageIndex imageIndex = buildImageIndex(bookDir, cleanBookName);

                processBook(headingsFile, vaultDir, cleanBookName, imageIndex);
            }
        }

        LOGGER.info("=== Vault-Generierung abgeschlossen ===");
    }

    // =====================================================================
    // Buchtitel
    // =====================================================================

    /**
     * Extrahiert den bereinigten Buchtitel aus der Verzeichnisstruktur.
     * "Die Flusslande (180)/Die Flusslande - 04 - Regionen" → "Die Flusslande"
     */
    private static String extractCleanBookName(Path bookContentDir)
    {
        // bookContentDir = .../Die Flusslande (180)/Die Flusslande - 04 - Regionen
        // Parent = .../Die Flusslande (180)
        Path bookIdDir = bookContentDir.getParent();
        if (bookIdDir != null)
        {
            String dirName = bookIdDir.getFileName().toString();
            // "(123)" am Ende entfernen
            return dirName.replaceAll("\\s*\\(\\d+\\)$", "").trim();
        }
        return bookContentDir.getFileName().toString();
    }

    // =====================================================================
    // Bild-Index
    // =====================================================================

    /**
     * Baut einen Index aller Bilder pro Seite auf.
     * Liest die Raw-JSON-Dateien um Duplikate zu erkennen.
     * Nur Originale (nicht-Duplikate) werden indiziert.
     */
    private static ImageIndex buildImageIndex(Path bookContentDir, String cleanBookName) throws IOException
    {
        ImageIndex index = new ImageIndex();

        // Bild-Verzeichnis parallel zum Text-Verzeichnis
        Path imageDir = resolveImageDir(bookContentDir);
        if (imageDir == null || !Files.isDirectory(imageDir))
        {
            return index;
        }
        index.imageSourceDir = imageDir;

        // Raw-Verzeichnis für Duplikat-Info
        Path rawDir = resolveRawDir(bookContentDir);
        if (rawDir == null || !Files.isDirectory(rawDir))
        {
            // Ohne Raw-Info: alle Bilder als Original behandeln
            collectAllImagesAsOriginals(imageDir, index);
            return index;
        }

        // Raw-JSONs lesen für Duplikat-Erkennung
        try (Stream<Path> jsonFiles = Files.list(rawDir))
        {
            jsonFiles.filter(p -> p.toString().endsWith(".json"))
                .sorted()
                .forEach(jsonFile ->
                {
                    try
                    {
                        RawPageData page = MAPPER.readValue(jsonFile.toFile(), RawPageData.class);
                        if (page.images == null) return;

                        for (RawPageData.RawImage img : page.images)
                        {
                            if (img.filename == null) continue;

                            boolean isDuplicate = img.filename.startsWith("DUPLIKAT:");
                            if (!isDuplicate)
                            {
                                // Original-Bild: in Index aufnehmen
                                Path imgFile = imageDir.resolve(img.filename);
                                if (Files.exists(imgFile))
                                {
                                    index.addImage(page.pageNumber, img.filename, imgFile);
                                }
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        LOGGER.warn("Fehler beim Lesen von {}: {}", jsonFile, e.getMessage());
                    }
                });
        }

        return index;
    }

    private static void collectAllImagesAsOriginals(Path imageDir, ImageIndex index) throws IOException
    {
        Pattern pagePattern = Pattern.compile("seite_(\\d+)_bild_\\d+\\.png");
        try (Stream<Path> files = Files.list(imageDir))
        {
            files.filter(p -> p.toString().endsWith(".png"))
                .sorted()
                .forEach(imgFile ->
                {
                    Matcher m = pagePattern.matcher(imgFile.getFileName().toString());
                    if (m.matches())
                    {
                        int pageNum = Integer.parseInt(m.group(1));
                        index.addImage(pageNum, imgFile.getFileName().toString(), imgFile);
                    }
                });
        }
    }

    /**
     * Löst das Image-Verzeichnis parallel zum Text-Verzeichnis auf.
     * text/.../Die Flusslande (180)/Die Flusslande - 04 - Regionen
     * → images/.../Die Flusslande (180)/Die Flusslande - 04 - Regionen
     */
    private static Path resolveImageDir(Path bookContentDir)
    {
        String contentPath = bookContentDir.toString().replace("\\", "/");
        String imagePath = contentPath.replace(TEXT_BASE.replace("\\", "/"), IMAGE_BASE);
        Path resolved = Paths.get(imagePath);
        return Files.isDirectory(resolved) ? resolved : null;
    }

    private static Path resolveRawDir(Path bookContentDir)
    {
        String contentPath = bookContentDir.toString().replace("\\", "/");
        String rawPath = contentPath.replace(
            TEXT_BASE.replace("\\", "/"),
            RAW_BASE
        );
        // Raw hat die gleiche Unterstruktur aber unter /raw/ statt /text/
        // und die Verzeichnisnamen können leicht abweichen
        Path resolved = Paths.get(rawPath);
        if (Files.isDirectory(resolved)) return resolved;

        // Fallback: nach Parent-Verzeichnis suchen
        Path rawBase = Paths.get(RAW_BASE);
        Path relative = Paths.get(TEXT_BASE).relativize(bookContentDir);
        resolved = rawBase.resolve(relative);
        return Files.isDirectory(resolved) ? resolved : null;
    }

    // =====================================================================
    // Buch verarbeiten
    // =====================================================================

    private static void processBook(Path headingsFile, Path vaultDir, String cleanBookName,
                                     ImageIndex imageIndex) throws IOException
    {
        List<String> lines = Files.readAllLines(headingsFile, StandardCharsets.UTF_8);

        List<HeadingNode> roots = parseHeadings(lines);

        // Seitenbereiche für Nodes berechnen
        assignPageRanges(roots);

        // Vault-Verzeichnis erstellen
        if (Files.exists(vaultDir))
        {
            deleteRecursive(vaultDir);
        }
        Files.createDirectories(vaultDir);

        // Bilder kopieren nach _Quellen/Bilder/{Buchtitel}/
        Path imageVaultDir = Paths.get(VAULT_BASE)
            .resolve(IMAGE_VAULT_FOLDER)
            .resolve(sanitizeFilename(cleanBookName));
        int imagesCopied = copyImagesToVault(imageIndex, imageVaultDir);

        // Relativer Pfad von Buchordner zu Bilderordner für Embeds
        // Vault: Regionen/{Buch}/ → _Quellen/Bilder/{Buch}/
        String imageRelPath = IMAGE_VAULT_FOLDER + "/" + sanitizeFilename(cleanBookName);

        // FolderNote für das Buch (nur Titelbild + Links zu Kapiteln)
        writeFolderNote(vaultDir, cleanBookName, roots, imageIndex, imageRelPath);

        // Artikel schreiben
        int totalFiles = 1; // FolderNote zählt mit
        for (HeadingNode root : roots)
        {
            totalFiles += writeNode(root, vaultDir, imageIndex, imageRelPath);
        }

        LOGGER.info("  {} Dateien, {} Bilder -> {}", totalFiles, imagesCopied, vaultDir);
    }

    // =====================================================================
    // Bilder kopieren
    // =====================================================================

    private static int copyImagesToVault(ImageIndex imageIndex, Path targetDir) throws IOException
    {
        if (imageIndex.isEmpty()) return 0;

        if (Files.exists(targetDir))
        {
            deleteRecursive(targetDir);
        }
        Files.createDirectories(targetDir);

        int count = 0;
        for (ImageInfo img : imageIndex.allImages())
        {
            Path target = targetDir.resolve(img.filename);
            Files.copy(img.sourcePath, target, StandardCopyOption.REPLACE_EXISTING);
            count++;
        }
        return count;
    }

    // =====================================================================
    // FolderNote pro Buch
    // =====================================================================

    private static void writeFolderNote(Path vaultDir, String bookName, List<HeadingNode> roots,
                                         ImageIndex imageIndex, String imageRelPath) throws IOException
    {
        StringBuilder content = new StringBuilder();

        // Titelbild (erstes Bild des Buches)
        ImageInfo titleImage = imageIndex.getFirstImage();
        if (titleImage != null)
        {
            content.append("![[").append(imageRelPath).append("/")
                .append(titleImage.filename).append("]]\n\n");
        }

        // Links zu Kapiteln
        for (HeadingNode root : roots)
        {
            content.append("- [[").append(root.title).append("]]\n");
        }

        Path folderNote = vaultDir.resolve(sanitizeFilename(bookName) + ".md");
        Files.writeString(folderNote, content.toString(), StandardCharsets.UTF_8);
    }

    // =====================================================================
    // Heading-Parsing
    // =====================================================================

    private static List<HeadingNode> parseHeadings(List<String> lines)
    {
        List<HeadingNode> roots = new ArrayList<>();
        HeadingNode currentH1 = null;
        HeadingNode currentH2 = null;
        HeadingNode currentH3 = null;
        HeadingNode currentNode = null;

        Pattern headingPattern = Pattern.compile("^(#{1,4})\\s+(.+)$");

        for (String line : lines)
        {
            // HTML-Kommentare: Seitennummer extrahieren, Zeile nicht als Body übernehmen
            if (line.trim().startsWith("<!--"))
            {
                Matcher pageMatcher = Pattern.compile("Seite\\s+(\\d+)").matcher(line);
                if (pageMatcher.find() && currentNode != null)
                {
                    currentNode.lastSeenPage = Integer.parseInt(pageMatcher.group(1));
                }
                continue;
            }

            Matcher m = headingPattern.matcher(line);
            if (m.matches())
            {
                int level = m.group(1).length();
                String title = m.group(2).trim();

                if (title.length() > 120)
                {
                    if (currentNode != null) currentNode.body.add(line);
                    continue;
                }

                if (shouldSkip(title)) continue;

                HeadingNode node = new HeadingNode(level, title);

                switch (level)
                {
                    case 1:
                        roots.add(node);
                        currentH1 = node;
                        currentH2 = null;
                        currentH3 = null;
                        currentNode = node;
                        break;
                    case 2:
                        if (currentH1 != null) currentH1.children.add(node);
                        else roots.add(node);
                        currentH2 = node;
                        currentH3 = null;
                        currentNode = node;
                        break;
                    case 3:
                        if (currentH2 != null) currentH2.children.add(node);
                        else if (currentH1 != null) currentH1.children.add(node);
                        else roots.add(node);
                        currentH3 = node;
                        currentNode = node;
                        break;
                    case 4:
                        if (currentH3 != null) currentH3.children.add(node);
                        else if (currentH2 != null) currentH2.children.add(node);
                        else if (currentH1 != null) currentH1.children.add(node);
                        else roots.add(node);
                        currentNode = node;
                        break;
                }
            }
            else
            {
                if (currentNode != null && !line.isBlank())
                {
                    currentNode.body.add(line);
                }
                else if (currentNode != null && !currentNode.body.isEmpty())
                {
                    currentNode.body.add("");
                }
            }
        }

        return roots;
    }

    /**
     * Weist jedem Node einen Seitenbereich (startPage/endPage) zu,
     * basierend auf den <!-- Seite N --> Kommentaren.
     */
    private static void assignPageRanges(List<HeadingNode> roots)
    {
        List<HeadingNode> flat = new ArrayList<>();
        flattenNodes(roots, flat);

        for (int i = 0; i < flat.size(); i++)
        {
            HeadingNode node = flat.get(i);
            // startPage = lastSeenPage dieses Nodes (erste Seite wo er beginnt)
            if (node.lastSeenPage > 0)
            {
                node.startPage = node.lastSeenPage;
            }
            // endPage = startPage des nächsten Nodes - 1 (oder lastSeenPage)
            if (i + 1 < flat.size())
            {
                HeadingNode next = flat.get(i + 1);
                if (next.lastSeenPage > 0)
                {
                    node.endPage = next.lastSeenPage;
                }
            }
            if (node.endPage == 0) node.endPage = node.startPage;
        }
    }

    private static void flattenNodes(List<HeadingNode> nodes, List<HeadingNode> flat)
    {
        for (HeadingNode node : nodes)
        {
            flat.add(node);
            flattenNodes(node.children, flat);
        }
    }

    // =====================================================================
    // Vault-Dateien schreiben
    // =====================================================================

    private static int writeNode(HeadingNode node, Path parentDir,
                                  ImageIndex imageIndex, String imageRelPath) throws IOException
    {
        String safeName = sanitizeFilename(node.title);
        if (safeName.isBlank()) return 0;

        int count = 0;

        if (node.children.isEmpty())
        {
            Path noteFile = parentDir.resolve(safeName + ".md");
            writeNote(noteFile, node, imageIndex, imageRelPath);
            count = 1;
        }
        else
        {
            Path folder = parentDir.resolve(safeName);
            Files.createDirectories(folder);

            Path folderNote = folder.resolve(safeName + ".md");
            writeNote(folderNote, node, imageIndex, imageRelPath);
            count = 1;

            for (HeadingNode child : node.children)
            {
                count += writeNode(child, folder, imageIndex, imageRelPath);
            }
        }

        return count;
    }

    private static void writeNote(Path file, HeadingNode node,
                                   ImageIndex imageIndex, String imageRelPath) throws IOException
    {
        StringBuilder content = new StringBuilder();

        // Bilder für den Seitenbereich dieses Nodes einfügen (vor Body)
        List<ImageInfo> nodeImages = imageIndex.getImagesForRange(node.startPage, node.endPage);
        for (ImageInfo img : nodeImages)
        {
            content.append("![[").append(imageRelPath).append("/")
                .append(img.filename).append("]]\n\n");
        }

        // Body-Text
        boolean hasBody = node.body.stream().anyMatch(l -> !l.isBlank());
        if (hasBody)
        {
            for (String line : node.body)
            {
                content.append(line).append("\n");
            }
            while (content.length() > 0 && content.charAt(content.length() - 1) == '\n'
                && content.length() > 1 && content.charAt(content.length() - 2) == '\n')
            {
                content.setLength(content.length() - 1);
            }
        }

        // Kinder als Links
        if (!node.children.isEmpty())
        {
            if (hasBody || !nodeImages.isEmpty()) content.append("\n");
            for (HeadingNode child : node.children)
            {
                content.append("- [[").append(child.title).append("]]\n");
            }
        }

        Files.writeString(file, content.toString(), StandardCharsets.UTF_8);
    }

    // =====================================================================
    // Hilfsmethoden
    // =====================================================================

    private static boolean shouldSkip(String title)
    {
        String upper = title.toUpperCase().replaceAll("[*#]", "").trim();
        for (String skip : SKIP_HEADINGS)
        {
            if (upper.contains(skip)) return true;
        }
        return false;
    }

    private static String sanitizeFilename(String name)
    {
        name = name.replaceAll("\\*+", "");
        name = name.replaceAll("[<>:\"/\\\\|?*#]", "");
        name = name.replaceAll("\\s+", " ");
        name = name.trim();
        if (name.length() > 100) name = name.substring(0, 100).trim();
        return name;
    }

    private static void deleteRecursive(Path dir) throws IOException
    {
        if (!Files.exists(dir)) return;
        try (Stream<Path> walk = Files.walk(dir))
        {
            walk.sorted((a, b) -> b.toString().compareTo(a.toString()))
                .forEach(p ->
                {
                    try { Files.delete(p); }
                    catch (IOException e) { LOGGER.warn("Konnte nicht loeschen: {}", p); }
                });
        }
    }

    // =====================================================================
    // Datenmodell
    // =====================================================================

    static class HeadingNode
    {
        final int level;
        final String title;
        final List<HeadingNode> children = new ArrayList<>();
        final List<String> body = new ArrayList<>();
        int startPage;
        int endPage;
        int lastSeenPage;

        HeadingNode(int level, String title)
        {
            this.level = level;
            this.title = title;
        }

        @Override
        public String toString()
        {
            return "#".repeat(level) + " " + title
                + " (S." + startPage + "-" + endPage + ", " + children.size() + " children)";
        }
    }

    static class ImageInfo
    {
        final String filename;
        final Path sourcePath;
        final int page;

        ImageInfo(String filename, Path sourcePath, int page)
        {
            this.filename = filename;
            this.sourcePath = sourcePath;
            this.page = page;
        }
    }

    static class ImageIndex
    {
        Path imageSourceDir;
        // page → Liste von Bildern (nur Originale)
        private final TreeMap<Integer, List<ImageInfo>> byPage = new TreeMap<>();

        void addImage(int page, String filename, Path sourcePath)
        {
            byPage.computeIfAbsent(page, k -> new ArrayList<>())
                .add(new ImageInfo(filename, sourcePath, page));
        }

        boolean isEmpty()
        {
            return byPage.isEmpty();
        }

        ImageInfo getFirstImage()
        {
            if (byPage.isEmpty()) return null;
            List<ImageInfo> first = byPage.firstEntry().getValue();
            return first.isEmpty() ? null : first.get(0);
        }

        List<ImageInfo> getImagesForRange(int startPage, int endPage)
        {
            if (startPage <= 0 || byPage.isEmpty()) return List.of();
            List<ImageInfo> result = new ArrayList<>();
            for (Map.Entry<Integer, List<ImageInfo>> entry : byPage.subMap(startPage, true, endPage, true).entrySet())
            {
                result.addAll(entry.getValue());
            }
            return result;
        }

        List<ImageInfo> allImages()
        {
            List<ImageInfo> all = new ArrayList<>();
            byPage.values().forEach(all::addAll);
            return all;
        }
    }
}
