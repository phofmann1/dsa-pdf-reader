package de.pho.dsapdfreader.dsaconverter;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Triplet;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsMysticalSkill;
import de.pho.dsapdfreader.exporter.model.MysticalSkillVariant;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMysticalSkill extends DsaConverter<MysticalSkillRaw, ConverterAtomicFlagsMysticalSkill>
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


    ConverterAtomicFlagsMysticalSkill flags = new ConverterAtomicFlagsMysticalSkill();

    public String[] getKeys()
    {
        return KEYS;
    }

    @Override
    protected ConverterAtomicFlagsMysticalSkill getFlags()
    {
        return flags;
    }

    @Override
    protected String getClassName()
    {
        return this.getClass().getName();
    }

    @Override
    protected void applyFlagsForKey(String key)
    {
        flags.wasName.set(false);
        flags.wasDescription.set(false);
        flags.wasDuration.set(key.trim().equals(KEY_DURATION_1) || key.trim().equals(KEY_DURATION_2));
        flags.wasFeature.set(key.trim().equals(KEY_FEATURE) || key.trim().equals(KEY_ASPEKT));
        flags.wasRange.set(key.trim().equals(KEY_RANGE));
        flags.wasRemarks.set(key.trim().equals(KEY_REMARK));
        flags.wasTargetCategory.set(key.trim().equals(KEY_TARGET_CATEGORY));
        flags.wasCheck.set(key.trim().equals(KEY_CHECK));
        flags.wasEffect.set(key.trim().equals(KEY_EFFECT));
        flags.wasCastingDuration.set(key.trim().equals(KEY_CASTING_DURATION_SPELL)
            || key.trim().equals(KEY_CASTING_DURATION_RITUAL)
            || key.trim().equals(KEY_CASTING_DURATION_LITURGY)
            || key.trim().equals(KEY_CASTING_DURATION_LITURGY_TYPO_1)
            || key.trim().equals(KEY_CASTING_DURATION_CEREMONY));
        flags.wasCost.set(key.trim().equals(KEY_COST_APS) || key.trim().equals(KEY_COST_KAP));
        flags.wasCommonness.set(key.trim().equals(KEY_COMMONNESS) || key.trim().equals(KEY_MUSIC_TRADITION));
        flags.wasAdvancementCategory.set(key.trim().equals(KEY_ADVANCEMENT_CATEGORY));
        flags.wasVariants.set(key.trim().equals(KEY_VARIANT_LITURGY)
            || key.trim().equals(KEY_VARIANT_SPELL));
        flags.wasTalent.set(key.trim().equals(KEY_TALENT));
        if (flags.wasVariants.get())
        {
            flags.wasFurtherInformation.set(false);
        }
    }

    @Override
    protected void applyDataValue(MysticalSkillRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
    {
        if (currentDataObject != null)
        {

            if ((flags.wasName.get() || flags.wasDescription.get()) && !flags.wasVariants.get())
                currentDataObject.description = concatForDataValue(currentDataObject.description, cleanText);
            if (flags.wasRange.get()) currentDataObject.range = concatForDataValue(currentDataObject.range, cleanText);
            if (flags.wasDuration.get()) currentDataObject.duration = concatForDataValue(currentDataObject.duration, cleanText);
            if (flags.wasFeature.get()) currentDataObject.feature = concatForDataValue(currentDataObject.feature, cleanText);
            if (flags.wasTargetCategory.get()) currentDataObject.targetCategory = concatForDataValue(currentDataObject.targetCategory, cleanText);
            if (flags.wasRemarks.get()) currentDataObject.remarks = concatForDataValue(currentDataObject.remarks, cleanText);

            if (flags.wasCheck.get()) currentDataObject.check = concatForDataValue(currentDataObject.check, cleanText).replace(":", "").trim();
            if (flags.wasEffect.get()) currentDataObject.effect = concatForDataValueWithMarkup(currentDataObject.effect, cleanText, isBold, isItalic);
            if (flags.wasCastingDuration.get()) currentDataObject.castingDuration = concatForDataValue(currentDataObject.castingDuration, cleanText);
            if (flags.wasCost.get()) currentDataObject.cost = concatForDataValue(currentDataObject.cost, cleanText);
            if (flags.wasCommonness.get()) currentDataObject.commonness = concatForDataValue(currentDataObject.commonness, cleanText);
            if (flags.wasFurtherInformation.get())
                currentDataObject.furtherInformation = concatForDataValue(currentDataObject.furtherInformation, cleanText);

            if (flags.wasAdvancementCategory.get())
            {
                currentDataObject.advancementCategory = concatForDataValue(currentDataObject.advancementCategory, cleanText);
                flags.wasAdvancementCategory.set(false);
                flags.wasFurtherInformation.set(true);
            }

            if (flags.wasQs1.get()) currentDataObject.qs1 = concatForDataValue(currentDataObject.qs1, cleanText).replace(":", "").trim();
            if (flags.wasQs2.get()) currentDataObject.qs2 = concatForDataValue(currentDataObject.qs2, cleanText).replace(":", "").trim();
            if (flags.wasQs3.get()) currentDataObject.qs3 = concatForDataValue(currentDataObject.qs3, cleanText);
            if (flags.wasQs4.get()) currentDataObject.qs4 = concatForDataValue(currentDataObject.qs4, cleanText);
            if (flags.wasQs5.get()) currentDataObject.qs5 = concatForDataValue(currentDataObject.qs5, cleanText);
            if (flags.wasQs6.get()) currentDataObject.qs6 = concatForDataValue(currentDataObject.qs6, cleanText);

            if (flags.wasVariants.get())
                currentDataObject.variantsText = concatForDataValueWithMarkup(currentDataObject.variantsText, cleanText, isBold, isItalic);

        } else
        {
            LOGGER.error("MysticalSkillRaw was null: " + cleanText);
        }
    }

    @Override
    protected void concludePredecessor(MysticalSkillRaw msr)
    {
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
    protected void applyFlagsForNoKeyStrings(ConverterAtomicFlagsMysticalSkill flags, String text)
    {
        if (text.trim().equals("QS 1") || text.trim().equals("1 QS") || text.trim().equals("1-2 QS")) flags.wasQs1.set(true);
        if (text.trim().equals("QS 2") || text.trim().equals("2 QS") || text.trim().equals("1-2 QS")) flags.wasQs2.set(true);
        if (text.trim().equals("QS 3") || text.trim().equals("3 QS") || text.trim().equals("3-4 QS")) flags.wasQs3.set(true);
        if (text.trim().equals("QS 4") || text.trim().equals("4 QS") || text.trim().equals("3-4 QS")) flags.wasQs4.set(true);
        if (text.trim().equals("QS 5") || text.trim().equals("5 QS") || text.trim().equals("5-6 QS")) flags.wasQs5.set(true);
        if (text.trim().equals("QS 6") || text.trim().equals("6 QS") || text.trim().equals("5-6 QS")) flags.wasQs6.set(true);


    }

    @Override
    protected void handleWasNoKeyStrings(ConverterAtomicFlagsMysticalSkill flags, TextWithMetaInfo t)
    {
        flags.wasQs1.set(flags.wasQs1.get() && !t.isBold);
        flags.wasQs2.set(flags.wasQs2.get() && !t.isBold);
        flags.wasQs3.set(flags.wasQs3.get() && !t.isBold);
        flags.wasQs4.set(flags.wasQs4.get() && !t.isBold);
        flags.wasQs5.set(flags.wasQs5.get() && !t.isBold);
        flags.wasQs6.set(flags.wasQs6.get() && !t.isBold);
    }

    @Override
    protected void handleFirstValue(List<MysticalSkillRaw> returnValue, TopicConfiguration conf, String cleanText)
    {

        if (!flags.getFirstFlag().get())
        {
          MysticalSkillRaw newEntry = new MysticalSkillRaw();
          flags.initDataFlags();
          newEntry.setTopic(conf.topic);
          newEntry.setPublication(conf.publication);
          returnValue.add(newEntry);
        }

      last(returnValue).setName(concatForDataValue(last(returnValue).getName(), cleanText));
    }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return t.size <= conf.dataSize && t.isBold;
  }

  @Override
  protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return t.size <= conf.dataSize && !t.isBold;
  }
}
