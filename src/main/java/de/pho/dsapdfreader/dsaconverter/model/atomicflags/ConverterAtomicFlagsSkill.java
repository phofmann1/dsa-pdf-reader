package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsSkill implements ConverterAtomicFlagsI
{
  public AtomicBoolean wasName;
  public AtomicBoolean wasCheck;
  public AtomicBoolean wasDescription;

  public AtomicBoolean wasApplication;
  public AtomicBoolean wasEncumbrance;
  public AtomicBoolean wasTools;
  public AtomicBoolean wasQuality;
  public AtomicBoolean wasFailure;
  public AtomicBoolean wasCriticalSuccess;
  public AtomicBoolean wasCriticalFailure;
  public AtomicBoolean wasAdvancementCategory;
  public AtomicBoolean wasNewApplication;

  public ConverterAtomicFlagsSkill()
  {
    this.initDataFlags();
  }

  public void initDataFlags()
  {
    this.wasName = new AtomicBoolean(Boolean.FALSE);
    this.wasCheck = new AtomicBoolean(Boolean.FALSE);
    this.wasDescription = new AtomicBoolean(Boolean.FALSE);
    this.wasApplication = new AtomicBoolean(Boolean.FALSE);
    this.wasEncumbrance = new AtomicBoolean(Boolean.FALSE);
    this.wasTools = new AtomicBoolean(Boolean.FALSE);
    this.wasQuality = new AtomicBoolean(Boolean.FALSE);
    this.wasFailure = new AtomicBoolean(Boolean.FALSE);
    this.wasCriticalSuccess = new AtomicBoolean(Boolean.FALSE);
    this.wasCriticalFailure = new AtomicBoolean(Boolean.FALSE);
    this.wasAdvancementCategory = new AtomicBoolean(Boolean.FALSE);
    this.wasNewApplication = new AtomicBoolean(Boolean.FALSE);
  }

  @Override
  public AtomicBoolean getFirstFlag()
  {
    return this.wasName;
  }
}
