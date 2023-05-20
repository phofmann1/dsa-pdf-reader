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
    source.skillUsage = new SkillUsage();
    source.skillUsage.usageKey = SkillUsageKey.ablenkungen;
    source.skillUsage.name = "sourceApplication";
    source.skillUsage.skillKeys = new ArrayList<>();
    source.skillUsage.skillKeys.add(SkillKey.alchimie);
    source.ap = 23;
    SpecialAbility target = new SpecialAbility();
    target.name = "target";
    target.skillUsage = new SkillUsage();
    target.skillUsage.name = "targetApplication";

    ObjectMerger.merge(source, target);

    assertEquals(23, target.ap);
    assertEquals("target", target.name);
    assertNotNull(target.skillUsage);
    assertEquals("targetApplication", target.skillUsage.name);
    assertEquals(SkillUsageKey.ablenkungen, target.skillUsage.usageKey);
    assertNotNull(target.skillUsage.skillKeys);
    assertEquals(SkillKey.alchimie, target.skillUsage.skillKeys.get(0));
  }


}