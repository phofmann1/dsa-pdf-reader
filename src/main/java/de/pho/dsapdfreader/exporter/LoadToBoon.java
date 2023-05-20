package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.dsaconverter.model.BoonRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorBoonKey;
import de.pho.dsapdfreader.exporter.model.Boon;
import de.pho.dsapdfreader.exporter.model.enums.Publication;


public class LoadToBoon
{

  private LoadToBoon()
  {
  }

  public static Boon migrate(BoonRaw ar)
  {
    Boon returnValue = new Boon();

    returnValue.name = ar.name.replace("\u00AD", "-");
    returnValue.key = ExtractorBoonKey.retrieve(ar.name);
    returnValue.publication = Publication.valueOf(ar.publication);
    /*
    returnValue.category;
    returnValue.ap;
    returnValue.levels;
    returnValue.multiselect;
    returnValue.newSkillApplication;
    returnValue.rule;
    returnValue.selectionCategory;
    returnValue.requirementBoons;
    returnValue.requirementsSpecie;
    returnValue.requiredTraditions;
    returnValue.requiredCulture;
    returnValue.valueChange;
    returnValue.variants;
     */
    return returnValue;
  }

}
