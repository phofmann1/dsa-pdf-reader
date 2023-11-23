package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MysticalSkillModification
{
  erzwingen,
  kosten_reduzieren,
  reichweite_erhöhen,
  zauberdauer_verkürzen,
  zauberdauer_erhöhen,
  formel_weglassen,
  gesten_weglassen;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
