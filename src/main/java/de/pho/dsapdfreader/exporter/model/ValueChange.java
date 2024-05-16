package de.pho.dsapdfreader.exporter.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillFeature;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;
import de.pho.dsapdfreader.exporter.model.enums.TargetCategory;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;
import de.pho.dsapdfreader.exporter.model.enums.UsageRestrictionKey;
import de.pho.dsapdfreader.exporter.model.enums.ValueChangeKey;
import de.pho.dsapdfreader.exporter.model.enums.ValueChangeType;

public class ValueChange implements Serializable {
  public ValueChangeKey key;
  public ValueChangeType type;
  @JsonProperty("valueChange")
  public Integer change;
  public Integer valueChangeMax;

  public Integer[] attributeValueChanges;

  public SkillKey skillKey;
  public List<SkillKey> skillKeysOneOf;
  public CombatSkillKey combatSkillKey;
  public List<CombatSkillKey> combatSkillKeysOneOf;

  public Boolean perLevel;
  public Boolean temporary;
  public Boolean useParentSelection;
  public Boolean conditionally; //Trifft nicht immer zu (Zwergennase)
  public List<SkillUsageKey> usageKeys;
  public TargetCategory targetCategory;
  public UsageRestrictionKey usageRestrictionKey;
  public MysticalSkillFeature featureKey;
  public List<MysticalSkillKey> mysticalSkillKeys;
  public MysticalSkillModification mysticalSkillModificationType;
  public TraditionKey traditionKey;
  public SkillUsageKey newSkillUsageKey;

  public MysticalSkillCategory mysticalSkillCategory;

  @JsonIgnore
  public boolean isValid()
  {
    return switch (this.type)
        {
          case fp -> this.change != 0
              && ((this.useParentSelection != null && this.useParentSelection) //Lieblingszauber...
              || (this.usageKeys != null && this.usageKeys.size() > 0) //Weg des Taschendiebs...
              || (this.mysticalSkillKeys != null && this.mysticalSkillKeys.size() > 0) //Scholar Gareth (Rüstung), Kuslik, Perricum...
              || this.featureKey != null //Merkmalskenntnis...
              || mysticalSkillModificationType != null //Scholar Hesindius Lichtblick
              || usageRestrictionKey != null // Kristallkraft
          );
          case traditionChangeForFeature -> this.featureKey != null && this.traditionKey != null;
          case value -> false;
          case max -> false;
          case qs -> this.change != 0
              && ((this.usageKeys != null && this.usageKeys.size() > 0)
              || (this.useParentSelection != null && this.useParentSelection)
              || (this.mysticalSkillKeys != null && this.mysticalSkillKeys.size() > 0)
          );
          case regeneration -> false;
          case difficulty -> false;
          case min -> false;
          case gifted -> false;
          case checkAttributeBonus -> false;
          default -> false;
        };
  }
}
