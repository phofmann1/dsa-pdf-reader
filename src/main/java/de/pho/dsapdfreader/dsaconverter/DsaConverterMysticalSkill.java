package de.pho.dsapdfreader.dsaconverter;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Triplet;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.MysticalSkillVariant;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMysticalSkill extends DsaConverter<MysticalSkillRaw>
{
    private static final Logger LOGGER = LogManager.getLogger();


    private static final int VARIANT_I_REQ = 8;
    private static final int VARIANT_II_REQ = -1;
    private static final int VARIANT_III_REQ = 12;
    private static final int VARIANT_IV_REQ = -1;
    private static final int VARIANT_V_REQ = 16;

    private static final String KEY_VARIANT_SPELL = "Zaubererweiterungen";
    private static final String KEY_VARIANT_LITURGY = "Liturgieerweiterungen";
    private static final String KEY_DURATION_1 = "Wirkungsdauer";
    private static final String KEY_DURATION_2 = "Dauer";
    private static final String KEY_FEATURE = "Merkmal";
    private static final String KEY_ASPEKT = "Aspekt";
    private static final String KEY_RANGE = "Reichweite";
    private static final String KEY_REMARK = "Anmerkung";
    private static final String KEY_TARGET_CATEGORY = "Zielkategorie";
    private static final String KEY_CHECK = "Probe";
    private static final String KEY_EFFECT = "Wirkung";
    private static final String KEY_CASTING_DURATION_SPELL = "Zauberdauer";
    private static final String KEY_CASTING_DURATION_RITUAL = "Ritualdauer";
    private static final String KEY_CASTING_DURATION_LITURGY = "Liturgiedauer";
    private static final String KEY_CASTING_DURATION_LITURGY_TYPO_1 = "Liturgiedauerdauer";
    private static final String KEY_CASTING_DURATION_CEREMONY = "Zeremoniedauer";
    private static final String KEY_COST_APS = "AsP-Kosten";
    private static final String KEY_COST_KAP = "KaP-Kosten";
    private static final String KEY_COMMONNESS = "Verbreitung";
    private static final String KEY_MUSIC_TRADITION = "Musiktradition";
    private static final String KEY_TALENT = "Talent";
    private static final String KEY_ADVANCEMENT_CATEGORY = "Steigerungsfaktor";
    protected static final String[] KEYS = {
        KEY_DURATION_1,
        KEY_DURATION_2,
        KEY_FEATURE,
        KEY_ASPEKT,
        KEY_RANGE,
        KEY_REMARK,
        KEY_TARGET_CATEGORY,
        KEY_CHECK,
        KEY_EFFECT,
        KEY_CASTING_DURATION_SPELL,
        KEY_CASTING_DURATION_RITUAL,
        KEY_CASTING_DURATION_LITURGY,
        KEY_CASTING_DURATION_LITURGY_TYPO_1,
        KEY_CASTING_DURATION_CEREMONY,
        KEY_COST_APS,
        KEY_COST_KAP,
        KEY_COMMONNESS,
        KEY_MUSIC_TRADITION,
        KEY_TALENT,
        KEY_ADVANCEMENT_CATEGORY,
        KEY_VARIANT_SPELL,
        KEY_VARIANT_LITURGY
    };

    private static final String REGEX_TITLE_CHARS = "[\\wäöüÄÖÜß\\s\\d!\\-,\\/\\.…]";
    /*
        (<i>[a-z A-Z0-9äöüÄÖÜß!\-,\/\.…]{1,2}<\/i>){0,1}<i>[a-z A-Z0-9äöüÄÖÜß!\-,\/\.…]*(<\/i>){0,1}([1-3]){0,1} (\(FW|\(<\/i> FW)|\.[a-z A-Z0-9äöüÄÖÜß!\-,\/\.…]*(\(FW)|^[a-z A-Z0-9äöüÄÖÜß!\-,\/\.…]*(\(FW)
     */
    private static final String REGEX_EXTRACT_TITLE = "(<i>" + REGEX_TITLE_CHARS + "{1,2}<\\/i> )?" + //case "<i>L</i> <i>ängere Wirkungsdauer</i>"
        "<i>" + REGEX_TITLE_CHARS + "*" + //case "<i>Längere Wirkungsdauer"
        "(<\\/i>)? " +
        "([1-3] )?" + // case "<i>Längere Wirkungsdauer</i> 1 (FW"
        "(\\(FW|\\(<\\/i> FW)" + //cases "</i> (FW" or " (</i> FW"
        "|" + //ALTERNATIVE: in one case the italic is missing, so the match is with the preceding "."
        "\\." + REGEX_TITLE_CHARS + "*(\\(FW)" + //case ". Längere Wirkungsdauer (FW"
        "|" +
        "^" + REGEX_TITLE_CHARS + "*(\\(FW)"; //case Begin line: "Längere Wirkungsdauer (FW "
    private static final Pattern PATTERN_EXTRACT_TITLE = Pattern.compile(REGEX_EXTRACT_TITLE);

    public String[] getKeys()
    {
        return KEYS;
    }

    @Override
    protected void applyFlagsForKey(AtomicConverterFlag flags, String text)
    {
        flags.wasName.set(false);
        flags.wasDescription.set(false);
        flags.wasDuration.set(text.trim().equals(KEY_DURATION_1) || text.trim().equals(KEY_DURATION_2));
        flags.wasFeature.set(text.trim().equals(KEY_FEATURE) || text.trim().equals(KEY_ASPEKT));
        flags.wasRange.set(text.trim().equals(KEY_RANGE));
        flags.wasRemarks.set(text.trim().equals(KEY_REMARK));
        flags.wasTargetCategory.set(text.trim().equals(KEY_TARGET_CATEGORY));
        flags.wasCheck.set(text.trim().equals(KEY_CHECK));
        flags.wasEffect.set(text.trim().equals(KEY_EFFECT));
        flags.wasCastingDuration.set(text.trim().equals(KEY_CASTING_DURATION_SPELL)
            || text.trim().equals(KEY_CASTING_DURATION_RITUAL)
            || text.trim().equals(KEY_CASTING_DURATION_LITURGY)
            || text.trim().equals(KEY_CASTING_DURATION_LITURGY_TYPO_1)
            || text.trim().equals(KEY_CASTING_DURATION_CEREMONY));
        flags.wasCost.set(text.trim().equals(KEY_COST_APS) || text.trim().equals(KEY_COST_KAP));
        flags.wasCommonness.set(text.trim().equals(KEY_COMMONNESS) || text.trim().equals(KEY_MUSIC_TRADITION));
        flags.wasAdvancementCategory.set(text.trim().equals(KEY_ADVANCEMENT_CATEGORY));
        flags.wasVariants.set(text.trim().equals(KEY_VARIANT_LITURGY)
            || text.trim().equals(KEY_VARIANT_SPELL));
        flags.wasTalent.set(text.trim().equals(KEY_TALENT));
        if (flags.wasVariants.get())
        {
            flags.wasFurtherInformation.set(false);
        }
    }

    @Override
    protected MysticalSkillRaw initializeType()
    {
        return new MysticalSkillRaw();
    }

    @Override
    protected void applyDataValue(MysticalSkillRaw msr, TextWithMetaInfo t, String cleanText, AtomicConverterFlag flags)
    {
        if (msr != null)
        {

            if ((flags.wasName.get() || flags.wasDescription.get()) && !flags.wasVariants.get())
                msr.description = concatForDataValue(msr.description, cleanText);
            if (flags.wasRange.get()) msr.range = concatForDataValue(msr.range, cleanText);
            if (flags.wasDuration.get()) msr.duration = concatForDataValue(msr.duration, cleanText);
            if (flags.wasFeature.get()) msr.feature = concatForDataValue(msr.feature, cleanText);
            if (flags.wasTargetCategory.get()) msr.targetCategory = concatForDataValue(msr.targetCategory, cleanText);
            if (flags.wasRemarks.get()) msr.remarks = concatForDataValue(msr.remarks, cleanText);

            if (flags.wasCheck.get()) msr.check = concatForDataValue(msr.check, cleanText).replace(":", "").trim();
            if (flags.wasEffect.get()) msr.effect = concatForDataValueWithMarkup(msr.effect, t, cleanText);
            if (flags.wasCastingDuration.get()) msr.castingDuration = concatForDataValue(msr.castingDuration, cleanText);
            if (flags.wasCost.get()) msr.cost = concatForDataValue(msr.cost, cleanText);
            if (flags.wasCommonness.get()) msr.commonness = concatForDataValue(msr.commonness, cleanText);
            if (flags.wasFurtherInformation.get()) msr.furtherInformation = concatForDataValue(msr.furtherInformation, cleanText);

            if (flags.wasAdvancementCategory.get())
            {
                msr.advancementCategory = concatForDataValue(msr.advancementCategory, cleanText);
                flags.wasAdvancementCategory.set(false);
                flags.wasFurtherInformation.set(true);
            }

            if (flags.wasQs1.get()) msr.qs1 = concatForDataValue(msr.qs1, cleanText).replace(":", "").trim();
            if (flags.wasQs2.get()) msr.qs2 = concatForDataValue(msr.qs2, cleanText).replace(":", "").trim();
            if (flags.wasQs3.get()) msr.qs3 = concatForDataValue(msr.qs3, cleanText);
            if (flags.wasQs4.get()) msr.qs4 = concatForDataValue(msr.qs4, cleanText);
            if (flags.wasQs5.get()) msr.qs5 = concatForDataValue(msr.qs5, cleanText);
            if (flags.wasQs6.get()) msr.qs6 = concatForDataValue(msr.qs6, cleanText);

            if (flags.wasVariants.get()) msr.variantsText = concatForDataValueWithMarkup(msr.variantsText, t, cleanText);

        } else
        {
            LOGGER.error("MysticalSkillRaw was null: " + t.text);
        }
    }

    @Override
    protected void concludePredecessor(List<MysticalSkillRaw> mysticalSkillRawList)
    {
        MysticalSkillRaw msr = last(mysticalSkillRawList);
        if (msr != null && msr.variantsText != null)
        {

            msr.variantsText = msr.variantsText
                .replace("• ", "")
                .replace("  ", " ");

            // Check all occurrences
            List<Triplet<Integer, Integer, String>> variantTitleMatches = extractVariantTitles(msr);
            AtomicInteger endOfPreviousHeadline = new AtomicInteger(-1);

            List<MysticalSkillVariant> variants = new ArrayList<>();

            variantTitleMatches.forEach(t -> {
                MysticalSkillVariant msv = new MysticalSkillVariant();
                msv.name = t.getValue2()
                    .replace("<i>", "")
                    .replace("</i>", "")
                    .replace(" (FW", "")
                    .replace(".", "")
                    .trim();
                int indexComma = msr.variantsText.indexOf(',', t.getValue1());
                int indexClosingBracket = msr.variantsText.indexOf("AP)", indexComma + 1);
                msv.minLevel = Integer.parseInt(msr.variantsText.substring(t.getValue1(), indexComma).trim());
                msv.ap = Integer.parseInt(msr.variantsText.substring(indexComma + 1, indexClosingBracket).trim());

                if (endOfPreviousHeadline.get() > 0)
                {
                    variants.get(variants.size() - 1).description = msr.variantsText
                        .substring(endOfPreviousHeadline.get() + 1, t.getValue0())
                        .trim();
                }

                endOfPreviousHeadline.set(indexClosingBracket + 3);

                variants.add(msv);
            });

            if (!variants.isEmpty())
                variants.get(variants.size() - 1).description = msr.variantsText.substring(endOfPreviousHeadline.get());

            variants.forEach(v -> {
                if (v.minLevel == VARIANT_I_REQ)
                {
                    msr.variant1 = v;
                } else if (v.minLevel == VARIANT_II_REQ)
                {
                    msr.variant2 = v;
                } else if (v.minLevel == VARIANT_III_REQ)
                {
                    msr.variant3 = v;
                } else if (v.minLevel == VARIANT_IV_REQ)
                {
                    msr.variant4 = v;
                } else if (v.minLevel == VARIANT_V_REQ)
                {
                    msr.variant5 = v;
                }
            });
        }
    }

    private List<Triplet<Integer, Integer, String>> extractVariantTitles(MysticalSkillRaw ms)
    {
        Matcher matcher = PATTERN_EXTRACT_TITLE.matcher(ms.variantsText);
        List<Triplet<Integer, Integer, String>> returnValue = new ArrayList<>();

        StringBuilder res = new StringBuilder();
        res.append(ms.variantsText)
            .append("\r\n");

        while (matcher.find())
        {
            res.append("Start index: ")
                .append(matcher.start());
            res.append(" End index: ")
                .append(matcher.end());
            res.append(" Found: ")
                .append(matcher.group())
                .append("\r\n");
            returnValue.add(new Triplet<>(matcher.start(), matcher.end(), matcher.group()));
        }

        if (returnValue.size() < 3)
        {
            String msg = String.format("VARIANTS NOT CORRECT:%n %s.%s -->%n%s%n", ms.publication, ms.name, res);
            LOGGER.error(msg);
        }

        return returnValue;
    }


    @Override
    protected void applyFlagsForNoKeyStrings(AtomicConverterFlag flags, String text)
    {
        if (text.trim().equals("QS 1") || text.trim().equals("1 QS") || text.trim().equals("1-2 QS")) flags.wasQs1.set(true);
        if (text.trim().equals("QS 2") || text.trim().equals("2 QS") || text.trim().equals("1-2 QS")) flags.wasQs2.set(true);
        if (text.trim().equals("QS 3") || text.trim().equals("3 QS") || text.trim().equals("3-4 QS")) flags.wasQs3.set(true);
        if (text.trim().equals("QS 4") || text.trim().equals("4 QS") || text.trim().equals("3-4 QS")) flags.wasQs4.set(true);
        if (text.trim().equals("QS 5") || text.trim().equals("5 QS") || text.trim().equals("5-6 QS")) flags.wasQs5.set(true);
        if (text.trim().equals("QS 6") || text.trim().equals("6 QS") || text.trim().equals("5-6 QS")) flags.wasQs6.set(true);


    }

    @Override
    protected void handleWasNoKeyStrings(AtomicConverterFlag flags, TextWithMetaInfo t)
    {
        flags.wasQs1.set(flags.wasQs1.get() && !t.isBold);
        flags.wasQs2.set(flags.wasQs2.get() && !t.isBold);
        flags.wasQs3.set(flags.wasQs3.get() && !t.isBold);
        flags.wasQs4.set(flags.wasQs4.get() && !t.isBold);
        flags.wasQs5.set(flags.wasQs5.get() && !t.isBold);
        flags.wasQs6.set(flags.wasQs6.get() && !t.isBold);
    }
}
