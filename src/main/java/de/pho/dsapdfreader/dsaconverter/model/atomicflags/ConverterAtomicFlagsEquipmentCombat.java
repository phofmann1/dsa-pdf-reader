package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsEquipmentCombat implements ConverterAtomicFlagsI {
  public AtomicBoolean wasName;
  public AtomicBoolean wasCombatSkill;
  public AtomicBoolean wasData;
  public AtomicBoolean wasRemark;
  public AtomicBoolean wasAdvantage;
  public AtomicBoolean wasDisadvantage;
  public AtomicBoolean wasComplexity;

  public AtomicBoolean isFirstValue;
  public AtomicBoolean wasFinished;

  public ConverterAtomicFlagsEquipmentCombat() {
    initDataFlags();
  }

  @Override
  public AtomicBoolean getFirstFlag() {
    return wasName;
  }

  @Override
  public void initDataFlags() {
    wasName = new AtomicBoolean(false);
    wasData = new AtomicBoolean(false);
    wasRemark = new AtomicBoolean(false);
    wasAdvantage = new AtomicBoolean(false);
    wasDisadvantage = new AtomicBoolean(false);
    wasCombatSkill = new AtomicBoolean(false);
    wasComplexity = new AtomicBoolean(false);
    isFirstValue = new AtomicBoolean(true);
    wasFinished = new AtomicBoolean(false);
  }

  public AtomicBoolean getWasEndOfEntry() {
    return new AtomicBoolean(wasCombatSkill.get() || wasDisadvantage.get() || wasFinished.get());
  }

}
