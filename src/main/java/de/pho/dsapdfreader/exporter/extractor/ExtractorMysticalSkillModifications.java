package de.pho.dsapdfreader.exporter.extractor;

import static de.pho.dsapdfreader.exporter.model.MysticalSkillModification.costReduction;
import static de.pho.dsapdfreader.exporter.model.MysticalSkillModification.enforce;
import static de.pho.dsapdfreader.exporter.model.MysticalSkillModification.fast;
import static de.pho.dsapdfreader.exporter.model.MysticalSkillModification.rangeIncrease;
import static de.pho.dsapdfreader.exporter.model.MysticalSkillModification.slow;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.MysticalSkillModification;

public class ExtractorMysticalSkillModifications extends Extractor
{
    private static final String NOT_MODIFIABLE = "nicht modifizierbar";

    public static MysticalSkillModification[] retrieveAllowedModifications(MysticalSkillRaw msr)
    {
        List<MysticalSkillModification> returnValue = new ArrayList<>();
        if (!msr.cost.contains(NOT_MODIFIABLE))
        {
            returnValue.add(enforce);
            returnValue.add(costReduction);
        }
        if (!msr.range.contains(NOT_MODIFIABLE))
        {
            returnValue.add(rangeIncrease);
        }
        if (!msr.castingDuration.contains(NOT_MODIFIABLE))
        {
            returnValue.add(fast);
            returnValue.add(slow);
        }
        return returnValue.toArray(new MysticalSkillModification[returnValue.size()]);
    }
}
