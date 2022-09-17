package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;
import de.pho.dsapdfreader.exporter.model.enums.Unit;

public abstract class Extractor
{
    protected static final Logger LOGGER = LogManager.getLogger();

    protected static final Pattern PAT_NUMBER = Pattern.compile("\\d+");
    protected static final Pattern PAT_LEADING_NUMBER = Pattern.compile("^[ \\d]*");
    protected static final Pattern PAT_CONTENT_PARENTHESES = Pattern.compile("\\((.*?)\\)");
    protected static final Pattern PAT_BETWEEN_BRACKETS = Pattern.compile("(?<=\\().*(?=\\))");

    protected static final String REG_COMMAS_OR_UND_NOT_IN_BRACKETS = "," + // Match ","
        "(?=" + // start lookahead...
        "(?:" + // the following...
        "[^()]*\\(" + // any number of none "(" or ")" followed by "("
        "[^)]*\\)" + // then any number of none ")" followed by ")"
        ")*" + // ...any amount of times
        "[^()]*" + // followed by any amount of none "(" or ")"
        "\\Z" + // until end of text
        ")" + // ...end of lookahead
        "|" + // or
        "und" + // Match "und"
        "(?=" + // start lookahead...
        "(?:" + // the following...
        "[^()]*\\(" + // any number of none "(" or ")" followed by "("
        "[^)]*\\)" + // then any number of none ")" followed by ")"
        ")*" + // ...any amount of times
        "[^()]*" + // followed by any amount of none "(" or ")"
        "\\Z" + // until end of text
        ")"; // ...end of lookahead;

    protected static int extractFirstNumberFromText(String t, MysticalSkillRaw msr)
    {
        List<Integer> results = extractNumbersFromText(t);
        if (results.size() == 0)
        {
            LOGGER.debug(getPrefix(msr) + "Der Text(" + t + ") enthält keine Zahl");
            return 0;
        }
        return results.get(0);
    }

    protected static List<Integer> extractNumbersFromText(String t)
    {
        List<Integer> returnValue = new ArrayList<>();
        Matcher matcher = PAT_NUMBER.matcher(t);
        while (matcher.find())
        {
            returnValue.add(Integer.valueOf(matcher.group().trim()));
        }
        return returnValue;
    }


    protected static String extractTextBetweenBrackets(String t)
    {
        Matcher matcher = PAT_BETWEEN_BRACKETS.matcher(t);
        if (!t.isEmpty() && matcher.find())
        {
            return matcher.group().trim();
        }
        return null;
    }

    protected static Unit extractUnitFromText(String t, MysticalSkillRaw msr)
    {
        Unit[] returnValue = extractUnitsFromText(t, msr, false);
        if (returnValue.length == 0)
        {
            return null;
        } else if (returnValue.length > 1)
        {
            LOGGER.error("Unit (" + t + ") is not unambiguously.");
            return null;
        } else
        {
            return returnValue[0];
        }
    }

    protected static Unit[] extractUnitsFromText(String t, MysticalSkillRaw msr, boolean isMandatory)
    {
        List<Unit> l = new ArrayList<>();
        if (t.contains("Aktion")) l.add(Unit.ACTION);
        // if (t.contains("")) l.add(Unit.AREA_OF_EFFECT);
        if (t.contains("Kontinent")) l.add(Unit.CONTINENT);
        if (t.contains("Jahrhundert")) l.add(Unit.CENTURY);
        if (t.contains("KR") || t.contains("Kampfrunde")) l.add(Unit.COMBAT_ROUND);
        if (t.contains("Doppelgänger")) l.add(Unit.DUPLICATE);
        if (t.contains("Tag")) l.add(Unit.DAY);
        if (t.contains("Stunde")) l.add(Unit.HOUR);
        if (t.contains("sofort")) l.add(Unit.IMMEDIATE);
        if (t.contains("Stein")) l.add(Unit.KG);
        if (t.contains("LeP")) l.add(Unit.LEP);
        if (t.contains("Giftstufe")) l.add(Unit.LEVEL_SICKNESS);
        if (t.contains("Krankheitsstufe")) l.add(Unit.LEVEL_POISON);
        if (t.contains("Minute")) l.add(Unit.MINUTE);
        if (t.contains("Meile")) l.add(Unit.MILE);
        if (t.contains("Schritt")) l.add(Unit.METER);
        if (t.contains("Rechtschritt")) l.add(Unit.METERS_SQUARE);
        if (t.contains("Monat")) l.add(Unit.MONTH);
        // if (t.contains("")) l.add(Unit.ONCE);
        if (t.contains("permanent")) l.add(Unit.PERMANENT);
        if (t.contains("Person")) l.add(Unit.PERSON);
        // if (t.contains("")) l.add(Unit.RS);
        // if (t.contains("")) l.add( Unit.SIZE);
        if (t.contains("Sekunde")) l.add(Unit.SECOND);
        if (t.contains("selbst") || t.contains("Selbst")) l.add(Unit.SELF);
        if (t.contains("Sicht")) l.add(Unit.SIGHT);
        // if (t.contains("")) l.add(Unit.SIZE_WEAPON);
        if (t.contains("aufrechterhalten")) l.add(Unit.SUSTAINED);
        // if (t.contains("")) l.add( Unit.TARGET);
        // if (t.contains("")) l.add( Unit.TARGET_ASP);
        // if (t.contains("")) l.add( Unit.TARGET_ATT_KL);
        if (t.contains("Berührung")) l.add(Unit.TOUCH);
        //if (t.contains("")) l.add(Unit.TURN);
        // if (t.contains("")) l.add( Unit.VOLUME_M);
        if (t.contains("Woche")) l.add(Unit.WEEK);
        if (t.contains("dere")) l.add(Unit.WORLD);
        if (t.contains("Jahr")) l.add(Unit.YEAR);
        // if (t.contains("")) l.add(Unit.ZONE);

        if (l.size() == 0 && isMandatory)
        {
            LOGGER.error(getPrefix(msr) + "String (" + t + ") contains no implemented unit.");
        }
        return l.toArray(new Unit[l.size()]);
    }

    public static TraditionKey extractTraditionKeyFromText(String t)
    {
      if (t.contains("allgemein (Schamanenritus)")) return TraditionKey.SHAMAN_ALL;
      if (t.contains("allgemein") | t.contains("Allgemein")) return TraditionKey.ALL;
      //if(t.contains("")) return TraditionKey.ALCHIMIST;
      if (t.contains("Angrosch")) return TraditionKey.ANGROSCH;
      //if(t.contains("")) return TraditionKey.ANIMIST;
      if (t.contains("Aves")) return TraditionKey.AVES;
      if (t.contains("Ceoladir") || t.contains("Derwisch") || t.contains("Sangara")) return TraditionKey.BARDE;
      if (t.contains("Boron")) return TraditionKey.BORON;
      //if(t.contains("")) return TraditionKey.BROBIM_GEODE;
        if (t.contains("Kristallomant")) return TraditionKey.CRISTALLOMANCER;
        if (t.contains("Majuna") || t.contains("Hazaqi") || t.contains("Rahkisa") || t.contains("Sharisad")) return TraditionKey.DANCER;
        if (t.contains("Druide")) return TraditionKey.DRUID;
        if (t.contains("Efferd")) return TraditionKey.EFFERD;
        if (t.contains("Elf")) return TraditionKey.ELF;
        if (t.contains("Firun")) return TraditionKey.FIRUN;
        if (t.contains("Geode")) return TraditionKey.GEODE;
        //if(t.contains("")) return TraditionKey.GIFTED;
      if (t.contains("Goblinzauberinnen")) return TraditionKey.GOBLIN;
      if (t.contains("Schelm")) return TraditionKey.HARLEQUIN;
        if (t.contains("Hesinde")) return TraditionKey.HESINDE;
        if (t.contains("Ifirn")) return TraditionKey.IFIRN;
        if (t.contains("Scharlatan")) return TraditionKey.ILLUSIONIST;
        if (t.contains("Ingerimm")) return TraditionKey.INGERIMM;
        //if(t.contains("")) return TraditionKey.INTUITIVE;
        if (t.contains("Kor")) return TraditionKey.KOR;
        if (t.contains("Levthan")) return TraditionKey.LEVTHAN;
        if (t.contains("Gildenmagier")) return TraditionKey.MAGE;
        if (t.contains("Marbo")) return TraditionKey.MARBO;
        if (t.contains("Namenlos")) return TraditionKey.NAMENLOS;
        if (t.contains("Nandus")) return TraditionKey.NANDUS;
        if (t.contains("Peraine")) return TraditionKey.PERAINE;
        if (t.contains("Phex")) return TraditionKey.PHEX;
        if (t.contains("Praios")) return TraditionKey.PRAIOS;
        if (t.contains("Qabalya")) return TraditionKey.QABALYA;
        if (t.contains("Rahja")) return TraditionKey.RAHJA;
        if (t.contains("Rondra")) return TraditionKey.RONDRA;
        if (t.contains("Ferkina")) return TraditionKey.SHAMAN_FERKINA;
      if (t.contains("Fjarninger")) return TraditionKey.SHAMAN_FJARNINGER;
      if (t.contains("Gjalsker")) return TraditionKey.SHAMAN_GJALSKER;
      if (t.contains("Moha")) return TraditionKey.SHAMAN_MOHA;
      if (t.contains("Nivese")) return TraditionKey.SHAMAN_NIVESE;
      if (t.contains("Trollzacker")) return TraditionKey.SHAMAN_TROLLZACKER;
      if (t.contains("Swafnir")) return TraditionKey.SWAFNIR;
      if (t.contains("Travia")) return TraditionKey.TRAVIA;
      if (t.contains("Tsa")) return TraditionKey.TSA;
      if (t.contains("Hexe")) return TraditionKey.WITCH;
      if (t.contains("Zibilja")) return TraditionKey.ZIBILJA;
      if (t.contains("Borbaradianer")) return TraditionKey.BORBARAD;
      throw new IllegalArgumentException("Tradition(" + t + ") konnte nicht interpretiert werden");
    }

    protected static boolean isClerical(TopicEnum topic)
    {
        return topic == TopicEnum.BLESSINGS || topic == TopicEnum.LITURGIES || topic == TopicEnum.CEREMONIES;
    }

    public static MysticalSkillCategory retrieveCategory(TopicEnum topic)
    {
        return switch (topic)
            {
              case TRICKS, TRICKS_GRIMORIUM -> MysticalSkillCategory.TRICK;
              case SPELLS, SPELLS_GRIMORIUM -> MysticalSkillCategory.SPELL;
              case RITUALS, RITUALS_GRIMORIUM -> MysticalSkillCategory.RITUAL;
              case BLESSINGS -> MysticalSkillCategory.BLESSING;
              case LITURGIES -> MysticalSkillCategory.LITURGY;
              case CEREMONIES -> MysticalSkillCategory.CEREMONY;
              case CURSES -> MysticalSkillCategory.CURSE;
              case ELFENSONGS -> MysticalSkillCategory.ELFENSONG;
              case MELODIES -> MysticalSkillCategory.MELODY;
              case DANCES -> MysticalSkillCategory.DANCE;
              default -> throw new IllegalArgumentException(topic + " not found");
            };
    }

    protected static String getPrefix(MysticalSkillRaw msr)
    {
        return msr.publication + " - " + msr.name + ": ";
    }

}
