package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CultureKey
{
    ANDERGASTER,
    ARANIER,
    BORNLAENDER,
    FERKINAS,
    FJARNINGER,
    GJALSKER,
    HORASIER,
    KOBOLDWELTLER,
    MARASKANER,
    MHANADISTANI,
    MITTELREICHER,
    MOHAS,
    NIVESEN,
    NORBARDEN,
    NORDAVENTURIER,
    NOSTRIER,
    NOVADIS,
    SUEDAVENTURIER,
    SVELLTTALER,
    THORWALER,
    TROLLZACKER,
    ZAHORI,
    ZYKLOPAEER,
    AUELFEN,
    FIRNELFEN,
    STEPPENELFEN,
    WALDELFEN,
    AMBOSSZWERGE,
    BRILLANTZWERGE,
    ERZZWERGE,
    HUEGELZWERGE,
    WILDZWERGE;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}
