package de.pho.dsapdfreader.exporter.model.enums;

public enum DsaCurrency
{
  KREUZER(0.01),
  HELLER(0.1),
  SILBER(1),
  DUKATEN(10);

  public double exchangeRateSilver;

  DsaCurrency(double exchangeRateSilver)
  {
    this.exchangeRateSilver = exchangeRateSilver;
  }
}
