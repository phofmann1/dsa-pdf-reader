package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyAddTextToLine extends DsaConverterStrategy
{
    private static final String LINE = "lineNo";
    private static final String INSERT_AFTER_POSITION = "insertAfterPosition";
    private static final String INSERT_TEXT = "insertText";


    @Override
    public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
    {
        List<TextWithMetaInfo> returnValue = texts;
        try
        {
            int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
            double line = super.extractParameterDouble(parameters, LINE);
            int insertAfterPosition = super.extractParameterInt(parameters, INSERT_AFTER_POSITION);
            String insertText = super.extractParameterString(parameters, INSERT_TEXT);

            logApplicationOfStrategy(description);
            List<TextWithMetaInfo> resultsByPage = texts.stream().filter(t -> t.onPage == applyToPage).collect(Collectors.toList());
            resultsByPage = applyStrategyToPage(resultsByPage, line, insertAfterPosition, insertText);
            returnValue = super.replacePage(texts, applyToPage, resultsByPage);

        }
        catch (DsaConverterException e)
        {
            logException(e);
        }
        return returnValue;
    }

    private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> textList, double line, int insertAfterPosition, String insertText)
    {
        return textList.stream().map(t -> {
            if (t.onLine == line)
            {
                t.text = t.text.substring(0, insertAfterPosition + 1)
                    + insertText
                    + t.text.substring(insertAfterPosition + 1);
            }
            return t;
        }).collect(Collectors.toList());
    }
}
