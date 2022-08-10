package de.pho.dsapdfreader.exporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.pho.dsapdfreader.exporter.model.enums.Unit;

public class CastingDuration
{
    @JsonProperty("castingDuration")
    public int duration;
    public Unit castingDurationUnit;
    public String castingDurationSpecial;
}
