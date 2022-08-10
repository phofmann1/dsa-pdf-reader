package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyPutToEndOfPage extends DsaConverterStrategy
{
    private static final String START_ENTRY_LINE_NO = "startEntryLineNo";
    private static final String END_ENTRY_LINE_NO = "endEntryLineNo";

    @Override
    public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
    {
        List<TextWithMetaInfo> returnValue = texts;
        try
        {
            int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
            double startEntryLineNo = super.extractParameterDouble(parameters, START_ENTRY_LINE_NO);
            double endEntryLineNo = super.extractParameterDouble(parameters, END_ENTRY_LINE_NO);

            logApplicationOfStrategy(description);
            List<TextWithMetaInfo> resultsByPage = texts.stream().filter(t -> t.onPage == applyToPage).collect(Collectors.toList());
            resultsByPage = applyStrategyToPage(resultsByPage, startEntryLineNo, endEntryLineNo);

            returnValue = super.replacePage(texts, applyToPage, resultsByPage);

        } catch (DsaConverterException e)
        {
            logException(e);
        }
        return returnValue;
    }

    private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> textList, double startLinNo, double endLineNo)
    {

        AtomicInteger order = new AtomicInteger(0);
        List<TextWithMetaInfo> newList = textList.stream()
            .filter(t -> t.onLine < startLinNo || t.onLine > endLineNo)
            .map(t -> {
                t.order = order.incrementAndGet();
                return t;
            })
            .collect(Collectors.toList());


        List<TextWithMetaInfo> moveList = textList.stream()
            .filter(t -> t.onLine >= startLinNo && t.onLine <= endLineNo)
            .map(t -> {
                t.order = order.incrementAndGet();
                return t;
            })
            .collect(Collectors.toList());

        newList.addAll(moveList);
        return newList;
    }
}
