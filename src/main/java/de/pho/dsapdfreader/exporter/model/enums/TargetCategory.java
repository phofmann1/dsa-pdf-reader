package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TargetCategory
{
    ALL,
    ANIMATED,
    DEMON,
  ELEMENTALS,
  HOLYOBJECT,
  CULTURAL,
  CREATURE,
  OBJECT,
  PROFANEOBJECT,
  ANIMAL,
  SUPERNATURAL,
  BEING,
  ZONE,
  PLANT,
  FAIRY,
  UNDEAD,
  GHOST,
  POSESSED,
  TRICK,
  LIVING_BEING,
  LITURGY,
  CEREMONY;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
