package de.pho.dsapdfreader.uid;

public enum UidCategorySub {
  //-----------------------------------
  //SUBCATEGORIES
  //-----------------------------------
  //EQUIPMENT - Waffen und Rüstung
  waffe("waffe"),
  ruestung("ruestung"),

  //EQUIPMENT - Alchimica
  gift("gift"),
  elixier("elixier"),
  material("material"),
  hilfsmittel("pflanzliches_hilfsmittel"),
  zutatalchimie("zutat_alchimie"),
  droge("droge"),
  alkohol("alkohol"),
  kraut("kraut"),

  //TALENTE
  anwendungsgebiet("anwendung"),
  einsatzmoeglichkeit("einsatz"),

  //MAGIE
  trick("trick"),
  zauber("zauber"),
  ritual("ritual"),
  handlung("handlung"),
  variante("variante"),

  //weihe
  segnung("segnung"),
  liturgie("liturgie"),
  zeremonie("zeremonie"),
  predigt("predigt"),
  vision("vision"),

  //profile
  kulturschaffend("lebewesen_kulturschaffend"),
  tier("lebewesen_tier"),
  pflanzen ("lebewesen_pflanzen"),
  fee("lebewesen_uebernatuerliche_fee"),
  chimaere("lebewesen_uebernatuerliche_chimaere"),
  drache ("lebewesen_uebernatuerliche_drache"),
  daimonide ("lebewesen_uebernatuerliche_daimonid"),
  geist("nicht_lebende_untot_geist"),
  hirnlose("nicht_lebende_untot_hirnlos"),
  vampir("nicht_lebende_untot_vampire"),
  nicht_lebende_untot_beseelte("nicht_lebende_untot_beseelte"),
  daemon("nicht_lebende_dämon"),
  elementar("nicht_lebende_elementar"),
  unelementar("nicht_lebende_unelementar"),
  golem("nicht_lebende_golem"),


  ;

  public final String prefix;
  private final String id;

  UidCategorySub(String id) {
    this.id = id;
    this.prefix = id + "_";
  }

  public String externalId() {
    return this.id;
  }
}
