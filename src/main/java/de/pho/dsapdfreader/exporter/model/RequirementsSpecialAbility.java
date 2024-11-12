package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.LogicalOperatorKey;

public class RequirementsSpecialAbility
{
  public LogicalOperatorKey logicalOpperator = LogicalOperatorKey.and;
  public List<RequirementSpecialAbility> requirements = new ArrayList<>();
  public RequirementsSpecialAbility childs;
}
