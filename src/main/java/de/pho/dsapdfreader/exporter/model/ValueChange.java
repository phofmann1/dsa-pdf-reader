package de.pho.dsapdfreader.exporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.ValueChangeKey;
import de.pho.dsapdfreader.exporter.model.enums.ValueChangeType;

public class ValueChange
{
    public ValueChangeKey key;
    public ValueChangeType type;
    @JsonProperty("valueChange")
    public int change;
    public int value;

    public SkillKey skillKey;
    public CombatSkillKey combatSkillKey;

    public boolean perLevel;
    public boolean temporary;
}
