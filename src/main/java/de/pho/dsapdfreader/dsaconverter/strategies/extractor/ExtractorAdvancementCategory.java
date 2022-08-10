package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.AdvancementCategory;

public class ExtractorAdvancementCategory extends Extractor
{
    public static AdvancementCategory retrieveAdvancementCategory(MysticalSkillRaw msr)
    {
        AdvancementCategory returnValue = null;
        try
        {
            returnValue = (msr.advancementCategory == null || msr.advancementCategory.isEmpty())
                ? AdvancementCategory.NONE
                : AdvancementCategory.valueOf(msr.advancementCategory
                .replaceAll(":", "")
                .replaceAll("\\h", " ")
                .trim());
        } catch (IllegalArgumentException e)
        {
            LOGGER.error(getPrefix(msr) + "AdvancementCategory (" + msr.advancementCategory + ") not interpretable");
        }
        return returnValue;
    }
}
