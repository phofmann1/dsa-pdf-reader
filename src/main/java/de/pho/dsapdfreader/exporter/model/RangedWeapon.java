package de.pho.dsapdfreader.exporter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.MunitionType;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.WeaponKey;

public class RangedWeapon implements EquipmentI
{
  public String name;
  public WeaponKey key;
  public CombatSkillKey combatSkillKey;
  public Publication publication;
  @JsonInclude
  public TP tp;
  public Double weight;
  public Double size;
  public Price price;
  public boolean isImprovised;
  public int distanceShort;
  public int distanceMedium;
  public int distanceLong;
  public int loadingTime;
  public int loadingTimeMagazin;
  public MunitionType munitionTypeKey;
  public int magazinSize;
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
    return key.toValue();
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getRules()
  {
    return null;
  }

  @Override
  public String getAdvantage()
  {
    return advantage;
  }

  @Override
  public String getDisadvantage()
  {
    return disadvantage;
  }

  @Override
  public String getRemark()
  {
    return this.remark;
  }
}
