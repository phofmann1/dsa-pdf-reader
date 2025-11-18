package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyReplaceStringInLine extends DsaConverterStrategy
{
  private static final String ON_LINE = "onLine";
  private static final String FROM_LINE = "fromLine";
  private static final String UNTIL_LINE = "untilLine";
  private static final String OLD_TEXT = "oldText";
  private static final String NEW_TEXT = "newText";

  private static final String SIZE = "size";
  private static final String IS_BOLD = "isBold";


  @Override
  public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
  {
    List<TextWithMetaInfo> returnValue = texts;
    try
    {
      Boolean isBold = null;
      int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
      double onLine = super.extractOptionalParameterDouble(parameters, ON_LINE);
      double fromLine = super.extractOptionalParameterDouble(parameters, FROM_LINE);
      double untilLine = super.extractOptionalParameterDouble(parameters, UNTIL_LINE);
      String oldText = extractOptionalParameterString(parameters, OLD_TEXT);
      String newText = extractOptionalParameterString(parameters, NEW_TEXT);
      if (parameters.stream().anyMatch(p -> p.getKey().equals(IS_BOLD)))
      {
        isBold = extractOptionalParameterBoolean(parameters, IS_BOLD, false);
      }
      OptionalInt newSize = extractOptionalParameterInt(parameters, SIZE);

      logApplicationOfStrategy(description);
      List<TextWithMetaInfo> resultsByPage = texts.stream().filter(t -> t.onPage == applyToPage).collect(Collectors.toList());
      resultsByPage = applyStrategyToPage(resultsByPage, onLine, fromLine, untilLine, oldText, newText, newSize, isBold, description);
      returnValue = super.replacePage(texts, applyToPage, resultsByPage);
    }
    catch (DsaConverterException e)
    {
      logException(e);
    }
    return returnValue;
  }

  private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> textList, double onLine, double fromLine, double untilLine, String oldText, String newText, OptionalInt newSize, Boolean isBold, String description)
  {
    return textList.stream()
        .map(t -> (t.onLine == onLine || t.onLine >= fromLine && t.onLine <= untilLine) ? changeText(t, oldText, newText, newSize, isBold) : t).collect(Collectors.toList());

  }

  private TextWithMetaInfo changeText(TextWithMetaInfo t, String oldText, String newText, OptionalInt newSize, Boolean isBold)
  {
    if (oldText != null && newText != null)
    {
      t.text = t.text.replace(oldText, newText);
    }

    if (newSize.isPresent())
    {
      t.size = newSize.getAsInt();
    }

    if (isBold != null)
    {
      t.isBold = isBold.booleanValue();
    }
    return t;
  }
}
