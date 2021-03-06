package de.pho.dsapdfreader.dsaconverter;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicConverterFlag
{
    public AtomicBoolean wasStarted;
    public AtomicBoolean wasName;
    public AtomicBoolean wasFeature;
    public AtomicBoolean wasDuration;
    public AtomicBoolean wasRemarks;
    public AtomicBoolean wasTargetCategory;
    public AtomicBoolean wasRange;
    public AtomicBoolean wasFinished;
    public AtomicBoolean wasCheck;
    public AtomicBoolean wasEffect;
    public AtomicBoolean wasCastingDuration;
    public AtomicBoolean wasCost;
    public AtomicBoolean wasCommonness;
    public AtomicBoolean wasAdvancementCategory;
    public AtomicBoolean wasQs1;
    public AtomicBoolean wasQs2;
    public AtomicBoolean wasQs3;
    public AtomicBoolean wasQs4;
    public AtomicBoolean wasQs5;
    public AtomicBoolean wasQs6;
    public AtomicBoolean wasFurtherInformation;
    public AtomicBoolean wasVariants;
    public AtomicBoolean wasVariant1;
    public AtomicBoolean wasVariant2;
    public AtomicBoolean wasVariant3;

    public AtomicConverterFlag()
    {
        this.wasStarted = new AtomicBoolean(false);
        this.initDataFlags();
        this.wasFinished = new AtomicBoolean(false);
    }

    public void initDataFlags()
    {
        this.wasName = new AtomicBoolean(false);
        this.wasFeature = new AtomicBoolean(false);
        this.wasDuration = new AtomicBoolean(false);
        this.wasRemarks = new AtomicBoolean(false);
        this.wasTargetCategory = new AtomicBoolean(false);
        this.wasRange = new AtomicBoolean(false);
        this.wasCheck = new AtomicBoolean(false);
        this.wasEffect = new AtomicBoolean(false);
        this.wasCastingDuration = new AtomicBoolean(false);
        this.wasCost = new AtomicBoolean(false);
        this.wasCommonness = new AtomicBoolean(false);
        this.wasAdvancementCategory = new AtomicBoolean(false);
        this.wasQs1 = new AtomicBoolean(false);
        this.wasQs2 = new AtomicBoolean(false);
        this.wasQs3 = new AtomicBoolean(false);
        this.wasQs4 = new AtomicBoolean(false);
        this.wasQs5 = new AtomicBoolean(false);
        this.wasQs6 = new AtomicBoolean(false);
        this.wasFurtherInformation = new AtomicBoolean(false);
        this.wasVariants = new AtomicBoolean(false);
        this.wasVariant1 = new AtomicBoolean(false);
        this.wasVariant2 = new AtomicBoolean(false);
        this.wasVariant3 = new AtomicBoolean(false);
    }
}
