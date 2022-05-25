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
    public int fromPage;
    @CsvBindByName
    public int untilPage;
    @CsvBindByName
    public int startSize;
    @CsvBindByName
    public String startContent;
    @CsvBindByName
    public int nameSize;
    @CsvBindByName
    public int dataSize;
    @CsvBindByName
    public int endSize;
    @CsvBindByName
    public String endContent;
    @CsvBindByName
    public String strategyMapping;

    public TopicConfiguration()
    {
    }

    @Override
    public String toString()
    {
        return "TopicConfiguration{" +
            "active=" + active +
            ", publication='" + publication + '\'' +
            ", path='" + path + '\'' +
            ", pdfName='" + pdfName + '\'' +
            ", topic='" + topic + '\'' +
            ", fromPage=" + fromPage +
            ", untilPage=" + untilPage +
            ", startSize=" + startSize +
            ", startContent='" + startContent + '\'' +
            ", nameSize=" + nameSize +
            ", dataSize=" + dataSize +
            ", endSize=" + endSize +
            ", endContent='" + endContent + '\'' +
            '}';
    }

}
