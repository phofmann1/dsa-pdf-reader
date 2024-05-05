package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.ProfessionKey;

public class ExtractorProfessionKey extends Extractor {
  public static ProfessionKey retrieve(String name) {
    ProfessionKey returnValue = null;
    try {
      returnValue = extractProfessionKeyFromText(name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e) {
      String msg = String.format("'%s' ProfessionKey could not be interpreted.", name);
      //LOGGER.error(msg);
      //System.out.println(name);
    }
    return returnValue;
  }

  private static ProfessionKey extractProfessionKeyFromText(String name) {
    ProfessionKey returnValue = null;
    String keyString = extractKeyTextFromTextWithUmlauts(
        name.replace("\u00AD", "-")
            .replace("(*)", "")
            .replace("*", "")
            .replace("...", "x")
            .replace("ß", "XXX") //Korrektur UpperCase

    ).replace("XXX", "ß").toLowerCase();

    keyString = keyString.trim();
    try {
      returnValue = ProfessionKey.valueOf(keyString);
    }
    catch (IllegalArgumentException e) {
      LOGGER.error("Invalid ProfessionKey: " + keyString);
    }
    return returnValue;
  }
}
