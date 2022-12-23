package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ElementKey
{
    FIRE,
    WATER,
    ICE,
    HUMUS,
    AIR,
    STONE;


  @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
