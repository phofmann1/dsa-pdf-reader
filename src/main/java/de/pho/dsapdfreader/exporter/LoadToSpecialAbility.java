package de.pho.dsapdfreader.exporter;

import static de.pho.dsapdfreader.tools.roman.RomanNumberHelper.intToRoman;
import static de.pho.dsapdfreader.tools.roman.RomanNumberHelper.romanToInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAP;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCombatSkillKeys;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSpecialAbility;
import de.pho.dsapdfreader.exporter.model.RequirementMysticalSkill;
import de.pho.dsapdfreader.exporter.model.RequirementSkill;
import de.pho.dsapdfreader.exporter.model.RequirementSkillSum;
import de.pho.dsapdfreader.exporter.model.RequirementsCombatSkill;
import de.pho.dsapdfreader.exporter.model.RequirementsSkill;
import de.pho.dsapdfreader.exporter.model.SkillUsage;
import de.pho.dsapdfreader.exporter.model.SpecialAbility;
import de.pho.dsapdfreader.exporter.model.enums.DsaState;
import de.pho.dsapdfreader.exporter.model.enums.LogicalOperatorKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SelectionCategory;
import de.pho.dsapdfreader.exporter.model.enums.SkillApplicationKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;
import de.pho.dsapdfreader.tools.merger.ObjectMerger;


public class LoadToSpecialAbility
{

  public static final Pattern EXTRACT_UPPER_ROMAN = Pattern.compile("(?<=(I-|\\/))[IVX]{1,4}");

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

  public static final String[] BRACKETED_NAMES = {
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
    raw.name = raw.name.replace("Göttlicher Schutz", "Göttlicher Schutz I-II")
        .replace("Herrschaft über Dämonen", "Herrschaft über Dämonen I-III");
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
        boolean isHealingSpec = specialAbility.name.equals("Heilungsspezialgebiet");
        boolean isGebieterDesAspekts = raw.name.equals("Gebieter des (Aspekts)");

        if (!isAuthor && !isHealingSpec && !isGebieterDesAspekts)
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

          SkillUsage su = ExtractorSpecialAbility.retrieveSkillUsage(raw.rules);
          if (su != null)
          {
            specialAbility.newSkillUsageKey = su.key;
          }
        }

        if (specialAbility.key != SpecialAbilityKey.fertigkeitsspezialisierung)
        {
          if (ExtractorSpecialAbility.PAT_HAS_NEW_SKILL_APPLICATION.matcher(raw.rules).find())
          {
            String keyString = Extractor.extractKeyTextFromTextWithUmlauts(specialAbility.name).toLowerCase();
            try
            {
              specialAbility.newSkillApplicationKey = SkillApplicationKey.valueOf(keyString);
            }
            catch (IllegalArgumentException e)
            {
              System.out.println("SA: " + keyString);
            }
          }
        }


        boolean isUseSamePrecondition = levels > 1 && !raw.preconditions.contains("Stufe");

        specialAbility.requiredState = extractRequiredState(raw.preconditions);

        raw.preconditions = raw.preconditions
            .replace("Status Kind der Finsternis", "")
            .replace("Status Lykanthrop", "")
            .replace("Sonderfertigkeit Selbstbestimmter Verwandlungszeitpunkt", "Sonderfertigkeit Selbstbestimmter Verwandlungszeitpunkt I");
        //specialAbility.name.startsWith("Schnellladen ");
        Map<String, String> preconditionsMap = ExtractorSpecialAbility.generatePreconditionMap(raw.preconditions);


        specialAbility.requiredSpecie = ExtractorSpecialAbility.retrieveRequiredSpecie(raw.preconditions);
        specialAbility.requireOneOfTraditions = ExtractorSpecialAbility.retrieveRequiredTradition(raw.preconditions, specialAbility.name);
        specialAbility.requireOneOfBoons = ExtractorSpecialAbility.retrieveRequiredOneOfBoons(specialAbility.category);
        specialAbility.requireNoneOfBoons = ExtractorSpecialAbility.retrieveRequiredNoneOfBoons(raw.preconditions, specialAbility.key);
        specialAbility.requirementsAttribute = ExtractorSpecialAbility.retrieveRequirementAttribute(preconditionsMap, levels, currentLevel, specialAbility.category, isUseSamePrecondition, specialAbility.name);

        Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> reqs = ExtractorSpecialAbility.retrieveRequirementsSkill(preconditionsMap, levels, currentLevel, specialAbility.name, isUseSamePrecondition);
        specialAbility.requirementsSkill = reqs.getValue0();
        specialAbility.requirementsSkillsSum = reqs.getValue1();
        specialAbility.requirementsCombatSkill = reqs.getValue2();
        specialAbility.requirementMysticalSkill = reqs.getValue3();
        specialAbility.requirementsAbility = ExtractorSpecialAbility.retrieveRequirementsAbility(preconditionsMap, specialAbility.name, levels, currentLevel, isUseSamePrecondition);
      /*
      specialAbility.valueChange;
      */
        // Heilungsspezialgebiet (Anwendungsgebiet)
        if (isAuthor)
        {
          returnValue.addAll(generateScribeList(specialAbility));
        }
        else if (isHealingSpec)
        {
          returnValue.addAll(generateHealingSpecList(specialAbility));
        }
        else if (isGebieterDesAspekts)
        {
          returnValue.addAll(generateGebieterDesAspektsList(specialAbility));
        }
        else
        {
          returnValue.add(specialAbility);
        }
      }
    }
    return returnValue.stream();
  }

  public static List<? extends SpecialAbility> generateGebieterDesAspektsList(SpecialAbility specialAbility)
  {
    List<SpecialAbility> returnValue = new ArrayList<>();

    specialAbility.newSkillUsageKey = null;
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_antimagie,
        "Gebieter/in der Antimagie")
    );

    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_begierde,
        "Gebieter/in der Begierde"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_bildung,
        "Gebieter/in der Bildung"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_ekstase,
        "Gebieter/in der Ekstase"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_erkenntnis,
        "Gebieter/in der Erkenntnis"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_flamme,
        "Gebieter/in der Flamme"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_freiheit,
        "Gebieter/in der Freiheit"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_freundschaft,
        "Gebieter/in der Freundschaft"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_harmonie,
        "Gebieter/in der Harmonie"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_heilung,
        "Gebieter/in der Heilung"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_hilfsbereitschaft,
        "Gebieter/in der Hilfsbereitschaft"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_jagd,
        "Gebieter/in der Jagd"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_kälte,
        "Gebieter/in der Kälte"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_kraft,
        "Gebieter/in der Kraft"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_landwirtschaft,
        "Gebieter/in der Landwirtschaft"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_magie,
        "Gebieter/in der Magie"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_natur,
        "Gebieter/in der Natur"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_ordnung,
        "Gebieter/in der Ordnung"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_reise,
        "Gebieter/in der Reise"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_schatten,
        "Gebieter/in der Schatten"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_tapferkeit,
        "Gebieter/in der Tapferkeit"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_träume,
        "Gebieter/in der Träume"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_vergänglichkeit,
        "Gebieter/in der Vergänglichkeit"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_vision,
        "Gebieter/in der Vision"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_der_wogen,
        "Gebieter/in der Wogen"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_erzes,
        "Gebieter/in des Erzes"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_feuers,
        "Gebieter/in des Feuers"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_guten_goldes,
        "Gebieter/in des guten Goldes"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_guten_kampfes,
        "Gebieter/in des guten Kampfes"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_handels,
        "Gebieter/in des Handels"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_handwerks,
        "Gebieter/in des Handwerks"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_heims,
        "Gebieter/in des Heims"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_rausches,
        "Gebieter/in des Rausches"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_schicksals,
        "Gebieter/in des Schicksals"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_schilds,
        "Gebieter/in des Schilds"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_sturms,
        "Gebieter/in des Sturms"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_todes,
        "Gebieter/in des Todes"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_wandels,
        "Gebieter/in des Wandels"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_winds,
        "Gebieter/in des Windes"));
    returnValue.add(generateGebieterDesAspekts(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.gebieter_in_des_wissens,
        "Gebieter/in des Wissens"));

    return returnValue;
  }

  private static SpecialAbility generateGebieterDesAspekts(SpecialAbility specialAbility, SpecialAbilityKey specialAbilityKey, String name)
  {
    specialAbility.key = specialAbilityKey;
    specialAbility.name = name;
    return specialAbility;
  }

  public static Collection<? extends SpecialAbility> generateHealingSpecList(SpecialAbility specialAbility)
  {
    List<SpecialAbility> returnValue = new ArrayList<>();

    specialAbility.newSkillUsageKey = null;
    returnValue.add(generateHealingSpec(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.heilungsspezialgebiet_amputieren,
        SkillUsageKey.amputieren,
        "Heilungsspezialist (Amputieren)",
        8, 8)
    );
    returnValue.add(generateHealingSpec(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.heilungsspezialgebiet_chirurgie,
        SkillUsageKey.chirurgie,
        "Heilungsspezialist (Chirurgie)",
        12, 12));
    returnValue.add(generateHealingSpec(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.heilungsspezialgebiet_knochenbrüche,
        SkillUsageKey.knochenbrüche,
        "Heilungsspezialist (Knochenbrüche)",
        5));
    returnValue.add(generateHealingSpec(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.heilungsspezialgebiet_verbrennungen,
        SkillUsageKey.verbrennungen,
        "Heilungsspezialist (Verbrennungen)",
        8, 8));
    returnValue.add(generateHealingSpec(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.heilungsspezialgebiet_zahnbehandlung,
        SkillUsageKey.zahnbehandlung,
        "Heilungsspezialist (Zahnebehandlung)",
        3));

    return returnValue;
  }

  private static SpecialAbility generateHealingSpec(SpecialAbility specialAbility, SpecialAbilityKey specialAbilityKey, SkillUsageKey skillUsageKey, String name, int ap)
  {
    return generateHealingSpec(specialAbility, specialAbilityKey, skillUsageKey, name, ap, 0);
  }

  private static SpecialAbility generateHealingSpec(SpecialAbility specialAbility, SpecialAbilityKey specialAbilityKey, SkillUsageKey skillUsageKey, String name, int ap, int minSkillValue)
  {
    specialAbility.key = specialAbilityKey;
    specialAbility.newSkillUsageKey = skillUsageKey;
    specialAbility.name = name;
    specialAbility.ap = ap;

    if (minSkillValue > 0)
    {
      RequirementsSkill rs = new RequirementsSkill();
      rs.logicalOpperator = LogicalOperatorKey.and;
      RequirementSkill r = new RequirementSkill();
      r.minValue = minSkillValue;
      r.skillKey = SkillKey.heilkunde_wunden;
      rs.requirements = List.of(r);
      specialAbility.requirementsSkill = rs;
    }
    return specialAbility;
  }

  private static DsaState extractRequiredState(String preconditions)
  {
    if (preconditions.contains("Status Kind der Finsternis")) return DsaState.kind_der_finsternis;
    else if (preconditions.contains("Status Lykanthrop")) return DsaState.lykanthrop;
    else return null;
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

  public static String extractName(String baseName, int levels, int currentLevel, boolean removeBrackets)
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

  public static int extractLevels(SpecialAbilityRaw msr)
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


  public static List<SpecialAbility> generateScribeList(SpecialAbility specialAbility)
  {
    List<SpecialAbility> returnValue = new ArrayList<>();

    specialAbility.newSkillUsageKey = null;
    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_betören_liebesroman,
        "Liebesroman",
        SkillKey.betören));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_etikette_poesie,
        "Poesie",
        SkillKey.etikette));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_bekehren_und_überzeugen_hetzschriften,
        "Hetzschriften",
        SkillKey.bekehren_und_überzeugen));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_gassenwissen_kriminalgeschichten,
        "Kriminalgeschichten",
        SkillKey.gassenwissen));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_überreden_romane,
        "Romane",
        SkillKey.überreden));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_sagen_und_legenden_märchen,
        "Märchen",
        SkillKey.sagen_und_legenden));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_brett_und_glücksspiel_fachpublikation,
        "Fachpublikation (Brett- & Glücksspiele)",
        SkillKey.brett_und_glücksspiel));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_geographie_fachpublikation,
        "Fachpublikation Geographie",
        SkillKey.geographie));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_geschichtswissen_fachpublikation,
        "Fachpublikation Geschichte",
        SkillKey.geschichtswissen));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_götter_und_kulte_fachpublikation,
        "Fachpublikation Götter & Kulte",
        SkillKey.götter_und_kulte));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_kriegskunst_fachpublikation,
        "Fachpublikation Kriegskunst",
        SkillKey.kriegskunst));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_magiekunde_fachpublikation,
        "Fachpublikation Magiekunde",
        SkillKey.magiekunde));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_mechanik_fachpublikation,
        "Fachpublikation Mechanik",
        SkillKey.mechanik));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_rechnen_fachpublikation,
        "Fachpublikation Rechnen",
        SkillKey.rechnen));


    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_rechtskunde_fachpublikation,
        "Fachpublikation Rechtskunde",
        SkillKey.rechtskunde));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_sphärenkunde_fachpublikation,
        "Fachpublikation Sphärenkunde",
        SkillKey.sphärenkunde));

    returnValue.add(generateSchriftstellerei(
        ObjectMerger.merge(specialAbility, new SpecialAbility()),
        SpecialAbilityKey.schriftstellerei_sternkunde_fachpublikation,
        "Fachpublikation Sternkunde",
        SkillKey.sternkunde));

    return returnValue;
  }

  private static SpecialAbility generateSchriftstellerei(SpecialAbility scribe, SpecialAbilityKey abilityKey, String usageName, SkillKey skillKey)
  {
    scribe.key = abilityKey;
    scribe.name = "Schriftstellerei " + usageName.replace("Fachpublikation", "").trim();
    scribe.newSkillUsageKey = SkillUsageKey.valueOf(Extractor.extractKeyTextFromTextWithUmlauts(usageName).toLowerCase());
    scribe.requirementsSkill = new RequirementsSkill();
    RequirementSkill r = new RequirementSkill();
    r.skillKey = skillKey;
    r.minValue = 4;
    scribe.requirementsSkill.requirements.add(r);
    return scribe;
  }

}
