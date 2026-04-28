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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extrahiert Abenteuer-Struktur (Abenteuer → Kapitel → Szenen → Texte + Profile + Proben)
 * aus _headings.md-Dateien und erzeugt JSON im Campaign-Format.
 */
public class AdventureExtractorMain
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    private static final String HEADINGS_BASE = "export/markdown/text";
    private static final String OUTPUT_BASE = "export/adventures";

    // --- Patterns ---
    private static final Pattern READ_ALOUD_MARKER = Pattern.compile(
        "\\*\\*\\*Zum Vorlesen oder Nacherzählen:\\*\\*\\*"
    );
    private static final Pattern PROFILE_PATTERN = Pattern.compile(
        "\\*\\*MU\\*\\*\\s*\\d+"
    );
    private static final Pattern ADVENTURE_BG = Pattern.compile(
        "^## Hintergrund von (.+)$"
    );
    private static final Pattern ADVENTURE_TITLE = Pattern.compile(
        "^## (Der Inquisitor|Die Einsiedlerin|Der Händler|Der Alchemyst)$"
    );

    // Proben-Patterns
    // "Probe auf *Klettern* +1", "Probe auf *Sinnesschärfe (Wahrnehmen)* –1"
    // "Sammelprobe auf *Steinbearbeitung (Mauern)*"
    private static final Pattern SKILL_CHECK_PATTERN = Pattern.compile(
        "(?:Probe|Erfolgsprobe|Sammelprobe|Vergleichsprobe|Gruppenprobe)\\s+auf\\s+" +
        "\\*([A-ZÄÖÜ][a-zäöüß &]+(?:\\s*\\([^)]+\\))?)\\*" +
        "(?:\\s*([+–\\-]\\s*\\d+))?"
    );
    // Fallback ohne Kursiv
    private static final Pattern SKILL_CHECK_PATTERN_PLAIN = Pattern.compile(
        "(?:Probe|Erfolgsprobe|Sammelprobe|Vergleichsprobe|Gruppenprobe)\\s+auf\\s+" +
        "([A-ZÄÖÜ][a-zäöüß &]+(?:\\s*\\([^)]+\\))?)" +
        "(?:\\s*([+–\\-]\\s*\\d+))?"
    );
    // QS-Ergebnis-Pattern: "QS 1:", "mit QS 2 oder mehr", "mindestens QS 3"
    private static final Pattern QS_RESULT_PATTERN = Pattern.compile(
        "(?:QS\\s*(\\d+)(?:\\s*oder\\s*mehr)?|mindestens\\s*QS\\s*(\\d+)|(?:Sind|sind).*QS\\s*(\\d+))\\s*[:\\-–]?\\s*(.+?)(?=QS\\s*\\d|$)"
    );

    // Skill-Name → SkillKey ordinal (aus dem ProfileExtractorMain SKILL_KEY_MAP)
    private static final Map<String, String> SKILL_NAME_MAP = new LinkedHashMap<>();
    private static Map<String, Integer> SKILL_ORDINALS = new LinkedHashMap<>();

    static
    {
        // Körper
        SKILL_NAME_MAP.put("Fliegen", "fliegen");
        SKILL_NAME_MAP.put("Gaukeleien", "gaukeleien");
        SKILL_NAME_MAP.put("Klettern", "klettern");
        SKILL_NAME_MAP.put("Körperbeherrschung", "körperbeherrschung");
        SKILL_NAME_MAP.put("Kraftakt", "kraftakt");
        SKILL_NAME_MAP.put("Reiten", "reiten");
        SKILL_NAME_MAP.put("Schwimmen", "schwimmen");
        SKILL_NAME_MAP.put("Selbstbeherrschung", "selbstbeherrschung");
        SKILL_NAME_MAP.put("Singen", "singen");
        SKILL_NAME_MAP.put("Sinnesschärfe", "sinnesschärfe");
        SKILL_NAME_MAP.put("Tanzen", "tanzen");
        SKILL_NAME_MAP.put("Taschendiebstahl", "taschendiebstahl");
        SKILL_NAME_MAP.put("Verbergen", "verbergen");
        SKILL_NAME_MAP.put("Zechen", "zechen");
        // Gesellschaft
        SKILL_NAME_MAP.put("Bekehren & Überzeugen", "bekehren_und_überzeugen");
        SKILL_NAME_MAP.put("Betören", "betören");
        SKILL_NAME_MAP.put("Einschüchtern", "einschüchtern");
        SKILL_NAME_MAP.put("Etikette", "etikette");
        SKILL_NAME_MAP.put("Gassenwissen", "gassenwissen");
        SKILL_NAME_MAP.put("Menschenkenntnis", "menschenkenntnis");
        SKILL_NAME_MAP.put("Überreden", "überreden");
        SKILL_NAME_MAP.put("Verkleiden", "verkleiden");
        SKILL_NAME_MAP.put("Willenskraft", "willenskraft");
        // Natur
        SKILL_NAME_MAP.put("Fährtensuchen", "fährtensuchen");
        SKILL_NAME_MAP.put("Fesseln", "fesseln");
        SKILL_NAME_MAP.put("Fischen & Angeln", "fischen_und_angeln");
        SKILL_NAME_MAP.put("Orientierung", "orientierung");
        SKILL_NAME_MAP.put("Pflanzenkunde", "pflanzenkunde");
        SKILL_NAME_MAP.put("Tierkunde", "tierkunde");
        SKILL_NAME_MAP.put("Wildnisleben", "wildnisleben");
        // Wissen
        SKILL_NAME_MAP.put("Brett- & Glücksspiel", "brett_und_glücksspiel");
        SKILL_NAME_MAP.put("Geographie", "geographie");
        SKILL_NAME_MAP.put("Geschichtswissen", "geschichtswissen");
        SKILL_NAME_MAP.put("Götter & Kulte", "götter_und_kulte");
        SKILL_NAME_MAP.put("Kriegskunst", "kriegskunst");
        SKILL_NAME_MAP.put("Magiekunde", "magiekunde");
        SKILL_NAME_MAP.put("Mechanik", "mechanik");
        SKILL_NAME_MAP.put("Rechnen", "rechnen");
        SKILL_NAME_MAP.put("Rechtskunde", "rechtskunde");
        SKILL_NAME_MAP.put("Sagen & Legenden", "sagen_und_legenden");
        SKILL_NAME_MAP.put("Sphärenkunde", "sphärenkunde");
        SKILL_NAME_MAP.put("Sternkunde", "sternkunde");
        // Handwerk
        SKILL_NAME_MAP.put("Alchimie", "alchimie");
        SKILL_NAME_MAP.put("Boote & Schiffe", "boote_und_schiffe");
        SKILL_NAME_MAP.put("Fahrzeuge", "fahrzeuge");
        SKILL_NAME_MAP.put("Handel", "handel");
        SKILL_NAME_MAP.put("Heilkunde Gift", "heilkunde_gift");
        SKILL_NAME_MAP.put("Heilkunde Krankheiten", "heilkunde_krankheiten");
        SKILL_NAME_MAP.put("Heilkunde Seele", "heilkunde_seele");
        SKILL_NAME_MAP.put("Heilkunde Wunden", "heilkunde_wunden");
        SKILL_NAME_MAP.put("Holzbearbeitung", "holzbearbeitung");
        SKILL_NAME_MAP.put("Lebensmittelbearbeitung", "lebensmittelbearbeitung");
        SKILL_NAME_MAP.put("Lederbearbeitung", "lederbearbeitung");
        SKILL_NAME_MAP.put("Malen & Zeichnen", "malen_und_zeichnen");
        SKILL_NAME_MAP.put("Metallbearbeitung", "metallbearbeitung");
        SKILL_NAME_MAP.put("Musizieren", "musizieren");
        SKILL_NAME_MAP.put("Schlösserknacken", "schlösserknacken");
        SKILL_NAME_MAP.put("Steinbearbeitung", "steinbearbeitung");
        SKILL_NAME_MAP.put("Stoffbearbeitung", "stoffbearbeitung");
    }

    // ---------------------------------------------------------------
    // Main
    // ---------------------------------------------------------------

    public static void main(String[] args) throws Exception
    {
        LOGGER.info("=== Abenteuer-Extraktor ===");
        loadEnumOrdinals();
        ProfileExtractorMain.initLookups();

        Path basePath = Paths.get(HEADINGS_BASE)
            .resolve("05 - Abenteuer/Spielsteine der Macht (153)/Spielsteine der Macht - 05 - Abenteuer");
        Path headingsFile = basePath.resolve("_headings.md");

        if (!Files.exists(headingsFile))
        {
            LOGGER.error("Datei nicht gefunden: {}", headingsFile);
            return;
        }

        String content = Files.readString(headingsFile, StandardCharsets.UTF_8);
        String[] lines = content.split("\n");

        List<AdventureRange> adventures = detectAdventures(lines);
        LOGGER.info("Erkannte Abenteuer: {}", adventures.size());

        Map<String, Object> campaign = new LinkedHashMap<>();
        campaign.put("uid", uid());
        campaign.put("name", "Spielsteine der Macht");
        campaign.put("publication", "Spielsteine der Macht");

        // Globale Profil-Registry (Deduplizierung)
        Map<String, Map<String, Object>> profileRegistry = new LinkedHashMap<>();

        List<Map<String, Object>> abenteuerliste = new ArrayList<>();
        int order = 1;
        int totalChecks = 0;
        for (AdventureRange adv : adventures)
        {
            Map<String, Object> abenteuer = extractAdventure(lines, adv, order++, profileRegistry);
            abenteuerliste.add(abenteuer);

            // Proben zählen
            int checks = countChecks(abenteuer);
            totalChecks += checks;
            LOGGER.info("  Abenteuer: {} ({} Kapitel, {} Proben)",
                abenteuer.get("name"),
                ((List<?>) abenteuer.get("chapters")).size(), checks);
        }

        campaign.put("adventures", abenteuerliste);
        campaign.put("profiles", new ArrayList<>(profileRegistry.values()));

        Path outputDir = Paths.get(OUTPUT_BASE);
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve("Spielsteine_der_Macht.json");
        MAPPER.writeValue(outputFile.toFile(), campaign);

        LOGGER.info("=== Output: {} ({} Abenteuer, {} Profile, {} Proben) ===",
            outputFile, abenteuerliste.size(), profileRegistry.size(), totalChecks);
    }

    @SuppressWarnings("unchecked")
    private static int countChecks(Map<String, Object> adventure)
    {
        int count = 0;
        for (Map<String, Object> ch : (List<Map<String, Object>>) adventure.get("chapters"))
            for (Map<String, Object> sc : (List<Map<String, Object>>) ch.get("scenes"))
            {
                List<?> proben = (List<?>) sc.get("vorbereiteteProben");
                if (proben != null) count += proben.size();
            }
        return count;
    }

    // ---------------------------------------------------------------
    // Abenteuer-Erkennung
    // ---------------------------------------------------------------

    record AdventureRange(String name, int startLine, int endLine) {}

    private static List<AdventureRange> detectAdventures(String[] lines)
    {
        List<AdventureRange> ranges = new ArrayList<>();
        Map<String, Integer> adventureStarts = new LinkedHashMap<>();

        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i].trim();
            Matcher m = ADVENTURE_BG.matcher(line);
            if (m.matches()) adventureStarts.putIfAbsent(m.group(1).trim(), i);
            Matcher m2 = ADVENTURE_TITLE.matcher(line);
            if (m2.matches()) adventureStarts.putIfAbsent(m2.group(1).trim(), i);
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(adventureStarts.entrySet());
        sorted.sort(Map.Entry.comparingByValue());

        for (int s = 0; s < sorted.size(); s++)
        {
            int startLine = sorted.get(s).getValue();
            int endLine = (s + 1 < sorted.size()) ? sorted.get(s + 1).getValue() - 1 : lines.length - 1;
            for (int i = startLine; i <= endLine; i++)
            {
                if (lines[i].trim().startsWith("# ANHÄNGE")) { endLine = i - 1; break; }
            }
            ranges.add(new AdventureRange(sorted.get(s).getKey(), startLine, endLine));
        }
        return ranges;
    }

    // ---------------------------------------------------------------
    // Kapitel → Szenen
    // ---------------------------------------------------------------

    record ChapterRange(String name, int startLine, int endLine) {}
    record SceneRange(String name, int startLine, int endLine) {}

    private static Map<String, Object> extractAdventure(String[] lines, AdventureRange range,
                                                         int order, Map<String, Map<String, Object>> profileRegistry)
    {
        Map<String, Object> adventure = new LinkedHashMap<>();
        adventure.put("uid", uid());
        adventure.put("name", range.name());
        adventure.put("status", "geplant");
        adventure.put("order", order);

        List<ChapterRange> chapterRanges = detectChapters(lines, range.startLine(), range.endLine());
        List<Map<String, Object>> chapters = new ArrayList<>();
        int chOrder = 1;
        for (ChapterRange ch : chapterRanges)
            chapters.add(extractChapter(lines, ch, chOrder++, profileRegistry));

        if (chapters.isEmpty())
            chapters.add(extractChapter(lines, new ChapterRange(range.name(), range.startLine(), range.endLine()), 1, profileRegistry));

        adventure.put("chapters", chapters);
        return adventure;
    }

    private static List<ChapterRange> detectChapters(String[] lines, int from, int to)
    {
        List<ChapterRange> chapters = new ArrayList<>();
        List<Integer> starts = new ArrayList<>();
        for (int i = from; i <= to; i++)
        {
            String line = lines[i].trim();
            if (line.matches("^# [A-ZÄÖÜ][A-ZÄÖÜ\\s,:']+$") || line.matches("^# EPISODE.*"))
                starts.add(i);
        }
        for (int s = 0; s < starts.size(); s++)
        {
            int start = starts.get(s);
            int end = (s + 1 < starts.size()) ? starts.get(s + 1) - 1 : to;
            chapters.add(new ChapterRange(lines[start].trim().substring(2).trim(), start, end));
        }
        return chapters;
    }

    private static Map<String, Object> extractChapter(String[] lines, ChapterRange range,
                                                       int order, Map<String, Map<String, Object>> profileRegistry)
    {
        Map<String, Object> chapter = new LinkedHashMap<>();
        chapter.put("uid", uid());
        chapter.put("name", range.name());
        chapter.put("order", order);
        chapter.put("open", false);

        List<SceneRange> sceneRanges = detectScenes(lines, range.startLine(), range.endLine());
        List<Map<String, Object>> scenes = new ArrayList<>();
        int scOrder = 1;
        for (SceneRange sc : sceneRanges)
            scenes.add(extractScene(lines, sc, scOrder++, profileRegistry));

        chapter.put("scenes", scenes);
        return chapter;
    }

    private static List<SceneRange> detectScenes(String[] lines, int from, int to)
    {
        List<SceneRange> scenes = new ArrayList<>();
        List<Integer> starts = new ArrayList<>();
        for (int i = from + 1; i <= to; i++)
        {
            String line = lines[i].trim();
            if (line.matches("^## [A-ZÄÖÜ].+") && !line.startsWith("## ***Zum Vorlesen")
                && !line.startsWith("## Hintergrund"))
                starts.add(i);
        }
        for (int s = 0; s < starts.size(); s++)
        {
            int start = starts.get(s);
            int end = (s + 1 < starts.size()) ? starts.get(s + 1) - 1 : to;
            String name = lines[start].trim().substring(3).trim()
                .replaceAll("\\*\\*\\*Zum Vorlesen.*", "").trim();
            if (!name.isEmpty()) scenes.add(new SceneRange(name, start, end));
        }
        return scenes;
    }

    // ---------------------------------------------------------------
    // Szenen-Extraktion: Texte + Profile + Proben
    // ---------------------------------------------------------------

    private static Map<String, Object> extractScene(String[] lines, SceneRange range,
                                                     int order, Map<String, Map<String, Object>> profileRegistry)
    {
        Map<String, Object> scene = new LinkedHashMap<>();
        scene.put("uid", uid());
        scene.put("name", range.name());
        scene.put("order", order);

        List<Map<String, Object>> textAbschnitte = new ArrayList<>();
        List<Map<String, Object>> nscProfile = new ArrayList<>();
        List<Map<String, Object>> vorbereiteteProben = new ArrayList<>();

        StringBuilder currentText = new StringBuilder();
        String currentType = "meisterinfo";
        String currentTitel = null;
        boolean inReadAloud = false;

        for (int i = range.startLine() + 1; i <= range.endLine(); i++)
        {
            String line = lines[i];
            String trimmed = line.trim();

            if (trimmed.matches("^## [A-ZÄÖÜ].+")) break;

            // ### oder #### Heading → neuer Textblock
            if (trimmed.matches("^#{3,4}\\s+.+"))
            {
                flushTextBlock(textAbschnitte, currentText, currentType, currentTitel);
                currentType = "meisterinfo";
                inReadAloud = false;
                currentTitel = trimmed.replaceAll("^#{3,4}\\s+", "").trim();
                continue;
            }

            // Profil erkennen
            if (PROFILE_PATTERN.matcher(trimmed).find())
            {
                flushTextBlock(textAbschnitte, currentText, currentType, currentTitel);
                currentTitel = null;

                String profileName = findProfileName(lines, i);
                int profileEnd = collectProfileBlock(lines, i, range.endLine());
                StringBuilder profileBlock = new StringBuilder();
                for (int j = i; j <= profileEnd; j++)
                    profileBlock.append(lines[j]).append("\n");

                String profileKey = ProfileExtractorMain.normalizeKey(profileName);
                if (!profileRegistry.containsKey(profileKey))
                {
                    Map<String, Object> profileData = ProfileExtractorMain.parseProfileFromBlock(
                        profileName, profileBlock.toString(), "Spielsteine der Macht");
                    if (profileData != null)
                    {
                        profileRegistry.put(profileKey, profileData);
                    }
                }

                if (profileRegistry.containsKey(profileKey))
                {
                    Map<String, Object> nscRef = new LinkedHashMap<>();
                    nscRef.put("uid", uid());
                    nscRef.put("name", profileName);
                    nscRef.put("profileRef", profileRegistry.get(profileKey).get("uid"));
                    nscRef.put("detailStufe", "standard");
                    nscRef.put("beschreibung", "");
                    nscProfile.add(nscRef);
                }

                i = profileEnd;
                continue;
            }

            // Vorlesetext erkennen
            if (READ_ALOUD_MARKER.matcher(trimmed).find())
            {
                flushTextBlock(textAbschnitte, currentText, currentType, currentTitel);
                currentType = "vorlesetext";
                currentTitel = null;
                inReadAloud = true;
                String afterMarker = trimmed.replaceAll(".*Nacherzählen:\\*\\*\\*", "").trim();
                if (!afterMarker.isEmpty())
                    currentText.append(cleanMarkdown(afterMarker)).append("\n");
                continue;
            }

            // Kursiver Block = noch Vorlesetext
            if (inReadAloud && trimmed.startsWith("*") && !trimmed.startsWith("**"))
            {
                currentText.append(cleanMarkdown(trimmed)).append("\n");
                continue;
            }

            // Ende Vorlesetext
            if (inReadAloud && !trimmed.isEmpty() && !trimmed.startsWith("*"))
            {
                flushTextBlock(textAbschnitte, currentText, currentType, currentTitel);
                currentType = "meisterinfo";
                currentTitel = null;
                inReadAloud = false;
            }

            // Proben aus Fließtext extrahieren
            extractSkillChecks(trimmed, vorbereiteteProben);

            // Normalen Text sammeln
            if (!trimmed.isEmpty() && !trimmed.startsWith("<!--"))
            {
                currentText.append(trimmed).append("\n");
            }
            else if (currentText.length() > 0 && trimmed.isEmpty())
            {
                currentText.append("\n");
            }
        }

        flushTextBlock(textAbschnitte, currentText, currentType, currentTitel);

        if (!textAbschnitte.isEmpty()) scene.put("textAbschnitte", textAbschnitte);
        if (!nscProfile.isEmpty()) scene.put("nscProfile", nscProfile);
        if (!vorbereiteteProben.isEmpty()) scene.put("vorbereiteteProben", vorbereiteteProben);

        return scene;
    }

    // ---------------------------------------------------------------
    // Textblock-Verwaltung
    // ---------------------------------------------------------------

    private static void flushTextBlock(List<Map<String, Object>> target, StringBuilder text,
                                        String type, String titel)
    {
        String content = text.toString().trim();
        if (!content.isEmpty() && content.length() > 10)
        {
            Map<String, Object> block = new LinkedHashMap<>();
            block.put("uid", uid());
            block.put("typ", type);
            if (titel != null) block.put("titel", titel);
            block.put("text", content);
            target.add(block);
        }
        text.setLength(0);
    }

    // Profile werden über ProfileExtractorMain.parseProfileFromBlock() erzeugt

    // ---------------------------------------------------------------
    // Proben-Erkennung aus Fließtext
    // ---------------------------------------------------------------

    private static void extractSkillChecks(String text, List<Map<String, Object>> proben)
    {
        extractSkillChecksWithPattern(text, SKILL_CHECK_PATTERN, proben);
        extractSkillChecksWithPattern(text, SKILL_CHECK_PATTERN_PLAIN, proben);
    }

    private static void extractSkillChecksWithPattern(String text, Pattern pattern,
                                                       List<Map<String, Object>> proben)
    {
        Matcher m = pattern.matcher(text);
        while (m.find())
        {
            String skillNameRaw = m.group(1).trim();
            String modStr = m.group(2) != null ? m.group(2).replaceAll("\\s+", "").replace("–", "-") : null;

            // Skill-Name und optionale Anwendungsgebiet/Einsatzgebiet trennen
            String skillName = skillNameRaw;
            String usage = null;
            if (skillNameRaw.contains("("))
            {
                skillName = skillNameRaw.substring(0, skillNameRaw.indexOf('(')).trim();
                usage = skillNameRaw.substring(skillNameRaw.indexOf('(') + 1).replace(")", "").trim();
            }

            // SkillKey auflösen
            String skillKey = SKILL_NAME_MAP.get(skillName);
            if (skillKey == null) continue; // kein bekanntes Talent

            Integer skillOrdinal = SKILL_ORDINALS.get(skillKey);
            int modifier = 0;
            if (modStr != null)
            {
                try { modifier = Integer.parseInt(modStr); } catch (NumberFormatException ignored) {}
            }

            // Probentyp bestimmen
            String checkType;
            if (text.contains("Sammelprobe")) checkType = "sammelprobe";
            else if (text.contains("Vergleichsprobe") || text.contains("Vergleichende Probe")) checkType = "vergleichsprobe";
            else if (text.contains("Gruppenprobe")) checkType = "gruppenprobe";
            else checkType = "erfolgsprobe";

            // Deduplizierung: gleicher Skill + gleicher Modifier = gleiche Probe
            final int mod = modifier;
            final String sk = skillKey;
            boolean exists = proben.stream().anyMatch(p ->
            {
                @SuppressWarnings("unchecked")
                Map<String, Object> sc = (Map<String, Object>) p.get("skillCheck");
                return sc != null && sk.equals(sc.get("_skillKey")) &&
                    mod == ((Number) sc.getOrDefault("modifikator", 0)).intValue();
            });
            if (exists) continue;

            // VorbereiteteProbe aufbauen
            Map<String, Object> probe = new LinkedHashMap<>();
            probe.put("uid", uid());
            probe.put("name", skillNameRaw + (modStr != null ? " " + modStr.replace("-", "–") : ""));

            Map<String, Object> skillCheck = new LinkedHashMap<>();
            skillCheck.put("uid", uid());
            skillCheck.put("checkType", 0); // CheckType.skill
            skillCheck.put("skillCheckType", checkTypeOrdinal(checkType));
            if (skillOrdinal != null) skillCheck.put("skillKey", skillOrdinal);
            skillCheck.put("_skillKey", skillKey); // intern für Deduplizierung
            skillCheck.put("modifikator", modifier);
            if (usage != null) skillCheck.put("topicKey", usage);

            probe.put("skillCheck", skillCheck);

            // QS-Ergebnisse aus dem umgebenden Text extrahieren
            List<Map<String, Object>> qsResults = extractQsResults(text);
            if (!qsResults.isEmpty()) probe.put("qsErgebnisse", qsResults);

            proben.add(probe);
        }
    }

    private static int checkTypeOrdinal(String type)
    {
        return switch (type)
        {
            case "erfolgsprobe" -> 0;
            case "vergleichsprobe" -> 1;
            case "sammelprobe" -> 2;
            case "gruppenprobe" -> 3;
            default -> 0;
        };
    }

    private static List<Map<String, Object>> extractQsResults(String text)
    {
        List<Map<String, Object>> results = new ArrayList<>();
        // Pattern: "QS 1: Text", "mit QS 2 oder mehr", "mindestens QS 3"
        Matcher m = Pattern.compile("QS\\s*(\\d+)\\s*(?:oder\\s*mehr)?\\s*[:\\-–]\\s*(.+?)(?=QS\\s*\\d|\\.|$)").matcher(text);
        while (m.find())
        {
            int qs = Integer.parseInt(m.group(1));
            String description = m.group(2).trim();
            if (description.length() < 5) continue;

            Map<String, Object> qr = new LinkedHashMap<>();
            qr.put("vonQs", qs);
            qr.put("text", description.replaceAll("\\*+", "").trim());
            results.add(qr);
        }
        return results;
    }

    // ---------------------------------------------------------------
    // Hilfsmethoden
    // ---------------------------------------------------------------

    private static String findProfileName(String[] lines, int attrLineIdx)
    {
        for (int j = attrLineIdx - 1; j >= Math.max(0, attrLineIdx - 10); j--)
        {
            String line = lines[j].trim();
            if (line.isEmpty() || line.startsWith("<!--")) continue;
            if (line.matches("^#{1,4}\\s+.+"))
                return line.replaceAll("^#{1,4}\\s+", "").replaceAll("\\*+", "").trim();
        }
        return "Unbekanntes Profil";
    }

    private static int collectProfileBlock(String[] lines, int startIdx, int maxIdx)
    {
        int end = startIdx;
        for (int j = startIdx + 1; j <= Math.min(maxIdx, startIdx + 60); j++)
        {
            String line = lines[j].trim();
            if (line.matches("^#{1,3}\\s+.+") && !line.contains("Aktionen:")) break;
            end = j;
            if (line.contains("**Schmerz") || line.contains("**Flucht:") || line.contains("**Erfahren:"))
                break;
        }
        return end;
    }

    private static String cleanMarkdown(String text)
    {
        return text.replaceAll("\\*+", "").trim();
    }

    private static String uid()
    {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @SuppressWarnings("unchecked")
    private static void loadEnumOrdinals()
    {
        try (InputStream is = AdventureExtractorMain.class.getResourceAsStream("/enum-ordinals.json"))
        {
            if (is == null) return;
            Map<String, Map<String, Integer>> root = new ObjectMapper().readValue(is, new TypeReference<>() {});
            SKILL_ORDINALS = root.getOrDefault("skillKey", Map.of());
        }
        catch (IOException e)
        {
            LOGGER.error("Fehler: {}", e.getMessage());
        }
    }
}
