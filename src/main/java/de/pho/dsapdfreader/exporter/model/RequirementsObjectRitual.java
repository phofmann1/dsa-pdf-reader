package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.LogicalOperatorKey;
import de.pho.dsapdfreader.exporter.model.enums.ObjectRitualKey;

import java.util.ArrayList;
import java.util.List;

public class RequirementsObjectRitual
{
  public LogicalOperatorKey logicalOpperator = LogicalOperatorKey.and;
  public List<ObjectRitualKey> requiredKeys = new ArrayList<>();
  public RequirementsObjectRitual childs;
}
