package de.pho.dsapdfreader.dsaconverter;

import static de.pho.dsapdfreader.dsaconverter.DsaConverterMsyticalSkillCommonness.PAT_MYSTICAL_SKILL_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMsyticalSkillElements
{
  public Map<String, String> convertTextWithMetaInfo(List<TextWithMetaInfo> texts)
  {
    Map<String, String> returnValue = new HashMap<>();


    AtomicReference<String> featureTitle = new AtomicReference<>();
    texts.forEach(t -> {
      if (t.size == 1100)
      {
        featureTitle.set(t.text);
      }
      else if (t.size == 700)
      {
        featureTitle.set(featureTitle.get() + t.text);
      }
      else
      {
        extractSeparatedValuesForLine(t).forEach(s -> {
          if (s != null && !s.isEmpty())
          {
            if (returnValue.containsKey(s))
            {
              returnValue.put(s, returnValue.get(s) + "|" + featureTitle.get().replace("–", "").trim());
            }
            else
            {
              returnValue.put(s.replaceAll("\\s\\s", " "), featureTitle.get().replace("–", "").trim());
            }
          }
        });
      }
    });
    return returnValue;
  }

  private List<String> extractSeparatedValuesForLine(TextWithMetaInfo t)
  {
    List<String> returnValue = new ArrayList<>();
    String cleanText = t.text
        .replace("ZauberspruchSeite", "")
        .replace("  ", " ");

    Matcher msNameMatcher = PAT_MYSTICAL_SKILL_NAME.matcher(cleanText);

    while (msNameMatcher.find())
    {
      String key = msNameMatcher.group();
      if (!key.isEmpty())
      {
        returnValue.add(key);
      }
    }
    return returnValue;
  }
}
