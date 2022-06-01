package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyRemoveLinesFromPage extends DsaConverterStrategy
{
    private static final String FROM_LINE = "fromLine";
    private static final String UNTIL_LINE = "untilLine";

    @Override
    public Map<Integer, List<TextWithMetaInfo>> applyStrategy(Map<Integer, List<TextWithMetaInfo>> resultsByPage, List<Parameter> parameters, String description)
    {
        try
        {
            int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
            int fromLine = extractParameterInt(parameters, FROM_LINE);
            int untilLine = extractOptionalParameterInt(parameters, UNTIL_LINE);

            Map<Integer, List<TextWithMetaInfo>> returnValue = new LinkedHashMap<>();

            resultsByPage.forEach((k, v) -> {
                if (k.intValue() == applyToPage)
                {
                    logApplicationOfStrategy(description);
                    returnValue.put(k, applyStrategyToPage(v, fromLine, untilLine));
                } else returnValue.put(k, v);
            });
            return returnValue;
        } catch (DsaConverterException e)
        {
            logException(e);
        }
        return resultsByPage;
    }

    private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> textList, int fromLine, int untilLine)
    {
        int endLine = untilLine > fromLine ? untilLine : textList.size();
        textList.subList(fromLine - 1, endLine).clear();
        return textList;
    }
}
