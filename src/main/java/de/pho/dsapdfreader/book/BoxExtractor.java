package de.pho.dsapdfreader.book;

import de.pho.dsapdfreader.markdown.RawPageData;

import java.util.ArrayList;
import java.util.List;

/**
 * Erkennt Boxen (gefuellte Rechtecke mit Hintergrundfarbe) auf einer Page und
 * splittet die Chars in (a) Hauptlauftext-Chars und (b) Box-Chars pro Box.
 *
 * <p>Eine Box ist eine RawRect mit:
 * <ul>
 *   <li>nicht-null fillColor (transparente werden ausgeschlossen)</li>
 *   <li>opacity > 0.05 (sichtbar)</li>
 *   <li>plausible Groesse: width &gt;= 100, height &gt;= 30</li>
 *   <li>nicht volle Seitenbreite: width &lt; 92 % der Seitenbreite</li>
 *   <li>nicht volle Seitenhoehe: height &lt; 92 % der Seitenhoehe</li>
 *   <li>Aspect Ratio nicht extrem (zwischen 0.05 und 20)</li>
 * </ul>
 *
 * <p>Nach dem Split werden Box-Regionen, die sich ueberlappen oder dicht
 * untereinander stehen (typisch fuer Header-Bar + Body bei zweifarbigen
 * Kaesten), zu einer logischen Box zusammengefuehrt — sonst landen Heading
 * und Body in getrennten Dateien.
 */
public class BoxExtractor {

    /** Maximaler vertikaler Abstand zwischen Header-Bar und Body, der noch als "ein Kasten" gilt. */
    static final float MAX_MERGE_Y_GAP = 5f;
    /** Mindest-Anteil der X-Ueberlappung an der schmaleren Box, damit zwei Boxen als gestapelt gelten. */
    static final float MIN_MERGE_X_OVERLAP_RATIO = 0.8f;

    /** Eine erkannte Box mit Bounds und zugehoerigen Chars. */
    public static class BoxRegion {
        public final int boxIndex;
        public final float x, y, width, height;
        public final float[] fillColor;
        public final List<RawPageData.RawChar> chars = new ArrayList<>();

        BoxRegion(int boxIndex, RawPageData.RawRect r) {
            this.boxIndex = boxIndex;
            this.x = r.x;
            this.y = r.y;
            this.width = r.width;
            this.height = r.height;
            this.fillColor = r.fillColor;
        }

        public BoxRegion(int boxIndex, float x, float y, float width, float height, float[] fillColor) {
            this.boxIndex = boxIndex;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.fillColor = fillColor;
        }

        public boolean contains(RawPageData.RawChar c) {
            return c.x >= x && c.x <= x + width
                    && c.y >= y && c.y <= y + height;
        }
    }

    /** Ergebnis: Hauptlauftext-Page (ohne Box-Chars) + erkannte Boxen mit ihren Chars. */
    public static class SplitResult {
        public final RawPageData mainPage;
        public final List<BoxRegion> boxes;

        public SplitResult(RawPageData mainPage, List<BoxRegion> boxes) {
            this.mainPage = mainPage;
            this.boxes = boxes;
        }
    }

    /** Filtert RawRects auf wahrscheinliche Boxen. */
    public List<BoxRegion> identifyBoxes(RawPageData page) {
        List<BoxRegion> result = new ArrayList<>();
        if (page.rects == null) return result;
        int idx = 0;
        for (RawPageData.RawRect r : page.rects) {
            if (!isLikelyBox(r, page.pageWidth, page.pageHeight)) continue;
            result.add(new BoxRegion(idx++, r));
        }
        return result;
    }

    /** Splittet die Page in Hauptlauftext-Chars und Box-Chars. */
    public SplitResult split(RawPageData page) {
        List<BoxRegion> boxes = identifyBoxes(page);
        List<RawPageData.RawChar> mainChars = new ArrayList<>();

        if (boxes.isEmpty()) {
            return new SplitResult(page, boxes);
        }

        if (page.chars != null) {
            for (RawPageData.RawChar c : page.chars) {
                BoxRegion containing = null;
                for (BoxRegion b : boxes) {
                    if (b.contains(c)) {
                        // bei mehreren ueberlappenden Boxen: die kleinste (innerste) waehlen
                        if (containing == null
                                || b.width * b.height < containing.width * containing.height) {
                            containing = b;
                        }
                    }
                }
                if (containing == null) {
                    mainChars.add(c);
                } else {
                    containing.chars.add(c);
                }
            }
        }

        // Header-Bar + Body zu einem logischen Kasten zusammenfuehren.
        boxes = mergeOverlappingBoxes(boxes);

        // Boxen ohne Chars verwerfen (waren wahrscheinlich Layout-Rechtecke ohne Text)
        boxes.removeIf(b -> b.chars.size() < 5);

        RawPageData mainPage = new RawPageData();
        mainPage.pageNumber = page.pageNumber;
        mainPage.pageWidth = page.pageWidth;
        mainPage.pageHeight = page.pageHeight;
        mainPage.chars = mainChars;
        mainPage.images = page.images;
        // rects entfernen, da wir sie schon verarbeitet haben
        mainPage.rects = new ArrayList<>();

        return new SplitResult(mainPage, boxes);
    }

    /**
     * Fuehrt Box-Regionen, die sich ueberlappen oder dicht uebereinander stehen,
     * iterativ zu einer Box zusammen. Bricht ab, sobald ein Durchlauf keine
     * weitere Verschmelzung findet.
     */
    static List<BoxRegion> mergeOverlappingBoxes(List<BoxRegion> input) {
        List<BoxRegion> boxes = new ArrayList<>(input);
        boolean changed = true;
        while (changed) {
            changed = false;
            outer:
            for (int i = 0; i < boxes.size(); i++) {
                for (int j = i + 1; j < boxes.size(); j++) {
                    if (shouldMerge(boxes.get(i), boxes.get(j))) {
                        BoxRegion merged = mergePair(boxes.get(i), boxes.get(j));
                        // groessere Box-Indizes zuerst entfernen, um Indexverschiebungen zu vermeiden
                        boxes.remove(j);
                        boxes.remove(i);
                        boxes.add(merged);
                        changed = true;
                        break outer;
                    }
                }
            }
        }
        return boxes;
    }

    static boolean shouldMerge(BoxRegion a, BoxRegion b) {
        // Echte Ueberlappung: rechteckige Bounds schneiden sich.
        boolean overlaps = a.x < b.x + b.width && b.x < a.x + a.width
                && a.y < b.y + b.height && b.y < a.y + a.height;
        if (overlaps) return true;

        // Gestapelt mit kleinem vertikalen Abstand und ausreichender X-Ueberlappung.
        BoxRegion upper = a.y <= b.y ? a : b;
        BoxRegion lower = a.y <= b.y ? b : a;
        float yGap = lower.y - (upper.y + upper.height);
        if (yGap < 0 || yGap > MAX_MERGE_Y_GAP) return false;
        float xOverlap = Math.min(a.x + a.width, b.x + b.width) - Math.max(a.x, b.x);
        float minWidth = Math.min(a.width, b.width);
        return minWidth > 0 && xOverlap >= MIN_MERGE_X_OVERLAP_RATIO * minWidth;
    }

    private static BoxRegion mergePair(BoxRegion a, BoxRegion b) {
        float x = Math.min(a.x, b.x);
        float y = Math.min(a.y, b.y);
        float maxRight = Math.max(a.x + a.width, b.x + b.width);
        float maxBottom = Math.max(a.y + a.height, b.y + b.height);
        // groessere Box dominiert die fillColor — als Hint fuer "Body-Farbe"
        float[] fill = (a.width * a.height >= b.width * b.height) ? a.fillColor : b.fillColor;
        BoxRegion merged = new BoxRegion(Math.min(a.boxIndex, b.boxIndex),
                x, y, maxRight - x, maxBottom - y, fill);
        merged.chars.addAll(a.chars);
        merged.chars.addAll(b.chars);
        return merged;
    }

    private boolean isLikelyBox(RawPageData.RawRect r, float pageWidth, float pageHeight) {
        if (r.fillColor == null) return false;
        if (r.opacity < 0.05f) return false;
        if (r.width < 100 || r.height < 30) return false;
        if (r.width > pageWidth * 0.92f) return false;
        if (r.height > pageHeight * 0.92f) return false;
        float ratio = r.width / r.height;
        if (ratio < 0.05f || ratio > 20f) return false;
        // sehr helle Hintergruende (Weiss-aehnlich) duerfen draus, sind oft Page-Background
        if (isNearWhite(r.fillColor)) return false;
        return true;
    }

    private boolean isNearWhite(float[] rgb) {
        if (rgb.length < 3) return false;
        return rgb[0] > 0.95f && rgb[1] > 0.95f && rgb[2] > 0.95f;
    }
}
