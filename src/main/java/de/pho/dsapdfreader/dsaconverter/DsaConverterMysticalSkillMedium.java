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
    protected void applyDataValue(MysticalSkillRaw msr, TextWithMetaInfo t, String cleanText, AtomicConverterFlag flags)
    {
        if (msr != null)
        {
            new DsaConverterMysticalSkillSmall().applyDataValue(msr, t, cleanText, flags);

            if (flags.wasCheck.get()) msr.check = concatForDataValue(msr.check, cleanText).replace(":", "").trim();
            if (flags.wasEffect.get()) msr.effect = concatForDataValueWithMarkup(msr.effect, t, cleanText);
            if (flags.wasCastingDuration.get()) msr.castingDuration = concatForDataValue(msr.castingDuration, cleanText);
            if (flags.wasCost.get()) msr.cost = concatForDataValue(msr.cost, cleanText);
            if (flags.wasCommonness.get()) msr.commonness = concatForDataValue(msr.commonness, cleanText);
            if (flags.wasFurtherInformation.get()) msr.furtherInformation = concatForDataValue(msr.furtherInformation, cleanText);

            if (flags.wasAdvancementCategory.get())
            {
                msr.advancementCategory = concatForDataValue(msr.advancementCategory, cleanText);
                flags.wasAdvancementCategory.set(false);
                flags.wasFurtherInformation.set(true);
            }

            if (flags.wasQs1.get()) msr.qs1 = concatForDataValue(msr.qs1, cleanText).replace(":", "").trim();
            if (flags.wasQs2.get()) msr.qs2 = concatForDataValue(msr.qs2, cleanText).replace(":", "").trim();
            if (flags.wasQs3.get()) msr.qs3 = concatForDataValue(msr.qs3, cleanText);
            if (flags.wasQs4.get()) msr.qs4 = concatForDataValue(msr.qs4, cleanText);
            if (flags.wasQs5.get()) msr.qs5 = concatForDataValue(msr.qs5, cleanText);
            if (flags.wasQs6.get()) msr.qs6 = concatForDataValue(msr.qs6, cleanText);

            if (flags.wasVariants.get()) msr.variantsText = concatForDataValueWithMarkup(msr.variantsText, t, cleanText);

        } else
        {
            LOGGER.error("MysticalSkillRaw was null: " + t.text);
        }
    }
}
