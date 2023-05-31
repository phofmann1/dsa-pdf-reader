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
      System.out.println(usage + ",");
      //LOGGER.error(e.getMessage(), e);
    }
    return null;
  }
}
