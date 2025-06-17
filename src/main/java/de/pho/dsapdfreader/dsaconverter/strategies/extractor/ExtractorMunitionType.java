package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.MunitionType;

public class ExtractorMunitionType extends Extractor
{

  public static MunitionType retrieve(String name)
  {
    MunitionType returnValue = MunitionType.KEINE;
    try
    {
      returnValue = extractMunitionTypeFromText(name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e)
    {
      String msg = String.format("%s munition type key could not be interpreted.", name);
      LOGGER.error(msg);
    }
    return returnValue;
  }

  public static MunitionType extractMunitionTypeFromText(String n)
  {
    MunitionType returnValue;
    String armorKeyString = extractKeyTextFromText(n)
        .replace("PFEILE", "PFEIL")
        .replace("KUGELN", "KUGEL");
    armorKeyString = armorKeyString.trim();

    try
    {
      returnValue = MunitionType.valueOf(armorKeyString);
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error("Invalid MunitionType: " + armorKeyString);
      returnValue = null;
    }
    return returnValue;
  }
}
