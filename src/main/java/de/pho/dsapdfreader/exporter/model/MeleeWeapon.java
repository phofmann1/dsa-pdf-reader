package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.CloseCombatRange;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.CraftingComplexityKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.WeaponKey;

public class MeleeWeapon
{
  public WeaponKey key;
  public String name;
  public CombatSkillKey combatSkillKey;
  public Publication publication;
  public TP tp;
  public List<AttributeTpBonus> attributeTpBonuses;
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
}
