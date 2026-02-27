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
