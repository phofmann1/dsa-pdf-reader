package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.LogicalOperatorKey;

public class RequirementsSkill
{
  public LogicalOperatorKey logicalOpperator;
  public List<RequirementSkill> requirements = new ArrayList<>();
  public RequirementsSkill childs;

}
