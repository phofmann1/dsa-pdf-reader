package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.enums.ElixierKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.enums.LaborKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.ElixierSO;
import de.pho.dsapdfreader.uid.UidCategory;
import de.pho.dsapdfreader.uid.UidCategorySub;

public class Elixier implements EquipmentI {

  public String uid;
  public EquipmentKey key;
  public ElixierKey elixierKey;

  public String name;
  public List<Alias> alternativeNamen;
  public LaborKey labor;

  @JsonIgnore
  public List<QSEntry> wirkung;
  @JsonIgnore
  public QSEntry hyperpotenteWirkung;

  public Price preis;
  public EquipmentCategoryKey equipmentCategoryKey = EquipmentCategoryKey.elixiere;

  public List<Publication> publications;

  public Elixier() {
  }

  public Elixier(ElixierSO eso) {
    this.key = Extractor.extractEnumKey(UidCategorySub.elixier.prefix + eso.name, EquipmentKey.class);
    this.elixierKey = eso.key;
    this.uid = UidCategory.ausruestung_elixier.prefix + Extractor.extractKeyTextFromText(eso.name).toLowerCase();

    this.name = eso.name;
    this.alternativeNamen = eso.alternativeNamen;

    //Diese sind Später relevant um ValueChanges abzubilden. Der Regeltext muss über Übersetzungen kommen:
    this.wirkung = eso.wirkung;
    this.hyperpotenteWirkung = eso.hyperpotenteWirkung;

    //Preise sind nicht in allen Publikationen gegeben, deswegen wird im Zweifelsfall die Ingredienzkosten Angabe für den Preis als minPriceInSilver genutzt
    if (eso.preis != null) this.preis = eso.preis;
    else if (eso.kostenIngredienzien != null) {
      this.preis = eso.kostenIngredienzien;
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
    return null;
  }
}
