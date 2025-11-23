package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.QsResult;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;

public class CheckQs {

    public CheckTopic checkTopic;

    public String subject;

    public SkillKey skill;
    public List<QsResult> qualities = new ArrayList<>();

    public CheckQs(SkillKey skill) {
        this.skill = skill;
    }

    public CheckQs(CheckTopic checkTopic, String subject, SkillKey skill) {
        this(skill);
        this.subject = subject;
        this.checkTopic = checkTopic;
    }

    @Override
    public String toString() {
        return skill + " -> " + qualities;
    }
}


