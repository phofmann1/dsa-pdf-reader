package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.ArmorRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorArmorKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorPrice;
import de.pho.dsapdfreader.exporter.model.Armor;
import de.pho.dsapdfreader.exporter.model.enums.Publication;


public class LoadToArmor
{

  private LoadToArmor()
  {
  }

  public static Armor migrate(ArmorRaw ar)
  {
    Armor returnValue = new Armor();

    returnValue.name = ar.name;
    returnValue.key = ExtractorArmorKey.retrieve(ar.name);
    returnValue.rs = Integer.valueOf(ar.armor);
    returnValue.be = Integer.valueOf(ar.encumberance);
    returnValue.additionalEncumbered = ar.additionalEncumberance;
    returnValue.weight = Double.valueOf(ar.weight.replace(".", "").replace(",", "."));
    returnValue.publication = Publication.valueOf(ar.publication.toUpperCase());
    returnValue.price = ExtractorPrice.retrieve(ar.price);
    return returnValue;
  }

}
