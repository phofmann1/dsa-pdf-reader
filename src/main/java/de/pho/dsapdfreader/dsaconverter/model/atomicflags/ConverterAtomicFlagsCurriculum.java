package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsCurriculum implements ConverterAtomicFlagsI {
  public AtomicBoolean wasName;
  public AtomicBoolean wasGuideline;
  public AtomicBoolean wasSpellSelection;
  public AtomicBoolean wasSpellRestriction;
  public AtomicBoolean wasPathName_I;
  public AtomicBoolean wasSpellChanges_I;
  public AtomicBoolean wasAdditionalSkills_I;
  public AtomicBoolean wasRemovedSkills_I;
  public AtomicBoolean wasPathName_II;
  public AtomicBoolean wasSpellChanges_II;
  public AtomicBoolean wasAdditionalSkills_II;
  public AtomicBoolean wasRemovedSkills_II;

  public ConverterAtomicFlagsCurriculum() {
    initDataFlags();
  }

  @Override
  public AtomicBoolean getFirstFlag() {
    return wasName;
  }

  @Override
  public void initDataFlags() {
    wasName = new AtomicBoolean(Boolean.FALSE);
    wasGuideline = new AtomicBoolean(Boolean.FALSE);
    wasSpellSelection = new AtomicBoolean(Boolean.FALSE);
    wasSpellRestriction = new AtomicBoolean(Boolean.FALSE);
    wasPathName_I = new AtomicBoolean(Boolean.FALSE);
    wasSpellChanges_I = new AtomicBoolean(Boolean.FALSE);
    wasAdditionalSkills_I = new AtomicBoolean(Boolean.FALSE);
    wasRemovedSkills_I = new AtomicBoolean(Boolean.FALSE);
    wasPathName_II = new AtomicBoolean(Boolean.FALSE);
    wasSpellChanges_II = new AtomicBoolean(Boolean.FALSE);
    wasAdditionalSkills_II = new AtomicBoolean(Boolean.FALSE);
    wasRemovedSkills_II = new AtomicBoolean(Boolean.FALSE);
  }
}
