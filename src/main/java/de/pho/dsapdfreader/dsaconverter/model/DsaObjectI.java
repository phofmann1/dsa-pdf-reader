package de.pho.dsapdfreader.dsaconverter.model;

import de.pho.dsapdfreader.config.TopicEnum;

public interface DsaObjectI
{
    String getName();

    void setName(String name);

    void setTopic(TopicEnum topic);

    void setPublication(String publication);
}
