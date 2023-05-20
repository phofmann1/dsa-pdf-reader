package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SelectionCategory
{
  ARTIFACT,
  COMBATSKILL,
  MYSTICALSKILL,
  DESEASE,
  POISON,
  SKILL,
  ELEMENT,
  SPECIE,
  FEATURE,
  SPECIALISATION,
  TRADE_SECRET,
  LANGUAGE,
  SCRIPT;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
