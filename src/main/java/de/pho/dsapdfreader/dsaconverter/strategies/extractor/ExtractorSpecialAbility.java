package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.pho.dsapdfreader.exporter.model.SkillApplication;
import de.pho.dsapdfreader.exporter.model.SpecialAbilityAdvancedSelection;
import de.pho.dsapdfreader.exporter.model.SpecialAbilityOption;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.WeaponKey;

public class ExtractorSpecialAbility extends Extractor
{

  public static final Pattern PAT_TALENT_MULTISELECT = Pattern.compile("bis zu drei .*alente aussuchen");
  public static final Pattern PAT_HAS_NEW_SKILL_USAGE = Pattern.compile("(erwirbt|erhält|bekommt|erlangt|gibt).*Anwendungsgebiet|Anwendungsgebiet.*erworben|schaltet.*Anwendungsgebiet.*frei");
  public static final Pattern PAT_EXTRACT_NEW_SKILL_USAGE = Pattern.compile("(?<=Anwendungsgebiet <i>)[A-zÄ-üß ()&]*(<\\/i> <i>)?[A-zÄ-üß ()&]*(?=<\\/i>)"); //die Trennung durch <i> ist z.B. im Anwendungsgebiet Instrumente bauen zu sehen
  public static final Pattern PAT_EXTRACT_SKILL = Pattern.compile("(?<=<i>)[A-zÄ-üß &-]*(?=<\\/i>)");

  private static final SpecialAbilityKey[] NEBENFACH = {
      SpecialAbilityKey.begnadeter_objektzauberer,
      SpecialAbilityKey.bewanderter_heilzauberer,
      SpecialAbilityKey.brillanter_telekinetiker,
      SpecialAbilityKey.erfahrener_antimagier,
      SpecialAbilityKey.hervorragender_illusionist,
      SpecialAbilityKey.kundiger_sphärologe,
      SpecialAbilityKey.matrixzauberei,
      SpecialAbilityKey.unübertroffener_verwandler,
      SpecialAbilityKey.vollkommener_beherrscher,
      SpecialAbilityKey.vortrefflicher_hellseher,
  };

  public static SpecialAbilityTypeKey retrieveType(String description)
  {
    if (description.contains("(passiv)")) return SpecialAbilityTypeKey.passive;
    if (description.contains("(Basismanöver)")) return SpecialAbilityTypeKey.basic;
    if (description.contains("(Spezialmanöver)")) return SpecialAbilityTypeKey.passive;
    if (description.contains("(aktiv)")) return SpecialAbilityTypeKey.active;
    return null;
  }


  public static SpecialAbilityKey retrieve(String name)
  {
    SpecialAbilityKey returnValue = null;
    try
    {
      returnValue = extractSpecialAbilityKeyFromText(name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e)
    {
      // String msg = String.format("%s key could not be interpreted.", name);
      //LOGGER.error(msg);
    }
    return returnValue;
  }

  private static SpecialAbilityKey extractSpecialAbilityKeyFromText(String name)
  {
    SpecialAbilityKey returnValue;
    String keyString = extractKeyTextFromTextWithUmlauts(name.replaceAll("ß", "xxx"))
        .toLowerCase()
        .replaceAll("xxx", "ß");
    keyString = keyString.trim();

    try
    {
      returnValue = SpecialAbilityKey.valueOf(keyString.toLowerCase());
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error("Invalid specialAbility name: " + name, e);
      returnValue = null;
    }
    return returnValue;
  }

  public static int retrieveMultiselect(String rules)
  {
    boolean hasThreeMultiselectTalents = PAT_TALENT_MULTISELECT.matcher(rules).find();
    boolean hasTwoSpells = rules.contains("Maximal können zwei Zauber mit dieser Sonderfertigkeit ausgestattet werden");
    return hasThreeMultiselectTalents ? 3 : (hasTwoSpells ? 2 : 1);
  }

  public static SpecialAbilityAdvancedSelection retrieveAdvancedAbilities(String advancedAbilities, List<CombatSkillKey> csk)
  {
    SpecialAbilityAdvancedSelection returnValue = null;
    if (advancedAbilities != null && !advancedAbilities.isEmpty())
    {
      returnValue = new SpecialAbilityAdvancedSelection();

      List<String> optionEntries = new ArrayList<>();

      Matcher m = Pattern.compile("eine weitere erweiterte.*|eine erweiterte.*|Zwei erweiterte.*<br>").matcher(advancedAbilities);
      while (m.find())
      {
        optionEntries.add(m.group());
      }

      for (String o : optionEntries)
      {
        int selectionCount = o.startsWith("Zwei") ? 2 : 1;
        if (o.contains("Nebenfach"))
        {
          SpecialAbilityOption sao = new SpecialAbilityOption();
          sao.nOf = selectionCount;
          sao.options = Arrays.stream(NEBENFACH).toList();
          returnValue.advancedAbilitiesOptions.add(sao);
        }
        else
        {
          List<SpecialAbilityKey> options = retrieveAdvancedAbilitiyKeys(o.trim().replaceAll("(Z|z)wei.*: ", "").replaceAll("(E|e)ine.*: ", ""), csk);
          if (options != null && options.size() > 0)
          {
            SpecialAbilityOption sao = new SpecialAbilityOption();
            sao.nOf = selectionCount;
            sao.options = options;
            returnValue.advancedAbilitiesOptions.add(sao);
          }
        }
        advancedAbilities = advancedAbilities.replace(o, "").trim();
      }
      returnValue.advancedAbilities = retrieveAdvancedAbilitiyKeys(advancedAbilities, csk).toArray(SpecialAbilityKey[]::new);
    }
    return returnValue;
  }

  public static List<SpecialAbilityKey> retrieveAdvancedAbilitiyKeys(String listOfAbilities, List<CombatSkillKey> csks)
  {
    String cleanList = cleanListOfAbilities(listOfAbilities);

    String[] splitAdvancedAbilities = cleanList.split(",|<br>");

    Stream<SpecialAbilityKey> r = (Arrays.stream(splitAdvancedAbilities)
        .filter(aaName -> !aaName.contains("oder"))
        .filter(aaName -> aaName != null && !aaName.trim().isEmpty())
        .map(aaName -> {
          List result = new ArrayList();
          String name = aaName
              .replace("Gesunder GeistXXX gesunder Körper", "Gesunder Geist, gesunder Körper")
              .replace("Vertrauen", "Vertrauenswürdig")
              .replace("Übertragung der Astralkraft", "Übertragung der Astralkräfte")
              .replace("Übertragung der Lebenskräfte", "Übertragung der Lebenskraft")
              .replace("Gebieter/in der Flammen", "Gebieter/in der Flamme")
              .replace("Fluchmeisterin", "Langer Fluch")
              .trim();
          if (name.startsWith("Gegnerische Zauberpraxis ("))
          {
            result.add(SpecialAbilityKey.gegnerische_zauberpraxis_eis);
            result.add(SpecialAbilityKey.gegnerische_zauberpraxis_luft);
            result.add(SpecialAbilityKey.gegnerische_zauberpraxis_wasser);
          }
          else if (name.startsWith("Nachladespezialist"))
          {
            if (csks != null)
            {
              for (CombatSkillKey csk : csks
              )
              {
                result.add(switch (csk)
                    {
                      case ARMBRÜSTE -> SpecialAbilityKey.nachladespezialist_armbrüste;
                      case BLASROHR -> SpecialAbilityKey.nachladespezialist_blasrohr;
                      case BÖGEN -> SpecialAbilityKey.nachladespezialist_bögen;
                      case DISKUS -> SpecialAbilityKey.nachladespezialist_diskus;
                      case SCHLEUDERN -> SpecialAbilityKey.nachladespezialist_schleudern;
                      case WURFWAFFEN -> SpecialAbilityKey.nachladespezialist_wurfwaffen;
                      default -> null;
                    });
              }
              result.removeIf(e -> e == null);
            }
            else
            {
              result.add(SpecialAbilityKey.nachladespezialist_armbrüste);
              result.add(SpecialAbilityKey.nachladespezialist_blasrohr);
              result.add(SpecialAbilityKey.nachladespezialist_bögen);
              result.add(SpecialAbilityKey.nachladespezialist_diskus);
              result.add(SpecialAbilityKey.nachladespezialist_schleudern);
              result.add(SpecialAbilityKey.nachladespezialist_wurfwaffen);
            }
          }
          else
          {
            result.add(ExtractorSpecialAbility.retrieve(name));
          }
          return result;
        })
        .filter(u -> u != null)
        .flatMap(Collection::stream));
    return r.collect(Collectors.toList());
  }

  private static String cleanListOfAbilities(String listOfAbilities)
  {
    Matcher m = Pattern.compile("(?<=\\().*,.*(?=\\))").matcher(listOfAbilities);
    String foundVal = null;
    while (m.find())
    {
      foundVal = m.group();
      listOfAbilities = listOfAbilities
          .replace("(" + foundVal + ")", "(" + foundVal.replace(",", ";") + ")")
          .replace("und", ";");
    }

    return listOfAbilities.trim()
        .replace("Gesunder Geist, Gesunder Körper", "Gesunder GeistXXX gesunder Körper")
        .replace("Gesunder Geist, gesunder Körper", "Gesunder GeistXXX gesunder Körper")
        .replace("Kälte Kältegewöhnung", "Kälte, Kältegewöhnung")
        .replace("Wissenstausch", "Wissensaustausch")
        .replace("Dä mo nenmeisterin", "Dämonenmeisterin")
        .replaceAll("(?<=[a-z])\u00AD(?=[a-z])", "");
  }

  public static List<CombatSkillKey> retrieveCombatSkillKeys(String combatSkillsText)
  {
    if (combatSkillsText == null || combatSkillsText.isEmpty()) return null;
    List<CombatSkillKey> returnValue = new ArrayList<>();
    if (combatSkillsText.startsWith("alle Zweihandwaffen"))
    {
      returnValue.add(CombatSkillKey.PIKE);
      returnValue.add(CombatSkillKey.POLE);
      returnValue.add(CombatSkillKey.TWOHANDED_BLUNT);
      returnValue.add(CombatSkillKey.TWOHANDED_SWORD);
    }
    else if (combatSkillsText.startsWith("alle Nahkampftechniken, die mit einhändigen Waffen"))
    {
      returnValue.add(CombatSkillKey.BLUNT);
      returnValue.add(CombatSkillKey.BRAWL);
      returnValue.add(CombatSkillKey.CHAIN);
      returnValue.add(CombatSkillKey.DAGGER);
      returnValue.add(CombatSkillKey.FAN);
      returnValue.add(CombatSkillKey.FENCING);
      returnValue.add(CombatSkillKey.SHIELD);
      returnValue.add(CombatSkillKey.SWORD);
      returnValue.add(CombatSkillKey.WHIP);
    }
    else if (combatSkillsText.startsWith("alle Nahkampf mit Parade") || combatSkillsText.startsWith("alle Nahkampfkampftechniken, die über einen PA-Wert verfügen"))
    {
      returnValue.add(CombatSkillKey.BLUNT);
      returnValue.add(CombatSkillKey.BRAWL);
      returnValue.add(CombatSkillKey.CHAIN);
      returnValue.add(CombatSkillKey.DAGGER);
      returnValue.add(CombatSkillKey.FAN);
      returnValue.add(CombatSkillKey.FENCING);
      returnValue.add(CombatSkillKey.POLE);
      returnValue.add(CombatSkillKey.SHIELD);
      returnValue.add(CombatSkillKey.SWORD);
      returnValue.add(CombatSkillKey.TWOHANDED_SWORD);
      returnValue.add(CombatSkillKey.TWOHANDED_BLUNT);
    }
    else if (combatSkillsText.startsWith("alle Nahkampf"))
    {
      returnValue.add(CombatSkillKey.BLUNT);
      returnValue.add(CombatSkillKey.BRAWL);
      returnValue.add(CombatSkillKey.CHAIN);
      returnValue.add(CombatSkillKey.DAGGER);
      returnValue.add(CombatSkillKey.FAN);
      returnValue.add(CombatSkillKey.FENCING);
      returnValue.add(CombatSkillKey.LANCE);
      returnValue.add(CombatSkillKey.PIKE);
      returnValue.add(CombatSkillKey.POLE);
      returnValue.add(CombatSkillKey.SHIELD);
      returnValue.add(CombatSkillKey.SWORD);
      returnValue.add(CombatSkillKey.TWOHANDED_SWORD);
      returnValue.add(CombatSkillKey.TWOHANDED_BLUNT);
      returnValue.add(CombatSkillKey.WHIP);
    }
    else if (combatSkillsText.startsWith("alle Fernkampf"))
    {
      returnValue.add(CombatSkillKey.ARMBRÜSTE);
      returnValue.add(CombatSkillKey.BLASROHR);
      returnValue.add(CombatSkillKey.BÖGEN);
      returnValue.add(CombatSkillKey.DISKUS);
      returnValue.add(CombatSkillKey.SPITFIRE);
      returnValue.add(CombatSkillKey.SCHLEUDERN);
      returnValue.add(CombatSkillKey.WURFWAFFEN);
    }
    else if (combatSkillsText.startsWith("alle") || combatSkillsText.startsWith("–"))
    {
      returnValue.add(CombatSkillKey.BLUNT);
      returnValue.add(CombatSkillKey.BRAWL);
      returnValue.add(CombatSkillKey.CHAIN);
      returnValue.add(CombatSkillKey.DAGGER);
      returnValue.add(CombatSkillKey.FAN);
      returnValue.add(CombatSkillKey.FENCING);
      returnValue.add(CombatSkillKey.LANCE);
      returnValue.add(CombatSkillKey.PIKE);
      returnValue.add(CombatSkillKey.POLE);
      returnValue.add(CombatSkillKey.SHIELD);
      returnValue.add(CombatSkillKey.SWORD);
      returnValue.add(CombatSkillKey.TWOHANDED_SWORD);
      returnValue.add(CombatSkillKey.TWOHANDED_BLUNT);
      returnValue.add(CombatSkillKey.WHIP);

      returnValue.add(CombatSkillKey.ARMBRÜSTE);
      returnValue.add(CombatSkillKey.BLASROHR);
      returnValue.add(CombatSkillKey.BÖGEN);
      returnValue.add(CombatSkillKey.DISKUS);
      returnValue.add(CombatSkillKey.SPITFIRE);
      returnValue.add(CombatSkillKey.SCHLEUDERN);
      returnValue.add(CombatSkillKey.WURFWAFFEN);
    }
    else
    {
      if (combatSkillsText.contains("Hiebwaffen"))
      {
        returnValue.add(CombatSkillKey.BLUNT);
      }
      if (combatSkillsText.contains("Raufen"))
      {
        returnValue.add(CombatSkillKey.BRAWL);
      }
      if (combatSkillsText.contains("Kettenwaffen"))
      {
        returnValue.add(CombatSkillKey.CHAIN);
      }
      if (combatSkillsText.contains("Dolche"))
      {
        returnValue.add(CombatSkillKey.DAGGER);
      }
      if (combatSkillsText.contains("Fächer"))
      {
        returnValue.add(CombatSkillKey.FAN);
      }
      if (combatSkillsText.contains("Fechtwaffen"))
      {
        returnValue.add(CombatSkillKey.FENCING);
      }
      if (combatSkillsText.contains("Lanzen"))
      {
        returnValue.add(CombatSkillKey.LANCE);
      }
      if (combatSkillsText.contains("Spießwaffen"))
      {
        returnValue.add(CombatSkillKey.PIKE);
      }
      if (combatSkillsText.contains("Stangenwaffen"))
      {
        returnValue.add(CombatSkillKey.POLE);
      }
      if (combatSkillsText.contains("Schilde"))
      {
        returnValue.add(CombatSkillKey.SHIELD);
      }
      if (combatSkillsText.contains("Schwerter"))
      {
        returnValue.add(CombatSkillKey.SWORD);
      }
      if (combatSkillsText.contains("Zweihandhiebwaffen"))
      {
        returnValue.add(CombatSkillKey.TWOHANDED_BLUNT);
      }
      if (combatSkillsText.contains("Zweihandschwerter"))
      {
        returnValue.add(CombatSkillKey.TWOHANDED_SWORD);
      }
      if (combatSkillsText.contains("Peitschen"))
      {
        returnValue.add(CombatSkillKey.WHIP);
      }
      if (combatSkillsText.contains("Armbrüste"))
      {
        returnValue.add(CombatSkillKey.ARMBRÜSTE);
      }
      if (combatSkillsText.contains("Blasrohr"))
      {
        returnValue.add(CombatSkillKey.BLASROHR);
      }
      if (combatSkillsText.contains("Bögen"))
      {
        returnValue.add(CombatSkillKey.BÖGEN);
      }
      if (combatSkillsText.contains("Diskus"))
      {
        returnValue.add(CombatSkillKey.DISKUS);
      }
      if (combatSkillsText.contains("Schleudern"))
      {
        returnValue.add(CombatSkillKey.SCHLEUDERN);
      }
      if (combatSkillsText.contains("Wurfwaffen"))
      {
        returnValue.add(CombatSkillKey.WURFWAFFEN);
      }
    }
    return returnValue;
  }

  public static SkillApplication retrieveSkillUsage(String rules)
  {
    SkillApplication returnValue = null;
    rules = rules
        .replace("<i>Anwendungsgebiet Gildenrecht</i>", "Anwendungsgebiet <i>Gildenrecht</i>")
        .replace("Ölgemälde malen", "<i>Ölgemälde malen</i>")
        .replace("Prunkkleider herstellen", "<i>Prunkkleider herstellen</i>");
    Matcher m = PAT_HAS_NEW_SKILL_USAGE.matcher(rules);

    if (m.find())
    {
      returnValue = new SkillApplication();
      String skillUsageText = null;
      m = PAT_EXTRACT_NEW_SKILL_USAGE.matcher(rules);
      if (m.find())
      {
        skillUsageText = m.group();
        returnValue.name = skillUsageText
            .replace("<i>", "")
            .replace("</i>", "")
            .replace("Magiespür", "Magiegespür");
        returnValue.usageKey = ExtractorSkillKey.retrieveSkillUsageKey(returnValue.name);
      }

      returnValue.skillKeys = etractSkillKeys(rules.replace("<i>" + skillUsageText + "</i>", ""));

    }

    return returnValue;
  }

  private static List<SkillKey> etractSkillKeys(String rules)
  {
    List<SkillKey> returnValue = new ArrayList<>();
    Matcher m = PAT_EXTRACT_SKILL.matcher(rules
        .replace("Brennend", "")
        .replace("<i>-</i>", "")
        .replace("<i>tung</i>", ""));

    while (m.find())
    {
      String skillString = m.group();
      skillString = (skillString.equalsIgnoreCase("Holz-") | skillString.equalsIgnoreCase("Holz"))
          ? "Holzbearbeitung"
          : skillString;
      skillString = (skillString.equalsIgnoreCase("Metall-") | skillString.equalsIgnoreCase("Metall"))
          ? "Metallbearbeitung"
          : skillString;
      skillString = (skillString.equalsIgnoreCase("Steinbearbei"))
          ? "Steinbearbeitung"
          : skillString;
      skillString = (skillString.equalsIgnoreCase("Malen & Zeichen"))
          ? "Malen & Zeichnen"
          : skillString;

      if (!skillString.isEmpty())
        returnValue.add(ExtractorSkillKey.retrieveSkillKey(skillString));
    }

    return returnValue;
  }

  public List<WeaponKey> retrieveAllowedWeaponKeys(String combatSkillsText)
  {
    List<WeaponKey> returnValue = new ArrayList<>();
    if (combatSkillsText.startsWith("alle Parierwaffen"))
    {
    }
    else if (combatSkillsText.startsWith("alle Elfenwaffen") || combatSkillsText.startsWith("alle (solange Elfenwaffen)"))
    {
    }
    else if (combatSkillsText.startsWith("alle (solange Zwergenwaffen)"))
    {

    }

    return returnValue;
  }
}
