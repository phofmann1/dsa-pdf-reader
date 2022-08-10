package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CombatSkillKey
{
    DAGGER,
    FAN,
    FENCING,
    BLUNT,
    CHAIN,
    LANCE,
    WHIP,
    BRAWL,
    SHIELD,
    SWORD,
    PIKE,
    POLE,
    TWOHANDED_BLUNT,
    TWOHANDED_SWORD,
    CROSSBOW,
    BLOWPIPE,
    BOW,
    DISCUS,
    SPITFIRE,
    SLING,
    THROWING;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
