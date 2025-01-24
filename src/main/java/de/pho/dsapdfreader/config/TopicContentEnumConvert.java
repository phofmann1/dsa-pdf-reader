package de.pho.dsapdfreader.config;

import com.opencsv.bean.AbstractBeanField;

@SuppressWarnings("rawtypes")
public class TopicContentEnumConvert extends AbstractBeanField {
  @Override
  protected Object convert(String s) {
    return TopicEnum.valueOf(s);
  }
}
