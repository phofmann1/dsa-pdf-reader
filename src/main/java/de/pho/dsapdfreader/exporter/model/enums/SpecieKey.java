package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SpecieKey
{
  HUMAN_KOBOLDWELTLER,
  HUMAN_MITTELLAENDER,
  HUMAN_NIVESEN,
  HUMAN_NORBARDEN,
  HUMAN_THORWALER,
  HUMAN_TULAMIDEN,
  HUMAN_WALDMENSCHEN,
  HUMAN_UTULUS,
  HUMAN,
  ELF,
  HALFELF,
  DWARF,
  LOBSTER,
  NERISTU,
  LIZARD,
  ORC;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
