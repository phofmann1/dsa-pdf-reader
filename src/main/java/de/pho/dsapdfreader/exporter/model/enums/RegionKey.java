package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RegionKey {
  albernia,
  almada,
  al_anfanisches_imperium,
  andergast,
  aranien,
  bergkönigreiche_der_zwerge,
  bornland,
  garetien,
  gjalskerland,
  hoher_norden,
  horasreich,
  kalifat,
  kosch,
  maraskan,
  nordmarken,
  nostria,
  orkland,
  rommilyser_mark,
  salamandersteine,
  schattenlande,
  städte,
  svellttal,
  südmeer_und_waldinseln,
  thorwal,
  tiefer_süden,
  tobrien,
  tulamidenlande,
  weiden,
  windhag,
  zyklopeninseln,
  ;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
