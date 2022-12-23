package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.Price;
import de.pho.dsapdfreader.exporter.model.enums.DsaCurrency;

public class ExtractorPrice extends Extractor
{
  public static Price retrieve(String value)
  {
    return retrieve(value, DsaCurrency.SILBER);
  }

  public static Price retrieve(String value, DsaCurrency currency)
  {
    Price returnValue = new Price();
    if (value == null || value.isEmpty() || value.equals("gratis"))
    {
      returnValue.isMinPrice = false;
      returnValue.priceInSilver = 0;
    }
    else
    {
      returnValue.isMinPrice = value.contains("+");
      returnValue.priceInSilver = Double.valueOf(value
          .replaceAll("\\+", "")
          .replaceAll("\\.", "")
          .replaceAll(",", ".")) * currency.exchangeRateSilver;
    }
    return returnValue;
  }

}
