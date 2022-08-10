package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyRemoveLinesFromPage extends DsaConverterStrategy
{

    @Override
    public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
    {
        List<TextWithMetaInfo> returnValue = texts;
        try
        {
            int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
            double fromLine = extractParameterDouble(parameters, FROM_LINE);
            double untilLine = extractOptionalParameterDouble(parameters, UNTIL_LINE);

            logApplicationOfStrategy(description);
            List<TextWithMetaInfo> resultsByPage = texts.stream().filter(t -> t.onPage == applyToPage).collect(Collectors.toList());
            resultsByPage = applyStrategyToPage(resultsByPage, fromLine, untilLine);
            returnValue = replacePage(texts, applyToPage, resultsByPage);

        } catch (DsaConverterException e)
        {
            logException(e);
        }

        return returnValue;
    }

    private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> textList, double fromLine, double untilLine)
    {
        double endLine = untilLine > fromLine ? untilLine : textList.size();
        return textList.stream().filter(t -> t.onLine < fromLine || t.onLine > endLine).collect(Collectors.toList());
    }
}
