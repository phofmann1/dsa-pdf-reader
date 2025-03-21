package de.pho.dsapdfreader.dsaconverter;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.dsaconverter.model.ArmorRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsEquipmentCombat;
import de.pho.dsapdfreader.exporter.model.enums.HitZoneKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DsaConverterArmorPart extends DsaConverter<ArmorRaw, ConverterAtomicFlagsEquipmentCombat> {
    /* Test Strings:
     */

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String KEY_REMARK = "Anmerkung";
    private static final String KEY_ADVANTAGE = "Rüstungsvorteil";
    private static final String KEY_DISADVANTAGE = "Rüstungsnachteil";
    private static final String KEY_ADVANTAGE_HELMET = "Helmvorteil";
    private static final String KEY_DISADVANTAGE_HELMET = "Helmnachteil";
    protected static final String[] KEYS = {
            KEY_REMARK,
            KEY_ADVANTAGE,
            KEY_DISADVANTAGE,
            KEY_ADVANTAGE_HELMET,
            KEY_DISADVANTAGE_HELMET
    };

    private ConverterAtomicFlagsEquipmentCombat flags;


    @Override
    public List<ArmorRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf) {
        List<ArmorRaw> returnValue = new ArrayList<>();
        String msg = String.format("parse  result to %s", getClassName());
        LOGGER.debug(msg);

        flags = new ConverterAtomicFlagsEquipmentCombat();

        resultList
                .forEach(t -> {

                    String cleanText = t.text
                            .trim();

                    if (!cleanText.isEmpty()) {
                        // validate the flags for conf
                        boolean isFirstValue = validateIsFirstValue(t, conf);
                        boolean isDataKey = validateIsDataKey(t, cleanText, conf);
                        boolean isDataValue = validateIsDataValue(t, cleanText, conf);
                        finishPredecessorAndStartNew(isFirstValue, false, returnValue, conf, cleanText);


                        // handle keys
                        if (isDataKey) {
                            applyFlagsForKey(t.text);
                        }

                        // handle values
                        if (isDataValue) {
                            applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
                            applyFlagsForNoKeyStrings(getFlags(), t.text);
                        }

                        getFlags().getFirstFlag().set(isFirstValue);


                    }

                });
        concludePredecessor(last(returnValue)); //finish the last entry in list
        return returnValue;
    }

    @Override
    protected String[] getKeys() {
        return KEYS;
    }

    @Override
    protected ConverterAtomicFlagsEquipmentCombat getFlags() {
        if (flags == null) {
            this.flags = new ConverterAtomicFlagsEquipmentCombat();
        }
        return flags;
    }

    @Override
    protected String getClassName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    protected void handleFirstValue(List<ArmorRaw> returnValue, TopicConfiguration conf, String cleanText) {
      ArmorRaw newEntry = new ArmorRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      newEntry.isArmorPart = true;
      newEntry.setName(cleanText
              .replace("ArtRüstungsschutzBelastung (Stufe)zusätzliche AbzügeGewichtPreisKomplexität", "")
              .replace("ArtRüstungsschutzBelastung (Stufe)zusätzliche AbzügeGewichtPreis", "")
              .replaceAll("Komplexität.*", "")
      );
      newEntry.weight = "0";
      newEntry.protectedZones = conf.topic == TopicEnum.RÜSTUNGEN_HELME ? List.of(HitZoneKey.head) : extractHitZoneKeyFromName(newEntry.name);
      this.getFlags().wasName.set(true);
      this.getFlags().isFirstValue.set(false);
      returnValue.add(newEntry);
    }

    private List<HitZoneKey> extractHitZoneKeyFromName(String name) {
        return name.toLowerCase().contains("hand") || name.toLowerCase().contains("arm")
                ? List.of(HitZoneKey.arm_right, HitZoneKey.arm_left)
                : List.of(HitZoneKey.leg_right, HitZoneKey.leg_left);
    }

    @Override
    protected void applyFlagsForKey(String key) {
        this.getFlags().wasName.set(false);
        this.getFlags().wasData.set(false);
        this.getFlags().wasRemark.set(key.trim().equals(KEY_REMARK));
        this.getFlags().wasAdvantage.set(key.trim().equals(KEY_ADVANTAGE) || key.trim().equals((KEY_ADVANTAGE_HELMET)));
        this.getFlags().wasDisadvantage.set(key.trim().equals(KEY_DISADVANTAGE) || key.trim().equals((KEY_DISADVANTAGE_HELMET)));
    }

    @Override
    protected void applyDataValue(ArmorRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic) {
        if (currentDataObject != null) {
            if (this.getFlags().wasRemark.get()) {
                currentDataObject.remark = concatForDataValueWithMarkup(currentDataObject.remark, cleanText, isBold, isItalic);
                currentDataObject.armor = this.extractArmorValueFromRemark(currentDataObject.remark);
                this.applyEncumberanceToArmor(currentDataObject);
            }
            if (this.getFlags().wasAdvantage.get())
                currentDataObject.advantage = concatForDataValue(currentDataObject.advantage, cleanText);
            if (this.getFlags().wasDisadvantage.get()) {
                if (cleanText.startsWith("»"))
                    this.getFlags().wasFinished.set(Boolean.TRUE); //Reset Disadvantage Flag to prevent FluffText in description
                if (!this.getFlags().wasFinished.get()) {
                    currentDataObject.disadvantage = concatForDataValue(currentDataObject.disadvantage, cleanText);
                }
            }
        }
    }

    @Override
    public boolean validateIsFirstValue(TextWithMetaInfo t, TopicConfiguration conf) {
        return t.isBold && t.size == conf.nameSize;
    }

    @Override
    protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
        return t.isBold && Arrays.stream(getKeys()).anyMatch(k -> k.equals(t.text)) && !t.text.equals("Übler Geruch");
    }

    @Override
    protected void concludePredecessor(ArmorRaw lastEntry) {
    }

    private void applyEncumberanceToArmor(ArmorRaw raw) {
        if (!raw.armor.isEmpty()) {
            switch (raw.armor) {
                case "6":
                    raw.encumberance = "3";
                    raw.additionalEncumberance = false;
                    break;
                case "5":
                    raw.encumberance = "2";
                    raw.additionalEncumberance = true;
                    break;
                case "4":
                    raw.encumberance = "2";
                    raw.additionalEncumberance = false;
                    break;
                case "3":
                    raw.encumberance = "1";
                    raw.additionalEncumberance = true;
                    break;
                case "2":
                    raw.encumberance = "1";
                    raw.additionalEncumberance = false;
                    break;
                case "1":
                    raw.encumberance = "0";
                    raw.additionalEncumberance = true;
                    break;
            }
        }
    }

    private String extractArmorValueFromRemark(String remark) {
        if (remark == null) return "0";
        if (remark.contains("Platte")) return "6";
        if (remark.contains("Schuppe")) return "5";
        if (remark.contains("Kette")) return "4";
        if (remark.contains("Leder")) return "3";
        if (remark.contains("Tuch")) return "2";
        if (remark.contains("Kleidung")) return "1";
        return "0";
    }
}
