package de.pho.dsapdfreader.exporter;

import static de.pho.dsapdfreader.tools.roman.RomanNumberHelper.intToRoman;
import static de.pho.dsapdfreader.tools.roman.RomanNumberHelper.romanToInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import de.pho.dsapdfreader.dsaconverter.model.MysticalActivityObjectRitualRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAP;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillCost;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorObjectRitual;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorRequirements;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorVolume;
import de.pho.dsapdfreader.exporter.model.Cost;
import de.pho.dsapdfreader.exporter.model.ObjectRitual;
import de.pho.dsapdfreader.exporter.model.RequirementSpecialAbility;
import de.pho.dsapdfreader.exporter.model.RequirementsSpecialAbility;
import de.pho.dsapdfreader.exporter.model.enums.LogicalOperatorKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillFeature;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;
import de.pho.dsapdfreader.tools.merger.ObjectMerger;


public class LoadToObjectRitual
{

  private static final String[] NAMES_TIERWANDLUNG = {
      "(Chamäleon)",
      "(Speikobra)",
      "(Eichhörnchen)",
      "(Biber)",
      "(Taube)",
      "(Rabe)",
  };

  private static final String[] TYPES_BANNSCHWERT = {
      "Dolche",
      "Schwerter",
      "Zweihandschwerter"
  };

  private static final String[] ENTITIES = {
      "Chimären",
      "Daimonide",
      "Golems",
      "Untote",
      "Feen",
      "Geister",
      "Dämonen",
      "Elementare"
  };

  private LoadToObjectRitual()
  {
  }

  public static Stream<ObjectRitual> migrate(MysticalActivityObjectRitualRaw raw)
  {
    List<ObjectRitual> returnValues = new ArrayList<>();

    int levels = extractLevels(raw);
    String baseName = levels > 1
        ? raw.name.split("(?= (I-|I\\/))")[0]
        : raw.name;

    for (int currentLevel = 0; currentLevel < levels; currentLevel++)
    {
      ObjectRitual or = new ObjectRitual();
      or.name = extractName(baseName, levels, currentLevel);
      or.key = ExtractorObjectRitual.extractOrKeyFromName(or.name);
      or.publication = Publication.valueOf(raw.publication);
      or.artifactKey = raw.artifactKey;

      or.ap = ExtractorAP.retrieve(raw.ap, currentLevel);
      if (raw.binding != null)
      {
        Cost c = new Cost();
        c.costText = raw.binding;
        c.permanentCost = 2;
        or.binding = c;
      }
      or.featureKey = MysticalSkillFeature.fromString(raw.feature);
      or.volume = ExtractorVolume.retrieve(raw.volume, currentLevel);

      Map<String, String> lvlReqMap = ExtractorRequirements.extractLevelRequirementMap(raw.requirements);
      or.requiredOrKeys = ExtractorObjectRitual.retrieveRequirementsObjectRitual(lvlReqMap, levels, currentLevel, or.key);

      handleReqsSpecialAbility(or, raw.requirements);

      or.costActivation = ExtractorMysticalSkillCost.retrieveCost(raw.cost, or.name);

      returnValues.add(or);
    }
    return returnValues.stream();
  }

  private static void handleReqsSpecialAbility(ObjectRitual or, String requirements)
  {
    if (requirements.contains("Schalenverzauberung"))
    {
      or.requirementsSpecialAbility = new RequirementsSpecialAbility();
      RequirementSpecialAbility req = new RequirementSpecialAbility();
      req.abilityKey = SpecialAbilityKey.schalenverzauberung;
      or.requirementsSpecialAbility.requirements.add(req);
    }
    else if (requirements.contains("Merkmal Illusion"))
    {
      or.requirementsSpecialAbility = new RequirementsSpecialAbility();
      or.requirementsSpecialAbility.logicalOpperator = LogicalOperatorKey.or;

      RequirementSpecialAbility req1 = new RequirementSpecialAbility();
      req1.abilityKey = SpecialAbilityKey.merkmalskenntnis_i;
      req1.variant = "Illusion";
      or.requirementsSpecialAbility.requirements.add(req1);

      RequirementSpecialAbility req2 = new RequirementSpecialAbility();
      req2.abilityKey = SpecialAbilityKey.merkmalskenntnis_ii;
      req2.variant = "Illusion";
      or.requirementsSpecialAbility.requirements.add(req2);

      RequirementSpecialAbility req3 = new RequirementSpecialAbility();
      req3.abilityKey = SpecialAbilityKey.merkmalskenntnis_iii;
      req3.variant = "Illusion";
      or.requirementsSpecialAbility.requirements.add(req3);
    }
    else if (requirements.contains("Merkmal ") || requirements.contains("Merkmalskenntnis"))
    {
      or.requirementsSpecialAbility = new RequirementsSpecialAbility();
      or.requirementsSpecialAbility.logicalOpperator = LogicalOperatorKey.or;

      RequirementSpecialAbility req = new RequirementSpecialAbility();
      req.abilityKey = SpecialAbilityKey.merkmalskenntnis_i;
      or.requirementsSpecialAbility.requirements.add(req);
    }
  }

  public static int extractLevels(MysticalActivityObjectRitualRaw msr)
  {
    Matcher m = LoadToSpecialAbility.EXTRACT_UPPER_ROMAN.matcher(msr.name);
    return switch (msr.name)
        {
          case "Tierwandlung" -> NAMES_TIERWANDLUNG.length;
          case "Bindung des Bannschwerts" -> TYPES_BANNSCHWERT.length;
          case "Klinge wider (Wesenheit)" -> ENTITIES.length;
          default -> m.find() ? romanToInt(m.group()) : 1;
        };
  }

  public static String extractName(String baseName, int levels, int currentLevel)
  {
    String n = extractSpecialName(baseName, currentLevel);
    String levelAffix = "";

    if (baseName.equals(n))
    {
      levelAffix = " " + intToRoman(currentLevel + 1);
    }

    return n + (levels > 1 ? levelAffix : "");
  }


  private static String extractSpecialName(String baseName, int i)
  {
    String suffix = switch (baseName)
        {
          case "Tierwandlung" -> " (" + NAMES_TIERWANDLUNG[i] + ")";
          case "Bindung des Bannschwerts" -> " (" + TYPES_BANNSCHWERT[i] + ")";
          case "Klinge wider (Wesenheit)" -> " (" + ENTITIES[i] + ")";
          default -> "";
        };
    return baseName.replaceAll(" \\(.*\\)", "") + suffix;
  }


  public static void applyCorrections(ObjectRitual oRitual, List<ObjectRitual> corrections)
  {
    Optional<ObjectRitual> correction = corrections.stream().filter(c -> c.key == oRitual.key).findFirst();
    if (correction.isPresent())
    {
      ObjectMerger.merge(correction.get(), oRitual);
    }
  }
}
