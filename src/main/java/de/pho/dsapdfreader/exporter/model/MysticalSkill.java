package de.pho.dsapdfreader.exporter.model;

import java.util.List;

public class MysticalSkill
{
    public MysticalSkillKey key;
    public String name;
    public AttributeShort[] check;
    public ResistDifficulty difficulty;
    public CastingDuration casting;
    public MysticalSkillCategory category;

    public Cost skillCost;
    public SkillRange skillRange;
    public Duration skillDuration;
    public TargetCategory[] targetCategories;

    public List<MysticalSkillFeature> features;
    public ElementKey[] elementalCategories;
    public AdvancementCategory advancementCategory;
    public MysticalSkillModification[] allowedModifications;
    public TraditionKey[] traditions;

    public List<MysticalSkillVariant> spellVariants;

    public Publication publication;

    // public boolean favorite;
    // public boolean isElementalAttuned;
    // public boolean isFamiliarized ;
    // public RequiredByCategory requiredBy;
    // public TraditionKey selectedTraditionKey;
    // public MysticalSkillVariant[] selectedVariants;
    // public int valueAdvancement ;
    // public int valueCulture ;
    // public int valueGenerationAdvancement ;
    // public int valueProfession ;

}
