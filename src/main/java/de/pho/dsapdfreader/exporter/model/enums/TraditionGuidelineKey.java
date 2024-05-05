package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TraditionGuidelineKey {
  arch_conservative,
  traditional,
  grounded,
  open,
  tolerant,
  freeminded;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
