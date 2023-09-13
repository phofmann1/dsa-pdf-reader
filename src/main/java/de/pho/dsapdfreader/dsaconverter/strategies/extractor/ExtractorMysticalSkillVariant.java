package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.MysticalSkillVariant;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillVariantKey;


public class ExtractorMysticalSkillVariant extends Extractor
{
  private static final String REGEX_PREREQUISITE_VARIANTS = ": Zaubererweiterung |: Zaubererweiterungen ";

  private static final String REGEX_EXTRACT_VARIANT_PREREQUISITE = "(?<=" + REGEX_PREREQUISITE_VARIANTS + ")([\\w\\d\\s]*(?=\\.|$)(?=\\.)|.*)";
  // (?<=: Zaubererweiterung |: Zaubererweiterungen )([\w\d\s]*(?=\.|$)(?=\.)|.*)
  private static final Pattern PATTERN_EXTRACT_VARIANT_PREREQUISITE = Pattern.compile(REGEX_EXTRACT_VARIANT_PREREQUISITE);

  public static List<MysticalSkillVariant> retrieveMysticalSkillVariants(MysticalSkillRaw msr, MysticalSkillKey msk)
  {
    return handleVariants(msr, msk).stream()
        .filter(v -> v != null)
        .collect(Collectors.toList());
  }

  private static List<MysticalSkillVariant> handleVariants(MysticalSkillRaw msr, MysticalSkillKey msk)
  {
    List<MysticalSkillVariant> returnValue = new ArrayList<>();

    if (msr.variant1 != null) returnValue.add(handleVariant(msk, msr.variant1, msr.variantsText));
    if (msr.variant2 != null) returnValue.add(handleVariant(msk, msr.variant2, msr.variantsText));
    if (msr.variant3 != null) returnValue.add(handleVariant(msk, msr.variant3, msr.variantsText));
    if (msr.variant4 != null) returnValue.add(handleVariant(msk, msr.variant4, msr.variantsText));
    if (msr.variant5 != null) returnValue.add(handleVariant(msk, msr.variant5, msr.variantsText));


    return returnValue;
  }

  private static MysticalSkillVariant handleVariant(MysticalSkillKey msk, MysticalSkillVariant variant, String variantText)
  {
    MysticalSkillVariant returnValue = variant;

    returnValue.key = ExtractorMysticalSkillKey.extractMysticalSkillVariantKeyFromText(msk, variant.name);
    returnValue.requiredVariantKeys = extractRequiredVariantKey(variant.description, msk);

    String[] descArray = returnValue.description.split("AP\\):|" + REGEX_PREREQUISITE_VARIANTS);
    if (descArray.length >= 2)
    {
      returnValue.description = descArray[1].trim();
    }

    return returnValue;
  }

  private static List<MysticalSkillVariantKey> extractRequiredVariantKey(String description, MysticalSkillKey msk)
  {
    List<MysticalSkillVariantKey> returnValue = new ArrayList<>();
    Matcher matcher = PATTERN_EXTRACT_VARIANT_PREREQUISITE.matcher(description);
    while (matcher.find())
    {
      String resultString = matcher.group()
          .replace("Wechselsinn", "Wechselsicht")
          .replace("Aufheben von selbst aufladenden Artefakten", "Entzauberung von selbst aufladenden Artefakten");
      resultString = (msk == MysticalSkillKey.spell_daemonisches_vergessen && resultString.equals("Längere Wirkungsdauer. "))
          ? "Längere Wirkungsdauer 1"
          : resultString;
      String[] results = resultString.split(" und (?!höher)");
      for (String r : results)
      {
        returnValue.add(ExtractorMysticalSkillKey.extractMysticalSkillVariantKeyFromText(msk, r));
      }
    }

    return returnValue;
  }
}
