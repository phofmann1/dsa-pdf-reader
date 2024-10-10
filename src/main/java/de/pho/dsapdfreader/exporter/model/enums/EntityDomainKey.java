package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EntityDomainKey {
  dämonisch,
  blakharaz,
  belhalhar,
  charyptoroth,
  lolgramoth,
  thargunitoth,
  amazeroth,
  nagrach,
  asfaloth,
  tasfarelel,
  mishkara,
  agrimoth,
  belkelel,
  aphasmayra,
  aphestadil,
  heskatet,
  elementar,
  feuer,
  wasser,
  erz,
  luft,
  humus,
  eis,
  feen,
  wald,
  licht,
  finternis,
  wasser_feen,
  berg;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
