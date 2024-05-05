package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GenderKey {
  divers,
  männlich,
  weiblich;


  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
