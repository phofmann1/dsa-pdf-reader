package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ValueChangeType
{
    VALUE,
    MAX,
    FP,
    QS,
    REGENERATION,
    CHECK;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
