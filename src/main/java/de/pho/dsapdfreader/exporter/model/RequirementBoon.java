package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.BoonKey;

public class RequirementBoon
{
  public BoonKey key;
  public boolean exists;
  public String variant;
  public int level;

  public RequirementBoon()
  {
  }

  public RequirementBoon(BoonKey key, boolean exists)
  {
    this.key = key;
    this.exists = exists;
  }


  public RequirementBoon(BoonKey key, boolean exists, String variant)
  {
    this.key = key;
    this.exists = exists;
    this.variant = variant;
  }
}
