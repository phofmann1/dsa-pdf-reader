package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Triplet;

import de.pho.dsapdfreader.exporter.model.RequirementAttribute;
import de.pho.dsapdfreader.exporter.model.RequirementBoon;
import de.pho.dsapdfreader.exporter.model.RequirementCombatSkill;
import de.pho.dsapdfreader.exporter.model.RequirementMysticalSkill;
import de.pho.dsapdfreader.exporter.model.RequirementSkill;
import de.pho.dsapdfreader.exporter.model.RequirementSkillSum;
import de.pho.dsapdfreader.exporter.model.RequirementSpecialAbility;
import de.pho.dsapdfreader.exporter.model.RequirementsAttribute;
import de.pho.dsapdfreader.exporter.model.RequirementsCombatSkill;
import de.pho.dsapdfreader.exporter.model.RequirementsSkill;
import de.pho.dsapdfreader.exporter.model.RequirementsSpecialAbility;
import de.pho.dsapdfreader.exporter.model.SkillUsage;
import de.pho.dsapdfreader.exporter.model.SpecialAbilityAdvancedSelection;
import de.pho.dsapdfreader.exporter.model.SpecialAbilityOption;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.LogicalOperatorKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;
import de.pho.dsapdfreader.tools.roman.RomanNumberHelper;

public class ExtractorSpecialAbility extends Extractor
{

  public static final Pattern PAT_TALENT_MULTISELECT = Pattern.compile("bis zu drei .*alente aussuchen");
  public static final Pattern PAT_HAS_NEW_SKILL_USAGE = Pattern.compile("(erwirbt|erhält|bekommt|erlangt|gibt).*Anwendungsgebiet|Anwendungsgebiet.*erworben|schaltet.*Anwendungsgebiet.*frei");
  public static final Pattern PAT_EXTRACT_NEW_SKILL_USAGE = Pattern.compile("(?<=Anwendungsgebiet <i>)[A-zÄ-üß ()&]*(<\\/i> <i>)?[A-zÄ-üß ()&]*(?=<\\/i>)"); //die Trennung durch <i> ist z.B. im Anwendungsgebiet Instrumente bauen zu sehen
  public static final Pattern PAT_EXTRACT_SKILL = Pattern.compile("(?<=<i>)[A-zÄ-üß &-]*(?=<\\/i>)");
  private static final Pattern PAT_EXTRACT_SPECIE = Pattern.compile("(?<=Spezies )\\w*");
  private static final Pattern PAT_EXTRACT_TRADITION = Pattern.compile("(?<!(keine )Sonderfertigkeit Tradition \\()(?<=Tradition \\()[^\\)]*");

  private static final Pattern PAT_EXTRACT_NONE_OF_BOONS = Pattern.compile("(?<=kein Nachteil )[\\w Ä-ü\\(\\).\\/]*");
  private static final Pattern PAT_EXTRACT_ATTRIBUTES = Pattern.compile("(([MUKLINCHFGE]{2}|Leiteigenschaft( der Tradition)?) \\d{2}( oder )?){1,2}");

  private static final Pattern PAT_EXTRACT_SKILL_REQ = Pattern.compile("([A-ZÄÖÜ]?[a-ü& -])+(?= \\d\\d?)");
  private static final Pattern PAT_EXTRACT_MYSTICAL_SKILL_REQ = Pattern.compile("(?<=Zauber )[A-Z -]{3,}\\d?\\d?");


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
  private static final String GESUNDER_GEIST_REPLACEMENT = "Gesunder GeistXXX gesunder Körper";

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
    String keyString = extractKeyTextFromTextWithUmlauts(name.replace("ß", "xxx"))
        .toLowerCase()
        .replace("xxx", "ß");
    keyString = keyString.trim();

    try
    {
      returnValue = SpecialAbilityKey.valueOf(keyString.toLowerCase());
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error("Invalid specialAbility name: " + name);
      returnValue = null;
    }
    return returnValue;
  }

  public static int retrieveMultiselect(String rules)
  {
    boolean hasThreeMultiselectTalents = PAT_TALENT_MULTISELECT.matcher(rules).find();
    boolean hasTwoSpells = rules.contains("Maximal können zwei Zauber mit dieser Sonderfertigkeit ausgestattet werden");
    int noOfSpellsFor2 = hasTwoSpells ? 2 : 1;
    return hasThreeMultiselectTalents ? 3 : noOfSpellsFor2;
  }

  public static SpecialAbilityAdvancedSelection retrieveAdvancedAbilities(String advancedAbilities, List<CombatSkillKey> csk)
  {
    SpecialAbilityAdvancedSelection returnValue = null;
    if (advancedAbilities != null && !advancedAbilities.isEmpty())
    {
      returnValue = new SpecialAbilityAdvancedSelection();

      List<String> optionEntries = retrieveAdvancedAbilitiesOptionEntries(advancedAbilities);


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
          List<SpecialAbilityKey> options = retrieveAdvancedAbilitiyKeys(o.trim().replaceAll("[Zz]wei.*: ", "").replaceAll("[Ee]ine.*: ", ""), csk);
          if (options != null && options.isEmpty())
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


  private static List<String> retrieveAdvancedAbilitiesOptionEntries(String advancedAbilities)
  {
    List<String> returnValue = new ArrayList<>();
    Matcher m = Pattern.compile("eine weitere erweiterte.*|eine erweiterte.*|Zwei erweiterte.*<br>").matcher(advancedAbilities);
    while (m.find())
    {
      returnValue.add(m.group());
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
              .replace(GESUNDER_GEIST_REPLACEMENT, "Gesunder Geist, gesunder Körper")
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
                      case armbrüste -> SpecialAbilityKey.nachladespezialist_armbrüste;
                      case blasrohre -> SpecialAbilityKey.nachladespezialist_blasrohre;
                      case bögen -> SpecialAbilityKey.nachladespezialist_bögen;
                      case diskusse -> SpecialAbilityKey.nachladespezialist_diskus;
                      case schleudern -> SpecialAbilityKey.nachladespezialist_schleudern;
                      case wurfwaffen -> SpecialAbilityKey.nachladespezialist_wurfwaffen;
                      default -> null;
                    });
              }
              result.removeIf(Objects::isNull);
            }
            else
            {
              result.add(SpecialAbilityKey.nachladespezialist_armbrüste);
              result.add(SpecialAbilityKey.nachladespezialist_blasrohre);
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
        .filter(Objects::nonNull)
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
        .replace("Gesunder Geist, Gesunder Körper", GESUNDER_GEIST_REPLACEMENT)
        .replace("Gesunder Geist, gesunder Körper", GESUNDER_GEIST_REPLACEMENT)
        .replace("Kälte Kältegewöhnung", "Kälte, Kältegewöhnung")
        .replace("Wissenstausch", "Wissensaustausch")
        .replace("Dä mo nenmeisterin", "Dämonenmeisterin")
        .replaceAll("(?<=[a-z])\u00AD(?=[a-z])", "");
  }

  public static SkillUsage retrieveSkillUsage(String rules)
  {
    SkillUsage returnValue = null;
    rules = rules
        .replace("<i>Anwendungsgebiet Gildenrecht</i>", "Anwendungsgebiet <i>Gildenrecht</i>")
        .replace("Ölgemälde malen", "<i>Ölgemälde malen</i>")
        .replace("Prunkkleider herstellen", "<i>Prunkkleider herstellen</i>");
    Matcher m = PAT_HAS_NEW_SKILL_USAGE.matcher(rules);

    if (m.find())
    {
      returnValue = new SkillUsage();
      String skillUsageText = null;
      m = PAT_EXTRACT_NEW_SKILL_USAGE.matcher(rules);
      if (m.find())
      {
        skillUsageText = m.group();
        returnValue.name = skillUsageText
            .replace("<i>", "")
            .replace("</i>", "")
            .replace("Magiespür", "Magiegespür");
        returnValue.key = ExtractorSkillKey.retrieveSkillUsageKey(returnValue.name);
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
      skillString = (skillString.equalsIgnoreCase("Holz-") || skillString.equalsIgnoreCase("Holz"))
          ? "Holzbearbeitung"
          : skillString;
      skillString = (skillString.equalsIgnoreCase("Metall-") || skillString.equalsIgnoreCase("Metall"))
          ? "Metallbearbeitung"
          : skillString;
      skillString = (skillString.equalsIgnoreCase("Steinbearbei"))
          ? "Steinbearbeitung"
          : skillString;
      skillString = (skillString.equalsIgnoreCase("Malen & Zeichen"))
          ? "Malen & Zeichnen"
          : skillString;

      //Fehlerkorrektur Kryptographie (da ist viel kursiv und wird falsch erkannt
      skillString = skillString.replace("Kryptographie", "")
          .replace("Einfache", "")
          .replace("Optionale Regel", "")
          .replace("Primitive", "");

      if (!skillString.isEmpty())
      {
        Optional<SkillKey> sko = SkillKey.fromString(skillString);
        if (sko.isPresent())
        {
          returnValue.add(sko.get());
        }
        else
        {
          LOGGER.error("Kein gültiger ENUM SkillKey: " + skillString);
        }
      }

    }

    return returnValue;
  }

  public static SpecieKey retrieveRequiredSpecie(String preconditions)
  {
    Matcher m = PAT_EXTRACT_SPECIE.matcher(preconditions);
    if (m.find())
    {
      return switch (m.group())
          {
            case "Zwerg" -> SpecieKey.DWARF;
            case "Elf" -> SpecieKey.ELF;
            default -> null;
          };
    }
    else
    {
      return null;
    }
  }

  public static List<TraditionKey> retrieveRequiredTradition(String preconditions, String name)
  {

    Matcher m = PAT_EXTRACT_TRADITION.matcher(preconditions);
    if (m.find())
    {
      String traditionsText = m.group();
      List<String> traditionTextList = Arrays.stream(traditionsText.split(",|oder")).toList();
      return traditionTextList.stream().map(tt -> extractTraditionKeyFromText(tt.replace("\u00AD", ""))).collect(Collectors.toList());
    }
    else
    {
      return new ArrayList<>();
    }
  }

  public static List<RequirementBoon> retrieveRequiredOneOfBoons(SpecialAbilityCategoryKey category)
  {
    List<RequirementBoon> returnValue = new ArrayList<>();
    switch (category)
    {
    case cleric, cleric_advanced, cleric_stile -> returnValue.add(new RequirementBoon(BoonKey.geweihter, true));
    case magic, magic_advanced, magic_stile, magic_signs -> returnValue.add(new RequirementBoon(BoonKey.zauberer, true));
    default -> returnValue = new ArrayList<>();
    }
    return returnValue;


  }

  public static List<RequirementBoon> retrieveRequiredNoneOfBoons(String preconditions, SpecialAbilityKey key)
  {
    List<RequirementBoon> returnValue = new ArrayList<>();
    if (key == SpecialAbilityKey.vertrautenbindung)
    {
      returnValue.add(new RequirementBoon(BoonKey.kein_vertrauter, false));
    }
    else if (key == SpecialAbilityKey.flugsalbe)
    {
      returnValue.add(new RequirementBoon(BoonKey.keine_flugsalbe, false));
    }
    else
    {
      Matcher m = PAT_EXTRACT_NONE_OF_BOONS.matcher(preconditions);
      if (m.find())
      {
        String boonsText = m.group();
        List<String> boonTextList = List.of(boonsText.split(",|oder|\\/"));
        returnValue.addAll(boonTextList.stream().map(bt -> switch (bt.trim())
                {
                  case "Blind" -> new RequirementBoon(BoonKey.blind, false);
                  case "Angst vor ..." -> new RequirementBoon(BoonKey.angst_vor_x, false);
                  case "Angst vor Blut" -> new RequirementBoon(BoonKey.angst_vor_x, false, "Blut");
                  case "Behäbig" -> new RequirementBoon(BoonKey.behäbig, false);
                  case "Unfrei" -> new RequirementBoon(BoonKey.unfrei, false);
                  case "Eingeschränkter Sinn (Tastsinn) (je nach Form des Leggaleg)" ->
                      new RequirementBoon(BoonKey.eingeschränkter_sinn, false, "Tastsinn");
                  case "Verstümmelt (Einäugig)" -> new RequirementBoon(BoonKey.verstümmelt, false, "Einäugig");
                  default -> null;
                }).filter(Objects::nonNull)
            .collect(Collectors.toList()));
      }
    }
    return returnValue;
  }

  public static RequirementsAttribute retrieveRequirementAttribute(String preconditions, int levels, int currentLevel, SpecialAbilityCategoryKey sack)
  {
    RequirementsAttribute returnValue = null;

    final String leiteigenschaftText = retrieveLeAttributeShort(sack);
    Matcher m = PAT_EXTRACT_ATTRIBUTES.matcher(preconditions);
    if (m.find())
    {
      String requirementsString = (levels > 1) ? extractReqStringForLevel(preconditions, currentLevel) : preconditions;

      m = PAT_EXTRACT_ATTRIBUTES.matcher(requirementsString);
      returnValue = new RequirementsAttribute();
      while (m.find())
      {
        String attributeText = m.group();
        returnValue.logicalOpperator = LogicalOperatorKey.and;
        if (attributeText.contains("oder"))
        {
          RequirementsAttribute ras = new RequirementsAttribute();
          ras.logicalOpperator = LogicalOperatorKey.or;
          List<String> attributeTextList = List.of(attributeText.split(" oder "));
          ras.requirements.addAll(attributeTextList.stream().map(at -> extractAttributeRequirement(at, leiteigenschaftText)).collect(Collectors.toList()));
          returnValue.childs = ras;
        }
        else
        {
          returnValue.requirements.add(extractAttributeRequirement(attributeText, leiteigenschaftText));
        }
      }
      if (returnValue.childs == null && returnValue.requirements.isEmpty()) returnValue = null;
    }
    return returnValue;
  }

  private static String retrieveLeAttributeShort(SpecialAbilityCategoryKey sack)
  {
    String nonClericString = (sack == SpecialAbilityCategoryKey.magic || sack == SpecialAbilityCategoryKey.magic_advanced || sack == SpecialAbilityCategoryKey.magic_stile || sack == SpecialAbilityCategoryKey.magic_signs)
        ? "LE_MAGIC"
        : "";
    return (sack == SpecialAbilityCategoryKey.cleric || sack == SpecialAbilityCategoryKey.cleric_advanced || sack == SpecialAbilityCategoryKey.cleric_stile)
        ? "LE_CLERIC"
        : nonClericString;
  }


  public static Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> retrieveRequirementsSkill(String preconditions, int levels, int currentLevel, String name)
  {
    Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> returnValue = new Quartet<>(null, null, null, null);

    String requirementsString = preconditions
        .replace("Leiteigenschaft der Tradition", "")
        .replace("Leiteigenschaft", "")
        .replace(" mindestens 2 Schicksalspunkte", "")
        .replace("Sozialer Stand ", "")
        .replace("Zeichen", "Zeichnen");
    Matcher m = PAT_EXTRACT_SKILL_REQ.matcher(preconditions);
    if (m.find())
    {
      requirementsString = (levels > 1) ? extractReqStringForLevel(requirementsString, currentLevel) : requirementsString;
      Quintet<Boolean, RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> handledReqExceptions =
          handleRequirementSkillExceptions(preconditions, name, currentLevel);

      returnValue = new Quartet<>(handledReqExceptions.getValue1(), handledReqExceptions.getValue2(), handledReqExceptions.getValue3(), handledReqExceptions.getValue4());
      if (Boolean.FALSE.equals(handledReqExceptions.getValue0()))
      {
        returnValue = handleRequirementSkillRegulars(returnValue, requirementsString, name);
      }

      m = PAT_EXTRACT_MYSTICAL_SKILL_REQ.matcher(requirementsString);
      if (m.find())
      {
        returnValue = handleRequirementSkillMystical(returnValue, m.group().replace("-", ""), name);
      }
    }


    return returnValue;
  }

  private static Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> handleRequirementSkillMystical(Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> returnValue, String requirementString, String name)
  {
    RequirementMysticalSkill rms = new RequirementMysticalSkill();
    String skillName = requirementString.replaceAll("[0-9]", "").trim();
    String skillValueText = requirementString.replaceAll("[^0-9]", "");
    int skillValue = skillValueText.isEmpty() ? 0 : Integer.parseInt(skillValueText);
    switch (skillName)
    {
    case "CHIMAEROFORM" -> rms.key = MysticalSkillKey.RITUAL_CHIMAEROFORM;
    case "ARCANOVI" -> rms.key = MysticalSkillKey.RITUAL_ARCANOVI;
    case "PENTAGRAMMA" -> rms.key = MysticalSkillKey.SPELL_PENTAGRAMMA;
    case "HEXAGRAMMA" -> rms.key = MysticalSkillKey.SPELL_HEXAGRAMMA;
    case "HEPTAGRAMMA" -> rms.key = MysticalSkillKey.SPELL_HEPTAGRAMMA;
    case "TRAUMGESTALT" -> rms.key = MysticalSkillKey.RITUAL_TRAUMGESTALT;
    case "STEIN WANDLE" -> rms.key = MysticalSkillKey.RITUAL_STEIN_WANDLE;
    case "TOTES HANDLE" -> rms.key = MysticalSkillKey.RITUAL_TOTES_HANDLE;
    default -> LOGGER.error("SwitchCase Missing for Requirement (MS):" + name + ": " + skillName + " ->> " + skillValue);
    }
    rms.minValue = skillValue;
    return returnValue.setAt3(rms);

  }

  private static Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> handleRequirementSkillRegulars(Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> q, String requirementsString, String name)
  {
    RequirementsSkill rss = q.getValue0();
    RequirementSkillSum rsss = q.getValue1();
    RequirementsCombatSkill rscs = q.getValue2();
    RequirementMysticalSkill rms = q.getValue3();

    Matcher m = PAT_EXTRACT_SKILL_REQ.matcher(requirementsString
        .replace("Fernkampftechnikwert 10", "")
        .replace("Leiteigenschaft", "")
    );
    while (m.find())
    {
      String skillText = m.group().trim();
      if (!skillText.isEmpty())
      {
        Matcher vm = Pattern.compile("(?<=" + skillText + " )" + "\\d\\d?").matcher(requirementsString);
        if (vm.find())
        {
          String valueText = vm.group();
          Optional<SkillKey> sko = SkillKey.fromString(skillText);
          Optional<MysticalSkillKey> msko = Optional.empty();
          Optional<CombatSkillKey> csko = Optional.empty();
          if (sko.isPresent())
          {
            rss = updateRequirementsSkill(rss, sko.get(), valueText);
          }

          if (!sko.isPresent())
          {
            msko = MysticalSkillKey.fromString(skillText);
            if (msko.isPresent())
            {
              rms = generateRequirementMysticalSkill(msko.get(), valueText);
            }
          }

          if (!sko.isPresent() && !msko.isPresent())
          {
            csko = CombatSkillKey.fromString(skillText);
            if (csko.isPresent())
            {
              rscs = updateRequirementsCombatSkill(rscs, csko.get(), valueText);
            }
          }

          if (!sko.isPresent() && !msko.isPresent() && !csko.isPresent())
          {
            throw new IllegalArgumentException("No enum MysticalSkillKey constant with name " + skillText);
          }

        }
      }
    }

    return new Quartet<>(rss, rsss, rscs, rms);
  }

  private static Quintet<Boolean, RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> handleRequirementSkillExceptions(String preconditions, String name, int currentLevel)
  {
    RequirementsSkill rss = null;
    RequirementSkillSum rsss = null;
    RequirementsCombatSkill rscs = null;
    RequirementMysticalSkill rms = null;

    boolean isHandled = false;
    if (preconditions.equals("FW von Holzbearbeitung und Metallbearbeitung muss zusammen 12 ergeben."))
    {
      rsss = new RequirementSkillSum();
      rsss.skillKey.add(SkillKey.holzbearbeitung);
      rsss.skillKey.add(SkillKey.metallbearbeitung);
      rsss.minSum = 12;
      isHandled = true;
    }
    else if (preconditions.contains("je nach verwendeten Material"))
    {
      rss = updateRequirementsSkill(rss, SkillKey.holzbearbeitung, "4");
      updateRequirementsSkill(rss, SkillKey.lederbearbeitung, "4");
      updateRequirementsSkill(rss, SkillKey.metallbearbeitung, "4");
      updateRequirementsSkill(rss, SkillKey.steinbearbeitung, "4");
      rss.logicalOpperator = LogicalOperatorKey.or;
      isHandled = true;
    }
    else if (preconditions.contains("je nach verwendeten Rüstungsmaterial"))
    {
      rss = updateRequirementsSkill(rss, SkillKey.holzbearbeitung, "8");
      updateRequirementsSkill(rss, SkillKey.lederbearbeitung, "8");
      updateRequirementsSkill(rss, SkillKey.metallbearbeitung, "8");
      updateRequirementsSkill(rss, SkillKey.stoffbearbeitung, "8");
      rss.logicalOpperator = LogicalOperatorKey.or;
      isHandled = true;
    }
    else if (preconditions.contains("je nach verwendeten Waffenmaterial"))
    {
      rss = updateRequirementsSkill(rss, SkillKey.holzbearbeitung, "8");
      updateRequirementsSkill(rss, SkillKey.metallbearbeitung, "8");
      updateRequirementsSkill(rss, SkillKey.steinbearbeitung, "8");
      rss.logicalOpperator = LogicalOperatorKey.or;
      isHandled = true;
    }
    else if (preconditions.contains("je nach verwendeter Giftart"))
    {
      Optional<SkillKey> sko = SkillKey.fromString(name.replace("Giftverstärkung ", ""));
      if (sko.isPresent())
      {
        rss = updateRequirementsSkill(rss, sko.get(), "8");
        isHandled = true;
      }
    }
    else if (preconditions.contains("Sonderfertigkeit Ermutigender Gesang, passender Talentstil")
        || preconditions.contains("Sonderfertigkeit Faszinierender Gesang, passender Talentstil"))
    {
      rss = updateRequirementsSkill(rss, SkillKey.musizieren, "12");
      updateRequirementsSkill(rss, SkillKey.singen, "12");
      rss.logicalOpperator = LogicalOperatorKey.or;
      isHandled = true;
    }
    else if (preconditions.contains("alle genannten Talente"))
    {
      rss = updateRequirementsSkill(rss, SkillKey.körperbeherrschung, "4");
      updateRequirementsSkill(rss, SkillKey.kraftakt, "4");
      isHandled = true;
    }
    else if (preconditions.contains("Fernkampftechnikwert"))
    {
      for (CombatSkillKey csk : ExtractorCombatSkillKeys.CSS_ALL_RANGED)
      {
        rscs = updateRequirementsCombatSkill(rscs, csk, "10");
      }
      if (rscs != null) rscs.logicalOperator = LogicalOperatorKey.or;
    }
    else if (preconditions.contains("FW des adaptierten Zaubers")
        || preconditions.contains("3 Zauber des Merkmals auf 10")
        || preconditions.contains("3 Liturgien und Zeremonien des Aspekts auf 10"))
    {
      LOGGER.debug("handle in code");
      isHandled = true;
    }
    else if (preconditions.contains("je nach Fachbereich"))
    {
      LOGGER.debug("handled in LoadToSpecialAbility");
      isHandled = true;
    }
    else if (name.startsWith("Zeremonialgegenstände herstellen"))
    {
      rms = new RequirementMysticalSkill();
      rms.key = MysticalSkillKey.CEREMONY_OBJEKTWEIHE;
      rms.minValue = 0;
    }

    return new Quintet<>(
        isHandled,
        rss,
        rsss,
        rscs,
        rms
    );
  }

  private static RequirementsCombatSkill updateRequirementsCombatSkill(RequirementsCombatSkill rscs, CombatSkillKey combatSkillKey, String minValue)
  {
    if (rscs == null)
    {
      rscs = new RequirementsCombatSkill();
    }
    RequirementCombatSkill rcs = new RequirementCombatSkill();
    rcs.key = combatSkillKey;
    rcs.minValue = Integer.valueOf(minValue);

    rscs.requirements.add(rcs);
    return rscs;
  }

  private static RequirementMysticalSkill generateRequirementMysticalSkill(MysticalSkillKey mysticalSkillKey, String minValue)
  {
    RequirementMysticalSkill rms = new RequirementMysticalSkill();
    rms.key = mysticalSkillKey;
    rms.minValue = Integer.valueOf(minValue);
    return rms;
  }

  private static RequirementsSkill updateRequirementsSkill(RequirementsSkill rss, SkillKey skillKey, String minValue)
  {
    RequirementSkill rs = new RequirementSkill();
    rs.skillKey = skillKey;
    rs.minValue = Integer.valueOf(minValue);
    if (rss == null)
    {
      rss = new RequirementsSkill();
      rss.logicalOpperator = LogicalOperatorKey.and;
    }
    rss.requirements.add(rs);
    return rss;
  }

  private static String extractReqStringForLevel(String text, int currentLevel)
  {
    List<String> requirementsTextList = List.of(text.split("Stufe "));
    String reqString = "";
    if (currentLevel == 0)
    {
      reqString = requirementsTextList.get(0);
    }
    Optional<String> applicableReqO = requirementsTextList.stream().filter(rt -> rt.startsWith(RomanNumberHelper.intToRoman(currentLevel + 1) + ":")).findFirst();
    reqString += applicableReqO.isPresent() ? applicableReqO.get() : "";
    /*reqString += (requirementsTextList.size() > currentLevel + 1)
        ? requirementsTextList.get(currentLevel + 1)
        : "";*/
    return reqString;
  }

  private static RequirementAttribute extractAttributeRequirement(String attributeText, String leiteigenschaftString)
  {
    RequirementAttribute returnValue = new RequirementAttribute();
    returnValue.attribute = AttributeShort.valueOf(attributeText
        .replace("Leiteigenschaft der Tradition", leiteigenschaftString)
        .replace("Leiteigenschaft", leiteigenschaftString)
        .replaceAll("\\d", "").trim());
    returnValue.minValue = Integer.valueOf(attributeText.replaceAll("[MUKLINCHFGE]{2}|Leiteigenschaft( der Tradition)?", "").trim());
    return returnValue;
  }

  public static Triplet<Boolean, Boolean, Boolean> retrieveAllowedWeapons(String combatSkillsText)
  {
    boolean isParryOnly = combatSkillsText.startsWith("alle Parierwaffen");
    boolean isElfWeaponOnly = combatSkillsText.startsWith("alle Elfenwaffen") || combatSkillsText.startsWith("alle (solange Elfenwaffen)");
    boolean isDwarfWeaponOnly = combatSkillsText.startsWith("alle (solange Zwergenwaffen)");
    return new Triplet<>(isParryOnly, isElfWeaponOnly, isDwarfWeaponOnly);
  }

  public static RequirementsSpecialAbility retrieveRequirementsAbility(String preconditions, String name, int levels, int currentLevel)
  {
    RequirementsSpecialAbility returnValue = null;
    String requirementsString = preconditions.replace("\u00AD", "-");
    requirementsString = (levels > 1) ? extractReqStringForLevel(requirementsString, currentLevel) : requirementsString;

    Matcher m = Pattern.compile("[A-ü &/()-]{3,}(?=$|,|(<br>))").matcher(requirementsString);
    while (m.find())
    {
      String text = m.group().trim();

      Pair<Boolean, RequirementsSpecialAbility> p = handleRequirementAbilitiesExceptions(text, name);
      if (!p.getValue0() && !checkInvalidSpecialAbilityTexts(text.trim()))
      {
        List<String> reqTexts = List.of(text.split(" und | oder "));
        returnValue = returnValue == null ? new RequirementsSpecialAbility() : returnValue;
        List<RequirementSpecialAbility> reqSa = reqTexts.stream().map(reqText -> {
          SpecialAbilityKey sak = extractSpecialAbilityKeyFromText(reqText
              .replace("Sonderfertigkeiten", "")
              .replace("Sonderfertigkeit", "")
              .replace("Reversalis I des gleichen Zaubers", "Reversalis I")
              .replace("Kampftechnik keine", "")
              .replaceAll("Kernschuss$", "Kernschuss I")
              .replaceAll("Präziser Schuss II$", "Präziser Schuss/Wurf II")
              .replace("Finte I/II/III (je nach Antäuschen-Stufe)", "Finte I")
              .replaceAll("Wirbelangriff$", "Wirbelangriff I")
              .replace("Ottagalder I", "Blutmagie (Ottagalder I)")
              .replace("Blutmagie (Ottagalder I)I", "Blutmagie (Ottagalder II)")
              .trim());
          return generateRequirementSpecialAbility(sak, null);
        }).collect(Collectors.toList());
        if (text.contains(" oder "))
        {
          RequirementsSpecialAbility childs = new RequirementsSpecialAbility();
          childs.requirements = reqSa;
          childs.logicalOpperator = LogicalOperatorKey.or;
          returnValue.childs = childs;
        }
        else
        {
          returnValue.requirements.addAll(reqSa);
        }
      }
      if (p.getValue0())
      {
        returnValue = p.getValue1();
      }
    }

    return returnValue;
  }

  private static Pair<Boolean, RequirementsSpecialAbility> handleRequirementAbilitiesExceptions(String text, String name)
  {
    RequirementsSpecialAbility reqs = null;
    Boolean isHandled = Boolean.FALSE;
    if (text.contains("Geländekunde"))
    {
      isHandled = Boolean.TRUE;
      reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
      reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.geländekunde, text.replace("Geländekunde", "").trim()));
    }
    else if (text.contains("Ortskenntnis"))
    {
      isHandled = Boolean.TRUE;
      reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
      reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.ortskenntnis, text.replace("Geländekunde", "").trim()));
    }
    else if (name.equals("Nachladespezialist Armbrüste"))
    {
      isHandled = Boolean.TRUE;
      reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
      reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_armbrüste, null));
    }
    else if (name.equals("Nachladespezialist Blasrohre"))
    {
      isHandled = Boolean.TRUE;
      reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
      reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_blasrohre, null));
    }
    else if (name.equals("Nachladespezialist Bögen"))
    {
      isHandled = Boolean.TRUE;
      reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
      reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_bögen, null));
    }
    else if (name.equals("Nachladespezialist Diskus"))
    {
      isHandled = Boolean.TRUE;
      reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
      reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_diskus, null));
    }
    else if (name.equals("Nachladespezialist Schleudern"))
    {
      isHandled = Boolean.TRUE;
      reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
      reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_schleudern, null));
    }
    else if (name.equals("Nachladespezialist Wurfwaffen"))
    {
      isHandled = Boolean.TRUE;
      reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
      reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_wurfwaffen, null));
    }
    return new Pair<>(isHandled, reqs);
  }

  private static RequirementSpecialAbility generateRequirementSpecialAbility(SpecialAbilityKey saKey, String variantText)
  {
    RequirementSpecialAbility returnValue = new RequirementSpecialAbility();
    returnValue.abilityKey = saKey;
    returnValue.variant = variantText;
    return returnValue;
  }

  private static boolean checkInvalidSpecialAbilityTexts(String text)
  {
    return text.contains("Vorteil")
        || text.contains("Nachteil")
        || text.contains("Tradition")
        || text.contains("Schrift")
        || text.contains("Sprache")
        || text.contains("Kampfstil")
        || text.contains("Zauberstil")
        || text.contains("Schicksalspunkte")
        || text.contains("Einweisung durch eine Dornrose")
        || text.equals("keine")
        || text.equals("Objektweihe")
        || text.equals("Geweihter")
        // COPIES from sout not matching the regEx. Has to be changed, if regEx changes:
        || text.equals("der ihn bei der Ausübung der Talente behindert")
        || text.equals("Mitglied einer Meschpoche")
        || text.equals("an dem die Ware verkauft werden soll")
        || text.startsWith("Eingeschränkter Sinn ")
        || text.equals("die beigebrachte Fähigkeit muss der Lehrer selbst beherrschen")
        || text.equals("Monate lang an diesem Ort gelebt oder Weg dutzendfach bereist haben")
        || text.equals("Held muss mindestens eine Schrift und die gesprochene Sprache beherrschen")
        || text.startsWith("für")
        || text.startsWith("Kampftechnik ")
        || text.startsWith("Voraussetzungen für ")
        || text.equals("nur gegen GK mittel und klein")
        || text.startsWith("Spezie")
        || text.startsWith("passende")
        || text.equals("Elfen")
        || text.startsWith("Zauber")
        || text.startsWith("oder Steinbearbeitung")
        || text.startsWith("oder höher")
        || text.contains("Daimonidenkonstrukteur")
        || text.contains("Lieblings")
        || text.contains("Gildenmagier")
        || text.contains("Hexen)")
        || text.contains("Kenntnisse des jeweiligen Zaubers")
        || text.contains("Geoden")
        || text.contains("Goblinzauberinnen")
        || text.contains("Zibilja)")
        || text.isEmpty();
  }
}
