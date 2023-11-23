package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CultureKey
{
  andergaster,
  aranier,
  bornlaender,
  ferkinas,
  fjarninger,
  gjalsker,
  horasier,
  koboldweltler,
  maraskaner,
  mhanadistani,
  mittelreicher,
  mohas,
  nivesen,
  norbarden,
  nordaventurier,
  nostrier,
  novadis,
  suedaventurier,
  svellttaler,
  thorwaler,
  trollzacker,
  zahori,
  zyklopaeer,
  auelfen,
  firnelfen,
  steppenelfen,
  waldelfen,
  ambosszwerge,
  brillantzwerge,
  erzzwerge,
  huegelzwerge,
  wildzwerge,
  orkland;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
