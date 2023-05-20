package de.pho.dsapdfreader.exporter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.pho.dsapdfreader.exporter.model.enums.ArmorCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.ArmorKey;
import de.pho.dsapdfreader.exporter.model.enums.CraftingComplexityKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;

public class Armor implements EquipmentI
{
  public ArmorKey key;
  public String name;
  public ArmorCategoryKey armorCategoryKey;
  public Publication publication;
  public int rs;
  public int be;
  public boolean additionalEncumbered;
  public double weight;
  public Price price;
  public CraftingComplexityKey craftingComplexity;
  public int craftingAp;
  public int breakingValue;
  public EquipmentCategoryKey equipmentCategoryKey;

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
