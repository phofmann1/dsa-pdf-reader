package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.exporter.model.enums.SkillCategoryKey;

public class SkillRaw implements DsaObjectI
{
  @CsvBindByName
  public String name = "";
  @CsvBindByName
  public TopicEnum topic;
  @CsvBindByName
  public String publication = "";
  @CsvBindByName
  public String description = "";
  @CsvBindByName
  public SkillCategoryKey skillCategoryKey;

  @CsvBindByName
  public String newApplication;
  @CsvBindByName
  public String check;
  @CsvBindByName
  public String application;
  @CsvBindByName
  public String encumbrance;
  @CsvBindByName
  public String tools;
  @CsvBindByName
  public String quality;
  @CsvBindByName
  public String failure;
  @CsvBindByName
  public String criticalSuccess;
  @CsvBindByName
  public String criticalFailure;
  @CsvBindByName
  public String advancementCategory;

  @Override
  public String getName()
  {
    return this.name;
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

  @Override
  public String toString()
  {
    return "SkillRaw{" +
        "name='" + name + '\'' +
        ", topic=" + topic +
        ", publication='" + publication + '\'' +
        ", description='" + description + '\'' +
        ", newApplication='" + newApplication + '\'' +
        ", check='" + check + '\'' +
        ", application='" + application + '\'' +
        ", encumbrance='" + encumbrance + '\'' +
        ", tools='" + tools + '\'' +
        ", quality='" + quality + '\'' +
        ", failure='" + failure + '\'' +
        ", criticalSuccess='" + criticalSuccess + '\'' +
        ", criticalFailure='" + criticalFailure + '\'' +
        ", advancementCategory='" + advancementCategory + '\'' +
        '}';
  }
}
