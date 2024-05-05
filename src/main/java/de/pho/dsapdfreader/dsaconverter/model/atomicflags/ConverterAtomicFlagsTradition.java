package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsTradition implements ConverterAtomicFlagsI {
  public AtomicBoolean wasName;
  public AtomicBoolean wasDescription;
  public AtomicBoolean wasPreconditions;
  public AtomicBoolean wasAp;

  public ConverterAtomicFlagsTradition() {
    this.initDataFlags();
  }

  public void initDataFlags() {
    this.wasName = new AtomicBoolean(Boolean.FALSE);
    this.wasDescription = new AtomicBoolean(Boolean.FALSE);
    this.wasPreconditions = new AtomicBoolean(Boolean.FALSE);
    this.wasAp = new AtomicBoolean(Boolean.FALSE);
  }

  @Override
  public AtomicBoolean getFirstFlag() {
    return this.wasName;
  }
}
