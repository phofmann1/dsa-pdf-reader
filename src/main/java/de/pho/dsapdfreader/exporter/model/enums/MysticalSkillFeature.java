package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MysticalSkillFeature
{
  common,
  defensive,
  desire,
  education,
  blood,
  demonic,
  djungle,
  influence,
  ice,
  extacy,
  elemental,
  epiphany,
  ore,
  fire,
  flame,
  freedom,
  friendship,
  community,
  good_combat,
  good_gold,
  trade,
  craft,
  harmony,
  hardness,
  healing,
  home,
  clairvoyance,
  helpfulness,
  illusion,
  hunt,
  cold,
  strength,
  agriculture,
  suffering,
  magic,
  moon,
  nameless,
  nature,
  object,
  order,
  intoxication,
  travel,
  shadow,
  fate,
  shield,
  sleep,
  sun,
  spheres,
  stone,
  storm,
  courage,
  telekinetic,
  temporal,
  animal,
  death,
  dream,
  transience,
  transformation,
  vision,
  change,
  savagery,
  wind,
  winter,
  knowledge,
  waves,
  wolfs,
  common_shaman,
  time;

  public static MysticalSkillFeature fromString(String text)
  {
    return switch (text.toLowerCase())
        {
          case "allgemein" -> MysticalSkillFeature.common;
          case "antimagie" -> MysticalSkillFeature.defensive;
          case "begierde" -> MysticalSkillFeature.desire;
          case "bildung" -> MysticalSkillFeature.education;
          case "blut" -> MysticalSkillFeature.blood;
          case "dämonisch" -> MysticalSkillFeature.demonic;
          case "dschungel" -> MysticalSkillFeature.djungle;
          case "einfluss" -> MysticalSkillFeature.influence;
          case "eis" -> MysticalSkillFeature.ice;
          case "ekstase" -> MysticalSkillFeature.extacy;
          case "elementar" -> MysticalSkillFeature.elemental;
          case "erkenntnis" -> MysticalSkillFeature.epiphany;
          case "erz" -> MysticalSkillFeature.order;
          case "feuer" -> MysticalSkillFeature.fire;
          case "flammen" -> MysticalSkillFeature.flame;
          case "freiheit" -> MysticalSkillFeature.freedom;
          case "freundschaft" -> MysticalSkillFeature.friendship;
          case "gemeinschaft" -> MysticalSkillFeature.community;
          case "guter kampf" -> MysticalSkillFeature.good_combat;
          case "gutes gold" -> MysticalSkillFeature.good_gold;
          case "handel" -> MysticalSkillFeature.trade;
          case "handwerk" -> MysticalSkillFeature.craft;
          case "harmonie" -> MysticalSkillFeature.harmony;
          case "härte" -> MysticalSkillFeature.hardness;
          case "heilung" -> MysticalSkillFeature.healing;
          case "heim" -> MysticalSkillFeature.home;
          case "hellsicht" -> MysticalSkillFeature.clairvoyance;
          case "hilfsbereitschaft" -> MysticalSkillFeature.helpfulness;
          case "illusion" -> MysticalSkillFeature.illusion;
          case "jagd" -> MysticalSkillFeature.hunt;
          case "kälte" -> MysticalSkillFeature.cold;
          case "kraft" -> MysticalSkillFeature.strength;
          case "landwirtschaft" -> MysticalSkillFeature.agriculture;
          case "leiden" -> MysticalSkillFeature.suffering;
          case "magie" -> MysticalSkillFeature.magic;
          case "mond" -> MysticalSkillFeature.moon;
          case "namenloser" -> MysticalSkillFeature.nameless;
          case "natur" -> MysticalSkillFeature.nature;
          case "objekt" -> MysticalSkillFeature.object;
          case "ordnung" -> MysticalSkillFeature.order;
          case "rausch" -> MysticalSkillFeature.intoxication;
          case "reise" -> MysticalSkillFeature.travel;
          case "schatten" -> MysticalSkillFeature.shadow;
          case "schicksal" -> MysticalSkillFeature.fate;
          case "schild" -> MysticalSkillFeature.shield;
          case "schlaf" -> MysticalSkillFeature.sleep;
          case "sonne" -> MysticalSkillFeature.sun;
          case "sphären" -> MysticalSkillFeature.spheres;
          case "stein" -> MysticalSkillFeature.stone;
          case "sturm" -> MysticalSkillFeature.storm;
          case "tapferkeit" -> MysticalSkillFeature.courage;
          case "telekinese" -> MysticalSkillFeature.telekinetic;
          case "temporal" -> MysticalSkillFeature.temporal;
          case "tier" -> MysticalSkillFeature.animal;
          case "tod" -> MysticalSkillFeature.death;
          case "traum" -> MysticalSkillFeature.dream;
          case "vergänglichkeit" -> MysticalSkillFeature.transience;
          case "verwandlung" -> MysticalSkillFeature.transformation;
          case "vision" -> MysticalSkillFeature.vision;
          case "wandel" -> MysticalSkillFeature.change;
          case "wildheit" -> MysticalSkillFeature.savagery;
          case "wind" -> MysticalSkillFeature.wind;
          case "winter" -> MysticalSkillFeature.winter;
          case "wissen" -> MysticalSkillFeature.knowledge;
          case "wogen" -> MysticalSkillFeature.waves;
          case "wölfe" -> MysticalSkillFeature.wolfs;
          case "allgemein schamanenritus" -> MysticalSkillFeature.common_shaman;
          case "zeit" -> MysticalSkillFeature.time;
          default -> null;
        };
    }

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
