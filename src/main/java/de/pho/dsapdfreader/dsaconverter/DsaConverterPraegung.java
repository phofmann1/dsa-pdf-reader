package de.pho.dsapdfreader.dsaconverter;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.PraegungRaw;
import de.pho.dsapdfreader.dsaconverter.model.SpecialAbilityRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCultureKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorRegionKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSpecieKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorWeaponKey;
import de.pho.dsapdfreader.exporter.model.AvailabilityWeapon;
import de.pho.dsapdfreader.exporter.model.enums.*;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DsaConverterPraegung {
  public static List<PraegungRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf) {
    List<PraegungRaw> returnValue = new ArrayList<>();

    System.out.println(resultList);

    resultList
            .forEach(t -> {

              String cleanText = t.text
                      .trim();
              System.out.println(cleanText);
            });
    return returnValue;
  }
}
