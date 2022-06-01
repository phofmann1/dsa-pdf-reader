package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyRemoveSingleLineFromPage extends DsaConverterStrategy
{
    private static final String LINE_NO = "lineNo";

    @Override
    public Map<Integer, List<TextWithMetaInfo>> applyStrategy(Map<Integer, List<TextWithMetaInfo>> resultsByPage, List<Parameter> parameters, String description)
    {
        try
        {
            int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
            int lineNo = extractParameterInt(parameters, LINE_NO);

            Map<Integer, List<TextWithMetaInfo>> returnValue = new LinkedHashMap<>();

            resultsByPage.forEach((k, v) -> {
                if (k.intValue() == applyToPage)
                {
                    logApplicationOfStrategy(description);
                    returnValue.put(k, applyStrategyToPage(v, lineNo));
                } else returnValue.put(k, v);
            });
            return returnValue;
        } catch (DsaConverterException e)
        {
            logException(e);
        }
        return resultsByPage;
    }

    private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> textList, int lineNo)
    {
        textList.remove(lineNo - 1);
        return textList;
    }
}
