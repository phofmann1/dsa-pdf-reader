package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AdvancementCategory
{
  NONE, A, B, C, D, E;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
