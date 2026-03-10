package de.pho.dsapdfreader.exporter.model.sammelobjekt;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.Duration;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.GiftKey;
import de.pho.dsapdfreader.exporter.model.enums.GiftTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.GiftVektorKey;

public class GiftSO extends AlchimieA {
    public GiftKey key;
    public AttributeShort widerstand;
    public boolean isStufeQs;
    public Integer stufe;
    public Duration beginn;
    public Duration dauer;
    public AlchimieWirkung wirkung;

    public GiftTypeKey artKey;
    public List<GiftVektorKey> vektor;
}
