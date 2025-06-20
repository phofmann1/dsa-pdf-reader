package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SkillApplicationKey
{
  analytiker,
  anführer,
  anpeitscher,
  bildhauerei,
  drachenkampf_taktik,
  drohgebärden,
  ermutigender_gesang,
  erweiterte_drachenkampf_taktik,
  falschspielen,
  faszinierender_gesang,
  iglubau,
  konditor,
  menschenstimmen_imitieren,
  rosstäuscher,
  schmerzen_unterdrücken,
  tierstimmen_imitieren,
  ungeheuer_taktik,
  unterminieren,
  viehzucht,
  scholar_der_akademie_schwert_und_stab_zu_gareth,
  scholar_der_schule_der_hellsicht_zu_thorwal,
  radscha_anhänger,
  tapferkeit_der_unsterblichen,
  einschüchternde_zurechtweisung,
  abrichter,
  ackerbau,
  abrollen,
  scholar_der_akademie_der_hohen_magie_zu_punin,
  verführerische_gestalt,
  majestätsstimme,
  gesunder_geist_gesunder_körper,
  harmonie_der_seele,
  wohlklang_der_seele,
  ermutigende_darstellung,
  fallentarnung,
  fallen_wieder_scharfmachen;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
