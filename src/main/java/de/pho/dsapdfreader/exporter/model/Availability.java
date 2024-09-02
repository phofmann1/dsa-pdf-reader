package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

import de.pho.dsapdfreader.dsaconverter.model.CsvCustomConvertEnumCultureList;
import de.pho.dsapdfreader.dsaconverter.model.CsvCustomConvertEnumProfessionList;
import de.pho.dsapdfreader.dsaconverter.model.CsvCustomConvertEnumRegionList;
import de.pho.dsapdfreader.exporter.model.enums.CultureKey;
import de.pho.dsapdfreader.exporter.model.enums.ProfessionKey;
import de.pho.dsapdfreader.exporter.model.enums.RegionKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;
import de.pho.dsapdfreader.exporter.model.enums.WeaponKey;

public abstract class Availability {
  @CsvBindByName
  public WeaponKey weaponKey;
  @CsvCustomBindByName(converter = CsvCustomConvertEnumRegionList.class)
  public List<RegionKey> availableByRegions;
  @CsvCustomBindByName(converter = CsvCustomConvertEnumCultureList.class)
  public List<CultureKey> availableByCultures;
  @CsvCustomBindByName(converter = CsvCustomConvertEnumProfessionList.class)
  public List<ProfessionKey> availableGenerationByProfessions;
  @CsvBindByName
  public TraditionKey availableByTradition;
  @CsvBindByName
  public String availableByCondition;

  public SpecieKey availableBySpecie;

  protected void init(WeaponKey weaponKey) {
    this.weaponKey = weaponKey;
    this.availableByRegions = new ArrayList<>();
    this.availableByCultures = new ArrayList<>();
    this.availableGenerationByProfessions = new ArrayList<>();
  }

  protected void init(WeaponKey weaponKey, Availability template) {
    this.init(weaponKey);
    this.availableByCultures = new ArrayList<>(template.availableByCultures);
    this.availableByRegions = new ArrayList<>(template.availableByRegions);
    this.availableGenerationByProfessions = new ArrayList<>(template.availableGenerationByProfessions);
    this.availableByTradition = template.availableByTradition;
    this.availableBySpecie = template.availableBySpecie;
    this.availableByCondition = template.availableByCondition;
  }
}
