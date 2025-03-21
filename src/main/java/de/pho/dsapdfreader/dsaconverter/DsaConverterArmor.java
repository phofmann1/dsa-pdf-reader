package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.exporter.model.enums.HitZoneKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.ArmorRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsEquipmentCombat;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterArmor extends DsaConverter<ArmorRaw, ConverterAtomicFlagsEquipmentCombat> {
  /* Test Strings:
   */

  private static final Logger LOGGER = LogManager.getLogger();

  private static final String KEY_REMARK = "Anmerkung";
  private static final String KEY_ADVANTAGE = "Rüstungsvorteil";
  private static final String KEY_DISADVANTAGE = "Rüstungsnachteil";
  private static final String KEY_ADVANTAGE_HELMET = "Helmvorteil";
  private static final String KEY_DISADVANTAGE_HELMET = "Helmnachteil";
  protected static final String[] KEYS = {
      KEY_REMARK,
      KEY_ADVANTAGE,
      KEY_DISADVANTAGE,
      KEY_ADVANTAGE_HELMET,
      KEY_DISADVANTAGE_HELMET
  };
  //^\d
  private static final Pattern PAT_ARMOR_VALUE = Pattern.compile("^\\d");
  //(?<=^\d)\d
  private static final Pattern PAT_ENCUMBERANCE_VALUE = Pattern.compile("(?<=^\\d|^\\d\\*)\\d"); //* für Ferkina Fellrüstung (Wüstenreiche)
  //
  private static final Pattern PAT_ADDITIONAL_ENCUMBERANCE = Pattern.compile("–1 GS, –1 INI");

  private ConverterAtomicFlagsEquipmentCombat flags;
  private AtomicReference<CombatSkillKey> combatSkillKey;


  @Override
  public List<ArmorRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
  {
    List<ArmorRaw> returnValue = new ArrayList<>();
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
            boolean isFirstValue = validateIsFirstValue(t, conf);
            boolean isDataKey = validateIsDataKey(t, cleanText, conf);
            boolean isDataValue = validateIsDataValue(t, cleanText, conf);
            finishPredecessorAndStartNew(isFirstValue, false, returnValue, conf, cleanText);


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

        });
    concludePredecessor(last(returnValue)); //finish the last entry in list
    return returnValue;
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
  protected void handleFirstValue(List<ArmorRaw> returnValue, TopicConfiguration conf, String cleanText)
  {

    if (this.getFlags().getWasEndOfEntry().get() || this.getFlags().isFirstValue.get() || conf.publication.equals("Basis"))
    {
      ArmorRaw newEntry = new ArmorRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      newEntry.setName(cleanText
          .replace("ArtRüstungsschutzBelastung (Stufe)zusätzliche AbzügeGewichtPreisKomplexität", "")
          .replace("ArtRüstungsschutzBelastung (Stufe)zusätzliche AbzügeGewichtPreis", "")
          .replaceAll("Komplexität.*", "")
      );
      this.getFlags().wasName.set(true);
      this.getFlags().isFirstValue.set(false);
      returnValue.add(newEntry);
    }
  }

  @Override
  protected void applyFlagsForKey(String key) {
    this.getFlags().wasName.set(false);
    this.getFlags().wasData.set(false);
    this.getFlags().wasRemark.set(key.trim().equals(KEY_REMARK));
    this.getFlags().wasAdvantage.set(key.trim().equals(KEY_ADVANTAGE) || key.trim().equals((KEY_ADVANTAGE_HELMET)));
    this.getFlags().wasDisadvantage.set(key.trim().equals(KEY_DISADVANTAGE) || key.trim().equals((KEY_DISADVANTAGE_HELMET)));
  }

  @Override
  protected void applyDataValue(ArmorRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    if (currentDataObject != null)
    {
      if (!this.getFlags().wasRemark.get() && !this.getFlags().wasAdvantage.get() && !this.getFlags().wasDisadvantage.get())
      {
        currentDataObject.armor = concatForDataValue(currentDataObject.armor, firstMatch(PAT_ARMOR_VALUE, cleanText));
        currentDataObject.encumberance = concatForDataValue(currentDataObject.encumberance, firstMatch(PAT_ENCUMBERANCE_VALUE, cleanText));
        currentDataObject.additionalEncumberance = isMatch(PAT_ADDITIONAL_ENCUMBERANCE, cleanText);

        currentDataObject.weight = firstMatch(DsaConverterMeleeWeapon.PAT_WEIGHT, cleanText); //concatForDataValue(currentDataObject.weight, firstMatch(DsaConverterMeleeWeapon.PAT_WEIGHT, cleanText));
        currentDataObject.price = concatForDataValue(currentDataObject.price, firstMatch(DsaConverterMeleeWeapon.PAT_PRICE, cleanText));
        currentDataObject.craft = concatForDataValue(currentDataObject.craft, firstMatch(DsaConverterMeleeWeapon.PAT_CRAFT, cleanText));
        currentDataObject.protectedZones = List.of(HitZoneKey.body);
      }
      if (this.getFlags().wasRemark.get())
        currentDataObject.remark = concatForDataValueWithMarkup(currentDataObject.remark, cleanText, isBold, isItalic);
      if (this.getFlags().wasAdvantage.get())
        currentDataObject.advantage = concatForDataValue(currentDataObject.advantage, cleanText);
      if (this.getFlags().wasDisadvantage.get()) {
        if (cleanText.startsWith("»")) this.getFlags().wasFinished.set(Boolean.TRUE); //Reset Disadvantage Flag to prevent FluffText in description
        if (!this.getFlags().wasFinished.get()) {
          currentDataObject.disadvantage = concatForDataValue(currentDataObject.disadvantage, cleanText);
        }
      }
    }
  }

  @Override
  public boolean validateIsFirstValue(TextWithMetaInfo t, TopicConfiguration conf)
  {
    return t.isBold
        && !t.text.equals("Regelwerk")
        && !isNumeric(t.text)
        && !t.isItalic
        && !t.text.equals("Übler Geruch")
        && Arrays.stream(this.getKeys()).noneMatch(k -> k.equals(t.text))
        && (getFlags().wasDisadvantage.get() || getFlags().isFirstValue.get() || conf.publication.equals("Basis"));
  }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return t.isBold && Arrays.stream(getKeys()).anyMatch(k -> k.equals(t.text)) && !t.text.equals("Übler Geruch");
  }

  @Override
  protected void concludePredecessor(ArmorRaw lastEntry)
  {
  }
}
