package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CraftingComplexityKey
{
  none,
  primitive,
  simple,
  complex;


  public static CraftingComplexityKey parse(String text)
  {
    return switch (text)
        {
          case "prim" -> CraftingComplexityKey.primitive;
          case "einf" -> CraftingComplexityKey.simple;
          default -> text.startsWith("komp") ? CraftingComplexityKey.complex : CraftingComplexityKey.none;
        };
  }

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
