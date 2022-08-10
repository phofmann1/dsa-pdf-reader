package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;
import de.pho.dsapdfreader.exporter.model.enums.TraditionSubKey;
import de.pho.dsapdfreader.tools.csv.ValueMapperTool;

public class ExtractorTradtion extends Extractor
{
    public static List<TraditionKey> retrieveTraditions(MysticalSkillRaw msr, MysticalSkillCategory category)
    {
        //handle two Marbo ceremonies with wrong assignment (commonness is printed in feature:
        String cTxt = (category == MysticalSkillCategory.CEREMONY && msr.commonness == null)
            ? msr.feature
            : msr.commonness;
        if (cTxt == null || cTxt.isEmpty())
        {
            return Arrays.stream(new TraditionKey[]{TraditionKey.ALL}).collect(Collectors.toList());
        }
        String[] tTrads = cTxt.split(REG_COMMAS_OR_UND_NOT_IN_BRACKETS);
        return Arrays.stream(tTrads)
            .map(Extractor::extractTraditionFromText)
            .collect(Collectors.toList());
    }

    public static List<TraditionSubKey> retrieveTraditionSubs(MysticalSkillRaw msr)
    {
        String[] tTrads = msr.commonness.split(REG_COMMAS_OR_UND_NOT_IN_BRACKETS);
        return Arrays.stream(tTrads)
            .map(t -> TraditionSubKey.valueOf(ValueMapperTool.mapStringToEnumName(
                t.toLowerCase()
                    .replace("derwische", "derwisch")
            )))
            .collect(Collectors.toList());
    }


}
