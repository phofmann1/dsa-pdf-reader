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
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

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

import de.pho.dsapdfreader.config.ConfigurationInitializer;
import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicEnum;

public class DsaPdfImageExtractorMain extends PDFStreamEngine
{

  private static final String PDF_BASE_PATH = "D:/Daten/Dropbox/pdf.library/RPG/DSA 5/";

  private static final AtomicReference<TopicEnum> currentTopic = new AtomicReference<>();
  private static final AtomicReference<String> currentPublication = new AtomicReference<>();
  private static final AtomicReference<Integer> currentPage = new AtomicReference<>();
  private static final Logger LOGGER = LogManager.getLogger();
  private static final List<String> checksums = new ArrayList<>();
  private static List<TopicConfiguration> configs = null;
  public int imageNumber = 1;

  public static void main(String[] args) throws IOException
  {
    URL url = DsaPdfReaderMain.class.getClassLoader().getResource("topic-conf-img.csv");
    configs = CsvHandler.readBeanFromUrl(TopicConfiguration.class, url);
    configs.stream()
        .filter(c -> c != null && c.active)
        .forEach(conf -> {
          String msg = String.format("Config PDF ImageExtract verarbeiten: %s (%s)", conf.publication, conf.topic);
          LOGGER.info(msg);

          currentTopic.set(conf.topic);
          currentPublication.set(conf.publication);

          String path = conf.path;
          if (path == null)
          {
            URL urlPdfLibInClassPath = DsaPdfImageExtractorMain.class.getClassLoader().getResource("./pdf_lib");
            if (urlPdfLibInClassPath == null)
            {
              throw new InvalidPathException("./pdf_lib", "der Pfad für die PDF-Bibliothek konnte nicht gefunden werden");
            }
            path = urlPdfLibInClassPath.getFile();
          }

          PDDocument document = null;
          try
          {

            document = PDDocument.load(new File(PDF_BASE_PATH + path + "/" + conf.pdfName));
            DsaPdfImageExtractorMain printer = new DsaPdfImageExtractorMain();
            currentPage.set(0);
            for (PDPage page : document.getPages())
            {
              currentPage.set(currentPage.get() + 1);
              printer.processPage(page);
            }
          }
          catch (IOException e)
          {
            throw new RuntimeException(e);
          }
          finally
          {
            if (document != null)
            {
              try
              {
                document.close();
              }
              catch (IOException e)
              {
                throw new RuntimeException(e);
              }
            }
          }
        });
  }

  /**
   * @param operator The operation to perform.
   * @param operands The list of arguments.
   * @throws IOException If there is an error processing the operation.
   */
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

          // same image to local
          BufferedImage bImage = image.getImage();
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ImageIO.write(bImage, "PNG", baos);
          byte[] bytes = baos.toByteArray();

          MessageDigest md = MessageDigest.getInstance("MD5");
          md.update(bytes);
          byte[] digest = md.digest();
          String myChecksum = DatatypeConverter
              .printHexBinary(digest).toUpperCase();

          if (!checksums.contains(myChecksum))
          {
            checksums.add(myChecksum);
            String pathname = "export/images/" + currentPublication + "_" + currentPage.get() + "_image_" + imageNumber + ".png";

            File output = new File(pathname);
            // Ensure the parent directory exists File
            File parentDir = output.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
              parentDir.mkdirs();
            }

            ImageIO.write(bImage, "PNG", output);
            imageNumber++;
          }


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

}
