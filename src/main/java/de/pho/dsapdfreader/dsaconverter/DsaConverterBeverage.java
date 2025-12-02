package de.pho.dsapdfreader.dsaconverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.dsaconverter.model.BeverageRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsTavern;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterBeverage extends DsaConverter<BeverageRaw, ConverterAtomicFlagsTavern> {

  private static final String KEY_ALTERNATIVE_NAMEN = "";
  protected static final String[] KEYS = {
      KEY_ALTERNATIVE_NAMEN,
  };
  private ConverterAtomicFlagsTavern flags;

  @Override
  protected String[] getKeys() {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsTavern getFlags() {
    if (flags == null) {
      this.flags = new ConverterAtomicFlagsTavern();
    }
    return flags;
  }

  @Override
  public List<BeverageRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> texts, TopicConfiguration conf) {
    Map<String, BeverageRaw> tavernMap = new HashMap<>();

    AtomicReference<String> currentName = new AtomicReference<>();
    AtomicBoolean isList = new AtomicBoolean(false);
    AtomicReference<String> currentMasseinheit = new AtomicReference<>();
    AtomicReference<String> currentType = new AtomicReference<>();
    texts.forEach(t -> {
      if (t.size == 1300) {
        currentType.set(t.text);
      }
      t.text = t.text.replace("- ", "-")
          .replace("Firuns-tröpfchen", "Firunströpfchen");
      if (t.text.startsWith("Qualität der Taverne")) return;
      if (t.text.startsWith("Preis pro MaßPreis pro ")) {
        currentMasseinheit.set(t.text
            .replace("Preis pro MaßPreis pro ", "")
            .replaceAll("Wirkung.*", "")
        );
        String name = t.text.replaceAll(".*Wirkung \\(g/m\\)", "").trim();
        if (!name.isEmpty()) currentName.set(name);
        t.text = t.text.replaceAll("Preis pro MaßPreis pro (Becher|Becherchen|Humpen|typischerEinheit)Wirkung \\(g/m\\)", "");
      }
      else if (t.isBold && t.text.startsWith("• ")) {
        String name = t.text.replace("• ", "");
        currentName.set(name);
        addNewBeverage(tavernMap, currentName.get(), conf.publication, currentMasseinheit.get(), currentType.get());
        isList.set(false);
      }
      else if (t.isBold) {
        isList.set(true);
        currentName.set(t.text);
      }
      else if (currentName.get() != null) {
        if (tavernMap.get(currentName.get()) == null) {
          addNewBeverage(tavernMap, currentName.get(), conf.publication, currentMasseinheit.get(), null);
        }
        tavernMap.get(currentName.get()).unit = currentMasseinheit.get();

        if (isList.get()) {
          tavernMap.get(currentName.get()).list = (tavernMap.get(currentName.get()).list + " " + t.text).trim();
        }
        else {
          tavernMap.get(currentName.get()).description = (tavernMap.get(currentName.get()).description + " " + t.text).trim();
        }
      }
    });

    Pattern pattern = Pattern.compile("^(?<verbreitung>.*)" +
        "(?<quality>\\d-\\d)" +
        "(?<priceLiter>[\\d,]+\\s+(?:Kreuzer|Heller|Silber|Dukaten))" +
        "(?<priceUnit>[\\d,]+\\s+(?:Kreuzer|Heller|Silber|Dukaten)(?:\\s*\\(.*?\\))?)\\s*" +
        "(?<effect>.*)$");

    Pattern pUnit = Pattern.compile(("(?<=\\().*(?=\\))"));

    return tavernMap.values().stream().map(e -> {
      Matcher m = pattern.matcher(e.list);

      if (m.find()) {
        e.verbreitung = m.group("verbreitung");
        String[] qualityRanges = m.group("quality").split("-");
        e.qualityMin = Integer.valueOf(qualityRanges[0]);
        e.qualityMax = Integer.valueOf(qualityRanges[1]);
        e.pricePerLiter = m.group("priceLiter");
        e.pricePerUnit = m.group("priceUnit");
        e.effect = m.group("effect");
      }

      if (e.pricePerUnit != null && !e.pricePerUnit.isEmpty()) {
        m = pUnit.matcher(e.pricePerUnit);
        if (m.find()) {
          e.unit = m.group();
        }
      }
      return e;
    }).toList();
  }

  private void addNewBeverage(Map<String, BeverageRaw> tavernMap, String name, String publication, String unit, String type) {
    if (!name.isEmpty()) {
      tavernMap.put(name, new BeverageRaw());
      tavernMap.get(name).name = name;
      tavernMap.get(name).publication = publication;
      tavernMap.get(name).unit = unit;
      tavernMap.get(name).description = "";
      tavernMap.get(name).list = "";
      tavernMap.get(name).type = type;
      tavernMap.get(name).topic = TopicEnum.AUSRÜSTUNG;
    }
  }


  @Override
  protected String getClassName() {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<BeverageRaw> returnValue, TopicConfiguration conf, String cleanText) {

    if (!this.getFlags().getFirstFlag().get()) {
      BeverageRaw newEntry = new BeverageRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      returnValue.add(newEntry);
    }
    last(returnValue).setName(concatForDataValue(last(returnValue).getName(), cleanText));
  }

  @Override
  protected void applyFlagsForKey(String key) {
    this.getFlags().wasName.set(false);
    this.getFlags().wasName.set(false);
  }

  @Override
  protected void applyDataValue(BeverageRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic) {
    if (currentDataObject != null) {
      if (this.getFlags().wasName.get()) currentDataObject.name = concatForDataValueWithMarkup(currentDataObject.name, cleanText, isBold, isItalic);
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
  protected void concludePredecessor(BeverageRaw lastEntry) {
  }
}
