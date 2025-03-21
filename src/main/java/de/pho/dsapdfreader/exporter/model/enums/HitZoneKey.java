package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HitZoneKey {
    head,
    body,
    arm_left,
    arm_right,
    leg_left,
    leg_right;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
