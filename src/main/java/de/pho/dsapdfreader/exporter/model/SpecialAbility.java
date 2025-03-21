package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.CultureKey;
import de.pho.dsapdfreader.exporter.model.enums.DsaState;
import de.pho.dsapdfreader.exporter.model.enums.EntityDomainKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SelectionCategory;
import de.pho.dsapdfreader.exporter.model.enums.SkillApplicationKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;

public class SpecialAbility
{
  public SpecialAbilityKey key;
  public String name;

  public Publication publication;
  public SpecialAbilityCategoryKey category;

  public Float ap;
  public SpecialAbilityTypeKey abilityType;
  public Integer multiselect;
  public SelectionCategory selectionCategory;
  public Integer selectSkillUsagesCount;
  public SkillCategoryKey skillCategory;
  public Boolean hasFreeText;
  public SpecialAbilityAdvancedSelection advancedAbilities;
  public List<ValueChange> valueChanges;
  public SkillUsageKey newSkillUsageKey;
  public SkillApplicationKey newSkillApplicationKey;

  public MysticalSkillCategory newMysticalSkillCategoryKey;

  public List<CombatSkillKey> combatSkillKeys;
  public Boolean isOnlyParryWeapon;
  public Boolean isOnlyElfenWeapon;
  public Boolean isOnlyDwarfenWeapon;

  public List<EntityDomainKey> requiredEntityDomainKeys;
  public Integer requiredPactLevel;

  public SpecieKey requiredSpecie;
  public DsaState requiredState;
  public List<TraditionKey> requireOneOfTraditions;
  public List<RequirementBoon> requireOneOfBoons;
  public List<RequirementBoon> requireNoneOfBoons;
  public List<CultureKey> requireOneOfCultures;
  public RequirementsAttribute requirementsAttribute;
  public RequirementsSpecialAbility requirementsAbility;
  public RequirementsSkill requirementsSkill;
  public RequirementSkillSum requirementsSkillsSum;
  public RequirementsCombatSkill requirementsCombatSkill;
  public RequirementMysticalSkill requirementMysticalSkill;
  public RequirementsObjectRitual requirementsObjectRituals;
}