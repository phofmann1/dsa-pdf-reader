package de.pho.dsapdfreader.book;

import de.pho.dsapdfreader.markdown.TextInterpreter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer die Phase-1-Helfer in {@link RegenerateBookText}:
 * Dateinamen-Bauen, Kollisions-Suffix, Cleanup von altem Flat-Layout
 * und Cleanup von Subverzeichnissen.
 */
class RegenerateBookTextPhase1Test {

    @Test
    void sanitizeFileName_replacesReservedChars() {
        // Windows verbotene Zeichen: \ / : * ? " < > |
        String s = RegenerateBookText.sanitizeFileName("Probe: GE/GE\\KO ?<test>?|");
        assertFalse(s.contains("/"));
        assertFalse(s.contains("\\"));
        assertFalse(s.contains(":"));
        assertFalse(s.contains("?"));
        assertFalse(s.contains("<"));
        assertFalse(s.contains(">"));
        assertFalse(s.contains("|"));
        // Mehrfach-Whitespace wird zu einem Space kollabiert.
        assertFalse(s.contains("  "));
    }

    @Test
    void sanitizeFileName_emptyFallsBackToAnmerkung() {
        assertEquals("Anmerkung", RegenerateBookText.sanitizeFileName(""));
        assertEquals("Anmerkung", RegenerateBookText.sanitizeFileName("   "));
        assertEquals("Anmerkung", RegenerateBookText.sanitizeFileName("///:::"));
    }

    @Test
    void buildBoxFileName_format() {
        TextInterpreter.BoxRendering box = new TextInterpreter.BoxRendering(
                1, 32, "Ungewohnter Einsatz von Talenten", "...",
                0f, 0f, 0f, 0f);
        Map<String, Integer> counts = new HashMap<>();
        String name = RegenerateBookText.buildBoxFileName(box, counts);
        assertEquals("032 - Ungewohnter Einsatz von Talenten.md", name);
    }

    @Test
    void buildBoxFileName_threeDigitPaddingForLowPages() {
        TextInterpreter.BoxRendering box = new TextInterpreter.BoxRendering(
                1, 7, "Hinweis", "...", 0, 0, 0, 0);
        String name = RegenerateBookText.buildBoxFileName(box, new HashMap<>());
        assertTrue(name.startsWith("007 - "), name);
    }

    @Test
    void buildBoxFileName_collisionAddsSuffix() {
        TextInterpreter.BoxRendering b1 = new TextInterpreter.BoxRendering(
                1, 50, "Hinweis", "...", 0, 0, 0, 0);
        TextInterpreter.BoxRendering b2 = new TextInterpreter.BoxRendering(
                2, 50, "Hinweis", "...", 0, 0, 0, 0);
        TextInterpreter.BoxRendering b3 = new TextInterpreter.BoxRendering(
                3, 50, "Hinweis", "...", 0, 0, 0, 0);
        Map<String, Integer> counts = new HashMap<>();
        assertEquals("050 - Hinweis.md", RegenerateBookText.buildBoxFileName(b1, counts));
        assertEquals("050 - Hinweis (2).md", RegenerateBookText.buildBoxFileName(b2, counts));
        assertEquals("050 - Hinweis (3).md", RegenerateBookText.buildBoxFileName(b3, counts));
    }

    @Test
    void buildBoxFileName_emptyHeadingFallsBackToAnmerkung() {
        TextInterpreter.BoxRendering box = new TextInterpreter.BoxRendering(
                1, 12, "", "...", 0, 0, 0, 0);
        String name = RegenerateBookText.buildBoxFileName(box, new HashMap<>());
        assertEquals("012 - Anmerkung.md", name);
    }

    @Test
    void cleanLegacyFlatLayout_removesOldArtifacts(@TempDir Path tmp) throws IOException {
        Files.writeString(tmp.resolve("seite_001.md"), "x", StandardCharsets.UTF_8);
        Files.writeString(tmp.resolve("seite_042.md"), "x", StandardCharsets.UTF_8);
        Files.writeString(tmp.resolve("_volltext.md"), "x", StandardCharsets.UTF_8);
        // Diese Dateien duerfen NICHT geloescht werden:
        Files.writeString(tmp.resolve("_headings.md"), "x", StandardCharsets.UTF_8);
        Files.writeString(tmp.resolve("_meta.txt"), "x", StandardCharsets.UTF_8);
        Files.createDirectory(tmp.resolve("result"));
        Files.writeString(tmp.resolve("result").resolve("seite_001.md"), "x", StandardCharsets.UTF_8);

        RegenerateBookText.cleanLegacyFlatLayout(tmp);

        assertFalse(Files.exists(tmp.resolve("seite_001.md")));
        assertFalse(Files.exists(tmp.resolve("seite_042.md")));
        assertFalse(Files.exists(tmp.resolve("_volltext.md")));
        // Andere Dateien bleiben unangetastet.
        assertTrue(Files.exists(tmp.resolve("_headings.md")));
        assertTrue(Files.exists(tmp.resolve("_meta.txt")));
        // Dateien in Unterordnern bleiben unangetastet (wir loeschen nur Top-Level).
        assertTrue(Files.exists(tmp.resolve("result").resolve("seite_001.md")));
    }

    @Test
    void cleanDirectory_emptiesContentRecursivelyButKeepsDir(@TempDir Path tmp) throws IOException {
        Path target = tmp.resolve("result");
        Files.createDirectory(target);
        Files.writeString(target.resolve("a.md"), "x", StandardCharsets.UTF_8);
        Files.createDirectory(target.resolve("nested"));
        Files.writeString(target.resolve("nested").resolve("b.md"), "x", StandardCharsets.UTF_8);

        RegenerateBookText.cleanDirectory(target);

        assertTrue(Files.exists(target), "Verzeichnis selbst muss bestehen bleiben");
        assertEquals(0L, Files.list(target).count(), "Inhalt muss leer sein");
    }

    @Test
    void cleanDirectory_noopOnMissingDir(@TempDir Path tmp) {
        Path missing = tmp.resolve("does-not-exist");
        assertDoesNotThrow(() -> RegenerateBookText.cleanDirectory(missing));
    }

    @Test
    void regenerate_endToEnd_producesLogAndResultStructure(@TempDir Path tmp) throws IOException {
        // Realistische page_NNN.json gibt es nur im echten Korpus — fuer einen
        // E2E-Test gegen den TempDir kopieren wir 3 Schwertes-Seiten als
        // Eingabe. Wenn Korpus nicht da ist, Test sauber ueberspringen.
        Path corpusDir = Path.of(
                "export/markdown/raw/01 - Regeln/"
                        + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln");
        Assumptions.assumeTrue(Files.exists(corpusDir),
                "E2E-Test uebersprungen: " + corpusDir + " nicht vorhanden");

        Path rawDir = tmp.resolve("raw");
        Files.createDirectory(rawDir);
        // Kopiere drei Seiten mit unterschiedlichen Charakteristiken:
        // 32 (Boxen + Sidebar mit Eye-Resolution), 36 (Talente), 42 (Sidebar S.42).
        for (int p : new int[]{32, 36, 42}) {
            Path src = corpusDir.resolve(String.format("page_%03d.json", p));
            if (Files.exists(src)) {
                Files.copy(src, rawDir.resolve(src.getFileName()));
            }
        }
        Assumptions.assumeTrue(Files.list(rawDir).count() >= 3,
                "Quellseiten fehlen — Test ueberspringen");

        Path outDir = tmp.resolve("out");
        Files.createDirectory(outDir);
        // Veraltete Top-Level-Datei vorab anlegen, damit cleanLegacyFlatLayout
        // ausgeloest und beobachtet werden kann.
        Files.writeString(outDir.resolve("seite_007.md"), "stale", StandardCharsets.UTF_8);
        Files.writeString(outDir.resolve("_volltext.md"), "stale", StandardCharsets.UTF_8);

        int pages = RegenerateBookText.regenerate(rawDir, outDir, "TestSource");

        // 1) Anzahl Seiten korrekt
        assertEquals(3, pages);

        // 2) Verzeichnisstruktur: raw/ + result/ existieren
        Path pageMdDir = outDir.resolve("raw");
        Path resultDir = outDir.resolve("result");
        assertTrue(Files.isDirectory(pageMdDir));
        assertTrue(Files.isDirectory(resultDir));

        // 3) seite_NNN.md liegen in raw/, NICHT mehr im Top-Level
        try (Stream<Path> pages2 = Files.list(pageMdDir)) {
            assertEquals(3L, pages2.filter(p -> p.getFileName().toString().matches("seite_\\d+\\.md")).count());
        }

        // 3b) log/-Verzeichnis bleibt unangetastet (User-Wunsch: log/ ist fuer manuelle
        // Analysen reserviert). Wir legen vorher etwas dort an und pruefen, dass es bleibt.
        Path userLogDir = outDir.resolve("log");
        Files.createDirectories(userLogDir);
        Files.writeString(userLogDir.resolve("user-analysis.txt"), "wichtige notizen",
                StandardCharsets.UTF_8);
        assertFalse(Files.exists(outDir.resolve("seite_007.md")),
                "Legacy-Top-Level-Datei muss entfernt sein");
        assertFalse(Files.exists(outDir.resolve("_volltext.md")),
                "Top-Level _volltext.md muss entfernt sein");

        // 4) _volltext.md liegt in result/
        Path volltext = resultDir.resolve("_volltext.md");
        assertTrue(Files.exists(volltext));
        String volltextContent = Files.readString(volltext, StandardCharsets.UTF_8);
        // Soft-Linebreaks sind drin (zwei trailing spaces)
        assertTrue(volltextContent.contains("  \n"));
        // Quelle-Header
        assertTrue(volltextContent.startsWith("# Quelle: TestSource"));
        // KEINE Seiten-Header mittendrin
        assertFalse(volltextContent.contains("# Seite 32"),
                "Volltext darf keine Seiten-Header enthalten");

        // 5) Kasten-MDs in result/ vorhanden, Format "<3-stellig> - <heading>.md"
        try (Stream<Path> boxes = Files.list(resultDir)) {
            long boxCount = boxes
                    .filter(p -> p.getFileName().toString().matches("\\d{3} - .+\\.md"))
                    .count();
            assertTrue(boxCount > 0, "Erwarte mindestens eine ausgelagerte Box-MD");
        }

        // 6) Cleanup beim zweiten Lauf: vorhandene Files in raw/result/ werden ersetzt,
        //    log/ bleibt unangetastet.
        Files.writeString(resultDir.resolve("999 - Stale.md"), "stale", StandardCharsets.UTF_8);
        RegenerateBookText.regenerate(rawDir, outDir, "TestSource");
        assertFalse(Files.exists(resultDir.resolve("999 - Stale.md")),
                "Stale Box-MD muss beim Re-Run geloescht werden");
        assertTrue(Files.exists(userLogDir.resolve("user-analysis.txt")),
                "Inhalte in log/ duerfen beim Re-Run NICHT geloescht werden");
    }
}
