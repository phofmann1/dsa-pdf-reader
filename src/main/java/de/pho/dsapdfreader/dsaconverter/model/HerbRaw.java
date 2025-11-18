package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.exporter.model.enums.HitZoneKey;

import java.util.List;

public class HerbRaw implements DsaObjectI
{
  @CsvBindByName public String name;
  @CsvBindByName public TopicEnum topic;
  @CsvBindByName public String publication;
  @CsvBindByName public String alternativeNamen;
  @CsvBindByName public String verbreitung;
  @CsvBindByName public String verbreitungLandschaftstyp;
  @CsvBindByName public String verbreitungRegionen;
  @CsvBindByName public String suchschwierigkeit;
  @CsvBindByName public String bestimmungsschwierigkeit;
  @CsvBindByName public String anwendungen;
  @CsvBindByName public String wirkung;
  @CsvBindByName public String preis;
  @CsvBindByName public String rezepte;
  @CsvBindByName public String alltagsarzneienUndVolksbrauchtum;
  @CsvBindByName public String haltbarkeit;
  @CsvBindByName public String wissensfertigkeit;
          ;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name)
  {
    this.name = name;
  }

  @Override
  public void setTopic(TopicEnum topic)
  {
    this.topic = topic;
  }

  @Override
  public void setPublication(String publication)
  {
    this.publication = publication;
  }
}
