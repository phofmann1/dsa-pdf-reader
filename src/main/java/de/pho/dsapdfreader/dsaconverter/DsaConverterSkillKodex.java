package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.SkillRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsSkill;
import de.pho.dsapdfreader.exporter.model.enums.SkillCategoryKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterSkillKodex extends DsaConverter<SkillRaw, ConverterAtomicFlagsSkill>
{

  private static final String KEY_NEW_APPLICATIONS = "Neues Anwendungsgebiet";
  private static final String KEY_CHECK = "Probe";
  private static final String KEY_APPLICATIONS = "Anwendungsgebiete";
  private static final String KEY_ENCUMBRANCE = "Belastung";
  private static final String KEY_TOOLS = "Werkzeuge";
  private static final String KEY_QUALITY = "Qualität";
  private static final String KEY_FAILED = "Misslungene Probe";
  private static final String KEY_CRITICAL_SUCCESS = "Kritischer Erfolg";
  private static final String KEY_CRITICAL_FAILURE_I = "Patzer";
  private static final String KEY_CRITICAL_FAILURE_II = ".Patzer";
  private static final String KEY_ADVANCEMENT_CATEGORY = "Steigerungskosten";

  protected static final String[] KEYS = {
      KEY_NEW_APPLICATIONS,
      KEY_CHECK,
      KEY_APPLICATIONS,
      KEY_ENCUMBRANCE,
      KEY_TOOLS,
      KEY_QUALITY,
      KEY_FAILED,
      KEY_CRITICAL_SUCCESS,
      KEY_CRITICAL_FAILURE_I,
      KEY_CRITICAL_FAILURE_II,
      KEY_ADVANCEMENT_CATEGORY,
  };
  private static final Logger LOGGER = LogManager.getLogger();

  ConverterAtomicFlagsSkill flags;

  public List<SkillRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
  {
    List<SkillRaw> returnValue = new ArrayList<>();
    String msg = String.format("parse  result to %s", getClassName());
    LOGGER.debug(msg);

    AtomicReference<SkillCategoryKey> skillCategoryKey = new AtomicReference<>();
    resultList
        .forEach(t -> {

          String cleanText = t.text
              .trim();

          boolean isTopic = t.size == 1800;
          // validate the flags for conf
          boolean isFirstValue = validateIsFirstValue(t, conf);
          boolean isFirstValueSkipped = isFirstValue && isNumeric(t.text); // gets skipped, when the firstValue is a number (Page Number in some documents)
          boolean isDataKey = validateIsDataKey(t, cleanText, conf);
          boolean isDataValue = validateIsDataValue(t, cleanText, conf);
          boolean isIgnore = isFirstValue && cleanText.endsWith("Strömungen"); // Strömungs Vorwort in Kodex des Götterwirkens
          handleWasNoKeyStrings(getFlags(), t); // used in MysticalSkill for QS flags, they act differently, because they are also part of the effect


          if (isTopic) skillCategoryKey.set(extractSkillCategoryKey(t.text));

          if (skillCategoryKey.get() != null)
          {
            if (!isIgnore)
            {
              finishPredecessorAndStartNew(isFirstValue, isFirstValueSkipped, returnValue, conf, cleanText);
              if (isFirstValue)
              {
                last(returnValue).skillCategoryKey = skillCategoryKey.get();
              }
              // handle keys
              if (isDataKey)
              {
                applyFlagsForKey(t.text);
              }

              // handle values
              if (isDataValue)
              {
                applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
                applySpecialAbilitiesFlagsForNoKeyStrings(getFlags(), t);
              }
              getFlags().getFirstFlag().set(isFirstValue && !isFirstValueSkipped);
            }
          }
        });
    concludePredecessor(last(returnValue)); //finish the last entry in list
    return returnValue;
  }

  private void applySpecialAbilitiesFlagsForNoKeyStrings(ConverterAtomicFlagsSkill flags, TextWithMetaInfo t)
  {
    if (flags.wasName.get() && !t.isBold)
    {
      flags.wasName.set(false);
      flags.wasDescription.set(true);
    }
  }

  private SkillCategoryKey extractSkillCategoryKey(String text)
  {
    return switch (text)
        {
          case "Körpertalente" -> SkillCategoryKey.körpertalente;
          case "Gesellschaftstalente" -> SkillCategoryKey.gesellschaftstalente;
          case "Naturtalente" -> SkillCategoryKey.naturtalente;
          case "Wissenstalente" -> SkillCategoryKey.wissenstalente;
          case "Handwerkstalente" -> SkillCategoryKey.handwerkstalente;
          default -> null;
        };
  }


  @Override
  protected String[] getKeys()
  {
    return KEYS;
  }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return Arrays.stream(this.getKeys()).anyMatch(k -> k.equalsIgnoreCase(t.text));
  }

  @Override
  protected ConverterAtomicFlagsSkill getFlags()
  {
    if (flags == null)
    {
      this.flags = new ConverterAtomicFlagsSkill();
    }
    return flags;
  }

  @Override
  protected String getClassName()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<SkillRaw> returnValue, TopicConfiguration conf, String cleanText)
  {

    if (!this.getFlags().getFirstFlag().get())
    {
      SkillRaw newEntry = new SkillRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      newEntry.name = cleanText;
      returnValue.add(newEntry);
    }
    this.getFlags().wasName.set(true);
  }

  @Override
  protected void applyFlagsForKey(String key)
  {
    this.getFlags().wasName.set(false);
    this.getFlags().wasDescription.set(false);

    this.getFlags().wasNewApplication.set(key.trim().equals(KEY_NEW_APPLICATIONS));
    this.getFlags().wasCheck.set(key.trim().equals(KEY_CHECK));
    this.getFlags().wasApplication.set(key.trim().equals(KEY_APPLICATIONS));
    this.getFlags().wasEncumbrance.set(key.trim().equals(KEY_ENCUMBRANCE));
    this.getFlags().wasTools.set(key.trim().equals(KEY_TOOLS));
    this.getFlags().wasQuality.set(key.trim().equals(KEY_QUALITY));
    this.getFlags().wasFailure.set(key.trim().equals(KEY_FAILED));
    this.getFlags().wasCriticalSuccess.set(key.trim().equals(KEY_CRITICAL_SUCCESS));
    this.getFlags().wasCriticalFailure.set(key.trim().equals(KEY_CRITICAL_FAILURE_I) || key.trim().equals(KEY_CRITICAL_FAILURE_II));
    this.getFlags().wasAdvancementCategory.set(key.trim().equals(KEY_ADVANCEMENT_CATEGORY));
  }

  @Override
  protected void applyDataValue(SkillRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    if (currentDataObject != null)
    {
      if (this.getFlags().wasName.get()) currentDataObject.name = concatForDataValue(currentDataObject.name, cleanText);
      if (this.getFlags().wasDescription.get()) currentDataObject.description = concatForDataValue(currentDataObject.description, cleanText);
      if (this.getFlags().wasNewApplication.get()) currentDataObject.newApplication = concatForDataValue(currentDataObject.newApplication, cleanText);
      if (this.getFlags().wasCheck.get())
      {
        String checkString = cleanText.substring(0, 8);
        String descriptionString = cleanText.length() > 8 ? cleanText.substring(8) : "";
        currentDataObject.check = concatForDataValue(currentDataObject.check, checkString);
        this.getFlags().wasCheck.set(Boolean.FALSE);

        currentDataObject.description = concatForDataValue(currentDataObject.description, descriptionString);
        this.getFlags().wasDescription.set(Boolean.TRUE);
      }
      if (this.getFlags().wasApplication.get()) currentDataObject.application = concatForDataValue(currentDataObject.application, cleanText);
      if (this.getFlags().wasEncumbrance.get()) currentDataObject.encumbrance = concatForDataValue(currentDataObject.encumbrance, cleanText);
      if (this.getFlags().wasTools.get()) currentDataObject.tools = concatForDataValue(currentDataObject.tools, cleanText);
      if (this.getFlags().wasQuality.get()) currentDataObject.quality = concatForDataValue(currentDataObject.quality, cleanText);
      if (this.getFlags().wasFailure.get()) currentDataObject.failure = concatForDataValue(currentDataObject.failure, cleanText);
      if (this.getFlags().wasCriticalSuccess.get())
        currentDataObject.criticalSuccess = concatForDataValue(currentDataObject.criticalSuccess, cleanText);
      if (this.getFlags().wasCriticalFailure.get())
        currentDataObject.criticalFailure = concatForDataValue(currentDataObject.criticalFailure, cleanText);
      if (this.getFlags().wasAdvancementCategory.get())
      {
        currentDataObject.advancementCategory = cleanText.substring(0, 1);
        this.getFlags().wasAdvancementCategory.set(Boolean.FALSE);
      }
    }
  }

  @Override
  protected void concludePredecessor(SkillRaw lastEntry)
  {
  }
}
