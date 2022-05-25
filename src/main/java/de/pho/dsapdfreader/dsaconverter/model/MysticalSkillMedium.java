package de.pho.dsapdfreader.dsaconverter.model;

import java.util.Arrays;

public class MysticalSkillMedium extends MysticalSkillSmall
{
    public String check;
    public String effect;
    public String castingDuration;
    public String commonness;
    public String cost;
    public String advancementCategory;
    public String qs1;
    public String qs2;
    public String qs3;
    public String qs4;
    public String qs5;
    public String qs6;
    public String[] variants;

    @Override
    public String toString()
    {
        return "MysticalSkillMedium{" +
            "name='" + getName() + '\'' +
            ", description='" + description + '\'' +
            ", check='" + check + '\'' +
            ", effect='" + effect + '\'' +
            ", castingDuration='" + castingDuration + '\'' +
            ", range='" + range + '\'' +
            ", duration='" + duration + '\'' +
            ", targetCategory='" + targetCategory + '\'' +
            ", cost=" + cost +
            ", feature='" + feature + '\'' +
            ", remarks='" + remarks + '\'' +
            ", topic=" + topic +
            ", commonness='" + commonness + '\'' +
            ", advancementCategory='" + advancementCategory + '\'' +
            ", variants=" + Arrays.toString(variants) +
            '}';
    }
}
