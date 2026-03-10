package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GiftKey {
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
  chimaerengift,
  axorda_katzensaeckchen,
  gardistenkeuche,
  goldleim,
  knoetergift,
  kroetenschemelgift,
  margolasch_abfuehrmittel,
  purpurner_lotosstaub,
  schwarzer_lotosstaub,
  shurinknollengift;

  @JsonValue
  public int toValue() {
    return ordinal();
  }


}
