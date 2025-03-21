package de.pho.dsapdfreader.dsaconverter.model;

import com.opencsv.bean.CsvBindByName;
import de.pho.dsapdfreader.config.TopicEnum;

public class ContentRaw implements DsaObjectI{
    @CsvBindByName
    public String content;
    @CsvBindByName
    public Boolean isBold;
    @CsvBindByName
    public Boolean isItalic;
    @CsvBindByName
    public Boolean isFinishedSentence;
    @CsvBindByName
    public Integer textSize;

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setTopic(TopicEnum topic) {

    }

    @Override
    public void setPublication(String publication) {

    }
}
