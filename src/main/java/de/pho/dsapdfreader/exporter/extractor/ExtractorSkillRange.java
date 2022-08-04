package de.pho.dsapdfreader.exporter.extractor;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.SkillRange;

public class ExtractorSkillRange extends Extractor
{

    public static SkillRange retrieveSkillRange(MysticalSkillRaw msr)
    {
        SkillRange returnValue = new SkillRange();
        returnValue.rangeUnit = extractUnitFromText(msr.range);
        returnValue.range = extractFirstNumberFromText(msr.range, msr);
        returnValue.isPerQs = msr.range.contains("QS x ");
        returnValue.isRadius = msr.range.contains("Radius");
        returnValue.remarks = extractTextBetweenBrackets(msr.range);

        return returnValue;
    }
}
