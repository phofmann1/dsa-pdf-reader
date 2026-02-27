package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.enums.PoisonKey;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.AlchimieWirkung;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.GiftSO;
import de.pho.dsapdfreader.uid.UidCategory;
import de.pho.dsapdfreader.uid.UidCategorySub;

public class Gift implements EquipmentI {

  public String uid;
  public PoisonKey giftKey;
  public EquipmentKey key;
  public String name;
  public String beschreibung; // Sprachinhalt
  public String besonderheiten; // Sprachinhalt
  public AlchimieWirkung wirkung; // Sprachinhalt
  public Price preis;
  public EquipmentCategoryKey equipmentCategoryKey = EquipmentCategoryKey.gifte;

  public AttributeShort widerstand;
  public List<String> vektor;
  public String stufe;
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
    this.wirkung = gso.wirkung;
    this.vektor = gso.vektor;

    this.stufe = gso.stufe;

    this.beginn = gso.beginn;
    this.dauer = gso.dauer;

    this.besonderheiten = gso.besonderheiten;

    if (gso.preis != null) this.preis = gso.preis;
    else if (gso.kostenIngredienzien != null) {
      this.preis = gso.kostenIngredienzien;
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
