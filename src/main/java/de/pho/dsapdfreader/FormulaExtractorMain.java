package de.pho.dsapdfreader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extrahiert Spruchformeln (Geste und Formel / Geste und Gebet)
 * aus den _headings.md-Dateien und erzeugt mystical-skill-formulas.json
 * fuer die Angular-App.
 */
public class FormulaExtractorMain
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    private static final String HEADINGS_BASE = "export/markdown/text/01 - Regeln";
    private static final String ANGULAR_NAMES_JSON =
        "D:/develop/project/angular/ng-dsa/src/app/_data-translation/mystical-skill-names.json";
    private static final String OUTPUT_JSON =
        "D:/develop/project/angular/ng-dsa/src/app/_data-translation/mystical-skill-formulas.json";

    private static final String GRIMORUM_DIR = "Grimorum Cantiones (156)";
    private static final String DIVINARIUM_DIR = "Divinarium Liturgia (188)";

    // Tradition-Mapping: PDF-Name → TraditionKey (Angular enum name)
    private static final Map<String, String> TRADITION_MAP = Map.ofEntries(
        Map.entry("Borbaradianer", "borbarad"),
        Map.entry("Druiden", "druiden"),
        Map.entry("Druide", "druiden"),
        Map.entry("Elfen", "elfen"),
        Map.entry("Elf", "elfen"),
        Map.entry("Geoden", "geode"),
        Map.entry("Geode", "geode"),
        Map.entry("Gildenmagier", "gildenmagier"),
        Map.entry("Magier", "gildenmagier"),
        Map.entry("Goblinzauberinnen", "goblinzauberin"),
        Map.entry("Goblinzauberin", "goblinzauberin"),
        Map.entry("Hexen", "hexen"),
        Map.entry("Hexe", "hexen"),
        Map.entry("Kristallomanten", "kristallomanten"),
        Map.entry("Kristallomant", "kristallomanten"),
        Map.entry("Scharlatane", "scharlatane"),
        Map.entry("Scharlatan", "scharlatane"),
        Map.entry("Qabalya", "qabalya"),
        Map.entry("Schelme", "schelme"),
        Map.entry("Zauberbarden", "zauberbarde"),
        Map.entry("Zauberalchimisten", "zauberalchimisten"),
        Map.entry("Zaubertänzer", "zaubertänzer"),
        // Kleriker
        Map.entry("Praios", "praios"),
        Map.entry("Rondra", "rondra"),
        Map.entry("Efferd", "efferd"),
        Map.entry("Travia", "travia"),
        Map.entry("Boron", "boron"),
        Map.entry("Hesinde", "hesinde"),
        Map.entry("Firun", "firun"),
        Map.entry("Tsa", "tsa"),
        Map.entry("Phex", "phex"),
        Map.entry("Peraine", "peraine"),
        Map.entry("Ingerimm", "ingerimm"),
        Map.entry("Rahja", "rahja"),
        Map.entry("Kor", "kor"),
        Map.entry("Namenlos", "namenlos"),
        Map.entry("Nandus", "nandus"),
        Map.entry("Swafnir", "swafnir"),
        Map.entry("Ifirn", "ifirn"),
        Map.entry("Aves", "aves"),
        Map.entry("Angrosch", "angrosch"),
        Map.entry("Levthan", "levthan"),
        Map.entry("Marbo", "marbo"),
        Map.entry("Shinxir", "shinxir"),
        Map.entry("Chr'Ssir'Ssr", "chr_ssir_ssr_kult"),
        Map.entry("Gravesh", "graveshkult"),
        Map.entry("H'Szint", "h_szint_kult"),
        Map.entry("Numinoru", "numinoru"),
        Map.entry("Tairach", "tairachschamane"),
        Map.entry("Zsahh", "zsahh")
    );

    // Gildenmagier-Sprachen erkennen: "Text (bosp.) / Text (tul.) / ..."
    private static final Pattern GUILD_FORMULA_PATTERN = Pattern.compile(
        "([^(/]+?)\\s*\\((gar|bosp|tul|thorw)\\.\\)"
    );
    // Einzelformel extrahieren: alles zwischen typographischen Anführungszeichen
    private static final Pattern SINGLE_FORMULA_PATTERN = Pattern.compile(
        "(?:Formel|spricht).*?\u201E(.*?)\u201C"  // „..." (deutsch)
    );
    // Fallback: ASCII-Anführungszeichen
    private static final Pattern SINGLE_FORMULA_PATTERN_ASCII = Pattern.compile(
        "(?:Formel|spricht).*?\"(.*?)\""
    );

    public static void main(String[] args) throws Exception
    {
        LOGGER.info("=== Formel-Extraktor ===");

        // 1. Name → Key Mapping laden
        Map<String, Integer> nameToKey = loadNameToKeyMap();
        LOGGER.info("{} MysticalSkill-Namen geladen", nameToKey.size());

        // 2. Formeln aus Grimorum extrahieren
        Map<Integer, FormulaEntry> formulas = new TreeMap<>();
        Path grimorumFile = findHeadingsFile(GRIMORUM_DIR);
        if (grimorumFile != null)
        {
            extractFormulas(grimorumFile, nameToKey, formulas, "spell");
            extractRhymes(grimorumFile, nameToKey, formulas);
            extractZhayad(grimorumFile, nameToKey, formulas);
            LOGGER.info("Grimorum: {} Einträge mit Formeln", formulas.size());
        }

        // 3. Formeln aus Divinarium extrahieren
        int beforeLiturgy = formulas.size();
        Path divinariumFile = findHeadingsFile(DIVINARIUM_DIR);
        if (divinariumFile != null)
        {
            extractFormulas(divinariumFile, nameToKey, formulas, "liturgy");
            LOGGER.info("Divinarium: {} neue Einträge", formulas.size() - beforeLiturgy);
        }

        // 4. Reimform/Zhayad auf Traditionen verteilen und JSON schreiben
        List<Map<String, Object>> output = new ArrayList<>();
        for (FormulaEntry entry : formulas.values())
        {
            entry.distributeGlobalFormulas();
            output.add(entry.toJsonMap());
        }

        MAPPER.writeValue(Paths.get(OUTPUT_JSON).toFile(), output);
        LOGGER.info("Geschrieben: {} Einträge -> {}", output.size(), OUTPUT_JSON);
    }

    // ---------------------------------------------------------------
    // Name → Key Mapping
    // ---------------------------------------------------------------

    private static Map<String, Integer> loadNameToKeyMap() throws IOException
    {
        List<List<Object>> raw = MAPPER.readValue(
            Paths.get(ANGULAR_NAMES_JSON).toFile(),
            new TypeReference<>() {}
        );

        Map<String, Integer> map = new LinkedHashMap<>();
        for (List<Object> pair : raw)
        {
            int key = ((Number) pair.get(0)).intValue();
            String name = (String) pair.get(1);
            // Normalisiert: lowercase für Vergleich, Original für Anzeige
            map.put(normalizeName(name), key);
        }
        return map;
    }

    private static String normalizeName(String name)
    {
        return name.toLowerCase()
            .replace("ä", "ae").replace("ö", "oe").replace("ü", "ue")
            .replace("ß", "ss")
            .replaceAll("[^a-z0-9]", " ")
            .trim()
            .replaceAll("\\s+", " ");
    }

    // Bekannte kaputte Headings aus fehlerhafter Spalteninterpretation
    private static final Map<String, String> GARBLED_HEADING_FIXES = Map.of(
        "HaSgteulrsmchgleabgr uünlld", "Hagelschlag und Sturmgebrüll"
    );

    /**
     * Findet den numerischen Key zu einem Spruchnamen aus dem Heading.
     */
    private static Integer resolveKey(String headingName, Map<String, Integer> nameToKey)
    {
        // Bekannte kaputte Headings reparieren
        String fixed = GARBLED_HEADING_FIXES.get(headingName);
        if (fixed != null) headingName = fixed;

        String normalized = normalizeName(headingName);
        Integer key = nameToKey.get(normalized);
        if (key != null) return key;

        // Fuzzy: Teilstring-Match
        for (Map.Entry<String, Integer> entry : nameToKey.entrySet())
        {
            if (entry.getKey().startsWith(normalized) || normalized.startsWith(entry.getKey()))
            {
                return entry.getValue();
            }
        }
        return null;
    }

    // ---------------------------------------------------------------
    // Headings-Datei finden
    // ---------------------------------------------------------------

    private static Path findHeadingsFile(String bookDir) throws IOException
    {
        Path base = Paths.get(HEADINGS_BASE);
        Path bookPath = base.resolve(bookDir);
        if (!Files.isDirectory(bookPath))
        {
            LOGGER.warn("Buch-Verzeichnis nicht gefunden: {}", bookPath);
            return null;
        }

        // Suche _headings.md im Unterverzeichnis
        try (var walk = Files.walk(bookPath, 2))
        {
            return walk.filter(p -> p.getFileName().toString().equals("_headings.md"))
                .findFirst().orElse(null);
        }
    }

    // ---------------------------------------------------------------
    // Formeln extrahieren (Geste und Formel / Geste und Gebet)
    // ---------------------------------------------------------------

    private static void extractFormulas(Path headingsFile, Map<String, Integer> nameToKey,
                                         Map<Integer, FormulaEntry> formulas,
                                         String defaultCategory) throws IOException
    {
        List<String> lines = Files.readAllLines(headingsFile, StandardCharsets.UTF_8);

        String currentHeading = null;
        Integer currentKey = null;
        boolean inFormulaBlock = false;
        String currentTraditionName = null;
        StringBuilder currentTraditionText = new StringBuilder();

        for (String line : lines)
        {
            // HTML-Kommentare überspringen
            if (line.trim().startsWith("<!--")) continue;

            // # Heading = Spruchname (H1 im Grimorum/Divinarium)
            if (line.startsWith("# ") && !line.startsWith("## "))
            {
                // Laufende Tradition abschließen
                flushTradition(currentKey, currentTraditionName, currentTraditionText, formulas);
                currentTraditionName = null;
                currentTraditionText.setLength(0);
                inFormulaBlock = false;

                currentHeading = line.substring(2).trim()
                    .replaceAll("\\*+", "").trim();
                currentKey = resolveKey(currentHeading, nameToKey);
            }
            // ## Heading = Spruchname (H2 in manchen Büchern)
            else if (line.startsWith("## ") && !line.startsWith("### "))
            {
                flushTradition(currentKey, currentTraditionName, currentTraditionText, formulas);
                currentTraditionName = null;
                currentTraditionText.setLength(0);
                inFormulaBlock = false;

                currentHeading = line.substring(3).trim()
                    .replaceAll("\\*+", "").trim();
                currentKey = resolveKey(currentHeading, nameToKey);
            }

            // "Geste und Formel:" / "Geste und Gebet:" erkennen
            if (line.contains("Geste und Formel") || line.contains("Geste und Gebet"))
            {
                inFormulaBlock = true;
                continue;
            }

            // Formelblock endet bei nächstem **Feld:** oder neuem Heading
            if (inFormulaBlock && currentKey != null)
            {
                // Neues Feld? (z.B. **Reversalis:**, **Anmerkung:**, etc.)
                if (line.matches("\\*\\*[A-ZÄÖÜ].*?:\\*\\*.*") && !line.contains("Geste"))
                {
                    flushTradition(currentKey, currentTraditionName, currentTraditionText, formulas);
                    currentTraditionName = null;
                    currentTraditionText.setLength(0);
                    inFormulaBlock = false;
                    continue;
                }

                // Traditions-Zeile: "- Gildenmagier: ..." oder "# Gildenmagier: ..."
                Matcher tradMatcher = Pattern.compile("^[-#]\\s*([^:]+):\\s*(.*)$").matcher(line);
                if (tradMatcher.matches())
                {
                    // Vorherige Tradition abschließen
                    flushTradition(currentKey, currentTraditionName, currentTraditionText, formulas);

                    currentTraditionName = tradMatcher.group(1).trim();
                    currentTraditionText.setLength(0);
                    String rest = tradMatcher.group(2).trim();
                    if (!rest.isEmpty())
                    {
                        currentTraditionText.append(rest);
                    }
                }
                else if (currentTraditionName != null && !line.isBlank())
                {
                    // Fortsetzungszeile
                    if (currentTraditionText.length() > 0) currentTraditionText.append(" ");
                    currentTraditionText.append(line.trim());
                }
            }
        }

        // Letzte Tradition abschließen
        flushTradition(currentKey, currentTraditionName, currentTraditionText, formulas);
    }

    private static void flushTradition(Integer key, String traditionName,
                                        StringBuilder text, Map<Integer, FormulaEntry> formulas)
    {
        if (key == null || traditionName == null || text.length() == 0) return;

        String traditionKey = mapTradition(traditionName);
        if (traditionKey == null)
        {
            LOGGER.debug("Unbekannte Tradition: '{}' (Key={})", traditionName, key);
            return;
        }

        FormulaEntry entry = formulas.computeIfAbsent(key, FormulaEntry::new);
        String fullText = text.toString().trim();

        TraditionFormula tf = new TraditionFormula();
        tf.tradition = traditionKey;

        // Formelblock im Gestentext finden und durch {formula} ersetzen
        String formulaBlock = extractQuotedBlock(fullText);
        if (formulaBlock != null)
        {
            tf.gesture = fullText.replace(
                "\u201E" + formulaBlock + "\u201C",
                "<i>\u201E{formula}\u201C</i>"
            );
            tf.defaultFormula = formulaBlock;
        }
        else
        {
            // Kein Formelblock in Anführungszeichen → Geste ohne Formel
            tf.gesture = fullText;
        }

        // Gildenmagier: Sprachen extrahieren
        if ("gildenmagier".equals(traditionKey) && formulaBlock != null)
        {
            Matcher gm = GUILD_FORMULA_PATTERN.matcher(formulaBlock);
            while (gm.find())
            {
                String rawMatch = gm.group(1).trim();
                int lastSep = Math.max(rawMatch.lastIndexOf('/'), rawMatch.lastIndexOf('"'));
                String formula = (lastSep >= 0)
                    ? rawMatch.substring(lastSep + 1).trim()
                    : rawMatch;
                formula = formula.replaceAll("^[!„\"\\s/]+|[!„\"\\s]+$", "").trim();
                String lang = gm.group(2);
                switch (lang)
                {
                    case "gar" -> tf.garethi = formula;
                    case "bosp" -> tf.bosparano = formula;
                    case "tul" -> tf.tulamidya = formula;
                    case "thorw" -> tf.thorwalsch = formula;
                }
            }
        }
        // formula entfällt — default enthält bereits den Formeltext

        entry.traditions.add(tf);
    }

    /**
     * Extrahiert den Inhalt des ersten „..."-Blocks (typographische Anführungszeichen).
     */
    private static String extractQuotedBlock(String text)
    {
        int start = text.indexOf('\u201E'); // „
        if (start < 0) return null;
        int end = text.indexOf('\u201C', start + 1); // "
        if (end < 0) return null;
        return text.substring(start + 1, end);
    }

    private static String mapTradition(String pdfName)
    {
        // Exakter Match
        String key = TRADITION_MAP.get(pdfName);
        if (key != null) return key;

        // Teilstring-Match
        for (Map.Entry<String, String> entry : TRADITION_MAP.entrySet())
        {
            if (pdfName.contains(entry.getKey()) || entry.getKey().contains(pdfName))
            {
                return entry.getValue();
            }
        }
        return null;
    }

    // ---------------------------------------------------------------
    // Reimformeln extrahieren
    // ---------------------------------------------------------------

    /**
     * Extrahiert Reimformeln aus dem Anhang des Grimoriums.
     * Das Format ist gemischt: Fließtext, Tabellenfragmente, zweispaltig.
     * Strategie: Alle Zeilen im Reimformeln-Abschnitt sammeln, bereinigen,
     * dann bekannte Spruchnamen am Zeilenanfang matchen.
     */
    private static void extractRhymes(Path headingsFile, Map<String, Integer> nameToKey,
                                       Map<Integer, FormulaEntry> formulas) throws IOException
    {
        List<String> lines = Files.readAllLines(headingsFile, StandardCharsets.UTF_8);

        // 1. Reimformeln-Abschnitt finden und Zeilen sammeln
        boolean inRhymeSection = false;
        List<String> rhymeLines = new ArrayList<>();

        for (String line : lines)
        {
            if (line.trim().startsWith("<!--")) continue;

            if (line.startsWith("# Gereimte Zauberformeln"))
            {
                inRhymeSection = true;
                continue;
            }
            // Sektion endet bei nächstem H1 der kein Reimformel-Sub-Header ist
            if (inRhymeSection && line.startsWith("# ") && !line.startsWith("####")
                && !line.contains("Reimformel") && !line.contains("Magnopendium"))
            {
                break;
            }
            if (inRhymeSection)
            {
                // Spalten-Header überspringen
                if (line.contains("Zauber-") && line.contains("Reimformel") && line.contains("spruch"))
                {
                    continue;
                }
                rhymeLines.add(line);
            }
        }

        // 2. Zeilen bereinigen: Pipes entfernen, Fließtext zusammenfügen
        List<String> cleanLines = cleanRhymeLines(rhymeLines);

        // 3. Spruchnamen nach Länge sortiert (längste zuerst) für greedy Matching
        List<Map.Entry<String, Integer>> sortedNames = new ArrayList<>();
        // Original-Namen (nicht normalisiert) aus der JSON für exaktes Matching
        List<List<Object>> rawNames;
        try
        {
            rawNames = MAPPER.readValue(
                Paths.get(ANGULAR_NAMES_JSON).toFile(),
                new TypeReference<>() {}
            );
        }
        catch (IOException e)
        {
            LOGGER.warn("Konnte Namen-JSON nicht laden: {}", e.getMessage());
            return;
        }

        // Name → Key Map mit Original-Schreibweise
        Map<String, Integer> displayNameToKey = new LinkedHashMap<>();
        for (List<Object> pair : rawNames)
        {
            int keyVal = ((Number) pair.get(0)).intValue();
            String name = (String) pair.get(1);
            displayNameToKey.put(name, keyVal);
        }

        // Nach Namenslänge sortiert (längste zuerst)
        List<String> sortedDisplayNames = new ArrayList<>(displayNameToKey.keySet());
        sortedDisplayNames.sort((a, b) -> Integer.compare(b.length(), a.length()));

        // 4. Jede bereinigte Zeile gegen bekannte Namen matchen
        int found = 0;
        for (String line : cleanLines)
        {
            if (line.isBlank()) continue;

            String matched = null;
            Integer key = null;

            for (String name : sortedDisplayNames)
            {
                if (line.startsWith(name + " ") || line.startsWith(name + "\t"))
                {
                    matched = name;
                    key = displayNameToKey.get(name);
                    break;
                }
            }

            if (matched != null && key != null)
            {
                String rhyme = line.substring(matched.length()).trim();
                if (!rhyme.isEmpty())
                {
                    FormulaEntry entry = formulas.computeIfAbsent(key, FormulaEntry::new);
                    entry.rhyme = rhyme;
                    found++;
                }
            }
        }
        LOGGER.info("  Reimformeln: {} gefunden", found);
    }

    /**
     * Bereinigt die Rohzeilen des Reimformeln-Abschnitts:
     * - Entfernt Pipe-Zeichen und Tabellen-Artefakte
     * - Fügt Fortsetzungszeilen zusammen
     * - Trennt zusammengeklebte Einträge (zwei Tabellenspalten)
     */
    private static List<String> cleanRhymeLines(List<String> raw)
    {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String line : raw)
        {
            if (line.isBlank())
            {
                if (current.length() > 0)
                {
                    result.add(current.toString().trim());
                    current.setLength(0);
                }
                continue;
            }

            // Fluff-Text und Einleitungen überspringen
            if (line.startsWith("Gereimte Zauberformeln") || line.startsWith("Die vorliegende")
                || line.startsWith("Das Magnopendium") || line.startsWith("Aventurisch")
                || line.startsWith("Die Reimformeln des") || line.startsWith("-"))
            {
                continue;
            }

            // Pipe-Zeichen und Formatierung entfernen
            String cleaned = line
                .replaceAll("\\|", " ")
                .replaceAll("\\*+", "")
                .replaceAll("\\s+", " ")
                .trim();

            if (cleaned.isEmpty()) continue;

            // An vorherige Zeile anhängen wenn es eine Fortsetzung ist
            // (beginnt nicht mit Großbuchstabe oder ist zu kurz für einen eigenen Eintrag)
            if (current.length() > 0 && !cleaned.isEmpty())
            {
                char first = cleaned.charAt(0);
                boolean startsLower = Character.isLowerCase(first);
                boolean isShort = cleaned.length() < 15 && !cleaned.contains("!");

                if (startsLower || isShort)
                {
                    current.append(" ").append(cleaned);
                    continue;
                }
            }

            // Vorherigen Eintrag abschließen
            if (current.length() > 0)
            {
                result.add(current.toString().trim());
            }
            current.setLength(0);
            current.append(cleaned);
        }

        if (current.length() > 0)
        {
            result.add(current.toString().trim());
        }

        return result;
    }

    // ---------------------------------------------------------------
    // Zhayad-Formeln extrahieren
    // ---------------------------------------------------------------

    private static void extractZhayad(Path headingsFile, Map<String, Integer> nameToKey,
                                       Map<Integer, FormulaEntry> formulas) throws IOException
    {
        List<String> lines = Files.readAllLines(headingsFile, StandardCharsets.UTF_8);
        boolean inZhayadSection = false;

        Pattern tablePattern = Pattern.compile("^\\|\\s*(.+?)\\s*\\|\\s*(.+?)\\s*\\|$");

        for (String line : lines)
        {
            if (line.contains("Zhayad-Formel") && (line.startsWith("#") || line.startsWith("####")))
            {
                inZhayadSection = true;
                continue;
            }
            if (inZhayadSection && line.startsWith("# ") && !line.contains("Zhayad"))
            {
                inZhayadSection = false;
                continue;
            }

            if (inZhayadSection)
            {
                Matcher m = tablePattern.matcher(line);
                if (m.matches())
                {
                    String spellName = m.group(1).trim().replaceAll("\\*+", "");
                    String zhayad = m.group(2).trim();

                    if (spellName.equalsIgnoreCase("Zauber") || spellName.equalsIgnoreCase("Ritual"))
                    {
                        continue;
                    }

                    Integer key = resolveKey(spellName, nameToKey);
                    if (key != null)
                    {
                        FormulaEntry entry = formulas.computeIfAbsent(key, FormulaEntry::new);
                        entry.zhayad = zhayad;
                    }
                    else
                    {
                        LOGGER.debug("Zhayad ohne Key: '{}'", spellName);
                    }
                }
            }
        }
    }

    // =====================================================================
    // Datenmodell
    // =====================================================================

    static class FormulaEntry
    {
        int key;
        List<TraditionFormula> traditions = new ArrayList<>();
        String rhyme;   // zwischengespeichert, wird auf Traditionen verteilt
        String zhayad;  // zwischengespeichert, wird auf Traditionen verteilt

        FormulaEntry(int key)
        {
            this.key = key;
        }

        private static final java.util.Set<String> ELVEN_TRADITIONS = java.util.Set.of(
            "elfen", "darna", "nachtalben"
        );

        /** Reimform und Zhayad auf Traditionen verteilen, die eine Formel haben. */
        void distributeGlobalFormulas()
        {
            for (TraditionFormula tf : traditions)
            {
                // Nur Traditionen mit Formel bekommen Sprachvarianten
                if (tf.defaultFormula == null) continue;

                boolean isElven = ELVEN_TRADITIONS.contains(tf.tradition);

                // Elfische Traditionen: nur Zhayad
                if (zhayad != null && tf.zhayad == null)
                {
                    tf.zhayad = zhayad;
                }
                if (!isElven && rhyme != null && tf.reimform == null)
                {
                    tf.reimform = rhyme;
                }
            }
        }

        Map<String, Object> toJsonMap()
        {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("key", key);

            List<Map<String, Object>> tradList = new ArrayList<>();
            for (TraditionFormula tf : traditions)
            {
                tradList.add(tf.toJsonMap());
            }
            map.put("traditions", tradList);
            return map;
        }
    }

    static class TraditionFormula
    {
        String tradition;
        String gesture;
        String defaultFormula; // Original-Formelblock (alle Sprachen)
        String garethi;        // Garethi-Sprachvariante (Gildenmagier)
        String bosparano;
        String tulamidya;
        String thorwalsch;
        String zhayad;
        String reimform;       // Gereimte Zauberformel (Scharlatane/Magnopendium)

        Map<String, Object> toJsonMap()
        {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("tradition", tradition);
            map.put("gesture", gesture);
            if (defaultFormula != null) map.put("default", defaultFormula);
            if (garethi != null) map.put("garethi", garethi);
            if (bosparano != null) map.put("bosparano", bosparano);
            if (tulamidya != null) map.put("tulamidya", tulamidya);
            if (thorwalsch != null) map.put("thorwalsch", thorwalsch);
            if (zhayad != null) map.put("zhayad", zhayad);
            if (reimform != null) map.put("reimform", reimform);
            return map;
        }
    }
}
