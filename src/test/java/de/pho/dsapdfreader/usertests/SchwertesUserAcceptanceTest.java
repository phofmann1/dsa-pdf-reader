package de.pho.dsapdfreader.usertests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.pho.dsapdfreader.book.RegenerateBookText;
import de.pho.dsapdfreader.markdown.RawPageData;
import de.pho.dsapdfreader.markdown.TextInterpreter;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User-Acceptance-Tests fuer die vom User reportierten konkreten Probleme im
 * Kodex des Schwertes. Jeder Test referenziert die User-Beobachtung im Namen.
 *
 * <p>Tests laufen gegen die echten Rohdaten unter
 * {@code export/markdown/raw/01 - Regeln/...}. Fehlen die Daten, werden die
 * Tests via {@link Assumptions#assumeTrue} uebersprungen.
 *
 * <p>Diese Tests dienen als verstaendlicher Beleg fuer den User. Logische
 * Unit-Tests bleiben in den fachlichen Test-Klassen.
 */
class SchwertesUserAcceptanceTest {

    private static final Path RAW_DIR = Path.of(
            "export/markdown/raw/01 - Regeln/"
                    + "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void requireCorpus() {
        Assumptions.assumeTrue(Files.exists(RAW_DIR),
                "Schwertes-Rohdaten fehlen unter " + RAW_DIR + " — User-Tests uebersprungen.");
    }

    private static String renderPage(int pageNumber) throws IOException {
        Path json = RAW_DIR.resolve(String.format("page_%03d.json", pageNumber));
        Assumptions.assumeTrue(Files.exists(json), "page_" + pageNumber + ".json fehlt");
        RawPageData page = MAPPER.readValue(json.toFile(), RawPageData.class);
        TextInterpreter interpreter = new TextInterpreter();
        interpreter.setEmitBoxesInline(false);
        return interpreter.interpretPage(page);
    }

    private static TextInterpreter renderPageWithBoxes(int pageNumber) throws IOException {
        Path json = RAW_DIR.resolve(String.format("page_%03d.json", pageNumber));
        Assumptions.assumeTrue(Files.exists(json), "page_" + pageNumber + ".json fehlt");
        RawPageData page = MAPPER.readValue(json.toFile(), RawPageData.class);
        TextInterpreter interpreter = new TextInterpreter();
        interpreter.setEmitBoxesInline(false);
        interpreter.interpretPage(page);
        return interpreter;
    }

    // ---------------------------------------------------------------------
    // Seite 32 — User-Reportagen
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("S32 / User: Kasten 'Ungewohnter Einsatz von Talenten' hat Heading vor Body")
    void s32_box_ungewohnterEinsatz_hatHeadingVorBody() throws Exception {
        TextInterpreter interpreter = renderPageWithBoxes(32);
        TextInterpreter.BoxRendering box = interpreter.getLastBoxes().stream()
                .filter(b -> b.headingGuess.equals("Ungewohnter Einsatz von Talenten"))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Kasten 'Ungewohnter Einsatz von Talenten' fehlt in lastBoxes"));
        // Heading-Zeile muss VOR der Body-Zeile stehen.
        int headingPos = box.markdown.indexOf("Ungewohnter Einsatz");
        int bodyPos = box.markdown.indexOf("Es gibt Talente");
        assertTrue(headingPos >= 0 && bodyPos > headingPos,
                "Heading muss vor Body stehen — got: " + box.markdown);
    }

    @Test
    @DisplayName("S32 / User: Kasten 'Talentgruppenproben' (Header+Body verschmolzen) enthaelt Heading + Tabelle")
    void s32_box_talentgruppenproben_headerBodyVerschmolzen() throws Exception {
        TextInterpreter interpreter = renderPageWithBoxes(32);
        TextInterpreter.BoxRendering box = interpreter.getLastBoxes().stream()
                .filter(b -> b.headingGuess.equals("Die Talentgruppenprobe"))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Kasten 'Die Talentgruppenprobe' fehlt"));
        assertTrue(box.markdown.contains("Anstatt bei jedem Talent"));
        assertTrue(box.markdown.contains("| Talentgruppe |"));
    }

    @Test
    @DisplayName("S32 / User: Sidebar-Kasten ohne eigene Heading bekommt Auge-Bezug 'Einsatz von Talenten'")
    void s32_sidebar_augeBezugAufEinsatzVonTalenten() throws Exception {
        TextInterpreter interpreter = renderPageWithBoxes(32);
        TextInterpreter.BoxRendering sidebar = interpreter.getLastBoxes().stream()
                .filter(b -> b.x > 400f && b.y < 100f)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Sidebar-Kasten fehlt"));
        assertEquals("Einsatz von Talenten", sidebar.headingGuess);
        assertTrue(sidebar.headingFromEyeRef);
    }

    @Test
    @DisplayName("S32 / User: Liste am Anfang des Body wird als Markdown-Liste erkannt (2 Items)")
    void s32_listeAmAnfang_zweiBulletItems() throws Exception {
        String md = renderPage(32);
        assertTrue(md.contains("- Für den Einsatz von Talenten"),
                "Erstes Item 'Für den Einsatz von Talenten' fehlt als Bullet");
        assertTrue(md.contains("- Selbst wenn ein Held"),
                "Zweites Item 'Selbst wenn ein Held' fehlt als Bullet");
    }

    @Test
    @DisplayName("S32 / User: Liste am Ende des Body (Routinevoraussetzungen) wird als Markdown-Liste erkannt")
    void s32_listeAmEnde_zweiBulletItems() throws Exception {
        String md = renderPage(32);
        assertTrue(md.contains("- Der Held muss in jeder Eigenschaft"),
                "Erstes Item der zweiten Liste fehlt");
        assertTrue(md.contains("- Zusätzlich muss er mindestens"),
                "Zweites Item der zweiten Liste fehlt");
    }

    @Test
    @DisplayName("S32 / User: Werkzeuge-Continuation in der Nachbarspalte wird NICHT faelschlich zur Liste")
    void s32_werkzeugeContinuation_keinFalschPositiv() throws Exception {
        String md = renderPage(32);
        assertFalse(md.contains("- machen. Welche Art"),
                "Werkzeuge-Werkfortsetzung darf nicht als Liste erscheinen");
        assertFalse(md.contains("- muss der Meister"),
                "Werkzeuge-Werkfortsetzung darf nicht als Liste erscheinen");
    }

    // ---------------------------------------------------------------------
    // Seite 33 — User-Reportagen
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("S33 / User: Qualitaetsstufen-Tabelle (einzeilig pro Zeile, 6 Reihen) korrekt")
    void s33_qualitaetsstufenTabelle_kompletteSechsZeilen() throws Exception {
        String md = renderPage(33);
        for (String row : new String[]{"| 0-3 | 1 |", "| 4-6 | 2 |", "| 7-9 | 3 |",
                                         "| 10-12 | 4 |", "| 13-15 | 5 |", "| 16+ | 6 |"}) {
            assertTrue(md.contains(row), "Qualitaetsstufen-Reihe fehlt: " + row);
        }
    }

    @Test
    @DisplayName("S33 / User: Routinevoraussetzungen-Tabelle inkl. Header korrekt")
    void s33_routineVoraussetzungen_mitHeader() throws Exception {
        String md = renderPage(33);
        assertTrue(md.contains("| Modifikator-Maximum | Fertigkeitswert-Minimum |"),
                "Routinevoraussetzungen-Header fehlt");
        for (String row : new String[]{"| +3 und höher | 1 |", "| +/–0 | 10 |", "| –3 | 19 |"}) {
            assertTrue(md.contains(row), "Routine-Reihe fehlt: " + row);
        }
    }

    @Test
    @DisplayName("S33 / User: Beispieltext fließt NICHT in Tabellenzellen rein")
    void s33_beispielTextNichtInTabellenzelle() throws Exception {
        String md = renderPage(33);
        // Fruehere Bugsymptome: "ihm einen Fertigkeitswert von 11" oder
        // "Da es für Helden" landeten in Tabellenzellen.
        assertFalse(md.contains("| ihm einen Fertigkeitswert"),
                "Beispieltext darf nicht in Tabellenzelle stehen");
        assertFalse(java.util.regex.Pattern.compile("\\|[^|]*Da es f.r Helden[^|]*\\|").matcher(md).find(),
                "Body-Continuation 'Da es für Helden' darf nicht in Tabellenzelle landen");
    }

    @Test
    @DisplayName("S33 / User: *Beispiel:*-Block ist durch Blank-Line vom vorigen Absatz getrennt")
    void s33_beispielBlock_blankLineVorBeispiel() throws Exception {
        String md = renderPage(33);
        // Vor "*Beispiel:" muss eine Leerzeile (also "\n\n") stehen.
        int idx = md.indexOf("*Beispiel:");
        assertTrue(idx > 0, "*Beispiel:* nicht im Output gefunden");
        // Mindestens \n\n unmittelbar vor der Italic-Zeile.
        String before = md.substring(0, idx);
        assertTrue(before.endsWith("\n\n") || before.endsWith("  \n\n"),
                "Vor *Beispiel:* fehlt eine Leerzeile — got tail: "
                        + before.substring(Math.max(0, before.length() - 20)));
    }

    // ---------------------------------------------------------------------
    // Seite 36 — User-Reportagen
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("S36 / User: Talent-Headings 'Fliegen' und 'Gaukeleien' liegen auf gleichem Heading-Level")
    void s36_talentHeadings_konsistenteEbene() throws Exception {
        String md = renderPage(36);
        assertTrue(md.contains("### Fliegen"),  "### Fliegen fehlt");
        assertTrue(md.contains("### Gaukeleien"), "### Gaukeleien fehlt");
        // Beide auf gleichem Level — keine andere Heading-Stufe (Zeilenanfang exakt
        // pruefen, nicht via contains weil "##" Substring von "###" ist).
        java.util.regex.Pattern h2Fliegen = java.util.regex.Pattern.compile("(?m)^## Fliegen\\b");
        java.util.regex.Pattern h2Gaukeleien = java.util.regex.Pattern.compile("(?m)^## Gaukeleien\\b");
        assertFalse(h2Fliegen.matcher(md).find(), "Fliegen darf nicht auf H2 sein");
        assertFalse(h2Gaukeleien.matcher(md).find(), "Gaukeleien darf nicht auf H2 sein");
    }

    @Test
    @DisplayName("S37 / User: Spaltentrennung saeubert Gaukeleien (links) und Klettern (rechts) sauber")
    void s37_spaltentrennung_gaukeleienUndKlettern() throws Exception {
        String md = renderPage(37);
        // Beide Talente haben eigene saubere Statlines, NICHT vermischt.
        assertTrue(md.contains("**Anwendungsgebiete:** Jonglieren, Possenreißen, Verstecktricks"),
                "Gaukeleien-Anwendungsgebiete fehlt oder vermischt");
        assertTrue(md.contains("**Anwendungsgebiete:** Baumklettern, Bergsteigen, Eisklettern, Fassadenklettern"),
                "Klettern-Anwendungsgebiete fehlt oder vermischt");
        // Keine Tabellen-Falsch-Positives durch Typesetting-Gaps.
        assertFalse(md.contains("| Anwendungsgebiete:"),
                "Statlines duerfen nicht in Pseudo-Tabellen-Cells erscheinen");
    }

    @Test
    @DisplayName("S36/S37 / User: 'Steigerungskosten:' wird als Bold gerendert, nicht als Heading")
    void s36_steigerungskosten_keinHeading() throws Exception {
        String md = renderPage(37);  // S37 enthaelt mehrere Steigerungskosten-Vorkommen
        for (String line : md.split("\n")) {
            assertFalse(line.matches("#{1,3}\\s+Steigerungskosten:.*"),
                    "Steigerungskosten darf kein Heading sein — got: " + line);
        }
        assertTrue(md.contains("**Steigerungskosten:**"),
                "Erwarte Steigerungskosten als Bold-Label");
    }

    // ---------------------------------------------------------------------
    // Seite 42 — User-Reportagen
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("S42 / User: Sidebar 'Viele Spieler...' bekommt Heading 'Gesellschaftstalente' aus Body-Naehe")
    void s42_sidebar_gesellschaftstalente() throws Exception {
        TextInterpreter interpreter = renderPageWithBoxes(42);
        TextInterpreter.BoxRendering sidebar = interpreter.getLastBoxes().stream()
                .filter(b -> b.x < 50f && b.y > 450f && b.y < 600f)
                .findFirst()
                .orElseThrow(() -> new AssertionError("S42-Sidebar fehlt"));
        assertEquals("Gesellschaftstalente", sidebar.headingGuess);
        assertTrue(sidebar.markdown.contains("Viele Spieler lassen sich"));
    }

    // ---------------------------------------------------------------------
    // S10 / S15 / S16 / S18 / S19 / S22 — User-Reportagen Phase 4.1
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("S10 / User: Body-Text wird trotz vollflaechigem rechtem-Spalten-Bild extrahiert")
    void s10_bodyTextWirdGerendert() throws Exception {
        String md = renderPage(10);
        // Auf S10 verdeckt ein 311×842pt-Image die rechte Spalte komplett.
        // Das Boltanspiel-Zitat (linke Spalte) muss trotzdem erscheinen.
        assertTrue(md.contains("Boltanspiel"),
                "S10-Body 'Das Boltanspiel folgt einfachen Regeln' fehlt komplett");
        assertTrue(md.contains("Halte dich an die Regeln"),
                "S10 Spielregel 1 fehlt");
    }

    @Test
    @DisplayName("S15 / User: Italic-Wort-Fortsetzung ueber Bindestrich-Umbruch joined")
    void s15_italicWortFortsetzung_joinsZuEinemWort() throws Exception {
        String md = renderPage(15);
        // "*Bestätigungs-*\n\n*wurf*" → "*Bestätigungswurf*"
        assertTrue(md.contains("*Bestätigungswurf*"),
                "Bestätigungswurf muss als ein Wort erscheinen, nicht als Bestätigungs-/wurf");
        // Kein Absatz-Break in der Mitte des Bestätigungswurf-Satzes
        assertFalse(md.contains("*Bestätigungs-*\n"),
                "Bindestrich-Umbruch darf keinen Absatz erzeugen");
    }

    @Test
    @DisplayName("S15 / User: 'Kritischer Erfolg' bleibt im Fliesstext, kein Absatz-Break danach")
    void s15_kritischerErfolg_inlineImFliesstext() throws Exception {
        String md = renderPage(15);
        // "ist ein *Kritischer Erfolg* zustande gekommen" — als zusammenhaengender Satz.
        assertTrue(md.contains("*Kritischer Erfolg* zustande gekommen"),
                "Kritischer Erfolg muss inline gefolgt von 'zustande gekommen' stehen");
    }

    @Test
    @DisplayName("S16 / User: Single-Row-'Tabelle' aus typesetting-Gaps wird NICHT als Tabelle gerendert")
    void s16_singleRowTabelle_alsFliesstext() throws Exception {
        String md = renderPage(16);
        // Frueheres Bug-Symptom: die Body-Zeile "Die Kampfregeln mit ihren Details
        // werden im Kapitel **Kampf** ab Seite **61** vorgestellt." erzeugte eine
        // 5-Cell-Tabelle mit leerem Header und einer Datenzeile.
        assertFalse(md.contains("|   |   |   |   |   |"),
                "Leere Header-Zeile aus Bold/Regular-Wechsel darf nicht als Tabelle erscheinen");
        // Stattdessen muss der Body als Fliesstext stehen
        assertTrue(md.contains("Die Kampfregeln mit ihren Details werden im Kapitel **Kampf** ab Seite **61** vorgestellt."),
                "Body-Saetze muessen als Fliesstext zusammenstehen");
    }

    @Test
    @DisplayName("S16 → S17 / User: Bildkommentare unterbrechen die Cross-Page-Liste nicht")
    void s16_bildkommentare_unterbrechenListeNicht(@TempDir Path tmp) throws IOException {
        // E2E ueber RegenerateBookText, weil der Effekt erst beim Volltext-Concat sichtbar ist.
        Path raw = tmp.resolve("raw");
        Files.createDirectory(raw);
        for (int p : new int[]{16, 17}) {
            Path src = RAW_DIR.resolve(String.format("page_%03d.json", p));
            Files.copy(src, raw.resolve(src.getFileName()));
        }
        Path out = tmp.resolve("out");
        Files.createDirectory(out);
        RegenerateBookText.regenerate(raw, out, "Schwertes");
        String volltext = Files.readString(out.resolve("result").resolve("_volltext.md"),
                StandardCharsets.UTF_8);

        // S16 endet mit Listen-Item-Fortsetzung "Hat ein". Direkt danach darf KEIN
        // <!-- Seite ... Bild ... -->-Kommentar stehen (das wuerde die Liste zerreissen).
        int idx = volltext.indexOf("Hat ein");
        assertTrue(idx > 0, "S16-Listen-Ende 'Hat ein' fehlt");
        String afterHatEin = volltext.substring(idx, Math.min(volltext.length(), idx + 200));
        assertFalse(afterHatEin.contains("<!-- Seite "),
                "Bildkommentar darf nicht direkt nach 'Hat ein' stehen — Liste wird unterbrochen");
    }

    @Test
    @DisplayName("S18 / User: 6. Qualitaetsstufen-Reihe (16+) hat keine Body-Text-Leakage")
    void s18_sechsteQualitaetsstufenReihe_keineLeakage() throws Exception {
        String md = renderPage(18);
        // Frueheres Bug: "| 16+ Probe mit 0 FP bestanden ... | 6 |"
        assertTrue(md.contains("| 16+ | 6 |"),
                "Letzte Reihe der Qualitaetsstufen-Tabelle muss '| 16+ | 6 |' sein");
        assertFalse(java.util.regex.Pattern.compile("\\| 16\\+ Probe mit").matcher(md).find(),
                "Body-Text 'Probe mit 0 FP bestanden' darf nicht in 16+-Cell stehen");
    }

    @Test
    @DisplayName("S19 / User: Mehrzeilige Header-Cell 'Wahrscheinlichkeit' wird zusammengefuehrt")
    void s19_multiLineHeaderCell_merged() throws Exception {
        String md = renderPage(19);
        // Frueheres Bug: "| Wurf | Wahrschein- | Prozent |\n|  | lichkeit |  |"
        assertTrue(md.contains("| Wurf | Wahrscheinlichkeit | Prozent |"),
                "Header-Cell 'Wahrscheinlichkeit' muss aus 'Wahrschein-' + 'lichkeit' zusammen sein");
        assertFalse(md.contains("|  | lichkeit |  |"),
                "Continuation-Zeile darf nicht als eigene Reihe erscheinen");
    }

    // ---------------------------------------------------------------------
    // S12 / S17 / S19 / S22 — Phase 4.2 User-Reportagen
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("S12 / User: Spaltenuebergreifende Headline 'Proben' bleibt EIN Heading")
    void s12_proben_einheading() throws Exception {
        String md = renderPage(12);
        assertTrue(md.contains("## Proben"),
                "'Proben' muss als ein H2-Heading erscheinen");
        // KEINE Aufteilung in '## Pro' + '## ben'
        assertFalse(md.matches("(?s).*## Pro\\s*\\n.*## ben.*"),
                "Heading darf nicht in 'Pro' und 'ben' zerrissen sein");
    }

    @Test
    @DisplayName("S17 / User: Aufzaehlungssymbole sind sichtbar (4 Items)")
    void s17_listenSymbole_vorhanden() throws Exception {
        String md = renderPage(17);
        // Mindestens 3 Bullet-Items auf S17 (Liste in linker Spalte)
        long bullets = java.util.regex.Pattern.compile("(?m)^- ").matcher(md).results().count();
        assertTrue(bullets >= 3, "Erwarte mind. 3 Bullet-Items, gefunden: " + bullets);
    }

    @Test
    @DisplayName("S19 / User: Wahrscheinlichkeitstabelle hat zwei Datenzeilen (Doppel + Dreifach)")
    void s19_wahrscheinlichkeitsTabelle_zweiDatenzeilen() throws Exception {
        String md = renderPage(19);
        assertTrue(md.contains("| Doppel-1/20 | je ca. 1 zu 140 | 0,71 % |"),
                "Doppel-1/20-Reihe fehlt");
        assertTrue(md.contains("| Dreifach-1/20 |"),
                "Dreifach-1/20-Reihe fehlt komplett");
    }

    @Test
    @DisplayName("S22 / User: Zwei Tabellen mit Heading dazwischen werden NICHT zusammengezogen")
    void s22_zweiTabellenMitHeadingDazwischen_getrennt() throws Exception {
        String md = renderPage(22);
        // Erste Tabelle: nur QS/Erfolg
        assertTrue(md.contains("| QS | Erfolg |"));
        assertTrue(md.contains("| 6 | Teilerfolg |"));
        assertTrue(md.contains("| 10 | Aufgabe erfüllt |"));
        // Heading dazwischen
        assertTrue(md.contains("Anzahl der erlaubten Proben"),
                "Heading 'Anzahl der erlaubten Proben' fehlt");
        // Zweite Tabelle: Anzahl/Herausforderung
        assertTrue(md.contains("| 5 | schwer |"));
        assertTrue(md.contains("| 7 | durchschnitt |"));
        assertTrue(md.contains("| 10 | leicht |"));
        // Negativ: Heading darf NICHT in einer Tabellenzelle landen
        assertFalse(md.matches("(?s).*\\|[^|]*Anzahl der erlaubten Proben[^|]*\\|.*\\|.*\\|.*Aufgabe erfüllt.*"),
                "'Anzahl der erlaubten Proben' darf nicht in QS-Tabellenzelle landen");
    }

    // ---------------------------------------------------------------------
    // S8 / S19 / S22 / TOC — Phase 4.3 User-Reportagen
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("S8 / User: Bold-Inline-Body-Text wird NICHT als Heading erkannt (Kodex-Reihe)")
    void s8_kodexReihe_keinFalschesHeading() throws Exception {
        // E2E ueber Volltext, weil die Stelle ueber 2 Seiten faellt und durch
        // Bindestrich-Joins entsteht.
        String md = renderPage(8);
        // Frueheres Bug-Symptom: "### dex der Magie , Kodex des Götterwirkens, Kodex des"
        // (mid-sentence body wurde wegen 92% bold als Heading promoviert).
        assertFalse(md.contains("### dex der Magie"),
                "Bindestrich-Wortfortsetzung 'dex der Magie' darf kein Heading sein");
        assertFalse(java.util.regex.Pattern.compile("(?m)^#{1,3}\\s+dex der ").matcher(md).find(),
                "Keine Heading-Promotion fuer Lowercase-Anfang");
    }

    @Test
    @DisplayName("S19 / User: Modifikator-Tabelle merged Multi-Line-Cells korrekt")
    void s19_modifikatorTabelle_multiLineCellsMerged() throws Exception {
        String md = renderPage(19);
        // Erwartete saubere Reihen — line-basierte Detection liefert die echte
        // 3-Spalten-Struktur (Modifikator | Bewertung | Klettern), Multi-line-Cells
        // werden ueber die horizontalen Trennlinien korrekt zusammengefasst.
        for (String row : new String[]{
                "| +5 | extrem leichte Probe | eher ein steiniger Hügel als eine Felswand |",
                "| +3 | sehr leichte Probe | Felswand enthält teilweise Stufen |",
                "| –1 | schwierige Probe | feuchte Moose an der Felswand |"}) {
            assertTrue(md.contains(row), "Multi-Line-Cell-Reihe fehlt: " + row);
        }
        // Fragmentartige "Probe"-Einzelzeilen darf es NICHT mehr geben
        assertFalse(md.contains("| Probe | gel als eine Felswand |"),
                "Continuation-Cell 'Probe' darf nicht als eigene Reihe bestehen");
    }

    @Test
    @DisplayName("S22 / User: Leerzeile zwischen Tabelle und folgendem Absatz")
    void s22_leerzeileNachTabelle() throws Exception {
        String md = renderPage(22);
        // Nach der letzten Tabellenzeile ("| 10 | leicht |") MUSS eine Leerzeile
        // stehen, sonst rendert der MD-Renderer den Folgeabsatz in die Cell.
        int idx = md.indexOf("| 10 | leicht |");
        assertTrue(idx > 0, "Tabellenzeile '| 10 | leicht |' fehlt");
        String after = md.substring(idx + "| 10 | leicht |".length());
        assertTrue(after.startsWith("\n\n") || after.startsWith("\r\n\r\n"),
                "Nach der Tabelle muss eine Leerzeile stehen — got: "
                        + after.substring(0, Math.min(20, after.length())));
    }

    @Test
    @DisplayName("TOC / User: TOC-Folgeseiten werden weggelassen, normaler Body bleibt")
    void toc_folgeseiten_weggelassen(@TempDir Path tmp) throws IOException {
        // E2E-Test: regenerate ueber den Volltext und pruefen, dass
        //  - INHALTSVERZEICHNIS-Hauptseite unterdrueckt
        //  - Folgeseiten (z. B. S6) ebenfalls unterdrueckt
        //  - aber S12 (mit "Proben" + Body) NICHT unterdrueckt
        Path raw = tmp.resolve("raw");
        Files.createDirectory(raw);
        for (int p : new int[]{5, 6, 12}) {
            Path src = RAW_DIR.resolve(String.format("page_%03d.json", p));
            if (Files.exists(src)) Files.copy(src, raw.resolve(src.getFileName()));
        }
        Path out = tmp.resolve("out");
        Files.createDirectory(out);
        RegenerateBookText.regenerate(raw, out, "Schwertes");
        String volltext = Files.readString(out.resolve("result").resolve("_volltext.md"),
                StandardCharsets.UTF_8);

        // S6 (TOC-Folgeseite) muss als Kommentar markiert sein
        assertTrue(volltext.contains("Inhaltsverzeichnis-Folgeseite weggelassen")
                        || volltext.contains("Inhaltsverzeichnis weggelassen"),
                "TOC-Filter muss greifen");
        // S12-Body muss durchkommen
        assertTrue(volltext.contains("## Proben") || volltext.contains("Probenwurf"),
                "Echter Body auf S12 darf nicht als TOC unterdrueckt werden");
    }

    // ---------------------------------------------------------------------
    // Phase 4.4 — Y-Spacing-basierte Multi-Line-Cell-Detection
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Y-Spacing / User: bimodal-spaced Tabelle erkennt Multi-Line-Cells, uniform-spaced bleibt unangetastet")
    void yspacing_bimodalVsUniform() throws Exception {
        // Modifikator-Tabelle (S19) hat bimodal Y-Spacings (12pt intra-cell, 15pt
        // zwischen Cells) — Multi-Line-Cells werden korrekt zusammengefasst.
        String md19 = renderPage(19);
        assertTrue(md19.contains("| +5 | extrem leichte Probe | eher ein steiniger Hügel als eine Felswand |"),
                "Bimodale Tabelle: 3-Spalten-Reihe mit gemergter Multi-Line-Cell");
        // QS-Tabelle (S33 / Phase 4 ursprünglich) hat UNIFORME Y-Spacings — alle 6
        // Reihen muessen separat bleiben.
        String md33 = renderPage(33);
        for (String row : new String[]{"| 0-3 | 1 |", "| 4-6 | 2 |", "| 7-9 | 3 |",
                                       "| 10-12 | 4 |", "| 13-15 | 5 |", "| 16+ | 6 |"}) {
            assertTrue(md33.contains(row),
                    "Uniform-spaced Tabelle: '" + row + "' darf nicht falsch gemergt werden");
        }
    }

    // ---------------------------------------------------------------------
    // Soft-Break-Regel & Page-Break — User-Reportagen
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Soft-Break / User: Seitenende ohne Satzzeichen erzeugt KEINEN Soft-Break (FW)")
    void s32Ende_ohneSatzzeichen_keinSoftBreak(@TempDir Path tmp) throws IOException {
        // E2E ueber RegenerateBookText, weil der Effekt erst beim Volltext-Concat sichtbar ist.
        Path raw = tmp.resolve("raw");
        Files.createDirectory(raw);
        for (int p : new int[]{32, 33}) {
            Path src = RAW_DIR.resolve(String.format("page_%03d.json", p));
            Files.copy(src, raw.resolve(src.getFileName()));
        }
        Path out = tmp.resolve("out");
        Files.createDirectory(out);
        RegenerateBookText.regenerate(raw, out, "Schwertes");
        String volltext = Files.readString(out.resolve("result").resolve("_volltext.md"),
                StandardCharsets.UTF_8);

        // S32 endet mit "...muss auch der FW" (kein Punkt). Im Volltext darf hier
        // KEIN Soft-Break (zwei Spaces) stehen — sonst erzeugt der MD-Renderer
        // einen ungewollten Zeilenumbruch mitten im Satz.
        int idx = volltext.indexOf("muss auch der FW");
        assertTrue(idx > 0, "FW-Stelle fehlt");
        // Nach "FW" kommen optional whitespaces und dann ein \n
        String after = volltext.substring(idx + "muss auch der FW".length());
        assertFalse(after.startsWith("  \n"),
                "Soft-Break (zwei Spaces) am Seitenende ohne Satzzeichen ist verboten");
        assertTrue(after.startsWith("\n"),
                "Erwarte einfaches \\n direkt nach 'FW' — got: " + after.substring(0, 5));
    }

    @Test
    @DisplayName("Soft-Break / User: Satzende mit Punkt erzeugt einen Soft-Break")
    void absatzMitPunkt_erzeugtSoftBreak() throws Exception {
        String md = renderPage(32);
        // "Routineproben muessen nicht ausgewuerfelt werden..." endet mit Punkt
        // an mind. einer Stelle. Wir testen generell: irgendwo im Body steht
        // ein "  \n" nach einem Punkt — die Regel wirkt ueberhaupt.
        assertTrue(md.contains(".  \n"),
                "Soft-Break nach Punkt muss irgendwo auftreten");
        // Negativ-Probe: Soft-Break direkt nach einem Buchstaben (ohne Satzzeichen)
        // darf NICHT vorkommen.
        assertFalse(md.matches("(?s).*[a-zäöüß]  \\n.*"),
                "Soft-Break nach Buchstabe (ohne Satzzeichen) ist verboten");
    }
}
