package de.pho.dsapdfreader.exporter.extractor;

import java.util.regex.Matcher;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.CastingDuration;
import de.pho.dsapdfreader.exporter.model.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.Unit;

public class ExtractorCastingDuration extends Extractor
{
    public static CastingDuration retrieveCastingDuration(MysticalSkillRaw msr, MysticalSkillCategory category)
    {
        CastingDuration returnValue = null;
        if (category == MysticalSkillCategory.blessing || category == MysticalSkillCategory.trick)
        {
            returnValue = new CastingDuration();
            returnValue.castingDuration = 1;
            returnValue.castingDurationUnit = Unit.action;
        } else
        {
            Matcher matcher = PAT_LEADING_NUMBER.matcher(msr.castingDuration);
            if (!msr.castingDuration.isEmpty() && matcher.find())
            {
                returnValue = new CastingDuration();
                returnValue.castingDuration = Integer.valueOf(matcher.group().trim());
                returnValue.castingDurationUnit = extractUnitFromText(msr.castingDuration);
            }
        }

        if (returnValue != null && (returnValue.castingDuration <= 0 || returnValue.castingDurationUnit == null))
        {
            LOGGER.error(getPrefix(msr) + "casting Duration not interpretable:\r\n" + msr.castingDuration);
        }

        return returnValue;
    }
}
