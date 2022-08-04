package de.pho.dsapdfreader.exporter.extractor;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.MysticalSkillKey;

public class ExtractorMysticalSkillKey extends Extractor
{
    public static MysticalSkillKey retrieveMysticalSkillKey(MysticalSkillRaw msr, MysticalSkillCategory category)
    {
        MysticalSkillKey returnValue = null;
        String skillKeyString = "";
        try
        {
            skillKeyString = category.name()
                .toLowerCase() + "_"
                + msr.name
                .toLowerCase()
                .replaceAll(" ", "_")
                //.replaceAll("ä", "ae")
                //.replaceAll("ö", "oe")
                //.replaceAll("ü", "ue")
                .replaceAll("__", "_")
                .replaceAll("ß", "ss")
                .replaceAll("&", "und")
                .replaceAll("!", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "");
            returnValue = MysticalSkillKey.valueOf(skillKeyString);
        } catch (IllegalArgumentException e)
        {
            LOGGER.error(getPrefix(msr) + "key (" + skillKeyString + ") could not be interpreted.");
        }
        return returnValue;
    }
}
