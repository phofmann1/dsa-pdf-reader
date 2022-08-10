package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MysticalSkillModification
{
    ENFORCE,
    COSTREDUCTION,
    RANGEINCREASE,
    FAST,
    SLOW,
    WITHOUTSPELLING,
    WITHOUTWEAVING;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
