package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsEquipment implements ConverterAtomicFlagsI
{
  public AtomicBoolean wasName;
  public AtomicBoolean wasCategory;
  public AtomicBoolean wasData;
  public AtomicBoolean isFirstValue;
  public AtomicBoolean wasRemark;

  public ConverterAtomicFlagsEquipment()
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
    wasData = new AtomicBoolean(false);
    wasCategory = new AtomicBoolean(false);
    wasRemark = new AtomicBoolean(false);
    isFirstValue = new AtomicBoolean(true);
  }
}
