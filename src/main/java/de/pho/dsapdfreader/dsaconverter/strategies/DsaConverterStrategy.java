package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public abstract class DsaConverterStrategy
{
    protected static final String APPLY_TO_PAGE = "applyToPage";

    protected static final String INSERT_SMALL = "insertSmall";
    protected static final String FROM_LINE = "fromLine";
    protected static final String UNTIL_LINE = "untilLine";

    protected static final double NEW_LINE_IDX = 0.1;
    protected static final double NEW_LINE_SMALL_IDX = 0.01;

    private final Logger LOGGER = LogManager.getLogger();

    public abstract List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> resultsByPage, List<Parameter> parameters, String description, String publication, TopicEnum topic);

    protected double extractOptionalParameterDouble(List<Parameter> parameterMap, String parameterName)
    {
        String p = this.extractOptionalParameterString(parameterMap, parameterName);
        return (p != null && !p.isEmpty())
            ? Double.valueOf(p)
            : -1;
    }

    protected double extractParameterDouble(List<Parameter> parameterMap, String parameterName) throws DsaConverterException
    {
        String returnValue = this.extractParameterString(parameterMap, parameterName);
        if (returnValue != null && !returnValue.isEmpty()) return Double.valueOf(returnValue);
        else throw new DsaConverterException(this.genExMsg(parameterName, "not valid"));
    }

  protected int extractParameterInt(List<Parameter> parameterMap, String parameterName) throws DsaConverterException
  {
    String returnValue = this.extractParameterString(parameterMap, parameterName);
    if (returnValue != null && !returnValue.isEmpty()) return Integer.valueOf(returnValue);
    else throw new DsaConverterException(this.genExMsg(parameterName, "not valid"));
  }

  protected int extractOptionalParameterInt(List<Parameter> parameterMap, String parameterName) throws DsaConverterException
  {
    try
    {
      return this.extractParameterInt(parameterMap, parameterName);
    }
    catch (DsaConverterException e)
    {
      LOGGER.debug("Optional parameter <" + parameterName + "> was not set. Default <-1> is returned");
    }
    return -1;
  }

  protected boolean extractOptionalParameterBoolean(List<Parameter> parameterMap, String parameterName) throws DsaConverterException
  {
    String p = this.extractOptionalParameterString(parameterMap, parameterName);
    return (p != null && !p.isEmpty())
        ? Boolean.valueOf(p)
        : false;
  }

  protected String extractOptionalParameterString(List<Parameter> parameterMap, String parameterName)
    {
        try
        {
            return extractParameterString(parameterMap, parameterName);
        } catch (DsaConverterException e)
        {
            LOGGER.debug("Optional parameter <" + parameterName + "> was not set. Default <-1> is returned");
        }
        return null;
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
            throw new DsaConverterException(this.genExMsg(parameterName, "not found"));
        }
        if (param != null) return param.getValue();
        else throw new DsaConverterException(this.genExMsg(parameterName, "not found"));
    }

    protected List<TextWithMetaInfo> replacePage(List<TextWithMetaInfo> texts, int applyToPage, List<TextWithMetaInfo> resultsByPage)
    {
        List<TextWithMetaInfo> returnValue = texts.stream().filter(t -> t.onPage != applyToPage).collect(Collectors.toList());
        returnValue.addAll(resultsByPage);
        returnValue = returnValue.stream()
            .sorted((a, b) -> a.sortIndex() < b.sortIndex() ? -1 : 1)
            .collect(Collectors.toList());

        return returnValue;
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

    protected void logException(Class c, Throwable e, String d, String m)
    {
        LOGGER.error("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        LOGGER.error(d);
        LOGGER.error(m);
        LOGGER.error(c, e);
        LOGGER.error("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    }
}
