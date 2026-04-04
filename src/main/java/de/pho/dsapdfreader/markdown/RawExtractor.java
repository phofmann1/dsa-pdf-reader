package de.pho.dsapdfreader.markdown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * Stufe 0: Extrahiert Rohdaten (Zeichenpositionen + Metadaten) aus einem PDF.
 * Produziert RawPageData-Objekte die als JSON gespeichert werden.
 * Liest alle Informationen aus dem PDF die spaeter fuer die Interpretation
 * benoetigt werden: Position, Groesse, Font, Bold/Italic.
 */
public class RawExtractor extends PDFTextStripper
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final List<RawPageData.RawChar> currentPageChars = new ArrayList<>();
    private final List<RawPageData> pages = new ArrayList<>();
    private float currentPageWidth;
    private float currentPageHeight;

    public RawExtractor() throws IOException
    {
        super();
        setSortByPosition(false); // Original-Reihenfolge beibehalten
    }

    /**
     * Extrahiert Rohdaten aus dem gesamten Dokument.
     */
    public List<RawPageData> extract(PDDocument document) throws IOException
    {
        pages.clear();
        setStartPage(1);
        setEndPage(document.getNumberOfPages());
        getText(document); // Triggert writeString/startPage/endPage Callbacks
        return pages;
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException
    {
        for (TextPosition tp : textPositions)
        {
            if (tp.getUnicode() == null || tp.getUnicode().isEmpty()) continue;

            String fontName = tp.getFont().getName() != null
                ? tp.getFont().getName()
                : tp.getFont().toString();

            // Bold-Erkennung: Font-Name UND Font-Descriptor pruefen
            boolean isBold = fontName.contains("Bold") || fontName.contains("bold");
            // Manche PDFs markieren Bold ueber den Font-Descriptor statt den Namen
            if (!isBold && tp.getFont().getFontDescriptor() != null)
            {
                isBold = tp.getFont().getFontDescriptor().isForceBold()
                    || (tp.getFont().getFontDescriptor().getFontWeight() >= 700);
            }

            boolean isItalic = fontName.contains("Italic") || fontName.contains("italic")
                || fontName.contains("Oblique") || fontName.contains("oblique");
            if (!isItalic && tp.getFont().getFontDescriptor() != null)
            {
                isItalic = tp.getFont().getFontDescriptor().isItalic()
                    || (tp.getFont().getFontDescriptor().getItalicAngle() != 0);
            }

            currentPageChars.add(new RawPageData.RawChar(
                tp.getUnicode(),
                tp.getXDirAdj(),
                tp.getYDirAdj(),
                tp.getWidthDirAdj(),
                tp.getHeightDir(),
                tp.getFontSizeInPt(),
                fontName,
                isBold,
                isItalic
            ));
        }
    }

    @Override
    protected void startPage(PDPage page) throws IOException
    {
        super.startPage(page);
        currentPageChars.clear();
        currentPageWidth = page.getMediaBox().getWidth();
        currentPageHeight = page.getMediaBox().getHeight();
    }

    @Override
    protected void endPage(PDPage page) throws IOException
    {
        super.endPage(page);
        pages.add(new RawPageData(
            getCurrentPageNo(),
            currentPageWidth,
            currentPageHeight,
            new ArrayList<>(currentPageChars)
        ));
    }
}
