package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.BoonKey;

public class ExtractorBoon extends Extractor {
  public static BoonKey retrieve(String name) {
    BoonKey returnValue = null;
    returnValue = extractBoonKeyFromText(name);
    return returnValue;
  }

  private static BoonKey extractBoonKeyFromText(String name) throws IllegalArgumentException {
    BoonKey returnValue = null;
    String keyString = extractKeyTextFromTextWithUmlauts(
        name.replace("\u00AD", "-")
            .replace("(*)", "")
            .replace("*", "")
            .replace("...", "x")
            .replace("ß", "XXX") //Korrektur UpperCase

    ).replace("XXX", "ß").toLowerCase();

    keyString = keyString.trim();
    try {
      returnValue = BoonKey.valueOf(keyString);
    }
    catch (IllegalArgumentException e) {
      String msg = String.format("Boon '%s' key could not be interpreted.", keyString);
      LOGGER.error(msg);
    }
    return returnValue;
  }
}
