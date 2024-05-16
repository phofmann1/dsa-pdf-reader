package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.ProfessionRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsProfession;
import de.pho.dsapdfreader.exporter.model.enums.ProfessionTypeKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterProfession extends DsaConverter<ProfessionRaw, ConverterAtomicFlagsProfession>
{

  private static final String KEY_AP_VALUE = "AP-Wert";
  private static final String KEY_PRECONDITIONS = "Voraussetzungen";
  private static final String KEY_SPECIAL_ABILITIES = "Sonderfertigkeiten";
  private static final String KEY_SKILLS_COMBAT = "Kampftechniken";
  private static final String KEY_SKILLS = "Talente";
  private static final String KEY_SKILLS_MAGIC = "Zauber";
  private static final String KEY_SKILLS_MAGIC_2 = "Zauber/Magische Handlungen";
  private static final String KEY_SKILLS_CLERIC = "Liturgien";
  private static final String KEY_MERITS_RECOMMENDED = "Empfohlene Vorteile";
  private static final String KEY_FLAWS_RECOMMENDED = "Empfohlene Nachteile";
  private static final String KEY_FLAWS_RECOMMENDED_II = "Geeignete Nachteile";
  private static final String KEY_MERITS_INAPPROPRIATE = "Ungeeignete Vorteile";
  private static final String KEY_FLAWS_INAPPROPRIATE = "Ungeeignete Nachteile";
  private static final String KEY_VARIANTS = "Varianten";
  private static final String KEY_VARIANTS_II = "Varianten:";
  private static final String KEY_VARIANT = "Variante";
  private static final String KEY_EQUIP = "Ausrüstung und Tracht";
  protected static final String[] KEYS = {
      KEY_AP_VALUE,
      KEY_PRECONDITIONS,
      KEY_SPECIAL_ABILITIES,
      KEY_SKILLS_COMBAT,
      KEY_SKILLS,
      KEY_SKILLS_MAGIC,
      KEY_SKILLS_MAGIC_2,
      KEY_SKILLS_CLERIC,
      KEY_MERITS_RECOMMENDED,
      KEY_FLAWS_RECOMMENDED_II,
      KEY_FLAWS_RECOMMENDED,
      KEY_MERITS_INAPPROPRIATE,
      KEY_FLAWS_INAPPROPRIATE,
      KEY_VARIANTS,
      KEY_VARIANTS_II,
      KEY_VARIANT,
      KEY_EQUIP
  };
  private ConverterAtomicFlagsProfession flags;

  @Override
  protected String[] getKeys()
  {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsProfession getFlags()
  {
    if (flags == null)
    {
      this.flags = new ConverterAtomicFlagsProfession();
    }
    return flags;
  }

  @Override
  public List<ProfessionRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> texts, TopicConfiguration conf) {
    List<ProfessionRaw> returnValue = new ArrayList<>();

    AtomicReference<String> currentProfessionName = new AtomicReference<>();
    AtomicReference<Boolean> isNameStarted = new AtomicReference<>(Boolean.FALSE);
    AtomicReference<ProfessionTypeKey> currentProfessionTypeKey = new AtomicReference<>();

    texts.forEach(t -> {

      String cleanText = t.text.trim();

      if (t.size == 1800) {
        switch (cleanText) {
        case "Weltliche Professionen":
          currentProfessionTypeKey.set(ProfessionTypeKey.normal);
          break;
        case "ZaubererprofessionenAnimisten":
          currentProfessionTypeKey.set(ProfessionTypeKey.magical);
          break;
        case "Geweihtenprofessionen (Alveranische Gottheiten)":
          currentProfessionTypeKey.set(ProfessionTypeKey.clerical_alveran);
          break;
        case "Geweihtenprofessionen (Halbgötter)":
          currentProfessionTypeKey.set(ProfessionTypeKey.clerical_halbgötter);
          break;
        case "Geweihtenprofessionen (Außeralveranische Gottheiten)":
          currentProfessionTypeKey.set(ProfessionTypeKey.clerical_außeralveranisch);
          break;
        case "Ordensprofessionen":
          currentProfessionTypeKey.set(ProfessionTypeKey.chapter);
          break;

        }

      }
      boolean isDataKey = validateIsDataKey(t, cleanText, conf);
      boolean isDataValue = validateIsDataValue(t, cleanText, conf);

      boolean isName = validateIsName(t, conf);
      isNameStarted.set(isName);

      //handle start of Profession
      if (t.text.equals("Professionspaket")) {
        ProfessionRaw newEntry = new ProfessionRaw();
        this.getFlags().initDataFlags();
        newEntry.setTopic(conf.topic);
        newEntry.setPublication(conf.publication);
        newEntry.name = currentProfessionName.get();
        newEntry.professionType = currentProfessionTypeKey.get();
        this.getFlags().initDataFlags();
        this.getFlags().wasName.set(true);
        returnValue.add(newEntry);
        currentProfessionName.set(null);
      }

      // handle name
      if (isName) {
        currentProfessionName.set(currentProfessionName.get() != null ? concatForDataValue(currentProfessionName.get(), t.text) : t.text);
      }
      else {
        currentProfessionName.set(null);
      }

      // handle keys
      if (isDataKey) {
        applyFlagsForKey(t.text);
      }

      // handle values
      if (isDataValue)
      {
        applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
        applyFlagsForNoKeyStrings(this.getFlags(), t.text);
      }
    });
    return returnValue;
  }

  private boolean validateIsName(TextWithMetaInfo t, TopicConfiguration conf) {
    return t.size == conf.nameSize;
  }

  @Override
  protected String getClassName()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<ProfessionRaw> returnValue, TopicConfiguration conf, String cleanText)
  {

    if (!this.getFlags().getFirstFlag().get())
    {
      ProfessionRaw newEntry = new ProfessionRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      returnValue.add(newEntry);
    }
    last(returnValue).setName(concatForDataValue(last(returnValue).getName(), cleanText));
  }

  @Override
  protected void applyFlagsForKey(String key)
  {
    this.getFlags().wasName.set(false);
    this.getFlags().wasDescription.set(false);
    this.getFlags().wasPrecondition.set(key.trim().equals(KEY_PRECONDITIONS));
    this.getFlags().wasApValue.set(key.trim().equals(KEY_AP_VALUE));

    this.getFlags().wasSpecialAbilities.set(key.trim().equals(KEY_SPECIAL_ABILITIES));
    this.getFlags().wasSkillCombat.set(key.trim().equals(KEY_SKILLS_COMBAT));
    this.getFlags().wasSkills.set(key.trim().equals(KEY_SKILLS));
    this.getFlags().wasSkillsMagic.set(key.trim().equals(KEY_SKILLS_MAGIC) || key.trim().equals(KEY_SKILLS_MAGIC_2));
    this.getFlags().wasSkillsCleric.set(key.trim().equals(KEY_SKILLS_CLERIC));
    this.getFlags().wasMeritsRecommended.set(key.trim().equals(KEY_MERITS_RECOMMENDED));
    this.getFlags().wasFlawsRecommended.set(key.trim().equals(KEY_FLAWS_RECOMMENDED) || key.trim().equals(KEY_FLAWS_RECOMMENDED_II));
    this.getFlags().wasMeritsInappropriate.set(key.trim().equals(KEY_MERITS_INAPPROPRIATE));
    this.getFlags().wasFlawsInappropriate.set(key.trim().equals(KEY_FLAWS_INAPPROPRIATE));
    this.getFlags().wasVariants.set(key.trim().equals(KEY_VARIANTS) || key.trim().equals(KEY_VARIANTS_II) || key.trim().equals(KEY_VARIANT));
    this.getFlags().wasEquip.set(key.trim().equals(KEY_EQUIP));
  }

  @Override
  protected void applyDataValue(ProfessionRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    if (currentDataObject != null)
    {
      if ((this.getFlags().wasName.get() || this.getFlags().wasDescription.get()))
        currentDataObject.description = concatForDataValueWithMarkup(currentDataObject.description, cleanText, isBold, isItalic);
      if (this.getFlags().wasPrecondition.get()) currentDataObject.preconditions = concatForDataValue(currentDataObject.preconditions, cleanText);
      if (this.getFlags().wasApValue.get()) currentDataObject.ap = concatForDataValue(currentDataObject.ap, cleanText);
      if (this.getFlags().wasSpecialAbilities.get())
        currentDataObject.specialAbilities = concatForDataValue(currentDataObject.specialAbilities, cleanText);
      if (this.getFlags().wasSkillCombat.get()) currentDataObject.skillsCombat = concatForDataValue(currentDataObject.skillsCombat, cleanText);
      if (this.getFlags().wasSkills.get()) currentDataObject.skills = concatForDataValue(currentDataObject.skills, cleanText);
      if (this.getFlags().wasSkillsMagic.get()) currentDataObject.skillsMagic = concatForDataValue(currentDataObject.skillsMagic, cleanText);
      if (this.getFlags().wasSkillsCleric.get()) currentDataObject.skillsCleric = concatForDataValue(currentDataObject.skillsCleric, cleanText);
      if (this.getFlags().wasMeritsRecommended.get())
        currentDataObject.meritsRecommended = concatForDataValue(currentDataObject.meritsRecommended, cleanText);
      if (this.getFlags().wasFlawsRecommended.get())
        currentDataObject.flawsRecommended = concatForDataValue(currentDataObject.flawsRecommended, cleanText);
      if (this.getFlags().wasMeritsInappropriate.get())
        currentDataObject.meritsInappropriate = concatForDataValue(currentDataObject.meritsInappropriate, cleanText);
      if (this.getFlags().wasFlawsInappropriate.get())
        currentDataObject.flawsInappropriate = concatForDataValue(currentDataObject.flawsInappropriate, cleanText);
      if (this.getFlags().wasVariants.get())
        currentDataObject.variants = concatForDataValueWithMarkup(currentDataObject.variants, cleanText, isBold, isItalic);
      if (this.getFlags().wasEquip.get()) currentDataObject.equip = concatForDataValue(currentDataObject.equip, cleanText);
    }
  }


  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return t.size == conf.dataSize && t.isBold && t.text != null && !t.text.isEmpty();
  }

  @Override
  protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return t.size == conf.dataSize && !this.validateIsDataKey(t, cleanText, conf);
  }

  @Override
  protected void concludePredecessor(ProfessionRaw lastEntry) {
  }
}
