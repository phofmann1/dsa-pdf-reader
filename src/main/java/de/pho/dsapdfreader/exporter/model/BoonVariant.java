package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

public class BoonVariant
{
    public String name;
    public int ap;
    public List<RequirementBoon> requirementBoon = new ArrayList<>();
    public String description;
}
