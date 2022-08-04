package de.pho.dsapdfreader.exporter.extractor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.MysticalSkillVariant;

public class ExtractorMysticalSkillVariant extends Extractor
{
    public static List<MysticalSkillVariant> retrieveMysticalSkillVariants(MysticalSkillRaw msr)
    {
        return Arrays.stream(new MysticalSkillVariant[]{msr.variant1, msr.variant2, msr.variant3, msr.variant4, msr.variant5})
            .filter(v -> v != null)
            .collect(Collectors.toList());
    }
}
