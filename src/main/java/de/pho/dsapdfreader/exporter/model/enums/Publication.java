package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Publication
{

  Almanach,
  Basis,
  Bote,
  Eiserne_Flammen,
  Katakomben_und_Ruinen,
  Kompendium_1,
  Kompendium_2,
  Magie_1,
  Magie_2,
  Magie_3,
  Siebenwindküste,
  Streitende_Königreiche,
  Unbekannt,
  Rüstkammer_1,
  Buendnis_der_Wacht,
  Der_weisse_See,
  Schirm,
  Namen,
  Hausregel,
  Glaube_Macht_und_Heldenmut,
  Götter_1,
  Götter_2,
  Klingen_der_Nacht,
  Bestiarium_1,
  Bestiarium_2,
  Kneipen_und_Tavernen,
  Flusslande,
  Gefangen_in_der_Gruft_der_Königin,
  Rüstkammer_2,
  Dornenreich,
  Der_dunkle_Mhanadi,
  Havena,
  Die_silberne_Wehr,
  Herbarium_1,
  Herbarium_2,
  Grimorium,
  Kodex_der_Magie,
  Kodex_des_Schwertes,
  Strassenstaub_und_Halsabschneider,
  Kodex_der_Helden,
  Rüstkammer_des_Dornenreiches,
  Rüstkammer_der_Gestade_des_Gottwals,
  Rüstkammer_der_Siebenwindküste,
  Rüstkammer_der_Sonnenküste,
  Rüstkammer_der_Flusslande,
  Rüstkammer_der_dampfenden_Dschungel,
  Rüstkammer_der_streitenden_Königreiche,
  Rüstkammer_der_Wüstenreiche,
  Kodex_des_Götterwirkens,
  ;


  @JsonValue
  public int toValue()
  {
    return ordinal();
  }

}
