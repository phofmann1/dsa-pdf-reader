package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMsyticalSkillCommonness
{
    //[A-Z]([a-z]|( [A-Z])|( [a-z]))*
    private static final Pattern PAT_MYSTICAL_SKILL_NAME = Pattern.compile("[A-ZÜÄÖ]([a-züäöß]|( [A-ZÜÖÄ])|( [a-züöäß]))*");

    public Map<String, List<String>> convertTextWithMetaInfo(List<TextWithMetaInfo> texts)
    {
        Map<String, List<String>> returnValue = new HashMap<>();

        AtomicReference<String> currentTradition = new AtomicReference<>();


        texts.forEach(t -> {
            if (t.isBold)
            {
                currentTradition.set(t.text);
            } else
            {
                handleCommonness(returnValue, t, currentTradition);
            }
        });


        return returnValue;
    }

    private void handleCommonness(Map<String, List<String>> returnValue, TextWithMetaInfo t, AtomicReference<String> currentTradition)
    {
        Matcher msNameMatcher = PAT_MYSTICAL_SKILL_NAME.matcher(t.text);
        while (msNameMatcher.find())
        {
            String key = msNameMatcher.group().replace("Invovatio", "Invocatio").trim();
            if (key != null)
            {
                if (!returnValue.containsKey(key)) returnValue.put(key, new ArrayList<>());

                if (!returnValue.get(key).contains(currentTradition.get()))
                {
                    returnValue.get(key).add(currentTradition.get());
                }
            }
        }
    }
}
