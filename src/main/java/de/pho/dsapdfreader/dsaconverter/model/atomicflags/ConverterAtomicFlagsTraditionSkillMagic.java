package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsTraditionSkillMagic implements ConverterAtomicFlagsI
{
  public AtomicBoolean wasName;
  public AtomicBoolean wasCheck;
  public AtomicBoolean wasEffect;
  public AtomicBoolean wasCost;
  public AtomicBoolean wasDuration;
  public AtomicBoolean wasFeature;
  public AtomicBoolean wasTribes;
  public AtomicBoolean wasAdvancementCategory;
  public AtomicBoolean wasRequirements;
  public AtomicBoolean wasVolume;
  public AtomicBoolean wasBinding;
  public AtomicBoolean wasAp;
  public AtomicBoolean wasCastingDuration;
  public AtomicBoolean wasSkill;
  public AtomicBoolean wasRange;
  public AtomicBoolean wasTargetCategory;
  public AtomicBoolean wasMusicTraditions;


  public AtomicBoolean wasCircleOfBanishment;
  public AtomicBoolean wasCircleOfProtection;
  public AtomicBoolean wasGebräu;


  public ConverterAtomicFlagsTraditionSkillMagic()
  {
    this.initDataFlags();
  }

  public void initDataFlags()
  {
    this.wasName = new AtomicBoolean(Boolean.FALSE);
    this.wasCheck = new AtomicBoolean(Boolean.FALSE);
    this.wasEffect = new AtomicBoolean(Boolean.FALSE);
    this.wasCost = new AtomicBoolean(Boolean.FALSE);
    this.wasDuration = new AtomicBoolean(Boolean.FALSE);
    this.wasFeature = new AtomicBoolean(Boolean.FALSE);
    this.wasTribes = new AtomicBoolean(Boolean.FALSE);
    this.wasMusicTraditions = new AtomicBoolean(Boolean.FALSE);
    this.wasMusicTraditions = new AtomicBoolean(Boolean.FALSE);
    this.wasAdvancementCategory = new AtomicBoolean(Boolean.FALSE);
    this.wasRequirements = new AtomicBoolean(Boolean.FALSE);
    this.wasVolume = new AtomicBoolean(Boolean.FALSE);
    this.wasBinding = new AtomicBoolean(Boolean.FALSE);
    this.wasAp = new AtomicBoolean(Boolean.FALSE);
    this.wasCastingDuration = new AtomicBoolean(Boolean.FALSE);
    this.wasSkill = new AtomicBoolean(Boolean.FALSE);
    this.wasRange = new AtomicBoolean(Boolean.FALSE);
    this.wasTargetCategory = new AtomicBoolean(Boolean.FALSE);
    this.wasCircleOfProtection = new AtomicBoolean(Boolean.FALSE);
    this.wasCircleOfBanishment = new AtomicBoolean(Boolean.FALSE);
    this.wasGebräu = new AtomicBoolean(Boolean.FALSE);
  }

  @Override
  public AtomicBoolean getFirstFlag()
  {
    return this.wasName;
  }
}
