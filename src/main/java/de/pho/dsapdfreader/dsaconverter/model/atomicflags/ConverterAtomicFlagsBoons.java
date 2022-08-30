package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsBoons implements ConverterAtomicFlagsI
{
  public AtomicBoolean wasName;
  public AtomicBoolean wasDescription;
  public AtomicBoolean wasRules;
  public AtomicBoolean wasPrecondition;
  public AtomicBoolean wasApValue;

  public ConverterAtomicFlagsBoons()
  {
    initDataFlags();
  }

  @Override
  public AtomicBoolean getFirstFlag()
  {
    return wasName;
  }

  @Override
  public void initDataFlags()
  {
    wasName = new AtomicBoolean(false);
    wasDescription = new AtomicBoolean(false);
    wasRules = new AtomicBoolean(false);
    wasPrecondition = new AtomicBoolean(false);
    wasApValue = new AtomicBoolean(false);
  }
}
