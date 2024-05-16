package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;

import de.pho.dsapdfreader.config.TopicEnum;

public class CurriculumRaw implements DsaObjectI {
  @CsvBindByName
  public String name;
  @CsvBindByName
  public TopicEnum topic;
  @CsvBindByName
  public String publication;
  @CsvBindByName
  public String guideline;
  @CsvBindByName
  public String spellSelection;
  @CsvBindByName
  public String spellRestriction;
  @CsvBindByName
  public String pathName_I;
  @CsvBindByName
  public String spellChanges_I;
  @CsvBindByName
  public String additionalSkills_I;
  @CsvBindByName
  public String removedSkills_I;
  @CsvBindByName
  public String pathName_II;
  @CsvBindByName
  public String spellChanges_II;
  @CsvBindByName
  public String additionalSkills_II;
  @CsvBindByName
  public String removedSkills_II;

  @Override
  public String getName() {
    return name;
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
