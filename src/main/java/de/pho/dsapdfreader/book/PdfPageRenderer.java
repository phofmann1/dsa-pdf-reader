package de.pho.dsapdfreader.book;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PdfPageRenderer {

    private PdfPageRenderer() {}

    public static void render(Path pdf, Path outDir, int firstPage, int lastPage, int dpi) throws IOException {
        Files.createDirectories(outDir);
        try (PDDocument doc = PDDocument.load(pdf.toFile())) {
            PDFRenderer renderer = new PDFRenderer(doc);
            int total = doc.getNumberOfPages();
            int from = Math.max(1, firstPage);
            int to = Math.min(total, lastPage);
            for (int p = from; p <= to; p++) {
                BufferedImage img = renderer.renderImageWithDPI(p - 1, dpi, ImageType.RGB);
                Path out = outDir.resolve(String.format("page_%03d.png", p));
                ImageIO.write(img, "png", out.toFile());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.err.println("Usage: PdfPageRenderer <pdf> <outDir> <firstPage> <lastPage> [dpi=150]");
            System.exit(1);
        }
        Path pdf = Path.of(args[0]);
        Path outDir = Path.of(args[1]);
        int first = Integer.parseInt(args[2]);
        int last = Integer.parseInt(args[3]);
        int dpi = args.length > 4 ? Integer.parseInt(args[4]) : 150;
        render(pdf, outDir, first, last, dpi);
    }
}
