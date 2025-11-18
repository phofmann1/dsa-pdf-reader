package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LandschaftsKey {
  blubb
  ;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
