package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;

public class MeleeWeaponRaw implements DsaObjectI
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
  public String tp;
  @CsvBindByName
  public String additionalDamage;

  @CsvBindByName
  public String combatDistance;
  @CsvBindByName
  public String atPaMod;
  @CsvBindByName
  public String weight;
  @CsvBindByName
  public String size;
  @CsvBindByName
  public String price;
  @CsvBindByName
  public String craft;
  @CsvBindByName
  public CombatSkillKey combatSkillKey;
  @CsvBindByName
  public boolean isImprovised;


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
