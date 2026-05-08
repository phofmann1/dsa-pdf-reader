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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generischer One-shot Extractor: nimmt einen PDF-Pfad und einen Buch-Sub-Pfad
 * als Argumente und extrahiert Text/Rects/Images in {@code export/markdown/raw/<bookSubPath>/}.
 *
 * <p>Aufruf:
 * <pre>java de.pho.dsapdfreader.ExtractPdf "C:\path\to\book.pdf" "01 - Regeln/Mein Buch (999)/Mein Buch - 01 - Regeln"</pre>
 */
public class ExtractPdf {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: ExtractPdf <pdf-path> <book-sub-path>");
            System.exit(1);
        }
        Path pdf = Path.of(args[0]);
        Path rawDir = Path.of("export/markdown/raw").resolve(args[1]);
        Files.createDirectories(rawDir);

        PDFParser parser = new PDFParser(new RandomAccessFile(pdf.toFile(), "r"));
        parser.parse();
        try (PDDocument document = new PDDocument(parser.getDocument())) {
            RawExtractor textExtractor = new RawExtractor();
            List<RawPageData> pages = textExtractor.extract(document);
            System.out.println("Text: " + pages.size() + " pages");

            RawRectExtractor rectExtractor = new RawRectExtractor(document.getPage(0));
            Map<Integer, List<RawPageData.RawRect>> rectMap = rectExtractor.extract(document);
            int totalRects = 0, totalLines = 0;
            for (RawPageData page : pages) {
                List<RawPageData.RawRect> rcs = rectMap.get(page.pageNumber);
                page.rects = rcs != null ? rcs : new ArrayList<>();
                totalRects += page.rects.size();
                for (RawPageData.RawRect r : page.rects) if (r.isLine) totalLines++;
            }
            System.out.println("Rects: " + totalRects + " (lines: " + totalLines + ")");

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
            System.out.println("Images: " + totalImages);

            for (RawPageData page : pages) {
                Path pageFile = rawDir.resolve(String.format("page_%03d.json", page.pageNumber));
                MAPPER.writeValue(pageFile.toFile(), page);
            }
            Files.writeString(rawDir.resolve("_extracted.marker"),
                    "source=" + pdf.toAbsolutePath() + "\npages=" + pages.size() + "\n");
            System.out.println("Done: " + pages.size() + " pages → " + rawDir);
        }
    }
}
