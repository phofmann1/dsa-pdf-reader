package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;

public class ExtractorEquipmentKey extends Extractor {
  public static final String EC_KRAUT = "kraut_";
  public static final String EC_ALKOHOL = "alkohol_";
  public static final String EC_ELIXIER = "elixier_";
  public static final String EC_ZUTAT_ALCHIMIE = "zutat_alchimie_";

  private static final String[] PREFIX = {
          EC_ELIXIER, EC_ALKOHOL, EC_KRAUT, EC_ZUTAT_ALCHIMIE
  };

  public static EquipmentKey retrieve(String name, EquipmentCategoryKey eck) {
    String prefix = _extractPrefixByCategory(eck);

    return _generateKey(name, prefix);

  }


  private static EquipmentKey _generateKey(String name, String prefix) {
    EquipmentKey returnValue = null;
    try {
      returnValue = _extractEquipmentKeyFromText(prefix + name);
      if(returnValue == null) {
        for(String p : PREFIX) {
          returnValue = _extractEquipmentKeyFromText(p + name);
          if(returnValue != null) break;
        }
      }
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e) {
      System.out.println(extractKeyTextFromText(prefix + name).toLowerCase() + ", ");
      String msg = String.format("%s equipment key could not be interpreted.", name);
      //LOGGER.error(msg);
    }
    return returnValue;
  }

  private static EquipmentKey _extractEquipmentKeyFromText(String name) {
    EquipmentKey returnValue;
    String keyString = extractKeyTextFromText(name);
    keyString = keyString.trim().toLowerCase();

    try {
      returnValue = EquipmentKey.valueOf(keyString);
    }
    catch (IllegalArgumentException e) {
      //System.out.println(keyString + ", ");
      //LOGGER.error("Invalid EquipmentKey: " + keyString);
      returnValue = null;
    }
    return returnValue;
  }


  private static String _extractPrefixByCategory(EquipmentCategoryKey eck) {
    if (eck == null) return "";
    return switch (eck) {
      case kräuter -> EC_KRAUT;
      case biere, weine, spirituosen -> EC_ALKOHOL;
      case elixiere -> EC_ELIXIER;
      case alchimistische_zutaten -> EC_ZUTAT_ALCHIMIE;
      case waffenzubehoer, kleidung, reisebedarf_und_werkzeuge, proviant, beleuchtung, nachfuellbedarf_und_zubehoer_licht, verbandszeug_und_heilmittel,
          behaeltnisse, seile_und_ketten, diebeswerkzeug, alchimistische_labore, handwerkszeug, orientierungshilfen, schmuck, edelsteine_und_feingestein,
          schreibwaren, buecher, alchimica, musikinstrumente, genussmittel_und_luxus, tiere, tierbedarf, fortbewegungsmittel, nahkampfwaffe,
          fernkampfwaffe, rüstung, werkzeug, besondere_gegenstaende, kunsthandwerk, taetowierung, hilfsmittel, zeremonialgegenstaende, modifikation_rüstung,
          modifikation_munition, modifikation_waffe, gifte, drogen -> "";
    };
  }

}
