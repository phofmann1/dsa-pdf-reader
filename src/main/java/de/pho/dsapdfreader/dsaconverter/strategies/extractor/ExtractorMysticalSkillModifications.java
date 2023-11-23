package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import static de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification.erzwingen;
import static de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification.kosten_reduzieren;
import static de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification.reichweite_erhöhen;
import static de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification.zauberdauer_erhöhen;
import static de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification.zauberdauer_verkürzen;

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
            returnValue.add(erzwingen);
            returnValue.add(kosten_reduzieren);
        }
        if (!msr.range.contains(NOT_MODIFIABLE))
        {
            returnValue.add(reichweite_erhöhen);
        }
        if (!msr.castingDuration.contains(NOT_MODIFIABLE))
        {
            returnValue.add(zauberdauer_verkürzen);
            returnValue.add(zauberdauer_erhöhen);
        }
        return returnValue;
    }
}
