package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.Unit;

public class Cost
{

  public String costText;
  // BASE COST
  public int cost;
  public List<Integer> costList;
  public List<String> costListValues;
  public Unit costListUnit;
  public int costMin;


  // PLUS COST
  public int plusCost;
  public int plusCostPerMultiplier;
  public int plusCostPerMax;
  public Unit plusCostUnit;
  public List<Integer> plusCostList;
  public List<String> plusCostListValues;
  public Unit plusCostListUnit;
  public boolean plusCostHalfBase;


  // PERMANENT COST
  public int permanentCost;
  public List<Integer> permanentCostList;
  public List<String> permanentCostValueList;
  public Unit permanentCostUnit;


  public int getCost()
  {
    return cost;
  }

  public int getCostMin()
  {
    return costMin;
  }


  public int getPlusCost()
  {
    return plusCost;
  }

  public int getPlusCostPerMultiplier()
  {
    return plusCostPerMultiplier;
  }

  public Unit getPlusCostUnit()
  {
    return plusCostUnit;
  }

  public int getPlusCostPerMax()
  {
    return plusCostPerMax;
  }


  public int getPermanentCost()
  {
    return permanentCost;
  }

  public List<Integer> getCostList()
  {
    return costList;
  }

  public List<Integer> getPermanentCostList()
  {
    return permanentCostList;
  }

  public List<Integer> getPlusCostList()
  {
    return plusCostList;
  }

  public List<String> getPlusCostListValues()
  {
    return plusCostListValues;
  }

  public List<String> getCostListValues()
  {
    return costListValues;
  }

  public Unit getCostListUnit()
  {
    return costListUnit;
  }

  public List<String> getPermanentCostValueList()
  {
    return permanentCostValueList;
  }

  public Unit getPermanentCostUnit()
  {
    return permanentCostUnit;
  }

  public Unit getPlusCostListUnit()
  {
    return plusCostListUnit;
  }

  public boolean isPlusCostHalfBase()
  {
    return plusCostHalfBase;
  }

  public String getCostText()
  {
    return costText;
  }
}

