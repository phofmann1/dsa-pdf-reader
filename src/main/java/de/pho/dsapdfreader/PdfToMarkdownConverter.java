package de.pho.dsapdfreader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PdfToMarkdownConverter {
    public static void main(String[] args) {
        String pdfPath = "D:\\Daten\\Dropbox\\pdf.library\\RPG\\DSA 5\\04 - Regionen\\Der Wolfsfrost (234)\\Der Wolfsfrost - 04 - Regionen.pdf";
        String markdownPath = "D:\\develop\\project\\java\\dsa-pdf-reader\\export\\markdown\\file.md";

        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            FileWriter writer = new FileWriter(markdownPath);
            writer.write(convertToMarkdown(text));
            writer.close();

            System.out.println("Conversion completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertToMarkdown(String text) {
        // Beispielkonvertierung: Hier kannst du erweiterte Regeln anwenden
        StringBuilder markdown = new StringBuilder();
        markdown.append("# PDF Inhalt\n\n");
        markdown.append(text);
        return markdown.toString();
    }
}
