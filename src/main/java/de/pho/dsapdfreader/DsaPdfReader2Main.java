package de.pho.dsapdfreader;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.pdf.DsaPDFTextStripper;
import de.pho.dsapdfreader.pdf.DsaPDFTextStripper2;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;
import de.pho.dsapdfreader.tools.csv.CsvHandler;
import de.pho.dsapdfreader.tools.csv.DsaStringCleanupTool;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DsaPdfReader2Main {

    public static void main(String[] args) {
        String pdfPath = "D:\\Daten\\Dropbox\\pdf.library\\RPG\\DSA 5\\04 - Regionen\\Der Wolfsfrost (230)\\Der Wolfsfrost - 04 - Regionen.pdf"; // Replace with your PDF path
        try {
            String markdown = convertPdfToText2(pdfPath);
            System.out.println(markdown);
        } catch (IOException e) {
            System.err.println("Error reading PDF: " + e.getMessage());
        }
    }

    public static String convertPdfToText2(String p) throws IOException {
        File f = new File(p);
        PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
        parser.parse();
        COSDocument cosDoc = parser.getDocument();
        DsaPDFTextStripper2 generalParser = new DsaPDFTextStripper2("Test", TopicEnum.CONTENT_LAND_UND_LEUTE);
        PDDocument pdDoc = new PDDocument(cosDoc);

        generalParser.setStartPage(6);
        generalParser.setEndPage(pdDoc.getNumberOfPages());

        generalParser.getText(pdDoc);

        pdDoc.close();
        cosDoc.close();

        final List<TextWithMetaInfo> texts = generalParser.resultTexts;


        pdDoc.close();
        cosDoc.close();

        File fOut = new File("D:\\Daten\\Der Wolfsfrost - 04 - Regionen.csv");
        CsvHandler.writeBeanToUrl(fOut, texts);
        return "Done";
    }

    public static String convertPDFToMarkdown(String filePath) throws IOException {
        StringBuilder markdown = new StringBuilder();

        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            int pageCount = document.getNumberOfPages();

            for (int page = 1; page <= pageCount; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String text = stripper.getText(document);


                // Simple formatting: detect headings by line length or capitalization
                for (String line : text.split("\n")) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty()) continue;

                    if (trimmed.length() < 40 && trimmed.equals(trimmed.toUpperCase())) {
                        markdown.append("### ").append(trimmed).append("\n\n");
                    } else {
                        markdown.append(trimmed).append("\n\n");
                    }
                }
            }
        }

        return markdown.toString();
    }
}

