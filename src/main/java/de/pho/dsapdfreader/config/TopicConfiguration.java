package de.pho.dsapdfreader.config;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

public class TopicConfiguration
{
    @CsvBindByName
    public boolean active;
    @CsvBindByName
    public String publication;
    @CsvBindByName
    public String path;
    @CsvBindByName
    public String pdfName;
    @CsvCustomBindByName(converter = TopicEnumConvert.class)
    public TopicEnum topic;
    @CsvBindByName
    public int startPage;
    @CsvBindByName
    public int endPage;
    @CsvBindByName
    public int startAfterLine;
    @CsvBindByName
    public int nameSize;
    @CsvBindByName
    public int dataSize;
    @CsvBindByName
    public int endAfterLine;
    @CsvBindByName
    public String strategyMapping;
    @CsvBindByName
    public String fileAffix;

    @Override
    public String toString()
    {
        return "TopicConfiguration{" +
            "active=" + active +
            ", publication='" + publication + '\'' +
            ", path='" + path + '\'' +
            ", pdfName='" + pdfName + '\'' +
            ", topic='" + topic + '\'' +
            ", startPage=" + startPage +
            ", endPage=" + endPage +
            ", startLine=" + startAfterLine +
            ", nameSize=" + nameSize +
            ", dataSize=" + dataSize +
            ", endLine=" + endAfterLine +
            '}';
    }

}
