package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;

public class ExtractorSkillKey
{
    private ExtractorSkillKey()
    {
    }

    public static List<SkillKey> retrieveSkillKeys(MysticalSkillRaw msr)
    {
        List<SkillKey> returnValue = new ArrayList<>();
        if (msr.talentKey != null)
        {
            if (msr.talentKey.contains("Musizieren")) returnValue.add(SkillKey.MUSIZIEREN);
            if (msr.talentKey.contains("Singen")) returnValue.add(SkillKey.SINGEN);
        }
        return returnValue;
    }
}
