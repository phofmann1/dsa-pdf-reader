package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.RangedWeaponRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsEquipmentCombat;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterRangedWeapon extends DsaConverter<RangedWeaponRaw, ConverterAtomicFlagsEquipmentCombat>
{
  /* Test Strings:
  1W22/10 Aktionen5/25/40Kugeln0,75 Stn 40 HF120 Skomp (4 AP)
  1W6+11 Aktion 2/10/150,25 Stn10 HF30 Seinf
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
  //\d(\/\d{1,2})? (Aktionen|Aktion)
  private static final Pattern PAT_LOADING_TIME = Pattern.compile("\\d(\\/\\d{1,2})? (Aktionen|Aktion)");
  //\d{1,3}\/\d{1,3}\/\d{1,3}
  private static final Pattern PAT_RANGED_RANGE = Pattern.compile("\\d{1,3}\\/\\d{1,3}\\/\\d{1,3}");
  //(Kugeln|Kugel|Steinchen|Pfeile|Bolzen|Blasrohrpfeile)
  private static final Pattern PAT_MUNITION_TYPE = Pattern.compile("(Kugeln|Kugel|Steinchen|Pfeile|Bolzen|Blasrohrpfeile)");

  private ConverterAtomicFlagsEquipmentCombat flags;
  private AtomicReference<CombatSkillKey> combatSkillKey;


  @Override
  public List<RangedWeaponRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
  {
    List<RangedWeaponRaw> returnValue = new ArrayList<>();
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
              String match = firstMatch(DsaConverterMeleeWeapon.PAT_COMBATSKILL, t.text);
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
  protected void handleFirstValue(List<RangedWeaponRaw> returnValue, TopicConfiguration conf, String cleanText)
  {

    if (this.getFlags().getWasEndOfEntry().get())
    {
      RangedWeaponRaw newEntry = new RangedWeaponRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      newEntry.combatSkillKey = combatSkillKey.get();
      newEntry.setName(cleanText
          .replace("WaffeTPLZRWMunitionstypGewichtLängePreisKomplexität", "")
          .replace("WaffeTPLZRWGewichtLängePreisKomplexität", "")
      );
      this.getFlags().wasName.set(true);
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
  protected void applyDataValue(RangedWeaponRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    if (currentDataObject != null && !this.getFlags().wasCombatSkill.get())
    {
      if (!this.getFlags().wasRemark.get() && !this.getFlags().wasAdvantage.get() && !this.getFlags().wasDisadvantage.get())
      {
        currentDataObject.isImprovised = isMatch(DsaConverterMeleeWeapon.PAT_IMPROVISED, cleanText);
        currentDataObject.tp = concatForDataValue(currentDataObject.tp, firstMatch(DsaConverterMeleeWeapon.PAT_TP, cleanText));
        if (currentDataObject.tp != null && !currentDataObject.tp.isEmpty() || currentDataObject.name.equals("Wurfnetz") || currentDataObject.name.equals("Fledermaus") || currentDataObject.name.equals("Granatapfel"))
        {
          currentDataObject.combatDistance = concatForDataValue(currentDataObject.combatDistance, firstMatch(PAT_RANGED_RANGE, cleanText));
          currentDataObject.weight = concatForDataValue(currentDataObject.weight, firstMatch(DsaConverterMeleeWeapon.PAT_WEIGHT, cleanText));
          currentDataObject.size = concatForDataValue(currentDataObject.size, firstMatch(DsaConverterMeleeWeapon.PAT_SIZE, cleanText));
          currentDataObject.price = concatForDataValue(currentDataObject.price, firstMatch(DsaConverterMeleeWeapon.PAT_PRICE, cleanText));
          currentDataObject.craft = concatForDataValue(currentDataObject.craft, firstMatch(DsaConverterMeleeWeapon.PAT_CRAFT, cleanText));
          currentDataObject.loadingTime = concatForDataValue(currentDataObject.loadingTime, firstMatch(PAT_LOADING_TIME, cleanText));
          currentDataObject.munitionType = concatForDataValue(currentDataObject.munitionType, firstMatch(PAT_MUNITION_TYPE, cleanText));

          if (currentDataObject.munitionType.isEmpty() && !currentDataObject.combatDistance.isEmpty())
          {
            String[] distances = currentDataObject.combatDistance.split("\\/");
            String lastDistance = distances[2];

            if (lastDistance.equals(currentDataObject.weight))
            { //Wurfkeule Sonderregelung
              currentDataObject.weight = currentDataObject.combatDistance.substring(currentDataObject.combatDistance.length() - 1);
              currentDataObject.combatDistance = currentDataObject.combatDistance.substring(0, currentDataObject.combatDistance.length() - 1);
            }
            else if (!currentDataObject.weight.isEmpty())
            {

              currentDataObject.weight = currentDataObject.weight.startsWith(lastDistance)
                  ? currentDataObject.weight.replace(lastDistance, "")
                  : currentDataObject.weight;
            }
          }
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
  protected void concludePredecessor(RangedWeaponRaw lastEntry)
  {
  }
}
