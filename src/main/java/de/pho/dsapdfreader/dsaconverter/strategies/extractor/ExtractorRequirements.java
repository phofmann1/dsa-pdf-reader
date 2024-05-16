package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;

import de.pho.dsapdfreader.exporter.model.RequirementBoon;
import de.pho.dsapdfreader.exporter.model.RequirementsSpecie;
import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.CultureKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;
import de.pho.dsapdfreader.tools.roman.RomanNumberHelper;

public class ExtractorRequirements extends Extractor
{
  public static Map<String, String> extractLevelRequirementMap(String reqText)
  {
    String regex = "Stufe( ?(VII|VI|V(?![a-ü])|IV|III|II|I)\\/?)+:?";
    Map<String, String> resultMap = new HashMap<>();
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(reqText);

    List<String> keys = new ArrayList<>();
    while (matcher.find())
    {
      keys.add(matcher.group());
    }

    if (keys.size() > 0)
    {
      int offset = reqText.indexOf((keys.get(0)));
      if (offset > 0)
      {
        resultMap.put("all", reqText.substring(0, offset));
      }
      for (int i = 0; i < keys.size() - 1; i++)
      {
        String key = keys.get(i);
        String value = reqText.substring(offset + key.length(), reqText.indexOf(keys.get(i + 1)));
        offset += key.length() + value.length();
        String cleanedKey = extractCleanedKey(key);
        resultMap.put(cleanedKey, value);
      }
      String lastKey = keys.get(keys.size() - 1);
      String lastValue = reqText.substring(reqText.indexOf(lastKey) + lastKey.length());
      resultMap.put(extractCleanedKey(lastKey), lastValue);
    }
    else
    {
      resultMap.put("all", reqText);
    }
    return resultMap;
  }

  public static String extractRequirementsStringForLevel(Map<String, String> preconditionMap, int levels, int currentLevel)
  {
    String returnValue = preconditionMap.get("all");
    if (levels > 0)
    {
      returnValue += " " + preconditionMap.get(RomanNumberHelper.intToRoman(currentLevel + 1));
    }
    return returnValue.replaceAll("null(?=$| )", " ");
  }

  private static String extractCleanedKey(String key)
  {
    Matcher m = Pattern.compile("(IV|V?I{1,3}|V)(?=( |:|<|$))").matcher(key);
    return (m.find())
        ? m.group()
        : key;
  }

  public static RequirementsSpecie extractSpecieReqsForBoon(BoonKey key)
  {
    RequirementsSpecie returnValue = null;
    switch (key)
    {
    case schurkenname:
    case unpassender_name:
    case allerweltsname:
    case böser_namensvetter:
      returnValue = new RequirementsSpecie();
      returnValue.forbidden.add(SpecieKey.elf);
      break;
    case wolfsblut:
      returnValue = new RequirementsSpecie();
      returnValue.required.add(SpecieKey.mensch);
      break;
    }
    return returnValue;
  }

  public static CultureKey extractCultureReqsForBoon(BoonKey key)
  {
    return switch (key)
        {
          case walwut_swafskari, friedlos -> CultureKey.thorwal;
          case wolfsblut -> CultureKey.nivesen;
          case yurach -> CultureKey.stammesorks;
          default -> null;
        };
  }

  public static List<RequirementBoon> extractRequirementsBoon(String preconditions, String name)
  {

    List<RequirementBoon> returnValue = new ArrayList<>();
    Pair<String, List<RequirementBoon>> noneOfBoonsReq = extractNoneOfBoons(preconditions, name);
    returnValue.addAll(noneOfBoonsReq.getValue1());
    preconditions = noneOfBoonsReq.getValue0();

    return returnValue;
  }

  private static Pair<String, List<RequirementBoon>> extractNoneOfBoons(String preconditions, String name)
  {
    List<RequirementBoon> reqs = new ArrayList<>();
    String reducedPreconditions = preconditions;
    //none of merits
    //(?<=[kK]ein Vorteil )[A-ü \(\)]*?(?=$|oder Nachteil|kein Nachteil|,)
    //none of flaws
    //(?<=([kK]ein|oder) Nachteil ).*(?=,|$)

    Pattern PAT_NONE_OF_BOONS = Pattern.compile("(?<=[kK]ein Vorteil )[A-ü \\(\\)]*?(?=$|oder Nachteil|kein Nachteil|,)|(?<=([kK]ein|oder) Nachteil ).*(?=,|$)");

    Matcher m = PAT_NONE_OF_BOONS.matcher(preconditions);
    while (m.find())
    {
      String t = m.group();
      reducedPreconditions = reducedPreconditions.replace(t, "");

      List<String> boonTexts = List.of(t.split(",| oder"));
      boonTexts.forEach(bt -> {
        String boonText = bt
            .replace(" <br> Elfen können diesen Vorteil nicht wählen,  da ihre Namen immer passend sind.", "")
            .replace(" <br> Elfen können diesen Vorteil nicht wählen, da ihre Namen immer passend sind.", "")
            /*  .replace(" kein Nachteil", "")
              .replace(" keine Nachteile", "")
              .replace(" kein Vorteil", "")
              .replace(" keine Vorteile", "")
              .replace(" Nachteil", "")
              .replace(" Vorteil", "")
              .replace(" Elfen", "")
              .replace(" nicht", "")
              .replace(" Keine", "")
              .replace(" keine", "")
              .replace(" Kein", "")
              .replace(" kein", "")*/
            .trim();
        boolean isSameSelection = boonText.contains(" für diese Fertigkeit")
            || boonText.contains(" in der Fertigkeit")
            || boonText.contains(" für den gleichen Sinn")
            || boonText.contains(" Kobolde")
            || boonText.contains(" auf die gleiche Umgebung")
            || boonText.contains(" die Tierart")
            || boonText.contains(" auf das gleiche Talent");
        Matcher mv = PAT_BETWEEN_BRACKETS.matcher(boonText);
        String variantText = ((mv.find() && !boonText.contains("Regeneration")) ? mv.group() : null);
        if (variantText != null)
        {
          boonText = boonText.replace("(" + variantText + ")", "");
        }
        else
        {
          variantText = boonText.contains("Gesellschaftstalent") ? "Gesellschaftstalent" : null;
        }

        boonText = boonText.replace(" für diese Fertigkeit", "")
            .replace(" in der Fertigkeit", "")
            .replace(" für den gleichen Sinn", "")
            .replace(" Kobolde", " x")
            .replace(" auf die gleiche Umgebung", "")
            .replace(" die Tierart", " x")
            .replace(" auf ein Gesellschaftstalent", "")
            .replace(" in einem Gesellschaftstalent", "")
            .replace(" auf das gleiche Talent", "");
        if (!boonText.isEmpty())
        {

          BoonKey boonKey = ExtractorBoonKey.retrieve(boonText);
          if (boonKey != null)
          {
            RequirementBoon req = new RequirementBoon();
            req.key = boonKey;
            req.exists = false;
            req.variantName = variantText;
            req.isSameSelection = isSameSelection;
            reqs.add(req);
          }
          else
          {
            LOGGER.error("BoonKey is null | " + name + ": " + boonText + "\r\n--> " + preconditions + "\r\n");
          }
        }
      });
    }
    if (!reducedPreconditions.equals(preconditions))
    {
      reducedPreconditions = reducedPreconditions.replaceAll("[kK]ein Vorteil ", "").replaceAll("[kK]ein Nachteil ", "");
    }
    return Pair.with(reducedPreconditions, reqs);
  }
}
