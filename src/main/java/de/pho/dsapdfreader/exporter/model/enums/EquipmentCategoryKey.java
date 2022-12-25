package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EquipmentCategoryKey
{
  waffenzubehoer,
  kleidung,
  reisebedarf_und_werkzeuge,
  proviant,
  beleuchtung,
  nachfuellbedarf_und_zubehoer_licht,
  verbandszeug_und_heilmittel,
  behaeltnisse,
  seile_und_ketten,
  diebeswerkzeug,
  alchimistische_labore,
  handwerkszeug,
  orientierungshilfen,
  schmuck,
  edelsteine_und_feingestein,
  schreibwaren,
  buecher,
  alchimica,
  elixiere,
  musikinstrumente,
  genussmittel_und_luxus,
  tiere,
  tierbedarf,
  fortbewegungsmittel;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
