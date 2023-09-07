package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyInsertLineAfter extends DsaConverterStrategy
{
  private static final String INSERT_AFTER = "insertAfterLineNo";
  private static final String TEXT = "text";
  private static final String SIZE = "size";


  @Override
  public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic)
  {
    List<TextWithMetaInfo> returnValue = texts;
    try
    {
      double insertAfter = extractParameterDouble(parameters, INSERT_AFTER);
      int applyToPage = super.extractParameterInt(parameters, APPLY_TO_PAGE);
      OptionalInt size = super.extractOptionalParameterInt(parameters, SIZE);
      String text = extractParameterString(parameters, TEXT);
      boolean insertSmall = super.extractOptionalParameterBoolean(parameters, INSERT_SMALL);

      logApplicationOfStrategy(description);
      List<TextWithMetaInfo> resultsByPage = texts.stream().filter(t -> t.onPage == applyToPage).collect(Collectors.toList());
      resultsByPage = applyStrategyToPage(resultsByPage, insertAfter, insertSmall, text, size, description);
      returnValue = super.replacePage(texts, applyToPage, resultsByPage);
    }
    catch (DsaConverterException e)
    {
      logException(e);
    }
    return returnValue;
  }


  private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> textList, double insertAfterLine, boolean insertSmall, String text, OptionalInt size, String d)
  {
    if (textList == null || textList.size() == 0) return textList;
    List<TextWithMetaInfo> returnValue = textList;

    OptionalInt addAfterIndexOpt = IntStream.range(0, textList.size())
        .filter(i -> insertAfterLine == textList.get(i).onLine)
        .findFirst();
    int addAfterIndex = addAfterIndexOpt.isPresent() ? addAfterIndexOpt.getAsInt() : -1;
    try
    {
      double idx = insertSmall ? NEW_LINE_SMALL_IDX : NEW_LINE_IDX;
      TextWithMetaInfo newLine = new TextWithMetaInfo(
          text,
          textList.get(addAfterIndex).isBold,
          textList.get(addAfterIndex).isItalic,
          size.isPresent() ? size.getAsInt() : textList.get(addAfterIndex).size,
          textList.get(addAfterIndex).font,
          textList.get(addAfterIndex).onPage,
          textList.get(addAfterIndex).onLine + idx,
          textList.get(addAfterIndex).publication
      );


      newLine.order = textList.get(addAfterIndex).order + idx;
      returnValue.add(addAfterIndex, newLine);
    }
    catch (IndexOutOfBoundsException e)
    {
      String msg = "insertAfter line(" + insertAfterLine + ") with index(" + addAfterIndex + ") outside pageSize(" + textList.size() + ")";
      super.logException(this.getClass(), e, d, msg);
    }

    AtomicInteger order = new AtomicInteger(0);

    returnValue.sort((a, b) -> a.onPage < b.onPage ? -1 : 1);
    returnValue.stream().map(r -> {
      r.order = order.incrementAndGet();
      return r;
    });
    return returnValue;
  }
}
