package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DsaState {
  baumartig,
  bewegungsunfähig,
  bewusstlos,
  blind,
  blutend,
  blutrausch,
  brennend,
  eingeengt,
  feylamia,
  fixiert,
  handlungsunfähig,
  hörigkeit,
  kind_der_finsternis,
  kind_der_nacht,
  krank,
  lamijah,
  liegend,
  lykanthrop,
  minderer_feylamia,
  minderer_vampir,
  pechmagnet,
  raserei,
  stumm,
  taub,
  unsichtbar,
  vergiftet,
  versteinert,
  wergestalt,
  überrascht,
  übler_geruch;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
