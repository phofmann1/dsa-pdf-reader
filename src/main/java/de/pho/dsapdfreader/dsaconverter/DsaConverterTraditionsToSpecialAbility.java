package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.TraditionRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsTradition;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityCategoryKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterTraditionsToSpecialAbility extends DsaConverter<TraditionRaw, ConverterAtomicFlagsTradition> {

  protected static final Logger LOGGER = LogManager.getLogger();
  protected static final List<String> TRAD_ALVERAN_MINOR_NAMES = List.of(new String[]{
      "Tradition AVeskirche",
      "Tradition Ifirnkirche",
      "Tradition Korkirche",
      "Tradition LeVthankult",
      "Tradition Marbokult",
      "Tradition Nanduskirche",
      "Tradition SWafnirkirche"
  });
  private static final String KEY_AP_VALUE = "AP-Wert";
  private static final String KEY_PRECONDITIONS = "Voraussetzungen";

  protected static final String[] KEYS = {
      KEY_AP_VALUE,
      KEY_PRECONDITIONS,
  };
  private ConverterAtomicFlagsTradition flags;

  public static String convertUppercase(String sentence) {

    // Define a regular expression pattern to match uppercase letters not preceded by a word boundary
    // or a hyphen or an apostrophe
    Pattern pattern = Pattern.compile("(?<=[A-ü])[A-Z]");

    // Create a matcher for the input sentence
    Matcher matcher = pattern.matcher(sentence);

    // Replace uppercase letters matching the pattern with their lowercase equivalents
    StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(result, matcher.group().toLowerCase());
    }
    matcher.appendTail(result);

    return result.toString();
  }

  @Override
  protected String[] getKeys() {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsTradition getFlags() {
    if (flags == null) {
      this.flags = new ConverterAtomicFlagsTradition();
    }
    return flags;
  }


  @Override
  public List<TraditionRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> texts, TopicConfiguration conf) {
    List<TraditionRaw> returnValue = new ArrayList<>();

    AtomicReference<DataInterpretationMode> previousInterpretationMode = new AtomicReference<>(DataInterpretationMode.none);
    AtomicReference<DataInterpretationMode> interpretationMode = new AtomicReference<>(DataInterpretationMode.none);
    AtomicReference<Integer> previousSize = new AtomicReference<>(0);
    AtomicReference<Integer> currentSize = new AtomicReference<>(0);
    AtomicReference<String> currentValue = new AtomicReference<>("");
    AtomicReference<String> currentPantheon = new AtomicReference<>();
    AtomicReference<TraditionRaw> currentTradition = new AtomicReference<>();
    texts.forEach(t -> {
      String cleanText = t.text.trim();

      currentSize.set(t.size);
      if (t.size == 9800) {
        interpretationMode.set(DataInterpretationMode.pantheon);
      }
      else if (t.size == 1400) {
        interpretationMode.set(DataInterpretationMode.name);
      }
      else if (t.size == 900 && t.text.equals("#")) {
        interpretationMode.set(DataInterpretationMode.description);
      }
      else if (t.size == 800 && t.text.startsWith("Voraussetzungen:")) {
        interpretationMode.set(DataInterpretationMode.preconditions);
      }
      else if (t.size == 800 && t.text.startsWith("AP")) {
        interpretationMode.set(DataInterpretationMode.ap);
      }

      if (previousInterpretationMode.get() == DataInterpretationMode.none) {
        previousInterpretationMode.set(interpretationMode.get());
      }

      if (previousInterpretationMode.get() != interpretationMode.get()) {

        switch (previousInterpretationMode.get()) {
        case pantheon -> {
          currentPantheon.set(currentValue.get());
        }
        case name -> {
          if (currentTradition.get() != null) returnValue.add(currentTradition.get());
          currentTradition.set(new TraditionRaw());
          currentTradition.get().name = convertUppercase(currentValue.get()
              .replace("Die ", "")
              .replace(" der ", " ")
              .replace("als Sonderfertigkeit", "")
              .replace("(", "")
              .replace(")", "")
              .replace("  ", " ")
              .trim());
          currentTradition.get().pantheon = currentPantheon.get();
          currentTradition.get().publication = conf.publication;
          currentTradition.get().topic = conf.topic;
          currentTradition.get().categoryKey = currentPantheon.get() == null
              ? SpecialAbilityCategoryKey.tradition_magic
              : (currentPantheon.get().equals(" Schamanen")
              ? SpecialAbilityCategoryKey.tradition_shaman
              : (currentPantheon.get().equals(" Priester anderer Gottheiten")
              ? (TRAD_ALVERAN_MINOR_NAMES.stream().anyMatch(tn -> tn.equals(currentTradition.get().name))
              ? SpecialAbilityCategoryKey.tradition_alveran_minor
              : SpecialAbilityCategoryKey.tradition_non_alveran)
              : SpecialAbilityCategoryKey.tradition_alveran_major));
        }
        case description -> {
          currentTradition.get().description = currentValue.get();
        }
        case preconditions -> {
          currentTradition.get().preconditions = currentValue.get().replace("Voraussetzungen:", "");
        }
        case ap -> {
          String cleanedValue = currentValue.get()
              .replace("AP-Wert:", "")
              .replace("397", "140 AP")
              .trim(); //Korrektur Fehler bei Firun
          currentTradition.get().ap = cleanedValue
              .substring(0, cleanedValue.indexOf(" "))

          ;
        }
        default -> {
          LOGGER.error("UNDEFINED InterpretationMode (" + t.text + ") Size:" + t.size);
        }
        }
        currentValue.set("");
        previousInterpretationMode.set(interpretationMode.get());
      }

      cleanText = (t.size == 9800 || t.size == 1400) ? " " + cleanText.toUpperCase() : cleanText;
      cleanText = cleanText.equals("#") ? " " + cleanText + " " : cleanText;
      currentValue.set(currentValue.get() + cleanText);

      previousSize.set(t.size);
    });

    // finish last
    String cleanedValue = currentValue.get()
        .replace("AP-Wert:", "")
        .replace("397", "140 AP")
        .trim(); //Korrektur Fehler bei Firun
    currentTradition.get().ap = cleanedValue
        .substring(0, cleanedValue.indexOf(" "));
    returnValue.add(currentTradition.get());
    //end finish last
    return returnValue;
  }

  private boolean validateIsName(TextWithMetaInfo t, TopicConfiguration conf) {
    return false;
  }

  @Override
  protected String getClassName() {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<TraditionRaw> returnValue, TopicConfiguration conf, String cleanText) {


  }

  @Override
  protected void applyFlagsForKey(String key) {

  }

  @Override
  protected void applyDataValue(TraditionRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic) {

  }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return false;
  }

  @Override
  protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return false;
  }

  @Override
  protected void concludePredecessor(TraditionRaw lastEntry) {
  }

  public enum DataInterpretationMode {
    none,
    pantheon,
    name,
    description,
    preconditions,
    ap
  }
}

