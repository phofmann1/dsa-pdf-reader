package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SkillCategoryKey
{
  körpertalente,
  gesellschaftstalente,
  naturtalente,
  wissenstalente,
  handwerkstalente;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }

}
