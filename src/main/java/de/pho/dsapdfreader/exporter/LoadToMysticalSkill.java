package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.extractor.Extractor;
import de.pho.dsapdfreader.exporter.extractor.ExtractorAdvancementCategory;
import de.pho.dsapdfreader.exporter.extractor.ExtractorCastingDuration;
import de.pho.dsapdfreader.exporter.extractor.ExtractorCheck;
import de.pho.dsapdfreader.exporter.extractor.ExtractorFeature;
import de.pho.dsapdfreader.exporter.extractor.ExtractorMysticalSkillCost;
import de.pho.dsapdfreader.exporter.extractor.ExtractorMysticalSkillKey;
import de.pho.dsapdfreader.exporter.extractor.ExtractorMysticalSkillModifications;
import de.pho.dsapdfreader.exporter.extractor.ExtractorMysticalSkillVariant;
import de.pho.dsapdfreader.exporter.extractor.ExtractorSkillDuration;
import de.pho.dsapdfreader.exporter.extractor.ExtractorSkillRange;
import de.pho.dsapdfreader.exporter.extractor.ExtractorTargetCategory;
import de.pho.dsapdfreader.exporter.extractor.ExtractorTradtion;
import de.pho.dsapdfreader.exporter.model.MysticalSkill;
import de.pho.dsapdfreader.exporter.model.Publication;


public class LoadToMysticalSkill
{

    private LoadToMysticalSkill()
    {
    }

    public static MysticalSkill migrate(MysticalSkillRaw msr)
    {
        MysticalSkill returnValue = new MysticalSkill();

        returnValue.name = msr.name;
        returnValue.advancementCategory = ExtractorAdvancementCategory.retrieveAdvancementCategory(msr);
        returnValue.features = ExtractorFeature.retrieveFeatures(msr);
        returnValue.publication = Publication.valueOf(msr.publication);
        returnValue.category = Extractor.retrieveCategory(msr.topic);
        returnValue.check = ExtractorCheck.retrieveCheck(msr, returnValue.category);
        returnValue.key = ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr, returnValue.category);
        returnValue.spellVariants = ExtractorMysticalSkillVariant.retrieveMysticalSkillVariants(msr);
        returnValue.casting = ExtractorCastingDuration.retrieveCastingDuration(msr, returnValue.category);
        returnValue.targetCategories = ExtractorTargetCategory.retrieveTargetCategories(msr);
        returnValue.skillRange = ExtractorSkillRange.retrieveSkillRange(msr);
        returnValue.traditions = ExtractorTradtion.retrieveTraditions(msr, returnValue.category);
        returnValue.skillDuration = ExtractorSkillDuration.retrieveSkillDuration(msr);
        returnValue.skillCost = ExtractorMysticalSkillCost.retrieveSkillCost(msr);
        returnValue.allowedModifications = ExtractorMysticalSkillModifications.retrieveAllowedModifications(msr);
        returnValue.difficulty = ExtractorMysticalSkillDifficulty.retrieveDifficulty(msr);
        // elemental Categories are missing
        return returnValue;
    }

}
