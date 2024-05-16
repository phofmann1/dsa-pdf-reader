package de.pho.dsapdfreader.dsaconverter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.CurriculumRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsCurriculum;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterCurriculum extends DsaConverter<CurriculumRaw, ConverterAtomicFlagsCurriculum> {
  private static final String KEY_GUIDELINE = "Leitlinie";
  private static final String KEY_SPELL_SELECTION = "Wahlzauberpaket";
  private static final String KEY_SPELL_RESTRICTION = "Eingeschränkte Zauber";
  private static final String KEY_SPELL_CHANGES = "Zauberänderungen:";
  private static final String KEY_ADDITIONAL_SKILLS = "Fertigkeiten (+):";
  private static final String KEY_REMOVED_SKILLS = "Fertigkeiten (–):";
  protected static final String[] KEYS = {
      KEY_GUIDELINE,
      KEY_SPELL_SELECTION,
      KEY_SPELL_RESTRICTION,
      KEY_SPELL_CHANGES,
      KEY_ADDITIONAL_SKILLS,
      KEY_REMOVED_SKILLS
  };
  private static final Logger LOGGER = LogManager.getLogger();
  private final AtomicInteger currentPath = new AtomicInteger(0);
  private List<CurriculumRaw> rawList;
  private ConverterAtomicFlagsCurriculum flags;

  @Override
  public List<CurriculumRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf) {
    rawList = new ArrayList<>();
    String msg = String.format("parse  result to %s", getClassName());
    LOGGER.debug(msg);

    flags = new ConverterAtomicFlagsCurriculum();

    resultList
        .forEach(t -> {

          String cleanText = t.text
              .trim();

          if (cleanText != null && !cleanText.isEmpty()) {
            // validate the flags for conf
            boolean isFirstValue = validateIsFirstValue(t, conf);
            boolean isDataKey = validateIsDataKey(t, cleanText, conf) && !isFirstValue;
            boolean isDataValue = validateIsDataValue(t, cleanText, conf);
            finishPredecessorAndStartNew(isFirstValue, false, rawList, conf, cleanText);

            if (isFirstValue) {
              concludePredecessor(last(rawList));
              CurriculumRaw newRaw = new CurriculumRaw();
              newRaw.name = cleanText.replace("Seminar der elfischen Verständi", "Seminar der elfischen Verständigung und natürlichen Heilung zu Donnerbach");
              newRaw.topic = conf.topic;
              newRaw.publication = conf.publication;
              rawList.add(newRaw);
              this.currentPath.set(0);
            }
            // handle keys
            if (isDataKey) {
              this.applyFlagsForKey(cleanText);
              if (this.getFlags().wasPathName_I.get() && last(rawList).pathName_I != null) { //Korrektur, da nicht alle curriculum sauber in die entfernten Fertigkeiten laufen.
                this.getFlags().wasPathName_I.set(false);
                this.getFlags().wasPathName_II.set(true);
                this.currentPath.set(1);
              }
            }

            // handle values
            if (isDataValue || this.getFlags().wasPathName_I.get() || this.getFlags().wasPathName_II.get()) {
              applyDataValue(last(rawList), cleanText, t.isBold, t.isItalic);
            }
          }

        });
    concludePredecessor(last(rawList)); //finish the last entry in list
    return rawList;
  }

  @Override
  protected String[] getKeys() {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsCurriculum getFlags() {
    return this.flags;
  }

  @Override
  protected String getClassName() {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<CurriculumRaw> returnValue, TopicConfiguration conf, String cleanText) {
    String name = cleanText;

    if (!name.isEmpty()) {
      this.getFlags().wasName.set(true);
    }
  }

  @Override
  protected void applyFlagsForKey(String txt) {
    this.getFlags().initDataFlags();
    switch (txt) {
    case KEY_GUIDELINE -> this.getFlags().wasGuideline.set(Boolean.TRUE);
    case KEY_SPELL_SELECTION -> this.getFlags().wasSpellSelection.set(Boolean.TRUE);
    case KEY_SPELL_RESTRICTION -> this.getFlags().wasSpellRestriction.set(Boolean.TRUE);
    case KEY_SPELL_CHANGES -> {
      if (this.currentPath.get() == 0) {
        this.getFlags().wasSpellChanges_I.set(Boolean.TRUE);
      }
      else {
        this.getFlags().wasSpellChanges_II.set(Boolean.TRUE);
      }
      break;
    }
    case KEY_ADDITIONAL_SKILLS -> {
      if (this.currentPath.get() == 0) {
        this.getFlags().wasAdditionalSkills_I.set(Boolean.TRUE);
      }
      else {
        this.getFlags().wasAdditionalSkills_II.set(Boolean.TRUE);
      }
      break;
    }
    case KEY_REMOVED_SKILLS -> {
      if (this.currentPath.get() == 0) {
        this.getFlags().wasRemovedSkills_I.set(Boolean.TRUE);
        this.currentPath.set(1);
      }
      else {
        this.getFlags().wasRemovedSkills_II.set(Boolean.TRUE);
      }
      break;
    }
    }

    if (txt.endsWith("AP)")) {
      if (this.currentPath.get() == 0) {
        this.getFlags().wasPathName_I.set(Boolean.TRUE);
      }
      else {
        this.getFlags().wasPathName_II.set(Boolean.TRUE);
      }
    }
  }

  @Override
  protected void applyDataValue(CurriculumRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic) {
    if (currentDataObject != null) {
      if (this.getFlags().wasName.get()) currentDataObject.name = concatForDataValue(currentDataObject.name, cleanText);
      if (this.getFlags().wasGuideline.get()) currentDataObject.guideline = concatForDataValue(currentDataObject.guideline, cleanText);
      if (this.getFlags().wasSpellSelection.get()) currentDataObject.spellSelection = concatForDataValue(currentDataObject.spellSelection, cleanText);
      if (this.getFlags().wasSpellRestriction.get())
        currentDataObject.spellRestriction = concatForDataValue(currentDataObject.spellRestriction, cleanText);

      if (this.getFlags().wasPathName_I.get()) currentDataObject.pathName_I = concatForDataValue(currentDataObject.pathName_I, cleanText);
      if (this.getFlags().wasSpellChanges_I.get()) currentDataObject.spellChanges_I = concatForDataValue(currentDataObject.spellChanges_I, cleanText);
      if (this.getFlags().wasAdditionalSkills_I.get())
        currentDataObject.additionalSkills_I = concatForDataValue(currentDataObject.additionalSkills_I, cleanText);
      if (this.getFlags().wasRemovedSkills_I.get())
        currentDataObject.removedSkills_I = concatForDataValue(currentDataObject.removedSkills_I, cleanText);

      if (this.getFlags().wasPathName_II.get()) currentDataObject.pathName_II = concatForDataValue(currentDataObject.pathName_II, cleanText);
      if (this.getFlags().wasSpellChanges_II.get())
        currentDataObject.spellChanges_II = concatForDataValue(currentDataObject.spellChanges_II, cleanText);
      if (this.getFlags().wasAdditionalSkills_II.get())
        currentDataObject.additionalSkills_II = concatForDataValue(currentDataObject.additionalSkills_II, cleanText);
      if (this.getFlags().wasRemovedSkills_II.get())
        currentDataObject.removedSkills_II = concatForDataValue(currentDataObject.removedSkills_II, cleanText);
    }
  }

  @Override
  public boolean validateIsFirstValue(TextWithMetaInfo t, TopicConfiguration conf) {
    return t.isBold && t.size == conf.nameSize && !this.getFlags().wasName.get();
  }


  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return Arrays.stream(KEYS).anyMatch(k -> k.equals(cleanText))
        && (t.isBold || t.isItalic)
        && !validateIsFirstValue(t, conf)
        || cleanText.endsWith("AP)");
  }

  @Override
  protected void concludePredecessor(CurriculumRaw lastEntry) {

  }
}
