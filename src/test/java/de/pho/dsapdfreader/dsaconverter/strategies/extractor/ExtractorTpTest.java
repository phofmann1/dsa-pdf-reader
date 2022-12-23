package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.pho.dsapdfreader.exporter.model.TP;

class ExtractorTpTest
{

  @BeforeEach
  void setUp()
  {
  }

  @AfterEach
  void tearDown()
  {
  }

  @Test
  void retrieve1W6Plus2()
  {
    TP result = ExtractorTp.retrieve("1W6 + 2");
    assertEquals(1, result.noOfDice);
    assertEquals(6, result.sidesOfDice);
    assertEquals(2, result.tpPlus);
  }


  @Test
  void retrieve2W6Plus5()
  {
    TP result = ExtractorTp.retrieve("2 W 6  + 5");
    assertEquals(2, result.noOfDice);
    assertEquals(6, result.sidesOfDice);
    assertEquals(5, result.tpPlus);
  }


  @Test
  void retrieve3W13()
  {
    TP result = ExtractorTp.retrieve("3W13");
    assertEquals(3, result.noOfDice);
    assertEquals(13, result.sidesOfDice);
    assertEquals(0, result.tpPlus);
  }


  @Test
  void retrieve1W20Plus2()
  {
    TP result = ExtractorTp.retrieve("1 W 20+2");
    assertEquals(1, result.noOfDice);
    assertEquals(20, result.sidesOfDice);
    assertEquals(2, result.tpPlus);
  }
}