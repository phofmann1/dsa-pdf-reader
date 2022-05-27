package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyPutToEndOfPage extends DsaConverterStrategy
{
    private static final String START_ENTRY_LINE_NO = "startEntryLineNo";
    private static final String END_ENTRY_LINE_NO = "endEntryLineNo";

    @Override
    public Map<Integer, List<TextWithMetaInfo>> applyStrategy(Map<Integer, List<TextWithMetaInfo>> resultsByPage, List<Parameter> parameters)
    {
        try
        {
            int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
            int startEntryLineNo = super.extractParameterInt(parameters, START_ENTRY_LINE_NO);
            int endEntryLineNo = super.extractParameterInt(parameters, END_ENTRY_LINE_NO);

            Map<Integer, List<TextWithMetaInfo>> returnValue = new LinkedHashMap<>();

            resultsByPage.forEach((k, v) -> {
                if (k.intValue() == applyToPage)
                {
                    returnValue.put(k, applyStrategyToPage(v, startEntryLineNo, endEntryLineNo));
                } else returnValue.put(k, v);
            });

            return returnValue;
        } catch (DsaConverterException e)
        {
            logException(e);
        }
        return resultsByPage;
    }

    private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> textList, int startLinNo, int endLineNo)
    {
        TextWithMetaInfo current;

        List<TextWithMetaInfo> newList = new ArrayList<>();
        List<TextWithMetaInfo> moveList = new ArrayList();

        boolean started;
        boolean stopped;

        for (int i = 0; i < textList.size(); i++)
        {
            current = textList.get(i);
            started = i >= startLinNo - 1;
            stopped = i > endLineNo - 1;
            if (!started || stopped)
            {
                newList.add(current);
            } else
            {
                moveList.add(current);
            }
        }

        newList.addAll(moveList);
        return newList;
    }
}
