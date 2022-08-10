package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;

public class ExtractorCheck extends Extractor
{
    public static List<AttributeShort> retrieveCheck(MysticalSkillRaw msr, MysticalSkillCategory category)
    {
        List<AttributeShort> shorts = new ArrayList<>();
        String extractedCheck = msr.check
            .replaceAll("\\(.*?\\)", "") // remove ZK and SK text
            .replaceAll("\\h", " ")
            .trim();
        if (category != MysticalSkillCategory.BLESSING && category != MysticalSkillCategory.TRICK)
        {
            for (String shortText : extractedCheck.split("/"))
            {
                try
                {
                    shorts.add(AttributeShort.valueOf(shortText.trim()));
                }
                catch (IllegalArgumentException e)
                {
                    LOGGER.error(getPrefix(msr) + "Attribute (" + shortText + ") unknown");
                }
            }
        }
        return shorts;
    }
}
