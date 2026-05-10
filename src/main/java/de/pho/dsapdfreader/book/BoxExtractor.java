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
        int idx = 0;
        if (page.rects != null) {
            for (RawPageData.RawRect r : page.rects) {
                if (!isLikelyBox(r, page.pageWidth, page.pageHeight)) continue;
                result.add(new BoxRegion(idx++, r));
            }
        }
        // Banner-Bild-Paare als Profilkasten erkennen.
        // Streng: 538x54 (±5pt) bei x=20 (±5pt), zwei Banner pro Seite (oben/
        // unten umrahmen den Profilkasten). Diese Form taucht in DSA5-Buechern
        // wie "Archiv der Daemonen" und "Archiv der Kreaturen" auf — der
        // Profilkasten hat keinen Vektor-Hintergrund, sondern nur die beiden
        // Banner-Bilder als visuelle Trennung.
        for (BoxRegion banner : identifyBannerImageBoxes(page, idx)) {
            result.add(banner);
            idx = Math.max(idx, banner.boxIndex + 1);
        }
        // Fallback: Profilkaesten in DSA-4-Style-Buechern (Grimorum, Liturgia,
        // Almanach etc.) sind mit einem grossen Hintergrund-Bild hinterlegt,
        // KEIN Banner und KEIN Vektor-Rect. Erkennung: Bild umfasst eine
        // signifikante Menge Seitentext (>100 chars und >30 % des Body-Text).
        // Vignetten (Bild mit Text drumherum) werden anhand niedriger Char-
        // Dichte ausgeschlossen.
        for (BoxRegion bg : identifyContentImageBoxes(page, idx, result)) {
            result.add(bg);
            idx = Math.max(idx, bg.boxIndex + 1);
        }
        // Quote-Kasten ueber AdornFrames-Glyph-Paare (top + bottom Ornament-Linie).
        // Wolfsfrost (230) S9 hat z. B. `q44<44r` (y=499) und `o11911p` (y=346) als
        // einzige visuelle Box-Marker — kein Vector-Rect, kein Banner-Image.
        for (BoxRegion af : identifyAdornFramePairBoxes(page, idx, result)) {
            result.add(af);
            idx = Math.max(idx, af.boxIndex + 1);
        }
        return result;
    }

    /**
     * Erkennt Quote-Kaesten, deren obere und untere Begrenzung durch
     * Ornament-Glyph-Cluster (Font-Familie {@code AdornFrames}) gesetzt sind.
     * Ein Pair besteht aus zwei Clustern auf annaehernd gleicher X-Range mit
     * vertikalem Abstand 50-300pt — Cluster-X-Mittelpunkte muessen sich um
     * weniger als 30pt unterscheiden.
     */
    private List<BoxRegion> identifyAdornFramePairBoxes(RawPageData page, int startIdx,
                                                         List<BoxRegion> existing) {
        List<BoxRegion> result = new ArrayList<>();
        if (page.chars == null || page.chars.isEmpty()) return result;
        // AdornFrames-Chars sammeln und nach Y in Cluster gruppieren (Toleranz 3pt).
        List<RawPageData.RawChar> ornChars = new ArrayList<>();
        for (RawPageData.RawChar c : page.chars) {
            if (c.fontName != null && c.fontName.contains("AdornFrames")) {
                ornChars.add(c);
            }
        }
        if (ornChars.size() < 4) return result;
        ornChars.sort(java.util.Comparator.comparingDouble(c -> c.y));
        List<List<RawPageData.RawChar>> clusters = new ArrayList<>();
        List<RawPageData.RawChar> current = new ArrayList<>();
        float currentY = Float.NaN;
        for (RawPageData.RawChar c : ornChars) {
            if (current.isEmpty() || Math.abs(c.y - currentY) < 3f) {
                current.add(c);
                currentY = c.y;
            } else {
                clusters.add(current);
                current = new ArrayList<>();
                current.add(c);
                currentY = c.y;
            }
        }
        if (!current.isEmpty()) clusters.add(current);
        if (clusters.size() < 2) return result;
        // Cluster-Bounds berechnen
        record Cluster(float xMin, float xMax, float y, float xCenter) {}
        List<Cluster> cls = new ArrayList<>();
        for (List<RawPageData.RawChar> grp : clusters) {
            if (grp.size() < 2) continue;
            float xMin = Float.MAX_VALUE, xMax = -Float.MAX_VALUE, ySum = 0;
            for (RawPageData.RawChar c : grp) {
                xMin = Math.min(xMin, c.x);
                xMax = Math.max(xMax, c.x + c.width);
                ySum += c.y;
            }
            cls.add(new Cluster(xMin, xMax, ySum / grp.size(), (xMin + xMax) / 2f));
        }
        if (cls.size() < 2) return result;
        boolean[] used = new boolean[cls.size()];
        int idx = startIdx;
        // Pair-Suche: bottom (kleines y) + top (grosses y) mit y-Diff 50-300pt.
        for (int i = 0; i < cls.size(); i++) {
            if (used[i]) continue;
            for (int j = i + 1; j < cls.size(); j++) {
                if (used[j]) continue;
                Cluster a = cls.get(i), b = cls.get(j);
                Cluster bottom = a.y < b.y ? a : b;
                Cluster top = a.y < b.y ? b : a;
                float yGap = top.y - bottom.y;
                if (yGap < 50f || yGap > 300f) continue;
                if (Math.abs(top.xCenter - bottom.xCenter) > 30f) continue;
                float boxX = Math.min(bottom.xMin, top.xMin) - 5f;
                float boxRight = Math.max(bottom.xMax, top.xMax) + 5f;
                // Marker-Glyph ist sehr gross (typ. 50pt), visuelle Bottom-Linie
                // liegt ~25pt unter der Baseline und kann eine Folge-Zeile ueber-
                // decken. Box am Bottom entsprechend grosszuegig erweitern.
                float boxY = bottom.y - 25f;
                float boxH = (top.y - bottom.y) + 35f;
                float boxW = boxRight - boxX;
                // Doppel-Detection mit bestehender Box vermeiden.
                boolean overlap = false;
                for (BoxRegion ex : existing) {
                    float ix1 = Math.max(boxX, ex.x), iy1 = Math.max(boxY, ex.y);
                    float ix2 = Math.min(boxX + boxW, ex.x + ex.width);
                    float iy2 = Math.min(boxY + boxH, ex.y + ex.height);
                    if (ix2 <= ix1 || iy2 <= iy1) continue;
                    float interArea = (ix2 - ix1) * (iy2 - iy1);
                    if (interArea > 0.30f * boxW * boxH) { overlap = true; break; }
                }
                if (overlap) continue;
                used[i] = true; used[j] = true;
                result.add(new BoxRegion(idx++, boxX, boxY, boxW, boxH,
                        new float[]{0.95f, 0.92f, 0.82f}));
                break;
            }
        }
        return result;
    }

    /**
     * Erkennt Profilkaesten ueber ein Paar identischer schmaler Banner-Bilder
     * (oben und unten). Die Heuristik ist absichtlich eng gefasst, um nur
     * echte Profil-Banner zu treffen (keine Illustrationen).
     */
    private List<BoxRegion> identifyBannerImageBoxes(RawPageData page, int startIdx) {
        List<BoxRegion> result = new ArrayList<>();
        if (page.images == null || page.images.isEmpty()) return result;
        List<RawPageData.RawImage> banners = new ArrayList<>();
        for (RawPageData.RawImage img : page.images) {
            float w = img.width;
            float h = img.height;
            float x = img.x;
            // Strenge Kriterien — nur die typischen DSA5-Profil-Banner.
            if (w < 530f || w > 545f) continue;
            if (h < 50f || h > 58f) continue;
            if (x < 10f || x > 30f) continue;
            banners.add(img);
        }
        if (banners.size() < 2) return result;
        banners.sort(java.util.Comparator.comparingDouble(i -> i.y));
        boolean[] used = new boolean[banners.size()];
        int idx = startIdx;
        for (int i = 0; i < banners.size(); i++) {
            if (used[i]) continue;
            RawPageData.RawImage top = banners.get(i);
            // Top-Banner muss am Seitenanfang sein (y < 80pt).
            if (top.y > 80f) continue;
            for (int j = i + 1; j < banners.size(); j++) {
                if (used[j]) continue;
                RawPageData.RawImage bot = banners.get(j);
                // Banner-Paar: gleicher x-Bereich, vernuenftiger y-Abstand.
                if (Math.abs(bot.x - top.x) > 3f) continue;
                if (Math.abs(bot.width - top.width) > 3f) continue;
                float yGap = bot.y - (top.y + top.height);
                if (yGap < 80f || yGap > 700f) continue;
                used[i] = true; used[j] = true;
                float boxX = top.x;
                float boxY = top.y + top.height;            // unter dem Top-Banner
                float boxW = top.width;
                float boxH = bot.y - (top.y + top.height);  // bis zum Bottom-Banner
                result.add(new BoxRegion(idx++, boxX, boxY, boxW, boxH,
                        new float[]{0.95f, 0.95f, 0.85f}));
                break;
            }
        }
        return result;
    }

    /**
     * Profilkasten-Detection ueber Hintergrund-Bilder mit substanziellem
     * Text-Inhalt. Trifft Buecher wie Grimorum Cantiones / Divinarium Liturgia
     * (524x737-Format), wo jede Spell-/Liturgie-Profilseite mit einer
     * Pergament-/Buch-Grafik hinterlegt ist.
     *
     * <p>Heuristik:
     * <ul>
     *   <li>Bild ist kein Voll-Seiten-Hintergrund (w &lt; 95 % oder h &lt; 95 %
     *       der Seitenflaeche, abzueglich Off-Page-Bereiche).</li>
     *   <li>Bild ist groesser als typische Vignetten (w &gt;= 200, h &gt;= 200).</li>
     *   <li>Mehr als 100 nicht-Whitespace-Chars haben ihren Mittelpunkt im Bild
     *       UND mind. 30 % des Body-Texts liegt im Bild
     *       — schliesst Vignetten aus, um die Text fliesst.</li>
     *   <li>Char-Dichte mind. 3 / 1000 pt² (zusaetzliche Schutzschicht gegen
     *       sehr sparsam ueberlappende Vignetten).</li>
     *   <li>Keine substanzielle Ueberlappung mit bereits erkannten Boxen
     *       (vermeidet Doppel-Detection bei Buechern mit Banner-Pair).</li>
     * </ul>
     */
    private List<BoxRegion> identifyContentImageBoxes(RawPageData page, int startIdx,
                                                       List<BoxRegion> existing) {
        List<BoxRegion> result = new ArrayList<>();
        if (page.images == null || page.images.isEmpty()) return result;
        if (page.chars == null || page.chars.isEmpty()) return result;
        float pw = page.pageWidth, ph = page.pageHeight;
        if (pw <= 0 || ph <= 0) return result;
        int totalNonBlank = 0;
        for (RawPageData.RawChar c : page.chars) {
            if (c.text != null && !c.text.isBlank()) totalNonBlank++;
        }
        if (totalNonBlank < 50) return result; // leere/Bildseiten ueberspringen
        int idx = startIdx;
        for (RawPageData.RawImage img : page.images) {
            float w = img.width, h = img.height;
            float x = img.x, y = img.y;
            if (w < 200f || h < 200f) continue;
            // Off-page Bilder skippen
            if (x < -10f || y < -10f) continue;
            if (x + w > pw + 10f || y + h > ph + 10f) continue;
            // Voll-Seiten-Hintergrund skippen
            if (w >= pw * 0.95f && h >= ph * 0.95f) continue;
            int charsInside = 0;
            for (RawPageData.RawChar c : page.chars) {
                if (c.text == null || c.text.isBlank()) continue;
                float cx = c.x + c.width / 2f;
                float cy = c.y + c.height / 2f;
                if (cx >= x && cx <= x + w && cy >= y && cy <= y + h) charsInside++;
            }
            if (charsInside < 100) continue;
            float ratio = (float) charsInside / totalNonBlank;
            if (ratio < 0.30f) continue;
            float density = (float) charsInside / (w * h) * 1000f;
            if (density < 3f) continue;
            // Ueberlappt eine bestehende Box mehr als 30 %? Skip.
            boolean overlap = false;
            float ax2 = x + w, ay2 = y + h;
            for (BoxRegion ex : existing) {
                float ix1 = Math.max(x, ex.x), iy1 = Math.max(y, ex.y);
                float ix2 = Math.min(ax2, ex.x + ex.width);
                float iy2 = Math.min(ay2, ex.y + ex.height);
                if (ix2 <= ix1 || iy2 <= iy1) continue;
                float interArea = (ix2 - ix1) * (iy2 - iy1);
                if (interArea > 0.30f * w * h) { overlap = true; break; }
            }
            if (overlap) continue;
            result.add(new BoxRegion(idx++, x, y, w, h,
                    new float[]{0.95f, 0.92f, 0.82f}));
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
                // AdornFrames-Marker (Box-Top/Bottom-Ornament) komplett verwerfen —
                // sie wurden bereits zur Box-Detection ausgewertet und sind reine
                // Dekoration. Wuerden sie in die Sub-Page gereicht, erkennt der
                // innere BoxExtractor sie erneut als Pair → endlose Rekursion.
                if (c.fontName != null && c.fontName.contains("AdornFrames")) continue;
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
