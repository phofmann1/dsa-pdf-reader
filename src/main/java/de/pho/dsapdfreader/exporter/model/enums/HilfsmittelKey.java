package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HilfsmittelKey {
  atanax,
  atmonbrei,
  blasrohr,
  blauschirmsporen,
  blutblattkranz,
  brandsalbe,
  dergolasch_pulver,
  dornrosengerte,
  egelschreckpaste,
  feengesteck,
  fiebersaft,
  finagebast,
  gruener_pilzschleim,
  gruenschleim_tropfen,
  hesindigo,
  hirad_antidot,
  horuschenoel,
  jacopo_duftbeutel,
  jacopo_suessigkeit,
  kairantrunk,
  kraehenfuesse,
  liebeskuechlein,
  lulanientrunk,
  matte_der_ruhestoerung,
  menchal_ruestung,
  messergras_skalpell,
  narbenpulver,
  orazalkleber,
  praiosmilch,
  sansaro_essenz,
  sansaro_paste,
  scherzschwamm,
  schlaengelbeutel,
  schwammzunder,
  schwarzaeugiges_amulett,
  stinkmirbelsaeckchen,
  talaschin_tinktur,
  tarntinktur,
  waldwebennetz,
  wandermoos_essenz,
  weissdorn_mazerat,
  wirseltrank;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
