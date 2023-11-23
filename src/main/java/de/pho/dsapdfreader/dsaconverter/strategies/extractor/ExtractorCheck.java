package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.dsaconverter.model.SkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;

public class ExtractorCheck extends Extractor
{
    public static List<AttributeShort> retrieveCheck(MysticalSkillRaw msr, MysticalSkillCategory category)
    {
        List<AttributeShort> shorts = new ArrayList<>();
        if (category != MysticalSkillCategory.blessing && category != MysticalSkillCategory.trick)
        {

            try
            {
                shorts = extractCheck(msr.check);
            }
            catch (IllegalArgumentException e)
            {
                LOGGER.error(getPrefix(msr.publication, msr.name) + "Attribute (" + msr.check + ") unknown");
            }


        }
        return shorts;
    }

    public static List<AttributeShort> retrieveCheck(SkillRaw raw)
    {
        List<AttributeShort> shorts = new ArrayList<>();
        try
        {
            shorts = extractCheck(raw.check);
        }
        catch (IllegalArgumentException e)
        {
            LOGGER.error("Skill Attributes (" + raw.check + ") unknown");
        }

        return shorts;
    }

    private static List<AttributeShort> extractCheck(String check)
    {
        List<AttributeShort> shorts = new ArrayList<>();

        String extractedCheck = check
            .replaceAll("\\(.*?\\)", "") // remove ZK and SK text
            .replaceAll("\\h", " ")
            .trim();


        for (String shortText : extractedCheck.split("/"))
        {
            shorts.add(AttributeShort.valueOf(shortText.trim()));
        }
        return shorts;
    }
}
