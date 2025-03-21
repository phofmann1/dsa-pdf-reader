package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;

import com.opencsv.bean.CsvCustomBindByName;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.exporter.model.enums.HitZoneKey;

import java.util.List;

public class ArmorRaw implements DsaObjectI
{
  @CsvBindByName
  public String name;
  @CsvBindByName
  public TopicEnum topic;
  @CsvBindByName
  public String publication;
  @CsvBindByName
  public String remark;
  @CsvBindByName
  public String advantage;
  @CsvBindByName
  public String disadvantage;
  @CsvBindByName
  public String armor;
  @CsvBindByName
  public String encumberance;
  @CsvBindByName
  public boolean additionalEncumberance;

  @CsvBindByName
  public String weight;
  @CsvBindByName
  public String price;
  @CsvBindByName
  public String craft;
  @CsvBindByName
  public boolean isArmorPart;
  @CsvCustomBindByName(converter = CsvCustomConvertHitZoneList.class)
  public List<HitZoneKey> protectedZones;


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
