package de.pho.dsapdfreader.config;

import com.opencsv.bean.AbstractBeanField;

@SuppressWarnings("rawtypes")
public class TopicEnumConvert extends AbstractBeanField
{
    @Override
    protected Object convert(String s)
    {
        return TopicEnum.valueOf(s);
    }
}
