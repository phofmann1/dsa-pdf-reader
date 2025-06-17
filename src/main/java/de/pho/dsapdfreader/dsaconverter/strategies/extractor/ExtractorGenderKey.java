package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.GenderKey;

public class ExtractorGenderKey extends Extractor {
  public static GenderKey retrieve(String name) {
    GenderKey returnValue = null;
    try {
      returnValue = extractCultureKeyFromText(name);
      if (returnValue == null) {
        throw new IllegalArgumentException();
      }
    }
    catch (IllegalArgumentException e) {
      e.printStackTrace();
      String msg = String.format("%s gender key could not be interpreted.", name);
      //LOGGER.error(msg);
    }
    return returnValue;
  }

  private static GenderKey extractCultureKeyFromText(String name) {
    GenderKey returnValue = null;
    String keyString = extractKeyTextFromTextWithUmlauts(
        name.replace("\u00AD", "-")
            .replace("(*)", "")
            .replace("*", "")
            .replace("...", "x")
            .replace("ß", "XXX") //Korrektur UpperCase
    ).replace("XXX", "ß").toLowerCase();

    keyString = keyString.trim();
    returnValue = GenderKey.valueOf(keyString);
    return returnValue;
  }
}
