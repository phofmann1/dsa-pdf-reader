package de.pho.dsapdfreader.exporter.extractor;

import java.util.Arrays;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.TraditionKey;

public class ExtractorTradtion extends Extractor
{
    public static TraditionKey[] retrieveTraditions(MysticalSkillRaw msr, MysticalSkillCategory category)
    {
        //handle two Marbo ceremonies with wrong assignment (commonness is printed in feature:
        String cTxt = (category == MysticalSkillCategory.ceremony && msr.commonness == null)
            ? msr.feature
            : msr.commonness;
        if (cTxt == null || cTxt.isEmpty()) return new TraditionKey[]{TraditionKey.all};
        String[] tTrads = cTxt.split(REG_COMMAS_OR_UND_NOT_IN_BRACKETS);
        return Arrays.stream(tTrads)
            .map(t -> extractTraditionFromText(t))
            .toArray(TraditionKey[]::new);
    }
}
