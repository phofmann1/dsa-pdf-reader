package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.WeaponKey;

public class ExtractorWeaponKey extends Extractor
{

  public static WeaponKey retrieve(String name)
  {
    WeaponKey returnValue = null;
    try
    {
      returnValue = extractWeaponKeyFromText(name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e)
    {
      String msg = String.format("%s key could not be interpreted.", name);
      LOGGER.error(msg);
    }
    return returnValue;
  }

  public static WeaponKey extractWeaponKeyFromText(String armorName)
  {
    WeaponKey returnValue;
    String armorKeyString = extractKeyTextFromText(armorName).toLowerCase();
    armorKeyString = armorKeyString.trim();

    try
    {
      returnValue = WeaponKey.valueOf(armorKeyString.toLowerCase());
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error("Invalid WeaponKey: " + armorKeyString);
      returnValue = null;
    }
    return returnValue;
  }
}
