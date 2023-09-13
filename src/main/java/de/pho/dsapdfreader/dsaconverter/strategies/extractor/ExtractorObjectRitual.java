package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.exporter.model.enums.ObjectRitualKey;

public class ExtractorObjectRitual extends Extractor
{
  public static ObjectRitualKey extractOrKeyFromName(String name)
  {
    ObjectRitualKey returnValue = null;
    try
    {
      returnValue = extractOrKeyFromText(name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e)
    {
      // String msg = String.format("%s key could not be interpreted.", name);
      //LOGGER.error(msg);
    }
    return returnValue;
  }

  private static ObjectRitualKey extractOrKeyFromText(String name)
  {
    ObjectRitualKey returnValue = null;
    String keyString = extractKeyTextFromTextWithUmlauts(name.replace("ß", "xxx"))
        .toLowerCase()
        .replace("xxx", "ß");
    keyString = keyString.trim();

    try
    {
      if (!keyString.isEmpty())
      {
        returnValue = ObjectRitualKey.valueOf(keyString.toLowerCase());
      }
    }
    catch (IllegalArgumentException e)
    {
      System.out.println(keyString.toLowerCase() + ",");
      //LOGGER.error("Invalid specialAbility name: " + name);
    }
    return returnValue;
  }

  public static List<ObjectRitualKey> retrieveRequirementsObjectRitual(Map<String, String> preconditionMap, int levels, int currentLevel, ObjectRitualKey currentOrk)
  {
    List<ObjectRitualKey> returnValue = new ArrayList<>();
    String requirementsString = ExtractorRequirements.extractRequirementsStringForLevel(preconditionMap, levels, currentLevel).replace("\u00AD", "-");

    Matcher m = Pattern.compile("[A-ü &/()-]{3,}(?=$|,|(<br>))").matcher(requirementsString);
    while (m.find())
    {
      String text = m.group().replace("für", "").trim();
      if (isNoValidObjectRitualKey(text))
      {
        text = text.contains("Merkmalsfokus") ? "Merkmalsfokus" : text;
        String orKeyString = extractKeyTextFromTextWithUmlauts(text.replace("ß", "xxx"))
            .toLowerCase();
        try
        {
          returnValue.add(ObjectRitualKey.valueOf(orKeyString));

        }
        catch (IllegalArgumentException e)
        {
          System.out.println("INVALID " + text);
        }
      }
    }

    if (currentLevel > 0)
    {
      returnValue.add(ObjectRitualKey.values()[currentOrk.ordinal() - 1]);
    }
    return returnValue;
  }

  private static boolean isNoValidObjectRitualKey(String text)
  {
    return !text.contains("keine")
        && !text.contains("bei höheren Stufen")
        && !text.contains("Stufe niedrigere Stufe der SF")
        && !text.contains("Merkmal ")
        && !text.contains("Merkmalskenntnis")
        && !text.contains("Schalenverzauberung")
        && !text.contains("Punkte Volumenerweiterung")
        && !text.isEmpty();
  }
}
