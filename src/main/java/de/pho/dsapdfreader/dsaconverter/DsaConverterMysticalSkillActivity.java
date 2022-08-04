package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMysticalSkillActivity extends DsaConverter<MysticalSkillRaw>
{
    @Override
    public List<MysticalSkillRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> texts, TopicConfiguration conf)
    {
        List<MysticalSkillRaw> returnValue = new ArrayList<>();
        AtomicConverterFlag flags = new AtomicConverterFlag();

        texts.forEach(t -> {

            String cleanText = t.text.trim();

            boolean isName = validateIsName(t);
            boolean isDataKey = t.isBold;
            boolean isDataValue = !t.isBold;

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
                applyFlagsForKey(flags, t.text);
            }

            // handle values
            if (isDataValue)
            {
                applyDataValue(last(returnValue), t, cleanText, flags);
            }
        });
        return returnValue;
    }


    @Override
    protected MysticalSkillRaw initializeType()
    {
        return new MysticalSkillRaw();
    }

    @Override
    protected void applyDataValue(MysticalSkillRaw ms, TextWithMetaInfo t, String cleanText, AtomicConverterFlag flags)
    {
        new DsaConverterMysticalSkillMedium().applyDataValue(ms, t, cleanText, flags);
    }

    private boolean validateIsName(TextWithMetaInfo t)
    {
        return t.text.endsWith("Probe") && t.isBold;
    }
}
