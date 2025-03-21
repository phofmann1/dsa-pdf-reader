package de.pho.dsapdfreader.exporter;

import java.util.List;

import de.pho.dsapdfreader.dsaconverter.model.TraditionRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorRequirements;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSpecialAbility;
import de.pho.dsapdfreader.exporter.model.RequirementBoon;
import de.pho.dsapdfreader.exporter.model.SpecialAbility;
import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.CultureKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityCategoryKey;

public class LoadToTraditionAbility {

  private LoadToTraditionAbility() {
  }

  public static SpecialAbility migrate(TraditionRaw raw) {
    SpecialAbility returnValue = new SpecialAbility();
    returnValue.name = raw.name
        .replaceAll("Angroschgeweihter$", "Angroschkirche")
        .replaceAll("Ysilischen Bannzeichner", "Ysilische Bannzeichner")
        .replaceAll("Intuitiven Zauberer", "Intuitive Zauberer");
    returnValue.key = ExtractorSpecialAbility.retrieve(returnValue.name);
    returnValue.ap = Float.parseFloat(raw.ap.replace("AP", "").trim());
    returnValue.category = raw.categoryKey;
    returnValue.requireNoneOfBoons = ExtractorRequirements.extractRequirementsBoon(raw.preconditions, returnValue.name);
    returnValue.requireOneOfBoons = (raw.categoryKey == SpecialAbilityCategoryKey.tradition_magic)
        ? List.of(new RequirementBoon(BoonKey.zauberer, true))
        : List.of(new RequirementBoon(BoonKey.geweihter, true));
    returnValue.requireOneOfCultures = List.of(CultureKey.ambosszwerge);
    //Borbaradianer
    return returnValue;
  }
}
