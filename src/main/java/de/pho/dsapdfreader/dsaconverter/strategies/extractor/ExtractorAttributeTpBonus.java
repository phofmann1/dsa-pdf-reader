package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.exporter.model.AttributeValuePair;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;

public class ExtractorAttributeTpBonus extends Extractor
{
  //(FF|GE|KK) ?\d\d
  static Pattern PAT_ATTRIBUTE_WITH_BONUS = Pattern.compile("(FF|GE|KK) ?\\d\\d");
  //(FF|GE|KK) ?(?=\/)
  static Pattern PAT_ATTRIBUTE_WITHOUT_BONUS = Pattern.compile("(FF|GE|KK) ?(?=\\/)");
  //(FF|GE|KK) ?\/(FF|GE|KK) ?\d\d\/\d\d
  static Pattern PAT_TWO_PAIRS = Pattern.compile("(FF|GE|KK) ?\\/(FF|GE|KK) ?\\d\\d\\/\\d\\d");

  public static List<AttributeValuePair> retrieve(String value) {
    int lastValue = 0;

    List<AttributeValuePair> returnValue = new ArrayList<>();
    Matcher matcher = PAT_TWO_PAIRS.matcher(value);
    if (matcher.find()) {
      String[] pairs = value.split(" ");
      String[] attributes = pairs[0].split("\\/");
      String[] values = pairs[1].split("\\/");

      for (int i = 0; i < 2; i++) {
        AttributeValuePair atb = new AttributeValuePair();
        atb.attribute = AttributeShort.valueOf(attributes[i]);
        atb.minValue = Integer.valueOf(values[i].trim());
        returnValue.add(atb);
      }
    }
    else
    {

      matcher = PAT_ATTRIBUTE_WITH_BONUS.matcher(value);
      while (matcher.find()) {
        AttributeValuePair atb = new AttributeValuePair();
        atb.attribute = AttributeShort.valueOf(matcher.group().substring(0, 2));
        atb.minValue = Integer.valueOf(matcher.group().substring(2).trim());
        lastValue = atb.minValue;
        returnValue.add(atb);
      }


      matcher = PAT_ATTRIBUTE_WITHOUT_BONUS.matcher(value);

      while (matcher.find()) {
        AttributeValuePair atb = new AttributeValuePair();
        atb.attribute = AttributeShort.valueOf(matcher.group());
        atb.minValue = lastValue;
        returnValue.add(atb);
      }
    }
    return returnValue;
  }

}
