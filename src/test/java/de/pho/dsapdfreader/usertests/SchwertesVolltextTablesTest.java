package de.pho.dsapdfreader.usertests;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Volltext-basierte User-Acceptance-Tests fuer den Kodex des Schwertes.
 *
 * <p>Im Gegensatz zu {@link SchwertesUserAcceptanceTest} liest dieser Test die
 * komplette {@code _volltext.md} ein, die {@code RegenerateText} schreibt. Jede
 * vom User reportierte Tabelle wird hier als <b>exakter Block</b> verifiziert —
 * Zeile fuer Zeile, mit Markdown-Trennern. Damit ist der Beleg nicht eine
 * lockere "contains"-Pruefung auf eine einzelne Zeile, sondern die vollstaendige
 * Tabelle so wie sie im Volltext steht.
 *
 * <p>Lauf-Voraussetzung: {@code RegenerateText} (oder die volle Pipeline) muss
 * den Volltext zuvor erzeugt haben. Fehlt die Datei, werden die Tests
 * uebersprungen.
 */
class SchwertesVolltextTablesTest {

    private static final Path VOLLTEXT = Path.of(
            "export/markdown/text/01 - Regeln/"
                    + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln/"
                    + "result/_volltext.md");

    private static String volltext;

    @BeforeAll
    static void loadVolltext() throws IOException {
        Assumptions.assumeTrue(Files.exists(VOLLTEXT),
                "Volltext fehlt unter " + VOLLTEXT
                        + " — RegenerateText laufen lassen, dann erneut testen.");
        volltext = Files.readString(VOLLTEXT, StandardCharsets.UTF_8);
        // Zeilenenden auf \n normalisieren, damit Tests plattformunabhaengig laufen.
        volltext = volltext.replace("\r\n", "\n");
    }

    /**
     * Pruefen, ob der Volltext den gegebenen mehrzeiligen Block exakt enthaelt
     * (inkl. Newlines, exklusive optionalem trailing-Newline). Bei Fehlschlag
     * wird ein lesbarer Vergleich ausgegeben.
     */
    private static void assertContainsBlock(String label, String expectedBlock) {
        String normalized = expectedBlock.replace("\r\n", "\n").stripTrailing();
        if (volltext.contains(normalized)) return;
        // Hilfreiche Fehlermeldung: zeige tatsaechlichen Bereich um die erste
        // gemeinsame Zeile.
        String firstLine = normalized.split("\n", 2)[0];
        int idx = volltext.indexOf(firstLine);
        String actualSnippet;
        if (idx < 0) {
            actualSnippet = "Erste erwartete Zeile '" + firstLine + "' nicht im Volltext gefunden.";
        } else {
            int end = Math.min(volltext.length(), idx + normalized.length() + 80);
            actualSnippet = volltext.substring(idx, end);
        }
        throw new AssertionError(
                label + " — Tabellenblock fehlt im Volltext.\n"
                        + "ERWARTET:\n" + normalized
                        + "\n\nIST (ab erster gemeinsamer Zeile):\n" + actualSnippet);
    }

    // -------------------------------------------------------------------
    // S18 — Qualitaetsstufen (FP/QS), 6 Datenzeilen
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S18 Volltext: Qualitaetsstufen-Tabelle exakt")
    void s18_qualitaetsstufen_exakt() {
        assertContainsBlock("S18 Qualitaetsstufen",
                """
                | Fertigkeitspunkte | Qualitätsstufe |
                | --- | --- |
                | 0-3 | 1 |
                | 4-6 | 2 |
                | 7-9 | 3 |
                | 10-12 | 4 |
                | 13-15 | 5 |
                | 16+ | 6 |
                """);
    }

    // -------------------------------------------------------------------
    // S19 — Modifikator-Tabelle (3 Spalten), Multi-Line-Cells
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S19 Volltext: Modifikator-Tabelle exakt")
    void s19_modifikator_exakt() {
        assertContainsBlock("S19 Modifikator",
                """
                | Modifikator | Bewertung | Klettern (Beispiel) |
                | --- | --- | --- |
                | +5 | extrem leichte Probe | eher ein steiniger Hügel als eine Felswand |
                | +3 | sehr leichte Probe | Felswand enthält teilweise Stufen |
                | +1 | leichte Probe | viele Griffmöglichkeiten |
                | +/– 0 | anspruchsvolle Probe | eine Felswand mit guten Griffmöglichkeiten |
                | –1 | schwierige Probe | feuchte Moose an der Felswand |
                | –3 | sehr schwierige Probe | sehr steil, wenig Griffmöglichkeiten |
                | –5 | extrem schwierige Probe | sehr steil, wenig Griffmöglichkeiten, feuchte Moose |
                """);
    }

    // -------------------------------------------------------------------
    // S19 — Wahrscheinlichkeitstabelle (3 Spalten, 2 Datenzeilen)
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S19 Volltext: Wahrscheinlichkeits-Tabelle exakt")
    void s19_wahrscheinlichkeit_exakt() {
        assertContainsBlock("S19 Wahrscheinlichkeit",
                """
                | Wurf | Wahrscheinlichkeit | Prozent |
                | --- | --- | --- |
                | Doppel-1/20 | je ca. 1 zu 140 | 0,71 % |
                | Dreifach-1/20 | je exakt 1 zu 8.000 | 0,0125 % |
                """);
    }

    // -------------------------------------------------------------------
    // S22 — Sammelproben + Anzahl der erlaubten Proben (zwei separate Tabellen)
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S22 Volltext: Ergebnisse bei Sammelproben (eigene Tabelle)")
    void s22_sammelproben_ergebnisse_exakt() {
        assertContainsBlock("S22 Sammelproben",
                """
                ### Ergebnisse bei Sammelproben

                | QS | Erfolg |
                | --- | --- |
                | 6 | Teilerfolg |
                | 10 | Aufgabe erfüllt |
                """);
    }

    @Test
    @DisplayName("S22 Volltext: Anzahl der erlaubten Proben (eigene Tabelle nach Heading)")
    void s22_anzahlErlaubteProben_exakt() {
        assertContainsBlock("S22 Anzahl der erlaubten Proben",
                """
                ### Anzahl der erlaubten Proben (Vorschläge)

                | Anzahl | Herausforderung |
                | --- | --- |
                | 5 | schwer |
                | 7 | durchschnitt |
                | 10 | leicht |
                """);
    }

    // -------------------------------------------------------------------
    // S23 — Beispiele fuer Vergleichsproben (3 Spalten, Multi-Line)
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S23 Volltext: Beispiele fuer Vergleichsproben exakt")
    void s23_vergleichsproben_exakt() {
        assertContainsBlock("S23 Vergleichsproben",
                """
                | Handlung | Fertigkeit aktive Partei | Fertigkeit passive Partei |
                | --- | --- | --- |
                | Fährtensuchen bzw. Fährten verwischen | Fährtensuchen | Fährtensuchen |
                | Jemanden Betören bzw. dem Versuch widerstehen | Betören | Willenskraft |
                | Lügen bzw. Lüge durchschauen | Überreden | Menschenkenntnis |
                | Sich verstecken bzw. jemanden entdecken | Verbergen | Sinnesschärfe |
                """);
    }

    // -------------------------------------------------------------------
    // S26 — Belastung (Phase 4.6 Spalten-Cluster-Fix)
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S26 Volltext: Belastungs-Tabelle exakt (Phase 4.6 — alle 4 Stufen)")
    void s26_belastung_exakt() {
        assertContainsBlock("S26 Belastung",
                """
                | Belastungsstufe | Auswirkung |
                | --- | --- |
                | Stufe I | leicht belastet, Proben auf Talente, die durch Belastung erschwert sind –1, AT –1, Verteidigung –1, INI –1, GS –1 |
                | Stufe II | belastet, Proben auf Talente, die durch Belastung erschwert sind –2, AT –2, Verteidigung –2, INI –2, GS –2 |
                | Stufe III | schwer belastet, Proben auf Talente, die durch Belastung erschwert sind –3, AT –3, Verteidigung –3, INI –3, GS –3 |
                | Stufe IV | handlungsunfähig, bis auf Last fallen lassen |
                """);
    }

    // -------------------------------------------------------------------
    // S26 — Ruestungs-Tabelle (4 Spalten)
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S26 Volltext: Ruestungs-Tabelle exakt")
    void s26_ruestung_exakt() {
        assertContainsBlock("S26 Ruestung",
                """
                | Art | Rüstungs­ schutz | Belastung (Stufe) | zusätzl. Abzüge |
                | --- | --- | --- | --- |
                | Normale Klei­ dung, Felle oder nackt | 0 | 0 | – |
                | Schwere Kleidung, Winterkleidung | 1 | 0 | –1 GS, –1 INI |
                | Stoffrüstung, Gambeson | 2 | 1 | – |
                | Lederrüstung | 3 | 1 | –1 GS, –1 INI |
                | Kettenrüstung | 4 | 2 | – |
                | Schuppen­ rüstung | 5 | 2 | –1 GS, –1 INI |
                | Plattenrüstung | 6 | 3 | – |
                | Gestechrüstung | 7 | 4 | – |
                | Turnierrüstung | 8 | 5 | – |
                """);
    }

    // -------------------------------------------------------------------
    // S26 — Betaeubung (4 Stufen)
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S26 Volltext: Betaeubungs-Tabelle exakt (alle 4 Stufen)")
    void s26_betaeubung_exakt() {
        assertContainsBlock("S26 Betaeubung",
                """
                | Betäubungsstufe | Auswirkung |
                | --- | --- |
                | Stufe I | leicht angeschlagen, alle Proben –1 |
                | Stufe II | angeschlagen, alle Proben –2 |
                | Stufe III | schwer angeschlagen, alle Proben –3 |
                | Stufe IV | handlungsunfähig |
                """);
    }

    // -------------------------------------------------------------------
    // S27 — Furcht (4 Stufen)
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S27 Volltext: Furcht-Tabelle exakt")
    void s27_furcht_exakt() {
        assertContainsBlock("S27 Furcht",
                """
                | Furchtstufe | Auswirkung |
                | --- | --- |
                | Stufe I | beunruhigt, alle Proben –1 |
                | Stufe II | verängstigt, alle Proben –2 |
                | Stufe III | in Panik, alle Proben –3 |
                | Stufe IV | katatonisch, handlungsunfähig |
                """);
    }

    // -------------------------------------------------------------------
    // S27 — Schmerz (4 Stufen)
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S27 Volltext: Schmerz-Tabelle exakt")
    void s27_schmerz_exakt() {
        assertContainsBlock("S27 Schmerz",
                """
                | Schmerzstufe | Auswirkung |
                | --- | --- |
                | Stufe I | leichte Schmerzen, alle Proben –1, GS –1 |
                | Stufe II | ablenkende Schmerzen, alle Proben –2, GS –2 |
                | Stufe III | starke Schmerzen, alle Proben –3, GS –3 |
                | Stufe IV | handlungsunfähig, ansonsten alle Proben –4 |
                """);
    }

    // -------------------------------------------------------------------
    // S27 — Verwirrung (4 Stufen, Multi-Line in Stufe III)
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S27 Volltext: Verwirrungs-Tabelle exakt")
    void s27_verwirrung_exakt() {
        assertContainsBlock("S27 Verwirrung",
                """
                | Verwirrungsstufe | Auswirkung |
                | --- | --- |
                | Stufe I | leicht verwirrt, alle Proben –1 |
                | Stufe II | verwirrt, alle Proben –2 |
                | Stufe III | sehr verwirrt, alle Proben –3, komplexe Aktivitäten wie Zaubern, Liturgien wirken und die Anwendung von Wissenstalenten ist unmöglich |
                | Stufe IV | handlungsunfähig |
                """);
    }

    // -------------------------------------------------------------------
    // S38 — Koerperbeherrschung-Tabelle (Phase 4.6 — spurious-boundary-Filter)
    // Die rohen Linien-Segmente liefern 4 Boundaries [56,131,223,285], doch
    // 131 ist nur ein PDF-Render-Artefakt: in den meisten Reihen laeuft Text
    // ueber 131 hinweg. Der Filter entfernt diese Pseudo-Boundary, sodass die
    // Tabelle korrekt mit 2 Spalten dargestellt wird.
    // -------------------------------------------------------------------
    @Test
    @DisplayName("S38 Volltext: Koerperbeherrschung-Tabelle exakt (Spurious-Boundary entfernt)")
    void s38_koerperbeherrschung_exakt() {
        assertContainsBlock("S38 Koerperbeherrschung",
                """
                | Handlung | Modifikator |
                | --- | --- |
                | Einen Purzelbaum schlagen | +5 |
                | Auf einem Balken balancieren | +3 |
                | Ein Rad schlagen | +1 |
                | Handstand | +/–0 |
                | Bei Sturm auf einem Schiff auf den Beinen bleiben | –1 |
                | Auf Skiern einen gefährlichen Abhang hinunterfahren | –3 |
                | Auf einem dünnen Drahtseil laufen | –5 |
                """);
    }

    // -------------------------------------------------------------------
    // CROSS-PAGE-PROBLEME — diese Tabellen laufen ueber Spalten-/Seitenwechsel
    // und sind aktuell GETRENNT. Phase 4.7 wird sie zusammenfuehren ueber
    // matching Spaltenbreiten (siehe Memory: md_pipeline_phase4_6.md).
    //
    // Die Tests bleiben hier als ausfuehrliche Referenz auf die offenen Bugs
    // und werden gruen, sobald 4.7 implementiert ist.
    // -------------------------------------------------------------------

    @Disabled("Phase 4.7: Entrueckung-Tabelle laeuft S26-rechts → S27-links, aktuell zwei separate Tabellen.")
    @Test
    @DisplayName("S26/S27 Volltext: Entrueckungs-Tabelle (cross-page) exakt")
    void s26_entrueckung_crossPage_exakt() {
        assertContainsBlock("S26 Entrueckung cross-page",
                """
                | Entrückungs­ stufe | Auswirkung |
                | --- | --- |
                | Stufe I | leicht entrückt, alle Proben auf Talente und Zauber –1, so sie nicht dem Gott des Geweihten gefällig sind |
                | Stufe II | entrückt, alle dem Gott des Geweihten gefällige Proben auf Talente und Zauber +1, alle anderen –2 |
                | Stufe III | göttlich berührt, alle dem Gott des Geweihten gefällige Proben auf Talente und Zauber +2, alle anderen –3 |
                | Stufe IV | ein Werkzeug des Gottes, alle dem Gott des Geweihten gefällige Proben auf Talente und Zauber +3, alle anderen um –4 erschwert |
                """);
    }

    @Disabled("Phase 4.7: Paralyse-Tabelle laeuft S27-rechts → S28-links und mischt aktuell mit Schmerz-Body.")
    @Test
    @DisplayName("S27/S28 Volltext: Paralyse-Tabelle (cross-page) exakt")
    void s27_paralyse_crossPage_exakt() {
        assertContainsBlock("S27 Paralyse cross-page",
                """
                | Paralysestufe | Auswirkung |
                | --- | --- |
                | Stufe I | leicht versteift, alle Proben, die Bewegung oder Sprache erfordern, –1, GS nur noch 75 % |
                | Stufe II | versteift, alle Proben, die Bewegung oder Sprache erfordern, –2, GS nur noch 50 % |
                | Stufe III | kaum mehr bewegungsfähig, alle Proben, die Bewegung oder Sprache erfordern, –3, GS nur noch 25 % |
                | Stufe IV | bewegungsunfähig |
                """);
    }
}
