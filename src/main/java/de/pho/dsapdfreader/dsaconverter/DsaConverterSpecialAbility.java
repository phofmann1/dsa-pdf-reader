package de.pho.dsapdfreader.dsaconverter;

import java.util.List;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.SpecialAbilityRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsSpecialAbility;

public class DsaConverterSpecialAbility extends DsaConverter<SpecialAbilityRaw, ConverterAtomicFlagsSpecialAbility>
{

  private static final String KEY_RULES = "Regel";
  private static final String KEY_PRECONDITIONS = "Voraussetzungen";
  private static final String KEY_PRECONDITIONS_II = "Voraussetzung";
  private static final String KEY_AP_VALUE = "AP-Wert";
  protected static final String[] KEYS = {
      KEY_RULES,
      KEY_PRECONDITIONS,
      KEY_PRECONDITIONS_II,
      KEY_AP_VALUE
  };
  ConverterAtomicFlagsSpecialAbility flags;

  @Override
  protected String[] getKeys()
  {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsSpecialAbility getFlags()
  {
    if (flags == null)
    {
      this.flags = new ConverterAtomicFlagsSpecialAbility();
    }
    return flags;
  }

  @Override
  protected String getClassName()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<SpecialAbilityRaw> returnValue, TopicConfiguration conf, String cleanText)
  {

    if (!this.getFlags().getFirstFlag().get())
    {
      SpecialAbilityRaw newEntry = new SpecialAbilityRaw();
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
    this.getFlags().wasRules.set(key.trim().equals(KEY_RULES));
    this.getFlags().wasPrecondition.set(key.trim().equals(KEY_PRECONDITIONS) || key.trim().equals(KEY_PRECONDITIONS_II));
    this.getFlags().wasApValue.set(key.trim().equals(KEY_AP_VALUE));
  }

  @Override
  protected void applyDataValue(SpecialAbilityRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    if (currentDataObject != null)
    {

      if ((this.getFlags().wasName.get() || this.getFlags().wasDescription.get()))
        currentDataObject.description = concatForDataValueWithMarkup(currentDataObject.description, cleanText, isBold, isItalic);
      if (this.getFlags().wasRules.get())
        currentDataObject.rules = concatForDataValueWithMarkup(currentDataObject.rules, cleanText, isBold, isItalic);
      if (this.getFlags().wasPrecondition.get()) currentDataObject.preconditions = concatForDataValue(currentDataObject.preconditions, cleanText);
      if (this.getFlags().wasApValue.get()) currentDataObject.ap = concatForDataValue(currentDataObject.ap, cleanText);
    }
  }

  @Override
  protected void concludePredecessor(SpecialAbilityRaw lastEntry)
  {
  }
}
