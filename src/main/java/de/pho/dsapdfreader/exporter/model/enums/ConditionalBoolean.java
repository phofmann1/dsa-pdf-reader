package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConditionalBoolean
{
  ja,
  nein,
  vielleicht;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
