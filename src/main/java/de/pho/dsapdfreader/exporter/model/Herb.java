package de.pho.dsapdfreader.exporter.model;

import com.opencsv.bean.CsvBindByName;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.exporter.CheckQs;
import de.pho.dsapdfreader.exporter.model.enums.*;

import java.io.Serializable;
import java.util.List;

public class Herb implements Serializable {
  public HerbKey key;
  public String name;
  public List<String> alternativeNamen;
  public List<Publication> publications;
  public List<LandschaftsKey> landschaftstypen;
  public List<RegionKey> regionen;
  public Integer suchschwierigkeit;
  public Integer bestimmungsschwierigkeit;
  public List<Integer> anwendungen;
  public List<HerbEffect> effekteRoh;
  public Cost preisRoh;
  public Cost preisVerarbeitet;
  public List<RezeptKey> rezepte;
  public String alltagsarzneienUndVolksbrauchtum;
  public String haltbarkeitRoh;
  public String haltbarkeitVerarbeitet;
  public List<QSEntry> wissensfertigkeit;
  public CheckQs checkQs;
}
