package de.pho.dsapdfreader.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * PDF-Text-Extraktor fuer Markdown-Pipeline.
 * Erkennt Spalten-Layouts ueber X-Positions-Histogramm und trackt
 * Textformatierung (bold, italic, size). Gibt Markdown aus.
 */
public class MarkdownPDFTextStripper extends PDFTextStripper
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final List<CharInfo> pageChars = new ArrayList<>();
    private final List<PageResult> pageResults = new ArrayList<>();

    // Zeilenabstand-Toleranz in PDF-Punkten
    private static final float LINE_Y_TOLERANCE = 2.0f;

    // Histogramm-Bin-Breite in PDF-Punkten
    private static final float BIN_WIDTH = 2.0f;

    // Minimale Breite einer leeren Zone um als Spaltengrenze zu gelten
    private static final int MIN_GAP_BINS = 5; // = 10pt

    public MarkdownPDFTextStripper() throws IOException
    {
        super();
        setSortByPosition(false);
    }

    public List<PageResult> getPageResults()
    {
        return pageResults;
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException
    {
        for (TextPosition tp : textPositions)
        {
            if (tp.getUnicode() == null || tp.getUnicode().isEmpty()) continue;

            String fontName = tp.getFont().getName() != null ? tp.getFont().getName() : tp.getFont().toString();
            boolean isBold = fontName.contains("Bold") || fontName.contains("bold");
            boolean isItalic = fontName.contains("Italic") || fontName.contains("italic")
                || fontName.contains("Oblique") || fontName.contains("oblique");

            pageChars.add(new CharInfo(
                tp.getUnicode(),
                tp.getXDirAdj(),
                tp.getYDirAdj(),
                tp.getWidthDirAdj(),
                isBold,
                isItalic,
                tp.getFontSizeInPt(),
                fontName
            ));
        }
    }

    @Override
    protected void startPage(PDPage page) throws IOException
    {
        super.startPage(page);
        pageChars.clear();
    }

    @Override
    protected void endPage(PDPage page) throws IOException
    {
        super.endPage(page);

        if (pageChars.isEmpty())
        {
            pageResults.add(new PageResult(getCurrentPageNo(), "", new ArrayList<>()));
            return;
        }

        float pageWidth = page.getMediaBox().getWidth();

        // 1. Spaltengrenze per Histogramm finden
        float splitX = findColumnSplitByHistogram(pageChars, pageWidth);

        // 2. Zeichen in Spalten aufteilen, dann pro Spalte Zeilen bilden
        List<List<TextLine>> columns;
        if (splitX > 0)
        {
            List<CharInfo> leftChars = new ArrayList<>();
            List<CharInfo> rightChars = new ArrayList<>();
            for (CharInfo ch : pageChars)
            {
                if (ch.x + ch.width / 2 < splitX)
                {
                    leftChars.add(ch);
                }
                else
                {
                    rightChars.add(ch);
                }
            }

            List<TextLine> leftLines = buildLines(leftChars);
            List<TextLine> rightLines = buildLines(rightChars);

            columns = new ArrayList<>();
            if (!leftLines.isEmpty()) columns.add(leftLines);
            if (!rightLines.isEmpty()) columns.add(rightLines);
            if (columns.isEmpty()) columns.add(buildLines(pageChars));
        }
        else
        {
            columns = List.of(buildLines(pageChars));
        }

        // 3. Markdown-Output
        StringBuilder markdown = new StringBuilder();
        List<FormattedSpan> spans = new ArrayList<>();

        for (int colIdx = 0; colIdx < columns.size(); colIdx++)
        {
            if (columns.size() > 1)
            {
                markdown.append("<!-- Spalte ").append(colIdx + 1).append(" -->\n");
            }

            List<TextLine> colLines = columns.get(colIdx);
            colLines.sort(Comparator.comparingDouble(l -> l.y));

            for (TextLine line : colLines)
            {
                String markdownLine = lineToMarkdown(line, spans);
                markdown.append(markdownLine).append("\n");
            }

            if (columns.size() > 1 && colIdx < columns.size() - 1)
            {
                markdown.append("\n");
            }
        }

        // Watermark und Seitenzahlen entfernen
        String cleaned = markdown.toString();
        // "Peter Hofmann (Order #nnnnn)" entfernen
        cleaned = cleaned.replaceAll("(?m)^.*Peter Hofmann \\(Order #\\d+\\).*\n?", "");
        // Alleinstehende Seitenzahlen entfernen (Zeile die nur eine Zahl enthaelt)
        cleaned = cleaned.replaceAll("(?m)^\\d{1,3}\\s*\n", "");

        pageResults.add(new PageResult(getCurrentPageNo(), cleaned, spans));
    }

    /**
     * Findet die Spaltengrenze ueber ein Histogramm der X-Positionen.
     * Baut ein Histogramm aller Zeichen-X-Positionen auf und sucht
     * im mittleren Drittel der Seite nach einer leeren Zone.
     *
     * @return splitX > 0 wenn Zweispalter erkannt, sonst -1
     */
    private float findColumnSplitByHistogram(List<CharInfo> chars, float pageWidth)
    {
        int numBins = (int) Math.ceil(pageWidth / BIN_WIDTH);
        int[] histogram = new int[numBins];

        for (CharInfo ch : chars)
        {
            // Jedes Zeichen belegt einen Bereich von x bis x+width
            int startBin = Math.max(0, (int) (ch.x / BIN_WIDTH));
            int endBin = Math.min(numBins - 1, (int) ((ch.x + ch.width) / BIN_WIDTH));
            for (int b = startBin; b <= endBin; b++)
            {
                histogram[b]++;
            }
        }

        // Suche im mittleren Drittel nach der breitesten leeren/fast-leeren Zone
        int searchStart = numBins / 3;
        int searchEnd = numBins * 2 / 3;

        // Schwellwert: Ein Bin gilt als "leer" wenn er weniger als 2 Zeichen hat
        int emptyThreshold = 2;

        int bestGapStart = -1;
        int bestGapEnd = -1;
        int bestGapWidth = 0;

        int gapStart = -1;
        for (int b = searchStart; b <= searchEnd; b++)
        {
            if (histogram[b] <= emptyThreshold)
            {
                if (gapStart < 0) gapStart = b;
            }
            else
            {
                if (gapStart >= 0)
                {
                    int gapWidth = b - gapStart;
                    if (gapWidth > bestGapWidth)
                    {
                        bestGapWidth = gapWidth;
                        bestGapStart = gapStart;
                        bestGapEnd = b;
                    }
                }
                gapStart = -1;
            }
        }
        // Abschluss-Check falls Gap bis zum Ende des Suchbereichs reicht
        if (gapStart >= 0)
        {
            int gapWidth = searchEnd - gapStart;
            if (gapWidth > bestGapWidth)
            {
                bestGapWidth = gapWidth;
                bestGapStart = gapStart;
                bestGapEnd = searchEnd;
            }
        }

        if (bestGapWidth >= MIN_GAP_BINS)
        {
            float split = (bestGapStart + bestGapEnd) / 2.0f * BIN_WIDTH;
            LOGGER.debug("  Seite {}: Zweispalter erkannt, Split bei x={} (Gap {}pt)",
                getCurrentPageNo(), split, bestGapWidth * BIN_WIDTH);
            return split;
        }

        return -1; // Einspaltig
    }

    /**
     * Gruppiert Zeichen zu Zeilen basierend auf Y-Position.
     */
    private List<TextLine> buildLines(List<CharInfo> chars)
    {
        if (chars.isEmpty()) return new ArrayList<>();

        List<CharInfo> sorted = new ArrayList<>(chars);
        sorted.sort(Comparator.comparingDouble((CharInfo c) -> c.y).thenComparingDouble(c -> c.x));

        List<TextLine> lines = new ArrayList<>();
        TextLine currentLine = null;

        for (CharInfo ch : sorted)
        {
            if (currentLine == null || Math.abs(ch.y - currentLine.y) > LINE_Y_TOLERANCE)
            {
                currentLine = new TextLine(ch.y);
                lines.add(currentLine);
            }
            currentLine.chars.add(ch);
            currentLine.y = (currentLine.y * (currentLine.chars.size() - 1) + ch.y) / currentLine.chars.size();
        }

        for (TextLine line : lines)
        {
            line.chars.sort(Comparator.comparingDouble(c -> c.x));
            if (!line.chars.isEmpty())
            {
                line.xMin = line.chars.get(0).x;
                line.xMax = line.chars.get(line.chars.size() - 1).x
                    + line.chars.get(line.chars.size() - 1).width;
            }
        }

        return lines;
    }

    /**
     * Konvertiert eine Textzeile in Markdown mit Formatierungsmarkup.
     */
    private String lineToMarkdown(TextLine line, List<FormattedSpan> allSpans)
    {
        if (line.chars.isEmpty()) return "";

        List<FormattedSpan> lineSpans = new ArrayList<>();
        FormattedSpan currentSpan = null;

        for (CharInfo ch : line.chars)
        {
            boolean newSpan = currentSpan == null
                || ch.isBold != currentSpan.isBold
                || ch.isItalic != currentSpan.isItalic
                || Math.abs(ch.fontSize - currentSpan.fontSize) > 0.5f;

            if (currentSpan != null && !currentSpan.chars.isEmpty())
            {
                CharInfo lastChar = currentSpan.chars.get(currentSpan.chars.size() - 1);
                float gap = ch.x - (lastChar.x + lastChar.width);
                if (gap > lastChar.width * 0.3f)
                {
                    currentSpan.text.append(" ");
                }
            }

            if (newSpan)
            {
                currentSpan = new FormattedSpan(ch.isBold, ch.isItalic, ch.fontSize, ch.fontName);
                lineSpans.add(currentSpan);
            }
            currentSpan.chars.add(ch);
            currentSpan.text.append(ch.unicode);
        }

        allSpans.addAll(lineSpans);

        // Heading-Erkennung
        StringBuilder result = new StringBuilder();
        float dominantSize = getDominantFontSize(lineSpans);
        int totalChars = lineSpans.stream().mapToInt(s -> s.text.toString().trim().length()).sum();
        int boldChars = lineSpans.stream().filter(s -> s.isBold)
            .mapToInt(s -> s.text.toString().trim().length()).sum();
        boolean allBold = totalChars > 0 && boldChars >= totalChars * 0.9;
        boolean isShortLine = totalChars < 60;

        boolean isHeading = dominantSize > 10.5f && allBold && isShortLine;
        boolean isSubHeading = dominantSize > 8.5f && dominantSize <= 10.5f && allBold && isShortLine;

        if (isHeading)
        {
            result.append("## ");
        }
        else if (isSubHeading)
        {
            result.append("### ");
        }

        for (FormattedSpan span : lineSpans)
        {
            String text = span.text.toString().trim();
            if (text.isEmpty()) continue;

            if (span.isBold && span.isItalic)
            {
                result.append("***").append(text).append("*** ");
            }
            else if (span.isBold)
            {
                if (!isHeading && !isSubHeading)
                {
                    result.append("**").append(text).append("** ");
                }
                else
                {
                    result.append(text).append(" ");
                }
            }
            else if (span.isItalic)
            {
                result.append("*").append(text).append("* ");
            }
            else
            {
                result.append(text).append(" ");
            }
        }

        return result.toString().stripTrailing();
    }

    private float getDominantFontSize(List<FormattedSpan> spans)
    {
        float maxLen = 0;
        float dominantSize = 0;
        for (FormattedSpan span : spans)
        {
            if (span.text.length() > maxLen)
            {
                maxLen = span.text.length();
                dominantSize = span.fontSize;
            }
        }
        return dominantSize;
    }

    // --- Datenklassen ---

    static class CharInfo
    {
        final String unicode;
        final float x;
        final float y;
        final float width;
        final boolean isBold;
        final boolean isItalic;
        final float fontSize;
        final String fontName;

        CharInfo(String unicode, float x, float y, float width,
                 boolean isBold, boolean isItalic, float fontSize, String fontName)
        {
            this.unicode = unicode;
            this.x = x;
            this.y = y;
            this.width = width;
            this.isBold = isBold;
            this.isItalic = isItalic;
            this.fontSize = fontSize;
            this.fontName = fontName;
        }
    }

    static class TextLine
    {
        double y;
        float xMin;
        float xMax;
        final List<CharInfo> chars = new ArrayList<>();

        TextLine(double y)
        {
            this.y = y;
        }
    }

    public static class FormattedSpan
    {
        public final boolean isBold;
        public final boolean isItalic;
        public final float fontSize;
        public final String fontName;
        public final StringBuilder text = new StringBuilder();
        final List<CharInfo> chars = new ArrayList<>();

        FormattedSpan(boolean isBold, boolean isItalic, float fontSize, String fontName)
        {
            this.isBold = isBold;
            this.isItalic = isItalic;
            this.fontSize = fontSize;
            this.fontName = fontName;
        }
    }

    public static class PageResult
    {
        public final int pageNumber;
        public final String markdownText;
        public final List<FormattedSpan> spans;

        PageResult(int pageNumber, String markdownText, List<FormattedSpan> spans)
        {
            this.pageNumber = pageNumber;
            this.markdownText = markdownText;
            this.spans = spans;
        }
    }
}
