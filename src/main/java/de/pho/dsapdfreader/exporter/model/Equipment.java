package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.pho.dsapdfreader.exporter.model.enums.CraftingComplexityKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;

public class Equipment implements EquipmentI
{
  public String name;
  public List<Alias> aliasse;
  public EquipmentKey key;
  public EquipmentCategoryKey equipmentCategoryKey;
  public Price price;
  public int structure;
  public double weight;
  public String color;
  public Publication publication;

  public CraftingComplexityKey craftingComplexity;
  public int craftingAp;

  @JsonIgnore
  public String rules;

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
    return this.rules;
  }

  @Override
  public String getAdvantage()
  {
    return null;
  }

  @Override
  public String getDisadvantage()
  {
    return null;
  }

  @Override
  public String getRemark()
  {
    return this.remark;
  }
}
