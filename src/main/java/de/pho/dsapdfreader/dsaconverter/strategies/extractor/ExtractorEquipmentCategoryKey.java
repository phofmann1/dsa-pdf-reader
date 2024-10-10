package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;

public class ExtractorEquipmentCategoryKey extends Extractor
{
  public static EquipmentCategoryKey retrieve(String name)
  {
    EquipmentCategoryKey returnValue = null;
    try
    {
      returnValue = extractEquipmentCategoryKeyFromText(name.replaceAll("^Reisebedarf und Werkzeug$", "Reisebedarf und Werkzeuge"));
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

  public static EquipmentCategoryKey extractEquipmentCategoryKeyFromText(String name)
  {
    EquipmentCategoryKey returnValue;
    String keyString = extractKeyTextFromText(name.replaceAll("^Werkzeuge$", "Werkzeug"));
    keyString = keyString.trim();

    try
    {
      returnValue = EquipmentCategoryKey.valueOf(keyString.toLowerCase());
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error("Invalid EquipmentCategoryKey: " + keyString.toLowerCase());
      returnValue = null;
    }
    return returnValue;
  }

}
