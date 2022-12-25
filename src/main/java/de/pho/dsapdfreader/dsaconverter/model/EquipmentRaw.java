package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;

import de.pho.dsapdfreader.config.TopicEnum;

public class EquipmentRaw implements DsaObjectI
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
  public String structure;
  @CsvBindByName
  public String weight;
  @CsvBindByName
  public String price;
  @CsvBindByName
  public String craft;
  @CsvBindByName
  public String category;
  @CsvBindByName
  public String color;
  @CsvBindByName
  public boolean isPricePerLevel;


  @Override
  public String getName()
  {
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
