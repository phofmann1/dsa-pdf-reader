package de.pho.dsapdfreader.exporter.helper;

import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;

public class RezepteHelper {

  public static SkillUsageKey extractUsaageKey(SkillKey sk, EquipmentCategoryKey genItemType) {
    return switch (sk) {
      case alchimie -> {
        if (genItemType == EquipmentCategoryKey.elixiere) yield SkillUsageKey.elixiere;
        else if (genItemType == EquipmentCategoryKey.gifte) yield SkillUsageKey.alchimistische_gifte;
        else yield null;
      }
      case pflanzenkunde -> {
        if (genItemType == EquipmentCategoryKey.elixiere) yield SkillUsageKey.heilpflanzen;
        else if (genItemType == EquipmentCategoryKey.gifte) yield SkillUsageKey.giftpflanzen;
        else yield null;
      }
      default -> null;
    };
  }
}
