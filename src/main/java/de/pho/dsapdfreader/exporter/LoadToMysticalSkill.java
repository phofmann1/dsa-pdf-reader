package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAdvancementCategory;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCastingDuration;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCheck;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorFeature;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillCost;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillDifficulty;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillModifications;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillVariant;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkillDuration;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkillKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkillRange;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorTargetCategory;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorTradtion;
import de.pho.dsapdfreader.exporter.model.MysticalSkill;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;
import de.pho.dsapdfreader.tools.csv.DsaStringCleanupTool;


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
        returnValue.publication = Publication.valueOf(DsaStringCleanupTool.mapStringToEnumName(msr.publication));
        returnValue.category = Extractor.retrieveCategory(msr.topic);
        returnValue.check = ExtractorCheck.retrieveCheck(msr, returnValue.category);
        returnValue.key = ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr, returnValue.category);
        returnValue.spellVariants = ExtractorMysticalSkillVariant.retrieveMysticalSkillVariants(msr);
        returnValue.casting = ExtractorCastingDuration.retrieveCastingDuration(msr, returnValue.category);
        returnValue.targetCategories = ExtractorTargetCategory.retrieveTargetCategories(msr);
        returnValue.skillRange = ExtractorSkillRange.retrieveSkillRange(msr);
        returnValue.traditions = ExtractorTradtion.retrieveTraditions(msr, returnValue.category);
        if (
            returnValue.traditions.contains(TraditionKey.DANCER)
                || returnValue.traditions.contains(TraditionKey.BARDE)
        )
        {
            returnValue.traditionSubs = ExtractorTradtion.retrieveTraditionSubs(msr);
        }
        returnValue.skillDuration = ExtractorSkillDuration.retrieveSkillDuration(msr);
        returnValue.skillCost = ExtractorMysticalSkillCost.retrieveSkillCost(msr);
        returnValue.allowedModifications = ExtractorMysticalSkillModifications.retrieveAllowedModifications(msr);
        returnValue.difficulty = ExtractorMysticalSkillDifficulty.retrieveDifficulty(msr);
        returnValue.skillKeys = ExtractorSkillKey.retrieveSkillKeys(msr);
        // elemental Categories are missing
        return returnValue;
    }

}
