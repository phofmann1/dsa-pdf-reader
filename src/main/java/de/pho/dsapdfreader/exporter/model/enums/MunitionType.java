package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MunitionType
{
  BLASROHRPFEIL,
  BOLZEN,
  KUGEL,
  PFEIL,
  STEINCHEN,
  KEINE;


  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
