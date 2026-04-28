package de.pho.dsapdfreader.book.analysis.structured;

import de.pho.dsapdfreader.book.analysis.BookFontClusters;
import de.pho.dsapdfreader.book.analysis.FontStyleKey;
import de.pho.dsapdfreader.markdown.RawPageData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Erzeugt {@link StructuredLine}-Listen pro Seite mit korrekter Spalten- und
 * Lesereihenfolge.
 * <p>
 * Pipeline pro Seite:
 * <ol>
 *   <li>Linien grob nach y gruppieren (y-Toleranz)</li>
 *   <li>In jeder y-Gruppe: an grossen x-Luecken trennen → Sublines (verhindert
 *       das Mischen zweier Spalten am gleichen y)</li>
 *   <li>Pro Subline: Spalten-Klassifikation (1=links, 2=rechts, 0=Vollbreite)
 *       relativ zur Seiten-Mitte</li>
 *   <li>Reading-Order linearisieren: Vollbreite wirkt als Band-Trenner; in
 *       jedem Band kommt erst die linke, dann die rechte Spalte (User-Regel:
 *       oben1 → ... → obenN → Heading → unten1 → ... → untenN)</li>
 *   <li>Pro Subline: dominantes Cluster → Tier; Type-Marker am Zeilenende
 *       extrahieren</li>
 * </ol>
 * <p>
 * <b>Boxen (Beispiel-Kaesten, Tabellen) werden noch nicht ausgespart</b> —
 * sie laufen als Teil des linearen Stroms mit. TODO: Box-Extractor
 * vorschalten.
 */
public class StructuredPageBuilder {

    /** y-Toleranz: Chars innerhalb dieses Abstands gehoeren zur selben Zeile. */
    private static final float Y_TOLERANCE = 3.0f;

    /**
     * x-Luecke, ab der eine Zeile gesplittet wird (Spalten-Gap im PDF).
     * Body-Char-Breiten sind ~5-7pt; eine Luecke ueber 12pt ist deutlich
     * groesser als ein normaler Wortabstand.
     */
    private static final float COLUMN_GAP = 12.0f;

    /** Eine Subline gilt als Vollbreite, wenn ihre Spanne diesen Anteil der Seitenbreite uebersteigt. */
    private static final float FULL_WIDTH_RATIO = 0.55f;

    /** Type-Marker am Zeilenende: "(passiv)", "(aktiv)", ... */
    private static final Pattern P_TYPE_MARKER = Pattern.compile(
            "\\(([a-zA-ZäöüÄÖÜ][a-zA-ZäöüÄÖÜ\\s]{1,30})\\)\\s*$");

    public List<StructuredLine> build(RawPageData page, BookFontClusters clusters) {
        if (page.chars == null || page.chars.isEmpty()) return new ArrayList<>();

        // 1+2: y-Gruppen, dann an x-Luecken splitten → Sublines
        List<List<RawPageData.RawChar>> sublines = buildSublines(page.chars);

        // 3: StructuredLine pro Subline
        List<StructuredLine> built = new ArrayList<>();
        for (List<RawPageData.RawChar> sub : sublines) {
            StructuredLine sl = buildOne(sub, page, clusters);
            if (sl != null) built.add(sl);
        }

        // 4: Reading-Order
        return reorderByReadingFlow(built);
    }

    /**
     * Erzeugt Sublines: Chars in y-Toleranz-Gruppen, jede Gruppe an grossen
     * x-Luecken weiter aufgeteilt. Damit landen zwei nebeneinanderstehende
     * Spalten am gleichen y in zwei separaten Sublines.
     */
    private static List<List<RawPageData.RawChar>> buildSublines(List<RawPageData.RawChar> chars) {
        List<RawPageData.RawChar> sorted = new ArrayList<>(chars);
        sorted.sort(Comparator
                .comparingDouble((RawPageData.RawChar c) -> c.y)
                .thenComparingDouble(c -> c.x));

        // y-Gruppe
        List<List<RawPageData.RawChar>> yGroups = new ArrayList<>();
        List<RawPageData.RawChar> currentY = null;
        float currentYVal = Float.NEGATIVE_INFINITY;
        for (RawPageData.RawChar c : sorted) {
            if (c.text == null || c.text.isBlank()) continue;
            if (currentY == null || Math.abs(c.y - currentYVal) > Y_TOLERANCE) {
                currentY = new ArrayList<>();
                yGroups.add(currentY);
                currentYVal = c.y;
            }
            currentY.add(c);
        }

        // Pro y-Gruppe: an x-Luecken splitten
        List<List<RawPageData.RawChar>> sublines = new ArrayList<>();
        for (List<RawPageData.RawChar> group : yGroups) {
            group.sort(Comparator.comparingDouble(c -> c.x));
            List<RawPageData.RawChar> currentSub = new ArrayList<>();
            for (int i = 0; i < group.size(); i++) {
                RawPageData.RawChar c = group.get(i);
                if (i > 0) {
                    RawPageData.RawChar prev = group.get(i - 1);
                    float gap = c.x - (prev.x + prev.width);
                    if (gap > COLUMN_GAP) {
                        sublines.add(currentSub);
                        currentSub = new ArrayList<>();
                    }
                }
                currentSub.add(c);
            }
            if (!currentSub.isEmpty()) sublines.add(currentSub);
        }
        return sublines;
    }

    private StructuredLine buildOne(List<RawPageData.RawChar> chars, RawPageData page,
                                    BookFontClusters clusters) {
        if (chars.isEmpty()) return null;

        StructuredLine line = new StructuredLine();
        line.page = page.pageNumber;
        line.y = chars.get(0).y;
        line.xStart = chars.get(0).x;
        line.xEnd = chars.stream()
                .map(c -> c.x + c.width).max(Float::compare).orElse(line.xStart);
        line.text = buildText(chars);

        line.columnIndex = detectColumn(line.xStart, line.xEnd, page.pageWidth);
        FontStyleKey dom = dominantCluster(chars, clusters);
        line.style = dom;
        line.tier = clusters.tierFor(dom);
        line.typeMarker = extractTypeMarker(line.text);
        return line;
    }

    private static String buildText(List<RawPageData.RawChar> chars) {
        StringBuilder sb = new StringBuilder();
        float lastEnd = -1;
        float lastFontSize = 0;
        for (RawPageData.RawChar c : chars) {
            if (c.text == null) continue;
            // Wortgrenze: Luecke zwischen Zeichen ist deutlich (>0.15em der vorigen Schrift).
            // PDFBox liefert oft c.width inkl. Halb-Spaceing, daher zaehlt der Abstand
            // vom Ende des vorigen Glyphen zum Start des aktuellen.
            if (lastEnd >= 0) {
                float gap = c.x - lastEnd;
                float threshold = Math.max(0.5f, lastFontSize * 0.15f);
                if (gap > threshold && sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
                    sb.append(' ');
                }
            }
            sb.append(c.text);
            lastEnd = c.x + c.width;
            lastFontSize = c.fontSize;
        }
        return sb.toString().trim();
    }

    private static int detectColumn(float xStart, float xEnd, float pageWidth) {
        float span = xEnd - xStart;
        if (span > pageWidth * FULL_WIDTH_RATIO) return 0; // Vollbreite
        float pageMid = pageWidth / 2f;
        float center = (xStart + xEnd) / 2f;
        return center < pageMid ? 1 : 2;
    }

    /**
     * Liefert das "fuehrende" Cluster der Zeile.
     * <p>
     * Nicht einfach der haeufigste Cluster, sondern der <b>prominenteste</b>
     * (= kleinster Tier-Index, also naechster zur "Heading-Spitze") der einen
     * signifikanten Anteil der Zeichen ausmacht. Damit wird ein Heading wie
     * "Auflaufen (Spezialmanöver)" als Heading erkannt, auch wenn der nicht-bold
     * Klammer-Suffix mehr Zeichen hat als das bold-gesetzte Auflaufen.
     * <p>
     * Mindestschwelle: 3 Zeichen ODER 15 % der Zeile, je nachdem was groesser
     * ist. Die absolute Untergrenze 3 ist wichtig fuer kurze Heading-Namen wie
     * "Wurf" (4 Zeichen) gefolgt von "(Spezialmanöver)" (16 Zeichen) — hier
     * waere 25 % zu streng.
     */
    private static FontStyleKey dominantCluster(List<RawPageData.RawChar> chars,
                                                BookFontClusters clusters) {
        Map<FontStyleKey, Long> counts = new HashMap<>();
        long total = 0;
        for (RawPageData.RawChar c : chars) {
            if (c.text == null || c.text.isBlank()) continue;
            FontStyleKey k = FontStyleKey.of(c.fontName, c.fontSize, c.bold, c.italic);
            counts.merge(k, 1L, Long::sum);
            total++;
        }
        if (counts.isEmpty()) return null;

        long minSig = Math.max(3, total * 15 / 100);

        FontStyleKey bestProminent = null;
        int bestTier = Integer.MAX_VALUE;
        for (Map.Entry<FontStyleKey, Long> e : counts.entrySet()) {
            if (e.getValue() < minSig) continue;
            int tier = clusters.tierFor(e.getKey());
            if (tier < 0) continue;
            if (tier > 0 && tier < bestTier) {
                bestTier = tier;
                bestProminent = e.getKey();
            }
        }
        if (bestProminent != null) return bestProminent;

        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static String extractTypeMarker(String text) {
        if (text == null) return null;
        Matcher m = P_TYPE_MARKER.matcher(text);
        if (m.find()) return m.group(1).trim();
        return null;
    }

    /**
     * Linearisiert Sublines gemaess Lesereihenfolge: Vollbreite-Linien wirken
     * als Band-Trenner; in jedem Band kommt erst die linke Spalte
     * (top-to-bottom), dann die rechte. Sublines sind y-aufsteigend sortiert.
     */
    private static List<StructuredLine> reorderByReadingFlow(List<StructuredLine> lines) {
        // Sublines sind nicht zwingend sortiert (kommen aus y-Gruppen mit innerem x-Split,
        // verschiedene Splits derselben y-Gruppe haben gleiche y aber unterschiedliche col).
        // Sortieren nach y, bei Gleichstand col 0 vor 1 vor 2 (Header zuerst).
        List<StructuredLine> sorted = new ArrayList<>(lines);
        sorted.sort(Comparator
                .comparingDouble((StructuredLine l) -> l.y)
                .thenComparingInt(l -> l.columnIndex));

        List<StructuredLine> result = new ArrayList<>(sorted.size());
        List<StructuredLine> leftPending = new ArrayList<>();
        List<StructuredLine> rightPending = new ArrayList<>();
        for (StructuredLine l : sorted) {
            if (l.columnIndex == 0) {
                // Vollbreite — vorherige Spalten flushen, dann diese Linie anhaengen
                result.addAll(leftPending);
                result.addAll(rightPending);
                leftPending.clear();
                rightPending.clear();
                result.add(l);
            } else if (l.columnIndex == 1) {
                leftPending.add(l);
            } else {
                rightPending.add(l);
            }
        }
        result.addAll(leftPending);
        result.addAll(rightPending);
        return result;
    }
}
