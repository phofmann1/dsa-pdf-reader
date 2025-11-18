package de.pho.dsapdfreader.dsaconverter;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.dsaconverter.model.HerbRaw;
import de.pho.dsapdfreader.dsaconverter.model.HerbRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsHerb;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsProfession;
import de.pho.dsapdfreader.exporter.model.enums.ProfessionTypeKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DsaConverterHerb extends DsaConverter<HerbRaw, ConverterAtomicFlagsHerb>
{

  private static final String KEY_ALTERNATIVE_NAMEN = "Alternative Namen";
  private static final String KEY_VERBREITUNG = "Verbreitung";
  private static final String KEY_SUCHSCHWIERIGKEIT = "Suchschwierigkeit";
  private static final String KEY_BESTIMMUNGSSCHWIERIGKEIT = "Bestimmungsschwierigkeit";
  private static final String KEY_ANWENDUNGEN = "Anwendungen";
  private static final String KEY_WIRKUNG = "Wirkung";
  private static final String KEY_VERARBEITET = "Verarbeitet";
  private static final String KEY_PREIS = "Preis";
  private static final String KEY_REZEPTE = "Rezepte";
  private static final String KEY_ALTAGSARZNEIEN_UND_VOLKSBRAUCHTUM = "Alltagsarzneien und Volksbrauchtum";
  private static final String KEY_HALTBARKEIT = "Haltbarkeit";
  private static final String KEY_WAS_WEISS_MEIN_HELD_ÜBER  = "Was weiß mein Held über ";
  protected static final String[] KEYS = {
          KEY_ALTERNATIVE_NAMEN,
          KEY_VERBREITUNG,
          KEY_SUCHSCHWIERIGKEIT,
          KEY_BESTIMMUNGSSCHWIERIGKEIT,
          KEY_ANWENDUNGEN,
          KEY_WIRKUNG,
          KEY_VERARBEITET,
          KEY_PREIS,
          KEY_REZEPTE,
          KEY_ALTAGSARZNEIEN_UND_VOLKSBRAUCHTUM,
          KEY_HALTBARKEIT,
          KEY_WAS_WEISS_MEIN_HELD_ÜBER
  };
  private ConverterAtomicFlagsHerb flags;

  @Override
  protected String[] getKeys()
  {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsHerb getFlags()
  {
    if (flags == null)
    {
      this.flags = new ConverterAtomicFlagsHerb();
    }
    return flags;
  }

  @Override
  public List<HerbRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> texts, TopicConfiguration conf) {
    List<HerbRaw> returnValue = new ArrayList<>();

   AtomicReference<Boolean> isNewPage = new AtomicReference<>(Boolean.FALSE);
    AtomicReference<Integer> lastPage = new AtomicReference<>(-1);

    texts.forEach(t -> {

      isNewPage.set(lastPage.get() != t.onPage);
      lastPage.set(t.onPage);

      String cleanText = t.text.trim();

      boolean isDataKey = validateIsDataKey(t, cleanText, conf);
      boolean isDataValue = validateIsDataValue(t, cleanText, conf);

      //handle start of Profession
      if (isNewPage.get()) {
        HerbRaw newEntry = new HerbRaw();
        newEntry.topic = conf.topic;
        newEntry.publication = conf.publication;
        this.getFlags().initDataFlags();
        returnValue.add(newEntry);
      }

      if(t.size == conf.nameSize) {
        last(returnValue).name = concatForDataValue(last(returnValue).name, cleanText);
      }

      // handle keys
      if (isDataKey) {
        applyFlagsForKey(t.text);
      }

      // handle values
      if (isDataValue)
      {
        applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
        applyFlagsForNoKeyStrings(this.getFlags(), t.text);
      }
    });
    return returnValue;
  }

  private boolean validateIsName(TextWithMetaInfo t, TopicConfiguration conf) {
    return t.size == conf.nameSize;
  }

  @Override
  protected String getClassName()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<HerbRaw> returnValue, TopicConfiguration conf, String cleanText)
  {

    if (!this.getFlags().getFirstFlag().get())
    {
      HerbRaw newEntry = new HerbRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      returnValue.add(newEntry);
    }
    last(returnValue).setName(concatForDataValue(last(returnValue).getName(), cleanText));
  }

  @Override
  protected void applyFlagsForKey(String key)
  {
    this.getFlags().wasName.set(false);this.getFlags().wasName.set(false);
    this.getFlags().wasAlternativeNamen.set(key.endsWith(KEY_ALTERNATIVE_NAMEN));
    this.getFlags().wasVerbreitung.set(key.endsWith(KEY_VERBREITUNG));
    this.getFlags().wasSuchschwierigkeit.set(key.equals(KEY_SUCHSCHWIERIGKEIT));
    this.getFlags().wasBestimmungsschwierigkeit.set(key.equals(KEY_BESTIMMUNGSSCHWIERIGKEIT));
    this.getFlags().wasAnwendungen.set(key.equals(KEY_ANWENDUNGEN));
    this.getFlags().wasWirkung.set(key.equals(KEY_WIRKUNG));
    this.getFlags().wasPreis.set(key.equals(KEY_PREIS));
    this.getFlags().wasRezepte.set(key.equals(KEY_REZEPTE));
    this.getFlags().wasAlltagsarzneienUndVolksbrauchtum.set(key.equals(KEY_ALTAGSARZNEIEN_UND_VOLKSBRAUCHTUM));
    this.getFlags().wasHaltbarkeit.set(key.equals(KEY_HALTBARKEIT));
    this.getFlags().wasWissenstalent.set(key.contains(KEY_WAS_WEISS_MEIN_HELD_ÜBER));
  }

  @Override
  protected void applyDataValue(HerbRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    if (currentDataObject != null)
    {
      if (this.getFlags().wasName.get()) currentDataObject.name = concatForDataValueWithMarkup(currentDataObject.name, cleanText, isBold, isItalic);
      if (this.getFlags().wasAlternativeNamen.get()) currentDataObject.alternativeNamen = concatForDataValueWithMarkup(currentDataObject.alternativeNamen, cleanText, isBold, isItalic);
      if (this.getFlags().wasVerbreitung.get()) currentDataObject.verbreitung = concatForDataValue(currentDataObject.verbreitung, cleanText);
      if (this.getFlags().wasSuchschwierigkeit.get()) currentDataObject.suchschwierigkeit = concatForDataValueWithMarkup(currentDataObject.suchschwierigkeit, cleanText, isBold, isItalic);
      if (this.getFlags().wasBestimmungsschwierigkeit.get()) currentDataObject.bestimmungsschwierigkeit = concatForDataValueWithMarkup(currentDataObject.bestimmungsschwierigkeit, cleanText, isBold, isItalic);
      if (this.getFlags().wasAnwendungen.get()) currentDataObject.anwendungen = concatForDataValueWithMarkup(currentDataObject.anwendungen, cleanText, isBold, isItalic);
      if (this.getFlags().wasWirkung.get()) currentDataObject.wirkung = concatForDataValueWithMarkup(currentDataObject.wirkung, cleanText, isBold, isItalic);
      if (this.getFlags().wasPreis.get()) currentDataObject.preis = concatForDataValueWithMarkup(currentDataObject.preis, cleanText, isBold, isItalic);
      if (this.getFlags().wasRezepte.get()) currentDataObject.rezepte = concatForDataValueWithMarkup(currentDataObject.rezepte, cleanText, isBold, isItalic);
      if (this.getFlags().wasAlltagsarzneienUndVolksbrauchtum.get()) currentDataObject.alltagsarzneienUndVolksbrauchtum = concatForDataValueWithMarkup(currentDataObject.alltagsarzneienUndVolksbrauchtum, cleanText, isBold, isItalic);
      if (this.getFlags().wasHaltbarkeit.get()) currentDataObject.haltbarkeit = concatForDataValue(currentDataObject.haltbarkeit, cleanText);
      if (this.getFlags().wasWissenstalent.get() || cleanText.startsWith("QS")) currentDataObject.wissensfertigkeit = concatForDataValueWithMarkup(currentDataObject.wissensfertigkeit, cleanText, isBold, isItalic);
    }
  }


  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return t.size <= conf.dataSize && t.isBold && t.text != null && !t.text.isEmpty();
  }

  @Override
  protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return t.size <= conf.dataSize && !this.validateIsDataKey(t, cleanText, conf);
  }

  @Override
  protected void concludePredecessor(HerbRaw lastEntry) {
  }
}
