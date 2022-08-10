package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TraditionSubKey
{
    NONE,
    CEOLADIR,
    DERWISCH,
    HAZAQI,
    MAJUNA,
    RAHKISA,
    SANGARA,
    SHARISAD,
    BLOODWARRIOR,
    FERKINA,
    FJARNINGER,
    GJALSKER,
    GOBLIN,
    MOHA,
    NIVESE,
    TROLLZACKER;


    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
