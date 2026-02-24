package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.AlchimieRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.*;
import de.pho.dsapdfreader.exporter.model.enums.*;
import de.pho.dsapdfreader.tools.merger.ObjectMerger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;


public class LoadToElixier extends LoadToAlchimieA {
  protected static final Logger LOGGER = LogManager.getLogger();

  private LoadToElixier() {
  }

  public static Elixier migrate(AlchimieRaw raw) {

    Elixier elixier = iniAlchimie(new Elixier(), raw);
    elixier.key = extractEnumKey(
            elixier.name.replace("1001", "Tausend und ein "),
            ElixierKey.class
    );
    elixier.wirkung = extractWirkung(raw.qualityLevels);
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
