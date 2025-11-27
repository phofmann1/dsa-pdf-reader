package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.Unit;

public class Price {
  public double priceInSilver;
  public Unit perUnit;
  public double minPriceInSilver;
  public double maxPriceInSilver;
  public boolean isPricePerLevel;
}
