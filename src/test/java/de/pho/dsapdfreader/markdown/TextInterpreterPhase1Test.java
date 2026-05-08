package de.pho.dsapdfreader.markdown;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer die Phase-1-Aenderungen im TextInterpreter:
 * <ul>
 *   <li>Soft-Linebreaks ({@code  \n}) am Absatzende
 *   <li>Box-Auslagerung via {@link TextInterpreter#setEmitBoxesInline(boolean)}
 *       und {@link TextInterpreter#getLastBoxes()}
 *   <li>Heading-Erkennung in {@link TextInterpreter#guessBoxHeading(String)}
 * </ul>
 *
 * <p>Der Integrations-Smoketest auf einer echten Seite laeuft nur, wenn die
 * Schwertes-Rohdaten lokal vorliegen — sonst wird er via Assumption uebersprungen.
 */
class TextInterpreterPhase1Test {

    @Test
    void guessBoxHeading_fromHashHeading() {
        String md = "### Anwendungsgebiete\nText des Kastens.";
        assertEquals("Anwendungsgebiete", TextInterpreter.guessBoxHeading(md));
    }

    @Test
    void guessBoxHeading_fromBoldLine() {
        String md = "**Ungewohnter Einsatz von Talenten**\nText des Kastens.";
        assertEquals("Ungewohnter Einsatz von Talenten", TextInterpreter.guessBoxHeading(md));
    }

    @Test
    void guessBoxHeading_fallbackFirstLine() {
        String md = "Statt einen fixen Wert anzunehmen, gilt folgendes ...";
        String h = TextInterpreter.guessBoxHeading(md);
        assertTrue(h.startsWith("Statt einen fixen Wert"));
        assertTrue(h.length() <= 60, "must be clipped to 60 chars, got: " + h);
    }

    @Test
    void guessBoxHeading_skipsLeadingComment() {
        String md = "<!-- Bild 1 -->\n**Talentgruppenproben**\nFlieszt darunter.";
        assertEquals("Talentgruppenproben", TextInterpreter.guessBoxHeading(md));
    }

    @Test
    void guessBoxHeading_stripsTrailingColon() {
        String md = "**Anwendungsgebiete:**";
        assertEquals("Anwendungsgebiete", TextInterpreter.guessBoxHeading(md));
    }

    @Test
    void guessBoxHeading_emptyForBlank() {
        assertEquals("", TextInterpreter.guessBoxHeading("   \n\n"));
        assertEquals("", TextInterpreter.guessBoxHeading(null));
    }

    @Test
    void boxRendering_recordCarriesPageAndGeometry() {
        TextInterpreter.BoxRendering b = new TextInterpreter.BoxRendering(
                3, 32, "Ungewohnter Einsatz", "**Ungewohnter Einsatz**\nText",
                66f, 200f, 250f, 120f);
        assertEquals(3, b.index);
        assertEquals(32, b.pageNumber);
        assertEquals("Ungewohnter Einsatz", b.headingGuess);
        assertEquals(200f, b.y);
    }

    @Test
    void interpretPage_demotesSteigerungskostenHeading_onRealPage() throws Exception {
        // S37 Schwertes hat im OCR mindestens einen Faelschlich-Heading
        // "### Steigerungskosten: A" — die Post-Processing-Regel muss ihn
        // zu "**Steigerungskosten:** A" zurueckstufen.
        Path pageJson = Path.of(
                "export/markdown/raw/01 - Regeln/"
                        + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln/page_037.json");
        Assumptions.assumeTrue(Files.exists(pageJson),
                "Integrationstest uebersprungen: " + pageJson + " nicht vorhanden");

        RawPageData page = new ObjectMapper().readValue(pageJson.toFile(), RawPageData.class);
        TextInterpreter interpreter = new TextInterpreter();
        interpreter.setEmitBoxesInline(false);
        String md = interpreter.interpretPage(page);

        // Es darf KEIN "### Steigerungskosten:" mehr im Output erscheinen.
        for (String line : md.split("\n")) {
            assertFalse(line.matches("#{1,3}\\s+Steigerungskosten:.*"),
                    "Steigerungskosten darf nicht als Heading erscheinen: " + line);
        }
        // Aber: mindestens eine bold-Variante muss vorhanden sein.
        assertTrue(md.contains("**Steigerungskosten:**"),
                "Erwarte mindestens ein **Steigerungskosten:**-Label im Output");
    }

    @Test
    void interpretPage_clearsBoxesAndProducesSoftLinebreaks_onRealPage() throws Exception {
        Path pageJson = Path.of(
                "export/markdown/raw/01 - Regeln/"
                        + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln/page_032.json");
        Assumptions.assumeTrue(Files.exists(pageJson),
                "Integrationstest uebersprungen: " + pageJson + " nicht vorhanden");

        RawPageData page = new ObjectMapper().readValue(pageJson.toFile(), RawPageData.class);

        TextInterpreter interpreter = new TextInterpreter();
        interpreter.setEmitBoxesInline(false);

        // Vorab: lastBoxes kuenstlich befuellen, damit wir sehen, dass interpretPage clearsen.
        // (Indirekt geprueft: nach erstem Aufruf sind die Boxen genau die der Seite.)
        String body = interpreter.interpretPage(page);

        assertNotNull(body);
        // Soft-Linebreaks: zwei Trailing-Spaces vor \n irgendwo im Text.
        assertTrue(body.contains("  \n"),
                "Body sollte Soft-Linebreaks enthalten (\"  \\n\")");

        // Boxen-Inhalte stehen NICHT mehr inline im Body.
        assertFalse(body.contains("<!-- Box-Anfang"),
                "Inline-Box-Marker duerfen bei emitBoxesInline=false nicht erscheinen");
        assertFalse(body.contains("<!-- Box-Ende"),
                "Inline-Box-Marker duerfen bei emitBoxesInline=false nicht erscheinen");

        // Schwertes-Seite 32 enthaelt mehrere Kaesten ("Ungewohnter Einsatz",
        // "Talentgruppenproben") — getLastBoxes() muss sie aufsammeln.
        assertFalse(interpreter.getLastBoxes().isEmpty(),
                "Erwarte mindestens eine Box auf Seite 32 von Schwertes");

        for (TextInterpreter.BoxRendering b : interpreter.getLastBoxes()) {
            assertEquals(32, b.pageNumber);
            assertNotNull(b.markdown);
            assertFalse(b.markdown.isBlank());
        }

        // Erneuter Aufruf auf einer leeren Seite muss lastBoxes zuruecksetzen.
        RawPageData empty = new RawPageData(99, 595f, 842f, java.util.List.of());
        interpreter.interpretPage(empty);
        assertTrue(interpreter.getLastBoxes().isEmpty(),
                "lastBoxes muss vor jedem Aufruf zurueckgesetzt werden");
    }
}
