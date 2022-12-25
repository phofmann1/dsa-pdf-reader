package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.ArmorCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.ArmorKey;
import de.pho.dsapdfreader.exporter.model.enums.CraftingComplexityKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;

public class Armor
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
}
