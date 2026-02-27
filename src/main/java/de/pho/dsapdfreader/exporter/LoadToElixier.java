package de.pho.dsapdfreader.exporter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.model.AlchimieRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.QSEntry;
import de.pho.dsapdfreader.exporter.model.enums.ElixierKey;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.ElixierSO;


public class LoadToElixier extends LoadToAlchimieA {
  protected static final Logger LOGGER = LogManager.getLogger();

  private LoadToElixier() {
  }

  public static ElixierSO migrate(AlchimieRaw raw) {

    ElixierSO elixier = initAlchimie(new ElixierSO(), raw);
    elixier.key = Extractor.extractEnumKey(
        elixier.name.replace("1001", "Tausend und ein "),
        ElixierKey.class
    );
    elixier.wirkung = extractWirkung(raw.qualityLevels);
    if (raw.hyperpotenteWirkung != null && !raw.hyperpotenteWirkung.isEmpty()) {
      elixier.hyperpotenteWirkung = new QSEntry();
      elixier.hyperpotenteWirkung.qs = 7;
      elixier.hyperpotenteWirkung.information = raw.hyperpotenteWirkung;
    }
    return elixier;
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
