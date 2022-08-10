package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AttributeShort
{
    MU,
    KL,
    IN,
    CH,
    FF,
    GE,
    KO,
    KK,
    SK,
    ZK,
    LE_MAGIC,
    LE_CLERIC;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
