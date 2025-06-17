package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.BoonCategory;
import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.CultureKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SelectionCategory;
import de.pho.dsapdfreader.exporter.model.enums.SkillApplicationKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;

public class Boon
{
  public BoonKey key;
  public String name;
  public BoonCategory category;
  public Integer levels;
  public Float ap;
  public List<Float> apForLevelList;
  public List<Publication> publications = new ArrayList<>();
  public Boolean selectable;
  public SelectionCategory selectionCategory;
  public Boolean freeText;
  public Integer multiselect;
  public List<BoonVariant> variants = new ArrayList<>();

  public RequirementsSpecie requirementsSpecie;
  public CultureKey requiredCulture;
  public SpecialAbilityKey requiredAbilityKey;
  public List<TraditionKey> requiredTraditions = new ArrayList<>();
  public List<RequirementBoon> requirementBoons = new ArrayList<>();
  public Boolean isRequirementBoonsOr = Boolean.FALSE;

  public Object type;
  public Integer range;

  public List<ValueChange> valueChanges;
  public SkillUsageKey newSkillUsageKey;
  public SkillApplicationKey newSkillApplicationKey;

  public Boolean isBloodLine = null;
}
