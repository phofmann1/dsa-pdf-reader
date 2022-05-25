package de.pho.dsapdfreader.pdf;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;

import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class PdfReader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Logger LOGGER_ANALYSE = LogManager.getLogger("analyseLogger");

    public static List<TextWithMetaInfo> convertToText(File f, int startPage, int endPage, String publication) throws IOException
    {
        LOGGER.debug("init PDFParser");
        PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
        LOGGER.debug("parse PDF");
        parser.parse();
        LOGGER.debug("retrieve COSDocument");
        COSDocument cosDoc = parser.getDocument();
        LOGGER.debug("strip PDF");
        DsaPDFTextStripper generalParser = new DsaPDFTextStripper(publication);
        LOGGER.debug("generate PDDocument");
        PDDocument pdDoc = new PDDocument(cosDoc);

        LOGGER.debug("reduce to pages");
        generalParser.setStartPage(startPage);
        generalParser.setEndPage(endPage);
        LOGGER.debug("extract text");

        generalParser.getText(pdDoc);

        pdDoc.close();
        cosDoc.close();

        logAnalysisForPage(publication, generalParser.resultTextPerPage);

        return generalParser.resultTexts;
    }

    private static void logAnalysisForPage(String publication, Map<Integer, List<TextWithMetaInfo>> resultTextPerPage)
    {
        LOGGER_ANALYSE.info("publication;page;isBold;isItalic;size;font;text");
        resultTextPerPage.forEach((k, v) -> logAnalysis(publication, v, k.intValue()));
    }


    private static void logAnalysis(String publication, List<TextWithMetaInfo> resultTexts, int... page)
    {
        String pageString = page == null ? "" : page[0] + ";";
        if (page == null)
            LOGGER_ANALYSE.info("publication;isBold;isItalic;size;font;text");

        resultTexts.forEach(t -> LOGGER_ANALYSE.info(
            publication + ";"
                + pageString
                + convertBooleanForExcel(t.isBold) + ";"
                + convertBooleanForExcel(t.isItalic) + ";"
                + t.size + ";"
                + t.font + ";"
                + t.text));
    }


    private static String convertBooleanForExcel(boolean b)
    {
        return b ? "WAHR" : "FALSCH";
    }


}
