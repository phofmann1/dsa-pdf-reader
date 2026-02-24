package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.AlchimieRaw;
import de.pho.dsapdfreader.exporter.model.Droge;
import de.pho.dsapdfreader.exporter.model.Gift;
import de.pho.dsapdfreader.exporter.model.QSEntry;
import de.pho.dsapdfreader.exporter.model.enums.DrugKey;
import de.pho.dsapdfreader.exporter.model.enums.PoisonKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;


public class LoadToDroge extends LoadToGift {
  protected static final Logger LOGGER = LogManager.getLogger();

  private LoadToDroge() {
  }

  public static Droge migrate(AlchimieRaw raw) {
      Droge droge = LoadToGift._init(raw, Droge.class);
      droge.key = extractEnumKey(droge.name, DrugKey.class);
      droge.einnahme = raw.einnahme;
      droge.legalitaet = raw.legalitaet;
      droge.nebenwirkung = raw.nebenwirkung;
      droge.sucht = raw.sucht;
      droge.ueberdosierung = raw.ueberdosierung;
      return droge;
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
