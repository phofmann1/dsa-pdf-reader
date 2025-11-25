package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsTavern implements ConverterAtomicFlagsI {

    public AtomicBoolean wasName;
    public AtomicBoolean wasDescription;

    public ConverterAtomicFlagsTavern() {
        initDataFlags();
    }

    @Override
    public AtomicBoolean getFirstFlag() {
        return wasName;
    }

    @Override
    public void initDataFlags() {
        wasName = new AtomicBoolean(false);
        wasDescription = new AtomicBoolean(false);
    }
}
