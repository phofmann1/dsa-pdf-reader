package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public abstract class DsaConverterStrategy
{
    protected static final String APPLY_TO_PAGE = "applyToPage";

    private final Logger LOGGER = LogManager.getLogger();

    public abstract Map<Integer, List<TextWithMetaInfo>> applyStrategy(Map<Integer, List<TextWithMetaInfo>> resultsByPage, List<Parameter> parameters, String description);

    protected int extractOptionalParameterInt(List<Parameter> parameterMap, String parameterName)
    {
        try
        {
            return extractParameterInt(parameterMap, parameterName);
        } catch (DsaConverterException e)
        {
            LOGGER.info("Optional parameter <" + parameterName + "> was not set. Default <-1> is returned");
        }
        return -1;
    }

    protected int extractParameterInt(List<Parameter> parameterMap, String parameterName) throws DsaConverterException
    {
        String returnValue = this.extractParameterString(parameterMap, parameterName);
        if (returnValue != null && !returnValue.isEmpty()) return Integer.valueOf(returnValue);
        else throw new DsaConverterException(this.genExMsg(parameterName, "not valid"));
    }

    protected String extractParameterString(List<Parameter> parameterMap, String parameterName) throws DsaConverterException
    {
        Parameter param = null;
        try
        {
            param = parameterMap.stream().filter(p -> p.getKey().equals(parameterName))
                .findFirst()
                .get();
        } catch (NoSuchElementException e)
        {
            LOGGER.error(e);
        }
        if (param != null) return param.getValue();
        else throw new DsaConverterException(this.genExMsg(parameterName, "not found"));
    }

    private String genExMsg(String parameterName, String reason)
    {
        return "DsaConverterStrategy("
            + this.getClass().getName()
            + "): Parameter <"
            + parameterName
            + "> - "
            + reason;
    }

    protected void logException(DsaConverterException e)
    {
        LOGGER.error("Strategy[" + this.getClass().getName() + "] not applied");
        LOGGER.error(e.getMessage(), e);
    }

    protected void logApplicationOfStrategy(String description)
    {
        LOGGER.debug("Applying strategy: " + description);
    }
}
