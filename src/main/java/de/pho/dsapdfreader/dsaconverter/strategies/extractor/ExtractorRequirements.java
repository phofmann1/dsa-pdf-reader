package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.tools.roman.RomanNumberHelper;

public class ExtractorRequirements extends Extractor
{
  public static Map<String, String> extractLevelRequirementMap(String reqText)
  {
    String regex = "Stufe( ?(VII|VI|V(?![a-ü])|IV|III|II|I)\\/?)+:?";
    Map<String, String> resultMap = new HashMap<>();
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(reqText);

    List<String> keys = new ArrayList<>();
    while (matcher.find())
    {
      keys.add(matcher.group());
    }

    if (keys.size() > 0)
    {
      int offset = reqText.indexOf((keys.get(0)));
      if (offset > 0)
      {
        resultMap.put("all", reqText.substring(0, offset));
      }
      for (int i = 0; i < keys.size() - 1; i++)
      {
        String key = keys.get(i);
        String value = reqText.substring(offset + key.length(), reqText.indexOf(keys.get(i + 1)));
        offset += key.length() + value.length();
        String cleanedKey = extractCleanedKey(key);
        resultMap.put(cleanedKey, value);
      }
      String lastKey = keys.get(keys.size() - 1);
      String lastValue = reqText.substring(reqText.indexOf(lastKey) + lastKey.length());
      resultMap.put(extractCleanedKey(lastKey), lastValue);
    }
    else
    {
      resultMap.put("all", reqText);
    }
    return resultMap;
  }

  public static String extractRequirementsStringForLevel(Map<String, String> preconditionMap, int levels, int currentLevel)
  {
    String returnValue = preconditionMap.get("all");
    if (levels > 0)
    {
      returnValue += " " + preconditionMap.get(RomanNumberHelper.intToRoman(currentLevel + 1));
    }
    return returnValue.replaceAll("null(?=$| )", " ");
  }

  private static String extractCleanedKey(String key)
  {
    Matcher m = Pattern.compile("(IV|V?I{1,3}|V)(?=( |:|<|$))").matcher(key);
    return (m.find())
        ? m.group()
        : key;
  }
}
