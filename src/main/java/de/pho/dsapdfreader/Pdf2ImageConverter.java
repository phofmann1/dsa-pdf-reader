package de.pho.dsapdfreader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pdf2ImageConverter {
    private static final String PDF_BASE_PATH = "D:/Daten/Dropbox/pdf.library/RPG/DSA 5/";

private static final String bogenDeluxe = "20 - Heldenbogen/Deluxe Heldenbogen (237)/Deluxe Heldenbogen - 20 - Heldenbogen.pdf";
private static final String bogenEchsensumpfe = "20 - Heldenbogen/Heldenbogen Echsensumpfe (235)/Heldenbogen Echsensumpfe - 20 - Heldenbogen.pdf";
private static final String bogenGjalsker = "20 - Heldenbogen/Heldenbogen Gjalsker (238)/Heldenbogen Gjalsker - 20 - Heldenbogen.pdf";
private static final String bogenSudHorasreich = "20 - Heldenbogen/Heldenbogen Sud-Horasreich (241)/Heldenbogen Sud-Horasreich - 20 - Heldenbogen.pdf";
private static final String bogenThorwal = "20 - Heldenbogen/Heldenbogen Thorwal (239)/Heldenbogen Thorwal - 20 - Heldenbogen.pdf";
private static final String bogenWolfsfrost = "20 - Heldenbogen/Heldenbogen Wolfsfrost (234)/Heldenbogen Wolfsfrost - 20 - Heldenbogen.pdf";
private static final String bogenZyklopeninsel = "20 - Heldenbogen/Heldenbogen Zyklopeninseln (240)/Heldenbogen Zyklopeninseln - 20 - Heldenbogen.pdf";
private static final String bogenHavena = "20 - Heldenbogen/Heldendokument Havena (236)/Heldendokument Havena - 20 - Heldenbogen.pdf";


    public static void main(String[] args) {
        List<String> bogenList = List.of(
                bogenDeluxe
//                bogenEchsensumpfe,
//                bogenGjalsker,
//                bogenSudHorasreich,
//                bogenThorwal,
//                bogenWolfsfrost,
//                bogenZyklopeninsel,
//                bogenHavena
        );
        bogenList.forEach(b -> {
            String pdfPath = PDF_BASE_PATH + b;
            String outputDir = "export/heldenbogen/";

            try {
                File file = new File(pdfPath);
                PDDocument document = PDDocument.load(file);
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                for (int page = 0; page < document.getNumberOfPages(); ++page) {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300); // 300 DPI = good quality

                    String fileName = outputDir + extractBogenName(b) + "/page_" + (page + 1) + ".png";

                    Path filePath = Paths.get(fileName);
                    if (Files.notExists(filePath.getParent())) {
                        Files.createDirectories(filePath.getParent());
                    }
                    ImageIO.write(bim, "png", new File(fileName));
                    System.out.println("Saved: " + fileName);
                }

                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
            }

            public static String extractBogenName(String path) {
                String regex = "(?<=Heldenbogen/).*(?= \\()";

                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(path);

                if (matcher.find()) {
                    return matcher.group();

                } else {
                    return "NOT FOUND";
                }
            }
}
