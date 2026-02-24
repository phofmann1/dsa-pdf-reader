package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.ElixierSO;

public class Elixier implements EquipmentI {

  public String uid;
  public EquipmentKey key;
  public String name;
  public String beschreibung;
  public List<QSEntry> wirkung;
  public Price preis;
  public EquipmentCategoryKey equipmentCategoryKey = EquipmentCategoryKey.elixiere;

  public Elixier() {
  }

  public Elixier(ElixierSO eso) {
    this.key = Extractor.extractEnumKey("elixier_" + eso.name, EquipmentKey.class);
    // this.uid = "ausruestung_"+this.key.name();
    this.name = eso.name;
    this.beschreibung = eso.description;
    this.wirkung = eso.wirkung;
    if (eso.preis != null) this.preis = eso.preis;
    else if (eso.kostenIngredienzien != null) {
      this.preis = eso.kostenIngredienzien;
      this.preis.minPriceInSilver = this.preis.priceInSilver;
      this.preis.priceInSilver = null;
    }
  }

  @Override
  public int getKeyValue() {
    return this.key.toValue();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getRules() {
    return this.wirkung.stream().map(w -> w.qs + ": " + w.information).reduce((rules, current) -> rules + "\r\n\r\n" + current).get();
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
