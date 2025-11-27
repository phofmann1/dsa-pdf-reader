package de.pho.dsapdfreader.exporter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.Unit;

public class BeverageEquipment implements EquipmentI {
  public String name;
  public EquipmentKey key;
  public EquipmentCategoryKey equipmentCategoryKey = EquipmentCategoryKey.genussmittel_und_luxus;
  public Price price;
  public Unit baseUnit;
  public Publication publication;


  @JsonIgnore
  public String rules;

  @JsonIgnore
  public String remark;

  @Override
  public int getKeyValue() {
    return this.key.toValue();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getRules() {
    return this.rules;
  }

  @Override
  public String getAdvantage() {
    return null;
  }

  @Override
  public String getDisadvantage() {
    return null;
  }

  @Override
  public String getRemark() {
    return this.remark;
  }
}
