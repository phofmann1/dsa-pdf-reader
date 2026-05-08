package de.pho.dsapdfreader.markdown;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer Phase 4 — Tabellen-Detektion nach Spaltentrennung.
 *
 * <p>Schluesselverhalten:
 * <ul>
 *   <li>Y-Kollisionen (links Body + rechts Tabellenzelle auf gleicher y)
 *       werden NICHT als fullwidth-Tabelle eingestuft, sondern per
 *       Spaltentrennung in zwei separate Spalteninhalte aufgeloest.
 *   <li>Tabellen-Header mit dichten Cell-Boundaries (z. B. 11pt-Gap
 *       statt Wortzwischenraum) werden ueber Force-Split-Recovery erkannt.
 *   <li>Y-Spacing-Waechter beim Continuation-Pfad verhindert, dass
 *       Body-Absaetze in die letzte Tabellenzelle absorbiert werden.
 *   <li>Typesetting-Gaps (~15pt zwischen Bold-Label und Wert) werden
 *       NICHT mehr als Tabellen-Spalten missgedeutet.
 * </ul>
 */
class TextInterpreterTablesTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static String renderPage(int pageNumber) throws Exception {
        Path json = Path.of(
                "export/markdown/raw/01 - Regeln/"
                        + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln/page_"
                        + String.format("%03d", pageNumber) + ".json");
        Assumptions.assumeTrue(Files.exists(json));
        RawPageData page = MAPPER.readValue(json.toFile(), RawPageData.class);
        TextInterpreter interpreter = new TextInterpreter();
        interpreter.setEmitBoxesInline(false);
        return interpreter.interpretPage(page);
    }

    @Test
    void s33_qualitaetsstufenTable_hatAlleSechsZeilen() throws Exception {
        String md = renderPage(33);
        // Alle 6 Datenreihen muessen als Tabellen-Cells erscheinen
        for (String row : new String[]{"| 0-3 | 1 |",
                                         "| 4-6 | 2 |",
                                         "| 7-9 | 3 |",
                                         "| 10-12 | 4 |",
                                         "| 13-15 | 5 |",
                                         "| 16+ | 6 |"}) {
            assertTrue(md.contains(row),
                    "Tabellen-Zeile fehlt oder ist deformiert: " + row);
        }
    }

    @Test
    void s33_qualitaetsstufenTable_keineLeakageVonBodyText() throws Exception {
        String md = renderPage(33);
        // Body-Text "Da es für Helden..." darf NICHT in einer Tabellenzelle erscheinen
        // (frueheres Bug: durch zu liberale Continuation-Logic absorbiert).
        java.util.regex.Pattern bodyInCell = java.util.regex.Pattern.compile(
                "\\|[^|]*Da es f.r Helden[^|]*\\|");
        assertFalse(bodyInCell.matcher(md).find(),
                "Body-Text 'Da es für Helden' darf nicht in Tabellenzelle landen");
    }

    @Test
    void s33_routineVoraussetzungenTable_hatHeader() throws Exception {
        String md = renderPage(33);
        // Header-Recovery muss "Modifikator-MaximumFertigkeitswert-Minimum"
        // anhand 11pt-Gaps in 2 Cells aufteilen
        assertTrue(md.contains("| Modifikator-Maximum | Fertigkeitswert-Minimum |"),
                "Header der Routinevoraussetzungen-Tabelle fehlt");
        // Alle 7 Datenzeilen
        for (String row : new String[]{"| +3 und höher | 1 |",
                                         "| +2 | 4 |",
                                         "| +1 | 7 |",
                                         "| +/–0 | 10 |",
                                         "| –1 | 13 |",
                                         "| –2 | 16 |",
                                         "| –3 | 19 |"}) {
            assertTrue(md.contains(row), "Routine-Tabellenzeile fehlt: " + row);
        }
    }

    @Test
    void s37_gaukeleienStatlines_keineFalseTable() throws Exception {
        String md = renderPage(37);
        // Frueheres Bug: 15pt-Typesetting-Gaps wurden als 3-Cell-Tabelle erkannt;
        // Body-Statlines wie "**Anwendungsgebiete:**" landeten in Pseudo-Tabellenzellen.
        assertFalse(md.contains("| Anwendungsgebiete:"),
                "Anwendungsgebiete:-Statline darf nicht in Tabellenzelle landen");
        assertTrue(md.contains("**Anwendungsgebiete:** Jonglieren, Possenreißen, Verstecktricks"),
                "Gaukeleien-Anwendungsgebiete muss als Bold-Statline gerendert sein");
    }

    @Test
    void s37_kletternStatlines_sauberGetrennt() throws Exception {
        String md = renderPage(37);
        // Klettern-Statlines (rechte Spalte auf S37) muessen ihre eigenen
        // Bold-Labels haben — nicht mit Gaukeleien-Spalte vermischt.
        assertTrue(md.contains("**Anwendungsgebiete:** Baumklettern, Bergsteigen, Eisklettern, Fassadenklettern"));
        assertTrue(md.contains("**Werkzeuge:** eventuell Kletterausrüstung"));
    }

    @Test
    void s37_steigerungskostenTabelle_kompletteSiebenZeilen() throws Exception {
        String md = renderPage(37);
        for (String row : new String[]{"| Einfachste Kartentricks | +5 |",
                                         "| Mit fünf Kugeln jonglieren | –3 |",
                                         "| Ein mürrisches Publikum zum Lachen bringen | –5 |"}) {
            assertTrue(md.contains(row), "Gaukeleien-Steigerungskosten-Tabellenzeile fehlt: " + row);
        }
    }
}
