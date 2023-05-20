package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsWeapon extends ConverterAtomicFlagsEquipmentCombat
{
  public AtomicBoolean wasFirstValue = new AtomicBoolean(false);

  public ConverterAtomicFlagsWeapon()
  {
    initDataFlags();
  }

  @Override
  public AtomicBoolean getWasEndOfEntry()
  {
    return new AtomicBoolean(wasCombatSkill.get() || wasDisadvantage.get() || none());
  }

  @Override
  public AtomicBoolean getFirstFlag()
  {
    return this.wasFirstValue;
  }

  public boolean none()
  {
    return !wasName.get()
        && !wasCombatSkill.get()
        && !wasAdvantage.get()
        && !wasDisadvantage.get()
        && !wasComplexity.get()
        && !wasData.get()
        && !wasRemark.get();
  }
}
