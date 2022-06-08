package de.pho.dsapdfreader;

import com.opencsv.bean.CsvBindByName;

public class Test
{
    @CsvBindByName
    public String name;

    @CsvBindByName
    public String age;
}
