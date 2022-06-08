package de.pho.dsapdfreader.dsaconverter.model;

import java.util.Arrays;

import com.opencsv.bean.CsvBindByName;

import de.pho.dsapdfreader.config.TopicEnum;

public class MysticalSkillRaw implements DsaObjectI
{
    @CsvBindByName
    public String name = "";
    @CsvBindByName
    public String publication = "";
    @CsvBindByName
    public String description = "";
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
    public String furtherInformation;
    @CsvBindByName
    public String[] variants;

    @Override
    public String toString()
    {
        return "MysticalSkillMedium{" +
            "name='" + getName() + '\'' +
            ", description='" + description + '\'' +
            ", check='" + check + '\'' +
            ", effect='" + effect + '\'' +
            ", castingDuration='" + castingDuration + '\'' +
            ", range='" + range + '\'' +
            ", duration='" + duration + '\'' +
            ", targetCategory='" + targetCategory + '\'' +
            ", cost=" + cost +
            ", feature='" + feature + '\'' +
            ", remarks='" + remarks + '\'' +
            ", topic=" + topic +
            ", publication=" + publication +
            ", commonness='" + commonness + '\'' +
            ", advancementCategory='" + advancementCategory + '\'' +
            ", variants=" + Arrays.toString(variants) +
            ", furtherInformation='" + furtherInformation + '\'' +
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
