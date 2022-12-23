package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;

public class RequirementsSpecie
{
  public List<SpecieKey> required = new ArrayList<>();
  public List<SpecieKey> forbidden = new ArrayList<>();
}
