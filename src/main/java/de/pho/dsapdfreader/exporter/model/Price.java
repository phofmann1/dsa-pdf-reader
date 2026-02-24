package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.Unit;

public class Price {
  public Double priceInSilver;
  public Unit perUnit;
  public Double minPriceInSilver;
  public Double maxPriceInSilver;
  public boolean isPricePerLevel;
}
