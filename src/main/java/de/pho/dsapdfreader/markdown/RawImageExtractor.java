package de.pho.dsapdfreader.markdown;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

/**
 * Stufe 0 (Bilder): Extrahiert Bild-Rohdaten (Position, Groesse, Metadaten) aus einem PDF.
 * Arbeitet analog zum RawExtractor fuer Text und produziert RawImage-Objekte
 * die in RawPageData eingebettet werden.
 *
 * Bilder werden als PNG in imageDir gespeichert, Duplikate (gleiche Checksumme) uebersprungen.
 */
public class RawImageExtractor extends PDFStreamEngine
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<String, String> checksumToOrigin = new HashMap<>();
    private final Map<Integer, List<RawPageData.RawImage>> pageImages = new HashMap<>();
    private final Path imageDir;
    private final String filePrefix;
    private int currentPage;
    private int imageIndex;
    private float currentPageHeight;

    /**
     * @param imageDir   Verzeichnis in das die PNGs geschrieben werden
     * @param filePrefix Praefix fuer Bilddateinamen (z.B. der PDF-Basisname)
     */
    public RawImageExtractor(Path imageDir, String filePrefix)
    {
        this.imageDir = imageDir;
        this.filePrefix = filePrefix;

        // Operator-Handler registrieren fuer korrekte CTM-Berechnung
        addOperator(new Concatenate());      // cm
        addOperator(new Save());             // q
        addOperator(new Restore());          // Q
        addOperator(new SetGraphicsStateParameters()); // gs
        addOperator(new SetMatrix());        // Tm
    }

    /**
     * Extrahiert Bilder aus dem gesamten Dokument.
     * Gibt eine Map zurueck: Seitennummer (1-basiert) → Liste der RawImages.
     */
    public Map<Integer, List<RawPageData.RawImage>> extract(PDDocument document) throws IOException
    {
        pageImages.clear();
        checksumToOrigin.clear();
        currentPage = 0;
        imageIndex = 0;

        Files.createDirectories(imageDir);

        for (PDPage page : document.getPages())
        {
            currentPage++;
            currentPageHeight = page.getMediaBox().getHeight();
            processPage(page);
        }

        return pageImages;
    }

    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException
    {
        try
        {
            String operation = operator.getName();
            if ("Do".equals(operation))
            {
                COSName objectName = (COSName) operands.get(0);
                PDXObject xobject = getResources().getXObject(objectName);
                if (xobject instanceof PDImageXObject)
                {
                    processImage((PDImageXObject) xobject);
                }
                else if (xobject instanceof PDFormXObject)
                {
                    showForm((PDFormXObject) xobject);
                }
            }
            else
            {
                super.processOperator(operator, operands);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Fehler bei Bild auf Seite {}: {}", currentPage, e.getMessage(), e);
        }
    }

    private void processImage(PDImageXObject image) throws Exception
    {
        imageIndex++;

        // Position aus CTM (PDF-Koordinaten: Ursprung unten-links)
        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
        float x = ctm.getTranslateX();
        float pdfY = ctm.getTranslateY();
        float width = ctm.getScalingFactorX();
        float height = ctm.getScalingFactorY();
        // Umrechnung in top-left Y (gleich wie Textkoordinaten)
        float y = currentPageHeight - pdfY - height;

        // Pixel-Metadaten
        int pixelWidth = image.getWidth();
        int pixelHeight = image.getHeight();
        String suffix = image.getSuffix() != null ? image.getSuffix() : "";
        int colorComponents = image.getColorSpace() != null
            ? image.getColorSpace().getNumberOfComponents() : 0;
        int bitsPerComponent = image.getBitsPerComponent();

        // Checksumme
        BufferedImage bImage = image.getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "PNG", baos);
        byte[] bytes = baos.toByteArray();

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);
        String checksum = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();

        // Dateiname
        String imageFilename = filePrefix + "_p" + currentPage + "_img" + imageIndex + ".png";

        // Bild speichern (Duplikate ueberspringen)
        String originFilename = checksumToOrigin.get(checksum);
        boolean isDuplicate = originFilename != null;
        if (!isDuplicate)
        {
            checksumToOrigin.put(checksum, imageFilename);
            File output = imageDir.resolve(imageFilename).toFile();
            ImageIO.write(bImage, "PNG", output);
        }

        RawPageData.RawImage raw = new RawPageData.RawImage(
            imageIndex, x, y, width, height,
            pixelWidth, pixelHeight, suffix,
            colorComponents, bitsPerComponent,
            checksum,
            isDuplicate ? "DUPLIKAT:" + imageFilename : imageFilename,
            isDuplicate ? originFilename : null
        );

        pageImages.computeIfAbsent(currentPage, k -> new ArrayList<>()).add(raw);

        LOGGER.debug("Bild: Seite={}, x={}, y={}, w={}, h={}, px={}x{}, {}",
            currentPage, x, y, width, height, pixelWidth, pixelHeight, imageFilename);
    }
}
