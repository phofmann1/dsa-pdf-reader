package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.LogicalOperatorKey;

public class RequirementsAttribute
{
  public LogicalOperatorKey logicalOpperator;
  public RequirementAttribute[] requirements;
  public RequirementsAttribute childs;
}
