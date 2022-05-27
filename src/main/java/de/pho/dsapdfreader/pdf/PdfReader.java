package de.pho.dsapdfreader.pdf;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class PdfReader
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static Map<Integer, List<TextWithMetaInfo>> convertToText(File f, TopicConfiguration conf) throws IOException
    {
        LOGGER.debug("init PDFParser");
        PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
        LOGGER.debug("parse PDF");
        parser.parse();
        LOGGER.debug("retrieve COSDocument");
        COSDocument cosDoc = parser.getDocument();
        LOGGER.debug("strip PDF");
        DsaPDFTextStripper generalParser = new DsaPDFTextStripper(conf.publication);
        LOGGER.debug("generate PDDocument");
        PDDocument pdDoc = new PDDocument(cosDoc);

        LOGGER.debug("reduce to pages");
        generalParser.setStartPage(conf.startPage);
        generalParser.setEndPage(conf.endPage);
        LOGGER.debug("extract text");

        generalParser.getText(pdDoc);

        pdDoc.close();
        cosDoc.close();

        Map<Integer, List<TextWithMetaInfo>> returnValue = sortResultTextByPage(generalParser.resultTextPerPage);

        return returnValue;
    }

    private static Map<Integer, List<TextWithMetaInfo>> sortResultTextByPage(Map<Integer, List<TextWithMetaInfo>> resultTextPerPage)
    {
        Map<Integer, List<TextWithMetaInfo>> returnValue = new LinkedHashMap<>();

        List<Integer> sortedKeys = resultTextPerPage.keySet().stream().sorted().collect(Collectors.toList());

        for (Integer key : sortedKeys)
        {
            returnValue.put(key, resultTextPerPage.get(key));
        }

        return returnValue;
    }
}
