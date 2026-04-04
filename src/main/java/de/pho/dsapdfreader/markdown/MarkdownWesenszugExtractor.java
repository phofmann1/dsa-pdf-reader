package de.pho.dsapdfreader.markdown;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extrahiert Wesenszüge aus Markdown-Seitentexten.
 * Wesenszüge sind kulturelle Eigenarten (Fokusregel Stufe I, Kulturen).
 *
 * Jeder Wesenszug hat:
 * - Name + Stufe (I oder II)
 * - 2-3 Traits (positive + negative Eigenart)
 * - Voraussetzungen
 * - Optional: Kombinierbar mit (bei Stufe II)
 * - AP-Wert (meist 0)
 */
public class MarkdownWesenszugExtractor
{
    private static final Logger LOGGER = LogManager.getLogger();
    private int nextKey = 0;

    /**
     * Extrahiert Wesenszüge seitenweise statt aus dem gesamten Text.
     * Nur Seiten die "Wesenszug" oder "(Stufe I" enthalten werden analysiert.
     */
    public List<Map<String, Object>> extractWesenszuege(List<String> pageTexts, String publication)
    {
        List<Map<String, Object>> result = new ArrayList<>();

        // Nur relevante Seiten sammeln
        StringBuilder relevantText = new StringBuilder();
        for (String page : pageTexts)
        {
            if (page.contains("Stufe I") || page.contains("Stufe II") || page.contains("Wesenszug"))
            {
                // Nur Spalte 2 wenn vorhanden, sonst ganzer Text (Wesenszuege stehen oft in Spalte 1)
                relevantText.append(page).append("\n\n---PAGE_BREAK---\n\n");
            }
        }

        String text = relevantText.toString();
        if (!text.contains("Wesenszug") && !text.contains("Wesenszüg"))
        {
            return result;
        }

        // Strategie: Zuerst alle "(Stufe I)" und "(Stufe II)" Positionen finden,
        // dann die Bloecke zwischen diesen Positionen parsen
        Pattern headerPattern = Pattern.compile(
            "(?:^|\\n)(?:#{2,3}\\s+)?(?:\\*\\*)?([^*\\n(]{3,80})\\(Stufe\\s+(I{1,2})\\)(?:\\*\\*)?",
            Pattern.MULTILINE);

        List<int[]> headerPositions = new ArrayList<>(); // [start, end, stufe]
        List<String> headerNames = new ArrayList<>();

        Matcher hm = headerPattern.matcher(text);
        while (hm.find())
        {
            String name = hm.group(1).replaceAll("\\*", "").trim();
            // Ueberspringen wenn es kein echter Wesenszug-Name ist
            if (name.startsWith("Wesenszüge") || name.startsWith("Wesenszug")
                || name.length() < 3 || name.contains("Fokusregel"))
            {
                continue;
            }
            headerPositions.add(new int[]{hm.start(), hm.end(), hm.group(2).equals("II") ? 2 : 1});
            headerNames.add(name);
        }

        // Bloecke extrahieren: von einem Header bis zum naechsten (oder max 2000 Zeichen)
        for (int i = 0; i < headerPositions.size(); i++)
        {
            int bodyStart = headerPositions.get(i)[1];
            int bodyEnd;
            if (i + 1 < headerPositions.size())
            {
                bodyEnd = headerPositions.get(i + 1)[0];
            }
            else
            {
                bodyEnd = Math.min(bodyStart + 2000, text.length());
            }

            // Zusaetzlich begrenzen bei PAGE_BREAK nach dem aktuellen Block (wenn kein naechster Header auf gleicher Seite)
            String body = text.substring(bodyStart, bodyEnd);

            // Bei AP-Wert abschneiden (markiert Ende eines Wesenszug-Blocks)
            int apEnd = findApWertEnd(body);
            if (apEnd > 0)
            {
                body = body.substring(0, apEnd);
            }

            // Traits extrahieren
            List<Map<String, String>> traits = extractTraits(body);

            // Validierung: echte Wesenszuege haben 1-5 Traits
            if (traits.isEmpty() || traits.size() > 5)
            {
                LOGGER.debug("  Wesenszug uebersprungen ({}): {} ({} Traits)",
                    traits.size(), headerNames.get(i), traits.size());
                continue;
            }

            String name = headerNames.get(i);
            int stufe = headerPositions.get(i)[2];

            String prerequisites = extractPrerequisites(body);
            String combinable = extractCombinable(body);

            Map<String, Object> wesenszug = new LinkedHashMap<>();
            wesenszug.put("key", nextKey);
            wesenszug.put("name", name);
            wesenszug.put("stufe", stufe);
            wesenszug.put("traits", traits);
            wesenszug.put("prerequisites", prerequisites != null ? prerequisites : "");
            if (combinable != null) wesenszug.put("combinableWithText", combinable);
            wesenszug.put("apCost", 0);
            wesenszug.put("publication", publication);

            result.add(wesenszug);
            nextKey++;
            LOGGER.info("  Wesenszug extrahiert: {} (Stufe {}, {} Traits)", name, stufe, traits.size());
        }

        return result;
    }

    public int getNextKey() { return nextKey; }
    public void setNextKey(int key) { this.nextKey = key; }

    /**
     * Findet das Ende des AP-Wert-Blocks (markiert Ende eines Wesenszugs).
     */
    private int findApWertEnd(String body)
    {
        // Suche "AP-Wert: 0" oder "AP-Wert:** 0"
        Pattern apPattern = Pattern.compile("AP-Wert[*:\\s]*0\\s*");
        Matcher m = apPattern.matcher(body);
        if (m.find())
        {
            return m.end();
        }
        return -1;
    }

    private List<Map<String, String>> extractTraits(String body)
    {
        List<Map<String, String>> traits = new ArrayList<>();

        // Trait-Pattern: **TraitName:** regel-text
        // Stoppt vor dem naechsten Bold-Label, Voraussetzungen, Kombinierbar, AP-Wert, oder Seitenende
        Pattern traitPattern = Pattern.compile(
            "\\*\\*([A-ZÄÖÜ][^*:]{2,40}?):\\*\\*\\s*(.+?)(?=\\*\\*[A-ZÄÖÜ][^*]*?:\\*\\*|---PAGE_BREAK|$)",
            Pattern.DOTALL);
        Matcher traitMatcher = traitPattern.matcher(body);

        while (traitMatcher.find())
        {
            String traitName = traitMatcher.group(1).trim();
            String rule = traitMatcher.group(2).trim();

            // Meta-Felder ueberspringen
            if (traitName.equals("Voraussetzungen") || traitName.startsWith("Kombinierbar")
                || traitName.equals("AP-Wert") || traitName.equals("Regel")
                || traitName.equals("Regel") || traitName.equals("Größe")
                || traitName.equals("Gewicht") || traitName.equals("Typus"))
            {
                continue;
            }

            // Bereinigen
            rule = rule.replaceAll("(\\w)-\\s+(\\w)", "$1$2");
            rule = rule.replaceAll("\\s+", " ").trim();
            rule = rule.replaceAll("\\*{1,3}", "");
            // Abschneiden bei Meta-Feldern falls mitgefangen
            rule = rule.replaceAll("\\s*Voraussetzungen:.*", "");
            rule = rule.replaceAll("\\s*Kombinierbar mit:.*", "");
            rule = rule.replaceAll("\\s*AP-Wert:.*", "");
            rule = rule.replaceAll("\\s*---PAGE_BREAK.*", "");
            rule = rule.trim();

            if (!traitName.isEmpty() && rule.length() > 10)
            {
                Map<String, String> trait = new LinkedHashMap<>();
                trait.put("name", traitName);
                trait.put("rule", rule);
                traits.add(trait);
            }
        }

        return traits;
    }

    private String extractPrerequisites(String body)
    {
        Pattern p = Pattern.compile("\\*\\*Voraussetzungen?:\\*\\*\\s*(.+?)(?=\\*\\*[A-ZÄÖÜ]|---PAGE|$)",
            Pattern.DOTALL);
        Matcher m = p.matcher(body);
        if (m.find())
        {
            return m.group(1).replaceAll("\\*", "").replaceAll("\\s+", " ").trim();
        }
        return null;
    }

    private String extractCombinable(String body)
    {
        Pattern p = Pattern.compile("\\*\\*Kombinierbar\\s+mit:\\*\\*\\s*(.+?)(?=\\*\\*[A-ZÄÖÜ]|---PAGE|$)",
            Pattern.DOTALL);
        Matcher m = p.matcher(body);
        if (m.find())
        {
            return m.group(1).replaceAll("\\*", "").replaceAll("\\s+", " ").trim();
        }
        return null;
    }

    /**
     * Normalisiert einen Wesenszug-Namen zu einem Enum-Key.
     */
    public static String normalizeKey(String name)
    {
        if (name == null) return null;
        return name.toLowerCase()
            .replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").replace("ß", "ss")
            .replace("/", "_").replace("-", "_")
            .replaceAll("[^a-z0-9_\\s]", "")
            .trim()
            .replaceAll("\\s+", "_")
            .replaceAll("_+", "_");
    }
}
