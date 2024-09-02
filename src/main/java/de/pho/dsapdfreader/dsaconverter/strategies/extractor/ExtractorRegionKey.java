package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.RegionKey;

public class ExtractorRegionKey extends Extractor {

  public static RegionKey retrieveOptional(String text) {
    return extractRegionKeyFromText(text);
  }

  public static RegionKey retrieve(String text) {
    RegionKey returnValue = null;
    try {
      returnValue = retrieveOptional(text);
      if (returnValue == null) {
        throw new IllegalArgumentException();
      }
    }
    catch (IllegalArgumentException e) {
      String msg = String.format("Region '%s' key could not be interpreted.", text);
      LOGGER.error(msg);
    }
    return returnValue;
  }

  private static RegionKey extractRegionKeyFromText(String name) {
    RegionKey returnValue = null;
    String keyString = extractKeyTextFromTextWithUmlauts(
        name.replace("\u00AD", "-")
            .replace("(*)", "")
            .replace("*", "")
            .replace("ʼ", "_")
            .replace("...", "x")
            .replace("ß", "XXX") //Korrektur UpperCase
    ).replace("XXX", "ß").toLowerCase();


    keyString = keyString.trim();
    try {
      returnValue = RegionKey.valueOf(keyString);
    }
    catch (IllegalArgumentException e) {
    }
    return returnValue;
  }
}
