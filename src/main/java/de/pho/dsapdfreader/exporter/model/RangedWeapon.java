package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MunitionType;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.WeaponKey;

public class RangedWeapon
{
  public String name;
  public WeaponKey key;
  public CombatSkillKey combatSkillKey;
  public Publication publication;
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
}
