package de.pho.dsapdfreader;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.pdf.model.ImageWithMetaInfo;
import de.pho.dsapdfreader.tools.csv.CsvHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

/**
 * Extrahiert Bilder aus DSA-PDFs und schreibt einen Raw-Datensatz (CSV) mit Metainformationen
 * (Position, Größe, Checksumme etc.) analog zum Text-Raw-Datensatz.
 *
 * Aktuell konfiguriert für: Rüstkammer der Wüstenreiche
 *
 * Bereits existierende Ausgabedateien werden übersprungen.
 */
public class DsaPdfImageMetaExtractorMain extends PDFStreamEngine
{
    private static final String PDF_BASE_PATH = "D:/Daten/Dropbox/pdf.library/RPG/DSA 5/";
    private static final String PDF_BASE_PATH_2 = "D:/Daten/OneDrive/pdf.library/RPG/DSA 5 - SL/";
    private static final String PDF_BASE_PATH_3 = "D:\\develop\\project\\pdf-archive\\";
    private static final String PATH_BASE = "d:\\develop\\project\\java\\dsa-pdf-reader\\export\\";
    private static final String PATH_IMAGE_RAW = PATH_BASE + "01 - pdf2text\\";
    private static final String PATH_IMAGES = PATH_BASE + "images\\";

    private static final Logger LOGGER = LogManager.getLogger();

    private final List<String> checksums = new ArrayList<>();
    private final List<ImageWithMetaInfo> results = new ArrayList<>();
    private String currentPublication;
    private int currentPage;
    private int imageIndex;

    public static void main(String[] args) throws IOException
    {
        URL url = DsaPdfImageMetaExtractorMain.class.getClassLoader().getResource("topic-conf-img.csv");
        List<TopicConfiguration> configs = CsvHandler.readBeanFromUrl(TopicConfiguration.class, url);

        configs.stream()
            .filter(c -> c != null && "Rüstkammer_der_Wüstenreiche".equals(c.publication))
            .forEach(conf -> {
                String csvFilename = PATH_IMAGE_RAW + conf.publication + "_IMG_txt.csv";
                File csvOut = new File(csvFilename);
                if (csvOut.exists())
                {
                    LOGGER.info("Überspringe (bereits vorhanden): {}", csvFilename);
                    return;
                }

                String msg = String.format("Image-Meta-Extraktion: %s", conf.publication);
                LOGGER.info(msg);

                PDDocument document = null;
                try
                {
                    document = loadDocument(conf.path, conf.pdfName);
                    if (document == null)
                    {
                        LOGGER.error("PDF nicht gefunden: {} / {}", conf.path, conf.pdfName);
                        return;
                    }

                    DsaPdfImageMetaExtractorMain extractor = new DsaPdfImageMetaExtractorMain();
                    extractor.currentPublication = conf.publication;
                    extractor.currentPage = 0;
                    extractor.imageIndex = 0;

                    for (PDPage page : document.getPages())
                    {
                        extractor.currentPage++;
                        extractor.processPage(page);
                    }

                    // Bilder als PNG speichern und CSV schreiben
                    CsvHandler.writeBeanToUrl(csvOut, extractor.results);
                    LOGGER.info("Geschrieben: {} ({} Bilder)", csvFilename, extractor.results.size());
                }
                catch (IOException e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
                finally
                {
                    if (document != null)
                    {
                        try { document.close(); } catch (IOException e) { LOGGER.error(e.getMessage(), e); }
                    }
                }
            });
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
                    PDImageXObject image = (PDImageXObject) xobject;
                    imageIndex++;

                    // Position aus der aktuellen Transformationsmatrix (CTM)
                    Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
                    float x = ctm.getTranslateX();
                    float y = ctm.getTranslateY();
                    float width = ctm.getScalingFactorX();
                    float height = ctm.getScalingFactorY();

                    // Bild-Pixel-Daten
                    int imgWidth = image.getWidth();
                    int imgHeight = image.getHeight();
                    String suffix = image.getSuffix();
                    int colorComponents = image.getColorSpace() != null ? image.getColorSpace().getNumberOfComponents() : 0;
                    int bitsPerComponent = image.getBitsPerComponent();

                    // Checksumme berechnen
                    BufferedImage bImage = image.getImage();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bImage, "PNG", baos);
                    byte[] bytes = baos.toByteArray();

                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(bytes);
                    String checksum = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();

                    // Dateiname für das Bild
                    String imageFilename = currentPublication + "_" + currentPage + "_image_" + imageIndex + ".png";

                    // Bild speichern (Duplikate überspringen)
                    boolean isDuplicate = checksums.contains(checksum);
                    if (!isDuplicate)
                    {
                        checksums.add(checksum);
                        File output = new File(PATH_IMAGES + imageFilename);
                        File parentDir = output.getParentFile();
                        if (parentDir != null && !parentDir.exists())
                        {
                            parentDir.mkdirs();
                        }
                        ImageIO.write(bImage, "PNG", output);
                    }

                    // Raw-Datensatz immer anlegen (auch bei Duplikaten, für vollständige Positionsdaten)
                    ImageWithMetaInfo meta = new ImageWithMetaInfo(
                        currentPublication,
                        currentPage,
                        imageIndex,
                        x,
                        y,
                        width,
                        height,
                        imgWidth,
                        imgHeight,
                        suffix != null ? suffix : "",
                        colorComponents,
                        bitsPerComponent,
                        checksum,
                        isDuplicate ? "DUPLIKAT:" + imageFilename : imageFilename
                    );
                    results.add(meta);

                    LOGGER.debug("Bild gefunden: Seite={}, x={}, y={}, w={}, h={}, px={}x{}, {}",
                        currentPage, x, y, width, height, imgWidth, imgHeight, imageFilename);
                }
                else if (xobject instanceof PDFormXObject)
                {
                    PDFormXObject form = (PDFormXObject) xobject;
                    showForm(form);
                }
            }
            else
            {
                super.processOperator(operator, operands);
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static PDDocument loadDocument(String path, String pdfName)
    {
        PDDocument document = loadFromPath(PDF_BASE_PATH + path + "/" + pdfName);
        if (document == null)
        {
            document = loadFromPath(PDF_BASE_PATH_2 + path + "/" + pdfName);
        }
        if (document == null)
        {
            document = loadFromPath(PDF_BASE_PATH_3 + path + "/" + pdfName);
        }
        return document;
    }

    private static PDDocument loadFromPath(String fullPath)
    {
        try
        {
            return PDDocument.load(new File(fullPath));
        }
        catch (IOException e)
        {
            LOGGER.debug("PDF nicht unter: {}", fullPath);
            return null;
        }
    }
}
