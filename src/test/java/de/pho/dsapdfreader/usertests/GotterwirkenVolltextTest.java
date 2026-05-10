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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Volltext-basierte Acceptance-Tests fuer den Kodex des Goetterwirkens.
 * Pro vom User reportiertem Problem ein eigener Test, der den korrigierten
 * Stand konserviert. Tests werden uebersprungen, wenn der Volltext fehlt.
 */
class GotterwirkenVolltextTest {

    private static final Path VOLLTEXT = Path.of(
            "export/markdown/text/01 - Regeln/"
                    + "Kodex des Gotterwirkens (174)/Kodex des Gotterwirkens - 01 - Regeln/"
                    + "result/_volltext.md");

    private static String volltext;

    @BeforeAll
    static void loadVolltext() throws IOException {
        Assumptions.assumeTrue(Files.exists(VOLLTEXT),
                "Volltext fehlt unter " + VOLLTEXT
                        + " — RegenerateText laufen lassen.");
        volltext = Files.readString(VOLLTEXT, StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private static void assertContainsLine(String label, String expectedLine) {
        if (volltext.contains("\n" + expectedLine + "\n")
                || volltext.startsWith(expectedLine + "\n")) return;
        throw new AssertionError(label + " — Zeile fehlt im Volltext:\n" + expectedLine);
    }

    // -------------------------------------------------------------------
    // Heading-Level-Mapping — bis zu 6 # verfuegbar; Body-Bold-Subentries
    // landen auf #### statt ###, Display-Headings bleiben ## bzw. ###
    // -------------------------------------------------------------------

    @Test
    @DisplayName("S261: Liturgiestil-Sektion bleibt ##, Apricarier wird ####")
    void s261_headingLevels_subSectionAsH4() {
        assertContainsLine("S261 H2", "## Liturgiestilsonderfertigkeiten");
        assertContainsLine("S261 H2", "## Praios-Strömungen");
        assertContainsLine("S261 H4", "#### Apricarier");
        assertContainsLine("S261 H4", "#### Braniborier");
    }

    @Test
    @DisplayName("S293: ## Praios + #### Sonnengruß als sauber abgestufte Headings")
    void s293_headingLevels_gebeteUndGesange() {
        assertContainsLine("S293 H2", "## Gebete und Gesänge");
        assertContainsLine("S293 H2", "## Praios");
        assertContainsLine("S293 H4", "#### Sonnengruß");
        assertContainsLine("S293 H4", "#### Lied zum Lob des Herrn Praios");
        assertContainsLine("S293 H4", "#### Die güldene Sonne");
    }

    @Test
    @DisplayName("Body-Bold-Attribute mit Doppelpunkt (z. B. **AP-Wert:**) bleiben KEIN Heading")
    void boldAttribute_isNoHeading() {
        // **AP-Wert:** kommt im Buch hunderte Male vor; darf NIE als Heading
        // promoviert werden — sonst ist das Mapping kaputt.
        assertFalse(volltext.contains("\n#### AP-Wert:"),
                "**AP-Wert:** darf kein H4-Heading sein");
        assertFalse(volltext.contains("\n##### AP-Wert:"),
                "**AP-Wert:** darf kein H5-Heading sein");
        assertFalse(volltext.contains("\n###### AP-Wert:"),
                "**AP-Wert:** darf kein H6-Heading sein");
    }

    // -------------------------------------------------------------------
    // Folgende Tests dokumentieren noch offene Probleme. Sie sind aktuell
    // disabled und werden gruen, sobald der jeweilige Bugfix kommt.
    // -------------------------------------------------------------------

    @Test
    @DisplayName("S251: Banner-Heading wird zu einer Zeile gemergt")
    void s251_bannerHeading_oneLine() {
        assertContainsLine("S251 merged", "# KAPITEL 4: KARMALE SONDERFERTIGKEITEN");
        assertFalse(volltext.contains("\n# KAPITEL 4: KARMALE\n# SONDERFERTIGKEITEN\n"),
                "Banner darf nicht als 2 H1-Zeilen erscheinen");
    }

    @Test
    @DisplayName("S293: Cross-column ## Praios kommt VOR den ersten Sonnengruß")
    void s293_praiosHeading_beforeSonnengrus() {
        int praiosIdx = volltext.indexOf("## Praios\n");
        int sonnengrusIdx = volltext.indexOf("#### Sonnengruß\n");
        assertTrue(praiosIdx >= 0, "## Praios fehlt");
        assertTrue(sonnengrusIdx >= 0, "Sonnengruß fehlt");
        assertTrue(praiosIdx < sonnengrusIdx,
                "## Praios muss vor #### Sonnengruß stehen — got praios=" + praiosIdx + " sonnengrus=" + sonnengrusIdx);
    }

    @Test
    @DisplayName("S425: Orphan-Heading (Body endet mit '#### Rüstungen') wird in die Box ausgelagert")
    void s425_orphanHeading_movedToBox() throws IOException {
        // Body soll NICHT mit der orphan Rüstungen-Heading enden
        Path bodyFile = Path.of(
                "export/markdown/text/01 - Regeln/"
                        + "Kodex des Gotterwirkens (174)/Kodex des Gotterwirkens - 01 - Regeln/"
                        + "raw/seite_425.md");
        Assumptions.assumeTrue(Files.exists(bodyFile));
        String body = Files.readString(bodyFile, StandardCharsets.UTF_8).replace("\r\n", "\n");
        assertFalse(body.endsWith("#### Rüstungen\n") || body.endsWith("#### Rüstungen"),
                "Body von S425 darf nicht mit orphan-Heading '#### Rüstungen' enden");
        // Die Box muss existieren und den Heading-Titel tragen
        Path boxFile = Path.of(
                "export/markdown/text/01 - Regeln/"
                        + "Kodex des Gotterwirkens (174)/Kodex des Gotterwirkens - 01 - Regeln/"
                        + "result/425 - Rüstungen.md");
        assertTrue(Files.exists(boxFile),
                "Box-Datei '425 - Rüstungen.md' muss existieren");
    }

    @Test
    @DisplayName("S400: Sikaryan-Verlust-Stufen I/II/III in linker Spalte am Ende (vor Stufe IV)")
    void s400_sikaryanVerlust_table_partial() {
        // Stufen I/II/III gehoeren in EINE zusammenhaengende Tabelle.
        assertContainsLine("S400 Stufe I",
                "| Stufe I | –1 LeP bei Regenerationsphasen (bis zum Minimum +0 LeP in dieser Regenerationsphase); Vampire bekommen zusätzlich die Schlechte Eigenschaft Sikaryan-Durst |");
        assertContainsLine("S400 Stufe II",
                "| Stufe II | –2 LeP bei Regenerationsphasen (bis zum Minimum +0 LeP in dieser Regenerationsphase); Proben auf Willenskraft bei Sikaryan-Durst sind um –2 erschwert |");
        assertContainsLine("S400 Stufe III",
                "| Stufe III | –3 LeP bei Regenerationsphasen (bis zum Minimum +0 LeP in dieser Regenerationsphase); Proben auf Willenskraft bei Sikaryan-Durst sind um –4 erschwert |");
        // Body-Bleeding: "**Stufe II** ..." darf nicht als Bold-Fliesstext erscheinen
        assertFalse(volltext.contains("**Stufe II** –2 LeP bei Regenerationsphasen"),
                "Stufe II darf nicht als Bold-Body-Fliesstext erscheinen — nur in Tabelle");
        assertFalse(volltext.contains("**Stufe III** –3 LeP bei Regenerationsphasen"),
                "Stufe III darf nicht als Bold-Body-Fliesstext erscheinen — nur in Tabelle");
        // Lesreihenfolge: Stufen I/II/III (linke Spalte unten) MUESSEN vor Stufe IV
        // (rechte Spalte oben) stehen. Sonst wird die Tabelle in der Mitte durch
        // Right-Column-Inhalt unterbrochen.
        int idxStufeIII = volltext.indexOf("| Stufe III | –3 LeP");
        int idxStufeIV = volltext.indexOf("| Stufe IV | Sikaryan erschöpft");
        assertTrue(idxStufeIII > 0 && idxStufeIV > 0 && idxStufeIII < idxStufeIV,
                "Stufen I/II/III muessen vor Stufe IV stehen (Lesreihenfolge linke Spalte → rechte Spalte)");
    }

    @Disabled("Offen: Stufe IV-Reihe steht im rechten Spalten-Top und ist von I/II/III getrennt — braucht cross-column-Tabellen-Merge")
    @Test
    @DisplayName("S400: Stufe IV in derselben Tabelle wie I/II/III")
    void s400_sikaryanVerlust_stufeIV() {
        // Stufe IV-Inhalt darf nicht "über LeP \n verfügt; erleidet ..." als
        // Body-Wrap zwischen den Tabellen liegen.
        int idxStufeIVTab = volltext.indexOf("| Stufe IV |");
        int idxVerfuegt = volltext.indexOf("verfügt; erleidet ein Sikaryan-Räuber");
        assertTrue(idxStufeIVTab > 0 && idxStufeIVTab < idxVerfuegt,
                "Stufe IV soll in der Tabelle vor dem Body-Wrap stehen");
    }

    @Test
    @DisplayName("S420: Echsenschuppen-Zeile mergt sinde-Wrap in Cell 1, Eigenblut bleibt eigene Reihe")
    void s420_echsenschuppen_complete() {
        // Wrap-Zeile "sinde, Nandus)" landet in Cell 1 (mit Bindestrich-Merge: He- + sinde → Hesinde)
        assertContainsLine("S420 Echsen",
                "| Echsenoder Schlangenschuppen (Hesinde, Nandus) | 1 Strukturpunkt | – | 1-5 K |");
        // Eigenblut MUSS eine separate Reihe sein, nicht mit Echsen merged
        assertContainsLine("S420 Eigenblut",
                "| Eigenblut (Rondra, Kor) | 1 Strukturpunkt | – | meist gratis |");
        // Rauschkraeuter-Wrap "viele Schamanen)" mergt analog
        assertContainsLine("S420 Rauschkraeuter",
                "| Rauschkräuter (Boron, Marbo, Levthan, viele Schamanen) | 1 Strukturpunkt | – | 0,1-1 S |");
    }
}
