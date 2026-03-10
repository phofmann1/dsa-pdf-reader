package de.pho.dsapdfreader.exporter.model.sammelobjekt;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.PflanzlichesHilfsmittelKey;

public class HilfsmittelSO {
  public PflanzlichesHilfsmittelKey key;
  public String name;
  public String beschreibung; //SPRACHINHALT
  public List<String> zutaten; //REZEPT
  public List<String> typischeHilfsmittel; //REZEPT
  public Integer verarbeitungsschwierigkeit;

  public HilfsmittelSO() {
  }
}
