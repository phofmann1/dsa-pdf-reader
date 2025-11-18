package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.HerbRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCombatSkillKeys;
import de.pho.dsapdfreader.exporter.model.*;
import de.pho.dsapdfreader.exporter.model.enums.*;
import de.pho.dsapdfreader.tools.merger.ObjectMerger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoadToHerb {
    protected static final Logger LOGGER = LogManager.getLogger();

    private static final Pattern VALUE_PATTERN = Pattern.compile("<i>(.*?)</i>\\s*([^<]+)");

    // Schlüsselbegriffe für "kein Effekt" (KI Erzeugt also ggf. nicht vollständig oder keine echten Fälle)
    private static final List<String> NO_EFFECT_WORDS = Arrays.asList(
            "keine", "keiner", "nicht bekannt", "unbekannt", "keine wirkung", "keine effekte"
    );
    private LoadToHerb() {
    }

    public static Herb migrate(HerbRaw raw) {

        Herb herb = new Herb();
        herb.name = raw.name.replace("67 Kukuka", "Kukuka");
        herb.key = extractHerbKey(herb.name);
        herb.alternativeNamen = Arrays.stream(raw.alternativeNamen.split(",")).toList();
        herb.publications = List.of(Publication.Archiv_der_Kräuter);
        herb.landschaftstypen = new ArrayList<>();
        herb.regionen = new ArrayList<>();
        herb.suchschwierigkeit = Integer.valueOf(cleanupNumber(raw.suchschwierigkeit));
        herb.bestimmungsschwierigkeit = Integer.valueOf(cleanupNumber(raw.bestimmungsschwierigkeit));
        herb.anwendungen = extractAnwendungen(raw.anwendungen);
        herb.effekteRoh = extractEffekteRoh(raw.wirkung);
        List<Integer> prices = extractPrices(raw.preis);

        if(!prices.isEmpty()) {
            herb.preisRoh =  new Cost();
            herb.preisRoh.cost = prices.get(0);
        }
        if(prices.size() >= 2) {
            herb.preisVerarbeitet =  new Cost();
            herb.preisVerarbeitet.cost = prices.get(1);
        }
        herb.rezepte = extractRezepte(raw.rezepte);
        herb.alltagsarzneienUndVolksbrauchtum = raw.alltagsarzneienUndVolksbrauchtum;

        List<String>haltbarkeiten = extractHaltbarkeit(raw.haltbarkeit);
        herb.haltbarkeitRoh = haltbarkeiten.get(0);
        herb.haltbarkeitVerarbeitet = haltbarkeiten.get(1);
        herb.checkQs = extractCheckQs(raw.wissensfertigkeit, herb.key);
        return herb;
    }

    private static HerbKey extractHerbKey(String name) {
        String enumKeyStr = Extractor.extractKeyTextFromText(name).toLowerCase();
        try {
            // Enum.valueOf erwartet existierenden Enum; hier könntest du dynamisch hinzufügen
            // Alternativ: nur Strings sammeln
            return  HerbKey.valueOf(enumKeyStr);
        } catch (IllegalArgumentException e) {
            // Enum existiert noch nicht, evtl. loggen oder ignorieren
            //System.out.println(enumKeyStr+",");
            System.out.println(enumKeyStr+",");
        }
        return null;
    }

    public static CheckQs extractCheckQs(String text, HerbKey herbKey) {
        CheckQs result = new CheckQs(SkillKey.pflanzenkunde);

            // Split each QS entry
            String[] qsParts = text.split("(?=QS \\d+\\+?:)"); // split by QS 1, 2, 3+
            for (String part : qsParts) {
                part = part.replace("+", "").trim();
                if (part.isEmpty() || herbKey == null) continue;

                int colonIndex = part.indexOf(":");
                if (colonIndex != -1) {
                    String qsStr = part.substring(3, colonIndex).trim(); // after QS
                    String description = part.substring(colonIndex + 1).trim();
                    Integer qsValue = qsStr.endsWith("+") ? 3 : Integer.parseInt(qsStr);
                    String enumKeyStr = herbKey.name() + "_pflanzenkunde_"+qsValue;
                    CheckQsEntryKey key = null;
                    try {
                        // Enum.valueOf erwartet existierenden Enum; hier könntest du dynamisch hinzufügen
                        // Alternativ: nur Strings sammeln
                        key = CheckQsEntryKey.valueOf(enumKeyStr);
                    } catch (IllegalArgumentException e) {
                        // Enum existiert noch nicht, evtl. loggen oder ignorieren
                        //System.out.println(enumKeyStr+",");
                        System.out.println(enumKeyStr+",");
                    }
                    result.qualities.add(new QualityEntry(key, qsValue, description));
                }
            }
        return result;
    }

    public static List<String> extractHaltbarkeit(String text) {
        String roh = null;
        String verarbeitet = null;

        if (text != null && !text.isEmpty()) {
            // Suche nach "Roh:" oder "Roh" und "Verarbeitet:"
            int rohIndex = text.toLowerCase().indexOf("roh");
            int verarbeitetIndex = text.toLowerCase().indexOf("verarbeitet");

            if (rohIndex != -1) {
                if (verarbeitetIndex != -1 && verarbeitetIndex > rohIndex) {
                    // Text zwischen Roh und Verarbeitet
                    roh = text.substring(rohIndex, verarbeitetIndex).replaceFirst("(?i)roh[:\\s]*", "").trim();
                } else {
                    // Alles nach Roh bis Ende
                    roh = text.substring(rohIndex).replaceFirst("(?i)roh[:\\s]*", "").trim();
                }
            }

            if (verarbeitetIndex != -1) {
                // Alles nach Verarbeitet bis Ende
                verarbeitet = text.substring(verarbeitetIndex).replaceFirst("(?i)verarbeitet[:\\s]*", "").trim();
            }
        }

        return Arrays.asList(roh, verarbeitet);
    }

        public static List<RezeptKey> extractRezepte(String text) {
            List<RezeptKey> result = new ArrayList<>();
            if (text == null || text.isEmpty()) return result;

            // Zeilen aufteilen
            String[] lines = text.split("\\r?\\n|,");

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Ignoriere "noch keine bekannt" und ähnliche
                if (line.toLowerCase().contains("noch keine bekannt") || line.toLowerCase().contains("keine rezepte")) {
                    continue;
                }

                // Prüfe auf <i>Tag und extrahiere Name nach dem ":"
                int colonIndex = line.indexOf(":");
                if (colonIndex == -1) continue;

                String namePart = line.substring(colonIndex + 1).trim();

                // Entferne alles nach "(siehe" oder "siehe Seite"
                namePart = namePart.replaceAll("\\(siehe.*?\\)", "").trim();
                namePart = namePart.replaceAll("siehe Seite.*", "").trim();

                if (namePart.isEmpty()) continue;
                // Konvertiere in Enum-Format
                String cleanedName = namePart
                        .replace("Blutblatt kann als „besonders geeignete Gerätschaft“ bei Ritualen verwendet werden","")
                        .replace("<i>","")
                        .replace("</i>","")
                        .replace("siehe","")
                        .replace("(trägt bis zu 80 Stein","")
                        .replace("(doppelt so belastbar wie Hanfseil)","")
                        .replace("1001","Tausend und ein ")
                        .trim();
                String enumKeyStr = Extractor.extractKeyTextFromText(cleanedName).toLowerCase();
                try {
                    // Enum.valueOf erwartet existierenden Enum; hier könntest du dynamisch hinzufügen
                    // Alternativ: nur Strings sammeln
                    if(!enumKeyStr.isEmpty()) result.add(RezeptKey.valueOf(enumKeyStr));
                } catch (IllegalArgumentException e) {
                    // Enum existiert noch nicht, evtl. loggen oder ignorieren
                    //System.out.println(enumKeyStr+",");
                    System.out.println(enumKeyStr+","+ "    --> "+ cleanedName);
                }
            }

            return result;
        }

    private static List<Integer> extractPrices(String text) {
        // Alle Zahlen im Text extrahieren
        String[] parts = text.split("\\D+"); // trennt an allen Nicht-Ziffern
        List<Integer> numbers = new ArrayList<>();
        for (String p : parts) {
            if (!p.isEmpty()) {
                numbers.add(Integer.parseInt(p));
            }
        }

        return numbers;
    }

    private static List<HerbEffect> extractEffekteRoh(String rawText) {
        List<HerbEffect> effects = new ArrayList<>();

        if (rawText == null || rawText.isBlank())
            return effects;

        // 1️⃣ Alles ab "Verarbeitet" abschneiden
        String text = cutOffVerarbeitet(rawText);

        // 2️⃣ Effekte mit Regex extrahieren
        Matcher matcher = VALUE_PATTERN.matcher(text);
        while (matcher.find()) {
            String key = matcher.group(1).trim().toLowerCase();
            String value = matcher.group(2).trim();

            EffectVectorKey vectorKey = parseVectorKey(key);
            if (vectorKey == null) continue;

            // 3️⃣ "keine" o.Ä. überspringen
            if (isNoEffect(value)) continue;

            HerbEffect effect = new HerbEffect();
            effect.vectorKey = vectorKey;
            effect.beschreibung = cleanText(value);
            effect.categories = detectCategories(value);

            effects.add(effect);
        }

        return effects;
    }

    private static EffectVectorKey parseVectorKey(String key) {
        switch (key) {
            case "berührung":
            case "beruehrung":
                return EffectVectorKey.beruehrung;
            case "einatmung":
                return EffectVectorKey.einatmung;
            case "verzehr":
                return EffectVectorKey.verzehr;
            default:
                return null;
        }
    }

    private static List<EffectCategoryKey> detectCategories(String text) {
        List<EffectCategoryKey> list = new ArrayList<>();
        String lower = text.toLowerCase();

        // 1. Zustände / Statuseffekte
        if (lower.contains("betäubung") || lower.contains("verwirrung") || lower.contains("paralyse") ||
                lower.contains("schmerz") || lower.contains("handlungsunfähig") || lower.contains("eingeengt") ||
                lower.contains("fixiert") || lower.contains("berauscht") || lower.contains("entrückung") ||
                lower.contains("furcht") || lower.contains("krank") || lower.contains("dämonische auszehrung")) {
            list.add(EffectCategoryKey.zustand);
        }

        // 2. Giftwirkungen
        if (lower.contains("gift") || lower.contains("vergiftung") || lower.contains("brechreiz") ||
                lower.contains("übelkeit") || lower.contains("erbrechen") || text.contains("SP")) {
            list.add(EffectCategoryKey.gift);
        }

        // 3. Heilung & Regeneration
        if (lower.contains("heilt") || lower.contains("regeneration") || lower.contains("heilm") ||
                lower.contains("lep") || lower.contains("krankheits") || lower.contains("gifteffekt")) {
            list.add(EffectCategoryKey.heilung);
        }

        // 4. Magische / Astrale Effekte
        if (lower.contains("astral") || lower.contains("magisch") || lower.contains("dämon") || lower.contains("zauber")) {
            list.add(EffectCategoryKey.magisch);
        }

        if (lower.contains("geweiht") || lower.contains("entrückung")) {
            list.add(EffectCategoryKey.karmal);
        }

        // 5. Sinne & Wahrnehmung
        if (lower.contains("duft") || lower.contains("rauch") || lower.contains("dunkelsicht") ||
                lower.contains("vision") || lower.contains("sinnes") || lower.contains("wahrnehmung") ||
                lower.contains("träume") || lower.contains("eidetisch")) {
            list.add(EffectCategoryKey.sinnesveränderung);
        }

        // 6. Kampf- und Körper-Effekte
        if (lower.contains("kraftakt") || lower.contains("blutrausch") || lower.contains("jähzorn") ||
                lower.contains("fixiert") || lower.contains("eingeengt") || lower.contains("brennend")) {
            list.add(EffectCategoryKey.kampf);
        }

        // 7. Besondere körperliche / physische Effekte
        if (lower.contains("haut") || lower.contains("haare") || lower.contains("niesreiz") ||
                lower.contains("magenkrämpfe") || lower.contains("gliederschmerzen") || lower.contains("hunger") ||
                lower.contains("wurzelt") || lower.contains("schüttelfrost")) {
            list.add(EffectCategoryKey.körperlich);
        }

        // 8. Geist & Psyche
        if (lower.contains("blutrausch") || lower.contains("euphorie") || lower.contains("halluzination") ||
                lower.contains("furcht") || lower.contains("traum") || lower.contains("gedächtnisverlust")) {
            list.add(EffectCategoryKey.geistig);
        }

        // 9. Alchemische / Verstärkende Wirkungen
        if (lower.contains("elixier") || lower.contains("trank") || lower.contains("rezept") ||
                lower.contains("alchemie") || lower.contains("verstärkt")) {
            list.add(EffectCategoryKey.alchemisch);
        }

        return list;
    }


    private static boolean isNoEffect(String text) {
        String lower = text.toLowerCase().trim();
        for (String word : NO_EFFECT_WORDS) {
            if (lower.startsWith(word)) return true;
        }
        return false;
    }

    private static String cutOffVerarbeitet(String text) {
        int idx = text.toLowerCase().indexOf("verarbeitet");
        return (idx >= 0) ? text.substring(0, idx) : text;
    }

    private static String cleanText(String input) {
        return input.replaceAll("<[^>]+>", "").replaceAll("\\s+", " ").trim();
    }

    private static String cleanupNumber(String text) {
        return text.replace("–", "-")
                .replace("+/-", "")
                .replace("nicht wild auffindbar", "0")
                .replace(" (Um die Pflanze zu entdecken, ist eine Probe auf <i>Sinnesschärfe (Suchen</i> oder <i>Wahrnehmen)</i> -3 notwendig)", "")
                .replace("-3/-5, wenn nicht in Blüte", "-3")
                .replace("Man kann Nemezijn nicht finden, Nemezijn findet einen.", "-20")
                .replace("Nemezijn erdenkt seine Form, wie es Nemezijn beliebt.", "-20");
    }

    public static List<Integer> extractAnwendungen(String text) {
        List<Integer> returnValue = new ArrayList<>();
        String[] results = text
                .replace(" (bei QS 1-3 jede Variante einmal, ab QS 4 eine weitere zufällige Anwendung zusätzlich)", "")
                .replace("keine (es können aber ", "")
                .replace(" Sträucher gefunden werden)", "")
                .split("/");
        if(results.length > 1) {
            returnValue.addAll(Arrays.stream(results).map(Integer::valueOf).toList());
        }
        return returnValue;
    }

    public static void applyCorrections(Profession profession, List<Profession> corrections) {
        Optional<Profession> correction = corrections.stream().filter(c -> c.key == profession.key).findFirst();
        if (correction.isPresent()) {
            ObjectMerger.merge(correction.get(), profession);
        }
    }
}
