package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;

public class ExtractorEquipmentKey extends Extractor {
  public static EquipmentKey retrieve(String name, EquipmentCategoryKey eck) {
    String prefix = _extractPrefixByCategory(eck);

    return _generateKey(name, prefix);

  }


  public static EquipmentKey extractEquipmentKeyFromText(String name) {
    EquipmentKey returnValue;
    String keyString = extractKeyTextFromText(name);
    keyString = keyString.trim().toLowerCase();

    try {
      returnValue = EquipmentKey.valueOf(keyString);
    }
    catch (IllegalArgumentException e) {
      System.out.println(keyString + ", ");
      //LOGGER.error("Invalid EquipmentKey: " + keyString);
      returnValue = null;
    }
    return returnValue;
  }


  private static EquipmentKey _generateKey(String name, String prefix) {
    EquipmentKey returnValue = null;
    try {
      returnValue = extractEquipmentKeyFromText(prefix + name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e) {
      String msg = String.format("%s equipment key could not be interpreted.", name);
      //LOGGER.error(msg);
    }
    return returnValue;
  }

  private static String _extractPrefixByCategory(EquipmentCategoryKey eck) {
    if (eck == null) return "";
    return switch (eck) {
      case biere, weine, spirituosen -> "alkohol_";
      case elixiere -> "elixier_";
      case waffenzubehoer, kleidung, reisebedarf_und_werkzeuge, proviant, beleuchtung, nachfuellbedarf_und_zubehoer_licht, verbandszeug_und_heilmittel,
          behaeltnisse, seile_und_ketten, diebeswerkzeug, alchimistische_labore, handwerkszeug, orientierungshilfen, schmuck, edelsteine_und_feingestein,
          schreibwaren, buecher, alchimica, musikinstrumente, genussmittel_und_luxus, tiere, tierbedarf, fortbewegungsmittel, nahkampfwaffe,
          fernkampfwaffe, rüstung, werkzeug, besondere_gegenstaende, kunsthandwerk, taetowierung, hilfsmittel, zeremonialgegenstaende, modifikation_rüstung,
          modifikation_munition, modifikation_waffe, kräuter, alchimistische_zutaten, gifte, drogen -> "";
    };
  }

}
