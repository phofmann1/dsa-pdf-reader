package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.exporter.model.MysticalSkillVariant;

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
    public String variantsText;
    @CsvCustomBindByName(converter = MysticalSkillVariantConvert.class)
    public MysticalSkillVariant variant1;
    @CsvCustomBindByName(converter = MysticalSkillVariantConvert.class)
    public MysticalSkillVariant variant2;
    @CsvCustomBindByName(converter = MysticalSkillVariantConvert.class)
    public MysticalSkillVariant variant3;
    @CsvCustomBindByName(converter = MysticalSkillVariantConvert.class)
    public MysticalSkillVariant variant4;
    @CsvCustomBindByName(converter = MysticalSkillVariantConvert.class)
    public MysticalSkillVariant variant5;
    @CsvBindByName
    public String talentKey;

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
            ", variant1=" + variant1 +
            ", variant2=" + variant2 +
            ", variant3=" + variant3 +
            ", variant4=" + variant4 +
            ", variant5=" + variant5 +
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
