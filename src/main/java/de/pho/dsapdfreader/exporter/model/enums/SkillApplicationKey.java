package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SkillApplicationKey
{
  schmerzen_unterdrücken,
  anführer,
  tierstimmen_immitieren,
  falschspiel,
  rosstäuscher;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
