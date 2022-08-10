package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SelectionCategory
{
    ARTIFACT,
    COMBATSKILL,
    MYSTICALSKILL,
    DESEASE,
    POISON,
    SKILL,
    ELEMENT,
    SPECIE,
    FEATURE;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
