package de.pho.dsapdfreader.dsaconverter.strategies;

import java.util.List;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.Parameter;
import de.pho.dsapdfreader.dsaconverter.exceptions.DsaConverterException;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class StrategyRemoveTextSize extends DsaConverterStrategy {

  private static final String TEXT_SIZE = "textSize";
  private static final String IS_ITALIC = "isItalic";
  private static final String IS_BOLD = "isBold";
  private static final String IS_REGULAR = "isRegular";

  @Override
  public List<TextWithMetaInfo> applyStrategy(List<TextWithMetaInfo> texts, List<Parameter> parameters, String description, String publication, TopicEnum topic) {
    List<TextWithMetaInfo> returnValue = texts;
    try {
      int textSize = super.extractParameterInt(parameters, TEXT_SIZE);
      boolean isItalic = super.extractOptionalParameterBoolean(parameters, IS_ITALIC, true);
      boolean isBold = super.extractOptionalParameterBoolean(parameters, IS_BOLD, true);
      boolean isRegular = super.extractOptionalParameterBoolean(parameters, IS_REGULAR, true);

      logApplicationOfStrategy(description);

      return applyStrategyToPage(texts, textSize, isItalic, isBold, isRegular);
    }
    catch (DsaConverterException e) {
      logException(e);
    }
    return returnValue;
  }

  private List<TextWithMetaInfo> applyStrategyToPage(List<TextWithMetaInfo> texts, int textSize, boolean isItalic, boolean isBold, boolean isRegular) {
    texts.removeIf(t -> t.size == textSize && (t.isBold && isBold || t.isItalic && isItalic || !t.isItalic && !t.isBold && isRegular));
    return texts;
  }
}
