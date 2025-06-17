package de.pho.dsapdfreader.pdf;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;
import de.pho.dsapdfreader.tools.csv.DsaStringCleanupTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PdfReader2
{
    private static final Logger LOGGER = LogManager.getLogger();

    private PdfReader2()
    {
    }

    public static List<TextWithMetaInfo> convertToText(File f, TopicConfiguration conf) throws IOException
    {
        LOGGER.debug("init PDFParser");
        PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
        LOGGER.debug("parse PDF");
        parser.parse();
        LOGGER.debug("retrieve COSDocument");
        COSDocument cosDoc = parser.getDocument();
        LOGGER.debug("strip PDF");
        DsaPDFTextStripper2 generalParser = new DsaPDFTextStripper2(conf.publication, conf.topic);
        LOGGER.debug("generate PDDocument");
        PDDocument pdDoc = new PDDocument(cosDoc);

      LOGGER.debug("reduce to pages");
      generalParser.setStartPage(conf.startPage);
      generalParser.setEndPage(conf.endPage);
      LOGGER.debug("extract text");

      generalParser.getText(pdDoc);

      pdDoc.close();
      cosDoc.close();

      return generalParser.resultTexts.stream().map(t -> {
        t.text = DsaStringCleanupTool.cleanupString(t.text.replace("\u00AD", "-"));
        return t;
      }).collect(Collectors.toList());
    }
}
