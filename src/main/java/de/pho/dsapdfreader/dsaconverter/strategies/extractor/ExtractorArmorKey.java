package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.ArmorKey;

public class ExtractorArmorKey extends Extractor
{
  public static ArmorKey retrieve(String name)
  {
    ArmorKey returnValue = null;
    try
    {
      returnValue = extractArmorKeyFromText(name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e)
    {
      String msg = String.format("%s key could not be interpreted.", name);
      //LOGGER.error(msg);
    }
    return returnValue;
  }

  public static ArmorKey extractArmorKeyFromText(String armorName)
  {
    ArmorKey returnValue = null;
    String armorKeyString = extractKeyTextFromText(armorName).toLowerCase();
    armorKeyString = armorKeyString.trim();

    try
    {
      returnValue = ArmorKey.valueOf(armorKeyString.toLowerCase());
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error("Invalid ArmorKey: " + armorKeyString);
    }
    return returnValue;
  }
}
