package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SkillApplicationKey
{
  analytiker,
  anführer,
  anpeitscher,
  drachenkampf_taktik,
  drohgebärden,
  erweiterte_drachenkampf_taktik,
  falschspiel,
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
  tapferkeit_der_unsterblichen;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
