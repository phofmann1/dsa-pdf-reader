package de.pho.dsapdfreader.dsaconverter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Triplet;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsMysticalSkill;
import de.pho.dsapdfreader.exporter.model.MysticalSkillVariant;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMysticalSkillGrimorium extends DsaConverter<MysticalSkillRaw, ConverterAtomicFlagsMysticalSkill>
{
  private static final Logger LOGGER = LogManager.getLogger();
  private static final int VARIANT_I_REQ = 8;
  private static final int VARIANT_II_REQ = 10;
  private static final int VARIANT_III_REQ = 12;
  private static final int VARIANT_IV_REQ = 14;
  private static final int VARIANT_V_REQ = 16;
  private static final String KEY_VARIANT_SPELL = "Zaubererweiterungen:";
  private static final String KEY_VARIANT_SPELL_2 = "Zaubererweiterungen";
  private static final String KEY_VARIANT_LITURGY = "Liturgieerweiterungen:";
  private static final String KEY_VARIANT_LITURGY_2 = "Liturgieerweiterungen";
  private static final String KEY_VARIANT_LITURGY_3 = "Liturgieerweiterung:";
  private static final String KEY_DURATION_1 = "Wirkungsdauer:";
  private static final String KEY_DURATION_2 = "Dauer:";
  private static final String KEY_DURATION_3 = "Wirkungsdauer";
  private static final String KEY_FEATURE = "Merkmal:";
  private static final String KEY_FEATURE_2 = "Merkmal";
  private static final String KEY_RANGE = "Reichweite:";
  private static final String KEY_RANGE_2 = "Reichweite";
  private static final String KEY_REMARK = "Anmerkung:";
  private static final String KEY_TARGET_CATEGORY = "Zielkategorie:";
  private static final String KEY_TARGET_CATEGORY_2 = "Zielkategorie";
  private static final String KEY_CHECK = "Probe:";
  private static final String KEY_EFFECT = "Wirkung";
  private static final String KEY_EFFECT_2 = "Wirkung:";
  private static final String KEY_CASTING_DURATION_SPELL = "Zauberdauer:";
  private static final String KEY_CASTING_DURATION_LITURGY = "Liturgiedauer:";
  private static final String KEY_CASTING_DURATION_RITUAL = "Ritualdauer:";
  private static final String KEY_CASTING_DURATION_RITUAL_2 = "Ritualdauer";
  private static final String KEY_CASTING_DURATION_CEREMONY = "Zeremoniedauer:";
  private static final String KEY_COST_ASP = "AsP-Kosten:";
  private static final String KEY_COST_ASP_2 = "AsP-Kosten";
  private static final String KEY_COST_KAP = "KaP-Kosten:";
  private static final String KEY_COMMONNESS = "Verbreitung:";
  private static final String KEY_COMMONNESS_2 = "Verbreitung";
  private static final String KEY_ADVANCEMENT_CATEGORY = "Steigerungsfaktor:";
  private static final String KEY_ADVANCEMENT_CATEGORY_2 = "Steigerungsfaktor";
  private static final String KEY_ADVANCEMENT_CATEGORY_3 = "teigerungsfaktor";

  private static final String KEY_GESTURE_AND_INCANTATION = "Geste und Formel:";
  private static final String KEY_GESTURE_AND_INCANTATION_2 = "Geste und Gebet:";

  private static final String KEY_REVERSALIS = "Reversalis:";
  private static final String KEY_REVERSALIS_2 = "Reversalis";
  protected static final String[] KEYS = {
      KEY_DURATION_1,
      KEY_DURATION_2,
      KEY_DURATION_3,
      KEY_FEATURE,
      KEY_FEATURE_2,
      KEY_RANGE,
      KEY_RANGE_2,
      KEY_REMARK,
      KEY_TARGET_CATEGORY,
      KEY_TARGET_CATEGORY_2,
      KEY_CHECK,
      KEY_EFFECT,
      KEY_EFFECT_2,
      KEY_CASTING_DURATION_SPELL,
      KEY_CASTING_DURATION_LITURGY,
      KEY_CASTING_DURATION_RITUAL,
      KEY_CASTING_DURATION_RITUAL_2,
      KEY_CASTING_DURATION_CEREMONY,
      KEY_COST_ASP,
      KEY_COST_ASP_2,
      KEY_COST_KAP,
      KEY_COMMONNESS,
      KEY_COMMONNESS_2,
      KEY_ADVANCEMENT_CATEGORY,
      KEY_ADVANCEMENT_CATEGORY_2,
      KEY_ADVANCEMENT_CATEGORY_3,
      KEY_VARIANT_SPELL,
      KEY_VARIANT_SPELL_2,
      KEY_VARIANT_LITURGY,
      KEY_VARIANT_LITURGY_2,
      KEY_VARIANT_LITURGY_3,
      KEY_GESTURE_AND_INCANTATION,
      KEY_GESTURE_AND_INCANTATION_2,
      KEY_REVERSALIS,
      KEY_REVERSALIS_2,
  };
  private static final String REGEX_TITLE_CHARS = "[\\wäöüÄÖÜß\\s\\d!\\-,\\/\\.…]";
  /*
      (<i>[a-z A-Z0-9äöüÄÖÜß!\-,\/\.…]{1,2}<\/i>){0,1}<i>[a-z A-Z0-9äöüÄÖÜß!\-,\/\.…]*(<\/i>){0,1}([1-3]){0,1} (\(FW|\(<\/i> FW)|\.[a-z A-Z0-9äöüÄÖÜß!\-,\/\.…]*(\(FW)|^[a-z A-Z0-9äöüÄÖÜß!\-,\/\.…]*(\(FW)
   */
  private static final String REGEX_EXTRACT_TITLE = "(<i>" + REGEX_TITLE_CHARS + "{1,2}<\\/i> )?" + //case "<i>L</i> <i>ängere Wirkungsdauer</i>"
      "<i>" + REGEX_TITLE_CHARS + "*" + //case "<i>Längere Wirkungsdauer"
      "(<\\/i>)? " +
      "([1-3] )?" + // case "<i>Längere Wirkungsdauer</i> 1 (FW"
      "(\\(FW|\\(<\\/i> FW)" + //cases "</i> (FW" or " (</i> FW"
      "|" + //ALTERNATIVE: in one case the italic is missing, so the match is with the preceding "."
      "\\." + REGEX_TITLE_CHARS + "*(\\(FW)" + //case ". Längere Wirkungsdauer (FW"
      "|" +
      "^" + REGEX_TITLE_CHARS + "*(\\(FW)"; //case Begin line: "Längere Wirkungsdauer (FW "
  private static final Pattern PATTERN_EXTRACT_TITLE = Pattern.compile(REGEX_EXTRACT_TITLE);

  ConverterAtomicFlagsMysticalSkill flags = new ConverterAtomicFlagsMysticalSkill();

  public String[] getKeys()
  {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsMysticalSkill getFlags()
  {
    return flags;
  }

  @Override
  protected String getClassName()
  {
    return this.getClass().getName();
  }


  @Override
  public List<MysticalSkillRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> texts, TopicConfiguration conf)
  {
    List<MysticalSkillRaw> returnValue = new ArrayList<>();

    AtomicInteger lastPage = new AtomicInteger(0);

    texts.forEach(t -> {

      MysticalSkillRaw currentEntry = last(returnValue);

      String cleanText = t.text.trim();

      boolean isDataKey = validateIsDataKey(t);
      boolean isDataValue = validateIsDataValue(t);
      handleWasNoKeyStrings(getFlags(), t); // used in MysticalSkill for QS flags, they act differently, because they are also part of the effect

      if (checkIsNewSpell(lastPage, t, conf))
      {
        concludePredecessor(last(returnValue));
        MysticalSkillRaw newEntry = new MysticalSkillRaw();
        this.getFlags().initDataFlags();
        newEntry.setTopic(conf.topic);
        newEntry.setPublication(conf.publication);
        if (conf.topic == TopicEnum.TRICKS_GRIMORIUM || conf.topic == TopicEnum.BLESSING_DIVINARIUM)
        {
          newEntry.cost = "1 AsP";
          newEntry.castingDuration = "1 Aktion";
        }
        returnValue.add(newEntry);

        lastPage.set(t.onPage);
        currentEntry = newEntry;
      }

      boolean isName = validateIsName(t, conf);

      // handle name
      if (isName)
      {
        isDataKey = false;
        this.getFlags().wasName.set(true);
        currentEntry.setName(concatForDataValue(currentEntry.getName(), t.text));
      }

      // handle keys
      if (isDataKey)
      {
        applyFlagsForKey(t.text);
      }

      // handle values
      if (isDataValue)
      {
        applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
        applyFlagsForNoKeyStrings(this.getFlags(), t.text);
      }
    });
    concludePredecessor(last(returnValue));

    last(returnValue).description = last(returnValue).description.replace("<i>Professionspaket</i> ", "");
    return returnValue;
  }

  protected boolean checkIsNewSpell(AtomicInteger lastPage, TextWithMetaInfo currentPage, TopicConfiguration conf)
  {
    return lastPage.get() != currentPage.onPage && currentPage.size >= conf.nameSize;
  }

  private boolean validateIsName(TextWithMetaInfo t, TopicConfiguration conf)
  {
    return t.size >= conf.nameSize;
  }

  @Override
  protected void applyFlagsForKey(String key)
  {
    flags.wasName.set(false);
    flags.wasDescription.set(false);
    flags.wasDuration.set(key.trim().equals(KEY_DURATION_1)
        || key.trim().equals(KEY_DURATION_2)
        || key.trim().equals(KEY_DURATION_3));
    flags.wasFeature.set(key.trim().equals(KEY_FEATURE)
        || key.trim().equals(KEY_FEATURE_2));
    flags.wasRange.set(key.trim().equals(KEY_RANGE)
        || key.trim().equals(KEY_RANGE_2));
    flags.wasRemarks.set(key.trim().equals(KEY_REMARK));
    flags.wasTargetCategory.set(key.trim().equals(KEY_TARGET_CATEGORY)
        || key.trim().equals(KEY_TARGET_CATEGORY_2));
    flags.wasCheck.set(key.trim().equals(KEY_CHECK));
    flags.wasEffect.set(key.trim().equals(KEY_EFFECT)
        || key.trim().equals(KEY_EFFECT_2));
    flags.wasCastingDuration.set(
        key.trim().equals(KEY_CASTING_DURATION_SPELL)
            || key.trim().equals(KEY_CASTING_DURATION_LITURGY)
            || key.trim().equals(KEY_CASTING_DURATION_RITUAL)
            || key.trim().equals(KEY_CASTING_DURATION_RITUAL_2)
            || key.trim().equals(KEY_CASTING_DURATION_CEREMONY));
    flags.wasCost.set(key.trim().equals(KEY_COST_ASP)
        || key.trim().equals(KEY_COST_ASP_2)
        || key.trim().equals(KEY_COST_KAP));
    flags.wasCommonness.set(key.trim().equals(KEY_COMMONNESS)
        || key.trim().equals(KEY_COMMONNESS_2));
    flags.wasAdvancementCategory.set(key.trim().equals(KEY_ADVANCEMENT_CATEGORY)
        || key.trim().equals(KEY_ADVANCEMENT_CATEGORY_2)
        || key.trim().equals(KEY_ADVANCEMENT_CATEGORY_3));
    flags.wasVariants.set(key.trim().equals(KEY_VARIANT_SPELL)
        || key.trim().equals(KEY_VARIANT_SPELL_2)
        || key.trim().equals(KEY_VARIANT_LITURGY)
        || key.trim().equals(KEY_VARIANT_LITURGY_2)
        || key.trim().equals(KEY_VARIANT_LITURGY_3)
    );
    flags.wasGestureAndIncantation.set(key.trim().equals(KEY_GESTURE_AND_INCANTATION)
        || key.trim().equals(KEY_GESTURE_AND_INCANTATION_2));
    flags.wasReversalis.set(key.trim().equals(KEY_REVERSALIS)
        || key.trim().equals(KEY_REVERSALIS_2));
    if (flags.wasVariants.get())
    {
      flags.wasFurtherInformation.set(false);
    }
  }

  @Override
  protected void applyDataValue(MysticalSkillRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    if (currentDataObject != null)
    {
      if ((flags.wasName.get() || flags.wasDescription.get()) && !flags.wasVariants.get())
        currentDataObject.description = concatForDataValue(currentDataObject.description, cleanText);
      if (flags.wasRange.get()) currentDataObject.range = concatForDataValue(currentDataObject.range, cleanText);
      if (flags.wasDuration.get()) currentDataObject.duration = concatForDataValue(currentDataObject.duration, cleanText);
      if (flags.wasFeature.get()) currentDataObject.feature = concatForDataValue(currentDataObject.feature, cleanText);
      if (flags.wasTargetCategory.get()) currentDataObject.targetCategory = concatForDataValue(currentDataObject.targetCategory, cleanText);
      if (flags.wasRemarks.get()) currentDataObject.remarks = concatForDataValue(currentDataObject.remarks, cleanText);

      if (flags.wasCheck.get()) currentDataObject.check = concatForDataValue(currentDataObject.check, cleanText).replace(":", "").trim();
      if (flags.wasEffect.get()) currentDataObject.effect = concatForDataValueWithMarkup(currentDataObject.effect, cleanText, isBold, isItalic);
      if (flags.wasCastingDuration.get()) currentDataObject.castingDuration = concatForDataValue(currentDataObject.castingDuration, cleanText);
      if (flags.wasCost.get()) currentDataObject.cost = concatForDataValue(currentDataObject.cost, cleanText);
      if (flags.wasCommonness.get()) currentDataObject.commonness = concatForDataValue(currentDataObject.commonness, cleanText);
      if (flags.wasReversalis.get())
        currentDataObject.reversalis = concatForDataValueWithMarkup(currentDataObject.reversalis, cleanText, isBold, isItalic);
      if (flags.wasGestureAndIncantation.get())
        currentDataObject.gesturesAndIncantations = concatForDataValueWithMarkup(currentDataObject.gesturesAndIncantations, cleanText, isBold, isItalic);
      if (flags.wasFurtherInformation.get())
        currentDataObject.furtherInformation = concatForDataValue(currentDataObject.furtherInformation, cleanText);

      if (flags.wasAdvancementCategory.get())
      {
        currentDataObject.advancementCategory = concatForDataValue(currentDataObject.advancementCategory, cleanText);
        flags.wasAdvancementCategory.set(false);
        flags.wasFurtherInformation.set(true);
      }

      if (flags.wasQs1.get()) currentDataObject.qs1 = concatForDataValue(currentDataObject.qs1, cleanText).replace(":", "").trim();
      if (flags.wasQs2.get()) currentDataObject.qs2 = concatForDataValue(currentDataObject.qs2, cleanText).replace(":", "").trim();
      if (flags.wasQs3.get()) currentDataObject.qs3 = concatForDataValue(currentDataObject.qs3, cleanText);
      if (flags.wasQs4.get()) currentDataObject.qs4 = concatForDataValue(currentDataObject.qs4, cleanText);
      if (flags.wasQs5.get()) currentDataObject.qs5 = concatForDataValue(currentDataObject.qs5, cleanText);
      if (flags.wasQs6.get()) currentDataObject.qs6 = concatForDataValue(currentDataObject.qs6, cleanText);

      if (flags.wasVariants.get())
        currentDataObject.variantsText = concatForDataValueWithMarkup(currentDataObject.variantsText, cleanText, isBold, isItalic);

    }
    else
    {
      LOGGER.error("MysticalSkillRaw was null: " + cleanText);
    }
  }


  @Override
  protected void concludePredecessor(MysticalSkillRaw msr)
  {
    if (msr != null && msr.variantsText != null)
    {
      List<String> variantStrings = Arrays.asList(msr.variantsText.split("#"))
          .stream()
          .filter(t -> t != null && !t.isEmpty())
          .collect(Collectors.toList());

      List<MysticalSkillVariant> variants = variantStrings.stream().map(vs -> {
        MysticalSkillVariant msv = new MysticalSkillVariant();
        msv.name = vs.substring(0, vs.indexOf("(")).trim();
        String cleaned = vs.replace("<br>", ",").replace(" KaP)", " AP)");
        msv.minLevel = Integer.valueOf(cleaned.substring(vs.indexOf("FW") + 2, cleaned.indexOf(",", vs.indexOf("("))).trim());
        msv.ap = Integer.valueOf(cleaned.substring(cleaned.indexOf(",", cleaned.indexOf("(")) + 1, cleaned.indexOf("AP")).trim());
        msv.description = vs.substring(cleaned.indexOf("("));

        return msv;
      }).collect(Collectors.toList());


      variants.forEach(v -> {
        if (v.minLevel == VARIANT_I_REQ)
        {
          msr.variant1 = v;
        }
        else if (v.minLevel == VARIANT_II_REQ)
        {
          msr.variant2 = v;
        }
        else if (v.minLevel == VARIANT_III_REQ)
        {
          msr.variant3 = v;
        }
        else if (v.minLevel == VARIANT_IV_REQ)
        {
          msr.variant4 = v;
        }
        else if (v.minLevel == VARIANT_V_REQ)
        {
          msr.variant5 = v;
        }
      });
    }
  }

  private List<Triplet<Integer, Integer, String>> extractVariantTitles(MysticalSkillRaw ms)
  {
    Matcher matcher = PATTERN_EXTRACT_TITLE.matcher(ms.variantsText);
    List<Triplet<Integer, Integer, String>> returnValue = new ArrayList<>();

    StringBuilder res = new StringBuilder();
    res.append(ms.variantsText)
        .append("\r\n");

    while (matcher.find())
    {
      res.append("Start index: ")
          .append(matcher.start());
      res.append(" End index: ")
          .append(matcher.end());
      res.append(" Found: ")
          .append(matcher.group())
          .append("\r\n");
      returnValue.add(new Triplet<>(matcher.start(), matcher.end(), matcher.group()));
    }

    if (returnValue.size() < 3)
    {
      String msg = String.format("VARIANTS NOT CORRECT:%n %s.%s -->%n%s%n", ms.publication, ms.name, res);
      LOGGER.error(msg);
    }

    return returnValue;
  }


  @Override
  protected void applyFlagsForNoKeyStrings(ConverterAtomicFlagsMysticalSkill flags, String text)
  {
    flags.wasQs1.set(text.trim().equals("QS 1:"));
    flags.wasQs2.set(text.trim().equals("QS 2:"));
    flags.wasQs3.set(text.trim().equals("QS 3:"));
    flags.wasQs4.set(text.trim().equals("QS 4:"));
    flags.wasQs5.set(text.trim().equals("QS 5:"));
    flags.wasQs6.set(text.trim().equals("QS 6:"));


  }

  @Override
  protected void handleWasNoKeyStrings(ConverterAtomicFlagsMysticalSkill flags, TextWithMetaInfo t)
  {
    flags.wasQs1.set(flags.wasQs1.get() && !validateIsDataKey(t));
    flags.wasQs2.set(flags.wasQs2.get() && !validateIsDataKey(t));
    flags.wasQs3.set(flags.wasQs3.get() && !validateIsDataKey(t));
    flags.wasQs4.set(flags.wasQs4.get() && !validateIsDataKey(t));
    flags.wasQs5.set(flags.wasQs5.get() && !validateIsDataKey(t));
    flags.wasQs6.set(flags.wasQs6.get() && !validateIsDataKey(t));
  }

  @Override
  protected void handleFirstValue(List<MysticalSkillRaw> returnValue, TopicConfiguration conf, String cleanText)
  {

    if (!flags.getFirstFlag().get())
    {
      MysticalSkillRaw newEntry = new MysticalSkillRaw();
      flags.initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      returnValue.add(newEntry);
    }

    last(returnValue).setName(concatForDataValue(last(returnValue).getName(), cleanText));
  }

  protected boolean validateIsDataValue(TextWithMetaInfo t)
  {
    return t.size == 900 || t.size == 800 && t.text.contains("QS");
  }


  protected boolean validateIsDataKey(TextWithMetaInfo t)
  {
    return (t.size == 800 || t.size == 1000) && !t.text.isEmpty() && Arrays.stream(getKeys()).anyMatch(k -> k.equals(t.text));
  }
}
