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
    private static final float GAP_THRESHOLD = 14.0f;
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
        if (page.chars == null || page.chars.isEmpty()) return "";

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

                if (!spansColumns) continue;

                // Heading-Check: groessere Schrift (bold ODER deutlich groesser)
                float avgSize = (float) line.chars.stream().mapToDouble(c -> c.raw.fontSize).average().orElse(0);
                boolean mostlyBold = line.chars.stream().filter(c -> c.isBold).count() > line.chars.size() * 0.5;
                boolean largerThanBody = avgSize > bodyFontSize + 1.5f;
                boolean muchLargerThanBody = avgSize > bodyFontSize * 1.4f;

                // Fullwidth Heading: bold+groesser ODER deutlich groesser (zentrierte Headlines)
                if ((largerThanBody && mostlyBold) || muchLargerThanBody)
                {
                    fullWidthYs.add((float) line.y);
                    continue;
                }

                // Fullwidth Tabelle: spannt beide Spalten + mehrere grosse Gaps
                // (Nicht nur am Seitenfuss, sondern ueberall auf der Seite)
                {
                    int largeGaps = 0;
                    for (int j = 1; j < sorted.size(); j++)
                    {
                        float gap = sorted.get(j).raw.x - (sorted.get(j - 1).raw.x + sorted.get(j - 1).raw.width);
                        if (gap > GAP_THRESHOLD) largeGaps++;
                    }
                    if (largeGaps >= 2)
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
                // Keine Divider: einfache Spaltenausgabe
                markdown.append("<!-- Spalte 1 -->\n");
                appendLinesWithTables(markdown, leftLines);
                markdown.append("<!-- Spalte 2 -->\n");
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
                        markdown.append("<!-- Spalte 1 -->\n");
                        appendLinesWithTables(markdown, leftSection);
                    }
                    if (!rightSection.isEmpty())
                    {
                        markdown.append("<!-- Spalte 2 -->\n");
                        appendLinesWithTables(markdown, rightSection);
                    }

                    // Divider-Block ausgeben (Cross-column Tabelle oder Heading)
                    if (bi < dividerBlocks.size())
                    {
                        markdown.append("<!-- Vollbreite -->\n");
                        appendLinesWithTables(markdown, dividerBlocks.get(bi));
                    }
                }
            }

            // Untere Fullwidth
            if (!bottomFw.isEmpty())
            {
                markdown.append("<!-- Vollbreite -->\n");
                appendLinesWithTables(markdown, bottomFw);
            }
        }
        else if (hasColumns)
        {
            markdown.append("<!-- Spalte 1 -->\n");
            leftLines.sort(Comparator.comparingDouble(l -> l.y));
            appendLinesWithTables(markdown, leftLines);
            markdown.append("<!-- Spalte 2 -->\n");
            rightLines.sort(Comparator.comparingDouble(l -> l.y));
            appendLinesWithTables(markdown, rightLines);
        }
        else
        {
            // Einspaltig
            fullWidthLines.sort(Comparator.comparingDouble(l -> l.y));
            appendLinesWithTables(markdown, fullWidthLines);
        }

        // Cleanup
        String result = markdown.toString();
        result = result.replaceAll("(?m)^.*Peter Hofmann \\(Order #\\d+\\).*\n?", "");
        result = result.replaceAll("(?m)^\\d{1,3}\\s*\n", "");
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

        boolean isHeading = !hasTableGaps && ((dominantSize > 10.5f && allBold && isShortLine)
            || (dominantSize > 15 && isShortLine));
        boolean isSubHeading = !hasTableGaps && (dominantSize > 8.5f && dominantSize <= 10.5f && allBold && isShortLine);

        if (isHeading) result.append("## ");
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
                if (!isHeading && !isSubHeading) result.append("**").append(text).append("** ");
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
                    prevLineY = line.y;
                    i++;
                    continue;
                }

                // Zeile beginnt mit Bold → vorherigen Absatz abschliessen
                boolean startsBold = md.startsWith("**");
                if (startsBold && paragraph.length() > 0)
                {
                    flushParagraph(markdown, paragraph);
                }

                // Format-Wechsel (italic ↔ nicht-italic) = Absatzgrenze
                // Nur bei rein kursiven Absaetzen (Start UND Ende mit *), nicht bei gemischten
                String prevTrimmed = paragraph.length() > 0 ? paragraph.toString().strip() : "";
                boolean prevIsItalic = prevTrimmed.startsWith("*") && !prevTrimmed.startsWith("**")
                    && prevTrimmed.endsWith("*") && !prevTrimmed.endsWith("**");
                if (paragraph.length() > 0 && isItalicLine != prevIsItalic)
                {
                    flushParagraph(markdown, paragraph);
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
            if (!text.isEmpty()) markdown.append(text).append("\n");
            paragraph.setLength(0);
        }
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
                    // Erste Zeile definiert die Referenz-Positionen
                    currentRows.add(split.row);
                    currentGapEnds.add(split.gapEnds);
                    referenceGapEnds = split.gapEnds;
                    firstIdx = i;
                    lastIdx = i;
                    tableColCount = split.row.cells.size();
                }
                else if (split.row.cells.size() == tableColCount
                         && gapPositionsAlign(split.gapEnds, referenceGapEnds, GAP_POS_TOLERANCE))
                {
                    // Gleiche Spaltenanzahl UND Gap-Positionen passen zur Referenz
                    currentRows.add(split.row);
                    currentGapEnds.add(split.gapEnds);
                    lastIdx = i;
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
                        lastIdx = i;
                    }
                    else if (split.row.cells.size() >= tableColCount - 2 && split.row.cells.size() >= 3
                             && gapPositionsAlign(split.gapEnds, referenceGapEnds, GAP_POS_TOLERANCE))
                    {
                        // Nahe Spaltenanzahl mit passenden Gap-Positionen: akzeptieren und auffuellen
                        currentRows.add(split.row);
                        currentGapEnds.add(split.gapEnds);
                        lastIdx = i;
                    }
                    else
                    {
                        if (currentRows.size() >= MIN_TABLE_ROWS && checkGapConsistency(currentGapEnds))
                        {
                            tables.add(new DetectedTable(currentRows, firstIdx, lastIdx));
                        }
                        currentRows = new ArrayList<>();
                        currentRows.add(split.row);
                        currentGapEnds = new ArrayList<>();
                        currentGapEnds.add(split.gapEnds);
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
                    SplitResult refSplit = splitLineByReference(line, referenceGapEnds);
                    if (refSplit != null && refSplit.row.cells.size() == tableColCount)
                    {
                        currentRows.add(refSplit.row);
                        currentGapEnds.add(refSplit.gapEnds);
                        lastIdx = i;
                        hadContinuation = false;
                        continue;
                    }
                }

                // Continuation-Zeile pruefen
                if (!currentRows.isEmpty() && !line.chars.isEmpty() && !hadContinuation)
                {
                    String lineText = extractText(line.chars);
                    TextLine firstLine = lines.get(firstIdx);
                    if (!firstLine.chars.isEmpty()
                        && Math.abs(line.chars.get(0).raw.x - firstLine.chars.get(0).raw.x) < 10
                        && lineText.length() < 40
                        && currentRows.get(currentRows.size() - 1).cells.get(0).length() < 80)
                    {
                        TableRow lastRow = currentRows.get(currentRows.size() - 1);
                        lastRow.cells.set(0, lastRow.cells.get(0) + " " + lineText);
                        lastIdx = i;
                        hadContinuation = true;
                        continue;
                    }
                }

                // Tabelle beenden
                if (currentRows.size() >= MIN_TABLE_ROWS && (tableColCount >= 4 || checkGapConsistency(currentGapEnds)))
                {
                    tables.add(new DetectedTable(currentRows, firstIdx, lastIdx));
                }
                currentRows = new ArrayList<>();
                currentGapEnds = new ArrayList<>();
                referenceGapEnds = null;
                firstIdx = -1;
                lastIdx = -1;
                tableColCount = 0;
                hadContinuation = false;
            }
        }

        if (currentRows.size() >= MIN_TABLE_ROWS && (tableColCount >= 4 || checkGapConsistency(currentGapEnds)))
        {
            tables.add(new DetectedTable(currentRows, firstIdx, lastIdx));
        }

        return tables;
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
        final int startLineIdx;
        final int endLineIdx;
        DetectedTable(List<TableRow> rows, int startLineIdx, int endLineIdx)
        {
            this.rows = rows;
            this.startLineIdx = startLineIdx;
            this.endLineIdx = endLineIdx;
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
