package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TerrainTypeKey
{
  dschungel,
  eis_und_schnee,
  gebirge,
  höhle,
  kulturland,
  meer,
  steppe,
  sumpf,
  wald,
  wüste;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
