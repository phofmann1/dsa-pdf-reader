package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;

public class ExtractorMysticalSkillKey extends Extractor
{
    public static MysticalSkillKey retrieveMysticalSkillKey(MysticalSkillRaw msr, MysticalSkillCategory category)
    {
        MysticalSkillKey returnValue = null;
        String skillKeyString = "";
        try
        {
            returnValue = extractKeyFromText(category, msr.name);
        }
        catch (IllegalArgumentException e)
        {
            LOGGER.error(getPrefix(msr) + "key (" + skillKeyString + ") could not be interpreted.");
        }
        return returnValue;
    }

    public static MysticalSkillKey extractKeyFromText(MysticalSkillCategory category, String name)
    {
        String skillKeyString = category.name()
            .toUpperCase() + "_"
            + name
            .toUpperCase()
            .replaceAll(" ", "_")
            .replaceAll("Ä", "AE")
            .replaceAll("Ö", "OE")
            .replaceAll("Ü", "UE")
            .replaceAll("__", "_")
            .replaceAll("ß", "SS")
            .replaceAll("&", "UND")
            .replaceAll("!", "")
            .replaceAll("\\(", "")
            .replaceAll("\\)", "");
        return MysticalSkillKey.valueOf(skillKeyString);
    }
}
