package de.pho.dsapdfreader.dsaconverter;

import java.util.List;

import de.pho.dsapdfreader.dsaconverter.model.BoonRaw;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMeritsAndFlaws extends DsaConverter<BoonRaw>
{
    @Override
    protected void applyFlagsForKey(AtomicConverterFlag flags, String text)
    {

    }

    @Override
    protected String[] getKeys()
    {
        return new String[0];
    }

    @Override
    protected void concludePredecessor(List<BoonRaw> elementList)
    {

    }

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
