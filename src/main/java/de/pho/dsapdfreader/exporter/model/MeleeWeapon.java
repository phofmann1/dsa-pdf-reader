package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.pho.dsapdfreader.exporter.model.enums.CloseCombatRange;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.CraftingComplexityKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.WeaponKey;

public class MeleeWeapon implements EquipmentI
{
  public WeaponKey key;
  public String name;
  public CombatSkillKey combatSkillKey;
  public Publication publication;
  public TP tp;
  public List<AttributeValuePair> attributeTpBonuses;
  public int atModifier;
  public int paModifier;
  public CloseCombatRange closeCombatRangeKey;
  public double weight;
  public double size;
  public Price price;
  public boolean parryForbidden;
  public boolean isImprovised;
  public CraftingComplexityKey craftingComplexity;
  public int craftingAp;
  public int breakingValue;
  public EquipmentCategoryKey equipmentCategoryKey;

  public int parryForMain;
  public int structurePoints;

  @JsonIgnore
  public String advantage;

  @JsonIgnore
  public String disadvantage;

  @JsonIgnore
  public String remark;

  @Override
  public int getKeyValue()
  {
    return this.key.toValue();
  }

  @Override
  public String getName()
  {
    return this.name;
  }

  @Override
  public String getRules()
  {
    return null;
  }

  @Override
  public String getAdvantage()
  {
    return this.advantage;
  }

  @Override
  public String getDisadvantage()
  {
    return this.disadvantage;
  }

  @Override
  public String getRemark()
  {
    return this.remark;
  }

}
