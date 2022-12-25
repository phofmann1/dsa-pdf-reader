package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.MeleeWeaponRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAttributeTpBonus;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorPrice;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorTp;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorWeaponKey;
import de.pho.dsapdfreader.exporter.model.MeleeWeapon;
import de.pho.dsapdfreader.exporter.model.enums.CloseCombatRange;
import de.pho.dsapdfreader.exporter.model.enums.CraftingComplexityKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;


public class LoadToMeleeWeapon
{

  private LoadToMeleeWeapon()
  {
  }

  public static MeleeWeapon migrate(MeleeWeaponRaw mwr)
  {
    MeleeWeapon returnValue = new MeleeWeapon();
    returnValue.name = mwr.name.replace("(2H)", "").trim();
    returnValue.key = ExtractorWeaponKey.retrieve(returnValue.name);
    returnValue.combatSkillKey = mwr.combatSkillKey;
    returnValue.publication = Publication.valueOf(mwr.publication.toUpperCase());
    returnValue.tp = ExtractorTp.retrieve(mwr.tp);
    returnValue.isImprovised = mwr.isImprovised;
    returnValue.attributeTpBonuses = ExtractorAttributeTpBonus.retrieve(mwr.additionalDamage);
    String[] weaponModifiers = mwr.atPaMod.replaceAll("–", "-").split("\\/");
    returnValue.parryForbidden = weaponModifiers.length < 2 || weaponModifiers[1].equals("-");
    returnValue.atModifier = weaponModifiers[0].isEmpty() ? 0 : Integer.valueOf(weaponModifiers[0].trim());
    if (!returnValue.parryForbidden)
    {
      returnValue.paModifier = Integer.valueOf(weaponModifiers[1].trim());
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
          case DAGGER -> 14;
          case SWORD, FAN -> 13;
          case BLUNT, BRAWL, DISCUS, PIKE, POLE, TWOHANDED_SWORD -> 12;
          case TWOHANDED_BLUNT -> 11;
          case BLOWPIPE, CHAIN, SHIELD, THROWING -> 10;
          case FENCING -> 8;
          case CROSSBOW, LANCE -> 6;
          case BOW, SLING, WHIP -> 4;
          case SPITFIRE -> -1;
        };

    return returnValue;
  }

}
