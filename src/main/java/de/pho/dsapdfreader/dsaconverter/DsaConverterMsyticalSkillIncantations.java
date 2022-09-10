package de.pho.dsapdfreader.dsaconverter;

import static de.pho.dsapdfreader.dsaconverter.DsaConverterMsyticalSkillCommonness.PAT_MYSTICAL_SKILL_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMsyticalSkillIncantations
{
  public Map<String, String> convertTextWithMetaInfo(List<TextWithMetaInfo> texts)
  {
    Map<String, String> returnValue = new HashMap<>();


    List<String> values = new ArrayList<>();
    texts.forEach(t -> values.addAll(extractSeparatedValuesForLine(t)));

    AtomicReference<String> currentSpell = new AtomicReference<>();
    values.forEach(v -> {
      if (currentSpell.get() == null || currentSpell.get().isEmpty())
      {
        currentSpell.set(v);
      }
      else
      {
        returnValue.put(currentSpell.get().replaceAll("\\s\\s", " "), v);
        currentSpell.set(null);
      }
    });
    return returnValue;
  }

  private List<String> extractSeparatedValuesForLine(TextWithMetaInfo t)
  {
    List<String> returnValue = new ArrayList<>();
    String cleanText = t.text
        .replace("ZauberspruchReimformel", "")
        .replace("RitualReimformel", "")
        .replace("ZauberZhayad-Formel", "")
        .replace("RitualZhayad-Formel", "")
        .replace("Desctructibo", "Destructibo")
        .replace("Infintum", "Infinitum")
        .replace("Karnifilo", "Karnifilio")
        .replace("Stumgebrüll", "Sturmgebrüll")
        .replace("  ", " ")
        .replace("KrötenzungeMit", "Krötenzunge - Mit");

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
