package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SelectionCategory
{
  artifact,
  combatSkill,
  mysticalSkill,
  desease,
  poison,
  skill,
  element,
  specie,
  feature,
  specialisation,
  tradeSecret,
  language,
  script,
  terrain,
  traditionCleric;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
