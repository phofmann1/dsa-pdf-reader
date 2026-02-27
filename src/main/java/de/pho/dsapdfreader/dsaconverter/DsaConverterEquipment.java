package de.pho.dsapdfreader.dsaconverter;

import static de.pho.dsapdfreader.dsaconverter.DsaConverterMeleeWeapon.PAT_CRAFT;
import static de.pho.dsapdfreader.dsaconverter.DsaConverterMeleeWeapon.PAT_PRICE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.EquipmentRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsEquipment;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterEquipment extends DsaConverter<EquipmentRaw, ConverterAtomicFlagsEquipment> {
  /* Test Strings:
   */

  private static final String[] JEWLERY_LEVELS = {"einfach", "verziert", "edel"};
  private static final String BREAKPOINT = "Achat";
  private static final Logger LOGGER = LogManager.getLogger();
  private static final String HL_KODEX_DES_GÖTTERWIRKENS = "ZeremonialgegenständeGegenstandStrukturpunkteGewichtPreisKomplexität";
  private static final String HL_BASE = "GegenstandGewichtStrukturpunktePreisKomplexität";
  private static final String HL_LEISURE = "GegenstandGewichtPreisKomplexität";
  private static final String HL_INSTRUMENT_AND_ANIMALEQUIPMENT = "GegenstandGewichtPreis";
  private static final String HL_DIAMONDS = "(geschliffen Preis 2-5fach)GegenstandFarbePreis/10 Karat";
  private static final String HL_ELIXIR = "Preis (beim Alchimisten)";
  private static final String HL_ANIMALS = "TierPreis";
  private static final String HL_VEHICLE = "GegenstandPreis";

  private static final String HL_RÜSTKAMMER = "GewichtPreisKomplexität";
  private static final String HL_REGIONALBAND = "Preise";
  private static final String HL_REGIONALBAND_II = "Preis";
  private static final String HL_ALCHIMICA_AS = "Anwendung";
  private static final String HL_UNEDLE_METALLE_AS = "MaterialVerfügbarkeitPreis pro SteinUnedle Metalle";
  private static final String HL_HALBEDLE_METALLE_AS = "Halbedle Metalle";
  private static final String HL_EDLE_METALLE_AS = "Edle Metalle";
  private static final String HL_MAGISCHE_METALLE_AS = "Magische Metalle";
  private static final String HL_STERNMETALLE_AS = "Sternenmetalle";
  private static final String HL_UNMETALLE_AS = "Unmetalle";
  private static final String HL_ASTRALSPEICHER_STEINE_AS = "Astralspeicher-Steine";
  private static final String HL_GESTEIN_AS = "Gestein";
  private static final String HL_EDELSTEINE_AS = "Edelsteine";
  private static final String HL_HOLZ_AS = "Holz";
  private static final String HL_STOFF_AS = "Stoff";
  private static final String HL_KOERPERTEILE_AS = "Körperteile";
  private static final String HL_KUENSTLICHE_MATERIALIEN_AS = "Künstliche Materialien";

  //\d+(?= ?StP)
  private static final Pattern PAT_STRUCTURE = Pattern.compile("[\\d.]+(?= ?StP)");
  //[A-z ,]+(?=[\d,.+\/( bis )]+ S)
  private static final Pattern PAT_GEMS_COLOR = Pattern.compile("[A-zßäöüÄÖÜ ,]+(?=[\\d,.+\\/( bis )]+ S)");
  private static final String KEY_REMARK = "Anmerkung";
  protected static final String[] KEYS = {
      KEY_REMARK,
  };
  //\d[\d,\.\/]+(?=\+? Stn)
  protected static Pattern PAT_WEIGHT_EQUIPMENT = Pattern.compile("\\d[\\d,\\.\\/]*(?=\\+? (Stn|Stein))");
  private List<EquipmentRaw> equipmentRawList;
  private ConverterAtomicFlagsEquipment flags;
  private AtomicReference<String> currentCategory;

  @Override
  public List<EquipmentRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf) {
    equipmentRawList = new ArrayList<>();
    String msg = String.format("parse  result to %s", getClassName());
    LOGGER.debug(msg);

    currentCategory = new AtomicReference<>();
    flags = new ConverterAtomicFlagsEquipment();

    resultList
        .forEach(t -> {

          String cleanText = t.text
              .trim();

          if (cleanText != null && !cleanText.isEmpty()) {
            // validate the flags for conf
            boolean isFirstValue = validateIsFirstValue(t, conf);
            boolean isDataKey = validateIsDataKey(t, cleanText, conf) && !isFirstValue;
            boolean isDataValue = validateIsDataValue(t, cleanText, conf);
            finishPredecessorAndStartNew(isFirstValue, false, equipmentRawList, conf, cleanText);

            // handle keys
            if (isDataKey) {
              if (cleanText.equals(KEY_REMARK)) {
                flags.wasRemark.set(true);
              }
              else {
                this.concludePredecessor(last(equipmentRawList));//called additionally,because equipment does iterate over categories not etnries
                this.generateNewEquipmentRaw(cleanText);
              }
            }

            // handle values
            if (isDataValue) {
              applyDataValue(last(equipmentRawList), cleanText, t.isBold, t.isItalic);
              flags.wasName.set(false);
              flags.wasData.set(true);
            }
            flags.isFirstValue.set(false);
          }

        });
    concludePredecessor(last(equipmentRawList)); //finish the last entry in list
    equipmentRawList = equipmentRawList.stream().map(er -> {
      er.publication = conf.publication;
      er.topic = conf.topic;
      return er;
    }).collect(Collectors.toList());
    return equipmentRawList;
  }

  private void generateNewEquipmentRaw(String name) {
    EquipmentRaw e = new EquipmentRaw();
    e.name = name;
    e.category = currentCategory.get();
    this.splitEntry(last(equipmentRawList));
    equipmentRawList.add(e);
    flags.wasName.set(true);
    flags.wasRemark.set(false);
    flags.wasData.set(false);
  }

  @Override
  protected String[] getKeys() {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsEquipment getFlags() {
    return this.flags;
  }

  @Override
  protected String getClassName() {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<EquipmentRaw> returnValue, TopicConfiguration conf, String cleanText) {
    this.applyCurrentCategory(cleanText);

    String name = cleanText
        .replace(HL_KODEX_DES_GÖTTERWIRKENS, "")
        .replace(HL_BASE, "")
        .replace(HL_LEISURE, "")
        .replace(HL_INSTRUMENT_AND_ANIMALEQUIPMENT, "")
        .replace(HL_DIAMONDS, "")
        .replace(HL_ELIXIR, "")
        .replace(HL_VEHICLE, "")
        .replace(HL_ANIMALS, "")
        .replace(HL_RÜSTKAMMER, "")
        .replace(HL_REGIONALBAND, "")
        .replace(HL_ALCHIMICA_AS, "")
        .replace(HL_UNEDLE_METALLE_AS, "")
        .replace(HL_HALBEDLE_METALLE_AS, "")
        .replace(HL_EDLE_METALLE_AS, "")
        .replace(HL_MAGISCHE_METALLE_AS, "")
        .replace(HL_STERNMETALLE_AS, "")
        .replace(HL_UNMETALLE_AS, "")
        .replace(HL_ASTRALSPEICHER_STEINE_AS, "")
        .replace(HL_GESTEIN_AS, "")
        .replace(HL_EDELSTEINE_AS, "")
        .replace(HL_HOLZ_AS, "")
        .replace(HL_STOFF_AS, "")
        .replace(HL_KOERPERTEILE_AS, "")
        .replace(HL_KUENSTLICHE_MATERIALIEN_AS, "")
        .replace(HL_REGIONALBAND_II, "")
        .replace(currentCategory.get(), "")
        .replaceAll("^paket Wüstenreich$", "Proviantpaket Wüstenreich");

    if (!name.isEmpty()) {
      this.generateNewEquipmentRaw(name);
      this.getFlags().isFirstValue.set(false);
    }
  }

  private void applyCurrentCategory(String cleanText) {
    if (cleanText.contains(HL_DIAMONDS)) {
      currentCategory.set(cleanText.substring(0, cleanText.indexOf("(geschliffen")));
    }
    else if (cleanText.startsWith(HL_KODEX_DES_GÖTTERWIRKENS)) {
      currentCategory.set("Zeremonialgegenstände");
    }
    else if (cleanText.contains(HL_ELIXIR)) {
      currentCategory.set(cleanText.substring(0, cleanText.indexOf("Preis")));
    }
    else if (cleanText.contains(HL_ANIMALS)) {
      currentCategory.set(cleanText.substring(0, cleanText.indexOf("TierPreis")));
    }
    else if (cleanText.contains(HL_RÜSTKAMMER) && !cleanText.contains("Gegenstand")) {
      currentCategory.set(cleanText.substring(0, cleanText.indexOf(HL_RÜSTKAMMER)));
    }
    else if (cleanText.contains("Gegenstand")) {
      currentCategory.set(cleanText.substring(0, cleanText.indexOf("Gegenstand")));
    }
    else if (cleanText.contains(HL_REGIONALBAND)) {
      currentCategory.set(cleanText.substring(0, cleanText.indexOf(HL_REGIONALBAND)));
    }
    else if (cleanText.contains(HL_ALCHIMICA_AS)) {
      currentCategory.set("alchimistische_zutaten");
    }
    else if (cleanText.contains(HL_UNEDLE_METALLE_AS)) {
      currentCategory.set("Unedle Metalle");
    }
    else if (cleanText.contains(HL_HALBEDLE_METALLE_AS)) {
      currentCategory.set(HL_HALBEDLE_METALLE_AS);
    }
    else if (cleanText.contains(HL_EDLE_METALLE_AS)) {
      currentCategory.set(HL_EDLE_METALLE_AS);
    }
    else if (cleanText.contains(HL_MAGISCHE_METALLE_AS)) {
      currentCategory.set(HL_MAGISCHE_METALLE_AS);
    }
    else if (cleanText.contains(HL_STERNMETALLE_AS)) {
      currentCategory.set(HL_STERNMETALLE_AS);
    }
    else if (cleanText.contains(HL_UNMETALLE_AS)) {
      currentCategory.set(HL_UNMETALLE_AS);
    }
    else if (cleanText.contains(HL_ASTRALSPEICHER_STEINE_AS)) {
      currentCategory.set(HL_ASTRALSPEICHER_STEINE_AS);
    }
    else if (cleanText.contains(HL_GESTEIN_AS)) {
      currentCategory.set(HL_GESTEIN_AS);
    }
    else if (cleanText.contains(HL_EDELSTEINE_AS)) {
      currentCategory.set(HL_EDELSTEINE_AS);
    }
    else if (cleanText.contains(HL_HOLZ_AS)) {
      currentCategory.set(HL_HOLZ_AS);
    }
    else if (cleanText.contains(HL_STOFF_AS)) {
      currentCategory.set(HL_STOFF_AS);
    }
    else if (cleanText.contains(HL_KOERPERTEILE_AS)) {
      currentCategory.set(HL_KOERPERTEILE_AS);
    }
    else if (cleanText.contains(HL_KUENSTLICHE_MATERIALIEN_AS)) {
      currentCategory.set(HL_KUENSTLICHE_MATERIALIEN_AS);
    }

    else if (cleanText.contains(HL_ALCHIMICA_AS)) {
      currentCategory.set("alchimistische_zutaten");
    }

    else if (cleanText.contains(HL_REGIONALBAND_II)) {
      currentCategory.set(cleanText.substring(0, cleanText.indexOf(HL_REGIONALBAND_II)));
    }
  }

  @Override
  protected void applyFlagsForKey(String key) {

  }

  @Override
  protected void applyDataValue(EquipmentRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic) {
    if (currentDataObject != null && currentDataObject.category.equals(currentCategory.get())) {
      if (flags.wasRemark.get()) {
        currentDataObject.remark = concatForDataValue(currentDataObject.remark, cleanText);
      }
      else {
        currentDataObject.weight = firstMatch(PAT_WEIGHT_EQUIPMENT, cleanText);
        currentDataObject.price = firstMatch(PAT_PRICE, cleanText);
        if (cleanText.contains(" S pro Stufe")) {
          currentDataObject.isPricePerLevel = true;
        }
        currentDataObject.craft = firstMatch(PAT_CRAFT, cleanText);
        currentDataObject.structure = firstMatch(PAT_STRUCTURE, cleanText);
        currentDataObject.category = currentCategory.get();
        if (currentCategory.get().equals("Edelsteine und Feingestein")) {
          currentDataObject.color = firstMatch(PAT_GEMS_COLOR, cleanText);
        }
      }
    }
    else {
      Matcher m = Pattern.compile("([A-ü ,-]+|\\d+ l)+\\d{1,3}(\\.\\d{3})*(,\\d{1,2})? (Silbertaler|S)").matcher(cleanText);
      while (m.find()) {
        String result = m.group();
        String price = result.replaceAll("Silbertaler|S", "")
            .replaceAll("^([A-ü ,-]+|\\d+ l)+", "").trim();
        String name = result.replaceAll(price + ".*", "").trim();
        this.generateNewEquipmentRaw(name);
        last(equipmentRawList).price = price;
      }
    }
  }

  @Override
  public boolean validateIsFirstValue(TextWithMetaInfo t, TopicConfiguration conf) {
    return t.isBold
        && (t.text.contains(HL_BASE)
        || t.text.contains(HL_INSTRUMENT_AND_ANIMALEQUIPMENT)
        || t.text.contains(HL_DIAMONDS)
        || t.text.contains(HL_ELIXIR)
        || t.text.contains(HL_LEISURE)
        || t.text.contains(HL_ANIMALS)
        || t.text.contains(HL_VEHICLE)
        || t.text.contains(HL_RÜSTKAMMER)
        || t.text.contains(HL_REGIONALBAND)
        || t.text.contains(HL_REGIONALBAND_II)
        || t.text.startsWith(HL_ALCHIMICA_AS)
        || t.text.startsWith(HL_UNEDLE_METALLE_AS)
        || t.text.startsWith(HL_HALBEDLE_METALLE_AS)
        || t.text.startsWith(HL_EDLE_METALLE_AS)
        || t.text.startsWith(HL_MAGISCHE_METALLE_AS)
        || t.text.startsWith(HL_STERNMETALLE_AS)
        || t.text.startsWith(HL_UNMETALLE_AS)
        || t.text.startsWith(HL_ASTRALSPEICHER_STEINE_AS)
        || t.text.startsWith(HL_GESTEIN_AS)
        || t.text.startsWith(HL_EDELSTEINE_AS)
        || t.text.startsWith(HL_HOLZ_AS)
        || t.text.startsWith(HL_STOFF_AS)
        || t.text.startsWith(HL_KOERPERTEILE_AS)
        || t.text.startsWith(HL_KUENSTLICHE_MATERIALIEN_AS)
    );
  }


  @Override
  protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return super.validateIsDataValue(t, cleanText, conf)
        && !cleanText.equals("Ausrüstung")
        && !cleanText.equals("Strassenraub & Halsabschneider")
        && !cleanText.endsWith(HL_INSTRUMENT_AND_ANIMALEQUIPMENT)
        && !cleanText.endsWith(HL_ANIMALS)
        && !cleanText.endsWith(HL_BASE)
        && !cleanText.endsWith(HL_DIAMONDS)
        && !cleanText.endsWith(HL_ELIXIR)
        && !cleanText.endsWith(HL_LEISURE)
        && !cleanText.endsWith(HL_VEHICLE)
        && !cleanText.contains(HL_RÜSTKAMMER)
        && !cleanText.contains(HL_REGIONALBAND)
        && !cleanText.contains(HL_REGIONALBAND_II);
  }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return t.isBold
        && !validateIsFirstValue(t, conf);
  }

  @Override
  protected void concludePredecessor(EquipmentRaw lastEntry) {
    this.splitEntry(lastEntry);
  }

  private void splitEntry(EquipmentRaw lastEntry) {
    if (lastEntry != null) {
      List<EquipmentRaw> newEquipmentRaws = new ArrayList<>();

      String[] multipleNames = lastEntry.name != null ? lastEntry.name.split("\\/") : new String[0];
      String[] multiplePrices = lastEntry.price != null ? lastEntry.price.split("\\/") : new String[0];
      String[] multipleStructure = lastEntry.structure != null ? lastEntry.structure.split("\\/") : new String[0];
      String[] multipleCraft = lastEntry.craft != null ? lastEntry.craft.split("\\/") : new String[0];
      String[] multipleWeight = lastEntry.weight != null ? lastEntry.weight.split("\\/") : new String[0];

      if (multipleNames.length > 1 && multipleNames.length == multiplePrices.length) {
        for (int idx = 0; idx < multipleNames.length; idx++) {
          EquipmentRaw newEr = new EquipmentRaw();
          newEr.category = lastEntry.category;
          newEr.remark = lastEntry.remark;
          newEr.name = multipleNames[idx];
          newEr.price = multiplePrices[idx];
          if (multipleNames.length == multipleCraft.length) {
            newEr.craft = multipleCraft[idx];
          }
          else {
            newEr.craft = lastEntry.craft;
          }

          if (multipleNames.length == multipleStructure.length) {
            newEr.structure = multipleStructure[idx];
          }
          else {
            newEr.structure = lastEntry.structure;
          }

          if (multipleNames.length == multipleWeight.length) {
            newEr.weight = multipleWeight[idx];
          }
          else {
            newEr.weight = lastEntry.weight;
          }

          newEquipmentRaws.add(newEr);
        }
        equipmentRawList.remove(lastEntry);
        equipmentRawList.addAll(newEquipmentRaws);
      }
      else if (multipleWeight.length > 1 && multipleWeight.length == multiplePrices.length) {
        for (int idx = 0; idx < multipleWeight.length; idx++) {
          EquipmentRaw newEr = new EquipmentRaw();
          newEr.category = lastEntry.category;
          newEr.remark = lastEntry.remark;
          newEr.name = lastEntry.name + ", " + JEWLERY_LEVELS[idx];
          newEr.price = multiplePrices[idx];
          newEr.craft = lastEntry.craft;

          newEr.structure = lastEntry.structure;
          newEr.weight = multipleWeight[idx];

          newEquipmentRaws.add(newEr);
        }
        equipmentRawList.remove(lastEntry);
        equipmentRawList.addAll(newEquipmentRaws);
      }
    }
  }
}
