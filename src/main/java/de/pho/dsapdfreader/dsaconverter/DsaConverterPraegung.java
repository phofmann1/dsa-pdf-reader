package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.PraegungRaw;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterPraegung {
  public static List<PraegungRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf) {
    List<PraegungRaw> returnValue = new ArrayList<>();

    resultList
            .forEach(t -> {

              String cleanText = t.text
                      .trim();
            });
    return returnValue;
  }
}
