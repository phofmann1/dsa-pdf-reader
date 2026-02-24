package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;
import de.pho.dsapdfreader.exporter.model.enums.TradeSecretKey;

import java.util.List;

public class Berufsgeheimnis {
        public TradeSecretKey key;
        public String name;
        public List<SkillKey> skillKeys;
        public List<Publication> publications;
        public int ap;
        public boolean isSecret;
        public RequirementSpecialAbility requirementAbility;
        public RequirementsSkill requirementsSkill;
        public SpecieKey requiredSpecieKey;
        public TradeSecretKey requiredTradeSecretKey;

}
