package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public interface ConverterAtomicFlagsI
{
  AtomicBoolean getFirstFlag();

  void initDataFlags();
}
