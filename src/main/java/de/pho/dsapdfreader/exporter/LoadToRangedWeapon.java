package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.RangedWeaponRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMunitionType;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorPrice;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorTp;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorWeaponKey;
import de.pho.dsapdfreader.exporter.model.RangedWeapon;
import de.pho.dsapdfreader.exporter.model.enums.MunitionType;
import de.pho.dsapdfreader.exporter.model.enums.Publication;


public class LoadToRangedWeapon
{

    private LoadToRangedWeapon()
    {
    }

    public static RangedWeapon migrate(RangedWeaponRaw mwr)
    {
        RangedWeapon returnValue = new RangedWeapon();
        returnValue.name = mwr.name;
        returnValue.key = ExtractorWeaponKey.retrieve(returnValue.name);
        returnValue.combatSkillKey = mwr.combatSkillKey;
        returnValue.publication = Publication.valueOf(mwr.publication.toUpperCase());
        returnValue.tp = ExtractorTp.retrieve(mwr.tp);

        String[] distances = mwr.combatDistance.split("\\/");
        returnValue.distanceShort = Integer.valueOf(distances[0]);
        returnValue.distanceMedium = Integer.valueOf(distances[1]);
        returnValue.distanceLong = Integer.valueOf(distances[2]);

        String[] loadingTimes = mwr.loadingTime
            .replaceAll("Aktionen", "")
            .replaceAll("Aktion", "")
            .split("\\/");
        returnValue.loadingTime = Integer.valueOf(loadingTimes[0].trim());
        returnValue.loadingTimeMagazin = loadingTimes.length > 1 ? Integer.valueOf(loadingTimes[1].trim()) : -1;
        returnValue.magazinSize = 1;
        if (!mwr.munitionType.isEmpty())
        {
            returnValue.munitionTypeKey = ExtractorMunitionType.retrieve(mwr.munitionType);
        }
        else
        {
            returnValue.munitionTypeKey = MunitionType.KEINE;
        }
        returnValue.isImprovised = mwr.isImprovised;


        if (!mwr.weight.isEmpty())
        {
            returnValue.weight = Double.valueOf(mwr.weight.replace(".", "").replace(",", "."));
        }
        if (!mwr.size.isEmpty())
        {
            returnValue.size = Double.valueOf(mwr.size.replace(".", "").replace(",", "."));
        }
        returnValue.price = ExtractorPrice.retrieve(mwr.price);

        return returnValue;
    }

}
