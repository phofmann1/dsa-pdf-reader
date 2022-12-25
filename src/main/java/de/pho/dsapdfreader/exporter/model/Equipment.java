package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.CraftingComplexityKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;

public class Equipment
{
  public String name;
  public EquipmentKey key;
  public EquipmentCategoryKey categoryKey;
  public Price price;
  public int structure;
  public double weight;
  public String color;
  public Publication publication;

  public CraftingComplexityKey craftingComplexity;
  public int craftingAp;
}
