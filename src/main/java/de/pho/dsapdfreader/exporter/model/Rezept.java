package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.helper.RezepteHelper;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.enums.LaborKey;
import de.pho.dsapdfreader.exporter.model.enums.RezeptKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;
import de.pho.dsapdfreader.uid.UidCategory;

public class Rezept {
  public String name;
  public String uid;
  public RezeptKey key;
  public EquipmentCategoryKey type;
  public EquipmentKey generatedItemKey;

  public SkillKey skillKey;
  public Integer difficulty;
  public SkillUsageKey skillUsageKey;
  public List<EquipmentKey> ingredients;
  public List<String> requirements;
  public RequirementsSpecialAbility reqAbility;
  public LaborKey labor;

  public Rezept() {
  }

  public Rezept(String name, EquipmentCategoryKey eck, SkillKey skillKey, Integer difficulty, EquipmentKey ek) {
    this.key = Extractor.extractEnumKey(name.replace("1001", "tausend_und_ein"), RezeptKey.class);
    this.uid = UidCategory.rezept.prefix + Extractor.extractKeyTextFromText(name.replace("1001", "tausend_und_ein")).toLowerCase();
    this.name = name;
    this.skillKey = skillKey;
    this.skillUsageKey = RezepteHelper.extractUsaageKey(skillKey, eck);
    this.type = eck;
    this.generatedItemKey = ek;
    this.difficulty = difficulty;
  }
}
