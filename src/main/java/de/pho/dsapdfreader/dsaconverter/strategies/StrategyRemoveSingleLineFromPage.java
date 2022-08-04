package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyRemoveSingleLineFromPage extends DsaConverterStrategy
{
    private static final String LINE_NO = "lineNo";

    @Override
    public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
    {
        List<TextWithMetaInfo> returnValue = texts;
        try
        {
            int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
            double lineNo = extractParameterDouble(parameters, LINE_NO);
            logApplicationOfStrategy(description);

            returnValue = texts.stream().filter(t -> t.onPage != applyToPage || t.onLine != lineNo).collect(Collectors.toList());

        } catch (DsaConverterException e)
        {
            logException(e);
        }
        return returnValue;
    }
}
