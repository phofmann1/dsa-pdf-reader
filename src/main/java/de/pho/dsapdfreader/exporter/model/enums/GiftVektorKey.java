package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GiftVektorKey {
  atemgift,
  einnahmegift,
  kontaktgift,
  waffengift;

  @JsonValue
  public int toValue() {
    return ordinal();
  }

}
