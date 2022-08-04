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
    protected void applyDataValue(MysticalSkillRaw ms, TextWithMetaInfo t, String cleanText, AtomicConverterFlag flags)
    {
        if (ms != null)
        {
            new DsaConverterMysticalSkillSmall().applyDataValue(ms, t, cleanText, flags);

            if (flags.wasCheck.get()) ms.check = concatForDataValue(ms.check, cleanText).replace(":", "").trim();
            if (flags.wasEffect.get()) ms.effect = concatForDataValueWithMarkup(ms.effect, t, cleanText);
            if (flags.wasCastingDuration.get()) ms.castingDuration = concatForDataValue(ms.castingDuration, cleanText);
            if (flags.wasCost.get()) ms.cost = concatForDataValue(ms.cost, cleanText);
            if (flags.wasCommonness.get()) ms.commonness = concatForDataValue(ms.commonness, cleanText);
            if (flags.wasFurtherInformation.get()) ms.furtherInformation = concatForDataValue(ms.furtherInformation, cleanText);

            if (flags.wasAdvancementCategory.get())
            {
                ms.advancementCategory = concatForDataValue(ms.advancementCategory, cleanText);
                flags.wasAdvancementCategory.set(false);
                flags.wasFurtherInformation.set(true);
            }

            if (flags.wasQs1.get()) ms.qs1 = concatForDataValue(ms.qs1, cleanText).replace(":", "").trim();
            if (flags.wasQs2.get()) ms.qs2 = concatForDataValue(ms.qs2, cleanText).replace(":", "").trim();
            if (flags.wasQs3.get()) ms.qs3 = concatForDataValue(ms.qs3, cleanText);
            if (flags.wasQs4.get()) ms.qs4 = concatForDataValue(ms.qs4, cleanText);
            if (flags.wasQs5.get()) ms.qs5 = concatForDataValue(ms.qs5, cleanText);
            if (flags.wasQs6.get()) ms.qs6 = concatForDataValue(ms.qs6, cleanText);

            if (flags.wasVariants.get()) ms.variantsText = concatForDataValueWithMarkup(ms.variantsText, t, cleanText);

        } else
        {
            LOGGER.error("msMedium was null: " + t.text);
        }
    }
}
