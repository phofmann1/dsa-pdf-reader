package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.pho.dsapdfreader.exporter.model.enums.Unit;

public class Cost
{
    @JsonProperty("cost")
    public int costValue;
    public int costPlus;
    public Unit[] costPlusUnit;
    public String costSpecial;
    public String costSpecialPlus;
    public int costPlusPer;
    public int costPlusPerMax;
    public int costMin;
    public int costPermanent;
    public List<Integer> costList;
    public List<Integer> costListPermanent;
    public List<String> costListValues;
}
