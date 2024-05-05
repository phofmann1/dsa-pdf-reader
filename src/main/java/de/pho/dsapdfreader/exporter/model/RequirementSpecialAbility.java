package de.pho.dsapdfreader.exporter.model;

import java.io.Serializable;

import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;

public class RequirementSpecialAbility implements Serializable {
  public SpecialAbilityKey abilityKey;
  public String variant;

  public RequirementSpecialAbility() {
  }

  public RequirementSpecialAbility(SpecialAbilityKey key, String variantString) {
    this.abilityKey = key;
    this.variant = variantString;
  }
}