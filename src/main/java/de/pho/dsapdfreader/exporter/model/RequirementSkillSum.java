package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.SkillKey;

public class RequirementSkillSum
{
  public List<SkillKey> skillKey = new ArrayList<>();
  public int minSum;
}
