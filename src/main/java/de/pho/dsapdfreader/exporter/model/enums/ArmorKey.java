package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ArmorKey
{
  iryanruestung,
  kettenhemd,
  kroetenhaut,
  lederharnisch,
  leichte_platte,
  schuppenpanzer,
  spiegelpanzer,
  tuchruestung,
  amazonen_ruestung,
  bronzeharnisch,
  eisenmantel,
  fellruestung,
  fuenflagenharnisch,
  gambeson,
  garether_platte,
  gestechruestung,
  gladiatoren_ruestung,
  hartholz_harnisch,
  hornruestung,
  ifirnsmantel,
  kettenweste,
  kuerass,
  kurbul,
  lamellar,
  langes_kettenhemd,
  lindwurmhaut,
  uebler_geruch,
  reiterharnisch,
  schuppen_gewand,
  schwere_platte,
  skorpionhemd,
  turnierruestung,
  wattierter_waffenrock,
  normale_kleidung_felle_oder_nackt,
  schwere_kleidung_winterkleidung,
  stoffruestung_gambeson,
  lederruestung,
  kettenruestung,
  schuppenruestung,
  plattenruestung;


  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
