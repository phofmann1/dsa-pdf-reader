package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;

public class PraegungRaw {
    @CsvBindByName
    public String name = "";
    @CsvBindByName
    public String description = "";
    @CsvBindByName
    public String advantages = "";
    @CsvBindByName
    public String disadvantages = "";
    @CsvBindByName
    public String requirementsText = "";

}
