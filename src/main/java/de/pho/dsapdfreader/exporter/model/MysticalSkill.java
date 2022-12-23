package de.pho.dsapdfreader.exporter.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.pho.dsapdfreader.exporter.model.enums.AdvancementCategory;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.ElementKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillFeature;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.TargetCategory;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;
import de.pho.dsapdfreader.exporter.model.enums.TraditionSubKey;

public class MysticalSkill
{
  public static final String TYPE = "mysticalskills";
  public MysticalSkillKey key;
  public String name;
  public List<AttributeShort> check;
  public ResistDifficulty difficulty;
  public CastingDuration casting;
  public MysticalSkillCategory category;

  public Cost skillCost;
  public SkillRange skillRange;
  public Duration skillDuration;
  public List<TargetCategory> targetCategories;

  public List<MysticalSkillFeature> features;
  public List<ElementKey> elementalCategories;
  public AdvancementCategory advancementCategory;
  public List<MysticalSkillModification> allowedModifications;
  public List<TraditionKey> traditions;
  public List<TraditionSubKey> traditionSubs;
  public Map<TraditionKey, String> traditionIncantationMap = new HashMap<>();

  public List<MysticalSkillVariant> spellVariants;

  public Publication publication;
  public List<SkillKey> skillKeys;

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
