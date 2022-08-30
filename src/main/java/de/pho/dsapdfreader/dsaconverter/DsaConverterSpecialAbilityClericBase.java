package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.SpecialAbilityRaw;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterSpecialAbilityClericBase extends DsaConverterSpecialAbility
{
    protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
    {
        return !t.isBold && Arrays.stream(getKeys()).noneMatch(k -> k.equals(cleanText));
    }

    protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
    {
        return t.isBold && Arrays.stream(getKeys()).anyMatch(k -> k.equals(cleanText));
    }

    protected boolean validateIsName(TextWithMetaInfo t)
    {
        return t.isBold && !Arrays.asList(getKeys()).contains(t.text);
    }

    @Override
    public List<SpecialAbilityRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> texts, TopicConfiguration conf)
    {
        List<SpecialAbilityRaw> returnValue = new ArrayList<>();

        texts.forEach(t -> {

            String cleanText = t.text.trim();

            boolean isName = validateIsName(t);
            boolean isDataKey = validateIsDataKey(t, cleanText, conf);
            boolean isDataValue = validateIsDataValue(t, cleanText, conf);

            // validate the QS flags, they act differently, because they are also part of the effect
            handleWasNoKeyStrings(this.getFlags(), t);

            // handle name
            if (isName)
            {
                SpecialAbilityRaw newEntry = new SpecialAbilityRaw();
                isDataKey = false;
                this.getFlags().initDataFlags();
                this.getFlags().wasName.set(true);
                newEntry.setName(concatForDataValue(newEntry.getName(), t.text.replace("Probe", "")));
                newEntry.setTopic(conf.topic);
                newEntry.setPublication(conf.publication);
                returnValue.add(newEntry);
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
                applyFlagsForNoKeyStrings(this.getFlags(), t.text);
            }
        });
        return returnValue;
    }
}
