package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SkillKey
{
    FLIEGEN,
    GAUKELEIEN,
    KLETTERN,
    KOERPERBEHERRSCHUNG,
    KRAFTAKT,
    REITEN,
    SCHWIMMEN,
    SELBSTBEHERRSCHUNG,
    SINGEN,
    SINNESSCHAERFE,
    TANZEN,
    TASCHENDIEBSTAHL,
    VERBERGEN,
    ZECHEN,

    BEKEHREN_UND_UEBERZEUGEN,
    BETOEREN,
    EINSCHUECHTERN,
    ETIKETTE,
    GASSENWISSEN,
    MENSCHENKENNTNIS,
    UEBERREDEN,
    VERKLEIDEN,
    WILLENSKRAFT,
    FAEHRTENSUCHEN,
    FESSELN,
    FISCHEN_UND_ANGELN,
    ORIENTIERUNG,
    PFLANZENKUNDE,
    TIERKUNDE,
    WILDNISLEBEN,
    BRETT_UND_GLUECKSSPIEL,
    GEOGRAPHIE,
    GESCHICHTSWISSEN,
    GOETTER_UND_KULTE,
    KRIEGSKUNST,
    MAGIEKUNDE,
    MECHANIK,
    RECHNEN,
    RECHTSKUNDE,
    SAGEN_UND_LEGENDEN,
    SPHAERENKUNDE,
    STERNKUNDE,
    ALCHIMIE,
    BOOTE_UND_SCHIFFE,
    FAHRZEUGE,
    HANDEL,
    HEILKUNDE_GIFT,
    HEILKUNDE_KRANKHEITEN,
    HEILKUNDE_SEELE,
    HEILKUNDE_WUNDEN,
    HOLZBEARBEITUNG,
    LEBENSMITTELBEARBEITUNG,
    LEDERBEARBEITUNG,
    MALEN_UND_ZEICHNEN,
    METALLBEARBEITUNG,
    MUSIZIEREN,
    SCHLOESSERKNACKEN,
    STEINBEARBEITUNG,
    STOFFBEARBEITUNG;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }
}