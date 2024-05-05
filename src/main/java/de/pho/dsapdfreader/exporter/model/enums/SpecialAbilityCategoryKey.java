package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SpecialAbilityCategoryKey
{
  common,
  order,
  combat_advanced,
  cleric_advanced,
  skill_advanced,
  magic_advanced,
  combat,
  combat_stile,
  cleric,
  cleric_stile,
  magic,
  sermon,
  brawl,
  fate,
  skill_stile,
  tradition,
  traditionArtifact,
  vampire,
  familiar,
  vision,
  combat_unarmed_stile,
  werebeeing,
  magic_stile,
  magic_signs,
  sikaryan_deprivation,
  combat_stile_alien,
  pact_demonic_common,
  pact_demonic_blakharaz,
  pact_demonic_belhalhar,
  pact_demonic_charyptoroth,
  pact_demonic_lolgramoth,
  pact_demonic_thargunitoth,
  pact_demonic_amazeroth,
  pact_demonic_nagrach,
  pact_demonic_asfaloth,
  pact_demonic_tasfarelel,
  pact_demonic_mishkara,
  pact_demonic_agrimoth,
  pact_demonic_belkelel,
  pact_demonic_aphasmayra,
  pact_demonic_aphestadil,
  pact_demonic_heskatet,
  tradition_magic,
  tradition_alveran_major,
  tradition_alveran_minor,
  tradition_non_alveran, tradition_shaman;

  @JsonValue
  public int toValue() {
    return ordinal();
  }

}
