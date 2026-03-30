package de.pho.dsapdfreader.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaPDFTextStripper extends PDFTextStripper
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final String publication;
    private final TopicEnum topic;
    private final Set<String> loggedFonts = new HashSet<>();

    public List<TextWithMetaInfo> resultTexts;


    /**
     * Instantiate a new PDFTextStripper object.
     *
     * @throws IOException If there is an error loading the properties.
     */
    public DsaPDFTextStripper(String publication, TopicEnum topic) throws IOException
    {
        super();
        this.publication = publication;
        this.topic = topic;
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

        AtomicInteger currentLine = new AtomicInteger(1);
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
                    text = text.replaceAll(";", "<br>")
                        .replaceAll("\\…", "...");

                    localResultTexts.add(new TextWithMetaInfo(
                        text,
                        wasBold.get(),
                        wasItalic.get(),
                        wasSize.get(),
                        wasFont.toString(),
                        this.getCurrentPageNo(),
                        currentLine.getAndIncrement(),
                        publication
                    ));
                }
                b.setLength(0);
                wasFont.setLength(0);
            }

            String unicode = tp.getUnicode();

            // Debug: Log fonts and problematic characters
            if (!loggedFonts.contains(font))
            {
                loggedFonts.add(font);
                LOGGER.warn("[FONT-DEBUG] Page {}: New font encountered: {}", this.getCurrentPageNo(), font);
            }

            if (unicode == null || unicode.isEmpty())
            {
                int[] codes = tp.getCharacterCodes();
                String codeStr = codes != null && codes.length > 0 ? String.valueOf(codes[0]) : "unknown";
                LOGGER.warn("[FONT-DEBUG] Page {}: Unrecognized glyph (null/empty unicode) in font [{}], glyph code: {}", this.getCurrentPageNo(), font, codeStr);
            }
            else
            {
                for (char c : unicode.toCharArray())
                {
                    if (c == '\uFFFD' || c == '\u0000' || (Character.getType(c) == Character.UNASSIGNED))
                    {
                        int[] codes = tp.getCharacterCodes();
                        String codeStr = codes != null && codes.length > 0 ? String.valueOf(codes[0]) : "unknown";
                        LOGGER.warn("[FONT-DEBUG] Page {}: Suspicious character U+{} in font [{}], glyph code: {}, surrounding text so far: [{}]",
                            this.getCurrentPageNo(),
                            String.format("%04X", (int) c),
                            font,
                            codeStr,
                            b.toString().substring(Math.max(0, b.length() - 20)));
                    }
                }
            }

            b.append(unicode);

            wasBold.set(isBold);
            wasItalic.set(isItalic);
            wasSize.set(size);
            wasFont.setLength(0);
            wasFont.append(font);
        }));
        this.resultTexts.addAll(localResultTexts);
    }

}

    

