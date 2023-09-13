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

public class DsaConverterMysticalSkillActivityAndArtifacts extends DsaConverter<MysticalActivityObjectRitualRaw, ConverterAtomicFlagsTraditionSkillMagic>
{
  private static final List<String> KEYS_TRADITION = List.of(
      "Die Animisten",
      "Der Bannzeichner",
      "Borbaradianer",
      "Die Darna",
      "Die Druiden",
      "Die Elfen",
      "Die Geoden",
      "Gildenmagier",
      "Die Goblinzauberinnen",
      "Die Hexen",
      "Der Kristallomant",
      "Magiedilettanten",
      "Runenschöpfer",
      "Scharlatane",
      "Schelme",
      "Die Zauberalchimisten",
      "Zauberbarden",
      "Zaubertänzer",
      "Die Zibilja"
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
      new AbstractMap.SimpleEntry<>("Die Animistenwaffe", ArtifactKey.animistenwaffe),
      new AbstractMap.SimpleEntry<>("Die Krallenkette", ArtifactKey.krallenkette),
      new AbstractMap.SimpleEntry<>("Der Druidendolch", ArtifactKey.druidendolch),
      new AbstractMap.SimpleEntry<>("Die Druidensichel", ArtifactKey.druidensichel),
      new AbstractMap.SimpleEntry<>("Der Lebensring", ArtifactKey.lebensring),
      new AbstractMap.SimpleEntry<>("Der Magierstab", ArtifactKey.magierstab),
      new AbstractMap.SimpleEntry<>("Das Bannschwert", ArtifactKey.bannschwert),
      new AbstractMap.SimpleEntry<>("Die gildenmagische Magierkugel", ArtifactKey.gildenmagische_magierkugel),
      new AbstractMap.SimpleEntry<>("Die Zauberschale", ArtifactKey.zauberschale),
      new AbstractMap.SimpleEntry<>("Die Goblinkeule", ArtifactKey.goblinkeule),
      new AbstractMap.SimpleEntry<>("Die Schweinetrommel", ArtifactKey.schweinetrommel),
      new AbstractMap.SimpleEntry<>("Die Hauerkette", ArtifactKey.hauerkette),
      new AbstractMap.SimpleEntry<>("Der Hexenkessel", ArtifactKey.hexenkessel),
      new AbstractMap.SimpleEntry<>("Der Schuppenbeutel", ArtifactKey.schuppenbeutel),
      new AbstractMap.SimpleEntry<>("Die kristallomantische Kristallkugel", ArtifactKey.kristallomantische_kristallkugel),//
      new AbstractMap.SimpleEntry<>("Die Echsenhaube", ArtifactKey.echsenhaube),//
      new AbstractMap.SimpleEntry<>("Die Scharlatanische Zauberkugel", ArtifactKey.scharlatanische_zauberkugel),
      new AbstractMap.SimpleEntry<>("Der Zauberstecken", ArtifactKey.zauberstecken),
      new AbstractMap.SimpleEntry<>("Die Narrenkappe", ArtifactKey.narrenkappe),
      new AbstractMap.SimpleEntry<>("Das Schelmenspielzeug", ArtifactKey.schelmenspielzeug),
      new AbstractMap.SimpleEntry<>("Die Alchimistenschale", ArtifactKey.alchimistenschale),
      new AbstractMap.SimpleEntry<>("Zauberinstrumente", ArtifactKey.zauberinstrument),
      new AbstractMap.SimpleEntry<>("Das Trinkhorn", ArtifactKey.trinkhorn),
      new AbstractMap.SimpleEntry<>("Zauberkleidung", ArtifactKey.zauberkleidung),
      new AbstractMap.SimpleEntry<>("Sippenchronik", ArtifactKey.sippenchronik)
  );

  private static final Map<ArtifactKey, String> KEYS_TRADITION_ARTIFACTS_FIRST_SF = Map.ofEntries(
      new AbstractMap.SimpleEntry<>(ArtifactKey.animistenwaffe, "Bindung der WaffeWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.krallenkette, "Bindung der KrallenketteWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.druidendolch, "Bindung des DolchesWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.druidensichel, "BannsichelWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.lebensring, "Bindung des RingsWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.magierstab, "AstralentzugWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.bannschwert, "Bannschwert des AdeptenWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.gildenmagische_magierkugel, "AufbewahrungWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.zauberschale, "Allegorische AnalyseWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.goblinkeule, "Bindung der GoblinkeuleWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.schweinetrommel, "Bindung der SchweinetrommelWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.hauerkette, "Bindung der HauerketteWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.hexenkessel, "Angsteinflößendes GeblubberWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.schuppenbeutel, "Beutel-ApportWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.kristallomantische_kristallkugel, "BilderspielWirkung"),//
      new AbstractMap.SimpleEntry<>(ArtifactKey.echsenhaube, "Astralspeicher-Haube I-IIIWirkung"),//
      new AbstractMap.SimpleEntry<>(ArtifactKey.scharlatanische_zauberkugel, "AufnahmeWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.zauberstecken, "Ewige FlammeWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.narrenkappe, "Bindung der NarrenkappeWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.schelmenspielzeug, "BeleidigungenWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.alchimistenschale, "Alchimistenschalen-ApportWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.zauberinstrument, "Bann des ÜbernatürlichenWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.trinkhorn, "Bindung des TrinkhornsWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.zauberkleidung, "BesitzanspruchWirkung"),
      new AbstractMap.SimpleEntry<>(ArtifactKey.sippenchronik, "Bindung der ChronikWirkung")
  );


  private static final String KEY_CHECK = "Probe";
  private static final String KEY_EFFECT = "Wirkung";
  private static final String KEY_COST = "AsP-Kosten";
  private static final String KEY_DURATION_I = "Wirkungsdauer";
  private static final String KEY_DURATION_II = "Wirkungsdauer (langsam / schnell)";
  private static final String KEY_FEATURE = "Merkmal";
  private static final String KEY_TRIBES = "Stammestradition";
  private static final String KEY_MUSIC_TRADITIONS = "Musiktradition";
  private static final String KEY_ADVANCEMENT_CATEGORY = "Steigerungsfaktor";
  private static final String KEY_REQUIREMENTS_I = "Voraussetzungen";
  private static final String KEY_REQUIREMENTS_II = "Voraussetzung";

  private static final String KEY_VOLUME = "Volumen";
  private static final String KEY_BINDING = "Bindungskosten";
  private static final String KEY_AP = "AP-Wert";
  private static final String KEY_CASTING_DURATION_I = "Herstellungszeit (langsam / schnell)";
  private static final String KEY_CASTING_DURATION_II = "Ritualdauer";
  private static final String KEY_CASTING_DURATION_III = "Dauer";
  private static final String KEY_CASTING_DURATION_IV = "Zauberdauer";
  private static final String KEY_SKILL = "Talent";
  private static final String KEY_RANGE = "Reichweite";
  private static final String KEY_TARGET_CATEGORY = "Zielkategorie";

  private static final String KEY_CIRCLE_OF_BANISHMENT = "Bannkreis";
  private static final String KEY_CIRCLE_OF_PROTECTION = "Schutzkreis";

  private static final String KEY_GEBRÄU = "Gebräu";

  protected static final String[] KEYS = {
      KEY_CHECK,
      KEY_EFFECT,
      KEY_COST,
      KEY_DURATION_I,
      KEY_DURATION_II,
      KEY_FEATURE,
      KEY_TRIBES,
      KEY_MUSIC_TRADITIONS,
      KEY_ADVANCEMENT_CATEGORY,
      KEY_REQUIREMENTS_I,
      KEY_REQUIREMENTS_II,
      KEY_VOLUME,
      KEY_BINDING,
      KEY_AP,
      KEY_CASTING_DURATION_I,
      KEY_CASTING_DURATION_II,
      KEY_CASTING_DURATION_III,
      KEY_CASTING_DURATION_IV,
      KEY_SKILL,
      KEY_RANGE,
      KEY_TARGET_CATEGORY,
      KEY_CIRCLE_OF_BANISHMENT,
      KEY_CIRCLE_OF_PROTECTION,
      KEY_GEBRÄU
  };
  private static final Logger LOGGER = LogManager.getLogger();

  ConverterAtomicFlagsTraditionSkillMagic flags;

  AtomicReference<MysticalSkillCategory> mysticalSkillCategory = new AtomicReference<>();
  AtomicReference<ArtifactKey> artifactKey = new AtomicReference<>();
  AtomicReference<Boolean> isActivityStarted = new AtomicReference<>(Boolean.FALSE);
  AtomicReference<Boolean> isArtifactStarted = new AtomicReference<>(Boolean.FALSE);


  public List<MysticalActivityObjectRitualRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
  {
    List<MysticalActivityObjectRitualRaw> returnValue = new ArrayList<>();
    String msg = String.format("parse  result to %s", getClassName());
    LOGGER.debug(msg);

    resultList
        .forEach(t -> {

          String cleanText = t.text.replaceAll("\u00AD", "-")
              .trim();


          isActivityStarted.set(isActivityStarted.get()
              || mysticalSkillCategory.get() != null && cleanText.equals(KEYS_MYSTICAL_SKILL_CATEGORY_FRIST_ACTIVITY.get(mysticalSkillCategory.get()))
          );
          isArtifactStarted.set(isArtifactStarted.get()
              || artifactKey.get() != null && cleanText.equals(KEYS_TRADITION_ARTIFACTS_FIRST_SF.get(artifactKey.get()))
          );

          boolean isTopic = !KEYS_TRADITION.contains(cleanText) && (t.size == 1800 || (t.size == 1300 && (cleanText.equals("Die kristallomantische Kristallkugel") || cleanText.equals("Die Echsenhaube"))));
          // validate the flags for conf

          // Start new Tradition
          if (t.size == 1800 && (KEYS_TRADITION.contains(cleanText)
              || KEYS_TRADITION_ARTIFACTS.containsKey(cleanText)
              || KEYS_MYSTICAL_SKILL_CATEGORY.containsKey(cleanText)
          ))
          {
            isActivityStarted.set(false);
            isArtifactStarted.set(false);
          }

          boolean isFirstValue = validateIsFirstValue(t, conf, isActivityStarted.get(), isArtifactStarted.get(), cleanText, mysticalSkillCategory.get());
          boolean isFirstValueSkipped = isFirstValue && isNumeric(t.text); // gets skipped, when the firstValue is a number (Page Number in some documents)
          boolean isDataKey = validateIsDataKey(t, cleanText, conf);
          boolean isDataValue = validateIsDataValue(t, cleanText, conf);
          handleWasNoKeyStrings(getFlags(), t); // used in MysticalSkill for QS flags, they act differently, because they are also part of the effect

          if (cleanText.startsWith("Diebstahl"))
          {
            System.out.println(cleanText);
          }
          if (isTopic)
          {
            mysticalSkillCategory.set(null);
            artifactKey.set(null);
            mysticalSkillCategory.set(extractTraditionSkillCategoryKey(t.text));
            artifactKey.set(extractArtifactKey(t.text));
            isActivityStarted.set(Boolean.FALSE);
            isArtifactStarted.set(Boolean.FALSE);
            this.getFlags().initDataFlags();
          }


          if (isActivityStarted.get() || isArtifactStarted.get())
          {
            finishPredecessorAndStartNew(isFirstValue, isFirstValueSkipped, returnValue, conf, cleanText);
            if (isFirstValue)
            {
              cleanText = cleanText.replace("Probe", "").replace("Wirkung", "").replace(last(returnValue).name, "");
            }
            // handle keys
            if (isDataKey)
            {
              applyFlagsForKey(t.text);
              applyFlagsForNoKeyStrings(getFlags(), t.text);
            }

            // handle values
            if (isDataValue)
            {
              applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
              applySpecialAbilitiesFlagsForNoKeyStrings(getFlags(), t);
            }
            getFlags().getFirstFlag().set(isFirstValue && !isFirstValueSkipped && !isArtifactStarted.get() && !isActivityStarted.get());
          }
        });
    concludePredecessor(last(returnValue)); //finish the last entry in list
    return returnValue;
  }

  private void applySpecialAbilitiesFlagsForNoKeyStrings(ConverterAtomicFlagsTraditionSkillMagic flags, TextWithMetaInfo t)
  {
    if (flags.wasName.get() && !t.isBold)
    {
      flags.wasName.set(false);
    }
  }

  private MysticalSkillCategory extractTraditionSkillCategoryKey(String text)
  {
    return KEYS_MYSTICAL_SKILL_CATEGORY.get(text);
  }

  private ArtifactKey extractArtifactKey(String text)
  {
    return KEYS_TRADITION_ARTIFACTS.get(text);
  }


  @Override
  protected String[] getKeys()
  {
    return KEYS;
  }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return Arrays.stream(this.getKeys()).anyMatch(k -> k.equalsIgnoreCase(t.text.replace(":", "")));
  }

  @Override
  protected ConverterAtomicFlagsTraditionSkillMagic getFlags()
  {
    if (flags == null)
    {
      this.flags = new ConverterAtomicFlagsTraditionSkillMagic();
    }
    return flags;
  }

  @Override
  protected String getClassName()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<MysticalActivityObjectRitualRaw> returnValue, TopicConfiguration conf, String cleanText)
  {
    if (!this.getFlags().getFirstFlag().get())
    {
      MysticalActivityObjectRitualRaw newEntry = new MysticalActivityObjectRitualRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      newEntry.setName(cleanText.replace("Wirkung", "").replace("Probe", ""));
      newEntry.msCategory = mysticalSkillCategory.get();
      newEntry.artifactKey = artifactKey.get();
      if (MysticalSkillCategory.power == mysticalSkillCategory.get())
      {
        newEntry.castingDuration = "1 Aktion";
      }
      else if (MysticalSkillCategory.elfensong == mysticalSkillCategory.get())
      {
        newEntry.castingDuration = "5 Minuten";
      }
      else if (MysticalSkillCategory.curse == mysticalSkillCategory.get())
      {
        newEntry.castingDuration = "1 Aktion";
        newEntry.advancementCategory = "B";
      }
      else if (MysticalSkillCategory.ritualOfDominion == mysticalSkillCategory.get())
      {
        newEntry.castingDuration = "8 Stunden";
        newEntry.advancementCategory = "B";
      }
      else if (MysticalSkillCategory.geode == mysticalSkillCategory.get())
      {
        newEntry.advancementCategory = "B";
      }
      returnValue.add(newEntry);
    }

    if (isActivityStarted.get())
    {
      this.getFlags().wasCheck.set(true);
      this.getFlags().wasName.set(false);
    }
    else if (isArtifactStarted.get())
    {
      this.getFlags().wasEffect.set(true);
      this.getFlags().wasName.set(false);
    }

  }

  @Override
  protected void applyFlagsForKey(String key)
  {
    this.getFlags().wasName.set(false);
    this.getFlags().wasCheck.set(key.trim().equals(KEY_CHECK));
    this.getFlags().wasEffect.set(key.trim().equals(KEY_EFFECT));
    this.getFlags().wasCost.set(key.trim().equals(KEY_COST));
    this.getFlags().wasDuration.set(key.trim().equals(KEY_DURATION_I) || key.trim().equals(KEY_DURATION_II));
    this.getFlags().wasFeature.set(key.trim().equals(KEY_FEATURE));
    this.getFlags().wasTribes.set(key.trim().equals(KEY_TRIBES));
    this.getFlags().wasMusicTraditions.set(key.trim().equals(KEY_MUSIC_TRADITIONS));
    this.getFlags().wasAdvancementCategory.set(key.trim().equals(KEY_ADVANCEMENT_CATEGORY));
    this.getFlags().wasRequirements.set(key.trim().equals(KEY_REQUIREMENTS_I) || key.trim().equals(KEY_REQUIREMENTS_II));
    this.getFlags().wasVolume.set(key.trim().equals(KEY_VOLUME));
    this.getFlags().wasBinding.set(key.trim().equals(KEY_BINDING));
    this.getFlags().wasAp.set(key.replace(":", "").trim().equals(KEY_AP));
    this.getFlags().wasCastingDuration.set(key.trim().equals(KEY_CASTING_DURATION_I) || key.trim().equals(KEY_CASTING_DURATION_II) || key.trim().equals(KEY_CASTING_DURATION_III) || key.trim().equals(KEY_CASTING_DURATION_IV));
    this.getFlags().wasSkill.set(key.trim().equals(KEY_SKILL));
    this.getFlags().wasRange.set(key.trim().equals(KEY_RANGE));
    this.getFlags().wasTargetCategory.set(key.trim().equals(KEY_TARGET_CATEGORY));
    this.getFlags().wasCircleOfBanishment.set(key.trim().equals(KEY_CIRCLE_OF_BANISHMENT));
    this.getFlags().wasCircleOfProtection.set(key.trim().equals(KEY_CIRCLE_OF_PROTECTION));
    this.getFlags().wasGebräu.set(key.trim().equals(KEY_GEBRÄU));

  }

  @Override
  protected void applyDataValue(MysticalActivityObjectRitualRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    if (currentDataObject != null)
    {
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

      if (flags.wasAdvancementCategory.get())
      {
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
  protected void concludePredecessor(MysticalActivityObjectRitualRaw lastEntry)
  {
  }


  public boolean validateIsFirstValue(TextWithMetaInfo t, TopicConfiguration conf, boolean isActivityStarted, Boolean isArtifactStarted, String cleanText, MysticalSkillCategory mysticalSkillCategory)
  {
    return (super.validateIsFirstValue(t, conf)
        && t.isBold
        && (isActivityStarted && cleanText.endsWith("Probe")
        || isArtifactStarted && cleanText.endsWith("Wirkung")
        || cleanText.equals("Goblin-Pflanzenwuchs")
        || cleanText.equals("Lied des Windgeflüsters")
        || cleanText.equals("Lied der Pflanzen")
        || cleanText.equals("Goblin-Zuflucht"))
        || (mysticalSkillCategory == MysticalSkillCategory.magicSign && t.size == 1300)
    )
        && Arrays.stream(KEYS).noneMatch(k -> k.equals(cleanText)) && !cleanText.startsWith("QS");
  }
}
