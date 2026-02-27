package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.DrugKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.AlchimieWirkung;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.DrogeSO;
import de.pho.dsapdfreader.uid.UidCategory;
import de.pho.dsapdfreader.uid.UidCategorySub;

public class Droge implements EquipmentI {

  public String uid;
  public DrugKey drogeKey;
  public EquipmentKey key;
  public String name;
  public String beschreibung; // Sprachinhalt
  public String besonderheiten; // Sprachinhalt
  public AlchimieWirkung wirkung; // Sprachinhalt
  public Price preis;
  public EquipmentCategoryKey equipmentCategoryKey = EquipmentCategoryKey.drogen;

  public AttributeShort widerstand;
  public List<String> vektor;
  public String stufe;
  public Duration beginn;
  public Duration dauer;


  public Droge() {
  }

  public Droge(DrogeSO dso) {
    this.uid = UidCategory.ausruestung_droge.prefix + Extractor.extractKeyTextFromText(dso.name).toLowerCase();
    this.key = Extractor.extractEnumKey(UidCategorySub.droge.prefix + dso.name, EquipmentKey.class);
    this.drogeKey = dso.key;

    this.name = dso.name;
    this.beschreibung = dso.description;
    this.wirkung = dso.wirkung;
    this.vektor = dso.vektor;

    this.stufe = dso.stufe;

    this.beginn = dso.beginn;
    this.dauer = dso.dauer;

    this.besonderheiten = dso.besonderheiten;

    if (dso.preis != null) this.preis = dso.preis;
    else if (dso.kostenIngredienzien != null) {
      this.preis = dso.kostenIngredienzien;
      this.preis.minPriceInSilver = this.preis.priceInSilver;
      this.preis.priceInSilver = null;
    }
  }

  @Override
  @JsonIgnore
  public int getKeyValue() {
    return this.key.toValue();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  @JsonIgnore
  public String getRules() {
    return this.wirkung != null
        ? (this.wirkung.regelSevere + " / " + this.wirkung.regelSevere)
        : null;
  }

  @Override
  public String getAdvantage() {
    return null;
  }

  @Override
  public String getDisadvantage() {
    return null;
  }

  @Override
  public String getRemark() {
    return this.beschreibung;
  }
}
