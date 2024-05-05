package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;

public class ExtractorCombatSkillKeys extends Extractor
{
  public static final List<CombatSkillKey> CSS_ALL_TWO_HANDED = Arrays.asList(
      CombatSkillKey.spießwaffen,
      CombatSkillKey.stangenwaffen,
      CombatSkillKey.zweihandhiebwaffen,
      CombatSkillKey.zweihandschwerter);

  public static final List<CombatSkillKey> CSS_ALL_ONE_HANDED = Arrays.asList(
      CombatSkillKey.hiebwaffen,
      CombatSkillKey.raufen,
      CombatSkillKey.kettenwaffen,
      CombatSkillKey.dolche,
      CombatSkillKey.fächer,
      CombatSkillKey.fechtwaffen,
      CombatSkillKey.schilde,
      CombatSkillKey.schwerter,
      CombatSkillKey.peitschen
  );

  public static final List<CombatSkillKey> CSS_ALL_WITH_PARRY = Arrays.asList(
      CombatSkillKey.hiebwaffen,
      CombatSkillKey.raufen,
      CombatSkillKey.kettenwaffen,
      CombatSkillKey.dolche,
      CombatSkillKey.fächer,
      CombatSkillKey.fechtwaffen,
      CombatSkillKey.stangenwaffen,
      CombatSkillKey.schilde,
      CombatSkillKey.schwerter,
      CombatSkillKey.zweihandschwerter,
      CombatSkillKey.zweihandhiebwaffen
  );
  public static final List<CombatSkillKey> CSS_ALL_MELEE = Arrays.asList(
      CombatSkillKey.hiebwaffen,
      CombatSkillKey.raufen,
      CombatSkillKey.kettenwaffen,
      CombatSkillKey.dolche,
      CombatSkillKey.fächer,
      CombatSkillKey.fechtwaffen,
      CombatSkillKey.lanzen,
      CombatSkillKey.spießwaffen,
      CombatSkillKey.stangenwaffen,
      CombatSkillKey.schilde,
      CombatSkillKey.schwerter,
      CombatSkillKey.zweihandschwerter,
      CombatSkillKey.zweihandhiebwaffen,
      CombatSkillKey.peitschen
  );
  public static final List<CombatSkillKey> CSS_ALL_RANGED = Arrays.asList(
      CombatSkillKey.armbrüste,
      CombatSkillKey.blasrohre,
      CombatSkillKey.bögen,
      CombatSkillKey.diskusse,
      CombatSkillKey.feuerspeien,
      CombatSkillKey.schleudern,
      CombatSkillKey.wurfwaffen
  );
  public static final List<CombatSkillKey> CSS_ALL = Arrays.asList(
      CombatSkillKey.hiebwaffen,
      CombatSkillKey.raufen,
      CombatSkillKey.kettenwaffen,
      CombatSkillKey.dolche,
      CombatSkillKey.fächer,
      CombatSkillKey.fechtwaffen,
      CombatSkillKey.lanzen,
      CombatSkillKey.spießwaffen,
      CombatSkillKey.stangenwaffen,
      CombatSkillKey.schilde,
      CombatSkillKey.schwerter,
      CombatSkillKey.zweihandschwerter,
      CombatSkillKey.zweihandhiebwaffen,
      CombatSkillKey.peitschen,
      CombatSkillKey.armbrüste,
      CombatSkillKey.blasrohre,
      CombatSkillKey.bögen,
      CombatSkillKey.diskusse,
      CombatSkillKey.feuerspeien,
      CombatSkillKey.schleudern,
      CombatSkillKey.wurfwaffen
  );

  public static CombatSkillKey retrieveFromName(String name) {
    CombatSkillKey returnValue;
    String skillKeyString = extractKeyTextFromTextWithUmlauts(name).toLowerCase();

    try {
      returnValue = CombatSkillKey.valueOf(skillKeyString);
    }
    catch (NullPointerException e) {
      returnValue = null;
      //System.out.println(skillKeyString + " --> ");
      //LOGGER.error("Invalid CombatSkill name: " + skillKeyString);
    }
    return returnValue;
  }

  public static List<CombatSkillKey> retrieveFromCombatSkillsText(String combatSkillsText) {
    if (combatSkillsText == null || combatSkillsText.isEmpty()) return null;
    List<CombatSkillKey> returnValue = new ArrayList<>();
    if (combatSkillsText.startsWith("alle Zweihandwaffen")) {
      return CSS_ALL_TWO_HANDED;
    }
    else if (combatSkillsText.startsWith("alle Nahkampftechniken, die mit einhändigen Waffen")) {
      return CSS_ALL_ONE_HANDED;
    }
    else if (combatSkillsText.startsWith("alle Nahkampf mit Parade") || combatSkillsText.startsWith("alle Nahkampfkampftechniken, die über einen PA-Wert verfügen")) {
      return CSS_ALL_WITH_PARRY;
    }
    else if (combatSkillsText.startsWith("alle Nahkampf")) {
      return CSS_ALL_MELEE;
    }
    else if (combatSkillsText.startsWith("alle Fernkampf"))
    {
      return CSS_ALL_RANGED;
    }
    else if (combatSkillsText.startsWith("alle") || combatSkillsText.startsWith("–"))
    {
      return CSS_ALL;
    }
    else
    {
      if (combatSkillsText.contains("Hiebwaffen"))
      {
        returnValue.add(CombatSkillKey.hiebwaffen);
      }
      if (combatSkillsText.contains("Raufen"))
      {
        returnValue.add(CombatSkillKey.raufen);
      }
      if (combatSkillsText.contains("Kettenwaffen"))
      {
        returnValue.add(CombatSkillKey.kettenwaffen);
      }
      if (combatSkillsText.contains("Dolche"))
      {
        returnValue.add(CombatSkillKey.dolche);
      }
      if (combatSkillsText.contains("Fächer"))
      {
        returnValue.add(CombatSkillKey.fächer);
      }
      if (combatSkillsText.contains("Fechtwaffen"))
      {
        returnValue.add(CombatSkillKey.fechtwaffen);
      }
      if (combatSkillsText.contains("Lanzen"))
      {
        returnValue.add(CombatSkillKey.lanzen);
      }
      if (combatSkillsText.contains("Spießwaffen"))
      {
        returnValue.add(CombatSkillKey.spießwaffen);
      }
      if (combatSkillsText.contains("Stangenwaffen"))
      {
        returnValue.add(CombatSkillKey.stangenwaffen);
      }
      if (combatSkillsText.contains("Schilde"))
      {
        returnValue.add(CombatSkillKey.schilde);
      }
      if (combatSkillsText.contains("Schwerter"))
      {
        returnValue.add(CombatSkillKey.schwerter);
      }
      if (combatSkillsText.contains("Zweihandhiebwaffen"))
      {
        returnValue.add(CombatSkillKey.zweihandhiebwaffen);
      }
      if (combatSkillsText.contains("Zweihandschwerter"))
      {
        returnValue.add(CombatSkillKey.zweihandschwerter);
      }
      if (combatSkillsText.contains("Peitschen"))
      {
        returnValue.add(CombatSkillKey.peitschen);
      }
      if (combatSkillsText.contains("Armbrüste"))
      {
        returnValue.add(CombatSkillKey.armbrüste);
      }
      if (combatSkillsText.contains("Blasrohr"))
      {
        returnValue.add(CombatSkillKey.blasrohre);
      }
      if (combatSkillsText.contains("Bögen"))
      {
        returnValue.add(CombatSkillKey.bögen);
      }
      if (combatSkillsText.contains("Diskus")) {
        returnValue.add(CombatSkillKey.diskusse);
      }
      if (combatSkillsText.contains("Schleudern")) {
        returnValue.add(CombatSkillKey.schleudern);
      }
      if (combatSkillsText.contains("Wurfwaffen")) {
        returnValue.add(CombatSkillKey.wurfwaffen);
      }
    }
    return returnValue;
  }

  public static boolean isCombatSkillKey(String skillName) {
    String enumName = extractKeyTextFromTextWithUmlauts(skillName).toLowerCase();

    try {
      CombatSkillKey.valueOf(enumName);
      return true;
    }
    catch (IllegalArgumentException e) {
      return false;
    }
  }

}
