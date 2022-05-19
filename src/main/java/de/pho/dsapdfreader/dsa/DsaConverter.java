package de.pho.dsapdfreader.dsa;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.DsaPdfReaderMain;
import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsa.model.Trick;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverter
{

    private static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static List<Trick> convertTextWithMetaInfoToTrick(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
    {
        List<Trick> returnValue = new ArrayList<>();
        DsaPdfReaderMain.LOGGER.debug("parseResultToTrick");
        AtomicBoolean categoryStarted = new AtomicBoolean(false);
        AtomicBoolean wasName = new AtomicBoolean(false);
        AtomicBoolean wasFeature = new AtomicBoolean(false);
        AtomicBoolean wasDuration = new AtomicBoolean(false);
        AtomicBoolean wasRemarks = new AtomicBoolean(false);
        AtomicBoolean wasTargetCategory = new AtomicBoolean(false);
        AtomicBoolean wasRange = new AtomicBoolean(false);

        AtomicBoolean wasFinished = new AtomicBoolean(false);

        resultList.stream()
            .forEach(t -> {
                if (!wasFinished.get())
                {
                    boolean isFinished = validateIsFinished(t, conf);
                    boolean isName = validateIsName(t, conf);
                    boolean isDataKey = validateIsDataKey(t, conf);
                    boolean isDataValue = validateIsDataValue(t, conf);

                    if (isName)
                    {
                        returnValue.add(new Trick());
                        last(returnValue).name = t.text;
                    }
                    if (isDataKey)
                    {
                        wasName.set(t.size == conf.getNameSize());
                        wasDuration.set(t.text.equals("Wirkungsdauer"));
                        wasFeature.set(t.text.equals("Merkmal"));
                        wasRange.set(t.text.equals("Reichweite"));
                        wasRemarks.set(t.text.equals("Anmerkung"));
                        wasTargetCategory.set(t.text.equals("Zielkategorie"));
                    }

                    if (isDataValue)
                    {
                        applyDataValue(
                            returnValue,
                            t,
                            conf,
                            wasName.get(),
                            wasDuration.get(),
                            wasFeature.get(),
                            wasRange.get(),
                            wasRemarks.get(),
                            wasTargetCategory.get()
                        );
                    }

                    wasName.set(isName);
                    wasFinished.set(isFinished);
                }
            });
        return returnValue;
    }

    private static void applyDataValue(List<Trick> returnValue, TextWithMetaInfo t, TopicConfiguration conf, boolean wasName, boolean wasDuration, boolean wasFeature, boolean wasRange, boolean wasRemarks, boolean wasTargetCategory)
    {
        Trick tr = last(returnValue);
        if (wasName) tr.description += concatForDataValue(tr.description, t.text);
        if (wasRange) tr.range += concatForDataValue(tr.range, t.text);
        if (wasDuration) tr.duration += concatForDataValue(tr.duration, t.text);
        if (wasFeature) tr.feature += concatForDataValue(tr.feature, t.text);
        if (wasTargetCategory) tr.targetCategory += concatForDataValue(tr.targetCategory, t.text);
        if (wasRemarks) tr.remarks += concatForDataValue(tr.remarks, t.text);
    }

    private static String concatForDataValue(String origin, String newValue)
    {
        String returnValue = origin == null ? "" : origin.trim();
        boolean isLinebreak = returnValue.endsWith("-");
        returnValue = isLinebreak ? returnValue.substring(0, returnValue.length() - 2) : returnValue;
        returnValue += isLinebreak ? "" : " " + newValue.trim();
        return returnValue;
    }

    private static boolean validateIsDataValue(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size <= conf.getDataSize() && !t.isBold;
    }

    private static boolean validateIsDataKey(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size <= conf.getDataSize() && t.isBold;
    }

    private static boolean validateIsName(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size == conf.getNameSize() && !isNumeric(t.text);
    }

    private static boolean validateIsFinished(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size == conf.getEndSize()
            && t.text.equals(conf.getEndContent());
    }

    private static boolean isNumeric(String strNum)
    {
        if (strNum == null)
        {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    private static Trick last(List<Trick> returnValue)
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
