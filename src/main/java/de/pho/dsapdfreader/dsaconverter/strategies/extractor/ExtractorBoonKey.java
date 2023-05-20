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
      String msg = String.format("%s key could not be interpreted.", name);
      // LOGGER.error(msg);
    }
    return returnValue;
  }

  public static BoonKey extractBoonKeyFromText(String name)
  {
    BoonKey returnValue;
    String keyString = extractKeyTextFromTextWithUmlauts(
        name.replace("\u00AD", "-")
            .replace("(*)", "")
            .replace("*", "")
    );
    keyString = keyString.trim();

    try
    {
      returnValue = BoonKey.valueOf(keyString.toLowerCase());
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error("Invalid BoonKey: " + keyString, e);
      //System.out.println(keyString.toLowerCase());
      returnValue = null;
    }
    return returnValue;
  }
}
