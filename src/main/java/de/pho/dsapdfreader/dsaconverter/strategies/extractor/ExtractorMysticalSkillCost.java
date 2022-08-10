package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

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
            "(?! (pro|für RS|bzw\\.)))" //not followed by "pro" or "für RS" or "bzw."
    );
    // \d*(?= (AsP|KaP|Aktivierungskosten) pro)
    protected static final Pattern PAT_COST_PLUS = Pattern.compile(
        "\\d*" + //digit
            "(?= (AsP|KaP|Aktivierungskosten) pro)" //followed by " AsP pro" or " KaP pro" or " Aktivierungskosten pro"
    );

    //(?<=\+ ).*
    protected static final Pattern PAT_COST_PLUS_TEXT = Pattern.compile(
        "(?<=\\+ )" +// preceding: "+ "
            ".*" // everything after
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
    //mindestens (jedoch )?\d* (AsP|KaP)
    protected static final Pattern PAT_COST_MIN = Pattern.compile(
        "mindestens (jedoch )?" + // leading with "mindestens (jedoch )"
            "\\d*" + //any digits
            " (AsP|KaP)" //followed by " AsP" or " KaP"
    );
    //(davon)? \\d*( (AsP|KaP))?( davon)? permanent
    protected static final Pattern PAT_COST_PERMANENT = Pattern.compile(
        "(davon)? " + // case "davon 4 KaP permanent"
            "\\d*" + //digits
            "( (AsP|KaP))?" +  // case "4 KaP permanent"
            "( davon)?" + // case "4 davon permanent"
            " permanent" // keyword " permanent"
    );

    //\d*\/\d*\/\d*\/\d*(\/\d*)?| \p{L}*\/\p{L}*\/\p{L}*\/\p{L}*(\/\p{L}*)?
    protected static final Pattern PAT_COST_LIST = Pattern.compile(
        "\\d*\\/\\d*\\/\\d*\\/\\d*" + // case "2/4/8/16"
            "(\\/\\d*)?" + // case "2/4/8/16/32"
            "|" + // OR
            " \\p{L}*\\/\\p{L}*\\/\\p{L}*\\/\\p{L}*" + // case " Tasse/Truhe/Tür/Burgtor"
            "(\\/\\p{L}*)?" // case " winzig/klein/normal/groß/riesig"
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

        restOfTxt = applyBaseCost(returnValue, restOfTxt);
        restOfTxt = applyMinimalCost(returnValue, restOfTxt, msr);
        restOfTxt = applyPermanentCost(returnValue, restOfTxt, msr);
        restOfTxt = applyPlusCost(returnValue, restOfTxt, msr);
        restOfTxt = applyListCost(returnValue, restOfTxt);
        restOfTxt = applyArmorSpellCost(returnValue, restOfTxt, msr);
        restOfTxt = applyCounterSpellCost(returnValue, restOfTxt, msr);
        // unapplied: returnValue.costPlusPerMax = 0;
        applySpecialCost(returnValue, restOfTxt);

        return returnValue;
    }

    private static String applySpecialCost(Cost returnValue, String restOfTxt)
    {
        if (returnValue.costValue == 0 && returnValue.costMin == 0 && returnValue.costList == null)
        {
            returnValue.costSpecial = restOfTxt;
        }
        return "";
    }

    private static String applyCounterSpellCost(Cost returnValue, String restOfTxt, MysticalSkillRaw msr)
    {
        Matcher m = PAT_COST_COUNTER_SKILL.matcher(restOfTxt);
        if (m.find())
        {
            String counterSkillString = m.group();
            List<Integer> numbers = extractNumbersFromText(counterSkillString);
            if (numbers.size() != 2)
            {
                String msg = String.format(
                    "%s Kosten (%s) für Antimagie-Spruch hat keine zwei Zahlen",
                    getPrefix(msr),
                    restOfTxt
                );
                LOGGER.error(msg);
            } else
            {
                returnValue.costList = new ArrayList<>();
                returnValue.costListValues = new ArrayList<>();

                returnValue.costList.add(numbers.get(0));
                returnValue.costList.add(numbers.get(1));
                returnValue.costListValues.add("");
                returnValue.costListValues.add("Zauber mit der Zielkategorie Zone");
            }
            restOfTxt = restOfTxt.replace(counterSkillString, "");
        }
        return restOfTxt;
    }

    private static String applyArmorSpellCost(Cost returnValue, String restOfTxt, MysticalSkillRaw msr)
    {
        Matcher m = PAT_COST_ARMOR_SKILLS.matcher(restOfTxt);
        if (m.find())
        {
            List<Integer> costs = new ArrayList<>();
            List<String> values = new ArrayList<>();
            do
            {
                String armorString = m.group();
                List<Integer> numbers = extractNumbersFromText(armorString);
                if (numbers.size() != 2)
                {
                    String msg = String.format(
                        "%s Die Kosten für einen RS-Skill (%s) haben keine eindeutig findbaren Zahlen für Kosten und Rüstung",
                        getPrefix(msr),
                        m.group()
                    );
                    LOGGER.error(msg);
                } else
                {
                    costs.add(numbers.get(0));
                    values.add("RS " + numbers.get(1));
                }
                restOfTxt = restOfTxt.replace(armorString, "");
            }
            while (m.find());

            returnValue.costList = costs;
            returnValue.costListValues = values;
        }
        return restOfTxt;
    }

    private static String applyListCost(Cost returnValue, String restOfTxt)
    {
        Matcher m = PAT_COST_LIST.matcher(restOfTxt);
        if (m.find())
        {
            String costListString = m.group();
            returnValue.costList = Arrays.stream(costListString.split("\\/")).map(s -> Integer.valueOf(s.trim())).collect(Collectors.toList());
            restOfTxt = restOfTxt.replace(costListString, "");
        }
        if (m.find())
        {
            String costListValuesString = m.group();
            returnValue.costListValues = Arrays.asList(costListValuesString.split("\\/"));
            restOfTxt = restOfTxt.replace(costListValuesString, "");
        }
        if (m.find())
        {
            String costListPerm = m.group();
            returnValue.costListPermanent = Arrays.stream(costListPerm.split("\\/")).map(s -> Integer.valueOf(s.trim())).collect(Collectors.toList());
            restOfTxt = restOfTxt.replace(costListPerm, "");
        }
        return restOfTxt;

    }

    private static String applyPlusCost(Cost returnValue, String restOfTxt, MysticalSkillRaw msr)
    {
        Matcher m = PAT_COST_PLUS.matcher(restOfTxt);
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
                returnValue.costPlusUnit = extractUnitsFromText(costPlusUnitString, msr, false);
                restOfTxt = restOfTxt.replace(costPlusUnitString, "");
            }


        }

        m = PAT_COST_PLUS_TEXT.matcher(msr.cost);
        if (m.find() && (returnValue.costPlusUnit != null || returnValue.costPlus == 0))
        {
            String costSpecialPlusString = m.group();
            returnValue.costSpecialPlus = costSpecialPlusString;
            restOfTxt = restOfTxt.replace(costSpecialPlusString, "");
        }

        return restOfTxt;
    }

    private static String applyPermanentCost(Cost returnValue, String restOfTxt, MysticalSkillRaw msr)
    {
        Matcher m = PAT_COST_PERMANENT.matcher(restOfTxt);
        if (m.find())
        {
            String costPermanentString = m.group();
            returnValue.costPermanent = extractFirstNumberFromText(costPermanentString, msr);
            restOfTxt = restOfTxt.replace(costPermanentString, "");
        }
        return restOfTxt;
    }

    private static String applyMinimalCost(Cost returnValue, String restOfTxt, MysticalSkillRaw msr)
    {
        Matcher m = PAT_COST_MIN.matcher(restOfTxt);
        if (m.find())
        {
            String costMinString = m.group();
            returnValue.costMin = extractFirstNumberFromText(costMinString, msr);
            restOfTxt = restOfTxt.replace(costMinString, "");
        }
        return restOfTxt;
    }

    private static String applyBaseCost(Cost returnValue, String restOfTxt)
    {
        Matcher m = PAT_COST_BASE.matcher(restOfTxt);

        if (m.find())
        {
            String costBaseString = m.group();
            returnValue.costValue = Integer.valueOf(costBaseString);
            restOfTxt = restOfTxt.replace(costBaseString, "");
        }
        return restOfTxt;
    }
}
