package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.Price;
import de.pho.dsapdfreader.exporter.model.enums.DsaCurrency;

public class ExtractorPrice extends Extractor
{
  public static Price retrieve(String value)
  {
    return retrieve(value, DsaCurrency.SILBER, false);
  }

  public static Price retrieve(String value, DsaCurrency currency, boolean isPerLevel)
  {
    Price returnValue = new Price();
    if (value == null || value.isEmpty() || value.equals("gratis"))
    {
      returnValue.priceInSilver = 0d;
      returnValue.minPriceInSilver = 0d;
      returnValue.maxPriceInSilver = 0d;
    }
    else
    {
      returnValue.isPricePerLevel = isPerLevel;
      if (value.contains("bis") || value.contains("+"))
      {
        String[] values = value.split("bis");
        returnValue.minPriceInSilver = convert2PriceDouble(values[0], currency.exchangeRateSilver);
        if (values.length == 2)
        {
          returnValue.maxPriceInSilver = convert2PriceDouble(values[1], currency.exchangeRateSilver);
        }
      }
      else
      {
        returnValue.priceInSilver = convert2PriceDouble(value, currency.exchangeRateSilver);
      }
    }
    return returnValue;
  }

  private static double convert2PriceDouble(String value, double exchangeRateSilver)
  {
    return Double.valueOf(value
        .replaceAll("\\+", "")
        .replaceAll("\\.", "")
        .replaceAll(",", ".")) * exchangeRateSilver;
  }

}
