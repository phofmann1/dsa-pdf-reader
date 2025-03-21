package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ValueChangeKey {
  skill,
  energy_asp,
  energy_kap,
  att_mu,
  att_kl,
  att_in,
  att_ch,
  att_ff,
  att_ge,
  att_ko,
  att_kk,
  att_sk,
  att_zk,
  skill_convince,
  energy_lep,
  combatSkill,
  money,
  skill_orientation,
  att_gs,
  cv_aw,
  cv_ini,
  new_ms_cat,
  REDUNDANT,
  cv_ws,
  energy_schip,
  sozialer_stand,
  mystical_skill_count_4_cat,
  cv_rs,
  trad_ap, // Hexen reduzieren bei den Nachteilen (keine Flugsalbe / kein Vertrauter) die Traditionskosten um 10
  cv_tp,
  ms_familiarization;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
