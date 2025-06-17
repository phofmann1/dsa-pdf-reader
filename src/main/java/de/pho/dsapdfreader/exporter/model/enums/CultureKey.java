package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CultureKey {
  andergaster,
  aranien,
  bornländer,
  ferkinas,
  fjarninger,
  gjalsker,
  horasier,
  koboldweltler,
  maraskaner,
  mhanadistan,
  mittelreich,
  waldmenschen,
  nivesen,
  norbarden,
  nordaventurier,
  nostrier,
  novadi,
  südaventurier,
  svellttal,
  thorwal,
  trollzacker,
  zahori,
  zyklopeninseln,
  auelfen,
  firnelfen,
  steppenelfen,
  waldelfen,
  ambosszwerge,
  brillantzwerge,
  erzzwerge,
  hügelzwerge,
  wildzwerge,

  engasal,
  tocamuyac,
  utulu,
  wüstenelfen,
  achaz_rha,
  ctki_ssrr,
  stammesachaz,
  stammesgoblins,
  stammesorks,
  holberk,
  räuberbande,
  tulamidenlande,
  nai_ashyrr,
  nuanaä_lie;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
