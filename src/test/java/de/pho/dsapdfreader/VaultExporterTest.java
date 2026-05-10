package de.pho.dsapdfreader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-Tests fuer die {@link VaultExporter}-Helfer:
 * Banner-Title-Validation, Heading-Filter, Section-Hierarchie-Aufbau aus
 * Markdown.
 */
class VaultExporterTest {

    // ---------- validateBannerTitle ----------------------------------------

    @Test
    @DisplayName("Banner: 'Land & Leute' wird akzeptiert")
    void banner_landUndLeute() {
        assertTrue(VaultExporter.validateBannerTitle("Land & Leute"));
    }

    @Test
    @DisplayName("Banner: 'Glaube im Wolfsfrost' wird akzeptiert (mit Connector 'im')")
    void banner_mitConnector() {
        assertTrue(VaultExporter.validateBannerTitle("Glaube im Wolfsfrost"));
    }

    @Test
    @DisplayName("Banner: 'Mythos & Historie' wird akzeptiert")
    void banner_mythosHistorie() {
        assertTrue(VaultExporter.validateBannerTitle("Mythos & Historie"));
    }

    @Test
    @DisplayName("Banner: einzelnes Wort wird abgelehnt")
    void banner_singleWord() {
        assertFalse(VaultExporter.validateBannerTitle("Land"));
    }

    @Test
    @DisplayName("Banner: lowercase-Anfang wird abgelehnt")
    void banner_lowerCaseFirst() {
        assertFalse(VaultExporter.validateBannerTitle("land & Leute"));
    }

    @Test
    @DisplayName("Banner: zu viele Woerter werden abgelehnt")
    void banner_tooManyWords() {
        assertFalse(VaultExporter.validateBannerTitle("Eins Zwei Drei Vier Fuenf Sechs"));
    }

    // ---------- isRealHeading ----------------------------------------------

    @Test
    @DisplayName("Heading-Filter: H1 mit '*' (Italic-Listenitem) wird abgelehnt")
    void heading_italicH1() {
        var m = java.util.regex.Pattern.compile("^(#{1,6})\\s+(.+?)\\s*$")
                .matcher("# Aino Kallas – *The Wolfs Bride*");
        assertTrue(m.matches());
        assertFalse(VaultExporter.isRealHeading("# Aino Kallas – *The Wolfs Bride*", m, 0));
    }

    @Test
    @DisplayName("Heading-Filter: H1 nach H4+ ist struktureller Bruch")
    void heading_h1AfterH4() {
        var m = java.util.regex.Pattern.compile("^(#{1,6})\\s+(.+?)\\s*$")
                .matcher("# 1020 BF suchte die Lichtvogel-Expedition");
        assertTrue(m.matches());
        assertFalse(VaultExporter.isRealHeading("# 1020 BF suchte die Lichtvogel-Expedition", m, 4));
    }

    @Test
    @DisplayName("Heading-Filter: H1 'KAPITEL 2: TALENTE' wird auch nach H4 akzeptiert")
    void heading_chapterAfterH4() {
        var m = java.util.regex.Pattern.compile("^(#{1,6})\\s+(.+?)\\s*$")
                .matcher("# KAPITEL 2:  TALENTE");
        assertTrue(m.matches());
        assertTrue(VaultExporter.isRealHeading("# KAPITEL 2:  TALENTE", m, 4));
    }

    @Test
    @DisplayName("Heading-Filter: H1 ALL-CAPS 'ERZDÄMONEN' wird auch nach H4 akzeptiert")
    void heading_allCapsAfterH4() {
        var m = java.util.regex.Pattern.compile("^(#{1,6})\\s+(.+?)\\s*$")
                .matcher("# ERZDÄMONEN");
        assertTrue(m.matches());
        assertTrue(VaultExporter.isRealHeading("# ERZDÄMONEN", m, 4));
    }

    @Test
    @DisplayName("Heading-Filter: H2 'Geographie' ist echt")
    void heading_legitH2() {
        var m = java.util.regex.Pattern.compile("^(#{1,6})\\s+(.+?)\\s*$")
                .matcher("## Geographie");
        assertTrue(m.matches());
        assertTrue(VaultExporter.isRealHeading("## Geographie", m, 0));
    }

    // ---------- parseSections (Banner-Promotion) ---------------------------

    @Test
    @DisplayName("parseSections: Banner-Body promotet zu virtueller H1, folgende H2 werden Kinder")
    void parseSections_bannerHierarchy() {
        String volltext = String.join("\n",
                "# Quelle: irgendwo",
                "## Vorwort",
                "vorwort body",
                "## Weg & Steg",
                "weg-body",
                "Land & Leute *agelang reisten wir durch...*",
                "## Der Nornja",
                "nornja body",
                "## Die Brydia",
                "brydia body");
        VaultExporter.Section root = VaultExporter.parseSections(volltext);
        // Erwartet: 'Weg & Steg' (H2), dann 'Land & Leute' (virtual H1) mit
        // Children 'Der Nornja' und 'Die Brydia'
        assertNotNull(root);
        assertEquals(2, root.children.size(),
                "Top-Level: Weg & Steg + Land & Leute");
        VaultExporter.Section wegUndSteg = root.children.get(0);
        assertEquals("Weg & Steg", wegUndSteg.title);
        VaultExporter.Section landLeute = root.children.get(1);
        assertEquals("Land & Leute", landLeute.title);
        assertEquals(1, landLeute.level, "Banner ist H1");
        assertEquals(2, landLeute.children.size(),
                "Land & Leute hat Der Nornja + Die Brydia als Kinder");
        assertEquals("Der Nornja", landLeute.children.get(0).title);
        assertEquals("Die Brydia", landLeute.children.get(1).title);
    }

    // ---------- categoryPrefix ---------------------------------------------

    @Test
    @DisplayName("categoryPrefix: '04 - Regionen' → '04'")
    void categoryPrefix_normalCase() {
        assertEquals("04", VaultExporter.categoryPrefix("04 - Regionen"));
    }

    @Test
    @DisplayName("categoryPrefix: ohne Prefix → null")
    void categoryPrefix_noPrefix() {
        org.junit.jupiter.api.Assertions.assertNull(
                VaultExporter.categoryPrefix("Florian Don-Schauen"));
    }

    // ---------- stripBookKey -----------------------------------------------

    @Test
    @DisplayName("stripBookKey: 'Der Wolfsfrost (230)' → 'Der Wolfsfrost'")
    void stripBookKey_normal() {
        assertEquals("Der Wolfsfrost",
                VaultExporter.stripBookKey("Der Wolfsfrost (230)"));
    }
}
