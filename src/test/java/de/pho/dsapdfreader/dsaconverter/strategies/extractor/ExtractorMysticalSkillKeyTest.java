package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;

class ExtractorMysticalSkillKeyTest
{

  @Test
  void extractKeyTextFromText()
  {
    String text = "Test text zum Text testen (ÄÖÜäöüß--&! ..-) Ende";
    String result = ExtractorMysticalSkillKey.extractKeyTextFromText(text);
    assertEquals("TEST_TEXT_ZUM_TEXT_TESTEN_AEOEUEAEOEUESS_UND_ENDE", result);
  }

  @Test
  void extractMysticalSkillKeyFromText()
  {
    MysticalSkillKey result = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(MysticalSkillCategory.spell, "Odem Arcanum");
    assertEquals(MysticalSkillKey.SPELL_ODEM_ARCANUM, result);
  }
}