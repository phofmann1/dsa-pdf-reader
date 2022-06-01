package de.pho.dsapdfreader.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaPDFTextStripper extends PDFTextStripper
{
    private final String publication;

    public List<TextWithMetaInfo> resultTexts;
    public Map<Integer, List<TextWithMetaInfo>> resultTextPerPage;


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
        this.resultTextPerPage = new LinkedHashMap<>();
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
        }

    }

    private void buildResultTexts()
    {
        List<TextWithMetaInfo> localResultTexts = new ArrayList<>();
        StringBuilder b = new StringBuilder();

        AtomicBoolean wasBold = new AtomicBoolean(false);
        AtomicBoolean wasItalic = new AtomicBoolean(false);
        AtomicInteger wasSize = new AtomicInteger(0);
        StringBuilder wasFont = new StringBuilder();

        charactersByArticle.forEach(a -> a.forEach(tp -> {

            boolean isBold = tp.getFont().toString().contains("Bold") || tp.getFont().equals("PDType0Font/PDCIDFontType2, PostScript name: HPQTGU+GentiumBasic");
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
                    text = text.replaceAll(";", "<br>");

                    localResultTexts.add(new TextWithMetaInfo(
                        text,
                        wasBold.get(),
                        wasItalic.get(),
                        wasSize.get(),
                        wasFont.toString(),
                        this.getCurrentPageNo()
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
        this.resultTexts.addAll(localResultTexts);
        this.resultTextPerPage.put(this.getCurrentPageNo(), localResultTexts);
    }

}

    

