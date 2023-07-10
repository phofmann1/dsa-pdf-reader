package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsMysticalSkill;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMysticalSkillActivity_LEGACY extends DsaConverterMysticalSkill
{
  protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return !t.isBold && Arrays.stream(getKeys()).noneMatch(k -> k.equals(cleanText))
        || t.text.startsWith("QS "); // exception for QS lists
  }

  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return t.isBold && Arrays.stream(getKeys()).anyMatch(k -> k.equals(cleanText));
    }

    protected boolean validateIsName(TextWithMetaInfo t)
    {
        return t.text.endsWith("Probe") && t.isBold;
    }

    @Override
    public List<MysticalSkillRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> texts, TopicConfiguration conf)
    {
      List<MysticalSkillRaw> returnValue = new ArrayList<>();
      ConverterAtomicFlagsMysticalSkill flags = new ConverterAtomicFlagsMysticalSkill();

        texts.forEach(t -> {

            String cleanText = t.text.trim();

            boolean isName = validateIsName(t);
            boolean isDataKey = validateIsDataKey(t, cleanText, conf);
            boolean isDataValue = validateIsDataValue(t, cleanText, conf);

            // validate the QS flags, they act differently, because they are also part of the effect
            handleWasNoKeyStrings(flags, t);

            // handle name
            if (isName)
            {
                MysticalSkillRaw newEntry = new MysticalSkillRaw();
                isDataKey = false;
                flags.initDataFlags();
                flags.wasCheck.set(true);
                newEntry.setName(concatForDataValue(newEntry.getName(), t.text.replace("Probe", "")));
                newEntry.setTopic(conf.topic);
                newEntry.setPublication(conf.publication);
                returnValue.add(newEntry);

                if (conf.topic == TopicEnum.CURSES)
                {
                    newEntry.castingDuration = "1 Aktion";
                    newEntry.range = "maximal 64 Schritt";
                    newEntry.duration = "Bis zur Erfüllung der Aufgabe, bis die Hexe den Fluch fallen lässt oder bis zum Tot der Hexe";
                    newEntry.advancementCategory = "B";
                    newEntry.targetCategory = "Kulturschaffende";
                }
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
              applyFlagsForNoKeyStrings(flags, t.text);
            }
        });
        return returnValue;
    }

  @Override
  protected void applyDataValue(MysticalSkillRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    new DsaConverterMysticalSkill().applyDataValue(currentDataObject, cleanText, isBold, isItalic);
    if (flags.wasTalent.get()) currentDataObject.talentKey = concatForDataValue(currentDataObject.talentKey, cleanText).replace(":", "").trim();
  }
}
