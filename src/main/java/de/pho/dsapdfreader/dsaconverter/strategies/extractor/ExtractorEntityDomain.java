package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.EntityDomainKey;

public class ExtractorEntityDomain extends Extractor {

  public static EntityDomainKey retrieve(String name) {
    EntityDomainKey returnValue = null;
    try {
      returnValue = EntityDomainKey.valueOf(extractKeyTextFromTextWithUmlauts(name.replace("allgemein", "dämonisch")).toLowerCase());
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e) {
      // String msg = String.format("%s key could not be interpreted.", name);
      //LOGGER.error(msg);
      System.out.println(extractKeyTextFromTextWithUmlauts(name).toLowerCase() + ",");
    }
    return returnValue;
  }
}
