package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MysticalSkillCategory
{
  spell,
  ritual,
  trick,
  curse,
  jest,
  power,
  elfensong,
  ritualOfDominion,
  melody,
  dance,
  zibilja,
  blessing,
  liturgy,
  ceremony,
  geode,
  bansign,
  goblinRitual,
  rune,
  familiar,
  magicSign,
  sermon,
  vision,
  distortedElfensong;


  @JsonValue
  public int toValue()
  {
    return ordinal();
  }

}
