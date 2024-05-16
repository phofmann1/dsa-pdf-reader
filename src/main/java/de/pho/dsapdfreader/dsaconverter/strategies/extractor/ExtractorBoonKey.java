package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.BoonKey;

public class ExtractorBoonKey extends Extractor
{
  public static BoonKey retrieve(String name)
  {
    BoonKey returnValue = null;
    try
    {
      returnValue = extractBoonKeyFromText(name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
      String msg = String.format("Boon '%s' key could not be interpreted.", name);
      LOGGER.error(msg);
    }
    return returnValue;
  }

  private static BoonKey extractBoonKeyFromText(String name)
  {
    BoonKey returnValue = null;
    String keyString = extractKeyTextFromTextWithUmlauts(
        name.replace("\u00AD", "-")
            .replace("(*)", "")
            .replace("*", "")
            .replace("...", "x")
            .replace("ß", "XXX") //Korrektur UpperCase

    ).replace("XXX", "ß").toLowerCase();

    keyString = keyString.trim();
    returnValue = BoonKey.valueOf(keyString);
    return returnValue;
  }
}
