package de.pho.dsapdfreader.markdown;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extrahiert Profile (Statblocks) aus Markdown-Seiten im DSA-Format.
 * Loest Keys fuer Boons, Abilities und Varianten auf, soweit moeglich.
 */
public class MarkdownProfileExtractor
{
    private static final Logger LOGGER = LogManager.getLogger();

    // Lookup-Maps, geladen aus JSON-Ressourcen
    private final Map<String, String> boonNameToKey;
    private final Map<String, String> abilityNameToKey;
    private final Map<String, String> boonVariantToKey;

    // Skill-Name -> SkillKey
    private static final Map<String, String> SKILL_KEY_MAP = new HashMap<>();

    static
    {
        SKILL_KEY_MAP.put("Fliegen", "fliegen");
        SKILL_KEY_MAP.put("Gaukeleien", "gaukeleien");
        SKILL_KEY_MAP.put("Klettern", "klettern");
        SKILL_KEY_MAP.put("Körperbeherrschung", "körperbeherrschung");
        SKILL_KEY_MAP.put("Kraftakt", "kraftakt");
        SKILL_KEY_MAP.put("Reiten", "reiten");
        SKILL_KEY_MAP.put("Schwimmen", "schwimmen");
        SKILL_KEY_MAP.put("Selbstbeherrschung", "selbstbeherrschung");
        SKILL_KEY_MAP.put("Singen", "singen");
        SKILL_KEY_MAP.put("Sinnesschärfe", "sinnesschärfe");
        SKILL_KEY_MAP.put("Tanzen", "tanzen");
        SKILL_KEY_MAP.put("Taschendiebstahl", "taschendiebstahl");
        SKILL_KEY_MAP.put("Verbergen", "verbergen");
        SKILL_KEY_MAP.put("Zechen", "zechen");
        SKILL_KEY_MAP.put("Bekehren & Überzeugen", "bekehren_und_überzeugen");
        SKILL_KEY_MAP.put("Betören", "betören");
        SKILL_KEY_MAP.put("Einschüchtern", "einschüchtern");
        SKILL_KEY_MAP.put("Etikette", "etikette");
        SKILL_KEY_MAP.put("Gassenwissen", "gassenwissen");
        SKILL_KEY_MAP.put("Menschenkenntnis", "menschenkenntnis");
        SKILL_KEY_MAP.put("Überreden", "überreden");
        SKILL_KEY_MAP.put("Verkleiden", "verkleiden");
        SKILL_KEY_MAP.put("Willenskraft", "willenskraft");
        SKILL_KEY_MAP.put("Fährtensuchen", "fährtensuchen");
        SKILL_KEY_MAP.put("Fesseln", "fesseln");
        SKILL_KEY_MAP.put("Fischen & Angeln", "fischen_und_angeln");
        SKILL_KEY_MAP.put("Orientierung", "orientierung");
        SKILL_KEY_MAP.put("Pflanzenkunde", "pflanzenkunde");
        SKILL_KEY_MAP.put("Tierkunde", "tierkunde");
        SKILL_KEY_MAP.put("Wildnisleben", "wildnisleben");
        SKILL_KEY_MAP.put("Brett- & Glücksspiel", "brett_und_glücksspiel");
        SKILL_KEY_MAP.put("Geographie", "geographie");
        SKILL_KEY_MAP.put("Geschichtswissen", "geschichtswissen");
        SKILL_KEY_MAP.put("Götter & Kulte", "götter_und_kulte");
        SKILL_KEY_MAP.put("Kriegskunst", "kriegskunst");
        SKILL_KEY_MAP.put("Magiekunde", "magiekunde");
        SKILL_KEY_MAP.put("Mechanik", "mechanik");
        SKILL_KEY_MAP.put("Rechnen", "rechnen");
        SKILL_KEY_MAP.put("Rechtskunde", "rechtskunde");
        SKILL_KEY_MAP.put("Sagen & Legenden", "sagen_und_legenden");
        SKILL_KEY_MAP.put("Sphärenkunde", "sphärenkunde");
        SKILL_KEY_MAP.put("Sternkunde", "sternkunde");
        SKILL_KEY_MAP.put("Alchimie", "alchimie");
        SKILL_KEY_MAP.put("Boote & Schiffe", "boote_und_schiffe");
        SKILL_KEY_MAP.put("Fahrzeuge", "fahrzeuge");
        SKILL_KEY_MAP.put("Handel", "handel");
        SKILL_KEY_MAP.put("Heilkunde Gift", "heilkunde_gift");
        SKILL_KEY_MAP.put("Heilkunde Krankheiten", "heilkunde_krankheiten");
        SKILL_KEY_MAP.put("Heilkunde Seele", "heilkunde_seele");
        SKILL_KEY_MAP.put("Heilkunde Wunden", "heilkunde_wunden");
        SKILL_KEY_MAP.put("Holzbearbeitung", "holzbearbeitung");
        SKILL_KEY_MAP.put("Lebensmittelbearbeitung", "lebensmittelbearbeitung");
        SKILL_KEY_MAP.put("Lederbearbeitung", "lederbearbeitung");
        SKILL_KEY_MAP.put("Malen & Zeichnen", "malen_und_zeichnen");
        SKILL_KEY_MAP.put("Metallbearbeitung", "metallbearbeitung");
        SKILL_KEY_MAP.put("Musizieren", "musizieren");
        SKILL_KEY_MAP.put("Schlösserknacken", "schlösserknacken");
        SKILL_KEY_MAP.put("Steinbearbeitung", "steinbearbeitung");
        SKILL_KEY_MAP.put("Stoffbearbeitung", "stoffbearbeitung");
    }

    // Gesammelte unaufgeloeste Keys
    private final List<String> unresolvedBoons = new ArrayList<>();
    private final List<String> unresolvedAbilities = new ArrayList<>();

    public MarkdownProfileExtractor()
    {
        ObjectMapper mapper = new ObjectMapper();
        boonNameToKey = loadJsonMap(mapper, "/boon-name-to-key.json");
        abilityNameToKey = loadJsonMap(mapper, "/ability-name-to-key.json");
        boonVariantToKey = loadJsonMap(mapper, "/boon-variant-to-key.json");
        LOGGER.info("Lookup geladen: {} Boons, {} Abilities, {} Varianten",
            boonNameToKey.size(), abilityNameToKey.size(), boonVariantToKey.size());
    }

    private Map<String, String> loadJsonMap(ObjectMapper mapper, String resource)
    {
        try (InputStream is = getClass().getResourceAsStream(resource))
        {
            if (is == null)
            {
                LOGGER.warn("Ressource nicht gefunden: {}", resource);
                return new HashMap<>();
            }
            return mapper.readValue(is, new TypeReference<>() {});
        }
        catch (IOException e)
        {
            LOGGER.error("Fehler beim Laden von {}: {}", resource, e.getMessage());
            return new HashMap<>();
        }
    }

    public List<String> getUnresolvedBoons() { return unresolvedBoons; }
    public List<String> getUnresolvedAbilities() { return unresolvedAbilities; }

    /**
     * Extrahiert alle Profile aus Markdown-Seitentexten.
     */
    public List<Map<String, Object>> extractProfiles(List<String> pageTexts)
    {
        List<Map<String, Object>> profiles = new ArrayList<>();

        for (String pageText : pageTexts)
        {
            String rightColumn = extractRightColumn(pageText);
            if (rightColumn == null || rightColumn.isBlank()) continue;
            if (!rightColumn.contains("**MU**")) continue;

            try
            {
                Map<String, Object> profile = parseStatBlock(rightColumn);
                if (profile != null && profile.containsKey("name"))
                {
                    String name = (String) profile.get("name");
                    profile.put("key", normalizeKey(name));
                    profiles.add(profile);
                    LOGGER.info("  Profil extrahiert: {}", name);
                }
            }
            catch (Exception e)
            {
                LOGGER.warn("  Fehler beim Parsen eines Statblocks: {}", e.getMessage());
            }
        }

        return profiles;
    }

    private String extractRightColumn(String pageText)
    {
        int idx = pageText.indexOf("<!-- Spalte 2 -->");
        if (idx < 0) return null;
        return pageText.substring(idx);
    }

    private Map<String, Object> parseStatBlock(String text)
    {
        Map<String, Object> profile = new LinkedHashMap<>();

        // Zeilenumbruch-Bindestriche reparieren
        text = text.replaceAll("(\\w)-\\s+(\\w)", "$1$2");

        String flat = text.replace("\n", " ").replaceAll("\\s+", " ");

        // Name
        String name = null;
        Matcher nameMatcher = Pattern.compile("(?m)^###\\s+(.+?)$").matcher(text);
        while (nameMatcher.find())
        {
            String candidate = cleanMarkdown(nameMatcher.group(1)).trim();
            if (!candidate.startsWith("Wissenswertes") && !candidate.startsWith("Tierkunde")
                && !candidate.startsWith("Sonderregeln") && !candidate.startsWith("Aktionen")
                && !candidate.contains(":"))
            {
                name = candidate;
                break;
            }
        }
        if (name == null)
        {
            name = extractFirst(text, "\\*\\*([^*:]+?)\\*\\*");
            if (name != null) name = cleanMarkdown(name).trim();
        }
        if (name == null) return null;
        profile.put("name", name);

        // Groesse & Gewicht
        String size = extractField(flat, "Größe:");
        if (size != null) profile.put("size", cleanMultiSpace(size));
        String weight = extractField(flat, "Gewicht:");
        if (weight != null) profile.put("weight", cleanMultiSpace(weight));

        // Attribute
        List<Map<String, Object>> attributes = new ArrayList<>();
        for (String attr : new String[]{"MU", "KL", "IN", "CH", "FF", "GE", "KO", "KK"})
        {
            Integer val = extractAttributeValue(flat, attr);
            if (val != null) attributes.add(Map.of("key", attr, "value", val));
        }
        if (!attributes.isEmpty()) profile.put("attributes", attributes);

        // Energien
        List<Map<String, Object>> energies = new ArrayList<>();
        addEnergy(energies, flat, "LeP", "LEP");
        addEnergy(energies, flat, "AsP", "ASP");
        addEnergy(energies, flat, "KaP", "KAP");
        if (!energies.isEmpty()) profile.put("energies", energies);

        // INI
        Matcher iniMatcher = Pattern.compile("\\bINI\\b[^0-9]*?(\\d+)\\s*\\+\\s*(\\d+)W(\\d+)").matcher(flat);
        if (iniMatcher.find())
        {
            profile.put("ini", Map.of(
                "base", Integer.parseInt(iniMatcher.group(1)),
                "diceCount", Integer.parseInt(iniMatcher.group(2)),
                "diceFaces", Integer.parseInt(iniMatcher.group(3))));
        }

        // Abgeleitete Werte
        List<Map<String, Object>> additionalAttributes = new ArrayList<>();
        for (String attr : new String[]{"VW", "SK", "ZK", "GS"})
        {
            Integer val = extractAttributeValue(flat, attr);
            if (val != null) additionalAttributes.add(Map.of("key", attr, "value", val));
        }
        if (!additionalAttributes.isEmpty()) profile.put("additionalAttributes", additionalAttributes);

        // Waffen
        List<Map<String, Object>> weapons = extractWeapons(flat);
        if (!weapons.isEmpty()) profile.put("weaponsMelee", weapons);

        // RS/BE
        Matcher rsMatcher = Pattern.compile("\\bRS/BE:\\s*(\\d+)/(\\d+)").matcher(flat);
        if (rsMatcher.find())
        {
            profile.put("armor", Map.of(
                "RS", Integer.parseInt(rsMatcher.group(1)),
                "BE", Integer.parseInt(rsMatcher.group(2))));
        }

        // Aktionen
        Matcher aktMatcher = Pattern.compile("Aktionen:\\s*(\\d+)").matcher(flat);
        if (aktMatcher.find())
        {
            profile.put("aktionen", Map.of("aktionen", Integer.parseInt(aktMatcher.group(1))));
        }

        // Vorteile / Nachteile (mit Key-Resolution)
        parseBoonsAndFlaws(flat, profile);

        // Sonderfertigkeiten (mit Key-Resolution)
        String abilities = extractField(flat, "Sonderfertigkeiten:");
        if (abilities != null) profile.put("abilities", parseAbilities(abilities));

        // Talente
        String talents = extractField(flat, "Talente:");
        if (talents != null) profile.put("skills", parseTalents(talents));

        // Anzahl
        String count = extractField(flat, "Anzahl:");
        if (count != null) profile.put("count", List.of(cleanMultiSpace(count)));

        // Groessenkategorie
        String sizeCat = extractField(flat, "Größenkategorie:");
        if (sizeCat != null)
        {
            String cat = cleanMultiSpace(sizeCat).trim().toLowerCase();
            profile.put("sizeCategory", cat);
        }

        // Typus
        String typus = extractField(flat, "Typus:");
        if (typus != null)
        {
            Map<String, Object> typeMap = new LinkedHashMap<>();
            typeMap.put("text", cleanMultiSpace(typus));
            String lower = typus.toLowerCase();
            if (lower.contains("tier")) typeMap.put("category", "animal");
            else if (lower.contains("übernatürlich")) typeMap.put("category", "supernatural");
            else if (lower.contains("kulturschaff")) typeMap.put("category", "cultural");
            else if (lower.contains("dämon")) typeMap.put("category", "demon");
            else if (lower.contains("elementar")) typeMap.put("category", "elementals");
            else if (lower.contains("pflanz")) typeMap.put("category", "plant");
            else if (lower.contains("fee") || lower.contains("fae")) typeMap.put("category", "fairy");
            else if (lower.contains("untot")) typeMap.put("category", "being");
            profile.put("type", typeMap);
        }

        // Beute
        String loot = extractField(flat, "Beute:");
        if (loot != null) profile.put("loot", List.of(cleanMultiSpace(loot)));

        // Kampfverhalten
        String combat = extractField(flat, "Kampfverhalten:");
        if (combat != null) profile.put("combatBehaviour", cleanMultiSpace(combat));

        // Flucht
        String flee = extractField(flat, "Flucht:");
        if (flee != null) profile.put("fleeThreshold", cleanMultiSpace(flee));

        // Schmerz
        String pain = extractField(flat, "Schmerz \\+1 bei:");
        if (pain != null)
        {
            pain = pain.replaceAll("\\s*###.*", "").replaceAll("\\s*Tierkunde.*", "");
            profile.put("painThresholds", List.of(cleanMultiSpace(pain)));
        }

        // Tierkunde / Magiekunde / Sphärenkunde QS
        List<Map<String, Object>> checkQs = extractCheckQs(flat);
        if (!checkQs.isEmpty()) profile.put("checkQs", checkQs);

        // Jagd-Modifikator
        Matcher jagdMatcher = Pattern.compile("\\bJagd:\\s*([–\\-+]?\\d+)").matcher(flat);
        if (jagdMatcher.find())
        {
            String jagdValue = jagdMatcher.group(1).replace("–", "-");
            addSpecialRule(profile, "Jagd", jagdValue);
        }

        // Sonderregeln
        extractSpecialRules(text, profile);

        // Beschwörungsschwierigkeit (Dämonen)
        Matcher bsMatcher = Pattern.compile("Beschwörungsschwierigkeit:\\s*([–\\-+]?\\d+)").matcher(flat);
        if (bsMatcher.find())
        {
            profile.put("summoningDifficulty", Integer.parseInt(bsMatcher.group(1).replace("–", "-")));
        }

        return profile;
    }

    // --- Key-Resolution ---

    private String resolveBoonKey(String germanName)
    {
        // Direkte Suche
        String key = boonNameToKey.get(germanName);
        if (key != null) return key;

        // Ohne Variante suchen: "Herausragender Sinn (Geruch)" -> "Herausragender Sinn"
        String baseName = germanName.replaceAll("\\s*\\([^)]+\\)$", "").trim();
        return boonNameToKey.get(baseName);
    }

    private String resolveBoonVariantKey(String variantName)
    {
        return boonVariantToKey.get(variantName);
    }

    private String resolveAbilityKey(String germanName)
    {
        // Direkte Suche
        String key = abilityNameToKey.get(germanName);
        if (key != null) return key;

        // Ohne Klammer suchen
        String baseName = germanName.replaceAll("\\s*\\([^)]+\\)$", "").trim();
        key = abilityNameToKey.get(baseName);
        if (key != null) return key;

        // Normalisierte Suche (lowercase)
        for (Map.Entry<String, String> entry : abilityNameToKey.entrySet())
        {
            if (entry.getKey().equalsIgnoreCase(germanName) || entry.getKey().equalsIgnoreCase(baseName))
            {
                return entry.getValue();
            }
        }
        return null;
    }

    // --- Hilfsmethoden ---

    private Integer extractAttributeValue(String text, String attrKey)
    {
        Pattern p = Pattern.compile("\\b" + Pattern.quote(attrKey) + "\\b[*\\s]*?(-?\\d+)");
        Matcher m = p.matcher(text);
        if (m.find()) return Integer.parseInt(m.group(1));
        return null;
    }

    private void addEnergy(List<Map<String, Object>> energies, String text, String label, String key)
    {
        Pattern p = Pattern.compile("\\b" + Pattern.quote(label) + "\\b[*\\s]*([0-9]+|–|-)");
        Matcher m = p.matcher(text);
        if (m.find())
        {
            String val = m.group(1);
            Map<String, Object> energy = new LinkedHashMap<>();
            energy.put("key", key);
            energy.put("value", (val.equals("–") || val.equals("-")) ? null : Integer.parseInt(val));
            energies.add(energy);
        }
    }

    private List<Map<String, Object>> extractWeapons(String text)
    {
        List<Map<String, Object>> weapons = new ArrayList<>();
        // Pattern 1: **Name: AT** nn **TP** x **RW** y
        Pattern wp = Pattern.compile(
            "\\*\\*([^*]+?):\\s*AT\\*\\*\\s*(\\d+)\\s*\\*\\*TP\\*\\*\\s*([^*]+?)\\s*\\*\\*RW\\*\\*\\s*(\\w+)");
        Matcher wm = wp.matcher(text);
        while (wm.find())
        {
            weapons.add(createWeapon(wm.group(1).trim(), wm.group(2), wm.group(3).trim(), wm.group(4).trim()));
        }
        // Pattern 2: **Name:** **AT** nn **TP** x **RW** y
        if (weapons.isEmpty())
        {
            Pattern wp2 = Pattern.compile(
                "\\*\\*([^*]+?):\\*\\*\\s*\\*\\*AT\\*\\*\\s*(\\d+)\\s*\\*\\*TP\\*\\*\\s*([^*]+?)\\s*\\*\\*RW\\*\\*\\s*(\\w+)");
            Matcher wm2 = wp2.matcher(text);
            while (wm2.find())
            {
                weapons.add(createWeapon(wm2.group(1).trim(), wm2.group(2), wm2.group(3).trim(), wm2.group(4).trim()));
            }
        }
        return weapons;
    }

    private Map<String, Object> createWeapon(String name, String at, String tp, String rw)
    {
        Map<String, Object> weapon = new LinkedHashMap<>();
        weapon.put("name", name);
        weapon.put("AT", Integer.parseInt(at));
        weapon.put("TP", tp);
        weapon.put("RW", rw);
        return weapon;
    }

    private void parseBoonsAndFlaws(String text, Map<String, Object> profile)
    {
        String raw = extractField(text, "Vorteile/Nachteile:");
        if (raw == null) return;
        raw = cleanMultiSpace(raw);

        String[] parts = raw.split("\\s*/\\s*", 2);
        List<Map<String, Object>> merits = parseBoonList(parts[0].trim(), true);
        List<Map<String, Object>> flaws = parts.length > 1 ? parseBoonList(parts[1].trim(), false) : new ArrayList<>();

        if (!merits.isEmpty()) profile.put("merits", merits);
        if (!flaws.isEmpty()) profile.put("flaws", flaws);
    }

    private List<Map<String, Object>> parseBoonList(String text, boolean isMerit)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        if (text == null || text.isBlank()) return result;

        String[] entries = text.split(",(?![^(]*\\))");
        for (String entry : entries)
        {
            entry = entry.trim();
            if (entry.isEmpty()) continue;

            Map<String, Object> boon = new LinkedHashMap<>();

            if (entry.startsWith("(empfohlen:"))
            {
                entry = entry.replaceAll("^\\(empfohlen:\\s*", "").replaceAll("\\)$", "").trim();
                // Kann mehrere empfohlene enthalten
                for (String sub : entry.split(","))
                {
                    sub = sub.trim();
                    if (sub.isEmpty()) continue;
                    Map<String, Object> rec = new LinkedHashMap<>();
                    rec.put("name", sub);
                    rec.put("remarks", "empfohlen");
                    String key = resolveBoonKey(sub);
                    if (key != null) rec.put("key", key);
                    result.add(rec);
                }
                continue;
            }

            // "Herausragender Sinn (Geruch)"
            Matcher vm = Pattern.compile("^(.+?)\\s*\\(([^)]+)\\)$").matcher(entry);
            if (vm.matches())
            {
                boon.put("name", vm.group(1).trim());
                boon.put("variantName", vm.group(2).trim());

                String key = resolveBoonKey(vm.group(1).trim());
                if (key != null) boon.put("key", key);
                else trackUnresolved(unresolvedBoons, vm.group(1).trim());

                String varKey = resolveBoonVariantKey(vm.group(2).trim());
                if (varKey != null) boon.put("variantKey", varKey);
            }
            else
            {
                boon.put("name", entry);
                String key = resolveBoonKey(entry);
                if (key != null) boon.put("key", key);
                else trackUnresolved(unresolvedBoons, entry);
            }
            result.add(boon);
        }
        return result;
    }

    private List<Map<String, Object>> parseAbilities(String text)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        text = cleanMultiSpace(text);
        String[] entries = text.split(",(?![^(]*\\))");
        for (String entry : entries)
        {
            entry = entry.trim();
            if (entry.isEmpty()) continue;

            if (entry.startsWith("(empfohlen:"))
            {
                entry = entry.replaceAll("^\\(empfohlen:\\s*", "").replaceAll("\\)$", "").trim();
                for (String sub : entry.split(","))
                {
                    sub = sub.trim();
                    if (sub.isEmpty()) continue;
                    Map<String, Object> rec = new LinkedHashMap<>();
                    rec.put("name", sub);
                    rec.put("remarks", "empfohlen");
                    String key = resolveAbilityKey(sub);
                    if (key != null) rec.put("key", key);
                    result.add(rec);
                }
                continue;
            }

            Map<String, Object> ability = new LinkedHashMap<>();
            Matcher am = Pattern.compile("^(.+?)\\s*\\(([^)]+)\\)$").matcher(entry);
            if (am.matches())
            {
                String name = am.group(1).trim();
                ability.put("name", name);
                ability.put("appliedFor", List.of(am.group(2).trim()));
                String key = resolveAbilityKey(name);
                if (key == null) key = resolveAbilityKey(entry); // try with full text
                if (key != null) ability.put("key", key);
                else trackUnresolved(unresolvedAbilities, name);
            }
            else
            {
                ability.put("name", entry);
                String key = resolveAbilityKey(entry);
                if (key != null) ability.put("key", key);
                else trackUnresolved(unresolvedAbilities, entry);
            }
            result.add(ability);
        }
        return result;
    }

    private Map<String, Object> parseTalents(String text)
    {
        text = cleanMarkdown(text);
        text = cleanMultiSpace(text);

        Map<String, Object> skillCategories = new LinkedHashMap<>();

        Pattern sp = Pattern.compile(
            "([A-ZÄÖÜ][a-zäöüß]+(?:\\s*&\\s*[A-ZÄÖÜ][a-zäöüß]+)*)\\s+" +
                "(?:\\([^)]*\\)\\s*,?\\s*)?(?:(\\d+))?");
        Matcher sm = sp.matcher(text);

        List<Map<String, Object>> koerper = new ArrayList<>();
        List<Map<String, Object>> gesellschaft = new ArrayList<>();
        List<Map<String, Object>> natur = new ArrayList<>();
        List<Map<String, Object>> wissen = new ArrayList<>();
        List<Map<String, Object>> handwerk = new ArrayList<>();

        while (sm.find())
        {
            String skillName = sm.group(1).trim();
            String valueStr = sm.group(2);
            String key = SKILL_KEY_MAP.get(skillName);
            if (key != null && valueStr != null)
            {
                Map<String, Object> skill = Map.of("key", key, "value", Integer.parseInt(valueStr));
                switch (key)
                {
                    case "fliegen": case "gaukeleien": case "klettern": case "körperbeherrschung":
                    case "kraftakt": case "reiten": case "schwimmen": case "selbstbeherrschung":
                    case "singen": case "sinnesschärfe": case "tanzen": case "taschendiebstahl":
                    case "verbergen": case "zechen":
                        koerper.add(skill); break;
                    case "bekehren_und_überzeugen": case "betören": case "einschüchtern":
                    case "etikette": case "gassenwissen": case "menschenkenntnis":
                    case "überreden": case "verkleiden": case "willenskraft":
                        gesellschaft.add(skill); break;
                    case "fährtensuchen": case "fesseln": case "fischen_und_angeln":
                    case "orientierung": case "pflanzenkunde": case "tierkunde":
                    case "wildnisleben":
                        natur.add(skill); break;
                    case "brett_und_glücksspiel": case "geographie": case "geschichtswissen":
                    case "götter_und_kulte": case "kriegskunst": case "magiekunde":
                    case "mechanik": case "rechnen": case "rechtskunde":
                    case "sagen_und_legenden": case "sphärenkunde": case "sternkunde":
                        wissen.add(skill); break;
                    default:
                        handwerk.add(skill); break;
                }
            }
        }

        if (!koerper.isEmpty()) skillCategories.put("körper", koerper);
        if (!gesellschaft.isEmpty()) skillCategories.put("gesellschaft", gesellschaft);
        if (!natur.isEmpty()) skillCategories.put("natur", natur);
        if (!wissen.isEmpty()) skillCategories.put("wissen", wissen);
        if (!handwerk.isEmpty()) skillCategories.put("handwerk", handwerk);
        return skillCategories;
    }

    private List<Map<String, Object>> extractCheckQs(String flat)
    {
        List<Map<String, Object>> result = new ArrayList<>();

        // Tierkunde/Magiekunde/Sphärenkunde Subject extrahieren
        Matcher topicMatcher = Pattern.compile("(Tierkunde|Magiekunde|Sphärenkunde)\\s*\\(([^)]+)\\)").matcher(flat);
        String skillName = "tierkunde";
        String subject = "domestizierte Tiere";
        if (topicMatcher.find())
        {
            skillName = topicMatcher.group(1).toLowerCase();
            if (skillName.equals("magiekunde")) skillName = "magiekunde";
            else if (skillName.equals("sphärenkunde")) skillName = "sphärenkunde";
            subject = topicMatcher.group(2).trim();
        }

        List<Map<String, Object>> qualities = new ArrayList<>();
        Pattern qsPattern = Pattern.compile("QS\\s*(\\d\\+?):\\s*(.+?)(?=\\s*\\*\\*QS|\\s*\\*\\*Jagd|\\s*###|$)");
        Matcher qsMatcher = qsPattern.matcher(flat);
        while (qsMatcher.find())
        {
            String level = qsMatcher.group(1);
            String description = cleanMultiSpace(cleanMarkdown(qsMatcher.group(2)));
            int minQs = Integer.parseInt(level.replace("+", ""));
            int maxQs = level.contains("+") ? 6 : minQs;
            qualities.add(Map.of("minQs", minQs, "maxQs", maxQs, "description", description));
        }

        if (!qualities.isEmpty())
        {
            Map<String, Object> checkQs = new LinkedHashMap<>();
            checkQs.put("subject", subject);
            checkQs.put("skill", skillName);
            checkQs.put("qualities", qualities);
            result.add(checkQs);
        }

        return result;
    }

    private void extractSpecialRules(String text, Map<String, Object> profile)
    {
        Pattern srSection = Pattern.compile("###\\s*Sonderregeln:\\s*\n(.+?)(?=\n(?:##|$))", Pattern.DOTALL);
        Matcher srMatcher = srSection.matcher(text);
        if (!srMatcher.find()) return;

        String rulesText = srMatcher.group(1);
        Pattern rulePattern = Pattern.compile("\\*([^*]+?):\\*\\s*(.+?)(?=\\*[^*]+?:\\*|$)", Pattern.DOTALL);
        Matcher ruleMatcher = rulePattern.matcher(rulesText);

        while (ruleMatcher.find())
        {
            String name = ruleMatcher.group(1).trim();
            String rule = cleanMultiSpace(ruleMatcher.group(2).trim());
            addSpecialRule(profile, name, rule);
        }
    }

    @SuppressWarnings("unchecked")
    private void addSpecialRule(Map<String, Object> profile, String name, String rule)
    {
        List<Map<String, Object>> rules;
        if (profile.containsKey("specialRules"))
        {
            rules = new ArrayList<>((List<Map<String, Object>>) profile.get("specialRules"));
        }
        else
        {
            rules = new ArrayList<>();
        }
        rules.add(Map.of("name", name, "rule", rule));
        profile.put("specialRules", rules);
    }

    private String extractField(String text, String fieldLabel)
    {
        Pattern p = Pattern.compile(
            "\\*\\*" + fieldLabel + "\\*\\*\\s*(.+?)(?=\\s*\\*\\*[A-ZÄÖÜ]|$)", Pattern.DOTALL);
        Matcher m = p.matcher(text);
        if (m.find()) return cleanMarkdown(m.group(1)).trim();

        String cleanLabel = fieldLabel.replace("\\", "");
        Pattern p2 = Pattern.compile(cleanLabel + "\\s*(.+?)(?=\\s*\\*\\*[A-ZÄÖÜ]|$)", Pattern.DOTALL);
        Matcher m2 = p2.matcher(text);
        if (m2.find()) return cleanMarkdown(m2.group(1)).trim();
        return null;
    }

    private String extractFirst(String text, String regex)
    {
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private String cleanMarkdown(String text)
    {
        return text == null ? null : text.replaceAll("\\*{1,3}", "").trim();
    }

    private String cleanMultiSpace(String text)
    {
        return text == null ? null : text.replaceAll("\\s+", " ").trim();
    }

    private void trackUnresolved(List<String> list, String name)
    {
        if (!list.contains(name)) list.add(name);
    }

    public static String normalizeKey(String name)
    {
        if (name == null) return null;
        return name.toLowerCase()
            .replaceAll("[^a-zäöüß0-9\\s]", "")
            .trim()
            .replaceAll("\\s+", "_");
    }
}
