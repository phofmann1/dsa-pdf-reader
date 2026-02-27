package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PoisonKey {
    stabschlangengift,
    arax,
    kelmon,
    tulmadron,
    wurara,
    angstgift,
    betäubungsgift,
    drachenspeichel,
    halbgift,
    purpurblitz,
    marbos_ruhe,
    urkaritgift,
    visarnetgift,
    boabungaha,
    hollbeerenbrechmittel,
    höhlenbovistgift,
    kukris,
    mandragora,
    margolaschabführmittel,
  rattenpilzgift,
  schattennebelgift,
  schwarzer_lotos,
  schwarzkorn,
  betaeubungsgift,
  freundfeind,
  lichtnebler_beutel,
  lotosoel,
  merach_extrakt,
  schlafgift,
  zazamotoxin,
  stabiles_zazamotoxin,
  chimaerengift;

  @JsonValue
  public int toValue() {
    return ordinal();
  }


}
