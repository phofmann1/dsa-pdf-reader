package de.pho.dsapdfreader.dsaconverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterApplyChangesToMysticalSkills extends DsaConverter<MysticalSkillRaw>
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
            new DsaConverterMysticalSkillMedium().applyDataValue(msr, t, cleanText, flags);
        } else
        {
            LOGGER.error("MysticalSkillRaw was null: %s", t.text);
        }

    }
}
