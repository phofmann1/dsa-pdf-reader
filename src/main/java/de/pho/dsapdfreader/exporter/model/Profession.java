package de.pho.dsapdfreader.exporter.model;

import java.io.Serializable;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.CultureKey;
import de.pho.dsapdfreader.exporter.model.enums.ElementKey;
import de.pho.dsapdfreader.exporter.model.enums.GenderKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.ObjectRitualKey;
import de.pho.dsapdfreader.exporter.model.enums.ProfessionKey;
import de.pho.dsapdfreader.exporter.model.enums.ProfessionTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;
import de.pho.dsapdfreader.exporter.model.enums.TerrainTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.TraditionGuidelineKey;

public class Profession implements Serializable {
  public String name;
  public String nameMale;
  public String nameFemale;

  public ProfessionKey key;
  public ProfessionKey variantOf;
  public ProfessionKey parentProfessionKey;
  public ProfessionTypeKey professionType;

  public int apValue;
  public List<Publication> publicationKeys;

  public int ap4Skills;
  public List<SkillKey> ap4SkillsSet;

  //REQUIREMENTS
  public GenderKey requiredGender;
  public List<SpecieKey> requiredSpecieKeys;
  public List<CultureKey> requiredCultureKeys;
  public List<AttributeValuePair> requiredAttributes;
  public List<RequirementBoon> requiredBoons;
  public List<RequirementSpecialAbility> requiredAbilities;

  //OPTIONS
  public List<ValueChange> specializationOptions;
  public List<TerrainTypeKey> terrainKnowledgeOptions;
  public List<ValueChange> combatSkillOptions;
  public List<MysticalSkillKey> trickOptions;

  //CHANGES
  public int minLanguageAp;
  public boolean only4languages = false;
  public int curseAp;
  public List<SpecialAbilityKey> specialAbilities;
  public List<ObjectRitualKey> objectRituals;

  public List<ValueChange> valueChanges;
  public List<ValueChange> combatSkillChanges;
  public List<ValueChange> skillChanges;
  public List<ValueChange> mysticalSkillChanges;

  public int noOfTricks;
  public int minMysticalSkillAp;
  public TraditionGuidelineKey traditionGuidLineKey;
  public List<ElementKey> elementsAttuned;
  public List<ElementKey> elementsAttunedSelectOne;

  public List<BoonKey> boonsRecomended;
  public List<BoonKey> boonsUnrecomended;
}
