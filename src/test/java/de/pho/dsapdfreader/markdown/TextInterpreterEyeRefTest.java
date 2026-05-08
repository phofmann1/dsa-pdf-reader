package de.pho.dsapdfreader.markdown;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.pho.dsapdfreader.book.BoxExtractor.BoxRegion;
import de.pho.dsapdfreader.markdown.RawPageData.RawImage;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer die Box-Heading-Aufloesung ueber Auge-Symbol-Bezug.
 *
 * <p>Boxen ohne eigene Heading verweisen ueber ein Auge-Symbol auf eine
 * Body-Heading. Da das Auge je nach PDF-Variante als kleines Bild, Font-Glyph
 * oder Vektor-Pfad gerendert wird, ist es nicht in jedem Fall extrahierbar.
 * Geometrische Naehe zum naechstgelegenen Body-Heading liefert die robuste
 * Default-Auflosung.
 */
class TextInterpreterEyeRefTest {

    private static BoxRegion box(float x, float y, float w, float h) {
        return new BoxRegion(0, x, y, w, h, new float[]{0.5f, 0.5f, 0.5f});
    }

    private static RawImage eyeImg(float x, float y) {
        RawImage im = new RawImage();
        im.x = x; im.y = y; im.width = 10.7f; im.height = 5.8f;
        im.pixelWidth = 23; im.pixelHeight = 13;
        im.origin = "Kodex des Schwertes - 01 - Regeln_p11_img35.png";
        return im;
    }

    @Test
    void hasExplicitHeading_detectsHashHeading() {
        assertTrue(TextInterpreter.hasExplicitHeading("### Anwendungsgebiete\nText..."));
        assertTrue(TextInterpreter.hasExplicitHeading("## Foo"));
    }

    @Test
    void hasExplicitHeading_falseForBoldOnly() {
        assertFalse(TextInterpreter.hasExplicitHeading("**Bold line**\nrest"));
        assertFalse(TextInterpreter.hasExplicitHeading("plain text"));
        assertFalse(TextInterpreter.hasExplicitHeading(""));
    }

    @Test
    void resolveBoxHeadingFromBody_withEye_picksPrecedingHeading() {
        // S32 Schwertes: Eye at (59, 89), heading "Einsatz von Talenten" at y=72.
        BoxRegion sidebar = box(420f, 45f, 132f, 127f);
        RawImage eye = eyeImg(59.7f, 89.7f);
        var headings = List.of(
                new TextInterpreter.HeadingAnchor(72f,  "Einsatz von Talenten"),
                new TextInterpreter.HeadingAnchor(400f, "Werkzeuge"),
                new TextInterpreter.HeadingAnchor(550f, "Routineprobe"));

        String h = TextInterpreter.resolveBoxHeadingFromBody(sidebar, List.of(eye), headings);
        assertEquals("Einsatz von Talenten", h);
    }

    @Test
    void resolveBoxHeadingFromBody_withoutEye_usesNearestHeading() {
        // S42 Schwertes: keine extrahierbaren Eye-Bilder; Box ist direkt unter
        // dem Heading "Gesellschaftstalente" (y=480) — Box bei y=501.
        BoxRegion sidebar = box(18f, 501f, 132f, 90f);
        var headings = List.of(
                new TextInterpreter.HeadingAnchor(150f, "Frueheres Heading"),
                new TextInterpreter.HeadingAnchor(480f, "Gesellschaftstalente"),
                new TextInterpreter.HeadingAnchor(700f, "Spaeteres Heading"));
        String h = TextInterpreter.resolveBoxHeadingFromBody(sidebar, List.of(), headings);
        assertEquals("Gesellschaftstalente", h);
    }

    @Test
    void resolveBoxHeadingFromBody_emptyHeadingsReturnsNull() {
        BoxRegion b = box(0, 0, 100, 100);
        assertNull(TextInterpreter.resolveBoxHeadingFromBody(b, List.of(), List.of()));
    }

    @Test
    void resolveBoxHeadingFromBody_eyeFarAwayFallsBackToNearest() {
        // Eye ist da, aber zu weit oberhalb (>200pt). Der Algorithmus faellt
        // auf den naechstgelegenen Heading per |dy| zur Box zurueck.
        BoxRegion b = box(50f, 600f, 200f, 80f);
        RawImage eyeFar = eyeImg(60f, 50f);  // Eye weit oben
        var headings = List.of(
                new TextInterpreter.HeadingAnchor(20f,  "Far Up"),
                new TextInterpreter.HeadingAnchor(580f, "Near Box"));
        String h = TextInterpreter.resolveBoxHeadingFromBody(b, List.of(eyeFar), headings);
        assertEquals("Near Box", h);
    }

    @Test
    void findEyeGlyphCandidates_smallDuplicateImageMatches() {
        RawPageData page = new RawPageData(1, 595f, 842f, List.of());
        page.images = new java.util.ArrayList<>();
        page.images.add(eyeImg(60f, 90f));            // valid
        // Zu gross
        RawImage tooBig = new RawImage(); tooBig.x = 100; tooBig.y = 100;
        tooBig.width = 50; tooBig.height = 50; tooBig.origin = "x.png";
        page.images.add(tooBig);
        // Kein Origin (= original, kein Duplikat)
        RawImage noOrigin = new RawImage(); noOrigin.x = 100; noOrigin.y = 100;
        noOrigin.width = 10.7f; noOrigin.height = 5.8f; noOrigin.origin = null;
        page.images.add(noOrigin);
        // Off-screen
        RawImage off = new RawImage(); off.x = -10; off.y = 50;
        off.width = 10.7f; off.height = 5.8f; off.origin = "x.png";
        page.images.add(off);

        List<RawImage> matches = TextInterpreter.findEyeGlyphCandidates(page);
        assertEquals(1, matches.size());
    }

    @Test
    void integrationS32_sidebarBoxResolvesToEinsatzVonTalenten() throws Exception {
        Path pageJson = Path.of(
                "export/markdown/raw/01 - Regeln/"
                        + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln/page_032.json");
        Assumptions.assumeTrue(Files.exists(pageJson),
                "Integrationstest uebersprungen: " + pageJson + " nicht vorhanden");

        RawPageData page = new ObjectMapper().readValue(pageJson.toFile(), RawPageData.class);
        TextInterpreter interpreter = new TextInterpreter();
        interpreter.setEmitBoxesInline(false);
        interpreter.interpretPage(page);

        // Sidebar-Box ohne eigene Heading muss "Einsatz von Talenten" tragen.
        TextInterpreter.BoxRendering sidebar = interpreter.getLastBoxes().stream()
                .filter(b -> b.x > 400f && b.y < 100f)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Sidebar-Box nicht gefunden"));
        assertEquals("Einsatz von Talenten", sidebar.headingGuess);
        assertTrue(sidebar.headingFromEyeRef, "Heading muss als auge-aufgeloest markiert sein");
    }

    @Test
    void integrationS42_sidebarBoxResolvesToGesellschaftstalente() throws Exception {
        Path pageJson = Path.of(
                "export/markdown/raw/01 - Regeln/"
                        + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln/page_042.json");
        Assumptions.assumeTrue(Files.exists(pageJson),
                "Integrationstest uebersprungen: " + pageJson + " nicht vorhanden");

        RawPageData page = new ObjectMapper().readValue(pageJson.toFile(), RawPageData.class);
        TextInterpreter interpreter = new TextInterpreter();
        interpreter.setEmitBoxesInline(false);
        interpreter.interpretPage(page);

        // S42 Sidebar bei x≈18, y≈501 — kein extrahierbares Eye-Bild,
        // Aufloesung muss ueber 2D-Naehe zur Body-Heading "Gesellschaftstalente" laufen.
        TextInterpreter.BoxRendering sidebar = interpreter.getLastBoxes().stream()
                .filter(b -> b.x < 50f && b.y > 450f && b.y < 600f)
                .findFirst()
                .orElseThrow(() -> new AssertionError("S42-Sidebar nicht gefunden"));
        assertEquals("Gesellschaftstalente", sidebar.headingGuess);
        assertTrue(sidebar.headingFromEyeRef);
    }
}
