package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.enums.TargetCategory;

public class ExtractorTargetCategory extends Extractor
{
    public static List<TargetCategory> retrieveTargetCategories(MysticalSkillRaw msr)
    {
        List<TargetCategory> returnValue = new ArrayList<>();
        if (msr.targetCategory != null && !msr.targetCategory.isEmpty())
        {
            String[] tCats = msr.targetCategory.split(REG_COMMAS_OR_UND_NOT_IN_BRACKETS);
            returnValue = Arrays.stream(tCats)
                .map(t -> extractTargetCategoryFromText(t, msr, false))
                .collect(Collectors.toList());
        }
        return returnValue;
    }

    protected static TargetCategory extractTargetCategoryFromText(String t, MysticalSkillRaw msr, boolean isMandatory)
    {
        if (t.contains("alle")) return TargetCategory.ALL;
        if (t.contains("Tier")) return TargetCategory.ANIMAL;
        //if (t.contains("")) return TargetCategory.ANIMATED;
        if (t.contains("übernatürliche Wesen")) return TargetCategory.SUPERNATURAL;
        if (t.contains("Lebewesen")) return TargetCategory.LIVING_BEING;
        if (t.contains("Wesen")) return TargetCategory.BEING;
        //if (t.contains("")) return TargetCategory.CREATURE;
        if (t.contains("Kulturschaffend")) return TargetCategory.CULTURAL;
        if (t.contains("Dämon")) return TargetCategory.DEMON;
        if (t.contains("Elementar")) return TargetCategory.ELEMENTALS;
        if (t.contains("profane Objekte")) return TargetCategory.PROFANEOBJECT;
        if (t.contains("geweihte Objekte")) return TargetCategory.HOLYOBJECT;
        if (t.contains("Objekt")) return TargetCategory.OBJECT;
        if (t.contains("Pflanze")) return TargetCategory.PLANT;
        if (t.contains("Zone")) return TargetCategory.ZONE;
        if (t.contains("Untot")) return TargetCategory.UNDEAD;
        if (t.contains("Geist")) return TargetCategory.GHOST;
        if (t.contains("Beseelt")) return TargetCategory.POSESSED;
        if (t.contains("Fee")) return TargetCategory.FAIRY;
        if (t.contains("trick")) return TargetCategory.TRICK;
        if (t.contains("Liturgie")) return TargetCategory.LITURGY;
        if (t.contains("Zeremonie")) return TargetCategory.CEREMONY;
        if (isMandatory)
        {
            LOGGER.error("{0} String ({1}) contains no implemented targetCategory.", getPrefix(msr.publication, msr.name), t);
        }
        return null;
    }
}
