package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MysticalSkillFeature
{
    COMMON,
    DEFENSIVE,
    DESIRE,
    EDUCATION,
    BLOOD,
    DEMONIC,
    DJUNGLE,
    INFLUENCE,
    ICE,
    EXTACY,
    ELEMENTAL,
    EPIPHANY,
    ORE,
    FIRE,
    FLAME,
    FREEDOM,
    FRIENDSHIP,
    COMMUNITY,
    GOOD_COMBAT,
    GOOD_GOLD,
    TRADE,
    CRAFT,
    HARMONY,
    HARDNESS,
    HEALING,
    HOME,
    CLAIRVOYANCE,
    HELPFULNESS,
    ILLUSION,
    HUNT,
    COLD,
    STRENGTH,
    AGRICULTURE,
    SUFFERING,
    MAGIC,
    MOON,
    NAMELESS,
    NATURE,
    OBJECT,
    ORDER,
    INTOXICATION,
    TRAVEL,
    SHADOW,
    FATE,
    SHIELD,
    SLEEP,
    SUN,
    SPHERES,
    STONE,
    STORM,
    COURAGE,
    TELEKINETIC,
    TEMPORAL,
    ANIMAL,
    DEATH,
    DREAM,
    TRANSIENCE,
    TRANSFORMATION,
    VISION,
    CHANGE,
    SAVAGERY,
    WIND,
    WINTER,
    KNOWLEDGE,
    WAVES,
    WOLFS,
    TIME,
    COMMON_SHAMAN;

    public static MysticalSkillFeature fromString(String text)
    {
        return switch (text.toLowerCase())
            {
                case "allgemein" -> MysticalSkillFeature.COMMON;
                case "antimagie" -> MysticalSkillFeature.DEFENSIVE;
                case "begierde" -> MysticalSkillFeature.DESIRE;
                case "bildung" -> MysticalSkillFeature.EDUCATION;
                case "blut" -> MysticalSkillFeature.BLOOD;
                case "dämonisch" -> MysticalSkillFeature.DEMONIC;
                case "dschungel" -> MysticalSkillFeature.DJUNGLE;
                case "einfluss" -> MysticalSkillFeature.INFLUENCE;
                case "eis" -> MysticalSkillFeature.ICE;
                case "ekstase" -> MysticalSkillFeature.EXTACY;
                case "elementar" -> MysticalSkillFeature.ELEMENTAL;
                case "erkenntnis" -> MysticalSkillFeature.EPIPHANY;
                case "erz" -> MysticalSkillFeature.ORE;
                case "feuer" -> MysticalSkillFeature.FIRE;
                case "flammen" -> MysticalSkillFeature.FLAME;
                case "freiheit" -> MysticalSkillFeature.FREEDOM;
                case "freundschaft" -> MysticalSkillFeature.FRIENDSHIP;
                case "gemeinschaft" -> MysticalSkillFeature.COMMUNITY;
                case "guter kampf" -> MysticalSkillFeature.GOOD_COMBAT;
                case "gutes gold" -> MysticalSkillFeature.GOOD_GOLD;
                case "handel" -> MysticalSkillFeature.TRADE;
                case "handwerk" -> MysticalSkillFeature.CRAFT;
                case "harmonie" -> MysticalSkillFeature.HARMONY;
                case "härte" -> MysticalSkillFeature.HARDNESS;
                case "heilung" -> MysticalSkillFeature.HEALING;
                case "heim" -> MysticalSkillFeature.HOME;
                case "hellsicht" -> MysticalSkillFeature.CLAIRVOYANCE;
                case "hilfsbereitschaft" -> MysticalSkillFeature.HELPFULNESS;
                case "illusion" -> MysticalSkillFeature.ILLUSION;
                case "jagd" -> MysticalSkillFeature.HUNT;
                case "kälte" -> MysticalSkillFeature.COLD;
                case "kraft" -> MysticalSkillFeature.STRENGTH;
                case "landwirtschaft" -> MysticalSkillFeature.AGRICULTURE;
                case "leiden" -> MysticalSkillFeature.SUFFERING;
                case "magie" -> MysticalSkillFeature.MAGIC;
                case "mond" -> MysticalSkillFeature.MOON;
                case "namenloser" -> MysticalSkillFeature.NAMELESS;
                case "natur" -> MysticalSkillFeature.NATURE;
                case "objekt" -> MysticalSkillFeature.OBJECT;
                case "ordnung" -> MysticalSkillFeature.ORDER;
                case "rausch" -> MysticalSkillFeature.INTOXICATION;
                case "reise" -> MysticalSkillFeature.TRAVEL;
                case "schatten" -> MysticalSkillFeature.SHADOW;
                case "schicksal" -> MysticalSkillFeature.FATE;
                case "schild" -> MysticalSkillFeature.SHIELD;
                case "schlaf" -> MysticalSkillFeature.SLEEP;
                case "sonne" -> MysticalSkillFeature.SUN;
                case "sphären" -> MysticalSkillFeature.SPHERES;
                case "stein" -> MysticalSkillFeature.STONE;
                case "sturm" -> MysticalSkillFeature.STORM;
                case "tapferkeit" -> MysticalSkillFeature.COURAGE;
                case "telekinese" -> MysticalSkillFeature.TELEKINETIC;
                case "temporal" -> MysticalSkillFeature.TEMPORAL;
                case "tier" -> MysticalSkillFeature.ANIMAL;
                case "tod" -> MysticalSkillFeature.DEATH;
                case "traum" -> MysticalSkillFeature.DREAM;
                case "vergänglichkeit" -> MysticalSkillFeature.TRANSIENCE;
                case "verwandlung" -> MysticalSkillFeature.TRANSFORMATION;
                case "vision" -> MysticalSkillFeature.VISION;
                case "wandel" -> MysticalSkillFeature.CHANGE;
                case "wildheit" -> MysticalSkillFeature.SAVAGERY;
                case "wind" -> MysticalSkillFeature.WIND;
                case "winter" -> MysticalSkillFeature.WINTER;
                case "wissen" -> MysticalSkillFeature.KNOWLEDGE;
                case "wogen" -> MysticalSkillFeature.WAVES;
                case "wölfe" -> MysticalSkillFeature.WOLFS;
                case "allgemein schamanenritus" -> MysticalSkillFeature.COMMON_SHAMAN;
                case "zeit" -> MysticalSkillFeature.TIME;
                default -> null;
            };
    }

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
