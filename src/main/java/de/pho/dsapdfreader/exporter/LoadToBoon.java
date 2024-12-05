package de.pho.dsapdfreader.exporter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.dsaconverter.model.BoonRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAP;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorBoon;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorRequirements;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkill;
import de.pho.dsapdfreader.exporter.model.Boon;
import de.pho.dsapdfreader.exporter.model.BoonVariant;
import de.pho.dsapdfreader.exporter.model.RequirementBoon;
import de.pho.dsapdfreader.exporter.model.SkillApplication;
import de.pho.dsapdfreader.exporter.model.SkillUsage;
import de.pho.dsapdfreader.exporter.model.ValueChange;
import de.pho.dsapdfreader.exporter.model.enums.BoonCategory;
import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.BoonVariantKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SelectionCategory;
import de.pho.dsapdfreader.exporter.model.enums.SkillCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.ValueChangeKey;
import de.pho.dsapdfreader.exporter.model.enums.ValueChangeType;
import de.pho.dsapdfreader.tools.merger.ObjectMerger;


public class LoadToBoon {
  protected static final int INFINITE = 999;
  protected static final Logger LOGGER = LogManager.getLogger();

  protected static final Map<BoonKey, List<BoonVariant>> BOON_VARIANTS = new EnumMap<>(BoonKey.class) {{
    put(BoonKey.biss, List.of(
        new BoonVariant(BoonVariantKey.biss_winzig, "winzig", 0, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.biss_klein, "klein", 1, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.biss_mittel, "mittel", 2, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.biss_groß, "groß", 3, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.biss_riesig, "riesig", 4, new ArrayList<>())
    ));
    put(BoonKey.hass_auf_x, List.of(
        new BoonVariant(BoonVariantKey.hass_auf_x_häufig, "häufig", 15, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.hass_auf_x_gelegentlich, "gelegentlich", 10, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.hass_auf_x_selten, "selten", 5, new ArrayList<>())
    ));
    put(BoonKey.herausragender_sinn, List.of(
        new BoonVariant(BoonVariantKey.herausragender_sinn_sicht, "Sicht", 12, List.of(new RequirementBoon(BoonKey.blind, false))),
        new BoonVariant(BoonVariantKey.herausragender_sinn_gehör, "Gehör", 12, List.of(new RequirementBoon(BoonKey.taub, false))),
        new BoonVariant(BoonVariantKey.herausragender_sinn_geruch_und_geschmack, "Geruch und Geschmack", 6, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.herausragender_sinn_tastsinn, "Tastsinn", 2, new ArrayList<>())
    ));
    put(BoonKey.angst_vor_x, List.of(
        new BoonVariant(BoonVariantKey.angst_vor_x_tierart, "vor Tierart"),
        new BoonVariant(BoonVariantKey.angst_vor_x_blut, "Blut"),
        new BoonVariant(BoonVariantKey.angst_vor_x_dunkelheit, "Dunkelangst"),
        new BoonVariant(BoonVariantKey.angst_vor_x_höhe, "Höhenangst"),
        new BoonVariant(BoonVariantKey.angst_vor_x_dem_meer, "Meeresangst"),
        new BoonVariant(BoonVariantKey.angst_vor_x_engen_räumen, "Raumangst"),
        new BoonVariant(BoonVariantKey.angst_vor_x_toten_und_untoten, "Totenangst")
    ));

    put(BoonKey.eingeschränkter_sinn, List.of(
        new BoonVariant(BoonVariantKey.eingeschränkter_sinn_sicht, "Sicht", -15, List.of(new RequirementBoon(BoonKey.blind, false))),
        new BoonVariant(BoonVariantKey.eingeschränkter_sinn_gehör, "Gehör", -10, List.of(new RequirementBoon(BoonKey.taub, false))),
        new BoonVariant(BoonVariantKey.eingeschränkter_sinn_geruch_und_geschmack, "Geruch und Geschmack", -6, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.eingeschränkter_sinn_tastsinn, "Tastsinn", -2, new ArrayList<>())
    ));

    put(BoonKey.persönlichkeitsschwächen, new ArrayList<>(
        /*
        List.of(
        new BoonVariant(BoonVariantKey.persönlichkeitsschwäche_arroganz, "Arroganz", -10, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.persönlichkeitsschwäche_eitelkeit, "Eitelkeit", -10, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.persönlichkeitsschwäche_neid, "Neid", -5, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.persönlichkeitsschwäche_streitsucht, "Streitsucht", -10, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.persönlichkeitsschwäche_unheimlich, "Unheimlich", -8, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.persönlichkeitsschwäche_verwöhnt, "Verwöhnt", -10, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.persönlichkeitsschwäche_vorurteile, "Vorurteile", -5, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.persönlichkeitsschwäche_weltfremd, "Weltfremd", -10, new ArrayList<>())
        */

    ));
    put(BoonKey.prinzipientreue, List.of(
        new BoonVariant(BoonVariantKey.prinzipientreue_i_, ""),
        new BoonVariant(BoonVariantKey.prinzipientreue_ii_, ""),
        new BoonVariant(BoonVariantKey.prinzipientreue_iii_, "")
    ));

    put(BoonKey.schlechte_angewohnheit, List.of(
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_barfüßer, "Barfüßer"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_dritte_person, "dritte Person"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_duzer, "Duzer"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_erster, "Erster"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_heulsuse, "Heulsuse"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_hypochonder, "Hypochonder"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_junge, "Junge"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_langschläfer, "Langschläfer"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_links_rechts_schwäche, "Links-Rechts Schwäche"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_mein_kind, "Mein Kind"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_nägelkauer, "Nägelkauer"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_nase_ohrenbohrer, "Nase-/Ohrenbohrer"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_nervös, "Nervös"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_raucher, "Raucher"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_putzfimmel, "Putzfimmel"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_redet_wie_ein_wasserfall, "Redet wie ein Wasserfall"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_schlechte_tischmanieren, "Schlechte Tischmanieren"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_selbstgespräche, "Selbstgespräche"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_unordentlich, "Unordentlich"),
        new BoonVariant(BoonVariantKey.schlechte_angewohnheit_wir, "Wir")
    ));

    put(BoonKey.schlechte_eigenschaft, List.of(
        new BoonVariant(BoonVariantKey.schlechte_eigenschaften_aberglaube, "Aberglaube", -5, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.schlechte_eigenschaften_autoritätsgläubig, "Autoritätsgläubig", -5, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.schlechte_eigenschaften_geiz, "Geiz", -5, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.schlechte_eigenschaften_goldgier, "Goldgier", -5, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.schlechte_eigenschaften_jähzorn, "Jähzorn", -10, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.schlechte_eigenschaften_kleptomanie, "Kleptomanie", -10, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.schlechte_eigenschaften_naiv, "Naiv", -10, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.schlechte_eigenschaften_neugier, "Neugier", -5, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.schlechte_eigenschaften_rachsucht, "Rachsucht", -5, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.schlechte_eigenschaften_spielsucht, "Spielsucht", -5, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.schlechte_eigenschaften_verschwendungssucht, "Verschwendungssucht", -5, new ArrayList<>())
    ));

    put(BoonKey.stigma, List.of(
        new BoonVariant(BoonVariantKey.stigma_albino, "Albino"),
        new BoonVariant(BoonVariantKey.stigma_grüne_haare, "Grüne Haare"),
        new BoonVariant(BoonVariantKey.stigma_brandmale, "Brandmale"),
        new BoonVariant(BoonVariantKey.stigma_katzenhafte_augen, "Katzenhafte Augen"),
        new BoonVariant(BoonVariantKey.stigma_schlangenschuppen, "Schlangenschuppen")
    ));
    put(BoonKey.verstümmelt, List.of(
        new BoonVariant(BoonVariantKey.verstümmelung_einarmig, "Einarmig", -30, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.verstümmelung_einäugig, "Einäugig", -10, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.verstümmelung_einbeinig, "Einbeinig", -30, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.verstümmelung_einhändig, "Einhändig", -20, new ArrayList<>()),
        new BoonVariant(BoonVariantKey.verstümmelung_einohrig, "Einohrig", -5, new ArrayList<>())
    ));
  }};

  protected static final Map<BoonKey, List<RequirementBoon>> NONE_OF_BOONS = new EnumMap<>(BoonKey.class) {{
    put(BoonKey.entfernungssinn, List.of(
        new RequirementBoon(BoonKey.blind, false),
        new RequirementBoon(BoonKey.eingeschränkter_sinn, false, BoonVariantKey.eingeschränkter_sinn_sicht),
        new RequirementBoon(BoonKey.verstümmelt, false, BoonVariantKey.verstümmelung_einäugig)
    ));
    put(BoonKey.flink, List.of(
        new RequirementBoon(BoonKey.behäbig, false),
        new RequirementBoon(BoonKey.fettleibig, false),
        new RequirementBoon(BoonKey.verstümmelt, false, BoonVariantKey.verstümmelung_einbeinig)
    ));
    put(BoonKey.koboldfreund, List.of(
        new RequirementBoon(BoonKey.hass_auf_x, false, "Kobolde"),
        new RequirementBoon(BoonKey.schlechte_eigenschaft, false, BoonVariantKey.persönlichkeitsschwäche_vorurteile, "Kobolde")
    ));
    put(BoonKey.richtungssinn, List.of(new RequirementBoon(BoonKey.unfähig, false, true)));
    put(BoonKey.tierfreund, List.of(new RequirementBoon(BoonKey.hass_auf_x, false, true)));
    put(BoonKey.blind, List.of(
        new RequirementBoon(BoonKey.herausragender_sinn, false, BoonVariantKey.herausragender_sinn_sicht),
        new RequirementBoon(BoonKey.verstümmelt, false, BoonVariantKey.verstümmelung_einäugig),
        new RequirementBoon(BoonKey.farbenblind, false),
        new RequirementBoon(BoonKey.nachtblind, false),
        new RequirementBoon(BoonKey.eingeschränkter_sinn, false, BoonVariantKey.eingeschränkter_sinn_sicht)
    ));
    put(BoonKey.allerweltsname, List.of(
        new RequirementBoon(BoonKey.heldenhafter_name, false),
        new RequirementBoon(BoonKey.schurkenname, false),
        new RequirementBoon(BoonKey.lächerlicher_name, false)
    ));
    put(BoonKey.begabung, List.of(new RequirementBoon(BoonKey.unfähig, false, true)));
    put(BoonKey.herausragender_sinn, List.of(new RequirementBoon(BoonKey.eingeschränkter_sinn, false, true)));
    put(BoonKey.soziale_anpassungsfähigkeit, List.of(
        new RequirementBoon(BoonKey.unfrei, false),
        new RequirementBoon(BoonKey.unfähig, false, SkillCategoryKey.gesellschaftstalente)
    ));
    put(BoonKey.blutrausch, List.of(
        new RequirementBoon(BoonKey.schlechte_eigenschaft, false, BoonVariantKey.schlechte_eigenschaften_jähzorn),
        new RequirementBoon(BoonKey.angst_vor_x, false, BoonVariantKey.angst_vor_x_blut)

    ));
    put(BoonKey.eingeschränkter_sinn, List.of(new RequirementBoon(BoonKey.herausragender_sinn, false, true)));
    put(BoonKey.schurkenname, List.of(
        new RequirementBoon(BoonKey.heldenhafter_name, false),
        new RequirementBoon(BoonKey.unpassender_name, false),
        new RequirementBoon(BoonKey.lächerlicher_name, false)
    ));
    put(BoonKey.unpassender_name, List.of(
        new RequirementBoon(BoonKey.heldenhafter_name, false),
        new RequirementBoon(BoonKey.schurkenname, false),
        new RequirementBoon(BoonKey.lächerlicher_name, false)
    ));
  }};

  private LoadToBoon() {
  }

  public static Boon migrate(BoonRaw raw) {
    Boon returnValue = new Boon();

    int levels = LoadToSpecialAbility.extractLevels(raw.name);
    String baseName = (levels > 1
        ? raw.name.split("(?= (I-|I\\/))")[0]
        : raw.name)
        .replace("Ahnenblut-Vorteile", "")
        .replace("Feenblut-Vorteile", "");

    returnValue.name = baseName;
    returnValue.key = ExtractorBoon.retrieve(baseName);

    if (returnValue.key == BoonKey.dunkelsicht) { // korrektur, da das Sternchen durch die Level abgeschnitten wurde.
      returnValue.name = returnValue.name + " (*)";
    }
    returnValue.publications = List.of(Publication.valueOf(raw.publication));
    returnValue.levels = levels;

    returnValue.ap = Float.valueOf(ExtractorAP.retrieve(raw.ap, 0));
    returnValue.category = returnValue.ap < 0 ? BoonCategory.FLAW : BoonCategory.MERIT;
    returnValue.selectable = !raw.name.contains("(*)");
    returnValue.variants = BOON_VARIANTS.containsKey(returnValue.key) ? BOON_VARIANTS.get(returnValue.key) : new ArrayList<>();
    try {
      returnValue.requiredTraditions = ExtractorRequirements.extractTraditionKeysFromText(raw.preconditions);
    }
    catch (IllegalArgumentException e) {
      LOGGER.error(e.getMessage());
    }

    SkillUsage su = ExtractorSkill.retrieveSkillUsage(raw.rules);
    if (su != null) {
      if (returnValue.valueChanges == null) returnValue.valueChanges = new ArrayList<>();
      su.skillKeys.forEach(sk -> {
        ValueChange vc = new ValueChange();
        vc.key = ValueChangeKey.skill;
        vc.type = ValueChangeType.newSkillUsage;
        vc.skillKey = sk;
        vc.newSkillUsageKey = su.key;
        returnValue.valueChanges.add(vc);
      });
    }

    SkillApplication sa = ExtractorSkill.retrieveSkillApplication(raw.rules, returnValue.name);
    if (sa != null) {
      if (returnValue.valueChanges == null) returnValue.valueChanges = new ArrayList<>();
      ValueChange vc = new ValueChange();
      vc.key = ValueChangeKey.skill;
      vc.type = ValueChangeType.newSkillApplication;
      vc.skillKey = sa.skillKey;
      vc.newSkillApplicationKey = sa.key;
      returnValue.valueChanges.add(vc);
    }

    returnValue.multiselect = extractMulitselect(returnValue.key);
    returnValue.selectionCategory = extractSelectionCategory(returnValue.key);
    returnValue.requirementsSpecie = ExtractorRequirements.extractSpecieReqsForBoon(returnValue.key);
    returnValue.requiredCulture = ExtractorRequirements.extractCultureReqsForBoon(returnValue.key);
    if (NONE_OF_BOONS.containsKey(returnValue.key)) {
      returnValue.requirementBoons = NONE_OF_BOONS.get(returnValue.key);
    }
    else {
      returnValue.requirementBoons = ExtractorRequirements.extractRequirementsBoon(raw.preconditions, returnValue.name);
    }

    returnValue.isBloodLine = raw.name.startsWith("Feenblut")
        || raw.name.startsWith("Drachenblut")
        || returnValue.name.startsWith("Wolfsblut");

    return returnValue;
  }

  private static SelectionCategory extractSelectionCategory(BoonKey key) {
    if (key == null) return null;
    return switch (key) {
      case artefaktgebunden -> SelectionCategory.artifact;
      case herausragende_kampftechnik, waffenbegabung -> SelectionCategory.combatSkill;
      case begabung_magisch, begabung_klerikal -> SelectionCategory.mysticalSkill;
      case immunität_gegen_krankheit -> SelectionCategory.desease;
      case immunität_gegen_gift -> SelectionCategory.poison;
      case herausragende_fertigkeit, begabung, unfähig -> SelectionCategory.skill;
      default -> null;
    };
  }

  private static int extractMulitselect(BoonKey key) {
    if (key == null) return -1;
    return switch (key) {
      case unfähig, eingeschränkter_sinn, körperliche_auffälligkeit, persönlichkeitsschwächen -> 2;
      case begabung, waffenbegabung, begabung_magisch, begabung_klerikal -> 3;
      case schlechte_angewohnheit, schlechte_eigenschaft, herausragende_fertigkeit, herausragender_sinn -> INFINITE;
      default -> 1;
    };
  }

  public static void applyCorrections(Boon b, List<Boon> corrections) {
    Optional<Boon> correction = corrections.stream().filter(c -> c.key == b.key).findFirst();
    if (correction.isPresent()) {
      ObjectMerger.merge(correction.get(), b);
    }
  }
}
