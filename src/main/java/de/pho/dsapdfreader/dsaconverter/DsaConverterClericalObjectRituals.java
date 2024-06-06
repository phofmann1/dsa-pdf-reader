package de.pho.dsapdfreader.dsaconverter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.MysticalActivityObjectRitualRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsTraditionSkillMagic;
import de.pho.dsapdfreader.exporter.model.enums.ArtifactKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterClericalObjectRituals extends DsaConverter<MysticalActivityObjectRitualRaw, ConverterAtomicFlagsTraditionSkillMagic> {
  private static final List<String> KEYS_TRADITION = List.of(
      "Die Tradition der Praioskirche",
      "Die Tradition der Rondrakirche",
      "Die Tradition der Efferdkirche",
      "Die Tradition der Traviakirche",
      "Die Tradition der Boronkirche",
      "Die Tradition der Hesindekirche",
      "Die Tradition der Firunkirche",
      "Die Tradition der Tsakirche",
      "Die Tradition der Phexkirche",
      "Die Tradition der Perainekirche",
      "Die Tradition der Ingerimmkirche",
      "Die Tradition der Rahjakirche",
      "Die Tradition der Aveskirche",
      "Die Tradition der Ifirnkirche",
      "Die Tradition der Korkirche",
      "Die Tradition der Nanduskirche",
      "Die Tradition der Swafnirkirche",
      "Die Tradition des Levthankults",
      "Die Tradition des Marbokults",
      "Die Tradition des Numinorukults",
      "Die Tradition des Shinxirkults",
      "Die Tradition der Ferkinaschamanen",
      "Die Tradition der Fjarningerschamanen",
      "Die Tradition der Gjalskerschamanen",
      "Die Tradition der Nivesenschamanen",
      "Die Tradition der Tahayaschamanen",
      "Die Tradition der Trollzackerschamanen",
      "Die Tradition des Rastullahglaubens",
      "Die Tradition der Rurund-Gror-Kirche",
      "Die Tradition des Satuaria-Kultes",
      "Die Tradition der Sumu-Kulte",
      "Die Tradition der Angroschkirche",
      "Die Tradition der Zsahh-Priester",
      "Die Tradition der H’Szint-Priester",
      "Die Tradition der Chr’Ssir’Ssr-Priester",
      "Die Tradition der Achazschamanen",
      "Die Tradition des Brazoraghkultes",
      "Die Tradition des Tairachkultes",
      "Die Tradition des Graveshkultes",
      "Die Tradition des Rikaikultes",
      "Die Tradition des Namenlosen Kults"
  );

  private static final Map<String, MysticalSkillCategory> KEYS_MYSTICAL_SKILL_CATEGORY = Map.ofEntries(
      new AbstractMap.SimpleEntry<>("Animistenkräfte", MysticalSkillCategory.power),
      new AbstractMap.SimpleEntry<>("Bannzeichen", MysticalSkillCategory.bansign),
      new AbstractMap.SimpleEntry<>("Herrschaftsrituale", MysticalSkillCategory.ritualOfDominion),
      new AbstractMap.SimpleEntry<>("Elfenlieder", MysticalSkillCategory.elfensong),
      new AbstractMap.SimpleEntry<>("Geodenrituale", MysticalSkillCategory.geode),
      new AbstractMap.SimpleEntry<>("Goblinrituale", MysticalSkillCategory.goblinRitual),
      new AbstractMap.SimpleEntry<>("Hexenflüche", MysticalSkillCategory.curse),
      new AbstractMap.SimpleEntry<>("Zauberrunen", MysticalSkillCategory.rune),
      new AbstractMap.SimpleEntry<>("Schelmenstreiche", MysticalSkillCategory.jest),
      new AbstractMap.SimpleEntry<>("Zaubermelodien", MysticalSkillCategory.melody),
      new AbstractMap.SimpleEntry<>("Zaubertänze", MysticalSkillCategory.dance),
      new AbstractMap.SimpleEntry<>("Zibiljarituale", MysticalSkillCategory.zibilja),
      new AbstractMap.SimpleEntry<>("Vertrautentiere", MysticalSkillCategory.familiar),
      new AbstractMap.SimpleEntry<>("Zauberzeichen", MysticalSkillCategory.magicSign)
  );
  private static final Map<MysticalSkillCategory, String> KEYS_MYSTICAL_SKILL_CATEGORY_FRIST_ACTIVITY = Map.ofEntries(
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.power, "Blut trinkenProbe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.bansign, "Bannzeichen der Geisterurne Probe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.ritualOfDominion, "Ausbruch unterdrückter GefühleProbe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.elfensong, "ErinnerungsmelodieProbe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.geode, "Gestalt aus RauchProbe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.goblinRitual, "Aufmerksamer WächterProbe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.curse, "Ängste mehrenProbe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.rune, "Entgiftungsrune (Eidurbanruna)Probe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.jest, "AufgeblasenProbe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.melody, "Melodie der AngriffslustProbe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.dance, "Tanz der AngriffslustProbe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.zibilja, "Band zur WareProbe"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.familiar, "DiebstahlWirkung"),
      new AbstractMap.SimpleEntry<>(MysticalSkillCategory.magicSign, "Auge des Basilisken")
  );
  private static final Map<String, ArtifactKey> KEYS_TRADITION_ARTIFACTS = Map.ofEntries(
      new AbstractMap.SimpleEntry<>("Das Sonnenzepter", ArtifactKey.sonnenzepter),
      new AbstractMap.SimpleEntry<>("Die Schutzkugel", ArtifactKey.schutzkugel),
      new AbstractMap.SimpleEntry<>("Der Rondrakamm", ArtifactKey.rondrakamm),
      new AbstractMap.SimpleEntry<>("Die Schwertfibel", ArtifactKey.schwertfibel),
      new AbstractMap.SimpleEntry<>("Der Efferdbart", ArtifactKey.efferdbart),
      new AbstractMap.SimpleEntry<>("Die Muschelkette", ArtifactKey.muschelkette),
      new AbstractMap.SimpleEntry<>("Der Gänsebeutel", ArtifactKey.gänsebeutel),
      new AbstractMap.SimpleEntry<>("Das Amulett des Heiligen Badilak", ArtifactKey.amulett_des_heiligen_badilak),
      new AbstractMap.SimpleEntry<>("Der Rabenschnabel", ArtifactKey.rabenschnabel),
      new AbstractMap.SimpleEntry<>("Das Dunkle Buch", ArtifactKey.das_dunkle_buch),
      new AbstractMap.SimpleEntry<>("Das Buch der Schlange", ArtifactKey.buch_der_schlange),
      new AbstractMap.SimpleEntry<>("Der Erkenntnisstab", ArtifactKey.erkenntnisstab),
      new AbstractMap.SimpleEntry<>("Das Firunsmesser", ArtifactKey.firunsmesser),
      new AbstractMap.SimpleEntry<>("Der Firunsbogen", ArtifactKey.firunsbogen),
      new AbstractMap.SimpleEntry<>("Das Prisma", ArtifactKey.prisma),
      new AbstractMap.SimpleEntry<>("Das Bunte Gewand", ArtifactKey.das_bunte_gewand),
      new AbstractMap.SimpleEntry<>("Das Mondamulett", ArtifactKey.mondamulett),
      new AbstractMap.SimpleEntry<>("Der Graue Umhang", ArtifactKey.der_graue_umhang),
      new AbstractMap.SimpleEntry<>("Die Grünen Handschuhe", ArtifactKey.grüne_handschuhe),
      new AbstractMap.SimpleEntry<>("Der Saatgutbeutel", ArtifactKey.saatgutbeutel),
      new AbstractMap.SimpleEntry<>("Der Ingerimmshammer", ArtifactKey.ingerimmshammer),
      new AbstractMap.SimpleEntry<>("Die Laterne des ewigen Feuers", ArtifactKey.laterne_des_ewigen_feuers),
      new AbstractMap.SimpleEntry<>("Der Rote Schleier", ArtifactKey.roter_schleier),
      new AbstractMap.SimpleEntry<>("Der Schmuck der Schönen Göttin", ArtifactKey.schmuck_der_schönen_götting),
      new AbstractMap.SimpleEntry<>("Der Avesstab", ArtifactKey.avesstab),
      new AbstractMap.SimpleEntry<>("Die Silberflöte", ArtifactKey.silberflöte),
      new AbstractMap.SimpleEntry<>("Der Ifirnsmantel", ArtifactKey.ifirnsmantel),
      new AbstractMap.SimpleEntry<>("Der Ifirnsbogen", ArtifactKey.ifirnsbogen),
      new AbstractMap.SimpleEntry<>("Der Korspieß", ArtifactKey.korspieß),
      new AbstractMap.SimpleEntry<>("Die Mantikorkette", ArtifactKey.mantikorkette),
      new AbstractMap.SimpleEntry<>("Das Einhornstirnband", ArtifactKey.einhornstirnband),
      new AbstractMap.SimpleEntry<>("Das Trigon-Amulett", ArtifactKey.trigon_amulett),
      new AbstractMap.SimpleEntry<>("Der Walschild", ArtifactKey.walschild),
      new AbstractMap.SimpleEntry<>("Das Flukenamulett", ArtifactKey.flukenamulett),
      new AbstractMap.SimpleEntry<>("Die Widderkeule", ArtifactKey.widderkeule),
      new AbstractMap.SimpleEntry<>("Das Füllhorn", ArtifactKey.füllhorn),
      new AbstractMap.SimpleEntry<>("Der Marbodolch", ArtifactKey.marbodolch),
      new AbstractMap.SimpleEntry<>("Das Stundenglas", ArtifactKey.stundenglas),
      new AbstractMap.SimpleEntry<>("Das Muschelhorn", ArtifactKey.muschelhorn),
      new AbstractMap.SimpleEntry<>("Die Bimssteinkette", ArtifactKey.bimssteinkette),
      new AbstractMap.SimpleEntry<>("Die Vitis", ArtifactKey.vitis),
      new AbstractMap.SimpleEntry<>("Der Hornissenstachel", ArtifactKey.hornissenstachel),
      new AbstractMap.SimpleEntry<>("Die Ferkinaknochenkeule", ArtifactKey.ferkinaknochenkeule),
      new AbstractMap.SimpleEntry<>("Die Fjarningerknochenkeule", ArtifactKey.fjarningerknochenkeule),
      new AbstractMap.SimpleEntry<>("Die Gjalskerknochenkeule", ArtifactKey.gjalskerknochenkeule),
      new AbstractMap.SimpleEntry<>("Die Nivesenknochenkeule", ArtifactKey.nivesenknochenkeule),
      new AbstractMap.SimpleEntry<>("Die Tahayaknochenkeule", ArtifactKey.tahayaknochenkeule),
      new AbstractMap.SimpleEntry<>("Die Trollzackerknochenkeule", ArtifactKey.trollzackerknochenkeule),
      new AbstractMap.SimpleEntry<>("Geisterfetisch", ArtifactKey.geisterfetisch),
      new AbstractMap.SimpleEntry<>("Der Angroschanhänger", ArtifactKey.angroschanhänger),
      new AbstractMap.SimpleEntry<>("Der Angroschhammer", ArtifactKey.angroschhammer),
      new AbstractMap.SimpleEntry<>("Die Eidechsenkleidung", ArtifactKey.eidechsenkleidung),
      new AbstractMap.SimpleEntry<>("Der Regenbogenstein", ArtifactKey.regenbogenstein),
      new AbstractMap.SimpleEntry<>("Der Schlangenstab", ArtifactKey.schlangenstab),
      new AbstractMap.SimpleEntry<>("Das Schlangenlederarmband", ArtifactKey.schlangenlederarmband),
      new AbstractMap.SimpleEntry<>("Die Sturmschwingen", ArtifactKey.sturmschwinge),
      new AbstractMap.SimpleEntry<>("Das Coelestin-Metallband", ArtifactKey.coelestin_metallband),
      new AbstractMap.SimpleEntry<>("Die Achazknochenkeule", ArtifactKey.achazknochenkeule),
      new AbstractMap.SimpleEntry<>("Die Knochenkeule der Tairachschamanen", ArtifactKey.tairachknochenkeule),
      new AbstractMap.SimpleEntry<>("Der Graveshhammer", ArtifactKey.graveshhammer),
      new AbstractMap.SimpleEntry<>("Der kupferrote Schleifstein", ArtifactKey.kupferroter_schleifstein),
      new AbstractMap.SimpleEntry<>("Der Opferdolch des Namenlosen", ArtifactKey.opferdolch_des_namenlosen),
      new AbstractMap.SimpleEntry<>("Die Gesichtslarve", ArtifactKey.geischtslarve)
  );


  private static final String KEY_EFFECT = "Wirkung";
  private static final String KEY_FEATURE = "Aspekt";
  private static final String KEY_REQUIREMENTS_I = "Voraussetzungen";
  private static final String KEY_REQUIREMENTS_II = "Voraussetzung";
  private static final String KEY_AP = "AP-Wert";
  private static final Map<ArtifactKey, String> KEYS_TRADITION_ARTIFACTS_FIRST_SF = Map.ofEntries(
      new AbstractMap.SimpleEntry<>(ArtifactKey.animistenwaffe, "Bindung der WaffeWirkung")
  );

  protected static final String[] KEYS = {
      KEY_EFFECT,
      KEY_FEATURE,
      KEY_REQUIREMENTS_I,
      KEY_REQUIREMENTS_II,
      KEY_AP,
  };
  private static final Logger LOGGER = LogManager.getLogger();

  ConverterAtomicFlagsTraditionSkillMagic flags;

  AtomicReference<ArtifactKey> artifactKey = new AtomicReference<>();
  AtomicReference<Boolean> isArtifactStarted = new AtomicReference<>(Boolean.FALSE);

  public List<MysticalActivityObjectRitualRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf) {
    List<MysticalActivityObjectRitualRaw> returnValue = new ArrayList<>();
    String msg = String.format("parse  result to %s", getClassName());
    LOGGER.debug(msg);

    resultList
        .forEach(t -> {

          String cleanText = t.text.replaceAll("\u00AD", "-")
              .trim();

          boolean isNewArtifact = !cleanText.contains(" Tradition ") && (t.size == 1800);
          // validate the flags for conf

          // Start new Tradition
          if (t.size == 1800 && cleanText.contains("Tradition")) {
            isArtifactStarted.set(Boolean.FALSE);
            //System.out.println("\"" + cleanText + "\",");
          }

          boolean isFirstValue = validateIsFirstValue(t, conf, isArtifactStarted.get(), cleanText);
          boolean isFirstValueSkipped = isFirstValue && isNumeric(t.text); // gets skipped, when the firstValue is a number (Page Number in some documents)
          boolean isDataKey = validateIsDataKey(t, cleanText, conf);
          boolean isDataValue = validateIsDataValue(t, cleanText, conf);
          handleWasNoKeyStrings(getFlags(), t); // used in MysticalSkill for QS flags, they act differently, because they are also part of the effect

          if (isNewArtifact) {
            finishPredecessorAndStartNew(isFirstValue, isFirstValueSkipped, returnValue, conf, cleanText);
            artifactKey.set(extractArtifactKey(t.text));
            isArtifactStarted.set(Boolean.TRUE);
            this.getFlags().initDataFlags();
          }


          if (isArtifactStarted.get()) {
            // handle name
            if (isFirstValue) {
              MysticalActivityObjectRitualRaw newOr = new MysticalActivityObjectRitualRaw();
              newOr.name = cleanText;
              newOr.artifactKey = artifactKey.get();
              newOr.publication = conf.publication;
              returnValue.add(newOr);
            }

            // handle keys
            if (isDataKey) {
              applyFlagsForKey(t.text);
              applyFlagsForNoKeyStrings(getFlags(), t.text);
            }

            // handle values
            if (isDataValue) {
              applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
              applySpecialAbilitiesFlagsForNoKeyStrings(getFlags(), t);
            }
            getFlags().getFirstFlag().set(isFirstValue && !isFirstValueSkipped && !isArtifactStarted.get());
          }
        });
    concludePredecessor(last(returnValue)); //finish the last entry in list
    return returnValue;
  }

  private void applySpecialAbilitiesFlagsForNoKeyStrings(ConverterAtomicFlagsTraditionSkillMagic flags, TextWithMetaInfo t) {
    if (flags.wasName.get() && !t.isBold) {
      flags.wasName.set(false);
    }
  }

  private MysticalSkillCategory extractTraditionSkillCategoryKey(String text) {
    return KEYS_MYSTICAL_SKILL_CATEGORY.get(text);
  }

  private ArtifactKey extractArtifactKey(String text) {
    return KEYS_TRADITION_ARTIFACTS.get(text);
  }


  @Override
  protected String[] getKeys() {
    return KEYS;
  }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return Arrays.stream(this.getKeys()).anyMatch(k -> k.equalsIgnoreCase(t.text.replace(":", "")));
  }

  @Override
  protected ConverterAtomicFlagsTraditionSkillMagic getFlags() {
    if (flags == null) {
      this.flags = new ConverterAtomicFlagsTraditionSkillMagic();
    }
    return flags;
  }

  @Override
  protected String getClassName() {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<MysticalActivityObjectRitualRaw> returnValue, TopicConfiguration conf, String cleanText) {
    if (!this.getFlags().getFirstFlag().get()) {
      MysticalActivityObjectRitualRaw newEntry = new MysticalActivityObjectRitualRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      newEntry.setName(cleanText.replace("Wirkung", "").replace("Probe", ""));
      newEntry.artifactKey = artifactKey.get();
      returnValue.add(newEntry);
    }

    if (isArtifactStarted.get()) {
      this.getFlags().wasEffect.set(true);
      this.getFlags().wasName.set(false);
    }

  }

  @Override
  protected void applyFlagsForKey(String key) {
    this.getFlags().wasName.set(false);
    this.getFlags().wasEffect.set(key.trim().equals(KEY_EFFECT));
    this.getFlags().wasFeature.set(key.trim().equals(KEY_FEATURE));
    this.getFlags().wasRequirements.set(key.trim().equals(KEY_REQUIREMENTS_I) || key.trim().equals(KEY_REQUIREMENTS_II));
    this.getFlags().wasAp.set(key.replace(":", "").trim().equals(KEY_AP));

  }

  @Override
  protected void applyDataValue(MysticalActivityObjectRitualRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic) {
    if (currentDataObject != null) {
      if (this.getFlags().wasName.get())
        currentDataObject.name = concatForDataValue(currentDataObject.name, cleanText.replaceAll("[A-Z]{2}\\/[A-Z]{2}\\/[A-Z]{2}", ""));

      if (flags.wasRange.get()) currentDataObject.range = concatForDataValue(currentDataObject.range, cleanText);
      if (flags.wasDuration.get()) currentDataObject.duration = concatForDataValue(currentDataObject.duration, cleanText);
      if (flags.wasFeature.get()) currentDataObject.feature = concatForDataValue(currentDataObject.feature, cleanText);
      if (flags.wasTargetCategory.get()) currentDataObject.targetCategory = concatForDataValue(currentDataObject.targetCategory, cleanText);

      if (flags.wasCheck.get()) currentDataObject.check = concatForDataValue(currentDataObject.check, cleanText).replace(":", "").trim();
      if (flags.wasEffect.get()) currentDataObject.effect = concatForDataValueWithMarkup(currentDataObject.effect, cleanText, isBold, isItalic);
      if (flags.wasCastingDuration.get()) currentDataObject.castingDuration = concatForDataValue(currentDataObject.castingDuration, cleanText);
      if (flags.wasCost.get()) currentDataObject.cost = concatForDataValue(currentDataObject.cost, cleanText);

      if (flags.wasAdvancementCategory.get()) {
        currentDataObject.advancementCategory = concatForDataValue(currentDataObject.advancementCategory, cleanText);
        if (!cleanText.endsWith(",") && !cleanText.isEmpty()) flags.wasAdvancementCategory.set(false);
      }

      if (flags.wasTribes.get()) currentDataObject.tribes = concatForDataValue(currentDataObject.tribes, cleanText);
      if (flags.wasMusicTraditions.get()) currentDataObject.musicTraditions = concatForDataValue(currentDataObject.musicTraditions, cleanText);
      if (flags.wasRequirements.get()) currentDataObject.requirements = concatForDataValue(currentDataObject.requirements, cleanText);
      if (flags.wasVolume.get()) currentDataObject.volume = concatForDataValue(currentDataObject.volume, cleanText);
      if (flags.wasBinding.get()) currentDataObject.binding = concatForDataValue(currentDataObject.binding, cleanText);
      if (flags.wasAp.get()) currentDataObject.ap = concatForDataValue(currentDataObject.ap, cleanText);
      if (flags.wasSkill.get()) currentDataObject.talentKey = concatForDataValue(currentDataObject.talentKey, cleanText);
      if (flags.wasCircleOfProtection.get())
        currentDataObject.circleOfProtection = concatForDataValue(currentDataObject.circleOfProtection, cleanText);
      if (flags.wasCircleOfBanishment.get())
        currentDataObject.circleOfBanishment = concatForDataValue(currentDataObject.circleOfBanishment, cleanText);
      if (flags.wasGebräu.get()) currentDataObject.gebraeu = concatForDataValue(currentDataObject.gebraeu, cleanText);

    }
  }

  @Override
  protected void concludePredecessor(MysticalActivityObjectRitualRaw lastEntry) {
  }


  public boolean validateIsFirstValue(TextWithMetaInfo t, TopicConfiguration conf, Boolean isArtifactStarted, String cleanText) {
    return super.validateIsFirstValue(t, conf)
        && isArtifactStarted
        && Arrays.stream(KEYS).noneMatch(k -> k.equals(cleanText)) && !cleanText.startsWith("QS");
  }
}
