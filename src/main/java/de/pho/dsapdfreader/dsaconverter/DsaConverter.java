package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private static final String KEY_FAETURE = "Merkmal";
    private static final String KEY_RANGE = "Reichweite";
    private static final String KEY_REMARK = "Anmerkung";
    private static final String KEY_TARGET_CATEGORY = "Zielkategorie";
    private static final String KEY_CHECK = "Probe";
    private static final String KEY_EFFECT = "Wirkung";
    private static final String KEY_CASTING_DURATION_SPELL = "Zauberdauer";
    private static final String KEY_CASTING_DURATION_RITUAL = "Ritualdauer";
    private static final String KEY_CASTING_DURATION_LITURGY = "Liturgiedauer";
    private static final String KEY_CASTING_DURATION_CEREMONY = "Zeremoniedauer";
    private static final String KEY_COST_APS = "AsP-Kosten";
    private static final String KEY_COST_KAP = "KaP-Kosten";
    private static final String KEY_COMMONNESS = "Verbreitung";
    private static final String KEY_ADVANCEMENT_CATEGORY = "Steigerungsfaktor";

    private static final String[] KEYS = {
        KEY_DURATION,
        KEY_FAETURE,
        KEY_RANGE,
        KEY_REMARK,
        KEY_TARGET_CATEGORY,
        KEY_CHECK,
        KEY_EFFECT,
        KEY_CASTING_DURATION_SPELL,
        KEY_CASTING_DURATION_RITUAL,
        KEY_CASTING_DURATION_LITURGY,
        KEY_CASTING_DURATION_CEREMONY,
        KEY_COST_APS,
        KEY_COST_KAP,
        KEY_COMMONNESS,
        KEY_ADVANCEMENT_CATEGORY,
    };

    private static void applyFlagsForKey(AtomicConverterFlag flags, String text)
    {
        flags.wasName.set(false);
        flags.wasDuration.set(text.trim().equals(KEY_DURATION));
        flags.wasFeature.set(text.trim().equals(KEY_FAETURE));
        flags.wasRange.set(text.trim().equals(KEY_RANGE));
        flags.wasRemarks.set(text.trim().equals(KEY_REMARK));
        flags.wasTargetCategory.set(text.trim().equals(KEY_TARGET_CATEGORY));
        flags.wasCheck.set(text.trim().equals(KEY_CHECK));
        flags.wasEffect.set(text.trim().equals(KEY_EFFECT));
        flags.wasCastingDuration.set(text.trim().equals(KEY_CASTING_DURATION_SPELL)
            || text.trim().equals(KEY_CASTING_DURATION_RITUAL)
            || text.trim().equals(KEY_CASTING_DURATION_LITURGY)
            || text.trim().equals(KEY_CASTING_DURATION_CEREMONY));
        flags.wasCost.set(text.trim().equals(KEY_COST_APS) || text.trim().equals(KEY_COST_KAP));
        flags.wasCommonness.set(text.trim().equals(KEY_COMMONNESS));
        flags.wasAdvancementCategory.set(text.trim().equals(KEY_ADVANCEMENT_CATEGORY));
    }

    private static boolean validateIsStarted(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size == conf.startSize && t.text.trim().equals(conf.startContent);
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
        return t.size <= conf.dataSize && !Arrays.stream(KEYS).anyMatch(k -> k.equals(t.text.trim()));
    }

    private static boolean validateIsDataKey(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size <= conf.dataSize && t.isBold && Arrays.stream(KEYS).anyMatch(k -> k.equals(t.text.trim()));
    }

    private static boolean validateIsName(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size == conf.nameSize;
    }

    private static boolean validateIsFinished(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size == conf.endSize
            && t.text.trim().equals(conf.endContent);
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

        resultList
            .forEach(t -> {
                if (!flags.wasFinished.get())
                {

                    boolean isStarted = validateIsStarted(t, conf);
                    boolean isFinished = validateIsFinished(t, conf);

                    if (flags.wasStarted.get() && !isFinished)
                    {
                        boolean isName = validateIsName(t, conf);
                        boolean isNameSkipped = isName && isNumeric(t.text);
                        boolean isDataKey = validateIsDataKey(t, conf);
                        boolean isDataValue = validateIsDataValue(t, conf);

                        handleWasQsValues(flags, t);

                        if (isName && !isNameSkipped)
                        {
                            returnValue.add(initializeType());
                            flags.initDataFlags();
                            last(returnValue).setName(t.text.trim());
                            last(returnValue).setTopic(conf.topic);
                        }
                        if (isDataKey)
                        {
                            applyFlagsForKey(flags, t.text);
                        }

                        if (isDataValue)
                        {
                            applyDataValue(last(returnValue), t, flags);
                            applyFlagsForQs(flags, t.text);
                        }
                        if (isName)
                        {
                            flags.wasName.set(true);
                        }
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
