package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.AlchimieRaw;
import de.pho.dsapdfreader.exporter.model.*;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.ElixierKey;
import de.pho.dsapdfreader.exporter.model.enums.PoisonKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;


public class LoadToGift extends LoadToAlchimieA {
  protected static final Logger LOGGER = LogManager.getLogger();

  protected LoadToGift() {
  }

  public static Gift migrate(AlchimieRaw raw) {
      Gift gift = _init(raw, Gift.class);
      gift.key = extractEnumKey(gift.name, PoisonKey.class);
      return gift;
  }

  protected static <T extends  AlchimieA> T _init(AlchimieRaw raw, Class<T>  clazz) {
      try {
          T t = clazz.getDeclaredConstructor().newInstance();
          iniAlchimie(t, raw);
          //gift.widerstand;
          //gift.art;
          //gift.stufe;
          //gift.beginn;
          //gift.dauer;
          //gift.wirkung;
          return t;
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          throw new RuntimeException("Cannot create instance of " + clazz.getName(), e);
      }

  }

  private static List<QSEntry> extractWirkung(String qualityLevels) {
    List<String> parts = Arrays.stream(qualityLevels
            .replaceAll("^\\d:", "")
            .split("[1-6]:")).filter(s ->  s != null && !s.isEmpty()).toList();

    return IntStream.range(0, parts.size())
            .mapToObj(i -> {
              QSEntry entry = new QSEntry();
              entry.qs = i + 1; // because QS starts at 1
              entry.information = parts.get(i).trim();
              return entry;
            })
            .toList();
  }
}
