package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.TP;

public class ExtractorTp extends Extractor
{
  public static TP retrieve(String tpString)
  {
    if (tpString.isEmpty()) return null;
    TP returnValue = new TP();
    returnValue.noOfDice = Integer.valueOf(tpString.substring(0, tpString.indexOf("W")).trim());
    int indexOfPlus = tpString.indexOf("+");
    returnValue.sidesOfDice = Integer.valueOf(tpString.substring(tpString.indexOf("W") + 1, indexOfPlus > 0 ? indexOfPlus : tpString.length()).trim());
    returnValue.tpPlus = indexOfPlus > 0 ? Integer.valueOf(tpString.substring(indexOfPlus + 1).trim()) : 0;
    return returnValue;
  }

}
