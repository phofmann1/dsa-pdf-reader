package de.pho.dsapdfreader.exporter;

import static de.pho.dsapdfreader.tools.roman.RomanNumberHelper.intToRoman;
import static de.pho.dsapdfreader.tools.roman.RomanNumberHelper.romanToInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.javatuples.Quartet;
import org.javatuples.Triplet;

import de.pho.dsapdfreader.dsaconverter.model.SpecialAbilityRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAP;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCombatSkillKeys;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSpecialAbility;
import de.pho.dsapdfreader.exporter.model.RequirementMysticalSkill;
import de.pho.dsapdfreader.exporter.model.RequirementSkill;
import de.pho.dsapdfreader.exporter.model.RequirementSkillSum;
import de.pho.dsapdfreader.exporter.model.RequirementsCombatSkill;
import de.pho.dsapdfreader.exporter.model.RequirementsSkill;
import de.pho.dsapdfreader.exporter.model.SpecialAbility;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SelectionCategory;
import de.pho.dsapdfreader.exporter.model.enums.SkillCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;
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

  private static final Map<SpecialAbilityKey, SelectionCategory> SA_SELECTION_CATEGORY_MAP = new EnumMap<>(SpecialAbilityKey.class);
  private static final Map<SpecialAbilityKey, SkillCategoryKey> SA_SKILL_CATEGORY_MAP = new EnumMap<>(SpecialAbilityKey.class);

  static
  {
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.aspektkenntnis_i, SelectionCategory.feature);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.aspektkenntnis_ii, SelectionCategory.feature);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.aspektkenntnis_iii, SelectionCategory.feature);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.lieblingsliturgie, SelectionCategory.mysticalSkill);

    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.anatomie, SelectionCategory.specie);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.fachwissen, SelectionCategory.skill);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.handwerkskunst, SelectionCategory.skill);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.kind_der_natur, SelectionCategory.skill);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.körperliches_geschick, SelectionCategory.skill);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.soziale_kompetenz, SelectionCategory.skill);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.universalgenie, SelectionCategory.skill);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.weg_der_gelehrten, SelectionCategory.skill);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.weg_der_künstlerin, SelectionCategory.skill);

    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.adaption, SelectionCategory.mysticalSkill);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.lieblingszauber, SelectionCategory.mysticalSkill);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.merkmalskenntnis_i, SelectionCategory.feature);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.merkmalskenntnis_ii, SelectionCategory.feature);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.merkmalskenntnis_iii, SelectionCategory.feature);

    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.geländekunde, SelectionCategory.terrain);
    SA_SELECTION_CATEGORY_MAP.put(SpecialAbilityKey.anhänger_des_güldenen, SelectionCategory.traditionCleric);
  }

  static
  {
    SA_SKILL_CATEGORY_MAP.put(SpecialAbilityKey.fachwissen, SkillCategoryKey.wissenstalente);
    SA_SKILL_CATEGORY_MAP.put(SpecialAbilityKey.handwerkskunst, SkillCategoryKey.handwerkstalente);
    SA_SKILL_CATEGORY_MAP.put(SpecialAbilityKey.kind_der_natur, SkillCategoryKey.naturtalente);
    SA_SKILL_CATEGORY_MAP.put(SpecialAbilityKey.körperliches_geschick, SkillCategoryKey.körpertalente);
    SA_SKILL_CATEGORY_MAP.put(SpecialAbilityKey.soziale_kompetenz, SkillCategoryKey.gesellschaftstalente);
    SA_SKILL_CATEGORY_MAP.put(SpecialAbilityKey.universalgenie, SkillCategoryKey.wissenstalente);
    SA_SKILL_CATEGORY_MAP.put(SpecialAbilityKey.weg_der_gelehrten, SkillCategoryKey.wissenstalente);
  }

  private LoadToSpecialAbility()
  {
  }

  public static Stream<SpecialAbility> migrate(SpecialAbilityRaw raw)
  {
    List<SpecialAbility> returnValue = new ArrayList<>();
    boolean isIgnored = extractSpecialAbilityIgnored(raw);
    if (!isIgnored)
    {
      int levels = extractLevels(raw);
      String baseName = levels > 1
          ? raw.name.split("(?= (I-|I\\/))")[0]
          : raw.name;

      boolean ignoreBrackets = Arrays.stream(BRACKETED_NAMES).filter(bn -> baseName.startsWith(bn)).count() == 0;
      for (int currentLevel = 0; currentLevel < levels; currentLevel++)
      {
        raw.preconditions = raw.preconditions.replace(" Intuition ", " IN ");
        SpecialAbility specialAbility = new SpecialAbility();
        specialAbility.name = extractName(baseName, levels, currentLevel, ignoreBrackets);
        boolean isAuthor = specialAbility.name.equals("Schriftstellerei");
        if (!isAuthor)
          specialAbility.key = ExtractorSpecialAbility.retrieve(specialAbility.name);
        specialAbility.publication = Publication.valueOf(raw.publication);
        specialAbility.category = raw.abilityCategory;
        specialAbility.ap = ExtractorAP.retrieve(raw.ap, currentLevel);
        specialAbility.abilityType = ExtractorSpecialAbility.retrieveType(raw.description);

        specialAbility.multiselect = ExtractorSpecialAbility.retrieveMultiselect(raw.rules);

        specialAbility.selectionCategory = SA_SELECTION_CATEGORY_MAP.get(specialAbility.key);
        specialAbility.skillCategory = SA_SKILL_CATEGORY_MAP.get(specialAbility.key);
        specialAbility.combatSkillKeys = ExtractorCombatSkillKeys.retrieveFromCombatSkillsText(raw.combatSkills);
        Triplet<Boolean, Boolean, Boolean> allowedWepons = ExtractorSpecialAbility.retrieveAllowedWeapons(raw.combatSkills);
        specialAbility.isOnlyParryWeapon = allowedWepons.getValue0();
        specialAbility.isOnlyElfenWeapon = allowedWepons.getValue1();
        specialAbility.isOnlyDwarfenWeapon = allowedWepons.getValue2();

        specialAbility.advancedAbilities = ExtractorSpecialAbility.retrieveAdvancedAbilities(raw.advancedAbilities, specialAbility.combatSkillKeys);
        specialAbility.hasFreeText = specialAbility.key == SpecialAbilityKey.ungeheuer_taktik; // Ungeheuer-Taktik

        if (specialAbility.key != SpecialAbilityKey.fertigkeitsspezialisierung)
        {
          specialAbility.newSkillUsage = ExtractorSpecialAbility.retrieveSkillUsage(raw.rules);
        }
        //TODO: specialAbility.skillApplication;

        specialAbility.requiredSpecie = ExtractorSpecialAbility.retrieveRequiredSpecie(raw.preconditions);
        specialAbility.requireOneOfTraditions = ExtractorSpecialAbility.retrieveRequiredTradition(raw.preconditions, specialAbility.name);
        specialAbility.requireOneOfBoons = ExtractorSpecialAbility.retrieveRequiredOneOfBoons(specialAbility.category);
        specialAbility.requireNoneOfBoons = ExtractorSpecialAbility.retrieveRequiredNoneOfBoons(raw.preconditions, specialAbility.key);
        specialAbility.requirementsAttribute = ExtractorSpecialAbility.retrieveRequirementAttribute(raw.preconditions, levels, currentLevel, specialAbility.category);
        Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> reqs = ExtractorSpecialAbility.retrieveRequirementsSkill(raw.preconditions, levels, currentLevel, specialAbility.name);
        specialAbility.requirementsSkill = reqs.getValue0();
        specialAbility.requirementsSkillsSum = reqs.getValue1();
        specialAbility.requirementsCombatSkill = reqs.getValue2();
        specialAbility.requirementMysticalSkill = reqs.getValue3();
        specialAbility.requirementsAbility = ExtractorSpecialAbility.retrieveRequirementsAbility(raw.preconditions, specialAbility.name, levels, currentLevel);
      /*
      specialAbility.valueChange;
      */

        if (isAuthor)
        {
          returnValue.addAll(generateScribeList(specialAbility));
        }
        else
        {
          returnValue.add(specialAbility);
        }
      }
    }
    return returnValue.stream();
  }

  private static boolean extractSpecialAbilityIgnored(SpecialAbilityRaw raw)
  {
    return raw.name.equals("Fertigkeitsspezialisierung (Talente)") || raw.name.startsWith("Sprache I") || raw.name.equals("Schrift");
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

    String bracket = (n.contains("(") && !n.contains(")")) ? ")" : "";
    return n + (levels > 1 ? levelAffix : "") + bracket;
  }

  private static int extractLevels(SpecialAbilityRaw msr)
  {
    Matcher m = EXTRACT_UPPER_ROMAN.matcher(msr.name);
    return switch (msr.name)
        {
          case "Beeindruckende Vorstellung" -> BEEINDRUCKENDE_VORSTELLUNG_VARIANTS.length;
          case "Giftverstärkung (Giftart)" -> GIFTVERSTAERKUNG_VARIANTS.length;
          case "Nachladespezialist (Kampftechnik)", "Schnellladen (Kampftechnik)" -> LOADING_TIME_VARIANTS.length;
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


  private static List<SpecialAbility> generateScribeList(SpecialAbility specialAbility)
  {
    List<SpecialAbility> returnValue = new ArrayList<>();

    specialAbility.newSkillUsage.skillKeys = new ArrayList<>();
    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_betören_liebesroman,
        "Liebesroman",
        SkillUsageKey.liebesroman,
        SkillKey.betören));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_etikette_poesie,
        "Poesie",
        SkillUsageKey.poesie,
        SkillKey.etikette));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_bekehren_und_überzeugen_hetzschriften,
        "Herzschriften",
        SkillUsageKey.hetzrschriften,
        SkillKey.bekehren_und_überzeugen));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_gassenwissen_kriminalgeschichten,
        "Kriminalgeschichten",
        SkillUsageKey.kriminalgeschichten,
        SkillKey.gassenwissen));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_überreden_romane,
        "Romane",
        SkillUsageKey.romane,
        SkillKey.überreden));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_sagen_und_legenden_märchen,
        "Märchen",
        SkillUsageKey.märchen,
        SkillKey.sagen_und_legenden));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_brett_und_glücksspiel_fachpublikation,
        "Fachpublikation (Brett- & Glücksspiele)",
        SkillUsageKey.fachpublikation_brett_und_glücksspiele,
        SkillKey.brett_und_glücksspiel));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_geographie_fachpublikation,
        "Fachpublikation Geographie",
        SkillUsageKey.fachpublikation_geographie,
        SkillKey.geographie));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_geschichtswissen_fachpublikation,
        "Fachpublikation Geschichte",
        SkillUsageKey.fachpublikation_geschichte,
        SkillKey.geschichtswissen));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_götter_und_kulte_fachpublikation,
        "Fachpublikation Götter & Kulte",
        SkillUsageKey.fachpublikation_götter_und_kulte,
        SkillKey.götter_und_kulte));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_kriegskunst_fachpublikation,
        "Fachpublikation Kriegskunst",
        SkillUsageKey.fachpublikation_kriegskunst,
        SkillKey.kriegskunst));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_magiekunde_fachpublikation,
        "Fachpublikation Magiekunde",
        SkillUsageKey.fachpublikation_magiekunde,
        SkillKey.magiekunde));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_mechanik_fachpublikation,
        "Fachpublikation Mechanik",
        SkillUsageKey.fachpublikation_mechanik,
        SkillKey.mechanik));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_rechnen_fachpublikation,
        "Fachpublikation Rechnen",
        SkillUsageKey.fachpublikation_rechnen,
        SkillKey.rechnen));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_rechtskunde_fachpublikation,
        "Fachpublikation Rechtskunde",
        SkillUsageKey.fachpublikation_rechtskunde,
        SkillKey.rechtskunde));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_sphärenkunde_fachpublikation,
        "Fachpublikation Sphärenkunde",
        SkillUsageKey.fachpublikation_sphärenkunde,
        SkillKey.sphärenkunde));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_sternkunde_fachpublikation,
        "Fachpublikation Sternkunde",
        SkillUsageKey.fachpublikation_sternkunde,
        SkillKey.sternkunde));

    return returnValue;
  }

  private static SpecialAbility generateSchriftstellerei(SpecialAbility scribe, SpecialAbilityKey abilityKey, String usageName, SkillUsageKey usageKey, SkillKey skillKey)
  {
    scribe.key = abilityKey;
    scribe.name = scribe.name + " " + usageName.replace("Fachpublikation", "").trim();
    scribe.newSkillUsage.name = usageName;
    scribe.newSkillUsage.key = usageKey;
    scribe.newSkillUsage.skillKeys.add(skillKey);
    scribe.requirementsSkill = new RequirementsSkill();
    RequirementSkill r = new RequirementSkill();
    r.skillKey = skillKey;
    r.minValue = 4;
    scribe.requirementsSkill.requirements.add(r);
    return scribe;
  }
}
