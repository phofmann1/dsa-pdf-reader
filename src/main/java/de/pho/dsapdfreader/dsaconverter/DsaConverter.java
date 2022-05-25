package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.DsaPdfReaderMain;
import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.DsaObjectI;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public abstract class DsaConverter<T extends DsaObjectI>
{

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern patternIsNumber = Pattern.compile("-?\\d+(\\.\\d+)?");

    private static void applyFlagsForKey(AtomicConverterFlag flags, String text)
    {
        flags.wasName.set(false);
        flags.wasDuration.set(text.trim().equals("Wirkungsdauer"));
        flags.wasFeature.set(text.trim().equals("Merkmal"));
        flags.wasRange.set(text.trim().equals("Reichweite"));
        flags.wasRemarks.set(text.trim().equals("Anmerkung"));
        flags.wasTargetCategory.set(text.trim().equals("Zielkategorie"));
        flags.wasCheck.set(text.trim().equals("Probe"));
        flags.wasEffect.set(text.trim().equals("Wirkung"));
        flags.wasCastingDuration.set(text.trim().equals("Zauberdauer"));
        flags.wasCost.set(text.trim().equals("AsP-Kosten"));
        flags.wasCommonness.set(text.trim().equals("Verbreitung"));
        flags.wasAdvancementCategory.set(text.trim().equals("Steigerungsfaktor"));
        flags.wasQs1.set(text.trim().equals("QS 1"));
        flags.wasQs2.set(text.trim().equals("QS 2"));
        flags.wasQs3.set(text.trim().equals("QS 3"));
        flags.wasQs4.set(text.trim().equals("QS 4"));
        flags.wasQs5.set(text.trim().equals("QS 5"));
        flags.wasQs6.set(text.trim().equals("QS 6"));
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

    private static boolean validateIsDataValue(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size <= conf.dataSize && !t.isBold;
    }

    private static boolean validateIsDataKey(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size <= conf.dataSize && t.isBold;
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
        DsaPdfReaderMain.LOGGER.debug("parse  result to " + initializeType().getClass().getName());
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

    protected String handleQsDescription(AtomicBoolean wasQs, String effect, String text, String qsPrefix, AtomicBoolean wasEffect)
    {
        effect = concatForDataValue(effect, qsPrefix);
        effect = concatForDataValue(effect, text);
        wasQs.set(false);
        wasEffect.set(true);
        return effect;
    }
}
