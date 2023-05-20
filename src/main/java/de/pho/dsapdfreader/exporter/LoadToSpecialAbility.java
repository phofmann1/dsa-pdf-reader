package de.pho.dsapdfreader.exporter;

import static de.pho.dsapdfreader.tools.roman.RomanNumberHelper.intToRoman;
import static de.pho.dsapdfreader.tools.roman.RomanNumberHelper.romanToInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import de.pho.dsapdfreader.dsaconverter.model.SpecialAbilityRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAP;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSpecialAbility;
import de.pho.dsapdfreader.exporter.model.SpecialAbility;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SelectionCategory;
import de.pho.dsapdfreader.exporter.model.enums.SkillCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;
import de.pho.dsapdfreader.tools.merger.ObjectMerger;


public class LoadToSpecialAbility
{

  private static final Pattern EXTRACT_UPPER_ROMAN = Pattern.compile("(?<=(I-|\\/))[IVX]{1,4}");

  private static final String[] BEEINDRUCKENDE_VORSTELLUNG_VARIANTS = {
      "Gaukeleien",
      "Musizieren",
      "Singen",
      "Tanzen"
  };

  private static final String[] GIFTVERSTAERKUNG_VARIANTS = {
      "Pflanzenkunde",
      "Tierkunde",
      "Steinbearbeitung",
      "Alchimie"
  };

  private static final String[] LOADING_TIME_VARIANTS = {
      "Armbrüste",
      "Blasrohre",
      "Bögen",
      "Diskus",
      "Schleudern",
      "Wurfwaffen"
  };

  private static final String[] ELEMENTS = {
      "Feuer",
      "Wasser",
      "Luft",
      "Erz",
      "Eis",
      "Humus"
  };

  private static final String[] BRACKETED_NAMES = {
      "Improvisierte Liturgie",
      "Gebieter des",
      "Machtvolle Entschwörung",
      "Improvisationszauberei",
      "Entschwörung",
      "Blutmagie"
  };

  private static final Map<SpecialAbilityKey, SelectionCategory> SA_SELECTION_CATEGORY_MAP = new HashMap()
  {{
    put(SpecialAbilityKey.aspektkenntnis_i, SelectionCategory.FEATURE);
    put(SpecialAbilityKey.aspektkenntnis_ii, SelectionCategory.FEATURE);
    put(SpecialAbilityKey.aspektkenntnis_iii, SelectionCategory.FEATURE);
    put(SpecialAbilityKey.lieblingsliturgie, SelectionCategory.MYSTICALSKILL);

    put(SpecialAbilityKey.anatomie, SelectionCategory.SPECIE);
    put(SpecialAbilityKey.fachwissen, SelectionCategory.SKILL);
    put(SpecialAbilityKey.handwerkskunst, SelectionCategory.SKILL);
    put(SpecialAbilityKey.kind_der_natur, SelectionCategory.SKILL);
    put(SpecialAbilityKey.körperliches_geschick, SelectionCategory.SKILL);
    put(SpecialAbilityKey.soziale_kompetenz, SelectionCategory.SKILL);
    put(SpecialAbilityKey.universalgenie, SelectionCategory.SKILL);
    put(SpecialAbilityKey.weg_der_gelehrten, SelectionCategory.SKILL);
    put(SpecialAbilityKey.weg_der_künstlerin, SelectionCategory.SKILL);

    put(SpecialAbilityKey.adaption, SelectionCategory.MYSTICALSKILL);
    put(SpecialAbilityKey.lieblingsliturgie, SelectionCategory.MYSTICALSKILL);
    put(SpecialAbilityKey.merkmalskenntnis_i, SelectionCategory.FEATURE);
    put(SpecialAbilityKey.merkmalskenntnis_ii, SelectionCategory.FEATURE);
    put(SpecialAbilityKey.merkmalskenntnis_iii, SelectionCategory.FEATURE);
  }};

  private static final Map<SpecialAbilityKey, SkillCategoryKey> SA_SKILL_CATEGORY_MAP = new HashMap()
  {{
    put(SpecialAbilityKey.fachwissen, SkillCategoryKey.wissenstalente);
    put(SpecialAbilityKey.handwerkskunst, SkillCategoryKey.handwerkstalente);
    put(SpecialAbilityKey.kind_der_natur, SkillCategoryKey.naturtalente);
    put(SpecialAbilityKey.körperliches_geschick, SkillCategoryKey.körpertalente);
    put(SpecialAbilityKey.soziale_kompetenz, SkillCategoryKey.gesellschaftstalente);
    put(SpecialAbilityKey.universalgenie, SkillCategoryKey.wissenstalente);
    put(SpecialAbilityKey.weg_der_gelehrten, SkillCategoryKey.wissenstalente);
  }};
  private LoadToSpecialAbility()
  {
  }

  public static Stream<SpecialAbility> migrate(SpecialAbilityRaw raw)
  {
    List<SpecialAbility> returnValue = new ArrayList<>();
    int levels = extractLevels(raw);
    String baseName = levels > 1
        ? raw.name.split("(?= (I-|I\\/))")[0]
        : raw.name;

    boolean ignoreBrackets = Arrays.stream(BRACKETED_NAMES).filter(bn -> baseName.startsWith(bn)).count() == 0;
    for (int i = 0; i < levels; i++)
    {
      SpecialAbility specialAbility = new SpecialAbility();
      specialAbility.name = extractName(baseName, levels, i, ignoreBrackets);
      specialAbility.publication = Publication.valueOf(raw.publication);
      specialAbility.category = raw.abilityCategory;
      specialAbility.key = ExtractorSpecialAbility.retrieve(specialAbility.name);
      specialAbility.ap = ExtractorAP.retrieve(raw.ap, i);
      specialAbility.abilityType = ExtractorSpecialAbility.retrieveType(raw.description);

      specialAbility.multiselect = ExtractorSpecialAbility.retrieveMultiselect(raw.rules);

      specialAbility.selectionCategory = SA_SELECTION_CATEGORY_MAP.get(specialAbility.key);
      specialAbility.skillCategory = SA_SKILL_CATEGORY_MAP.get(specialAbility.key);
      specialAbility.combatSkillKeys = ExtractorSpecialAbility.retrieveCombatSkillKeys(raw.combatSkills);
      specialAbility.advancedAbilities = ExtractorSpecialAbility.retrieveAdvancedAbilities(raw.advancedAbilities, specialAbility.combatSkillKeys);
      specialAbility.hasFreeText = specialAbility.key == SpecialAbilityKey.ungeheuer_taktik; // Ungeheuer-Taktik

      if (specialAbility.key == SpecialAbilityKey.prunkkleidung_herstellen
          || specialAbility.key == SpecialAbilityKey.bine_maschores
          || specialAbility.key == SpecialAbilityKey.rechnungswesen
          || specialAbility.key == SpecialAbilityKey.schriftstellerei)
      {
        System.out.println(raw.rules);
      }

      if (specialAbility.key != SpecialAbilityKey.fertigkeitsspezialisierung)
        specialAbility.skillUsage = ExtractorSpecialAbility.retrieveSkillUsage(raw.rules);

      /*
      specialAbility.valueChange;
      specialAbility.skillApplication;

      specialAbility.requiredSpecie;
      specialAbility.requiredTradition;
      specialAbility.requireOneOfBoons;
      specialAbility.requireNoneOfBoons;
      specialAbility.requirementsAttribute;
      specialAbility.requirementsAbility;
      specialAbility.requirementsSkill;
      specialAbility.requirementsSkillsSum;
      specialAbility.requirementCombatSkill;
      specialAbility.requirementMysticalSkill;

      specialAbility.variants;
      */
      returnValue.add(specialAbility);
    }
    return returnValue.stream();
  }

  private static String extractSpecialName(String baseName, int i)
  {
    String suffix = switch (baseName)
        {
          case "Beeindruckende Vorstellung" -> " " + BEEINDRUCKENDE_VORSTELLUNG_VARIANTS[i];
          case "Giftverstärkung" -> " " + GIFTVERSTAERKUNG_VARIANTS[i];
          case "Nachladespezialist", "Schnellladen" -> " " + LOADING_TIME_VARIANTS[i];
          case "Gegnerische Zauberpraxis" -> " " + ELEMENTS[i];
          default -> "";
        };
    return baseName + suffix;
  }

  private static String extractName(String baseName, int levels, int currentLevel, boolean removeBrackets)
  {
    String name = removeBrackets ? baseName.replaceAll(" \\(.*\\)", "") : baseName;
    String n = extractSpecialName(name, currentLevel);
    String levelAffix = "";

    if (baseName.equals(n))
    {
      levelAffix = " " + intToRoman(currentLevel + 1);
    }
    return n + (levels > 1 ? levelAffix : "");
  }

  private static int extractLevels(SpecialAbilityRaw msr)
  {
    Matcher m = EXTRACT_UPPER_ROMAN.matcher(msr.name);
    return switch (msr.name)
        {
          case "Beeindruckende Vorstellung" -> BEEINDRUCKENDE_VORSTELLUNG_VARIANTS.length;
          case "Giftverstärkung" -> GIFTVERSTAERKUNG_VARIANTS.length;
          case "Nachladespezialist", "Schnellladen" -> LOADING_TIME_VARIANTS.length;
          case "Merkmalskenntnis", "Aspektkenntnis" -> 3;
          case "Gegnerische Zauberpraxis (Element)" -> 6;
          default -> m.find() ? romanToInt(m.group()) : 1;
        };
  }

  public static void applyCorrections(SpecialAbility sa, List<SpecialAbility> corrections)
  {
    Optional<SpecialAbility> correction = corrections.stream().filter(c -> c.key == sa.key).findFirst();
    if (correction.isPresent())
    {
      ObjectMerger.merge(correction.get(), sa);
    }
  }
}
