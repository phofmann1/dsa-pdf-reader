package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.CultureKey;

public class ExtractorCultureKey extends Extractor {
  public static CultureKey retrieve(String name) {
    CultureKey returnValue = null;
    try {
      returnValue = extractCultureKeyFromText(name);
      if (returnValue == null) {
        throw new IllegalArgumentException();
      }
    }
    catch (IllegalArgumentException e) {
      String msg = String.format("Culture '%s' key could not be interpreted.", name);
      //LOGGER.error(msg);
    }
    return returnValue;
  }

  private static CultureKey extractCultureKeyFromText(String name) {
    CultureKey returnValue = null;
    String keyString = extractKeyTextFromTextWithUmlauts(
        name.replace("\u00AD", "-")
            .replace("(*)", "")
            .replace("*", "")
            .replace("...", "x")
            .replace("ß", "XXX") //Korrektur UpperCase
            .replace("Utulus", "Utulu")
            .replace("Novadis", "Novadi")
            .replace("Thorwaler", "Thorwal")
            .replace("Aranier", "Aranien")
            .replace("Mhanadistani", "Mhanadistan")

    ).replace("XXX", "ß").toLowerCase();

    keyString = keyString.trim();
    returnValue = CultureKey.valueOf(keyString);
    return returnValue;
  }
}
