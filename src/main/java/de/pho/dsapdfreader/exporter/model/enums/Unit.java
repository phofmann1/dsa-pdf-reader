package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Unit {
  sofort,
  aufrechterhaltend,
  permanent,
  einmalig,
  aktion,
  sekunde,
  runde,
  minute,
  stunde,
  tag,
  woche,
  monat,
  jahr,
  jahrhundert,
  selbst,
  beruehrung,
  meter,
  sicht,
  meile,
  kontinent,
  welt,
  zone,
  ruestungsschutz,
  ziel,
  gift_stufe,
  groesse,
  zielattribut_kl,
  krankheit_stufe,
  person,
  kubikmeter,
  waffengroesse,
  asp_ziel,
  zonen_effekt,
  lep,
  doppelgaenger,
  kampfrunde,
  kilogramm,
  quadratmeter,
  liter,
  stueck,
  becherchen,
  becher,
  kelch,
  humpen,
  krug;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
