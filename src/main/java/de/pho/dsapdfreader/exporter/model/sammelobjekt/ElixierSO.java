package de.pho.dsapdfreader.exporter.model.sammelobjekt;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.QSEntry;
import de.pho.dsapdfreader.exporter.model.enums.ElixierKey;

public class ElixierSO extends AlchimieA {
  public ElixierKey key;
  public List<QSEntry> wirkung;

  public QSEntry hyperpotenteWirkung;
}
