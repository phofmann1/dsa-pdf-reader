package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillVariantKey;

public class ExtractorMysticalSkillKey extends Extractor
{
  public static MysticalSkillKey retrieveMysticalSkillKey(String publication, String name, MysticalSkillCategory category)
  {
    MysticalSkillKey returnValue = null;
    try
    {
      returnValue = extractMysticalSkillKeyFromText(category, name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e)
    {
      String msg = String.format("%s key for (%s - %s) could not be interpreted.", getPrefix(publication, name), category, name);
      //LOGGER.error(msg);
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
    String skillKeyString = msk
        + "_"
        + extractKeyTextFromText(variantText).toLowerCase();
    skillKeyString = skillKeyString.trim();

    try
    {
      returnValue = MysticalSkillVariantKey.valueOf(skillKeyString);
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error("Invalid MysticalSkillVariantKey: " + skillKeyString, e);
      //System.out.println(skillKeyString+",");
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
        .replace("[", "")
        .replace("]", "")
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
    String skillKeyString = (extractKeyTextFromText(category.name())
        + "_"
        + extractKeyTextFromText(name)).replaceAll("_+", "_").toLowerCase();

    try
    {
      returnValue = MysticalSkillKey.valueOf(skillKeyString);
    }
    catch (IllegalArgumentException e)
    {
      returnValue = null;
      //System.out.println(skillKeyString + ", ");
      LOGGER.error("Invalid MysticalSkillKey: " + skillKeyString);
    }
    return returnValue;
  }
}
