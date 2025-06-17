package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SpecieKey {
  human_koboldweltler,
  human_mittelländer,
  human_nivesen,
  human_norbarden,
  human_thorwaler,
  human_tulamiden,
  human_waldmenschen,
  human_utulus,
  mensch,
  elf,
  halfelf,
  zwerg,
  lobster,
  neristu,
  achaz,
  ork,
  goblin,
  halbork,
  holberker,
  lizard_echsensumpf,
  lizard_maraskan,
  lizard_orkland,
  lizard_regenwald,
  lizard_waldinseln,
  nachtalb;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
