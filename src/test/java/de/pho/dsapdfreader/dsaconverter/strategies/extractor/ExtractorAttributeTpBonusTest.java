package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.pho.dsapdfreader.exporter.model.AttributeTpBonus;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;

class ExtractorAttributeTpBonusTest
{

  @Test
  void retrieveKK14()
  {
    List<AttributeTpBonus> result = ExtractorAttributeTpBonus.retrieve("KK14");
    assertEquals(1, result.size());
    assertEquals(AttributeShort.KK, result.get(0).attribute);
    assertEquals(14, result.get(0).minValue);
  }

  @Test
  void retrieveKKGE14()
  {
    List<AttributeTpBonus> result = ExtractorAttributeTpBonus.retrieve("KK/GE 14");
    assertEquals(2, result.size());
    assertEquals(AttributeShort.GE, result.get(0).attribute);
    assertEquals(14, result.get(0).minValue);
    assertEquals(AttributeShort.KK, result.get(1).attribute);
    assertEquals(14, result.get(1).minValue);
  }

  @Test
  void retrieveKK14GE16()
  {
    List<AttributeTpBonus> result = ExtractorAttributeTpBonus.retrieve("KK 14 / GE 16");
    assertEquals(2, result.size());
    assertEquals(AttributeShort.KK, result.get(0).attribute);
    assertEquals(14, result.get(0).minValue);
    assertEquals(AttributeShort.GE, result.get(1).attribute);
    assertEquals(16, result.get(1).minValue);
  }

  @Test
  void retrieveKKGE1416()
  {
    List<AttributeTpBonus> result = ExtractorAttributeTpBonus.retrieve("KK/GE 14/16");
    assertEquals(2, result.size());
    assertEquals(AttributeShort.KK, result.get(0).attribute);
    assertEquals(14, result.get(0).minValue);
    assertEquals(AttributeShort.GE, result.get(1).attribute);
    assertEquals(16, result.get(1).minValue);
  }
}