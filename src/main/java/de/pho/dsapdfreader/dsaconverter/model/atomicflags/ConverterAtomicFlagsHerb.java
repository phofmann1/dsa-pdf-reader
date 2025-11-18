package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsHerb implements ConverterAtomicFlagsI {

    public AtomicBoolean wasName;
    public AtomicBoolean wasAlternativeNamen;
    public AtomicBoolean wasVerbreitung;
    public AtomicBoolean wasVerbreitungLandschaftstyp;
    public AtomicBoolean wasVerbreitungRegionen;
    public AtomicBoolean wasSuchschwierigkeit;
    public AtomicBoolean wasBestimmungsschwierigkeit;
    public AtomicBoolean wasAnwendungen;
    public AtomicBoolean wasWirkung;
    public AtomicBoolean wasPreis;
    public AtomicBoolean wasRezepte;
    public AtomicBoolean wasAlltagsarzneienUndVolksbrauchtum;
    public AtomicBoolean wasHaltbarkeit;
    public AtomicBoolean wasWissenstalent;

    public ConverterAtomicFlagsHerb() {
        initDataFlags();
    }

    @Override
    public AtomicBoolean getFirstFlag() {
        return wasName;
    }

    @Override
    public void initDataFlags() {
        wasName = new AtomicBoolean(false);
        wasAlternativeNamen = new AtomicBoolean(false);
        wasVerbreitung = new AtomicBoolean(false);
        wasVerbreitungLandschaftstyp = new AtomicBoolean(false);
        wasVerbreitungRegionen = new AtomicBoolean(false);
        wasSuchschwierigkeit = new AtomicBoolean(false);
        wasBestimmungsschwierigkeit = new AtomicBoolean(false);
        wasAnwendungen = new AtomicBoolean(false);
        wasWirkung = new AtomicBoolean(false);
        wasPreis = new AtomicBoolean(false);
        wasRezepte = new AtomicBoolean(false);
        wasAlltagsarzneienUndVolksbrauchtum = new AtomicBoolean(false);
        wasHaltbarkeit = new AtomicBoolean(false);
        wasWissenstalent = new AtomicBoolean(false);
    }
}
