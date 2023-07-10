package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.dsaconverter.model.SkillRaw;
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
        }
        catch (IllegalArgumentException e)
        {
            LOGGER.error(getPrefix(msr) + "AdvancementCategory (" + msr.advancementCategory + ") not interpretable");
        }
        return returnValue;
    }

    public static AdvancementCategory retrieveAdvancementCategory(SkillRaw raw)
    {
        AdvancementCategory returnValue = null;
        try
        {
            returnValue = extractCat(raw.advancementCategory);
        }
        catch (IllegalArgumentException e)
        {
            LOGGER.error("Skill AdvancementCategory (" + raw.advancementCategory + ") not interpretable");
        }
        return returnValue;
    }

    private static AdvancementCategory extractCat(String advancementCategory)
    {
        return (advancementCategory == null || advancementCategory.isEmpty())
            ? AdvancementCategory.NONE
            : AdvancementCategory.valueOf(advancementCategory
            .replaceAll(":", "")
            .replaceAll("\\h", " ")
            .trim());
    }
}
