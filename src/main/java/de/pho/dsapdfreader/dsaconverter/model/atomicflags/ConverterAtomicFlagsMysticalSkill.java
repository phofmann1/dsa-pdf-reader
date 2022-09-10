package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsMysticalSkill implements ConverterAtomicFlagsI
{
  public AtomicBoolean wasName;
  public AtomicBoolean wasDescription;
  public AtomicBoolean wasFeature;
  public AtomicBoolean wasDuration;
  public AtomicBoolean wasRemarks;
  public AtomicBoolean wasTargetCategory;
  public AtomicBoolean wasRange;
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
  public AtomicBoolean wasTalent;

  public AtomicBoolean wasGestureAndIncantation;
  public AtomicBoolean wasReversalis;

  public ConverterAtomicFlagsMysticalSkill()
  {
    this.initDataFlags();
  }

  public void initDataFlags()
  {
    this.wasName = new AtomicBoolean(false);
    this.wasDescription = new AtomicBoolean(false);
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
    this.wasTalent = new AtomicBoolean(false);
    this.wasFurtherInformation = new AtomicBoolean(false);
    this.wasVariants = new AtomicBoolean(false);
    this.wasGestureAndIncantation = new AtomicBoolean(false);
    this.wasReversalis = new AtomicBoolean(false);
  }

  @Override
  public AtomicBoolean getFirstFlag()
  {
    return this.wasName;
  }
}
