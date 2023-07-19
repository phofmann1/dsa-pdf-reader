package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.AdvancementCategory;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.ConditionalBoolean;
import de.pho.dsapdfreader.exporter.model.enums.SkillApplicationKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;

public class Skill
{
  public String name;
  public SkillKey key;
  public SkillCategoryKey skillCategory;
  public AdvancementCategory advancementCategory;
  public List<AttributeShort> check;
  public ConditionalBoolean isEncumbered;
  public List<SkillUsageKey> skillUsageKeys;
  public List<SkillUsageKey> additionalUsageKeys;

  public List<SkillApplicationKey> applicationKeys;
}

