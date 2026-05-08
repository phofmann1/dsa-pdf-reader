package de.pho.dsapdfreader.markdown;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.pho.dsapdfreader.markdown.RawPageData.RawImage;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer die Listen-Erkennung in Phase 3:
 * <ul>
 *   <li>{@link TextInterpreter#findSmallIconImages(RawPageData)}
 *   <li>{@link TextInterpreter#clusterIconsByXColumn(List, float)}
 *   <li>{@link TextInterpreter#computeBulletAnchorRanges(RawPageData)}
 *   <li>Discrimination: Bullet-Cluster werden NICHT als Eye-Glyphs erkannt
 *   <li>Integration: Schwertes S32 rendert die zwei Listen als {@code - ...}
 * </ul>
 */
class TextInterpreterListsTest {

    private static RawImage img(float x, float y, float w, float h, String origin) {
        RawImage im = new RawImage();
        im.x = x; im.y = y; im.width = w; im.height = h;
        im.origin = origin;
        return im;
    }

    private static RawPageData pageWithImages(RawImage... imgs) {
        RawPageData p = new RawPageData(1, 595f, 842f, List.of());
        p.images = new ArrayList<>(List.of(imgs));
        return p;
    }

    @Test
    void findSmallIconImages_filtersOnSize() {
        RawPageData p = pageWithImages(
                img(60, 90, 10.7f, 5.8f, "p11_img35.png"),         // ok
                img(60, 100, 50, 50, "x.png"),                      // zu gross
                img(60, 100, 1, 1, "x.png"),                        // zu klein
                img(-5, 100, 10, 5, "x.png"),                       // off-screen
                img(60, 1000, 10, 5, "x.png"));                     // off-screen y
        List<RawImage> got = TextInterpreter.findSmallIconImages(p);
        assertEquals(1, got.size());
    }

    @Test
    void clusterIconsByXColumn_groupsBySharedX() {
        List<RawImage> imgs = List.of(
                img(60, 90, 10, 6, "e"),
                img(62, 114, 10, 6, "e"),
                img(302, 690, 10, 6, "e"),
                img(305, 726, 10, 6, "e"));
        var clusters = TextInterpreter.clusterIconsByXColumn(imgs, 5f);
        assertEquals(2, clusters.size());
        assertEquals(2, clusters.get(0).size());
        assertEquals(2, clusters.get(1).size());
    }

    @Test
    void clusterIconsByXColumn_singletonsAreSeparate() {
        var imgs = List.of(
                img(60, 90, 10, 6, "e"),
                img(200, 300, 10, 6, "e"),
                img(400, 500, 10, 6, "e"));
        var clusters = TextInterpreter.clusterIconsByXColumn(imgs, 5f);
        assertEquals(3, clusters.size());
    }

    @Test
    void computeBulletAnchorRanges_clusterOfTwoYieldsTwoAnchors() {
        // Zwei Bullets in derselben x-Spalte mit y-Abstand 24 pt → gueltige Liste.
        RawPageData p = pageWithImages(
                img(60, 90,  10.7f, 5.8f, "p11_img35.png"),
                img(60, 114, 10.7f, 5.8f, "p11_img35.png"));
        List<float[]> anchors = TextInterpreter.computeBulletAnchorRanges(p);
        assertEquals(2, anchors.size());
        // x-Range: [bullet.x - 8, bullet.x + w + 20] = [52, 90.7]
        assertEquals(52f, anchors.get(0)[0], 0.5f);
        assertTrue(anchors.get(0)[1] >= 90f);
        // y-Range deckt Bullet-Position
        assertTrue(anchors.get(0)[2] <= 90f && anchors.get(0)[3] >= 90f);
    }

    @Test
    void computeBulletAnchorRanges_singletonGivesNoAnchors() {
        RawPageData p = pageWithImages(img(60, 90, 10.7f, 5.8f, "p11_img35.png"));
        assertTrue(TextInterpreter.computeBulletAnchorRanges(p).isEmpty());
    }

    @Test
    void computeBulletAnchorRanges_clusterTooDenseRejected() {
        // Zwei Bullets nur 4 pt vertikal entfernt — kein plausibler Listenabstand.
        RawPageData p = pageWithImages(
                img(60, 100, 10.7f, 5.8f, "x.png"),
                img(60, 104, 10.7f, 5.8f, "x.png"));
        assertTrue(TextInterpreter.computeBulletAnchorRanges(p).isEmpty());
    }

    @Test
    void computeBulletAnchorRanges_clusterAcceptedDespiteLargeSpacing() {
        // Listen-Items mit langen Texten (Schwertes S17-Variante: Bullets Y=114, 234)
        // haben konsekutiv >100pt Abstand. Solche Cluster bleiben gueltig — wir
        // verlassen uns hier auf x-Spalten-Konsistenz, nicht auf y-Maxabstand.
        RawPageData p = pageWithImages(
                img(60, 100, 10.7f, 5.8f, "x.png"),
                img(60, 350, 10.7f, 5.8f, "x.png"));
        assertEquals(2, TextInterpreter.computeBulletAnchorRanges(p).size(),
                "Konsekutive Bullets in gleicher x-Spalte bleiben Cluster, auch bei groesserem y-Abstand");
    }

    @Test
    void findEyeGlyphCandidates_excludesClusterMembers() {
        // Cluster (= Listen-Bullets) duerfen NICHT als Eye-Glyph zaehlen.
        RawPageData p = pageWithImages(
                img(60, 90,  10.7f, 5.8f, "p11_img35.png"),
                img(60, 114, 10.7f, 5.8f, "p11_img35.png"));
        List<RawImage> eyes = TextInterpreter.findEyeGlyphCandidates(p);
        assertTrue(eyes.isEmpty(), "Cluster-Mitglieder sind keine Eyes");
    }

    @Test
    void findEyeGlyphCandidates_singletonWithOriginIsEye() {
        RawPageData p = pageWithImages(
                img(60, 90, 10.7f, 5.8f, "p11_img35.png"));
        List<RawImage> eyes = TextInterpreter.findEyeGlyphCandidates(p);
        assertEquals(1, eyes.size());
    }

    @Test
    void findEyeGlyphCandidates_singletonWithoutOriginRejected() {
        RawPageData p = pageWithImages(
                img(60, 90, 10.7f, 5.8f, null));
        assertTrue(TextInterpreter.findEyeGlyphCandidates(p).isEmpty());
    }

    @Test
    void integrationS32_rendersTwoListsAsBullets() throws Exception {
        Path pageJson = Path.of(
                "export/markdown/raw/01 - Regeln/"
                        + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln/page_032.json");
        Assumptions.assumeTrue(Files.exists(pageJson),
                "Integrationstest uebersprungen: " + pageJson + " nicht vorhanden");

        RawPageData page = new ObjectMapper().readValue(pageJson.toFile(), RawPageData.class);
        TextInterpreter interpreter = new TextInterpreter();
        interpreter.setEmitBoxesInline(false);
        String md = interpreter.interpretPage(page);

        // Erstes Listenelement: "- Für den Einsatz von Talenten gelten alle Regeln, die..."
        assertTrue(md.contains("- Für den Einsatz von Talenten"),
                "Erstes List-Item der ersten Liste fehlt");
        assertTrue(md.contains("- Selbst wenn ein Held"),
                "Zweites List-Item der ersten Liste fehlt");
        // Zweite Liste in der rechten Spalte (Routineprobe-Bedingungen):
        assertTrue(md.contains("- Der Held muss in jeder Eigenschaft"),
                "Erstes List-Item der zweiten Liste fehlt");
        assertTrue(md.contains("- Zusätzlich muss er mindestens"),
                "Zweites List-Item der zweiten Liste fehlt");
    }

    @Test
    void integrationS32_doesNotMisdetectRightColumnLines() throws Exception {
        // Defensiv: Die Bullet-Anker auf der linken Spalte duerfen NICHT
        // Body-Zeilen in der rechten Spalte fuer dieselbe y zu Listen-Items machen.
        Path pageJson = Path.of(
                "export/markdown/raw/01 - Regeln/"
                        + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln/page_032.json");
        Assumptions.assumeTrue(Files.exists(pageJson));

        RawPageData page = new ObjectMapper().readValue(pageJson.toFile(), RawPageData.class);
        TextInterpreter interpreter = new TextInterpreter();
        interpreter.setEmitBoxesInline(false);
        String md = interpreter.interpretPage(page);

        // Werkzeuge-Block darf KEINE faelschlichen Bullets bekommen
        assertFalse(md.contains("- machen. Welche Art"),
                "Werkzeuge-Werkfortsetzung darf nicht als Liste erscheinen");
        assertFalse(md.contains("- muss der Meister"),
                "Werkzeuge-Werkfortsetzung darf nicht als Liste erscheinen");
    }
}
