package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.RangedWeaponRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMunitionType;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorPrice;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorTp;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorWeaponKey;
import de.pho.dsapdfreader.exporter.model.Armor;
import de.pho.dsapdfreader.exporter.model.RangedWeapon;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.MunitionType;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.tools.merger.ObjectMerger;

import java.util.List;
import java.util.Optional;


public class LoadToRangedWeapon
{

    private LoadToRangedWeapon()
    {
    }

  public static RangedWeapon migrate(RangedWeaponRaw rwr)
  {
    RangedWeapon returnValue = new RangedWeapon();
    returnValue.name = rwr.name;
    returnValue.key = ExtractorWeaponKey.retrieve(returnValue.name);
    returnValue.combatSkillKey = rwr.combatSkillKey;
    returnValue.publication = Publication.valueOf(rwr.publication);
    returnValue.tp = ExtractorTp.retrieve(rwr.tp);

    String[] distances = rwr.combatDistance.split("\\/");
    returnValue.distanceShort = Integer.valueOf(distances[0]);
    returnValue.distanceMedium = Integer.valueOf(distances[1]);
    returnValue.distanceLong = Integer.valueOf(distances[2]);

    returnValue.advantage = rwr.advantage;
    returnValue.disadvantage = rwr.disadvantage;
    returnValue.remark = rwr.remark;

    String[] loadingTimes = rwr.loadingTime
        .replaceAll("Aktionen", "")
        .replaceAll("Aktion", "")
        .split("\\/");
    returnValue.loadingTime = Integer.valueOf(loadingTimes[0].trim());
    returnValue.loadingTimeMagazin = loadingTimes.length > 1 ? Integer.valueOf(loadingTimes[1].trim()) : -1;
    returnValue.magazinSize = 1;
    returnValue.equipmentCategoryKey = EquipmentCategoryKey.fernkampfwaffe;

    if (!rwr.munitionType.isEmpty())
    {
      returnValue.munitionTypeKey = ExtractorMunitionType.retrieve(rwr.munitionType);
    }
    else
    {
      returnValue.munitionTypeKey = MunitionType.KEINE;
    }
    returnValue.isImprovised = rwr.isImprovised;


    if (!rwr.weight.isEmpty())
    {
      returnValue.weight = Double.valueOf(rwr.weight.replace(".", "").replace(",", "."));
    }
    if (!rwr.size.isEmpty())
    {
      returnValue.size = Double.valueOf(rwr.size.replace(".", "").replace(",", "."));
    }
    returnValue.price = ExtractorPrice.retrieve(rwr.price);

        return returnValue;
    }

  public static void applyCorrections(RangedWeapon sa, List<RangedWeapon> corrections) {
    Optional<RangedWeapon> correction = corrections.stream().filter(c -> c.key == sa.key).findFirst();
      correction.ifPresent(rangedWeapon -> ObjectMerger.merge(rangedWeapon, sa));
  }

}
