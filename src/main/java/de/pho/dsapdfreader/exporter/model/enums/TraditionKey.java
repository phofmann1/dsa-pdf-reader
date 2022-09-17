package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TraditionKey
{
    ALL,
    ANGROSCH,
    ANIMIST,
    AVES,
    BARDE,
    BORON,
    DRUID,
    EFFERD,
    ELF,
    FIRUN,
    GEODE,
    MAGE,
    GOBLIN,
    HESINDE,
    WITCH,
    IFIRN,
    INGERIMM,
    INTUITIVE,
    KOR,
    CRISTALLOMANCER,
    LEVTHAN,
    MARBO,
    GIFTED,
    NAMENLOS,
    NANDUS,
    PERAINE,
    PHEX,
    PRAIOS,
    QABALYA,
    RAHJA,
    RONDRA,
    SHAMAN_ALL,
    ILLUSIONIST,
    HARLEQUIN,
    SWAFNIR,
    TRAVIA,
  TSA,
  ALCHIMIST,
  DANCER,
  ZIBILJA,
  BROBIM_GEODE,
  SHAMAN_FERKINA,
  SHAMAN_FJARNINGER,
  SHAMAN_GJALSKER,
  SHAMAN_MOHA,
  SHAMAN_NIVESE,
  SHAMAN_TROLLZACKER,
  BORBARAD;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
