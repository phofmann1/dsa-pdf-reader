package de.pho.dsapdfreader.exporter.model;

import java.io.Serializable;

import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;

public class AttributeValuePair implements Serializable {
  public AttributeShort attribute;
  public int minValue;

  public AttributeValuePair() {
  }

  public AttributeValuePair(AttributeShort attribute, int minValue) {
    this.attribute = attribute;
    this.minValue = minValue;
  }
}
