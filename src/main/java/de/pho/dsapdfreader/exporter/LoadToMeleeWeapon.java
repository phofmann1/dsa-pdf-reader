package de.pho.dsapdfreader.exporter;

import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.model.MeleeWeaponRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAttributeTpBonus;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorParryForMain;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorPrice;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorShieldStructurePoints;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorTp;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorWeaponKey;
import de.pho.dsapdfreader.exporter.model.MeleeWeapon;
import de.pho.dsapdfreader.exporter.model.enums.CloseCombatRange;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.CraftingComplexityKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;


public class LoadToMeleeWeapon
{

  private static final Logger LOGGER = LogManager.getLogger();
  private LoadToMeleeWeapon()
  {
  }

  public static MeleeWeapon migrate(MeleeWeaponRaw mwr) {
    MeleeWeapon returnValue = new MeleeWeapon();
    returnValue.name = mwr.name.replace("(2H)", "")
        .replace("Flöte und ähnliche Instrumente", "Flöte")
        .replace("Laute und ähnliche Instrumente", "Laute")
        .replace("Nipauka-Axt", "Geisteraxt (Nipakau-Nigoma)")
        .replace("Tulamidisches Kurzschwert (Scimshar)", "Scimshar (Tulamidisches Kurzschwert)")
        .replace("Zyklopäisches Kurzschwert) (Parazonium)", "Parazonium (Zyklopäisches Kurzschwert)")
        .trim();
    if (mwr.tp.contains(" ")) {
      LOGGER.error("WAFFENPROFILE FEHLEN!!! => " + returnValue.name + " --> " + mwr.tp);
      return null;
    }
    else {
      returnValue.key = ExtractorWeaponKey.retrieve(returnValue.name);
      returnValue.combatSkillKey = mwr.combatSkillKey;
      returnValue.publication = Publication.valueOf(mwr.publication);
      returnValue.tp = ExtractorTp.retrieve(mwr.tp);
      returnValue.isImprovised = mwr.isImprovised;
      returnValue.equipmentCategoryKey = EquipmentCategoryKey.nahkampfwaffe;
      returnValue.attributeTpBonuses = ExtractorAttributeTpBonus.retrieve(mwr.additionalDamage);
      String[] weaponModifiers = mwr.atPaMod.replaceAll("–", "-").split("\\/");
      returnValue.parryForbidden = Stream.of(CombatSkillKey.lanzen, CombatSkillKey.spießwaffen, CombatSkillKey.kettenwaffen, CombatSkillKey.peitschen).anyMatch(csk -> csk == returnValue.combatSkillKey);
      returnValue.atModifier = weaponModifiers[0].isEmpty() ? 0 : Integer.valueOf(weaponModifiers[0].trim());

      returnValue.advantage = mwr.advantage;
      returnValue.disadvantage = mwr.disadvantage;
      returnValue.remark = mwr.remark;


      if (returnValue.remark != null && !returnValue.remark.isEmpty())
      {
        returnValue.structurePoints = ExtractorShieldStructurePoints.retrieve(returnValue.remark);
        returnValue.parryForMain = ExtractorParryForMain.retrieve(returnValue.remark);
      }

      if (returnValue.combatSkillKey == CombatSkillKey.schilde)
      {
        returnValue.parryForMain = returnValue.paModifier;
      }

      if (!returnValue.parryForbidden)
      {
        if (weaponModifiers[1].trim().equals("-")) {
          returnValue.parryForbidden = true;
        }
        else {
          returnValue.paModifier = Integer.valueOf(weaponModifiers[1].trim());
        }
      }

      returnValue.closeCombatRangeKey = mwr.combatDistance.isEmpty() ? (mwr.name.equals("Kriegslanze") ? CloseCombatRange.ÜBERLANG : CloseCombatRange.KURZ) : CloseCombatRange.valueOf(mwr.combatDistance.toUpperCase());
      if (!mwr.weight.isEmpty())
      {
        returnValue.weight = Double.valueOf(mwr.weight.replace(".", "").replace(",", "."));
      }
      if (!mwr.size.isEmpty())
      {
        returnValue.size = Double.valueOf(mwr.size.replace(".", "").replace(",", "."));
      }
      returnValue.price = ExtractorPrice.retrieve(mwr.price);

      returnValue.craftingComplexity = CraftingComplexityKey.parse(mwr.craft);
      if (mwr.craft.startsWith("komp"))
      {
        returnValue.craftingAp = Integer.valueOf(mwr.craft.substring(mwr.craft.indexOf("(") + 1, mwr.craft.indexOf("AP")).trim());
      }
      returnValue.breakingValue = switch (returnValue.combatSkillKey)
          {
            case dolche -> 14;
            case schwerter, fächer -> 13;
            case hiebwaffen, raufen, diskusse, spießwaffen, stangenwaffen, zweihandschwerter -> 12;
            case zweihandhiebwaffen -> 11;
            case blasrohre, kettenwaffen, schilde, wurfwaffen -> 10;
            case fechtwaffen -> 8;
            case armbrüste, lanzen -> 6;
            case bögen, schleudern, peitschen -> 4;
            case feuerspeien -> -1;
          };


      return returnValue;
    }
  }

}
