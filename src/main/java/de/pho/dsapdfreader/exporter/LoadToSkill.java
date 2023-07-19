package de.pho.dsapdfreader.exporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.dsaconverter.model.SkillRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorAdvancementCategory;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCheck;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkillKey;
import de.pho.dsapdfreader.exporter.model.Skill;
import de.pho.dsapdfreader.exporter.model.SkillUsage;
import de.pho.dsapdfreader.exporter.model.enums.ConditionalBoolean;
import de.pho.dsapdfreader.exporter.model.enums.SkillCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;


public class LoadToSkill
{

  private LoadToSkill()
  {
  }

  public static List<SkillUsage> migrateUsage(SkillRaw raw)
  {
    List<SkillUsage> returnValue = new ArrayList<>();

    List<String> usageStrings = retrieveUsageStrings(raw.application);

    AtomicReference<SkillKey> skillKey = new AtomicReference<>(ExtractorSkillKey.retrieveSkillKey(raw.name.replace("\u00AD", "-").replace("Einsatzmöglichkeit", "")));


    usageStrings.forEach(s -> {
      SkillUsage r = new SkillUsage();
      r.name = s
          .replace("\u00AD", "-")
          .replaceAll("-\\s*", "");
      r.key = retrieveUsageKey(s);
      r.skillKeys.add(skillKey.get());
      returnValue.add(r);
    });
    return returnValue;
  }

  public static SkillUsageKey retrieveUsageKey(String s)
  {
    return ExtractorSkillKey.retrieveSkillUsageKey(
        Extractor.extractKeyTextFromTextWithUmlauts(s
            .replace("ß", "xxx")
            .replace("\u00AD", "-")
            .replaceAll("-\\s*", "")
            .replace("Verwischen eigener Fährte", "Verwischen eigener Fährten")
            .replace("Lager suche", "Lagersuche")
        ).toLowerCase().replace("xxx", "ß")
    );
  }

  private static List<String> retrieveUsageStrings(String usages)
  {
    if (usages.startsWith("einzelne Provinz"))
    {
      return extractUsageStringsGeographie();
    }
    else if (usages.startsWith("je nach Gottheit"))
    {
      return extractUsageStringsGods();
    }
    else if (usages.startsWith("jeweilige Krankheit"))
    {
      return extractUsageStringsSicknesses();
    }
    else
    {
      return Arrays.stream(usages.split(",")).map(s -> s.trim()).collect(Collectors.toList());
    }
  }

  private static List<String> extractUsageStringsSicknesses()
  {
    return Arrays.asList(
        "Dumpfschädel",
        "Efferdsieche",
        "Flinker Difar",
        "Kerkersieche",
        "Lutanas",
        "Rascher Wahn",
        "Wundfieber",
        "Sumpffieber oder Brabaker Schweiß",
        "Zorganpocken",
        "Aussatz",
        "Blaue Keuche",
        "Blutiger Rotz",
        "Gilbe oder Schlachtfeldfieber",
        "Jahresfieber",
        "Schwarze Wut",
        "Tollwut",
        "Augenpest",
        "Basiliskenblick",
        "Paralyse",
        "Bleiche Sieche",
        "Blutkrächzen",
        "Grasse",
        "Guruk Phaor",
        "Gänsepusteln",
        "Herbstpocken",
        "Horasierkrankheit",
        "Höhenkrankheit",
        "Ilaris Sucht",
        "Karmesin",
        "Leichenblässe",
        "Lungenpilz",
        "Lykanthropie",
        "Marderkrätze",
        "Orkenkrätze",
        "Pilzflechte",
        "Reisswasserseuche",
        "Rotbacke",
        "Schlafkrankheit",
        "Triefnase"
    );
  }

  private static List<String> extractUsageStringsGods()
  {
    return Arrays.asList(
        "Praios",
        "Rondra",
        "Efferd",
        "Phex",
        "Hesinde",
        "Boron",
        "Rahja",
        "Travia",
        "Ingerimm",
        "Firun",
        "Peraine",
        "Tsa",
        "Der Namenlose",
        "Swafnir",
        "Kor",
        "Ifirn",
        "Simia",
        "Nandus",
        "Satuaria",
        "Marbo",
        "Aves",
        "Satinav",
        "Levthan",
        "Gorfang",
        "Mada",
        "Los",
        "Sumu",
        "Rur und Gror",
        "Gebote des Raschtullah"
    );
  }

  private static List<String> extractUsageStringsGeographie()
  {
    return Arrays.asList("Albernia",
        "Almada",
        "Garetien",
        "Kosch",
        "Nordmarken",
        "Rommilyser Mark",
        "Tobrien",
        "Weiden",
        "Windhag",
        "Al'Anfanisches Imperium",
        "Andergast",
        "Aranien",
        "Bergkönigreiche der Zwerge",
        "Bornland",
        "Gjalskerland",
        "Hoher Norden",
        "Horasreich",
        "Kalifat",
        "Maraskan",
        "Nostria",
        "Orkland",
        "Salamandersteine & umliegende Gebiete der Elfen",
        "Schattenlande",
        "Südmeer & Waldinseln",
        "Svellttal",
        "Thorwal",
        "Tiefer Süden",
        "Tulamidenlande",
        "Zyklopeninseln");

  }

  public static Skill migrate(SkillRaw raw)
  {
    Skill returnValue = new Skill();

    returnValue.name = raw.name.replace("\u00AD", "-").replace("Einsatzmöglichkeit", "");
    returnValue.key = ExtractorSkillKey.retrieveSkillKey(returnValue.name);

    returnValue.advancementCategory = ExtractorAdvancementCategory.retrieveAdvancementCategory(raw);
    returnValue.check = ExtractorCheck.retrieveCheck(raw);
    returnValue.skillCategory = extractSkillCategory(returnValue.key);
    returnValue.isEncumbered = switch (raw.encumbrance)
        {
          case "ja" -> ConditionalBoolean.ja;
          case "nein" -> ConditionalBoolean.nein;
          default -> ConditionalBoolean.vielleicht;
        };
    returnValue.skillUsageKeys = extractSkillUsageKeys(raw.application);

    return returnValue;
  }

  private static List<SkillUsageKey> extractSkillUsageKeys(String application)
  {
    return retrieveUsageStrings(application).stream().map(us -> retrieveUsageKey(us)).collect(Collectors.toList());
  }

  private static SkillCategoryKey extractSkillCategory(SkillKey key)
  {

    return switch (key)
        {
          case fliegen, gaukeleien, klettern, körperbeherrschung, kraftakt, reiten, schwimmen, selbstbeherrschung, singen, sinnesschärfe, tanzen, taschendiebstahl, verbergen, zechen ->
              SkillCategoryKey.körpertalente;
          case bekehren_und_überzeugen, betören, einschüchtern, etikette, gassenwissen, menschenkenntnis, überreden, verkleiden, willenskraft ->
              SkillCategoryKey.gesellschaftstalente;
          case fährtensuchen, fesseln, fischen_und_angeln, orientierung, pflanzenkunde, tierkunde, wildnisleben -> SkillCategoryKey.naturtalente;
          case brett_und_glücksspiel, geographie, geschichtswissen, götter_und_kulte, kriegskunst, magiekunde, mechanik, rechnen, rechtskunde, sagen_und_legenden, sphärenkunde, sternkunde ->
              SkillCategoryKey.wissenstalente;
          case alchimie, boote_und_schiffe, fahrzeuge, handel, heilkunde_gift, heilkunde_krankheiten, heilkunde_seele, heilkunde_wunden, holzbearbeitung, lebensmittelbearbeitung, lederbearbeitung, malen_und_zeichnen, metallbearbeitung, musizieren, schlösserknacken, steinbearbeitung, stoffbearbeitung ->
              SkillCategoryKey.handwerkstalente;
        };
  }

}
