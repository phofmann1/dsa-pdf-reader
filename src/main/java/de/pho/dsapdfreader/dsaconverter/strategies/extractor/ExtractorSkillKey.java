package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;

public class ExtractorSkillKey extends Extractor
{
  private ExtractorSkillKey()
  {
  }

  public static SkillKey retrieveSkillKey(String name)
  {
    return extractSkillKeyFromText(name);
  }

  public static List<SkillKey> retrieveSkillKeysForMysticalSkillRaw(MysticalSkillRaw msr)
  {
    List<SkillKey> returnValue = new ArrayList<>();
    if (msr.talentKey != null)
    {
      if (msr.talentKey.contains("Musizieren")) returnValue.add(SkillKey.musizieren);
      if (msr.talentKey.contains("Singen")) returnValue.add(SkillKey.singen);
    }
    return returnValue;
  }

  public static SkillUsageKey retrieveSkillUsageKey(String usage)
  {
    try
    {
      return SkillUsageKey.fromString(usage);

    }
    catch (IllegalArgumentException e)
    {
      //System.out.println("US: " + usage + ",");
      LOGGER.error(e.getMessage(), e);
    }
    return null;
  }

  private static SkillKey extractSkillKeyFromText(String name)
  {
    SkillKey returnValue;
    String skillKeyString = extractKeyTextFromTextWithUmlauts(name).toLowerCase();

    try {
      returnValue = SkillKey.valueOf(skillKeyString);
    }
    catch (IllegalArgumentException e) {
      returnValue = null;
      //System.out.println(skillKeyString + " --> ");
      LOGGER.error("Invalid Skill name: " + skillKeyString);
    }
    return returnValue;
  }

  public static boolean isSkillKey(String skillName) {
    String enumName = extractKeyTextFromTextWithUmlauts(skillName).toLowerCase();

    try {
      SkillKey.valueOf(enumName);
      return true;
    }
    catch (IllegalArgumentException e) {
      return false;
    }
  }
}
