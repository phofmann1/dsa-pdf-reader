package de.pho.dsapdfreader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillVariantKey;

/**
 * Extrahiert Mystical Skills (Zauber, Rituale, Tricks, Segnungen, Liturgien, Zeremonien)
 * aus den _headings.md-Dateien und erzeugt JSON im gleichen Format wie der alte PDF-Extraktor.
 */
public class MysticalSkillExtractorMain
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    private static final String HEADINGS_BASE = "export/markdown/text";
    private static final String OUTPUT_BASE = "export/mysticalskills_md";
    private static final String REFERENCE_BASE =
        "D:/develop/project/angular/ng-dsa/src/app/_data/mysticalskills";

    // Enum-Ordinals
    private static Map<String, Integer> ATTR_ORDINALS = new LinkedHashMap<>();
    private static Map<String, Integer> TARGET_CAT_ORDINALS = new LinkedHashMap<>();

    // Bücher und was sie enthalten
    private static final List<BookConfig> BOOKS = List.of(
        new BookConfig("01 - Regeln/Grimorum Cantiones (156)/Grimorum Cantiones - 01 - Regeln",
            "_headings.md", "Grimorium", 35, true),
        new BookConfig("01 - Regeln/Divinarium Liturgia (188)/Divinarium Liturgia - 01 - Regeln",
            "_headings.md", "Divinarium", 49, false)
    );

    enum SkillType
    {
        SPELL(0, true, "#"),
        RITUAL(1, true, "#"),
        TRICK(2, true, "###"),
        BLESSING(11, false, "###"),
        LITURGY(12, false, "#"),
        CEREMONY(13, false, "#");

        final int categoryOrdinal;
        final boolean isMagic;
        final String headingPrefix;

        SkillType(int cat, boolean isMagic, String headingPrefix)
        {
            this.categoryOrdinal = cat;
            this.isMagic = isMagic;
            this.headingPrefix = headingPrefix;
        }
    }

    record BookConfig(String path, String headingsFile, String publication,
                      int publicationOrdinal, boolean isMagic) {}

    // --- Feld-Patterns ---
    private static final Pattern CHECK_PATTERN = Pattern.compile(
        "\\*\\*Probe:\\*\\*\\s*([A-ZÄÖÜ]{2}/[A-ZÄÖÜ]{2}/[A-ZÄÖÜ]{2})"
    );
    private static final Pattern VARIANT_PATTERN = Pattern.compile(
        "-\\s*(.+?)\\s*\\(FW\\s*(\\d+),\\s*(\\d+)\\s*AP\\):\\s*(.+)"
    );
    private static final Pattern ADV_CATEGORY_PATTERN = Pattern.compile(
        "\\*\\*Steigerungsfaktor:\\*\\*\\s*([A-E])"
    );

    // Feature-Mapping (Merkmal → MysticalSkillFeature ordinal)
    private static final Map<String, Integer> FEATURE_MAP = new LinkedHashMap<>();
    // Tradition-Mapping (Name → TraditionKey ordinal)
    private static final Map<String, Integer> TRADITION_MAP = new LinkedHashMap<>();

    static
    {
        // Features: Deutsch → MysticalSkillFeature ordinal
        // 12 magische Grundmerkmale
        FEATURE_MAP.put("Antimagie", 1);       // defensive
        FEATURE_MAP.put("Dämonisch", 5);        // demonic
        FEATURE_MAP.put("Einfluss", 7);         // influence
        FEATURE_MAP.put("Elementar", 10);       // elemental
        FEATURE_MAP.put("Heilung", 24);         // healing
        FEATURE_MAP.put("Hellsicht", 26);       // clairvoyance
        FEATURE_MAP.put("Illusion", 28);        // illusion
        FEATURE_MAP.put("Objekt", 38);          // object
        FEATURE_MAP.put("Sphären", 47);         // spheres
        FEATURE_MAP.put("Telekinese", 51);      // telekinetic
        FEATURE_MAP.put("Temporal", 52);        // temporal
        FEATURE_MAP.put("Verwandlung", 57);     // transformation
        // Göttliche Aspekte
        FEATURE_MAP.put("Allgemein", 0);        // common
        FEATURE_MAP.put("Blut", 4);             // blood
        FEATURE_MAP.put("Dschungel", 6);        // djungle
        FEATURE_MAP.put("Eis", 8);              // ice
        FEATURE_MAP.put("Ekstase", 9);          // extacy
        FEATURE_MAP.put("Erkenntnis", 11);      // epiphany
        FEATURE_MAP.put("Erz", 12);             // ore
        FEATURE_MAP.put("Feuer", 13);           // fire
        FEATURE_MAP.put("Flamme", 14);          // flame
        FEATURE_MAP.put("Freiheit", 15);        // freedom
        FEATURE_MAP.put("Freundschaft", 16);    // friendship
        FEATURE_MAP.put("Gemeinschaft", 17);    // community
        FEATURE_MAP.put("Gutes Gold", 19);      // good_gold
        FEATURE_MAP.put("Guter Kampf", 18);     // good_combat
        FEATURE_MAP.put("Handel", 20);          // trade
        FEATURE_MAP.put("Handwerk", 21);        // craft
        FEATURE_MAP.put("Harmonie", 22);        // harmony
        FEATURE_MAP.put("Härte", 23);           // hardness
        FEATURE_MAP.put("Heim", 25);            // home
        FEATURE_MAP.put("Hilfsbereitschaft", 27); // helpfulness
        FEATURE_MAP.put("Jagd", 29);            // hunt
        FEATURE_MAP.put("Kälte", 30);           // cold
        FEATURE_MAP.put("Kraft", 31);           // strength
        FEATURE_MAP.put("Landwirtschaft", 32);  // agriculture
        FEATURE_MAP.put("Leid", 33);            // suffering
        FEATURE_MAP.put("Magie", 34);           // magic
        FEATURE_MAP.put("Mond", 35);            // moon
        FEATURE_MAP.put("Namenlos", 36);        // nameless
        FEATURE_MAP.put("Natur", 37);           // nature
        FEATURE_MAP.put("Ordnung", 39);         // order
        FEATURE_MAP.put("Rausch", 40);          // intoxication
        FEATURE_MAP.put("Reise", 41);           // travel
        FEATURE_MAP.put("Schatten", 42);        // shadow
        FEATURE_MAP.put("Schicksal", 43);       // fate
        FEATURE_MAP.put("Schild", 44);          // shield
        FEATURE_MAP.put("Schlaf", 45);          // sleep
        FEATURE_MAP.put("Sonne", 46);           // sun
        FEATURE_MAP.put("Stein", 48);           // stone
        FEATURE_MAP.put("Sturm", 49);           // storm
        FEATURE_MAP.put("Tapferkeit", 50);      // courage
        FEATURE_MAP.put("Tier", 53);            // animal
        FEATURE_MAP.put("Tod", 54);             // death
        FEATURE_MAP.put("Traum", 55);           // dream
        FEATURE_MAP.put("Vergänglichkeit", 56); // transience
        FEATURE_MAP.put("Vision", 58);          // vision
        FEATURE_MAP.put("Wandel", 59);          // change
        FEATURE_MAP.put("Wildheit", 60);        // savagery
        FEATURE_MAP.put("Wind", 61);            // wind
        FEATURE_MAP.put("Winter", 62);          // winter
        FEATURE_MAP.put("Wissen", 63);          // knowledge
        FEATURE_MAP.put("Wogen", 64);           // waves
        FEATURE_MAP.put("Wölfe", 65);           // wolfs
        FEATURE_MAP.put("Zeit", 67);            // time
        FEATURE_MAP.put("Verlangen", 2);        // desire
        FEATURE_MAP.put("Bildung", 3);          // education

        // Traditions: Deutsch → TraditionKey ordinal
        TRADITION_MAP.put("allgemein", 0);          // all
        TRADITION_MAP.put("Angrosch", 1);
        TRADITION_MAP.put("Animisten", 2);          // animist
        TRADITION_MAP.put("Aves", 3);
        TRADITION_MAP.put("Zauberbarden", 4);       // zauberbarde
        TRADITION_MAP.put("Boron", 5);
        TRADITION_MAP.put("Druiden", 6);
        TRADITION_MAP.put("Efferd", 7);
        TRADITION_MAP.put("Elfen", 8);
        TRADITION_MAP.put("Firun", 9);
        TRADITION_MAP.put("Geoden", 10);            // geode
        TRADITION_MAP.put("Gildenmagier", 11);
        TRADITION_MAP.put("Goblinzauberinnen", 12);
        TRADITION_MAP.put("Hesinde", 13);
        TRADITION_MAP.put("Hexen", 14);
        TRADITION_MAP.put("Ifirn", 15);
        TRADITION_MAP.put("Ingerimm", 16);
        TRADITION_MAP.put("Intuitive Zauberer", 17);
        TRADITION_MAP.put("Kor", 18);
        TRADITION_MAP.put("Kristallomanten", 19);
        TRADITION_MAP.put("Levthan", 20);
        TRADITION_MAP.put("Marbo", 21);
        TRADITION_MAP.put("Meisterhandwerker", 22);
        TRADITION_MAP.put("Namenloser", 23);
        TRADITION_MAP.put("Nandus", 24);
        TRADITION_MAP.put("Peraine", 25);
        TRADITION_MAP.put("Phex", 26);
        TRADITION_MAP.put("Praios", 27);
        TRADITION_MAP.put("Qabalya", 28);
        TRADITION_MAP.put("Rahja", 29);
        TRADITION_MAP.put("Rondra", 30);
        TRADITION_MAP.put("Schamanen", 31);
        TRADITION_MAP.put("Scharlatane", 32);
        TRADITION_MAP.put("Schelme", 33);
        TRADITION_MAP.put("Swafnir", 34);
        TRADITION_MAP.put("Travia", 35);
        TRADITION_MAP.put("Tsa", 36);
        TRADITION_MAP.put("Zauberalchimisten", 37);
        TRADITION_MAP.put("Zaubertänzer", 38);
        TRADITION_MAP.put("Zibilja", 39);
        TRADITION_MAP.put("Borbaradianer", 47);
        TRADITION_MAP.put("Nachtalben", 59);
        TRADITION_MAP.put("Bannzeichner", 58);
        TRADITION_MAP.put("Runenschöpfer", 49);
        // Göttliche Kulte
        TRADITION_MAP.put("Chr'Ssir'Ssr", 51);
        TRADITION_MAP.put("Gravesh", 52);
        TRADITION_MAP.put("H'Szint", 53);
        TRADITION_MAP.put("Numinoru", 54);
        TRADITION_MAP.put("Shinxir", 55);
        TRADITION_MAP.put("Tairach", 56);
        TRADITION_MAP.put("Zsahh", 57);
    }

    public static void main(String[] args) throws Exception
    {
        LOGGER.info("=== Mystical Skill Extraktor ===");
        loadEnumOrdinals();

        for (BookConfig book : BOOKS)
        {
            Path headingsPath = Paths.get(HEADINGS_BASE).resolve(book.path()).resolve(book.headingsFile());

            if (!Files.exists(headingsPath))
            {
                LOGGER.warn("Headings nicht gefunden: {}", headingsPath);
                continue;
            }

            String content = Files.readString(headingsPath, StandardCharsets.UTF_8);

            // Alle Skills aus dem Buch extrahieren
            List<Map<String, Object>> allSkills = extractAllSkills(content, book);
            LOGGER.info("  {} total: {} Skills", book.publication(), allSkills.size());

            // Nach Kategorie (bestimmt durch Key-Prefix) aufteilen
            Map<String, List<Map<String, Object>>> byCategory = new LinkedHashMap<>();
            for (Map<String, Object> skill : allSkills)
            {
                String cat = (String) skill.remove("_categoryFile");
                if (cat == null) cat = "UNKNOWN";
                byCategory.computeIfAbsent(cat, k -> new ArrayList<>()).add(skill);
            }

            for (Map.Entry<String, List<Map<String, Object>>> entry : byCategory.entrySet())
            {
                String fileName = book.publication() + "_" + entry.getKey() + ".json";
                Path outputPath = Paths.get(OUTPUT_BASE).resolve(fileName);
                MAPPER.writeValue(outputPath.toFile(), entry.getValue());
                LOGGER.info("    {}: {} Skills", entry.getKey(), entry.getValue().size());

                // Vergleich mit Referenz
                Path refPath = Paths.get(REFERENCE_BASE).resolve(book.publication() + "_" + entry.getKey() + ".json");
                if (Files.exists(refPath))
                {
                    compareWithReference(refPath, entry.getValue(), entry.getKey());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void compareWithReference(Path refPath, List<Map<String, Object>> extracted, String category)
    {
        try
        {
            List<Map<String, Object>> reference = MAPPER.readValue(refPath.toFile(), new TypeReference<>() {});
            Map<Integer, Map<String, Object>> refByKey = new LinkedHashMap<>();
            for (Map<String, Object> r : reference) refByKey.put(((Number) r.get("key")).intValue(), r);

            Map<Integer, Map<String, Object>> extByKey = new LinkedHashMap<>();
            for (Map<String, Object> e : extracted) extByKey.put(((Number) e.get("key")).intValue(), e);

            // Fehlende Keys
            List<Integer> missing = new ArrayList<>(refByKey.keySet());
            missing.removeAll(extByKey.keySet());
            List<Integer> extra = new ArrayList<>(extByKey.keySet());
            extra.removeAll(refByKey.keySet());

            if (!missing.isEmpty())
            {
                List<String> missingNames = missing.stream()
                    .map(k -> refByKey.get(k).get("name") + " (" + k + ")")
                    .toList();
                LOGGER.warn("    FEHLEND in {}: {}", category, missingNames);
            }
            if (!extra.isEmpty())
            {
                List<String> extraNames = extra.stream()
                    .map(k -> extByKey.get(k).get("name") + " (" + k + ")")
                    .toList();
                LOGGER.warn("    EXTRA in {}: {}", category, extraNames);
            }

            // Feld-Vergleich für gemeinsame Keys (Stichprobe: erste 5 Unterschiede)
            int diffs = 0;
            for (int key : refByKey.keySet())
            {
                if (!extByKey.containsKey(key)) continue;
                Map<String, Object> ref = refByKey.get(key);
                Map<String, Object> ext = extByKey.get(key);

                for (String field : new String[]{"check", "advancementCategory", "features", "traditions"})
                {
                    Object refVal = ref.get(field);
                    Object extVal = ext.get(field);
                    if (refVal != null && !refVal.equals(extVal) && diffs < 5)
                    {
                        LOGGER.info("    DIFF {}.{}: ref={} vs ext={}", ref.get("name"), field, refVal, extVal);
                        diffs++;
                    }
                }
            }

            LOGGER.info("    Vergleich {}: ref={}, ext={}, fehlend={}, extra={}",
                category, reference.size(), extracted.size(), missing.size(), extra.size());
        }
        catch (IOException e)
        {
            LOGGER.error("Vergleich fehlgeschlagen: {}", e.getMessage());
        }
    }

    private static List<Map<String, Object>> extractAllSkills(String content, BookConfig book)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] lines = content.split("\n");

        // Finde alle Skill-Blöcke: # Heading oder ### Heading gefolgt von Stat-Feldern
        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i].trim();

            // Skill-Heading erkennen (# oder ###)
            String name = null;
            boolean isTrickLevel = false;
            if (line.matches("^### [A-ZÄÖÜ].+"))
            {
                name = line.substring(4).trim();
                isTrickLevel = true;
            }
            else if (line.matches("^# [A-ZÄÖÜ].+") && !line.startsWith("# Impressum")
                && !line.startsWith("# Inhalts") && !line.startsWith("# Vorwort")
                && !line.startsWith("# u4") && !line.startsWith("# Merkmale")
                && !line.startsWith("# DIE ") && !line.startsWith("# Vom "))
            {
                name = line.substring(2).trim();
            }

            if (name == null) continue;

            // Sammle den Block bis zum nächsten Heading gleicher/höherer Ebene
            StringBuilder block = new StringBuilder();
            for (int j = i + 1; j < lines.length; j++)
            {
                String nextLine = lines[j].trim();
                if (isTrickLevel && nextLine.matches("^### [A-ZÄÖÜ].+")) break;
                if (!isTrickLevel && nextLine.matches("^# [A-ZÄÖÜ].+")) break;
                block.append(lines[j]).append("\n");
            }

            String flat = block.toString().replace("\n", " ").replaceAll("\\s+", " ");

            // Nur Skill-Einträge (haben Merkmal oder Wirkung)
            boolean hasFields = flat.contains("**Merkmal:**") || flat.contains("**Wirkung:**");
            if (!hasFields) continue;

            // Kategorie über Key-Prefix bestimmen
            MysticalSkillKey msk = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(name, book.isMagic());
            if (msk == null)
            {
                // Versuche das Gegenteil (manche Liturgien/Zeremonien haben magische Namen)
                msk = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(name, !book.isMagic());
            }
            if (msk == null) continue;

            SkillType type = categorizeByKey(msk, isTrickLevel, book.isMagic());

            Map<String, Object> skill = parseSkill(name, flat, type, book);
            if (skill != null)
            {
                // Datei-Kategorie für spätere Aufteilung
                skill.put("_categoryFile", mapToFileName(type));
                result.add(skill);
            }
        }

        return result;
    }

    private static Map<String, Object> parseSkill(String name, String flat, SkillType type, BookConfig book)
    {
        Map<String, Object> skill = new LinkedHashMap<>();

        // Key
        MysticalSkillKey msk = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(name, type.isMagic);
        if (msk == null)
        {
            LOGGER.debug("  Key nicht gefunden für: {} ({})", name, type);
            return null;
        }
        skill.put("key", msk.ordinal());
        skill.put("name", name);

        // Probe (Check) — nicht bei Tricks/Segnungen
        Matcher checkM = CHECK_PATTERN.matcher(flat);
        if (checkM.find())
        {
            String[] attrs = checkM.group(1).split("/");
            List<Object> check = new ArrayList<>();
            for (String a : attrs)
            {
                Object ord = ATTR_ORDINALS.get(a.trim());
                check.add(ord != null ? ord : a.trim());
            }
            skill.put("check", check);
        }

        // Casting duration
        Map<String, Object> casting = extractCastingDuration(flat, type);
        if (casting != null) skill.put("casting", casting);

        // Category
        skill.put("category", type.categoryOrdinal);

        // Cost
        Map<String, Object> cost = extractCost(flat, type);
        if (cost != null) skill.put("skillCost", cost);

        // Range
        Map<String, Object> range = extractRange(flat);
        if (range != null) skill.put("skillRange", range);

        // Duration
        Map<String, Object> duration = extractDuration(flat);
        if (duration != null) skill.put("skillDuration", duration);

        // Target categories
        List<Integer> targets = extractTargetCategories(flat);
        if (!targets.isEmpty()) skill.put("targetCategories", targets);

        // Features (Merkmal)
        List<Integer> features = extractFeatures(flat);
        if (!features.isEmpty()) skill.put("features", features);

        // Advancement category
        Matcher advM = ADV_CATEGORY_PATTERN.matcher(flat);
        if (advM.find())
        {
            int adv = switch (advM.group(1))
            {
                case "A" -> 1; case "B" -> 2; case "C" -> 3; case "D" -> 4; case "E" -> 5;
                default -> 0;
            };
            skill.put("advancementCategory", adv);
        }

        // Traditions (Verbreitung)
        List<Integer> traditions = extractTraditions(flat);
        if (!traditions.isEmpty()) skill.put("traditions", traditions);

        // Spell variants (Erweiterungen)
        List<Map<String, Object>> variants = extractVariants(flat, msk);
        if (!variants.isEmpty()) skill.put("spellVariants", variants);

        // Publication
        skill.put("publication", book.publicationOrdinal());

        return skill;
    }

    private static SkillType categorizeByKey(MysticalSkillKey msk, boolean isTrickLevel, boolean isMagic)
    {
        String keyName = msk.name();
        if (keyName.startsWith("trick_") || keyName.startsWith("jest_")) return SkillType.TRICK;
        if (keyName.startsWith("spell_") || keyName.startsWith("curse_") || keyName.startsWith("elfensong_")
            || keyName.startsWith("melody_") || keyName.startsWith("dance_") || keyName.startsWith("zibilja_")
            || keyName.startsWith("power_")) return SkillType.SPELL;
        if (keyName.startsWith("ritual_") || keyName.startsWith("ritualofdominion_")
            || keyName.startsWith("goblinritual_") || keyName.startsWith("rune_")
            || keyName.startsWith("bansign_")) return SkillType.RITUAL;
        if (keyName.startsWith("blessing_")) return SkillType.BLESSING;
        if (keyName.startsWith("liturgy_")) return SkillType.LITURGY;
        if (keyName.startsWith("ceremony_")) return SkillType.CEREMONY;
        // Fallback
        if (isTrickLevel) return isMagic ? SkillType.TRICK : SkillType.BLESSING;
        return isMagic ? SkillType.SPELL : SkillType.LITURGY;
    }

    private static String mapToFileName(SkillType type)
    {
        return switch (type)
        {
            case SPELL -> "SPELLS_GRIMORIUM";
            case RITUAL -> "RITUALS_GRIMORIUM";
            case TRICK -> "TRICKS_GRIMORIUM";
            case BLESSING -> "BLESSING_DIVINARIUM";
            case LITURGY -> "LITURGY_DIVINARIUM";
            case CEREMONY -> "CEREMONY_DIVINARIUM";
        };
    }

    // ---------------------------------------------------------------
    // Feld-Extraktoren
    // ---------------------------------------------------------------

    private static Map<String, Object> extractCastingDuration(String flat, SkillType type)
    {
        String label = type.isMagic ? "Zauberdauer:" : "Liturgiedauer:";
        String raw = extractField(flat, label);
        if (raw == null) return null;

        Map<String, Object> result = new LinkedHashMap<>();
        raw = raw.toLowerCase().trim();

        if (raw.contains("aktion"))
        {
            Matcher m = Pattern.compile("(\\d+)\\s*aktion").matcher(raw);
            if (m.find())
            {
                result.put("castingDurationUnit", 4); // aktion
                result.put("castingDuration", Integer.parseInt(m.group(1)));
            }
        }
        else if (raw.contains("minute"))
        {
            Matcher m = Pattern.compile("(\\d+)\\s*minute").matcher(raw);
            if (m.find())
            {
                result.put("castingDurationUnit", 7); // minute
                result.put("castingDuration", Integer.parseInt(m.group(1)));
            }
        }
        else if (raw.contains("stunde"))
        {
            Matcher m = Pattern.compile("(\\d+)\\s*stunde").matcher(raw);
            if (m.find())
            {
                result.put("castingDurationUnit", 8); // stunde
                result.put("castingDuration", Integer.parseInt(m.group(1)));
            }
        }

        return result.isEmpty() ? null : result;
    }

    private static Map<String, Object> extractCost(String flat, SkillType type)
    {
        String label = type.isMagic ? "AsP-Kosten:" : "KaP-Kosten:";
        String raw = extractField(flat, label);
        if (raw == null) return null;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("costText", raw);

        // Basis-Kosten extrahieren
        Matcher costM = Pattern.compile("(\\d+)\\s*(?:AsP|KaP)").matcher(raw);
        if (costM.find())
        {
            result.put("cost", Integer.parseInt(costM.group(1)));
        }
        result.put("costMin", 0);
        result.put("plusCost", 0);
        result.put("plusCostPerMultiplier", 0);
        result.put("plusCostPerMax", 0);
        result.put("plusCostHalfBase", false);
        result.put("permanentCost", 0);

        // "X AsP + Y AsP pro Z" Pattern
        Matcher plusM = Pattern.compile("\\+\\s*(\\d+)\\s*(?:AsP|KaP)\\s*pro").matcher(raw);
        if (plusM.find())
        {
            result.put("plusCost", Integer.parseInt(plusM.group(1)));
        }

        return result;
    }

    private static Map<String, Object> extractRange(String flat)
    {
        String raw = extractField(flat, "Reichweite:");
        if (raw == null) return null;

        Map<String, Object> result = new LinkedHashMap<>();
        String lower = raw.toLowerCase().trim();

        if (lower.equals("selbst"))
        {
            result.put("range", 0);
            result.put("rangeUnit", 14); // selbst
        }
        else if (lower.startsWith("berührung") || lower.startsWith("ber\u00FChrung"))
        {
            result.put("range", 0);
            result.put("rangeUnit", 15); // berührung
        }
        else if (lower.contains("schritt"))
        {
            Matcher m = Pattern.compile("(\\d+)\\s*schritt").matcher(lower);
            if (m.find())
            {
                result.put("range", Integer.parseInt(m.group(1)));
                result.put("rangeUnit", 16); // meter/schritt
            }
        }
        else if (lower.contains("sicht"))
        {
            result.put("range", 0);
            result.put("rangeUnit", 17); // sicht
        }

        if (result.isEmpty()) return null;
        result.put("isPerQs", false);
        result.put("isRadius", false);
        return result;
    }

    private static Map<String, Object> extractDuration(String flat)
    {
        String raw = extractField(flat, "Wirkungsdauer:");
        if (raw == null) return null;

        Map<String, Object> result = new LinkedHashMap<>();
        String lower = raw.toLowerCase().trim();

        if (lower.equals("sofort"))
        {
            result.put("duration", 0);
            result.put("durationUnit", 0); // sofort
        }
        else if (lower.contains("aufrechterhaltend"))
        {
            result.put("duration", 0);
            result.put("durationUnit", 1); // aufrechterhaltend
        }
        else if (lower.contains("permanent"))
        {
            result.put("duration", 0);
            result.put("durationUnit", 2); // permanent
        }
        else if (lower.contains("minute"))
        {
            Matcher m = Pattern.compile("(\\d+)\\s*minute").matcher(lower);
            if (m.find())
            {
                result.put("duration", Integer.parseInt(m.group(1)));
                result.put("durationUnit", 7); // minute
            }
        }
        else if (lower.contains("kr") || lower.contains("kampfrunde"))
        {
            Matcher m = Pattern.compile("(\\d+)\\s*(?:kr|kampfrunde)").matcher(lower);
            if (m.find())
            {
                result.put("duration", Integer.parseInt(m.group(1)));
                result.put("durationUnit", 35); // kampfrunde
            }
        }
        else if (lower.contains("stunde"))
        {
            Matcher m = Pattern.compile("(\\d+)\\s*stunde").matcher(lower);
            if (m.find())
            {
                result.put("duration", Integer.parseInt(m.group(1)));
                result.put("durationUnit", 8); // stunde
            }
        }
        else if (lower.contains("tag"))
        {
            Matcher m = Pattern.compile("(\\d+)\\s*tag").matcher(lower);
            if (m.find())
            {
                result.put("duration", Integer.parseInt(m.group(1)));
                result.put("durationUnit", 9); // tag
            }
        }

        if (result.isEmpty()) return null;
        result.put("isPerQS", lower.contains("pro qs") || lower.contains("x qs"));
        result.put("maxDuration", 0);
        result.put("maxIsPerQs", false);
        return result;
    }

    private static List<Integer> extractTargetCategories(String flat)
    {
        String raw = extractField(flat, "Zielkategorie:");
        if (raw == null) return List.of();

        List<Integer> result = new ArrayList<>();
        String lower = raw.toLowerCase();

        Map<String, Integer> map = Map.ofEntries(
            Map.entry("kulturschaffende", 5), Map.entry("lebewesen", 19),
            Map.entry("wesen", 19), Map.entry("zone", 12),
            Map.entry("objekte", 7), Map.entry("profane objekte", 8),
            Map.entry("tier", 9), Map.entry("pflanz", 13),
            Map.entry("elementar", 3), Map.entry("dämon", 2),
            Map.entry("feen", 14), Map.entry("übernatürlich", 10),
            Map.entry("untot", 15), Map.entry("geist", 16),
            Map.entry("alle", 0)
        );

        for (Map.Entry<String, Integer> entry : map.entrySet())
        {
            if (lower.contains(entry.getKey()))
            {
                result.add(entry.getValue());
            }
        }

        // Fallback
        if (result.isEmpty() && TARGET_CAT_ORDINALS.containsKey(raw.trim().toLowerCase()))
        {
            result.add(TARGET_CAT_ORDINALS.get(raw.trim().toLowerCase()));
        }

        return result;
    }

    private static List<Integer> extractFeatures(String flat)
    {
        String raw = extractField(flat, "Merkmal:");
        if (raw == null) return List.of();

        List<Integer> result = new ArrayList<>();
        for (String part : raw.split("[,;]"))
        {
            String name = part.trim();
            Integer ordinal = FEATURE_MAP.get(name);
            if (ordinal != null) result.add(ordinal);
        }
        return result;
    }

    private static List<Integer> extractTraditions(String flat)
    {
        // Verbreitung oder Anmerkung
        String raw = extractField(flat, "Verbreitung:");
        if (raw == null) raw = extractField(flat, "Anmerkung:");
        if (raw == null) return List.of();

        if (raw.toLowerCase().contains("allgemein")) return List.of(TRADITION_MAP.get("allgemein"));

        List<Integer> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : TRADITION_MAP.entrySet())
        {
            if (raw.contains(entry.getKey()))
            {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    private static List<Map<String, Object>> extractVariants(String flat, MysticalSkillKey parentKey)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        // Suche nach "Erweiterungen:" Block
        int extIdx = flat.indexOf("erweiterungen:");
        if (extIdx < 0) return result;

        String extText = flat.substring(extIdx);
        // Bis zum nächsten Bold-Feld
        int endIdx = extText.indexOf("**Geste");
        if (endIdx < 0) endIdx = extText.indexOf("**Reversalis");
        if (endIdx < 0) endIdx = extText.length();
        extText = extText.substring(0, endIdx);

        // Pattern: "- Name (FW XX, YY AP): Beschreibung"
        Matcher m = VARIANT_PATTERN.matcher(extText);
        while (m.find())
        {
            String variantName = m.group(1).trim();
            int minLevel = Integer.parseInt(m.group(2));
            int ap = Integer.parseInt(m.group(3));
            String description = m.group(4).trim();
            // Beschreibung bis zum nächsten "- " oder Ende
            int descEnd = description.indexOf(" - ");
            if (descEnd > 0 && descEnd < 200) description = description.substring(0, descEnd);

            MysticalSkillVariantKey vk = ExtractorMysticalSkillKey.extractMysticalSkillVariantKeyFromText(
                parentKey, variantName);

            Map<String, Object> variant = new LinkedHashMap<>();
            if (vk != null) variant.put("key", vk.ordinal());
            variant.put("name", variantName);
            variant.put("minLevel", minLevel);
            variant.put("ap", ap);
            variant.put("description", description);
            result.add(variant);
        }

        return result;
    }

    // ---------------------------------------------------------------
    // Hilfsmethoden
    // ---------------------------------------------------------------

    private static String extractField(String flat, String label)
    {
        int idx = flat.indexOf("**" + label + "**");
        if (idx < 0) idx = flat.indexOf(label);
        if (idx < 0) return null;

        int start = flat.indexOf("**", idx + label.length() + 4);
        if (start < 0) start = idx + label.length() + 4;
        else start = idx + label.length() + 4; // nach dem schließenden **

        // Suche den Wert nach dem Label bis zum nächsten **Label:**
        String after = flat.substring(idx + label.length() + 4).trim();
        // Bis zum nächsten **Bold-Key:**
        Matcher endM = Pattern.compile("\\*\\*[A-ZÄÖÜ][^*]*?:\\*\\*").matcher(after);
        if (endM.find())
        {
            return after.substring(0, endM.start()).replaceAll("\\*+", "").trim();
        }
        return after.replaceAll("\\*+", "").trim();
    }

    @SuppressWarnings("unchecked")
    private static void loadEnumOrdinals()
    {
        try (InputStream is = MysticalSkillExtractorMain.class.getResourceAsStream("/enum-ordinals.json"))
        {
            if (is == null) return;
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, Integer>> root = mapper.readValue(is, new TypeReference<>() {});
            ATTR_ORDINALS = root.getOrDefault("attributeShort", Map.of());
            TARGET_CAT_ORDINALS = root.getOrDefault("targetCategory", Map.of());
        }
        catch (IOException e)
        {
            LOGGER.error("Fehler: {}", e.getMessage());
        }
    }
}
