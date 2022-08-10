package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.DsaObjectI;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;
import de.pho.dsapdfreader.tools.csv.DsaStringCleanupTool;

public abstract class DsaConverter<T extends DsaObjectI>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern patternIsNumber = Pattern.compile("-?\\d+(\\.\\d+)?");

    protected static String concatForDataValue(String origin, String newValue)
    {
        String returnValue = origin == null ? "" : origin.trim();
        String spacer = returnValue.endsWith("-") ? "" : " ";
        returnValue = returnValue.endsWith("-") ? returnValue.substring(0, returnValue.length() - 1) : returnValue;
        returnValue = returnValue.trim() + spacer + newValue.trim();
        return DsaStringCleanupTool.cleanupString(returnValue);
    }

    public List<T> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
    {
        List<T> returnValue = new ArrayList<>();
        String msg = String.format("parse  result to %s", initializeType().getClass().getName());
        LOGGER.debug(msg);
        AtomicConverterFlag flags = new AtomicConverterFlag();

        resultList
            .forEach(t -> {

                String cleanText = t.text
                    .trim();

                // validate the flags for conf
                boolean isName = validateIsName(t, conf);
                boolean isNameSkipped = isName && isNumeric(t.text); // gets skipped, when the name is a number (Page Number in some documents)
                boolean isDataKey = validateIsDataKey(t, cleanText, conf);
                boolean isDataValue = validateIsDataValue(t, cleanText, conf);

                // used in MysticalSkill for QS flags, they act differently, because they are also part of the effect
                handleWasNoKeyStrings(flags, t);
                handleName(isName, isNameSkipped, flags, returnValue, conf, cleanText);

                // handle keys
                if (isDataKey)
                {
                    applyFlagsForKey(flags, t.text);
                }

                // handle values
                if (isDataValue)
                {
                    applyDataValue(last(returnValue), t, cleanText, flags);
                    applyFlagsForNoKeyStrings(flags, t.text);
                }

                flags.wasName.set(isName && !isNameSkipped);

            });
        concludePredecessor(returnValue); //finish the last entry in list
        return returnValue;
    }

    protected void applyFlagsForNoKeyStrings(AtomicConverterFlag flags, String text)
    {
    }

    protected void handleWasNoKeyStrings(AtomicConverterFlag flags, TextWithMetaInfo t)
    {
    }

    protected abstract void applyFlagsForKey(AtomicConverterFlag flags, String text);

    protected abstract String[] getKeys();

    protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
    {
        return t.size <= conf.dataSize && Arrays.stream(getKeys()).noneMatch(k -> k.equals(cleanText));
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

    protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
    {
        return t.size <= conf.dataSize && t.isBold && Arrays.asList(getKeys()).contains(cleanText);
    }

    protected static boolean validateIsName(TextWithMetaInfo t, TopicConfiguration conf)
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


    private void handleName(boolean isName, boolean isNameSkipped, AtomicConverterFlag flags, List<T> returnValue, TopicConfiguration conf, String cleanText)
    {
        // handle name
        if (isName && !isNameSkipped)
        {
            if (!flags.wasName.get())
            {
                concludePredecessor(returnValue);
                T newEntry = initializeType();
                flags.initDataFlags();
                newEntry.setTopic(conf.topic);
                newEntry.setPublication(conf.publication);
                returnValue.add(newEntry);
            }

            last(returnValue).setName(concatForDataValue(last(returnValue).getName(), cleanText));
        }
    }

    protected abstract void concludePredecessor(List<T> elementList);

    protected abstract T initializeType();

    protected abstract void applyDataValue(T last, TextWithMetaInfo t, String cleanText, AtomicConverterFlag flags);

    protected T last(List<T> returnValue)
    {
        if (returnValue != null && !returnValue.isEmpty())
        {
            return returnValue.get(returnValue.size() - 1);
        } else
        {
            return null;
        }
    }
}
