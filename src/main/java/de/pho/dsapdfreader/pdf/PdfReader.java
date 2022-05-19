package de.pho.dsapdfreader.pdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class PdfReader
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static ArrayList<TextWithMetaInfo> convertToText(File f, int fromPage, int untilPage, String publication) throws IOException
    {
        LOGGER.debug("init PDFParser");
        PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
        LOGGER.debug("parse PDF");
        parser.parse();
        LOGGER.debug("retrieve COSDocument");
        COSDocument cosDoc = parser.getDocument();
        LOGGER.debug("strip PDF");
        GeneralParser generalParser = new GeneralParser(publication);
        LOGGER.debug("generate PDDocument");
        PDDocument pdDoc = new PDDocument(cosDoc);
        LOGGER.debug("reduce to pages");
        pdDoc = extractPages(pdDoc, fromPage, untilPage);
        LOGGER.debug("extract text");
        generalParser.getText(pdDoc);
        cosDoc.close();
        return generalParser.resultTexts;
    }

    public static PDDocument extractPages(PDDocument pdDoc, int from, int until)
    {
        PDDocument returnValue = new PDDocument();
        Iterable<PDPage> iterable = () -> pdDoc.getPages().iterator();

        AtomicInteger firstPage = new AtomicInteger();
        StreamSupport.stream(iterable.spliterator(), false)
            .filter(p -> firstPage.addAndGet(1) > from && firstPage.get() <= until + 1)
            .forEach(p -> returnValue.addPage(p));
        return returnValue;
    }
}
