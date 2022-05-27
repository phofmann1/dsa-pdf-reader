package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategySplitLineAfterPosition extends DsaConverterStrategy
{
    private static final String SPLIT_LINE = "splitLine";
    private static final String SPLIT_AFTER_POSITION = "splitAfterPosition";

    @Override
    public Map<Integer, List<TextWithMetaInfo>> applyStrategy(Map<Integer, List<TextWithMetaInfo>> resultsByPage, List<Parameter> parameters)
    {
        try
        {
            int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
            int splitLine = super.extractParameterInt(parameters, SPLIT_LINE);
            int splitAfterPosition = super.extractParameterInt(parameters, SPLIT_AFTER_POSITION);

            Map<Integer, List<TextWithMetaInfo>> returnValue = new LinkedHashMap<>();

            resultsByPage.forEach((k, v) -> {
                if (k.intValue() == applyToPage)
                {
                    returnValue.put(k, applyStrategyToPage(v, splitLine, splitAfterPosition));
                } else returnValue.put(k, v);
            });

            return returnValue;
        } catch (DsaConverterException e)
        {
            logException(e);
        }
        return resultsByPage;
    }

    private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> textList, int splitLine, int splitAfterPosition)
    {
        TextWithMetaInfo lineToSplit = textList.get(splitLine - 1);

        String newLineText = lineToSplit.text.substring(splitAfterPosition);
        TextWithMetaInfo newLine = new TextWithMetaInfo(
            newLineText,
            lineToSplit.isBold,
            lineToSplit.isItalic,
            lineToSplit.size,
            lineToSplit.font
        );

        lineToSplit.text = lineToSplit.text.substring(0, splitAfterPosition);
        textList.add(splitLine, newLine);
        return textList;
    }
}
