package de.pho.dsapdfreader.uid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class UidCategoryTest {

  @Test
  void testSimpleIds() {
    assertEquals("att_", UidCategory.attribut.prefix);
    assertEquals("ausr_", UidCategory.ausruestung.prefix);
    assertEquals("rezept_", UidCategory.rezept.prefix);
    assertEquals("sf_", UidCategory.sonderfertigkeit.prefix);
    assertEquals("talent_", UidCategory.talent.prefix);
  }

  @Test
  void testExportIds() {
    // prefix liefert bei dir direkt die Base-ID
    assertEquals("att_", UidCategory.attribut.prefix);
    assertEquals("ausr_", UidCategory.ausruestung.prefix);
    assertEquals("talent_anwendung_", UidCategory.talent_anwendung.prefix);
    assertEquals("magie_zauber_variante_", UidCategory.magie_zauber_variante.prefix);
  }

  @Test
  void testCompositeIdsWithSubcategories() {
    assertEquals("ausr_typ_", UidCategory.ausruestung_typ.prefix);
    assertEquals("ausr_gift_", UidCategory.ausruestung_gift.prefix);
    assertEquals("ausr_elixier_", UidCategory.ausruestung_elixier.prefix);
    assertEquals("ausr_droge_", UidCategory.ausruestung_droge.prefix);
    assertEquals("ausr_waffe_", UidCategory.ausruestung_waffe.prefix);
    assertEquals("ausr_ruestung_", UidCategory.ausruestung_ruestung.prefix);
  }

  @Test
  void testMagieCategoryIds() {
    assertEquals("magie_trick_", UidCategory.magie_trick.prefix);
    assertEquals("magie_zauber_", UidCategory.magie_zauber.prefix);
    assertEquals("magie_zauber_variante_", UidCategory.magie_zauber_variante.prefix);
    assertEquals("magie_ritual_", UidCategory.magie_ritual.prefix);
    assertEquals("magie_ritual_variante_", UidCategory.magie_ritual_variante.prefix);
    assertEquals("magie_handlung_", UidCategory.magie_handlung.prefix);
  }

  @Test
  void testWeiheCategoryIds() {
    assertEquals("weihe_segnung_", UidCategory.weihe_trick.prefix);
    assertEquals("weihe_liturgie_", UidCategory.weihe_liturgie.prefix);
    assertEquals("weihe_liturgie_variante_", UidCategory.weihe_liturgie_variante.prefix);
    assertEquals("weihe_zeremonie_", UidCategory.weihe_zeremonie.prefix);
    assertEquals("weihe_zeremonie_variante_", UidCategory.weihe_zeremonie_variante.prefix);
    assertEquals("weihe_predigt_", UidCategory.weihe_predigt.prefix);
    assertEquals("weihe_vision_", UidCategory.weihe_vision.prefix);
  }

  @Test
  void testTalentCategoryIds() {
    assertEquals("talent_anwendung_", UidCategory.talent_anwendung.prefix);
    assertEquals("talent_einsatz_", UidCategory.talent_einsatz.prefix);
  }

  @Test
  void testNoNullOrEmptyIds() {
    for (UidCategory c : UidCategory.values()) {
      assertNotNull(c.prefix, "ID must not be null: " + c.name());
      assertFalse(c.prefix.isEmpty(), "ID must not be empty: " + c.name());
    }
  }

  @Test
  void testUniqueIds() {
    long distinctCount = java.util.Arrays.stream(UidCategory.values())
        .map(UidCategory::externalId)
        .distinct()
        .count();
    assertEquals(UidCategory.values().length, distinctCount, "All IDs must be unique");
  }
}