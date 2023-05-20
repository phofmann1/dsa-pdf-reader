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
  ;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }

}
