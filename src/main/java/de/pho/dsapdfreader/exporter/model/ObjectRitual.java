package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.ArtifactKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillFeature;
import de.pho.dsapdfreader.exporter.model.enums.ObjectRitualKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;

public class ObjectRitual
{

  public ObjectRitualKey key;
  public String name;
  public Publication publication;
  public ArtifactKey artifactKey;
  public Integer ap;
  public Cost binding;
  public Cost costActivation;
  public Integer volume;

  public List<ObjectRitualKey> requiredOrKeys = new ArrayList<>();
  public List<ObjectRitualKey> requiredNoneOrKeys = new ArrayList<>();

  public Integer minVolumeExtensions;
  public MysticalSkillFeature featureKey;
  public RequirementsSpecialAbility requirementsSpecialAbility;
  public RequirementSkill requirementSkill;
  //public List<TraditionKey> requiredTraditionKeys;
}
