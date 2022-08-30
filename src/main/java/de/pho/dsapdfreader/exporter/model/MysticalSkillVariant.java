package de.pho.dsapdfreader.exporter.model;

import static de.pho.dsapdfreader.dsaconverter.model.CsvCustomConvertMysticalSkillVariant.MSV_SEPARATOR;

public class MysticalSkillVariant
{
    public String name;
    public int minLevel;
    public int ap;
    public String description;
    public String requiredVariantName;

    @Override
    public String toString()
    {
        return name + MSV_SEPARATOR
            + minLevel + MSV_SEPARATOR
            + ap + MSV_SEPARATOR
            + description + MSV_SEPARATOR
            + requiredVariantName;
    }
}
