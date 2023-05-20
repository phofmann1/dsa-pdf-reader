package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SpecialAbilityTypeKey
{
  basic,
  passive,
  special, active;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }

}
