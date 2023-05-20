package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SelectionCategory;
import de.pho.dsapdfreader.exporter.model.enums.SkillApplicationKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillCategoryKey;
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

  public Integer ap;
  public SpecialAbilityTypeKey abilityType;
  public Integer multiselect;
  public SelectionCategory selectionCategory;
  public SkillCategoryKey skillCategory;
  public Boolean hasFreeText;
  public SpecialAbilityAdvancedSelection advancedAbilities;
  public ValueChange valueChange;
  public SkillUsage skillUsage;
  public SkillApplicationKey[] skillApplications;

  public SpecieKey requiredSpecie;
  public TraditionKey requiredTradition;
  public RequirementBoon[] requireOneOfBoons;
  public RequirementBoon[] requireNoneOfBoons;
  public RequirementsAttribute requirementsAttribute;
  public RequirementsSpecialAbility requirementsAbility;
  public RequirementsSkill requirementsSkill;
  public RequirementSkillSum requirementsSkillsSum;
  public RequirementCombatSkill requirementCombatSkill;
  public RequirementMysticalSkill requirementMysticalSkill;

  public SpecialAbilityVariant[] variants;

  public List<CombatSkillKey> combatSkillKeys;
}
