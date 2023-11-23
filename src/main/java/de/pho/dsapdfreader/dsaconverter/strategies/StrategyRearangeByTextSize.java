package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyRearangeByTextSize extends DsaConverterStrategy
{

  private static final String TEXT_SIZE = "textSize";
  private static final String POSITION = "sort";
  private static final String PARAM_VAL_BOTTOM = "bottom";

  @Override
  public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
  {
    List<TextWithMetaInfo> returnValue = texts;
    try
    {
      int textSize = super.extractParameterInt(parameters, TEXT_SIZE);
      String position = super.extractOptionalParameterString(parameters, POSITION);

      logApplicationOfStrategy(description);

      return applyStrategyToPage(texts, textSize, position);
    }
    catch (DsaConverterException e)
    {
      logException(e);
    }
    return returnValue;
  }

  private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> texts, int textSize, String position)
  {
    List<TextWithMetaInfo> returnValue = new ArrayList<>();

    List<TextWithMetaInfo> textsOfSize = texts.stream().filter(t -> t.size == textSize).collect(Collectors.toList());
    List<TextWithMetaInfo> textsRest = texts.stream().filter(t -> t.size != textSize).collect(Collectors.toList());

    if (position != null && position.equals(PARAM_VAL_BOTTOM))
    {
      returnValue.addAll(textsRest);
      returnValue.addAll(textsOfSize);
    }
    else
    {
      returnValue.addAll(textsOfSize);
      returnValue.addAll(textsRest);
    }

    AtomicInteger order = new AtomicInteger(0);
    AtomicInteger currentPage = new AtomicInteger(0);
    returnValue.sort(Comparator.comparingInt(a -> a.onPage));
    returnValue = returnValue.stream()
        .map(t -> {
          if (t.onPage != currentPage.get())
          {
            currentPage.set(t.onPage);
            order.set(0);
          }
          t.order = order.incrementAndGet();
          return t;
        }).collect(Collectors.toList());

    return returnValue;
  }
}
