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
            String msg = String.format("%s key (%s) could not be interpreted.", getPrefix(msr), skillKeyString);
            LOGGER.error(msg);
        }
        return returnValue;
    }

    public static MysticalSkillKey extractKeyFromText(MysticalSkillCategory category, String name)
    {
        String skillKeyString = category.name()
            .toUpperCase() + "_"
            + name
            .toUpperCase()
            .replace(" ", "_")
            .replace("Ä", "AE")
            .replace("Ö", "OE")
            .replace("Ü", "UE")
            .replace("__", "_")
            .replace("ß", "SS")
            .replace("&", "UND")
            .replace("!", "")
            .replace("(", "")
            .replace(")", "");
        return MysticalSkillKey.valueOf(skillKeyString);
    }
}
