package de.pho.dsapdfreader.dsaconverter;


import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.ContentRaw;
import de.pho.dsapdfreader.dsaconverter.model.CurriculumRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicContentFlags;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsCurriculum;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DsaConverterContent extends DsaConverter<ContentRaw, ConverterAtomicContentFlags> {
    private static final String KEY_GUIDELINE = "Leitlinie";
    private static final String KEY_SPELL_SELECTION = "Wahlzauberpaket";
    private static final String KEY_SPELL_RESTRICTION = "Eingeschränkte Zauber";
    private static final String KEY_SPELL_CHANGES = "Zauberänderungen:";
    private static final String KEY_ADDITIONAL_SKILLS = "Fertigkeiten (+):";
    private static final String KEY_REMOVED_SKILLS = "Fertigkeiten (–):";
    protected static final String[] KEYS = {
            KEY_GUIDELINE,
            KEY_SPELL_SELECTION,
            KEY_SPELL_RESTRICTION,
            KEY_SPELL_CHANGES,
            KEY_ADDITIONAL_SKILLS,
            KEY_REMOVED_SKILLS
    };
    private static final Logger LOGGER = LogManager.getLogger();
    private final AtomicInteger currentPath = new AtomicInteger(0);
    private List<ContentRaw> rawList;
    private ConverterAtomicContentFlags flags;

    private AtomicReference<ContentRaw> currentContent = new AtomicReference<>();

    @Override
    public List<ContentRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf) {
        rawList = new ArrayList<>();
        String msg = String.format("parse  result to %s", getClassName());
        LOGGER.debug(msg);

        flags = new ConverterAtomicContentFlags();

        resultList.stream()
                .filter(t -> t.onLine > (t.onPage % 2 == 0 ? 1 : 2)) //Filter um Seite und Land & Leute in Gestaden auszufiltern, da die immer in den ersten beiden Zeilen zu finden sind
                .forEach(t -> {
                    String cleanText = formatInline(t.text.trim(), t.isBold, t.isItalic, t.font, t.size);

                    if (!cleanText.isEmpty()) {
                        ContentRaw predecessor = this.currentContent.get();
                        if (predecessor != null && rawIsSameText(predecessor, t)) {
                            predecessor.content = predecessor.content.trim() + (predecessor.textSize < 1000 ? " " : "") + cleanText.trim();
                            predecessor.isFinishedSentence = t.text.endsWith(".") || t.text.endsWith("!") || t.text.endsWith("?");
                        } else {
                            if (predecessor != null) {
                                formatRaw(predecessor);
                                rawList.add(predecessor);
                            }
                            ContentRaw newRaw = new ContentRaw();
                            newRaw.textSize = t.size;
                            newRaw.isBold = t.isBold || t.font.toLowerCase().contains("bold");
                            newRaw.isItalic = t.isItalic || t.font.toLowerCase().contains("italic");
                            newRaw.content = cleanText;
                            newRaw.isFinishedSentence = t.text.endsWith(".") || t.text.endsWith("!") || t.text.endsWith("?");
                            if (newRaw.isFinishedSentence) {
                                formatRaw(newRaw);
                                rawList.add(newRaw);
                                currentContent.set(null);
                            } else {
                                currentContent.set(newRaw);
                            }
                        }
                    }

                });
        concludePredecessor(last(rawList)); //finish the last entry in list
        return rawList;
    }

    private String formatInline(String cleanText, boolean isBold, boolean isItalic, String font, int size) {
        if (size > 900) return cleanText;
        else if (isBold || font.toLowerCase().contains("bold")) return "**" + cleanText + "**";
        else if (isItalic || font.toLowerCase().contains("italic")) return "*" + cleanText + "*";
        else return cleanText;
    }

    private void formatRaw(ContentRaw newRaw) {
        newRaw.content = (newRaw.content.matches("[0-9\\s§]*")) ? "" : newRaw.content;

        if (isCamelCase(newRaw.content)) {
            newRaw.content = convertCamelCase(newRaw.content);
        }

        if (newRaw.textSize >= 1800) newRaw.content = "# " + newRaw.content;
        else if (newRaw.textSize >= 1000) newRaw.content = "## " + newRaw.content;

    }

    public static boolean isCamelCase(String str) {
        if (str == null || str.isEmpty()) return false;
        System.out.println(str + " --> " + str.matches(".*([a-z])([A-Z]).*"));
        return str.matches(".*([a-z])([A-Z]).*");
    }

    // Method to convert the string to lowercase except the first letter
    public static String convertCamelCase(String str) {
        if (str == null || str.isEmpty()) return str;
        String lowerCased = str.toLowerCase();
        return Character.toUpperCase(lowerCased.charAt(0)) + lowerCased.substring(1);
    }

    private boolean rawIsSameText(ContentRaw cr, TextWithMetaInfo t) {
        return cr.textSize >= 1000 && cr.textSize < 1800 && t.size >= 1000 && t.size < 1800
                || cr.textSize >= 1800 && t.size >= 1800
                || cr.textSize < 1000 && t.size < 1000 && !cr.isFinishedSentence
                //&& predecessor.isBold == t.isBold
                //   && predecessor.isItalic == t.isItalic
                ;
    }

    @Override
    protected String[] getKeys() {
        return KEYS;
    }

    @Override
    protected ConverterAtomicContentFlags getFlags() {
        return this.flags;
    }

    @Override
    protected String getClassName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    protected void handleFirstValue(List<ContentRaw> returnValue, TopicConfiguration conf, String cleanText) {
    }

    @Override
    protected void applyFlagsForKey(String txt) {
    }

    @Override
    protected void applyDataValue(ContentRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic) {
    }

    @Override
    public boolean validateIsFirstValue(TextWithMetaInfo t, TopicConfiguration conf) {
        return Boolean.TRUE;
    }


    @Override
    protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
        return Arrays.stream(KEYS).anyMatch(k -> k.equals(cleanText))
                && (t.isBold || t.isItalic)
                && !validateIsFirstValue(t, conf)
                || cleanText.endsWith("AP)");
    }

    @Override
    protected void concludePredecessor(ContentRaw lastEntry) {
    }
}
