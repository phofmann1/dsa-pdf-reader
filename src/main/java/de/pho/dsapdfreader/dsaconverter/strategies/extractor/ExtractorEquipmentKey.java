package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;

public class ExtractorEquipmentKey extends Extractor
{
  public static EquipmentKey retrieve(String name)
  {
    EquipmentKey returnValue = null;
    try
    {
      returnValue = extractEquipmentKeyFromText(name);
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

  public static EquipmentKey extractEquipmentKeyFromText(String name)
  {
    EquipmentKey returnValue;
    String keyString = extractKeyTextFromText(name);
    keyString = keyString.trim();

    try
    {
      returnValue = EquipmentKey.valueOf(keyString.toLowerCase());
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error("Invalid ArmorKey: " + keyString);
      returnValue = null;
    }
    return returnValue;
  }
}
