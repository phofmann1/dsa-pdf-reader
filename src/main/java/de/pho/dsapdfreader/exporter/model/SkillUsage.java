package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;

public class SkillUsage
{
  public String name;
  public SkillUsageKey usageKey;
  public List<SkillKey> skillKeys = new ArrayList<>();
}
