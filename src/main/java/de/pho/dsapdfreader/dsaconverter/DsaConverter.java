package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.DsaObjectI;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsI;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;
import de.pho.dsapdfreader.tools.csv.DsaStringCleanupTool;

/**
 * Basis Converter für das umwandeln von TextWithMedaInfo in ein RAW Format
 *
 * @param <T> gewünschtes Ergebnis der Konvertierung. Muss vm Typ {@link de.pho.dsapdfreader.dsaconverter.model.DsaObjectI} sein.
 * @param <F> flags zum differenzieren der aktuell gelesenen Inhalte. Abhängig von T. Muss vom Typ {@link ConverterAtomicFlagsI} sein.
 */
public abstract class DsaConverter<T extends DsaObjectI, F extends ConverterAtomicFlagsI>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern patternIsNumber = Pattern.compile("-?\\d+(\\.\\d+)?");

    protected static String concatForDataValue(String origin, String newValue)
    {
        String returnValue = origin == null ? "" : origin.trim();
        if (newValue == null) return returnValue;
        String spacer = (returnValue.endsWith("-") || returnValue.length() == 1) ? "" : " ";
        returnValue = returnValue.endsWith("-") ? returnValue.substring(0, returnValue.length() - 1) : returnValue;
        returnValue = returnValue.trim() + spacer + newValue.trim();
        returnValue = returnValue.replaceAll("\\s\\s", " ");
        returnValue = returnValue.replaceAll("Uberlegener", "Überlegener");
        returnValue = returnValue.replaceAll("Angste", "Ängste");
        return DsaStringCleanupTool.cleanupString(returnValue);
    }

    protected static boolean isNumeric(String strNum)
    {
        if (strNum == null)
        {
            return false;
        }
        return patternIsNumber.matcher(strNum).matches();
    }

    protected String concatForDataValueWithMarkup(String origin, String cleanText, boolean isBold, boolean isItalic)
    {
        String newText = cleanText;
        if (!newText.isEmpty())
        {
            if (isBold) newText = "<b>" + newText + "</b>";
            if (isItalic) newText = "<i>" + newText + "</i>";
        }
        return concatForDataValue(origin, newText);
    }

    public List<T> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
    {
        List<T> returnValue = new ArrayList<>();
        String msg = String.format("parse  result to %s", getClassName());
        LOGGER.debug(msg);

        resultList
            .forEach(t -> {

                String cleanText = t.text
                    .trim();

                // validate the flags for conf
                boolean isFirstValue = validateIsFirstValue(t, conf);
                boolean isFirstValueSkipped = isFirstValue && isNumeric(t.text); // gets skipped, when the firstValue is a number (Page Number in some documents)
                boolean isDataKey = validateIsDataKey(t, cleanText, conf);
                boolean isDataValue = validateIsDataValue(t, cleanText, conf);
                handleWasNoKeyStrings(getFlags(), t); // used in MysticalSkill for QS flags, they act differently, because they are also part of the effect

                finishPredecessorAndStartNew(isFirstValue, isFirstValueSkipped, returnValue, conf, cleanText);

                // handle keys
                if (isDataKey)
                {
                    applyFlagsForKey(t.text);
                }

                // handle values
                if (isDataValue)
                {
                    applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
                    applyFlagsForNoKeyStrings(getFlags(), t.text);
                }

                getFlags().getFirstFlag().set(isFirstValue && !isFirstValueSkipped);


            });
        concludePredecessor(last(returnValue)); //finish the last entry in list
        return returnValue;
    }

    protected void finishPredecessorAndStartNew(boolean isFirstValue, boolean isFirstValueSkipped, List<T> returnValue, TopicConfiguration conf, String cleanText)
    {
        if (isFirstValue && !isFirstValueSkipped && !getFlags().getFirstFlag().get())
        {
            concludePredecessor(last(returnValue));
            handleFirstValue(returnValue, conf, cleanText);
        }
    }

    /**
     * gibt alle Strings zurück welche einen KEY in den konvertierten Texten darstellt. Diese sind abhängig von dem Typen (T)
     *
     * @return Array mit Keys eines DSA Objekts. Arbeitet mit den flags zusammen.
     */
    protected abstract String[] getKeys();

    /**
     * gibt alle Flags zur Konvertierung für den Text in das Zielobjekt zurück. Diese sind abhängig von dem Typen (T)
     *
     * @return Objekt mit Atomic Flags zum Abarbeiten der eines DSA Objekts. Arbeitet mit den keys zusammen.
     */
    protected abstract F getFlags();

    /**
     * gibt den Klassennamen der tatsächlichen Instanz für das Logging zurück
     *
     * @return name der tatsächlichen Implementierung
     */
    protected abstract String getClassName();

    /**
     * initialisiert das Zielobjekt, wenn der aktuelle Text als erstes Element eines DSA Objekts identifiziert wurde.
     *
     * @param returnValue
     * @param conf
     * @param cleanText
     */
    protected abstract void handleFirstValue(List<T> returnValue, TopicConfiguration conf, String cleanText);

    /**
     * Übernimmt einen key und setzt die entsprechenden flags
     *
     * @param key Text, welcher einen key repräsentieren sollte
     */
    protected abstract void applyFlagsForKey(String key);

    /**
     * Wendet einen Datenwert auf das aktuellste Zielobjekt an
     *
     * @param currentDataObject das aktuellste Zielobjekt
     * @param cleanText         bereinigte Version des textes zum Setzen im aktuellen Zielobjekt
     * @param isBold            markiert den Text als Fettdruck
     * @param isItalic          markiert den Text als Kursivdruck
     */
    protected abstract void applyDataValue(T currentDataObject, String cleanText, boolean isBold, boolean isItalic);

    protected abstract void concludePredecessor(T lastEntry);

    public boolean validateIsFirstValue(TextWithMetaInfo t, TopicConfiguration conf)
    {
        return t.size == conf.nameSize;
    }

    protected void applyFlagsForNoKeyStrings(F flags, String text)
    {
    }

    protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
    {
        return t.size == 800 && !t.text.isEmpty();
    }

    protected void handleWasNoKeyStrings(F flags, TextWithMetaInfo t)
    {

    }

    protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
    {
        return !this.validateIsDataKey(t, cleanText, conf);
    }

    protected T last(List<T> returnValue)
    {
        if (returnValue != null && !returnValue.isEmpty())
        {
            return returnValue.get(returnValue.size() - 1);
        }
        else
        {
            return null;
        }
    }

    protected boolean isMatch(Pattern pattern, String text)
    {
        return pattern.matcher(text).find();
    }

    protected String firstMatch(Pattern pattern, String text)
    {
        List<String> results = this.matches(pattern, text);
        return results.size() > 0 ? results.get(0) : null;
    }

    protected List<String> matches(Pattern pattern, String text)
    {
        List<String> returnValue = new ArrayList<>();
        Matcher msNameMatcher = pattern.matcher(text);

        while (msNameMatcher.find())
        {
            returnValue.add(msNameMatcher.group().trim());
        }

        return returnValue;
    }
}
