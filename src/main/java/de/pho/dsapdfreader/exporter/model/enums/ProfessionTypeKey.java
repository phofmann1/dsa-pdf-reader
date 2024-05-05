package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProfessionTypeKey {
  normal,
  clerical_alveran,
  magical,
  clerical_halbgötter,
  clerical_außeralveranisch,
  chapter;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
