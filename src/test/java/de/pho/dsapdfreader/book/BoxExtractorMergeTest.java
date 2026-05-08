package de.pho.dsapdfreader.book;

import de.pho.dsapdfreader.book.BoxExtractor.BoxRegion;
import de.pho.dsapdfreader.markdown.RawPageData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer {@link BoxExtractor#mergeOverlappingBoxes(List)} und
 * {@link BoxExtractor#shouldMerge(BoxRegion, BoxRegion)}.
 * Hintergrund: Kaesten in DSA-Buechern bestehen oft aus zwei Rechtecken
 * (Header-Bar in einer Farbe, Body in einer zweiten) die als getrennte
 * Box-Regionen erkannt werden. Vor der Auslagerung in eigene MD-Dateien
 * muessen diese zu einer logischen Box verschmolzen werden.
 */
class BoxExtractorMergeTest {

    private static BoxRegion box(int idx, float x, float y, float w, float h) {
        return new BoxRegion(idx, x, y, w, h, new float[]{0.5f, 0.5f, 0.5f});
    }

    private static BoxRegion boxWithChar(int idx, float x, float y, float w, float h,
                                         String charText, float cx, float cy) {
        BoxRegion b = box(idx, x, y, w, h);
        RawPageData.RawChar c = new RawPageData.RawChar();
        c.text = charText; c.x = cx; c.y = cy;
        b.chars.add(c);
        return b;
    }

    @Test
    void shouldMerge_overlappingRects() {
        BoxRegion a = box(0, 100, 100, 200, 50);
        BoxRegion b = box(1, 100, 130, 200, 80);  // overlaps vertically
        assertTrue(BoxExtractor.shouldMerge(a, b));
    }

    @Test
    void shouldMerge_sameBoundsAreOverlapping() {
        // Schwertes-S32-Realfall: Header-Bar und Body teilen genau die gleichen Bounds,
        // weil zwei Rechtecke uebereinander gestapelt sind.
        BoxRegion a = box(0, 299, 190, 250, 30);
        BoxRegion b = box(1, 299, 190, 250, 120);
        assertTrue(BoxExtractor.shouldMerge(a, b));
    }

    @Test
    void shouldMerge_stackedSmallGap() {
        // Header-Bar oben, Body direkt darunter, 3pt Luecke -> Merge
        BoxRegion header = box(0, 56, 100, 250, 30);
        BoxRegion body   = box(1, 56, 133, 250, 100); // gap = 3
        assertTrue(BoxExtractor.shouldMerge(header, body));
    }

    @Test
    void shouldMerge_stackedTooFarApart() {
        BoxRegion a = box(0, 56, 100, 250, 30);
        BoxRegion b = box(1, 56, 200, 250, 100); // gap = 70 (zu gross)
        assertFalse(BoxExtractor.shouldMerge(a, b));
    }

    @Test
    void shouldMerge_disjointHorizontally() {
        BoxRegion a = box(0,  50, 100, 100, 50);
        BoxRegion b = box(1, 300, 100, 100, 50); // selbe Hoehe, weit horizontal getrennt
        assertFalse(BoxExtractor.shouldMerge(a, b));
    }

    @Test
    void shouldMerge_smallXOverlapBelowThreshold() {
        // Boxen sind vertikal benachbart, X-Ueberlappung aber nur 20pt von 200pt = 10 %
        BoxRegion a = box(0,   0, 100, 200, 30);
        BoxRegion b = box(1, 180, 132, 200, 50); // gap=2, x-overlap = 20
        assertFalse(BoxExtractor.shouldMerge(a, b));
    }

    @Test
    void mergeOverlappingBoxes_combinesPair() {
        BoxRegion header = boxWithChar(0, 56, 100, 250, 30,  "T", 60f, 110f);
        BoxRegion body   = boxWithChar(1, 56, 130, 250, 120, "B", 60f, 200f);
        List<BoxRegion> merged = BoxExtractor.mergeOverlappingBoxes(List.of(header, body));
        assertEquals(1, merged.size());
        BoxRegion m = merged.get(0);
        assertEquals(56f, m.x);
        assertEquals(100f, m.y);
        assertEquals(250f, m.width);
        assertEquals(150f, m.height); // 30 + 120, da gap=0
        assertEquals(2, m.chars.size());
    }

    @Test
    void mergeOverlappingBoxes_keepsDisjointSeparate() {
        BoxRegion a = boxWithChar(0,  56, 100, 200, 30, "A", 60f, 110f);
        BoxRegion b = boxWithChar(1, 300, 100, 200, 30, "B", 310f, 110f);
        List<BoxRegion> result = BoxExtractor.mergeOverlappingBoxes(List.of(a, b));
        assertEquals(2, result.size());
    }

    @Test
    void mergeOverlappingBoxes_isTransitive() {
        // Drei Bandstreifen: Header, Mittelteil, Footer — alle vertikal touched
        BoxRegion top    = boxWithChar(0, 56, 100, 250, 30,  "T", 60f, 110f);
        BoxRegion middle = boxWithChar(1, 56, 130, 250, 60,  "M", 60f, 150f);
        BoxRegion bottom = boxWithChar(2, 56, 190, 250, 40,  "F", 60f, 210f);
        List<BoxRegion> merged = BoxExtractor.mergeOverlappingBoxes(
                new ArrayList<>(List.of(top, middle, bottom)));
        assertEquals(1, merged.size(), "Drei gestapelte Boxen muessen zu einer verschmelzen");
        assertEquals(3, merged.get(0).chars.size());
    }

    @Test
    void mergeOverlappingBoxes_emptyAndSingleton() {
        assertTrue(BoxExtractor.mergeOverlappingBoxes(List.of()).isEmpty());
        BoxRegion solo = box(0, 0, 0, 100, 50);
        List<BoxRegion> result = BoxExtractor.mergeOverlappingBoxes(List.of(solo));
        assertEquals(1, result.size());
    }
}
