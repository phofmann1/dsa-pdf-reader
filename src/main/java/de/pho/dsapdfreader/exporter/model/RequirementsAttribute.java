package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.LogicalOperatorKey;

public class RequirementsAttribute
{
  public LogicalOperatorKey logicalOpperator;
  public List<RequirementAttribute> requirements = new ArrayList<>();
  public RequirementsAttribute childs;
}
