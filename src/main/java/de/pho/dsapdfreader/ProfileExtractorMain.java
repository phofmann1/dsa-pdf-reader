package de.pho.dsapdfreader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.uid.UidCategory;

/**
 * Extrahiert alle Profile (Kreaturen, NSCs, Dämonen, Tiere, ...)
 * aus den _headings.md-Dateien und schreibt pro Buch eine JSON-Datei
 * ins Angular-Projekt. Output-Format orientiert sich am Angular Profile-Interface.
 */
public class ProfileExtractorMain
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    private static final String HEADINGS_BASE = "export/markdown/text";
    private static final String OUTPUT_BASE =
        "D:/develop/project/angular/ng-dsa/src/app/_data/profiles";
    private static final String OUTPUT_COMMON = OUTPUT_BASE + "/common";
    private static final String OUTPUT_SECRET = OUTPUT_BASE + "/secret";

    private static final Set<String> EXCLUDED_DIRECTORIES = Set.of(
        "50 - Scriptorium", "98 - Material", "Florian Don-Schauen", "Mona",
        "Unbekannt", "06 - Karten", "10 - Abenteuer Zubehor", "11 - Spielplane",
        "09 - Brevier"
    );

    private static final Set<String> SECRET_CATEGORIES = Set.of(
        "04 - Mysterien", "05 - Abenteuer", "05b - Abenteuer DSA 4", "07 - Solo"
    );

    // --- Key-Aliase: Alternativnamen → kanonischer Key ---
    private static final Map<String, String> KEY_ALIASES = Map.ofEntries(
        Map.entry("eisdschinn", "dschinn_des_eises"),
        Map.entry("erzdschinn", "dschinn_des_erzes"),
        Map.entry("feuerdschinn", "dschinn_des_feuers"),
        Map.entry("humusdschinn", "dschinn_des_humus"),
        Map.entry("luftdschinn", "dschinn_der_luft"),
        Map.entry("wasserdschinn", "dschinn_des_wassers"),
        Map.entry("uneisdschinn", "undschinn_des_eises"),
        Map.entry("unerzdschinn", "undschinn_des_erzes"),
        Map.entry("unfeuerdschinn", "undschinn_des_feuers"),
        Map.entry("unhumusdschinn", "undschinn_des_humus"),
        Map.entry("unluftdschinn", "undschinn_der_luft"),
        Map.entry("unwasserdschinn", "undschinn_des_wassers")
    );

    // --- Lookup-Maps ---
    private static Map<String, String> boonNameToKey = new HashMap<>();
    private static Map<String, String> abilityNameToKey = new HashMap<>();
    private static Map<String, String> boonVariantToKey = new HashMap<>();

    // --- Skill-Name → SkillKey ---
    private static final Map<String, String> SKILL_KEY_MAP = new HashMap<>();

    static
    {
        // Körper
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
        // Gesellschaft
        SKILL_KEY_MAP.put("Bekehren & Überzeugen", "bekehren_und_überzeugen");
        SKILL_KEY_MAP.put("Betören", "betören");
        SKILL_KEY_MAP.put("Einschüchtern", "einschüchtern");
        SKILL_KEY_MAP.put("Etikette", "etikette");
        SKILL_KEY_MAP.put("Gassenwissen", "gassenwissen");
        SKILL_KEY_MAP.put("Menschenkenntnis", "menschenkenntnis");
        SKILL_KEY_MAP.put("Überreden", "überreden");
        SKILL_KEY_MAP.put("Verkleiden", "verkleiden");
        SKILL_KEY_MAP.put("Willenskraft", "willenskraft");
        // Natur
        SKILL_KEY_MAP.put("Fährtensuchen", "fährtensuchen");
        SKILL_KEY_MAP.put("Fesseln", "fesseln");
        SKILL_KEY_MAP.put("Fischen & Angeln", "fischen_und_angeln");
        SKILL_KEY_MAP.put("Orientierung", "orientierung");
        SKILL_KEY_MAP.put("Pflanzenkunde", "pflanzenkunde");
        SKILL_KEY_MAP.put("Tierkunde", "tierkunde");
        SKILL_KEY_MAP.put("Wildnisleben", "wildnisleben");
        // Wissen
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
        // Handwerk
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

    // --- TargetCategory Mapping ---
    private static final Map<String, String> TARGET_CATEGORY_MAP = new LinkedHashMap<>();

    static
    {
        TARGET_CATEGORY_MAP.put("tier", "animal");
        TARGET_CATEGORY_MAP.put("übernatürlich", "supernatural");
        TARGET_CATEGORY_MAP.put("kulturschaff", "cultural");
        TARGET_CATEGORY_MAP.put("dämon", "demon");
        TARGET_CATEGORY_MAP.put("elementar", "elementals");
        TARGET_CATEGORY_MAP.put("pflanz", "plant");
        TARGET_CATEGORY_MAP.put("fee", "fairy");
        TARGET_CATEGORY_MAP.put("fae", "fairy");
        TARGET_CATEGORY_MAP.put("untot", "undead");
        TARGET_CATEGORY_MAP.put("geist", "ghost");
        TARGET_CATEGORY_MAP.put("chimär", "creature");
        TARGET_CATEGORY_MAP.put("wesen", "being");
    }

    // --- Patterns ---
    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,4})\\s+(.+)$");
    private static final Pattern ATTR_PATTERN = Pattern.compile(
        "\\*\\*([A-ZÄÖÜ]{2,3})\\*\\*\\s*(\\d+|–|-)"
    );
    private static final Pattern INI_PATTERN = Pattern.compile(
        "\\*\\*INI\\*\\*\\s*(\\d+)\\s*\\+\\s*(\\d+)[Ww](\\d+)"
    );
    private static final Pattern WEAPON_MELEE_PATTERN = Pattern.compile(
        "\\*\\*([^*:]+?)(?::\\s*AT|:\\*\\*\\s*\\*\\*AT)\\*\\*\\s*(\\d+)" +
        "(?:\\s*\\*\\*PA\\*\\*\\s*(\\d+))?" +
        "\\s*\\*\\*TP\\*\\*\\s*([^*]+?)\\s*\\*\\*RW\\*\\*\\s*(\\S+)"
    );
    private static final Pattern WEAPON_RANGED_PATTERN = Pattern.compile(
        "\\*\\*([^*:]+?)(?::\\s*FK|:\\*\\*\\s*\\*\\*FK)\\*\\*\\s*(\\d+)" +
        "(?:\\s*\\*\\*LZ\\*\\*\\s*(\\d+))?" +
        "\\s*\\*\\*TP\\*\\*\\s*([^*]+?)\\s*\\*\\*RW\\*\\*\\s*(\\S+)"
    );
    private static final Pattern RS_BE_PATTERN = Pattern.compile(
        "\\*\\*RS/BE[:\\s]*\\*\\*\\s*(\\d+)/(\\d+)"
    );
    private static final Pattern RS_BE_INLINE = Pattern.compile(
        "RS/BE:?\\s*(\\d+)/(\\d+)"
    );
    private static final Pattern AKTIONEN_PATTERN = Pattern.compile(
        "Aktionen:?\\s*(\\d+)"
    );
    private static final Pattern TP_PARSE = Pattern.compile(
        "(\\d+)[Ww](\\d+)(?:\\s*\\+\\s*(\\d+))?"
    );
    private static final Pattern SUMMONING_PATTERN = Pattern.compile(
        "(?:Beschw.rungsschwierigkeit|Anrufungsschwierigkeit)[:\\s]+([–\\-+]?\\d+)"
    );
    private static final Pattern PAIN_NUMBER_PATTERN = Pattern.compile("(\\d+)\\s*LeP");
    private static final Pattern VARIANT_PATTERN = Pattern.compile(
        "^\\*\\*(Erfahren|Kompetent|Meisterlich|Brillant):\\*\\*\\s*(.+)$"
    );

    private static final String[] PROFILE_SUB_SECTIONS = {
        "Aktionen", "Sonderregeln", "Tierkunde", "Magiekunde", "Sphärenkunde",
        "Zusätzliche Dienste", "Wissenswertes", "Anrufungsschwierigkeit",
        "Beschwörungsschwierigkeit", "Paktgeschenke", "Jagd"
    };

    // Stats — getrennt nach common/secret
    private static final List<String> unresolvedBoonsCommon = new ArrayList<>();
    private static final List<String> unresolvedBoonsSecret = new ArrayList<>();
    private static final List<String> unresolvedAbilitiesCommon = new ArrayList<>();
    private static final List<String> unresolvedAbilitiesSecret = new ArrayList<>();

    // Thread-local Kontext: welche Liste gerade aktiv ist
    private static List<String> unresolvedBoons = unresolvedBoonsCommon;
    private static List<String> unresolvedAbilities = unresolvedAbilitiesCommon;

    // Sub-Varianten-Konfiguration (Golemiden etc.)
    private static Map<String, List<Map<String, Object>>> subVariantConfig = new HashMap<>();
    private static Map<String, List<Map<String, String>>> subVariantCommonRules = new HashMap<>();

    // Waffen-Lookup für Reverse-Engineering von Kampftalentwerten
    private static Map<String, Map<String, Object>> weaponLookup = new HashMap<>();

    // Enum-Ordinal-Mappings (String → int) geladen aus enum-ordinals.json
    private static Map<String, Integer> ATTR_ORDINALS = new HashMap<>();
    private static Map<String, Integer> SKILL_ORDINALS = new HashMap<>();
    private static Map<String, Integer> SIZE_CAT_ORDINALS = new HashMap<>();
    private static Map<String, Integer> TARGET_CAT_ORDINALS = new HashMap<>();
    private static Map<String, Integer> RANGE_ORDINALS = new HashMap<>();
    private static Map<String, Integer> COMBAT_SKILL_ORDINALS = new HashMap<>();
    private static Map<String, Integer> BOON_ORDINALS = new HashMap<>();
    private static Map<String, Integer> BOON_VARIANT_ORDINALS = new HashMap<>();
    private static Map<String, Integer> ABILITY_ORDINALS = new HashMap<>();
    private static Map<String, Integer> PROFILE_KEY_ORDINALS = new HashMap<>();

    // Reverse: AttributeShort-Ordinal → String-Key (für Waffen-Leitattribut-Lookup)
    private static final Map<Integer, String> ATTR_INDEX_TO_KEY = Map.of(
        0, "MU", 1, "KL", 2, "IN", 3, "CH", 4, "FF", 5, "GE", 6, "KO", 7, "KK"
    );

    public static void main(String[] args) throws Exception
    {
        LOGGER.info("=== Profil-Extraktor ===");

        loadLookupMaps();

        Path basePath = Paths.get(HEADINGS_BASE);
        Path commonPath = Paths.get(OUTPUT_COMMON);
        Path secretPath = Paths.get(OUTPUT_SECRET);
        Files.createDirectories(commonPath);
        Files.createDirectories(secretPath);

        List<Path> headingsFiles;
        try (Stream<Path> walk = Files.walk(basePath))
        {
            headingsFiles = walk
                .filter(p -> p.getFileName().toString().equals("_headings.md"))
                .filter(p -> !isExcluded(p))
                .collect(Collectors.toList());
        }

        int totalProfiles = 0;
        int commonCount = 0;
        int secretCount = 0;
        int totalBooks = 0;

        for (Path file : headingsFiles)
        {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            if (!content.contains("**MU**")) continue;

            String bookName = extractBookName(file);
            List<Map<String, Object>> profiles = extractProfiles(content, bookName);
            if (profiles.isEmpty()) continue;

            boolean isSecret = isSecretCategory(file);
            unresolvedBoons = isSecret ? unresolvedBoonsSecret : unresolvedBoonsCommon;
            unresolvedAbilities = isSecret ? unresolvedAbilitiesSecret : unresolvedAbilitiesCommon;
            Path outputPath = isSecret ? secretPath : commonPath;
            String fileName = sanitizeFileName(bookName) + "_PROFILES.json";
            MAPPER.writeValue(outputPath.resolve(fileName).toFile(), profiles);

            totalProfiles += profiles.size();
            if (isSecret) secretCount += profiles.size();
            else commonCount += profiles.size();
            totalBooks++;
            LOGGER.info("  [{}] {} : {} Profile", isSecret ? "secret" : "common", bookName, profiles.size());
        }

        LOGGER.info("=== Gesamt: {} Profile in {} Büchern (common: {}, secret: {}) ===",
            totalProfiles, totalBooks, commonCount, secretCount);
        if (!unresolvedBoonsCommon.isEmpty())
        {
            LOGGER.info("Unaufgelöste Boons COMMON ({}): {}", unresolvedBoonsCommon.size(),
                String.join(" | ", unresolvedBoonsCommon));
        }
        if (!unresolvedBoonsSecret.isEmpty())
        {
            LOGGER.info("Unaufgelöste Boons SECRET ({}): {}", unresolvedBoonsSecret.size(),
                String.join(" | ", unresolvedBoonsSecret));
        }
        if (!unresolvedAbilitiesCommon.isEmpty())
        {
            LOGGER.info("Unaufgelöste Abilities COMMON ({}): {}", unresolvedAbilitiesCommon.size(),
                String.join(" | ", unresolvedAbilitiesCommon));
        }
        if (!unresolvedAbilitiesSecret.isEmpty())
        {
            LOGGER.info("Unaufgelöste Abilities SECRET ({}): {}", unresolvedAbilitiesSecret.size(),
                String.join(" | ", unresolvedAbilitiesSecret));
        }
    }

    // ---------------------------------------------------------------
    // Lookup-Maps laden
    // ---------------------------------------------------------------

    // ---------------------------------------------------------------
    // Öffentliche API für andere Extraktoren
    // ---------------------------------------------------------------

    private static boolean lookupsLoaded = false;

    /** Initialisiert alle Lookups (Boons, Abilities, Enums, Waffen). Idempotent. */
    public static void initLookups()
    {
        if (!lookupsLoaded)
        {
            loadLookupMaps();
            lookupsLoaded = true;
        }
    }

    /**
     * Parst einen Profil-Statblock aus Markdown und gibt ein Profile-Map im Zielformat zurück.
     * @param name Profilname
     * @param block Markdown-Block (mit Zeilenumbrüchen)
     * @param bookName Publikationsname
     * @return Profile-Map oder null wenn kein gültiges Profil
     */
    public static Map<String, Object> parseProfileFromBlock(String name, String block, String bookName)
    {
        initLookups();
        return parseBaseProfile(name, block, Map.of(), bookName);
    }

    private static void loadLookupMaps()
    {
        ObjectMapper mapper = new ObjectMapper();
        boonNameToKey = loadJsonMap(mapper, "/boon-name-to-key.json");
        abilityNameToKey = loadJsonMap(mapper, "/ability-name-to-key.json");
        boonVariantToKey = loadJsonMap(mapper, "/boon-variant-to-key.json");
        LOGGER.info("Lookup geladen: {} Boons, {} Abilities, {} Varianten",
            boonNameToKey.size(), abilityNameToKey.size(), boonVariantToKey.size());
        loadEnumOrdinals(mapper);
        loadSubVariantConfig(mapper);
        loadWeaponLookup(mapper);
    }

    @SuppressWarnings("unchecked")
    private static void loadWeaponLookup(ObjectMapper mapper)
    {
        try (InputStream is = ProfileExtractorMain.class.getResourceAsStream("/weapon-lookup.json"))
        {
            if (is == null) { LOGGER.warn("weapon-lookup.json nicht gefunden"); return; }
            weaponLookup = mapper.readValue(is, new TypeReference<>() {});
            LOGGER.info("Waffen-Lookup geladen: {} Einträge", weaponLookup.size());
        }
        catch (IOException e)
        {
            LOGGER.error("Fehler beim Laden des Waffen-Lookups: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadEnumOrdinals(ObjectMapper mapper)
    {
        try (InputStream is = ProfileExtractorMain.class.getResourceAsStream("/enum-ordinals.json"))
        {
            if (is == null) { LOGGER.warn("enum-ordinals.json nicht gefunden"); return; }
            Map<String, Map<String, Integer>> root = mapper.readValue(is, new TypeReference<>() {});
            ATTR_ORDINALS = root.getOrDefault("attributeShort", Map.of());
            SKILL_ORDINALS = root.getOrDefault("skillKey", Map.of());
            SIZE_CAT_ORDINALS = root.getOrDefault("sizeCategory", Map.of());
            TARGET_CAT_ORDINALS = root.getOrDefault("targetCategory", Map.of());
            RANGE_ORDINALS = root.getOrDefault("closeCombatRangeKey", Map.of());
            COMBAT_SKILL_ORDINALS = root.getOrDefault("combatSkillKey", Map.of());
            BOON_ORDINALS = root.getOrDefault("boonKey", Map.of());
            BOON_VARIANT_ORDINALS = root.getOrDefault("boonVariantKey", Map.of());
            ABILITY_ORDINALS = root.getOrDefault("abilityKey", Map.of());
            PROFILE_KEY_ORDINALS = root.getOrDefault("profileKey", Map.of());
            LOGGER.info("Enum-Ordinals geladen: {} Attr, {} Skills, {} Boons, {} Abilities, {} ProfileKeys",
                ATTR_ORDINALS.size(), SKILL_ORDINALS.size(), BOON_ORDINALS.size(),
                ABILITY_ORDINALS.size(), PROFILE_KEY_ORDINALS.size());
        }
        catch (IOException e)
        {
            LOGGER.error("Fehler beim Laden der Enum-Ordinals: {}", e.getMessage());
        }
    }

    /** Konvertiert einen String-Key in den numerischen Enum-Ordinal, oder gibt den String zurück. */
    private static Object toOrdinal(Map<String, Integer> ordinals, String key)
    {
        if (key == null) return null;
        Integer ordinal = ordinals.get(key);
        return ordinal != null ? ordinal : key;
    }

    /** Konvertiert Boon-Keys (key, selectedVariantKey) von String zu Enum-Ordinal. */
    private static void convertBoonKeysToOrdinals(Map<String, Object> boon)
    {
        Object key = boon.get("key");
        if (key instanceof String)
        {
            Object ordinal = toOrdinal(BOON_ORDINALS, (String) key);
            if (ordinal instanceof Integer) boon.put("key", ordinal);
            else boon.remove("key"); // unbekannter Key → entfernen
        }
        Object varKey = boon.get("selectedVariantKey");
        if (varKey instanceof String)
        {
            Object ordinal = toOrdinal(BOON_VARIANT_ORDINALS, (String) varKey);
            if (ordinal instanceof Integer) boon.put("selectedVariantKey", ordinal);
            else boon.remove("selectedVariantKey");
        }
    }

    /** Konvertiert Ability-Key von String zu Enum-Ordinal. */
    private static void convertAbilityKeyToOrdinal(Map<String, Object> ability)
    {
        Object key = ability.get("key");
        if (key instanceof String)
        {
            Object ordinal = toOrdinal(ABILITY_ORDINALS, (String) key);
            if (ordinal instanceof Integer) ability.put("key", ordinal);
            else ability.remove("key"); // unbekannter Key → entfernen
        }
    }

    /** Publikationskürzel-Pattern: AMA112, AKO296, ABE8, AGÖ045 etc. */
    private static final Pattern PUB_REF_PATTERN = Pattern.compile(
        "\\b[A-Z]{2,4}\\d{1,4}\\b"
    );

    @SuppressWarnings("unchecked")
    private static void loadSubVariantConfig(ObjectMapper mapper)
    {
        try (InputStream is = ProfileExtractorMain.class.getResourceAsStream("/profile-sub-variants.json"))
        {
            if (is == null) { LOGGER.warn("profile-sub-variants.json nicht gefunden"); return; }
            Map<String, Object> root = mapper.readValue(is, new TypeReference<>() {});
            List<Map<String, Object>> entries = (List<Map<String, Object>>) root.get("subVariants");
            for (Map<String, Object> entry : entries)
            {
                String baseKey = (String) entry.get("baseProfileKey");
                subVariantConfig.put(baseKey, (List<Map<String, Object>>) entry.get("variants"));
                List<Map<String, Object>> commonRulesRaw = (List<Map<String, Object>>) entry.get("commonSpecialRules");
                if (commonRulesRaw != null)
                {
                    List<Map<String, String>> commonRules = new ArrayList<>();
                    for (Map<String, Object> r : commonRulesRaw)
                    {
                        commonRules.add(Map.of("name", (String) r.get("name"), "rule", (String) r.get("rule")));
                    }
                    subVariantCommonRules.put(baseKey, commonRules);
                }
            }
            LOGGER.info("Sub-Varianten geladen: {} Basis-Profile", subVariantConfig.size());
        }
        catch (IOException e)
        {
            LOGGER.error("Fehler beim Laden der Sub-Varianten: {}", e.getMessage());
        }
    }

    private static Map<String, String> loadJsonMap(ObjectMapper mapper, String resource)
    {
        try (InputStream is = ProfileExtractorMain.class.getResourceAsStream(resource))
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

    // ---------------------------------------------------------------
    // Profil-Extraktion
    // ---------------------------------------------------------------

    private static List<Map<String, Object>> extractProfiles(String content, String bookName)
    {
        List<Map<String, Object>> profiles = new ArrayList<>();
        String[] lines = content.split("\n");

        for (int i = 0; i < lines.length; i++)
        {
            if (!lines[i].contains("**MU**")) continue;

            String name = findProfileName(lines, i);
            if (name == null) continue;

            String block = collectProfileBlock(lines, i);

            List<Map<String, Object>> parsed = parseProfiles(name, block, bookName);
            profiles.addAll(parsed);
        }
        return profiles;
    }

    private static String findProfileName(String[] lines, int attrLineIdx)
    {
        for (int j = attrLineIdx - 1; j >= Math.max(0, attrLineIdx - 15); j--)
        {
            String line = lines[j].trim();
            if (line.startsWith("<!--") || line.isEmpty()) continue;
            Matcher hm = HEADING_PATTERN.matcher(line);
            if (hm.matches())
            {
                String headingText = hm.group(2).trim().replaceAll("\\*+", "").trim();

                // Prüfe ob der Name ein Fragment ist (z.B. "1. Infektionsstufe)" — beginnt
                // mit Zahl oder endet mit Klammer aber öffnet keine). Dann suche weiter
                // rückwärts nach dem Bold-Anfang des zusammengehörigen Namens.
                boolean isFragment = headingText.matches("^\\d+\\..*")
                    || (headingText.contains(")") && !headingText.contains("("));
                if (isFragment)
                {
                    for (int k = j - 1; k >= Math.max(0, j - 5); k--)
                    {
                        String prevLine = lines[k].trim();
                        if (prevLine.isEmpty()) continue;
                        // Suche nach Bold-Text der den Namen enthält
                        Matcher boldM = Pattern.compile("\\*\\*([^*]+)").matcher(prevLine);
                        if (boldM.find())
                        {
                            String prefix = boldM.group(1).trim();
                            // Erst Felder-Kram abschneiden: "Name, Talente: ..." → "Name,"
                            Matcher fieldCut = Pattern.compile("(.*?)\\s*(?:Talente:|Sonderfertigkeiten:|Vorteile|MU\\b)")
                                .matcher(prefix);
                            if (fieldCut.find()) prefix = fieldCut.group(1).trim();
                            // Zusammenführen: "Qu'Anoth-Infizierter (durchschnittlichen Bürgerin," + "1. Infektionsstufe)"
                            String combined = (prefix + " " + headingText)
                                .replaceAll(",\\s+", ", ")
                                .replaceAll("\\s+", " ").trim();
                            return combined;
                        }
                    }
                }
                return headingText;
            }
        }
        return null;
    }

    private static String collectProfileBlock(String[] lines, int startIdx)
    {
        StringBuilder block = new StringBuilder();
        for (int j = startIdx; j < Math.min(lines.length, startIdx + 150); j++)
        {
            String line = lines[j];
            if (line.trim().startsWith("<!--")) continue;
            if (j > startIdx && line.matches("^#{1,4}\\s+.+") && !isProfileSubSection(line))
            {
                break;
            }
            block.append(line).append("\n");
        }
        return block.toString();
    }

    // ---------------------------------------------------------------
    // Profil-Parsing (Zielformat)
    // ---------------------------------------------------------------

    private static List<Map<String, Object>> parseProfiles(String name, String block, String bookName)
    {
        // 1) Varianten-Blöcke abtrennen (Erfahren/Kompetent)
        SplitResult split = splitVariants(block);
        String baseBlock = split.baseBlock;
        Map<String, String> variants = split.variants;

        // 2) Größen-Varianten erkennen (z.B. "Weiche Golemiden (mittel / groß / riesig)")
        int sizeVariantCount = detectSizeVariantCount(baseBlock);
        List<String> sizeVariantNames = extractSizeVariantNames(name, sizeVariantCount);

        // Für jede Größen-Variante ein eigenes Profil erzeugen
        List<Map<String, Object>> baseProfiles = new ArrayList<>();
        if (sizeVariantCount > 1)
        {
            for (int vi = 0; vi < sizeVariantCount; vi++)
            {
                String variantBaseBlock = preprocessForSizeVariant(baseBlock, sizeVariantCount, vi);
                String variantName = sizeVariantNames.get(vi);
                Map<String, Object> profile = parseBaseProfile(variantName, variantBaseBlock, variants, bookName);
                if (profile != null) baseProfiles.add(profile);
            }
        }
        else
        {
            Map<String, Object> profile = parseBaseProfile(name, baseBlock, variants, bookName);
            if (profile != null) baseProfiles.add(profile);
        }

        if (baseProfiles.isEmpty()) return List.of();

        // 3) Sub-Varianten aus Konfiguration (Golemiden-Materialien etc.)
        List<Map<String, Object>> expandedProfiles = new ArrayList<>();
        for (Map<String, Object> profile : baseProfiles)
        {
            String profileKey = (String) profile.get("key");
            // Prüfe ob es Sub-Varianten für den Key-Stamm gibt (ohne Größen-Suffix)
            String baseKeyForLookup = findSubVariantBaseKey(profileKey);
            if (baseKeyForLookup != null)
            {
                List<Map<String, Object>> subProfiles = expandSubVariants(profile, baseKeyForLookup);
                expandedProfiles.addAll(subProfiles);
            }
            else
            {
                expandedProfiles.add(profile);
            }
        }

        // 4) Erfahrungsvarianten auf alle Profile anwenden
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> profile : expandedProfiles)
        {
            result.add(profile);

            for (Map.Entry<String, String> variant : variants.entrySet())
            {
                String variantLabel = variant.getKey();
                String variantText = variant.getValue();
                Map<String, Object> variantProfile = deepCloneProfile(profile);
                String baseName = (String) profile.get("name");
                String variantName = baseName + " (" + variantLabel.toLowerCase() + ")";
                String variantKey = normalizeKey(variantName);
                variantProfile.put("key", variantKey);
                variantProfile.put("name", variantName);
                @SuppressWarnings("unchecked")
                Map<String, Object> typeObj = (Map<String, Object>) variantProfile.get("type");
                String typusRaw = typeObj != null ? (String) typeObj.get("text") : null;
                UidCategory uidCat = mapTypusToUidCategory(typusRaw);
                variantProfile.put("uid", uidCat.prefix + variantKey);
                variantProfile.put("experienceLevel", variantLabel.toLowerCase());

                applyVariant(variantProfile, variantText);
                result.add(variantProfile);
            }
        }

        // Key String → Ordinal konvertieren (am Ende, nach allem Klonen/Varianten)
        for (Map<String, Object> p : result)
        {
            Object key = p.get("key");
            if (key instanceof String)
            {
                p.put("key", toOrdinal(PROFILE_KEY_ORDINALS, (String) key));
            }
        }

        return result;
    }

    // ---------------------------------------------------------------
    // Sub-Varianten aus Konfiguration (Golemiden-Materialien etc.)
    // ---------------------------------------------------------------

    /**
     * Prüft ob der profileKey zu einer Sub-Varianten-Konfiguration passt.
     * Z.B. "weiche_golemiden_mittel" → baseKey "weiche_golemiden"
     */
    private static String findSubVariantBaseKey(String profileKey)
    {
        for (String baseKey : subVariantConfig.keySet())
        {
            if (profileKey.startsWith(baseKey)) return baseKey;
        }
        return null;
    }

    /**
     * Erzeugt Sub-Varianten-Profile aus einem Basisprofil + Konfiguration.
     * Das Original-Profil wird durch die Sub-Varianten ERSETZT (nicht beibehalten).
     */
    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> expandSubVariants(Map<String, Object> baseProfile, String baseKey)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> variantDefs = subVariantConfig.get(baseKey);
        List<Map<String, String>> commonRules = subVariantCommonRules.getOrDefault(baseKey, List.of());

        String baseName = (String) baseProfile.get("name");
        // Größen-Suffix extrahieren: "Weiche Golemiden (mittel)" → "(mittel)"
        String sizeSuffix = "";
        Matcher sizM = Pattern.compile("(\\([^)]+\\))$").matcher(baseName);
        if (sizM.find()) sizeSuffix = " " + sizM.group(1);

        for (Map<String, Object> variantDef : variantDefs)
        {
            String variantName = (String) variantDef.get("name");
            Map<String, Object> mods = (Map<String, Object>) variantDef.get("modifications");

            Map<String, Object> subProfile = deepCloneProfile(baseProfile);
            String fullName = variantName + sizeSuffix;
            String subKey = normalizeKey(fullName);
            subProfile.put("name", fullName);
            subProfile.put("key", subKey);

            // UID aktualisieren
            Map<String, Object> typeObj = (Map<String, Object>) subProfile.get("type");
            String typusRaw = typeObj != null ? (String) typeObj.get("text") : null;
            UidCategory uidCat = mapTypusToUidCategory(typusRaw);
            subProfile.put("uid", uidCat.prefix + subKey);

            // Modifikationen anwenden
            if (mods != null)
            {
                applySubVariantModifications(subProfile, mods);
            }

            // Sonderregeln: alte Material-Regeln entfernen, gemeinsame + neue hinzufügen
            List<Map<String, String>> newRules = new ArrayList<>();
            // Gemeinsame Regeln
            newRules.addAll(commonRules);
            // Varianten-spezifische Regeln
            List<Map<String, Object>> addRulesRaw = (List<Map<String, Object>>) (mods != null ? mods.get("specialRulesAdd") : null);
            if (addRulesRaw != null)
            {
                for (Map<String, Object> r : addRulesRaw)
                {
                    newRules.add(Map.of("name", (String) r.get("name"), "rule", (String) r.get("rule")));
                }
            }
            subProfile.put("specialRules", newRules);

            result.add(subProfile);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void applySubVariantModifications(Map<String, Object> profile, Map<String, Object> mods)
    {
        // rsAdd: RS erhöhen
        if (mods.containsKey("rsAdd"))
        {
            int add = ((Number) mods.get("rsAdd")).intValue();
            Map<String, Object> armor = (Map<String, Object>) profile.get("armor");
            if (armor != null)
            {
                armor = new LinkedHashMap<>(armor);
                armor.put("RS", ((Number) armor.getOrDefault("RS", 0)).intValue() + add);
                profile.put("armor", armor);
            }
            else
            {
                profile.put("armor", Map.of("RS", add, "BE", 0));
            }
        }

        // lepAdd: LeP erhöhen
        if (mods.containsKey("lepAdd"))
        {
            int add = ((Number) mods.get("lepAdd")).intValue();
            List<Map<String, Object>> energies = (List<Map<String, Object>>) profile.get("energies");
            if (energies != null)
            {
                for (int i = 0; i < energies.size(); i++)
                {
                    if ("LEP".equals(energies.get(i).get("key")))
                    {
                        Map<String, Object> updated = new LinkedHashMap<>(energies.get(i));
                        Object val = updated.get("value");
                        if (val instanceof Number)
                        {
                            updated.put("value", ((Number) val).intValue() + add);
                        }
                        energies.set(i, updated);
                        break;
                    }
                }
            }
        }

        // tpAdd: TP aller Nahkampfwaffen erhöhen
        if (mods.containsKey("tpAdd"))
        {
            int add = ((Number) mods.get("tpAdd")).intValue();
            List<Map<String, Object>> weapons = (List<Map<String, Object>>) profile.get("weaponsMelee");
            if (weapons != null)
            {
                for (int i = 0; i < weapons.size(); i++)
                {
                    Map<String, Object> w = new LinkedHashMap<>(weapons.get(i));
                    Object tpObj = w.get("TP");
                    if (tpObj instanceof Map)
                    {
                        Map<String, Object> tp = new LinkedHashMap<>((Map<String, Object>) tpObj);
                        int currentPlus = tp.get("tpPlus") instanceof Number ? ((Number) tp.get("tpPlus")).intValue() : 0;
                        tp.put("tpPlus", currentPlus + add);
                        w.put("TP", tp);
                    }
                    weapons.set(i, w);
                }
            }
        }

        // skillAdd: einzelne Talente erhöhen (z.B. {"einschüchtern": 4})
        if (mods.containsKey("skillAdd"))
        {
            Map<String, Object> skillAdds = (Map<String, Object>) mods.get("skillAdd");
            Map<String, List<Map<String, Object>>> skills = (Map<String, List<Map<String, Object>>>) profile.get("skills");
            if (skills != null)
            {
                for (Map.Entry<String, Object> sa : skillAdds.entrySet())
                {
                    String skillKey = sa.getKey();
                    int add = ((Number) sa.getValue()).intValue();
                    for (List<Map<String, Object>> catSkills : skills.values())
                    {
                        for (int i = 0; i < catSkills.size(); i++)
                        {
                            if (skillKey.equals(catSkills.get(i).get("key")))
                            {
                                Map<String, Object> updated = new LinkedHashMap<>(catSkills.get(i));
                                updated.put("value", ((Number) updated.get("value")).intValue() + add);
                                catSkills.set(i, updated);
                            }
                        }
                    }
                }
            }
        }

        // allAttributesAdd: alle Attribute um N ändern
        if (mods.containsKey("allAttributesAdd"))
        {
            int add = ((Number) mods.get("allAttributesAdd")).intValue();
            List<Map<String, Object>> attrs = (List<Map<String, Object>>) profile.get("attributes");
            if (attrs != null)
            {
                for (int i = 0; i < attrs.size(); i++)
                {
                    Map<String, Object> updated = new LinkedHashMap<>(attrs.get(i));
                    updated.put("value", ((Number) updated.get("value")).intValue() + add);
                    attrs.set(i, updated);
                }
            }
        }

        // allSkillsAdd: alle Talente um N ändern
        if (mods.containsKey("allSkillsAdd"))
        {
            int add = ((Number) mods.get("allSkillsAdd")).intValue();
            Map<String, List<Map<String, Object>>> skills = (Map<String, List<Map<String, Object>>>) profile.get("skills");
            if (skills != null)
            {
                for (List<Map<String, Object>> catSkills : skills.values())
                {
                    for (int i = 0; i < catSkills.size(); i++)
                    {
                        Map<String, Object> updated = new LinkedHashMap<>(catSkills.get(i));
                        updated.put("value", ((Number) updated.get("value")).intValue() + add);
                        catSkills.set(i, updated);
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // Größen-Varianten-Erkennung und -Splitting
    // ---------------------------------------------------------------

    /**
     * Erkennt ob ein Stat-Block mehrere Größen-Varianten enthält.
     * Slash-Format: "**MU** 18/18/18" → 3 Varianten
     * Klammer-Format: "**MU** 18 (18) **KL** 14 (15)" → 2 Varianten
     */
    private static int detectSizeVariantCount(String baseBlock)
    {
        String flat = baseBlock.replace("\n", " ");
        // Slash-Format: **MU** 18/18/18
        Matcher slashM = Pattern.compile("\\*\\*MU\\*\\*\\s*(\\d+)/(\\d+)(?:/(\\d+))?").matcher(flat);
        if (slashM.find())
        {
            return slashM.group(3) != null ? 3 : 2;
        }
        // Klammer-Format: **MU** 18 (18) **KL** 14 (15)
        Matcher parenM = Pattern.compile("\\*\\*MU\\*\\*\\s*\\d+\\s*\\(\\d+\\)").matcher(flat);
        if (parenM.find())
        {
            return 2;
        }
        return 1;
    }

    /**
     * Extrahiert Variantennamen aus dem Profiltitel.
     * "Weiche Golemiden (mittel / groß* / riesig*)" → ["Weiche Golemiden (mittel)", "Weiche Golemiden (groß)", "Weiche Golemiden (riesig)"]
     * "Mittelgroßer Holzgolem / Großer Holzgolem / Riesiger Holzgolem" → die 3 Namen
     */
    private static List<String> extractSizeVariantNames(String name, int count)
    {
        if (count <= 1) return List.of(name);

        // Pattern 1: "Basisname (var1 / var2 / var3)" — Slash-getrennte Größen in Klammern
        Matcher parenSlashM = Pattern.compile("^(.+?)\\s*\\(([^)]+/[^)]+)\\)\\s*$").matcher(name);
        if (parenSlashM.matches())
        {
            String baseName = parenSlashM.group(1).trim();
            String[] parts = parenSlashM.group(2).split("\\s*/\\s*");
            if (parts.length == count)
            {
                List<String> names = new ArrayList<>();
                for (String part : parts)
                {
                    names.add(baseName + " (" + part.replaceAll("\\*", "").trim() + ")");
                }
                return names;
            }
        }

        // Pattern 2: "Name1 (Name2)" — Klammer-Format mit 2 Varianten, eigenständige Namen
        if (count == 2)
        {
            Matcher parenM = Pattern.compile("^(.+?)\\s*\\(([^)]+)\\)\\s*$").matcher(name);
            if (parenM.matches())
            {
                return List.of(parenM.group(1).trim(), parenM.group(2).trim());
            }
        }

        // Pattern 3: "Name1 / Name2 / Name3"
        String[] slashParts = name.split("\\s*/\\s*");
        if (slashParts.length == count)
        {
            return Arrays.stream(slashParts).map(String::trim).collect(Collectors.toList());
        }

        // Fallback: Nummerierung
        List<String> names = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            names.add(name + " (" + (i + 1) + ")");
        }
        return names;
    }

    /**
     * Erstellt eine Variante des baseBlock, in der alle N-fachen Varianten-Werte
     * durch den i-ten Wert ersetzt werden.
     * Slash-Format: "18/18/18" bei variantIndex=1 → "18"
     * Klammer-Format: "14 (15)" bei variantIndex=1 → "15"
     */
    private static String preprocessForSizeVariant(String baseBlock, int variantCount, int variantIndex)
    {
        if (variantCount == 2 && baseBlock.replace("\n", " ").matches(".*\\*\\*MU\\*\\*\\s*\\d+\\s*\\(\\d+\\).*"))
        {
            // Klammer-Format: "X (Y)" → Variante 0=X, Variante 1=Y
            return preprocessParenVariant(baseBlock, variantIndex);
        }

        // Slash-Format: "X/Y/Z"
        String tokenPattern = "[\\w+–\\-äöüÄÖÜß.]+";
        String slashSep = "\\s*/\\s*";

        StringBuilder regexBuilder = new StringBuilder();
        regexBuilder.append("(").append(tokenPattern).append(")");
        for (int i = 1; i < variantCount; i++)
        {
            regexBuilder.append(slashSep).append("(").append(tokenPattern).append(")");
        }
        Pattern p = Pattern.compile(regexBuilder.toString());

        Matcher m = p.matcher(baseBlock);
        StringBuilder sb = new StringBuilder();
        while (m.find())
        {
            boolean allPresent = true;
            for (int g = 1; g <= variantCount; g++)
            {
                if (m.group(g) == null) { allPresent = false; break; }
            }
            if (allPresent)
            {
                m.appendReplacement(sb, Matcher.quoteReplacement(m.group(variantIndex + 1)));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Verarbeitet Klammer-Format: "14 (15)" → bei Index 0 = "14", bei Index 1 = "15"
     * Auch: "13+1W6 (10+1W6)", "2W6 (2W6+3)", "mittel (lang)", "– (–)"
     */
    private static String preprocessParenVariant(String baseBlock, int variantIndex)
    {
        // Token: Zahl, Würfel, Textkürzel, Dash
        Pattern p = Pattern.compile("([\\w+–\\-äöüÄÖÜß.]+)\\s*\\(([\\w+–\\-äöüÄÖÜß.]+)\\)");
        Matcher m = p.matcher(baseBlock);
        StringBuilder sb = new StringBuilder();
        while (m.find())
        {
            String replacement = variantIndex == 0 ? m.group(1) : m.group(2);
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static Map<String, Object> parseBaseProfile(String name, String baseBlock,
                                                         Map<String, String> variants, String bookName)
    {
        Map<String, Object> profile = new LinkedHashMap<>();

        String key = normalizeKey(name);

        // key & name
        profile.put("key", key); // String intern, Ordinal-Konvertierung am Ende
        profile.put("name", name);
        profile.put("publication", bookName);

        // Zeilenumbruch-Bindestriche reparieren + gebrochene Kursiv-Blöcke zusammenführen
        String flat = baseBlock.replace("\n", " ").replaceAll("\\s+", " ");
        // "*Waf-* *fen:*" → "*Waffen:*" (PDF-Zeilenumbruch in kursivem Text)
        flat = flat.replaceAll("(\\w)-\\*\\s+\\*(\\w)", "$1$2");  // Bindestrich-Umbruch in Kursiv
        flat = flat.replaceAll("\\*\\s+\\*", " ");                // gebrochene Kursiv-Blöcke mergen
        // Publikationskürzel entfernen (AMA1112, AKO296, AMAIII127 etc.)
        flat = stripPubRefs(flat);

        // size (Größe)
        String size = extractBoldField(flat, "Größe:");
        if (size != null) profile.put("size", clean(size));

        // weight (Gewicht) - nur bis zum nächsten Bold-Feld, nicht bis **MU**
        String weight = extractWeightField(flat);
        if (weight != null) profile.put("weight", clean(weight));

        // sizeCategory
        String sizeCatRaw = extractBoldField(flat, "Größenkategorie:");
        if (sizeCatRaw != null)
        {
            String cat = clean(sizeCatRaw).toLowerCase().trim();
            // Nur den ersten Wert nehmen (vor " / je nach GK" etc.)
            cat = cat.split("\\s*/\\s*")[0].trim();
            if (cat.matches("winzig|klein|mittel|groß|riesig"))
            {
                profile.put("sizeCategory", toOrdinal(SIZE_CAT_ORDINALS, cat));
            }
        }

        // type (Zielformat: {text, category?})
        String typus = extractBoldField(flat, "Typus:");
        if (typus != null)
        {
            Map<String, Object> typeObj = new LinkedHashMap<>();
            typeObj.put("text", clean(typus));
            String tc = mapTargetCategory(typus);
            if (tc != null) typeObj.put("category", toOrdinal(TARGET_CAT_ORDINALS, tc));
            profile.put("type", typeObj);
        }

        // uid (aus UidCategory profile_* + key)
        UidCategory uidCat = mapTypusToUidCategory(typus);
        profile.put("uid", uidCat.prefix + key);

        // count (string[])
        String count = extractBoldField(flat, "Anzahl:");
        if (count != null) profile.put("count", List.of(clean(count)));

        // --- Attribute (Zielformat: attributes) ---
        List<Map<String, Object>> attributes = new ArrayList<>();
        for (String attr : new String[]{"MU", "KL", "IN", "CH", "FF", "GE", "KO", "KK"})
        {
            Integer val = extractAttrValue(flat, attr);
            if (val != null)
            {
                attributes.add(Map.of("key", toOrdinal(ATTR_ORDINALS, attr), "value", val));
            }
        }
        if (attributes.isEmpty()) return null; // kein gültiges Profil
        profile.put("attributes", attributes);

        // --- Energien (Zielformat: energies[]) ---
        List<Map<String, Object>> energies = new ArrayList<>();
        Integer lep = extractAttrValue(flat, "LeP");
        if (lep != null) energies.add(Map.of("key", toOrdinal(ATTR_ORDINALS, "LEP"), "value", lep));
        else if (flat.contains("**LeP**"))
        {
            Map<String, Object> lepMap = new LinkedHashMap<>();
            lepMap.put("key", toOrdinal(ATTR_ORDINALS, "LEP"));
            lepMap.put("value", null);
            energies.add(lepMap);
        }
        Integer asp = extractAttrValue(flat, "AsP");
        if (asp != null) energies.add(Map.of("key", toOrdinal(ATTR_ORDINALS, "ASP"), "value", asp));
        Integer kap = extractAttrValue(flat, "KaP");
        if (kap != null) energies.add(Map.of("key", toOrdinal(ATTR_ORDINALS, "KAP"), "value", kap));
        if (!energies.isEmpty()) profile.put("energies", energies);

        // --- INI ---
        Matcher iniM = INI_PATTERN.matcher(flat);
        if (iniM.find())
        {
            profile.put("ini", Map.of(
                "base", Integer.parseInt(iniM.group(1)),
                "diceCount", Integer.parseInt(iniM.group(2)),
                "diceFaces", Integer.parseInt(iniM.group(3))));
        }

        // --- Abgeleitete Werte (Zielformat: additionalAttributes[]) ---
        List<Map<String, Object>> additionalAttributes = new ArrayList<>();
        for (String[] pair : new String[][]{{"AW", "AW"}, {"VW", "VW"}, {"SK", "SK"}, {"ZK", "ZK"}, {"GS", "GS"}})
        {
            Integer val = extractAttrValue(flat, pair[0]);
            if (val != null) additionalAttributes.add(Map.of("key", toOrdinal(ATTR_ORDINALS, pair[1]), "value", val));
        }
        if (!additionalAttributes.isEmpty()) profile.put("additionalAttributes", additionalAttributes);

        // --- RS/BE (Zielformat: armor: {RS, BE}) ---
        Matcher rsM = RS_BE_PATTERN.matcher(flat);
        if (!rsM.find()) rsM = RS_BE_INLINE.matcher(flat);
        if (rsM.find())
        {
            profile.put("armor", Map.of(
                "RS", Integer.parseInt(rsM.group(1)),
                "BE", Integer.parseInt(rsM.group(2))));
        }

        // --- Aktionen (Zielformat: {aktionen: N}) ---
        Matcher aktM = AKTIONEN_PATTERN.matcher(flat);
        if (aktM.find())
        {
            profile.put("aktionen", Map.of("aktionen", Integer.parseInt(aktM.group(1))));
        }

        // --- Nahkampfwaffen (Zielformat: weaponsMelee mit TP als String) ---
        List<Map<String, Object>> melee = parseMeleeWeaponsProfile(flat);
        if (!melee.isEmpty())
        {
            enrichMeleeWeaponsWithKeys(melee, attributes);
            profile.put("weaponsMelee", melee);
        }

        // --- Fernkampfwaffen (Zielformat: weaponsRanged mit range-Objekt) ---
        List<Map<String, Object>> ranged = parseRangedWeaponsProfile(flat);
        if (!ranged.isEmpty()) profile.put("weaponsRanged", ranged);

        // --- Vorteile/Nachteile → merits + flaws ---
        String boonsRaw = extractBoldField(flat, "Vorteile/Nachteile:");
        if (boonsRaw != null)
        {
            boonsRaw = clean(boonsRaw);
            List<Map<String, Object>> charBoons = parseBoonsWithKeys(boonsRaw);
            List<Map<String, Object>> merits = charBoons.stream()
                .filter(b -> "merit".equals(b.get("category")) || "pet_merit".equals(b.get("category")))
                .peek(b -> b.remove("category"))
                .peek(ProfileExtractorMain::convertBoonKeysToOrdinals)
                .collect(Collectors.toList());
            List<Map<String, Object>> flaws = charBoons.stream()
                .filter(b -> "flaw".equals(b.get("category")) || "pet_flaw".equals(b.get("category")))
                .peek(b -> b.remove("category"))
                .peek(ProfileExtractorMain::convertBoonKeysToOrdinals)
                .collect(Collectors.toList());
            if (!merits.isEmpty()) profile.put("merits", merits);
            if (!flaws.isEmpty()) profile.put("flaws", flaws);
        }

        // --- Sonderfertigkeiten → abilities ---
        String abilitiesRaw = extractBoldField(flat, "Sonderfertigkeiten:");
        if (abilitiesRaw != null)
        {
            abilitiesRaw = clean(abilitiesRaw);
            List<Map<String, Object>> abilities = parseAbilitiesWithKeys(abilitiesRaw);
            abilities.forEach(ProfileExtractorMain::convertAbilityKeyToOrdinal);
            if (!abilities.isEmpty()) profile.put("abilities", abilities);
        }

        // --- Talente → skills (kategorisiert) ---
        String talentsRaw = extractBoldField(flat, "Talente:");
        if (talentsRaw != null)
        {
            Map<String, List<Map<String, Object>>> skills = parseTalentsCategorized(clean(talentsRaw));
            if (!skills.isEmpty()) profile.put("skills", skills);
        }

        // --- Zauber → spells ---
        String spellsRaw = extractBoldField(flat, "Zauber:");
        if (spellsRaw != null)
        {
            List<Map<String, Object>> spells = parseRawSkills(clean(spellsRaw), true);
            if (!spells.isEmpty()) profile.put("spells", spells);
        }

        // --- Liturgien → liturgies ---
        String liturgiesRaw = extractBoldField(flat, "Liturgien:");
        if (liturgiesRaw != null)
        {
            List<Map<String, Object>> liturgies = parseRawSkills(clean(liturgiesRaw), false);
            if (!liturgies.isEmpty()) profile.put("liturgies", liturgies);
        }

        // --- Beschwörungsschwierigkeit ---
        Matcher sumM = SUMMONING_PATTERN.matcher(flat);
        if (!sumM.find()) sumM = SUMMONING_PATTERN.matcher(baseBlock);
        if (sumM.find())
        {
            String sdVal = sumM.group(1).trim()
                .replace("\u2013", "-")
                .replaceAll("[^0-9\\-+]", "")
                .trim();
            try
            {
                profile.put("summoningDifficulty", Integer.parseInt(sdVal));
            }
            catch (NumberFormatException e)
            {
                LOGGER.debug("Unparseable summoningDifficulty: '{}'", sdVal);
            }
        }

        // --- Beute → loot (string[]) ---
        String loot = extractBoldField(flat, "Beute:");
        if (loot != null) profile.put("loot", List.of(clean(loot)));

        // --- Kampfverhalten ---
        String combat = extractBoldField(flat, "Kampfverhalten:");
        if (combat != null) profile.put("combatBehaviour", clean(combat));

        // --- Flucht → fleeThreshold ---
        String flee = extractBoldField(flat, "Flucht:");
        if (flee != null) profile.put("fleeThreshold", clean(flee));

        // --- Sprachen ---
        String langs = extractBoldField(flat, "Sprachen:");
        if (langs != null) profile.put("languages", clean(langs));

        // --- painThresholds als string[] ---
        String painRaw = extractBoldField(flat, "Schmerz \\+1 bei:");
        if (painRaw == null) painRaw = extractBoldField(flat, "Schmerz +1 bei:");
        if (painRaw != null)
        {
            List<String> thresholds = parsePainThresholds(clean(painRaw))
                .stream().map(String::valueOf).collect(Collectors.toList());
            if (!thresholds.isEmpty()) profile.put("painThresholds", thresholds);
        }

        // --- Tierkunde/Magiekunde/Sphärenkunde QS ---
        List<Map<String, Object>> checkQs = extractCheckQs(baseBlock);
        if (!checkQs.isEmpty())
        {
            // knowledgeSkillKey + knowledgeLevels aus erstem Eintrag
            Map<String, Object> first = checkQs.get(0);
            profile.put("knowledgeSkillKey", first.get("skill"));
            profile.put("checkQs", checkQs);
        }

        // --- Jagd ---
        Matcher jagdM = Pattern.compile("\\bJagd:\\s*([–\\-+]?\\d+)").matcher(flat);
        if (jagdM.find())
        {
            profile.put("hunt", jagdM.group(1).replace("\u2013", "-"));
        }

        // --- Sonderregeln (aus flat, da ### Sonderregeln: oft inline steht) ---
        List<Map<String, String>> rules = extractSpecialRules(flat);
        if (!rules.isEmpty()) profile.put("specialRules", rules);

        // --- Zusätzliche Dienste ---
        List<Map<String, String>> services = extractSpecialServices(flat);
        if (!services.isEmpty()) profile.put("specialServices", services);

        return profile;
    }

    // ---------------------------------------------------------------
    // Varianten-Abtrennung
    // ---------------------------------------------------------------

    private static class SplitResult
    {
        String baseBlock;
        Map<String, String> variants = new LinkedHashMap<>();
    }

    private static SplitResult splitVariants(String block)
    {
        SplitResult result = new SplitResult();
        StringBuilder base = new StringBuilder();
        String[] lines = block.split("\n");

        for (String line : lines)
        {
            Matcher vm = VARIANT_PATTERN.matcher(line.trim());
            if (vm.matches())
            {
                result.variants.put(vm.group(1), vm.group(2).trim());
            }
            else
            {
                base.append(line).append("\n");
            }
        }
        result.baseBlock = base.toString();
        return result;
    }

    // ---------------------------------------------------------------
    // Deep-Clone & Varianten-Anwendung
    // ---------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static Map<String, Object> deepCloneProfile(Map<String, Object> original)
    {
        try
        {
            String json = MAPPER.writeValueAsString(original);
            return MAPPER.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {});
        }
        catch (IOException e)
        {
            throw new RuntimeException("Deep clone failed", e);
        }
    }

    /**
     * Wendet einen Varianten-Text auf ein geklontes Profil an.
     * Format (Semikolon-getrennt):
     *   Attribute:  "IN 15 statt 14, KK 13 statt 12"
     *   Energien:   "LeP 36 statt 33"
     *   Waffen:     "Waffenlos AT 11 / PA 6, Speer AT 11 / PA 6, Wurfspeer FK 12"
     *   SFs:        "zusätzlich SF Name1, Name2"
     *   Talente:    "Einschüchtern 6 statt 4, ..."
     *   TP-Bonus:   "Waffenlos +1 TP"
     *   Zauber:     "SpellName 11 statt 9, ..."
     */
    @SuppressWarnings("unchecked")
    private static void applyVariant(Map<String, Object> profile, String variantText)
    {
        // Attribute-Keys und Energy-Keys für die Erkennung
        Set<String> attrKeys = Set.of("MU", "KL", "IN", "CH", "FF", "GE", "KO", "KK");
        Set<String> energyKeys = Set.of("LeP", "AsP", "KaP");

        // Semicolon-Segmente verarbeiten
        String[] segments = variantText.split(";");
        for (String segment : segments)
        {
            segment = segment.trim();
            if (segment.isEmpty()) continue;

            // "zusätzlich SF ..." → zusätzliche Sonderfertigkeiten
            if (segment.startsWith("zusätzlich") && segment.contains("SF"))
            {
                String sfPart = segment.replaceFirst("zusätzlich[e]?\\s+SF\\s+", "").trim();
                applyAdditionalAbilities(profile, sfPart);
                continue;
            }

            // Komma-getrennte Einträge innerhalb eines Segments
            String[] entries = segment.split(",");

            for (String entry : entries)
            {
                entry = entry.trim();
                if (entry.isEmpty()) continue;

                // "XX NN statt NN" — Attribut oder Energie oder Talent/Zauber
                Matcher stattMatcher = Pattern.compile(
                    "^(.+?)\\s+(\\d+)\\s+statt\\s+(\\d+)"
                ).matcher(entry);
                if (stattMatcher.find())
                {
                    String label = stattMatcher.group(1).trim();
                    int newVal = Integer.parseInt(stattMatcher.group(2));

                    if (attrKeys.contains(label))
                    {
                        applyAttributeChange(profile, label, newVal);
                    }
                    else if (energyKeys.contains(label))
                    {
                        applyEnergyChange(profile, label, newVal);
                    }
                    else
                    {
                        // Könnte Talent oder Zauber/Liturgie sein
                        boolean skillApplied = applySkillChange(profile, label, newVal);
                        if (!skillApplied)
                        {
                            applyMysticalSkillChange(profile, label, newVal);
                        }
                        LOGGER.debug("  statt: '{}' -> {} (skillApplied={})", label, newVal, skillApplied);
                    }
                    continue;
                }

                // "WeaponName AT NN / PA NN" — Nahkampfwaffe
                Matcher meleeMatcher = Pattern.compile(
                    "^(.+?)\\s+AT\\s+(\\d+)\\s*/\\s*PA\\s+(\\d+)$"
                ).matcher(entry);
                if (meleeMatcher.matches())
                {
                    applyMeleeWeaponChange(profile, meleeMatcher.group(1).trim(),
                        Integer.parseInt(meleeMatcher.group(2)),
                        Integer.parseInt(meleeMatcher.group(3)));
                    continue;
                }

                // "WeaponName FK NN" — Fernkampfwaffe
                Matcher rangedMatcher = Pattern.compile(
                    "^(.+?)\\s+FK\\s+(\\d+)$"
                ).matcher(entry);
                if (rangedMatcher.matches())
                {
                    applyRangedWeaponChange(profile, rangedMatcher.group(1).trim(),
                        Integer.parseInt(rangedMatcher.group(2)));
                    continue;
                }

                // "WeaponName +N TP" — TP-Bonus
                Matcher tpMatcher = Pattern.compile(
                    "^(.+?)\\s+\\+(\\d+)\\s+TP$"
                ).matcher(entry);
                if (tpMatcher.matches())
                {
                    applyTpBonus(profile, tpMatcher.group(1).trim(),
                        Integer.parseInt(tpMatcher.group(2)));
                    continue;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void applyAttributeChange(Map<String, Object> profile, String attrKey, int newVal)
    {
        List<Map<String, Object>> attrs = (List<Map<String, Object>>) profile.get("attributes");
        if (attrs == null) return;
        for (int i = 0; i < attrs.size(); i++)
        {
            if (attrKey.equals(attrs.get(i).get("key")))
            {
                Map<String, Object> updated = new LinkedHashMap<>(attrs.get(i));
                updated.put("value", newVal);
                attrs.set(i, updated);
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void applyEnergyChange(Map<String, Object> profile, String energyLabel, int newVal)
    {
        String energyKey = switch (energyLabel)
        {
            case "LeP" -> "LEP";
            case "AsP" -> "ASP";
            case "KaP" -> "KAP";
            default -> null;
        };
        if (energyKey == null) return;
        List<Map<String, Object>> energies = (List<Map<String, Object>>) profile.get("energies");
        if (energies == null) return;
        for (int i = 0; i < energies.size(); i++)
        {
            if (energyKey.equals(energies.get(i).get("key")))
            {
                Map<String, Object> updated = new LinkedHashMap<>(energies.get(i));
                updated.put("value", newVal);
                energies.set(i, updated);
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean applySkillChange(Map<String, Object> profile, String skillName, int newVal)
    {
        Map<String, List<Map<String, Object>>> skills = (Map<String, List<Map<String, Object>>>) profile.get("skills");
        if (skills == null) return false;
        String targetKey = SKILL_KEY_MAP.get(skillName);
        if (targetKey == null) return false;
        // In allen Kategorien suchen
        for (List<Map<String, Object>> catSkills : skills.values())
        {
            for (int i = 0; i < catSkills.size(); i++)
            {
                if (targetKey.equals(catSkills.get(i).get("key")))
                {
                    Map<String, Object> updated = new LinkedHashMap<>(catSkills.get(i));
                    updated.put("value", newVal);
                    catSkills.set(i, updated);
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static void applyMysticalSkillChange(Map<String, Object> profile, String spellName, int newVal)
    {
        // Versuche in charMysticalSkillsRaw (Zauber)
        if (applyRawSkillChange((List<Map<String, Object>>) profile.get("spells"), spellName, newVal))
            return;
        // Versuche in charLiturgiesRaw (Liturgien)
        applyRawSkillChange((List<Map<String, Object>>) profile.get("liturgies"), spellName, newVal);
    }

    private static boolean applyRawSkillChange(List<Map<String, Object>> skills, String name, int newVal)
    {
        if (skills == null) return false;
        for (int i = 0; i < skills.size(); i++)
        {
            if (name.equals(skills.get(i).get("name")))
            {
                Map<String, Object> updated = new LinkedHashMap<>(skills.get(i));
                updated.put("value", newVal);
                skills.set(i, updated);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static void applyMeleeWeaponChange(Map<String, Object> profile, String weaponName, int at, int pa)
    {
        List<Map<String, Object>> weapons = (List<Map<String, Object>>) profile.get("weaponsMelee");
        if (weapons == null) return;
        for (int i = 0; i < weapons.size(); i++)
        {
            if (weaponName.equals(weapons.get(i).get("name")))
            {
                Map<String, Object> updated = new LinkedHashMap<>(weapons.get(i));
                updated.put("AT", at);
                updated.put("PA", pa);
                weapons.set(i, updated);
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void applyRangedWeaponChange(Map<String, Object> profile, String weaponName, int fk)
    {
        List<Map<String, Object>> weapons = (List<Map<String, Object>>) profile.get("weaponsRanged");
        if (weapons == null) return;
        for (int i = 0; i < weapons.size(); i++)
        {
            if (weaponName.equals(weapons.get(i).get("name")))
            {
                Map<String, Object> updated = new LinkedHashMap<>(weapons.get(i));
                updated.put("FK", fk);
                weapons.set(i, updated);
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void applyTpBonus(Map<String, Object> profile, String weaponName, int bonus)
    {
        List<Map<String, Object>> weapons = (List<Map<String, Object>>) profile.get("weaponsMelee");
        if (weapons == null) return;
        for (int i = 0; i < weapons.size(); i++)
        {
            if (weaponName.equals(weapons.get(i).get("name")))
            {
                Map<String, Object> updated = new LinkedHashMap<>(weapons.get(i));
                Object tpObj = updated.get("TP");
                if (tpObj instanceof Map)
                {
                    Map<String, Object> tp = new LinkedHashMap<>((Map<String, Object>) tpObj);
                    int currentPlus = tp.get("tpPlus") instanceof Number ? ((Number) tp.get("tpPlus")).intValue() : 0;
                    tp.put("tpPlus", currentPlus + bonus);
                    updated.put("TP", tp);
                }
                weapons.set(i, updated);
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void applyAdditionalAbilities(Map<String, Object> profile, String sfText)
    {
        List<Map<String, Object>> abilities = (List<Map<String, Object>>) profile.get("abilities");
        if (abilities == null)
        {
            abilities = new ArrayList<>();
            profile.put("abilities", abilities);
        }

        // Komma-Split, aber Klammern respektieren
        String[] entries = sfText.split(",(?![^(]*\\))");
        for (String entry : entries)
        {
            entry = entry.trim();
            if (entry.isEmpty()) continue;

            Map<String, Object> ability = new LinkedHashMap<>();
            Matcher am = Pattern.compile("^(.+?)\\s*\\(([^)]+)\\)$").matcher(entry);
            if (am.matches())
            {
                ability.put("name", am.group(1).trim());
                ability.put("appliedFor", am.group(2).trim());
            }
            else
            {
                ability.put("name", entry);
            }

            String key = abilityNameToKey.get(ability.get("name"));
            if (key != null) ability.put("key", key);
            abilities.add(ability);
        }
    }

    // ---------------------------------------------------------------
    // Waffen-Parsing (strukturiertes Format)
    // ---------------------------------------------------------------

    // ---------------------------------------------------------------
    // Waffen-Erkennung: Standardwaffen identifizieren + KtW berechnen
    // ---------------------------------------------------------------

    /**
     * Versucht für jede Nahkampfwaffe im Profil eine Standardwaffe zu finden.
     * Falls gefunden: weaponKey und combatSkillValue werden hinzugefügt.
     *
     * Formeln:
     *   AT = KtW + floor((MU - 8) / 3) + AT_mod
     *   PA = ceil(KtW / 2) + floor((maxLeitattr - 8) / 3) + PA_mod
     *   TP_bonus = max(0, maxLeitattr - Schwelle)
     */
    @SuppressWarnings("unchecked")
    private static void enrichMeleeWeaponsWithKeys(List<Map<String, Object>> weapons,
                                                    List<Map<String, Object>> attributes)
    {
        if (weaponLookup.isEmpty()) return;

        // Attribut-Map aufbauen: Ordinal -> Wert (0/MU -> 12, 5/GE -> 13, etc.)
        // Zusätzlich String-Key-Map für Leitattribut-Lookup
        Map<String, Integer> attrByName = new HashMap<>();
        for (Map<String, Object> a : attributes)
        {
            Object keyObj = a.get("key");
            int value = ((Number) a.get("value")).intValue();
            // Key ist jetzt ein Ordinal (Integer) — über ATTR_INDEX_TO_KEY zurückmappen
            if (keyObj instanceof Number)
            {
                String name = ATTR_INDEX_TO_KEY.get(((Number) keyObj).intValue());
                if (name != null) attrByName.put(name, value);
            }
            else
            {
                attrByName.put(keyObj.toString(), value);
            }
        }
        int mu = attrByName.getOrDefault("MU", 8);

        for (int i = 0; i < weapons.size(); i++)
        {
            Map<String, Object> weapon = weapons.get(i);
            String weaponName = (String) weapon.get("name");
            if (weaponName == null) continue;

            // Lookup: Name (case-insensitive)
            Map<String, Object> knownWeapon = weaponLookup.get(weaponName.toLowerCase().trim());
            if (knownWeapon == null) continue;

            int atMod = ((Number) knownWeapon.getOrDefault("atModifier", 0)).intValue();
            int paMod = ((Number) knownWeapon.getOrDefault("paModifier", 0)).intValue();
            int weaponKey = ((Number) knownWeapon.get("weaponKey")).intValue();
            int combatSkillKey = ((Number) knownWeapon.get("combatSkillKey")).intValue();

            // Höchstes Leitattribut des Profils für diese Waffe bestimmen
            List<Map<String, Object>> bonuses = (List<Map<String, Object>>) knownWeapon.get("attributeTpBonuses");

            int profileAT = ((Number) weapon.get("AT")).intValue();
            int profilePA = weapon.get("PA") != null ? ((Number) weapon.get("PA")).intValue() : 0;

            // KtW aus AT berechnen: KtW = AT - floor((MU - 8) / 3) - AT_mod
            int muBonus = (int) Math.floor((mu - 8) / 3.0);
            int ktw = profileAT - muBonus - atMod;

            if (ktw < 0) continue; // ungültig, keine Standardwaffe

            // Verifikation über PA
            int maxLeitattr = getMaxLeitattribut(attrByName, bonuses);
            int leitBonus = (int) Math.floor((maxLeitattr - 8) / 3.0);
            int expectedPA = (int) Math.ceil(ktw / 2.0) + leitBonus + paMod;

            if (Math.abs(expectedPA - profilePA) > 1) continue; // PA passt nicht, Skip

            // Treffer! Waffe anreichern
            Map<String, Object> enriched = new LinkedHashMap<>(weapon);
            enriched.put("weaponKey", weaponKey);
            enriched.put("combatSkillKey", combatSkillKey);
            enriched.put("combatSkillValue", ktw);
            weapons.set(i, enriched);
        }
    }

    private static int getMaxLeitattribut(Map<String, Integer> attrMap,
                                           List<Map<String, Object>> bonuses)
    {
        int max = 8;
        if (bonuses != null)
        {
            for (Map<String, Object> bonus : bonuses)
            {
                int attrIndex = ((Number) bonus.get("attribute")).intValue();
                String attrKey = ATTR_INDEX_TO_KEY.get(attrIndex);
                if (attrKey != null)
                {
                    max = Math.max(max, attrMap.getOrDefault(attrKey, 8));
                }
            }
        }
        return max;
    }

    private static String formatTpString(int noOfDice, int sidesOfDice, int tpPlus)
    {
        String plus = tpPlus > 0 ? "+" + tpPlus : tpPlus < 0 ? String.valueOf(tpPlus) : "";
        return noOfDice + "W" + sidesOfDice + plus;
    }

    private static List<Map<String, Object>> parseMeleeWeaponsProfile(String flat)
    {
        List<Map<String, Object>> weapons = new ArrayList<>();
        Matcher wm = WEAPON_MELEE_PATTERN.matcher(flat);
        while (wm.find())
        {
            Map<String, Object> w = new LinkedHashMap<>();
            w.put("name", wm.group(1).trim());
            w.put("AT", Integer.parseInt(wm.group(2)));
            w.put("PA", wm.group(3) != null ? Integer.parseInt(wm.group(3)) : 0);
            w.put("TP", parseTP(wm.group(4).trim()));
            w.put("RW", toOrdinal(RANGE_ORDINALS, mapRangeKey(wm.group(5).trim())));
            weapons.add(w);
        }
        return weapons;
    }

    private static List<Map<String, Object>> parseRangedWeaponsProfile(String flat)
    {
        List<Map<String, Object>> weapons = new ArrayList<>();
        Matcher wr = WEAPON_RANGED_PATTERN.matcher(flat);
        while (wr.find())
        {
            Map<String, Object> w = new LinkedHashMap<>();
            w.put("name", wr.group(1).trim());
            w.put("FK", Integer.parseInt(wr.group(2)));
            if (wr.group(3) != null) w.put("LZ", Integer.parseInt(wr.group(3)));
            if (wr.group(4) != null)
            {
                w.put("TP", parseTP(wr.group(4).trim()));
            }
            // Range als Objekt
            String rw = wr.group(5) != null ? wr.group(5).trim() : "";
            String[] rangeParts = rw.split("/");
            Map<String, Object> range = new LinkedHashMap<>();
            try
            {
                if (rangeParts.length >= 1) range.put("short", Integer.parseInt(rangeParts[0].trim()));
                if (rangeParts.length >= 2) range.put("medium", Integer.parseInt(rangeParts[1].trim()));
                if (rangeParts.length >= 3) range.put("long", Integer.parseInt(rangeParts[2].trim()));
            }
            catch (NumberFormatException ignored) {}
            w.put("range", range);
            weapons.add(w);
        }
        return weapons;
    }

    private static Map<String, List<Map<String, Object>>> parseTalentsCategorized(String raw)
    {
        Map<String, List<Map<String, Object>>> categories = new LinkedHashMap<>();
        Pattern p = Pattern.compile(
            "([A-ZÄÖÜ][a-zäöüß]+(?:\\s*[&]\\s*[A-ZÄÖÜ][a-zäöüß]+)*(?:\\s+[A-ZÄÖÜ][a-zäöüß]+)*)\\s+" +
            "(?:\\([^)]*\\)\\s*,?\\s*)?(\\d+|–|-)"
        );
        Matcher m = p.matcher(raw);
        while (m.find())
        {
            String skillName = m.group(1).trim();
            String valStr = m.group(2);
            if (valStr.equals("–") || valStr.equals("-")) continue;

            String key = SKILL_KEY_MAP.get(skillName);
            if (key == null) continue;
            int value = Integer.parseInt(valStr);

            String cat = getSkillCategory(key);
            categories.computeIfAbsent(cat, k -> new ArrayList<>())
                .add(Map.of("key", toOrdinal(SKILL_ORDINALS, key), "value", value));
        }
        return categories;
    }

    private static String getSkillCategory(String skillKey)
    {
        return switch (skillKey)
        {
            case "fliegen", "gaukeleien", "klettern", "körperbeherrschung",
                 "kraftakt", "reiten", "schwimmen", "selbstbeherrschung",
                 "singen", "sinnesschärfe", "tanzen", "taschendiebstahl",
                 "verbergen", "zechen" -> "körper";
            case "bekehren_und_überzeugen", "betören", "einschüchtern",
                 "etikette", "gassenwissen", "menschenkenntnis",
                 "überreden", "verkleiden", "willenskraft" -> "gesellschaft";
            case "fährtensuchen", "fesseln", "fischen_und_angeln",
                 "orientierung", "pflanzenkunde", "tierkunde",
                 "wildnisleben" -> "natur";
            case "brett_und_glücksspiel", "geographie", "geschichtswissen",
                 "götter_und_kulte", "kriegskunst", "magiekunde",
                 "mechanik", "rechnen", "rechtskunde",
                 "sagen_und_legenden", "sphärenkunde", "sternkunde" -> "wissen";
            default -> "handwerk";
        };
    }

    private static List<Map<String, Object>> parseMeleeWeapons(String flat)
    {
        List<Map<String, Object>> weapons = new ArrayList<>();
        Matcher wm = WEAPON_MELEE_PATTERN.matcher(flat);
        while (wm.find())
        {
            Map<String, Object> w = new LinkedHashMap<>();
            w.put("name", wm.group(1).trim());
            w.put("at", Integer.parseInt(wm.group(2)));
            w.put("pa", wm.group(3) != null ? Integer.parseInt(wm.group(3)) : 0);
            w.put("tp", parseTP(wm.group(4).trim()));
            w.put("closeCombatRangeKey", mapRangeKey(wm.group(5).trim()));
            weapons.add(w);
        }
        return weapons;
    }

    private static List<Map<String, Object>> parseRangedWeapons(String flat)
    {
        List<Map<String, Object>> weapons = new ArrayList<>();
        Matcher wr = WEAPON_RANGED_PATTERN.matcher(flat);
        while (wr.find())
        {
            Map<String, Object> w = new LinkedHashMap<>();
            w.put("name", wr.group(1).trim());
            w.put("fk", Integer.parseInt(wr.group(2)));
            if (wr.group(3) != null) w.put("lz", Integer.parseInt(wr.group(3)));
            w.put("tp", parseTP(wr.group(4).trim()));
            w.put("rw", wr.group(5).trim());
            weapons.add(w);
        }
        return weapons;
    }

    private static Map<String, Object> parseTP(String tpStr)
    {
        Matcher m = TP_PARSE.matcher(tpStr);
        if (m.find())
        {
            Map<String, Object> tp = new LinkedHashMap<>();
            tp.put("noOfDice", Integer.parseInt(m.group(1)));
            tp.put("sidesOfDice", Integer.parseInt(m.group(2)));
            tp.put("tpPlus", m.group(3) != null ? Integer.parseInt(m.group(3)) : 0);
            return tp;
        }
        // Fallback: kein Würfel-Format (z.B. nur Zahl)
        return Map.of("noOfDice", 0, "sidesOfDice", 0, "tpPlus", 0);
    }

    private static String mapRangeKey(String rw)
    {
        return switch (rw.toLowerCase())
        {
            case "kurz" -> "kurz";
            case "mittel" -> "mittel";
            case "lang" -> "lang";
            case "überlang" -> "überlang";
            default -> rw.toLowerCase();
        };
    }

    // ---------------------------------------------------------------
    // Boons/Abilities/Skills mit Key-Resolution
    // ---------------------------------------------------------------

    // Freitext-Patterns die keine Boons sind
    private static final Pattern FREETEXT_PATTERN = Pattern.compile(
        "^(keine\\b|individuell\\b|typisch\\b|oft\\b|etwa\\b|weitere\\b|manchmal\\b|" +
        "häufig\\b|vereinzelt\\b|möglicherweise\\b|eventuell\\b|teilweise\\b|" +
        "AP müssen|Auswahl aus|ob ein|–$|-$|Siehe\\b|" +
        "eine Auswahl|eine individuelle|z\\.\\s*B\\.|sowie\\b|" +
        "vor allem\\b|passende\\b|Diverse\\b|" +
        "Zu vielen|Es handelt|hunderten|" +
        // Feld-Labels die in Boons gerutscht sind
        "Sonderfertigkeiten:|Sonderregeln:|Talente:|Zauber:|Liturgien:|" +
        "Tierkunde\\b|Magiekunde\\b|Schmerz \\+1|QS \\d|" +
        // Attribut/Kampfwerte
        "AT \\d|PA \\d|INI \\d|TP\\b|DK\\b|" +
        // Narrative Sätze
        "folgen sie|versuchen sie|entführen|verwandelt|Dukaten|Wappen|" +
        "Probe |Gegner|Angriff|der Meister|die Meisterin|" +
        "Nachteil möglich|Nachteilen des)",
        Pattern.CASE_INSENSITIVE
    );

    // Buch-Referenz-Pattern (AKO104, AMAII072, ABE007 etc.)
    private static final Pattern BOOK_REF = Pattern.compile(
        "\\s*[A-Z]{2,}\\d{2,}\\s*$"
    );

    // Inline Buch-Referenzen mitten im Wort: "DunkelaBeii117 sicht", "NatürKdH399 liche"
    private static final Pattern INLINE_BOOK_REF = Pattern.compile(
        "[A-Za-zÄÖÜäöü]*[A-Z][a-z]*[A-Z]\\w*\\d{2,}\\s*"
    );

    // DSA4-Zahlenwert: "Arroganz 10", "Goldgier 8"
    private static final Pattern DSA4_VALUE = Pattern.compile(
        "^(.+?)\\s+(\\d{1,2})\\s*$"
    );

    // Nicht-Boon-Einträge: Einzelbuchstaben, reine Zahlen, abgebrochene Wörter
    private static final Pattern NOT_A_BOON = Pattern.compile(
        "^.{1,3}$|" +                       // 1-3 Zeichen
        "^\\d+$|" +                          // reine Zahlen
        "^\\d+\\s*LeP|" +                    // "16 LeP" etc.
        "^.+-\\s*$|" +                       // endet mit Bindestrich (abgebrochen)
        "^[A-ZÄÖÜ][a-zäöüß]*-$|" +          // "Menschen-", "Natürlicher Rüstungs-"
        "^[(:•\\-]|" +                       // beginnt mit Satzzeichen
        "^Schmerz \\+1|" +                   // Schmerz-Feld
        "^Zauber:|" +                        // Zauber-Feld
        "^INI\\s|^AT\\s|^PA\\s"              // Kampfwerte
    );

    // Talent-/Zauber-/Sprach-Namen die als Boons/Abilities fehlgeparst werden
    private static final Set<String> KNOWN_SKILL_NAMES = Set.of(
        "Fliegen", "Klettern", "Körperbeherrschung", "Kraftakt", "Schwimmen",
        "Selbstbeherrschung", "Sinnesschärfe", "Verbergen", "Willenskraft",
        "Einschüchtern", "Menschenkenntnis", "Überreden", "Verkleiden",
        "Fährtensuchen", "Orientierung", "Tierkunde", "Wildnisleben",
        "Magiekunde", "Sphärenkunde", "Heilkunde Wunden", "Betören",
        "Armatrutz", "Dschinnenruf", "Elementarer Diener", "Gardianum",
        "Ignifaxius", "Manifesto", "Psychostabilis", "Blitz dich find",
        // Sprachen und Schriften (keine Abilities)
        "Garethi", "Thorwalsch", "Tulamidya", "Isdira", "Rogolan",
        "Rssah", "Bosparano", "Zhayad", "Neethaner"
    );

    // Maximale Boon-Name-Länge (alles drüber ist Text-Bleed)
    private static final int MAX_BOON_LENGTH = 80;

    private static List<Map<String, Object>> parseBoonsWithKeys(String raw)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] parts = splitBoonsFromFlaws(raw);

        for (int p = 0; p < parts.length; p++)
        {
            // Slash-Verbunde aufsplitten: "Dunkelsicht II/Angst vor Feuer"
            // Aber nur am "/" ohne Spaces (mit Spaces ist Vorteile/Nachteile-Trenner)
            String partText = parts[p];
            // Inline-Slashes aufbrechen (z.B. "Zauberer/Angst vor Feuer")
            // Aber NICHT in bekannten Boon-Namen wie "Vorteile/Nachteile"
            String[] subParts = splitInlineSlash(partText);

            for (String subPart : subParts)
            {
                // Komma und Semikolon als Trennzeichen (außerhalb von Klammern)
                String[] entries = subPart.split("[,;](?![^(]*\\))");
                for (String entry : entries)
                {
                    entry = cleanBoonEntry(entry);
                    if (entry.isEmpty()) continue;

                    // Zu lang = Text-Bleed, kein Boon
                    if (entry.length() > MAX_BOON_LENGTH) continue;

                    // Freitext ignorieren
                    if (FREETEXT_PATTERN.matcher(entry).find()) continue;

                    // Nicht-Boon-Einträge filtern
                    if (NOT_A_BOON.matcher(entry).find()) continue;

                    // Beginnt mit Kleinbuchstabe = kein deutscher Boon-Name
                    if (!entry.isEmpty() && Character.isLowerCase(entry.charAt(0))) continue;

                    // Talentwerte/Zauberwerte die als Boons geparst wurden
                    if (isSkillValue(entry)) continue;

                    // "(empfohlen: X, Y, Z)" → einzelne empfohlene Boons
                    if (entry.startsWith("(empfohlen"))
                    {
                        String inner = entry.replaceAll("^\\(empfohlen[er]*:\\s*", "")
                            .replaceAll("\\)$", "").trim();
                        for (String sub : inner.split(","))
                        {
                            sub = sub.trim();
                            if (sub.isEmpty()) continue;
                            Map<String, Object> rec = new LinkedHashMap<>();
                            rec.put("name", sub);
                            rec.put("remarks", "empfohlen");
                            String key = resolveBoonKey(sub);
                            if (key != null) rec.put("key", key);
                            boolean isPetRec = key != null && key.startsWith("pet_");
                            rec.put("category", p > 0
                                ? (isPetRec ? "pet_flaw" : "flaw")
                                : (isPetRec ? "pet_merit" : "merit"));
                            result.add(rec);
                        }
                        continue;
                    }

                    Map<String, Object> boon = parseSingleBoon(entry);
                    if (boon == null) continue;

                    // Kategorie bestimmen
                    String resolvedKey = (String) boon.get("key");
                    boolean isPet = resolvedKey != null && resolvedKey.startsWith("pet_");
                    boon.put("category", p > 0
                        ? (isPet ? "pet_flaw" : "flaw")
                        : (isPet ? "pet_merit" : "merit"));
                    result.add(boon);
                }
            }
        }
        return result;
    }

    private static String cleanBoonEntry(String entry)
    {
        entry = entry.trim();
        // Soft-Hyphens und unsichtbare Zeichen entfernen
        entry = entry.replace("\u00AD", "").replace("\u200B", "");
        // Buch-Referenzen am Ende entfernen (AKO104, ABE007 etc.)
        entry = BOOK_REF.matcher(entry).replaceAll("").trim();
        // Inline-Buch-Referenzen entfernen (NatürKdH399 liche → Natürliche)
        entry = INLINE_BOOK_REF.matcher(entry).replaceAll("").trim();
        // Sternchen entfernen
        entry = entry.replaceAll("\\*+", "").trim();
        // Markdown-Heading-Fragmente entfernen
        entry = entry.replaceAll("^#{1,4}\\s*", "").trim();
        // Punkt am Ende entfernen
        entry = entry.replaceAll("\\.$", "").trim();
        return entry;
    }

    private static boolean isSkillValue(String entry)
    {
        // "Kraftakt 7", "Willenskraft 9", "Armatrutz 10" etc.
        Matcher m = DSA4_VALUE.matcher(entry);
        if (m.matches())
        {
            return KNOWN_SKILL_NAMES.contains(m.group(1).trim());
        }
        return false;
    }

    private static String[] splitInlineSlash(String text)
    {
        // Splitte an "/" ohne umgebende Spaces, aber nicht innerhalb von Klammern
        // und nicht bei "Vorteile/Nachteile" oder "Fell/Gefieder"
        List<String> parts = new ArrayList<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (depth == 0 && c == '/' && i > 0 && i < text.length() - 1
                && text.charAt(i - 1) != ' ' && text.charAt(i + 1) != ' '
                && !isKnownSlashName(text, i))
            {
                parts.add(text.substring(start, i).trim());
                start = i + 1;
            }
        }
        parts.add(text.substring(start).trim());
        return parts.toArray(new String[0]);
    }

    private static boolean isKnownSlashName(String text, int slashPos)
    {
        // "Vorteile/Nachteile", "Fell/Gefieder", "Schuss/Wurf" etc.
        String around = text.substring(Math.max(0, slashPos - 15),
            Math.min(text.length(), slashPos + 15));
        return around.contains("Vorteile/Nachteile") ||
            around.contains("Fell/Gefieder") ||
            around.contains("Schuss/Wurf") ||
            around.contains("werden/werden");
    }

    // Römische Zahl am Ende: "Angst vor Feuer III", "Dunkelsicht II"
    // Auch mit Klammer: "Lichtempfindlich I (bei Shakagra ...)"
    private static final Pattern ROMAN_SUFFIX = Pattern.compile(
        "^(.+?)\\s+(I{1,3}V?|VI{0,3}|V)(?:\\s*\\(.*\\))?$"
    );

    private static Map<String, Object> parseSingleBoon(String entry)
    {
        Map<String, Object> boon = new LinkedHashMap<>();

        // 1) Römische Zahl am Ende erkennen und abtrennen
        String baseName = entry;
        Integer level = null;
        Matcher rm = ROMAN_SUFFIX.matcher(entry);
        if (rm.matches())
        {
            baseName = rm.group(1).trim();
            level = romanToInt(rm.group(2));
        }

        // 2) Variante erkennen: "Herausragender Sinn (Geruch)" oder "Begabung (Schwimmen)"
        //    baseName kann noch eine Klammer haben wenn kein Roman-Match
        Matcher vm = Pattern.compile("^(.+?)\\s*\\(([^)]+)\\)$").matcher(baseName);
        if (vm.matches())
        {
            String boonName = vm.group(1).trim();
            String variant = vm.group(2).trim();
            boon.put("name", boonName);
            boon.put("variantName", variant);
            if (level != null) boon.put("selectedLevel", level);

            String key = resolveBoonKey(boonName);
            if (key != null) boon.put("key", key);
            else trackUnresolved(unresolvedBoons, boonName);

            String varKey = boonVariantToKey.get(variant);
            if (varKey != null) boon.put("selectedVariantKey", varKey);
            return boon;
        }

        // 3) Mit Level (römische Zahl wurde oben erkannt)
        if (level != null)
        {
            boon.put("name", baseName);
            boon.put("selectedLevel", level);
            // Mehrstufige Auflösung: erst baseName, dann ganzen entry
            String key = resolveBoonKey(baseName);
            if (key == null) key = resolveBoonKey(entry);
            if (key != null) boon.put("key", key);
            else trackUnresolved(unresolvedBoons, baseName);
            return boon;
        }

        // 4) DSA4-Format: "Arroganz 10" → Schlechte Eigenschaft mit Zahlenwert
        Matcher dm = DSA4_VALUE.matcher(entry);
        if (dm.matches())
        {
            String boonName = dm.group(1).trim();
            String key = resolveBoonKey(boonName);
            if (key != null)
            {
                boon.put("name", boonName);
                boon.put("key", key);
                boon.put("dsa4Value", Integer.parseInt(dm.group(2)));
                return boon;
            }
        }

        // 5) Einfacher Name
        boon.put("name", entry);
        String key = resolveBoonKey(entry);
        if (key != null) boon.put("key", key);
        else trackUnresolved(unresolvedBoons, entry);
        return boon;
    }

    private static String[] splitBoonsFromFlaws(String raw)
    {
        // Trenne am " / " aber nur auf Top-Level (nicht innerhalb von Klammern)
        int depth = 0;
        for (int i = 0; i < raw.length() - 2; i++)
        {
            char c = raw.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (depth == 0 && c == ' ' && i + 2 < raw.length()
                && raw.charAt(i + 1) == '/' && raw.charAt(i + 2) == ' ')
            {
                return new String[]{raw.substring(0, i).trim(), raw.substring(i + 3).trim()};
            }
        }
        return new String[]{raw};
    }

    // Bereichs-Notation: "I-II", "I–III", "I+II" → höchste Stufe
    private static final Pattern RANGE_ROMAN = Pattern.compile(
        "^(.+?)\\s+(I{1,3}V?|VI{0,3}|V)\\s*[–\\-+]\\s*(I{1,3}V?|VI{0,3}|V)(?:\\s*\\(([^)]+)\\))?$"
    );

    // Einzelne römische Zahl mit optionaler Klammer: "Finte I (Klaue, Riemen)"
    private static final Pattern ABILITY_ROMAN = Pattern.compile(
        "^(.+?)\\s+(I{1,3}V?|VI{0,3}|V)(?:\\s*\\(([^)]+)\\))?$"
    );

    // Sprachen/Schriften die als Abilities fehlgeparst werden
    private static final Set<String> LANGUAGE_NAMES = Set.of(
        "Garethi", "Thorwalsch", "Tulamidya", "Isdira", "Rogolan", "Rssah",
        "Bosparano", "Zhayad", "Neethaner", "Dschadra", "Alaani", "Nujuka",
        "Mohisch", "Hruruzat", "Oloarkh", "Ologhaijan", "Angram", "Asdharia",
        "Tahaya", "Ferkina", "Trollisch", "Wudu", "Golp", "Aureliani",
        "Skyik", "Atak", "Mendlicumer", "Altes Alaani", "Pilzsprache",
        "Ur-Tulamidya", "Bine Machores"
    );

    private static List<Map<String, Object>> parseAbilitiesWithKeys(String raw)
    {
        List<Map<String, Object>> result = new ArrayList<>();

        // Semikolon und Slash (mit Spaces) als Trenner
        String[] topParts = raw.split("\\s*/\\s*|;");

        for (String part : topParts)
        {
            String[] entries = part.split(",(?![^(]*\\))");
            for (String entry : entries)
            {
                entry = cleanAbilityEntry(entry);
                if (entry.isEmpty()) continue;
                if (entry.length() > MAX_BOON_LENGTH) continue;
                if (NOT_A_BOON.matcher(entry).find()) continue;
                if (!entry.isEmpty() && Character.isLowerCase(entry.charAt(0))) continue;
                if (FREETEXT_PATTERN.matcher(entry).find()) continue;
                if (isSkillValue(entry)) continue;
                if (KNOWN_SKILL_NAMES.contains(entry)) continue;
                if (LANGUAGE_NAMES.contains(entry)
                    || LANGUAGE_NAMES.contains(entry.replaceAll("\\s+\\d+$", ""))) continue;
                // Sprachen/Schriften-Patterns
                if (entry.startsWith("Sprachen") || entry.startsWith("Schrift")
                    || entry.startsWith("Sprache ") || entry.startsWith("Sämtliche")
                    || entry.startsWith("Muttersprache") || entry.startsWith("Schriften:")
                    || entry.endsWith("-Zeichen") || entry.endsWith("-Runen")
                    || entry.endsWith("-Bilderschrift")) continue;
                // Schließende Klammer ohne öffnende = Fragment
                if (entry.startsWith(")") || (entry.contains(")") && !entry.contains("("))) continue;
                // Selbstbeherrschung/Willenskraft – (automatisch gelingt)
                if (entry.endsWith("–") || entry.endsWith("-")) continue;
                // Feld-Labels die durchgerutscht sind
                if (entry.startsWith("Sonderregeln:") || entry.startsWith("Talente:")
                    || entry.startsWith("Zauber:") || entry.startsWith("Kampf-SF:")) continue;

                Map<String, Object> ability = parseSingleAbility(entry);
                if (ability != null)
                {
                    result.add(ability);
                }
            }
        }
        return result;
    }

    private static String cleanAbilityEntry(String entry)
    {
        entry = cleanBoonEntry(entry);
        // Whitespace normalisieren (doppelte Leerzeichen)
        entry = entry.replaceAll("\\s+", " ").trim();
        // IIII → IV
        entry = entry.replace("IIII", "IV");
        // Text nach schließender Klammer abschneiden (Text-Bleed)
        // "Verbeißen (Biss) oft zutraulich..." → "Verbeißen (Biss)"
        Matcher closeParen = Pattern.compile("^(.+?\\))\\s+[a-zäöüA-ZÄÖÜ]{3,}").matcher(entry);
        if (closeParen.find())
        {
            // Prüfe ob der Text nach der Klammer ein neuer Satz/Wort ist (kein Ability-Name)
            String afterParen = entry.substring(closeParen.group(1).length()).trim();
            if (!afterParen.matches("^(I{1,3}V?|VI{0,3}|V)\\b.*") && afterParen.length() > 3)
            {
                entry = closeParen.group(1).trim();
            }
        }
        // Unbalancierte öffnende Klammer: "Mächtiger Schlag (Waffenlos" → schließen
        if (entry.contains("(") && !entry.contains(")"))
        {
            entry = entry + ")";
        }
        // Doppel-Klammern entfernen: "Wuchtschlag I (Waffenlos) (Wuchtschlag I-II (Waffenlos))"
        // → nur erste Klammer behalten
        Matcher doubleParen = Pattern.compile("^(.+?\\))\\s*\\(.+$").matcher(entry);
        if (doubleParen.matches())
        {
            entry = doubleParen.group(1).trim();
        }
        // Überzählige schließende Klammer am Ende: "Verbeißen (Biss))" → "Verbeißen (Biss)"
        if (entry.endsWith("))"))
        {
            entry = entry.substring(0, entry.length() - 1);
        }
        // "Wuchtschlag  I" → "Wuchtschlag I" (nochmal nach allen Transformationen)
        entry = entry.replaceAll("\\s+", " ").trim();
        return entry;
    }

    private static Map<String, Object> parseSingleAbility(String entry)
    {
        Map<String, Object> ability = new LinkedHashMap<>();

        // 1) Bereichs-Notation: "Wuchtschlag I-III (Waffenlos)"
        Matcher rm = RANGE_ROMAN.matcher(entry);
        if (rm.matches())
        {
            String aName = rm.group(1).trim();
            int maxLevel = romanToInt(rm.group(3));
            ability.put("name", aName);
            ability.put("selectedLevel", maxLevel);
            if (rm.group(4) != null)
            {
                ability.put("appliedFor", List.of(rm.group(4).trim().split("\\s*,\\s*")));
            }
            String key = resolveAbilityKey(aName);
            if (key != null) ability.put("key", key);
            else trackUnresolved(unresolvedAbilities, aName);
            return ability;
        }

        // 2) Einzelne römische Zahl mit optionaler Klammer: "Finte I (Klaue, Riemen)"
        Matcher am = ABILITY_ROMAN.matcher(entry);
        if (am.matches())
        {
            String aName = am.group(1).trim();
            ability.put("name", aName);
            ability.put("selectedLevel", romanToInt(am.group(2)));
            if (am.group(3) != null)
            {
                ability.put("appliedFor", List.of(am.group(3).trim().split("\\s*,\\s*")));
            }
            String key = resolveAbilityKey(aName);
            if (key != null) ability.put("key", key);
            else trackUnresolved(unresolvedAbilities, aName);
            return ability;
        }

        // 3) Name mit Klammer aber ohne Level: "Tradition (Gildenmagier)"
        Matcher vm = Pattern.compile("^(.+?)\\s*\\(([^)]+)\\)$").matcher(entry);
        if (vm.matches())
        {
            String aName = vm.group(1).trim();
            ability.put("name", aName);
            ability.put("appliedFor", List.of(vm.group(2).trim().split("\\s*,\\s*")));
            String key = resolveAbilityKey(aName);
            if (key != null) ability.put("key", key);
            else trackUnresolved(unresolvedAbilities, aName);
            return ability;
        }

        // 4) Einfacher Name: "Aufmerksamkeit"
        ability.put("name", entry);
        String key = resolveAbilityKey(entry);
        if (key != null) ability.put("key", key);
        else trackUnresolved(unresolvedAbilities, entry);
        return ability;
    }

    private static List<Map<String, Object>> parseTalentsWithKeys(String raw)
    {
        List<Map<String, Object>> prfSkills = new ArrayList<>();
        // "Einschüchtern 4, Handel 3, ..." — aber auch "Schwimmen 8 (10/12/13)" möglich
        Pattern p = Pattern.compile(
            "([A-ZÄÖÜ][a-zäöüß]+(?:\\s*[&]\\s*[A-ZÄÖÜ][a-zäöüß]+)*(?:\\s+[A-ZÄÖÜ][a-zäöüß]+)*)\\s+" +
            "(?:\\([^)]*\\)\\s*,?\\s*)?(\\d+|–|-)"
        );
        Matcher m = p.matcher(raw);
        while (m.find())
        {
            String skillName = m.group(1).trim();
            String valStr = m.group(2);
            if (valStr.equals("–") || valStr.equals("-")) continue;

            String key = SKILL_KEY_MAP.get(skillName);
            if (key != null)
            {
                prfSkills.add(Map.of("key", key, "value", Integer.parseInt(valStr)));
            }
            else
            {
                // rawSkill ohne Key
                prfSkills.add(Map.of("name", skillName, "value", Integer.parseInt(valStr)));
            }
        }
        return prfSkills;
    }

    /** Entfernt Publikationskürzel wie AMA1112, AKO296, AMAIII127 aus einem Text. */
    private static String stripPubRefs(String text)
    {
        if (text == null) return null;
        // Standalone: AMA1112, AKO296, AMAIII127
        text = text.replaceAll("\\b[A-Z]{2,4}(?:I{1,3})?\\d{2,4}\\b", "");
        // Inline mitten in Wörtern: "GeRSH162 danken" → "Gedanken"
        text = text.replaceAll("([a-zäöüß])[A-Z]{2,4}(?:I{1,3})?\\d{2,4}\\s+([a-zäöüß])", "$1$2");
        return text.replaceAll("\\s{2,}", " ").trim();
    }

    private static List<Map<String, Object>> parseRawSkills(String raw, boolean isMagical)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        // Pub-Referenzen vorab entfernen
        raw = stripPubRefs(raw);
        // "Angst auslösen 10, Blick in die Gedanken 10, ..."
        Pattern p = Pattern.compile("([^,]+?)\\s+(\\d+)(?:\\s*,|$)");
        Matcher m = p.matcher(raw);
        while (m.find())
        {
            String name = m.group(1).trim();
            int value = Integer.parseInt(m.group(2));
            MysticalSkillKey msk = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(name, isMagical);
            Map<String, Object> entry = new LinkedHashMap<>();
            if (msk != null) entry.put("key", msk.ordinal());
            entry.put("name", name);
            entry.put("value", value);
            result.add(entry);
        }
        return result;
    }

    // ---------------------------------------------------------------
    // painThresholds als number[]
    // ---------------------------------------------------------------

    private static List<Integer> parsePainThresholds(String raw)
    {
        // "25 LeP, 17 LeP, 8 LeP, 5 LeP oder weniger"
        // Nur die Zahlen vor "LeP" extrahieren
        List<Integer> thresholds = new ArrayList<>();
        Matcher m = PAIN_NUMBER_PATTERN.matcher(raw);
        while (m.find())
        {
            thresholds.add(Integer.parseInt(m.group(1)));
        }
        return thresholds;
    }

    // ---------------------------------------------------------------
    // CheckQs (Tierkunde/Magiekunde/Sphärenkunde)
    // ---------------------------------------------------------------

    private static List<Map<String, Object>> extractCheckQs(String block)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        Pattern section = Pattern.compile(
            "###\\s*(Tierkunde|Magiekunde|Sphärenkunde)\\s*\\(([^)]+)\\):?\\s*\n(.+?)(?=\n#{1,3}\\s|$)",
            Pattern.DOTALL
        );
        Matcher sm = section.matcher(block);
        while (sm.find())
        {
            String skill = sm.group(1).toLowerCase();
            String subject = sm.group(2).trim();
            String qsBlock = sm.group(3);

            List<String> levels = new ArrayList<>();
            Pattern qp = Pattern.compile("\\*\\*QS\\s*(\\d\\+?):\\*\\*\\s*(.+?)(?=\\*\\*QS|$)", Pattern.DOTALL);
            Matcher qm = qp.matcher(qsBlock);
            while (qm.find())
            {
                levels.add(clean(qm.group(2)));
            }

            if (!levels.isEmpty())
            {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("skill", skill);
                entry.put("subject", subject);
                entry.put("levels", levels);
                result.add(entry);
            }
        }
        return result;
    }

    // ---------------------------------------------------------------
    // Sonderregeln / Dienste
    // ---------------------------------------------------------------

    private static List<Map<String, String>> extractSpecialRules(String flat)
    {
        List<Map<String, String>> rules = new ArrayList<>();

        // ### Sonderregeln: steht oft inline im flat-Text
        int idx = flat.indexOf("### Sonderregeln:");
        if (idx < 0) idx = flat.indexOf("###Sonderregeln:");
        if (idx < 0) return rules;

        String rulesText = flat.substring(idx + "### Sonderregeln:".length()).trim();

        // Bis zum nächsten ###, oder Bold-Stat-Key (Erfahren/Kompetent/Flucht etc.) abschneiden
        Matcher endMatcher = Pattern.compile(
            "\\s*###\\s+(?!Sonderregeln)|\\s*\\*\\*(?:Erfahren|Kompetent|Meisterlich|Brillant|Flucht|Jagd):\\*\\*"
        ).matcher(rulesText);
        if (endMatcher.find())
        {
            rulesText = rulesText.substring(0, endMatcher.start());
        }

        // *RuleName:* Text bis zum nächsten *NextRule:* oder Ende
        // Pattern: ein oder mehrere * , optionale )/Leerzeichen, dann Name bis :, dann *
        Pattern rp = Pattern.compile(
            "\\*+\\)?\\s*([^*]+?):\\*\\s*(.+?)(?=\\s*\\*+\\)?\\s*[^*]+?:\\*|$)"
        );
        Matcher rm = rp.matcher(rulesText);
        while (rm.find())
        {
            String name = rm.group(1).trim()
                .replaceAll("^[*)]+\\s*", ""); // **) oder *) Prefix entfernen
            String rule = clean(rm.group(2));
            if (!name.isEmpty() && !rule.isEmpty())
            {
                rules.add(Map.of("name", name, "rule", rule));
            }
        }
        return rules;
    }

    private static List<Map<String, String>> extractSpecialServices(String flat)
    {
        List<Map<String, String>> services = new ArrayList<>();

        int idx = flat.indexOf("### Zusätzliche Dienste:");
        if (idx < 0) idx = flat.indexOf("###Zusätzliche Dienste:");
        if (idx < 0) return services;

        String servText = flat.substring(idx + "### Zusätzliche Dienste:".length()).trim();
        Matcher endMatcher = Pattern.compile("\\s*###\\s+(?!Zusätzliche)").matcher(servText);
        if (endMatcher.find())
        {
            servText = servText.substring(0, endMatcher.start());
        }

        Pattern sp = Pattern.compile(
            "\\*{1,3}\\)?\\s*([^*:]+?):\\*\\s*(.+?)(?=\\s*\\*{1,3}\\)?\\s*[^*:]+?:\\*|$)"
        );
        Matcher spm = sp.matcher(servText);
        while (spm.find())
        {
            String name = spm.group(1).trim().replaceAll("^[*)]+\\s*", "");
            String rule = clean(spm.group(2));
            if (!name.isEmpty() && !rule.isEmpty())
            {
                services.add(Map.of("name", name, "rule", rule));
            }
        }
        return services;
    }

    // ---------------------------------------------------------------
    // Key-Resolution
    // ---------------------------------------------------------------

    private static String resolveBoonKey(String germanName)
    {
        String key = boonNameToKey.get(germanName);
        if (key != null) return key;
        String baseName = germanName.replaceAll("\\s*\\([^)]+\\)$", "").trim();
        return boonNameToKey.get(baseName);
    }

    private static String resolveAbilityKey(String germanName)
    {
        String key = abilityNameToKey.get(germanName);
        if (key != null) return key;
        String baseName = germanName.replaceAll("\\s*\\([^)]+\\)$", "").trim();
        key = abilityNameToKey.get(baseName);
        if (key != null) return key;
        // Case-insensitive fallback
        for (Map.Entry<String, String> entry : abilityNameToKey.entrySet())
        {
            if (entry.getKey().equalsIgnoreCase(germanName) || entry.getKey().equalsIgnoreCase(baseName))
            {
                return entry.getValue();
            }
        }
        return null;
    }

    private static String mapTargetCategory(String typusRaw)
    {
        String lower = typusRaw.toLowerCase();
        for (Map.Entry<String, String> entry : TARGET_CATEGORY_MAP.entrySet())
        {
            if (lower.contains(entry.getKey())) return entry.getValue();
        }
        return null;
    }

    private static UidCategory mapTypusToUidCategory(String typusRaw)
    {
        if (typusRaw == null) return UidCategory.profile;
        String lower = typusRaw.toLowerCase();
        if (lower.contains("chimär")) return UidCategory.profile_chimaere;
        if (lower.contains("drach")) return UidCategory.profile_drache;
        if (lower.contains("daimonid")) return UidCategory.profile_daimonide;
        if (lower.contains("fee") || lower.contains("fae")) return UidCategory.profile_fee;
        if (lower.contains("golem")) return UidCategory.profile_golem;
        if (lower.contains("unelementar")) return UidCategory.profile_unelementar;
        if (lower.contains("elementar")) return UidCategory.profile_elementar;
        if (lower.contains("dämon")) return UidCategory.profile_daemon;
        if (lower.contains("geist")) return UidCategory.profile_geist;
        if (lower.contains("hirnlos")) return UidCategory.profile_hirnlose;
        if (lower.contains("vampir")) return UidCategory.profile_vampir;
        if (lower.contains("untot") && lower.contains("beseelt")) return UidCategory.profile_nicht_lebende_untot_beseelte;
        if (lower.contains("kulturschaff")) return UidCategory.profile_kulturschaffend;
        if (lower.contains("tier")) return UidCategory.profile_tier;
        if (lower.contains("pflanz")) return UidCategory.profile_pflanzen;
        return UidCategory.profile;
    }

    // ---------------------------------------------------------------
    // Hilfsmethoden
    // ---------------------------------------------------------------

    private static Integer extractAttrValue(String text, String attrKey)
    {
        Pattern p = Pattern.compile("\\*\\*" + Pattern.quote(attrKey) + "\\*\\*\\s*(\\d+|–|-)");
        Matcher m = p.matcher(text);
        if (m.find())
        {
            String val = m.group(1);
            if (!val.equals("–") && !val.equals("-")) return Integer.parseInt(val);
        }
        return null;
    }

    private static void putAttrDirect(Map<String, Object> profile, String flat,
                                       String attrKey, String fieldName)
    {
        Integer val = extractAttrValue(flat, attrKey);
        if (val != null) profile.put(fieldName, val);
    }

    // Terminator: stoppt bei nächstem Bold-Key-Feld (**Key:**), Heading, oder RS/BE
    private static final String FIELD_TERMINATOR =
        "(?=\\s*\\*\\*[A-ZÄÖÜ][^*]*?:\\*\\*|\\s*#{2,4}\\s|\\s*RS/BE:?\\s*\\d|$)";

    private static String extractBoldField(String text, String label)
    {
        Pattern p = Pattern.compile(
            "\\*\\*" + Pattern.quote(label) + "\\*\\*\\s*(.+?)" + FIELD_TERMINATOR,
            Pattern.DOTALL
        );
        Matcher m = p.matcher(text);
        if (m.find()) return m.group(1).trim();

        // Fallback ohne Bold
        Pattern p2 = Pattern.compile(
            Pattern.quote(label) + "\\s*(.+?)" + FIELD_TERMINATOR,
            Pattern.DOTALL
        );
        Matcher m2 = p2.matcher(text);
        return m2.find() ? m2.group(1).trim() : null;
    }

    private static String extractWeightField(String flat)
    {
        // Gewicht steht zwischen **Gewicht:** und **MU**
        Pattern p = Pattern.compile("\\*\\*Gewicht:\\*\\*\\s*(.+?)\\s*\\*\\*MU\\*\\*");
        Matcher m = p.matcher(flat);
        return m.find() ? m.group(1).trim() : null;
    }

    private static boolean isProfileSubSection(String headingLine)
    {
        for (String sub : PROFILE_SUB_SECTIONS)
        {
            if (headingLine.contains(sub)) return true;
        }
        return false;
    }

    private static boolean isSecretCategory(Path file)
    {
        Path relative = Paths.get(HEADINGS_BASE).relativize(file);
        if (relative.getNameCount() > 0)
        {
            String category = relative.getName(0).toString();
            return SECRET_CATEGORIES.contains(category);
        }
        return false;
    }

    private static boolean isExcluded(Path file)
    {
        for (Path component : file)
        {
            if (EXCLUDED_DIRECTORIES.contains(component.toString())) return true;
        }
        return false;
    }

    private static String extractBookName(Path headingsFile)
    {
        Path parent = headingsFile.getParent();
        while (parent != null)
        {
            String dirName = parent.getFileName().toString();
            if (dirName.matches(".*\\(\\d+\\)$"))
            {
                return dirName.replaceAll("\\s*\\(\\d+\\)$", "").trim();
            }
            parent = parent.getParent();
        }
        return headingsFile.getParent().getFileName().toString();
    }

    private static String sanitizeFileName(String name)
    {
        return name.replaceAll("[<>:\"/\\\\|?*]", "")
            .replaceAll("\\s+", "_")
            .trim();
    }

    private static String clean(String text)
    {
        if (text == null) return null;
        text = text.replaceAll("\\*+", "").replaceAll("\\s+", " ").trim();
        return stripPubRefs(text);
    }

    public static String normalizeKey(String name)
    {
        if (name == null) return null;
        String key = name.toLowerCase()
            .replaceAll("[^a-zäöüß0-9\\s]", "")
            .trim()
            .replaceAll("\\s+", "_");
        // Alias auflösen (z.B. "luftdschinn" → "dschinn_der_luft")
        return KEY_ALIASES.getOrDefault(key, key);
    }

    private static int romanToInt(String roman)
    {
        return switch (roman)
        {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            case "V" -> 5;
            default -> 1;
        };
    }

    private static void trackUnresolved(List<String> list, String name)
    {
        if (!list.contains(name)) list.add(name);
    }
}
