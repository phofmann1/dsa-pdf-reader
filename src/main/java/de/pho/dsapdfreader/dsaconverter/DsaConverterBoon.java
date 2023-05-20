package de.pho.dsapdfreader.dsaconverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.BoonRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsBoons;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterBoon extends DsaConverter<BoonRaw, ConverterAtomicFlagsBoons>
{
    private static final String KEY_RULES = "Regel";
    private static final String KEY_PRECONDITIONS = "Voraussetzungen";
    private static final String KEY_PRECONDITIONS_2 = "Voraussetzung";
    private static final String KEY_AP_VALUE = "AP-Wert";
    protected static final String[] KEYS = {
        KEY_RULES,
        KEY_PRECONDITIONS,
        KEY_PRECONDITIONS_2,
        KEY_AP_VALUE
    };
    ConverterAtomicFlagsBoons flags;

    @Override
    protected void applyFlagsForKey(String key)
    {
        flags.wasName.set(false);
        flags.wasDescription.set(false);
        flags.wasRules.set(key.trim().equals(KEY_RULES));
        flags.wasPrecondition.set(key.trim().equals(KEY_PRECONDITIONS) || key.trim().equals(KEY_PRECONDITIONS_2));
        flags.wasApValue.set(key.trim().equals(KEY_AP_VALUE));
    }

    @Override
    protected String[] getKeys()
    {
        return KEYS;
    }

    @Override
    protected ConverterAtomicFlagsBoons getFlags()
    {
        if (flags == null)
        {
            this.flags = new ConverterAtomicFlagsBoons();
        }
        return flags;
    }

    @Override
    protected String getClassName()
    {
        return this.getClass().getName();
    }

    @Override
    protected void handleFirstValue(List<BoonRaw> returnValue, TopicConfiguration conf, String cleanText)
    {
        if (!flags.getFirstFlag().get())
        {
            BoonRaw newEntry = new BoonRaw();
            flags.initDataFlags();
            newEntry.setTopic(conf.topic);
            newEntry.setPublication(conf.publication);
            returnValue.add(newEntry);
        }
        last(returnValue).setName(concatForDataValue(last(returnValue).getName(), cleanText));
    }

    @Override
    protected void concludePredecessor(BoonRaw lastEntry)
    {

    }

    @Override
    protected void applyDataValue(BoonRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
    {
      if (currentDataObject != null)
      {

        if ((flags.wasName.get() || flags.wasDescription.get()))
          currentDataObject.description = concatForDataValueWithMarkup(currentDataObject.description, cleanText, isBold, isItalic);
        if (flags.wasRules.get()) currentDataObject.rules = concatForDataValueWithMarkup(currentDataObject.rules, cleanText, isBold, isItalic);
        if (flags.wasPrecondition.get()) currentDataObject.preconditions = concatForDataValue(currentDataObject.preconditions, cleanText);
        if (flags.wasApValue.get()) currentDataObject.ap = concatForDataValue(currentDataObject.ap, cleanText);
      }
    }

  @Override
  protected void applyFlagsForNoKeyStrings(ConverterAtomicFlagsBoons flags, String text)
  {
    flags.wasDescription.set(!flags.wasApValue.get() && !flags.wasPrecondition.get() && !flags.wasRules.get());
  }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return t.isBold && Arrays.stream(this.getKeys()).filter(k -> t.text.contains(k)).collect(Collectors.toList()).size() > 0;
  }
}
