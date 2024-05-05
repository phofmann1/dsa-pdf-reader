package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityCategoryKey;

public class TraditionRaw implements DsaObjectI {
  @CsvBindByName
  public String name = "";
  @CsvBindByName
  public TopicEnum topic;
  @CsvBindByName
  public String publication = "";
  @CsvBindByName
  public String description = "";
  @CsvBindByName
  public String preconditions = "";
  @CsvBindByName
  public String ap = "";
  @CsvBindByName
  public String pantheon = "";
  @CsvBindByName
  public SpecialAbilityCategoryKey categoryKey;

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setTopic(TopicEnum topic) {
    this.topic = topic;
  }

  @Override
  public void setPublication(String publication) {
    this.publication = publication;
  }

}
