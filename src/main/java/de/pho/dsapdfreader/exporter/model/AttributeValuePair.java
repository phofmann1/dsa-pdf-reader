package de.pho.dsapdfreader.exporter.model;

import java.io.Serializable;

import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;

public class AttributeValuePair implements Serializable {
  public AttributeShort attributeKey;
  public int attributeValue;

  public AttributeValuePair() {
  }

  public AttributeValuePair(AttributeShort attribute, int minValue) {
    this.attributeKey = attribute;
    this.attributeValue = minValue;
  }
}
