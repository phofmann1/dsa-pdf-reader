package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;

public class ExtractorSpecieKey extends Extractor {
  public static SpecieKey retrieveOptional(String text) {
    return extractSpecieKeyFromText(text);

  }

  public static SpecieKey retrieve(String text) {
    SpecieKey returnValue = null;
    try {
      returnValue = retrieveOptional(text);
      if (returnValue == null) {
        throw new IllegalArgumentException();
      }
    }
    catch (IllegalArgumentException e) {
      String msg = String.format("Specie '%s' key could not be interpreted.", text);
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
    try {
      returnValue = SpecieKey.valueOf(keyString);
    }
    catch (IllegalArgumentException e) {
    }

    return returnValue;
  }
}
