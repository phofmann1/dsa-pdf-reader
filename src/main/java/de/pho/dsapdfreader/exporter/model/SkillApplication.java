package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.SkillApplicationKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;

public class SkillApplication
{
  public String name;
  public SkillApplicationKey key;
  public SkillKey skillKey;
  public List<SkillUsageKey> skillUsages = new ArrayList<>();
}
