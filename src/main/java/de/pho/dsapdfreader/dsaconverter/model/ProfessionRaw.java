package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;

import de.pho.dsapdfreader.config.TopicEnum;

public class ProfessionRaw implements DsaObjectI
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
    public String preconditions;
    @CsvBindByName
    public String ap;
    @CsvBindByName
    public String specialAbilities;
    @CsvBindByName
    public String skillsCombat;
    @CsvBindByName
    public String skills;
    @CsvBindByName
    public String skillsMagic;
    @CsvBindByName
    public String skillsCleric;
    @CsvBindByName
    public String meritsRecommended;
    @CsvBindByName
    public String flawsRecommended;
    @CsvBindByName
    public String meritsInappropriate;
    @CsvBindByName
    public String flawsInappropriate;
    @CsvBindByName
    public String variants;
    @CsvBindByName
    public String equip;

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
