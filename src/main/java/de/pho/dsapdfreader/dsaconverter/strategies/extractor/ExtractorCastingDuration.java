package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.CastingDuration;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.Unit;

public class ExtractorCastingDuration extends Extractor
{

  public static List<CastingDuration> retrieveCastingDurations(MysticalSkillRaw msr, MysticalSkillCategory catgeory)
  {
    List<CastingDuration> returnValue = new ArrayList<>();
    if (msr.name.equals("Ottarune (Ottaruna)"))
    {
      CastingDuration shortCd = new CastingDuration();
      shortCd.castingDurationUnit = Unit.ACTION;
      shortCd.duration = 4;
      shortCd.castingDurationSpecial = "4 Aktionen pro angefangenen 20 Personen";
      returnValue.add(shortCd);

      CastingDuration longCd = new CastingDuration();
      longCd.castingDurationUnit = Unit.DAY;
      longCd.duration = 4;
      longCd.castingDurationSpecial = "4 Tage pro angefangenen 20 Personen";
      returnValue.add(longCd);
    }
    else
    {
      List<String> castingDurations = List.of(msr.castingDuration.split("\\/"));
      returnValue = castingDurations.stream().map(cd -> retrieveCastingDuration(cd, catgeory, getPrefix(msr.publication, msr.name))).collect(Collectors.toList());
    }
    return returnValue;
  }

  private static CastingDuration retrieveCastingDuration(String castingDuration, MysticalSkillCategory category, String errorPrefix)
  {
    CastingDuration returnValue = null;
    if (category == MysticalSkillCategory.blessing || category == MysticalSkillCategory.trick)
    {
      returnValue = new CastingDuration();
      returnValue.duration = 1;
      returnValue.castingDurationUnit = Unit.ACTION;
    }
    else
    {
      if (castingDuration.startsWith("kurz"))
      {
        returnValue = new CastingDuration();
        returnValue.duration = 3;
        returnValue.castingDurationUnit = Unit.COMBAT_ROUND;

      }
      else if (castingDuration.startsWith("lang"))
      {
        returnValue = new CastingDuration();
        returnValue.duration = 3;
        returnValue.castingDurationUnit = Unit.MINUTE;
      }
      else
      {
        Matcher matcher = PAT_LEADING_NUMBER.matcher(castingDuration);
        if (!castingDuration.isEmpty() && matcher.find())
        {
          String m = matcher.group().trim();
          if (!m.isEmpty())
          {
            returnValue = new CastingDuration();
            returnValue.duration = Integer.valueOf(m);
            returnValue.castingDurationUnit = extractUnitFromText(castingDuration, errorPrefix);
          }
          else
          {
            LOGGER.error(errorPrefix + " is invalid casting Duration Pattern Match " + m);
          }
        }
      }
    }

    if (returnValue != null && (returnValue.duration <= 0 || returnValue.castingDurationUnit == null))
    {
      LOGGER.error(errorPrefix + "casting Duration not interpretable:\r\n" + castingDuration);
    }

    return returnValue;
  }
}
