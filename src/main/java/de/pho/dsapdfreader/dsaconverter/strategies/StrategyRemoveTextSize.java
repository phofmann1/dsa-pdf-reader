package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyRemoveTextSize extends DsaConverterStrategy
{

  private static final String TEXT_SIZE = "textSize";

  @Override
  public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
  {
    List<TextWithMetaInfo> returnValue = texts;
    try
    {
      int textSize = super.extractParameterInt(parameters, TEXT_SIZE);

      logApplicationOfStrategy(description);

      return applyStrategyToPage(texts, textSize);
    }
    catch (DsaConverterException e)
    {
      logException(e);
    }
    return returnValue;
  }

  private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> texts, int textSize)
  {
    texts.removeIf(t -> t.size == textSize);
    return texts;
  }
}
