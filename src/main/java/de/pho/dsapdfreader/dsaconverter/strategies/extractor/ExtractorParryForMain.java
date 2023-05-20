package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractorParryForMain extends Extractor
{

  private static final Pattern PATTERN_EXTRACT_PARRY_FOR_MAIN = Pattern.compile("(?<=PA-Bonus\\s\\+)\\d*");

  public static int retrieve(String remarkString)
  {
    int returnValue = 0;
    Matcher matcher = PATTERN_EXTRACT_PARRY_FOR_MAIN.matcher(remarkString);
    while (matcher.find())
    {
      String resultString = matcher.group();
      if (resultString != null && !resultString.isEmpty())
      {
        returnValue = Integer.valueOf(resultString);
      }
    }
    return returnValue;
  }

}
