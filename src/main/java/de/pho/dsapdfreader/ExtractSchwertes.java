package de.pho.dsapdfreader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.pho.dsapdfreader.markdown.RawExtractor;
import de.pho.dsapdfreader.markdown.RawImageExtractor;
import de.pho.dsapdfreader.markdown.RawPageData;
import de.pho.dsapdfreader.markdown.RawRectExtractor;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * One-shot Re-Extraction nur fuer Kodex des Schwertes.
 * Wird verwendet, wenn der RawRectExtractor erweitert wurde und nur EIN Buch
 * neu extrahiert werden muss (das gesamte Korpus zu re-extracten dauert sonst
 * sehr lange).
 */
public class ExtractSchwertes {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static void main(String[] args) throws Exception {
        Path pdf = Path.of(
                "D:/Daten/Dropbox/pdf.library/RPG/DSA 5/01 - Regeln/"
                        + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln.pdf");
        Path rawDir = Path.of(
                "export/markdown/raw/01 - Regeln/"
                        + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln");

        Files.createDirectories(rawDir);

        PDFParser parser = new PDFParser(new RandomAccessFile(pdf.toFile(), "r"));
        parser.parse();
        try (PDDocument document = new PDDocument(parser.getDocument())) {
            // Text
            RawExtractor textExtractor = new RawExtractor();
            List<RawPageData> pages = textExtractor.extract(document);
            System.out.println("Text: " + pages.size() + " Seiten");

            // Rects (jetzt inkl. Linien)
            RawRectExtractor rectExtractor = new RawRectExtractor(document.getPage(0));
            Map<Integer, List<RawPageData.RawRect>> rectMap = rectExtractor.extract(document);
            int totalRects = 0, totalLines = 0;
            for (RawPageData page : pages) {
                List<RawPageData.RawRect> rcs = rectMap.get(page.pageNumber);
                page.rects = rcs != null ? rcs : new ArrayList<>();
                totalRects += page.rects.size();
                for (RawPageData.RawRect r : page.rects) if (r.isLine) totalLines++;
            }
            System.out.println("Rects: " + totalRects + " (davon " + totalLines + " Linien)");

            // Image-Metadaten neu extrahieren (Bullets etc. brauchen die images-Liste).
            // Bilder selbst landen in images/ und sind unveraendert.
            String pdfBaseName = pdf.getFileName().toString().replaceAll("(?i)\\.pdf$", "");
            Path imageDir = rawDir.resolve("images");
            RawImageExtractor imageExtractor = new RawImageExtractor(imageDir, pdfBaseName);
            Map<Integer, List<RawPageData.RawImage>> imageMap = imageExtractor.extract(document);
            int totalImages = 0;
            for (RawPageData page : pages) {
                List<RawPageData.RawImage> imgs = imageMap.get(page.pageNumber);
                page.images = imgs != null ? imgs : new ArrayList<>();
                totalImages += page.images.size();
            }
            System.out.println("Images: " + totalImages + " Meta-Eintraege");

            // Per-Page-JSON
            for (RawPageData page : pages) {
                Path pageFile = rawDir.resolve(String.format("page_%03d.json", page.pageNumber));
                MAPPER.writeValue(pageFile.toFile(), page);
            }

            Files.writeString(rawDir.resolve("_extracted.marker"),
                    "source=" + pdf.toAbsolutePath() + "\n"
                    + "timestamp=" + Files.getLastModifiedTime(pdf) + "\n"
                    + "pages=" + pages.size() + "\n"
                    + "lines_extracted=true\n");
            System.out.println("Done.");
        }
    }
}
