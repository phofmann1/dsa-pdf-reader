package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExtractorVolume extends Extractor
{
  private static final Pattern VOLUME = Pattern.compile("\\d+(?= Punkt|Punkt| pro Stufe| Volumen|\\/)");

  public static int retrieve(String volumeString, int currentIndex)
  {
    Integer returnValue = 0;
    Matcher apMatcher = VOLUME.matcher(volumeString);
    List<Integer> results = apMatcher.results()
        .map(MatchResult::group) // Convert MatchResult to string
        .filter(v -> !v.isEmpty()) // filter empty String (Berufsgeheimnis...)
        .map(Integer::valueOf) // convert to Integer
        .filter(v -> v > 0) // filter only valid Results (-1 is always the End of the List)
        .collect(Collectors.toList());

    if (results.size() > 0)
    {
      returnValue = results.size() > currentIndex ? results.get(currentIndex) : results.get(0);
    }
    return returnValue;
  }
}
