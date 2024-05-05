package de.pho.dsapdfreader.exporter.model;

import java.io.Serializable;

import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.BoonVariantKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;

public class RequirementBoon implements Serializable {
  public BoonKey key;
  public BoonVariantKey variantKey;
  public SkillKey selectedSkillKey;
  public String variantName;
  public boolean exists;

  public int level;
  public boolean isSameSelection;
  public SkillCategoryKey skillCategoryKey;

  public RequirementBoon()
  {
  }

  public RequirementBoon(BoonKey key, boolean exists)
  {
    this.key = key;
    this.exists = exists;
  }

  public RequirementBoon(BoonKey key, boolean exists, String variantName)
  {
    this(key, exists, null, variantName);
  }

  public RequirementBoon(BoonKey key, boolean exists, BoonVariantKey variantKey)
  {
    this(key, exists, variantKey, null);
  }

  public RequirementBoon(BoonKey key, boolean exists, BoonVariantKey variantKey, String variantName)
  {
    this(key, exists);
    this.variantKey = variantKey;
    this.variantName = variantName;
  }

  public RequirementBoon(BoonKey key, boolean exists, boolean isSameSelection) {
    this(key, exists);
    this.isSameSelection = isSameSelection;
  }

  public RequirementBoon(BoonKey key, boolean exists, SkillCategoryKey skillCategoryKey) {
    this(key, exists);
    this.skillCategoryKey = skillCategoryKey;
  }

  public RequirementBoon(BoonKey key, boolean exists, String variantName, int level) {
    this(key, exists, null, variantName);
    this.level = level;
  }
}
