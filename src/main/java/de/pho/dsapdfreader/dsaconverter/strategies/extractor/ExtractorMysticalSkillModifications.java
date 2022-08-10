package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import static de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification.COSTREDUCTION;
import static de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification.ENFORCE;
import static de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification.FAST;
import static de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification.RANGEINCREASE;
import static de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification.SLOW;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification;

public class ExtractorMysticalSkillModifications extends Extractor
{
    private static final String NOT_MODIFIABLE = "nicht modifizierbar";

    public static List<MysticalSkillModification> retrieveAllowedModifications(MysticalSkillRaw msr)
    {
        List<MysticalSkillModification> returnValue = new ArrayList<>();
        if (!msr.cost.contains(NOT_MODIFIABLE))
        {
            returnValue.add(ENFORCE);
            returnValue.add(COSTREDUCTION);
        }
        if (!msr.range.contains(NOT_MODIFIABLE))
        {
            returnValue.add(RANGEINCREASE);
        }
        if (!msr.castingDuration.contains(NOT_MODIFIABLE))
        {
            returnValue.add(FAST);
            returnValue.add(SLOW);
        }
        return returnValue;
    }
}
