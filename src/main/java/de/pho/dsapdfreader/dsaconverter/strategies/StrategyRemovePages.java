package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyRemovePages extends DsaConverterStrategy
{

    private static final String UNTIL_PAGE = "untilPage";

    @Override
    public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
    {
        List<TextWithMetaInfo> returnValue = texts;
        try
        {
            int fromPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
            OptionalInt untilPageOptional = super.extractOptionalParameterInt(parameters, UNTIL_PAGE);
            int untilPage = untilPageOptional.isPresent() ? untilPageOptional.getAsInt() : fromPage;

            logApplicationOfStrategy(description);
            returnValue = texts.stream().filter(t -> t.onPage < fromPage || t.onPage > untilPage).collect(Collectors.toList());

        }
        catch (DsaConverterException e)
        {
            logException(e);
        }

        return returnValue;
    }
}
