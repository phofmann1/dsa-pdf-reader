package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.ObjectRitualKey;

import java.io.Serializable;

public class RequirementObjectRitual implements Serializable {
  public ObjectRitualKey ork;

  public RequirementObjectRitual() {
  }

  public RequirementObjectRitual(ObjectRitualKey key) {
    this.ork = key;
  }
}