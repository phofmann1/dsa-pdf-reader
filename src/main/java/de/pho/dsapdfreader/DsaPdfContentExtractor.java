package de.pho.dsapdfreader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.ConfigurationInitializer;
import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicContentEnum;
import de.pho.dsapdfreader.pdf.PdfReader;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;
import de.pho.dsapdfreader.tools.csv.CsvHandler;

public class DsaPdfContentExtractor {
  private static final String PATH_BASE = "C:\\develop\\project\\dsa-pdf-reader\\export\\content\\";

  private static final String PATH_PDF_2_TEXT = PATH_BASE + "01 - pdf2text\\";
  private static final String FILE_PDF_2_TEXT = PATH_PDF_2_TEXT + "%s_txt.csv";

  private static final AtomicReference<TopicContentEnum> currentTopic = new AtomicReference<>();
  private static final AtomicReference<String> currentPublication = new AtomicReference<>();
  private static final AtomicReference<Integer> currentPage = new AtomicReference<>();
  private static final Logger LOGGER = LogManager.getLogger();
  private static List<TopicConfiguration> configs = null;

  public static void main(String[] args) {
    configs = ConfigurationInitializer.readTopicContentConfigurations();
    configs.stream()
        .filter(c -> {
          boolean r = c != null && c.active;
          System.out.println(r);
          System.out.println(c.active);
          System.out.println(c);
          return r;
        })
        .forEach(conf -> {
          System.out.println(conf);
          String msg = String.format("Config PDF Content verarbeiten: %s (%s)", conf.publication, conf.topic);
          LOGGER.info(msg);
          List<TextWithMetaInfo> pdfResults;
          try {
            pdfResults = extractPdfResults(conf);

            LOGGER.debug("export Texts");
            final List<TextWithMetaInfo> texts = pdfResults.stream()
                .filter(t -> {
                      boolean isStartPage = t.onPage == conf.startPage;
                      boolean isNormalPage = t.onPage > conf.startPage && t.onPage < conf.endPage;
                      boolean isEndPage = t.onPage == conf.endPage;
                      boolean isStartPageValidLine = isStartPage && isEndPage
                          ? (t.onLine > conf.startAfterLine || conf.startAfterLine == 0) && (t.onLine <= conf.endAfterLine || conf.endAfterLine == 0)
                          : isStartPage && (t.onLine > conf.startAfterLine || conf.startAfterLine == 0);
                      boolean isEndPageValidLine = isStartPage && isEndPage
                          ? isStartPageValidLine
                          : isEndPage && (t.onLine <= conf.endAfterLine || conf.endAfterLine == 0);
                      return isStartPageValidLine || isNormalPage || isEndPageValidLine;
                    }
                )
                .sorted((a, b) -> a.sortIndex() < b.sortIndex() ? -1 : 1)
                .collect(Collectors.toList());

            File fOut = new File(generateFileName(FILE_PDF_2_TEXT, conf));
            CsvHandler.writeBeanToUrl(fOut, texts);
          }
          catch (IOException | NullPointerException e) {
            LOGGER.error(e.getMessage(), e);
          }
        });
  }

  private static List<TextWithMetaInfo> extractPdfResults(TopicConfiguration conf) throws IOException {
    List<TextWithMetaInfo> returnValue = new ArrayList<>();

    String path = conf.path;
    if (path == null) {
      URL urlPdfLibInClassPath = DsaPdfReaderMain.class.getClassLoader().getResource("./pdf_lib");
      if (urlPdfLibInClassPath == null) {
        throw new InvalidPathException("./pdf_lib", "der Pfad für die PDF-Bibliothek konnte nicht gefunden werden");
      }
      path = urlPdfLibInClassPath.getFile();
    }
    File fIn = new File(path + "/" + conf.pdfName);
    returnValue.addAll(PdfReader.convertToText(fIn, conf));
    return returnValue;

  }

  private static String generateFileName(String filePattern, TopicConfiguration conf) {
    String appendToFileName = (conf.fileAffix == null || conf.fileAffix.isEmpty()) ? "" : "_" + conf.fileAffix;
    return String.format(filePattern, conf.publication + "_" + conf.topic + appendToFileName);
  }
}
