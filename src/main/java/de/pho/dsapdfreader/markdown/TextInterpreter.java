package de.pho.dsapdfreader.markdown;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Stufe 1: Interpretiert Rohdaten (RawPageData) zu strukturiertem Markdown.
 *
 * Verbesserte Schriftarten-Erkennung:
 * - Font-Family-Wechsel erzwingt neue Spans
 * - SC700/Weight-Indikatoren im Font-Namen = Bold
 * - SmallCaps-Fonts = optisch Bold/Heading
 * - Grosse Fonts (>15pt) = Headings/Initiale
 * - Dekorative Initialen (>30pt) werden erkannt und dem Folgetext zugeordnet
 */
public class TextInterpreter
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final float LINE_Y_TOLERANCE = 2.0f;
    private static final float BIN_WIDTH = 2.0f;
    private static final int MIN_GAP_BINS = 5;

    // Tabellenerkennung
    // 18pt: oberhalb typischer Typesetting-Variationen (Bold/Regular-Wechsel um ~15pt),
    // unterhalb der typischen Tabellen-Spalten-Gaps (>20pt).
    private static final float GAP_THRESHOLD = 18.0f;
    private static final float GAP_THRESHOLD_IN_TABLE = 5.0f;
    private static final float GAP_POS_TOLERANCE = 30.0f;
    private static final int MIN_TABLE_ROWS = 2;

    // Einrueckungserkennung
    private static final float INDENT_BUCKET_SIZE = 3.0f;
    private static final float INDENT_TOLERANCE = 5.0f;

    // Bullet-Icons: Position (x, y) fuer Spalten-Zuordnung
    static class BulletIcon
    {
        final float x, y, width, height;
        BulletIcon(float x, float y) { this.x = x; this.y = y; this.width = 0; this.height = 0; }
        BulletIcon(float x, float y, float width, float height) { this.x = x; this.y = y; this.width = width; this.height = height; }
    }

    // Ornament-Schriftarten: rein dekorative Zeichen ohne Textinhalt
    private static final java.util.Set<String> ORNAMENT_FONTS = java.util.Set.of(
        "TypeEmbellishmentsOne",
        "PaganSymbols"
    );

    // Toggle: Body-Text in Headings-Ausgabe einschliessen
    private boolean includeBodyText = false;
    // Toggle: Bilder als ![image]() in Ausgabe einbinden
    private boolean includeImages = false;
    // Toggle: Ornament-Zeichen mit ausgeben (in <span> gewrappt)
    private boolean includeOrnaments = false;
    // Toggle: Boxen inline im Body rendern (true = altes Verhalten),
    // false = nur in lastBoxes sammeln, Body bleibt frei von Box-Inhalten.
    private boolean emitBoxesInline = true;

    // Box-Render-Ergebnisse der letzten interpretPage-Ausfuehrung.
    // Wird vor jedem Aufruf zurueckgesetzt; Caller kann sie nach interpretPage
    // auslesen, um Boxen als separate MD-Dateien zu schreiben.
    private final List<BoxRendering> lastBoxes = new ArrayList<>();

    /** Ergebnis einer gerenderten Box (fuer Auslagerung in eigene MD-Datei). */
    public static final class BoxRendering
    {
        public final int index;          // 1-basiert, in Y-Reihenfolge auf der Seite
        public final int pageNumber;
        public final String headingGuess; // erste Heading-/Bold-Zeile, gesaeubert; ggf. ""
        public final String markdown;     // Box-Inhalt als MD (ohne Anfang/Ende-Marker)
        public final float x, y, width, height;
        /** True wenn der Heading aus einem Auge-Symbol-Bezug stammt (nicht aus Box-Inhalt). */
        public final boolean headingFromEyeRef;

        public BoxRendering(int index, int pageNumber, String headingGuess, String markdown,
                            float x, float y, float width, float height)
        {
            this(index, pageNumber, headingGuess, markdown, x, y, width, height, false);
        }

        public BoxRendering(int index, int pageNumber, String headingGuess, String markdown,
                            float x, float y, float width, float height, boolean headingFromEyeRef)
        {
            this.index = index;
            this.pageNumber = pageNumber;
            this.headingGuess = headingGuess;
            this.markdown = markdown;
            this.x = x; this.y = y; this.width = width; this.height = height;
            this.headingFromEyeRef = headingFromEyeRef;
        }
    }

    /** Anker fuer ein im Body erkanntes Heading mit Y-Position auf der Seite. */
    static final class HeadingAnchor
    {
        final float y;
        final String text;
        HeadingAnchor(float y, String text) { this.y = y; this.text = text; }
    }

    private final List<HeadingAnchor> lastBodyHeadings = new ArrayList<>();
    private boolean collectingBodyHeadings = false;

    /** Anker fuer einen Listen-Bullet (Bildposition). Body-Zeilen, die in der
     *  gleichen x-Spalte liegen und y im Bullet-Y-Bereich treffen, werden als
     *  Listen-Item gerendert (`- ...`). Format: {xMin, xMax, yMin, yMax}. */
    private final List<float[]> currentBulletAnchors = new ArrayList<>();

    /** Line-basierte Tabellen der zuletzt verarbeiteten Seite (nach
     *  {@link LineBasedTableDetector}). Werden ueber Pseudo-TextLines an der
     *  richtigen Y-Position in den Body-Strom eingespeist. */
    private final List<LineBasedTableDetector.LineBasedTable> lastLineBasedTables = new ArrayList<>();

    private static List<RawPageData.RawChar> removeCharsInTableRegions(
            List<RawPageData.RawChar> chars,
            List<LineBasedTableDetector.LineBasedTable> tables)
    {
        List<RawPageData.RawChar> kept = new ArrayList<>(chars.size());
        for (RawPageData.RawChar c : chars)
        {
            boolean inTable = false;
            for (LineBasedTableDetector.LineBasedTable t : tables)
            {
                if (c.y >= t.yTop - 1f && c.y <= t.yBottom + 1f
                        && c.x >= t.xLeft - 1f && c.x <= t.xRight + 1f)
                {
                    inTable = true;
                    break;
                }
            }
            if (!inTable) kept.add(c);
        }
        return kept;
    }

    /** Italic-Status des zuletzt geflushten Paragraphen. Wird verwendet, um beim
     *  Wechsel italic ↔ nicht-italic eine Blank-Line einzufuegen — sonst wirken
     *  zwei aufeinanderfolgende Paragraphen mit Soft-Linebreak gerendert wie ein
     *  Absatz, was bei Beispielbloecken in *italic* unerwuenscht ist. */
    private boolean lastFlushedItalic = false;
    /** Endete der zuletzt geflushte Paragraph mit echtem Satzende? Wird zur
     *  Disambiguation des italic-Format-Wechsels herangezogen — Wort-Fortsetzungen
     *  ueber Bindestrich-Umbruch enden NICHT mit Satzende. */
    private boolean lastFlushEndedSentence = false;

    /** Bild-Platzhalter-Kommentare der zuletzt verarbeiteten Seite. Werden NICHT
     *  inline ans Seitenende geschrieben — sonst zerreissen sie Listen oder
     *  Absaetze, die ueber den Seitenumbruch laufen. Caller kann sie via
     *  {@link #getLastImageComments()} abholen und am Ende des logischen Blocks
     *  (z. B. am Ende des Buchs) ausgeben. */
    private final List<String> lastImageComments = new ArrayList<>();

    /** Bildkommentare der zuletzt verarbeiteten Seite (z. B. {@code "<!-- Bild 1: y=… -->"}). */
    public List<String> getLastImageComments()
    {
        return java.util.Collections.unmodifiableList(lastImageComments);
    }

    public void setEmitBoxesInline(boolean emitBoxesInline)
    {
        this.emitBoxesInline = emitBoxesInline;
    }

    public List<BoxRendering> getLastBoxes()
    {
        return java.util.Collections.unmodifiableList(lastBoxes);
    }

    public void setIncludeBodyText(boolean includeBodyText)
    {
        this.includeBodyText = includeBodyText;
    }

    public void setIncludeImages(boolean includeImages)
    {
        this.includeImages = includeImages;
    }

    public void setIncludeOrnaments(boolean includeOrnaments)
    {
        this.includeOrnaments = includeOrnaments;
    }

    /**
     * Prueft ob ein Font ein Ornament-Font ist.
     */
    private static boolean isOrnamentFont(String fontName)
    {
        if (fontName == null) return false;
        // Prefix entfernen (z.B. "RCCVHP+TypeEmbellishmentsOne")
        String clean = fontName.contains("+") ? fontName.substring(fontName.indexOf('+') + 1) : fontName;
        for (String ornament : ORNAMENT_FONTS)
        {
            if (clean.startsWith(ornament)) return true;
        }
        return false;
    }

    /**
     * Extrahiert NUR die Ueberschriften einer Seite in korrekter Leserichtung.
     * Beruecksichtigt:
     * - Spaltenreihenfolge (links vor rechts, fullwidth oben/unten)
     * - Fusszeilen werden ignoriert (y > pageHeight - 30)
     * - Kalligraphische Initialen: Band-Erkennung (y-overlap)
     * - UPPERCASE mit kleinen Grossbuchstaben (fontSize > minSize → uppercase)
     * - Heading-Hierarchie rein nach Schriftgroesse:
     *   H1: fontSize > 25 (Kapitelueberschriften, z.B. 31pt Andalus)
     *   H2: fontSize > 14 (Abschnitte, z.B. 18pt Andalus)
     *   H3: fontSize > 10 AND bold (Unterabschnitte, z.B. 13pt GentiumBasic-Bold)
     *   H4: fontSize == bodySize AND bold AND short (<60 Zeichen)
     * - Keine Heading-Vergabe wenn fontSize == bodySize und nicht bold
     */
    public String interpretHeadingsOnly(RawPageData page)
    {
        if (page.chars == null || page.chars.isEmpty()) return "";

        // Bild-Bounding-Boxes sammeln (ohne Fullpage-Hintergrundbilder)
        List<float[]> imageBounds = new ArrayList<>(); // [x, y, w, h] normalisiert
        if (page.images != null)
        {
            for (RawPageData.RawImage img : page.images)
            {
                float ix = img.x;
                float iy = img.y;
                float iw = img.width;
                float ih = img.height;
                // Negative Hoehe normalisieren (Y nach oben verschieben)
                if (ih < 0) { iy += ih; ih = -ih; }
                if (iw < 0) { ix += iw; iw = -iw; }
                // Hintergrund/Layout-Bilder ignorieren:
                // - Volle Breite (>90%): Fullpage, Header, Fussleiste etc.
                // - Volle Hoehe (>90%) UND breiter als 40%: Spalten-Hintergrund
                boolean wideEnough = iw > page.pageWidth * 0.9f;
                boolean tallEnough = ih > page.pageHeight * 0.9f;
                boolean halfWide = iw > page.pageWidth * 0.4f;
                if (wideEnough) continue;                             // volle Breite = immer Hintergrund
                if (tallEnough && halfWide) continue;                 // hohe Spalte
                // Winzige Bilder ignorieren (Icons, Schmuckelemente)
                if (iw < 20 || ih < 20) continue;
                imageBounds.add(new float[]{ix, iy, iw, ih});
            }
        }

        // Vektor-Rechtecke als zusaetzliche Overlay-Boxen
        if (page.rects != null)
        {
            for (RawPageData.RawRect rect : page.rects)
            {
                float rx = rect.x;
                float ry = rect.y;
                float rw = rect.width;
                float rh = rect.height;
                if (rh < 0) { ry += rh; rh = -rh; }
                if (rw < 0) { rx += rw; rw = -rw; }
                // Auf sichtbaren Bereich clippen (Boxen ragen oft ueber die Seite hinaus)
                float visibleX = Math.max(rx, 0);
                float visibleY = Math.max(ry, 0);
                float visibleW = Math.min(rx + rw, page.pageWidth) - visibleX;
                float visibleH = Math.min(ry + rh, page.pageHeight) - visibleY;
                if (visibleW < 20 || visibleH < 20) continue;
                boolean wideEnough = visibleW > page.pageWidth * 0.9f;
                boolean tallEnough = visibleH > page.pageHeight * 0.9f;
                boolean halfWide = visibleW > page.pageWidth * 0.4f;
                if (wideEnough && tallEnough) continue;               // Fullpage
                if (tallEnough && halfWide) continue;                 // hohe Spalte
                imageBounds.add(new float[]{rx, ry, rw, rh});
            }
        }

        // Bulletpoint-Icons: Position (x, y) fuer Spalten-Zuordnung
        List<BulletIcon> bulletIcons = new ArrayList<>();
        if (page.images != null)
        {
            for (RawPageData.RawImage img : page.images)
            {
                float iw = Math.abs(img.width);
                float ih = Math.abs(img.height);
                // Bullet-Icons: 3-15pt breit/hoch
                if (iw >= 3 && iw <= 15 && ih >= 3 && ih <= 15)
                {
                    bulletIcons.add(new BulletIcon(img.x, img.y, iw, ih));
                }
            }
        }

        // Schritt 0a: Initiale/grosse Zeichen mit Folgezeichen zusammenfuehren
        mergeInitialChars(page.chars);

        // Schritt 0b: Schriftarten-Klassifikation
        List<ClassifiedChar> classified = classifyChars(page.chars);

        // Fusszeilen entfernen (y > pageHeight - 30)
        float footerThreshold = page.pageHeight - 30;
        classified.removeIf(c -> c.raw.y > footerThreshold);
        if (classified.isEmpty()) return "";

        // Body-Fontgroesse bestimmen
        float bodyFontSize = findBodyFontSize(classified);

        // Spaltengrenze finden (ohne Fusszeilen, die das Histogramm verfaelschen)
        List<RawPageData.RawChar> charsWithoutFooter = new ArrayList<>();
        for (RawPageData.RawChar rc : page.chars)
        {
            if (rc.y <= footerThreshold) charsWithoutFooter.add(rc);
        }
        float splitX = findColumnSplitByHistogram(charsWithoutFooter, page.pageWidth);

        // Fallback: wenn kein Split gefunden, Histogramm ohne Tabellenzeilen wiederholen.
        // Fullwidth-Tabellen fuellen den Spalten-Zwischenraum im Histogramm und
        // verhindern so die Erkennung der eigentlichen Spaltengrenze.
        if (splitX <= 0)
        {
            List<TextLine> preLines = buildLines(classified);
            java.util.Set<Float> tableLineYs = new java.util.HashSet<>();
            for (TextLine tl : preLines)
            {
                SplitResult gapCheck = splitLineAtGaps(tl, GAP_THRESHOLD);
                if (gapCheck != null && gapCheck.row.cells.size() >= 3)
                {
                    tableLineYs.add((float) tl.y);
                }
            }
            if (!tableLineYs.isEmpty())
            {
                List<RawPageData.RawChar> nonTableChars = new ArrayList<>();
                for (RawPageData.RawChar rc : charsWithoutFooter)
                {
                    boolean isTable = false;
                    for (Float ty : tableLineYs)
                    {
                        if (Math.abs(rc.y - ty) < LINE_Y_TOLERANCE + 1) { isTable = true; break; }
                    }
                    if (!isTable) nonTableChars.add(rc);
                }
                splitX = findColumnSplitByHistogram(nonTableChars, page.pageWidth);
            }
        }

        // Spalten aufteilen (gleiche Logik wie interpretPage)
        List<ClassifiedChar> leftChars = new ArrayList<>();
        List<ClassifiedChar> rightChars = new ArrayList<>();
        List<ClassifiedChar> fullWidthChars = new ArrayList<>();

        if (splitX > 0)
        {
            java.util.Set<Float> fullWidthYs = new java.util.HashSet<>();
            for (ClassifiedChar ch : classified)
            {
                if (ch.isInitial) fullWidthYs.add(ch.raw.y);
            }

            List<TextLine> preLines = buildLines(classified);
            for (TextLine line : preLines)
            {
                if (line.chars.isEmpty()) continue;

                // Regel: fontSize > 25 ist IMMER fullwidth (Kapitelueberschriften)
                float avgSize = (float) line.chars.stream().mapToDouble(c -> c.raw.fontSize).average().orElse(0);
                if (avgSize > 25)
                {
                    fullWidthYs.add((float) line.y);
                    continue;
                }

                List<ClassifiedChar> sorted = new ArrayList<>(line.chars);
                sorted.sort(Comparator.comparingDouble(c -> c.raw.x));
                float lineMinX = sorted.get(0).raw.x;
                float lineMaxX = sorted.get(sorted.size() - 1).raw.x + sorted.get(sorted.size() - 1).raw.width;
                boolean spansColumns = lineMinX < splitX - 30 && lineMaxX > splitX + 30;
                if (!spansColumns) continue;

                // Pruefen ob ein grosser Gap die Spaltengrenze ueberlappt.
                // Wenn ja, sind das zwei Spalten-Headings, nicht ein fullwidth-Heading.
                boolean hasGapAtSplit = false;
                for (int j = 1; j < sorted.size(); j++)
                {
                    float prevEnd = sorted.get(j - 1).raw.x + sorted.get(j - 1).raw.width;
                    float nextStart = sorted.get(j).raw.x;
                    float gapSize = nextStart - prevEnd;
                    // Gap > 3x Zeichenbreite UND die Spaltengrenze liegt innerhalb des Gaps
                    if (gapSize > sorted.get(j - 1).raw.width * 3
                        && prevEnd < splitX && nextStart > splitX)
                    {
                        hasGapAtSplit = true;
                        break;
                    }
                }
                if (hasGapAtSplit) continue;

                // Zeile die ueber die Spaltengrenze geht OHNE Gap dort = fullwidth
                // (z.B. ein Wort das die Spaltengrenze ueberbrueckt)
                fullWidthYs.add((float) line.y);
            }

            for (ClassifiedChar ch : classified)
            {
                boolean isFullWidth = false;
                for (Float fwY : fullWidthYs)
                {
                    if (Math.abs(ch.raw.y - fwY) < LINE_Y_TOLERANCE + 1) { isFullWidth = true; break; }
                }
                if (isFullWidth) fullWidthChars.add(ch);
                else if (ch.raw.x + ch.raw.width / 2 < splitX) leftChars.add(ch);
                else rightChars.add(ch);
            }
        }
        else
        {
            fullWidthChars.addAll(classified);
        }

        // Pro Spalte Zeilen bilden
        List<TextLine> leftLines = buildLines(leftChars);
        List<TextLine> rightLines = buildLines(rightChars);
        List<TextLine> fullWidthLines = buildLines(fullWidthChars);

        // Tabellen-Y-Positionen sammeln
        // Schritt 1: Fullwidth-Tabellen (>=3 Zellen) auf Pre-Split-Zeilen erkennen
        java.util.Set<Float> tableYs = new java.util.HashSet<>();
        {
            List<TextLine> allPreLines = buildLines(classified);
            for (TextLine preLine : allPreLines)
            {
                SplitResult gapCheck = splitLineAtGaps(preLine, GAP_THRESHOLD);
                if (gapCheck != null && gapCheck.row.cells.size() >= 3)
                {
                    tableYs.add((float) preLine.y);
                }
            }
        }

        // Schritt 2/3/3b: 2-Spalten-Tabellen per Spalte erkennen (nach Spaltenaufteilung)
        detectTwoColumnTables(leftLines, tableYs);
        detectTwoColumnTables(rightLines, tableYs);
        detectTwoColumnTables(fullWidthLines, tableYs);

        // Heading-Erkennung in Leserichtung: sammle (level, y, text)
        // Fullwidth-Zeilen die zwischen Spaltentext liegen wirken als Divider:
        // Die Leserichtung ist: oben-fw → (links bis Divider, rechts bis Divider)
        // → Divider → (links bis naechster Divider, rechts bis naechster Divider) → ...
        List<float[]> levelYs = new ArrayList<>(); // float[]{level, y}
        List<String> texts = new ArrayList<>();
        boolean hasColumns = !leftLines.isEmpty() && !rightLines.isEmpty();

        if (hasColumns && !fullWidthLines.isEmpty())
        {
            fullWidthLines.sort(Comparator.comparingDouble(l -> l.y));
            leftLines.sort(Comparator.comparingDouble(l -> l.y));
            rightLines.sort(Comparator.comparingDouble(l -> l.y));

            double columnMinY = Math.min(
                leftLines.stream().mapToDouble(l -> l.y).min().orElse(Double.MAX_VALUE),
                rightLines.stream().mapToDouble(l -> l.y).min().orElse(Double.MAX_VALUE));
            double columnMaxY = Math.max(
                leftLines.stream().mapToDouble(l -> l.y).max().orElse(0),
                rightLines.stream().mapToDouble(l -> l.y).max().orElse(0));

            // Fullwidth aufteilen: oben (vor Spalten), Divider (zwischen Spalten), unten (nach Spalten)
            List<TextLine> topFw = new ArrayList<>();
            List<TextLine> dividers = new ArrayList<>();
            List<TextLine> bottomFw = new ArrayList<>();
            for (TextLine line : fullWidthLines)
            {
                if (line.y < columnMinY) topFw.add(line);
                else if (line.y > columnMaxY) bottomFw.add(line);
                else dividers.add(line);
            }

            // Obere Fullwidth
            for (TextLine line : topFw)
                collectHeading(levelYs, texts, line, bodyFontSize, tableYs, imageBounds, bulletIcons);

            // Spalten-Abschnitte, unterteilt durch Divider
            // Schnitt-Y-Positionen: alle Divider-Ys + unendlich am Ende
            List<Double> cutYs = new ArrayList<>();
            for (TextLine d : dividers) cutYs.add(d.y);
            cutYs.add(Double.MAX_VALUE);

            int leftIdx = 0, rightIdx = 0;
            for (int di = 0; di < cutYs.size(); di++)
            {
                double cutY = cutYs.get(di);

                // Linke Spalte bis cutY
                while (leftIdx < leftLines.size() && leftLines.get(leftIdx).y < cutY)
                {
                    collectHeading(levelYs, texts, leftLines.get(leftIdx), bodyFontSize, tableYs, imageBounds, bulletIcons);
                    leftIdx++;
                }
                // Rechte Spalte bis cutY
                while (rightIdx < rightLines.size() && rightLines.get(rightIdx).y < cutY)
                {
                    collectHeading(levelYs, texts, rightLines.get(rightIdx), bodyFontSize, tableYs, imageBounds, bulletIcons);
                    rightIdx++;
                }
                // Divider selbst ausgeben
                if (di < dividers.size())
                {
                    collectHeading(levelYs, texts, dividers.get(di), bodyFontSize, tableYs, imageBounds, bulletIcons);
                }
            }

            // Untere Fullwidth
            for (TextLine line : bottomFw)
                collectHeading(levelYs, texts, line, bodyFontSize, tableYs, imageBounds, bulletIcons);
        }
        else if (hasColumns)
        {
            leftLines.sort(Comparator.comparingDouble(l -> l.y));
            for (TextLine line : leftLines) collectHeading(levelYs, texts, line, bodyFontSize, tableYs, imageBounds, bulletIcons);
            rightLines.sort(Comparator.comparingDouble(l -> l.y));
            for (TextLine line : rightLines) collectHeading(levelYs, texts, line, bodyFontSize, tableYs, imageBounds, bulletIcons);
        }
        else
        {
            // Einspaltig oder nur eine Spalte hat Inhalt: alle Zeilen zusammenfuehren
            List<TextLine> allLines = new ArrayList<>(fullWidthLines);
            allLines.addAll(leftLines);
            allLines.addAll(rightLines);
            allLines.sort(Comparator.comparingDouble(l -> l.y));
            for (TextLine line : allLines) collectHeading(levelYs, texts, line, bodyFontSize, tableYs, imageBounds, bulletIcons);
        }

        // Aufeinanderfolgende Ueberschriften gleicher Ebene zusammenfassen,
        // aber nur wenn:
        // - der Y-Abstand klein ist (mehrzeilige Ueberschrift, max ~50pt)
        // - keine Nicht-Heading-Zeile dazwischen liegt (Separator = level -1)
        //
        // Image-Overlay-Zeilen (level -2) werden gesammelt und am Ende der
        // aktuellen Ueberschrift als Block ausgegeben.
        StringBuilder headings = new StringBuilder();
        int i = 0;
        while (i < levelYs.size())
        {
            int level = (int) levelYs.get(i)[0];
            if (level == -2)
            {
                // Image-Overlay-Zeile: stillschweigend ignorieren
                i++;
                continue;
            }
            if (level == -3)
            {
                // Bulletpoint: Folgezeilen die Body-Text oder weitere Bullets sind zusammenfassen
                float lastBulletY = levelYs.get(i)[1];
                StringBuilder bulletMerged = new StringBuilder(texts.get(i));
                while (i + 1 < levelYs.size())
                {
                    int nextLevel = (int) levelYs.get(i + 1)[0];
                    float nextY = levelYs.get(i + 1)[1];
                    // Nur zusammenfassen: Body-Text (-1) mit kleinem Y-Abstand
                    if (nextLevel == -1 && Math.abs(nextY - lastBulletY) < 14
                        && !texts.get(i + 1).isEmpty())
                    {
                        String nextText = texts.get(i + 1);
                        String prevText = bulletMerged.toString();
                        if (prevText.endsWith("-") || prevText.endsWith("–"))
                        {
                            bulletMerged.setLength(bulletMerged.length() - 1);
                            bulletMerged.append(nextText);
                        }
                        else
                        {
                            bulletMerged.append(" ").append(nextText);
                        }
                        i++;
                        lastBulletY = nextY;
                    }
                    else break;
                }
                headings.append("- ").append(bulletMerged).append("\n");
                i++;
                continue;
            }
            if (level == -1)
            {
                // Separator / Body-Text / Tabelle
                if (includeBodyText && !texts.get(i).isEmpty())
                {
                    headings.append(texts.get(i)).append("\n");
                }
                i++;
                continue;
            }

            float lastY = levelYs.get(i)[1];
            StringBuilder merged = new StringBuilder(texts.get(i));
            while (i + 1 < levelYs.size()
                && (int) levelYs.get(i + 1)[0] == level
                && Math.abs(levelYs.get(i + 1)[1] - lastY) < 50)
            {
                // Nur zusammenfassen wenn der naechste Text den vorherigen fortsetzt:
                // - Y-Abstand sehr klein (< 20pt → Zeilenumbruch innerhalb einer Ueberschrift)
                // - beginnt mit Kleinbuchstabe (Wortfortsetzung)
                // - oder vorheriger Text endet mit Bindestrich (Silbentrennung)
                String nextText = texts.get(i + 1);
                String prevText = merged.toString();
                float yDelta = Math.abs(levelYs.get(i + 1)[1] - lastY);
                boolean continues = !nextText.isEmpty()
                    && (yDelta < 20
                        || Character.isLowerCase(nextText.charAt(0))
                        || prevText.endsWith("-")
                        || prevText.endsWith("–"));
                if (!continues) break;

                i++;
                lastY = levelYs.get(i)[1];
                // Bei Silbentrennung: Bindestrich entfernen
                if (prevText.endsWith("-") || prevText.endsWith("–"))
                {
                    merged.setLength(merged.length() - 1);
                    merged.append(nextText);
                }
                else
                {
                    merged.append(" ").append(nextText);
                }
            }
            String prefix = "#".repeat(level) + " ";
            headings.append(prefix).append(merged).append("\n");
            i++;
        }

        return headings.toString();
    }

    // =====================================================================
    // Phase-based structured page interpretation
    // =====================================================================

    /**
     * Content block representing a single logical element on the page.
     */
    static class ContentBlock
    {
        enum Type { HEADING, BULLET, TABLE, BODY, BOX_CONTENT }

        Type type;
        int headingLevel; // 1-4 for HEADING type
        String text;
        List<String> tableCells; // for TABLE type
        double y;

        ContentBlock(Type type, String text, double y)
        {
            this.type = type;
            this.text = text;
            this.y = y;
        }

        static ContentBlock heading(int level, String text, double y)
        {
            ContentBlock b = new ContentBlock(Type.HEADING, text, y);
            b.headingLevel = level;
            return b;
        }

        static ContentBlock bullet(String text, double y)
        {
            return new ContentBlock(Type.BULLET, text, y);
        }

        static ContentBlock table(List<String> cells, double y)
        {
            ContentBlock b = new ContentBlock(Type.TABLE, null, y);
            b.tableCells = new ArrayList<>(cells);
            return b;
        }

        static ContentBlock body(String text, double y)
        {
            return new ContentBlock(Type.BODY, text, y);
        }

        static ContentBlock boxContent(String text, double y)
        {
            return new ContentBlock(Type.BOX_CONTENT, text, y);
        }
    }

    /**
     * Interpretiert eine Seite mit phasenbasierter Architektur.
     * Ersetzt interpretHeadingsOnly mit sauberer Trennung:
     *   Phase 1: Vorbereitung (classify, footer, boxes, bullets)
     *   Phase 2: Box-Inhalt isolieren
     *   Phase 3: Spaltenaufteilung
     *   Phase 4: Per-Column Klassifikation (bullet, table, heading, body)
     *   Phase 5: Cross-column Tabellen-Merging
     *   Phase 6: Leserichtung
     *   Phase 7: Textkonsolidierung und Ausgabe
     */
    public String interpretPageStructured(RawPageData page)
    {
        if (page.chars == null || page.chars.isEmpty()) return "";

        // ===== PHASE 1: Prepare =====

        mergeInitialChars(page.chars);
        List<ClassifiedChar> classified = classifyChars(page.chars);

        // Remove footers
        float footerThreshold = page.pageHeight - 30;
        classified.removeIf(c -> c.raw.y > footerThreshold);
        if (classified.isEmpty()) return "";

        float bodyFontSize = findBodyFontSize(classified);

        // Collect boxes (images + rects) with existing filtering logic
        List<float[]> boxes = new ArrayList<>(); // [x, y, w, h]
        if (page.images != null)
        {
            for (RawPageData.RawImage img : page.images)
            {
                float ix = img.x;
                float iy = img.y;
                float iw = img.width;
                float ih = img.height;
                if (ih < 0) { iy += ih; ih = -ih; }
                if (iw < 0) { ix += iw; iw = -iw; }
                boolean wideEnough = iw > page.pageWidth * 0.9f;
                boolean tallEnough = ih > page.pageHeight * 0.9f;
                boolean halfWide = iw > page.pageWidth * 0.4f;
                if (wideEnough) continue;
                if (tallEnough && halfWide) continue;
                if (iw < 20 || ih < 20) continue;
                boxes.add(new float[]{ix, iy, iw, ih});
            }
        }
        if (page.rects != null)
        {
            for (RawPageData.RawRect rect : page.rects)
            {
                float rx = rect.x;
                float ry = rect.y;
                float rw = rect.width;
                float rh = rect.height;
                if (rh < 0) { ry += rh; rh = -rh; }
                if (rw < 0) { rx += rw; rw = -rw; }
                float visibleX = Math.max(rx, 0);
                float visibleY = Math.max(ry, 0);
                float visibleW = Math.min(rx + rw, page.pageWidth) - visibleX;
                float visibleH = Math.min(ry + rh, page.pageHeight) - visibleY;
                if (visibleW < 20 || visibleH < 20) continue;
                boolean wideEnough = visibleW > page.pageWidth * 0.9f;
                boolean tallEnough = visibleH > page.pageHeight * 0.9f;
                boolean halfWide = visibleW > page.pageWidth * 0.4f;
                if (wideEnough && tallEnough) continue;
                if (tallEnough && halfWide) continue;
                boxes.add(new float[]{rx, ry, rw, rh});
            }
        }

        // Collect bullet icons (3-15pt images)
        List<BulletIcon> bulletIcons = new ArrayList<>();
        if (page.images != null)
        {
            for (RawPageData.RawImage img : page.images)
            {
                float iw = Math.abs(img.width);
                float ih = Math.abs(img.height);
                if (iw >= 3 && iw <= 15 && ih >= 3 && ih <= 15)
                {
                    bulletIcons.add(new BulletIcon(img.x, img.y, iw, ih));
                }
            }
        }

        // ===== PHASE 2: Isolate box content =====
        // For each box, find lines where >=80% of chars are inside the box.
        // Remove those chars from classified and store separately.
        List<ContentBlock> boxBlocks = new ArrayList<>();
        if (!boxes.isEmpty())
        {
            List<TextLine> preLines = buildLines(classified);
            java.util.Set<Double> boxLineYs = new java.util.HashSet<>();

            for (float[] box : boxes)
            {
                for (TextLine line : preLines)
                {
                    if (boxLineYs.contains(line.y)) continue;
                    List<ClassifiedChar> nonBlank = line.chars.stream()
                        .filter(c -> !c.raw.text.isBlank()).toList();
                    int totalNonBlank = nonBlank.size();
                    if (totalNonBlank == 0) continue;

                    int charsInBox = 0;
                    for (ClassifiedChar ch : nonBlank)
                    {
                        if (ch.raw.x >= box[0] - 2 && ch.raw.x <= box[0] + box[2] + 2
                            && ch.raw.y >= box[1] - 2 && ch.raw.y <= box[1] + box[3] + 2)
                        {
                            charsInBox++;
                        }
                    }
                    if (charsInBox >= totalNonBlank * 0.8)
                    {
                        boxLineYs.add(line.y);
                        String boxText = buildHeadingText(line);
                        if (!boxText.isBlank())
                        {
                            boxBlocks.add(ContentBlock.boxContent(boxText, line.y));
                        }
                    }
                }
            }

            // Remove box chars from classified — nur Zeichen die tatsaechlich
            // in einer Box liegen, nicht per Y-Toleranz (nahe Haupttext-Zeilen
            // wuerden sonst faelschlich mitentfernt)
            if (!boxLineYs.isEmpty())
            {
                classified.removeIf(c ->
                {
                    // Nur entfernen wenn das Zeichen selbst in einer Box liegt
                    for (float[] box : boxes)
                    {
                        if (c.raw.x >= box[0] - 2 && c.raw.x <= box[0] + box[2] + 2
                            && c.raw.y >= box[1] - 2 && c.raw.y <= box[1] + box[3] + 2)
                        {
                            return true;
                        }
                    }
                    return false;
                });
            }
        }

        if (classified.isEmpty()) return "";

        // ===== PHASE 3: Column split =====
        List<RawPageData.RawChar> charsWithoutFooter = new ArrayList<>();
        for (RawPageData.RawChar rc : page.chars)
        {
            if (rc.y <= footerThreshold) charsWithoutFooter.add(rc);
        }
        // Remove box chars from histogram input too
        if (!boxBlocks.isEmpty())
        {
            java.util.Set<Double> boxYs = new java.util.HashSet<>();
            for (ContentBlock bb : boxBlocks) boxYs.add(bb.y);
            charsWithoutFooter.removeIf(rc ->
            {
                for (Double by : boxYs)
                {
                    if (Math.abs(rc.y - by) < LINE_Y_TOLERANCE + 1) return true;
                }
                return false;
            });
        }

        float splitX = findColumnSplitByHistogram(charsWithoutFooter, page.pageWidth);

        // Fallback: retry without fullwidth table lines
        if (splitX <= 0)
        {
            List<TextLine> preLines = buildLines(classified);
            java.util.Set<Float> tableLineYs = new java.util.HashSet<>();
            for (TextLine tl : preLines)
            {
                SplitResult gapCheck = splitLineAtGaps(tl, GAP_THRESHOLD);
                if (gapCheck != null && gapCheck.row.cells.size() >= 3)
                {
                    tableLineYs.add((float) tl.y);
                }
            }
            if (!tableLineYs.isEmpty())
            {
                List<RawPageData.RawChar> nonTableChars = new ArrayList<>();
                for (RawPageData.RawChar rc : charsWithoutFooter)
                {
                    boolean isTable = false;
                    for (Float ty : tableLineYs)
                    {
                        if (Math.abs(rc.y - ty) < LINE_Y_TOLERANCE + 1) { isTable = true; break; }
                    }
                    if (!isTable) nonTableChars.add(rc);
                }
                splitX = findColumnSplitByHistogram(nonTableChars, page.pageWidth);
            }
        }

        // Detect fullwidth lines and split into columns
        List<ClassifiedChar> leftChars = new ArrayList<>();
        List<ClassifiedChar> rightChars = new ArrayList<>();
        List<ClassifiedChar> fullWidthChars = new ArrayList<>();

        if (splitX > 0)
        {
            java.util.Set<Float> fullWidthYs = new java.util.HashSet<>();
            for (ClassifiedChar ch : classified)
            {
                if (ch.isInitial) fullWidthYs.add(ch.raw.y);
            }

            List<TextLine> preLines = buildLines(classified);
            for (TextLine line : preLines)
            {
                if (line.chars.isEmpty()) continue;
                float avgSize = (float) line.chars.stream().mapToDouble(c -> c.raw.fontSize).average().orElse(0);
                if (avgSize > 25)
                {
                    fullWidthYs.add((float) line.y);
                    continue;
                }

                List<ClassifiedChar> sorted = new ArrayList<>(line.chars);
                sorted.sort(Comparator.comparingDouble(c -> c.raw.x));
                float lineMinX = sorted.get(0).raw.x;
                float lineMaxX = sorted.get(sorted.size() - 1).raw.x + sorted.get(sorted.size() - 1).raw.width;
                boolean spansColumns = lineMinX < splitX - 30 && lineMaxX > splitX + 30;
                if (!spansColumns) continue;

                // Check if a large gap sits at the split point
                boolean hasGapAtSplit = false;
                for (int j = 1; j < sorted.size(); j++)
                {
                    float prevEnd = sorted.get(j - 1).raw.x + sorted.get(j - 1).raw.width;
                    float nextStart = sorted.get(j).raw.x;
                    float gapSize = nextStart - prevEnd;
                    if (gapSize > sorted.get(j - 1).raw.width * 3
                        && prevEnd < splitX && nextStart > splitX)
                    {
                        hasGapAtSplit = true;
                        break;
                    }
                }
                if (hasGapAtSplit) continue;

                fullWidthYs.add((float) line.y);
            }

            for (ClassifiedChar ch : classified)
            {
                boolean isFullWidth = false;
                for (Float fwY : fullWidthYs)
                {
                    if (Math.abs(ch.raw.y - fwY) < LINE_Y_TOLERANCE + 1) { isFullWidth = true; break; }
                }
                if (isFullWidth) fullWidthChars.add(ch);
                else if (ch.raw.x + ch.raw.width / 2 < splitX) leftChars.add(ch);
                else rightChars.add(ch);
            }
        }
        else
        {
            fullWidthChars.addAll(classified);
        }

        // Build lines per column
        List<TextLine> leftLines = buildLines(leftChars);
        List<TextLine> rightLines = buildLines(rightChars);
        List<TextLine> fullWidthLines = buildLines(fullWidthChars);

        // ===== PHASE 4: Per-column classification =====

        List<ContentBlock> leftBlocks = classifyColumnLines(leftLines, bodyFontSize, bulletIcons);
        List<ContentBlock> rightBlocks = classifyColumnLines(rightLines, bodyFontSize, bulletIcons);
        List<ContentBlock> fullWidthBlocks = classifyColumnLines(fullWidthLines, bodyFontSize, bulletIcons);

        // ===== PHASE 5: Cross-column table merging =====
        // If left and right columns have TABLE blocks at matching Y positions, merge cells
        if (!leftBlocks.isEmpty() && !rightBlocks.isEmpty())
        {
            for (ContentBlock lb : leftBlocks)
            {
                if (lb.type != ContentBlock.Type.TABLE) continue;
                for (ContentBlock rb : rightBlocks)
                {
                    if (rb.type != ContentBlock.Type.TABLE) continue;
                    if (Math.abs(lb.y - rb.y) < LINE_Y_TOLERANCE + 1)
                    {
                        // Merge right cells into left
                        if (lb.tableCells != null && rb.tableCells != null)
                        {
                            lb.tableCells.addAll(rb.tableCells);
                        }
                        // Mark right as consumed (empty body)
                        rb.type = ContentBlock.Type.BODY;
                        rb.text = "";
                        rb.tableCells = null;
                    }
                }
            }
        }

        // ===== PHASE 6: Reading order assembly =====
        List<ContentBlock> ordered = new ArrayList<>();
        boolean hasColumns = !leftBlocks.isEmpty() && !rightBlocks.isEmpty();

        if (hasColumns && !fullWidthBlocks.isEmpty())
        {
            fullWidthBlocks.sort(Comparator.comparingDouble(b -> b.y));
            leftBlocks.sort(Comparator.comparingDouble(b -> b.y));
            rightBlocks.sort(Comparator.comparingDouble(b -> b.y));

            double columnMinY = Math.min(
                leftBlocks.stream().mapToDouble(b -> b.y).min().orElse(Double.MAX_VALUE),
                rightBlocks.stream().mapToDouble(b -> b.y).min().orElse(Double.MAX_VALUE));
            double columnMaxY = Math.max(
                leftBlocks.stream().mapToDouble(b -> b.y).max().orElse(0),
                rightBlocks.stream().mapToDouble(b -> b.y).max().orElse(0));

            List<ContentBlock> topFw = new ArrayList<>();
            List<ContentBlock> dividers = new ArrayList<>();
            List<ContentBlock> bottomFw = new ArrayList<>();
            for (ContentBlock b : fullWidthBlocks)
            {
                if (b.y < columnMinY) topFw.add(b);
                else if (b.y > columnMaxY) bottomFw.add(b);
                else dividers.add(b);
            }

            ordered.addAll(topFw);

            // Cut points at divider Y positions
            List<Double> cutYs = new ArrayList<>();
            for (ContentBlock d : dividers) cutYs.add(d.y);
            cutYs.add(Double.MAX_VALUE);

            int leftIdx = 0, rightIdx = 0;
            for (int di = 0; di < cutYs.size(); di++)
            {
                double cutY = cutYs.get(di);

                while (leftIdx < leftBlocks.size() && leftBlocks.get(leftIdx).y < cutY)
                    ordered.add(leftBlocks.get(leftIdx++));
                while (rightIdx < rightBlocks.size() && rightBlocks.get(rightIdx).y < cutY)
                    ordered.add(rightBlocks.get(rightIdx++));

                if (di < dividers.size())
                    ordered.add(dividers.get(di));
            }

            ordered.addAll(bottomFw);
        }
        else if (hasColumns)
        {
            leftBlocks.sort(Comparator.comparingDouble(b -> b.y));
            ordered.addAll(leftBlocks);
            rightBlocks.sort(Comparator.comparingDouble(b -> b.y));
            ordered.addAll(rightBlocks);
        }
        else
        {
            List<ContentBlock> allBlocks = new ArrayList<>(fullWidthBlocks);
            allBlocks.addAll(leftBlocks);
            allBlocks.addAll(rightBlocks);
            allBlocks.sort(Comparator.comparingDouble(b -> b.y));
            ordered.addAll(allBlocks);
        }

        // ===== PHASE 7: Text consolidation & output =====
        return consolidateBlocks(ordered);
    }

    /**
     * Phase 4 helper: classifies all lines in a column into ContentBlocks.
     * Performs bullet detection, table detection, heading detection, and body fallback.
     */
    private List<ContentBlock> classifyColumnLines(List<TextLine> lines, float bodyFontSize,
                                                    List<BulletIcon> bulletIcons)
    {
        if (lines.isEmpty()) return new ArrayList<>();

        List<TextLine> sorted = new ArrayList<>(lines);
        sorted.sort(Comparator.comparingDouble(l -> l.y));

        // (a) Bullet detection: mark lines with nearby bullet icon
        // Bei mehrzeiligen Bullets wird die Zeile mit besserem Y-Hoehen-Overlap gewaehlt
        boolean[] hasBullet = new boolean[sorted.size()];
        for (BulletIcon bi : bulletIcons)
        {
            int bestLine = -1;
            float bestOverlap = -1;
            for (int i = 0; i < sorted.size(); i++)
            {
                TextLine line = sorted.get(i);
                if (line.chars.isEmpty()) continue;

                float lineMinX = line.chars.stream().filter(c -> !c.raw.text.isBlank())
                    .map(c -> c.raw.x).min(Float::compare).orElse(0f);
                if (bi.x < lineMinX - 20 || bi.x > lineMinX + 5) continue;

                if (bi.height > 0)
                {
                    // Hoehen-Overlap berechnen: Ueberlappung der Y-Bereiche
                    float biTop = bi.y - bi.height;
                    float biBottom = bi.y;
                    float lineAvgHeight = (float) line.chars.stream()
                        .filter(c -> !c.raw.text.isBlank())
                        .mapToDouble(c -> c.raw.height).average().orElse(10);
                    float lineTop = (float) line.y - lineAvgHeight;
                    float lineBottom = (float) line.y;

                    float overlapTop = Math.max(biTop, lineTop);
                    float overlapBottom = Math.min(biBottom, lineBottom);
                    float overlap = Math.max(0, overlapBottom - overlapTop);
                    if (overlap > bestOverlap)
                    {
                        bestOverlap = overlap;
                        bestLine = i;
                    }
                }
                else
                {
                    // Fallback: nur Y-Position (alte Logik)
                    float yDist = Math.abs((float) line.y - bi.y);
                    if (yDist < 6 && (bestLine < 0 || yDist < bestOverlap))
                    {
                        bestOverlap = yDist;
                        bestLine = i;
                    }
                }
            }
            if (bestLine >= 0) hasBullet[bestLine] = true;
        }

        // (b) Table detection: use detectTwoColumnTables logic
        // Tabellen brauchen IMMER mindestens 2 Zeilen mit uebereinstimmenden Spalten
        java.util.Set<Float> tableYs = new java.util.HashSet<>();
        detectTwoColumnTables(sorted, tableYs);
        // >=3 cell Zeilen: nur als Tabelle wenn Nachbar-Zeile ebenfalls passende Gaps hat
        for (int ti = 0; ti < sorted.size(); ti++)
        {
            TextLine line = sorted.get(ti);
            if (tableYs.contains((float) line.y)) continue;
            SplitResult gapCheck = splitLineAtGaps(line, GAP_THRESHOLD);
            if (gapCheck == null || gapCheck.row.cells.size() < 3) continue;
            // Nachbar-Zeile mit passenden Gap-Positionen suchen
            boolean hasNeighbor = false;
            for (int tj = Math.max(0, ti - 2); tj <= Math.min(sorted.size() - 1, ti + 2); tj++)
            {
                if (tj == ti) continue;
                SplitResult neighborGap = splitLineAtGaps(sorted.get(tj), GAP_THRESHOLD);
                if (neighborGap != null && neighborGap.row.cells.size() >= 3
                    && gapPositionsAlign(neighborGap.gapEnds, gapCheck.gapEnds, GAP_POS_TOLERANCE))
                {
                    hasNeighbor = true;
                    break;
                }
            }
            if (hasNeighbor) tableYs.add((float) line.y);
        }

        boolean[] isTable = new boolean[sorted.size()];
        for (int i = 0; i < sorted.size(); i++)
        {
            for (Float ty : tableYs)
            {
                if (Math.abs((float) sorted.get(i).y - ty) < LINE_Y_TOLERANCE + 1)
                {
                    isTable[i] = true;
                    break;
                }
            }
        }

        // (c) & (d) Heading detection and body fallback
        List<ContentBlock> blocks = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++)
        {
            TextLine line = sorted.get(i);
            if (line.chars.isEmpty()) continue;

            String text = buildHeadingText(line);
            if (text.isBlank() || text.length() < 2) continue;

            // Compute dominant font size
            java.util.Map<Integer, Integer> sizeCounts = new java.util.HashMap<>();
            for (ClassifiedChar ch : line.chars)
            {
                if (ch.isInitial || ch.raw.text.isBlank()) continue;
                sizeCounts.merge(Math.round(ch.raw.fontSize), 1, Integer::sum);
            }
            if (sizeCounts.isEmpty())
            {
                for (ClassifiedChar ch : line.chars)
                {
                    if (ch.raw.text.isBlank()) continue;
                    sizeCounts.merge(Math.round(ch.raw.fontSize), 1, Integer::sum);
                }
            }
            float dominantSize = sizeCounts.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(e -> (float) e.getKey())
                .orElse(0f);

            int totalChars = (int) line.chars.stream().filter(c -> !c.raw.text.isBlank()).count();
            int boldChars = (int) line.chars.stream().filter(c -> !c.raw.text.isBlank() && c.isBold).count();
            boolean allBold = totalChars > 0 && boldChars >= totalChars * 0.9;
            boolean isShort = text.length() < 60;
            boolean endsWithColon = text.stripTrailing().endsWith(":");

            // Heading level
            int headingLevel = 0;
            if (dominantSize > 25)
            {
                headingLevel = 1;
            }
            else if (dominantSize > 14)
            {
                headingLevel = 2;
            }
            else if (dominantSize > bodyFontSize + 0.5f && allBold && !endsWithColon)
            {
                headingLevel = 3;
            }
            else if (allBold && isShort && dominantSize >= bodyFontSize - 0.5f && !endsWithColon)
            {
                headingLevel = 4;
            }

            // Fallback: line starts with bold chars at larger font size
            if (headingLevel == 0 && isShort)
            {
                float leadingSize = 0;
                int leadingBoldCount = 0;
                boolean leadingDone = false;
                for (ClassifiedChar ch : line.chars)
                {
                    if (ch.raw.text.isBlank() || ch.isInitial) continue;
                    if (!leadingDone && ch.isBold && ch.raw.fontSize > bodyFontSize + 0.5f)
                    {
                        leadingSize = Math.max(leadingSize, ch.raw.fontSize);
                        leadingBoldCount++;
                    }
                    else
                    {
                        leadingDone = true;
                    }
                }
                if (leadingBoldCount >= 2 && leadingSize > bodyFontSize + 0.5f)
                {
                    if (leadingSize > 25) headingLevel = 1;
                    else if (leadingSize > 14) headingLevel = 2;
                    else headingLevel = 3;
                }
            }

            // Decision: heading wins over bullet (bullet is decoration)
            if (headingLevel > 0)
            {
                // Headings: Plaintext (Markdown-Prefix kommt in consolidateBlocks)
                blocks.add(ContentBlock.heading(headingLevel, text, line.y));
            }
            else if (isTable[i] && !hasBullet[i])
            {
                // Table row
                SplitResult gapCheck = splitLineAtGaps(line, GAP_THRESHOLD);
                if (gapCheck != null && gapCheck.row.cells.size() >= 2)
                {
                    blocks.add(ContentBlock.table(gapCheck.row.cells, line.y));
                }
                else
                {
                    // Single-cell table row (e.g. header continuation)
                    List<String> singleCell = new ArrayList<>();
                    singleCell.add(text);
                    blocks.add(ContentBlock.table(singleCell, line.y));
                }
            }
            else
            {
                // Body und Bullet: Markdown mit Bold/Italic-Formatierung
                String mdText = lineToMarkdown(line);
                if (hasBullet[i])
                {
                    blocks.add(ContentBlock.bullet(mdText, line.y));
                }
                else
                {
                    blocks.add(ContentBlock.body(mdText, line.y));
                }
            }
        }

        return blocks;
    }

    /**
     * Phase 7: Consolidates ordered ContentBlocks into final markdown output.
     * Merges consecutive headings, consolidates paragraphs, handles bullets and tables.
     */
    private String consolidateBlocks(List<ContentBlock> blocks)
    {
        StringBuilder output = new StringBuilder();
        int i = 0;
        while (i < blocks.size())
        {
            ContentBlock block = blocks.get(i);

            // Skip empty body blocks (consumed by cross-column merge)
            if (block.type == ContentBlock.Type.BODY && (block.text == null || block.text.isEmpty()))
            {
                i++;
                continue;
            }

            // Skip box content (discarded)
            if (block.type == ContentBlock.Type.BOX_CONTENT)
            {
                i++;
                continue;
            }

            switch (block.type)
            {
                case HEADING:
                {
                    // (a) Heading merging: consecutive same-level headings with small Y-delta
                    StringBuilder merged = new StringBuilder(block.text);
                    double lastY = block.y;
                    int level = block.headingLevel;

                    while (i + 1 < blocks.size())
                    {
                        ContentBlock next = blocks.get(i + 1);
                        if (next.type != ContentBlock.Type.HEADING || next.headingLevel != level)
                            break;
                        double yDelta = Math.abs(next.y - lastY);
                        if (yDelta > 50) break;

                        String prevText = merged.toString();
                        String nextText = next.text;
                        boolean continues = !nextText.isEmpty()
                            && (yDelta < 35
                                || Character.isLowerCase(nextText.charAt(0))
                                || prevText.endsWith("-")
                                || prevText.endsWith("\u2013"));
                        if (!continues) break;

                        i++;
                        lastY = next.y;
                        if (prevText.endsWith("-") || prevText.endsWith("\u2013"))
                        {
                            merged.setLength(merged.length() - 1);
                            merged.append(nextText);
                        }
                        else
                        {
                            merged.append(" ").append(nextText);
                        }
                    }

                    String prefix = "#".repeat(level) + " ";
                    output.append(prefix).append(merged).append("\n");
                    i++;
                    break;
                }
                case BULLET:
                {
                    // (c) Bullet consolidation: include following BODY lines
                    StringBuilder bulletText = new StringBuilder(block.text);
                    double lastY = block.y;

                    while (i + 1 < blocks.size())
                    {
                        ContentBlock next = blocks.get(i + 1);
                        if (next.type != ContentBlock.Type.BODY) break;
                        if (next.text == null || next.text.isEmpty()) break;
                        if (Math.abs(next.y - lastY) > 14) break;

                        i++;
                        lastY = next.y;
                        String prevText = bulletText.toString();
                        if (prevText.endsWith("-") || prevText.endsWith("\u2013"))
                        {
                            bulletText.setLength(bulletText.length() - 1);
                            bulletText.append(next.text);
                        }
                        else
                        {
                            bulletText.append(" ").append(next.text);
                        }
                    }

                    output.append("- ").append(bulletText).append("\n");
                    i++;
                    break;
                }
                case TABLE:
                {
                    // (d) Table output: format as markdown table
                    if (block.tableCells != null && !block.tableCells.isEmpty())
                    {
                        output.append("|");
                        for (String cell : block.tableCells)
                        {
                            output.append(" ").append(cell.trim()).append(" |");
                        }
                        output.append("\n");
                    }
                    i++;
                    break;
                }
                case BODY:
                {
                    if (!includeBodyText)
                    {
                        i++;
                        break;
                    }

                    // (b) Paragraph consolidation: merge consecutive BODY lines
                    StringBuilder para = new StringBuilder(block.text != null ? block.text : "");
                    double lastY = block.y;

                    while (i + 1 < blocks.size())
                    {
                        ContentBlock next = blocks.get(i + 1);
                        if (next.type != ContentBlock.Type.BODY) break;
                        if (next.text == null || next.text.isEmpty())
                        {
                            i++;
                            continue;
                        }

                        // Bold-Einleitung mit ":" startet neuen Absatz
                        // Pattern: **Label** : Text  oder  **Label:** Text
                        String nextText = next.text;
                        if (nextText.startsWith("**"))
                        {
                            int firstColon = nextText.indexOf(':');
                            if (firstColon > 0 && firstColon < 60)
                            {
                                break;
                            }
                        }

                        String prevText = para.toString();

                        // Join hyphenated words
                        if (prevText.endsWith("-") || prevText.endsWith("\u2013"))
                        {
                            para.setLength(para.length() - 1);
                            para.append(nextText);
                        }
                        else
                        {
                            para.append(" ").append(nextText);
                        }

                        lastY = next.y;
                        i++;

                        // Paragraph ends at sentence-ending punctuation
                        String currentPara = para.toString().stripTrailing();
                        if (!currentPara.isEmpty())
                        {
                            char last = currentPara.charAt(currentPara.length() - 1);
                            if (last == '.' || last == '!' || last == '?'
                                || last == '\u00BB' || last == '"' || last == '\u00AB'
                                || last == '\u2019')
                            {
                                break;
                            }
                        }
                    }

                    String paraText = para.toString().replaceAll("  +", " ").strip();
                    if (!paraText.isEmpty())
                    {
                        // "#" als Aufzaehlungszeichen → Markdown-Liste
                        // Am Anfang: "# Text" → "- Text"
                        // Im Text: "... # Text" → "...\n- Text"
                        if (paraText.contains("#"))
                        {
                            if (paraText.startsWith("#"))
                            {
                                paraText = "- " + paraText.substring(1).stripLeading();
                            }
                            paraText = paraText.replace(" # ", "\n- ");
                        }
                        output.append(paraText).append("\n\n");
                    }
                    i++;
                    break;
                }
                default:
                    i++;
                    break;
            }
        }

        return output.toString();
    }

    /**
     * Prueft ob eine Zeile eine Ueberschrift ist und sammelt sie als (level, text, y).
     * Heading-Hierarchie rein nach Schriftgroesse (groesser als bodySize = Heading):
     *   H1: dominantSize > 25 (Kapitelueberschriften)
     *   H2: dominantSize > 14 (Abschnitte)
     *   H3: dominantSize > bodySize AND bold (Unterabschnitte)
     *   H4: dominantSize ~ bodySize AND bold AND short (<60 Zeichen)
     */
    private void collectHeading(List<float[]> levelYs, List<String> texts,
                                TextLine line, float bodyFontSize,
                                java.util.Set<Float> tableYs,
                                List<float[]> imageBounds,
                                List<BulletIcon> bulletIcons)
    {
        if (line.chars.isEmpty()) return;

        // Pruefen ob die Zeile innerhalb eines Bildes/Rechtecks liegt (Overlay).
        // Overlay hat Vorrang vor Bulletpoints, da Boxen Bullets enthalten koennen.
        // Nur wenn >=80% der Zeichen innerhalb einer Box liegen (echter Overlay),
        // nicht wenn das Bild nur dekorativ im Textfluss liegt.
        if (!imageBounds.isEmpty())
        {
            List<ClassifiedChar> nonBlank = line.chars.stream()
                .filter(c -> !c.raw.text.isBlank()).toList();
            int totalNonBlank = nonBlank.size();
            if (totalNonBlank > 0)
            {
                for (float[] ib : imageBounds)
                {
                    int charsInBox = 0;
                    for (ClassifiedChar ch : nonBlank)
                    {
                        if (ch.raw.x >= ib[0] - 2 && ch.raw.x <= ib[0] + ib[2] + 2
                            && ch.raw.y >= ib[1] - 2 && ch.raw.y <= ib[1] + ib[3] + 2)
                        {
                            charsInBox++;
                        }
                    }
                    if (charsInBox >= totalNonBlank * 0.8)
                    {
                        String overlayText = buildHeadingText(line);
                        if (!overlayText.isBlank())
                        {
                            levelYs.add(new float[]{-2, (float) line.y}); // -2 = Image-Overlay
                            texts.add(overlayText);
                        }
                        return;
                    }
                }
            }
        }

        // Bulletpoint-Erkennung: Icon muss in derselben Spalte liegen wie die Textzeile
        // (Icon-X maximal 20pt links vom ersten Textzeichen, Y-Toleranz 6pt)
        // AUSNAHME: wenn die Zeile ein Heading-Kandidat ist (bold, kurz), wird sie
        // als Heading behandelt — das Bullet-Icon ist dann nur Dekoration.
        boolean hasBullet = false;
        if (!bulletIcons.isEmpty())
        {
            float lineMinX = line.chars.stream().filter(c -> !c.raw.text.isBlank())
                .map(c -> c.raw.x).min(Float::compare).orElse(0f);
            for (BulletIcon bi : bulletIcons)
            {
                if (Math.abs((float) line.y - bi.y) < 6
                    && bi.x >= lineMinX - 20 && bi.x <= lineMinX + 5)
                {
                    hasBullet = true;
                    break;
                }
            }
        }

        // Tabellen-Zeile ausschliessen (erkannt auf vollen preLines vor Spaltenaufteilung)
        // Bullets und Headings haben Vorrang vor der Tabellenerkennung
        if (!hasBullet)
        {
            for (Float ty : tableYs)
            {
                if (Math.abs((float) line.y - ty) < LINE_Y_TOLERANCE + 1)
                {
                    if (!levelYs.isEmpty())
                    {
                        levelYs.add(new float[]{-1, (float) line.y});
                        texts.add(includeBodyText ? formatTableRow(line) : "");
                    }
                    return;
                }
            }
        }

        String text = buildHeadingText(line);
        if (text.isBlank() || text.length() < 2) return;

        // Haeufigste fontSize (ohne Initiale, Fallback auf alle wenn alles Initial)
        java.util.Map<Integer, Integer> sizeCounts = new java.util.HashMap<>();
        for (ClassifiedChar ch : line.chars)
        {
            if (ch.isInitial || ch.raw.text.isBlank()) continue;
            sizeCounts.merge(Math.round(ch.raw.fontSize), 1, Integer::sum);
        }
        if (sizeCounts.isEmpty())
        {
            for (ClassifiedChar ch : line.chars)
            {
                if (ch.raw.text.isBlank()) continue;
                sizeCounts.merge(Math.round(ch.raw.fontSize), 1, Integer::sum);
            }
        }
        float dominantSize = sizeCounts.entrySet().stream()
            .max(java.util.Map.Entry.comparingByValue())
            .map(e -> (float) e.getKey())
            .orElse(0f);

        // Tabellen-Zeilen ausschliessen: Zeilen mit mehreren grossen Gaps sind Tabellen
        SplitResult gapCheck = splitLineAtGaps(line, GAP_THRESHOLD);
        if (gapCheck != null && gapCheck.row.cells.size() >= 3)
        {
            // Tabellenzeile → als Separator behandeln
            if (!levelYs.isEmpty())
            {
                levelYs.add(new float[]{-1, (float) line.y});
                texts.add(includeBodyText ? formatTableRow(line) : "");
            }
            return;
        }

        int totalChars = (int) line.chars.stream().filter(c -> !c.raw.text.isBlank()).count();
        int boldChars = (int) line.chars.stream().filter(c -> !c.raw.text.isBlank() && c.isBold).count();
        boolean allBold = totalChars > 0 && boldChars >= totalChars * 0.9;
        boolean isShort = text.length() < 60;
        boolean endsWithColon = text.stripTrailing().endsWith(":");

        // Hierarchie bestimmen
        int level = 0;
        if (dominantSize > 25)
        {
            level = 1;
        }
        else if (dominantSize > 14)
        {
            level = 2;
        }
        else if (dominantSize > bodyFontSize + 0.5f && allBold && !endsWithColon)
        {
            level = 3;
        }
        else if (allBold && isShort && dominantSize >= bodyFontSize - 0.5f && !endsWithColon)
        {
            level = 4;
        }

        // Fallback: Zeile beginnt mit bold-Text in groesserer Schrift,
        // gefolgt von kleinerem Text (z.B. "Anspringen (Spezialmanöver)")
        if (level == 0 && isShort)
        {
            float leadingSize = 0;
            int leadingBoldCount = 0;
            boolean leadingDone = false;
            for (ClassifiedChar ch : line.chars)
            {
                if (ch.raw.text.isBlank() || ch.isInitial) continue;
                if (!leadingDone && ch.isBold && ch.raw.fontSize > bodyFontSize + 0.5f)
                {
                    leadingSize = Math.max(leadingSize, ch.raw.fontSize);
                    leadingBoldCount++;
                }
                else
                {
                    leadingDone = true;
                }
            }
            if (leadingBoldCount >= 2 && leadingSize > bodyFontSize + 0.5f)
            {
                if (leadingSize > 25) level = 1;
                else if (leadingSize > 14) level = 2;
                else level = 3;
            }
        }

        // Fallback 2: ALLCAPS-Display-Heading (z.B. "VORWORT", "IMPRESSUM",
        // "KAPITEL 1: GRUNDREGELN"). Bei manchen Display-/Ornament-Fonts ist
        // die deklarierte fontSize kleiner als die optisch gerenderte Hoehe,
        // weshalb die Standard-Schwellen (>14, >25) nicht greifen.
        // Heuristik: alle Buchstaben Grossbuchstaben, kurz, fontSize merklich
        // ueber bodyFontSize, kein Doppelpunkt am Ende (sonst Inline-Marker).
        if (level == 0 && isShort && !endsWithColon)
        {
            int letters = 0, uppers = 0;
            for (int i = 0; i < text.length(); i++)
            {
                char c = text.charAt(i);
                if (Character.isLetter(c))
                {
                    letters++;
                    if (Character.isUpperCase(c)) uppers++;
                }
            }
            boolean allCaps = letters >= 4 && uppers == letters;
            if (allCaps && dominantSize > bodyFontSize + 1f)
            {
                if (dominantSize > bodyFontSize + 8f) level = 1;
                else if (dominantSize > bodyFontSize + 3f) level = 2;
                else level = 3;
            }
        }

        if (level > 0)
        {
            // Heading hat Vorrang vor Bullet (Bullet-Icon ist nur Dekoration)
            levelYs.add(new float[]{level, (float) line.y});
            texts.add(text);
        }
        else if (hasBullet)
        {
            // Bulletpoint (kein Heading)
            levelYs.add(new float[]{-3, (float) line.y}); // -3 = Bulletpoint
            texts.add(text);
        }
        else
        {
            // Nicht-Heading-Zeile: Separator einfuegen um falsches Zusammenfassen zu verhindern
            if (!levelYs.isEmpty())
            {
                levelYs.add(new float[]{-1, (float) line.y}); // -1 = Separator
                texts.add(includeBodyText ? text : "");
            }
        }
    }

    /**
     * Baut den Text einer Heading-Zeile zusammen.
     * Beruecksichtigt:
     * - Kalligraphische Buchstaben (Band-Erkennung: y-overlap → gleiche Zeile)
     * - UPPERCASE-Korrektur: wenn innerhalb eines Fonts die fontSize variiert,
     *   werden kleinere Buchstaben mit groesserer fontSize als Grossbuchstaben erkannt
     * - Wortabstaende bei grossen Gaps
     */
    private String buildHeadingText(TextLine line)
    {
        StringBuilder text = new StringBuilder();

        // Fontgroessen analysieren (ohne Initialen, Ornamente, Leerzeichen)
        List<ClassifiedChar> textChars = line.chars.stream()
            .filter(c -> !c.isInitial && !c.isOrnament && !c.raw.text.isBlank())
            .toList();

        float minFontSize = textChars.stream()
            .map(c -> c.raw.fontSize).min(Float::compareTo).orElse(0f);
        float maxFontSize = textChars.stream()
            .map(c -> c.raw.fontSize).max(Float::compareTo).orElse(0f);
        boolean hasMixedSizes = maxFontSize - minFontSize > 0.5f;

        // Pruefen ob ueberwiegend uppercase (typisch fuer Kapitelueberschriften)
        long upperCount = textChars.stream()
            .filter(c -> c.raw.text.length() == 1 && Character.isUpperCase(c.raw.text.charAt(0))).count();
        long letterCount = textChars.stream()
            .filter(c -> c.raw.text.length() == 1 && Character.isLetter(c.raw.text.charAt(0))).count();
        boolean mostlyUppercase = letterCount > 0 && upperCount > letterCount * 0.8;

        // Normalisierung: UPPERCASE mit gemischten Groessen → nur groessere uppercase
        boolean normalizeCase = hasMixedSizes && mostlyUppercase;

        ClassifiedChar prev = null;
        for (ClassifiedChar ch : line.chars)
        {
            // Ornamente in Headings immer ueberspringen
            if (ch.isOrnament) continue;

            // Wortabstand einfuegen
            if (prev != null)
            {
                float gap = ch.raw.x - (prev.raw.x + prev.raw.width);
                if (gap > prev.raw.width * 0.3f) text.append(" ");
            }

            String charText = ch.raw.text;

            if (normalizeCase && charText.length() == 1 && Character.isLetter(charText.charAt(0)))
            {
                if (ch.raw.fontSize > minFontSize + 0.5f)
                {
                    // Groesserer Buchstabe → uppercase (Wortanfang)
                    charText = charText.toUpperCase();
                }
                else
                {
                    // Kleinerer Buchstabe → lowercase
                    charText = charText.toLowerCase();
                }
            }

            text.append(charText);
            prev = ch;
        }
        return text.toString().trim();
    }

    /**
     * Interpretiert eine einzelne Seite aus Rohdaten.
     */
    public String interpretPage(RawPageData page)
    {
        lastBoxes.clear();
        lastBodyHeadings.clear();
        lastImageComments.clear();
        lastLineBasedTables.clear();
        collectingBodyHeadings = true;
        lastFlushedItalic = false;
        lastFlushEndedSentence = false;
        // Listen-Bullet-Anker fuer diese Seite vorab berechnen — nur Cluster (>=2 Bullets
        // in gleicher x-Spalte) zaehlen als Liste, Singletons sind Eye-Glyphs.
        currentBulletAnchors.clear();
        currentBulletAnchors.addAll(computeBulletAnchorRanges(page));
        if (page.chars == null || page.chars.isEmpty()) return "";

        // Schritt 0: Footer-/Kolumnentitel-Bereich verwerfen.
        // Typischer Aufbau: Seitenzahl + Kapiteltitel im untersten ~35pt-Streifen
        // (z. B. "6 Vorwort", "171"). Verhindert dass diese Token als Body-Text
        // mitgenommen werden.
        if (page.pageHeight > 0)
        {
            float footerCutoff = page.pageHeight - 35f;
            page.chars.removeIf(c -> c.y > footerCutoff);
        }

        // Schritt 0c: Boxen vom Hauptlauftext trennen.
        // Box-Inhalte werden separat unten gerendert mit Box-Anfang/Ende-Markern.
        // Damit fliesst Box-Text nicht in die Spalten-/Tabellenheuristik des Body.
        List<de.pho.dsapdfreader.book.BoxExtractor.BoxRegion> extractedBoxes = new ArrayList<>();
        if (page.rects != null && !page.rects.isEmpty())
        {
            de.pho.dsapdfreader.book.BoxExtractor extractor =
                new de.pho.dsapdfreader.book.BoxExtractor();
            de.pho.dsapdfreader.book.BoxExtractor.SplitResult split = extractor.split(page);
            if (!split.boxes.isEmpty())
            {
                page.chars = split.mainPage.chars;
                extractedBoxes.addAll(split.boxes);
            }
        }

        // Schritt 0d: Line-basierte Tabellen-Detection.
        // Tabellen mit horizontalen Trennlinien (RawRect.isLine) werden direkt aus
        // den Linien gebaut — exakte Cell-Boundaries, kein Y-Spacing-Heuristik-Spagat.
        // Chars im Tabellen-Bereich werden aus page.chars entfernt, damit der
        // Standardflow sie nicht erneut verarbeitet. Pseudo-TextLines fuehren die
        // Tabellen spaeter an der richtigen Y-Position ein.
        lastLineBasedTables.clear();
        if (page.rects != null && !page.rects.isEmpty())
        {
            List<LineBasedTableDetector.LineBasedTable> tables =
                    LineBasedTableDetector.detect(page);
            for (LineBasedTableDetector.LineBasedTable t : tables)
            {
                lastLineBasedTables.add(t);
            }
            if (!lastLineBasedTables.isEmpty())
            {
                page.chars = removeCharsInTableRegions(page.chars, lastLineBasedTables);
            }
        }

        // Schritt 0a: Initiale/grosse Zeichen mit Folgezeichen zusammenfuehren
        // Wenn ein grosses Zeichen (Initial) existiert und nachfolgende Zeichen
        // in dessen Bounding-Box fallen (Y-Bereich ueberlappt), werden sie
        // auf die gleiche Y-Position verschoben -> landen in einer Zeile
        mergeInitialChars(page.chars);

        // Schritt 0b: Schriftarten-Klassifikation
        List<ClassifiedChar> classified = classifyChars(page.chars);

        // Schritt 1: Spaltengrenze finden
        float splitX = findColumnSplitByHistogram(page.chars, page.pageWidth);

        // Schritt 2: Zeichen ZUERST in Spalten aufteilen, DANN pro Spalte Zeilen bilden.
        // Grundprinzip: Jedes Zeichen wird rein nach X-Position einer Spalte zugeordnet.
        // Ausnahme: Zeichen die Teil einer fullwidth-Initiale/Titel-Zeile sind
        // (erkannt durch mergeInitialChars - diese haben y des Ankers).
        List<ClassifiedChar> leftChars = new ArrayList<>();
        List<ClassifiedChar> rightChars = new ArrayList<>();
        List<ClassifiedChar> fullWidthChars = new ArrayList<>();

        if (splitX > 0)
        {
            float bodyFontSize = findBodyFontSize(classified);

            // Sammle Y-Positionen die fullwidth sind
            java.util.Set<Float> fullWidthYs = new java.util.HashSet<>();
            for (ClassifiedChar ch : classified)
            {
                if (ch.isInitial) fullWidthYs.add(ch.raw.y);
            }

            // Zeilen vorab bilden fuer Fullwidth-Erkennung (nur Headings/Initialen)
            List<TextLine> preLines = buildLines(classified);

            for (TextLine line : preLines)
            {
                if (line.chars.isEmpty()) continue;
                List<ClassifiedChar> sorted = new ArrayList<>(line.chars);
                sorted.sort(Comparator.comparingDouble(c -> c.raw.x));

                float lineMaxX = sorted.get(sorted.size() - 1).raw.x + sorted.get(sorted.size() - 1).raw.width;
                float lineMinX = sorted.get(0).raw.x;
                boolean spansColumns = lineMinX < splitX - 30 && lineMaxX > splitX + 30;
                boolean crossesSplit = lineMinX < splitX && lineMaxX > splitX;

                // Heading-Schriftgroesse: groessere Schrift (bold ODER deutlich groesser)
                float avgSize = (float) line.chars.stream().mapToDouble(c -> c.raw.fontSize).average().orElse(0);
                boolean mostlyBold = line.chars.stream().filter(c -> c.isBold).count() > line.chars.size() * 0.5;
                boolean largerThanBody = avgSize > bodyFontSize + 1.5f;
                boolean muchLargerThanBody = avgSize > bodyFontSize * 1.4f;

                // Kurze Heading-Style-Zeile, die splitX kreuzt (zentrierte Headline wie
                // "Proben" auf S12 — chars von x=265-309 mit splitX=300) wuerde sonst in
                // zwei Stuecke zerrissen. Solche Zeilen direkt als fullwidth markieren.
                if (!spansColumns && crossesSplit && (largerThanBody || muchLargerThanBody)
                        && line.chars.size() <= 40)
                {
                    fullWidthYs.add((float) line.y);
                    continue;
                }

                if (!spansColumns) continue;

                // Fullwidth Heading: bold+groesser ODER deutlich groesser (zentrierte Headlines)
                // ABER: nicht wenn an splitX eine grosse Luecke ist — dann sind es
                // zwei separate Spalten-Headings auf gleicher Y-Position (z. B. zwei
                // Sonderfertigkeiten-Titel nebeneinander).
                if ((largerThanBody && mostlyBold) || muchLargerThanBody)
                {
                    boolean splitGap = false;
                    for (int j = 1; j < sorted.size(); j++)
                    {
                        ClassifiedChar prev = sorted.get(j - 1);
                        ClassifiedChar cur = sorted.get(j);
                        float gap = cur.raw.x - (prev.raw.x + prev.raw.width);
                        if (gap > 12f
                            && prev.raw.x + prev.raw.width < splitX
                            && cur.raw.x > splitX)
                        {
                            splitGap = true;
                            break;
                        }
                    }
                    if (!splitGap)
                    {
                        fullWidthYs.add((float) line.y);
                    }
                    continue;
                }

                // Fullwidth Tabelle: spannt beide Spalten + mehrere grosse Gaps.
                //
                // Y-Kollision-Discrimination: zwei unterschiedliche Spalten-Inhalte
                // (links Body-Fliesstext, rechts Tabellenzelle) auf gleicher Y duerfen
                // NICHT als fullwidth eingestuft werden — sonst greift die Spaltentrennung
                // nicht, und Body + Tabellenzelle landen vermischt im Output.
                //
                // Heuristik: wenn EINE der Spalten gar keine internen grossen Gaps hat
                // (d. h. dort flieszt nur Fliesstext) UND die Zeile einen Gap ueber der
                // Spaltengrenze hat, dann ist es eine Y-Kollision, kein fullwidth.
                {
                    // Wir unterscheiden zwei Gap-Klassen:
                    //  • LARGE_GAP (>14pt) — kann Spalten-Gap ODER nur Typesetting-Variation
                    //    bei Bold/Italic-Wechsel sein.
                    //  • TABLE_GAP (>20pt) — ist eindeutig kein Wortzwischenraum mehr.
                    //
                    // Fullwidth-Tabelle erfordert MEHRERE TABLE_GAPs.
                    // Y-Kollision (Body links + Body rechts auf gleicher y) erkennen wir
                    // an einem TABLE_GAP, der die Spaltengrenze ueberbrueckt — und KEINEM
                    // TABLE_GAP innerhalb einer Spalte.
                    final float TABLE_GAP_MIN = 20f;
                    int tableGaps = 0;
                    int tableGapsLeft = 0;
                    int tableGapsRight = 0;
                    int tableGapsStraddling = 0;
                    for (int j = 1; j < sorted.size(); j++)
                    {
                        float gapStart = sorted.get(j - 1).raw.x + sorted.get(j - 1).raw.width;
                        float gapEnd = sorted.get(j).raw.x;
                        float gap = gapEnd - gapStart;
                        if (gap <= TABLE_GAP_MIN) continue;
                        tableGaps++;
                        if (gapEnd <= splitX) tableGapsLeft++;
                        else if (gapStart >= splitX) tableGapsRight++;
                        else tableGapsStraddling++;
                    }
                    // Fullwidth nur wenn die Tabellen-Gaps WIRKLICH ueber die Spalten
                    // gehen — entweder direkt straddling, oder Gaps in beiden Haelften.
                    // Reine "Stat-Block links + Body rechts"-Layouts (alle Gaps in einer
                    // Spalte, KEIN straddling) sind keine cross-column-Tabellen.
                    boolean isCrossColumn =
                            (tableGapsLeft >= 1 && tableGapsRight >= 1)
                            || tableGapsStraddling >= 1;
                    if (tableGaps >= 2 && isCrossColumn)
                    {
                        fullWidthYs.add((float) line.y);
                    }
                }
            }

            // Propagation: Zeilen die beide Spalten ueberspannen UND direkt neben
            // einer bereits erkannten fullwidth-Zeile liegen (Y-Abstand < 20pt)
            // werden ebenfalls fullwidth (Tabellenzeilen mit weniger Gaps, Umbruchzeilen)
            {
                boolean changed = true;
                while (changed)
                {
                    changed = false;
                    for (TextLine line : preLines)
                    {
                        if (fullWidthYs.contains((float) line.y)) continue;
                        if (line.chars.isEmpty()) continue;

                        List<ClassifiedChar> sorted = new ArrayList<>(line.chars);
                        sorted.sort(Comparator.comparingDouble(c -> c.raw.x));
                        float lineMinX = sorted.get(0).raw.x;
                        float lineMaxX = sorted.get(sorted.size() - 1).raw.x
                            + sorted.get(sorted.size() - 1).raw.width;
                        boolean spansColumns = lineMinX < splitX - 30 && lineMaxX > splitX + 30;
                        if (!spansColumns) continue;

                        // Hat diese Zeile einen Nachbarn (Y-Abstand < 20pt) der fullwidth ist?
                        for (Float fwY : fullWidthYs)
                        {
                            if (Math.abs((float) line.y - fwY) < 20)
                            {
                                fullWidthYs.add((float) line.y);
                                changed = true;
                                break;
                            }
                        }
                    }
                }
            }

            // Cross-column Tabellen: beide Spalten haben Tabellenstruktur an gleichen Y-Positionen
            java.util.Set<Float> crossColumnYs = detectCrossColumnTableYs(classified, splitX, fullWidthYs);
            fullWidthYs.addAll(crossColumnYs);

            // Alles unterhalb des Cutoffs ist fullwidth
            for (ClassifiedChar ch : classified)
            {
                boolean isFullWidth = false;
                for (Float fwY : fullWidthYs)
                {
                    if (Math.abs(ch.raw.y - fwY) < LINE_Y_TOLERANCE + 1)
                    {
                        isFullWidth = true;
                        break;
                    }
                }

                if (isFullWidth) fullWidthChars.add(ch);
                else if (ch.raw.x + ch.raw.width / 2 < splitX) leftChars.add(ch);
                else rightChars.add(ch);
            }
        }
        else
        {
            fullWidthChars.addAll(classified);
        }

        // Pro Spalte Zeilen bilden
        List<TextLine> leftLines = buildLines(leftChars);
        List<TextLine> rightLines = buildLines(rightChars);
        List<TextLine> fullWidthLines = buildLines(fullWidthChars);

        // Pseudo-TextLines fuer line-basierte Tabellen einsortieren — pro Tabelle
        // entsprechend ihrer x-Range (linke Spalte / rechte Spalte / fullwidth).
        for (LineBasedTableDetector.LineBasedTable t : lastLineBasedTables)
        {
            TextLine pseudo = new TextLine(t.yTop);
            pseudo.lineBasedTable = t;
            // Spalten-Zuordnung anhand der x-Mitte der Tabelle
            float xCenter = (t.xLeft + t.xRight) / 2f;
            if (splitX > 0 && t.xLeft < splitX - 5f && t.xRight > splitX + 5f)
            {
                fullWidthLines.add(pseudo);
            }
            else if (splitX > 0 && xCenter < splitX)
            {
                leftLines.add(pseudo);
            }
            else if (splitX > 0)
            {
                rightLines.add(pseudo);
            }
            else
            {
                fullWidthLines.add(pseudo);
            }
        }

        // Schritt 3: Markdown erzeugen
        StringBuilder markdown = new StringBuilder();
        boolean hasColumns = !leftLines.isEmpty() && !rightLines.isEmpty();

        if (hasColumns && !fullWidthLines.isEmpty())
        {
            fullWidthLines.sort(Comparator.comparingDouble(l -> l.y));
            leftLines.sort(Comparator.comparingDouble(l -> l.y));
            rightLines.sort(Comparator.comparingDouble(l -> l.y));

            double columnMinY = Math.min(
                leftLines.stream().mapToDouble(l -> l.y).min().orElse(Double.MAX_VALUE),
                rightLines.stream().mapToDouble(l -> l.y).min().orElse(Double.MAX_VALUE));
            double columnMaxY = Math.max(
                leftLines.stream().mapToDouble(l -> l.y).max().orElse(0),
                rightLines.stream().mapToDouble(l -> l.y).max().orElse(0));

            // Fullwidth aufteilen: oben / Divider (zwischen Spalten) / unten
            List<TextLine> topFw = new ArrayList<>();
            List<TextLine> dividers = new ArrayList<>();
            List<TextLine> bottomFw = new ArrayList<>();
            for (TextLine line : fullWidthLines)
            {
                if (line.y < columnMinY) topFw.add(line);
                else if (line.y > columnMaxY) bottomFw.add(line);
                else dividers.add(line);
            }

            // Obere Fullwidth
            if (!topFw.isEmpty()) appendLinesWithTables(markdown, topFw);

            if (dividers.isEmpty())
            {
                // Keine Divider: einfache Spaltenausgabe (einspaltig konsolidiert)
                appendLinesWithTables(markdown, leftLines);
                appendLinesWithTables(markdown, rightLines);
            }
            else
            {
                // Divider (Cross-column Tabellen/Headings) teilen die Spalten in Abschnitte
                List<List<TextLine>> dividerBlocks = new ArrayList<>();
                List<TextLine> currentBlock = new ArrayList<>();
                for (TextLine d : dividers)
                {
                    if (!currentBlock.isEmpty()
                        && d.y - currentBlock.get(currentBlock.size() - 1).y > 20)
                    {
                        dividerBlocks.add(currentBlock);
                        currentBlock = new ArrayList<>();
                    }
                    currentBlock.add(d);
                }
                if (!currentBlock.isEmpty()) dividerBlocks.add(currentBlock);

                List<Double> cutYs = new ArrayList<>();
                for (List<TextLine> block : dividerBlocks) cutYs.add(block.get(0).y);
                cutYs.add(Double.MAX_VALUE);

                int leftIdx = 0, rightIdx = 0;
                for (int bi = 0; bi < cutYs.size(); bi++)
                {
                    double cutY = cutYs.get(bi);

                    // Linke Spalte bis cutY
                    List<TextLine> leftSection = new ArrayList<>();
                    while (leftIdx < leftLines.size() && leftLines.get(leftIdx).y < cutY)
                        leftSection.add(leftLines.get(leftIdx++));

                    // Rechte Spalte bis cutY
                    List<TextLine> rightSection = new ArrayList<>();
                    while (rightIdx < rightLines.size() && rightLines.get(rightIdx).y < cutY)
                        rightSection.add(rightLines.get(rightIdx++));

                    if (!leftSection.isEmpty())
                    {
                        appendLinesWithTables(markdown, leftSection);
                    }
                    if (!rightSection.isEmpty())
                    {
                        appendLinesWithTables(markdown, rightSection);
                    }

                    // Divider-Block ausgeben (Cross-column Tabelle oder Heading)
                    if (bi < dividerBlocks.size())
                    {
                        appendLinesWithTables(markdown, dividerBlocks.get(bi));
                    }
                }
            }

            // Untere Fullwidth
            if (!bottomFw.isEmpty())
            {
                appendLinesWithTables(markdown, bottomFw);
            }
        }
        else if (hasColumns)
        {
            leftLines.sort(Comparator.comparingDouble(l -> l.y));
            appendLinesWithTables(markdown, leftLines);
            rightLines.sort(Comparator.comparingDouble(l -> l.y));
            appendLinesWithTables(markdown, rightLines);
        }
        else
        {
            // Einspaltig — Inhalt kann in fullWidthLines, leftLines ODER rightLines stehen.
            // Frueher wurde nur fullWidthLines gerendert → bei Seiten, deren rechte
            // Spalte komplett von einem Bild verdeckt ist (z. B. Schwertes S10), gingen
            // die linken Spalten-Lines verloren.
            List<TextLine> all = new ArrayList<>();
            all.addAll(fullWidthLines);
            all.addAll(leftLines);
            all.addAll(rightLines);
            all.sort(Comparator.comparingDouble(l -> l.y));
            appendLinesWithTables(markdown, all);
        }

        // Bild-Platzhalter sammeln (Hinweise, ohne Extraktion).
        // Filter: echte Inhaltsbilder, keine Bullet-Icons, kein Seitenhintergrund.
        if (page.images != null && !page.images.isEmpty())
        {
            List<RawPageData.RawImage> realImages = new ArrayList<>();
            for (RawPageData.RawImage img : page.images)
            {
                float iw = Math.abs(img.width);
                float ih = Math.abs(img.height);
                if (iw < 50 || ih < 50) continue; // Bullet/Icon
                if (page.pageWidth > 0 && page.pageHeight > 0
                    && iw * ih > page.pageWidth * page.pageHeight * 0.85f) continue; // Hintergrund
                realImages.add(img);
            }
            if (!realImages.isEmpty())
            {
                realImages.sort(Comparator.comparingDouble(i -> i.y));
                // Bildkommentare NICHT inline anhaengen — sie wuerden Listen/Absaetze
                // am Seitenumbruch zerreissen. Stattdessen in lastImageComments
                // sammeln; Caller (RegenerateBookText) emittiert sie am Ende des
                // logischen Blocks (Buch-Ende).
                for (int idx = 0; idx < realImages.size(); idx++)
                {
                    RawPageData.RawImage img = realImages.get(idx);
                    lastImageComments.add(String.format(
                        "<!-- Seite %d, Bild %d: y=%.0f x=%.0f size=%.0fx%.0f -->",
                        page.pageNumber, idx + 1,
                        img.y, img.x, Math.abs(img.width), Math.abs(img.height)));
                }
            }
        }

        // Box-Inhalte rendern: jede Box als eigener Markdown-Abschnitt mit klaren
        // Anfang/Ende-Markern. Box-Text fliesst nicht mehr im Body mit.
        // Wir rendern nicht rekursiv ueber interpretPage, sondern direkt ueber
        // buildLines + lineToMarkdown — kein Spalten-Histogramm noetig
        // (Boxen sind klein, einspaltig).
        // Ab hier sind wir bei Box-Rendering — keine weiteren Body-Headings einsammeln,
        // und Bullet-Anker des Body NICHT mehr auf Box-Zeilen anwenden (Box-Inhalte
        // koennen zufaellig dieselben y-Bereiche treffen).
        collectingBodyHeadings = false;
        currentBulletAnchors.clear();

        // Auge-Glyph-Kandidaten finden (kleine Bilder, die Duplikate eines Master-Bilds sind).
        List<RawPageData.RawImage> eyeGlyphs = findEyeGlyphCandidates(page);

        if (!extractedBoxes.isEmpty())
        {
            extractedBoxes.sort(Comparator.comparingDouble(b -> b.y));
            for (int idx = 0; idx < extractedBoxes.size(); idx++)
            {
                de.pho.dsapdfreader.book.BoxExtractor.BoxRegion box = extractedBoxes.get(idx);
                if (box.chars.size() < 5) continue;

                // Box-Rendering durch rekursiven interpretPage-Aufruf:
                // dadurch greift die volle Pipeline (mergeInitial, Spalten-
                // Histogramm, Line-basierte Tabellen, Fullwidth-Headings) auch
                // im Box-Inneren. Das ist insbesondere fuer breite Profilkaesten
                // wichtig, die zwei Spalten umspannen.
                String boxText = renderBoxAsSubPage(box, page).stripTrailing();
                if (boxText.isEmpty()) continue;

                // Heading-Guess: erste Heading-/Bold-Zeile aus dem Box-MD ziehen.
                String headingGuess = guessBoxHeading(boxText);
                // Profilkaesten ohne Titel-Zeile (z. B. Stat-Block, dessen Heading
                // auf der vorigen Seite liegt) erkennen wir am Fehlen eines
                // Heading als ERSTE Inhaltszeile. Ein "###"-Heading mitten im
                // Stat-Block (z. B. "### Anrufungsschwierigkeit: -4") gilt
                // hier NICHT als Titel.
                boolean hasOwnHeading = hasTitleHeading(boxText);

                // Box ohne eigene Heading: per 2D-Naehe auf eine Body-Heading mappen
                // (Boxen ohne Titel verweisen ueber ein Auge-Symbol auf einen Body-Heading;
                // das Auge ist nicht immer als Bild/Glyph extrahierbar — geometrische
                // Naehe ist robuster und liefert in der Regel dasselbe Ergebnis).
                boolean fromRef = false;
                if (!hasOwnHeading)
                {
                    String fromRefHeading = resolveBoxHeadingFromBody(box, eyeGlyphs, lastBodyHeadings);
                    if (fromRefHeading != null)
                    {
                        headingGuess = fromRefHeading;
                        fromRef = true;
                    }
                }

                lastBoxes.add(new BoxRendering(
                    idx + 1, page.pageNumber, headingGuess, boxText,
                    box.x, box.y, box.width, box.height, fromRef));

                if (emitBoxesInline)
                {
                    if (markdown.length() > 0 && markdown.charAt(markdown.length() - 1) != '\n')
                        markdown.append('\n');
                    markdown.append('\n');
                    markdown.append(String.format(
                        "<!-- Box-Anfang %d: y=%.0f x=%.0f size=%.0fx%.0f -->%n",
                        idx + 1, box.y, box.x, box.width, box.height));
                    markdown.append(boxText).append('\n');
                    markdown.append("<!-- Box-Ende ").append(idx + 1).append(" -->\n");
                }
            }
        }

        // Cleanup
        String result = markdown.toString();
        result = result.replaceAll("(?m)^.*Peter Hofmann \\(Order #\\d+\\).*\n?", "");
        result = result.replaceAll("(?m)^\\d{1,3}\\s*\n", "");
        // Aufeinander folgende Tabellen mit gleicher Spaltenanzahl zusammenführen.
        // Detect-Pipeline trennt Tabellen durch Spalten-/Box-Aufteilung manchmal in
        // mehrere Mini-Tabellen mit Plain-Text-Lines dazwischen — auf Markdown-Ebene
        // zusammenfassen, weil das robuster ist als auf Cell-Ebene.
        result = mergeAdjacentMarkdownTables(result);
        // Bullet-Glyphen am Zeilenanfang in Markdown-Listenpunkte umwandeln
        // "**• Foo:** Bar"  -> "- **Foo:** Bar"
        // "• Foo"           -> "- Foo"
        result = result.replaceAll("(?m)^\\*\\*•\\s+", "- **");
        result = result.replaceAll("(?m)^•\\s+", "- ");
        // Talent-Statline-Labels werden manchmal faelschlich zu Headings hochgestuft,
        // weil sie in leicht groesserer Schrift gerendert sind. Demote zurueck zu Bold.
        // Beispiel: "### Steigerungskosten: A" -> "**Steigerungskosten:** A"
        result = result.replaceAll(
            "(?m)^#{1,3}\\s+(Steigerungskosten):\\s*([A-D])\\s*$",
            "**$1:** $2");
        // Trennstriche bei umbrochenen Wörtern entfernen.
        // "Gewinnersei- te" -> "Gewinnerseite", "Or- kräuber" -> "Orkräuber".
        // Nur greifen wenn beide Seiten Kleinbuchstaben sind (legitime
        // Bindestrich-Komposita wie "Lebens-Energie" oder "DSA-Regeln" haben
        // entweder keinen Leerraum oder Großbuchstaben dahinter und bleiben unangetastet).
        result = result.replaceAll("([a-zäöüß])-\\s+([a-zäöüß])", "$1$2");
        // Inhaltsverzeichnis-Seiten reduzieren. Seitenverweise eines Buchs sind in
        // digitaler Aufarbeitung nicht mehr relevant — wir behalten nur das Heading.
        if (result.matches("(?s)^\\s*(?:#{1,3})\\s+INHALTSVERZEICHNIS\\b.*"))
        {
            int nl = result.indexOf('\n');
            String head = nl > 0 ? result.substring(0, nl) : result;
            result = head + "\n\n<!-- Inhaltsverzeichnis weggelassen — Seitenverweise im digitalen Format irrelevant -->\n";
        }
        else if (looksLikeTocPage(result))
        {
            // TOC-Folgeseite: dominant aus "Titel | Seitenzahl"-Tabellenzeilen oder
            // langen Run-On-Listen mit Kapiteltiteln. Komplett unterdruecken.
            result = "<!-- Inhaltsverzeichnis-Folgeseite weggelassen -->\n";
        }
        // Mehrzeilige Display-Headings zusammenfuehren:
        // Wenn zwei aufeinanderfolgende #/##/### Headings derselben Stufe direkt aufeinander
        // folgen UND der erste mit Doppelpunkt endet ODER beide ALLCAPS sind, dann mergen.
        // Z. B. "# KAPITEL 1:" + "# GRUNDREGELN" -> "# KAPITEL 1: GRUNDREGELN"
        result = result.replaceAll(
            "(?m)^(#{1,3})\\s+([^\\n]*?:)\\s*\\n\\1\\s+([\\p{Lu}\\p{N}][^\\n]*)$",
            "$1 $2 $3");
        result = result.replaceAll(
            "(?m)^(#{1,3})\\s+([\\p{Lu}\\p{N}][\\p{Lu}\\p{N}\\s]{2,})\\s*\\n\\1\\s+([\\p{Lu}\\p{N}][\\p{Lu}\\p{N}\\s]{2,})$",
            "$1 $2 $3");
        return result;
    }

    // =====================================================================
    // Schriftarten-Klassifikation
    // =====================================================================

    /**
     * Klassifiziert jedes Zeichen anhand seines Font-Namens.
     * Erkennt Bold, Italic, SmallCaps, und Font-Familiy aus dem Font-Namen.
     */
    private List<ClassifiedChar> classifyChars(List<RawPageData.RawChar> chars)
    {
        List<ClassifiedChar> result = new ArrayList<>(chars.size());
        for (RawPageData.RawChar raw : chars)
        {
            result.add(classifyChar(raw));
        }
        return result;
    }

    private ClassifiedChar classifyChar(RawPageData.RawChar raw)
    {
        String fn = raw.fontName != null ? raw.fontName : "";

        // Font-Name normalisieren: Prefix entfernen (z.B. "KFOYTO+GentiumBasic" -> "GentiumBasic")
        String cleanFont = fn.contains("+") ? fn.substring(fn.indexOf('+') + 1) : fn;

        // Font-Family extrahieren (ohne Suffix wie -Bold, -Italic, -SC700)
        String family = cleanFont
            .replaceAll("-(Bold|Italic|Oblique|Regular|SC\\d+|Light|Medium|Heavy|Black|Thin).*$", "")
            .replaceAll("(Bold|Italic|Regular)$", "")
            .trim();

        // Bold-Erkennung: mehrere Strategien
        boolean isBold = raw.bold; // Basiswert aus RawExtractor
        if (!isBold)
        {
            isBold = cleanFont.contains("Bold") || cleanFont.contains("bold")
                || cleanFont.contains("SC700") || cleanFont.contains("SC600")
                || cleanFont.contains("SC800") || cleanFont.contains("SC900")
                || cleanFont.contains("Heavy") || cleanFont.contains("Black")
                || cleanFont.contains("SmallCaps"); // SmallCaps sind optisch prominent
        }

        // Italic-Erkennung
        boolean isItalic = raw.italic
            || cleanFont.contains("Italic") || cleanFont.contains("italic")
            || cleanFont.contains("Oblique") || cleanFont.contains("oblique");

        // SmallCaps-Erkennung (oft fuer Zauber-/Ritualnamen)
        boolean isSmallCaps = cleanFont.contains("SmallCaps") || cleanFont.contains("SC700")
            || cleanFont.contains("SC600") || cleanFont.contains("SC800");

        // Ornament-Erkennung
        boolean isOrnament = isOrnamentFont(fn);

        // Dekorative Initiale (>30pt)
        boolean isInitial = raw.fontSize > 30 && !isOrnament;

        // Grosse Schrift (>15pt, aber nicht initial)
        boolean isLargeFont = raw.fontSize > 15 && !isInitial && !isOrnament;

        return new ClassifiedChar(raw, family, isBold, isItalic, isSmallCaps, isInitial, isLargeFont, isOrnament);
    }

    // =====================================================================
    // Zeilenbildung - beruecksichtigt Font-Family-Wechsel
    // =====================================================================

    private List<TextLine> buildLines(List<ClassifiedChar> chars)
    {
        if (chars.isEmpty()) return new ArrayList<>();

        List<ClassifiedChar> sorted = new ArrayList<>(chars);
        sorted.sort(Comparator.comparingDouble((ClassifiedChar c) -> c.raw.y)
            .thenComparingDouble(c -> c.raw.x));

        List<TextLine> lines = new ArrayList<>();
        TextLine currentLine = null;

        for (ClassifiedChar ch : sorted)
        {
            boolean newLine = currentLine == null
                || Math.abs(ch.raw.y - currentLine.y) > LINE_Y_TOLERANCE;

            if (newLine)
            {
                currentLine = new TextLine(ch.raw.y);
                lines.add(currentLine);
            }
            currentLine.chars.add(ch);
            currentLine.y = (currentLine.y * (currentLine.chars.size() - 1) + ch.raw.y) / currentLine.chars.size();
        }

        for (TextLine line : lines)
        {
            line.chars.sort(Comparator.comparingDouble(c -> c.raw.x));
        }
        return lines;
    }

    // =====================================================================
    // Markdown-Konvertierung
    // =====================================================================

    private String lineToMarkdown(TextLine line)
    {
        if (line.chars.isEmpty()) return "";

        // Spans bilden: neuer Span bei Wechsel von Bold/Italic/FontFamily/FontSize
        List<FormattedSpan> spans = new ArrayList<>();
        FormattedSpan currentSpan = null;

        for (ClassifiedChar ch : line.chars)
        {
            // Groessenwechsel innerhalb der gleichen SmallCaps-Font ignorieren
            // (SmallCaps haben groessere Buchstaben fuer Grossbuchstaben)
            boolean sameSmallCapsFont = currentSpan != null
                && ch.isSmallCaps && currentSpan.isSmallCaps
                && ch.family.equals(currentSpan.fontFamily);

            boolean newSpan = currentSpan == null
                || ch.isBold != currentSpan.bold
                || ch.isItalic != currentSpan.italic
                || (!sameSmallCapsFont && Math.abs(ch.raw.fontSize - currentSpan.fontSize) > 0.5f)
                || !ch.family.equals(currentSpan.fontFamily);

            // Leerzeichen bei grossem X-Abstand einfuegen
            if (currentSpan != null && !currentSpan.chars.isEmpty())
            {
                ClassifiedChar lastChar = currentSpan.chars.get(currentSpan.chars.size() - 1);
                float gap = ch.raw.x - (lastChar.raw.x + lastChar.raw.width);
                if (gap > lastChar.raw.width * 0.3f)
                {
                    currentSpan.text.append(" ");
                }
            }

            if (newSpan)
            {
                currentSpan = new FormattedSpan(ch.isBold, ch.isItalic, ch.raw.fontSize,
                    ch.family, ch.isSmallCaps, ch.isInitial, ch.isOrnament);
                spans.add(currentSpan);
            }
            currentSpan.chars.add(ch);
            currentSpan.text.append(ch.raw.text);
        }

        // SmallCaps-Korrektur: Grossbuchstaben anhand der fontSize erkennen
        for (FormattedSpan span : spans)
        {
            if (!span.isSmallCaps || span.chars.size() < 2) continue;

            // Kleinste fontSize im Span = Kleinbuchstaben-Groesse
            float minSize = span.chars.stream()
                .map(c -> c.raw.fontSize)
                .min(Float::compare).orElse(0f);

            // Nur korrigieren wenn es tatsaechlich unterschiedliche Groessen gibt
            float maxSize = span.chars.stream()
                .map(c -> c.raw.fontSize)
                .max(Float::compare).orElse(0f);
            if (maxSize - minSize < 1.0f) continue;

            // Text neu aufbauen mit korrekter Gross/Kleinschreibung
            StringBuilder fixed = new StringBuilder();
            for (ClassifiedChar ch : span.chars)
            {
                if (ch.raw.fontSize > minSize + 0.5f)
                {
                    fixed.append(ch.raw.text.toUpperCase());
                }
                else
                {
                    fixed.append(ch.raw.text);
                }
            }
            span.text.setLength(0);
            span.text.append(fixed);
        }

        // Markdown zusammenbauen
        StringBuilder result = new StringBuilder();

        // Heading-Erkennung
        float dominantSize = getDominantFontSize(spans);
        int totalChars = spans.stream().mapToInt(s -> s.text.toString().trim().length()).sum();
        int boldChars = spans.stream().filter(s -> s.bold)
            .mapToInt(s -> s.text.toString().trim().length()).sum();
        boolean allBold = totalChars > 0 && boldChars >= totalChars * 0.9;
        boolean isShortLine = totalChars < 60;

        // Initiale erkennen (einzelnes grosses Zeichen)
        boolean startsWithInitial = !spans.isEmpty() && spans.get(0).isInitial
            && spans.get(0).text.toString().trim().length() <= 2;

        // Heading-Unterdrueckung fuer Zeilen mit vielen grossen Gaps (Tabellen-Header)
        SplitResult headingGapCheck = splitLineAtGaps(line, GAP_THRESHOLD);
        boolean hasTableGaps = headingGapCheck != null && headingGapCheck.row.cells.size() >= 3;

        // Display-Schrift erkennen (Andalus = DSA-Heading-Schrift, deutlich groeszer
        // als typischer Body). GentiumBasic-Bold ist eine Sub-Heading-Schrift mit
        // kleinerer Pt-Groesze — sollte auf ### gehen, nicht ##.
        boolean inDisplayFont = false;
        for (FormattedSpan s : spans)
        {
            if (s.isOrnament) continue;
            String fam = s.fontFamily;
            if (fam != null && (fam.contains("Andalus") || fam.contains("Display")))
            {
                inDisplayFont = true;
                break;
            }
        }
        // Body-Continuation-Wächter: eine Heading-Kandidatin, die wie eine
        // Bindestrich-Wortfortsetzung oder Mid-Sentence-Bold aussieht, ist KEIN
        // Heading. Indikatoren:
        //   • Erster sichtbarer Buchstabe ist Kleinbuchstabe (z. B. "dex der Magie")
        //   • Letzter sichtbarer Char ist ein Komma (Mid-Sentence-Bold-Abbruch)
        //   • Letzter sichtbarer Char ist ein Bindestrich (Wortumbruch ohne Satzende)
        boolean looksLikeBodyContinuation = false;
        {
            String joinedTrimmed = spans.stream()
                    .filter(s -> !s.isOrnament)
                    .map(s -> s.text.toString())
                    .reduce("", String::concat)
                    .trim();
            if (!joinedTrimmed.isEmpty())
            {
                int firstLetterIdx = -1;
                for (int i = 0; i < joinedTrimmed.length(); i++)
                {
                    if (Character.isLetter(joinedTrimmed.charAt(i))) { firstLetterIdx = i; break; }
                }
                if (firstLetterIdx >= 0
                        && Character.isLowerCase(joinedTrimmed.charAt(firstLetterIdx)))
                {
                    looksLikeBodyContinuation = true;
                }
                char last = joinedTrimmed.charAt(joinedTrimmed.length() - 1);
                if (last == ',' || last == ';' || last == '-')
                {
                    looksLikeBodyContinuation = true;
                }
            }
        }

        // Attribut-Wächter: Bold-Body-Zeile mit Doppelpunkt ist ein Attribut
        // (z. B. "Anrufungsschwierigkeit: -4", "Sphärenkunde (Sphärenwesen):"),
        // KEINE Heading. Auch wenn der Wert versehentlich mit fett gesetzt wurde
        // (PDF-Editfehler), darf die Zeile nicht zur Sub-Heading promoviert werden,
        // sondern bleibt als Bold-Body-Text bestehen.
        boolean looksLikeBoldAttribute = false;
        if (allBold && dominantSize <= 11.5f && !inDisplayFont)
        {
            String joinedAttr = spans.stream()
                    .filter(s -> !s.isOrnament)
                    .map(s -> s.text.toString())
                    .reduce("", String::concat);
            if (joinedAttr.contains(":")) looksLikeBoldAttribute = true;
        }

        boolean isHeading = !hasTableGaps && !looksLikeBodyContinuation && !looksLikeBoldAttribute && (
            (dominantSize > 10.5f && allBold && isShortLine && (inDisplayFont || dominantSize > 14f))
            || (dominantSize > 15 && isShortLine));
        boolean isSubHeading = !hasTableGaps && !looksLikeBodyContinuation && !looksLikeBoldAttribute && (
            // Bold + groeszer als Body, aber unter Display-Schwelle: Sub-Sektion
            (dominantSize > 10.5f && allBold && isShortLine && !inDisplayFont && dominantSize <= 14f)
            // Klassischer Sub-Heading-Pfad (Bold im Bereich zwischen Body und 10.5pt)
            || (dominantSize > 8.5f && dominantSize <= 10.5f && allBold && isShortLine));
        // H1 fuer wirklich grosse Display-Headlines (Cover, Kapitel-Titel)
        boolean isH1 = false;

        // ALLCAPS-Display-Headings (z.B. "VORWORT", "IMPRESSUM", "KAPITEL 1: GRUNDREGELN").
        // Bei manchen Display-Fonts ist die deklarierte fontSize kleiner als die optisch
        // gerenderte Hoehe, weshalb die Standard-Schwellen (>15) nicht greifen — und bei
        // grossem Display-Tracking wird die Zeile faelschlich als Tabelle erkannt
        // (Buchstabenabstand > GAP_THRESHOLD). Beides muss diese Heuristik ueberstimmen.
        // Wichtig: Display-Glyphen mit fontSize > 30 werden frueher als isInitial markiert,
        // duerfen aber hier NICHT herausgefiltert werden — sonst sieht der Heading-Detector
        // grosse Display-Headlines gar nicht.
        if (!isHeading && !isSubHeading && isShortLine)
        {
            String joined = spans.stream()
                .filter(s -> !s.isOrnament)
                .map(s -> s.text.toString())
                .reduce("", String::concat)
                .trim();
            int letters = 0, uppers = 0;
            for (int i = 0; i < joined.length(); i++)
            {
                char c = joined.charAt(i);
                if (Character.isLetter(c))
                {
                    letters++;
                    if (Character.isUpperCase(c)) uppers++;
                }
            }
            boolean allCaps = letters >= 4 && uppers == letters;
            boolean endsWithColon = joined.endsWith(":");
            // Display-Schrift einheitlich? Dann sind grosse Gaps Letter-Tracking, nicht Tabelle.
            boolean uniformDisplay = false;
            if (!spans.isEmpty())
            {
                String firstFamily = spans.get(0).fontFamily;
                float firstSize = spans.get(0).fontSize;
                uniformDisplay = spans.stream()
                    .filter(s -> !s.isOrnament)
                    .allMatch(s -> firstFamily.equals(s.fontFamily)
                        && Math.abs(s.fontSize - firstSize) < 0.5f);
            }
            // Maximale Glyph-fontSize (Initiale werden im dominantSize sonst untergewichtet)
            float maxGlyphSize = 0f;
            for (FormattedSpan s : spans)
            {
                if (s.isOrnament) continue;
                if (s.fontSize > maxGlyphSize) maxGlyphSize = s.fontSize;
            }
            if (allCaps)
            {
                if (!endsWithColon && dominantSize > 11.5f && !hasTableGaps)
                {
                    isHeading = true;
                }
                else if (uniformDisplay && maxGlyphSize > 18f)
                {
                    // Display-ALLCAPS mit weitem Tracking — Tabellen-Gaps sind ein
                    // Mess-Artefakt, der Heading-Status ist eindeutig.
                    // Doppelpunkt erlaubt (z. B. "KAPITEL 1:" bei zweizeiligen Display-Headers).
                    isHeading = true;
                }
                // Sehr grosse Display-Caps -> H1 (Cover, Kapiteleroeffnung)
                if (isHeading && uniformDisplay && maxGlyphSize > 26f) isH1 = true;
            }
        }
        // Mixed-Case Display-Headings (z. B. "Kodex des Schwertes", "Komplexitaetsgrade
        // der Regeln") — wenn die Glyphen einheitlich gross sind, isH1 bei sehr grosser
        // Schrift, isHeading kommt vom Standardpfad.
        if (isHeading && !isH1 && !spans.isEmpty())
        {
            float maxGlyphSize2 = 0f;
            String firstFamily = spans.get(0).fontFamily;
            float firstSize = spans.get(0).fontSize;
            boolean uniformDisplay2 = true;
            for (FormattedSpan s : spans)
            {
                if (s.isOrnament) continue;
                if (s.fontSize > maxGlyphSize2) maxGlyphSize2 = s.fontSize;
                if (!firstFamily.equals(s.fontFamily) || Math.abs(s.fontSize - firstSize) >= 0.5f)
                {
                    uniformDisplay2 = false;
                }
            }
            if (uniformDisplay2 && maxGlyphSize2 > 26f) isH1 = true;
        }

        if (isH1) result.append("# ");
        else if (isHeading) result.append("## ");
        else if (isSubHeading) result.append("### ");

        for (int i = 0; i < spans.size(); i++)
        {
            FormattedSpan span = spans.get(i);
            String text = span.text.toString().trim();
            if (text.isEmpty()) continue;

            // Ornamente: ueberspringen oder in <span> wrappen
            if (span.isOrnament)
            {
                if (includeOrnaments)
                {
                    result.append("<span class=\"ornament\">").append(text).append("</span> ");
                }
                continue;
            }

            // Initiale: mit Folgetext verbinden (kein Zeilenumbruch)
            if (span.isInitial && i + 1 < spans.size())
            {
                // Initial-Buchstabe voranstellen, Markdown-Prefix kommt vom naechsten Span
                result.append(text);
                continue;
            }

            // SmallCaps = optisch wie Bold/Heading behandeln
            boolean effectiveBold = span.bold || span.isSmallCaps;

            if (effectiveBold && span.italic) result.append("***").append(text).append("*** ");
            else if (effectiveBold)
            {
                if (!isHeading && !isSubHeading && !isH1) result.append("**").append(text).append("** ");
                else result.append(text).append(" ");
            }
            else if (span.italic) result.append("*").append(text).append("* ");
            else result.append(text).append(" ");
        }

        // Aufeinanderfolgende gleiche Markdown-Marker zusammenfassen
        // z.B. **Novadis** **außerhalb** → **Novadis außerhalb**
        String md = result.toString().stripTrailing();
        md = md.replace("*** ***", " ");
        md = md.replace("** **", " ");
        md = md.replace("* *", " ");
        return md;
    }

    private float getDominantFontSize(List<FormattedSpan> spans)
    {
        float maxLen = 0;
        float dominantSize = 0;
        for (FormattedSpan span : spans)
        {
            if (span.text.length() > maxLen && !span.isInitial)
            {
                maxLen = span.text.length();
                dominantSize = span.fontSize;
            }
        }
        return dominantSize;
    }

    /**
     * Bestimmt die haeufigste Schriftgroesse auf der Seite (= Fliesstext).
     */
    private float findBodyFontSize(List<ClassifiedChar> chars)
    {
        java.util.Map<Integer, Integer> sizeCounts = new java.util.HashMap<>();
        for (ClassifiedChar ch : chars)
        {
            if (ch.isInitial) continue;
            int sizeKey = Math.round(ch.raw.fontSize);
            sizeCounts.merge(sizeKey, 1, Integer::sum);
        }
        return sizeCounts.entrySet().stream()
            .max(java.util.Map.Entry.comparingByValue())
            .map(e -> (float) e.getKey())
            .orElse(10.0f);
    }

    // =====================================================================
    // Stat-Value-Erkennung (Eigenschaftsblock, Waffenprofile)
    // =====================================================================

    /**
     * Baut FormattedSpans aus einer TextLine (extrahiert aus lineToMarkdown).
     */
    private List<FormattedSpan> buildFormattedSpans(TextLine line)
    {
        List<FormattedSpan> spans = new ArrayList<>();
        if (line.chars.isEmpty()) return spans;

        FormattedSpan currentSpan = null;
        for (ClassifiedChar ch : line.chars)
        {
            boolean sameSmallCapsFont = currentSpan != null
                && ch.isSmallCaps && currentSpan.isSmallCaps
                && ch.family.equals(currentSpan.fontFamily);

            boolean newSpan = currentSpan == null
                || ch.isBold != currentSpan.bold
                || ch.isItalic != currentSpan.italic
                || (!sameSmallCapsFont && Math.abs(ch.raw.fontSize - currentSpan.fontSize) > 0.5f)
                || !ch.family.equals(currentSpan.fontFamily);

            if (currentSpan != null && !currentSpan.chars.isEmpty())
            {
                ClassifiedChar lastChar = currentSpan.chars.get(currentSpan.chars.size() - 1);
                float gap = ch.raw.x - (lastChar.raw.x + lastChar.raw.width);
                if (gap > lastChar.raw.width * 0.3f)
                {
                    currentSpan.text.append(" ");
                }
            }

            if (newSpan)
            {
                currentSpan = new FormattedSpan(ch.isBold, ch.isItalic, ch.raw.fontSize,
                    ch.family, ch.isSmallCaps, ch.isInitial, ch.isOrnament);
                spans.add(currentSpan);
            }
            currentSpan.chars.add(ch);
            currentSpan.text.append(ch.raw.text);
        }

        // SmallCaps-Korrektur
        for (FormattedSpan span : spans)
        {
            if (!span.isSmallCaps || span.chars.size() < 2) continue;
            float minSize = span.chars.stream().map(c -> c.raw.fontSize).min(Float::compare).orElse(0f);
            float maxSize = span.chars.stream().map(c -> c.raw.fontSize).max(Float::compare).orElse(0f);
            if (maxSize - minSize < 1.0f) continue;
            StringBuilder fixed = new StringBuilder();
            for (ClassifiedChar ch : span.chars)
            {
                if (ch.raw.fontSize > minSize + 0.5f) fixed.append(ch.raw.text.toUpperCase());
                else fixed.append(ch.raw.text);
            }
            span.text.setLength(0);
            span.text.append(fixed);
        }

        return spans;
    }

    /**
     * Erkennt Stat-Value-Zeilen (Eigenschaftsblock, Waffenprofile).
     * Pattern: Alternierende kurze Bold/Normal-Spans.
     * Bold-Spans mit ":" werden am Doppelpunkt gesplittet (Waffenprofile).
     * Gibt Liste von Zellen zurueck oder null.
     */
    private List<String> tryBuildStatValueCells(TextLine line)
    {
        List<FormattedSpan> spans = buildFormattedSpans(line);
        if (spans.size() < 4) return null;

        List<String> cells = new ArrayList<>();
        int boldCells = 0, normalCells = 0, longCells = 0;
        int shortBoldKeys = 0; // Bold-Zellen <=5 Zeichen ohne ":"

        for (FormattedSpan span : spans)
        {
            String text = span.text.toString().trim();
            if (text.isEmpty()) continue;

            boolean effectiveBold = span.bold || span.isSmallCaps;

            if (effectiveBold && text.contains(":"))
            {
                int colonIdx = text.indexOf(':');
                String before = text.substring(0, colonIdx + 1).trim();
                String after = text.substring(colonIdx + 1).trim();
                if (!before.isEmpty())
                {
                    cells.add("**" + before + "**");
                    boldCells++;
                    if (before.length() > 20) longCells++;
                }
                if (!after.isEmpty())
                {
                    cells.add("**" + after + "**");
                    boldCells++;
                    if (after.length() <= 5) shortBoldKeys++;
                }
            }
            else if (effectiveBold)
            {
                cells.add("**" + text + "**");
                boldCells++;
                if (text.length() > 20) longCells++;
                if (text.length() <= 5) shortBoldKeys++;
            }
            else
            {
                cells.add(text);
                normalCells++;
                if (text.length() > 20) longCells++;
            }
        }

        // Validierung: mindestens 4 Zellen, Bold/Normal-Mix, kurze Texte
        if (cells.size() < 4) return null;
        if (boldCells < 2 || normalCells < 2) return null;
        if (longCells > 1) return null;
        if (shortBoldKeys < 2) return null; // Mindestens 2 kurze Bold-Keys (MU, KL, AT, PA...)

        return cells;
    }

    /**
     * Rendert gesammelte Stat-Value-Zeilen als Markdown-Tabelle mit leerer Kopfzeile.
     */
    private void flushStatRows(StringBuilder markdown, List<List<String>> statRows)
    {
        if (statRows.isEmpty()) return;

        // Tabelle braucht IMMER mindestens 2 Daten-Zeilen mit Inhalt — sonst ist es
        // keine Tabelle, sondern ein Body-Linien-Fragment, das durch Bold-Wechsel
        // zufaellig grosse Lucken hatte (z. B. "tails werden im Kapitel **Kampf** ab
        // Seite **61** vorgestellt." auf S16). In dem Fall keine Tabelle ausgeben,
        // sondern die Reihen als platten Text zusammenkleben.
        int nonEmptyRows = 0;
        for (List<String> row : statRows)
        {
            for (String c : row)
            {
                if (c != null && !c.trim().isEmpty()) { nonEmptyRows++; break; }
            }
            if (nonEmptyRows >= 2) break;
        }
        if (nonEmptyRows < 2)
        {
            // Fallback: Reihen als Fliesstext zusammenfuegen (Cells per Space, Reihen per Newline).
            for (List<String> row : statRows)
            {
                StringBuilder line = new StringBuilder();
                for (String c : row)
                {
                    if (c == null || c.trim().isEmpty()) continue;
                    if (line.length() > 0) line.append(' ');
                    line.append(c.trim());
                }
                if (line.length() > 0) markdown.append(line).append('\n');
            }
            statRows.clear();
            return;
        }

        int maxCols = statRows.stream().mapToInt(List::size).max().orElse(0);

        // Leere Kopfzeile
        markdown.append("| ");
        for (int c = 0; c < maxCols; c++)
        {
            if (c > 0) markdown.append(" | ");
            markdown.append(" ");
        }
        markdown.append(" |\n");

        // Separator
        markdown.append("| ");
        for (int c = 0; c < maxCols; c++)
        {
            if (c > 0) markdown.append(" | ");
            markdown.append("---");
        }
        markdown.append(" |\n");

        // Datenzeilen
        for (List<String> row : statRows)
        {
            while (row.size() < maxCols) row.add("");
            markdown.append("| ");
            for (int c = 0; c < row.size(); c++)
            {
                if (c > 0) markdown.append(" | ");
                markdown.append(row.get(c));
            }
            markdown.append(" |\n");
        }

        statRows.clear();
    }

    /**
     * Emittiert eine line-basierte Tabelle als Markdown. Die Tabelle wird direkt
     * aus den horizontalen Trennlinien des PDFs gebaut — Multi-line Cells und
     * mehrzeilige Header sind dadurch immer korrekt.
     */
    private void appendLineBasedTableAsMarkdown(StringBuilder markdown,
                                                LineBasedTableDetector.LineBasedTable table)
    {
        if (table == null || table.cells.isEmpty()) return;
        // Leerzeile vor der Tabelle, sonst rendert MD-Renderer sie nicht.
        if (markdown.length() > 0)
        {
            String tail = markdown.substring(Math.max(0, markdown.length() - 2));
            if (!tail.endsWith("\n\n"))
            {
                if (tail.endsWith("\n")) markdown.append('\n');
                else markdown.append("\n\n");
            }
        }
        int colCount = table.colCount();
        for (int r = 0; r < table.cells.size(); r++)
        {
            List<String> row = table.cells.get(r);
            markdown.append("| ");
            for (int c = 0; c < colCount; c++)
            {
                if (c > 0) markdown.append(" | ");
                String cell = c < row.size() ? row.get(c) : "";
                markdown.append(cell);
            }
            markdown.append(" |\n");
            if (r == 0)
            {
                markdown.append("| ");
                for (int c = 0; c < colCount; c++)
                {
                    if (c > 0) markdown.append(" | ");
                    markdown.append("---");
                }
                markdown.append(" |\n");
            }
        }
        // Leerzeile nach der Tabelle.
        markdown.append('\n');
    }

    /**
     * Berechnet den typischen Zeilenabstand (Median).
     */
    private float calculateTypicalLineSpacing(List<TextLine> lines)
    {
        if (lines.size() < 2) return 12.0f;
        List<Float> spacings = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++)
        {
            float spacing = (float) (lines.get(i).y - lines.get(i - 1).y);
            if (spacing > 0 && spacing < 30) spacings.add(spacing);
        }
        if (spacings.isEmpty()) return 12.0f;
        spacings.sort(Float::compareTo);
        return spacings.get(spacings.size() / 2);
    }

    // =====================================================================
    // Initiale-Erkennung: Grosse Zeichen mit Folgezeichen zusammenfuehren
    // =====================================================================

    /**
     * Findet grosse Zeichen (Initialen, dekorative Buchstaben) und prueft ob
     * nachfolgende Zeichen in deren Bounding-Box fallen. Wenn ja, werden die
     * Folgezeichen auf die gleiche Y-Position verschoben, damit sie beim
     * Zeilenaufbau zusammenbleiben.
     *
     * Universelle Regel: Oberkante = y - height. Wenn die Oberkante eines
     * Zeichens innerhalb des Y-Bereichs [top..bottom] eines grossen Zeichens
     * liegt UND das Zeichen in X-Richtung nach dem grossen Zeichen kommt
     * (oder ueberlappt), gehoert es zur selben logischen Zeile.
     */
    private void mergeInitialChars(List<RawPageData.RawChar> chars)
    {
        // Das groesste Zeichen pro Bereich finden (Anker-Initiale)
        // Sortiert nach fontSize absteigend
        List<RawPageData.RawChar> anchors = new ArrayList<>();
        for (RawPageData.RawChar ch : chars)
        {
            if (ch.fontSize > 25) anchors.add(ch);
        }
        if (anchors.isEmpty()) return;
        anchors.sort((a, b) -> Float.compare(b.fontSize, a.fontSize));

        // Fuer den groessten Anker: alle Zeichen die in seine Bounding-Box fallen
        // auf seine Y-Position setzen (auch andere grosse Zeichen wie Titeltext)
        for (RawPageData.RawChar anchor : anchors)
        {
            // Nur der allergroesste pro Y-Bereich ist ein Anker
            // (Titelzeichen werden vom groesseren Anker absorbiert)
            if (anchor.y != anchors.get(0).y && anchor.fontSize < anchors.get(0).fontSize)
            {
                continue; // Dieser wurde schon von einem groesseren Anker absorbiert
            }

            float anchorTop = anchor.y - anchor.height;
            float anchorBottom = anchor.y;

            for (RawPageData.RawChar ch : chars)
            {
                if (ch == anchor) continue;

                float chTop = ch.y - ch.height;

                // Y-Overlap: Oberkante des Zeichens liegt im Y-Bereich des Ankers
                boolean yOverlaps = chTop >= anchorTop - 2 && chTop <= anchorBottom;

                // X: Das Zeichen liegt rechts vom Anker-Start
                boolean xFollows = ch.x >= anchor.x;

                if (yOverlaps && xFollows)
                {
                    ch.y = anchor.y;
                }
            }
        }
    }

    // =====================================================================
    // Tabellenerkennung
    // =====================================================================

    /**
     * Gibt Zeilen als Markdown aus, erkennt dabei Tabellen und fasst
     * Fliesstext in Absaetze zusammen (mit Bindestrich-Entfernung).
     */
    private void appendLinesWithTables(StringBuilder markdown, List<TextLine> lines)
    {
        List<DetectedTable> tables = detectTables(lines);

        java.util.Set<Integer> tableLineIndices = new java.util.HashSet<>();
        java.util.Map<Integer, DetectedTable> tableStarts = new java.util.HashMap<>();
        for (DetectedTable table : tables)
        {
            for (int i = table.startLineIdx; i <= table.endLineIdx; i++)
                tableLineIndices.add(i);
            tableStarts.put(table.startLineIdx, table);
        }

        // Spalten-Rechtkante berechnen fuer Kurzzeilen-Erkennung
        float columnRightEdge = 0;
        for (TextLine line : lines)
        {
            for (ClassifiedChar ch : line.chars)
            {
                float right = ch.raw.x + ch.raw.width;
                if (right > columnRightEdge) columnRightEdge = right;
            }
        }

        // Typischer Zeilenabstand fuer Y-Gap-Erkennung
        float typicalSpacing = calculateTypicalLineSpacing(lines);

        // Einrueckungsanker: weniger eingerueckte Zeilen = strukturelle Marker
        List<Float> indentAnchors = detectIndentAnchors(lines);
        float bodyIndent = -1;
        if (indentAnchors.size() >= 2)
        {
            // Haeufigster Anker = Body-Text-Einrueckung
            int maxCount = 0;
            for (Float anchor : indentAnchors)
            {
                int count = 0;
                for (TextLine l : lines)
                {
                    if (!l.chars.isEmpty()
                        && Math.abs(l.chars.get(0).raw.x - anchor) < INDENT_TOLERANCE) count++;
                }
                if (count > maxCount) { maxCount = count; bodyIndent = anchor; }
            }
        }

        StringBuilder paragraph = new StringBuilder();
        List<List<String>> statRows = new ArrayList<>();
        int statRowColCount = -1;
        double prevLineY = Double.NaN;
        int i = 0;
        while (i < lines.size())
        {
            TextLine line = lines.get(i);

            // Pseudo-TextLine fuer line-basierte Tabelle (siehe LineBasedTableDetector)?
            // Direkt als Markdown-Tabelle emittieren und naechste Line.
            if (line.lineBasedTable != null)
            {
                flushStatRows(markdown, statRows);
                statRowColCount = -1;
                flushParagraph(markdown, paragraph);
                appendLineBasedTableAsMarkdown(markdown, line.lineBasedTable);
                prevLineY = line.y;
                i++;
                continue;
            }

            // Y-Gap-Erkennung: grosser Abstand → Absatz-/Blockgrenze
            if (!Double.isNaN(prevLineY) && typicalSpacing > 0)
            {
                double yGap = line.y - prevLineY;
                if (yGap > typicalSpacing * 2.0)
                {
                    flushStatRows(markdown, statRows);
                    statRowColCount = -1;
                    flushParagraph(markdown, paragraph);
                }
            }

            // Stat-Value-Erkennung (hoechste Prioritaet, ueberschreibt Tabellenerkennung)
            List<String> statCells = tryBuildStatValueCells(line);
            if (statCells != null)
            {
                // Spaltenanzahl-Wechsel → vorherige Stat-Tabelle abschliessen
                if (statRowColCount >= 0 && statCells.size() != statRowColCount)
                {
                    flushStatRows(markdown, statRows);
                }
                flushParagraph(markdown, paragraph);
                statRows.add(statCells);
                statRowColCount = statCells.size();
                prevLineY = line.y;
                i++;
                continue;
            }
            if (!statRows.isEmpty())
            {
                flushStatRows(markdown, statRows);
                statRowColCount = -1;
            }

            if (tableStarts.containsKey(i))
            {
                flushParagraph(markdown, paragraph);
                appendTableAsMarkdown(markdown, tableStarts.get(i));
                prevLineY = lines.get(tableStarts.get(i).endLineIdx).y;
                i = tableStarts.get(i).endLineIdx + 1;
            }
            else if (!tableLineIndices.contains(i))
            {
                // Einrueckung pruefen: weniger eingerueckt als Body = strukturelle Grenze
                if (bodyIndent > 0 && !line.chars.isEmpty())
                {
                    float lineXMin = line.chars.get(0).raw.x;
                    if (lineXMin < bodyIndent - INDENT_TOLERANCE)
                    {
                        flushStatRows(markdown, statRows);
                        statRowColCount = -1;
                        flushParagraph(markdown, paragraph);
                    }
                }

                String md = lineToMarkdown(line);

                boolean isHeading = md.startsWith("#");
                boolean isComment = md.startsWith("<!--");
                boolean isEmpty = md.trim().isEmpty();
                boolean isItalicLine = md.startsWith("*") && !md.startsWith("**");

                if (isHeading || isComment || isEmpty)
                {
                    flushParagraph(markdown, paragraph);
                    markdown.append(md).append("\n");
                    if (isHeading && collectingBodyHeadings)
                    {
                        String text = md.replaceFirst("^#+\\s*", "").strip();
                        if (!text.isEmpty())
                        {
                            lastBodyHeadings.add(new HeadingAnchor((float) line.y, text));
                        }
                    }
                    prevLineY = line.y;
                    i++;
                    continue;
                }

                // Listen-Item-Anfang: Zeile liegt y- und x-naehe an einem Bullet-Anker.
                // Vorhergehende Paragraph schliessen, Zeile mit "- " praefixieren.
                double firstX = line.chars.isEmpty() ? Double.MAX_VALUE : line.chars.get(0).raw.x;
                boolean isListItemStart = isInBulletAnchor(line.y, firstX);
                if (isListItemStart)
                {
                    flushParagraph(markdown, paragraph);
                    md = "- " + md.stripLeading();
                }

                // Zeile beginnt mit Bold → vorherigen Absatz abschliessen
                boolean startsBold = md.startsWith("**");
                if (startsBold && paragraph.length() > 0)
                {
                    flushParagraph(markdown, paragraph);
                }

                // Format-Wechsel (italic ↔ nicht-italic) als Absatzgrenze NUR, wenn
                // der vorherige Absatz mit echtem Satzende-Zeichen endet. Sonst ist es
                // typisch eine durch Bindestrich getrennte Italic-Wort-Fortsetzung
                // ("*Bestätigungs-*" + "*wurf*") und gehoert zum gleichen Absatz.
                String prevTrimmed = paragraph.length() > 0 ? paragraph.toString().strip() : "";
                boolean prevIsItalic = isWhollyItalic(prevTrimmed);
                boolean prevEndsSentence = endsWithFinalPunctuation(prevTrimmed);
                if (paragraph.length() > 0 && isItalicLine != prevIsItalic && prevEndsSentence)
                {
                    flushParagraph(markdown, paragraph);
                }

                // Italic-Format-Wechsel ueber Paragraph-Grenzen hinweg → Blank-Line
                // einschieben (z. B. zwischen Body-Text und einem *Beispiel:*-Block).
                // Auch hier: nur wenn der zuletzt geflushte Inhalt mit Satzende endet
                // (sonst floss der Absatz noch unfertig in den naechsten weiter, und
                // ein zusaetzlicher Blank-Line zerreisst eine Wort-Fortsetzung).
                if (paragraph.length() == 0
                        && isItalicLine != lastFlushedItalic
                        && markdown.length() > 0
                        && lastFlushEndedSentence)
                {
                    char last = markdown.charAt(markdown.length() - 1);
                    boolean alreadyBlank = markdown.length() >= 2
                            && markdown.charAt(markdown.length() - 2) == '\n'
                            && last == '\n';
                    if (last == '\n' && !alreadyBlank)
                    {
                        markdown.append('\n');
                    }
                }

                // An bestehenden Absatz anfuegen
                if (paragraph.length() > 0)
                {
                    String prev = paragraph.toString().stripTrailing();
                    String next = md.stripLeading();

                    // Bindestrich am Zeilenende entfernen (auch mit trailing * Markern)
                    String prevClean = prev;
                    String trailingMarkers = "";
                    while (prevClean.endsWith("*") || prevClean.endsWith(" "))
                    {
                        trailingMarkers = prevClean.charAt(prevClean.length() - 1) + trailingMarkers;
                        prevClean = prevClean.substring(0, prevClean.length() - 1);
                    }

                    if (prevClean.length() > 1 && prevClean.endsWith("-") && !prevClean.endsWith("–")
                        && !prevClean.endsWith("—") && Character.isLetter(prevClean.charAt(prevClean.length() - 2))
                        && !next.isEmpty() && Character.isLowerCase(next.charAt(0)))
                    {
                        paragraph.setLength(0);
                        String withoutHyphen = prevClean.substring(0, prevClean.length() - 1);
                        if (trailingMarkers.isBlank())
                        {
                            paragraph.append(withoutHyphen).append(next);
                        }
                        else
                        {
                            // Wort-Fortsetzung in die Formatierung aufnehmen
                            int wordEnd = 0;
                            while (wordEnd < next.length() && Character.isLetter(next.charAt(wordEnd))) wordEnd++;
                            String wordCont = next.substring(0, wordEnd);
                            String rest = next.substring(wordEnd).stripLeading();
                            paragraph.append(withoutHyphen).append(wordCont).append(trailingMarkers.strip());
                            if (!rest.isEmpty()) paragraph.append(" ").append(rest);
                        }
                    }
                    else
                    {
                        paragraph.setLength(0);
                        paragraph.append(prev).append(" ").append(next);
                    }
                }
                else
                {
                    paragraph.append(md);
                }

                // Absatzende? Kurze Zeile + finales Satzzeichen
                boolean shortLine = isShortLine(line, columnRightEdge);
                boolean endsPunct = endsWithFinalPunctuation(paragraph.toString());
                if (shortLine && endsPunct)
                {
                    flushParagraph(markdown, paragraph);
                }

                prevLineY = line.y;
                i++;
            }
            else
            {
                prevLineY = line.y;
                i++;
            }
        }
        flushStatRows(markdown, statRows);
        flushParagraph(markdown, paragraph);
    }

    private void flushParagraph(StringBuilder markdown, StringBuilder paragraph)
    {
        if (paragraph.length() > 0)
        {
            String text = paragraph.toString().replaceAll("  +", " ").strip();
            // Kursiv-Spans zusammenfuehren: *text1* *text2* → *text1 text2*
            while (text.contains("* *"))
            {
                text = text.replace("* *", " ");
            }
            if (!text.isEmpty())
            {
                // Soft-Linebreak (zwei Trailing-Spaces) NUR nach echtem Satzende.
                // Andernfalls wuerde an einer Stelle ohne Satzende (z. B. am
                // Seitenumbruch mitten im Satz) faelschlich ein Linebreak landen.
                boolean endsSentence = endsWithFinalPunctuation(text);
                String separator = endsSentence ? "  \n" : "\n";
                markdown.append(text).append(separator);
                lastFlushedItalic = isWhollyItalic(text);
                lastFlushEndedSentence = endsSentence;
            }
            paragraph.setLength(0);
        }
    }

    private static boolean isWhollyItalic(String text)
    {
        return text.startsWith("*") && !text.startsWith("**")
                && text.endsWith("*") && !text.endsWith("**");
    }

    /**
     * Versucht aus dem Box-Markdown eine sinnvolle Heading-Bezeichnung zu ziehen
     * (fuer Dateinamen). Reihenfolge: erste #-Heading-Zeile, sonst erste **bold**-Zeile,
     * sonst erste nicht-leere Zeile. Markdown-Marker werden entfernt, Laenge auf 60 gekappt.
     */
    /**
     * Sammelt kleine Icon-Bilder (3-15 pt) on-page — Vorstufe fuer
     * Listen-Bullet- und Eye-Glyph-Detection.
     */
    static List<RawPageData.RawImage> findSmallIconImages(RawPageData page)
    {
        List<RawPageData.RawImage> result = new ArrayList<>();
        if (page.images == null) return result;
        for (RawPageData.RawImage img : page.images)
        {
            float w = Math.abs(img.width);
            float h = Math.abs(img.height);
            if (w < 3f || w > 15f) continue;
            if (h < 3f || h > 15f) continue;
            if (img.x < 0 || img.y < 0) continue;
            if (img.x > page.pageWidth || img.y > page.pageHeight) continue;
            result.add(img);
        }
        return result;
    }

    /**
     * Gruppiert kleine Bilder nach x-Spalte (toleranz {@code xTolerance}).
     * Bilder in derselben Spalte werden zur selben Cluster-Liste zusammengefasst,
     * sortiert nach x. Gleiche x-Werte → gleicher Cluster.
     */
    static List<List<RawPageData.RawImage>> clusterIconsByXColumn(
            List<RawPageData.RawImage> icons, float xTolerance)
    {
        List<RawPageData.RawImage> sorted = new ArrayList<>(icons);
        sorted.sort(Comparator.comparingDouble(i -> i.x));
        List<List<RawPageData.RawImage>> clusters = new ArrayList<>();
        for (RawPageData.RawImage img : sorted)
        {
            boolean placed = false;
            for (List<RawPageData.RawImage> c : clusters)
            {
                float clusterX = c.get(0).x;
                if (Math.abs(img.x - clusterX) <= xTolerance)
                {
                    c.add(img);
                    placed = true;
                    break;
                }
            }
            if (!placed)
            {
                List<RawPageData.RawImage> nc = new ArrayList<>();
                nc.add(img);
                clusters.add(nc);
            }
        }
        return clusters;
    }

    /**
     * Erkennt Auge-Glyph-Kandidaten auf einer Seite: kleine Bilder (Breite 8-15 pt,
     * Hoehe 4-9 pt) mit gesetztem {@code origin} (= Duplikat eines Master-Bilds), die
     * NICHT Teil eines vertikalen Bullet-Clusters sind (Cluster &ge; 2 in gleicher
     * x-Spalte gelten als Listen-Bullets, nicht als Box-Verweis).
     */
    static List<RawPageData.RawImage> findEyeGlyphCandidates(RawPageData page)
    {
        List<RawPageData.RawImage> small = findSmallIconImages(page);
        List<List<RawPageData.RawImage>> clusters = clusterIconsByXColumn(small, 5f);
        List<RawPageData.RawImage> result = new ArrayList<>();
        for (List<RawPageData.RawImage> c : clusters)
        {
            if (c.size() != 1) continue;       // Cluster mit >=2 → Listen-Bullets
            RawPageData.RawImage img = c.get(0);
            if (img.origin == null || img.origin.isBlank()) continue;
            float w = Math.abs(img.width);
            float h = Math.abs(img.height);
            if (w < 8f || w > 15f) continue;
            if (h < 4f || h > 9f) continue;
            result.add(img);
        }
        return result;
    }

    /**
     * Liefert die Y-Bereiche (yMin, yMax) der erkannten Listen-Bullets auf der Seite.
     * Eine Body-Zeile, deren y in einem dieser Bereiche liegt, ist der Anfang eines
     * Listen-Items und wird als {@code - ...} gerendert.
     */
    static List<float[]> computeBulletAnchorRanges(RawPageData page)
    {
        List<RawPageData.RawImage> small = findSmallIconImages(page);
        List<List<RawPageData.RawImage>> clusters = clusterIconsByXColumn(small, 5f);
        List<float[]> result = new ArrayList<>();
        for (List<RawPageData.RawImage> c : clusters)
        {
            if (c.size() < 2) continue;
            // Zusaetzlich: verifizieren, dass die Cluster-Mitglieder vertikal
            // beabstandet sind (mindestens 8 pt) — verhindert, dass irrtuemlich
            // ueberlagernde Glyphen zur Liste werden. Obergrenze entfaellt: Listen
            // mit langen Items koennen >100pt Abstand zwischen Bullets haben.
            c.sort(Comparator.comparingDouble(i -> i.y));
            boolean validSpacing = true;
            for (int i = 1; i < c.size(); i++)
            {
                float dy = c.get(i).y - c.get(i - 1).y;
                if (dy < 8f) { validSpacing = false; break; }
            }
            if (!validSpacing) continue;
            for (RawPageData.RawImage img : c)
            {
                float h = Math.abs(img.height);
                float w = Math.abs(img.width);
                // X-Range: Bullet sitzt typischerweise leicht links vom Text-Anfang.
                // Body-Zeile mit firstCharX in [bullet.x - 8, bullet.x + w + 20] gilt als Item-Start.
                result.add(new float[]{
                    img.x - 8f, img.x + w + 20f,
                    img.y - 3f, img.y + h + 3f
                });
            }
        }
        return result;
    }

    /**
     * Prueft, ob eine Body-Zeile (an y-Position {@code lineY} und mit erstem
     * Zeichen bei {@code firstCharX}) auf einen Listen-Bullet trifft.
     */
    private boolean isInBulletAnchor(double lineY, double firstCharX)
    {
        for (float[] r : currentBulletAnchors)
        {
            if (firstCharX >= r[0] && firstCharX <= r[1]
                    && lineY >= r[2] && lineY <= r[3]) return true;
        }
        return false;
    }

    /**
     * Pruef, ob das Box-Markdown eine echte Heading-Zeile (Markdown {@code #}) enthaelt.
     */
    static boolean hasExplicitHeading(String boxMd)
    {
        if (boxMd == null) return false;
        for (String raw : boxMd.split("\n"))
        {
            if (raw.strip().startsWith("#")) return true;
        }
        return false;
    }

    /**
     * Strenger als {@link #hasExplicitHeading}: liefert nur dann true, wenn
     * die ERSTE Inhalts-Zeile eine echte Heading-Zeile ist. Damit erkennen wir
     * Profilkaesten ohne Titel — der Wertblock fuer "Sharbazz" auf S66
     * beginnt z. B. mit "**Groesse:**" und nicht mit "### Sharbazz", die
     * Kreatur-Heading liegt auf der vorigen Seite. Solche Boxen sollten ihre
     * Heading per Body-Lookup beziehen.
     */
    static boolean hasTitleHeading(String boxMd)
    {
        if (boxMd == null) return false;
        for (String raw : boxMd.split("\n"))
        {
            String l = raw.strip();
            if (l.isEmpty() || l.startsWith("<!--")) continue;
            return l.startsWith("#");
        }
        return false;
    }

    /**
     * Liefert den Heading-Text aus dem Body, auf den der Kasten per Auge-Symbol
     * verweist. Da das Auge-Symbol nicht in jeder PDF-Variante als Bild oder Glyph
     * extrahierbar ist (manche Buecher zeichnen es als Vektor-Pfad), fungiert
     * geometrische Naehe als robuster Primaer-Heuristik:
     *
     * <ol>
     *   <li>Wenn Auge-Bilder erkannt wurden (z. B. Schwertes p11_img35-Duplikate):
     *       waehle das Auge naechstgelegen zur Box, dann das Heading mit kleinstem
     *       (eye.y - heading.y) bei heading.y &le; eye.y, sonst |dy|-naechstes.
     *   <li>Sonst: waehle das Body-Heading mit kleinster Manhattan-Distanz zum
     *       Box-Mittelpunkt.
     * </ol>
     *
     * @return Heading-Text oder {@code null}, wenn keine Zuordnung moeglich.
     */
    /**
     * Rendert eine Box ueber den vollen interpretPage-Pfad: erzeugt eine
     * synthetische Sub-Page mit den Box-Chars/Rects/Images und ruft eine
     * frische TextInterpreter-Instanz darauf auf. Damit gilt im Box-Inneren
     * dieselbe Pipeline wie im Body — Spalten-Histogramm, Line-Tabellen,
     * Fullwidth-Headings, Listen-Erkennung.
     *
     * <p>Filterregeln fuer die Sub-Page:
     * <ul>
     *   <li>Rects/Images werden nur uebernommen, wenn ihr Mittelpunkt in den
     *       Box-Bounds liegt (sonst koennte der Sub-Aufruf wieder dieselbe
     *       Box als Container detektieren).</li>
     *   <li>pageWidth/pageHeight bleiben die der Parent-Page — so funktioniert
     *       das Spalten-Histogramm, weil Box-Chars absolute X-Koords haben.</li>
     *   <li>Inline-Box-Marker werden im Sub-Renderer aktiviert, falls die Box
     *       wiederum innere (rekursive) Boxen enthaelt — derzeit selten.</li>
     * </ul>
     */
    private String renderBoxAsSubPage(de.pho.dsapdfreader.book.BoxExtractor.BoxRegion box,
                                      RawPageData parentPage)
    {
        RawPageData sub = new RawPageData();
        sub.pageNumber = parentPage.pageNumber;
        sub.pageWidth = parentPage.pageWidth;
        sub.pageHeight = parentPage.pageHeight;
        sub.chars = new ArrayList<>(box.chars);

        float bxL = box.x, bxR = box.x + box.width;
        float byT = box.y, byB = box.y + box.height;

        sub.rects = new ArrayList<>();
        if (parentPage.rects != null)
        {
            for (RawPageData.RawRect r : parentPage.rects)
            {
                // Nur Linien-Rects in die Sub-Page durchreichen — gefuellte Box-
                // Rects wuerden vom BoxExtractor des Sub-Aufrufs erneut als Box
                // erkannt (rekursive Selbst-Detektion). Wir wollen nur die
                // Tabellen-Trennlinien fuer LineBasedTableDetector behalten.
                if (!r.isLine) continue;
                float cx = r.x + r.width / 2f;
                float cy = r.y + r.height / 2f;
                if (cx < bxL || cx > bxR || cy < byT || cy > byB) continue;
                sub.rects.add(r);
            }
        }
        sub.images = new ArrayList<>();
        if (parentPage.images != null)
        {
            for (RawPageData.RawImage img : parentPage.images)
            {
                float cx = img.x + img.width / 2f;
                float cy = img.y + img.height / 2f;
                if (cx >= bxL && cx <= bxR && cy >= byT && cy <= byB)
                    sub.images.add(img);
            }
        }

        TextInterpreter sub_ti = new TextInterpreter();
        sub_ti.setEmitBoxesInline(true); // innere Boxen inline halten
        return sub_ti.interpretPage(sub);
    }

    static String resolveBoxHeadingFromBody(de.pho.dsapdfreader.book.BoxExtractor.BoxRegion box,
                                            List<RawPageData.RawImage> eyeGlyphs,
                                            List<HeadingAnchor> bodyHeadings)
    {
        if (bodyHeadings.isEmpty()) return null;

        // Pfad A: Auge erkannt — finde das naechstgelegene Auge zur Box, dann
        // das vorhergehende Heading. Trauen wir dem Auge nur, wenn es nahe an
        // der Box sitzt — sonst gehoert es zu einer anderen Box.
        final float MAX_EYE_TO_BOX_DIST = 500f;
        if (!eyeGlyphs.isEmpty())
        {
            RawPageData.RawImage bestEye = null;
            float bestDist = Float.MAX_VALUE;
            for (RawPageData.RawImage eye : eyeGlyphs)
            {
                float dx = horizontalDistance(eye.x, box.x, box.x + box.width);
                float dy = verticalDistance(eye.y, box.y, box.y + box.height);
                float dist = dx + dy;
                if (dist < bestDist) { bestDist = dist; bestEye = eye; }
            }
            if (bestEye != null && bestDist <= MAX_EYE_TO_BOX_DIST)
            {
                HeadingAnchor preceding = null;
                for (HeadingAnchor h : bodyHeadings)
                {
                    if (h.y <= bestEye.y && (preceding == null || h.y > preceding.y))
                        preceding = h;
                }
                if (preceding != null && (bestEye.y - preceding.y) <= 200f)
                    return preceding.text;
            }
        }

        // Pfad B (Default und Fallback): naechstgelegenes Body-Heading per
        // Manhattan-Distanz zum Box-Mittelpunkt.
        float cx = box.x + box.width / 2f;
        float cy = box.y + box.height / 2f;
        HeadingAnchor closest = null;
        float closestDist = Float.MAX_VALUE;
        for (HeadingAnchor h : bodyHeadings)
        {
            float dist = Math.abs(h.y - cy);
            if (dist < closestDist) { closestDist = dist; closest = h; }
        }
        return closest == null ? null : closest.text;
    }

    private static float horizontalDistance(float px, float rx0, float rx1)
    {
        if (px < rx0) return rx0 - px;
        if (px > rx1) return px - rx1;
        return 0f;
    }

    private static float verticalDistance(float py, float ry0, float ry1)
    {
        if (py < ry0) return ry0 - py;
        if (py > ry1) return py - ry1;
        return 0f;
    }

    static String guessBoxHeading(String boxMd)
    {
        if (boxMd == null) return "";
        String[] lines = boxMd.split("\n");
        // 1) Heading-Zeile
        for (String raw : lines)
        {
            String l = raw.strip();
            if (l.startsWith("#"))
            {
                String h = l.replaceFirst("^#+\\s*", "").strip();
                if (!h.isEmpty()) return clipHeading(h);
            }
        }
        // 2) Vollstaendig-Bold-Zeile (z. B. "**Ungewohnter Einsatz von Talenten**")
        for (String raw : lines)
        {
            String l = raw.strip();
            if (l.startsWith("**") && l.endsWith("**") && l.length() > 4)
            {
                String h = l.substring(2, l.length() - 2).strip();
                if (!h.isEmpty() && !h.contains("**")) return clipHeading(h);
            }
        }
        // 3) Erste nicht-leere Zeile
        for (String raw : lines)
        {
            String l = raw.strip();
            if (l.isEmpty() || l.startsWith("<!--")) continue;
            String h = l.replaceAll("\\*+", "").strip();
            if (!h.isEmpty()) return clipHeading(h);
        }
        return "";
    }

    private static String clipHeading(String h)
    {
        // Doppelpunkt am Ende stoert in Dateinamen
        h = h.replaceAll("[:\\u00BB\\u00AB\\u201C\\u201D]+$", "").strip();
        if (h.length() > 60) h = h.substring(0, 60).strip();
        return h;
    }

    private boolean isShortLine(TextLine line, float columnRightEdge)
    {
        if (line.chars.isEmpty() || columnRightEdge <= 0) return true;
        ClassifiedChar lastChar = line.chars.get(line.chars.size() - 1);
        float lineEnd = lastChar.raw.x + lastChar.raw.width;
        float margin = columnRightEdge * 0.12f;
        return lineEnd < columnRightEdge - margin;
    }

    private boolean endsWithFinalPunctuation(String text)
    {
        String stripped = text.stripTrailing();
        if (stripped.isEmpty()) return false;
        // Markdown-Formatierung am Ende ignorieren
        stripped = stripped.replaceAll("[* ]+$", "").stripTrailing();
        if (stripped.isEmpty()) return false;
        char last = stripped.charAt(stripped.length() - 1);
        return last == '.' || last == '!' || last == '?' || last == '»' || last == '«'
            || last == '\u00BB' || last == '\u00AB';
    }

    /**
     * Formatiert eine erkannte Tabelle als Markdown-Tabelle.
     */
    private void appendTableAsMarkdown(StringBuilder markdown, DetectedTable table)
    {
        if (table.rows.isEmpty()) return;

        // Markdown-Tabellen brauchen eine Leerzeile vor der Header-Zeile, sonst
        // werden sie nicht als Tabelle gerendert.
        if (markdown.length() > 0)
        {
            String tail = markdown.substring(Math.max(0, markdown.length() - 2));
            if (!tail.endsWith("\n\n"))
            {
                if (tail.endsWith("\n")) markdown.append('\n');
                else markdown.append("\n\n");
            }
        }

        // Spaltenanzahl normalisieren
        int maxCols = table.rows.stream().mapToInt(r -> r.cells.size()).max().orElse(0);

        for (int rowIdx = 0; rowIdx < table.rows.size(); rowIdx++)
        {
            TableRow row = table.rows.get(rowIdx);
            while (row.cells.size() < maxCols) row.cells.add("");

            markdown.append("| ");
            for (int c = 0; c < row.cells.size(); c++)
            {
                if (c > 0) markdown.append(" | ");
                markdown.append(row.cells.get(c));
            }
            markdown.append(" |\n");

            // Separator nach Header-Zeile
            if (rowIdx == 0)
            {
                markdown.append("| ");
                for (int c = 0; c < row.cells.size(); c++)
                {
                    if (c > 0) markdown.append(" | ");
                    markdown.append("---");
                }
                markdown.append(" |\n");
            }
        }
        // Leerzeile nach der Tabelle, damit ein direkt folgender Absatz NICHT als
        // Cell-Continuation der letzten Zeile in den MD-Renderer landet.
        markdown.append('\n');
    }

    /**
     * Erkennt Tabellen in einer Liste von Zeilen.
     * Die erste erkannte Zeile definiert die Gap-Positionen (Spalten-Referenz).
     * Folgezeilen werden anhand dieser Referenz-Positionen aufgesplittet,
     * auch wenn ihre Gaps an leicht anderen Stellen liegen.
     */
    private List<DetectedTable> detectTables(List<TextLine> lines)
    {
        List<DetectedTable> tables = new ArrayList<>();
        List<TableRow> currentRows = new ArrayList<>();
        List<float[]> currentGapEnds = new ArrayList<>();
        List<Integer> currentRowLineIdx = new ArrayList<>();
        float[] referenceGapEnds = null; // Gap-Positionen der Header-Zeile
        int firstIdx = -1;
        int lastIdx = -1;
        int tableColCount = 0;
        boolean hadContinuation = false;

        for (int i = 0; i < lines.size(); i++)
        {
            TextLine line = lines.get(i);
            SplitResult split = splitLineAtGaps(line, GAP_THRESHOLD);

            if (split != null)
            {
                hadContinuation = false;

                if (currentRows.isEmpty())
                {
                    // Header-Recovery: pruefe ob die direkt vorhergehende Line ein
                    // bold Tabellen-Header ist, der durch zu kleine Gaps nicht selbst
                    // als Tabellenzeile erkannt wurde (z.B. "Gift Stufe Art Widerstand
                    // Beginn Dauer Preis" mit Header-Worten enger gesetzt als Daten).
                    int headerStart = i;
                    if (i > 0)
                    {
                        TextLine prev = lines.get(i - 1);
                        if (isBoldHeaderCandidate(prev))
                        {
                            // Force-split den Header an den Daten-Cell-X-Positionen.
                            // Damit auch dicht gesetzte Header ("Stufe Art" mit 12pt Lücke)
                            // korrekt in die Datenzeilen-Cells aufgeteilt werden.
                            SplitResult headerSplit = forceSplitAtPositions(prev, split.gapEnds);
                            if (headerSplit != null
                                && headerSplit.row.cells.size() == split.row.cells.size())
                            {
                                currentRows.add(headerSplit.row);
                                currentGapEnds.add(headerSplit.gapEnds);
                                currentRowLineIdx.add(i - 1);
                                headerStart = i - 1;
                            }
                        }
                    }
                    // Erste Zeile definiert die Referenz-Positionen
                    currentRows.add(split.row);
                    currentGapEnds.add(split.gapEnds);
                    currentRowLineIdx.add(i);
                    referenceGapEnds = split.gapEnds;
                    firstIdx = headerStart;
                    lastIdx = i;
                    tableColCount = split.row.cells.size();
                }
                else if (split.row.cells.size() == tableColCount
                         && gapPositionsAlign(split.gapEnds, referenceGapEnds, GAP_POS_TOLERANCE))
                {
                    // Gleiche Spaltenanzahl UND Gap-Positionen passen zur Referenz
                    currentRows.add(split.row);
                    currentGapEnds.add(split.gapEnds);
                    currentRowLineIdx.add(i);
                    lastIdx = i;
                }
                else if (currentRows.size() == 1 && split.row.cells.size() > tableColCount)
                {
                    // Header hat zu wenige Cells (Header-Worte stehen enger zusammen als
                    // Daten-Cells). Refit den Header anhand der Datenzeile-Gap-Positionen
                    // (force-split). Behebt z. B. "Gift Stufe Art Widerstand Beginn Dauer
                    // Preis" wo "Stufe Art" nur 12pt Lücke hat, Daten aber 27pt.
                    SplitResult headerRefit = forceSplitAtPositions(lines.get(firstIdx), split.gapEnds);
                    if (headerRefit != null && headerRefit.row.cells.size() == split.row.cells.size())
                    {
                        currentRows.set(0, headerRefit.row);
                        currentGapEnds.set(0, headerRefit.gapEnds);
                        referenceGapEnds = split.gapEnds;
                        tableColCount = split.row.cells.size();
                        currentRows.add(split.row);
                        currentGapEnds.add(split.gapEnds);
                        currentRowLineIdx.add(i);
                        lastIdx = i;
                        continue;
                    }
                    // Refit fehlgeschlagen — fall through zur Standard-Behandlung
                    // Andere Spaltenanzahl oder Gap-Positionen passen nicht:
                    // versuche mit Referenz-Positionen aufzuteilen
                    SplitResult refSplit = splitLineByReference(line, referenceGapEnds);
                    if (refSplit != null && refSplit.row.cells.size() == tableColCount
                        && gapPositionsAlign(refSplit.gapEnds, referenceGapEnds, GAP_POS_TOLERANCE))
                    {
                        currentRows.add(refSplit.row);
                        currentGapEnds.add(refSplit.gapEnds);
                        currentRowLineIdx.add(i);
                        lastIdx = i;
                    }
                    else
                    {
                        if (currentRows.size() >= MIN_TABLE_ROWS
                            && hasMultipleNonEmptyRows(currentRows)
                            && checkGapConsistency(currentGapEnds)
                            && !isProbablyColumnFlow(currentRows, tableColCount))
                        {
                            tables.add(new DetectedTable(currentRows, firstIdx, lastIdx,
                                    new ArrayList<>(currentRowLineIdx)));
                        }
                        currentRows = new ArrayList<>();
                        currentRows.add(split.row);
                        currentGapEnds = new ArrayList<>();
                        currentGapEnds.add(split.gapEnds);
                        currentRowLineIdx = new ArrayList<>();
                        currentRowLineIdx.add(i);
                        referenceGapEnds = split.gapEnds;
                        firstIdx = i;
                        lastIdx = i;
                        tableColCount = split.row.cells.size();
                    }
                }
                else
                {
                    // Andere Spaltenanzahl oder Gap-Positionen passen nicht:
                    // versuche mit Referenz-Positionen aufzuteilen
                    SplitResult refSplit = splitLineByReference(line, referenceGapEnds);
                    if (refSplit != null && refSplit.row.cells.size() == tableColCount
                        && gapPositionsAlign(refSplit.gapEnds, referenceGapEnds, GAP_POS_TOLERANCE))
                    {
                        currentRows.add(refSplit.row);
                        currentGapEnds.add(refSplit.gapEnds);
                        currentRowLineIdx.add(i);
                        lastIdx = i;
                    }
                    else if (split.row.cells.size() >= tableColCount - 2 && split.row.cells.size() >= 3
                             && gapPositionsAlign(split.gapEnds, referenceGapEnds, GAP_POS_TOLERANCE))
                    {
                        // Nahe Spaltenanzahl mit passenden Gap-Positionen: akzeptieren und auffuellen
                        currentRows.add(split.row);
                        currentGapEnds.add(split.gapEnds);
                        currentRowLineIdx.add(i);
                        lastIdx = i;
                    }
                    else
                    {
                        if (currentRows.size() >= MIN_TABLE_ROWS
                            && hasMultipleNonEmptyRows(currentRows)
                            && checkGapConsistency(currentGapEnds)
                            && !isProbablyColumnFlow(currentRows, tableColCount))
                        {
                            tables.add(new DetectedTable(currentRows, firstIdx, lastIdx,
                                    new ArrayList<>(currentRowLineIdx)));
                        }
                        currentRows = new ArrayList<>();
                        currentRows.add(split.row);
                        currentGapEnds = new ArrayList<>();
                        currentGapEnds.add(split.gapEnds);
                        currentRowLineIdx = new ArrayList<>();
                        currentRowLineIdx.add(i);
                        referenceGapEnds = split.gapEnds;
                        firstIdx = i;
                        lastIdx = i;
                        tableColCount = split.row.cells.size();
                    }
                }
            }
            else
            {
                // Kein Gap mit Standard-Threshold: versuche mit Referenz-Positionen
                if (!currentRows.isEmpty() && referenceGapEnds != null)
                {
                    // Y-Spacing-Wächter: wenn die Zeile deutlich weiter weg ist als
                    // typischer Tabellen-Zeilenabstand, gehoert sie nicht mehr zur
                    // Tabelle. Verhindert, dass Heading- oder Body-Zeilen via
                    // refSplit/lowSplit/forceSplit in die Tabelle "hineingezwungen"
                    // werden (S22: Heading "Anzahl der erlaubten Proben" 25pt nach
                    // Tabellenzeile mit 15pt-Spacing).
                    boolean rowSpacingOk = true;
                    if (lastIdx >= 0 && lastIdx < lines.size() && firstIdx >= 0)
                    {
                        float dy = (float)(line.y - lines.get(lastIdx).y);
                        float typicalT = (lastIdx - firstIdx > 0)
                            ? (float)(lines.get(lastIdx).y - lines.get(firstIdx).y) / (lastIdx - firstIdx)
                            : 14.0f;
                        if (typicalT > 0 && dy > typicalT * 1.3f) rowSpacingOk = false;
                    }
                    if (!rowSpacingOk)
                    {
                        // Tabelle abschliessen — die Zeile wird ueber den weiter unten
                        // folgenden Continuation-Pfad NICHT angefasst (gleicher Wächter).
                        if (currentRows.size() >= MIN_TABLE_ROWS
                            && hasMultipleNonEmptyRows(currentRows)
                            && (tableColCount >= 4 || checkGapConsistency(currentGapEnds))
                            && !isProbablyColumnFlow(currentRows, tableColCount))
                        {
                            tables.add(new DetectedTable(currentRows, firstIdx, lastIdx,
                                    new ArrayList<>(currentRowLineIdx)));
                        }
                        currentRows = new ArrayList<>();
                        currentGapEnds = new ArrayList<>();
                        currentRowLineIdx = new ArrayList<>();
                        referenceGapEnds = null;
                        firstIdx = -1;
                        lastIdx = -1;
                        tableColCount = 0;
                        hadContinuation = false;
                        continue;
                    }
                    SplitResult refSplit = splitLineByReference(line, referenceGapEnds);
                    if (refSplit != null && refSplit.row.cells.size() == tableColCount)
                    {
                        currentRows.add(refSplit.row);
                        currentGapEnds.add(refSplit.gapEnds);
                        currentRowLineIdx.add(i);
                        lastIdx = i;
                        hadContinuation = false;
                        continue;
                    }
                    // Erweiterung: niedrigere Gap-Schwelle versuchen (Bold-Tabellenzeilen
                    // mit dichter gepackter erster Zelle wie "**Körperbeherr-** GE/GE/KO ja D ...").
                    SplitResult lowSplit = splitLineAtGaps(line, GAP_THRESHOLD_IN_TABLE);
                    if (lowSplit != null && lowSplit.row.cells.size() == tableColCount
                        && gapPositionsAlign(lowSplit.gapEnds, referenceGapEnds, GAP_POS_TOLERANCE))
                    {
                        currentRows.add(lowSplit.row);
                        currentGapEnds.add(lowSplit.gapEnds);
                        currentRowLineIdx.add(i);
                        lastIdx = i;
                        hadContinuation = false;
                        continue;
                    }
                    // Letzter Fallback: Force-split an Referenz-Cell-Positionen.
                    // Strenge Schutzmaßnahmen, damit weder Headings noch Cell-Continuations
                    // fälschlich als neue Tabellenzeile aufgenommen werden:
                    //   • Y-Spacing zur vorigen Reihe < 2x typischer Tabellen-Spacing
                    //   • Bold-Start in Cell 0
                    //   • Nicht deutlich größere fontSize als die erste Tabellenzeile (Heading-Schutz)
                    //   • Mindestens 3 nicht-leere Cells (sonst Continuation, nicht eigene Reihe)
                    if (lastIdx >= 0 && lastIdx < lines.size())
                    {
                        float lineSpacing = (float) (line.y - lines.get(lastIdx).y);
                        float typicalT = (lastIdx - firstIdx > 0)
                            ? (float)(lines.get(lastIdx).y - lines.get(firstIdx).y) / (lastIdx - firstIdx)
                            : 14.0f;
                        if (lineSpacing > 0 && lineSpacing < typicalT * 2.0f)
                        {
                            SplitResult forced = forceSplitAtPositions(line, referenceGapEnds);
                            if (forced != null && forced.row.cells.size() == tableColCount)
                            {
                                boolean hasBoldStart = !line.chars.isEmpty()
                                    && line.chars.stream()
                                        .filter(c -> !c.raw.text.isBlank())
                                        .findFirst()
                                        .map(c -> c.isBold)
                                        .orElse(false);
                                int nonEmpty = 0;
                                for (String c : forced.row.cells)
                                {
                                    if (!c.trim().isEmpty()) nonEmpty++;
                                }
                                float lineMaxFs = 0f;
                                for (ClassifiedChar ch : line.chars)
                                {
                                    if (ch.raw.text == null || ch.raw.text.isBlank()) continue;
                                    if (ch.raw.fontSize > lineMaxFs) lineMaxFs = ch.raw.fontSize;
                                }
                                float refMaxFs = 0f;
                                for (ClassifiedChar ch : lines.get(firstIdx).chars)
                                {
                                    if (ch.raw.text == null || ch.raw.text.isBlank()) continue;
                                    if (ch.raw.fontSize > refMaxFs) refMaxFs = ch.raw.fontSize;
                                }
                                boolean fontOk = lineMaxFs <= refMaxFs + 1.5f;
                                if (hasBoldStart && fontOk && nonEmpty >= 3)
                                {
                                    currentRows.add(forced.row);
                                    currentGapEnds.add(forced.gapEnds);
                                    currentRowLineIdx.add(i);
                                    lastIdx = i;
                                    hadContinuation = false;
                                    continue;
                                }
                            }
                        }
                    }
                }

                // Continuation-Zeile pruefen — pro Cell, mehrfach pro Tabelle erlaubt.
                // Eine non-table-line kann eine umbrochene Zellen-Fortsetzung sein.
                // Bestimme die Ziel-Cell anhand der X-Startposition: passt sie zu einer
                // Cell-Start-X (Cell 0 oder gapEnds[N-1] der Referenz), ist es eine
                // Continuation dieser Cell der LETZTEN Tabellenzeile.
                //
                // WICHTIG: Y-Spacing-Wächter — wenn der Abstand zur letzten Tabellen-
                // zeile deutlich groesser ist als der typische Zeilenabstand der Tabelle,
                // gehoert die Zeile nicht mehr zur Tabelle (sonst werden Body-Absaetze
                // in die letzte Zelle absorbiert).
                if (!currentRows.isEmpty() && !line.chars.isEmpty())
                {
                    boolean spacingOk = true;
                    if (lastIdx >= 0 && lastIdx < lines.size() && firstIdx >= 0)
                    {
                        float dy = (float)(line.y - lines.get(lastIdx).y);
                        float typicalT = (lastIdx - firstIdx > 0)
                            ? (float)(lines.get(lastIdx).y - lines.get(firstIdx).y) / (lastIdx - firstIdx)
                            : 14.0f;
                        // 1.3x typischer Spacing als Toleranz — alles darueber ist Body.
                        // 1.5x war zu liberal: Body-Zeilen bei knapper Tabellenzeilen-Naehe
                        // (~22pt nach 16pt-Spacings) wurden als Continuation absorbiert.
                        if (typicalT > 0 && dy > typicalT * 1.3f) spacingOk = false;
                    }
                    String lineText = extractText(line.chars);
                    TextLine firstLine = lines.get(firstIdx);
                    if (spacingOk
                        && !firstLine.chars.isEmpty() && lineText.length() < 80
                        && !lineText.isBlank())
                    {
                        float lineX = line.chars.get(0).raw.x;
                        int cellIdx = -1;
                        float bestDist = Float.MAX_VALUE;
                        float cell0X = firstLine.chars.get(0).raw.x;
                        float d0 = Math.abs(lineX - cell0X);
                        if (d0 < 15)
                        {
                            cellIdx = 0;
                            bestDist = d0;
                        }
                        if (referenceGapEnds != null)
                        {
                            for (int k = 0; k < referenceGapEnds.length; k++)
                            {
                                float dist = Math.abs(lineX - referenceGapEnds[k]);
                                if (dist < bestDist && dist < 15)
                                {
                                    cellIdx = k + 1;
                                    bestDist = dist;
                                }
                            }
                        }
                        TableRow lastRow = currentRows.get(currentRows.size() - 1);
                        if (cellIdx >= 0 && cellIdx < lastRow.cells.size())
                        {
                            String cur = lastRow.cells.get(cellIdx);
                            // Nicht endlos akkumulieren — Zelle darf nicht uebermaessig wachsen
                            if (cur.length() < 200)
                            {
                                lastRow.cells.set(cellIdx, cur + " " + lineText);
                                lastIdx = i;
                                hadContinuation = true;
                                continue;
                            }
                        }
                    }
                }

                // Tabelle beenden
                if (currentRows.size() >= MIN_TABLE_ROWS
                    && hasMultipleNonEmptyRows(currentRows)
                    && (tableColCount >= 4 || checkGapConsistency(currentGapEnds))
                    && !isProbablyColumnFlow(currentRows, tableColCount))
                {
                    tables.add(new DetectedTable(currentRows, firstIdx, lastIdx,
                            new ArrayList<>(currentRowLineIdx)));
                }
                currentRows = new ArrayList<>();
                currentGapEnds = new ArrayList<>();
                currentRowLineIdx = new ArrayList<>();
                referenceGapEnds = null;
                firstIdx = -1;
                lastIdx = -1;
                tableColCount = 0;
                hadContinuation = false;
            }
        }

        if (currentRows.size() >= MIN_TABLE_ROWS
            && hasMultipleNonEmptyRows(currentRows)
            && (tableColCount >= 4 || checkGapConsistency(currentGapEnds))
            && !isProbablyColumnFlow(currentRows, tableColCount))
        {
            tables.add(new DetectedTable(currentRows, firstIdx, lastIdx,
                    new ArrayList<>(currentRowLineIdx)));
        }

        // Post-Pass A: Spalten-Verfeinerung. Wenn alle Reihen einer Tabelle mit
        // niedrigerem Threshold konsistent MEHR Cells liefern, splitten wir alle
        // Reihen neu. Behebt Tabellen wie S. 19 "Modifikator | Bewertung | Klettern"
        // wo die Cell-Lücken (8-12pt) unter dem Standard-Threshold von 14pt liegen.
        for (DetectedTable table : tables)
        {
            if (table.rows.size() < 3) continue;
            int origCols = table.rows.get(0).cells.size();
            // Sammle pro Reihe Cell-Anzahl mit niedrigerem Threshold (8pt)
            int[] lowCols = new int[table.rows.size()];
            float[][] lowGapEnds = new float[table.rows.size()][];
            int validRows = 0;
            for (int r = 0; r < table.rows.size(); r++)
            {
                int lineIdx = (table.rowLineIdx != null && r < table.rowLineIdx.size())
                        ? table.rowLineIdx.get(r)
                        : (table.startLineIdx + r);
                if (lineIdx >= lines.size()) { lowCols[r] = -1; continue; }
                SplitResult lowSplit = splitLineAtGaps(lines.get(lineIdx), 8.0f);
                if (lowSplit == null)
                {
                    lowCols[r] = -1;
                    continue;
                }
                lowCols[r] = lowSplit.row.cells.size();
                lowGapEnds[r] = lowSplit.gapEnds;
                validRows++;
            }
            if (validRows < table.rows.size() / 2) continue;
            // Häufigste Cell-Anzahl mit niedrigem Threshold finden
            java.util.Map<Integer, Integer> counts = new java.util.HashMap<>();
            for (int c : lowCols) if (c > 0) counts.merge(c, 1, Integer::sum);
            int targetCols = counts.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey).orElse(origCols);
            if (targetCols <= origCols) continue;
            // Reihen mit weniger Cells über force-split auf Referenz-Positionen anpassen
            float[] refGapEnds = null;
            for (int r = 0; r < table.rows.size(); r++)
            {
                if (lowCols[r] == targetCols) { refGapEnds = lowGapEnds[r]; break; }
            }
            if (refGapEnds == null) continue;
            for (int r = 0; r < table.rows.size(); r++)
            {
                int lineIdx = (table.rowLineIdx != null && r < table.rowLineIdx.size())
                        ? table.rowLineIdx.get(r)
                        : (table.startLineIdx + r);
                if (lineIdx >= lines.size()) continue;
                TableRow newRow = null;
                float[] newGapEnds = null;
                if (lowCols[r] == targetCols)
                {
                    SplitResult ls = splitLineAtGaps(lines.get(lineIdx), 8.0f);
                    if (ls != null) { newRow = ls.row; newGapEnds = ls.gapEnds; }
                }
                else
                {
                    SplitResult fs = forceSplitAtPositions(lines.get(lineIdx), refGapEnds);
                    if (fs != null && fs.row.cells.size() == targetCols)
                    {
                        newRow = fs.row; newGapEnds = fs.gapEnds;
                    }
                }
                if (newRow == null) continue;
                // Continuation-Lines (zwischen lineIdx und naechstem rowLineIdx oder endLineIdx)
                // erneut in die passende Zelle einfuegen — sonst geht Multi-line-Cell-Inhalt
                // (z. B. "lichkeit" als Fortsetzung von "Wahrschein-") beim Re-Split verloren.
                int nextLineExclusive;
                if (table.rowLineIdx != null && r + 1 < table.rowLineIdx.size())
                {
                    nextLineExclusive = table.rowLineIdx.get(r + 1);
                }
                else
                {
                    nextLineExclusive = table.endLineIdx + 1;
                }
                for (int contLine = lineIdx + 1; contLine < nextLineExclusive; contLine++)
                {
                    if (contLine >= lines.size()) break;
                    TextLine cl = lines.get(contLine);
                    if (cl.chars.isEmpty()) continue;
                    String contText = extractText(cl.chars).trim();
                    if (contText.isEmpty()) continue;
                    float clX = cl.chars.get(0).raw.x;
                    int targetCell = findClosestCellIndex(clX, lines.get(lineIdx), newGapEnds);
                    if (targetCell < 0 || targetCell >= newRow.cells.size()) continue;
                    String prevCell = newRow.cells.get(targetCell);
                    String pcStripped = prevCell.stripTrailing();
                    String merged;
                    if (pcStripped.endsWith("-") && pcStripped.length() >= 2
                            && Character.isLetter(pcStripped.charAt(pcStripped.length() - 2))
                            && !contText.isEmpty()
                            && Character.isLowerCase(contText.charAt(0)))
                    {
                        merged = pcStripped.substring(0, pcStripped.length() - 1) + contText;
                    }
                    else if (prevCell.isEmpty())
                    {
                        merged = contText;
                    }
                    else
                    {
                        merged = prevCell + " " + contText;
                    }
                    newRow.cells.set(targetCell, merged);
                }
                table.rows.set(r, newRow);
            }
        }

        // Post-Pass B2: Tabellen mit gleicher Spaltenanzahl und konsistentem Y-Spacing
        // zusammenführen. Mehrzeilige Cells (z. B. "Fernrohr,/zusammenschiebbar") trennen
        // sonst eine logische Tabelle in mehrere Mini-Tabellen mit Plain-Text-Lines
        // dazwischen. Diese Plain-Text-Lines werden als Cell-Continuations erkannt und
        // an die letzte Reihe der vorigen Tabelle angehängt.
        for (int t = tables.size() - 2; t >= 0; t--)
        {
            DetectedTable a = tables.get(t);
            DetectedTable b = tables.get(t + 1);
            if (a.rows.isEmpty() || b.rows.isEmpty()) continue;
            int aCols = a.rows.get(0).cells.size();
            int bCols = b.rows.get(0).cells.size();
            if (aCols != bCols) continue;
            if (a.endLineIdx >= lines.size() || b.startLineIdx >= lines.size()) continue;
            // Y-Spacing innerhalb a (Mittel ueber Reihen)
            if (a.endLineIdx <= a.startLineIdx) continue;
            float aSpacing = (float) (lines.get(a.endLineIdx).y - lines.get(a.startLineIdx).y)
                / (a.endLineIdx - a.startLineIdx);
            // Y-Distanz zwischen Tabellen-Ende und naechster Tabellen-Anfang
            float gapY = (float) (lines.get(b.startLineIdx).y - lines.get(a.endLineIdx).y);
            // Akzeptiere Lücke wenn sie maximal 4x typischen Tabellen-Spacings entspricht
            if (aSpacing <= 0 || gapY > aSpacing * 4 || gapY < 0) continue;
            // Wenn zwischen den beiden Tabellen eine Heading-Zeile liegt, gehoeren sie
            // zu verschiedenen logischen Bloecken und duerfen NICHT verschmolzen werden.
            // Heading-Erkennung: entweder Markdown-#-Heading ODER Bold-Header-Kandidat
            // (mehrere Worte fett, body-font-groesse, kein Doppelpunkt am Ende —
            // z. B. "Anzahl der erlaubten Proben (Vorschläge)" auf S22).
            boolean headingBetween = false;
            for (int li = a.endLineIdx + 1; li < b.startLineIdx; li++)
            {
                TextLine cl = lines.get(li);
                if (cl.chars.isEmpty()) continue;
                String md = lineToMarkdown(cl);
                if (md.startsWith("#") || isBoldHeaderCandidate(cl))
                {
                    headingBetween = true;
                    break;
                }
            }
            if (headingBetween) continue;
            // Lines dazwischen als Cell-Continuation der letzten Reihe von a aufnehmen.
            // Bestimme Cell-Index anhand der X-Position des ersten Chars.
            // Referenz-Cell-X aus a.rows.get(0) reichlich abgleichen — wir nutzen die
            // first-row-Lines.
            TextLine firstRow = lines.get(a.startLineIdx);
            float[] cellStarts = new float[aCols];
            // Cell 0 startet bei firstRow.chars[0].x; Cells 1..N-1 könnten via splitLine ermittelt werden
            if (!firstRow.chars.isEmpty()) cellStarts[0] = firstRow.chars.get(0).raw.x;
            SplitResult fr = splitLineAtGaps(firstRow, GAP_THRESHOLD);
            if (fr != null && fr.gapEnds != null)
            {
                int n = Math.min(cellStarts.length - 1, fr.gapEnds.length);
                for (int k = 0; k < n; k++) cellStarts[k + 1] = fr.gapEnds[k];
            }
            for (int li = a.endLineIdx + 1; li < b.startLineIdx; li++)
            {
                TextLine cl = lines.get(li);
                if (cl.chars.isEmpty()) continue;
                String text = extractText(cl.chars).trim();
                if (text.isEmpty() || text.length() > 80) continue;
                float clX = cl.chars.get(0).raw.x;
                int targetCell = -1;
                float bestDist = Float.MAX_VALUE;
                for (int k = 0; k < cellStarts.length; k++)
                {
                    float dist = Math.abs(clX - cellStarts[k]);
                    if (dist < bestDist && dist < 15)
                    {
                        bestDist = dist;
                        targetCell = k;
                    }
                }
                TableRow lastRow = a.rows.get(a.rows.size() - 1);
                if (targetCell >= 0 && targetCell < lastRow.cells.size())
                {
                    String cur = lastRow.cells.get(targetCell);
                    if (cur.length() < 200)
                    {
                        lastRow.cells.set(targetCell, cur.isEmpty() ? text : cur + " " + text);
                    }
                }
            }
            // Reihen aus b in a aufnehmen
            a.rows.addAll(b.rows);
            a.endLineIdx = b.endLineIdx;
            tables.remove(t + 1);
        }

        // Post-Pass B: aufeinanderfolgende Tabellenzeilen mergen, wenn die zweite
        // eine Cell-Continuation der ersten ist (nummerierte Tabellen mit umbrochener
        // Zelle). Beispiel: "4 Geschickte | Bis zum Ende..." + "Kampfbewegungen | einsetzen."
        // werden zusammengezogen.
        for (DetectedTable table : tables)
        {
            // Y-Spacing-basierte Continuation-Schwelle.
            // Wir berechnen MIN und MAX Zeilenabstand IM TABELLEN-BEREICH. Eine
            // Multi-line-Cell ist nur dann erkennbar, wenn min und max DEUTLICH
            // unterschiedlich sind (≥ 15 % Verhaeltnis): kleine dys = Innerhalb-Cell,
            // grosse dys = Reihen-Wechsel. Liegen alle dys nah beieinander, ist die
            // Tabelle "uniform" — KEINE Multi-line-Cells, KEINE Y-Continuation.
            float minDy = Float.MAX_VALUE;
            float maxDy = 0f;
            for (int li = table.startLineIdx; li >= 0 && li < table.endLineIdx && li + 1 < lines.size(); li++)
            {
                float dy = (float)(lines.get(li + 1).y - lines.get(li).y);
                if (dy > 0)
                {
                    if (dy < minDy) minDy = dy;
                    if (dy > maxDy) maxDy = dy;
                }
            }
            float ySpacingThreshold;
            if (minDy < Float.MAX_VALUE && maxDy > 0 && maxDy >= minDy * 1.15f)
            {
                // Bimodal: kleine vs grosse Spacings. Threshold = minDy * 1.05
                // (etwas Toleranz gegenueber Mess-Schwankungen).
                ySpacingThreshold = minDy * 1.05f;
            }
            else
            {
                // Uniform — keine Multi-line-Cell-Erkennung
                ySpacingThreshold = -1f;
            }

            for (int r = table.rows.size() - 1; r > 0; r--)
            {
                TableRow prev = table.rows.get(r - 1);
                TableRow curr = table.rows.get(r);
                // Multi-line-Cell-Continuation Erkennung — primaer per Y-Spacing,
                // sekundaer per Inhalts-Heuristik.
                boolean isYContinuation = false;
                if (ySpacingThreshold > 0
                        && table.rowLineIdx != null
                        && r < table.rowLineIdx.size())
                {
                    int li1 = table.rowLineIdx.get(r - 1);
                    int li2 = table.rowLineIdx.get(r);
                    if (li1 >= 0 && li2 >= 0 && li1 < lines.size() && li2 < lines.size())
                    {
                        float dy = (float)(lines.get(li2).y - lines.get(li1).y);
                        if (dy > 0 && dy <= ySpacingThreshold) isYContinuation = true;
                    }
                }
                boolean isContentContinuation = false;
                if (!prev.cells.isEmpty() && !curr.cells.isEmpty())
                {
                    String prevFirst = prev.cells.get(0).trim();
                    String currFirst = curr.cells.get(0).trim();
                    boolean prevHasMarker = !prevFirst.isEmpty() && isRowMarkerStart(prevFirst.charAt(0));
                    boolean currHasMarker = !currFirst.isEmpty() && isRowMarkerStart(currFirst.charAt(0));
                    if (prevHasMarker && !currHasMarker) isContentContinuation = true;
                    if (!isContentContinuation)
                    {
                        // Hyphen am Ende einer prev-Zelle → Wort-Continuation
                        for (String pc : prev.cells)
                        {
                            String t = pc.stripTrailing();
                            if (t.length() >= 2 && t.endsWith("-")
                                    && Character.isLetter(t.charAt(t.length() - 2)))
                            {
                                // ABER nur wenn curr nicht selbst klar eine neue Reihe ist
                                if (!currHasMarker)
                                {
                                    isContentContinuation = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (isYContinuation || isContentContinuation || looksLikeRowContinuation(prev, curr))
                {
                    int n = Math.min(prev.cells.size(), curr.cells.size());
                    for (int c = 0; c < n; c++)
                    {
                        String prevCell = prev.cells.get(c);
                        String currCell = curr.cells.get(c).trim();
                        if (currCell.isEmpty()) continue;
                        if (prevCell.isEmpty())
                        {
                            prev.cells.set(c, currCell);
                        }
                        else
                        {
                            // Bindestrich am Zellen-Ende: "Wahrschein-" + "lichkeit"
                            // → "Wahrscheinlichkeit" (nur wenn beide Seiten Buchstabe).
                            String pcStripped = prevCell.stripTrailing();
                            if (pcStripped.endsWith("-") && pcStripped.length() >= 2
                                && Character.isLetter(pcStripped.charAt(pcStripped.length() - 2))
                                && !currCell.isEmpty()
                                && Character.isLowerCase(currCell.charAt(0)))
                            {
                                prev.cells.set(c, pcStripped.substring(0, pcStripped.length() - 1) + currCell);
                            }
                            else
                            {
                                prev.cells.set(c, prevCell + " " + currCell);
                            }
                        }
                    }
                    table.rows.remove(r);
                }
            }
        }

        return tables;
    }

    /**
     * Heuristik: Tabellenzeile ist die Fortsetzung der Vorgaengerzeile mit umbrochenen
     * Zellen. Konservativ — derzeit greift es nur bei nummerierten Tabellen, in denen
     * die Vorgaengerzeile mit einer Zahl/einem Buchstaben-Marker beginnt und die neue
     * Zeile keinen solchen Marker hat (typisch fuer Cell-Wraps).
     */
    private boolean looksLikeRowContinuation(TableRow prev, TableRow curr)
    {
        if (prev.cells.size() != curr.cells.size()) return false;
        if (prev.cells.isEmpty()) return false;

        // Multi-line-Cell-Continuation: die neue Zeile hat HOECHSTENS eine Zelle mit
        // Inhalt, alle anderen sind leer. Das ist typisch fuer eine umbrochene Zelle
        // (z. B. "Wahrschein-" + " | lichkeit | "). Akzeptieren — Rest macht der Caller.
        int currNonEmpty = 0;
        for (String c : curr.cells)
        {
            if (c != null && !c.trim().isEmpty()) currNonEmpty++;
        }
        if (currNonEmpty <= 1)
        {
            return currNonEmpty == 1; // genau eine Zelle hat Inhalt → Continuation
        }

        String prevFirst = prev.cells.get(0).trim();
        String currFirst = curr.cells.get(0).trim();
        if (prevFirst.isEmpty() || currFirst.isEmpty()) return false;
        boolean prevHasNumberMarker = Character.isDigit(prevFirst.charAt(0));
        boolean currHasNumberMarker = !currFirst.isEmpty() && Character.isDigit(currFirst.charAt(0));
        if (prevHasNumberMarker && !currHasNumberMarker)
        {
            // Continuation, wenn die neue Zeile ohne Listen-Index startet
            return true;
        }
        // Hyphen-Continuation entfaellt: zu false-positiv-anfaellig (ein Header-Cell
        // "Wahrschein-" wurde sonst mit der naechsten Datenzeile gemergt). Multi-line
        // Cells werden ueber den currNonEmpty<=1-Pfad oben behandelt.
        return false;
    }

    /**
     * Markdown-Level-Merge: zwei aufeinander folgende Tabellen mit gleicher
     * Spaltenanzahl werden zu einer einzigen Tabelle zusammengezogen. Plain-Text-Lines
     * zwischen den Tabellen werden — wenn sie kurz sind und mit Kleinbuchstaben/Komma
     * starten — als Cell-Continuation an die letzte Reihe der ersten Tabelle gehängt.
     * Lange Lines oder Headings dazwischen brechen den Merge ab.
     */
    String mergeAdjacentMarkdownTables(String md)
    {
        String[] lines = md.split("\n", -1);
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < lines.length)
        {
            // Tabellen-Anfang erkennen: Line beginnt mit "| " UND naechste Line ist Separator
            if (i + 1 < lines.length
                && lines[i].startsWith("| ")
                && lines[i + 1].matches("^\\|( -+ \\|)+\\s*$"))
            {
                // Sammle die ganze erste Tabelle
                List<String> tableLines = new ArrayList<>();
                tableLines.add(lines[i]);          // Header
                tableLines.add(lines[i + 1]);      // Separator
                int colCount = countTableColumns(lines[i]);
                int j = i + 2;
                while (j < lines.length && lines[j].startsWith("| ")
                       && countTableColumns(lines[j]) == colCount)
                {
                    tableLines.add(lines[j]);
                    j++;
                }
                // Suche eine direkt anschliessende Tabelle gleicher Spaltenanzahl
                while (true)
                {
                    int gapStart = j;
                    int gapEnd = j;
                    boolean gapHasHeading = false;
                    boolean gapHasLongLine = false;
                    while (gapEnd < lines.length
                           && !(gapEnd + 1 < lines.length
                                && lines[gapEnd].startsWith("| ")
                                && lines[gapEnd + 1].matches("^\\|( -+ \\|)+\\s*$")))
                    {
                        String gl = lines[gapEnd];
                        if (gl.startsWith("#")) gapHasHeading = true;
                        // Eine pur-bold Zeile (`**...**` ohne anderen Text) ist eine
                        // verkleidete Heading — z. B. "Anzahl der erlaubten Proben
                        // (Vorschläge)" auf S22, das im PDF body-Schriftgroesse hat
                        // aber komplett fett gerendert ist.
                        String trimmed = gl.trim();
                        if (trimmed.matches("^\\*\\*[^*][^\\n]*[^*]\\*\\*$")
                                && !trimmed.contains(" *") && !trimmed.contains("* "))
                        {
                            gapHasHeading = true;
                        }
                        if (gl.length() > 80) gapHasLongLine = true;
                        gapEnd++;
                    }
                    int gapSize = gapEnd - gapStart;
                    if (gapSize > 5 || gapEnd >= lines.length) break;
                    if (gapHasLongLine) break;
                    // Heading zwischen Tabellen → KEIN Merge, sondern Tabellen getrennt
                    // ausgeben (User-Wunsch: Heading bricht logischen Block).
                    if (gapHasHeading) break;
                    int nextHeader = gapEnd;
                    int nextCols = countTableColumns(lines[nextHeader]);
                    if (nextCols != colCount) break;
                    // Merge: Lines zwischen den Tabellen als Cell-0-Continuation an
                    // die letzte Reihe der ersten Tabelle.
                    for (int g = gapStart; g < gapEnd; g++)
                    {
                        String gtext = lines[g].trim();
                        if (gtext.isEmpty()) continue;
                        if (gtext.startsWith("#"))
                        {
                            // Heading-Marker entfernen, Text als Continuation
                            gtext = gtext.replaceFirst("^#+\\s+", "");
                        }
                        if (gtext.isEmpty()) continue;
                        // An letzte Tabellenreihe (Zelle 0) anhängen
                        int lastRowIdx = tableLines.size() - 1;
                        String lastRow = tableLines.get(lastRowIdx);
                        // Zelle 0 finden und ergänzen
                        int firstPipe = lastRow.indexOf("| ");
                        int secondPipe = lastRow.indexOf(" |", firstPipe + 2);
                        if (firstPipe >= 0 && secondPipe > firstPipe)
                        {
                            String cell0 = lastRow.substring(firstPipe + 2, secondPipe);
                            String newCell0 = cell0.isEmpty() ? gtext : cell0 + " " + gtext;
                            tableLines.set(lastRowIdx,
                                lastRow.substring(0, firstPipe + 2) + newCell0
                                    + lastRow.substring(secondPipe));
                        }
                    }
                    // Reihen der zweiten Tabelle (ohne Header & Separator) anhängen
                    int k = nextHeader + 2;
                    // Header der zweiten Tabelle ggf. ALS Datenzeile übernehmen wenn er
                    // wie eine Datenzeile aussieht (kein typischer Header-Stil)
                    tableLines.add(lines[nextHeader]);
                    while (k < lines.length && lines[k].startsWith("| ")
                           && countTableColumns(lines[k]) == colCount)
                    {
                        tableLines.add(lines[k]);
                        k++;
                    }
                    j = k;
                }
                // Tabelle ausgeben
                for (String tl : tableLines)
                {
                    out.append(tl).append('\n');
                }
                i = j;
                continue;
            }
            out.append(lines[i]).append('\n');
            i++;
        }
        // Trailing-Newline normalisieren
        String s = out.toString();
        if (s.endsWith("\n\n")) s = s.substring(0, s.length() - 1);
        return s;
    }

    private int countTableColumns(String line)
    {
        if (line == null || !line.startsWith("|")) return 0;
        int count = 0;
        for (int k = 0; k < line.length(); k++)
        {
            if (line.charAt(k) == '|') count++;
        }
        return Math.max(0, count - 1); // n pipes → n-1 cells
    }

    /**
     * Force-Split einer Zeile an gegebenen Referenz-X-Positionen, OHNE Gap-Schwelle.
     * Anders als {@link #splitLineByReference} braucht es keine echte Lücke an der
     * Position — chars werden anhand ihrer X-Koordinate dem Cell-Bin zugeordnet, in
     * den sie laut Referenz-Positionen gehören. Wird beim Header-Recovery genutzt,
     * wenn der Header dichter gesetzt ist als die Daten.
     */
    private SplitResult forceSplitAtPositions(TextLine line, float[] refGapEnds)
    {
        if (line.chars.isEmpty() || refGapEnds == null || refGapEnds.length == 0) return null;
        List<ClassifiedChar> sorted = new ArrayList<>(line.chars);
        sorted.sort(Comparator.comparingDouble(c -> c.raw.x));
        List<String> cells = new ArrayList<>();
        List<ClassifiedChar> cellChars = new ArrayList<>();
        float[] gapEnds = new float[refGapEnds.length];
        int splitIdx = 0;
        for (ClassifiedChar ch : sorted)
        {
            // Wenn das Zeichen in den naechsten Cell-Bereich gehoert (X >= ref-Position),
            // schliesze die aktuelle Zelle ab und starte die naechste.
            while (splitIdx < refGapEnds.length && ch.raw.x >= refGapEnds[splitIdx] - 2f)
            {
                cells.add(extractText(cellChars));
                gapEnds[splitIdx] = ch.raw.x;
                cellChars = new ArrayList<>();
                splitIdx++;
            }
            cellChars.add(ch);
        }
        cells.add(extractText(cellChars));
        // Falls keine chars in eine bestimmte Cell fielen, Cell als leer auffuellen
        while (cells.size() < refGapEnds.length + 1) cells.add("");
        return new SplitResult(new TableRow(cells), gapEnds);
    }

    /**
     * Pruefen ob eine Zeile ein bold-Header-Kandidat ist (kurz, mehrheitlich bold,
     * kein Body-Text-Stil). Wird genutzt, um Tabellen-Header zu re-claimen, die
     * wegen zu kleiner Header-Wort-Luecken nicht selbst als Tabellenzeile erkannt
     * wurden.
     */
    private boolean isBoldHeaderCandidate(TextLine line)
    {
        if (line.chars.isEmpty()) return false;
        int total = 0, bold = 0;
        StringBuilder sb = new StringBuilder();
        for (ClassifiedChar ch : line.chars)
        {
            if (ch.raw.text == null || ch.raw.text.isBlank()) continue;
            total++;
            if (ch.isBold) bold++;
            sb.append(ch.raw.text);
        }
        if (total < 4 || total > 80) return false;
        if (bold < total * 0.8) return false;
        String text = sb.toString().trim();
        // Header brauchen mehrere Zell-Bezeichner: entweder durch Leerzeichen getrennt
        // ODER durch einen sichtbaren Gap (>= 5pt) — z. B. wenn das PDF Spaltennamen
        // ohne Wortzwischenraum direkt aneinanderreiht ("Modifikator-MaximumFertigkeitswert-Minimum").
        if (!text.contains(" ") && !hasInternalGap(line, 5f)) return false;
        // Endet mit Doppelpunkt = inline-Marker, kein Header
        if (text.endsWith(":")) return false;
        return true;
    }

    /**
     * Findet die Cell-Index, deren Start-X am naechsten an {@code lineX} liegt.
     * Die Cell-Starts werden aus der Header-Line (chars.get(0).raw.x fuer Cell 0)
     * und {@code gapEnds} (Cells 1..N) bestimmt. Mindestabstand: 15 pt.
     */
    private static int findClosestCellIndex(float lineX, TextLine refLine, float[] gapEnds)
    {
        if (refLine.chars.isEmpty()) return -1;
        float cell0X = refLine.chars.get(0).raw.x;
        int bestIdx = -1;
        float bestDist = 15f; // max tolerance
        float d0 = Math.abs(lineX - cell0X);
        if (d0 < bestDist) { bestDist = d0; bestIdx = 0; }
        if (gapEnds != null)
        {
            for (int k = 0; k < gapEnds.length; k++)
            {
                float dist = Math.abs(lineX - gapEnds[k]);
                if (dist < bestDist) { bestDist = dist; bestIdx = k + 1; }
            }
        }
        return bestIdx;
    }

    /**
     * Erkennt typische Reihen-Marker am Zellenanfang einer Tabelle:
     * Ziffer, Plus/Minus, normale Bindestriche und mathematische Minus-Varianten.
     */
    private static boolean isRowMarkerStart(char c)
    {
        return Character.isDigit(c) || c == '+' || c == '-' || c == '–' || c == '−' || c == '/';
    }

    /**
     * Heuristik: ist diese Seite eine Inhaltsverzeichnis-Folgeseite?
     *
     * <p>Konservativ: erfordert mindestens 5 Tabellenzeilen, in denen Cell 0
     * ODER Cell N eine kleine Zahl (1-999) ist UND dass der Anteil dieser
     * Zeilen an allen Tabellenzeilen ueber 70 % liegt UND die Seite NICHT
     * substanziellen Body-Fliesstext enthaelt (kein Absatz &gt; 200 Zeichen
     * und endet mit Satzzeichen). Damit bleiben echte Body-Seiten mit
     * gelegentlichen Tabellen verschont.
     */
    private static boolean looksLikeTocPage(String md)
    {
        if (md == null || md.isBlank()) return false;
        String[] lines = md.split("\n");
        int tocTableRows = 0;
        int totalTableRows = 0;
        for (String line : lines)
        {
            if (!line.startsWith("| ")) continue;
            if (line.matches("^\\| -+ \\|.*")) continue;
            totalTableRows++;
            String[] cells = line.split("\\|");
            if (cells.length < 3) continue;
            String first = cells.length > 1 ? cells[1].trim() : "";
            String last = cells.length > 2 ? cells[cells.length - 2].trim() : "";
            boolean firstIsNum = first.matches("\\d{1,3}") || first.matches("\\d{1,3}\\s+\\d{1,3}");
            boolean lastIsNum = last.matches("\\d{1,3}");
            if (firstIsNum || lastIsNum) tocTableRows++;
        }
        if (tocTableRows < 5) return false;
        if (tocTableRows < totalTableRows * 0.7) return false;
        // Wenn die Seite SUBSTANZIELLEN Body-Text enthaelt (Saetze ueber 200 Zeichen,
        // die mit Satzende abschlieszen), ist sie KEINE reine TOC-Seite.
        for (String line : lines)
        {
            if (line.startsWith("|") || line.startsWith("#")) continue;
            String t = line.trim();
            if (t.length() > 200 && t.matches(".*[.!?][*\\s]*$")) return false;
        }
        return true;
    }

    /**
     * Echte Tabellen haben MINDESTENS zwei Reihen mit Inhalt. Eine Reihe gilt als
     * inhaltlich, wenn mindestens eine Zelle nach Trim nicht leer ist. Wir verwenden
     * das, um Falsch-Positives wie "leerer synthetischer Header + 1 Datenzeile" zu
     * verwerfen — der User-Wunsch lautet: Tabelle braucht IMMER &ge; 2 echte Zeilen.
     */
    private static boolean hasMultipleNonEmptyRows(List<TableRow> rows)
    {
        int nonEmpty = 0;
        for (TableRow r : rows)
        {
            for (String c : r.cells)
            {
                if (c != null && !c.trim().isEmpty()) { nonEmpty++; break; }
            }
            if (nonEmpty >= 2) return true;
        }
        return false;
    }

    /** Prueft, ob in der Zeile irgendwo ein horizontaler Gap >= {@code minGap} pt vorkommt. */
    private boolean hasInternalGap(TextLine line, float minGap)
    {
        List<ClassifiedChar> sorted = new ArrayList<>(line.chars);
        sorted.sort(Comparator.comparingDouble(c -> c.raw.x));
        for (int j = 1; j < sorted.size(); j++)
        {
            float gap = sorted.get(j).raw.x
                    - (sorted.get(j - 1).raw.x + sorted.get(j - 1).raw.width);
            if (gap >= minGap) return true;
        }
        return false;
    }

    /**
     * Heuristik: false-positive Tabelle aus zweispaltigem Fliesstext.
     *
     * Echte 2-Spalten-Tabellen sind typischerweise key-value-Strukturen mit
     * mindestens einer kurzen Spalte (Label, Wert, Modifikator). Wenn beide
     * Spalten konsistent Prosa-Laenge enthalten (Mittelwert > 30 Zeichen je
     * Spalte), handelt es sich vermutlich um zwei Spalten Fliesstext, die
     * wegen der Spaltenluecke versehentlich als Tabelle erkannt wurden.
     */
    private boolean isProbablyColumnFlow(List<TableRow> rows, int colCount)
    {
        if (colCount != 2 || rows.size() < 2) return false;
        long sumLeft = 0, sumRight = 0;
        int n = 0;
        for (TableRow row : rows)
        {
            if (row.cells.size() < 2) continue;
            sumLeft += row.cells.get(0).length();
            sumRight += row.cells.get(1).length();
            n++;
        }
        if (n == 0) return false;
        double avgLeft = (double) sumLeft / n;
        double avgRight = (double) sumRight / n;
        return avgLeft > 30 && avgRight > 30;
    }

    /**
     * Splittet eine Zeile anhand von Referenz-Gap-Positionen (aus der Header-Zeile).
     * Sucht nach dem naechsten Gap bei jeder Referenz-Position (±15pt Toleranz).
     */
    private SplitResult splitLineByReference(TextLine line, float[] refGapEnds)
    {
        if (line.chars.size() < 2 || refGapEnds == null) return null;

        List<ClassifiedChar> sorted = new ArrayList<>(line.chars);
        sorted.sort(Comparator.comparingDouble(c -> c.raw.x));

        // Finde fuer jede Referenz-Position den naechsten Gap in der Zeile
        List<Float> splitPositions = new ArrayList<>();
        for (float refPos : refGapEnds)
        {
            float bestGapPos = -1;
            float bestDist = Float.MAX_VALUE;
            for (int j = 1; j < sorted.size(); j++)
            {
                float prevEnd = sorted.get(j - 1).raw.x + sorted.get(j - 1).raw.width;
                float nextStart = sorted.get(j).raw.x;
                float gap = nextStart - prevEnd;
                if (gap < GAP_THRESHOLD_IN_TABLE) continue;
                float gapCenter = (prevEnd + nextStart) / 2;
                float dist = Math.abs(gapCenter - refPos);
                if (dist < bestDist && dist < 15)
                {
                    bestDist = dist;
                    bestGapPos = prevEnd;
                }
            }
            if (bestGapPos < 0) return null; // Keine passende Gap-Position gefunden
            splitPositions.add(bestGapPos);
        }

        // An den gefundenen Positionen splitten
        splitPositions.sort(Float::compareTo);
        List<String> cells = new ArrayList<>();
        List<ClassifiedChar> cellChars = new ArrayList<>();
        float[] gapEnds = new float[splitPositions.size()];
        int splitIdx = 0;

        for (ClassifiedChar ch : sorted)
        {
            if (splitIdx < splitPositions.size() && ch.raw.x > splitPositions.get(splitIdx) + 1)
            {
                cells.add(extractText(cellChars));
                gapEnds[splitIdx] = ch.raw.x;
                cellChars = new ArrayList<>();
                cellChars.add(ch);
                splitIdx++;
            }
            else
            {
                cellChars.add(ch);
            }
        }
        cells.add(extractText(cellChars));

        if (cells.size() != refGapEnds.length + 1) return null;
        return new SplitResult(new TableRow(cells), gapEnds);
    }

    /**
     * Formatiert eine Tabellenzeile als Markdown: | Zelle1 | Zelle2 | ...
     */
    private String formatTableRow(TextLine line)
    {
        SplitResult gapCheck = splitLineAtGaps(line, GAP_THRESHOLD);
        if (gapCheck == null || gapCheck.row.cells.size() < 2)
        {
            return "| " + buildHeadingText(line) + " |";
        }
        StringBuilder sb = new StringBuilder("|");
        for (String cell : gapCheck.row.cells)
        {
            sb.append(" ").append(cell.trim()).append(" |");
        }
        return sb.toString();
    }

    /**
     * Erkennt 2-Spalten-Tabellen innerhalb einer Spalte (nach Spaltenaufteilung).
     * Schritt 2: Streaks von 2-Zellen-Zeilen mit konsistenter Gap-Position.
     * Schritt 3: Propagation zu Nachbarzeilen mit Gaps.
     * Schritt 3b: Tabellenkopf-Fortsetzungszeilen (bold, kurz, ohne Gap, direkt neben Tabelle).
     */
    private void detectTwoColumnTables(List<TextLine> lines, java.util.Set<Float> tableYs)
    {
        if (lines.isEmpty()) return;
        List<TextLine> sorted = new ArrayList<>(lines);
        sorted.sort(Comparator.comparingDouble(l -> l.y));

        // Body-Text linker Rand bestimmen (haeufigster X-Start)
        java.util.Map<Integer, Integer> xBuckets = new java.util.HashMap<>();
        for (TextLine line : sorted)
        {
            if (line.chars.isEmpty()) continue;
            float firstX = line.chars.stream().filter(c -> !c.raw.text.isBlank())
                .map(c -> c.raw.x).min(Float::compare).orElse(0f);
            xBuckets.merge(Math.round(firstX / 3) * 3, 1, Integer::sum);
        }
        float bodyLeftX = xBuckets.entrySet().stream()
            .max(java.util.Map.Entry.comparingByValue())
            .map(e -> (float) e.getKey()).orElse(0f);

        // Typischer Zeilenabstand
        float typicalSpacing = calculateTypicalLineSpacing(sorted);

        // Schritt 2: Streaks von 2-Zellen-Zeilen
        for (int pi = 0; pi < sorted.size(); pi++)
        {
            TextLine line = sorted.get(pi);
            if (tableYs.contains((float) line.y)) continue;
            SplitResult gapCheck = splitLineAtGaps(line, GAP_THRESHOLD);
            if (gapCheck == null || gapCheck.row.cells.size() != 2) continue;
            float gapPos = gapCheck.gapEnds[0];

            int streak = 1;
            boolean allIndented = true;
            boolean hasHigherSpacing = false;
            for (int pj = pi + 1; pj < sorted.size(); pj++)
            {
                TextLine nextLine = sorted.get(pj);
                SplitResult nextGap = splitLineAtGaps(nextLine, GAP_THRESHOLD);
                if (nextGap == null || nextGap.row.cells.size() < 2) break;
                if (nextGap.row.cells.size() == 2
                    && Math.abs(nextGap.gapEnds[0] - gapPos) < GAP_POS_TOLERANCE)
                {
                    streak++;
                    // Zeilenabstand pruefen
                    float spacing = (float) (nextLine.y - sorted.get(pj - 1).y);
                    if (typicalSpacing > 0 && spacing > typicalSpacing * 1.3f)
                    {
                        hasHigherSpacing = true;
                    }
                }
                else break;
            }

            // Einrueckung pruefen
            for (int pj = pi; pj < pi + streak; pj++)
            {
                float firstX = sorted.get(pj).chars.stream().filter(c -> !c.raw.text.isBlank())
                    .map(c -> c.raw.x).min(Float::compare).orElse(0f);
                if (firstX <= bodyLeftX + 3) allIndented = false;
            }

            // Mindest-Streak: 3 normalerweise, aber 2 reicht wenn eingerueckt ODER hoeherer Zeilenabstand
            int minStreak = (allIndented || hasHigherSpacing) ? 2 : 3;
            if (streak >= minStreak)
            {
                // Fragmentvalidierung: Wenn die meisten Zellen nur kurze Wortfragmente
                // enthalten (Silben, abgeschnittene Woerter), ist es kein echter Tabelleninhalt
                int fragmentCells = 0;
                int totalCells = 0;
                for (int pj = pi; pj < pi + streak; pj++)
                {
                    SplitResult sr = splitLineAtGaps(sorted.get(pj), GAP_THRESHOLD);
                    if (sr != null)
                    {
                        for (String cell : sr.row.cells)
                        {
                            totalCells++;
                            String trimmed = cell.trim();
                            if (trimmed.length() <= 2
                                || (trimmed.length() <= 4 && !trimmed.contains(" ")))
                            {
                                fragmentCells++;
                            }
                        }
                    }
                }
                boolean mostlyFragments = totalCells > 0 && fragmentCells > totalCells * 0.5;

                if (!mostlyFragments)
                {
                    for (int pj = pi; pj < pi + streak; pj++)
                    {
                        tableYs.add((float) sorted.get(pj).y);
                    }
                }
            }
        }

        // Schritt 3: Propagation — Zeilen mit 1+ Gap neben bekannter Tabellenzeile
        boolean changed = true;
        while (changed)
        {
            changed = false;
            for (int pi = 0; pi < sorted.size(); pi++)
            {
                TextLine line = sorted.get(pi);
                if (tableYs.contains((float) line.y)) continue;
                SplitResult gapCheck = splitLineAtGaps(line, GAP_THRESHOLD);
                if (gapCheck == null || gapCheck.row.cells.size() < 2) continue;
                boolean neighborAbove = pi > 0
                    && tableYs.contains((float) sorted.get(pi - 1).y)
                    && Math.abs(line.y - sorted.get(pi - 1).y) < 20;
                boolean neighborBelow = pi + 1 < sorted.size()
                    && tableYs.contains((float) sorted.get(pi + 1).y)
                    && Math.abs(sorted.get(pi + 1).y - line.y) < 20;
                if (neighborAbove || neighborBelow)
                {
                    tableYs.add((float) line.y);
                    changed = true;
                }
            }
        }

        // Schritt 3b: Tabellenkopf-Fortsetzungszeilen
        for (int pi = 0; pi < sorted.size(); pi++)
        {
            TextLine line = sorted.get(pi);
            if (tableYs.contains((float) line.y)) continue;
            SplitResult gapCheck = splitLineAtGaps(line, GAP_THRESHOLD);
            if (gapCheck != null && gapCheck.row.cells.size() >= 2) continue;
            boolean neighborAbove = pi > 0
                && tableYs.contains((float) sorted.get(pi - 1).y)
                && Math.abs(line.y - sorted.get(pi - 1).y) < 14;
            boolean neighborBelow = pi + 1 < sorted.size()
                && tableYs.contains((float) sorted.get(pi + 1).y)
                && Math.abs(sorted.get(pi + 1).y - line.y) < 14;
            if (neighborAbove || neighborBelow)
            {
                String text = buildHeadingText(line);
                int totalChars = (int) line.chars.stream().filter(c -> !c.raw.text.isBlank()).count();
                int boldChars = (int) line.chars.stream().filter(c -> !c.raw.text.isBlank() && c.isBold).count();
                boolean allBold = totalChars > 0 && boldChars >= totalChars * 0.9;
                if (allBold && text.length() < 60)
                {
                    tableYs.add((float) line.y);
                }
            }
        }
    }

    /**
     * Splittet eine Zeile an grossen Gaps in Tabellenzellen.
     * Gibt null zurueck wenn keine Gaps gefunden.
     */
    private SplitResult splitLineAtGaps(TextLine line, float threshold)
    {
        if (line.chars.size() < 2) return null;

        List<float[]> gaps = new ArrayList<>(); // [gapStart, gapEnd]
        for (int j = 1; j < line.chars.size(); j++)
        {
            float prevEnd = line.chars.get(j - 1).raw.x + line.chars.get(j - 1).raw.width;
            float gapSize = line.chars.get(j).raw.x - prevEnd;
            if (gapSize > threshold)
            {
                gaps.add(new float[]{prevEnd, line.chars.get(j).raw.x});
            }
        }

        if (gaps.isEmpty()) return null;

        gaps.sort(Comparator.comparingDouble(g -> g[0]));
        float[] gapEnds = new float[gaps.size()];
        for (int g = 0; g < gaps.size(); g++) gapEnds[g] = gaps.get(g)[1];

        // Zeichen an Gap-Positionen aufteilen
        List<String> cells = new ArrayList<>();
        List<ClassifiedChar> cellChars = new ArrayList<>();
        int gapIdx = 0;

        for (ClassifiedChar ch : line.chars)
        {
            if (gapIdx < gaps.size() && ch.raw.x > gaps.get(gapIdx)[0] + 1)
            {
                cells.add(extractText(cellChars));
                cellChars = new ArrayList<>();
                cellChars.add(ch);
                gapIdx++;
            }
            else
            {
                cellChars.add(ch);
            }
        }
        cells.add(extractText(cellChars));

        return new SplitResult(new TableRow(cells), gapEnds);
    }

    /**
     * Prueft ob Gap-End-Positionen ueber alle Zeilen konsistent sind.
     */
    private boolean checkGapConsistency(List<float[]> allGapEnds)
    {
        if (allGapEnds.size() < 2) return true;

        int numGaps = allGapEnds.get(0).length;
        for (int g = 0; g < numGaps; g++)
        {
            float sum = 0;
            int count = 0;
            for (float[] ge : allGapEnds)
            {
                if (g < ge.length) { sum += ge[g]; count++; }
            }
            if (count < 2) continue;
            float avg = sum / count;
            for (float[] ge : allGapEnds)
            {
                if (g < ge.length && Math.abs(ge[g] - avg) > GAP_POS_TOLERANCE) return false;
            }
        }
        return true;
    }

    /**
     * Prueft ob die Gap-Positionen einer Zeile mit den Referenz-Positionen uebereinstimmen.
     * Verhindert, dass Fliesstext um Bilder (mit zufaelligen Blocksatz-Luecken) als Tabelle erkannt wird.
     */
    private boolean gapPositionsAlign(float[] gapEnds, float[] refGapEnds, float tolerance)
    {
        if (gapEnds == null || refGapEnds == null) return false;
        int minLen = Math.min(gapEnds.length, refGapEnds.length);
        if (minLen == 0) return false;
        for (int i = 0; i < minLen; i++)
        {
            if (Math.abs(gapEnds[i] - refGapEnds[i]) > tolerance) return false;
        }
        return true;
    }

    /**
     * Extrahiert Text aus einer Liste von ClassifiedChars mit Wortabstaenden.
     */
    private String extractText(List<ClassifiedChar> chars)
    {
        if (chars.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.size(); i++)
        {
            if (i > 0)
            {
                float gap = chars.get(i).raw.x - (chars.get(i - 1).raw.x + chars.get(i - 1).raw.width);
                if (gap > 1.5f) sb.append(" ");
            }
            sb.append(chars.get(i).raw.text);
        }
        return sb.toString().trim();
    }

    // =====================================================================
    // Gap-Analyse fuer Zeilen
    // =====================================================================

    /**
     * Findet den Gap an einer bestimmten X-Position innerhalb einer Zeile.
     */
    private float findGapAtPosition(List<ClassifiedChar> chars, float targetX)
    {
        ClassifiedChar lastBefore = null;
        ClassifiedChar firstAfter = null;
        for (ClassifiedChar ch : chars)
        {
            float cx = ch.raw.x + ch.raw.width / 2;
            if (cx < targetX) lastBefore = ch;
            else if (firstAfter == null) firstAfter = ch;
        }
        if (lastBefore != null && firstAfter != null)
        {
            return firstAfter.raw.x - (lastBefore.raw.x + lastBefore.raw.width);
        }
        return 0;
    }

    /**
     * Findet den groessten Gap in einer Zeile.
     */
    private float findMaxGap(List<ClassifiedChar> chars)
    {
        float max = 0;
        for (int i = 1; i < chars.size(); i++)
        {
            float gap = chars.get(i).raw.x - (chars.get(i - 1).raw.x + chars.get(i - 1).raw.width);
            if (gap > max) max = gap;
        }
        return max;
    }

    /**
     * Findet die X-Position des groessten Gaps.
     */
    private float findMaxGapPosition(List<ClassifiedChar> chars)
    {
        float max = 0;
        float pos = 0;
        for (int i = 1; i < chars.size(); i++)
        {
            float prevEnd = chars.get(i - 1).raw.x + chars.get(i - 1).raw.width;
            float gap = chars.get(i).raw.x - prevEnd;
            if (gap > max)
            {
                max = gap;
                pos = (prevEnd + chars.get(i).raw.x) / 2;
            }
        }
        return pos;
    }

    // =====================================================================
    // Spaltenerkennung
    // =====================================================================

    private float findColumnSplitByHistogram(List<RawPageData.RawChar> chars, float pageWidth)
    {
        // Display-only-Pages (Cover, Kapitel-Titelseiten) duerfen NIE in Spalten
        // gesplittet werden — sonst zerreisst es Headlines wie "Kodex des Schwertes"
        // in der Mitte. Heuristik: sehr wenige Chars insgesamt ODER alle non-blank
        // chars haben fontSize > 18 (Display-Schrift, kein Fliesstext).
        int nonBlank = 0;
        int displayChars = 0;
        for (RawPageData.RawChar ch : chars)
        {
            if (ch.text == null || ch.text.isBlank()) continue;
            nonBlank++;
            if (ch.fontSize > 18f) displayChars++;
        }
        if (nonBlank > 0 && nonBlank < 80) return -1;
        if (nonBlank > 0 && displayChars >= nonBlank * 0.85) return -1;

        int numBins = (int) Math.ceil(pageWidth / BIN_WIDTH);
        int[] histogram = new int[numBins];

        for (RawPageData.RawChar ch : chars)
        {
            int startBin = Math.max(0, (int) (ch.x / BIN_WIDTH));
            int endBin = Math.min(numBins - 1, (int) ((ch.x + ch.width) / BIN_WIDTH));
            for (int b = startBin; b <= endBin; b++)
            {
                histogram[b]++;
            }
        }

        int searchStart = numBins / 3;
        int searchEnd = numBins * 2 / 3;

        int totalInRange = 0;
        for (int b = searchStart; b <= searchEnd; b++) totalInRange += histogram[b];
        float avgDensity = (float) totalInRange / (searchEnd - searchStart + 1);

        // Strategie 1: Leere Zone
        int emptyThreshold = 2;
        int bestGapStart = -1, bestGapEnd = -1, bestGapWidth = 0, gapStart = -1;
        for (int b = searchStart; b <= searchEnd; b++)
        {
            if (histogram[b] <= emptyThreshold) { if (gapStart < 0) gapStart = b; }
            else
            {
                if (gapStart >= 0 && b - gapStart > bestGapWidth)
                {
                    bestGapWidth = b - gapStart;
                    bestGapStart = gapStart;
                    bestGapEnd = b;
                }
                gapStart = -1;
            }
        }
        if (gapStart >= 0 && searchEnd - gapStart > bestGapWidth)
        {
            bestGapWidth = searchEnd - gapStart;
            bestGapStart = gapStart;
            bestGapEnd = searchEnd;
        }
        if (bestGapWidth >= MIN_GAP_BINS)
        {
            return (bestGapStart + bestGapEnd) / 2.0f * BIN_WIDTH;
        }

        // Strategie 2: Tiefstes Tal
        int windowSize = 8;
        float lowestDensity = Float.MAX_VALUE;
        int lowestBin = -1;
        for (int b = searchStart; b <= searchEnd - windowSize; b++)
        {
            int sum = 0;
            for (int w = 0; w < windowSize; w++) sum += histogram[b + w];
            float density = (float) sum / windowSize;
            if (density < lowestDensity) { lowestDensity = density; lowestBin = b + windowSize / 2; }
        }

        if (lowestBin > 0 && lowestDensity < avgDensity * 0.5f)
        {
            float splitX = lowestBin * BIN_WIDTH;
            long leftCount = chars.stream().filter(c -> c.x < splitX - 20).count();
            long rightCount = chars.stream().filter(c -> c.x > splitX + 20).count();
            long totalCount = chars.size();
            if (leftCount > totalCount * 0.2 && rightCount > totalCount * 0.2) return splitX;
        }

        return -1;
    }

    // =====================================================================
    // Cross-column Tabellenerkennung
    // =====================================================================

    /**
     * Erkennt Tabellen die ueber beide Spalten hinweggehen.
     * Zwei Erkennungswege:
     * 1. Beide Spalten haben Tabellenstruktur an exakt gleichen Y-Positionen
     * 2. Zeilen haben Text der ueber die Spaltengrenze geht (kein Gap am splitX)
     *    UND Tabellenstruktur (Gaps anderswo)
     * Gibt die Y-Positionen zurueck die als fullwidth behandelt werden sollen.
     */
    private java.util.Set<Float> detectCrossColumnTableYs(
        List<ClassifiedChar> classified, float splitX,
        java.util.Set<Float> existingFullWidthYs)
    {
        java.util.Set<Float> result = new java.util.HashSet<>();

        // --- Weg 2: Zeilen die ueber die Spaltengrenze gehen mit Tabellenstruktur ---
        // Alle Zeichen (inkl. nicht-fullwidth) zu Zeilen zusammenbauen
        List<ClassifiedChar> allNonFw = new ArrayList<>();
        for (ClassifiedChar ch : classified)
        {
            boolean isExistingFw = false;
            for (Float fwY : existingFullWidthYs)
            {
                if (Math.abs(ch.raw.y - fwY) < LINE_Y_TOLERANCE + 1)
                {
                    isExistingFw = true;
                    break;
                }
            }
            if (!isExistingFw) allNonFw.add(ch);
        }
        List<TextLine> allPreLines = buildLines(allNonFw);

        // Zeilen finden die die Spaltengrenze ueberbruecken + Tabellenstruktur haben
        List<TextLine> spanningTableLines = new ArrayList<>();
        for (TextLine line : allPreLines)
        {
            if (line.chars.isEmpty()) continue;
            List<ClassifiedChar> sortedChars = new ArrayList<>(line.chars);
            sortedChars.sort(Comparator.comparingDouble(c -> c.raw.x));
            float lineMinX = sortedChars.get(0).raw.x;
            float lineMaxX = sortedChars.get(sortedChars.size() - 1).raw.x
                + sortedChars.get(sortedChars.size() - 1).raw.width;

            // Zeile muss beide Spalten ueberspannen
            if (lineMinX >= splitX - 10 || lineMaxX <= splitX + 10) continue;

            // Kein grosser Gap am Split-Punkt → Text laeuft durch
            boolean hasGapAtSplit = false;
            for (int j = 1; j < sortedChars.size(); j++)
            {
                float prevEnd = sortedChars.get(j - 1).raw.x + sortedChars.get(j - 1).raw.width;
                float nextStart = sortedChars.get(j).raw.x;
                float gapSize = nextStart - prevEnd;
                if (gapSize > sortedChars.get(j - 1).raw.width * 3
                    && prevEnd < splitX && nextStart > splitX)
                {
                    hasGapAtSplit = true;
                    break;
                }
            }
            if (hasGapAtSplit) continue;

            // Tabellenstruktur: mindestens 1 grosser Gap anderswo
            SplitResult sr = splitLineAtGaps(line, GAP_THRESHOLD);
            if (sr != null && sr.row.cells.size() >= 2)
            {
                spanningTableLines.add(line);
            }
        }

        // Zusammenhaengende Bloecke von spanning-table-lines → Cross-Column
        if (spanningTableLines.size() >= MIN_TABLE_ROWS)
        {
            List<List<TextLine>> spanBlocks = findContiguousTableBlocks(spanningTableLines, allPreLines);
            for (List<TextLine> block : spanBlocks)
            {
                if (block.size() >= MIN_TABLE_ROWS)
                {
                    LOGGER.debug("Cross-column Tabelle (Text ueber Gap) erkannt: {} Zeilen ab Y={}",
                        block.size(), block.get(0).y);
                    for (TextLine line : block)
                    {
                        result.add((float) line.y);
                    }
                }
            }
        }

        // --- Weg 1: Vorlaeufig in links/rechts aufteilen (ohne bereits erkannte fullwidth) ---
        List<ClassifiedChar> leftChars = new ArrayList<>();
        List<ClassifiedChar> rightChars = new ArrayList<>();
        for (ClassifiedChar ch : allNonFw)
        {
            boolean isNewFw = false;
            for (Float fwY : result)
            {
                if (Math.abs(ch.raw.y - fwY) < LINE_Y_TOLERANCE + 1)
                {
                    isNewFw = true;
                    break;
                }
            }
            if (isNewFw) continue;
            if (ch.raw.x + ch.raw.width / 2 < splitX) leftChars.add(ch);
            else rightChars.add(ch);
        }

        List<TextLine> leftLines = buildLines(leftChars);
        List<TextLine> rightLines = buildLines(rightChars);

        // Tabellenstrukturierte Zeilen pro Seite finden (mindestens 1 grosser Gap)
        List<TextLine> leftTableLines = new ArrayList<>();
        for (TextLine line : leftLines)
        {
            SplitResult sr = splitLineAtGaps(line, GAP_THRESHOLD);
            if (sr != null && sr.row.cells.size() >= 2) leftTableLines.add(line);
        }

        List<TextLine> rightTableLines = new ArrayList<>();
        for (TextLine line : rightLines)
        {
            SplitResult sr = splitLineAtGaps(line, GAP_THRESHOLD);
            if (sr != null && sr.row.cells.size() >= 2) rightTableLines.add(line);
        }

        if (!leftTableLines.isEmpty() && !rightTableLines.isEmpty())
        {
            // Zusammenhaengende Bloecke in links und rechts finden
            List<List<TextLine>> leftBlocks = findContiguousTableBlocks(leftTableLines, leftLines);
            List<List<TextLine>> rightBlocks = findContiguousTableBlocks(rightTableLines, rightLines);

            // Bloecke matchen: gleiche Zeilenanzahl UND gleiche Y-Positionen
            for (List<TextLine> leftBlock : leftBlocks)
            {
                for (List<TextLine> rightBlock : rightBlocks)
                {
                    if (leftBlock.size() != rightBlock.size()) continue;
                    if (leftBlock.size() < MIN_TABLE_ROWS) continue;

                    boolean allMatch = true;
                    for (int i = 0; i < leftBlock.size(); i++)
                    {
                        if (Math.abs(leftBlock.get(i).y - rightBlock.get(i).y) > LINE_Y_TOLERANCE + 1)
                        {
                            allMatch = false;
                            break;
                        }
                    }

                    if (allMatch)
                    {
                        LOGGER.debug("Cross-column Tabelle (Y-Match) erkannt: {} Zeilen ab Y={}",
                            leftBlock.size(), leftBlock.get(0).y);
                        for (TextLine line : leftBlock)
                        {
                            result.add((float) line.y);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Gruppiert Tabellenzeilen in zusammenhaengende Bloecke.
     * Ein Block besteht aus aufeinanderfolgenden Zeilen (im Kontext aller Zeilen),
     * die alle Tabellenstruktur haben.
     */
    private List<List<TextLine>> findContiguousTableBlocks(
        List<TextLine> tableLines, List<TextLine> allLines)
    {
        List<List<TextLine>> blocks = new ArrayList<>();
        if (tableLines.isEmpty()) return blocks;

        // Indices der Tabellenzeilen in allLines finden
        java.util.Set<Double> tableYs = new java.util.HashSet<>();
        for (TextLine tl : tableLines) tableYs.add(tl.y);

        List<Integer> tableIndices = new ArrayList<>();
        for (int i = 0; i < allLines.size(); i++)
        {
            for (Double ty : tableYs)
            {
                if (Math.abs(allLines.get(i).y - ty) < LINE_Y_TOLERANCE)
                {
                    tableIndices.add(i);
                    break;
                }
            }
        }

        // Zusammenhaengende Index-Folgen gruppieren
        List<TextLine> currentBlock = new ArrayList<>();
        int prevIdx = -2;
        for (int idx : tableIndices)
        {
            if (idx != prevIdx + 1 && !currentBlock.isEmpty())
            {
                blocks.add(currentBlock);
                currentBlock = new ArrayList<>();
            }
            currentBlock.add(allLines.get(idx));
            prevIdx = idx;
        }
        if (!currentBlock.isEmpty()) blocks.add(currentBlock);

        return blocks;
    }

    // =====================================================================
    // Einrueckungserkennung
    // =====================================================================

    /**
     * Erkennt wiederkehrende Einrueckungspositionen (Anker) in den Zeilen.
     * Gibt sortierte Liste von X-Ankerpositionen zurueck.
     * Die haeufigste Position = Body-Text, niedrigere Positionen = strukturelle Marker.
     */
    private List<Float> detectIndentAnchors(List<TextLine> lines)
    {
        if (lines.isEmpty()) return new ArrayList<>();

        // xMin-Werte in Buckets sammeln
        java.util.Map<Integer, Integer> histogram = new java.util.TreeMap<>();
        for (TextLine line : lines)
        {
            if (line.chars.isEmpty()) continue;
            float xMin = line.chars.get(0).raw.x;
            int bucket = Math.round(xMin / INDENT_BUCKET_SIZE);
            histogram.merge(bucket, 1, Integer::sum);
        }

        // Peaks finden: Buckets mit mindestens 2 Zeilen
        List<Float> anchors = new ArrayList<>();
        for (var entry : histogram.entrySet())
        {
            if (entry.getValue() >= 2)
            {
                anchors.add(entry.getKey() * INDENT_BUCKET_SIZE);
            }
        }
        anchors.sort(Float::compareTo);
        return anchors;
    }

    /**
     * Bestimmt das Einrueckungslevel einer Zeile relativ zu den Ankern.
     * Level 0 = am wenigsten eingerueckt (strukturell hoeher), aufsteigend.
     * Gibt -1 zurueck wenn kein passender Anker gefunden.
     */
    private int getIndentLevel(TextLine line, List<Float> anchors)
    {
        if (line.chars.isEmpty() || anchors.isEmpty()) return -1;
        float xMin = line.chars.get(0).raw.x;

        float bestDist = Float.MAX_VALUE;
        int bestLevel = -1;
        for (int i = 0; i < anchors.size(); i++)
        {
            float dist = Math.abs(xMin - anchors.get(i));
            if (dist < bestDist && dist < INDENT_TOLERANCE)
            {
                bestDist = dist;
                bestLevel = i;
            }
        }
        return bestLevel;
    }

    // =====================================================================
    // PageNode Tree Architecture
    // =====================================================================

    enum NodeType { PAGE, BACKGROUND, CONTENT_BOX, DECO_IMAGE, VIRTUAL_DIVIDER }

    static class PageNode
    {
        NodeType nodeType;
        float x, y, width, height;
        String imageFilename;   // null for rects, set for images
        boolean isRect;
        float opacity;
        float[] fillColor;
        List<PageNode> children = new ArrayList<>();
        List<ClassifiedChar> chars = new ArrayList<>();
        List<BulletIcon> bullets = new ArrayList<>();
        String processedContent = "";

        PageNode(NodeType type, float x, float y, float width, float height)
        {
            this.nodeType = type;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        boolean contains(float px, float py)
        {
            return px >= x - 1 && px <= x + width + 1 && py >= y - 1 && py <= y + height + 1;
        }

        boolean containsRect(PageNode other)
        {
            return other.x >= x - 2 && other.x + other.width <= x + width + 2
                && other.y >= y - 2 && other.y + other.height <= y + height + 2;
        }

        float area() { return width * height; }
    }

    /**
     * Main entry point for the hierarchical PageNode tree architecture.
     * Builds a tree from page elements (images, rects), assigns chars to nodes,
     * classifies nodes, and processes them bottom-up into markdown.
     */
    public String interpretPageTree(RawPageData page)
    {
        if (page.chars == null || page.chars.isEmpty()) return "";

        // Step 1: Prepare chars
        mergeInitialChars(page.chars);
        List<ClassifiedChar> classified = classifyChars(page.chars);
        float footerThreshold = page.pageHeight - 30;
        classified.removeIf(c -> c.raw.y > footerThreshold);
        if (classified.isEmpty()) return "";

        // Check for special page types (TOC / Index)
        String specialType = detectSpecialPageType(classified);
        if ("TOC".equals(specialType))
        {
            return renderTableOfContentsPage(classified, page);
        }
        if ("INDEX".equals(specialType))
        {
            return renderIndexPage(classified);
        }

        float bodyFontSize = findBodyFontSize(classified);

        // Step 2: Collect bullet icons
        List<BulletIcon> bulletIcons = new ArrayList<>();
        if (page.images != null)
        {
            for (RawPageData.RawImage img : page.images)
            {
                float iw = Math.abs(img.width), ih = Math.abs(img.height);
                if (iw >= 3 && iw <= 15 && ih >= 3 && ih <= 15)
                    bulletIcons.add(new BulletIcon(img.x, img.y, iw, ih));
            }
        }

        // Step 3: Build page tree from rects only (keine Bilder als Bereiche)
        PageNode root = buildPageTree(page);

        // Step 4: Assign chars and bullets to leaf nodes
        assignCharsToNodes(root, classified);
        assignBulletsToNodes(root, bulletIcons);

        // Step 5: Classify node types (CONTENT_BOX vs BACKGROUND)
        classifyNodeTypes(root);

        // Step 6: Process each node bottom-up
        processNodeBottomUp(root, bodyFontSize);

        // Step 7: Bilder separat ausgeben (nur wenn Toggle aktiv)
        if (!includeImages)
        {
            return root.processedContent;
        }

        StringBuilder result = new StringBuilder(root.processedContent);
        if (page.images != null)
        {
            for (RawPageData.RawImage img : page.images)
            {
                float iw = Math.abs(img.width), ih = Math.abs(img.height);
                // Bullet-Icons ueberspringen
                if (iw >= 3 && iw <= 15 && ih >= 3 && ih <= 15) continue;
                // Seitenhintergrund ueberspringen
                if (iw * ih > page.pageWidth * page.pageHeight * 0.95f) continue;
                if (img.filename != null && !img.filename.isEmpty())
                {
                    result.append("![image](").append(img.filename).append(")\n");
                }
            }
        }

        return result.toString();
    }

    // =====================================================================
    // TOC / Index Detection and Rendering
    // =====================================================================

    /**
     * Detects if the page is a TOC or Index page.
     * TOC: has large-font title "INHALTSVERZEICHNIS", or >60% of lines end with digits
     * Index: has large-font title "INDEX" or >50% of lines contain "xxx"
     */
    private String detectSpecialPageType(List<ClassifiedChar> classified)
    {
        // Check for large-font title
        StringBuilder largeText = new StringBuilder();
        for (ClassifiedChar ch : classified)
        {
            if (ch.raw.fontSize >= 25) largeText.append(ch.raw.text);
        }
        String title = largeText.toString().trim().toUpperCase();
        if (title.contains("INHALTSVERZEICHNIS")) return "TOC";
        if (title.contains("INDEX")) return "INDEX";

        List<TextLine> lines = buildLines(classified);
        if (lines.size() < 5) return null;

        // Check for Index continuation pages: many "xxx" patterns
        int xxxLineCount = 0;
        for (TextLine line : lines)
        {
            StringBuilder lineText = new StringBuilder();
            for (ClassifiedChar ch : line.chars) lineText.append(ch.raw.text);
            if (lineText.toString().contains("xxx")) xxxLineCount++;
        }
        if (xxxLineCount > lines.size() * 0.5) return "INDEX";

        // Check for TOC continuation pages: many lines ending with trailing digits
        int digitEndCount = 0;
        for (TextLine line : lines)
        {
            List<ClassifiedChar> chars = line.chars;
            if (chars.isEmpty()) continue;
            // Check if rightmost non-space char is a digit
            for (int i = chars.size() - 1; i >= 0; i--)
            {
                String ch = chars.get(i).raw.text;
                if (" ".equals(ch)) continue;
                if (ch.matches("[0-9]")) digitEndCount++;
                break;
            }
        }
        if (digitEndCount > lines.size() * 0.6) return "TOC";

        return null;
    }

    /**
     * Renders a TOC page as a markdown table with 2 columns: Entry | Page.
     * Chars inside content-box rects are rendered separately (headings + sub-table).
     * PDF has 2 visual columns; they are concatenated sequentially (left first, then right).
     * Bold entries are rendered as **bold** in the table.
     * Accepts both digits and "XXX" as page numbers.
     */
    private String renderTableOfContentsPage(List<ClassifiedChar> classified, RawPageData page)
    {
        float pageWidth = page.pageWidth;
        float pageArea = pageWidth * page.pageHeight;

        // Collect content-box rects (non-fullpage, significant size)
        List<float[]> contentBoxes = new ArrayList<>();
        if (page.rects != null)
        {
            for (RawPageData.RawRect rect : page.rects)
            {
                float rx = rect.x, ry = rect.y, rw = rect.width, rh = rect.height;
                if (rh < 0) { ry += rh; rh = -rh; }
                if (rw < 0) { rx += rw; rw = -rw; }
                if (rw * rh > pageArea * 0.95f) continue; // skip fullpage
                if (rw > 100 && rh > 100)
                {
                    contentBoxes.add(new float[]{rx, ry, rw, rh});
                }
            }
        }

        // Separate chars into: title, box-content, and main-TOC
        List<ClassifiedChar> titleChars = new ArrayList<>();
        List<ClassifiedChar> boxChars = new ArrayList<>();
        List<ClassifiedChar> tocChars = new ArrayList<>();

        for (ClassifiedChar ch : classified)
        {
            if (ch.raw.fontSize >= 25)
            {
                titleChars.add(ch);
                continue;
            }
            boolean inBox = false;
            for (float[] box : contentBoxes)
            {
                if (ch.raw.x >= box[0] && ch.raw.x <= box[0] + box[2]
                    && ch.raw.y >= box[1] && ch.raw.y <= box[1] + box[3])
                {
                    inBox = true;
                    break;
                }
            }
            if (inBox) boxChars.add(ch);
            else tocChars.add(ch);
        }

        // Build title text
        StringBuilder titleText = new StringBuilder();
        titleChars.sort(Comparator.comparingDouble(c -> c.raw.x));
        for (ClassifiedChar ch : titleChars) titleText.append(ch.raw.text);

        // --- Render main TOC table ---
        StringBuilder sb = new StringBuilder();
        if (!titleText.toString().isBlank())
        {
            sb.append("# ").append(titleText.toString().trim()).append("\n");
        }

        if (!tocChars.isEmpty())
        {
            float splitX = findTocColumnSplit(tocChars, pageWidth);

            List<ClassifiedChar> leftChars = new ArrayList<>();
            List<ClassifiedChar> rightChars = new ArrayList<>();
            for (ClassifiedChar ch : tocChars)
            {
                if (splitX > 0 && ch.raw.x > splitX) rightChars.add(ch);
                else leftChars.add(ch);
            }

            List<String[]> rows = new ArrayList<>();
            extractTocColumnRows(buildLines(leftChars), rows);
            if (!rightChars.isEmpty())
            {
                extractTocColumnRows(buildLines(rightChars), rows);
            }

            sb.append("| | |\n|---|---|\n");
            for (String[] row : rows)
            {
                String entry = row[0].trim();
                String pageNum = row[1].trim();
                boolean bold = "true".equals(row[2]);
                if (entry.isEmpty() && pageNum.isEmpty()) continue;
                if (bold)
                {
                    sb.append("| **").append(entry).append("** | **").append(pageNum).append("** |\n");
                }
                else
                {
                    sb.append("| ").append(entry).append(" | ").append(pageNum).append(" |\n");
                }
            }
            sb.append("\n");
        }

        // --- Render box content separately ---
        if (!boxChars.isEmpty())
        {
            renderTocBoxContent(boxChars, pageWidth, sb);
        }

        return sb.toString();
    }

    /**
     * Renders content-box chars from a TOC page.
     * Large-font lines (>=18pt) become headings.
     * Other lines become table entries with XXX/digit page numbers.
     */
    private void renderTocBoxContent(List<ClassifiedChar> boxChars, float pageWidth, StringBuilder sb)
    {
        float splitX = findTocColumnSplit(boxChars, pageWidth);

        // Split into columns
        List<ClassifiedChar> leftChars = new ArrayList<>();
        List<ClassifiedChar> rightChars = new ArrayList<>();
        for (ClassifiedChar ch : boxChars)
        {
            if (splitX > 0 && ch.raw.x > splitX) rightChars.add(ch);
            else leftChars.add(ch);
        }

        // Process left column, then right column
        List<List<ClassifiedChar>> columns = new ArrayList<>();
        columns.add(leftChars);
        if (!rightChars.isEmpty()) columns.add(rightChars);

        for (List<ClassifiedChar> colChars : columns)
        {
            List<TextLine> lines = buildLines(colChars);
            boolean inTable = false;

            for (TextLine line : lines)
            {
                if (line.chars.isEmpty()) continue;

                // Check if this is a heading (large font, no page number pattern)
                float maxFontSize = 0;
                for (ClassifiedChar ch : line.chars)
                {
                    if (ch.raw.fontSize > maxFontSize) maxFontSize = ch.raw.fontSize;
                }

                StringBuilder lineText = new StringBuilder();
                for (ClassifiedChar ch : line.chars) lineText.append(ch.raw.text);
                String text = lineText.toString().trim();

                if (maxFontSize >= 16)
                {
                    // This is a heading - close any open table and emit heading
                    if (inTable)
                    {
                        sb.append("\n");
                        inTable = false;
                    }
                    sb.append("### ").append(text).append("\n");
                }
                else
                {
                    // Table entry - open table if not already open
                    if (!inTable)
                    {
                        sb.append("| | |\n|---|---|\n");
                        inTable = true;
                    }
                    // Extract page number (digits or XXX at end)
                    String[] entryAndNum = splitTocEntryAndNumber(line.chars);
                    String entry = entryAndNum[0].trim();
                    String pageNum = entryAndNum[1].trim();
                    if (!entry.isEmpty() || !pageNum.isEmpty())
                    {
                        sb.append("| ").append(entry).append(" | ").append(pageNum).append(" |\n");
                    }
                }
            }
            if (inTable) sb.append("\n");
        }
    }

    /**
     * Splits a TOC line into entry text and page number.
     * Accepts trailing digits or trailing X-patterns (XXX, XXXI, etc.) as page numbers.
     */
    private String[] splitTocEntryAndNumber(List<ClassifiedChar> chars)
    {
        // Scan from right: trailing digits or X chars are the page number
        int numStartIdx = chars.size();
        for (int i = chars.size() - 1; i >= 0; i--)
        {
            String ch = chars.get(i).raw.text;
            if (ch.matches("[0-9X]"))
            {
                numStartIdx = i;
            }
            else if (" ".equals(ch) && numStartIdx < chars.size())
            {
                // Space within number area - skip
            }
            else
            {
                break;
            }
        }

        StringBuilder entryBuilder = new StringBuilder();
        StringBuilder numBuilder = new StringBuilder();
        for (int i = 0; i < chars.size(); i++)
        {
            if (i < numStartIdx) entryBuilder.append(chars.get(i).raw.text);
            else numBuilder.append(chars.get(i).raw.text);
        }
        return new String[]{entryBuilder.toString(), numBuilder.toString()};
    }

    /**
     * Finds the column split for a TOC page by locating the right edge of
     * the left column's page numbers (digits or XXX). The split is placed
     * just after the rightmost page-number char in the left half.
     */
    private float findTocColumnSplit(List<ClassifiedChar> chars, float pageWidth)
    {
        float midpoint = pageWidth / 2.0f;

        // Find the rightmost page-number-end X in the left half
        float leftMaxEnd = 0;
        for (ClassifiedChar ch : chars)
        {
            if (ch.raw.text.matches("[0-9XIVxiv]"))
            {
                float end = ch.raw.x + ch.raw.width;
                if (end < midpoint && end > leftMaxEnd)
                {
                    leftMaxEnd = end;
                }
            }
        }

        if (leftMaxEnd > 0)
        {
            return leftMaxEnd + 3;
        }

        return midpoint;
    }

    /**
     * Extracts TOC rows from lines of a single column.
     * Page numbers are trailing digits or XXX patterns at the end of the line.
     */
    private void extractTocColumnRows(List<TextLine> lines, List<String[]> rows)
    {
        for (TextLine line : lines)
        {
            if (line.chars.isEmpty()) continue;
            List<ClassifiedChar> chars = line.chars;

            // Check if line is bold (section heading)
            boolean isBold = false;
            int boldCount = 0;
            for (ClassifiedChar ch : chars) if (ch.isBold) boldCount++;
            if (boldCount > chars.size() * 0.5) isBold = true;

            String[] entryAndNum = splitTocEntryAndNumber(chars);
            String entryText = entryAndNum[0].trim();
            String pageNum = entryAndNum[1].trim();

            if (!entryText.isEmpty() || !pageNum.isEmpty())
            {
                rows.add(new String[]{entryText, pageNum, isBold ? "true" : "false"});
            }
        }
    }

    /**
     * Renders an Index page as a markdown table with 2 columns: Entry | Page.
     * PDF has 3 visual columns per line, each with entry + "xxx" page number.
     * Columns are concatenated sequentially (column 1, then 2, then 3).
     */
    private String renderIndexPage(List<ClassifiedChar> classified)
    {
        // Separate title chars
        List<ClassifiedChar> contentChars = new ArrayList<>();
        StringBuilder titleText = new StringBuilder();
        List<ClassifiedChar> titleChars = new ArrayList<>();
        for (ClassifiedChar ch : classified)
        {
            if (ch.raw.fontSize >= 25) titleChars.add(ch);
            else contentChars.add(ch);
        }
        titleChars.sort(Comparator.comparingDouble(c -> c.raw.x));
        for (ClassifiedChar ch : titleChars) titleText.append(ch.raw.text);

        if (contentChars.isEmpty()) return "";

        // Build lines
        List<TextLine> lines = buildLines(contentChars);

        // Find column boundaries by analyzing "xxx" X-positions across all lines
        // Collect all X-positions where "xxx" runs start
        List<Float> xxxPositions = new ArrayList<>();
        for (TextLine line : lines)
        {
            List<ClassifiedChar> chars = line.chars;
            for (int i = 0; i < chars.size() - 2; i++)
            {
                if ("x".equals(chars.get(i).raw.text)
                    && "x".equals(chars.get(i + 1).raw.text)
                    && "x".equals(chars.get(i + 2).raw.text))
                {
                    xxxPositions.add(chars.get(i).raw.x);
                    // Skip past this xxx run
                    while (i < chars.size() - 1 && "x".equals(chars.get(i + 1).raw.text)) i++;
                }
            }
        }

        // Cluster xxx positions to find the distinct column page-number positions
        xxxPositions.sort(Float::compare);
        List<Float> xxxClusters = new ArrayList<>();
        if (!xxxPositions.isEmpty())
        {
            float clusterStart = xxxPositions.get(0);
            float clusterSum = clusterStart;
            int clusterCount = 1;
            for (int i = 1; i < xxxPositions.size(); i++)
            {
                if (xxxPositions.get(i) - xxxPositions.get(i - 1) > 30)
                {
                    xxxClusters.add(clusterSum / clusterCount);
                    clusterSum = xxxPositions.get(i);
                    clusterCount = 1;
                }
                else
                {
                    clusterSum += xxxPositions.get(i);
                    clusterCount++;
                }
            }
            xxxClusters.add(clusterSum / clusterCount);
        }

        // Determine column split boundaries: right after each xxx cluster ends.
        // Each "xxx" is about 14 units wide (3 chars * ~4.5 width).
        // The boundary sits between the end of xxx and the start of the next column's text.
        List<Float> colBoundaries = new ArrayList<>();
        if (xxxClusters.size() >= 2)
        {
            for (int i = 0; i < xxxClusters.size() - 1; i++)
            {
                colBoundaries.add(xxxClusters.get(i) + 14);
            }
        }

        // Parse each column independently, collect rows per column
        List<List<String[]>> columnRows = new ArrayList<>();

        if (colBoundaries.isEmpty())
        {
            // Single column - just parse directly
            List<String[]> singleCol = new ArrayList<>();
            extractIndexColumnRows(lines, singleCol);
            columnRows.add(singleCol);
        }
        else
        {
            // Split chars into columns based on boundaries
            for (int col = 0; col <= colBoundaries.size(); col++)
            {
                float minX = col == 0 ? 0 : colBoundaries.get(col - 1);
                float maxX = col < colBoundaries.size() ? colBoundaries.get(col) : Float.MAX_VALUE;

                List<ClassifiedChar> colChars = new ArrayList<>();
                for (ClassifiedChar ch : contentChars)
                {
                    if (ch.raw.x >= minX && ch.raw.x < maxX) colChars.add(ch);
                }

                List<TextLine> colLines = buildLines(colChars);
                List<String[]> colRowList = new ArrayList<>();
                extractIndexColumnRows(colLines, colRowList);
                columnRows.add(colRowList);
            }
        }

        // Build markdown
        StringBuilder sb = new StringBuilder();
        if (!titleText.toString().isBlank())
        {
            sb.append("# ").append(titleText.toString().trim()).append("\n");
        }
        sb.append("| | |\n|---|---|\n");
        for (List<String[]> colRows : columnRows)
        {
            for (String[] row : colRows)
            {
                String entry = row[0].trim();
                String pageNum = row[1].trim();
                if (entry.isEmpty() && pageNum.isEmpty()) continue;
                sb.append("| ").append(entry).append(" | ").append(pageNum).append(" |\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Extracts Index rows from lines. Each line has entry text followed by "xxx" page numbers.
     * Splits at "xxx" boundaries.
     */
    private void extractIndexColumnRows(List<TextLine> lines, List<String[]> rows)
    {
        for (TextLine line : lines)
        {
            if (line.chars.isEmpty()) continue;
            List<ClassifiedChar> chars = line.chars;

            // Build text and find xxx boundaries
            StringBuilder entryText = new StringBuilder();
            StringBuilder pageNum = new StringBuilder();
            boolean inXxx = false;

            for (int i = 0; i < chars.size(); i++)
            {
                String ch = chars.get(i).raw.text;
                if ("x".equals(ch))
                {
                    // Check if this starts/continues an xxx run
                    if (!inXxx)
                    {
                        // Check if at least 3 consecutive x
                        int xCount = 0;
                        for (int j = i; j < chars.size() && "x".equals(chars.get(j).raw.text); j++) xCount++;
                        if (xCount >= 3)
                        {
                            inXxx = true;
                            pageNum.append(ch);
                        }
                        else
                        {
                            entryText.append(ch);
                        }
                    }
                    else
                    {
                        pageNum.append(ch);
                    }
                }
                else
                {
                    if (inXxx)
                    {
                        // End of xxx run - this should not happen within a single column
                        // (new entry would be in a different column)
                        // Save current entry and start new
                        String e = entryText.toString().trim();
                        String p = pageNum.toString().trim();
                        if (!e.isEmpty() || !p.isEmpty())
                        {
                            rows.add(new String[]{e, p});
                        }
                        entryText = new StringBuilder();
                        pageNum = new StringBuilder();
                        inXxx = false;
                    }
                    entryText.append(ch);
                }
            }

            // Final entry on line
            String e = entryText.toString().trim();
            String p = pageNum.toString().trim();
            if (!e.isEmpty() || !p.isEmpty())
            {
                rows.add(new String[]{e, p});
            }
        }
    }

    /**
     * Builds the PageNode tree from images and rects on the page.
     * The root is PAGE with page dimensions.
     * Children are sorted by area (largest first) and nested into the
     * smallest existing node that fully contains them.
     */
    private PageNode buildPageTree(RawPageData page)
    {
        PageNode root = new PageNode(NodeType.PAGE, 0, 0, page.pageWidth, page.pageHeight);
        float pageArea = page.pageWidth * page.pageHeight;

        List<PageNode> candidates = new ArrayList<>();

        // Bilder werden NICHT als Bereiche in den Baum eingefuegt.
        // Grund: Bilder haben Transparenzen, Text fliesst um sie herum,
        // aber manche Textzeichen liegen innerhalb der Bildgrenzen.
        // Bilder als Bereiche fuehren zu falscher Textzuordnung.
        // Stattdessen werden Bilder nur als Referenzen am Schluss ausgegeben.
        // Bildausgabe erfolgt separat ueber processedContent.

        // Collect rect nodes (NUR Rects bilden die Bereichshierarchie)
        if (page.rects != null)
        {
            for (RawPageData.RawRect rect : page.rects)
            {
                float rx = rect.x;
                float ry = rect.y;
                float rw = rect.width;
                float rh = rect.height;
                if (rh < 0) { ry += rh; rh = -rh; }
                if (rw < 0) { rx += rw; rw = -rw; }
                // Clip to page bounds
                float visibleX = Math.max(rx, 0);
                float visibleY = Math.max(ry, 0);
                float visibleW = Math.min(rx + rw, page.pageWidth) - visibleX;
                float visibleH = Math.min(ry + rh, page.pageHeight) - visibleY;
                if (visibleW < 3 || visibleH < 3) continue;
                // Skip truly fullpage backgrounds (>95% of page area)
                if (visibleW * visibleH > pageArea * 0.95f) continue;

                PageNode node = new PageNode(NodeType.BACKGROUND, visibleX, visibleY, visibleW, visibleH);
                node.isRect = true;
                node.opacity = rect.opacity;
                node.fillColor = rect.fillColor;
                candidates.add(node);
            }
        }

        // Sort candidates by area (largest first)
        candidates.sort((a, b) -> Float.compare(b.area(), a.area()));

        // For each candidate, find the smallest existing node that fully contains it
        for (PageNode candidate : candidates)
        {
            PageNode parent = findSmallestContainer(root, candidate);
            parent.children.add(candidate);
        }

        return root;
    }

    /**
     * Finds the smallest (deepest) node in the tree that fully contains the given candidate.
     * Searches depth-first to prefer deeper nodes over shallower ones.
     */
    private PageNode findSmallestContainer(PageNode node, PageNode candidate)
    {
        // Check children first (they are smaller, so they are preferred)
        for (PageNode child : node.children)
        {
            if (child.containsRect(candidate))
            {
                return findSmallestContainer(child, candidate);
            }
        }
        // No child contains it, so this node is the smallest container
        return node;
    }

    /**
     * Assigns each classified char to the smallest (deepest) node that contains it.
     */
    private void assignCharsToNodes(PageNode root, List<ClassifiedChar> chars)
    {
        for (ClassifiedChar ch : chars)
        {
            // Alle Zeichen dem tiefsten Rect-Knoten zuordnen.
            // Bilder sind nicht mehr im Baum, daher keine Sonderbehandlung noetig.
            PageNode target = findSmallestContainerForPoint(root, ch.raw.x, ch.raw.y);
            target.chars.add(ch);
        }
    }

    /**
     * Assigns each bullet icon to the smallest (deepest) node that contains it.
     */
    private void assignBulletsToNodes(PageNode root, List<BulletIcon> bullets)
    {
        for (BulletIcon bi : bullets)
        {
            PageNode target = findSmallestContainerForPoint(root, bi.x, bi.y);
            target.bullets.add(bi);
        }
    }

    /**
     * Finds the smallest (deepest) node containing the given point.
     */
    private PageNode findSmallestContainerForPoint(PageNode node, float px, float py)
    {
        for (PageNode child : node.children)
        {
            if (child.contains(px, py))
            {
                return findSmallestContainerForPoint(child, px, py);
            }
        }
        return node;
    }

    /**
     * Classifies node types via post-order traversal.
     * Nodes with chars or children with chars become CONTENT_BOX.
     * Image nodes without content become DECO_IMAGE.
     * Rect nodes without content become BACKGROUND.
     * Root is always PAGE.
     */
    private void classifyNodeTypes(PageNode node)
    {
        // Post-order: children first
        for (PageNode child : node.children)
        {
            classifyNodeTypes(child);
        }

        // Root is always PAGE
        if (node.nodeType == NodeType.PAGE) return;

        // Count non-blank chars in this node and all descendants
        int charCount = countNonBlankChars(node);

        // Zeichendichte: echte Textkaesten haben mind. ~0.002 Chars/pt²
        // (z.B. 30 Chars in einer 130x112pt Box = 0.002)
        // Ein 310x842pt Bild mit 31 Chars = 0.0001 → kein Textkasten
        float area = node.area();
        float density = area > 0 ? charCount / area : 0;
        boolean hasSignificantContent = charCount >= 10 && density > 0.001f;

        if (!hasSignificantContent)
        {
            // Kein signifikanter Textinhalt
            if (node.isRect) node.nodeType = NodeType.BACKGROUND;
            else node.nodeType = NodeType.DECO_IMAGE;
            // Chars zurueck zum Elternknoten verschieben (sind kein Box-Inhalt)
            // Finde den Elternknoten nicht direkt verfuegbar, daher:
            // Chars bleiben im Node, werden aber nicht als Box behandelt.
            // Stattdessen: Typ auf BACKGROUND setzen → processNodeBottomUp
            // behandelt BACKGROUND wie PAGE (merged content, kein Blockquote)
        }
        else
        {
            node.nodeType = NodeType.CONTENT_BOX;
        }
    }

    /**
     * Counts non-blank chars in a node and all its descendants.
     */
    private int countNonBlankChars(PageNode node)
    {
        int count = 0;
        for (ClassifiedChar ch : node.chars)
        {
            if (!ch.raw.text.isBlank()) count++;
        }
        for (PageNode child : node.children)
        {
            count += countNonBlankChars(child);
        }
        return count;
    }

    /**
     * Processes nodes bottom-up (post-order).
     * Leaf nodes with chars get their content processed (columns, headings, tables, body).
     * CONTENT_BOX nodes wrap their output in blockquote.
     * DECO_IMAGE nodes output image references.
     * PAGE/BACKGROUND nodes merge children's content by Y position.
     */
    private void processNodeBottomUp(PageNode node, float bodyFontSize)
    {
        // Post-order: process children first
        for (PageNode child : node.children)
        {
            processNodeBottomUp(child, bodyFontSize);
        }

        switch (node.nodeType)
        {
            case DECO_IMAGE:
            {
                if (node.imageFilename != null && !node.imageFilename.isEmpty())
                {
                    node.processedContent = "![image](" + node.imageFilename + ")\n";
                }
                break;
            }
            case CONTENT_BOX:
            {
                // Process own chars if any
                String ownContent = processNodeChars(node, bodyFontSize);
                // Merge children content
                String childContent = mergeChildContent(node);
                node.processedContent = ownContent + childContent;
                break;
            }
            case PAGE:
            case BACKGROUND:
            {
                // Process own chars
                String ownContent = processNodeChars(node, bodyFontSize);
                // Merge children content at their Y positions into the main flow
                String childContent = mergeChildContent(node);
                node.processedContent = ownContent + childContent;
                break;
            }
            default:
                break;
        }
    }

    /**
     * Merges processed content of child nodes, sorted by their Y position.
     */
    private String mergeChildContent(PageNode node)
    {
        if (node.children.isEmpty()) return "";
        List<PageNode> sorted = new ArrayList<>(node.children);
        sorted.sort(Comparator.comparingDouble(n -> n.y));
        StringBuilder sb = new StringBuilder();
        for (PageNode child : sorted)
        {
            if (!child.processedContent.isEmpty())
            {
                sb.append(child.processedContent);
            }
        }
        return sb.toString();
    }

    /**
     * Processes the chars directly owned by a node into markdown content.
     * Detects columns, headings, bullets, tables, and body text.
     */
    private String processNodeChars(PageNode node, float bodyFontSize)
    {
        if (node.chars.isEmpty()) return "";

        // Get raw chars for histogram-based column detection
        List<RawPageData.RawChar> rawChars = new ArrayList<>();
        for (ClassifiedChar cc : node.chars) rawChars.add(cc.raw);

        float nodeWidth = node.width > 0 ? node.width : 600;
        float splitX = findColumnSplitByHistogram(rawChars, nodeWidth);

        // Split into columns
        List<ClassifiedChar> leftChars = new ArrayList<>();
        List<ClassifiedChar> rightChars = new ArrayList<>();
        List<ClassifiedChar> fullWidthChars = new ArrayList<>();

        if (splitX > 0)
        {
            // Adjust splitX relative to node position for nodes that are not at x=0
            // The histogram works on raw x positions, so splitX is already absolute

            java.util.Set<Float> fullWidthYs = new java.util.HashSet<>();
            for (ClassifiedChar ch : node.chars)
            {
                if (ch.isInitial) fullWidthYs.add(ch.raw.y);
            }

            List<TextLine> preLines = buildLines(node.chars);
            for (TextLine line : preLines)
            {
                if (line.chars.isEmpty()) continue;
                float avgSize = (float) line.chars.stream().mapToDouble(c -> c.raw.fontSize).average().orElse(0);
                if (avgSize > 25)
                {
                    fullWidthYs.add((float) line.y);
                    continue;
                }

                List<ClassifiedChar> sorted = new ArrayList<>(line.chars);
                sorted.sort(Comparator.comparingDouble(c -> c.raw.x));
                float lineMinX = sorted.get(0).raw.x;
                float lineMaxX = sorted.get(sorted.size() - 1).raw.x + sorted.get(sorted.size() - 1).raw.width;
                boolean spansColumns = lineMinX < splitX - 30 && lineMaxX > splitX + 30;
                if (!spansColumns) continue;

                boolean hasGapAtSplit = false;
                for (int j = 1; j < sorted.size(); j++)
                {
                    float prevEnd = sorted.get(j - 1).raw.x + sorted.get(j - 1).raw.width;
                    float nextStart = sorted.get(j).raw.x;
                    float gapSize = nextStart - prevEnd;
                    if (gapSize > sorted.get(j - 1).raw.width * 3
                        && prevEnd < splitX && nextStart > splitX)
                    {
                        hasGapAtSplit = true;
                        break;
                    }
                }
                if (hasGapAtSplit) continue;

                fullWidthYs.add((float) line.y);
            }

            for (ClassifiedChar ch : node.chars)
            {
                boolean isFullWidth = false;
                for (Float fwY : fullWidthYs)
                {
                    if (Math.abs(ch.raw.y - fwY) < LINE_Y_TOLERANCE + 1) { isFullWidth = true; break; }
                }
                if (isFullWidth) fullWidthChars.add(ch);
                else if (ch.raw.x + ch.raw.width / 2 < splitX) leftChars.add(ch);
                else rightChars.add(ch);
            }
        }
        else
        {
            fullWidthChars.addAll(node.chars);
        }

        // Build lines per column
        List<TextLine> leftLines = buildLines(leftChars);
        List<TextLine> rightLines = buildLines(rightChars);
        List<TextLine> fullWidthLines = buildLines(fullWidthChars);

        // Classify each column into content blocks
        List<ContentBlock> leftBlocks = classifyColumnLines(leftLines, bodyFontSize, node.bullets);
        List<ContentBlock> rightBlocks = classifyColumnLines(rightLines, bodyFontSize, node.bullets);
        List<ContentBlock> fullWidthBlocks = classifyColumnLines(fullWidthLines, bodyFontSize, node.bullets);

        // Cross-column table merging
        if (!leftBlocks.isEmpty() && !rightBlocks.isEmpty())
        {
            for (ContentBlock lb : leftBlocks)
            {
                if (lb.type != ContentBlock.Type.TABLE) continue;
                for (ContentBlock rb : rightBlocks)
                {
                    if (rb.type != ContentBlock.Type.TABLE) continue;
                    if (Math.abs(lb.y - rb.y) < LINE_Y_TOLERANCE + 1)
                    {
                        if (lb.tableCells != null && rb.tableCells != null)
                        {
                            lb.tableCells.addAll(rb.tableCells);
                        }
                        rb.type = ContentBlock.Type.BODY;
                        rb.text = "";
                        rb.tableCells = null;
                    }
                }
            }
        }

        // Reading order assembly
        List<ContentBlock> ordered = new ArrayList<>();
        boolean hasColumns = !leftBlocks.isEmpty() && !rightBlocks.isEmpty();

        if (hasColumns && !fullWidthBlocks.isEmpty())
        {
            fullWidthBlocks.sort(Comparator.comparingDouble(b -> b.y));
            leftBlocks.sort(Comparator.comparingDouble(b -> b.y));
            rightBlocks.sort(Comparator.comparingDouble(b -> b.y));

            double columnMinY = Math.min(
                leftBlocks.stream().mapToDouble(b -> b.y).min().orElse(Double.MAX_VALUE),
                rightBlocks.stream().mapToDouble(b -> b.y).min().orElse(Double.MAX_VALUE));
            double columnMaxY = Math.max(
                leftBlocks.stream().mapToDouble(b -> b.y).max().orElse(0),
                rightBlocks.stream().mapToDouble(b -> b.y).max().orElse(0));

            List<ContentBlock> topFw = new ArrayList<>();
            List<ContentBlock> dividers = new ArrayList<>();
            List<ContentBlock> bottomFw = new ArrayList<>();
            for (ContentBlock b : fullWidthBlocks)
            {
                if (b.y < columnMinY) topFw.add(b);
                else if (b.y > columnMaxY) bottomFw.add(b);
                else dividers.add(b);
            }

            ordered.addAll(topFw);

            List<Double> cutYs = new ArrayList<>();
            for (ContentBlock d : dividers) cutYs.add(d.y);
            cutYs.add(Double.MAX_VALUE);

            int leftIdx = 0, rightIdx = 0;
            for (int di = 0; di < cutYs.size(); di++)
            {
                double cutY = cutYs.get(di);
                while (leftIdx < leftBlocks.size() && leftBlocks.get(leftIdx).y < cutY)
                    ordered.add(leftBlocks.get(leftIdx++));
                while (rightIdx < rightBlocks.size() && rightBlocks.get(rightIdx).y < cutY)
                    ordered.add(rightBlocks.get(rightIdx++));
                if (di < dividers.size())
                    ordered.add(dividers.get(di));
            }

            ordered.addAll(bottomFw);
        }
        else if (hasColumns)
        {
            leftBlocks.sort(Comparator.comparingDouble(b -> b.y));
            ordered.addAll(leftBlocks);
            rightBlocks.sort(Comparator.comparingDouble(b -> b.y));
            ordered.addAll(rightBlocks);
        }
        else
        {
            List<ContentBlock> allBlocks = new ArrayList<>(fullWidthBlocks);
            allBlocks.addAll(leftBlocks);
            allBlocks.addAll(rightBlocks);
            allBlocks.sort(Comparator.comparingDouble(b -> b.y));
            ordered.addAll(allBlocks);
        }

        // Consolidate into text
        return consolidateBlocks(ordered);
    }

    // =====================================================================
    // Datenklassen
    // =====================================================================

    static class ClassifiedChar
    {
        final RawPageData.RawChar raw;
        final String family;
        final boolean isBold;
        final boolean isItalic;
        final boolean isSmallCaps;
        final boolean isInitial;
        final boolean isLargeFont;
        final boolean isOrnament;

        ClassifiedChar(RawPageData.RawChar raw, String family, boolean isBold, boolean isItalic,
                        boolean isSmallCaps, boolean isInitial, boolean isLargeFont)
        {
            this(raw, family, isBold, isItalic, isSmallCaps, isInitial, isLargeFont, false);
        }

        ClassifiedChar(RawPageData.RawChar raw, String family, boolean isBold, boolean isItalic,
                        boolean isSmallCaps, boolean isInitial, boolean isLargeFont, boolean isOrnament)
        {
            this.raw = raw;
            this.family = family;
            this.isBold = isBold;
            this.isItalic = isItalic;
            this.isSmallCaps = isSmallCaps;
            this.isInitial = isInitial;
            this.isLargeFont = isLargeFont;
            this.isOrnament = isOrnament;
        }
    }

    static class TextLine
    {
        double y;
        final List<ClassifiedChar> chars = new ArrayList<>();
        /** Wenn gesetzt: diese Line ist eine Pseudo-Line, die statt normaler Verarbeitung
         *  eine line-basierte Tabelle (siehe {@link LineBasedTableDetector}) als
         *  Markdown-Tabelle emittiert. */
        LineBasedTableDetector.LineBasedTable lineBasedTable;
        TextLine(double y) { this.y = y; }
    }

    static class TableRow
    {
        final List<String> cells;
        TableRow(List<String> cells) { this.cells = new ArrayList<>(cells); }
    }

    static class DetectedTable
    {
        final List<TableRow> rows;
        int startLineIdx;
        int endLineIdx;
        /** Line-Index pro Reihe (parallel zu {@code rows}). Sobald Continuation-Cells
         *  die 1:1-Beziehung Reihe↔Line brechen, ist {@code rows.size() != endLineIdx-startLineIdx+1};
         *  dieser Index wird dann zum sicheren Mapping verwendet. */
        List<Integer> rowLineIdx;
        DetectedTable(List<TableRow> rows, int startLineIdx, int endLineIdx)
        {
            this.rows = rows;
            this.startLineIdx = startLineIdx;
            this.endLineIdx = endLineIdx;
        }
        DetectedTable(List<TableRow> rows, int startLineIdx, int endLineIdx, List<Integer> rowLineIdx)
        {
            this.rows = rows;
            this.startLineIdx = startLineIdx;
            this.endLineIdx = endLineIdx;
            this.rowLineIdx = rowLineIdx;
        }
    }

    static class SplitResult
    {
        final TableRow row;
        final float[] gapEnds;
        SplitResult(TableRow row, float[] gapEnds)
        {
            this.row = row;
            this.gapEnds = gapEnds;
        }
    }

    static class FormattedSpan
    {
        final boolean bold;
        final boolean italic;
        final float fontSize;
        final String fontFamily;
        final boolean isSmallCaps;
        final boolean isInitial;
        final boolean isOrnament;
        final StringBuilder text = new StringBuilder();
        final List<ClassifiedChar> chars = new ArrayList<>();

        FormattedSpan(boolean bold, boolean italic, float fontSize, String fontFamily,
                       boolean isSmallCaps, boolean isInitial)
        {
            this(bold, italic, fontSize, fontFamily, isSmallCaps, isInitial, false);
        }

        FormattedSpan(boolean bold, boolean italic, float fontSize, String fontFamily,
                       boolean isSmallCaps, boolean isInitial, boolean isOrnament)
        {
            this.bold = bold;
            this.italic = italic;
            this.fontSize = fontSize;
            this.fontFamily = fontFamily;
            this.isSmallCaps = isSmallCaps;
            this.isInitial = isInitial;
            this.isOrnament = isOrnament;
        }
    }
}
