package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.AlchimieRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsAlchimie;
import de.pho.dsapdfreader.exporter.model.enums.AlchimieTypeKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterAlchimie extends DsaConverter<AlchimieRaw, ConverterAtomicFlagsAlchimie> {

    private static final String KEY_STUFE = "Stufe";
    private static final String KEY_ART = "Art";
    private static final String KEY_WIDERSTAND = "Widerstand";
    private static final String KEY_WIRKUNG = "Wirkung";
    private static final String KEY_WIRKUNG_2 = "Wirkung*";
    private static final String KEY_BEGINN = "Beginn";
    private static final String KEY_DAUER = "Dauer";
  private static final String KEY_WERT_PREIS = "Wert/Preis";
  private static final String KEY_WERT_PREIS_2 = "Kosten";
  private static final String KEY_ALTERNATIVE_NAMEN = "Alternative Namen";
    private static final String KEY_ALTERNATIVE_NAMEN_2 = "Alternativer Name";
  private static final String KEY_ALTERNATIVE_NAMEN_3 = "Alternative Name";
  private static final String KEY_ALTERNATIVE_NAMEN_4 = "Alternativnamen";
  private static final String KEY_TYPISCHE_INGREDIENZIEN = "Typische Ingredienzien";
  private static final String KEY_ZUTATEN = "Zutaten";
  private static final String KEY_KOSTEN = "Kosten der Ingredienzienstufe";
  private static final String KEY_KOSTEN_2 = "Kosten der Ingredienzienstufen";
  private static final String KEY_LABOR = "Labor";
  private static final String KEY_BRAUSCHWIERIGKEIT = "Brauschwierigkeit";
  private static final String KEY_VERARBEITUNGSSCHWIERIGKEIT = "Verarbeitungsschwierigkeit";
  private static final String KEY_VORAUSSETZUNGEN = "Voraussetzungen (Brauvorgang)";
  private static final String KEY_TYPISCHE_HILFSMITTEL = "Typische Hilfsmittel";
  private static final String KEY_AP_WERT = "AP-Wert (Berufsgeheimnis)";
  private static final String KEY_AP_WERT_2 = "AP-Wert (Berufsgeheimnis, Geheimwissen)";
  private static final String KEY_QUALITAETSSTUFEN = "Qualitätsstufen";
    private static final String KEY_QUALITAETSSTUFEN_2 = "Qualitätsstufen1";
    private static final String KEY_HYPERPOTENTE_WIRKUNG = "Hyperpotente Wirkung";
    private static final String KEY_EINNAHME = "Einnahme";
    private static final String KEY_NEBENWIRKUNG = "Nebenwirkung";
    private static final String KEY_LEGALITAET = "Legalität";
    private static final String KEY_SUCHT = "Sucht";
    private static final String KEY_BSEONDERHEITEN = "Besonderheiten";
    private static final String KEY_BESONDERHEITEN_2 = "Besonderheit";
    private static final String KEY_UEBERDOSIERUNG = "Überdosierung";

    protected static final String[] KEYS = {
        KEY_ALTERNATIVE_NAMEN,
        KEY_ALTERNATIVE_NAMEN_2,
        KEY_ALTERNATIVE_NAMEN_3,
        KEY_ALTERNATIVE_NAMEN_4,

        KEY_STUFE,
        KEY_ART,
        KEY_WIDERSTAND,
        KEY_EINNAHME,
        KEY_WIRKUNG,
        KEY_WIRKUNG_2,
        KEY_NEBENWIRKUNG,
        KEY_BEGINN,
        KEY_DAUER,
        KEY_LEGALITAET,
        KEY_WERT_PREIS,
        KEY_WERT_PREIS_2,
        KEY_SUCHT,

        KEY_TYPISCHE_INGREDIENZIEN,
        KEY_ZUTATEN,
        KEY_KOSTEN,
        KEY_KOSTEN_2,
        KEY_LABOR,
        KEY_BRAUSCHWIERIGKEIT,
        KEY_VERARBEITUNGSSCHWIERIGKEIT,
        KEY_VORAUSSETZUNGEN,
        KEY_TYPISCHE_HILFSMITTEL,
        KEY_AP_WERT,
        KEY_AP_WERT_2,
        KEY_QUALITAETSSTUFEN,
        KEY_QUALITAETSSTUFEN_2,
        KEY_HYPERPOTENTE_WIRKUNG,
        KEY_BSEONDERHEITEN,
        KEY_BESONDERHEITEN_2,
        KEY_UEBERDOSIERUNG
    };

    private ConverterAtomicFlagsAlchimie flags;

    @Override
    protected String[] getKeys() {
        return KEYS;
    }

    @Override
    protected ConverterAtomicFlagsAlchimie getFlags() {
        if (flags == null) {
            this.flags = new ConverterAtomicFlagsAlchimie();
        }
        return flags;
    }

    @Override
    public List<AlchimieRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> texts, TopicConfiguration conf) {
        List<AlchimieRaw> returnValue = new ArrayList<>();


        texts.forEach(t -> {


            String cleanText = t.text.trim();

            boolean isDataKey = validateIsDataKey(t, cleanText, conf);
            boolean isDataValue = validateIsDataValue(t, cleanText, conf);

            //handle start of Profession
            if (this.validateIsName(t, cleanText, conf)) {
                this.finishPredecessorAndStartNew(true, false, returnValue,conf, cleanText);
            } else {
                // handle keys
                if (isDataKey) {
                    applyFlagsForKey(t.text);
                }

                // handle values
                if (isDataValue) {
                    applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
                    applyFlagsForNoKeyStrings(this.getFlags(), t.text);
                }
            }
        });
        this.concludePredecessor(last(returnValue));
        return returnValue;
    }

    private static final Pattern ONLY_NUMBER_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern INVALID_PREFIX_PATTERN =
            Pattern.compile("^(Seite|Regelwerk|,)\\b.*", Pattern.CASE_INSENSITIVE);

    private boolean validateIsName(TextWithMetaInfo t,
                                   String cleanText,
                                   TopicConfiguration conf) {

        if (t.size != conf.nameSize) return false;
        if (!t.isBold) return false;
        if (this.validateIsDataKey(t, cleanText, conf)) return false;

        // ❌ reine Zahl
        if (ONLY_NUMBER_PATTERN.matcher(cleanText).matches()) return false;

        // ❌ beginnt mit "Seite" oder "Regelwerk"
        if (INVALID_PREFIX_PATTERN.matcher(cleanText).matches()) return false;

        return !cleanText.isEmpty();
    }

    @Override
    protected String getClassName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    protected void handleFirstValue(List<AlchimieRaw> returnValue, TopicConfiguration conf, String cleanText) {


        if (!this.getFlags().getFirstFlag().get()) {
            AlchimieRaw newEntry = new AlchimieRaw();
            newEntry.topic = conf.topic;
            newEntry.publication = conf.publication;
            newEntry.name = cleanText;
            this.getFlags().initDataFlags();
            this.getFlags().wasDescription.set(Boolean.TRUE);
            returnValue.add(newEntry);
        }
    }

    @Override
    protected void applyFlagsForKey(String key) {

      this.getFlags().initDataFlags();

      this.getFlags().wasStufe.set(key.equals(KEY_STUFE));
      this.getFlags().wasArt.set(key.equals(KEY_ART));
      this.getFlags().wasWiderstand.set(key.equals(KEY_WIDERSTAND));
      this.getFlags().wasWirkung.set(key.equals(KEY_WIRKUNG) || key.equals(KEY_WIRKUNG_2));
      this.getFlags().wasBeginn.set(key.equals(KEY_BEGINN));
      this.getFlags().wasDauer.set(key.equals(KEY_DAUER));
      this.getFlags().wasWertPreis.set(key.equals(KEY_WERT_PREIS) || key.equals(KEY_WERT_PREIS));
      this.getFlags().wasTypicalIngredients.set(key.equals(KEY_TYPISCHE_INGREDIENZIEN));
      this.getFlags().wasZutaten.set(key.equals(KEY_ZUTATEN));
      this.getFlags().wasCost.set(key.equals(KEY_KOSTEN) || key.equals(KEY_KOSTEN_2));
      this.getFlags().wasLabor.set(key.equals(KEY_LABOR));
      this.getFlags().wasBrewingDifficulty.set(key.equals(KEY_BRAUSCHWIERIGKEIT) || key.equals(KEY_VERARBEITUNGSSCHWIERIGKEIT));
      this.getFlags().wasRequirements.set(key.equals(KEY_VORAUSSETZUNGEN) || key.equals(KEY_TYPISCHE_HILFSMITTEL));
      this.getFlags().wasApValue.set(key.equals(KEY_AP_WERT) || key.equals(KEY_AP_WERT_2));
      this.getFlags().isSecret.set(key.equals(KEY_AP_WERT_2));
      this.getFlags().wasQualityLevels.set(key.equals(KEY_QUALITAETSSTUFEN) || key.equals(KEY_QUALITAETSSTUFEN_2));
      this.getFlags().wasAlternativeNamen.set(key.equals(KEY_ALTERNATIVE_NAMEN) || key.equals(KEY_ALTERNATIVE_NAMEN_2) || key.equals(KEY_ALTERNATIVE_NAMEN_3) || key.equals(KEY_ALTERNATIVE_NAMEN_4));
      this.getFlags().wasEinnahme.set(key.equals(KEY_EINNAHME));
      this.getFlags().wasNebenwirkung.set(key.equals(KEY_NEBENWIRKUNG));
      this.getFlags().wasLegalitaet.set(key.equals(KEY_LEGALITAET));
      this.getFlags().wasSucht.set(key.equals(KEY_SUCHT));
      this.getFlags().wasUeberdosierung.set(key.equals(KEY_UEBERDOSIERUNG));
      this.getFlags().wasBesonderheiten.set(key.equals(KEY_BSEONDERHEITEN) || key.equals(KEY_BESONDERHEITEN_2));
      this.getFlags().wasHyperpotenteWirkung.set(key.equals(KEY_HYPERPOTENTE_WIRKUNG));
    }

    @Override
    protected void applyDataValue(AlchimieRaw currentDataObject,
                                  String cleanText,
                                  boolean isBold,
                                  boolean isItalic) {
      if (currentDataObject == null) return;

      if (this.getFlags().wasName.get())
        currentDataObject.name =
            concatForDataValueWithMarkup(currentDataObject.name, cleanText, isBold, isItalic);

      if (this.getFlags().wasTypicalIngredients.get())
        currentDataObject.typicalIngredients =
            concatForDataValueWithMarkup(currentDataObject.typicalIngredients, cleanText, isBold, isItalic);

      if (this.getFlags().wasZutaten.get())
        currentDataObject.zutaten =
            concatForDataValueWithMarkup(currentDataObject.zutaten, cleanText, isBold, isItalic);

      if (this.getFlags().wasCost.get())
        currentDataObject.cost =
            concatForDataValueWithMarkup(currentDataObject.cost, cleanText, isBold, isItalic);

      if (this.getFlags().wasLabor.get())
        currentDataObject.labor =
            concatForDataValueWithMarkup(currentDataObject.labor, cleanText, isBold, isItalic);

      if (this.getFlags().wasBrewingDifficulty.get())
            currentDataObject.brewingDifficulty =
                concatForDataValueWithMarkup(currentDataObject.brewingDifficulty, cleanText, isBold, isItalic);

      if (this.getFlags().wasRequirements.get())
        currentDataObject.requirements =
            concatForDataValue(currentDataObject.requirements, cleanText);

      if (this.getFlags().wasApValue.get())
        currentDataObject.apValue =
            concatForDataValueWithMarkup(currentDataObject.apValue, cleanText, isBold, isItalic);

      if (this.getFlags().isSecret.get()) {
        currentDataObject.isGeheimwissen = true;
      }

      if (this.getFlags().wasQualityLevels.get() || cleanText.matches("\\d+:.*"))
        currentDataObject.qualityLevels =
            concatForDataValueWithMarkup(currentDataObject.qualityLevels, cleanText, isBold, isItalic);

      if (this.getFlags().wasStufe.get())
        currentDataObject.stufe =
            concatForDataValueWithMarkup(currentDataObject.stufe, cleanText, isBold, isItalic);

      if (this.getFlags().wasArt.get())
            currentDataObject.art =
                    concatForDataValueWithMarkup(currentDataObject.art, cleanText, isBold, isItalic);

        if (this.getFlags().wasWiderstand.get())
            currentDataObject.widerstand =
                    concatForDataValueWithMarkup(currentDataObject.widerstand, cleanText, isBold, isItalic);

        if (this.getFlags().wasWirkung.get())
            currentDataObject.wirkungKurz =
                    concatForDataValueWithMarkup(currentDataObject.wirkungKurz, cleanText, isBold, isItalic);

        if (this.getFlags().wasBeginn.get())
            currentDataObject.beginn =
                    concatForDataValueWithMarkup(currentDataObject.beginn, cleanText, isBold, isItalic);

        if (this.getFlags().wasDauer.get())
            currentDataObject.dauer =
                    concatForDataValueWithMarkup(currentDataObject.dauer, cleanText, isBold, isItalic);

        if (this.getFlags().wasWertPreis.get())
            currentDataObject.wertPreis =
                    concatForDataValueWithMarkup(currentDataObject.wertPreis, cleanText, isBold, isItalic);
        if (this.getFlags().wasAlternativeNamen.get()) {
            String[] split = cleanText.split("(?<=[a-zäöüß])(?=[A-ZÄÖÜ])");
            currentDataObject.alternativeNamen =
                    concatForDataValueWithMarkup(currentDataObject.alternativeNamen, split[0], isBold, isItalic);
            if (split.length > 1) {
                currentDataObject.description = concatForDataValueWithMarkup(currentDataObject.description, split[1], isBold, isItalic);
                this.getFlags().wasAlternativeNamen.set(false);
                this.getFlags().wasDescription.set(true);
            }
        }

        if (this.getFlags().wasEinnahme.get())
            currentDataObject.einnahme =
                    concatForDataValueWithMarkup(currentDataObject.einnahme, cleanText, isBold, isItalic);

        if (this.getFlags().wasNebenwirkung.get())
            currentDataObject.nebenwirkung =
                    concatForDataValueWithMarkup(currentDataObject.nebenwirkung, cleanText, isBold, isItalic);

        if (this.getFlags().wasLegalitaet.get())
            currentDataObject.legalitaet =
                    concatForDataValueWithMarkup(currentDataObject.legalitaet, cleanText, isBold, isItalic);

        if (this.getFlags().wasSucht.get())
            currentDataObject.sucht =
                    concatForDataValueWithMarkup(currentDataObject.sucht, cleanText, isBold, isItalic);
        if (this.getFlags().wasBesonderheiten.get())
            currentDataObject.besonderheiten =
                    concatForDataValueWithMarkup(currentDataObject.besonderheiten, cleanText, isBold, isItalic);
        if (this.getFlags().wasHyperpotenteWirkung.get())
            currentDataObject.hyperpotenteWirkung =
                    concatForDataValueWithMarkup(currentDataObject.hyperpotenteWirkung, cleanText, isBold, isItalic);
        if (this.getFlags().wasUeberdosierung.get())
            currentDataObject.ueberdosierung =
                    concatForDataValueWithMarkup(currentDataObject.ueberdosierung, cleanText, isBold, isItalic);
        // Alles was kein Key ist und nicht Name → Beschreibung
        if (!isBold && !anyKeyActive())
            currentDataObject.description =
                    concatForDataValueWithMarkup(currentDataObject.description, cleanText, isBold, isItalic);

    }

    private boolean anyKeyActive() {
        ConverterAtomicFlagsAlchimie f = this.getFlags();
        return f.wasName.get()
                || f.wasDescription.get()
                || f.wasTypicalIngredients.get()
                || f.wasCost.get()
                || f.wasLabor.get()
                || f.wasBrewingDifficulty.get()
                || f.wasRequirements.get()
                || f.wasApValue.get()
                || f.wasQualityLevels.get()
                || f.wasStufe.get()
                || f.wasArt.get()
                || f.wasWiderstand.get()
                || f.wasWirkung.get()
                || f.wasBeginn.get()
                || f.wasDauer.get()
                || f.wasWertPreis.get()
                || f.wasAlternativeNamen.get()
                || f.wasEinnahme.get()
                || f.wasNebenwirkung.get()
                || f.wasLegalitaet.get()
                || f.wasSucht.get()
                || f.wasUeberdosierung.get()
                || f.wasBesonderheiten.get()
                || f.wasHyperpotenteWirkung.get();
    }


    @Override
    protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
        return t.size <= conf.dataSize && t.isBold && t.text != null && !t.text.isEmpty() && Arrays.asList(this.getKeys()).contains(cleanText);
    }

    @Override
    protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
        return t.size <= conf.dataSize && !this.validateIsDataKey(t, cleanText, conf);
    }

    @Override
    protected void concludePredecessor(AlchimieRaw lastEntry) {
        if(lastEntry != null) {
            if (lastEntry.sucht != null) {
                lastEntry.type = AlchimieTypeKey.droge;
            } else if (lastEntry.stufe != null) {
              lastEntry.type = AlchimieTypeKey.gift;
            }
            else if (lastEntry.zutaten == null) {
              lastEntry.type = AlchimieTypeKey.elixier;
            }
            else {
              lastEntry.type = AlchimieTypeKey.hilfsmittel;
            }
        }
    }
}
