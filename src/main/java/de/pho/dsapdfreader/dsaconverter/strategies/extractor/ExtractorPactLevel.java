package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExtractorPactLevel {

    public static Integer retrieveDemonic(String pactLevelString, int currentIndex) {
        Integer returnValue = 0;
        Matcher apMatcher = Pattern.compile("\\d+").matcher(pactLevelString);
        List<Integer> results = apMatcher.results()
                .map(MatchResult::group) // Convert MatchResult to string
                .filter(v -> !v.isEmpty() && !v.equals("-")) // filter empty String (Berufsgeheimnis...)
                .map(Integer::valueOf) // convert to Integer
                .filter(v -> v != 0) // filter only valid Results (-1 is always the End of the List)
                .toList();

        if (!results.isEmpty()) {
            returnValue = results.size() > currentIndex ? results.get(currentIndex) : results.get(0);
        }
        return returnValue;
    }

    public static Integer retrieve(String preconditions, int currentIndex) {
        Integer returnValue = 0;
        if (preconditions.contains("pakt der entsprechenden Stufe")) returnValue = currentIndex + 1;
        else {
            Matcher pactLevelMatcher = Pattern.compile("(?<=pakt Stufe )[I]*").matcher(preconditions);
            List<Integer> results = pactLevelMatcher.results()
                    .map(MatchResult::group)
                    .filter(v -> !v.isEmpty() && !v.equals("-"))
                    .map(String::length)
                    .toList();
            if (!results.isEmpty()) {
                returnValue = results.size() > currentIndex ? results.get(currentIndex) : results.get(0);
            }
        }
        return returnValue;
    }

}
