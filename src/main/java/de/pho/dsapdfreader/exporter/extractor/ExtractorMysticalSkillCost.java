package de.pho.dsapdfreader.exporter.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.Cost;

public class ExtractorMysticalSkillCost extends Extractor
{
    // ^\d*(?= (AsP|KaP)(?! (pro|für|bzw\.)))
    protected static final Pattern PAT_COST_BASE = Pattern.compile(
        "^\\d*" + //leading digits in string
            "(?= (AsP|KaP)" + //followed by "AsP" or "KaP"
            "(?! (pro|für|bzw\\.)))" //not followed by "pro" or "für" or "bzw."
    );
    // \d*(?= (AsP|KaP) pro)
    protected static final Pattern PAT_COST_PLUS = Pattern.compile(
        "\\d*" + //digit
            "(?= (AsP|KaP) pro)" //followed by "AsP pro" or "KaP pro"
    );
    //(?<= (AsP|KaP) (pro|für) )\d*( )*\p{L}*
    protected static final Pattern PAT_COST_PLUS_UNIT = Pattern.compile(
        "(?<= (AsP|KaP) (pro|für) )" + //preceded by "AsP|KaP pro|für "
            "\\d*" + //digits (amount of units)
            "( )*" + // zero to n " "
            "\\p{L}*" + //all Latin letters (unit)
            "( )*" + // zero to n " "
            "\\p{L}*" //all Latin letters (allow second word)
    );
    //mindestens (jedoch ){0,1}\d* (AsP|KaP)
    protected static final Pattern PAT_COST_MIN = Pattern.compile(
        "mindestens (jedoch ){0,1}" + // leading with "mindestens (jedoch )"
            "\\d*" + //any digits
            " (AsP|KaP)" //followed by " AsP" or " KaP"
    );
    //(davon){0,1} \\d*( (AsP|KaP)){0,1}( davon){0,1} permanent
    protected static final Pattern PAT_COST_PERMANENT = Pattern.compile(
        "(davon){0,1} " + // case "davon 4 KaP permanent"
            "\\d*" + //digits
            "( (AsP|KaP)){0,1}" +  // case "4 KaP permanent"
            "( davon){0,1}" + // case "4 davon permanent"
            " permanent" // keyword " permanent"
    );

    //\d*\/\d*\/\d*\/\d*(\/\d*){0,1}| \p{L}*\/\p{L}*\/\p{L}*\/\p{L}*(\/\p{L}*){0,1}
    protected static final Pattern PAT_COST_LIST = Pattern.compile(
        "\\d*\\/\\d*\\/\\d*\\/\\d*" + // case "2/4/8/16"
            "(\\/\\d*){0,1}" + // case "2/4/8/16/32"
            "|" + // OR
            " \\p{L}*\\/\\p{L}*\\/\\p{L}*\\/\\p{L}*" + // case " Tasse/Truhe/Tür/Burgtor"
            "(\\/\\p{L}*){0,1}" // case " winzig/klein/normal/groß/riesig"
    );

    //(\d* (AsP|KaP) für RS \d)+
    protected static final Pattern PAT_COST_ARMOR_SKILLS = Pattern.compile(
        "(\\d* (AsP|KaP) für RS \\d)+" // "4 AsP für RS 1"
    );

    //\d+ (AsP|KaP) bzw\. \d+ (AsP|KaP)
    protected static final Pattern PAT_COST_COUNTER_SKILL = Pattern.compile(
        "\\d+ (AsP|KaP) bzw\\. \\d+ (AsP|KaP)" // "8 AsP bzw. 16 AsP"
    );

    public static Cost retrieveSkillCost(MysticalSkillRaw msr)
    {
        Cost returnValue = new Cost();
        String restOfTxt = msr.cost;

        Matcher m = PAT_COST_BASE.matcher(restOfTxt);

        if (m.find())
        {
            String costBaseString = m.group();
            returnValue.cost = Integer.valueOf(costBaseString);
            restOfTxt = restOfTxt.replace(costBaseString, "");
        }

        m = PAT_COST_MIN.matcher(restOfTxt);
        if (m.find())
        {
            String costMinString = m.group();
            returnValue.costMin = extractFirstNumberFromText(costMinString, msr);
            restOfTxt = restOfTxt.replace(costMinString, "");
        }

        m = PAT_COST_PERMANENT.matcher(restOfTxt);
        if (m.find())
        {
            String costPermanentString = m.group();
            returnValue.costPermanent = extractFirstNumberFromText(costPermanentString, msr);
            restOfTxt = restOfTxt.replace(costPermanentString, "");
        }

        m = PAT_COST_PLUS.matcher(restOfTxt);
        if (m.find())
        {
            String costPlusString = m.group();
            if (costPlusString.isEmpty())
            {
                returnValue.costSpecialPlus = restOfTxt;
            } else
            {
                returnValue.costPlus = Integer.valueOf(costPlusString);
                restOfTxt = restOfTxt.replace(costPlusString, "");
            }
            m = PAT_COST_PLUS_UNIT.matcher(restOfTxt);
            if (m.find())
            {
                String costPlusUnitString = m.group();
                returnValue.costPlusPer = extractFirstNumberFromText(costPlusUnitString, msr);
                returnValue.costPlusPer = returnValue.costPlusPer == 0 ? 1 : returnValue.costPlusPer;
                returnValue.costPlusUnit = extractUnitsFromText(costPlusUnitString, false);
                restOfTxt = restOfTxt.replace(costPlusUnitString, "");
            }

            if (returnValue.costPlusUnit != null && returnValue.costPlus == 0)
            {
                returnValue.costSpecialPlus = restOfTxt;
            }
        }

        m = PAT_COST_LIST.matcher(restOfTxt);
        if (m.find())
        {
            String costListString = m.group();
            returnValue.costList = Arrays.stream(costListString.split("\\/")).map(s -> Integer.valueOf(s.trim())).collect(Collectors.toList());
        }
        if (m.find())
        {
            String costListValuesString = m.group();
            returnValue.costListValues = Arrays.asList(costListValuesString.split("\\/"));
        }
        if (m.find())
        {
            String costListPerm = m.group();
            returnValue.costListPermanent = Arrays.stream(costListPerm.split("\\/")).map(s -> Integer.valueOf(s.trim())).collect(Collectors.toList());
        }

        m = PAT_COST_ARMOR_SKILLS.matcher(restOfTxt);
        if (m.find())
        {
            List<Integer> costs = new ArrayList<>();
            List<String> values = new ArrayList<>();
            do
            {
                List<Integer> numbers = extractNumbersFromText(m.group());
                if (numbers.size() != 2)
                {
                    LOGGER.error("Die Kosten für einen RS-Skill (" + m.group() + ") haben keine eindeutig findbaren Zahlen für Kosten und Rüstung");
                } else
                {
                    costs.add(numbers.get(0));
                    values.add("RS " + numbers.get(1));
                }
            }
            while (m.find());

            m = PAT_COST_COUNTER_SKILL.matcher(restOfTxt);
            if (m.find())
            {
                String counterSkillString = m.group();
                List<Integer> numbers = extractNumbersFromText(counterSkillString);
                if (numbers.size() != 2)
                {
                    LOGGER.error("Kosten (" + restOfTxt + ") für Antimagie-Spruch hat keine zwei Zahlen");
                }
            }

            returnValue.costList = costs;
            returnValue.costListValues = values;
        }

        if (returnValue.cost == 0 && returnValue.costMin == 0 && returnValue.costList == null)
        {
            returnValue.costSpecial = restOfTxt;
        }
        returnValue.costPlusPerMax = 0;
        returnValue.costSpecialPlus = "";


        return returnValue;
    }
}
