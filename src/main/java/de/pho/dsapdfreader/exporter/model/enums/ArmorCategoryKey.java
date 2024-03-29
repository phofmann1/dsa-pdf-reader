package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ArmorCategoryKey
{
  PLATE,
  SCALE,
  CHAIN,
  WOOD,
  LEATHER,
  CLOTH,
  CLOTHING,
  HEAVY_CLOTHING, HEAVY_PLATE, HEAVY_TOURNAMENT_ARMOR, NONE;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }

}
