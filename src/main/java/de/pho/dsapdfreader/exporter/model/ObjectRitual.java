package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.ArtifactKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillFeature;
import de.pho.dsapdfreader.exporter.model.enums.ObjectRitualKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;

public class ObjectRitual
{

  public ObjectRitualKey key;
  public String name;
  public Publication publication;
  public ArtifactKey artifactKey;
  public int ap;
  public Cost binding;
  public Cost costActivation;
  public Integer volume;

  public List<ObjectRitualKey> requiredOrKeys = new ArrayList<>();
  public List<ObjectRitualKey> requiredNoneOrKeys = new ArrayList<>();

  public Integer minVolumeExtensions;
  public MysticalSkillFeature featureKey;
  public RequirementsSpecialAbility requirementsSpecialAbility;
  public List<TraditionKey> requiredTraditionKeys;
}
