package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Triplet;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.DsaObjectI;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.MysticalSkillVariant;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public abstract class DsaConverter<T extends DsaObjectI>
{

    private static final String REGEX_TITLE_CHARS = "[a-z A-Z0-9äöüÄÖÜß!\\-,\\/\\.]";
    private static final String KEY_VARIANT_SPELL = "Zaubererweiterungen";
    private static final String KEY_VARIANT_LITURGY = "Liturgieerweiterungen";

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern patternIsNumber = Pattern.compile("-?\\d+(\\.\\d+)?");

    private static final String KEY_DURATION = "Wirkungsdauer";
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
    private static final String KEY_ADVANCEMENT_CATEGORY = "Steigerungsfaktor";
    private static final String[] KEYS = {
        KEY_DURATION,
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
        KEY_ADVANCEMENT_CATEGORY,
        KEY_VARIANT_SPELL,
        KEY_VARIANT_LITURGY
    };
    private static final int VARIANT_I_REQ = 8;
    private static final int VARIANT_II_REQ = -1;
    private static final int VARIANT_III_REQ = 12;
    private static final int VARIANT_IV_REQ = -1;
    private static final int VARIANT_V_REQ = 16;
    /*
        (<i>[a-z A-Z0-9äöüÄÖÜß!\-,\/\.]{1,2}<\/i>){0,1}<i>[a-z A-Z0-9äöüÄÖÜß!\-,\/\.]*(<\/i>){0,1}([1-3]){0,1} (\(FW|\(<\/i> FW)|\.[a-z A-Z0-9äöüÄÖÜß!\-,\/\.]*(\(FW)|^[a-z A-Z0-9äöüÄÖÜß!\-,\/\.]*(\(FW)
     */
    final String REGEX_EXTRACT_TITLE = "(<i>" + REGEX_TITLE_CHARS + "{1,2}<\\/i> ){0,1}" + //case "<i>L</i> <i>ängere Wirkungsdauer</i>"
        "<i>" + REGEX_TITLE_CHARS + "*" + //case "<i>Längere Wirkungsdauer"
        "(<\\/i>){0,1} " +
        "([1-3] ){0,1}" + // case "<i>Längere Wirkungsdauer</i> 1 (FW"
        "(\\(FW|\\(<\\/i> FW)" + //cases "</i> (FW" or " (</i> FW"
        "|" + //ALTERNATIVE: in one case the italic is missing, so the match is with the preceding "."
        "\\." + REGEX_TITLE_CHARS + "*(\\(FW)" + //case ". Längere Wirkungsdauer (FW"
        "|" +
        "^" + REGEX_TITLE_CHARS + "*(\\(FW)"; //case Begin line: "Längere Wirkungsdauer (FW "
    Pattern PATTERN_EXTRACT_TITLE = Pattern.compile(REGEX_EXTRACT_TITLE);

    private static void applyFlagsForKey(AtomicConverterFlag flags, String text)
    {
        flags.wasName.set(false);
        flags.wasDescription.set(false);
        flags.wasDuration.set(text.trim().equals(KEY_DURATION));
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
        flags.wasCommonness.set(text.trim().equals(KEY_COMMONNESS));
        flags.wasAdvancementCategory.set(text.trim().equals(KEY_ADVANCEMENT_CATEGORY));
        flags.wasVariants.set(text.trim().equals(KEY_VARIANT_LITURGY)
            || text.trim().equals(KEY_VARIANT_SPELL));
        if (flags.wasVariants.get())
        {
            flags.wasFurtherInformation.set(false);
        }
    }

    protected static String concatForDataValue(String origin, String newValue)
    {
        String returnValue = origin == null ? "" : origin.trim();
        String spacer = returnValue.endsWith("-") ? "" : " ";
        returnValue = returnValue.endsWith("-") ? returnValue.substring(0, returnValue.length() - 1) : returnValue;
        returnValue = returnValue.trim() + spacer + newValue.trim();
        return returnValue
            .replaceAll("\\h", " ") // replace non breaking white space with white space
            .replaceAll("(?<=[a-zßöäü])-(?=[a-zßöäü])", "") // replace "-" between lower chars wit ""
            .replaceAll("(?<=[a-zßöäü]) -(?=[a-zßöäü])", "") // replace " -" between lower chars wit ""
            .replaceAll("> -", ">-") // case: "<i>Sinnesschärfe</i> -Probe"
            .replaceAll("^ :|^:", "") // case: ": Sinnesschärfe-Probe"
            .trim();
    }

    protected static String concatForDataValueWithMarkup(String origin, TextWithMetaInfo t, String cleanText)
    {
        String newText = cleanText;
        if (!newText.isEmpty())
        {
            if (t.isBold) newText = "<b>" + newText + "</b>";
            if (t.isItalic) newText = "<i>" + newText + "</i>";
        }
        return concatForDataValue(origin, newText);
    }


    private static boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
    {
        return t.size <= conf.dataSize && Arrays.stream(KEYS).noneMatch(k -> k.equals(cleanText));
    }

    private static boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
    {
        return t.size <= conf.dataSize && t.isBold && Arrays.stream(KEYS).anyMatch(k -> k.equals(cleanText));
    }

    private static boolean validateIsName(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size == conf.nameSize;
    }


    private static boolean isNumeric(String strNum)
    {
        if (strNum == null)
        {
            return false;
        }
        return patternIsNumber.matcher(strNum).matches();
    }

    public List<T> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
    {
        List<T> returnValue = new ArrayList<>();
        LOGGER.debug("parse  result to " + initializeType().getClass().getName());
        AtomicConverterFlag flags = new AtomicConverterFlag();

        AtomicInteger lineOnPage = new AtomicInteger(0);
        AtomicInteger lastPage = new AtomicInteger(0);
        resultList
            .forEach(t -> {

                String cleanText = t.text
                    .trim();

                // if Page is changed, reset the line count
                if (lastPage.get() != t.onPage)
                {
                    lastPage.set(t.onPage);
                    lineOnPage.set(0);
                }
                lineOnPage.incrementAndGet();

                // handle line if it config was not finished
                if (!flags.wasFinished.get())
                {


                    // validate the flags for conf
                    boolean isName = validateIsName(t, conf);
                    boolean isNameSkipped = isName && isNumeric(t.text); // gets skipped, when the name is a number (Page Number in some documents)
                    boolean isDataKey = validateIsDataKey(t, cleanText, conf);
                    boolean isDataValue = validateIsDataValue(t, cleanText, conf);

                    // validate the QS flags, they act differently, because they are also part of the effect
                    handleWasQsValues(flags, t);

                    // handle name
                    if (isName && !isNameSkipped)
                    {
                        if (!flags.wasName.get())
                        {
                            concludeVariantsOfPredecessor(returnValue);
                            T newEntry = initializeType();
                            flags.initDataFlags();
                            newEntry.setTopic(conf.topic);
                            newEntry.setPublication(conf.publication);
                            returnValue.add(newEntry);
                        }

                        last(returnValue).setName(concatForDataValue(last(returnValue).getName(), cleanText));

                    }

                    // handle keys
                    if (isDataKey)
                    {
                        applyFlagsForKey(flags, t.text);
                    }

                    // handle values
                    if (isDataValue)
                    {
                        applyDataValue(last(returnValue), t, cleanText, flags);
                        applyFlagsForQs(flags, t.text);
                    }

                    flags.wasName.set(isName && !isNameSkipped);
                }
            });
        concludeVariantsOfPredecessor(returnValue);
        return returnValue;
    }

    private void concludeVariantsOfPredecessor(List<T> elementList)
    {
        T element = last(elementList);
        if (element instanceof MysticalSkillRaw)
        {
            MysticalSkillRaw ms = (MysticalSkillRaw) element;

            if (ms.variantsText != null)
            {
                ms.variantsText = ms.variantsText
                    .replace("• ", "")
                    .replace("  ", " ");


                Matcher matcher = PATTERN_EXTRACT_TITLE.matcher(ms.variantsText);

                StringBuilder res = new StringBuilder();
                res.append(ms.variantsText + "\r\n");
                // Check all occurrences
                List<Triplet<Integer, Integer, String>> variantTitleMatches = new ArrayList<>();
                while (matcher.find())
                {
                    res.append("Start index: " + matcher.start());
                    res.append(" End index: " + matcher.end());
                    res.append(" Found: " + matcher.group() + "\r\n");
                    variantTitleMatches.add(new Triplet<>(matcher.start(), matcher.end(), matcher.group()));
                }

                if (variantTitleMatches.size() < 3)
                    LOGGER.error("VARIANTS NOT CORRECT:\r\n " + ms.publication + "." + ms.name + " -->\r\n" + res + "\r\n");

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
                    int indexComma = ms.variantsText.indexOf(',', t.getValue1());
                    int indexClosingBracket = ms.variantsText.indexOf("AP)", indexComma + 1);
                    msv.minLevel = Integer.valueOf(ms.variantsText.substring(t.getValue1(), indexComma).trim());
                    msv.ap = Integer.valueOf(ms.variantsText.substring(indexComma + 1, indexClosingBracket).trim());

                    if (endOfPreviousHeadline.get() > 0)
                    {
                        variants.get(variants.size() - 1).description = ms.variantsText
                            .substring(endOfPreviousHeadline.get() + 1, t.getValue0())
                            .trim();
                    }

                    endOfPreviousHeadline.set(indexClosingBracket + 3);

                    variants.add(msv);
                });

                if (variants.size() > 0)
                    variants.get(variants.size() - 1).description = ms.variantsText.substring(endOfPreviousHeadline.get());

                variants.forEach(v -> {
                    if (v.minLevel == VARIANT_I_REQ)
                    {
                        ms.variant1 = v;
                    } else if (v.minLevel == VARIANT_II_REQ)
                    {
                        ms.variant2 = v;
                    } else if (v.minLevel == VARIANT_III_REQ)
                    {
                        ms.variant3 = v;
                    } else if (v.minLevel == VARIANT_IV_REQ)
                    {
                        ms.variant4 = v;
                    } else if (v.minLevel == VARIANT_V_REQ)
                    {
                        ms.variant5 = v;
                    }
                });
            }
        }
    }

    private void applyFlagsForQs(AtomicConverterFlag flags, String text)
    {
        if (text.trim().equals("QS 1") || text.trim().equals("1 QS") || text.trim().equals("1-2 QS")) flags.wasQs1.set(true);
        if (text.trim().equals("QS 2") || text.trim().equals("2 QS") || text.trim().equals("1-2 QS")) flags.wasQs2.set(true);
        if (text.trim().equals("QS 3") || text.trim().equals("3 QS") || text.trim().equals("3-4 QS")) flags.wasQs3.set(true);
        if (text.trim().equals("QS 4") || text.trim().equals("4 QS") || text.trim().equals("3-4 QS")) flags.wasQs4.set(true);
        if (text.trim().equals("QS 5") || text.trim().equals("5 QS") || text.trim().equals("5-6 QS")) flags.wasQs5.set(true);
        if (text.trim().equals("QS 6") || text.trim().equals("6 QS") || text.trim().equals("5-6 QS")) flags.wasQs6.set(true);
    }

    private void handleWasQsValues(AtomicConverterFlag flags, TextWithMetaInfo t)
    {
        flags.wasQs1.set(flags.wasQs1.get() && !t.isBold);
        flags.wasQs2.set(flags.wasQs2.get() && !t.isBold);
        flags.wasQs3.set(flags.wasQs3.get() && !t.isBold);
        flags.wasQs4.set(flags.wasQs4.get() && !t.isBold);
        flags.wasQs5.set(flags.wasQs5.get() && !t.isBold);
        flags.wasQs6.set(flags.wasQs6.get() && !t.isBold);
    }

    protected abstract T initializeType();

    protected abstract void applyDataValue(T last, TextWithMetaInfo t, String cleanText, AtomicConverterFlag flags);

    private T last(List<T> returnValue)
    {
        if (returnValue != null && returnValue.size() > 0)
        {
            return returnValue.get(returnValue.size() - 1);
        } else
        {
            return null;
        }
    }
}
