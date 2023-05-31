package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.LogicalOperatorKey;

public class RequirementsCombatSkill
{
  public LogicalOperatorKey logicalOperator = LogicalOperatorKey.and;
  public List<RequirementCombatSkill> requirements = new ArrayList<>();
  public RequirementsSkill childs;
}
