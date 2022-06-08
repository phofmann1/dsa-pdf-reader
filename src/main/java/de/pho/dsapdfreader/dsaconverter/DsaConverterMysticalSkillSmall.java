package de.pho.dsapdfreader.dsaconverter;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMysticalSkillSmall extends DsaConverter<MysticalSkillRaw>
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
            if (flags.wasName.get()) ms.description = concatForDataValue(ms.description, t.text);
            if (flags.wasRange.get()) ms.range = concatForDataValue(ms.range, t.text);
            if (flags.wasDuration.get()) ms.duration = concatForDataValue(ms.duration, t.text);
            if (flags.wasFeature.get()) ms.feature = concatForDataValue(ms.feature, t.text);
            if (flags.wasTargetCategory.get()) ms.targetCategory = concatForDataValue(ms.targetCategory, t.text);
            if (flags.wasRemarks.get()) ms.remarks = concatForDataValue(ms.remarks, t.text);
        } else
        {
            LOGGER.error("ms was null: " + t);
        }
    }
}
