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
  private static final int LAST = 999999999;


    @Override
    public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
    {
        List<TextWithMetaInfo> returnValue = texts;
        try
        {
          int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
          double line = super.extractParameterDouble(parameters, LINE);

          int insertAfterPosition = -1;
          try
          {
            insertAfterPosition = super.extractParameterInt(parameters, INSERT_AFTER_POSITION);
          }
          catch (DsaConverterException e)
          {
            insertAfterPosition = LAST;
          }
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
              if (insertAfterPosition < 0)
              {
                t.text = insertText + t.text;

              }
              else if (insertAfterPosition < t.text.length())
              {
                t.text = t.text.substring(0, insertAfterPosition + 1)
                    + insertText
                    + t.text.substring(insertAfterPosition + 1);
              }
              else
              {
                t.text = t.text + insertText;
              }
            }
            return t;
        }).collect(Collectors.toList());
    }
}
