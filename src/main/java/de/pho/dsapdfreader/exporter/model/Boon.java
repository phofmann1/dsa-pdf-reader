package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.BoonCategory;
import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.CultureKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.RequiredByCategory;
import de.pho.dsapdfreader.exporter.model.enums.SelectionCategory;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;

public class Boon
{
    public BoonKey key;
    public String name;
    public BoonCategory category;
    public int levels;
    public int ap;
    public Publication publication;
    public boolean selectable = true;
    public SelectionCategory selectionCategory;
    public boolean freeText;
    public int multiselect = 1;
    public List<BoonVariant> variants = new ArrayList<>();

    public RequirementsSpecie requirementsSpecie;
    public CultureKey requiredCulture;
    public List<TraditionKey> requiredTraditions = new ArrayList<>();
    public List<RequirementBoon> requirementBoons = new ArrayList<>();

    public List<RequiredByCategory> requiredBy = new ArrayList<>();

    public Object type;
    public String rule;
    public int range;

    public ValueChange valueChange;
  public SkillUsage newSkillApplication;
}
