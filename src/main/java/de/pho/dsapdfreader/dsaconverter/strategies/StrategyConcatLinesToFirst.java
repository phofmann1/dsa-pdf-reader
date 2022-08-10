package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyConcatLinesToFirst extends DsaConverterStrategy
{

    @Override
    public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
    {
        List<TextWithMetaInfo> returnValue = texts;
        try
        {
            int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
            int fromLine = super.extractParameterInt(parameters, FROM_LINE);
            int untilLine = super.extractParameterInt(parameters, UNTIL_LINE);

            logApplicationOfStrategy(description);
            List<TextWithMetaInfo> resultsByPage = texts.stream().filter(t -> t.onPage == applyToPage).collect(Collectors.toList());
            resultsByPage = applyStrategyToPage(resultsByPage, fromLine, untilLine);
            returnValue = super.replacePage(texts, applyToPage, resultsByPage);
        }
        catch (DsaConverterException e)
        {
            logException(e);
        }
        return returnValue;
    }

    private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> texts, int fromLine, int untilLine)
    {
        String joinedText = texts.stream()
            .filter(t -> t.onLine >= fromLine && t.onLine <= untilLine)
            .map(t -> t.text.trim())
            .collect(Collectors.joining(""));
        texts.removeIf(t -> t.onLine > fromLine && t.onLine <= untilLine);
        return texts.stream().map(t -> {
            if (t.onLine == fromLine)
            {
                t.text = joinedText;
            }
            return t;
        }).collect(Collectors.toList());
    }
}
