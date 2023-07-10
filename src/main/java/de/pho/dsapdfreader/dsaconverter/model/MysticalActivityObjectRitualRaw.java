package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.exporter.model.enums.ArtifactKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;

public class MysticalActivityObjectRitualRaw implements DsaObjectI
{
  @CsvBindByName
  public String name = "";
  @CsvBindByName
  public String publication = "";
  @CsvBindByName
  public String range = "";
  @CsvBindByName
  public String duration = "";
  @CsvBindByName
  public String targetCategory = "";
  @CsvBindByName
  public String feature = "";
  @CsvBindByName
  public String remarks = "";
  @CsvBindByName
  public TopicEnum topic;
  @CsvBindByName
  public String check;
  @CsvBindByName
  public String effect;
  @CsvBindByName
  public String castingDuration;
  @CsvBindByName
  public String commonness;
  @CsvBindByName
  public String cost;
  @CsvBindByName
  public String advancementCategory;
  @CsvBindByName
  public String qs1;
  @CsvBindByName
  public String qs2;
  @CsvBindByName
  public String qs3;
  @CsvBindByName
  public String qs4;
  @CsvBindByName
  public String qs5;
  @CsvBindByName
  public String qs6;
  @CsvBindByName
  public String talentKey;
  @CsvBindByName
  public String elements;

  @CsvBindByName
  public MysticalSkillCategory msCategory;
  @CsvBindByName
  public ArtifactKey artifactKey;
  @CsvBindByName
  public String tribes;
  @CsvBindByName
  public String musicTraditions;
  @CsvBindByName
  public String requirements;
  @CsvBindByName
  public String volume;
  @CsvBindByName
  public String binding;
  @CsvBindByName
  public String ap;

  @Override
  public String toString()
  {
    return "MysticalActivityObjectRitualRaw{" +
        "name='" + name + '\'' +
        ", publication='" + publication + '\'' +
        ", range='" + range + '\'' +
        ", duration='" + duration + '\'' +
        ", targetCategory='" + targetCategory + '\'' +
        ", feature='" + feature + '\'' +
        ", remarks='" + remarks + '\'' +
        ", topic=" + topic +
        ", check='" + check + '\'' +
        ", effect='" + effect + '\'' +
        ", castingDuration='" + castingDuration + '\'' +
        ", commonness='" + commonness + '\'' +
        ", cost='" + cost + '\'' +
        ", advancementCategory='" + advancementCategory + '\'' +
        ", qs1='" + qs1 + '\'' +
        ", qs2='" + qs2 + '\'' +
        ", qs3='" + qs3 + '\'' +
        ", qs4='" + qs4 + '\'' +
        ", qs5='" + qs5 + '\'' +
        ", qs6='" + qs6 + '\'' +
        ", talentKey='" + talentKey + '\'' +
        ", elements='" + elements + '\'' +
        ", msCategory=" + msCategory +
        ", artifactKey=" + artifactKey +
        ", tribes='" + tribes + '\'' +
        ", musicTraditions='" + musicTraditions + '\'' +
        ", requirements='" + requirements + '\'' +
        ", volume='" + volume + '\'' +
        ", binding='" + binding + '\'' +
        ", ap='" + ap + '\'' +
        '}';
  }

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

}
