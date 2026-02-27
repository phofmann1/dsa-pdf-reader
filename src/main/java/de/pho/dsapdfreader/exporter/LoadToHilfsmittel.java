package de.pho.dsapdfreader.exporter;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.model.AlchimieRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.enums.HilfsmittelKey;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.HilfsmittelSO;


public class LoadToHilfsmittel extends LoadToAlchimieA {
  protected static final Logger LOGGER = LogManager.getLogger();

  private LoadToHilfsmittel() {
  }

  public static HilfsmittelSO migrate(AlchimieRaw raw) {

    HilfsmittelSO hm = new HilfsmittelSO();
    hm.key = Extractor.extractEnumKey(raw.name.replace("1001", "Tausend und ein "), HilfsmittelKey.class);
    hm.name = raw.name;
    hm.beschreibung = raw.description;
    hm.zutaten = Arrays.stream(raw.typicalIngredients.split(",")).map(String::trim).toList();
    if (raw.brewingDifficulty != null && !raw.brewingDifficulty.isEmpty()) {
      hm.verarbeitungsschwierigkeit = Integer.valueOf(raw.brewingDifficulty.replace("–", "-").replaceAll("\\+/- ?0", "0"));
    }
    hm.typischeHilfsmittel = Arrays.stream(raw.requirements.split(",")).map(String::trim).filter(req -> req != null && !req.isEmpty()).toList();
    return hm;
  }
}
