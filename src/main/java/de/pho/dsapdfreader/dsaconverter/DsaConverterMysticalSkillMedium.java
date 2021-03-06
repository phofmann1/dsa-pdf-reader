package de.pho.dsapdfreader.dsaconverter;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMysticalSkillMedium extends DsaConverter<MysticalSkillRaw>
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected MysticalSkillRaw initializeType()
    {
        return new MysticalSkillRaw();
    }

    @Override
    protected void applyDataValue(MysticalSkillRaw ms, TextWithMetaInfo t, AtomicConverterFlag flags)
    {
        if (ms != null)
        {
            new DsaConverterMysticalSkillSmall().applyDataValue(ms, t, flags);

            if (flags.wasCheck.get()) ms.check = concatForDataValue(ms.check, t.text).replace(":", "").trim();
            if (flags.wasEffect.get()) ms.effect = concatForEffect(ms.effect, t);
            if (flags.wasCastingDuration.get()) ms.castingDuration = concatForDataValue(ms.castingDuration, t.text);
            if (flags.wasCost.get()) ms.cost = concatForDataValue(ms.cost, t.text);
            if (flags.wasCommonness.get()) ms.commonness = concatForDataValue(ms.commonness, t.text);
            if (flags.wasFurtherInformation.get()) ms.furtherInformation = concatForDataValue(ms.furtherInformation, t.text);

            if (flags.wasAdvancementCategory.get())
            {
                ms.advancementCategory = concatForDataValue(ms.advancementCategory, t.text);
                flags.wasAdvancementCategory.set(false);
                flags.wasFurtherInformation.set(true);
            }

            if (flags.wasQs1.get()) ms.qs1 = concatForDataValue(ms.qs1, t.text).replace(":", "").trim();
            if (flags.wasQs2.get()) ms.qs2 = concatForDataValue(ms.qs2, t.text).replace(":", "").trim();
            if (flags.wasQs3.get()) ms.qs3 = concatForDataValue(ms.qs3, t.text);
            if (flags.wasQs4.get()) ms.qs4 = concatForDataValue(ms.qs4, t.text);
            if (flags.wasQs5.get()) ms.qs5 = concatForDataValue(ms.qs5, t.text);
            if (flags.wasQs6.get()) ms.qs6 = concatForDataValue(ms.qs6, t.text);

        } else
        {
            LOGGER.error("msMedium was null: " + t);
        }
    }
}
