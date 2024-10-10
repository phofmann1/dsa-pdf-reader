package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyConcatLinesToFirst extends DsaConverterStrategy {

  final static String SEPARATOR = "seperator";

  @Override
  public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic) {
    List<TextWithMetaInfo> returnValue = texts;
    try {
      int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
      double fromLine = super.extractParameterDouble(parameters, FROM_LINE);
      double untilLine = super.extractParameterDouble(parameters, UNTIL_LINE);
      String seperator = super.extractOptionalParameterString(parameters, SEPARATOR);

          logApplicationOfStrategy(description);
          List<TextWithMetaInfo> resultsByPage = texts.stream().filter(t -> t.onPage == applyToPage).collect(Collectors.toList());
      resultsByPage = applyStrategyToPage(resultsByPage, fromLine, untilLine, seperator);
      returnValue = super.replacePage(texts, applyToPage, resultsByPage);
        }
        catch (DsaConverterException e)
        {
            logException(e);
        }
        return returnValue;
    }

  private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> texts, double fromLine, double untilLine, String seperator) {
    String del = seperator != null ? seperator : "";
    String joinedText = texts.stream()
        .filter(t -> t.onLine >= fromLine && t.onLine <= untilLine)
        .map(t -> t.text.trim())
        .collect(Collectors.joining(del));
    texts.removeIf(t -> t.onLine > fromLine && t.onLine <= untilLine);
    return texts.stream().map(t -> {
      if (t.onLine == fromLine) {
        t.text = joinedText;
            }
            return t;
        }).collect(Collectors.toList());
    }
}
