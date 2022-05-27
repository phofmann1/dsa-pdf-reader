package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyInsertEmptyBold extends DsaConverterStrategy
{
    private static final String INSERT_AFTER = "insertAfterLineNo";

    @Override
    public Map<Integer, List<TextWithMetaInfo>> applyStrategy(Map<Integer, List<TextWithMetaInfo>> resultsByPage, List<Parameter> parameters)
    {
        try
        {
            int insertAfter = extractParameterInt(parameters, INSERT_AFTER);
            int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);

            Map<Integer, List<TextWithMetaInfo>> returnValue = new LinkedHashMap<>();

            resultsByPage.forEach((k, v) -> {
                if (k.intValue() == applyToPage)
                {
                    returnValue.put(k, applyStrategyToPage(v, insertAfter));
                } else returnValue.put(k, v);
            });
            return returnValue;
        } catch (DsaConverterException e)
        {
            logException(e);
        }
        return resultsByPage;
    }

    private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> textList, int insertAfter)
    {
        TextWithMetaInfo emptyBoldLine = new TextWithMetaInfo("", true, false, 1, "");
        textList.add(insertAfter, emptyBoldLine);
        return textList;
    }
}
