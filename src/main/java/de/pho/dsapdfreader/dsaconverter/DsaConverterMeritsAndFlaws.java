package de.pho.dsapdfreader.dsaconverter;

import de.pho.dsapdfreader.dsaconverter.model.BoonRaw;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMeritsAndFlaws extends DsaConverter<BoonRaw>
{
    @Override
    protected BoonRaw initializeType()
    {
        return new BoonRaw();
    }

    @Override
    protected void applyDataValue(BoonRaw last, TextWithMetaInfo t, String cleanText, AtomicConverterFlag flags)
    {
        /** apply data to Boon */
    }
}
