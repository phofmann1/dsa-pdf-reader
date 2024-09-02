package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.MeleeWeaponRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsEquipmentCombat;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMeleeWeapon extends DsaConverter<MeleeWeaponRaw, ConverterAtomicFlagsEquipmentCombat>
{
  /* Test Strings:
  1W6+2GE 14+0/–1kurz0,5 Stn30 HF50 Skomp (2 AP)
  1W6+2GE 14+0/–1kurz0,5 Stn30 HF50 Skomp (1AP)
  1W6+1GE 140/0kurz0,5 Stn30 HF45 Seinf
  1W6+5KK 14–1/–2mittel2 Stn90 HF170 Seinf
  (2H)2W6+6KK 14–2/–mittel6 Stn120 HF260 Seinf
  (i)1W6+1KK 14–2/–lang1,5 Stn100 HF7,3 Sprim
  1W6FF 160/–lang3 Stn250 HF20 Sprim
  1W6+1GE/KK 150/0kurz1 Stn50 HF70 Seinf
  1W6+2GE 140/–1kurz1 Stn35 HF100 Seinf
  (2H)1W6+2GE/KK 16–1 /+2lang0,75 Stn150 HF80 Sprim
  (2H)1W6+2GE/KK 16–1 /+2lang0,75 Stn150 HF80 S
  1W6+2KK 140/–2mittel0,5 Stn80 HFgratisprim
   */

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String KEY_REMARK = "Anmerkung";
  private static final String KEY_WAFFENVORTEIL = "Waffenvorteil";
  private static final String KEY_WAFFENNACHTEIL = "Waffennachteil";
  protected static final String[] KEYS = {
      KEY_REMARK,
      KEY_WAFFENVORTEIL,
      KEY_WAFFENNACHTEIL
  };
  private static final String KEY_COMBATSKILL = "Kampftechnik";
  //^\(i\)
  protected static Pattern PAT_IMPROVISED = Pattern.compile("^\\(i\\)");
  //(?<=\(2H\)|\(i\)|)(\dW(6|3)\+?\d?|1W2)
  protected static Pattern PAT_TP = Pattern.compile("(?<=\\(2H\\)|\\(i\\)|)(\\dW(6|3)\\+?\\d?|1W2)");
  //((GE|KK|FF)(\/(GE|KK|FF))? \d\d){1,2}
  protected static Pattern PAT_TP_PLUS = Pattern.compile("((GE|KK|FF)(\\/(GE|KK|FF))? ?\\d\\d){1,2}");
  //(?<!KK \d)(\-|\+)?\d{1}\s?\/\s?(\-|\+)?(\d|\-)
  protected static Pattern PAT_AT_PA_MOD = Pattern.compile("(?<!KK \\d)(\\-|\\–|\\+)?\\d{1}\\s?\\/\\s?(\\-|\\–|\\+)?(\\d|\\-|\\–)");
  //(kurz|mittel|lang)
  protected static Pattern PAT_MELEE_RANGE = Pattern.compile("(kurz|mittel|überlang|lang)");
  //\d[\d,\.]+(?=\+? Stn)
  protected static Pattern PAT_WEIGHT = Pattern.compile("\\d[\\d,\\.]*(?=\\+? (Stn|Stein))");
  //[\d,]+(?= HF)
  protected static Pattern PAT_SIZE = Pattern.compile("[\\d,]+(?= HF)");
  //\d[\d,\.+\/( bis )]+(?= ?(Sk|Se|Sp|S |S–|S$))|gratis
  protected static Pattern PAT_PRICE = Pattern.compile("\\d[\\d,\\.+\\/( bis )]*(?= ?(Sk|Se|Sp|S |S–|S$|Silbertaler))|gratis");
  //(einf|prim|komp \(\d+ AP\))
  protected static Pattern PAT_CRAFT = Pattern.compile("(einf|prim|komp \\(\\d+( )?AP\\))");
  //(?<=^Kampftechnik )\w*
  protected static Pattern PAT_COMBATSKILL = Pattern.compile("(?<=^Kampftechnik )[\\wäßöüÖÄÜ]*");
  private ConverterAtomicFlagsEquipmentCombat flags;
  private AtomicReference<CombatSkillKey> combatSkillKey;
  private List<MeleeWeaponRaw> returnValue;


  @Override
  public List<MeleeWeaponRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
  {
    returnValue = new ArrayList<>();
    String msg = String.format("parse  result to %s", getClassName());
    LOGGER.debug(msg);

    combatSkillKey = new AtomicReference<>();
    flags = new ConverterAtomicFlagsEquipmentCombat();

    resultList
        .forEach(t -> {

          String cleanText = t.text
              .trim();

          if (cleanText != null && !cleanText.isEmpty())
          {
            // validate the flags for conf
            boolean isCombatSkill = validateIsCombatSkill(t);
            boolean hasCombatSkill = combatSkillKey.get() != null;
            boolean isFirstValue = validateIsFirstValue(t, conf);
            boolean isDataKey = validateIsDataKey(t, cleanText, conf);
            boolean isDataValue = validateIsDataValue(t, cleanText, conf);

            finishPredecessorAndStartNew(isFirstValue, false, returnValue, conf, cleanText);

            if (isCombatSkill)
            {
              String match = firstMatch(PAT_COMBATSKILL, t.text);
              if (match != null)
              {
                combatSkillKey.set(CombatSkillKey.parse(match));
                flags.initDataFlags();
                flags.wasCombatSkill.set(true);
              }
            }
            else if (hasCombatSkill)
            {
              // handle keys
              if (isDataKey)
              {
                applyFlagsForKey(t.text);
              }

              // handle values
              if (isDataValue)
              {
                applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
                applyFlagsForNoKeyStrings(getFlags(), t.text);
              }

              getFlags().getFirstFlag().set(isFirstValue);


            }
          }
        });
    concludePredecessor(last(returnValue)); //finish the last entry in list
    return returnValue;
  }

  private boolean validateIsCombatSkill(TextWithMetaInfo t)
  {
    return t.text.startsWith(KEY_COMBATSKILL);
  }

  @Override
  protected String[] getKeys()
  {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsEquipmentCombat getFlags()
  {
    if (flags == null)
    {
      this.flags = new ConverterAtomicFlagsEquipmentCombat();
    }
    return flags;
  }

  @Override
  protected String getClassName()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<MeleeWeaponRaw> returnValue, TopicConfiguration conf, String cleanText)
  {

    if (this.getFlags().getWasEndOfEntry().get())
    {
      MeleeWeaponRaw newEntry = new MeleeWeaponRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      newEntry.combatSkillKey = combatSkillKey.get();
      newEntry.setName(cleanText.replace("WaffeTPL+SAT/PA-ModRWGewichtLängePreisKomplexität", ""));
      returnValue.add(newEntry);
    }
  }

  @Override
  protected void applyFlagsForKey(String key)
  {
    this.getFlags().wasName.set(false);
    this.getFlags().wasData.set(false);
    this.getFlags().wasRemark.set(key.trim().equals(KEY_REMARK));
    this.getFlags().wasAdvantage.set(key.trim().equals(KEY_WAFFENVORTEIL));
    this.getFlags().wasDisadvantage.set(key.trim().equals(KEY_WAFFENNACHTEIL));
  }

  @Override
  protected void applyDataValue(MeleeWeaponRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    if (currentDataObject != null && !this.getFlags().wasCombatSkill.get())
    {
      if (!this.getFlags().wasRemark.get() && !this.getFlags().wasAdvantage.get() && !this.getFlags().wasDisadvantage.get())
      {
        currentDataObject.isImprovised = isMatch(PAT_IMPROVISED, cleanText);
        currentDataObject.tp = concatForDataValue(currentDataObject.tp, firstMatch(PAT_TP, cleanText));
        if (currentDataObject.tp != null && !currentDataObject.tp.isEmpty()
            || currentDataObject.name.equals("Lederschild")
            || currentDataObject.name.equals("Großer Lederschild"))
        {
          currentDataObject.additionalDamage = concatForDataValue(currentDataObject.additionalDamage, firstMatch(PAT_TP_PLUS, cleanText));
          currentDataObject.atPaMod = concatForDataValue(currentDataObject.atPaMod, firstMatch(PAT_AT_PA_MOD, cleanText.replaceAll("–", "-")));
          currentDataObject.combatDistance = concatForDataValue(currentDataObject.combatDistance, firstMatch(PAT_MELEE_RANGE, cleanText));
          currentDataObject.weight = concatForDataValue(currentDataObject.weight, firstMatch(PAT_WEIGHT, cleanText));
          currentDataObject.size = concatForDataValue(currentDataObject.size, firstMatch(PAT_SIZE, cleanText));
          currentDataObject.price = concatForDataValue(currentDataObject.price, firstMatch(PAT_PRICE, cleanText));
          currentDataObject.craft = concatForDataValue(currentDataObject.craft, firstMatch(PAT_CRAFT, cleanText));
        }
      }
      if (this.getFlags().wasRemark.get())
        currentDataObject.remark = concatForDataValueWithMarkup(currentDataObject.remark, cleanText, isBold, isItalic);
      if (this.getFlags().wasAdvantage.get())
        currentDataObject.advantage = concatForDataValue(currentDataObject.advantage, cleanText);
      if (this.getFlags().wasDisadvantage.get())
        currentDataObject.disadvantage = concatForDataValue(currentDataObject.disadvantage, cleanText);
    }
  }

  @Override
  public boolean validateIsFirstValue(TextWithMetaInfo t, TopicConfiguration conf)
  {
    return !t.text.equals("Regelwerk")
        && !t.text.equals("Aventurisches")
        && !t.text.equals("Aventurisches Kompendium")
        && !isNumeric(t.text)
        && t.isBold
        && !t.isItalic
        && Arrays.stream(this.getKeys()).noneMatch(k -> k.equals(t.text))
        && (getFlags().wasCombatSkill.get() || getFlags().wasDisadvantage.get());
  }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return t.isBold && Arrays.stream(getKeys()).anyMatch(k -> k.equals(t.text));
  }

  @Override
  protected void concludePredecessor(MeleeWeaponRaw lastEntry)
  {
    if (lastEntry != null && lastEntry.name.equals("Neunschwänzige"))
    {
      MeleeWeaponRaw second = new MeleeWeaponRaw();
      second.name = "Neunschwänzige mit Metallstücken/Dornen";
      second.tp = "1W6+1";
      lastEntry.tp = lastEntry.tp.replace(second.tp, "").trim();
      second.additionalDamage = "FF 15";
      lastEntry.additionalDamage = "FF 15";
      second.craft = "einf";
      lastEntry.craft = "einf";
      second.price = "25";
      lastEntry.price = "18";
      second.weight = "2,1";
      lastEntry.weight = "2";
      second.combatDistance = "mittel";
      lastEntry.combatDistance = "mittel";
      second.disadvantage = lastEntry.disadvantage;
      second.advantage = lastEntry.advantage;
      second.remark = lastEntry.remark;
      second.atPaMod = "–2/–";
      lastEntry.atPaMod = "–2/–";
      second.combatSkillKey = CombatSkillKey.peitschen;
      second.size = "90";
      lastEntry.size = lastEntry.size.replace(second.size, "").trim();
      second.publication = lastEntry.publication;
      second.isImprovised = false;
      second.topic = lastEntry.topic;
      returnValue.add(second);
    }
  }
}
