package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EffectCategoryKey {
    heilung,
    gift, zustand, magisch, karmal, sinnesveränderung, kampf, körperlich, geistig, alchemisch;
    @JsonValue
    public int toValue()
    {
        return ordinal();
    }

}


