package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;

import de.pho.dsapdfreader.config.TopicEnum;

public class BoonRaw implements DsaObjectI
{
  @CsvBindByName
  public String name;
  @CsvBindByName
  public TopicEnum topic;
  @CsvBindByName
  public String publication;
  @CsvBindByName
  public String description;
  @CsvBindByName
  public String rules;
  @CsvBindByName
  public String preconditions;
  @CsvBindByName
  public String ap;

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
