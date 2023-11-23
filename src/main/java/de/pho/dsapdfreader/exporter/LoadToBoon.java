package de.pho.dsapdfreader.exporter;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.model.BoonRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAP;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorBoonKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorRequirements;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSpecialAbility;
import de.pho.dsapdfreader.exporter.model.Boon;
import de.pho.dsapdfreader.exporter.model.SkillUsage;
import de.pho.dsapdfreader.exporter.model.enums.BoonCategory;
import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SelectionCategory;


public class LoadToBoon
{
  protected static final int INFINITE = 999;
  protected static final Logger LOGGER = LogManager.getLogger();

  private LoadToBoon()
  {
  }

  public static Boon migrate(BoonRaw raw)
  {
    Boon returnValue = new Boon();

    int levels = LoadToSpecialAbility.extractLevels(raw.name);
    String baseName = levels > 1
        ? raw.name.split("(?= (I-|I\\/))")[0]
        : raw.name;

    returnValue.name = baseName;
    returnValue.key = ExtractorBoonKey.retrieve(baseName);
    returnValue.publications = List.of(Publication.valueOf(raw.publication));
    returnValue.levels = levels;
    returnValue.ap = ExtractorAP.retrieve(raw.ap, 1);
    returnValue.category = returnValue.ap < 0 ? BoonCategory.FLAW : BoonCategory.MERIT;
    returnValue.selectable = !raw.name.contains("(*)");
    try
    {
      returnValue.requiredTraditions = ExtractorRequirements.extractTraditionKeysFromText(raw.preconditions);
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error(e.getMessage());
    }
    SkillUsage su = ExtractorSpecialAbility.retrieveSkillUsage(raw.rules);
    if (su != null)
    {
      returnValue.newSkillUsageKey = su.key;
    }

    returnValue.multiselect = extractMulitselect(returnValue.key);
    returnValue.selectionCategory = extractSelectionCategory(returnValue.key);
    returnValue.requirementsSpecie = ExtractorRequirements.extractSpecieReqsForBoon(returnValue.key);
    returnValue.requiredCulture = ExtractorRequirements.extractCultureReqsForBoon(returnValue.key);
    returnValue.requirementBoons = ExtractorRequirements.extractRequirementsBoon(raw.preconditions);

    /*

    returnValue.valueChange;
    returnValue.variants;
     */
    return returnValue;
  }

  private static SelectionCategory extractSelectionCategory(BoonKey key)
  {
    return switch (key)
        {
          case artefaktgebunden -> SelectionCategory.artifact;
          case herausragende_kampftechnik, waffenbegabung -> SelectionCategory.combatSkill;
          case begabung_mystisch -> SelectionCategory.mysticalSkill;
          case immunität_gegen_krankheit -> SelectionCategory.desease;
          case immunität_gegen_gift -> SelectionCategory.poison;
          case herausragende_fertigkeit, begabung, unfähig -> SelectionCategory.skill;
          default -> null;
        };
  }

  private static int extractMulitselect(BoonKey key)
  {
    return switch (key)
        {
          case eingeschränkter_sinn, körperliche_auffälligkeit, persönlichkeitsschwäche -> 2;
          case unfähig, begabung, waffenbegabung, begabung_mystisch -> 3;
          case schlechte_angewohnheit, schlechte_eigenschaft, herausragende_fertigkeit -> INFINITE;
          default -> 1;
        };
  }

}
