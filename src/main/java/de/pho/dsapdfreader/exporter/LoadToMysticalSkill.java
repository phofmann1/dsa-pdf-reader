package de.pho.dsapdfreader.exporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Sextet;
import org.javatuples.Triplet;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAdvancementCategory;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCastingDuration;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCheck;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorElementKeys;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorFeature;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillCost;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillDifficulty;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillModifications;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillVariant;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkillDuration;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkillKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkillRange;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorTargetCategory;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorTradtion;
import de.pho.dsapdfreader.exporter.model.CastingDuration;
import de.pho.dsapdfreader.exporter.model.Cost;
import de.pho.dsapdfreader.exporter.model.MysticalSkill;
import de.pho.dsapdfreader.exporter.model.RequirementMysticalSkill;
import de.pho.dsapdfreader.exporter.model.enums.AdvancementCategory;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillFeature;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.PatronAnimalKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;
import de.pho.dsapdfreader.exporter.model.enums.Unit;
import de.pho.dsapdfreader.tools.merger.ObjectMerger;


public class LoadToMysticalSkill
{
  protected static final Logger LOGGER = LogManager.getLogger();

  protected static final List<Sextet<PatronAnimalKey, String, Integer, AdvancementCategory, Integer, AdvancementCategory>> ANIMAL_PATRONS = List.of(
      new Sextet(PatronAnimalKey.adler, "Adler", 6, AdvancementCategory.B, 6, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.bär, "Bär", 12, AdvancementCategory.C, 12, AdvancementCategory.C),
      new Sextet(PatronAnimalKey.eule, "Eule", 6, AdvancementCategory.B, 6, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.falke, "Falke", 6, AdvancementCategory.B, 6, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.feuermolch, "Feuermolch", 4, AdvancementCategory.A, 4, AdvancementCategory.A),
      new Sextet(PatronAnimalKey.fischotter, "Fischotter", 4, AdvancementCategory.A, 4, AdvancementCategory.A),
      new Sextet(PatronAnimalKey.gebirgsbock, "Gebirgsbock", 4, AdvancementCategory.A, 4, AdvancementCategory.A),
      new Sextet(PatronAnimalKey.gepard_jaguar, "Gepard/Jaguar", 12, AdvancementCategory.C, 12, AdvancementCategory.C),
      new Sextet(PatronAnimalKey.khoramsbestie, "Khoramsbestie", 12, AdvancementCategory.B, 12, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.khoramswühler, "Khoramswühler", 6, AdvancementCategory.A, 6, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.löwe, "Löwe", 12, AdvancementCategory.C, 12, AdvancementCategory.C),
      new Sextet(PatronAnimalKey.luchs, "Luchs", 12, AdvancementCategory.B, 6, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.mammut, "Mammut", 16, AdvancementCategory.D, 16, AdvancementCategory.D),
      new Sextet(PatronAnimalKey.mungo, "Mungo", 6, AdvancementCategory.B, 6, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.nashorn, "Nashorn", 14, AdvancementCategory.C, 14, AdvancementCategory.C),
      new Sextet(PatronAnimalKey.nebelkrähe, "Nebelkrähe", 6, AdvancementCategory.B, 6, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.pferd, "Pferd", 12, AdvancementCategory.B, 12, AdvancementCategory.C),
      new Sextet(PatronAnimalKey.rabe, "Rabe", 4, AdvancementCategory.B, 4, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.säbelzahntiger, "Säbelzahntiger", 14, AdvancementCategory.C, 14, AdvancementCategory.C),
      new Sextet(PatronAnimalKey.sandwolf, "Sandwolf", 8, AdvancementCategory.B, 12, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.stier, "Stier", 10, AdvancementCategory.C, 12, AdvancementCategory.C),
      new Sextet(PatronAnimalKey.vielfraß, "Vielfraß", 8, AdvancementCategory.B, 8, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.widder, "Widder", 4, AdvancementCategory.A, 6, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.wildkatze, "Wildkatze", 4, AdvancementCategory.A, 6, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.wildschwein, "Wildschwein", 8, AdvancementCategory.B, 8, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.wolf, "Wolf", 8, AdvancementCategory.B, 8, AdvancementCategory.B),
      new Sextet(PatronAnimalKey.würgeschlange, "Würgeschlange", 6, AdvancementCategory.B, 6, AdvancementCategory.B)
  );

  protected static List<Triplet<String, Integer, AdvancementCategory>> RUNE_ENTITIES = List.of(
      Triplet.with("Alfen (Alfibanruna)", 12, AdvancementCategory.B),
      Triplet.with("Daimonide & Chimären (Skepnabanruna)", 12, AdvancementCategory.B),
      Triplet.with("Dämonen (Vondurbanruna)", 18, AdvancementCategory.C),
      Triplet.with("Elementare (Verabanruna)", 18, AdvancementCategory.C),
      Triplet.with("Geister (Vandrendabanruna)", 12, AdvancementCategory.B),
      Triplet.with("Hranngargezücht (Fylgjaruna)", 18, AdvancementCategory.C),
      Triplet.with("Untote (Draugerbanruna)", 12, AdvancementCategory.B)
  );

  protected static List<Triplet<String, Integer, AdvancementCategory>> BANSIGN_ENTITIES = List.of(
      Triplet.with("Feen", 12, AdvancementCategory.B),
      Triplet.with("Daimonide & Chimären", 12, AdvancementCategory.B),
      Triplet.with("Dämonen", 18, AdvancementCategory.C),
      Triplet.with("Geister", 12, AdvancementCategory.B),
      Triplet.with("Spinnen", 12, AdvancementCategory.A),
      Triplet.with("Untote", 12, AdvancementCategory.B)
  );

  private LoadToMysticalSkill()
  {
  }

  public static Stream<MysticalSkill> migrate(MysticalSkillRaw msr, MysticalSkillCategory msCategory)
  {

    List<MysticalSkill> returnList = new ArrayList<>();

    MysticalSkill ms = initMysticalSkill(msr, msCategory);


    if (msr.name.equals("Mächtiger Patronruf (Tierart) I/II")
        || msr.name.equals("Tierverwandlung (Tierart)")
        || msr.name.equals("Tierkräfte I-III")
        || msr.name.equals("Bannzeichen wider (Wesenheit)")
        || msr.name.equals("Schutzrune vor (Wesenheit)")
    )
    {
      returnList.addAll(initCodedMysticalSkills(ms, msr, msCategory));
    }
    else
    {
      ms.name = msr.name;
      ms.key = ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr, ms.category);
      ms.advancementCategory = ExtractorAdvancementCategory.retrieveAdvancementCategory(msr);
      ms.spellVariants = ExtractorMysticalSkillVariant.retrieveMysticalSkillVariants(msr, ms.key);
      returnList.add(ms);
    }


    return returnList.stream();
  }

  private static List<? extends MysticalSkill> initCodedMysticalSkills(MysticalSkill ms, MysticalSkillRaw msr, MysticalSkillCategory msCategory)
  {
    return switch (msr.name)
        {
          case "Mächtiger Patronruf (Tierart) I/II" -> mightyPatron(ms, msr, msCategory);
          case "Tierverwandlung (Tierart)" -> animalTransformation(ms, msr, msCategory);
          case "Tierkräfte I-III" -> animalPowers(ms, msr, msCategory);
          case "Bannzeichen wider (Wesenheit)" -> bansignAgainst(ms, msr, msCategory);
          case "Schutzrune vor (Wesenheit)" -> runeOfProtectionAgainst(ms, msr, msCategory);
          default -> new ArrayList<>();
        };
  }


  private static List<? extends MysticalSkill> bansignAgainst(MysticalSkill ms, MysticalSkillRaw msr, MysticalSkillCategory msCategory)
  {

    List<MysticalSkill> returnValue = new ArrayList<>();

    BANSIGN_ENTITIES.forEach(e -> {
      MysticalSkill nMs = ObjectMerger.merge(ms, new MysticalSkill());
      nMs.name = msr.name.replace("Wesenheit", e.getValue0());
      nMs.key = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(msCategory, nMs.name);
      nMs.skillCost = new Cost();
      nMs.skillCost.cost = e.getValue1();
      nMs.advancementCategory = e.getValue2();
      returnValue.add(nMs);
    });

    return returnValue;
  }

  private static List<? extends MysticalSkill> runeOfProtectionAgainst(MysticalSkill ms, MysticalSkillRaw msr, MysticalSkillCategory msCategory)
  {
    List<MysticalSkill> returnValue = new ArrayList<>();

    RUNE_ENTITIES.forEach(e -> {
      MysticalSkill nMs = ObjectMerger.merge(ms, new MysticalSkill());
      nMs.name = msr.name.replace("(Wesenheit)", " - " + e.getValue0());
      nMs.key = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(msCategory, nMs.name);
      nMs.skillCost = new Cost();
      nMs.skillCost.cost = e.getValue1();
      nMs.advancementCategory = e.getValue2();
      returnValue.add(nMs);
    });


    return returnValue;
  }


  private static List<? extends MysticalSkill> animalPowers(MysticalSkill ms, MysticalSkillRaw msr, MysticalSkillCategory msCategory)
  {
    List<MysticalSkill> returnValue = new ArrayList<>();

    MysticalSkill nMs1 = ObjectMerger.merge(ms, new MysticalSkill());
    nMs1.name = msr.name.replace("I-III", "I");
    nMs1.key = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(msCategory, nMs1.name);
    nMs1.advancementCategory = AdvancementCategory.B;
    returnValue.add(nMs1);

    MysticalSkill nMs2 = ObjectMerger.merge(ms, new MysticalSkill());
    nMs2.name = msr.name.replace("I-III", "II");
    nMs2.key = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(msCategory, nMs2.name);
    nMs2.requirementMysticalSkill = new RequirementMysticalSkill();
    //nMs.requirementMysticalSkill.key = MysticalSkillKey.power_tierkräfte_i;
    nMs2.requirementMysticalSkill.minValue = 10;
    nMs2.advancementCategory = AdvancementCategory.B;
    returnValue.add(nMs2);

    MysticalSkill nMs3 = ObjectMerger.merge(ms, new MysticalSkill());
    nMs3.name = msr.name.replace("I-III", "III");
    nMs3.key = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(msCategory, nMs3.name);
    nMs3.requirementMysticalSkill = new RequirementMysticalSkill();
    //nMs.requirementMysticalSkill.key = MysticalSkillKey.power_tierkräfte_ii;
    nMs3.requirementMysticalSkill.minValue = 10;
    nMs3.advancementCategory = AdvancementCategory.B;
    returnValue.add(nMs3);
    return returnValue;
  }

  private static List<? extends MysticalSkill> animalTransformation(MysticalSkill ms, MysticalSkillRaw msr, MysticalSkillCategory msCategory)
  {
    List<MysticalSkill> returnValue = new ArrayList<>();

    ANIMAL_PATRONS.forEach(p -> {
      MysticalSkill nMs = ObjectMerger.merge(ms, new MysticalSkill());
      nMs.name = msr.name
          .replace("Tierart", p.getValue1());
      nMs.key = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(msCategory, nMs.name);
      nMs.skillCost = new Cost();
      nMs.skillCost.cost = p.getValue4();
      nMs.skillCost.plusCostHalfBase = true;
      nMs.skillCost.plusCost = p.getValue4() / 2;
      nMs.skillCost.plusCostUnit = Unit.MINUTE;
      nMs.skillCost.plusCostPerMultiplier = 30;
      nMs.advancementCategory = p.getValue5();
      nMs.requirementMysticalSkill = new RequirementMysticalSkill();
      //nMs.requirementMysticalSkill.key = MysticalSkillKey.power_tierkräfte_iii;
      nMs.requirementMysticalSkill.minValue = 10;

      returnValue.add(nMs);
    });
    return returnValue;
  }

  private static List<? extends MysticalSkill> mightyPatron(MysticalSkill ms, MysticalSkillRaw msr, MysticalSkillCategory msCategory)
  {
    List<MysticalSkill> returnValue = new ArrayList<>();
    ANIMAL_PATRONS.forEach(p -> {
      MysticalSkill nMs1 = ObjectMerger.merge(ms, new MysticalSkill());
      nMs1.name = msr.name
          .replace("Tierart", p.getValue1())
          .replace("I/II", "I");
      nMs1.key = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(msCategory, nMs1.name);
      nMs1.skillCost = new Cost();
      nMs1.skillCost.cost = p.getValue2();
      nMs1.advancementCategory = p.getValue3();
      nMs1.requirementMysticalSkill = new RequirementMysticalSkill();
      nMs1.requirementMysticalSkill.key = MysticalSkillKey.power_patronruf;
      nMs1.requirementMysticalSkill.minValue = 10;


      MysticalSkill nMs2 = ObjectMerger.merge(nMs1, new MysticalSkill());
      nMs2.name = msr.name
          .replace("Tierart", p.getValue1())
          .replace("I/II", "II");
      nMs2.key = ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(ms.category, nMs2.name);
      nMs2.requirementMysticalSkill = new RequirementMysticalSkill();
      nMs2.requirementMysticalSkill.key = nMs1.key;
      nMs2.requirementMysticalSkill.minValue = 10;

      returnValue.add(nMs1);
      returnValue.add(nMs2);
    });
    return returnValue;
  }

  private static MysticalSkill initMysticalSkill(MysticalSkillRaw msr, MysticalSkillCategory msCategory)
  {
    MysticalSkill ms = new MysticalSkill();
    ms.features = (msCategory == MysticalSkillCategory.magicSign && (msr.feature == null || msr.feature.isEmpty())) ? List.of(MysticalSkillFeature.defensive) : ExtractorFeature.retrieveFeatures(msr);
    ms.publication = Publication.valueOf(msr.publication);
    ms.category = msCategory;
    List<CastingDuration> castingDurations = ExtractorCastingDuration.retrieveCastingDurations(msr, ms.category);
    castingDurations.sort((a, b) -> a.castingDurationUnit == null
        ? -1
        : (b.castingDurationUnit == null ? 1 : a.castingDurationUnit.toValue() - b.castingDurationUnit.toValue())
    );
    if (!castingDurations.isEmpty())
    {
      ms.casting = castingDurations.get(0);
    }
    if (castingDurations.size() > 1)
    {
      ms.castingLong = castingDurations.get(1);
    }
    if (castingDurations.size() > 2)
    {
      LOGGER.error("Too many casting durations for (" + ms.name + "): " + msr.castingDuration);
    }

    ms.targetCategories = ExtractorTargetCategory.retrieveTargetCategories(msr);
    ms.skillRange = ExtractorSkillRange.retrieveSkillRange(msr);
    ms.traditions = ExtractorTradtion.retrieveTraditions(msr, ms.category);

    if (
        ms.traditions.contains(TraditionKey.DANCER)
            || ms.traditions.contains(TraditionKey.BARDE)
    )
    {
      ms.traditionSubs = ExtractorTradtion.retrieveTraditionSubs(msr);
    }
    ms.traditionIncantationMap = ExtractorTradtion.retrieveIncantations(msr);
    ms.skillDuration = ExtractorSkillDuration.retrieveSkillDuration(msr);
    ms.skillCost = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);
    ms.allowedModifications = ExtractorMysticalSkillModifications.retrieveAllowedModifications(msr);
    ms.difficulty = ExtractorMysticalSkillDifficulty.retrieveDifficulty(msr);
    ms.skillKeys = ExtractorSkillKey.retrieveSkillKeysForMysticalSkillRaw(msr);
    ms.elementalCategories = ExtractorElementKeys.retrieveElementKeys(msr);

    if (msCategory != MysticalSkillCategory.magicSign)
    {
      ms.check = ExtractorCheck.retrieveCheck(msr, ms.category);
    }

    return ms;
  }

  public static void applyCorrections(MysticalSkill ms, List<MysticalSkill> corrections)
  {
    Optional<MysticalSkill> correction = corrections.stream().filter(c -> c.key == ms.key).findFirst();
    correction.ifPresent(mysticalSkill -> ObjectMerger.merge(mysticalSkill, ms));

  }
}
