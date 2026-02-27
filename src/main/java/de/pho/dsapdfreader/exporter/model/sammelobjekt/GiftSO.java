package de.pho.dsapdfreader.exporter.model.sammelobjekt;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.Duration;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.PoisonKey;

public class GiftSO extends AlchimieA {
    public PoisonKey key;
    public AttributeShort widerstand;
  public List<String> vektor;
  public String stufe;
    public Duration beginn;
    public Duration dauer;
    public AlchimieWirkung wirkung;
}
