package de.pho.dsapdfreader.exporter.extractor;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.AttributeShort;
import de.pho.dsapdfreader.exporter.model.MysticalSkillCategory;

public class ExtractorCheck extends Extractor
{
    public static AttributeShort[] retrieveCheck(MysticalSkillRaw msr, MysticalSkillCategory category)
    {
        List<AttributeShort> shorts = new ArrayList<>();
        String extractedCheck = msr.check
            .replaceAll("\\(.*?\\)", "") // remove ZK and SK text
            .replaceAll("\\h", " ")
            .trim();
        if (category != MysticalSkillCategory.blessing && category != MysticalSkillCategory.trick)
        {
            for (String shortText : extractedCheck.split("/"))
            {
                try
                {
                    shorts.add(AttributeShort.valueOf(shortText.trim()));
                } catch (IllegalArgumentException e)
                {
                    LOGGER.error(getPrefix(msr) + "Attribute (" + shortText + ") unknown");
                }
            }
        }
        return shorts.toArray(new AttributeShort[0]);
    }
}
