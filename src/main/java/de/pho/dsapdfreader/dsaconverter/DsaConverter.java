package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.DsaObjectI;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public abstract class DsaConverter<T extends DsaObjectI>
{

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
    private static final String KEY_VARIANTS = "Zaubererweiterungen";

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
        KEY_VARIANTS
    };

    private static void applyFlagsForKey(AtomicConverterFlag flags, String text)
    {
        flags.wasName.set(false);
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
        flags.wasVariants.set(text.trim().equals(KEY_VARIANTS));
    }

    protected static String concatForDataValue(String origin, String newValue)
    {
        String returnValue = origin == null ? "" : origin.trim();
        String spacer = returnValue.endsWith("-") ? "" : " ";
        returnValue = returnValue.endsWith("-") ? returnValue.substring(0, returnValue.length() - 1) : returnValue;
        returnValue = returnValue.trim() + spacer + newValue.trim();
        return returnValue.trim();
    }

    protected static String concatForEffect(String origin, TextWithMetaInfo t)
    {
        String newText = t.text;
        if (t.isBold) newText = "<b>" + newText + "</b>";
        if (t.isItalic) newText = "<i>" + newText + "</i>";
        return concatForDataValue(origin, newText);
    }


    private static boolean validateIsDataValue(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size <= conf.dataSize && Arrays.stream(KEYS).noneMatch(k -> k.equals(t.text.trim()));
    }

    private static boolean validateIsDataKey(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size <= conf.dataSize && t.isBold && Arrays.stream(KEYS).anyMatch(k -> k.equals(t.text.trim()));
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

                    // determine if current line is the start after which configuration gets interpreted
                    boolean isStarted = t.onPage >= conf.startPage && (
                        conf.startAfterLine <= 0 || (lineOnPage.get() >= conf.startAfterLine)
                    );

                    // determine if current line is the last line for which configuration gets interpreted
                    boolean isFinished = t.onPage >= conf.endPage && (
                        conf.endAfterLine > 0 && (lineOnPage.get() > conf.endAfterLine)
                    );

                    // as long as it was started and is not finished
                    if (flags.wasStarted.get() && !isFinished)
                    {
                        // validate the flags for conf
                        boolean isName = validateIsName(t, conf);
                        boolean isNameSkipped = isName && isNumeric(t.text); // gets skipped, when the name is a number (Page Number in some documents)
                        boolean isDataKey = validateIsDataKey(t, conf);
                        boolean isDataValue = validateIsDataValue(t, conf);
                        boolean isVariant = validateIsVariant(t, flags);

                        // handle new variant, find out which one.
                        // assemble text and assign to right one
                        // turn over to next
                        // do a good finish

                        // validate the QS flags, they act differently, because they are also part of the effect
                        handleWasQsValues(flags, t);

                        // handle name
                        if (isName && !isNameSkipped)
                        {
                            if (!flags.wasName.get())
                            {
                                T newEntry = initializeType();
                                flags.initDataFlags();
                                newEntry.setTopic(conf.topic);
                                returnValue.add(newEntry);
                            }

                            last(returnValue).setName(concatForDataValue(last(returnValue).getName(), t.text.trim()));

                        }

                        // handle keys
                        if (isDataKey)
                        {
                            applyFlagsForKey(flags, t.text);
                        }

                        // handle values
                        if (isDataValue)
                        {
                            applyDataValue(last(returnValue), t, flags);
                            applyFlagsForQs(flags, t.text);
                        }

                        flags.wasName.set(isName && !isNameSkipped);

                    }
                    if (!flags.wasStarted.get())
                    {
                        flags.wasStarted.set(isStarted);
                    }
                    flags.wasFinished.set(isFinished);
                }
            });
        return returnValue;
    }

    protected boolean validateIsVariant(TextWithMetaInfo t, AtomicConverterFlag flags)
    {
        return flags.wasVariants.get() && t.isItalic;
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

    protected abstract void applyDataValue(T last, TextWithMetaInfo t, AtomicConverterFlag flags);

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

    protected String handleQsDescription(AtomicBoolean wasQs, String effect, String text, AtomicBoolean wasEffect)
    {
        effect = concatForDataValue(effect, text);
        wasEffect.set(true);
        return effect;
    }
}
