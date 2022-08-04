package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategySplitLineAfterPosition extends DsaConverterStrategy
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SPLIT_LINE = "splitLine";
    private static final String SPLIT_AFTER_POSITION = "splitAfterPosition";


    @Override
    public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
    {
        List<TextWithMetaInfo> returnValue = texts;
        try
        {
            int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
            double splitLine = super.extractParameterDouble(parameters, SPLIT_LINE);
            int splitAfterPosition = super.extractParameterInt(parameters, SPLIT_AFTER_POSITION);
            boolean insertSmall = super.extractOptionalParameterBoolean(parameters, INSERT_SMALL);

            logApplicationOfStrategy(description);
            List<TextWithMetaInfo> resultsByPage = texts.stream().filter(t -> t.onPage == applyToPage).collect(Collectors.toList());
            resultsByPage = applyStrategyToPage(resultsByPage, splitLine, splitAfterPosition, insertSmall, description);
            returnValue = super.replacePage(texts, applyToPage, resultsByPage);

        } catch (DsaConverterException e)
        {
            logException(e);
        }
        return returnValue;
    }

    private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> textList, double splitLine, int splitAfterPosition, boolean insertSmall, String d)
    {
        try
        {
            Optional<TextWithMetaInfo> lineToSplitOptional = textList.stream().filter(t -> t.onLine == splitLine).findFirst();
            if (lineToSplitOptional.isPresent())
            {
                TextWithMetaInfo lineToSplit = lineToSplitOptional.get();
                try
                {
                    String newLineText = lineToSplit.text.substring(splitAfterPosition);
                    double idx = insertSmall ? NEW_LINE_SMALL_IDX : NEW_LINE_IDX;
                    TextWithMetaInfo newLine = new TextWithMetaInfo(
                        newLineText,
                        lineToSplit.isBold,
                        lineToSplit.isItalic,
                        lineToSplit.size,
                        lineToSplit.font,
                        lineToSplit.onPage,
                        lineToSplit.onLine + idx,
                        lineToSplit.publication
                    );

                    newLine.order = lineToSplit.order + idx;

                    lineToSplit.text = lineToSplit.text.substring(0, splitAfterPosition);
                    textList.add((int) splitLine, newLine);
                } catch (StringIndexOutOfBoundsException e)
                {
                    String msg = "Split Line(" + splitLine + " of " + textList.size() + ") on Position (" + splitAfterPosition + " of " + lineToSplit.text.length() + "): \r\n\t" + lineToSplit.text;
                    super.logException(this.getClass(), e, d, msg);
                }
            } else
            {
                LOGGER.debug("Line(" + splitLine + ") not existing in List. REASON: dropped by previous rule?");
            }
        } catch (IndexOutOfBoundsException e)
        {
            String msg = "Split Line does not exist(" + splitLine + " of " + textList.size() + ")";
            super.logException(this.getClass(), e, d, msg);
        }
        return textList;
    }
}
