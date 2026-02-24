package de.pho.dsapdfreader.exporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.dsaconverter.model.AlchimieRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorEquipmentKey;
import de.pho.dsapdfreader.exporter.model.Alias;
import de.pho.dsapdfreader.exporter.model.Berufsgeheimnis;
import de.pho.dsapdfreader.exporter.model.Equipment;
import de.pho.dsapdfreader.exporter.model.Price;
import de.pho.dsapdfreader.exporter.model.QSEntry;
import de.pho.dsapdfreader.exporter.model.RequirementSkill;
import de.pho.dsapdfreader.exporter.model.RequirementsSkill;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.LaborKey;
import de.pho.dsapdfreader.exporter.model.enums.LanguageKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.AlchimieA;

public abstract class LoadToAlchimieA {

  public static <T extends AlchimieA> T iniAlchimie(T newObject, AlchimieRaw raw) {
    newObject.name = raw.name;
    newObject.alternativeNamen = Arrays.stream(raw.alternativeNamen.split(",")).flatMap(t -> parseEntry(t).stream()).toList();
    newObject.berufsgeheimnis = extractBerufsgeheimnis(raw.apValue);
    newObject.typicalIngredients = Arrays.stream(raw.typicalIngredients.split(",")).map(String::trim).toList();
    if (raw.cost != null && !raw.cost.isEmpty()) {
      newObject.kostenIngredienzien = new Price();
            newObject.kostenIngredienzien.isPricePerLevel = true;

            Pattern pattern = Pattern.compile("^(\\d+)");
            Matcher matcher = pattern.matcher(raw.cost);

            if (matcher.find()) {
                newObject.kostenIngredienzien.priceInSilver = Double.parseDouble(matcher.group(1));
            }
        }
    newObject.labor = Extractor.extractEnumKey(raw.labor.toLowerCase(), LaborKey.class);
        if(raw.brewingDifficulty !=  null && !raw.brewingDifficulty.isEmpty()) {
            newObject.brewingDifficulty = Integer.valueOf(raw.brewingDifficulty.replace("–", "-").replaceAll("\\+/- ?0", "0"));
        }
        newObject.requirements = Arrays.stream(raw.requirements.split(",")).map(String::trim).toList();
        newObject.description = raw.description;
        //newObject.;
        if(raw.wertPreis != null && !raw.wertPreis.isEmpty()) {
            newObject.preis = new Price();
            newObject.preis.isPricePerLevel = true;

            Pattern pattern = Pattern.compile("^([\\d.]+)");
            Matcher matcher = pattern.matcher(raw.wertPreis);

            if (matcher.find()) {
                newObject.preis.priceInSilver = Double.parseDouble(matcher.group(1).replace(".", ""));
            }
        }
        newObject.besonderheiten = raw.besonderheiten;

        if (raw.hyperpotenteWirkung != null && !raw.hyperpotenteWirkung.isEmpty()) {
            newObject.hyperpotenteWirkung = new QSEntry();
            newObject.hyperpotenteWirkung.qs = 7;
            newObject.hyperpotenteWirkung.information = raw.hyperpotenteWirkung;
        }


        return newObject;
    }

    private static Berufsgeheimnis extractBerufsgeheimnis(String bgh) {
        Berufsgeheimnis bg = null;
        Pattern pattern = Pattern.compile("(?<ap>\\d*) AP( \\(Voraussetzung(en)?: (?<skill>[A-ü &-]*[a-ü]) (?<skillValue>\\d*)\\))?");
        Matcher matcher = pattern.matcher(bgh);

        if (matcher.find()) {
            bg = new Berufsgeheimnis();

            String apStr = matcher.group("ap");
            bg.ap = Integer.parseInt(apStr);

            String skill = matcher.group("skill");       // can be null
            String skillValueStr = matcher.group("skillValue"); // can be null

            if (skill != null && skillValueStr != null) {
                bg.requirementsSkill = new RequirementsSkill();
                bg.requirementsSkill.requirements = new ArrayList<>();
                RequirementSkill req = new RequirementSkill();
              req.minValue = Integer.parseInt(skillValueStr);
              req.skillKey = Extractor.extractEnumKey(skill.toLowerCase(), SkillKey.class);
              bg.requirementsSkill.requirements.add(req);
            }
        }
        return bg;
    }

    public static List<Equipment> extractEquipment(AlchimieA alchimie, EquipmentCategoryKey eck) {
        List<Equipment> returnValue = new ArrayList<>();
        if (alchimie.kostenIngredienzien != null) {
            Equipment e = new Equipment();
            e.name = "Ingredienzien " + alchimie.name + " (pro Stufe)";
            e.key = ExtractorEquipmentKey.extractEquipmentKeyFromText(eck.name() + " " + e.name);
            e.equipmentCategoryKey = EquipmentCategoryKey.alchimistische_zutaten;
            e.price = alchimie.kostenIngredienzien;
            returnValue.add(e);
        }
        Equipment e = new Equipment();
        e.name = alchimie.name;
        e.key = ExtractorEquipmentKey.extractEquipmentKeyFromText(eck.name() + " " + e.name);
        e.equipmentCategoryKey = EquipmentCategoryKey.alchimica;
        e.price = alchimie.preis;
        returnValue.add(e);

        return returnValue;
    }

    //INTERNAL
    private static List<Alias> parseEntry(String entry) {
        // 1. Extract language from full string
        Matcher m = Pattern.compile("^(.*)\\(([^)]+)\\)\\s*$").matcher(entry);
        LanguageKey lang = null;

        if (m.find()) {
            String langString = m.group(2).trim();
            lang = mapLanguage(langString);

            // remove "(Language)" from the text
            entry = m.group(1).trim();
        } else {
            entry = entry.trim();
        }

        // 2. Split into multiple names by "oder"
        List<String> names = Arrays.stream(entry.split("\\s+oder\\s+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // 3. Apply the SAME language to all names
        List<Alias> result = new ArrayList<>();
        for (String n : names) {
            result.add(new Alias(n, lang));
        }

        return result;
    }

    public static LanguageKey mapLanguage(String lang) {
        // normalize
        String normalized = lang
                .toLowerCase()
                .replaceAll("[^a-zäöüß]", "_")  // remove special chars
                .replaceAll("_+", "_")          // collapse __ → _
                .replaceAll("_$", "");          // remove trailing _

        for (LanguageKey k : LanguageKey.values()) {
            if (k.name().equalsIgnoreCase(normalized)) {
                return k;
            }
        }
        System.err.println("Unknown language: " + lang);
        return null;
        //throw new IllegalArgumentException("Unknown language: " + lang);
    }
}


