package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.SkillRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAdvancementCategory;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCheck;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkillKey;
import de.pho.dsapdfreader.exporter.model.Skill;


public class LoadToSkill
{

  private LoadToSkill()
  {
  }

  public static Skill migrate(SkillRaw raw)
  {
    Skill returnValue = new Skill();

    returnValue.name = raw.name.replace("\u00AD", "-");
    returnValue.key = ExtractorSkillKey.retrieveSkillKey(raw.name);

    returnValue.advancementCategory = ExtractorAdvancementCategory.retrieveAdvancementCategory(raw);
    returnValue.check = ExtractorCheck.retrieveCheck(raw);
    /*
    returnValue.skillCategory;

    returnValue.isEncumbered;

    returnValue.applicationKeys;
    returnValue.skillUsageKeys;

     */


    return returnValue;
  }

}
