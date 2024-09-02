package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.ArmorRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorArmorKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorPrice;
import de.pho.dsapdfreader.exporter.model.Armor;
import de.pho.dsapdfreader.exporter.model.enums.ArmorCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.CraftingComplexityKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;


public class LoadToArmor
{

  private LoadToArmor()
  {
  }

  public static Armor migrate(ArmorRaw ar)
  {
    Armor returnValue = new Armor();

    returnValue.name = ar.name.replace("\u00AD", "-");

    returnValue.key = ExtractorArmorKey.retrieve(ar.name);
    if (ar.armor == null || ar.armor.isEmpty()) {
      System.out.println(ar.name);
    }
    returnValue.rs = Integer.parseInt(ar.armor);
    returnValue.be = Integer.parseInt(ar.encumberance);
    returnValue.additionalEncumbered = ar.additionalEncumberance;
    returnValue.weight = Double.parseDouble(ar.weight.replace(".", "").replace(",", "."));
    returnValue.publication = Publication.valueOf(ar.publication);
    returnValue.price = ExtractorPrice.retrieve(ar.price);
    returnValue.equipmentCategoryKey = EquipmentCategoryKey.rüstung;
    returnValue.advantage = ar.advantage;
    returnValue.disadvantage = ar.disadvantage;
    returnValue.remark = ar.remark;

    returnValue.armorCategoryKey = switch (returnValue.rs)
        {
          case 8 -> ArmorCategoryKey.HEAVY_TOURNAMENT_ARMOR;
          case 7 -> ArmorCategoryKey.HEAVY_PLATE;
          case 6 -> ArmorCategoryKey.PLATE;
          case 5 -> ArmorCategoryKey.SCALE;
          case 4 -> (returnValue.name.equals("Hartholzharnisch") || returnValue.name.equals("Mammutonpanzer"))
              ? ArmorCategoryKey.WOOD
              : ArmorCategoryKey.CHAIN;
          case 3 -> ArmorCategoryKey.LEATHER;
          case 2 -> ArmorCategoryKey.CLOTH;
          case 1 -> ArmorCategoryKey.HEAVY_CLOTHING;
          default -> ArmorCategoryKey.CLOTHING;
        };

    returnValue.breakingValue = switch (returnValue.armorCategoryKey)
        {
          case HEAVY_TOURNAMENT_ARMOR, HEAVY_PLATE -> 10;
          case PLATE -> 11;
          case SCALE -> 12;
          case CHAIN -> 13;
          case WOOD -> 9;
          case LEATHER -> 8;
          case CLOTH -> 6;
          case HEAVY_CLOTHING -> 5;
          case CLOTHING -> 4;
          case NONE -> -1;
        };


    returnValue.craftingComplexity = CraftingComplexityKey.parse(ar.craft);
    if (ar.craft.startsWith("komp"))
    {
      returnValue.craftingAp = Integer.parseInt(ar.craft.substring(ar.craft.indexOf("(") + 1, ar.craft.indexOf("AP")).trim());
    }
    return returnValue;
  }

}
