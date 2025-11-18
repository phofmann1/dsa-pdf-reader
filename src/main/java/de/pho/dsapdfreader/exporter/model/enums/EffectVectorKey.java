package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EffectVectorKey {
    beruehrung,
    einatmung,
    verzehr;
    @JsonValue
    public int toValue()
    {
        return ordinal();
    }

}
