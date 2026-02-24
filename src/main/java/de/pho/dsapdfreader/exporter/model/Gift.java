package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.PoisonKey;

import java.util.List;

public class Gift extends AlchimieA {
    public PoisonKey key;
    public AttributeShort widerstand;
    public List<String> art;
    public String stufe;
    public Duration beginn;
    public Duration dauer;
    public AlchimieWirkung wirkung;
}
