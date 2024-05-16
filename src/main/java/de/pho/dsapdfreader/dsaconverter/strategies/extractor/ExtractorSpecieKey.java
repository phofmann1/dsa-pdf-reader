package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;

public class ExtractorSpecieKey extends Extractor {
  public static SpecieKey retrieve(String name) {
    SpecieKey returnValue = null;
    try {
      returnValue = extractSpecieKeyFromText(name);
      if (returnValue == null) {
        throw new IllegalArgumentException();
      }
    }
    catch (IllegalArgumentException e) {
      String msg = String.format("Specie '%s' key could not be interpreted.", name);
      //LOGGER.error(msg);
    }
    return returnValue;
  }

  private static SpecieKey extractSpecieKeyFromText(String name) {
    SpecieKey returnValue = null;
    String keyString = extractKeyTextFromTextWithUmlauts(
        name.replace("\u00AD", "-")
            .replace("(*)", "")
            .replace("*", "")
            .replace("...", "x")
            .replace("ß", "XXX") //Korrektur UpperCase

    ).replace("XXX", "ß").toLowerCase();

    keyString = keyString.trim();
    returnValue = SpecieKey.valueOf(keyString);
    return returnValue;
  }
}
