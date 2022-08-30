package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsProfession implements ConverterAtomicFlagsI
{
    public AtomicBoolean wasName;
    public AtomicBoolean wasDescription;
    public AtomicBoolean wasApValue;
    public AtomicBoolean wasPrecondition;
    public AtomicBoolean wasSpecialAbilities;
    public AtomicBoolean wasSkillCombat;
    public AtomicBoolean wasSkills;
    public AtomicBoolean wasSkillsMagic;
    public AtomicBoolean wasSkillsCleric;
    public AtomicBoolean wasMeritsRecommended;
    public AtomicBoolean wasFlawsRecommended;
    public AtomicBoolean wasMeritsInappropriate;
    public AtomicBoolean wasFlawsInappropriate;
    public AtomicBoolean wasVariants;
    public AtomicBoolean wasEquip;

    public ConverterAtomicFlagsProfession()
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
        wasPrecondition = new AtomicBoolean(false);
        wasApValue = new AtomicBoolean(false);
        wasSpecialAbilities = new AtomicBoolean(false);
        wasSkillCombat = new AtomicBoolean(false);
        wasSkills = new AtomicBoolean(false);
        wasSkillsMagic = new AtomicBoolean(false);
        wasSkillsCleric = new AtomicBoolean(false);
        wasMeritsRecommended = new AtomicBoolean(false);
        wasFlawsRecommended = new AtomicBoolean(false);
        wasMeritsInappropriate = new AtomicBoolean(false);
        wasFlawsInappropriate = new AtomicBoolean(false);
        wasVariants = new AtomicBoolean(false);
        wasEquip = new AtomicBoolean(false);
    }
}
