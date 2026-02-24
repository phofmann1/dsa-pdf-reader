package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DrugKey {
    aufputschmittel,
    drachentrunk,
    dreh_oel,
    feuerschlick_essenz,
    feuerschlick_pulver,
    katzenaugensalbe,
    regenbogenstaub,
    traumsand;

    @JsonValue
    public int toValue() {
        return ordinal();
    }


}
