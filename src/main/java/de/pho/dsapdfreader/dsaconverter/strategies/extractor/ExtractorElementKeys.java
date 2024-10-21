package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.ElementKey;

public class ExtractorElementKeys extends Extractor
{
  public static List<ElementKey> retrieveElementKeys(MysticalSkillRaw msr)
  {
    String[] elements = msr.elements != null ? msr.elements.split("\\|") : new String[]{};
    return Arrays.stream(elements)
        .map(es -> switch (es) {
          case "feuer" -> ElementKey.FIRE;
          case "wasser" -> ElementKey.WATER;
          case "humus" -> ElementKey.HUMUS;
          case "eis" -> ElementKey.ICE;
          case "erz" -> ElementKey.STONE;
          case "luft" -> ElementKey.AIR;
          default -> null;
        }).filter(es -> es != null)
        .collect(Collectors.toList());
  }
}
