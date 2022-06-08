package de.pho.dsapdfreader.exporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.AdvancementCategory;
import de.pho.dsapdfreader.exporter.model.AttributeShort;
import de.pho.dsapdfreader.exporter.model.CastingDuration;
import de.pho.dsapdfreader.exporter.model.Cost;
import de.pho.dsapdfreader.exporter.model.Duration;
import de.pho.dsapdfreader.exporter.model.ElementKey;
import de.pho.dsapdfreader.exporter.model.MysticalSkill;
import de.pho.dsapdfreader.exporter.model.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.MysticalSkillFeature;
import de.pho.dsapdfreader.exporter.model.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.MysticalSkillModification;
import de.pho.dsapdfreader.exporter.model.MysticalSkillVariant;
import de.pho.dsapdfreader.exporter.model.Publication;
import de.pho.dsapdfreader.exporter.model.SkillRange;
import de.pho.dsapdfreader.exporter.model.TargetCategory;
import de.pho.dsapdfreader.exporter.model.TraditionKey;


public class LoadToMysticalSkill
{
    protected static final Pattern PAT_CONTENT_PARENTHESES = Pattern.compile("\\((.*?)\\)");
    protected static final Pattern PAT_COMMON_CLERIC = Pattern.compile("allgemein(?! \\(Schamanenritus\\))");
    protected static final Pattern PAT_COMMON_SHAMAN = Pattern.compile("allgemein \\(Schamanenritus\\)");
    protected static final Pattern PAT_NAMELESS = Pattern.compile("Namenloser");
    protected static final Logger LOAD_ERRORS = LogManager.getLogger("fileLoadErrors");

    public static MysticalSkill migrate(MysticalSkillRaw ms)
    {
        MysticalSkill returnValue = new MysticalSkill();

        returnValue.name = ms.name;
        try
        {
            returnValue.advancementCategory = (ms.advancementCategory == null || ms.advancementCategory.isEmpty())
                ? AdvancementCategory.NONE
                : AdvancementCategory.valueOf(ms.advancementCategory
                .replaceAll(":", "")
                .trim());
        } catch (IllegalArgumentException e)
        {
            LOAD_ERRORS.error(getPrefix(ms.publication, ms.name, "AdvancementCategory") + "<" + ms.advancementCategory + "> could not be interpreted.");
        }
        returnValue.features = isClerical(ms)
            ? retrieveFeaturesCleric(ms.feature, ms.commonness, ms.name, ms.publication)
            : retrieveFeatureMagic(ms.feature, ms.name, ms.publication);
        returnValue.publication = Publication.valueOf(ms.publication);

        returnValue.category = retrieveCategory(ms.topic);
        returnValue.check = retrieveCheck(ms.check, returnValue.category, ms.name, ms.publication);
        /*
        returnValue.elementalCategories = retrieveElementalCategories(ms);
        returnValue.key = retrieveMysticalSkillKey(ms.name);
        returnValue.resist = retrieveResistAttribute(ms.check);
        returnValue.resistMultiplier = retrieveResistMultiplier(ms.check);
        returnValue.allowedModifications = retrieveAllowedModifications(ms);
        returnValue.casting = retrieveCastingDuration(ms.castingDuration);
        returnValue.skillCost = retrieveSkillCost(ms.cost);
        returnValue.skillDuration = retrieveSkillDuration(ms.duration);
        returnValue.skillRange = retrieveSkillRange(ms.range);
        returnValue.spellVariants =retrieveSpellVariants(ms.variants);
        returnValue.targetCategories = retrieveTargetCategories(ms.targetCategory);
        returnValue.traditions = retrieveTraditions(ms.commonness);
        */
        return returnValue;
    }

    protected static MysticalSkillCategory retrieveCategory(TopicEnum topic)
    {
        return switch (topic)
            {
                case TRICKS -> MysticalSkillCategory.trick;
                case SPELLS -> MysticalSkillCategory.spell;
                case RITUALS -> MysticalSkillCategory.ritual;
                case BLESSINGS -> MysticalSkillCategory.blessing;
                case LITURGIES -> MysticalSkillCategory.liturgy;
                case CEREMONIES -> MysticalSkillCategory.ceremony;
            };
    }


    protected static boolean isClerical(MysticalSkillRaw ms)
    {
        return ms.topic == TopicEnum.BLESSINGS || ms.topic == TopicEnum.LITURGIES || ms.topic == TopicEnum.CEREMONIES;
    }

    protected static String getPrefix(String publication, String name, String cause)
    {
        return publication + " - " + name + " (" + cause + "): ";
    }

    protected static List<MysticalSkillFeature> retrieveFeatureMagic(String feature, String name, String publication)
    {
        List<MysticalSkillFeature> returnValue = new ArrayList<>();
        MysticalSkillFeature msFeature = MysticalSkillFeature.fromString(feature);
        if (msFeature == null) LOAD_ERRORS.error(getPrefix(publication, name, "Feature") + "<" + feature + "> could not be interpreted.");
        else returnValue.add(msFeature);
        return returnValue;
    }


    protected static List<MysticalSkillFeature> retrieveFeaturesCleric(String feature, String commonness, String name, String publication)
    {
        String clericalFeatureString = (commonness == null || commonness.isEmpty()) ? feature : commonness;
        List<MysticalSkillFeature> returnValue = extractFeaturesFromString(clericalFeatureString).stream()
            .map(MysticalSkillFeature::fromString)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (returnValue.size() == 0)
            LOAD_ERRORS.error(getPrefix(publication, name, "Feature") + "<" + clericalFeatureString + "> could not be interpreted.");

        return returnValue;

    }

    protected static List<String> extractFeaturesFromString(String clericalFeatureString)
    {
        List<String> returnValue = new ArrayList<>();

        Matcher matchParenthesesContent = PAT_CONTENT_PARENTHESES.matcher(clericalFeatureString);
        Matcher matchCommon = PAT_COMMON_CLERIC.matcher(clericalFeatureString);
        Matcher matchShaman = PAT_COMMON_SHAMAN.matcher(clericalFeatureString);
        Matcher matchNameless = PAT_NAMELESS.matcher(clericalFeatureString);

        if (matchCommon.find()) returnValue.add("allgemein");
        if (matchShaman.find()) returnValue.add("allgemein Schamanenritus");
        if (matchNameless.find()) returnValue.add("Namenloser");

        while (matchParenthesesContent.find())
        {
            String featureString = matchParenthesesContent.group(1);
            List<String> features = Arrays.stream(featureString.split(",| und | oder "))
                .map(String::trim)
                .filter(e -> !e.equalsIgnoreCase("Schamanenritus"))
                .collect(Collectors.toList());
            returnValue.addAll(features);
        }
        return returnValue;
    }


    protected static TraditionKey[] retrieveTraditions(String commonness)
    {
        throw new NotImplementedException("retrieveTraditions");
    }

    protected static TargetCategory[] retrieveTargetCategories(String targetCategory)
    {
        throw new NotImplementedException("retrieveTargetCategories");
    }

    protected static MysticalSkillVariant[] retrieveSpellVariants(String[] variants)
    {
        throw new NotImplementedException("retrieveSpellVariants");
    }

    protected static SkillRange retrieveSkillRange(String range)
    {
        throw new NotImplementedException("retrieveSkillRange");
    }

    protected static Duration retrieveSkillDuration(String duration)
    {
        throw new NotImplementedException("retrieveSkillRange");
    }

    protected static Cost retrieveSkillCost(String cost)
    {
        throw new NotImplementedException("retrieveSkillCost");
    }

    protected static int retrieveResistMultiplier(String check)
    {
        throw new NotImplementedException("retrieveResistMultiplier");
    }

    protected static AttributeShort retrieveResistAttribute(String check)
    {
        throw new NotImplementedException("retrieveResistAttribute");
    }

    protected static MysticalSkillKey retrieveMysticalSkillKey(String name)
    {
        throw new NotImplementedException("retrieveMysticalSkillKey");
    }

    protected static ElementKey[] retrieveElementalCategories(MysticalSkillRaw ms)
    {
        throw new NotImplementedException("retrieveElementalCategories");
    }

    protected static AttributeShort[] retrieveCheck(String check, MysticalSkillCategory category, String name, String publication)
    {
        List<AttributeShort> shorts = new ArrayList<>();
        String extractedCheck = check
            .replaceAll("\\(.*?\\)", "") // remove ZK and SK text
            .trim();
        if (category != MysticalSkillCategory.blessing && category != MysticalSkillCategory.trick)
        {
            for (String shortText : extractedCheck.split("/"))
            {
                try
                {
                    shorts.add(AttributeShort.valueOf(shortText.trim()));
                } catch (IllegalArgumentException e)
                {
                    LOAD_ERRORS.error(getPrefix(publication, name, "attribute <" + shortText + "> unknown"));
                }
            }
        }
        return shorts.toArray(new AttributeShort[0]);
    }

    protected static CastingDuration retrieveCastingDuration(String ms)
    {
        throw new NotImplementedException("retrieveCastingDuration");
    }

    protected static MysticalSkillModification[] retrieveAllowedModifications(MysticalSkillRaw ms)
    {
        throw new NotImplementedException("retrieveAllowedModifications");
    }

}
