package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RequiredByCategory
{
    SPECIE,
    CULTURE,
    PROFESSION;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
