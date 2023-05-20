package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillVariantKey;

public class ExtractorMysticalSkillKey extends Extractor
{
  public static MysticalSkillKey retrieveMysticalSkillKey(MysticalSkillRaw msr, MysticalSkillCategory category)
  {
    MysticalSkillKey returnValue = null;
    try
    {
      returnValue = extractMysticalSkillKeyFromText(category, msr.name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e)
    {
      String msg = String.format("%s key for (%s - %s) could not be interpreted.", getPrefix(msr), category, msr.name);
      LOGGER.error(msg);
    }
    return returnValue;
  }

  public static MysticalSkillVariantKey extractMysticalSkillVariantKeyFromText(MysticalSkillKey msk, String variantText)
  {
    MysticalSkillVariantKey returnValue;
    if (msk == null)
    {
      return null;
    }
    //handle und (Kraft des Humus)
    //handle . ()
    //handle Zaubererweiterung
    String skillKeyString = msk
        + "_"
        + extractKeyTextFromText(variantText);
    skillKeyString = skillKeyString.trim();

    try
    {
      returnValue = MysticalSkillVariantKey.valueOf(skillKeyString);
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error("Invalid SkillVariantKey: " + skillKeyString, e);
      returnValue = null;
    }
    return returnValue;
  }

  public static String extractKeyTextFromText(String txt)
  {
    return txt == null ? "" : (txt.toUpperCase()
        .replace("Ä", "AE")
        .replace("Ö", "OE")
        .replace("Ü", "UE")
        .replace("ß", "SS")
        .replace("&", "UND")
        .replace("!", "")
        .replace("(", "")
        .replace(")", "")
        .replace("/", " ")
        .replace("?", "")
        .replace("’", " ")
        .replace(",", " ")
        .replace(" ..", "")
        .replace(".", "")
        .replaceAll("\s+", " ")
        .replace("-", "_")
    ).trim()
        .replace(" ", "_")
        .replace("__", "_");
  }


  public static MysticalSkillKey extractMysticalSkillKeyFromText(MysticalSkillCategory category, String name)
  {
    MysticalSkillKey returnValue;
    String skillKeyString = extractKeyTextFromText(category.name())
        + "_"
        + extractKeyTextFromText(name);

    try
    {
      returnValue = MysticalSkillKey.valueOf(skillKeyString);
    }
    catch (IllegalArgumentException e)
    {
      returnValue = null;
    }
    return returnValue;
  }
}
