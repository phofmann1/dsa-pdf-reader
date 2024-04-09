package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicEnum;
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

  protected static int extractFirstNumberFromText(String t, String errorPrefix)
  {
    List<Integer> results = extractNumbersFromText(t);
    if (results.size() == 0)
    {
      LOGGER.debug(errorPrefix + "Der Text(" + t + ") enthält keine Zahl");
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

  protected static Unit extractUnitFromText(String t, String errorPrefix)
  {
    Unit[] returnValue = extractUnitsFromText(t, errorPrefix, false);
    if (returnValue.length == 0)
    {
      return null;
    }
    else if (returnValue.length > 1)
    {
      LOGGER.error("Unit (" + t + ") is not unambiguously.");
      return null;
    }
    else
    {
      return returnValue[0];
    }
  }

  protected static Unit[] extractUnitsFromText(String t, String errorPrefix, boolean isMandatory)
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
      LOGGER.error(errorPrefix + "String (" + t + ") contains no implemented unit.");
    }
    return l.toArray(new Unit[l.size()]);
  }

  public static TraditionKey extractTraditionKeyFromText(String t)
  {
    List<TraditionKey> traditionKeys = extractTraditionKeysFromText(t);
    if (traditionKeys.size() == 1)
    {
      return traditionKeys.get(0);
    }
    else
    {
      throw new IllegalArgumentException("Es konnte keine eindeutige Tradition im String (" + t + ") gefunden werden.");
    }
  }

  public static List<TraditionKey> extractTraditionKeysFromText(String t)
  {
    List<TraditionKey> returnValue = new ArrayList<>();
    if (t.contains("allgemein (Schamanenritus)")) returnValue.add(TraditionKey.SHAMAN_ALL);
    else if (t.contains("allgemein") | t.contains("Allgemein")) returnValue.add(TraditionKey.ALL);
    if (t.contains("Zauberalchimisten")) returnValue.add(TraditionKey.ALCHIMIST);
    if (t.contains("Angrosch")) returnValue.add(TraditionKey.ANGROSCH);
    //if(t.contains("")) returnValue.add(TraditionKey.ANIMIST);
    if (t.contains("Aves")) returnValue.add(TraditionKey.AVES);
    if (t.contains("Ceoladir") || t.contains("Derwisch") || t.contains("Sangara") || t.contains("Zauberbarden")) returnValue.add(TraditionKey.BARDE);
    if (t.contains("Boron")) returnValue.add(TraditionKey.BORON);
    //if(t.contains("")) returnValue.add(TraditionKey.BROBIM_GEODE);
    if (t.contains("Kristallomant")) returnValue.add(TraditionKey.CRISTALLOMANCER);
    if (t.contains("Majuna") || t.contains("Hazaqi") || t.contains("Rahkisa") || t.contains("Sharisad") || t.contains("Zaubertänzer"))
      returnValue.add(TraditionKey.DANCER);
    if (t.contains("Druide")) returnValue.add(TraditionKey.DRUID);
    if (t.contains("Efferd")) returnValue.add(TraditionKey.EFFERD);
    if (t.contains("Elf")) returnValue.add(TraditionKey.ELF);
    if (t.contains("Firun")) returnValue.add(TraditionKey.FIRUN);
    if (t.contains("Geode")) returnValue.add(TraditionKey.GEODE);
    if (t.contains("Meistertalentierte")) returnValue.add(TraditionKey.GIFTED);
    if (t.contains("Goblinzauberinnen")) returnValue.add(TraditionKey.GOBLIN);
    if (t.contains("Schelm")) returnValue.add(TraditionKey.HARLEQUIN);
    if (t.contains("Hesinde")) returnValue.add(TraditionKey.HESINDE);
    if (t.contains("Ifirn")) returnValue.add(TraditionKey.IFIRN);
    if (t.contains("Scharlatan")) returnValue.add(TraditionKey.ILLUSIONIST);
    if (t.contains("Ingerimm")) returnValue.add(TraditionKey.INGERIMM);
    if (t.contains("Intuitive Zauberer")) returnValue.add(TraditionKey.INTUITIVE);
    if (t.contains("Kor")) returnValue.add(TraditionKey.KOR);
    if (t.contains("Levthan")) returnValue.add(TraditionKey.LEVTHAN);
    if (t.contains("Gildenmagier")) returnValue.add(TraditionKey.MAGE);
    if (t.contains("Marbo")) returnValue.add(TraditionKey.MARBO);
    if (t.contains("Namenlos")) returnValue.add(TraditionKey.NAMENLOS);
    if (t.contains("Nandus")) returnValue.add(TraditionKey.NANDUS);
    if (t.contains("Peraine")) returnValue.add(TraditionKey.PERAINE);
    if (t.contains("Phex")) returnValue.add(TraditionKey.PHEX);
    if (t.contains("Praios")) returnValue.add(TraditionKey.PRAIOS);
    if (t.contains("Qabalya")) returnValue.add(TraditionKey.QABALYA);
    if (t.contains("Rahja")) returnValue.add(TraditionKey.RAHJA);
    if (t.contains("Rondra")) returnValue.add(TraditionKey.RONDRA);
    if (t.contains("Ferkina")) returnValue.add(TraditionKey.SHAMAN_FERKINA);
    if (t.contains("Fjarninger")) returnValue.add(TraditionKey.SHAMAN_FJARNINGER);
    if (t.contains("Gjalsker")) returnValue.add(TraditionKey.SHAMAN_GJALSKER);
    if (t.contains("Moha")) returnValue.add(TraditionKey.SHAMAN_MOHA);
    if (t.contains("Nivese")) returnValue.add(TraditionKey.SHAMAN_NIVESE);
    if (t.contains("Trollzacker")) returnValue.add(TraditionKey.SHAMAN_TROLLZACKER);
    if (t.contains("Swafnir")) returnValue.add(TraditionKey.SWAFNIR);
    if (t.contains("Travia")) returnValue.add(TraditionKey.TRAVIA);
    if (t.contains("Tsa")) returnValue.add(TraditionKey.TSA);
    if (t.contains("Hexe")) returnValue.add(TraditionKey.WITCH);
    if (t.contains("Zibilja")) returnValue.add(TraditionKey.ZIBILJA);
    if (t.contains("Borbaradianer")) returnValue.add(TraditionKey.BORBARAD);
    if (t.contains("Darna")) returnValue.add(TraditionKey.DARNA);
    if (t.contains("Runenschöpfer")) returnValue.add(TraditionKey.RUNECARVER);
    if (t.contains("Bannzeichner")) returnValue.add(TraditionKey.BAN_DRAFTSMAN);
    if (t.contains("eine Schamanentradition")) returnValue.add(TraditionKey.SHAMAN_ALL);
    if (t.contains("Chr’Ssir’Ssr")) returnValue.add(TraditionKey.CHR_SSIR_SSR);
    if (t.contains("Numinoru")) returnValue.add(TraditionKey.NUMINORU);
    return returnValue;
  }

  protected static boolean isClerical(TopicEnum topic)
  {
    return topic == TopicEnum.BLESSINGS || topic == TopicEnum.LITURGIES || topic == TopicEnum.CEREMONIES
        || topic == TopicEnum.BLESSING_DIVINARIUM || topic == TopicEnum.LITURGY_DIVINARIUM || topic == TopicEnum.CEREMONY_DIVINARIUM;
  }

  public static MysticalSkillCategory retrieveMsCategory(TopicEnum topic)
  {
    return switch (topic)
        {
          case TRICKS, TRICKS_GRIMORIUM -> MysticalSkillCategory.trick;
          case SPELLS, SPELLS_GRIMORIUM -> MysticalSkillCategory.spell;
          case RITUALS, RITUALS_GRIMORIUM -> MysticalSkillCategory.ritual;
          case BLESSINGS, BLESSING_DIVINARIUM -> MysticalSkillCategory.blessing;
          case LITURGIES, LITURGY_DIVINARIUM -> MysticalSkillCategory.liturgy;
          case CEREMONIES, CEREMONY_DIVINARIUM -> MysticalSkillCategory.ceremony;
          case CURSES -> MysticalSkillCategory.curse;
          case ELFENSONGS -> MysticalSkillCategory.elfensong;
          case MELODIES -> MysticalSkillCategory.melody;
          case DANCES -> MysticalSkillCategory.dance;
          default -> throw new IllegalArgumentException(topic + " not found");
        };
  }


  public static String extractKeyTextFromText(String txt)
  {
    return txt == null ? "" : extractKeyTextFromTextWithUmlauts(txt.toUpperCase()
        .replace("Ä", "AE")
        .replace("Ö", "OE")
        .replace("Ü", "UE")
        .replace("ß", "SS")
    ).trim()
        .replace(" ", "_")
        .replace("__", "_");
  }

  public static String extractKeyTextFromTextWithUmlauts(String txt)
  {
    return txt == null ? "" : (txt.toUpperCase()
        .replace("&", "UND")
        .replace("'", "_")
        .replace("!", "")
        .replace("(", "")
        .replace(")", "")
        .replace("/", " ")
        .replace("?", "")
        .replace("’", " ")
        .replace(",", " ")
        .replace(" ..", "")
        .replace(".", "")
        .replaceAll("\s+", " ")
        .replaceAll("\u00AD", " ")
        .replace("-", "_")
        .replace("–", "_")
        .replace("à", "A")
        .replace("ë".toUpperCase(), "E")
    ).trim()
        .replace(" ", "_")
        .replace("__", "_");
  }

  protected static String getPrefix(String publication, String name)
  {
    return publication + " - " + name + ": ";
  }

  protected static List<String> convertMatcherToListString(Matcher m)
  {
    return m.results()
        .map(MatchResult::group) // Convert MatchResult to string
        .filter(v -> !v.isEmpty())
        .collect(Collectors.toList());
  }

}
