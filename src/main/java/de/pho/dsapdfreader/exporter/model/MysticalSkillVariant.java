package de.pho.dsapdfreader.exporter.model;

import static de.pho.dsapdfreader.dsaconverter.model.CsvCustomConvertMysticalSkillVariant.MSV_SEPARATOR;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillVariantKey;

public class MysticalSkillVariant
{
  public MysticalSkillVariantKey key;
  public String name;
  public int minLevel;
  public int ap;
  public String description;
  public List<MysticalSkillVariantKey> requiredVariantKeys;

  @Override
  public String toString()
  {
    return name + MSV_SEPARATOR
        + minLevel + MSV_SEPARATOR
        + ap + MSV_SEPARATOR
        + description + MSV_SEPARATOR
        + requiredVariantKeys;
  }
}
