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
 */
public class BoxExtractor {

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
