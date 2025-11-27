package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;

import de.pho.dsapdfreader.config.TopicEnum;

public class BeverageRaw implements DsaObjectI {
  @CsvBindByName
  public String name;
  @CsvBindByName
  public TopicEnum topic;
  @CsvBindByName
  public String publication;
  @CsvBindByName
  public String unit;
  @CsvBindByName
  public String description;
  @CsvBindByName
  public String list;
  @CsvBindByName
  public String type;
  @CsvBindByName
  public String verbreitung;
  @CsvBindByName
  public Integer qualityMin;
  @CsvBindByName
  public Integer qualityMax;
  @CsvBindByName
  public String pricePerLiter;
  @CsvBindByName
  public String pricePerUnit;
  @CsvBindByName
  public String effect;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
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
