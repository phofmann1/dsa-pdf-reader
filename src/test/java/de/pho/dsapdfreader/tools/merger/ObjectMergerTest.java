package de.pho.dsapdfreader.tools.merger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import de.pho.dsapdfreader.exporter.model.SkillUsage;
import de.pho.dsapdfreader.exporter.model.SpecialAbility;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;

class ObjectMergerTest
{

  @Test
  void merge()
  {
    SpecialAbility source = new SpecialAbility();
    source.name = "source";
    source.newSkillUsage = new SkillUsage();
    source.newSkillUsage.usageKey = SkillUsageKey.ablenkungen;
    source.newSkillUsage.name = "sourceApplication";
    source.newSkillUsage.skillKeys = new ArrayList<>();
    source.newSkillUsage.skillKeys.add(SkillKey.alchimie);
    source.ap = 23;
    SpecialAbility target = new SpecialAbility();
    target.name = "target";
    target.newSkillUsage = new SkillUsage();
    target.newSkillUsage.name = "targetApplication";

    ObjectMerger.merge(source, target);

    assertEquals(23, target.ap);
    assertEquals("target", target.name);
    assertNotNull(target.newSkillUsage);
    assertEquals("targetApplication", target.newSkillUsage.name);
    assertEquals(SkillUsageKey.ablenkungen, target.newSkillUsage.usageKey);
    assertNotNull(target.newSkillUsage.skillKeys);
    assertEquals(SkillKey.alchimie, target.newSkillUsage.skillKeys.get(0));
  }


}