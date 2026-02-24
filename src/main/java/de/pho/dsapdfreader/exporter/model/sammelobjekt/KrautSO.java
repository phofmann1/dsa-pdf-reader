package de.pho.dsapdfreader.exporter.model.sammelobjekt;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.pho.dsapdfreader.exporter.model.Alias;
import de.pho.dsapdfreader.exporter.model.CheckQs;
import de.pho.dsapdfreader.exporter.model.Price;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.enums.HerbKey;
import de.pho.dsapdfreader.exporter.model.enums.LandschaftsKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.RegionKey;
import de.pho.dsapdfreader.exporter.model.enums.RezeptKey;

public class KrautSO implements Serializable {
  public HerbKey key;
  public String name;
  public List<Alias> alternativeNamen;
  public List<Publication> publications;
  public List<LandschaftsKey> landschaftstypen;
  public List<RegionKey> regionen;
  public Integer suchschwierigkeit;
  public Integer bestimmungsschwierigkeit;
  public List<Integer> anwendungen;
  public List<KrautWirkung> effekteRoh;
  public Price preisRoh;
  public Price preisVerarbeitet;
  public List<RezeptKey> rezepte;
  public String alltagsarzneienUndVolksbrauchtum;
  public String haltbarkeitRoh;
  public String haltbarkeitVerarbeitet;
  @JsonIgnore
  public CheckQs checkQs;
  public List<EquipmentKey> equipmentKeys;
}
