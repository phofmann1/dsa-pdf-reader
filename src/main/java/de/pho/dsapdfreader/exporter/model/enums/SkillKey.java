package de.pho.dsapdfreader.exporter.model.enums;

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

    public static SkillKey fromString(String str)
    {
        for (SkillKey e : SkillKey.values())
        {
            if (e.name().equalsIgnoreCase(str.toLowerCase().replace(" ", "_")
                .replace("&", "und")))
            {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + str);
    }
}
