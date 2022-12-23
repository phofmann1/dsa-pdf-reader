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

    public static CombatSkillKey parse(String text)
    {
        return switch (text)
            {
                case "Dolche" -> DAGGER;
                case "Fächer" -> FAN;
                case "Fechtwaffen" -> FENCING;
                case "Hiebwaffen" -> BLUNT;
                case "Kettenwaffen" -> CHAIN;
                case "Lanzen" -> LANCE;
                case "Peitschen" -> WHIP;
                case "Raufen" -> BRAWL;
                case "Schilde" -> SHIELD;
                case "Schwerter" -> SWORD;
                case "Spießwaffen" -> PIKE;
                case "Stangenwaffen" -> POLE;
                case "Zweihandhiebwaffen" -> TWOHANDED_BLUNT;
                case "Zweihandschwerter" -> TWOHANDED_SWORD;
                case "Armbrüste" -> CROSSBOW;
                case "Blasrohre" -> BLOWPIPE;
                case "Bögen" -> BOW;
                case "Diskusse" -> DISCUS;
                case "Feuerspucken" -> SPITFIRE;
                case "Schleudern" -> SLING;
                case "Wurfwaffen" -> THROWING;
                default -> throw new IllegalArgumentException("Argument " + text + " unknown in Enum " + CombatSkillKey.class.getSimpleName());
            };
    }
}
