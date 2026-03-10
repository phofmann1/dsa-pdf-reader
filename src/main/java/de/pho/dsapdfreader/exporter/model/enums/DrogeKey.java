package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DrogeKey {
  aufputschmittel,
  drachentrunk,
  dreh_oel,
  feuerschlick_essenz,
  feuerschlick_pulver,
  katzenaugensalbe,
  regenbogenstaub,
  traumsand,
  beruhigungsmittel,
  boronwein,
  cheriacha,
  dornrosenoel,
  einbeerentrank,
  ilmenblatt_rauchpaeckchen,
  lotoswein,
  marbos_odem,
  mibel_absud,
  moarana_liebessaft,
  quinja_schnaps,
  rahjasine,
  samthauch,
  schleieroeffner,
  schwarzer_pfeffer,
  schwarzer_weihrauch,
  schwarzer_wein;

  @JsonValue
  public int toValue() {
    return ordinal();
  }


}
