package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TraditionGuidelineKey {
  erzkonservativ,
  traditionell,
  bodenständig,
  aufgeschlossen,
  tolerant,
  freigeistig;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
