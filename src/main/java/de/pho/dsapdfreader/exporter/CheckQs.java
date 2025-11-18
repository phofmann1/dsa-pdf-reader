package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.exporter.model.enums.SkillKey;

import java.util.ArrayList;
import java.util.List;

public class CheckQs {

    public SkillKey skill;
    public List<QualityEntry> qualities = new ArrayList<>();

    public CheckQs(SkillKey skill) {
        this.skill = skill;
    }

    @Override
    public String toString() {
        return skill + " -> " + qualities;
    }
}
