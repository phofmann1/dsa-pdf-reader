package de.pho.dsapdfreader.dsaconverter.model;

import de.pho.dsapdfreader.config.TopicEnum;

public class MysticalSkillSmall implements DsaObjectI
{
    public String name = "";
    public String description = "";
    public String range = "";
    public String duration = "";
    public String targetCategory = "";
    public String feature = "";
    public String remarks = "";
    public TopicEnum topic;

    @Override
    public String toString()
    {
        return "Trick{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", range='" + range + '\'' +
            ", duration='" + duration + '\'' +
            ", targetCategory='" + targetCategory + '\'' +
            ", feature='" + feature + '\'' +
            ", remarks='" + remarks + '\'' +
            ", topic='" + topic + '\'' +
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
}
