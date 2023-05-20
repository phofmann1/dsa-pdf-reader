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
      //LOGGER.error(msg);
    }
    return returnValue;
  }

  public static WeaponKey extractWeaponKeyFromText(String weaponName)
  {
    WeaponKey returnValue;
    String keyString = extractKeyTextFromText(weaponName).toLowerCase();
    keyString = keyString.trim();

    try
    {
      returnValue = WeaponKey.valueOf(keyString.toLowerCase());
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error("Invalid WeaponKey: " + weaponName);
      returnValue = null;
    }
    return returnValue;
  }
}
