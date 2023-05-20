package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractorShieldStructurePoints extends Extractor
{

  private static final Pattern PATTERN_EXTRACT_SHIELD_STRUCTURE_POINTS = Pattern.compile("\\d*(?= Struktur)");

  public static int retrieve(String remarkString)
  {
    int returnValue = 0;
    Matcher matcher = PATTERN_EXTRACT_SHIELD_STRUCTURE_POINTS.matcher(remarkString);
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
