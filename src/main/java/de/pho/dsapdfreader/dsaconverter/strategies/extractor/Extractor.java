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

  public static Unit[] extractUnitsFromText(String t, String errorPrefix, boolean isMandatory) {
    List<Unit> l = new ArrayList<>();
    if (t.contains("Aktion")) l.add(Unit.aktion);
    // if (t.contains("")) l.add(Unit.AREA_OF_EFFECT);
    if (t.contains("Kontinent")) l.add(Unit.kontinent);
    if (t.contains("Jahrhundert")) l.add(Unit.jahrhundert);
    if (t.contains("KR") || t.contains("Kampfrunde")) l.add(Unit.kampfrunde);
    if (t.contains("Doppelgänger")) l.add(Unit.doppelgaenger);
    if (t.contains("Tag")) l.add(Unit.tag);
    if (t.contains("Stunde")) l.add(Unit.stunde);
    if (t.contains("sofort")) l.add(Unit.sofort);
    if (t.contains("Stein")) l.add(Unit.kilogramm);
    if (t.contains("LeP")) l.add(Unit.lep);
    if (t.contains("Giftstufe")) l.add(Unit.krankheit_stufe);
    if (t.contains("Krankheitsstufe")) l.add(Unit.gift_stufe);
    if (t.contains("Minute")) l.add(Unit.minute);
    if (t.contains("Meile")) l.add(Unit.meile);
    if (t.contains("Schritt")) l.add(Unit.meter);
    if (t.contains("Rechtschritt")) l.add(Unit.quadratmeter);
    if (t.contains("Monat")) l.add(Unit.monat);
    // if (t.contains("")) l.add(Unit.ONCE);
    if (t.contains("permanent")) l.add(Unit.permanent);
    if (t.contains("Person")) l.add(Unit.person);
    // if (t.contains("")) l.add(Unit.RS);
    // if (t.contains("")) l.add( Unit.SIZE);
    if (t.contains("Sekunde")) l.add(Unit.sekunde);
    if (t.contains("selbst") || t.contains("Selbst")) l.add(Unit.selbst);
    if (t.contains("Sicht")) l.add(Unit.sicht);
    // if (t.contains("")) l.add(Unit.SIZE_WEAPON);
    if (t.contains("aufrechterhalten")) l.add(Unit.aufrechterhaltend);
    // if (t.contains("")) l.add( Unit.TARGET);
    // if (t.contains("")) l.add( Unit.TARGET_ASP);
    // if (t.contains("")) l.add( Unit.TARGET_ATT_KL);
    if (t.contains("Berührung")) l.add(Unit.beruehrung);
    //if (t.contains("")) l.add(Unit.TURN);
    // if (t.contains("")) l.add( Unit.VOLUME_M);
    if (t.contains("Woche")) l.add(Unit.woche);
    if (t.contains("Dere")) l.add(Unit.welt);
    if (t.contains("Jahr")) l.add(Unit.jahr);
    if (t.contains("Becherchen")) l.add(Unit.becherchen);
    if (t.contains("Becher")) l.add(Unit.becher);
    if (t.contains("Kelch")) l.add(Unit.kelch);
    if (t.contains("Humpen")) l.add(Unit.humpen);
    if (t.contains("Krug")) l.add(Unit.krug);
    // if (t.contains("")) l.add(Unit.ZONE);

    if (l.size() == 0 && isMandatory) {
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
    if (t.contains("allgemein (Schamanenritus)")) returnValue.add(TraditionKey.schamanen_allgemein);
    else if (t.contains("allgemein") | t.contains("Allgemein")) returnValue.add(TraditionKey.all);
    if (t.contains("Zauberalchimisten")) returnValue.add(TraditionKey.zauberalchimisten);
    if (t.contains("Angrosch")) returnValue.add(TraditionKey.angrosch);
    if (t.contains("Animist")) returnValue.add(TraditionKey.animist);
    if (t.contains("Aves")) returnValue.add(TraditionKey.aves);
    if (t.contains("Ceoladir") || t.contains("Derwisch") || t.contains("Sangara") || t.contains("Zauberbarden"))
      returnValue.add(TraditionKey.zauberbarde);
    if (t.contains("Boron")) returnValue.add(TraditionKey.boron);
    if (t.contains("Brobim")) returnValue.add(TraditionKey.brobim_geode);
    if (t.contains("Kristallomant")) returnValue.add(TraditionKey.kristallomanten);
    if (t.contains("Majuna") || t.contains("Hazaqi") || t.contains("Rahkisa") || t.contains("Sharisad") || t.contains("Zaubertänzer"))
      returnValue.add(TraditionKey.zaubertänzer);
    if (t.contains("Druide")) returnValue.add(TraditionKey.druiden);
    if (t.contains("Efferd")) returnValue.add(TraditionKey.efferd);
    if (t.contains("Elf")) returnValue.add(TraditionKey.elfen);
    if (t.contains("Firun")) returnValue.add(TraditionKey.firun);
    if (t.contains("Geode")) returnValue.add(TraditionKey.geode);
    if (t.contains("Meistertalentierte")) returnValue.add(TraditionKey.meisterhandwerker);
    if (t.contains("Goblinzauberinnen")) returnValue.add(TraditionKey.goblinzauberin);
    if (t.contains("Schelm")) returnValue.add(TraditionKey.schelme);
    if (t.contains("Hesinde")) returnValue.add(TraditionKey.hesinde);
    if (t.contains("Ifirn")) returnValue.add(TraditionKey.ifirn);
    if (t.contains("Scharlatan")) returnValue.add(TraditionKey.scharlatane);
    if (t.contains("Ingerimm")) returnValue.add(TraditionKey.ingerimm);
    if (t.contains("Intuitive Zauberer")) returnValue.add(TraditionKey.intuitive_zauberer);
    if (t.contains("Kor")) returnValue.add(TraditionKey.kor);
    if (t.contains("Levthan")) returnValue.add(TraditionKey.levthan);
    if (t.contains("Gildenmagier")) returnValue.add(TraditionKey.gildenmagier);
    if (t.contains("Marbo")) returnValue.add(TraditionKey.marbo);
    if (t.contains("Namenlos")) returnValue.add(TraditionKey.namenlos);
    if (t.contains("Nandus")) returnValue.add(TraditionKey.nandus);
    if (t.contains("Peraine")) returnValue.add(TraditionKey.peraine);
    if (t.contains("Phex")) returnValue.add(TraditionKey.phex);
    if (t.contains("Praios")) returnValue.add(TraditionKey.praios);
    if (t.contains("Qabalya")) returnValue.add(TraditionKey.qabalya);
    if (t.contains("Rahja")) returnValue.add(TraditionKey.rahja);
    if (t.contains("Rondra")) returnValue.add(TraditionKey.rondra);
    if (t.contains("Ferkina")) returnValue.add(TraditionKey.ferkinaschamane);
    if (t.contains("Fjarninger")) returnValue.add(TraditionKey.fjarningerschamane);
    if (t.contains("Gjalsker")) returnValue.add(TraditionKey.gjalskerschamane);
    if (t.contains("Moha") || t.contains("Tahaya")) returnValue.add(TraditionKey.tahayaschamane);
    if (t.contains("Nivese")) returnValue.add(TraditionKey.nivesenschamane);
    if (t.contains("Trollzacker")) returnValue.add(TraditionKey.trollzackerschamane);
    if (t.contains("Swafnir")) returnValue.add(TraditionKey.swafnir);
    if (t.contains("Travia")) returnValue.add(TraditionKey.travia);
    if (t.contains("Tsa")) returnValue.add(TraditionKey.tsa);
    if (t.contains("Hexe")) returnValue.add(TraditionKey.hexen);
    if (t.contains("Zibilja")) returnValue.add(TraditionKey.zibilja);
    if (t.contains("Borbaradianer")) returnValue.add(TraditionKey.borbarad);
    if (t.contains("Darna")) returnValue.add(TraditionKey.darna);
    if (t.contains("Runenschöpfer")) returnValue.add(TraditionKey.runenschöpfer);
    if (t.contains("Bannzeichner")) returnValue.add(TraditionKey.bannzeichner);
    if (t.contains("eine Schamanentradition")) returnValue.add(TraditionKey.schamanen_allgemein);
    if (t.contains("Chr’Ssir’Ssr")) returnValue.add(TraditionKey.chr_ssir_ssr_kult);
    if (t.contains("Numinoru")) returnValue.add(TraditionKey.numinoru);
    if (t.contains("Shinxir")) returnValue.add(TraditionKey.shinxir);
    if (t.contains("Szint")) returnValue.add(TraditionKey.h_szint_kult);
    if (t.contains("Tairach")) returnValue.add(TraditionKey.tairachschamane);
    if (t.contains("Zsahh")) returnValue.add(TraditionKey.zsahh);
    if (t.contains("Gravesh")) returnValue.add(TraditionKey.graveshkult);
    if (t.contains("Achazschamane")) returnValue.add(TraditionKey.achazschamane);
    if (t.contains("Nachtalben")) returnValue.add(TraditionKey.nachtalben);
    return returnValue;
  }

  protected static boolean isClerical(TopicEnum topic)
  {
    return topic == TopicEnum.BLESSING_DIVINARIUM || topic == TopicEnum.LITURGY_DIVINARIUM || topic == TopicEnum.CEREMONY_DIVINARIUM;
  }

  public static MysticalSkillCategory retrieveMsCategory(TopicEnum topic)
  {
    return switch (topic)
        {
          case TRICKS_GRIMORIUM -> MysticalSkillCategory.trick;
          case SPELLS_GRIMORIUM -> MysticalSkillCategory.spell;
          case RITUALS_GRIMORIUM -> MysticalSkillCategory.ritual;
          case BLESSING_DIVINARIUM -> MysticalSkillCategory.blessing;
          case LITURGY_DIVINARIUM -> MysticalSkillCategory.liturgy;
          case CEREMONY_DIVINARIUM -> MysticalSkillCategory.ceremony;
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
        .replaceAll("^DAS ", "")
        .replaceAll("^DER ", "")
        .replaceAll("^DIE ", "")
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
        .replaceAll("HEILKUNDE KRANKHEIT$", "HEILKUNDE KRANKHEITEN")
    ).trim()
        .replace(" ", "_")
        .replace("__", "_");
  }

  public static <T extends Enum<T>> T extractEnumKey(String name, Class<T> enumClass) {
    String enumKeyStr = Extractor.extractKeyTextFromText(name).toLowerCase();

    try {
      return Enum.valueOf(enumClass, enumKeyStr);
    }
    catch (IllegalArgumentException e) {
      System.out.println(enumKeyStr + ",");
      return null;
    }
  }

  protected static String getPrefix(String publication, String name) {
    return publication + " - " + name + ": ";
  }

  protected static List<String> convertMatcherToListString(Matcher m) {
    return m.results()
        .map(MatchResult::group) // Convert MatchResult to string
        .filter(v -> !v.isEmpty())
        .collect(Collectors.toList());
  }

}
