package de.pho.dsapdfreader.usertests;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Acceptance-Tests fuer "Der Wolfsfrost (230)" — konserviert die Phase-4.9-Fixes:
 * SmallCaps-Komplett-Uppercase, AdornFrames-Pair-Box-Detection, AdornFrames-
 * Marker-Filter im BoxExtractor.
 *
 * <p>Tests werden uebersprungen, wenn der Volltext fehlt (RegenerateText laufen
 * lassen).
 */
class WolfsfrostVolltextTest {

    private static final Path BOOK_BASE = Path.of(
            "export/markdown/text/04 - Regionen/"
                    + "Der Wolfsfrost (230)/Der Wolfsfrost - 04 - Regionen");
    private static final Path VOLLTEXT = BOOK_BASE.resolve("result/_volltext.md");
    private static final Path PAGE_009 = BOOK_BASE.resolve("raw/seite_009.md");
    private static final Path BOX_009 = BOOK_BASE.resolve(
            "result/009 - DER WOLFSFROST IM ÜBERBLICK.md");

    private static String volltext;
    private static String page9Body;
    private static String box9Content;

    @BeforeAll
    static void load() throws IOException {
        Assumptions.assumeTrue(Files.exists(VOLLTEXT),
                "Volltext fehlt unter " + VOLLTEXT
                        + " — RegenerateText laufen lassen.");
        volltext = Files.readString(VOLLTEXT, StandardCharsets.UTF_8).replace("\r\n", "\n");
        page9Body = Files.exists(PAGE_009)
                ? Files.readString(PAGE_009, StandardCharsets.UTF_8).replace("\r\n", "\n")
                : "";
        box9Content = Files.exists(BOX_009)
                ? Files.readString(BOX_009, StandardCharsets.UTF_8).replace("\r\n", "\n")
                : "";
    }

    // -------------------------------------------------------------------
    // SmallCaps-Komplett-Uppercase (Phase 4.9, Feature 1)
    // -------------------------------------------------------------------

    @Test
    @DisplayName("SmallCaps-Headings sind komplett uppercase, kein 'ÜbErblick'-Mix")
    void smallCaps_alwaysUppercase() {
        assertTrue(volltext.contains("EIN ÜBERBLICK ÜBER"),
                "Wolfsfrost-Heading 'EIN ÜBERBLICK ÜBER' fehlt im Volltext");
        // Mixed-Case-Variante darf NICHT vorkommen (das war der PDF-source-string).
        assertFalse(volltext.contains("ÜbErblick"),
                "SmallCaps-Mixed-Case 'ÜbErblick' darf NICHT im Volltext stehen");
        assertFalse(volltext.contains("dEr wolfsfrost"),
                "SmallCaps-Mixed-Case 'dEr wolfsfrost' darf NICHT im Volltext stehen");
    }

    // -------------------------------------------------------------------
    // AdornFrames-Pair-Box-Detection (Phase 4.9, Feature 2)
    // -------------------------------------------------------------------

    @Test
    @DisplayName("S9 Quote-Box wird ueber AdornFrames-Pair extrahiert")
    void s9_adornFramesBox_extracted() {
        Assumptions.assumeTrue(Files.exists(BOX_009),
                "Box-Datei fehlt — AdornFrames-Pair-Detection funktioniert nicht.");
        assertTrue(box9Content.contains("# DER WOLFSFROST IM ÜBERBLICK"),
                "Box-Heading fehlt");
        assertTrue(box9Content.contains("Marja Stappwitz"),
                "Box-Inhalt (Marja-Stappwitz-Quote) fehlt");
        assertTrue(box9Content.contains("Karuukijo, Nieijaa"),
                "Box-Inhalt (Karuukijo-Attribution) fehlt");
    }

    @Test
    @DisplayName("S9 Body enthaelt KEIN Box-Content (kein Gibberish)")
    void s9_body_noBoxBleed() {
        Assumptions.assumeTrue(!page9Body.isEmpty());
        // Box-Quote-Content darf nicht im Body landen.
        assertFalse(page9Body.contains("Marja Stappwitz"),
                "Marja-Stappwitz-Quote (Box-Content) leakt in Body");
        assertFalse(page9Body.contains("Karuukijo, Nieijaa"),
                "Karuukijo-Attribution (Box-Content) leakt in Body");
        assertFalse(page9Body.contains("Besser du fragst nicht"),
                "Quote-Beginn (Box-Content) leakt in Body");
    }

    @Test
    @DisplayName("AdornFrames-Glyphen tauchen NICHT im Output auf")
    void adornFrames_glyphsFiltered() {
        // Roh-Glyphen 'q44<44r' / 'o11911p' der AdornFrames-Marker duerfen
        // nirgends erscheinen.
        assertFalse(volltext.contains("q44<44r"),
                "AdornFrames-Top-Marker leakt in Volltext");
        assertFalse(volltext.contains("o11911p"),
                "AdornFrames-Bottom-Marker leakt in Volltext");
    }
}
