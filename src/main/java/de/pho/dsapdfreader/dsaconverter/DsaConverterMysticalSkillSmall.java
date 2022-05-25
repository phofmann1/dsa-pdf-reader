package de.pho.dsapdfreader.dsaconverter;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillSmall;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMysticalSkillSmall extends DsaConverter<MysticalSkillSmall>
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected MysticalSkillSmall initializeType()
    {
        return new MysticalSkillSmall();
    }

    @Override
    protected void applyDataValue(MysticalSkillSmall msSmall, TextWithMetaInfo t, AtomicConverterFlag flags)
    {
        if (msSmall != null)
        {
            if (flags.wasName.get()) msSmall.description = concatForDataValue(msSmall.description, t.text);
            if (flags.wasRange.get()) msSmall.range = concatForDataValue(msSmall.range, t.text);
            if (flags.wasDuration.get()) msSmall.duration = concatForDataValue(msSmall.duration, t.text);
            if (flags.wasFeature.get()) msSmall.feature = concatForDataValue(msSmall.feature, t.text);
            if (flags.wasTargetCategory.get()) msSmall.targetCategory = concatForDataValue(msSmall.targetCategory, t.text);
            if (flags.wasRemarks.get()) msSmall.remarks = concatForDataValue(msSmall.remarks, t.text);
        } else
        {
            LOGGER.error("msSmall was null: " + t);
        }
    }
}
