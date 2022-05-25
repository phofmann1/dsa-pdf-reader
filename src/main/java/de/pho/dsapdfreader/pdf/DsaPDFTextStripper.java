package de.pho.dsapdfreader.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaPDFTextStripper extends PDFTextStripper
{
    static final Logger LOGGER_ANALYSE = LogManager.getLogger("analyseLogger");
    private final String publication;

    public ArrayList<TextWithMetaInfo> resultTexts;


    /**
     * Instantiate a new PDFTextStripper object.
     *
     * @throws IOException If there is an error loading the properties.
     */
    public DsaPDFTextStripper(String publication) throws IOException
    {
        super();
        this.publication = publication;
        this.resultTexts = new ArrayList<>();
        // see https://pdfbox.apache.org/2.0/getting-started.html
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    }


    @Override
    public void processPage(PDPage page) throws IOException
    {
        if (this.getCurrentPageNo() >= this.getStartPage()
            && this.getCurrentPageNo() <= this.getEndPage())
        {

            int numberOfArticleSections = 1;

            int originalSize = this.charactersByArticle.size();
            this.charactersByArticle.ensureCapacity(numberOfArticleSections);
            int lastIndex = Math.max(numberOfArticleSections, originalSize);

            for (int i = 0; i < lastIndex; ++i)
            {
                if (i < originalSize)
                {
                    this.charactersByArticle.get(i).clear();
                } else
                {
                    //noinspection unchecked,rawtypes
                    this.charactersByArticle.add(new ArrayList());
                }

                super.processPage(page);
                super.writePage();
                super.endPage(page);
            }

            // convert content to result text model
            this.buildResultTexts();

            // export analysis log
            this.logAnalysis();
        }

    }

    private void buildResultTexts()
    {
        StringBuilder b = new StringBuilder();

        AtomicBoolean wasBold = new AtomicBoolean(false);
        AtomicBoolean wasItalic = new AtomicBoolean(false);
        AtomicInteger wasSize = new AtomicInteger(0);
        StringBuilder wasFont = new StringBuilder();

        charactersByArticle.forEach(a -> a.forEach(tp -> {

            boolean isBold = tp.getFont().toString().contains("Bold");
            boolean isItalic = tp.getFont().toString().contains("Italic");
            int size = (int) (tp.getFontSizeInPt() * 100);
            String font = tp.getFont().toString();

            boolean boldChanged = isBold != wasBold.get();
            boolean italicChanged = isItalic != wasItalic.get();
            boolean sizeChanged = size != wasSize.get();
            boolean fontChanged = !font.equalsIgnoreCase(wasFont.toString());

            if (boldChanged || italicChanged || sizeChanged || fontChanged)
            {
                String text = b.toString();
                if (!text.isEmpty())
                {
                    text = wasBold.get()
                        ? text.replaceAll(":", "").trim()
                        : text;

                    resultTexts.add(new TextWithMetaInfo(
                        text,
                        wasBold.get(),
                        wasItalic.get(),
                        wasSize.get(),
                        wasFont.toString()
                    ));
                }
                b.setLength(0);
                wasFont.setLength(0);
            }

            b.append(tp.getUnicode());

            wasBold.set(isBold);
            wasItalic.set(isItalic);
            wasSize.set(size);
            wasFont.setLength(0);
            wasFont.append(font);
        }));
    }

    private void logAnalysis()
    {
        this.resultTexts.forEach(t -> LOGGER_ANALYSE.info(
            publication + ";"
                + convertBooleanForExcel(t.isBold) + ";"
                + convertBooleanForExcel(t.isItalic) + ";"
                + t.size + ";"
                + t.font + ";"
                + t.text));
    }

    public String convertBooleanForExcel(boolean b)
    {
        return b ? "WAHR" : "FALSCH";
    }
}

    

