package de.pho.dsapdfreader.config;


import com.opencsv.bean.CsvBindByName;

import de.pho.dsapdfreader.config.TopicEnum;

public class TopicConfiguration
{
    @CsvBindByName(column="publication")
    private String publication;
    @CsvBindByName
    private String pdfName;
    @CsvBindByName
    private String topicString;
    @CsvBindByName
    private int fromPage;
    @CsvBindByName
    private int untilPage;
    @CsvBindByName
    private int startSize;
    @CsvBindByName
    private String startContent;
    @CsvBindByName
    private int nameSize;
    @CsvBindByName
    private int dataSize;
    @CsvBindByName
    private int endSize;
    @CsvBindByName
    private String endContent;

    public TopicConfiguration()
    {
    }

    public TopicEnum getTopic()
    {
        return TopicEnum.valueOf(this.topicString);
    }

    public String getPublication()
    {
        return publication;
    }

    public void setPublication(String publication)
    {
        this.publication = publication;
    }

    public String getPdfName()
    {
        return pdfName;
    }

    public void setPdfName(String pdfName)
    {
        this.pdfName = pdfName;
    }

    public String getTopicString()
    {
        return topicString;
    }

    public void setTopicString(String topicString)
    {
        this.topicString = topicString;
    }

    public int getFromPage()
    {
        return fromPage;
    }

    public void setFromPage(int fromPage)
    {
        this.fromPage = fromPage;
    }

    public int getUntilPage()
    {
        return untilPage;
    }

    public void setUntilPage(int untilPage)
    {
        this.untilPage = untilPage;
    }

    public int getStartSize()
    {
        return startSize;
    }

    public void setStartSize(int startSize)
    {
        this.startSize = startSize;
    }

    public String getStartContent()
    {
        return startContent;
    }

    public void setStartContent(String startContent)
    {
        this.startContent = startContent;
    }

    public int getNameSize()
    {
        return nameSize;
    }

    public void setNameSize(int nameSize)
    {
        this.nameSize = nameSize;
    }

    public int getDataSize()
    {
        return dataSize;
    }

    public void setDataSize(int dataSize)
    {
        this.dataSize = dataSize;
    }

    public int getEndSize()
    {
        return endSize;
    }

    public void setEndSize(int endSize)
    {
        this.endSize = endSize;
    }

    public String getEndContent()
    {
        return endContent;
    }

    public void setEndContent(String endContent)
    {
        this.endContent = endContent;
    }
}
