package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GiftTypeKey {
  alchimistisch,
  mineralisch,
  pflanzlich,
  tierisch;

  @JsonValue
  public int toValue() {
    return ordinal();
  }


}
