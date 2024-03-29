package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.Duration;
import de.pho.dsapdfreader.exporter.model.enums.Unit;

public class ExtractorSkillDuration extends Extractor
{
    //(?<=maximal|längstens).*|^.*(?=aufrechterhaltend)
    protected static final Pattern PAT_MAX_DURATION_STRING = Pattern.compile(
        "(?<=maximal|längstens|bzw\\.).*" + // everything after "maximal" or "längstens"
            "|" + // OR
            "^.+(?=aufrechterhaltend)" // everything before aufrechterhaltend
    );

    public static Duration retrieveSkillDuration(MysticalSkillRaw msr)
    {
        Duration returnValue = null;
        if (msr.duration != null && !msr.duration.isEmpty())
        {
            returnValue = new Duration();
            String durationTxt = msr.duration;
            Matcher m = PAT_MAX_DURATION_STRING.matcher(msr.duration);
            if (m != null && m.find())
            {
                String maxTxt = m.group();
                if (!maxTxt.isEmpty())
                {
                    Unit[] maxUnits = extractUnitsFromText(maxTxt, getPrefix(msr.publication, msr.name), false);
                    if (maxUnits.length == 0)
                    {
                        returnValue.maxDurationSpecial = maxTxt.trim();
                    } else if (maxUnits.length == 1)
                    {
                        returnValue.maxDurationUnit = maxUnits[0];
                    } else
                    {
                      LOGGER.error(getPrefix(msr.publication, msr.name) + "Unit for maximum Duration (" + maxTxt + ") is not ambiguously");
                    }

                  returnValue.maxDuration = extractFirstNumberFromText(maxTxt, getPrefix(msr.publication, msr.name));
                  returnValue.maxDuration = returnValue.maxDuration == 0 ? 1 : returnValue.maxDuration;
                    returnValue.maxIsPerQs = maxTxt.contains("QS ");

                    durationTxt = durationTxt.replace(maxTxt, "");
                } else
                {
                  LOGGER.error(getPrefix(msr.publication, msr.name) + "Duration (" + msr.duration + ") has no valid max");
                }
            }

          Unit[] units = extractUnitsFromText(durationTxt, getPrefix(msr.publication, msr.name), false);
            if (units.length == 0)
            {
                returnValue.durationSpecial = msr.duration;
            } else if (units.length == 1)
            {
                returnValue.durationUnit = units[0];
                if (returnValue.durationUnit != Unit.SUSTAINED && returnValue.durationUnit != Unit.IMMEDIATE)
                {
                  returnValue.duration = extractFirstNumberFromText(durationTxt, getPrefix(msr.publication, msr.name));
                  returnValue.duration = returnValue.duration == 0 ? 1 : returnValue.duration;
                    returnValue.isPerQS = durationTxt.contains("QS ");
                }
            } else
            {
                Optional<Unit> sustainedOrAny = Arrays.stream(units).filter(u ->
                    u == Unit.SUSTAINED || u == Unit.IMMEDIATE
                ).findFirst();
                if (sustainedOrAny.isPresent())
                {
                    returnValue.durationUnit = sustainedOrAny.get();
                }
            }

            if (returnValue.durationSpecial == null || returnValue.durationSpecial.isEmpty())
            {
                String specInBrackets = extractTextBetweenBrackets(msr.duration);
                String specAfterComma = msr.duration.contains(",") ? msr.duration.substring(msr.duration.indexOf(",") + 1).trim() : "";
                returnValue.durationSpecial = (specInBrackets != null) ? specInBrackets : (
                    specAfterComma
                );
            }
        }
        return returnValue;
    }
}
