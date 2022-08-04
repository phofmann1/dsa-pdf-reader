package de.pho.dsapdfreader.exporter.extractor;

import java.util.Arrays;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.TargetCategory;

public class ExtractorTargetCategory extends Extractor
{
    public static TargetCategory[] retrieveTargetCategories(MysticalSkillRaw msr)
    {
        String[] tCats = msr.targetCategory.split(REG_COMMAS_OR_UND_NOT_IN_BRACKETS);
        return Arrays.stream(tCats)
            .map(t -> extractTargetCategoryFromText(t, msr))
            .toArray(TargetCategory[]::new);
    }

    protected static TargetCategory extractTargetCategoryFromText(String t, MysticalSkillRaw msr)
    {
        if (t.contains("alle")) return TargetCategory.all;
        if (t.contains("Tier")) return TargetCategory.animal;
        //if (t.contains("")) return TargetCategory.animated;
        if (t.contains("übernatürliche Wesen")) return TargetCategory.supernatural;
        if (t.contains("Lebewesen")) return TargetCategory.living_being;
        if (t.contains("Wesen")) return TargetCategory.being;
        //if (t.contains("")) return TargetCategory.creature;
        if (t.contains("Kulturschaffend")) return TargetCategory.cultural;
        if (t.contains("Dämon")) return TargetCategory.demon;
        if (t.contains("Elementar")) return TargetCategory.elementals;
        if (t.contains("profane Objekte")) return TargetCategory.profaneObject;
        if (t.contains("geweihte Objekte")) return TargetCategory.holyObject;
        if (t.contains("Objekt")) return TargetCategory.object;
        if (t.contains("Pflanze")) return TargetCategory.plant;
        if (t.contains("Zone")) return TargetCategory.zone;
        if (t.contains("Untot")) return TargetCategory.undead;
        if (t.contains("Geist")) return TargetCategory.ghost;
        if (t.contains("Beseelt")) return TargetCategory.posessed;
        if (t.contains("Fee")) return TargetCategory.fairy;
        if (t.contains("trick")) return TargetCategory.trick;
        if (t.contains("Liturgie")) return TargetCategory.liturgy;
        if (t.contains("Zeremonie")) return TargetCategory.ceremony;
        LOGGER.error(getPrefix(msr) + "String (" + t + ") contains no implemented targetCategory.");
        return null;
    }
}
