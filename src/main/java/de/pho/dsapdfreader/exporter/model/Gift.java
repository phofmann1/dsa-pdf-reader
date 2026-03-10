package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.enums.GiftKey;
import de.pho.dsapdfreader.exporter.model.enums.GiftTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.GiftVektorKey;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.GiftSO;
import de.pho.dsapdfreader.uid.UidCategory;
import de.pho.dsapdfreader.uid.UidCategorySub;

public class Gift implements EquipmentI {

  public String uid;
  public GiftKey giftKey;
  public EquipmentKey key;

  public GiftTypeKey artKey;

  public String name;
  public String beschreibung; // Sprachinhalt
  public String besonderheiten; // Sprachinhalt
  public List<ValueChange> wirkungVoll = new ArrayList<>();
  public List<ValueChange> wirkungAbgemildert = new ArrayList<>();
  public Price price;
  public EquipmentCategoryKey equipmentCategoryKey = EquipmentCategoryKey.gifte;

  public AttributeShort widerstand;
  public List<GiftVektorKey> vektor;
  public Boolean isStufeLvl;
  public Integer stufe;
  public Duration beginn;
  public Duration dauer;


  public Gift() {
  }

  public Gift(GiftSO gso) {
    this.uid = UidCategory.ausruestung_gift.prefix + Extractor.extractKeyTextFromText(gso.name).toLowerCase();
    this.key = Extractor.extractEnumKey(UidCategorySub.gift.prefix + gso.name, EquipmentKey.class);
    this.giftKey = gso.key;

    this.name = gso.name;
    this.beschreibung = gso.description;

    this.vektor = gso.vektor;
    this.artKey = gso.artKey;

    this.widerstand = gso.widerstand;

    this.stufe = gso.stufe;
    this.isStufeLvl = gso.isStufeQs;

    this.beginn = gso.beginn;
    this.dauer = gso.dauer;

    this.besonderheiten = gso.besonderheiten;

    if (gso.preis != null) this.price = gso.preis;
    else if (gso.kostenIngredienzien != null) {
      this.price = gso.kostenIngredienzien;
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
    return null;
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
