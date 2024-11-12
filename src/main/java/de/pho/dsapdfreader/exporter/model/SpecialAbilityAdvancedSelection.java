package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;

public class SpecialAbilityAdvancedSelection {
  public List<SpecialAbilityKey> advancedAbilityKeys;
  public List<SpecialAbilityOption> advancedAbilityKeyOptions = new ArrayList<>();

  public SpecialAbilityAdvancedSelection() {
  }
}
