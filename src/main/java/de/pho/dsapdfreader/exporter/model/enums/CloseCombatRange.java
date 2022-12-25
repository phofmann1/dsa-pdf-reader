package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CloseCombatRange
{
  KURZ,
  MITTEL,
  LANG,
  ÜBERLANG;


  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
