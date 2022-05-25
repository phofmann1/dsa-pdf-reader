package de.pho.dsapdfreader.dsaconverter;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillMedium;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMysticalSkillMedium extends DsaConverter<MysticalSkillMedium>
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected MysticalSkillMedium initializeType()
    {
        return new MysticalSkillMedium();
    }

    @Override
    protected void applyDataValue(MysticalSkillMedium ms, TextWithMetaInfo t, AtomicConverterFlag flags)
    {
        if (ms != null)
        {
            new DsaConverterMysticalSkillSmall().applyDataValue(ms, t, flags);

            if (flags.wasCheck.get()) ms.check = concatForDataValue(ms.check, t.text);
            if (flags.wasEffect.get()) ms.effect = concatForDataValue(ms.effect, t.text);
            if (flags.wasCastingDuration.get()) ms.castingDuration = concatForDataValue(ms.castingDuration, t.text);
            if (flags.wasCost.get()) ms.cost = concatForDataValue(ms.cost, t.text);
            if (flags.wasCommonness.get()) ms.commonness = concatForDataValue(ms.commonness, t.text);
            if (flags.wasAdvancementCategory.get()) ms.advancementCategory = concatForDataValue(ms.advancementCategory, t.text);

            if (flags.wasQs1.get()) ms.qs1 = concatForDataValue(ms.qs1, t.text);
            if (flags.wasQs1.get()) ms.effect = this.handleQsDescription(flags.wasQs1, ms.effect, t.text, "QS 1: ", flags.wasEffect);

            if (flags.wasQs2.get()) ms.qs2 = concatForDataValue(ms.qs2, t.text);
            if (flags.wasQs2.get()) ms.effect = this.handleQsDescription(flags.wasQs2, ms.effect, t.text, "QS 2: ", flags.wasEffect);

            if (flags.wasQs3.get()) ms.qs3 = concatForDataValue(ms.qs3, t.text);
            if (flags.wasQs3.get()) ms.effect = this.handleQsDescription(flags.wasQs3, ms.effect, t.text, "QS 3: ", flags.wasEffect);

            if (flags.wasQs4.get()) ms.qs4 = concatForDataValue(ms.qs4, t.text);
            if (flags.wasQs4.get()) ms.effect = this.handleQsDescription(flags.wasQs4, ms.effect, t.text, "QS 4: ", flags.wasEffect);

            if (flags.wasQs5.get()) ms.qs5 = concatForDataValue(ms.qs5, t.text);
            if (flags.wasQs5.get()) ms.effect = this.handleQsDescription(flags.wasQs5, ms.effect, t.text, "QS 5: ", flags.wasEffect);

            if (flags.wasQs6.get()) ms.qs6 = concatForDataValue(ms.qs6, t.text);
            if (flags.wasQs6.get()) ms.effect = this.handleQsDescription(flags.wasQs6, ms.effect, t.text, "QS 6: ", flags.wasEffect);


        } else
        {
            LOGGER.error("msMedium was null: " + t);
        }
    }
}
