package de.pho.dsapdfreader.exporter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.model.AlchimieRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkillDuration;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.GiftKey;
import de.pho.dsapdfreader.exporter.model.enums.GiftTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.GiftVektorKey;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.AlchimieWirkung;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.GiftSO;


public class LoadToGift extends LoadToAlchimieA {
  protected static final Logger LOGGER = LogManager.getLogger();

  protected LoadToGift() {
  }

  public static GiftSO migrate(AlchimieRaw raw) {
    GiftSO gift = _init(raw, GiftSO.class);
    gift.key = Extractor.extractEnumKey(gift.name, GiftKey.class);
    return gift;
  }

  protected static <T extends GiftSO> T _init(AlchimieRaw raw, Class<T> clazz) {
    try {
      T t = clazz.getDeclaredConstructor().newInstance();
      initAlchimie(t, raw);
      t.widerstand = _extractWiderstand(raw.widerstand);
      t.isStufeQs = raw.stufe.equalsIgnoreCase("qs");
      if (!t.isStufeQs && raw.stufe != null && !raw.stufe.isEmpty()) t.stufe = Integer.valueOf(raw.stufe);
      t.beginn = ExtractorSkillDuration.retrieveDurationFromRaw(raw.beginn);
      t.dauer = ExtractorSkillDuration.retrieveDurationFromRaw(raw.dauer);
      t.wirkung = new AlchimieWirkung();
      _applyGiftTypeAndVektor(raw.art, t);

      return t;
    }
    catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException("Cannot create instance of " + clazz.getName(), e);
    }

  }

  private static AttributeShort _extractWiderstand(String raw) {
    return switch (raw.toLowerCase()) {
      case "zk", "zähigkeit" -> AttributeShort.ZK;
      case "sk", "seelenkraft" -> AttributeShort.SK;
      default -> null;
    };
  }

  private static void _applyGiftTypeAndVektor(String raw, GiftSO gift) {
    String normalized = raw
        .toLowerCase()
        .replace("ä", "ae")
        .replace("ö", "oe")
        .replace("ü", "ue")
        .replace("ß", "ss");

    // -------------------------
    // GiftTypeKey (Art)
    // -------------------------
    for (GiftTypeKey type : GiftTypeKey.values()) {
      if (normalized.contains(type.name())) {
        gift.artKey = type;
        break;
      }
    }

    // -------------------------
    // GiftVektoren
    // -------------------------
    EnumSet<GiftVektorKey> vectors = EnumSet.noneOf(GiftVektorKey.class);

    if (normalized.contains("atem")) {
      vectors.add(GiftVektorKey.atemgift);
    }
    if (normalized.contains("einnahme")) {
      vectors.add(GiftVektorKey.einnahmegift);
    }
    if (normalized.contains("kontakt")) {
      vectors.add(GiftVektorKey.kontaktgift);
    }
    if (normalized.contains("waffen")) {
      vectors.add(GiftVektorKey.waffengift);
    }

    gift.vektor = new ArrayList<>(vectors);
  }
}
