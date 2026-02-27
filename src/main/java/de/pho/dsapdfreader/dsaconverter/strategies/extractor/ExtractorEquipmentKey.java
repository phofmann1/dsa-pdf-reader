package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.uid.UidCategorySub;

public class ExtractorEquipmentKey extends Extractor {

  private static final String[] PREFIX = {
      UidCategorySub.elixier.externalId(),
      UidCategorySub.kraut.externalId(),
      UidCategorySub.alkohol.externalId(),
      UidCategorySub.zutatalchimie.externalId(),
      UidCategorySub.material.externalId(),
      UidCategorySub.gift.externalId(),
  };

  public static EquipmentKey retrieve(String name, EquipmentCategoryKey eck) {
    String prefix = _extractPrefixByCategory(eck);

    return _generateKey(name, prefix, false);

  }

  public static EquipmentKey retrieveCategoryAsFallback(String name, EquipmentCategoryKey eck) {
    String prefix = _extractPrefixByCategory(eck);
    return _generateKey(name, prefix, true);
  }


  private static EquipmentKey _generateKey(String name, String prefix, boolean isPrefixFallback) {
    EquipmentKey returnValue = null;
    String suffix = "";
    String cleanName = name
        .replace("Dolchscheid", "Dolchscheide")
        .replace("Rattenpilze", "Rattenpilz")
        .replaceAll("^Jade", "grüne Jade")
        .replaceAll("^Nitriol$", "Rauchendes Braunöl (Nitriol)")
        .replaceAll("Rauchendes Braunöl$", "Rauchendes Braunöl (Nitriol)")
        .replace("Blut eines Ochsen", "Ochsenblut")
        .replace("Ochsenblut Olginwurz", "Ochsenblut, Olginwurz")
        .replace("echtes Premer Feuer", "Premer Feuer")
        .replaceAll("Alraunen(?=\\s|$)", "Alraune")
        .replace("fernhaltensollen", "fernhalten sollen")
        .replace("verschiedene", "").trim();
    try {
      if (isPrefixFallback) {
        // bei dieser Option, wird das Prefix am Schluss probiert, um erst alle anderen Varianten zu testen. Am Ende geht es hauptsächlich um die Ausgabe
        // des präferierten Keys
        returnValue = _extractEquipmentKeyFromText(cleanName);
      }
      else {
        returnValue = _extractEquipmentKeyFromText(prefix + cleanName);
      }
      if (returnValue == null) {
        for (String p : PREFIX) {
          suffix = p.equals(UidCategorySub.kraut.prefix) ? "_roh" : "";
          returnValue = _extractEquipmentKeyFromText(p + cleanName + suffix);
          if (returnValue != null) break;
        }
      }
      if (returnValue == null && isPrefixFallback) {
        returnValue = _extractEquipmentKeyFromText(prefix + cleanName);
      }

      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e) {
      System.out.println(extractKeyTextFromText(prefix + cleanName).toLowerCase() + ", ");
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
      case kräuter -> UidCategorySub.kraut.prefix;
      case gifte -> UidCategorySub.gift.prefix;
      case biere, weine, spirituosen -> UidCategorySub.alkohol.prefix;
      case elixiere -> UidCategorySub.elixier.prefix;
      case alchimistische_zutaten -> UidCategorySub.zutatalchimie.prefix;
      case pflanzliche_hilfsmittel -> UidCategorySub.hilfsmittel.externalId();
      case unedle_metalle, halbedle_metalle, edle_metalle, magische_metalle, sternenmetalle, unmetalle, astralspeicher_steine, gestein,
          holz, stoff, koerperteile, kuenstliche_materialien -> UidCategorySub.material.prefix;
      case waffenzubehoer, kleidung, reisebedarf_und_werkzeuge, proviant, beleuchtung, nachfuellbedarf_und_zubehoer_licht, verbandszeug_und_heilmittel,
          schreibwaren, buecher, alchimica, musikinstrumente, genussmittel_und_luxus, tiere, tierbedarf, fortbewegungsmittel, nahkampfwaffe, behaeltnisse,
          seile_und_ketten, diebeswerkzeug, alchimistische_labore, handwerkszeug, orientierungshilfen, schmuck, fernkampfwaffe, rüstung, werkzeug,
          besondere_gegenstaende, kunsthandwerk, taetowierung, hilfsmittel, zeremonialgegenstaende, modifikation_rüstung, modifikation_munition, modifikation_waffe,
          drogen, edelsteine_und_feingestein -> "";
    };
  }

}
