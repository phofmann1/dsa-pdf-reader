package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.BoonVariantKey;

public class BoonVariant
{
    public BoonVariantKey key;
    public String name;
    public int ap;

    public List<ValueChange> valueChanges;
    public List<RequirementBoon> requirementBoonList = new ArrayList<>();
    public String description;

    public BoonVariant()
    {
    }

    public BoonVariant(BoonVariantKey key, String name)
    {
        this(key, name, 0, new ArrayList<>());
    }

    public BoonVariant(BoonVariantKey key, String name, int ap, List<RequirementBoon> requirementBoonList)
    {
        this.key = key;
        this.name = name;
        this.ap = ap;
        this.requirementBoonList = requirementBoonList;
    }
}
