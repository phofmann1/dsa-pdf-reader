package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MysticalSkillCategory
{
    SPELL,
    RITUAL,
    TRICK,
    CURSE,
    JEST,
    POWER,
    ELFENSONG,
    RITUALOFDOMINION,
    MELODY,
    DANCE,
    ZIBILJA,
    BLESSING,
    LITURGY,
    CEREMONY,
    GEODE;


    @JsonValue
    public int toValue()
    {
        return ordinal();
    }

}
