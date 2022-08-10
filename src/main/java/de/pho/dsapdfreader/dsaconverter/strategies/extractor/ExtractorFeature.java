package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillFeature;

public class ExtractorFeature extends Extractor
{
    protected static final Pattern PAT_COMMON_CLERIC = Pattern.compile("allgemein(?! \\(Schamanenritus\\))");
    protected static final Pattern PAT_COMMON_SHAMAN = Pattern.compile("allgemein \\(Schamanenritus\\)");
    protected static final Pattern PAT_NAMELESS = Pattern.compile("Namenloser");

    public static List<MysticalSkillFeature> retrieveFeatures(MysticalSkillRaw msr)
    {
        return isClerical(msr.topic)
            ? retrieveFeaturesCleric(msr)
            : retrieveFeatureMagic(msr.feature, msr.name, msr.publication);
    }

    protected static List<MysticalSkillFeature> retrieveFeatureMagic(String feature, String name, String publication)
    {
        List<MysticalSkillFeature> returnValue = new ArrayList<>();
        MysticalSkillFeature msFeature = MysticalSkillFeature.fromString(feature);
        if (msFeature == null) LOGGER.error("Feature (" + feature + ") could not be interpreted.");
        else returnValue.add(msFeature);
        return returnValue;
    }


    protected static List<MysticalSkillFeature> retrieveFeaturesCleric(MysticalSkillRaw msr)
    {
        String clericalFeatureString = (msr.commonness == null || msr.commonness.isEmpty()) ? msr.feature : msr.commonness;
        List<MysticalSkillFeature> returnValue = extractFeaturesFromString(clericalFeatureString).stream()
            .map(MysticalSkillFeature::fromString)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (returnValue.size() == 0)
            LOGGER.error(getPrefix(msr) + "Feature (" + clericalFeatureString + ") could not be interpreted.");

        return returnValue;

    }

    protected static List<String> extractFeaturesFromString(String clericalFeatureString)
    {
        List<String> returnValue = new ArrayList<>();

        Matcher matchParenthesesContent = PAT_CONTENT_PARENTHESES.matcher(clericalFeatureString);
        Matcher matchCommon = PAT_COMMON_CLERIC.matcher(clericalFeatureString);
        Matcher matchShaman = PAT_COMMON_SHAMAN.matcher(clericalFeatureString);
        Matcher matchNameless = PAT_NAMELESS.matcher(clericalFeatureString);

        if (matchCommon.find()) returnValue.add("allgemein");
        if (matchShaman.find()) returnValue.add("allgemein Schamanenritus");
        if (matchNameless.find()) returnValue.add("Namenloser");

        while (matchParenthesesContent.find())
        {
            String featureString = matchParenthesesContent.group(1);
            List<String> features = Arrays.stream(featureString.split(",| und | oder "))
                .map(String::trim)
                .filter(e -> !e.equalsIgnoreCase("Schamanenritus"))
                .collect(Collectors.toList());
            returnValue.addAll(features);
        }
        return returnValue;
    }


}
