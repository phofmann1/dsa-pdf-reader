package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.DrogeKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.enums.GiftTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.GiftVektorKey;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.AlchimieWirkung;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.DrogeSO;
import de.pho.dsapdfreader.uid.UidCategory;
import de.pho.dsapdfreader.uid.UidCategorySub;

public class Droge implements EquipmentI {

  public String uid;
  public DrogeKey drogeKey;
  public EquipmentKey key;
  public GiftTypeKey artKey;

  public String name;
  public String beschreibung; // Sprachinhalt
  public String besonderheiten; // Sprachinhalt
  public AlchimieWirkung wirkung; // Sprachinhalt
  public Price price;
  public EquipmentCategoryKey equipmentCategoryKey = EquipmentCategoryKey.drogen;

  public AttributeShort widerstand;
  public List<GiftVektorKey> vektor;
  public Integer stufe;
  public Boolean isStufeLvl;
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
    this.widerstand = dso.widerstand;

    this.vektor = dso.vektor;
    this.artKey = dso.artKey;


    this.stufe = dso.stufe;
    this.isStufeLvl = dso.isStufeQs;

    this.beginn = dso.beginn;
    this.dauer = dso.dauer;

    this.besonderheiten = dso.besonderheiten;

    if (dso.preis != null) this.price = dso.preis;
    else if (dso.kostenIngredienzien != null) {
      this.price = dso.kostenIngredienzien;
      this.price.minPriceInSilver = this.price.priceInSilver;
      this.price.priceInSilver = null;
    }
    else {
      this.price = new Price();
      this.price.isPricePerLevel = this.isStufeLvl;
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
