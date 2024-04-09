package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ValueChangeKey
{
    SKILL,
    ENERGY_ASP,
    ENERGY_KAP,
    ATT_MU,
    ATT_KL,
    ATT_IN,
    ATT_CH,
    ATT_FF,
    ATT_GE,
    ATT_KO,
    ATT_KK,
    ATT_SK,
    ATT_ZK,
    SKILL_CONVINCE,
    ENERGY_LEP,
    COMBATSKILL,
    MONEY,
    SKILL_ORIENTATION,
    ATT_GS,
    CV_AW,
    CV_INI,
    ATT_AW,
    ATT_INI,
    ATT_WS,
    ATT_SCHIP,
    SOZIALER_STAND,
    MS_COUNT_4_CAT,
    CV_RS;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
