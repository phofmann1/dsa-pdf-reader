package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.EquipmentRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorEquipmentCategoryKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorEquipmentKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorPrice;
import de.pho.dsapdfreader.exporter.model.Equipment;
import de.pho.dsapdfreader.exporter.model.enums.CraftingComplexityKey;
import de.pho.dsapdfreader.exporter.model.enums.DsaCurrency;
import de.pho.dsapdfreader.exporter.model.enums.Publication;


public class LoadToEquipment
{

  private LoadToEquipment()
  {
  }

  public static Equipment migrate(EquipmentRaw er)
  {
    Equipment returnValue = new Equipment();

    returnValue.name = er.name;
    returnValue.key = ExtractorEquipmentKey.retrieve(er.name.toLowerCase());
    returnValue.structure = (er.structure != null && !er.structure.isEmpty()) ? Integer.valueOf(er.structure.replace(".", "")) : 0;
    returnValue.color = er.color;
    returnValue.categoryKey = ExtractorEquipmentCategoryKey.retrieve(er.category);
    if (er.weight != null && !er.weight.isEmpty()) {
      returnValue.weight = Double.valueOf(er.weight.replace(".", "").replace(",", "."));
    }
    returnValue.publication = Publication.valueOf(er.publication);
    returnValue.price = ExtractorPrice.retrieve(er.price, DsaCurrency.SILBER, er.isPricePerLevel);

    returnValue.remark = er.remark;

    returnValue.craftingComplexity = CraftingComplexityKey.parse(er.craft);
    if (er.craft.startsWith("komp"))
    {
      returnValue.craftingAp = Integer.valueOf(er.craft.substring(er.craft.indexOf("(") + 1, er.craft.indexOf("AP")).trim());
    }
    return returnValue;
  }

}
