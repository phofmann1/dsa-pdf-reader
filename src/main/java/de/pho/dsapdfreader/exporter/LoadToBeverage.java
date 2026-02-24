package de.pho.dsapdfreader.exporter;

import java.util.Map;

import de.pho.dsapdfreader.dsaconverter.model.BeverageRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorEquipmentKey;
import de.pho.dsapdfreader.exporter.model.Equipment;
import de.pho.dsapdfreader.exporter.model.Price;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.Unit;


public class LoadToBeverage {
  static final Map<String, EquipmentCategoryKey> CUSTOM_BEVERAGE_EC_MAP = Map.of(
      "Weinbrand", EquipmentCategoryKey.weine,
      "Beerenwein", EquipmentCategoryKey.weine,
      "Bitterwein", EquipmentCategoryKey.weine,
      "Met", EquipmentCategoryKey.weine,
      "Kornbrand", EquipmentCategoryKey.spirituosen,
      "Dünnbier", EquipmentCategoryKey.biere,
      "Starkbier", EquipmentCategoryKey.biere,
      "Schnaps", EquipmentCategoryKey.spirituosen,
      "Landwein", EquipmentCategoryKey.weine
  );

  private LoadToBeverage() {
  }

  public static Equipment migrate(BeverageRaw er) {
    Equipment returnValue = new Equipment();
    returnValue.name = er.name.replaceAll("(?<=[a-zäöüß])(?=[A-ZÄÖÜ])", " ");
    returnValue.equipmentCategoryKey = extractCategoryType(er.type, er.name);

    returnValue.key = ExtractorEquipmentKey.retrieve(er.name.toLowerCase(), returnValue.equipmentCategoryKey);
    returnValue.publication = Publication.valueOf(er.publication);
    Price p = new Price();
    if (er.pricePerUnit != null && !er.pricePerUnit.isEmpty()) {

      String txt = er.pricePerUnit.replace(",", ".").replaceAll("[\\sA-ü\\(\\)]*", "");
      p.priceInSilver = Double.parseDouble(txt) / 10;
      Unit[] units = Extractor.extractUnitsFromText(er.unit, "LoatToBeverage", false);
      if (units.length > 0) p.perUnit = units[0];

    }
    returnValue.price = p;

    returnValue.remark = er.description;
    returnValue.rules = er.effect;

    return returnValue;
  }

  private static EquipmentCategoryKey extractCategoryType(String type, String name) {
    return switch (type) {
      case "Biere" -> EquipmentCategoryKey.biere;
      case "Weine" -> EquipmentCategoryKey.weine;
      case "Spirituosen" -> EquipmentCategoryKey.spirituosen;
      default -> CUSTOM_BEVERAGE_EC_MAP.get(name);
    };
  }

}
