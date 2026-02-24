package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.enums.LaborKey;
import de.pho.dsapdfreader.exporter.model.enums.RezeptKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;

public class Rezept {
    public String name;
    public String uid;
    public RezeptKey key;
    public SkillKey skillKey;
    public Integer difficulty;
    public SkillUsageKey skillUsageKey;
    public List<EquipmentKey> ingredients;
    public List<EquipmentKey> tools;
    public LaborKey workshop;

    public Rezept() {
    }

    public Rezept(String name, SkillKey skillKey, Integer difficulty) {
        this.key = Extractor.extractEnumKey(name, RezeptKey.class);
        this.uid = "rezept_" + this.key;
        this.name = name;
        this.skillKey = skillKey;
        this.difficulty = difficulty;
    }
}
