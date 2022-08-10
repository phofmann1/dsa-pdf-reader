package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Unit
{
    IMMEDIATE,
    SUSTAINED,
    PERMANENT,
    ONCE,
    ACTION,
    SECOND,
    TURN,
    MINUTE,
    HOUR,
    DAY,
    WEEK,
    MONTH,
    YEAR,
    CENTURY,
    SELF,
    TOUCH,
    METER,
    SIGHT,
    MILE,
    CONTINENT,
    WORLD,
    ZONE,
    RS,
    TARGET,
    LEVEL_POISON,
    SIZE,
    TARGET_ATT_KL,
    LEVEL_SICKNESS,
    PERSON,
    VOLUME_M,
    SIZE_WEAPON,
    TARGET_ASP,
    AREA_OF_EFFECT,
    LEP,
    DUPLICATE,
    COMBAT_ROUND,
    KG,
    METERS_SQUARE;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
