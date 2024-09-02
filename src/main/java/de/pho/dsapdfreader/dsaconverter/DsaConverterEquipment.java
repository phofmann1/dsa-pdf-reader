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

public class DsaConverterEquipment extends DsaConverter<EquipmentRaw, ConverterAtomicFlagsEquipment>
{
  /* Test Strings:
   */

  private static final String[] JEWLERY_LEVELS = {"einfach", "verziert", "edel"};
  private static final String BREAKPOINT = "Achat";
  private static final Logger LOGGER = LogManager.getLogger();
  private static final String HL_BASE = "GegenstandGewichtStrukturpunktePreisKomplexität";
  private static final String HL_INSTRUMENT = "GegenstandGewichtPreis";
  private static final String HL_DIAMONDS = "(geschliffen Preis 2-5fach)GegenstandFarbePreis/10 Karat";
  private static final String HL_ELIXIR = "Preis (beim Alchimisten)";
  private static final String HL_LEISURE = "GegenstandGewichtPreisKomplexität";
  private static final String HL_ANIMALS = "TierPreis";
  private static final String HL_ANIMAL_EQUIPMENT = "GegenstandGewichtPreis";
  private static final String HL_VEHICLE = "GegenstandPreis";

  private static final String HL_RÜSTKAMMER = "GewichtPreisKomplexität";
  private static final String HL_REGIONALBAND = "Preise";
  private static final String HL_REGIONALBAND_II = "Preis";
  private static final String HL_KODEX_DES_GÖTTERWIRKENS = "ZeremonialgegenständeGegenstandStrukturpunkteGewichtPreisKomplexität";

  //\d+(?= ?StP)
  private static final Pattern PAT_STRUCTURE = Pattern.compile("\\d+(?= ?StP)");
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
  public List<EquipmentRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
  {
    equipmentRawList = new ArrayList<>();
    String msg = String.format("parse  result to %s", getClassName());
    LOGGER.debug(msg);

    currentCategory = new AtomicReference<>();
    flags = new ConverterAtomicFlagsEquipment();

    resultList
        .forEach(t -> {

          String cleanText = t.text
              .trim();

          if (cleanText != null && !cleanText.isEmpty())
          {
            // validate the flags for conf
            boolean isFirstValue = validateIsFirstValue(t, conf);
            boolean isDataKey = validateIsDataKey(t, cleanText, conf) && !isFirstValue;
            boolean isDataValue = validateIsDataValue(t, cleanText, conf);
            finishPredecessorAndStartNew(isFirstValue, false, equipmentRawList, conf, cleanText);

            // handle keys
            if (isDataKey)
            {
              if (cleanText.equals(KEY_REMARK))
              {
                flags.wasRemark.set(true);
              }
              else
              {
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
  protected String[] getKeys()
  {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsEquipment getFlags()
  {
    return this.flags;
  }

  @Override
  protected String getClassName()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<EquipmentRaw> returnValue, TopicConfiguration conf, String cleanText)
  {
    this.applyCurrentCategory(cleanText);

    String name = cleanText
        .replace(HL_BASE, "")
        .replace(HL_KODEX_DES_GÖTTERWIRKENS, "")
        .replace(HL_ANIMALS, "")
        .replace(HL_DIAMONDS, "")
        .replace(HL_VEHICLE, "")
        .replace(HL_LEISURE, "")
        .replace(HL_ELIXIR, "")
        .replace(HL_INSTRUMENT, "")
        .replace(HL_ANIMAL_EQUIPMENT, "")
        .replace(HL_RÜSTKAMMER, "")
        .replace(HL_REGIONALBAND, "")
        .replace(HL_REGIONALBAND_II, "")
        .replace(currentCategory.get(), "")
        .replaceAll("^paket Wüstenreich$", "Proviantpaket Wüstenreich");

    if (!name.isEmpty())
    {
      this.generateNewEquipmentRaw(name);
      this.getFlags().isFirstValue.set(false);
    }
  }

  private void applyCurrentCategory(String cleanText)
  {
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
    else if (cleanText.contains(HL_RÜSTKAMMER)) {
      currentCategory.set(cleanText.substring(0, cleanText.indexOf(HL_RÜSTKAMMER)));
    }
    else if (cleanText.contains(HL_REGIONALBAND)) {
      currentCategory.set(cleanText.substring(0, cleanText.indexOf(HL_REGIONALBAND)));
    }
    else if (cleanText.contains(HL_REGIONALBAND_II)) {
      currentCategory.set(cleanText.substring(0, cleanText.indexOf(HL_REGIONALBAND_II)));
    }
    else {
      currentCategory.set(cleanText.substring(0, cleanText.indexOf("Gegenstand")));
    }
  }

  @Override
  protected void applyFlagsForKey(String key)
  {

  }

  @Override
  protected void applyDataValue(EquipmentRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
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
        String name = result.replaceAll(price + ".*", "");
        this.generateNewEquipmentRaw(name);
        last(equipmentRawList).price = price;
      }
    }
  }

  @Override
  public boolean validateIsFirstValue(TextWithMetaInfo t, TopicConfiguration conf)
  {
    return t.isBold
        && (t.text.contains(HL_BASE)
        || t.text.contains(HL_INSTRUMENT)
        || t.text.contains(HL_DIAMONDS)
        || t.text.contains(HL_ELIXIR)
        || t.text.contains(HL_LEISURE)
        || t.text.contains(HL_ANIMALS)
        || t.text.contains(HL_ANIMAL_EQUIPMENT)
        || t.text.contains(HL_VEHICLE)
        || t.text.contains(HL_RÜSTKAMMER)
        || t.text.contains(HL_REGIONALBAND)
        || t.text.contains(HL_REGIONALBAND_II)
    );
  }


  @Override
  protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return super.validateIsDataValue(t, cleanText, conf)
        && !cleanText.equals("Ausrüstung")
        && !cleanText.equals("Strassenraub & Halsabschneider")
        && !cleanText.endsWith(HL_ANIMAL_EQUIPMENT)
        && !cleanText.endsWith(HL_ANIMALS)
        && !cleanText.endsWith(HL_BASE)
        && !cleanText.endsWith(HL_DIAMONDS)
        && !cleanText.endsWith(HL_ELIXIR)
        && !cleanText.endsWith(HL_INSTRUMENT)
        && !cleanText.endsWith(HL_LEISURE)
        && !cleanText.endsWith(HL_VEHICLE)
        && !cleanText.contains(HL_RÜSTKAMMER)
        && !cleanText.contains(HL_REGIONALBAND)
        && !cleanText.contains(HL_REGIONALBAND_II);
  }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return t.isBold
        && !validateIsFirstValue(t, conf);
  }

  @Override
  protected void concludePredecessor(EquipmentRaw lastEntry)
  {
    this.splitEntry(lastEntry);
  }

  private void splitEntry(EquipmentRaw lastEntry)
  {
    if (lastEntry != null)
    {
      List<EquipmentRaw> newEquipmentRaws = new ArrayList<>();

      String[] multipleNames = lastEntry.name != null ? lastEntry.name.split("\\/") : new String[0];
      String[] multiplePrices = lastEntry.price != null ? lastEntry.price.split("\\/") : new String[0];
      String[] multipleStructure = lastEntry.structure != null ? lastEntry.structure.split("\\/") : new String[0];
      String[] multipleCraft = lastEntry.craft != null ? lastEntry.craft.split("\\/") : new String[0];
      String[] multipleWeight = lastEntry.weight != null ? lastEntry.weight.split("\\/") : new String[0];

      if (multipleNames.length > 1 && multipleNames.length == multiplePrices.length)
      {
        for (int idx = 0; idx < multipleNames.length; idx++)
        {
          EquipmentRaw newEr = new EquipmentRaw();
          newEr.category = lastEntry.category;
          newEr.remark = lastEntry.remark;
          newEr.name = multipleNames[idx];
          newEr.price = multiplePrices[idx];
          if (multipleNames.length == multipleCraft.length)
          {
            newEr.craft = multipleCraft[idx];
          }
          else
          {
            newEr.craft = lastEntry.craft;
          }

          if (multipleNames.length == multipleStructure.length)
          {
            newEr.structure = multipleStructure[idx];
          }
          else
          {
            newEr.structure = lastEntry.structure;
          }

          if (multipleNames.length == multipleWeight.length)
          {
            newEr.weight = multipleWeight[idx];
          }
          else
          {
            newEr.weight = lastEntry.weight;
          }

          newEquipmentRaws.add(newEr);
        }
        equipmentRawList.remove(lastEntry);
        equipmentRawList.addAll(newEquipmentRaws);
      }
      else if (multipleWeight.length > 1 && multipleWeight.length == multiplePrices.length)
      {
        for (int idx = 0; idx < multipleWeight.length; idx++)
        {
          EquipmentRaw newEr = new EquipmentRaw();
          newEr.category = lastEntry.category;
          newEr.remark = lastEntry.remark;
          newEr.name = lastEntry.name + ", " + JEWLERY_LEVELS[idx];
          newEr.price = multiplePrices[idx];
          newEr.craft = lastEntry.craft;

          newEr.structure = lastEntry.craft;
          newEr.weight = multipleWeight[idx];

          newEquipmentRaws.add(newEr);
        }
        equipmentRawList.remove(lastEntry);
        equipmentRawList.addAll(newEquipmentRaws);
      }
    }
  }
}
