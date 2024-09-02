package de.pho.dsapdfreader.dsaconverter;

import static de.pho.dsapdfreader.dsaconverter.DsaConverterMeleeWeapon.PAT_AT_PA_MOD;
import static de.pho.dsapdfreader.dsaconverter.DsaConverterMeleeWeapon.PAT_IMPROVISED;
import static de.pho.dsapdfreader.dsaconverter.DsaConverterMeleeWeapon.PAT_MELEE_RANGE;
import static de.pho.dsapdfreader.dsaconverter.DsaConverterMeleeWeapon.PAT_PRICE;
import static de.pho.dsapdfreader.dsaconverter.DsaConverterMeleeWeapon.PAT_SIZE;
import static de.pho.dsapdfreader.dsaconverter.DsaConverterMeleeWeapon.PAT_TP;
import static de.pho.dsapdfreader.dsaconverter.DsaConverterMeleeWeapon.PAT_TP_PLUS;
import static de.pho.dsapdfreader.dsaconverter.DsaConverterMeleeWeapon.PAT_WEIGHT;
import static de.pho.dsapdfreader.dsaconverter.DsaConverterRangedWeapon.PAT_LOADING_TIME;
import static de.pho.dsapdfreader.dsaconverter.DsaConverterRangedWeapon.PAT_MUNITION_TYPE;
import static de.pho.dsapdfreader.dsaconverter.DsaConverterRangedWeapon.PAT_RANGED_RANGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.WeaponRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsWeapon;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterWeaponLists extends DsaConverter<WeaponRaw, ConverterAtomicFlagsWeapon> {
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
  private ConverterAtomicFlagsWeapon flags;
  private List<WeaponRaw> returnValue;


  @Override
  public List<WeaponRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf) {
    returnValue = new ArrayList<>();
    String msg = String.format("parse  result to %s", getClassName());
    LOGGER.debug(msg);

    flags = new ConverterAtomicFlagsWeapon();

    resultList
        .forEach(t -> {

          String cleanText = t.text
              .trim();

          if (cleanText != null && !cleanText.isEmpty()) {
            // validate the flags for conf
            boolean isFirstValue = validateIsFirstValue(t, conf);
            boolean isDataKey = validateIsDataKey(t, cleanText, conf);
            boolean isDataValue = validateIsDataValue(t, cleanText, conf);
            finishPredecessorAndStartNew(isFirstValue, false, returnValue, conf, cleanText);

            // handle keys
            if (isDataKey) {
              applyFlagsForKey(t.text);
            }

            // handle values
            if (isDataValue) {
              applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
              applyFlagsForNoKeyStrings(getFlags(), t.text);
            }


            getFlags().wasData.set(isFirstValue);
            getFlags().getFirstFlag().set(isFirstValue);
          }
        });
    concludePredecessor(last(returnValue)); //finish the last entry in list
    return returnValue;
  }

  @Override
  protected String[] getKeys() {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsWeapon getFlags() {
    if (flags == null) {
      this.flags = new ConverterAtomicFlagsWeapon();
    }
    return flags;
  }

  @Override
  protected String getClassName() {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<WeaponRaw> returnValue, TopicConfiguration conf, String cleanText) {

    WeaponRaw lastValue = last(returnValue);
    if (this.getFlags().getWasEndOfEntry().get() || lastValue != null && lastValue.name.equals("Krähenfüße") && this.getFlags().wasRemark.get()) {
      WeaponRaw newEntry = new WeaponRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      newEntry.isImprovised = cleanText.contains("(2H, i)") || cleanText.contains("(i)");
      newEntry.name = cleanText
          .replace("WaffeKampftechnikTPL+SAT/PA-ModRWGewichtLängePreisKomplexität", "")
          .replace("WaffeKampftechnikTPLZRWMunitionstypGewichtLängePreisKomplexität", "")
          .replace("(2H, i)", "")
          .replace("(2H)", "")
          .replace("(i)", "")
          .replace("AmazonenschildSchilde", "Amazonenschild");

      if (cleanText.endsWith("AmazonenschildSchilde")) newEntry.combatSkillKey = CombatSkillKey.schilde;
      if (newEntry.name != null && !newEntry.name.isEmpty()) {
        returnValue.add(newEntry);
      }

    }
  }

  @Override
  protected void applyFlagsForKey(String key) {
    this.getFlags().wasName.set(false);
    this.getFlags().wasData.set(false);
    this.getFlags().wasRemark.set(key.trim().equals(KEY_REMARK));
    this.getFlags().wasAdvantage.set(key.trim().equals(KEY_WAFFENVORTEIL));
    this.getFlags().wasDisadvantage.set(key.trim().equals(KEY_WAFFENNACHTEIL));
  }

  @Override
  protected void applyFlagsForNoKeyStrings(ConverterAtomicFlagsWeapon flags, String text) {
    String cleanText = text.replace("NameKampftechnikTP", "")
        .replace("L+S", "")
        .replace("LZ", "")
        .replace("AT/PA-Mod", "")
        .replace("RW", "")
        .replace("Gewicht", "")
        .replace("Länge", "")
        .replace("Preis", "")
        .replace("Munitionstyp", "");
    super.applyFlagsForNoKeyStrings(flags, text);
    if (text.endsWith("Komplexität")) this.getFlags().wasComplexity.set(true);
    else if (flags.wasComplexity.get()) {
      this.getFlags().wasComplexity.set(false);
      this.getFlags().wasName.set(true);
    }
    else if (flags.wasName.get() && !cleanText.isEmpty()) {
      this.getFlags().wasName.set(false);
      this.getFlags().wasData.set(true);
    }
    else if (text.contains("Komplexität")) {
      this.getFlags().wasData.set(true);
    }
    if (flags.wasDisadvantage.get() && text.endsWith(".")) {
      this.getFlags().wasDisadvantage.set(false);
    }
  }

  @Override
  protected void applyDataValue(WeaponRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic) {
    if (currentDataObject != null) {
      if (this.getFlags().wasData.get()) {
        currentDataObject.isImprovised = (currentDataObject != null && currentDataObject.isImprovised) || isMatch(PAT_IMPROVISED, cleanText);
        currentDataObject.tp = concatForDataValue(currentDataObject.tp, firstMatch(PAT_TP, cleanText));
        if (currentDataObject.tp != null && !currentDataObject.tp.isEmpty()
            || currentDataObject.name.equals("Lederschild")
            || currentDataObject.name.equals("Großer Lederschild")
            || currentDataObject.name.equals("Krähenfüße")
            || currentDataObject.name.equals("Netz der Keke-Wanaq")) {
          currentDataObject.combatSkillKey = extractCombatSkillKey(currentDataObject.combatSkillKey, cleanText);
          currentDataObject.additionalDamage = concatForDataValue(currentDataObject.additionalDamage, firstMatch(PAT_TP_PLUS, cleanText));
          currentDataObject.atPaMod = concatForDataValue(currentDataObject.atPaMod, firstMatch(PAT_AT_PA_MOD, cleanText));
          currentDataObject.combatDistance = concatForDataValue(currentDataObject.combatDistance, firstMatch(PAT_MELEE_RANGE, cleanText));
          currentDataObject.weight = concatForDataValue(currentDataObject.weight, firstMatch(PAT_WEIGHT, cleanText));
          currentDataObject.size = concatForDataValue(currentDataObject.size, firstMatch(PAT_SIZE, cleanText));
          currentDataObject.price = concatForDataValue(currentDataObject.price, firstMatch(PAT_PRICE, cleanText));
          currentDataObject.loadingTime = concatForDataValue(currentDataObject.loadingTime, firstMatch(PAT_LOADING_TIME, cleanText));
          currentDataObject.combatDistance = concatForDataValue(currentDataObject.combatDistance, firstMatch(PAT_RANGED_RANGE, cleanText));
          currentDataObject.munitionType = concatForDataValue(currentDataObject.munitionType, firstMatch(PAT_MUNITION_TYPE, cleanText));
        }
      }
      if (this.getFlags().wasRemark.get())
        currentDataObject.remark = concatForDataValueWithMarkup(currentDataObject.remark, cleanText, isBold, isItalic);
      if (this.getFlags().wasAdvantage.get())
        currentDataObject.advantage = concatForDataValue(currentDataObject.advantage, cleanText);
      if (this.getFlags().wasDisadvantage.get())
        currentDataObject.disadvantage = concatForDataValue(currentDataObject.disadvantage, cleanText);
      if (this.getFlags().wasComplexity.get())
        currentDataObject.craft = concatForDataValue(currentDataObject.craft, cleanText);
    }
  }

  private CombatSkillKey extractCombatSkillKey(CombatSkillKey combatSkillKey, String cleanText) {
    String weaponString = firstMatch(Pattern.compile("^[A-zäöüÄÖÜß]*"), cleanText
        .replace("(2H)", "")
        .replace("(2H, i)", "")
        .replace("(i)", "")
        .replace("Stangewaffen", "Stangenwaffen")
        .replace("Zweihand- hiebwaffen", "Zweihandhiebwaffen")
        .trim());
    CombatSkillKey result = combatSkillKey;
    try {
      result = CombatSkillKey.parse(weaponString);
    }
    catch (IllegalArgumentException e) {

    }
    return combatSkillKey != null ? combatSkillKey : result;
  }

  @Override
  public boolean validateIsFirstValue(TextWithMetaInfo t, TopicConfiguration conf) {
    return !t.text.equals("Kodex des Schwertes")
        && !t.text.equals("Aventurisches Kompendium")
        && !t.text.equals("Aventurisches")
        && !t.text.equals("Kompendium")
        && !t.text.equals("Regelwerk")
        && !t.text.equals("62ff.")
        && !t.text.equals("(i)")
        && !t.text.isEmpty()
        && !isNumeric(t.text)
        && t.isBold
        && !t.isItalic
        && Arrays.stream(this.getKeys()).noneMatch(k -> k.equals(t.text));
  }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return t.isBold && Arrays.stream(getKeys()).anyMatch(k -> k.equals(t.text));
  }

  @Override
  protected void concludePredecessor(WeaponRaw lastEntry) {
  }
}
