package de.pho.dsapdfreader.exporter.model.enums;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SkillKey
{

    fliegen,
    gaukeleien,
    klettern,
    körperbeherrschung,
    kraftakt,
    reiten,
    schwimmen,
    selbstbeherrschung,
    singen,
    sinnesschärfe,
    tanzen,
    taschendiebstahl,
    verbergen,
    zechen,

    bekehren_und_überzeugen,
    betören,
    einschüchtern,
    etikette,
    gassenwissen,
    menschenkenntnis,
    überreden,
    verkleiden,
    willenskraft,
    fährtensuchen,
    fesseln,
    fischen_und_angeln,
    orientierung,
    pflanzenkunde,
    tierkunde,
    wildnisleben,
    brett_und_glücksspiel,
    geographie,
    geschichtswissen,
    götter_und_kulte,
    kriegskunst,
    magiekunde,
    mechanik,
    rechnen,
    rechtskunde,
    sagen_und_legenden,
    sphärenkunde,
    sternkunde,
    alchimie,
    boote_und_schiffe,
    fahrzeuge,
    handel,
    heilkunde_gift,
    heilkunde_krankheiten,
    heilkunde_seele,
    heilkunde_wunden,
    holzbearbeitung,
    lebensmittelbearbeitung,
    lederbearbeitung,
    malen_und_zeichnen,
    metallbearbeitung,
    musizieren,
    schlösserknacken,
    steinbearbeitung,
    stoffbearbeitung,
    ;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }

    public static Optional<SkillKey> fromString(String str)
    {
        String cleanName = str.toUpperCase()
            .replace("-", "")
            .replace(" ", "_")
            .replace("&", "UND");
        return Arrays.stream(SkillKey.values()).filter(msv -> msv.name().equalsIgnoreCase(cleanName)).findFirst();
    }
}
