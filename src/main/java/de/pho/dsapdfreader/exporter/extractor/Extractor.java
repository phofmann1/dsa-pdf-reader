package de.pho.dsapdfreader.exporter.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.TraditionKey;
import de.pho.dsapdfreader.exporter.model.Unit;

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

    protected static Unit extractUnitFromText(String t)
    {
        Unit[] returnValue = extractUnitsFromText(t, false);
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

    protected static Unit[] extractUnitsFromText(String t, boolean allowNoHit)
    {
        List<Unit> l = new ArrayList<>();
        if (t.contains("Aktion")) l.add(Unit.action);
        // if (t.contains("")) l.add(Unit.area_of_effect);
        if (t.contains("Kontinent")) l.add(Unit.continent);
        if (t.contains("Jahrhundert")) l.add(Unit.century);
        if (t.contains("KR") || t.contains("Kampfrunde")) l.add(Unit.combat_round);
        if (t.contains("Doppelgänger")) l.add(Unit.duplicate);
        if (t.contains("Tag")) l.add(Unit.day);
        if (t.contains("Stunde")) l.add(Unit.hour);
        if (t.contains("sofort")) l.add(Unit.immediate);
        if (t.contains("Stein")) l.add(Unit.kg);
        if (t.contains("LeP")) l.add(Unit.lep);
        if (t.contains("Giftstufe")) l.add(Unit.level_sickness);
        if (t.contains("Krankheitsstufe")) l.add(Unit.level_poison);
        if (t.contains("Minute")) l.add(Unit.minute);
        if (t.contains("Meile")) l.add(Unit.mile);
        if (t.contains("Schritt")) l.add(Unit.meter);
        if (t.contains("Rechtschritt")) l.add(Unit.meters_square);
        if (t.contains("Monat")) l.add(Unit.month);
        // if (t.contains("")) l.add(Unit.once);
        if (t.contains("permanent")) l.add(Unit.permanent);
        if (t.contains("Person")) l.add(Unit.person);
        // if (t.contains("")) l.add(Unit.rs);
        // if (t.contains("")) l.add( Unit.size);
        if (t.contains("Sekunde")) l.add(Unit.second);
        if (t.contains("selbst") || t.contains("Selbst")) l.add(Unit.self);
        if (t.contains("Sicht")) l.add(Unit.sight);
        // if (t.contains("")) l.add(Unit.size_weapon);
        if (t.contains("aufrechterhalten")) l.add(Unit.sustained);
        // if (t.contains("")) l.add( Unit.target);
        // if (t.contains("")) l.add( Unit.target_asp);
        // if (t.contains("")) l.add( Unit.target_att_kl);
        if (t.contains("Berührung")) l.add(Unit.touch);
        //if (t.contains("")) l.add(Unit.turn);
        // if (t.contains("")) l.add( Unit.volume_m);
        if (t.contains("Woche")) l.add(Unit.week);
        if (t.contains("dere")) l.add(Unit.world);
        if (t.contains("Jahr")) l.add(Unit.year);
        // if (t.contains("")) l.add(Unit.zone);

        if (l.size() == 0 && !allowNoHit)
        {
            LOGGER.error("String (" + t + ") contains no implemented unit.");
        }
        return l.toArray(new Unit[l.size()]);
    }

    protected static TraditionKey extractTraditionFromText(String t)
    {
        if (t.contains("allgemein (Schamanenritus)")) return TraditionKey.shaman_all;
        if (t.contains("allgemein")) return TraditionKey.all;
        //if(t.contains("")) return TraditionKey.alchimist;
        if (t.contains("Angrosch")) return TraditionKey.angrosch;
        //if(t.contains("")) return TraditionKey.animist;
        if (t.contains("Aves")) return TraditionKey.aves;
        //if(t.contains("")) return TraditionKey.barde;
        if (t.contains("Boron")) return TraditionKey.boron;
        //if(t.contains("")) return TraditionKey.brobim_geode;
        if (t.contains("Kristallomant")) return TraditionKey.cristallomancer;
        //if(t.contains("")) return TraditionKey.dancer;
        if (t.contains("Druide")) return TraditionKey.druid;
        if (t.contains("Efferd")) return TraditionKey.efferd;
        if (t.contains("Elf")) return TraditionKey.elf;
        if (t.contains("Firun")) return TraditionKey.firun;
        if (t.contains("Geode")) return TraditionKey.geode;
        //if(t.contains("")) return TraditionKey.gifted;
        //if(t.contains("")) return TraditionKey.goblin;
        if (t.contains("Schelm")) return TraditionKey.harlequin;
        if (t.contains("Hesinde")) return TraditionKey.hesinde;
        if (t.contains("Ifirn")) return TraditionKey.ifirn;
        if (t.contains("Scharlatan")) return TraditionKey.illusionist;
        if (t.contains("Ingerimm")) return TraditionKey.ingerimm;
        //if(t.contains("")) return TraditionKey.intuitive;
        if (t.contains("Kor")) return TraditionKey.kor;
        if (t.contains("Levthan")) return TraditionKey.levthan;
        if (t.contains("Gildenmagier")) return TraditionKey.mage;
        if (t.contains("Marbo")) return TraditionKey.marbo;
        if (t.contains("Namenlos")) return TraditionKey.namenlos;
        if (t.contains("Nandus")) return TraditionKey.nandus;
        if (t.contains("Peraine")) return TraditionKey.peraine;
        if (t.contains("Phex")) return TraditionKey.phex;
        if (t.contains("Praios")) return TraditionKey.praios;
        if (t.contains("Qabalya")) return TraditionKey.qabalya;
        if (t.contains("Rahja")) return TraditionKey.rahja;
        if (t.contains("Rondra")) return TraditionKey.rondra;
        if (t.contains("Ferkina")) return TraditionKey.shaman_ferkina;
        if (t.contains("Fjarninger")) return TraditionKey.shaman_fjarninger;
        if (t.contains("Gjalsker")) return TraditionKey.shaman_gjalsker;
        if (t.contains("Moha")) return TraditionKey.shaman_moha;
        if (t.contains("Nivese")) return TraditionKey.shaman_nivese;
        if (t.contains("Trollzacker")) return TraditionKey.shaman_trollzacker;
        if (t.contains("Swafnir")) return TraditionKey.swafnir;
        if (t.contains("Travia")) return TraditionKey.travia;
        if (t.contains("Tsa")) return TraditionKey.tsa;
        if (t.contains("Hexe")) return TraditionKey.witch;
        if (t.contains("Zibilja")) return TraditionKey.zibilja;

        LOGGER.error("Tradition(" + t + ") konnte nicht interpretiert werden");
        return null;
    }

    protected static boolean isClerical(TopicEnum topic)
    {
        return topic == TopicEnum.BLESSINGS || topic == TopicEnum.LITURGIES || topic == TopicEnum.CEREMONIES;
    }

    public static MysticalSkillCategory retrieveCategory(TopicEnum topic)
    {
        return switch (topic)
            {
                case TRICKS -> MysticalSkillCategory.trick;
                case SPELLS -> MysticalSkillCategory.spell;
                case RITUALS -> MysticalSkillCategory.ritual;
                case BLESSINGS -> MysticalSkillCategory.blessing;
                case LITURGIES -> MysticalSkillCategory.liturgy;
                case CEREMONIES -> MysticalSkillCategory.ceremony;
                case CURSES -> MysticalSkillCategory.curse;
            };
    }

    protected static String getPrefix(MysticalSkillRaw msr)
    {
        return msr.publication + " - " + msr.name + ": ";
    }

}
