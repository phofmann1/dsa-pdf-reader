package de.pho.dsapdfreader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Triplet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.pho.dsapdfreader.config.ConfigurationInitializer;
import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.TopicStrategies;
import de.pho.dsapdfreader.dsaconverter.DsaConverterArmor;
import de.pho.dsapdfreader.dsaconverter.DsaConverterArmorAvailability;
import de.pho.dsapdfreader.dsaconverter.DsaConverterArmorLists;
import de.pho.dsapdfreader.dsaconverter.DsaConverterArmorPart;
import de.pho.dsapdfreader.dsaconverter.DsaConverterBeverage;
import de.pho.dsapdfreader.dsaconverter.DsaConverterBoon;
import de.pho.dsapdfreader.dsaconverter.DsaConverterClericalObjectRituals;
import de.pho.dsapdfreader.dsaconverter.DsaConverterContent;
import de.pho.dsapdfreader.dsaconverter.DsaConverterCurriculum;
import de.pho.dsapdfreader.dsaconverter.DsaConverterEquipment;
import de.pho.dsapdfreader.dsaconverter.DsaConverterHerb;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMsyticalSkillElements;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMsyticalSkillIncantations;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillActivityAndArtifacts;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillGrimorium;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillGrimoriumTricks;
import de.pho.dsapdfreader.dsaconverter.DsaConverterPraegung;
import de.pho.dsapdfreader.dsaconverter.DsaConverterProfession;
import de.pho.dsapdfreader.dsaconverter.DsaConverterProfile;
import de.pho.dsapdfreader.dsaconverter.DsaConverterSkillKodex;
import de.pho.dsapdfreader.dsaconverter.DsaConverterSpecialAbilityKodex;
import de.pho.dsapdfreader.dsaconverter.DsaConverterTraditionsToSpecialAbility;
import de.pho.dsapdfreader.dsaconverter.DsaConverterWeapon;
import de.pho.dsapdfreader.dsaconverter.DsaConverterWeaponAvailability;
import de.pho.dsapdfreader.dsaconverter.DsaConverterWeaponLists;
import de.pho.dsapdfreader.dsaconverter.model.ArmorRaw;
import de.pho.dsapdfreader.dsaconverter.model.BeverageRaw;
import de.pho.dsapdfreader.dsaconverter.model.BoonRaw;
import de.pho.dsapdfreader.dsaconverter.model.ContentRaw;
import de.pho.dsapdfreader.dsaconverter.model.CurriculumRaw;
import de.pho.dsapdfreader.dsaconverter.model.EquipmentRaw;
import de.pho.dsapdfreader.dsaconverter.model.HerbRaw;
import de.pho.dsapdfreader.dsaconverter.model.MeleeWeaponRaw;
import de.pho.dsapdfreader.dsaconverter.model.MysticalActivityObjectRitualRaw;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.dsaconverter.model.ProfessionRaw;
import de.pho.dsapdfreader.dsaconverter.model.RangedWeaponRaw;
import de.pho.dsapdfreader.dsaconverter.model.SkillRaw;
import de.pho.dsapdfreader.dsaconverter.model.SpecialAbilityRaw;
import de.pho.dsapdfreader.dsaconverter.model.TraditionRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.DsaConverterStrategy;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorBoon;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorEquipmentKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorObjectRitual;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkill;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSpecialAbility;
import de.pho.dsapdfreader.exporter.LoadToArmor;
import de.pho.dsapdfreader.exporter.LoadToBeverage;
import de.pho.dsapdfreader.exporter.LoadToBoon;
import de.pho.dsapdfreader.exporter.LoadToEquipment;
import de.pho.dsapdfreader.exporter.LoadToHerb;
import de.pho.dsapdfreader.exporter.LoadToMeleeWeapon;
import de.pho.dsapdfreader.exporter.LoadToMysticalSkill;
import de.pho.dsapdfreader.exporter.LoadToObjectRitual;
import de.pho.dsapdfreader.exporter.LoadToProfession;
import de.pho.dsapdfreader.exporter.LoadToRangedWeapon;
import de.pho.dsapdfreader.exporter.LoadToSkill;
import de.pho.dsapdfreader.exporter.LoadToSpecialAbility;
import de.pho.dsapdfreader.exporter.LoadToTraditionAbility;
import de.pho.dsapdfreader.exporter.SaSpecialRequirementsToggle;
import de.pho.dsapdfreader.exporter.model.Armor;
import de.pho.dsapdfreader.exporter.model.AvailabilityWeapon;
import de.pho.dsapdfreader.exporter.model.Boon;
import de.pho.dsapdfreader.exporter.model.CheckQs;
import de.pho.dsapdfreader.exporter.model.Equipment;
import de.pho.dsapdfreader.exporter.model.EquipmentI;
import de.pho.dsapdfreader.exporter.model.Herb;
import de.pho.dsapdfreader.exporter.model.MeleeWeapon;
import de.pho.dsapdfreader.exporter.model.MysticalSkill;
import de.pho.dsapdfreader.exporter.model.ObjectRitual;
import de.pho.dsapdfreader.exporter.model.Price;
import de.pho.dsapdfreader.exporter.model.Profession;
import de.pho.dsapdfreader.exporter.model.RangedWeapon;
import de.pho.dsapdfreader.exporter.model.RequirementBoon;
import de.pho.dsapdfreader.exporter.model.Skill;
import de.pho.dsapdfreader.exporter.model.SkillApplication;
import de.pho.dsapdfreader.exporter.model.SkillUsage;
import de.pho.dsapdfreader.exporter.model.SpecialAbility;
import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.CultureKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.ObjectRitualKey;
import de.pho.dsapdfreader.exporter.model.enums.ProfessionTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;
import de.pho.dsapdfreader.pdf.PdfReader;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;
import de.pho.dsapdfreader.tools.csv.CsvHandler;
import de.pho.dsapdfreader.tools.merger.ObjectMerger;

public class DsaPdfReaderMain {

  static final CombatSkillKey[] COMBAT_SKILL_KEYS_RANGED = {CombatSkillKey.blasrohre, CombatSkillKey.bögen, CombatSkillKey.armbrüste, CombatSkillKey.diskusse, CombatSkillKey.schleudern, CombatSkillKey.wurfwaffen};
  static final Map<String, String> mapProfession2CurriculumRaw;
  private static final boolean TOGGLE_READ_PDF = false;
  private static final String PDF_BASE_PATH = "D:/Daten/Dropbox/pdf.library/RPG/DSA 5/";
  private static final String PDF_BASE_PATH_2 = "D:/Daten/OneDrive/pdf.library/RPG/DSA 5 - SL/";
  private static final String PDF_BASE_PATH_3 = "D:\\develop\\project\\pdf-archive\\";
  private static final String STRATEGY_PACKAGE = DsaConverterStrategy.class.getPackageName() + ".";
  //private static final String PATH_BASE = "d:\\develop\\project\\java\\dsa-pdf-reader\\export\\";
  private static final String PATH_BASE = "C:\\develop\\project\\dsa-pdf-reader\\export\\";
  private static final String PATH_PDF_2_TEXT = PATH_BASE + "01 - pdf2text\\";
  private static final String FILE_PDF_2_TEXT = PATH_PDF_2_TEXT + "%s_txt.csv";
  private static final String PATH_TEXT_2_STRATEGY = PATH_BASE + "02 - applyStrategies\\";
  private static final String FILE_TEXT_2_STRATEGY = PATH_TEXT_2_STRATEGY + "%s_str.csv";
  private static final String PATH_STRATEGY_2_RAW = PATH_BASE + "03 - raw\\";
  private static final String FILE_STRATEGY_2_RAW = PATH_STRATEGY_2_RAW + "%s_raw.csv";
  private static final String FILE_STRATEGY_2_RAW_ALL = PATH_STRATEGY_2_RAW + "\\%s\\all.csv";
  private static final String PATH_RAW_2_JSON = PATH_BASE + "04 - json\\%s\\";
  private static final String FILE_RAW_2_JSON = PATH_RAW_2_JSON + "%s.json";
  private static final String FILE_RAW_2_MD = PATH_RAW_2_JSON + "%s.md";
  private static final String SEPARATOR = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
  private static final Logger LOGGER = LogManager.getLogger();
  private static List<TopicConfiguration> configs = null;

  static {
    mapProfession2CurriculumRaw = new HashMap<>();
    mapProfession2CurriculumRaw.put("Graumagierin der Akademie der Erscheinungen zu Grangor", "Akademie der Erscheinungen zu Grangor");
    mapProfession2CurriculumRaw.put("Schwarzmagierin (Al’Achami zu Fasar)", "Akademie der Geistigen Kraft zu Fasar");
    mapProfession2CurriculumRaw.put("Graumagierin der Akademie der Geistreisen zu Belhanka", "Akademie der Geistreisen zu Belhanka");
    mapProfession2CurriculumRaw.put("Weißmagierin der Akademie der Herrschaft zu Elenvina", "Akademie der Herrschaft zu Elenvina");
    mapProfession2CurriculumRaw.put("Graumagierin (Schule der Hohen Magie zu Punin)", "Akademie der Hohen Magie zu Punin");
    mapProfession2CurriculumRaw.put("Weißmagierin der Akademie der magischen Rüstung zu Gareth", "Akademie der magischen Rüstung zu Gareth");
    mapProfession2CurriculumRaw.put("Graumagierin (Schule der Verformungen zu Lowangen)", "Akademie der Verformungen zu Lowangen");
    mapProfession2CurriculumRaw.put("Weißmagier (Schwert & Stab- Akademie zu Gareth)", "Akademie Schwert und Stab zu Gareth");
    mapProfession2CurriculumRaw.put("Weißmagierin der Akademie von Licht und Dunkelheit zu Nostria", "Akademie von Licht und Dunkelheit zu Nostria");
    mapProfession2CurriculumRaw.put("Graumagierin der Akademie zu Wagenhalt", "Akademie zu Wagenhalt");
    mapProfession2CurriculumRaw.put("Gildenloser Magier (Schüler des Alrik Dagabor)", "Alrik Dagabor");
    mapProfession2CurriculumRaw.put("Weißmagierin der Anatomischen Akademie zu Vinsalt", "Anatomische Akademie zu Vinsalt");
    mapProfession2CurriculumRaw.put("Gildenloser Magier der Bannakademie von Fasar", "Bannakademie von Fasar");
    mapProfession2CurriculumRaw.put("Schwarzmagier (Schüler des Demirion Ophenos)", "Demirion Ophenos");
    mapProfession2CurriculumRaw.put("Graumagier der Drachenei-Akademie zu Khunchom", "Drachenei-Akademie zu Khunchom");
    mapProfession2CurriculumRaw.put("Schwarzmagierin der Dunklen Halle der Geister zu Brabak", "Dunkle Halle der Geister zu Brabak");
    mapProfession2CurriculumRaw.put("Gildenlose Magierin nach Halib abu’l Ketab", "Halib abu’l Ketab");
    mapProfession2CurriculumRaw.put("Graumagier (Halle der Antimagie zu Kuslik)", "Halle der Antimagie zu Kuslik");
    mapProfession2CurriculumRaw.put("Schwarzmagier (Halle der Erleuchtung zu Al’Anfa)", "Halle der Erleuchtung zu Al’Anfa");
    mapProfession2CurriculumRaw.put("Schwarzmagier der Halle der Macht zu Lowangen", "Halle der Macht zu Lowangen");
    mapProfession2CurriculumRaw.put("Weißmagierin der Halle der Metamorphosen zu Kuslik", "Halle der Metamorphose zu Kuslik");
    mapProfession2CurriculumRaw.put("Gildenloser Magier der Halle der Winde zu Olport", "Halle der Winde zu Olport");
    mapProfession2CurriculumRaw.put("Weißmagier der Halle des Lebens zu Norburg", "Halle des Lebens zu Norburg");
    mapProfession2CurriculumRaw.put("Graumagier der Halle des Quecksilbers zu Festum", "Halle des Quecksilbers zu Festum");
    mapProfession2CurriculumRaw.put("Graumagierin der Halle des vollendeten Kampfes zu Bethana", "Halle des vollendeten Kampfes zu Bethana");
    mapProfession2CurriculumRaw.put("Weißmagier (Schüler des Hesindius Lichtblick)", "Hesindius Lichtblick");
    mapProfession2CurriculumRaw.put("Gildenlose Magierin der Heptagonakademie zu Yol-Ghurmak", "Heptagonakademie zu Yol-Ghurmak");
    mapProfession2CurriculumRaw.put("Weißmagier des Informations- Instituts zu Rommilys", "Informations-Institut zu Rommilys");
    mapProfession2CurriculumRaw.put("Graumagier des Kampfseminars zu Andergast", "Kampfseminar zu Andergast");
    mapProfession2CurriculumRaw.put("Gildenlose Magierin (Schülerin der Khelbara ay Baburia)", "Khelbara ay Baburia");
    mapProfession2CurriculumRaw.put("Graumagier des Konzils der Elemente zu Drakonia (Erzelementarist)", "Konzil der Elemente zu Drakonia");
    mapProfession2CurriculumRaw.put("Gildenloser Magier des Kreises der Einfühlung", "Kreis der Einfühlung");
    mapProfession2CurriculumRaw.put("Gildenlose Magierin des Magierkollegs zu Honingen", "Magierkolleg zu Honingen");
    mapProfession2CurriculumRaw.put("Qabaloth der Nachtwinde", "Nachtwinde");
    mapProfession2CurriculumRaw.put("Graumagierin der Pentagramm- Akademie zu Rashdul", "Pentagramm-Akademie zu Rashdul");
    mapProfession2CurriculumRaw.put("Gildenlose Magierin nach Rafim Bey", "Rafim Bey");
    mapProfession2CurriculumRaw.put("Rashduler Dämonologe", "Rashduler Dämonologen");
    mapProfession2CurriculumRaw.put("Weißmagierin (Schule der Austreibung)", "Schule der Austreibung zu Perricum");
    mapProfession2CurriculumRaw.put("Graumagierin der Schule der Beherrschung zu Neersand", "Schule der Beherrschung zu Neersand");
    mapProfession2CurriculumRaw.put("Graumagier (Schule der Hellsicht zu Thorwal)", "Schule der Hellsicht zu Thorwal");
    mapProfession2CurriculumRaw.put("Schwarzmagierin der Schule der variablen Form zu Mirham", "Schule der variablen Form zu Mirham");
    mapProfession2CurriculumRaw.put("Graumagierin (Schule der Vierfachen Verwandlung zu Sinoda)", "Schule der Vierfachen Verwandlung zu Sinoda");
    mapProfession2CurriculumRaw.put("Graumagier (Schule des Direkten Weges zu Gerasim)", "Schule des Direkten Weges zu Gerasim");
    mapProfession2CurriculumRaw.put("Weißmagier der Schule des magischen Wissens zu Methumis", "Schule des magischen Wissens zu Methumis");
    mapProfession2CurriculumRaw.put("Graumagierin (Schule des Seienden Scheins zu Zorgan)", "Schule des Seienden Scheins zu Zorgan");
    mapProfession2CurriculumRaw.put("Gildenloser Magier des Seminars der elfischen Verständigung und natürlichen Heilung zu Donnerbach", "Seminar der elfischen Verständigung und natürlichen Heilung zu Donnerbach");
    mapProfession2CurriculumRaw.put("Gildenloser Magier nach Sevastana Gevendar", "Sevastana Gevendar");
    mapProfession2CurriculumRaw.put("Schwarzmagier nach Shanada von Ben-Oni", "Shanada von Ben-Oni");
    mapProfession2CurriculumRaw.put("Graumagier des Stoerrebrandt-Kollegs zu Riva", "Stoerrebrandt-Kolleg zu Riva");
    mapProfession2CurriculumRaw.put("Qabaloth der Töchter Niobaras", "Töchter Niobaras");
    mapProfession2CurriculumRaw.put("Gildenloser Magier (Schüler des Vadif sal Karim)", "Vadif sal Karim");
    mapProfession2CurriculumRaw.put("Graumagier der Zauberschule des Kalifen zu Mherwed", "Zauberschule des Kalifen zu Mherwed");
    mapProfession2CurriculumRaw.put("Gildenloser Magier nach Kalliomathëa Dorikeikos von Sorabis", "Kalliomathëa Dorikeikos von Sorabis");
  }

  public static void main(String[] args) {
    LOGGER.info(SEPARATOR);
    LOGGER.info("start - PDF Reader");
    Instant start = Instant.now();
    LOGGER.debug("init config");
    initConfig();
    LOGGER.info(SEPARATOR);

    boolean isToText = Arrays.asList(args).contains("toText");
    boolean isToStrategy = Arrays.asList(args).contains("toStrategy");
    boolean isToRaws = Arrays.asList(args).contains("toRaws");
    boolean isToJson = Arrays.asList(args).contains("toJson");
    boolean isSummarize = Arrays.asList(args).contains("summarize");
    boolean isNone = !(isToText || isToRaws || isToJson || isToStrategy);

    isToText = isToText || isNone;
    isToStrategy = isToStrategy || isNone;
    isToRaws = isToRaws || isNone;
    isToJson = isToJson || isNone;

    isToText = TOGGLE_READ_PDF;
    isToStrategy = true;
    isToRaws = true;
    isToJson = true;

    if (isToText) convertToText();
    if (isToStrategy) convertToStrategy();
    if (isToRaws) convertToRaws();
    if (isSummarize) summarizeRawByType();
    if (isToJson) convertToJson();

    Instant end = Instant.now();
    String timeString = Duration.between(start, end)
        .toString()
        .substring(2)
        .replaceAll("(\\d[HMS])(?!$)", "$1 ")
        .toLowerCase();
    LOGGER.info(timeString);

    LOGGER.info("----------------------------------");
    LOGGER.info("finish");
    LOGGER.info("----------------------------------");

  }

  private static String readFromInputStream(File file) {
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br
             = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
      String line;
      while ((line = br.readLine()) != null) {
        resultStringBuilder.append(line).append("\n");
      }
    }
    catch (IOException e) {
      LOGGER.error("Error reading File (%s)", file.getAbsolutePath());
    }
    return resultStringBuilder.toString();
  }


  private static void convertToText() {
    configs.stream()
        .filter(c -> c != null && c.active)
        .forEach(conf -> {
          String msg = String.format("Config PDF import verarbeiten: %s (%s)", conf.publication, conf.topic);
          LOGGER.info(msg);

          List<TextWithMetaInfo> pdfResults;
          try {
            pdfResults = extractPdfResults(conf);

            LOGGER.debug("export Texts");
            final List<TextWithMetaInfo> texts = pdfResults.stream()
                .filter(t -> {
                      boolean isStartPage = t.onPage == conf.startPage;
                      boolean isNormalPage = t.onPage > conf.startPage && t.onPage < conf.endPage;
                      boolean isEndPage = t.onPage == conf.endPage;
                      boolean isStartPageValidLine = isStartPage && isEndPage
                          ? (t.onLine > conf.startAfterLine || conf.startAfterLine == 0) && (t.onLine <= conf.endAfterLine || conf.endAfterLine == 0)
                          : isStartPage && (t.onLine > conf.startAfterLine || conf.startAfterLine == 0);
                      boolean isEndPageValidLine = isStartPage && isEndPage
                          ? isStartPageValidLine
                          : isEndPage && (t.onLine <= conf.endAfterLine || conf.endAfterLine == 0);
                      return isStartPageValidLine || isNormalPage || isEndPageValidLine;
                    }
                )
                .sorted((a, b) -> a.sortIndex() < b.sortIndex() ? -1 : 1)
                .collect(Collectors.toList());

            File fOut = new File(generateFileName(FILE_PDF_2_TEXT, conf));
            CsvHandler.writeBeanToUrl(fOut, texts);
          }
          catch (IOException | NullPointerException e) {
            LOGGER.error(e.getMessage(), e);
          }
          LOGGER.debug(SEPARATOR);
        });
  }

  private static void convertToStrategy() {
    configs.stream()
        .filter(c -> c != null && c.active)
        .forEach(conf -> {
          String msg = String.format("Config Strategie verarbeiten: %s (%s)", conf.publication, conf.topic);
          LOGGER.info(msg);
          LOGGER.debug(generateFileName(FILE_PDF_2_TEXT, conf));
          File fIn = new File(generateFileName(FILE_PDF_2_TEXT, conf));

          List<TextWithMetaInfo> texts;
          try {
            texts = CsvHandler.readBeanFromFile(TextWithMetaInfo.class, fIn);

            texts = applyStrategies(texts, conf);

            File fOut = new File(generateFileName(FILE_TEXT_2_STRATEGY, conf));
            CsvHandler.writeBeanToUrl(fOut, texts);
          }
          catch (NullPointerException e) {
            LOGGER.error(e.getMessage(), e);
          }
          LOGGER.debug(SEPARATOR);
        });
  }


  private static void convertToRaws() {
    configs.stream()
        .filter(conf ->
            conf != null && conf.active
        )
        .forEach(conf -> {
          String msg = String.format("Config zu raw-objects verarbeiten: %s (%s)", conf.publication, conf.topic);
          LOGGER.info(msg);

          File fIn = new File(generateFileName(FILE_TEXT_2_STRATEGY, conf));

          List<TextWithMetaInfo> texts;
          try {
            texts = CsvHandler.readBeanFromFile(TextWithMetaInfo.class, fIn);
            List results = parseResult(texts, conf);

            if (results != null) {
              File fOut = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
              msg = String.format("# of Entries of Type (%s): %s", conf.topic, results.size());
              LOGGER.info(msg);
              CsvHandler.writeBeanToUrl(fOut, results);
            }
          }
          catch (NullPointerException e) {
            LOGGER.error(e.getMessage(), e);
          }
          LOGGER.debug(SEPARATOR);
        });
  }

  private static String generateFileName(String filePattern, TopicConfiguration conf) {
    String appendToFileName = (conf.fileAffix == null || conf.fileAffix.isEmpty()) ? "" : "_" + conf.fileAffix;
    return String.format(filePattern, conf.publication + "_" + conf.topic + appendToFileName);
  }

  private static String generateFileNameTypedDirectory(String filePattern, TopicEnum topic, String publication, String fileAffix, String type) {
    String appendToFileName = (fileAffix == null || fileAffix.isEmpty()) ? "" : "_" + fileAffix;
    return String.format(filePattern, type, publication + "_" + topic + appendToFileName);
  }


  private static void convertToJson() {
    configs.stream()
        .filter(conf -> conf != null && conf.active)
        .forEach(DsaPdfReaderMain::parseToJson);
  }

  private static void summarizeRawByType() {
    Map<String, List<MysticalSkillRaw>> allByTypeMap = new HashMap<>();
    List<MeleeWeaponRaw> meleeWeaponRawList = new ArrayList<>();
    List<RangedWeaponRaw> rangedWeaponRawList = new ArrayList<>();
    List<ArmorRaw> armorRawList = new ArrayList<>();

    configs.stream()
        .filter(conf -> conf != null && conf.active)
        .forEach(conf -> {
          String msg = String.format("Raw nach typ zusammenfassen: %s (%s)", conf.publication, conf.topic);
          LOGGER.info(msg);
          switch (conf.topic) {
          case SPELLS_GRIMORIUM, RITUALS_GRIMORIUM, TRICKS_GRIMORIUM, BLESSING_DIVINARIUM,
              LITURGY_DIVINARIUM, CEREMONY_DIVINARIUM -> {
            List<MysticalSkillRaw> mysticalSkillRawList = CsvHandler.readBeanFromFile(MysticalSkillRaw.class, new File(generateFileName(FILE_STRATEGY_2_RAW, conf)));
            if (allByTypeMap.containsKey(MysticalSkill.TYPE))
              allByTypeMap.get(MysticalSkill.TYPE).addAll(mysticalSkillRawList);
            else allByTypeMap.put(MysticalSkill.TYPE, mysticalSkillRawList);
          }
          case INCANTATIONS_RIME_SPELLS_GRIMORIUM, INCANTATIONS_RIME_RITUALS_GRIMORIUM,
              INCANTATIONS_ZHAYAD_SPELLS_GRIMORIUM, INCANTATIONS_ZHAYAD_RITUALS_GRIMORIUM,
              ELEMENTS_SPELLS_GRIMORIUM, ELEMENTS_RITUALS_GRIMORIUM -> {
            break;
          }
          case WAFFENLISTE, WAFFEN -> {

          }
          case RÜSTUNGENLISTE, RÜSTUNGEN, RÜSTUNGEN_HELME, RÜSTUNGEN_TEILE -> {
            List<ArmorRaw> rawList = CsvHandler.readBeanFromFile(ArmorRaw.class, new File(generateFileName(FILE_STRATEGY_2_RAW, conf)));
            armorRawList.addAll(rawList);
          }
          default -> LOGGER.error(String.format("Unexpected value (summarizeRawByType): %s", conf.topic));
          }
        });

    allByTypeMap.forEach((k, v) -> CsvHandler.writeBeanToUrl(new File(String.format(FILE_STRATEGY_2_RAW_ALL, k)), v));
    CsvHandler.writeBeanToUrl(new File(String.format(FILE_STRATEGY_2_RAW_ALL, "equipment-melee")), meleeWeaponRawList);
    CsvHandler.writeBeanToUrl(new File(String.format(FILE_STRATEGY_2_RAW_ALL, "equipment-ranged")), rangedWeaponRawList);
    CsvHandler.writeBeanToUrl(new File(String.format(FILE_STRATEGY_2_RAW_ALL, "equipment-armor")), armorRawList);
  }


  private static void parseToJson(TopicConfiguration conf) {
    String msg = String.format("Config zu JSON verarbeiten: %s (%s)", conf.publication, conf.topic);
    LOGGER.info(msg);
    switch (conf.topic) {
    // Mystical Skills
    case SPELLS_GRIMORIUM, RITUALS_GRIMORIUM, TRICKS_GRIMORIUM, BLESSING_DIVINARIUM, LITURGY_DIVINARIUM,
        CEREMONY_DIVINARIUM -> {
      try {
        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));

        List<MysticalSkillRaw> rawMysticalSkills = CsvHandler.readBeanFromFile(MysticalSkillRaw.class, fIn);
        List<MysticalSkill> mysticalSkills = rawMysticalSkills.stream().flatMap(r -> LoadToMysticalSkill.migrate(r, Extractor.retrieveMsCategory(r.topic))).collect(Collectors.toList());

        ObjectMapper mapper = initObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(mysticalSkills);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "mysticalskills"));
        writer.write(jsonResult);
        writer.close();

        if ((conf.topic == TopicEnum.RITUALS_GRIMORIUM
            || conf.topic == TopicEnum.SPELLS_GRIMORIUM
            || conf.topic == TopicEnum.CEREMONY_DIVINARIUM
            || conf.topic == TopicEnum.LITURGY_DIVINARIUM
            || conf.topic == TopicEnum.TRICKS_GRIMORIUM
            || conf.topic == TopicEnum.BLESSING_DIVINARIUM
        )) {

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "mysticalskills_name"));
          writer.write(generateMsName(mysticalSkills));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "mysticalskills_descriptions"));
          writer.write(generateMsDescriptionString(rawMysticalSkills));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "mysticalskills_costs"));
          writer.write(generateMsCostString(rawMysticalSkills));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "mysticalskills_effects"));
          writer.write(generateMsEffectString(rawMysticalSkills));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "mysticalskills_variant_names"));
          writer.write(generateMsVariantNameString(rawMysticalSkills));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "mysticalskills_variant_descriptions"));
          writer.write(generateMsVariantDescriptionString(rawMysticalSkills));
          writer.close();
        }

      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    case ABILITIES -> {
      try {
        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));

        List<SpecialAbilityRaw> raws = CsvHandler.readBeanFromFile(SpecialAbilityRaw.class, fIn);

        extractAdditionalSkillUsages(raws, conf);
        extractAdditionalApplicationsAbilities(raws, conf);

        List<SpecialAbility> corrections = initExporterCorrections(SpecialAbility.class);
        List<SpecialAbility> specialAbilities = raws.stream().flatMap(LoadToSpecialAbility::migrate).collect(Collectors.toList());
        specialAbilities.addAll(generateTraditionsByPublication(conf.publication));
        specialAbilities = specialAbilities.stream().peek(sa -> LoadToSpecialAbility.applyCorrections(sa, corrections)).collect(Collectors.toList());


        ObjectMapper mapper = initObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(specialAbilities);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "abilities"));
        writer.write(jsonResult);
        writer.close();

        if (conf.topic == TopicEnum.ABILITIES) {

          Quartet<StringBuilder, StringBuilder, StringBuilder, StringBuilder> t = generateSaStringBuilders(raws);
          // name
          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "special_abilities_names"));
          writer.write(t.getValue0().toString());
          writer.close();

          // rules
          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "special_abilities_rules"));
          writer.write(t.getValue1().toString());
          writer.close();

          // description
          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "special_abilities_descriptions"));
          writer.write(t.getValue2().toString());
          writer.close();

          // preconditions
          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "special_abilities_preconditions"));
          writer.write(t.getValue3().toString());
          writer.close();
        }

      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    case INCANTATIONS_RIME_SPELLS_GRIMORIUM, INCANTATIONS_RIME_RITUALS_GRIMORIUM,
        INCANTATIONS_ZHAYAD_SPELLS_GRIMORIUM, INCANTATIONS_ZHAYAD_RITUALS_GRIMORIUM, ELEMENTS_SPELLS_GRIMORIUM,
        ELEMENTS_RITUALS_GRIMORIUM -> {
      break;
    }
    case BOONS -> {
      try {

        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<BoonRaw> raws = CsvHandler.readBeanFromFile(BoonRaw.class, fIn);
        extractAdditionalApplicationsBoons(raws, conf);

        List<Boon> corrections = initExporterCorrections(Boon.class);
        List<Boon> boons = raws.stream().map(LoadToBoon::migrate)
            .map(b -> {
              LoadToBoon.applyCorrections(b, corrections);
              return b;
            })
            .collect(Collectors.toList());
        List<Boon> additionals = corrections.stream()
            .filter(c -> (c.publications == null || c.publications.contains(Publication.valueOf(conf.publication))) && boons.stream().noneMatch(b -> b.key == c.key))
            .collect(Collectors.toList());
        boons.addAll(additionals);
        List<Boon> finalList = boons.stream().filter(b -> b.name != null).collect(Collectors.toList());

        ObjectMapper mapper = initObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(finalList);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "boons"));
        writer.write(jsonResult);
        writer.close();


        Quintet<StringBuilder, StringBuilder, StringBuilder, StringBuilder, StringBuilder> t = generateBoonStringBuilders(raws);
        // name
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "boons_names"));
        writer.write(t.getValue0().toString());
        writer.close();

        // rules
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "boons_rules"));
        writer.write(t.getValue1().toString());
        writer.close();

        // description
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "boons_descriptions"));
        writer.write(t.getValue2().toString());
        writer.close();

        // preconditions
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "boons_preconditions"));
        writer.write(t.getValue3().toString());
        writer.close();

        // ap
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "boons_ap"));
        writer.write(t.getValue4().toString());
        writer.close();

        // Varianten Namen
        StringBuilder sb = new StringBuilder();
        boons.stream().flatMap(b -> b.variants.stream()).forEach(bv -> {
          appendLocalisationString(sb, "BoonVariantKey", bv.key.ordinal(), "name", bv.name);
        });

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "boon_variant_names"));
        writer.write(sb.toString());
        writer.close();

      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    // Equipment
    case AUSRÜSTUNG -> {
      try {

        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<EquipmentRaw> raws = CsvHandler.readBeanFromFile(EquipmentRaw.class, fIn);

        List<Equipment> equipmentList = raws.stream().map(LoadToEquipment::migrate).collect(Collectors.toList());

        if (equipmentList.size() > 0) {
          ObjectMapper mapper = initObjectMapper();
          String jsonResult = mapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(equipmentList);

          BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "equipment"));
          writer.write(jsonResult);
          writer.close();


          String prefix = "equipment";
          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_name"));
          writer.write(generateLocalisationString(equipmentList.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "EquipmentKey", "name"));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_remark"));
          writer.write(generateLocalisationString(equipmentList.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "EquipmentKey", "remark"));
          writer.close();
        }

      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    case VERBREITUNG_WAFFEN, VERBREITUNG_RÜSTUNG -> {
      System.err.println("--------------------------------");
      System.err.println("Entwicklungsstand: Die Verbreitung der Waffen ist in der erhebung durch, momentan wird noch ein Fehler gemacht, da manche Regionen auch als Kultur erkannt werden. Eine Einbindung in die Ausrüstungsliste fehlt noch");
      System.err.println("--------------------------------");
      try {
        File fInR = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<AvailabilityWeapon> pures = CsvHandler.readBeanFromFile(AvailabilityWeapon.class, fInR);
        if (!pures.isEmpty()) {
          ObjectMapper mapper = initObjectMapper();
          String jsonResult = mapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(pures);

          BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "equipment"));
          writer.write(jsonResult);
          writer.close();
        }
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    case WAFFENLISTE, WAFFEN -> {
      File fInR = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
      List<RangedWeaponRaw> rawsRanged = CsvHandler.readBeanFromFile(RangedWeaponRaw.class, fInR).stream().filter(rw -> rw.loadingTime != null && !rw.loadingTime.isEmpty()).collect(Collectors.toList());
      loadRangedWeapons(rawsRanged, conf);

      File fInM = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
      List<MeleeWeaponRaw> rawsMelee = CsvHandler.readBeanFromFile(MeleeWeaponRaw.class, fInM).stream().filter(rw -> rw.combatSkillKey == CombatSkillKey.lanzen || rw.additionalDamage != null && !rw.additionalDamage.isEmpty()).collect(Collectors.toList());
      loadMeleeWeapons(rawsMelee, conf);
    }
    case RÜSTUNGENLISTE, RÜSTUNGEN, RÜSTUNGEN_HELME, RÜSTUNGEN_TEILE -> {
      try {
        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<ArmorRaw> raws = CsvHandler.readBeanFromFile(ArmorRaw.class, fIn);

        List<Armor> corrections = initExporterCorrections(Armor.class);
        List<Armor> pures = raws.stream()
            .map(LoadToArmor::migrate)
            .map(armor -> {
              LoadToArmor.applyCorrections(armor, corrections);
              return armor;
            })
            .collect(Collectors.toList());

        if (pures.size() > 0) {


          ObjectMapper mapper = initObjectMapper();
          String jsonResult = mapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(pures);

          BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "equipment"));
          writer.write(jsonResult);
          writer.close();

          String prefix = "equipment_armor";
          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_name"));
          writer.write(generateLocalisationString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "ArmorKey", "name"));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_advantage"));
          writer.write(generateLocalisationString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "ArmorKey", "advantage"));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_disadvantage"));
          writer.write(generateLocalisationString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "ArmorKey", "disadvantage"));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_remark"));
          writer.write(generateLocalisationString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "ArmorKey", "remark"));
          writer.close();
        }
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }

    }
    case SKILLS -> {
      try {
        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<SkillRaw> raws = CsvHandler.readBeanFromFile(SkillRaw.class, fIn);

        exportUsages(raws, conf);

        List<Skill> pures = raws.stream().map(LoadToSkill::migrate).collect(Collectors.toList());

        pures = applyAdditionalUsages(pures);
        pures = applyAdditionalApplications(pures);

        ObjectMapper mapper = initObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(pures);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "skills"));
        writer.write(jsonResult);
        writer.close();

        StringBuilder skillDescriptionSB = new StringBuilder();
        pures.stream().forEach(p -> skillDescriptionSB.append(p.key.ordinal() + " {" + raws.stream().filter(r -> r.name.equals(p.name)).findFirst().get().description + "}\r\n"));

        String prefix = "skills";
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_description"));
        writer.write(skillDescriptionSB.toString());
        writer.close();
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }

    }
    case MYSTICAL_SKILL_ACTIVITIES_MAGIC, CLERICAL_OBJECT_RITUALS -> {

      try {
        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<MysticalActivityObjectRitualRaw> raws = CsvHandler.readBeanFromFile(MysticalActivityObjectRitualRaw.class, fIn);

        List<MysticalSkill> correctionsMas = initExporterCorrections(MysticalSkill.class);
        List<MysticalSkill> pureMas = raws.stream()
            .filter(r -> r.msCategory != null)
            .map(r -> castRawByName(MysticalSkillRaw.class, r))
            .flatMap(r -> LoadToMysticalSkill.migrate(r, r.msCategory))
            .map(msa -> {
              LoadToMysticalSkill.applyCorrections(msa, correctionsMas);
              return msa;
            })
            .collect(Collectors.toList());

        ObjectMapper mapperMas = initObjectMapper();
        String jsonResultMas = mapperMas
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(pureMas);

        BufferedWriter writerMas = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, "activities", "mystical_activities"));
        writerMas.write(jsonResultMas);
        writerMas.close();

        BufferedWriter writerMasName = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "mysticalskills_activities_name"));
        writerMasName.write(generateMsName(pureMas));
        writerMasName.close();

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "mysticalskills_activities_costs"));
        writer.write(generateMsaCostString(raws));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "mysticalskills_activities_effects"));
        writer.write(generateMsaEffectString(raws));
        writer.close();


        List<ObjectRitual> corrections = initExporterCorrections(ObjectRitual.class);
        List<ObjectRitual> pureOrs = raws.stream()
            .filter(r -> r.artifactKey != null)
            .flatMap(LoadToObjectRitual::migrate)
            .map(or -> {
              LoadToObjectRitual.applyCorrections(or, corrections);
              return or;
            })
            .collect(Collectors.toList());

        ObjectMapper mapperOrs = initObjectMapper();
        String jsonResultOrs = mapperOrs
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(pureOrs);

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, "object_rituals", "object_rituals"));
        writer.write(jsonResultOrs);
        writer.close();

        Triplet<StringBuilder, StringBuilder, StringBuilder> pOr = generateOrStringBuilders(raws.stream().filter(r -> r.artifactKey != null).collect(Collectors.toList()));


        String prefix = "object_rituals";
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_name"));
        writer.write(pOr.getValue0().toString());
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_rules"));
        writer.write(pOr.getValue1().toString());
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_preconditions"));
        writer.write(pOr.getValue2().toString());
        writer.close();

      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    case PROFESSIONS -> {
      try {
        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<ProfessionRaw> raws = CsvHandler.readBeanFromFile(ProfessionRaw.class, fIn);

        fIn = new File("D:\\develop\\project\\java\\dsa-pdf-reader\\export\\03 - raw\\Kodex_der_Magie_CURRICULUM_raw.csv");
        List<CurriculumRaw> curriculumRaws = CsvHandler.readBeanFromFile(CurriculumRaw.class, fIn);

        List<Profession> corrections = initExporterCorrections(Profession.class);
        List<Profession> pureProfessions = raws.stream()
            .flatMap(r -> LoadToProfession.migrate(r, extractCurriculumByProfessionName(r.name, curriculumRaws)))
            .map(p -> {
              LoadToProfession.applyCorrections(p, corrections);
              return p;
            })
            .collect(Collectors.toList());

        List<Profession> normals = pureProfessions.stream().filter(p -> p.professionType == ProfessionTypeKey.normal).collect(Collectors.toList());
        ObjectMapper mapperOrs = initObjectMapper();
        LOGGER.info("JSON-Professions Weltliche Professionen: " + normals.size() + " (Letzte Erzeugung: 223)");
        String jsonNormals = mapperOrs
            //.writerWithDefaultPrettyPrinter()
            .writeValueAsString(normals);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, "normal", "professions"));
        writer.write(jsonNormals);
        writer.close();

        List<Profession> chapters = pureProfessions.stream().filter(p -> p.professionType == ProfessionTypeKey.chapter).collect(Collectors.toList());
        LOGGER.info("JSON-Professions Ordensprofessionen: " + chapters.size() + " (Letzte Erzeugung: 38)");
        String jsonChapters = mapperOrs
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(chapters);

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, "chapter", "professions"));
        writer.write(jsonChapters);
        writer.close();

        List<Profession> magicals = pureProfessions.stream().filter(p -> p.professionType == ProfessionTypeKey.magical).collect(Collectors.toList());
        LOGGER.info("JSON-Professions Magische Professionen: " + magicals.size() + " (Letzte Erzeugung: 130)");
        String jsonMagicals = mapperOrs
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(magicals);

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, "magical", "professions"));
        writer.write(jsonMagicals);
        writer.close();

        List<Profession> curriculums = pureProfessions.stream().filter(p -> p.professionType == ProfessionTypeKey.curriculum).collect(Collectors.toList());
        LOGGER.info("JSON-Professions Curriculum: " + curriculums.size() + " (Letzte Erzeugung: 116)");
        String jsonCurriculums = mapperOrs
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(curriculums);

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, "curriculum", "professions"));
        writer.write(jsonCurriculums);
        writer.close();

        List<Profession> clericals = pureProfessions.stream().filter(p ->
            p.professionType == ProfessionTypeKey.clerical_alveran
                || p.professionType == ProfessionTypeKey.clerical_außeralveranisch
                || p.professionType == ProfessionTypeKey.clerical_halbgötter
        ).collect(Collectors.toList());
        LOGGER.info("JSON-Professions Geweihte Professionen: " + clericals.size() + " (Letzte Erzeugung: 53)");
        String jsonClerical = mapperOrs
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(clericals);

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, "clerical", "professions"));
        writer.write(jsonClerical);
        writer.close();

        StringBuilder pNames = new StringBuilder();
        pureProfessions.stream().map(p -> p.key.toValue() + " {" + p.name + "}\r\n").forEach(txt -> pNames.append(txt));

        String prefix = "professions";
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_name"));
        writer.write(pNames.toString());
        writer.close();

      }
      catch (IOException e) {
        LOGGER.error(e);
      }
    }
    case TRADITIONS -> {
      try {

        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<TraditionRaw> raws = CsvHandler.readBeanFromFile(TraditionRaw.class, fIn);

        List<SpecialAbility> corrections = initExporterCorrections(SpecialAbility.class);
        List<SpecialAbility> pureSas = raws.stream()
            .map(LoadToTraditionAbility::migrate)
            .peek(sa -> LoadToSpecialAbility.applyCorrections(sa, corrections))
            .collect(Collectors.toList());


        ObjectMapper mapperOrs = initObjectMapper();
        String jsonResultOrs = mapperOrs
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(pureSas);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, "", "special_abilities"));
        writer.write(jsonResultOrs);
        writer.close();

        Triplet<StringBuilder, StringBuilder, StringBuilder> t = generateTraditionStringBuilders(raws);
        // name
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "special_abilities_names"));
        writer.write(t.getValue0().toString());
        writer.close();

        // description
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "special_abilities_descriptions"));
        writer.write(t.getValue1().toString());
        writer.close();

        // preconditions
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "special_abilities_preconditions"));
        writer.write(t.getValue2().toString());
        writer.close();


      }
      catch (IOException e) {
        LOGGER.error(e);
      }
    }
    case CURRICULUM -> LOGGER.debug("Nothing to do, used in PROFESSION extraction");
    case CONTENT_LAND_UND_LEUTE -> {
      try {

        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<ContentRaw> raws = CsvHandler.readBeanFromFile(ContentRaw.class, fIn);

        StringBuilder content = new StringBuilder();
        for (ContentRaw raw : raws) {
          content.append(raw.content).append(System.lineSeparator()).append(System.lineSeparator());
        }

        // Convert StringBuilder to String
        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_MD, conf.topic, conf.publication, "", "content_LandUndLeute"));
        writer.write(content.toString());
        writer.close();
      }
      catch (IOException e) {
        LOGGER.error(e);
      }
    }
    case HERBS -> {
      try {
        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));

        List<HerbRaw> raws = CsvHandler.readBeanFromFile(HerbRaw.class, fIn);

        List<Herb> pures = raws.stream().map(LoadToHerb::migrate).collect(Collectors.toList());

        ObjectMapper mapper = initObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(pures);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "herbs"));
        writer.write(jsonResult);
        writer.close();

        generateHerbEquipmentEntries(pures, conf);
        generateQsEntries(pures.stream().map(p -> p.checkQs).toList(), conf);
        generateHerbNames(pures, conf);


      }
      catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }

    }
    case TAVERN -> {
      try {
        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));

        List<BeverageRaw> raws = CsvHandler.readBeanFromFile(BeverageRaw.class, fIn);

        List<Equipment> pures = raws.stream().map(LoadToBeverage::migrate).collect(Collectors.toList());

        ObjectMapper mapper = initObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(pures);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "equipment"));
        writer.write(jsonResult);
        writer.close();

        String prefix = "equipment";
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_name"));
        writer.write(generateLocalisationString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "EquipmentKey", "name"));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_remark"));
        writer.write(generateLocalisationString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "EquipmentKey", "remark"));
        writer.close();
      }
      catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    default -> LOGGER.error(String.format("Unexpected value (parseToJson): %s", conf.topic));
    }
  }

  private static void generateQsEntries(List<CheckQs> checkQs, TopicConfiguration conf) throws IOException {
    ObjectMapper mapper = initObjectMapper();
    String jsonResult = mapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(checkQs);

    String prefix = "check_qs";
    BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix));
    writer.write(jsonResult);
    writer.close();
  }

  private static void generateHerbEquipmentEntries(List<Herb> pures, TopicConfiguration conf) throws IOException {

    List<Equipment> kräuterEquips = pures.stream().flatMap(p -> {
      boolean added = false;
      List<Equipment> returnValues = new ArrayList<>();

      if (p.preisRoh != null) {
        Equipment equipment = new Equipment();
        equipment.key = ExtractorEquipmentKey.extractEquipmentKeyFromText("Kräuter " + p.name + " (roh)");
        equipment.name = p.name + " (roh)";
        equipment.equipmentCategoryKey = EquipmentCategoryKey.kräuter;
        equipment.aliasse = p.alternativeNamen;
        equipment.price = p.preisRoh;
        returnValues.add(equipment);
        added = true;
      }
      if (p.preisVerarbeitet != null) {
        Equipment equipment = new Equipment();
        equipment.key = ExtractorEquipmentKey.extractEquipmentKeyFromText("Kräuter " + p.name + " (verarbeitet)");
        equipment.name = p.name + " (verarbeitet)";
        equipment.equipmentCategoryKey = EquipmentCategoryKey.kräuter;
        equipment.aliasse = p.alternativeNamen;
        equipment.price = p.preisVerarbeitet;
        returnValues.add(equipment);
        added = true;
      }

      if (!added) {
        Equipment equipment = new Equipment();
        equipment.key = ExtractorEquipmentKey.extractEquipmentKeyFromText("Kräuter " + p.name);
        equipment.name = p.name;
        equipment.equipmentCategoryKey = EquipmentCategoryKey.kräuter;
        equipment.aliasse = p.alternativeNamen;
        equipment.price = new Price();
        returnValues.add(equipment);
      }
      return returnValues.stream();
    }).toList();

    ObjectMapper mapper = initObjectMapper();
    String jsonResult = mapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(kräuterEquips);

    BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "equipment_kräuter"));
    writer.write(jsonResult);
    writer.close();

    String prefix = "equipment_kräuter";
    writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_name"));
    writer.write(generateLocalisationString(kräuterEquips.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "EquipmentKey", "name"));
    writer.close();
  }


  private static void generateHerbNames(List<Herb> pures, TopicConfiguration conf) throws IOException {
    String prefix = "kräuter";
    BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_name"));
    writer.write(generateLocalisationString(pures, "HerbKey", "name"));
    writer.close();
  }

  private static List<? extends SpecialAbility> generateTraditionsByPublication(String publication) {
    return switch (Publication.valueOf(publication)) {
      case Kodex_der_Magie -> generateMagicTraditions();
      case Kodex_des_Götterwirkens -> generateClericalTraditions();
      default -> new ArrayList<>();
    };
  }

  private static Optional<CurriculumRaw> extractCurriculumByProfessionName(String name, List<CurriculumRaw> curriculumRaws) {
    return curriculumRaws.stream()
        .filter(cr -> cr.name.equals(mapProfession2CurriculumRaw.get(name)))
        .findFirst();
  }

  private static <T> T castRawByName(Class<T> targetClass, Object raw) {
    T returnValue = null;
    try {
      returnValue = targetClass.getDeclaredConstructor().newInstance();
      for (Field field : targetClass.getDeclaredFields()) {
        try {
          Object value = raw.getClass().getDeclaredField(field.getName()).get(raw);
          targetClass.getDeclaredField(field.getName()).set(returnValue, value);
        }
        catch (NoSuchFieldException e) {
        }
      }
    }
    catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
           InvocationTargetException e) {
    }
    return returnValue;
  }

  private static ObjectMapper initObjectMapper() {
    ObjectMapper returnValue = new ObjectMapper();
    //mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    returnValue.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    return returnValue;
  }

  private static void exportUsages(List<SkillRaw> raws, TopicConfiguration conf) throws IOException {
    Map<SkillUsageKey, SkillUsage> suAggregateMap = new HashMap<>();
    raws.stream()
        .map(LoadToSkill::migrateUsage)
        .flatMap(List::stream)
        .forEach(su -> {
          if (suAggregateMap.containsKey(su.key)) {
            suAggregateMap.get(su.key).skillKeys.addAll(su.skillKeys);
          }
          else {
            suAggregateMap.put(su.key, su);
          }
        });
    ObjectMapper mapper = initObjectMapper();
    String jsonResultUsages = mapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(suAggregateMap.values());

    BufferedWriter writerUsages = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix + "usages", "skills"));
    writerUsages.write(jsonResultUsages);
    writerUsages.close();
  }

  private static List<Skill> applyAdditionalUsages(List<Skill> pures) {
    String directoryPath = PATH_BASE + "04 - json/skills";
    final String ENDING = "ABILITIES_usages.json";

    List<File> files = extractFilesByEnding(directoryPath, ENDING);

    List<SkillUsage> usages = files.stream().map(f -> {
      String sb = readFromInputStream(f);
      final ObjectMapper objectMapper = initObjectMapper();
      try {
        return Arrays.stream(
            objectMapper.readValue(sb, ((SkillUsage[]) Array.newInstance(SkillUsage.class, 0)).getClass())).toList();
      }
      catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }).flatMap(List::stream).collect(Collectors.toList());

    return pures.stream().map(p -> {
      p.additionalUsageKeys = usages.stream()
          .filter(u -> u.skillKeys.contains(p.key))
          .map(u -> u.key)
          .collect(Collectors.toList());
      return p;
    }).collect(Collectors.toList());
  }

  private static List<Skill> applyAdditionalApplications(List<Skill> pures) {
    String directoryPath = PATH_BASE + "04 - json/skills";
    final String ENDING = "ABILITIES_applications.json";

    List<File> files = extractFilesByEnding(directoryPath, ENDING);

    List<SkillApplication> applications = files.stream().map(f -> {
      String sb = readFromInputStream(f);
      final ObjectMapper objectMapper = initObjectMapper();
      try {
        return Arrays.stream(
            objectMapper.readValue(sb, ((SkillApplication[]) Array.newInstance(SkillApplication.class, 0)).getClass())).toList();
      }
      catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }).flatMap(List::stream).collect(Collectors.toList());

    return pures.stream().map(p -> {
      p.applicationKeys = applications.stream()
          .filter(u -> u.skillKey == p.key)
          .map(u -> u.key)
          .collect(Collectors.toList());
      return p;
    }).collect(Collectors.toList());
  }

  private static List<File> extractFilesByEnding(String directoryPath, String ending) {
    File directory = new File(directoryPath);
    if (!directory.exists() || !directory.isDirectory()) {
      System.err.println("Das Verzeichnis " + directoryPath + " existiert nicht oder ist kein Verzeichnis.");

    }
    return List.of(directory.listFiles((dir, name) -> name.endsWith(ending)));
  }

  private static void extractAdditionalApplicationsAbilities(List<SpecialAbilityRaw> raws, TopicConfiguration conf) throws IOException {
    List<SkillApplication> result = raws.stream()
        .map(raw -> ExtractorSkill.retrieveSkillApplication(raw.rules, raw.name))
        .filter(sa -> sa != null && sa.name != null)
        .collect(Collectors.toList());

    if (result.size() > 0) {
      ObjectMapper usageMapper = initObjectMapper();
      String jsonResultUsages = usageMapper
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(result);

      BufferedWriter writerUsages = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix + "applications", "skills"));
      writerUsages.write(jsonResultUsages);
      writerUsages.close();
    }
  }

  private static void extractAdditionalApplicationsBoons(List<BoonRaw> raws, TopicConfiguration conf) throws IOException {
    List<SkillApplication> result = raws.stream()
        .map(raw -> ExtractorSkill.retrieveSkillApplication(raw.rules, raw.name))
        .filter(sa -> sa != null && sa.name != null)
        .collect(Collectors.toList());

    if (result.size() > 0) {
      ObjectMapper usageMapper = initObjectMapper();
      String jsonResultUsages = usageMapper
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(result);

      BufferedWriter writerUsages = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix + "applications", "skills"));
      writerUsages.write(jsonResultUsages);
      writerUsages.close();
    }
  }

  private static void extractAdditionalSkillUsages(List<SpecialAbilityRaw> raws, TopicConfiguration conf) throws IOException {

    List<SkillUsage> suCorrections = initExporterCorrections(SkillUsage.class);
    final List<SkillUsage> suList = raws.stream()
        .map(raw -> ExtractorSkill.retrieveSkillUsage(raw.rules))
        .filter(su -> su != null && su.name != null)
        .collect(Collectors.toList());

    List<SkillUsage> missing = suCorrections.stream()
        .filter(suc -> suList.stream().noneMatch(su -> su.key == suc.key))
        .collect(Collectors.toList());

    List<SkillUsage> result = suList.stream().map(su -> {
      Optional<SkillUsage> correction = suCorrections.stream().filter(c -> su.key == c.key).findFirst();
      if (correction.isPresent()) {
        return ObjectMerger.merge(correction.get(), su);
      }
      else {
        return su;
      }
    }).collect(Collectors.toList());

    if (conf.publication.equals("Kodex_des_Schwertes")) {
      result.addAll(missing);
    }

    if (result.size() > 0) {
      ObjectMapper usageMapper = initObjectMapper();
      String jsonResultUsages = usageMapper
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(result);

      BufferedWriter writerUsages = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix + "usages", "skills"));
      writerUsages.write(jsonResultUsages);
      writerUsages.close();
    }
  }

  private static <T> List<T> initExporterCorrections(Class<T> clazz) {
    List<T> returnValue;
    try {
      URL url = DsaPdfReaderMain.class.getClassLoader().getResource("exporter/" + clazz.getSimpleName() + ".json");
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

      StringBuilder sb = new StringBuilder();
      String content = null;
      while ((content = in.readLine()) != null) {
        sb.append(content);
      }
      in.close();


      Path path = Paths.get(url.toURI());
      byte[] bytes = Files.readAllBytes(path);

      final ObjectMapper objectMapper = initObjectMapper();
      returnValue = Arrays.stream(
          objectMapper.readValue(sb.toString(), ((T[]) Array.newInstance(clazz, 0)).getClass())
      ).map(o -> (T) o).toList();
    }
    catch (URISyntaxException | IOException e) {
      throw new RuntimeException(e);
    }

    return returnValue;
  }

  private static BufferedWriter generateBufferedWriter(String filePath) throws IOException {
    File file = new File(filePath);
    file.getParentFile().mkdirs();
    return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
  }

  private static void loadMeleeWeapons(List<MeleeWeaponRaw> raws, TopicConfiguration conf) {
    try {

      String pubPlusWeaponType = conf.publication + "_MELEE";

      List<MeleeWeapon> meleeWeapons = raws.stream()
          .filter(w -> Arrays.stream(COMBAT_SKILL_KEYS_RANGED).noneMatch(csk -> csk == w.combatSkillKey))
          .map(LoadToMeleeWeapon::migrate).filter(w -> w != null).collect(Collectors.toList());

      if (meleeWeapons.size() > 0) {
        ObjectMapper mapper = initObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(meleeWeapons);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, "equipment"));
        writer.write(jsonResult);
        writer.close();

        String prefix = "equipment_weapon_melee";
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_name"));
        writer.write(generateLocalisationString(meleeWeapons.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "WeaponKey", "name"));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_advantage"));
        writer.write(generateLocalisationString(meleeWeapons.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "WeaponKey", "advantage"));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_disadvantage"));
        writer.write(generateLocalisationString(meleeWeapons.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "WeaponKey", "disadvantage"));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_remark"));
        writer.write(generateLocalisationString(meleeWeapons.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "WeaponKey", "remark"));
        writer.close();
      }
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void loadRangedWeapons(List<RangedWeaponRaw> raws, TopicConfiguration conf) {
    try {
      String pubPlusWeaponType = conf.publication + "_RANGED";
      List<RangedWeapon> pures = raws.stream()
          .filter(w -> Arrays.stream(COMBAT_SKILL_KEYS_RANGED).anyMatch(csk -> csk == w.combatSkillKey))
          .map(LoadToRangedWeapon::migrate).collect(Collectors.toList());

      if (pures.size() > 0) {
        ObjectMapper mapper = initObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(pures);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, "equipment"));
        writer.write(jsonResult);
        writer.close();

        String prefix = "equipment_weapon_ranged";
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_name"));
        writer.write(generateLocalisationString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "WeaponKey", "name"));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_advantage"));
        writer.write(generateLocalisationString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "WeaponKey", "advantage"));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_disadvantage"));
        writer.write(generateLocalisationString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "WeaponKey", "disadvantage"));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_remark"));
        writer.write(generateLocalisationString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList()), "WeaponKey", "remark"));
        writer.close();
      }
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String generateMsEffectString(List<MysticalSkillRaw> rawMysticalSkills) {
    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream().forEach(msr -> {
      returnValue.append(ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr.publication, msr.name, Extractor.retrieveMsCategory(msr.topic)).toValue() + " {" + msr.effect + "}\r\n");
    });
    return returnValue.toString();

  }

  private static String generateMsaEffectString(List<MysticalActivityObjectRitualRaw> rawMysticalSkills) {
    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream().filter(msr -> msr.msCategory != null).forEach(msr -> {
      MysticalSkillKey key = ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr.publication, msr.name, msr.msCategory);
      if (key != null) {
        returnValue.append(key.toValue() + " {" + msr.effect + "}\r\n");
      }
      else {
        if (msr.name.startsWith("Mächtiger Patronruf")) {
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_adler_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_adler_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_baer_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_baer_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_eule_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_eule_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_falke_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_falke_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_feuermolch_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_feuermolch_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_fischotter_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_fischotter_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_gebirgsbock_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_gebirgsbock_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_gepard_jaguar_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_gepard_jaguar_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_khoramsbestie_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_khoramsbestie_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_khoramswuehler_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_khoramswuehler_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_loewe_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_loewe_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_luchs_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_luchs_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_mammut_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_mammut_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_mungo_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_mungo_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_nashorn_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_nashorn_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_nebelkraehe_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_nebelkraehe_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_pferd_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_pferd_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_rabe_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_rabe_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_saebelzahntiger_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_saebelzahntiger_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_sandwolf_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_sandwolf_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_stier_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_stier_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_vielfrass_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_vielfrass_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_widder_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_widder_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wildkatze_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wildkatze_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wildschwein_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wildschwein_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wolf_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wolf_ii.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wuergeschlange_i.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wuergeschlange_ii.toValue() + " {" + msr.effect + "}\r\n");
        }
        else if (msr.name.startsWith("Tierverwandlung ")) {
          returnValue.append(MysticalSkillKey.power_tierverwandlung_baer.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_eule.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_falke.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_feuermolch.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_fischotter.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_gebirgsbock.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_gepard_jaguar.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_khoramsbestie.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_khoramswuehler.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_loewe.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_luchs.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_mammut.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_mungo.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_nashorn.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_nebelkraehe.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_pferd.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_rabe.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_saebelzahntiger.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_sandwolf.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_stier.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_vielfrass.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_widder.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_wildkatze.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_wildschwein.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_wolf.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_wuergeschlange.toValue() + " {" + msr.effect + "}\r\n");
        }
        else if (msr.name.startsWith("Bannzeichen wider ")) {
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_feen.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_daimonide_und_chimaeren.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_daemonen.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_geister.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_spinnen.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_untote.toValue() + " {" + msr.effect + "}\r\n");

        }
        else if (msr.name.startsWith("Schutzrune vor ")) {
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_alfen_alfibanruna.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_daimonide_und_chimaeren_skepnabanruna.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_daemonen_vondurbanruna.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_elementare_verabanruna.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_geister_vandrendabanruna.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_hranngargezuecht_fylgjaruna.toValue() + " {" + msr.effect + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_untote_draugerbanruna.toValue() + " {" + msr.effect + "}\r\n");
        }
      }
    });
    return returnValue.toString();

  }


  private static String generateMsVariantNameString(List<MysticalSkillRaw> rawMysticalSkills) {

    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream()
        .forEach(msr -> {
          if (msr.variant1 != null && msr.variant1.key != null)
            returnValue.append(msr.variant1.key.ordinal() + " {" + msr.variant1.name + "}\r\n");
          if (msr.variant2 != null && msr.variant2.key != null)
            returnValue.append(msr.variant2.key.ordinal() + " {" + msr.variant2.name + "}\r\n");
          if (msr.variant3 != null && msr.variant3.key != null)
            returnValue.append(msr.variant3.key.ordinal() + " {" + msr.variant3.name + "}\r\n");
          if (msr.variant4 != null && msr.variant4.key != null)
            returnValue.append(msr.variant4.key.ordinal() + " {" + msr.variant4.name + "}\r\n");
          if (msr.variant5 != null && msr.variant5.key != null)
            returnValue.append(msr.variant5.key.ordinal() + " {" + msr.variant5.name + "}\r\n");
        });
    return returnValue.toString();
  }

  private static String generateMsVariantDescriptionString(List<MysticalSkillRaw> rawMysticalSkills) {

    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream()
        .forEach(msr -> {
          if (msr.variant1 != null && msr.variant1.key != null)
            returnValue.append(msr.variant1.key.ordinal() + " {" + msr.variant1.description + "}\r\n");
          if (msr.variant2 != null && msr.variant2.key != null)
            returnValue.append(msr.variant2.key.ordinal() + " {" + msr.variant2.description + "}\r\n");
          if (msr.variant3 != null && msr.variant3.key != null)
            returnValue.append(msr.variant3.key.ordinal() + " {" + msr.variant3.description + "}\r\n");
          if (msr.variant4 != null && msr.variant4.key != null)
            returnValue.append(msr.variant4.key.ordinal() + " {" + msr.variant4.description + "}\r\n");
          if (msr.variant5 != null && msr.variant5.key != null)
            returnValue.append(msr.variant5.key.ordinal() + " {" + msr.variant5.description + "}\r\n");
        });
    return returnValue.toString();
  }

  private static Triplet<StringBuilder, StringBuilder, StringBuilder> generateTraditionStringBuilders(List<TraditionRaw> rawTraditions) {
    Triplet<StringBuilder, StringBuilder, StringBuilder> returnValue = new Triplet<>(new StringBuilder(), new StringBuilder(), new StringBuilder());
    rawTraditions.forEach(raw -> {

      SpecialAbilityKey key = ExtractorSpecialAbility.retrieve(raw.name
          .replace("Tradition Intuitiven Zauberer", "tradition_intuitive_zauberer")
          .replace("Tradition Ysilischen Bannzeichner", "tradition_ysilische_bannzeichner")
          .replace("Tradition Angroschgeweihter", "tradition_angroschkirche")
          .replace("Tradition Nivesenscham -anen", "tradition_nivesenschamanen")
          .replace("Tradition Tairachschamane", "tradition_tairachkult")
      );

      List<Pair<SpecialAbilityKey, String>> keyNames = new ArrayList<>();

      keyNames.add(new Pair<>(key, raw.name));

      keyNames.forEach(p -> {
        StringBuilder htmlListBuilder = new StringBuilder();
        htmlListBuilder.append("<ul>");
        Arrays.stream(raw.description.split("#")).filter(d -> !d.trim().isEmpty()).forEach(d -> htmlListBuilder.append("<li>").append(d.trim()).append("</li>"));
        htmlListBuilder.append("</ul>");
        if (p.getValue0() != null) {
          returnValue.getValue0().append(p.getValue0().toValue() + " {" + p.getValue1() + "}\r\n");
          returnValue.getValue1().append(p.getValue0().toValue() + " {" + htmlListBuilder + "} \r\n");
          returnValue.getValue2().append(p.getValue0().toValue() + " {" + raw.preconditions + "} \r\n");
        }
        else {
          LOGGER.error("Tradition Special Ability (" + p.getValue1() + ") has no key!");
        }
      });
    });
    return returnValue;
  }

  private static Quartet<StringBuilder, StringBuilder, StringBuilder, StringBuilder> generateSaStringBuilders(List<SpecialAbilityRaw> rawMysticalSkills) {
    Quartet<StringBuilder, StringBuilder, StringBuilder, StringBuilder> returnValue = new Quartet<>(new StringBuilder(), new StringBuilder(), new StringBuilder(), new StringBuilder());
    rawMysticalSkills.stream().forEach(raw -> {

      int levels = LoadToSpecialAbility.extractLevels(raw.name);
      String baseName = levels > 1
          ? raw.name.split("(?= (I-|I\\/))")[0]
          : raw.name;

      boolean ignoreBrackets = Arrays.stream(LoadToSpecialAbility.BRACKETED_NAMES).filter(bn -> baseName.startsWith(bn)).count() == 0;
      for (int currentLevel = 0; currentLevel < levels; currentLevel++) {
        String name = LoadToSpecialAbility.extractName(baseName, levels, currentLevel, ignoreBrackets);
        SpecialAbilityKey key = null;

        SaSpecialRequirementsToggle saReqToggle = new SaSpecialRequirementsToggle(raw.name, raw.publication);

        if (saReqToggle.isBaseRequirement())
          key = ExtractorSpecialAbility.retrieve(name);

        List<Pair<SpecialAbilityKey, String>> keyNames = new ArrayList<>();
        if (saReqToggle.isAuthor) {
          keyNames.addAll(
              LoadToSpecialAbility.generateScribeList(new SpecialAbility()).stream()
                  .map(sa -> new Pair<>(sa.key, sa.name))
                  .collect(Collectors.toList()));
        }
        else if (saReqToggle.isHealingSpec) {
          keyNames.addAll(
              LoadToSpecialAbility.generateHealingSpecList(new SpecialAbility()).stream()
                  .map(sa -> new Pair<>(sa.key, sa.name))
                  .collect(Collectors.toList()));
        }
        else if (saReqToggle.isGebieterDesAspekts) {
          keyNames.addAll(
              LoadToSpecialAbility.generateGebieterDesAspektsList(new SpecialAbility(), "").stream()
                  .map(sa -> new Pair<>(sa.key, sa.name))
                  .collect(Collectors.toList()));
        }
        else if (saReqToggle.isDemonicBinding) {
          keyNames.addAll(
              LoadToSpecialAbility.generateDemonicBinding(new SpecialAbility()).stream()
                  .map(sa -> new Pair<>(sa.key, sa.name))
                  .toList());
        }
        else if (saReqToggle.isDemonicTrueName) {
          keyNames.addAll(
              LoadToSpecialAbility.generateDemonicWahreNamen(new SpecialAbility()).stream()
                  .map(sa -> new Pair<>(sa.key, sa.name))
                  .toList());
        }
        else if (saReqToggle.isElementalBinding) {
          keyNames.addAll(
              LoadToSpecialAbility.generateElementalBinding(new SpecialAbility()).stream()
                  .map(sa -> new Pair<>(sa.key, sa.name))
                  .toList());
        }
        else if (saReqToggle.isElementalTrueName) {
          keyNames.addAll(
              LoadToSpecialAbility.generateElementalWahreNamen(new SpecialAbility()).stream()
                  .map(sa -> new Pair<>(sa.key, sa.name))
                  .toList());
        }
        else if (saReqToggle.isFairyBinding) {
          keyNames.addAll(
              LoadToSpecialAbility.generateFairyBinding(new SpecialAbility()).stream()
                  .map(sa -> new Pair<>(sa.key, sa.name))
                  .toList());
        }
        else {
          keyNames.add(new Pair<>(key, name));
        }

        keyNames.forEach(p -> {
          if (p.getValue0() != null) {
            appendLocalisationJson(returnValue.getValue0(), "SpecialAbilityKey", p.getValue0().toValue(), "name", p.getValue1());
            appendLocalisationString(returnValue.getValue1(), "SpecialAbilityKey", p.getValue0().toValue(), "rule", raw.rules);
            appendLocalisationString(returnValue.getValue2(), "SpecialAbilityKey", p.getValue0().toValue(), "description", raw.description);
            appendLocalisationString(returnValue.getValue3(), "SpecialAbilityKey", p.getValue0().toValue(), "precondition", raw.preconditions);
          }
          else {
            LOGGER.error("Special Ability (" + p.getValue1() + ") has no key!");
          }
        });
      }

    });
    return returnValue;
  }

  private static Triplet<StringBuilder, StringBuilder, StringBuilder> generateOrStringBuilders(List<MysticalActivityObjectRitualRaw> raws) {
    Triplet<StringBuilder, StringBuilder, StringBuilder> returnValue = new Triplet<>(new StringBuilder(), new StringBuilder(), new StringBuilder());
    raws.stream()
        .forEach(raw -> {

          int levels = LoadToObjectRitual.extractLevels(raw);
          String baseName = levels > 1
              ? raw.name.split("(?= (I-|I\\/))")[0]
              : raw.name;

          for (int currentLevel = 0; currentLevel < levels; currentLevel++) {
            String name = LoadToObjectRitual.extractName(baseName, levels, currentLevel);
            ObjectRitualKey key = ExtractorObjectRitual.extractOrKeyFromName(LoadToObjectRitual.extractKeyName(baseName, levels, currentLevel, raw.artifactKey));

            if (key != null) {
              returnValue.getValue0().append(key.toValue() + " {" + name + "}\r\n");
              returnValue.getValue1().append(key.toValue() + " {" + raw.effect + "} \r\n");
              returnValue.getValue2().append(key.toValue() + " {" + raw.requirements + "} \r\n");
            }
            else {
              LOGGER.error("ObjectRitual (" + raw.name + ") has no key!");
            }
          }

        });
    return returnValue;
  }

  private static Quintet<StringBuilder, StringBuilder, StringBuilder, StringBuilder, StringBuilder> generateBoonStringBuilders(List<BoonRaw> rawBoon) {
    Quintet<StringBuilder, StringBuilder, StringBuilder, StringBuilder, StringBuilder> returnValue = new Quintet<>(new StringBuilder(), new StringBuilder(), new StringBuilder(), new StringBuilder(), new StringBuilder());

    rawBoon.stream().forEach(raw -> {
      int levels = LoadToSpecialAbility.extractLevels(raw.name);
      String name = (levels > 1
          ? raw.name.split("(?= (I-|I\\/))")[0]
          : raw.name)
          .replace("Ahnenblut-Vorteile", "")
          .replace("Feenblut-Vorteile", "");
      BoonKey key = ExtractorBoon.retrieve(name);

      Pair<BoonKey, String> p = new Pair<>(key, name);

      if (p.getValue0() != null) {
        appendLocalisationString(returnValue.getValue0(), "BoonKey", key.ordinal(), "name", p.getValue1());
        appendLocalisationString(returnValue.getValue1(), "BoonKey", key.ordinal(), "regel", raw.rules);
        appendLocalisationString(returnValue.getValue2(), "BoonKey", key.ordinal(), "beschreibung", raw.description);
        appendLocalisationString(returnValue.getValue3(), "BoonKey", key.ordinal(), "vorbedingungen", raw.preconditions);
        appendLocalisationString(returnValue.getValue4(), "BoonKey", key.ordinal(), "ap", raw.ap);
//        returnValue.getValue0().append(p.getValue0().toValue() + " {" + p.getValue1() + "}\r\n");
//        returnValue.getValue1().append(p.getValue0().toValue() + " {" + raw.rules + "} \r\n");
//        returnValue.getValue2().append(p.getValue0().toValue() + " {" + raw.description + "} \r\n");
//        returnValue.getValue3().append(p.getValue0().toValue() + " {" + raw.preconditions + "} \r\n");
//        returnValue.getValue4().append(p.getValue0().toValue() + " {" + raw.ap + "} \r\n");
      }
      else {
        LOGGER.error("Boon (" + p.getValue1() + ") has no key!");
      }


    });
    return returnValue;
  }

  private static String generateMsName(List<MysticalSkill> rawMysticalSkills) {
    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.forEach(msr -> {
      returnValue.append(ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr.publication.name(), msr.name, msr.category).toValue())
          .append(" {").append(msr.name).append("} ")
          .append("\r\n");
      if (msr.name == null || msr.name.isEmpty()) {
        LOGGER.error("Mystical Skill ({}) has no name!", msr.name);
      }
    });
    return returnValue.toString();

  }

  private static String generateMsDescriptionString(List<MysticalSkillRaw> rawMysticalSkills) {
    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream().forEach(msr -> {
      returnValue.append(
          ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr.publication, msr.name, Extractor.retrieveMsCategory(msr.topic)).toValue()
              + " {"
              + msr.description
              + "} "
              + "\r\n");
      if ((msr.description == null || msr.description.isEmpty())
          && msr.msCategory != MysticalSkillCategory.blessing
          && msr.msCategory != MysticalSkillCategory.trick) {
        LOGGER.error("Mystical Skill (" + msr.name + ") has no description!");
      }
    });
    return returnValue.toString();
  }

  private static String generateMsCostString(List<MysticalSkillRaw> rawMysticalSkills) {
    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream().forEach(msr -> {
      returnValue.append(
          ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr.publication, msr.name, Extractor.retrieveMsCategory(msr.topic)).toValue()
              + " {"
              + msr.cost
              + "} "
              + "\r\n");
      if (msr.cost == null || msr.cost.isEmpty()) {
        LOGGER.error("Mystical Skill (" + msr.name + ") has no description!");
      }
    });
    return returnValue.toString();
  }

  private static String generateMsaCostString(List<MysticalActivityObjectRitualRaw> rawMysticalSkills) {
    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream().filter(msr -> msr.msCategory != null).forEach(msr -> {
      MysticalSkillKey key = ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr.publication, msr.name, msr.msCategory);
      if (key != null) {
        returnValue.append(
            key.toValue()
                + " {"
                + msr.cost
                + "} "
                + "\r\n");
        if (msr.cost == null || msr.cost.isEmpty()) {
          LOGGER.error("Mystical Skill (" + msr.name + ") has no description!");
        }
      }
      else {
        if (msr.name.startsWith("Mächtiger Patronruf")) {
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_adler_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_adler_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_baer_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_baer_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_eule_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_eule_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_falke_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_falke_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_feuermolch_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_feuermolch_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_fischotter_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_fischotter_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_gebirgsbock_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_gebirgsbock_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_gepard_jaguar_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_gepard_jaguar_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_khoramsbestie_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_khoramsbestie_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_khoramswuehler_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_khoramswuehler_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_loewe_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_loewe_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_luchs_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_luchs_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_mammut_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_mammut_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_mungo_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_mungo_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_nashorn_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_nashorn_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_nebelkraehe_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_nebelkraehe_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_pferd_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_pferd_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_rabe_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_rabe_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_saebelzahntiger_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_saebelzahntiger_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_sandwolf_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_sandwolf_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_stier_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_stier_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_vielfrass_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_vielfrass_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_widder_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_widder_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wildkatze_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wildkatze_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wildschwein_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wildschwein_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wolf_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wolf_ii.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wuergeschlange_i.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_maechtiger_patronruf_wuergeschlange_ii.toValue() + " {" + msr.cost + "}\r\n");
        }
        else if (msr.name.startsWith("Tierverwandlung ")) {
          returnValue.append(MysticalSkillKey.power_tierverwandlung_baer.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_eule.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_falke.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_feuermolch.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_fischotter.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_gebirgsbock.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_gepard_jaguar.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_khoramsbestie.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_khoramswuehler.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_loewe.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_luchs.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_mammut.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_mungo.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_nashorn.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_nebelkraehe.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_pferd.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_rabe.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_saebelzahntiger.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_sandwolf.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_stier.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_vielfrass.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_widder.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_wildkatze.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_wildschwein.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_wolf.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.power_tierverwandlung_wuergeschlange.toValue() + " {" + msr.cost + "}\r\n");
        }
        else if (msr.name.startsWith("Bannzeichen wider ")) {
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_feen.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_daimonide_und_chimaeren.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_daemonen.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_geister.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_spinnen.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.bansign_bannzeichen_wider_untote.toValue() + " {" + msr.cost + "}\r\n");

        }
        else if (msr.name.startsWith("Schutzrune vor ")) {
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_alfen_alfibanruna.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_daimonide_und_chimaeren_skepnabanruna.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_daemonen_vondurbanruna.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_elementare_verabanruna.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_geister_vandrendabanruna.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_hranngargezuecht_fylgjaruna.toValue() + " {" + msr.cost + "}\r\n");
          returnValue.append(MysticalSkillKey.rune_schutzrune_vor_untote_draugerbanruna.toValue() + " {" + msr.cost + "}\r\n");
        }
      }
    });
    return returnValue.toString();
  }

  private static String generateLocalisationStringLEGACY(List<EquipmentI> equipments, String enumName, String valueName) {
    StringBuilder returnValue = new StringBuilder();

    //[ArmorCategoryKey.plate, $localize`:@@armorCategoryKey-0:Plattenrüstung`]
    equipments.stream().forEach(e -> appendLocalisationString(
        returnValue,
        enumName,
        e.getKeyValue(),
        valueName,
        callGetter(e, valueName)
    ));
    return returnValue.toString();
  }

  private static <T> String generateLocalisationString(
      List<T> equipments,
      String enumName,
      String valueName
  ) {
    StringBuilder returnValue = new StringBuilder();

    for (T e : equipments) {
      try {
        // get enum object
        Object enumObj = e.getClass().getField("key").get(e);

        if (!(enumObj instanceof Enum<?> enumValue)) {
          throw new RuntimeException("Field 'key' is not an enum!");
        }

        int key = enumValue.ordinal();


        appendLocalisationString(
            returnValue,
            enumName,
            key,
            valueName,
            e.getClass().getField(valueName).get(e)
        );
      }
      catch (NoSuchFieldException | IllegalAccessException ex) {
        throw new RuntimeException(ex);
      }
    }

    return returnValue.toString();
  }

  private static void appendLocalisationString(StringBuilder returnValue, String enumName, Integer keyValue, String valueName, Object value) {
    String keyName = getEnumKeyName(enumName, keyValue);
    if (value == null || ((String) value).isEmpty()) {// Achtung, der Cast auf String könnte falsch sein
      returnValue.append("//");
      returnValue.append(enumName);
      returnValue.append(".");
      returnValue.append(keyName);
      returnValue.append(" hat keine Übersetzung für ");
      returnValue.append(valueName);
    }
    else {
      returnValue.append("[");
      returnValue.append(enumName);
      returnValue.append(".");
      returnValue.append(keyName);
      returnValue.append(", $localize`:@@");
      returnValue.append(keyName);
      returnValue.append("-");
      returnValue.append(valueName);
      returnValue.append("-");
      returnValue.append(keyValue);
      returnValue.append(":");
      returnValue.append(value);
      returnValue.append("`],");
    }
    returnValue.append("\r\n");
  }

  private static void appendLocalisationJson(StringBuilder returnValue, String enumName, Integer keyValue, String valueName, Object value) {
    String keyName = getEnumKeyName(enumName, keyValue);
    if (value == null || ((String) value).isEmpty()) {// Achtung, der Cast auf String könnte falsch sein
    }
    else {
      returnValue.append("[");
      returnValue.append(keyValue);
      returnValue.append(", \"");
//      returnValue.append("@@");
//      returnValue.append(keyName);
//      returnValue.append("-");
//      returnValue.append(valueName);
//      returnValue.append("-");
//      returnValue.append(keyValue);
//      returnValue.append(":");
      returnValue.append(value);
      returnValue.append("\"],");
    }
    returnValue.append("\r\n");
  }

  private static String getEnumKeyName(String enumName, int keyValue) {
    try {
      // Load enum class dynamically
      Class<?> enumClass = Class.forName("de.pho.dsapdfreader.exporter.model.enums." + enumName);

      // Ensure it's actually an enum
      if (!enumClass.isEnum()) {
        throw new IllegalArgumentException(enumName + " is not an enum type");
      }

      // Get all enum constants
      Object[] constants = enumClass.getEnumConstants();

      // Get the correct enum constant from ordinal / index
      Object enumConst = constants[keyValue];

      // Return the enum's name (e.g. "dolch")
      return ((Enum<?>) enumConst).name();

    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException("Enum not found: " + enumName, e);
    }
  }

  private static Object callGetter(Object target, String valueName) {
    try {
      // Build method name, e.g. "name" -> "getName"
      String getterName =
          "get" + valueName.substring(0, 1).toUpperCase() + valueName.substring(1);

      // Look up method
      Method getter = target.getClass().getMethod(getterName);

      // Execute the getter
      return getter.invoke(target);

    }
    catch (Exception ex) {
      throw new RuntimeException("Could not call getter for " + valueName, ex);
    }
  }

  private static List<TextWithMetaInfo> extractPdfResults(TopicConfiguration conf) throws IOException {
    List<TextWithMetaInfo> returnValue = new ArrayList<>();

    String path = conf.path;
    if (path == null) {
      URL urlPdfLibInClassPath = DsaPdfReaderMain.class.getClassLoader().getResource("./pdf_lib");
      if (urlPdfLibInClassPath == null) {
        throw new InvalidPathException("./pdf_lib", "der Pfad für die PDF-Bibliothek konnte nicht gefunden werden");
      }
      path = urlPdfLibInClassPath.getFile();
    }
    //File fIn = new File(PDF_BASE_PATH + path + "/" + conf.pdfName);
    File fIn = loadFile(path, conf.pdfName);
    returnValue.addAll(PdfReader.convertToText(fIn, conf));
    return returnValue;

  }

  private static File loadFile(String pathSegment, String pdfName) {

    File file = new File(PDF_BASE_PATH + pathSegment + "/" + pdfName);

    if (file.exists() && file.isFile()) {
      return file;
    }
    else {
      file = new File(PDF_BASE_PATH_2 + pathSegment + "/" + pdfName);
      if (!file.exists() || !file.isFile()) file = new File(PDF_BASE_PATH_3 + pathSegment + "/" + pdfName);
      if (!file.exists() || !file.isFile()) return null;
      else return file;
    }
  }

  private static List<TextWithMetaInfo> applyStrategies(List<TextWithMetaInfo> texts, TopicConfiguration conf) {
    if (conf.strategyMapping != null && !conf.strategyMapping.isEmpty()) {
      TopicStrategies topicMappings = unmarshall(conf.strategyMapping);
      if (topicMappings != null) {
        List<TopicStrategies.Strategy> applicableStrategies = topicMappings.getStrategy().stream()
            .filter(s -> s.getParams().getParameter().stream()
                .filter(p -> p.getKey().equals("applyToPage"))
                .filter(p -> p.getValue().equals("all") || (Integer.valueOf(p.getValue()) >= conf.startPage) && (Integer.valueOf(p.getValue()) <= conf.endPage))
                .count() > 0
            ).collect(Collectors.toList());
        for (TopicStrategies.Strategy s : applicableStrategies) {
          try {
            DsaConverterStrategy currentStrategy = (DsaConverterStrategy) Class
                .forName(STRATEGY_PACKAGE + s.getStrategyClass().trim())
                .getDeclaredConstructor()
                .newInstance();
            texts = currentStrategy.applyStrategy(texts, s.getParams().getParameter(), s.getName(), conf.publication, conf.topic);
          }
          catch (InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException
                 | NoSuchMethodException
                 | ClassNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
          }
        }
      }
    }
    return texts;
  }

  private static TopicStrategies unmarshall(String fileName) {
    LOGGER.debug("initialize Strategy mapping: %s", fileName);
    URL url = DsaPdfReaderMain.class.getResource("/" + fileName);
    File file = new File(url.getFile());
    JAXBContext jaxbContext;
    TopicStrategies returnValue = null;
    try {
      jaxbContext = JAXBContext.newInstance(TopicStrategies.class);

      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      returnValue = (TopicStrategies) jaxbUnmarshaller.unmarshal(file);
    }
    catch (JAXBException e) {
      e.printStackTrace();
    }

    return returnValue;
  }

  private static void initConfig() {
    configs = ConfigurationInitializer.readTopicConfigurations();
  }

  private static List parseResult(List<TextWithMetaInfo> texts, TopicConfiguration conf) {
    List results = null;
    switch (conf.topic) {
    case BOONS -> results = new DsaConverterBoon().convertTextWithMetaInfo(texts, conf);
    case SPELLS_GRIMORIUM, RITUALS_GRIMORIUM, LITURGY_DIVINARIUM, CEREMONY_DIVINARIUM ->
        results = new DsaConverterMysticalSkillGrimorium().convertTextWithMetaInfo(texts, conf);
    case TRICKS_GRIMORIUM, BLESSING_DIVINARIUM -> results = new DsaConverterMysticalSkillGrimoriumTricks().convertTextWithMetaInfo(texts, conf);
    case TRADITIONS -> results = new DsaConverterTraditionsToSpecialAbility().convertTextWithMetaInfo(texts, conf);
    case INCANTATIONS_RIME_SPELLS_GRIMORIUM, INCANTATIONS_ZHAYAD_SPELLS_GRIMORIUM -> {
      Map<String, String> incantations = new DsaConverterMsyticalSkillIncantations().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(incantations, TopicEnum.SPELLS_GRIMORIUM, conf.topic, conf.publication);
    }
    case INCANTATIONS_RIME_RITUALS_GRIMORIUM, INCANTATIONS_ZHAYAD_RITUALS_GRIMORIUM -> {
      Map<String, String> incantations = new DsaConverterMsyticalSkillIncantations().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(incantations, TopicEnum.RITUALS_GRIMORIUM, conf.topic, conf.publication);
    }
    case ELEMENTS_SPELLS_GRIMORIUM -> {
      Map<String, String> spellsToElements = new DsaConverterMsyticalSkillElements().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(spellsToElements, TopicEnum.SPELLS_GRIMORIUM, conf.topic, conf.publication);
    }
    case ELEMENTS_RITUALS_GRIMORIUM -> {
      Map<String, String> spellsToElements = new DsaConverterMsyticalSkillElements().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(spellsToElements, TopicEnum.RITUALS_GRIMORIUM, conf.topic, conf.publication);
    }
    case PROFESSIONS -> results = new DsaConverterProfession().convertTextWithMetaInfo(texts, conf);
    case WAFFEN -> results = new DsaConverterWeapon().convertTextWithMetaInfo(texts, conf);
    case WAFFENLISTE -> results = new DsaConverterWeaponLists().convertTextWithMetaInfo(texts, conf);
    case RÜSTUNGEN -> results = new DsaConverterArmor().convertTextWithMetaInfo(texts, conf);
    case RÜSTUNGEN_HELME -> results = new DsaConverterArmorPart().convertTextWithMetaInfo(texts, conf);
    case RÜSTUNGEN_TEILE -> results = new DsaConverterArmorPart().convertTextWithMetaInfo(texts, conf);
    case RÜSTUNGENLISTE -> results = new DsaConverterArmorLists().convertTextWithMetaInfo(texts, conf);
    case VERBREITUNG_WAFFEN -> results = DsaConverterWeaponAvailability.convertTextWithMetaInfo(texts, conf);
    case VERBREITUNG_RÜSTUNG -> results = DsaConverterArmorAvailability.convertTextWithMetaInfo(texts, conf);
    case AUSRÜSTUNG -> results = new DsaConverterEquipment().convertTextWithMetaInfo(texts, conf);
    case ABILITIES -> results = new DsaConverterSpecialAbilityKodex().convertTextWithMetaInfo(texts, conf);
    case SKILLS -> results = new DsaConverterSkillKodex().convertTextWithMetaInfo(texts, conf);
    case MYSTICAL_SKILL_ACTIVITIES_MAGIC -> results = new DsaConverterMysticalSkillActivityAndArtifacts().convertTextWithMetaInfo(texts, conf);
    case CLERICAL_OBJECT_RITUALS -> results = new DsaConverterClericalObjectRituals().convertTextWithMetaInfo(texts, conf);
    case PROFILE -> results = new DsaConverterProfile().convertTextWithMetaInfo(texts, conf);
    case CURRICULUM -> results = new DsaConverterCurriculum().convertTextWithMetaInfo(texts, conf);
    case CONTENT_LAND_UND_LEUTE -> results = new DsaConverterContent().convertTextWithMetaInfo(texts, conf);
    case PRAEGUNG -> results = DsaConverterPraegung.convertTextWithMetaInfo(texts, conf);
    case HERBS -> results = new DsaConverterHerb().convertTextWithMetaInfo(texts, conf);
    case TAVERN -> results = new DsaConverterBeverage().convertTextWithMetaInfo(texts, conf);
    default -> LOGGER.error(String.format("Unexpected value (parseToRaw): %s", conf.topic));
    }
    return results;
  }

  private static void applyContentMapToMysticalSkills(Map<String, String> contentMap, TopicEnum topicApplyTo, TopicEnum topicFrom, String publication) {
    try (Stream<Path> paths = Files.walk(Paths.get(PATH_STRATEGY_2_RAW))) {
      HashMap<Path, List<MysticalSkillRaw>> resultMap = new HashMap();
      HashMap<Path, List<MysticalSkillRaw>> handledResults = new HashMap<>();
      paths.filter(Files::isRegularFile)
          .filter(p -> !p.endsWith("all_raw.csv")
              && (p.toString().contains(topicApplyTo.name())
              && p.toString().contains(publication)))
          .forEach(p -> resultMap.put(p, CsvHandler.readBeanFromPath(MysticalSkillRaw.class, p)));

      resultMap.forEach((k, v) -> {
        List newMsList = handledResults.containsKey(k)
            ? applyNewContentToMsr(handledResults.get(k), contentMap, topicFrom)
            : applyNewContentToMsr(v, contentMap, topicFrom);
        handledResults.put(k, newMsList);
        File fOut = new File(k.toString());
        CsvHandler.writeBeanToUrl(fOut, newMsList);
      });
    }
    catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private static List applyNewContentToMsr(List<MysticalSkillRaw> valueList, Map<String, String> contentMap, TopicEnum topic) {
    return valueList.stream().map(msr -> {
      Optional<String> incantationKeyOptional = contentMap.keySet().stream().filter(c ->
          c.toLowerCase().contains(msr.name.toLowerCase())
              || msr.name.toLowerCase().contains(c.toLowerCase())).findFirst();
      return applyContentToMysticalSkillRaw(msr, contentMap, incantationKeyOptional, topic);
    }).collect(Collectors.toList());
  }

  private static MysticalSkillRaw applyContentToMysticalSkillRaw(MysticalSkillRaw msr, Map<String, String> contentMap, Optional<String> keyOptional, TopicEnum topicFrom) {
    if (keyOptional.isPresent()) {
      if (topicFrom == TopicEnum.INCANTATIONS_RIME_SPELLS_GRIMORIUM || topicFrom == TopicEnum.INCANTATIONS_RIME_RITUALS_GRIMORIUM)
        msr.rime = contentMap.get(keyOptional.get());
      if (topicFrom == TopicEnum.INCANTATIONS_ZHAYAD_SPELLS_GRIMORIUM || topicFrom == TopicEnum.INCANTATIONS_ZHAYAD_RITUALS_GRIMORIUM)
        msr.zhayad = contentMap.get(keyOptional.get());
      if (topicFrom == TopicEnum.ELEMENTS_SPELLS_GRIMORIUM || topicFrom == TopicEnum.ELEMENTS_RITUALS_GRIMORIUM)
        msr.elements = contentMap.get(keyOptional.get());
    }
    return msr;
  }

  private static void applyToMysticalSkills(Map<String, List<String>> commonnessList) {
    try (Stream<Path> paths = Files.walk(Paths.get(PATH_STRATEGY_2_RAW))) {
      HashMap<Path, List<MysticalSkillRaw>> resultMap = new HashMap();
      paths.filter(Files::isRegularFile)
          .filter(p -> !p.endsWith("all_raw.csv"))
          .forEach(p -> resultMap.put(p, CsvHandler.readBeanFromPath(MysticalSkillRaw.class, p)));

      resultMap.forEach((k, v) -> {
        List<MysticalSkillRaw> newMsList = v.stream().map(msr -> {
          if (commonnessList.containsKey(msr.name)) {
            for (String commonness : commonnessList.get(msr.name)) {
              if (!(msr.commonness.contains(commonness) || msr.commonness.contains(commonness.toLowerCase())))
                msr.commonness = msr.commonness + ", " + commonness;
            }
          }
          return msr;
        }).collect(Collectors.toList());
        File fOut = new File(k.toString());
        CsvHandler.writeBeanToUrl(fOut, newMsList);
      });
    }
    catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private static List<SpecialAbility> generateClericalTraditions() {
    List<SpecialAbility> returnValue = new ArrayList<>();

    SpecialAbility tradPraios = new SpecialAbility();
    tradPraios.name = "Tradition (Praioskirche)";
    tradPraios.key = SpecialAbilityKey.tradition_praioskirche;
    tradPraios.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradPraios.ap = 130f;
    tradPraios.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradPraios);

    SpecialAbility tradRondra = new SpecialAbility();
    tradRondra.name = "Tradition (Rondrakirche)";
    tradRondra.key = SpecialAbilityKey.tradition_rondrakirche;
    tradRondra.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradRondra.ap = 150f;
    tradRondra.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradRondra);


    SpecialAbility tradEfferd = new SpecialAbility();
    tradEfferd.name = "Tradition (Efferdkirche)";
    tradEfferd.key = SpecialAbilityKey.tradition_efferdkirche;
    tradEfferd.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradEfferd.ap = 130f;
    tradEfferd.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradEfferd);

    SpecialAbility tradTravia = new SpecialAbility();
    tradTravia.name = "Tradition (Traviakirche)";
    tradTravia.key = SpecialAbilityKey.tradition_traviakirche;
    tradTravia.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradTravia.ap = 110f;
    tradTravia.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradTravia);

    SpecialAbility tradBoron = new SpecialAbility();
    tradBoron.name = "Tradition (Boronkirche)";
    tradBoron.key = SpecialAbilityKey.tradition_boronkirche;
    tradBoron.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradBoron.ap = 130f;
    tradBoron.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradBoron);

    SpecialAbility tradHesinde = new SpecialAbility();
    tradHesinde.name = "Tradition (Hesindekirche)";
    tradHesinde.key = SpecialAbilityKey.tradition_hesindekirche;
    tradHesinde.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradHesinde.ap = 130f;
    tradHesinde.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradHesinde);

    SpecialAbility tradFirun = new SpecialAbility();
    tradFirun.name = "Tradition (Firunkirche)";
    tradFirun.key = SpecialAbilityKey.tradition_firunkirche;
    tradFirun.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradFirun.ap = 140f;
    tradFirun.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradFirun);

    SpecialAbility tradTsa = new SpecialAbility();
    tradTsa.name = "Tradition (Tsakirche)";
    tradTsa.key = SpecialAbilityKey.tradition_tsakirche;
    tradTsa.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradTsa.ap = 140f;
    tradTsa.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradTsa);

    SpecialAbility tradPhex = new SpecialAbility();
    tradPhex.name = "Tradition (Phexkirche)";
    tradPhex.key = SpecialAbilityKey.tradition_phexkirche;
    tradPhex.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradPhex.ap = 150f;
    tradPhex.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradPhex);

    SpecialAbility tradPeraine = new SpecialAbility();
    tradPeraine.name = "Tradition (Perainekirche)";
    tradPeraine.key = SpecialAbilityKey.tradition_perainekirche;
    tradPeraine.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradPeraine.ap = 110f;
    tradPeraine.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradPeraine);

    SpecialAbility tradIngerimm = new SpecialAbility();
    tradIngerimm.name = "Tradition (Ingerimmkirche)";
    tradIngerimm.key = SpecialAbilityKey.tradition_ingerimmkirche;
    tradIngerimm.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradIngerimm.ap = 125f;
    tradIngerimm.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradIngerimm);

    SpecialAbility tradRahja = new SpecialAbility();
    tradRahja.name = "Tradition (Rahjakirche)";
    tradRahja.key = SpecialAbilityKey.tradition_rahjakirche;
    tradRahja.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradRahja.ap = 125f;
    tradRahja.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradRahja);

    SpecialAbility tradAves = new SpecialAbility();
    tradAves.name = "Tradition (Aveskirche)";
    tradAves.key = SpecialAbilityKey.tradition_aveskirche;
    tradAves.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradAves.ap = 110f;
    tradAves.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradAves);

    SpecialAbility tradIfirn = new SpecialAbility();
    tradIfirn.name = "Tradition (Ifirnkirche)";
    tradIfirn.key = SpecialAbilityKey.tradition_ifirnkirche;
    tradIfirn.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradIfirn.ap = 105f;
    tradIfirn.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradIfirn);

    SpecialAbility tradKor = new SpecialAbility();
    tradKor.name = "Tradition (Korkirche)";
    tradKor.key = SpecialAbilityKey.tradition_korkirche;
    tradKor.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradKor.ap = 130f;
    tradKor.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradKor);

    SpecialAbility tradNandus = new SpecialAbility();
    tradNandus.name = "Tradition (Nanduskirche)";
    tradNandus.key = SpecialAbilityKey.tradition_nanduskirche;
    tradNandus.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradNandus.ap = 130f;
    tradNandus.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradNandus);

    SpecialAbility tradSwafnir = new SpecialAbility();
    tradSwafnir.name = "Tradition (Swafnirkirche)";
    tradSwafnir.key = SpecialAbilityKey.tradition_swafnirkirche;
    tradSwafnir.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradSwafnir.ap = 115f;
    tradSwafnir.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradSwafnir);

    SpecialAbility Levthan = new SpecialAbility();
    Levthan.name = "Tradition (Levthankult)";
    Levthan.key = SpecialAbilityKey.tradition_levthankult;
    Levthan.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    Levthan.ap = 125f;
    Levthan.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(Levthan);

    SpecialAbility tradMarbo = new SpecialAbility();
    tradMarbo.name = "Tradition (Marbokult)";
    tradMarbo.key = SpecialAbilityKey.tradition_marbokult;
    tradMarbo.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradMarbo.ap = 120f;
    tradMarbo.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradMarbo);

    SpecialAbility tradNuminorukult = new SpecialAbility();
    tradNuminorukult.name = "Tradition (Numinorukult)";
    tradNuminorukult.key = SpecialAbilityKey.tradition_numinorukult;
    tradNuminorukult.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradNuminorukult.ap = 125f;
    tradNuminorukult.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradNuminorukult);

    SpecialAbility tradShinxir = new SpecialAbility();
    tradShinxir.name = "Tradition (Shinxirkult)";
    tradShinxir.key = SpecialAbilityKey.tradition_shinxirkult;
    tradShinxir.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradShinxir.ap = 165f;
    tradShinxir.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradShinxir);

    SpecialAbility tradAngrosch = new SpecialAbility();
    tradAngrosch.name = "Tradition (Angroschkirche)";
    tradAngrosch.key = SpecialAbilityKey.tradition_angroschkirche;
    tradAngrosch.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradAngrosch.ap = 125f;
    tradAngrosch.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradAngrosch);

    SpecialAbility tradZsahh = new SpecialAbility();
    tradZsahh.name = "Tradition (Zsahh Kult)";
    tradZsahh.key = SpecialAbilityKey.tradition_zsahh_kult;
    tradZsahh.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradZsahh.ap = 140f;
    tradZsahh.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradZsahh);

    SpecialAbility tradHSzint = new SpecialAbility();
    tradHSzint.name = "Tradition (H'Szint Kult)";
    tradHSzint.key = SpecialAbilityKey.tradition_h_szint_kult;
    tradHSzint.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradHSzint.ap = 130f;
    tradHSzint.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradHSzint);

    SpecialAbility tradChrSsirSsr = new SpecialAbility();
    tradChrSsirSsr.name = "Tradition (Chr'Ssir'Ssr-Kult)";
    tradChrSsirSsr.key = SpecialAbilityKey.tradition_chr_ssir_ssr_kult;
    tradChrSsirSsr.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradChrSsirSsr.ap = 130f;
    tradChrSsirSsr.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradChrSsirSsr);

    SpecialAbility tradTairach = new SpecialAbility();
    tradTairach.name = "Tradition (Tairachkult)";
    tradTairach.key = SpecialAbilityKey.tradition_tairachkult;
    tradTairach.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradTairach.ap = 135f;
    tradTairach.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradTairach);

    SpecialAbility tradGravesh = new SpecialAbility();
    tradGravesh.name = "Tradition (Graveshkult)";
    tradGravesh.key = SpecialAbilityKey.tradition_graveshkult;
    tradGravesh.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradGravesh.ap = 120f;
    tradGravesh.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradGravesh);

    SpecialAbility tradNamenlos = new SpecialAbility();
    tradNamenlos.name = "Tradition (Namenloser Kult)";
    tradNamenlos.key = SpecialAbilityKey.tradition_namenloser_kult;
    tradNamenlos.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradNamenlos.ap = 150f;
    tradNamenlos.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradNamenlos);

    SpecialAbility tradFerkina = new SpecialAbility();
    tradFerkina.name = "Tradition (Ferkinaschamane)";
    tradFerkina.key = SpecialAbilityKey.tradition_ferkinaschamanen;
    tradFerkina.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradFerkina.ap = 100f;
    tradFerkina.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradFerkina.requireOneOfCultures = List.of(CultureKey.ferkinas);
    returnValue.add(tradFerkina);

    SpecialAbility tradFjarninger = new SpecialAbility();
    tradFjarninger.name = "Tradition (Fjarningerschamane)";
    tradFjarninger.key = SpecialAbilityKey.tradition_fjarningerschamanen;
    tradFjarninger.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradFjarninger.ap = 100f;
    tradFjarninger.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradFjarninger.requireOneOfCultures = List.of(CultureKey.fjarninger);
    returnValue.add(tradFjarninger);

    SpecialAbility tradGjalsker = new SpecialAbility();
    tradGjalsker.name = "Tradition (Gjalskerschamane)";
    tradGjalsker.key = SpecialAbilityKey.tradition_gjalskerschamanen;
    tradGjalsker.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradGjalsker.ap = 100f;
    tradGjalsker.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradGjalsker.requireOneOfCultures = List.of(CultureKey.gjalsker);
    returnValue.add(tradGjalsker);

    SpecialAbility tradNivesen = new SpecialAbility();
    tradNivesen.name = "Tradition (Nivesenschamane)";
    tradNivesen.key = SpecialAbilityKey.tradition_nivesenschamanen;
    tradNivesen.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradNivesen.ap = 100f;
    tradNivesen.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradNivesen.requireOneOfCultures = List.of(CultureKey.nivesen);
    returnValue.add(tradNivesen);

    SpecialAbility tradTahaya = new SpecialAbility();
    tradTahaya.name = "Tradition (Tahayaschamanen)";
    tradTahaya.key = SpecialAbilityKey.tradition_tahayaschamanen;
    tradTahaya.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradTahaya.ap = 100f;
    tradTahaya.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradTahaya.requireOneOfCultures = List.of(CultureKey.waldmenschen, CultureKey.utulu);
    returnValue.add(tradTahaya);

    SpecialAbility tradTrollzacker = new SpecialAbility();
    tradTrollzacker.name = "Tradition (Trollzackerschamanen)";
    tradTrollzacker.key = SpecialAbilityKey.tradition_trollzackerschamanen;
    tradTrollzacker.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradTrollzacker.ap = 100f;
    tradTrollzacker.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradTrollzacker.requireOneOfCultures = List.of(CultureKey.trollzacker);
    returnValue.add(tradTrollzacker);

    SpecialAbility tradAchazschamanen = new SpecialAbility();
    tradAchazschamanen.name = "Tradition (Achazschamanen)";
    tradAchazschamanen.key = SpecialAbilityKey.tradition_achazschamanen;
    tradAchazschamanen.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradAchazschamanen.ap = 135f;
    tradAchazschamanen.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradAchazschamanen);


    return returnValue;
  }

  private static List<SpecialAbility> generateMagicTraditions() {
    List<SpecialAbility> returnValue = new ArrayList<>();

    SpecialAbility tradAnimisten = new SpecialAbility();
    tradAnimisten.name = "Tradition (Animisten)";
    tradAnimisten.key = SpecialAbilityKey.tradition_animisten;
    tradAnimisten.category = SpecialAbilityCategoryKey.tradition_magic;
    tradAnimisten.ap = 125f;
    tradAnimisten.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradAnimisten.requireOneOfCultures = List.of(CultureKey.ferkinas, CultureKey.fjarninger, CultureKey.gjalsker, CultureKey.nivesen, CultureKey.stammesorks, CultureKey.svellttal, CultureKey.waldmenschen, CultureKey.utulu, CultureKey.trollzacker);
    returnValue.add(tradAnimisten);

    SpecialAbility tradBannzeichner = new SpecialAbility();
    tradBannzeichner.name = "Tradition (Bannzeichner)";
    tradBannzeichner.key = SpecialAbilityKey.tradition_ysilische_bannzeichner;
    tradBannzeichner.category = SpecialAbilityCategoryKey.tradition_magic;
    tradBannzeichner.ap = 40f;
    tradBannzeichner.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradBannzeichner);

    SpecialAbility tradBorbaradianer = new SpecialAbility();
    tradBorbaradianer.name = "Tradition (Borbaradianer)";
    tradBorbaradianer.key = SpecialAbilityKey.tradition_borbaradianer;
    tradBorbaradianer.category = SpecialAbilityCategoryKey.tradition_magic;
    tradBorbaradianer.ap = 70f;
    returnValue.add(tradBorbaradianer);

    SpecialAbility tradDarna = new SpecialAbility();
    tradDarna.name = "Tradition (Darna)";
    tradDarna.key = SpecialAbilityKey.tradition_darna;
    tradDarna.category = SpecialAbilityCategoryKey.tradition_magic;
    tradDarna.ap = 110f;
    tradDarna.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradDarna.requireOneOfCultures = List.of(CultureKey.waldmenschen, CultureKey.utulu);
    returnValue.add(tradDarna);

    SpecialAbility tradDruiden = new SpecialAbility();
    tradDruiden.name = "Tradition (Druiden)";
    tradDruiden.key = SpecialAbilityKey.tradition_druiden;
    tradDruiden.category = SpecialAbilityCategoryKey.tradition_magic;
    tradDruiden.ap = 125f;
    tradDruiden.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradDruiden.requireNoneOfBoons = List.of(new RequirementBoon(BoonKey.eisenaffine_aura, Boolean.FALSE));
    returnValue.add(tradDruiden);

    SpecialAbility tradElfen = new SpecialAbility();
    tradElfen.name = "Tradition (Elfen)";
    tradElfen.key = SpecialAbilityKey.tradition_elfen;
    tradElfen.category = SpecialAbilityCategoryKey.tradition_magic;
    tradElfen.ap = 125f;
    tradElfen.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradElfen);

    SpecialAbility tradGeoden = new SpecialAbility();
    tradGeoden.name = "Tradition (Geoden)";
    tradGeoden.key = SpecialAbilityKey.tradition_geoden;
    tradGeoden.category = SpecialAbilityCategoryKey.tradition_magic;
    tradGeoden.ap = 130f;
    tradGeoden.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradGeoden.requireNoneOfBoons = List.of(new RequirementBoon(BoonKey.eisenaffine_aura, Boolean.FALSE));
    tradGeoden.requiredSpecie = SpecieKey.zwerg;
    returnValue.add(tradGeoden);

    SpecialAbility tradBrobimGeoden = new SpecialAbility();
    tradBrobimGeoden.name = "Tradition (Brobim-Geoden)";
    tradBrobimGeoden.key = SpecialAbilityKey.tradition_brobim_geoden;
    tradBrobimGeoden.category = SpecialAbilityCategoryKey.tradition_magic;
    tradBrobimGeoden.ap = 125f;
    tradBrobimGeoden.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradBrobimGeoden.requireNoneOfBoons = List.of(new RequirementBoon(BoonKey.eisenaffine_aura, Boolean.FALSE));
    tradBrobimGeoden.requireOneOfCultures = List.of(CultureKey.wildzwerge);
    tradBrobimGeoden.requiredSpecie = SpecieKey.zwerg;
    returnValue.add(tradBrobimGeoden);

    SpecialAbility tradGildenmagier = new SpecialAbility();
    tradGildenmagier.name = "Tradition (Gildenmagier)";
    tradGildenmagier.key = SpecialAbilityKey.tradition_gildenmagier;
    tradGildenmagier.category = SpecialAbilityCategoryKey.tradition_magic;
    tradGildenmagier.ap = 155f;
    tradGildenmagier.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradGildenmagier);

    SpecialAbility tradQabalyamagier = new SpecialAbility();
    tradQabalyamagier.name = "Tradition (Qabalyamagier)";
    tradQabalyamagier.key = SpecialAbilityKey.tradition_qabalyamagier;
    tradQabalyamagier.category = SpecialAbilityCategoryKey.tradition_magic;
    tradQabalyamagier.ap = 165f;
    tradQabalyamagier.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradQabalyamagier);

    SpecialAbility tradGoblinzauber = new SpecialAbility();
    tradGoblinzauber.name = "Tradition (Goblinzauber)";
    tradGoblinzauber.key = SpecialAbilityKey.tradition_goblinzauberinnen;
    tradGoblinzauber.category = SpecialAbilityCategoryKey.tradition_magic;
    tradGoblinzauber.ap = 100f;
    tradGoblinzauber.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradGoblinzauber.requireOneOfCultures = List.of(CultureKey.stammesgoblins);
    returnValue.add(tradGoblinzauber);

    SpecialAbility tradHexen = new SpecialAbility();
    tradHexen.name = "Tradition (Hexen)";
    tradHexen.key = SpecialAbilityKey.tradition_hexen;
    tradHexen.category = SpecialAbilityCategoryKey.tradition_magic;
    tradHexen.ap = 135f;
    tradHexen.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradHexen);

    SpecialAbility tradKristallomanten = new SpecialAbility();
    tradKristallomanten.name = "Tradition (Kristallomanten)";
    tradKristallomanten.key = SpecialAbilityKey.tradition_kristallomanten;
    tradKristallomanten.category = SpecialAbilityCategoryKey.tradition_magic;
    tradKristallomanten.ap = 115f;
    tradKristallomanten.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradKristallomanten.requiredSpecie = SpecieKey.achaz;
    returnValue.add(tradKristallomanten);

    SpecialAbility tradIntuitiverZauberer = new SpecialAbility();
    tradIntuitiverZauberer.name = "Tradition (Intuitive Zauberer)";
    tradIntuitiverZauberer.key = SpecialAbilityKey.tradition_intuitive_zauberer;
    tradIntuitiverZauberer.category = SpecialAbilityCategoryKey.tradition_magic;
    tradIntuitiverZauberer.ap = 50f;
    tradIntuitiverZauberer.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradIntuitiverZauberer);

    SpecialAbility tradMeistertalentierte = new SpecialAbility();
    tradMeistertalentierte.name = "Tradition (Meisterhandwerker)";
    tradMeistertalentierte.key = SpecialAbilityKey.tradition_meistertalentierte;
    tradMeistertalentierte.category = SpecialAbilityCategoryKey.tradition_magic;
    tradMeistertalentierte.ap = 35f;
    tradMeistertalentierte.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradMeistertalentierte);

    SpecialAbility tradRunenschöpfer = new SpecialAbility();
    tradRunenschöpfer.name = "Tradition (Runenschöpfer)";
    tradRunenschöpfer.key = SpecialAbilityKey.tradition_runenschöpfer;
    tradRunenschöpfer.category = SpecialAbilityCategoryKey.tradition_magic;
    tradRunenschöpfer.ap = 75f;
    tradRunenschöpfer.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradRunenschöpfer);

    SpecialAbility tradScharlatane = new SpecialAbility();
    tradScharlatane.name = "Tradition (Scharlatane)";
    tradScharlatane.key = SpecialAbilityKey.tradition_scharlatane;
    tradScharlatane.category = SpecialAbilityCategoryKey.tradition_magic;
    tradScharlatane.ap = 125f;
    tradScharlatane.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradScharlatane);

    SpecialAbility tradSchelme = new SpecialAbility();
    tradSchelme.name = "Tradition (Schelme)";
    tradSchelme.key = SpecialAbilityKey.tradition_schelme;
    tradSchelme.category = SpecialAbilityCategoryKey.tradition_magic;
    tradSchelme.ap = 125f;
    tradSchelme.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradSchelme.requireOneOfCultures = List.of(CultureKey.koboldweltler);
    returnValue.add(tradSchelme);

    SpecialAbility tradZauberalchimisten = new SpecialAbility();
    tradZauberalchimisten.name = "Tradition (Zauberalchimisten)";
    tradZauberalchimisten.key = SpecialAbilityKey.tradition_zauberalchimisten;
    tradZauberalchimisten.category = SpecialAbilityCategoryKey.tradition_magic;
    tradZauberalchimisten.ap = 45f;
    tradZauberalchimisten.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradZauberalchimisten);

    SpecialAbility tradZauberbarden = new SpecialAbility();
    tradZauberbarden.name = "Tradition (Zauberbarden)";
    tradZauberbarden.key = SpecialAbilityKey.tradition_zauberbarden;
    tradZauberbarden.category = SpecialAbilityCategoryKey.tradition_magic;
    tradZauberbarden.ap = 80f;
    tradZauberbarden.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradSchelme.requireOneOfCultures = List.of(CultureKey.mittelreich, CultureKey.novadi, CultureKey.thorwal, CultureKey.zyklopeninseln);
    returnValue.add(tradZauberbarden);

    SpecialAbility tradZaubertänzer = new SpecialAbility();
    tradZaubertänzer.name = "Tradition (Zaubertänzer)";
    tradZaubertänzer.key = SpecialAbilityKey.tradition_zaubertänzer;
    tradZaubertänzer.category = SpecialAbilityCategoryKey.tradition_magic;
    tradZaubertänzer.ap = 75f;
    tradZaubertänzer.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradSchelme.requireOneOfCultures = List.of(CultureKey.zahori, CultureKey.aranien, CultureKey.novadi, CultureKey.mhanadistan);
    returnValue.add(tradZaubertänzer);

    SpecialAbility tradZibilja = new SpecialAbility();
    tradZibilja.name = "Tradition (Zibilja)";
    tradZibilja.key = SpecialAbilityKey.tradition_zibilja;
    tradZibilja.category = SpecialAbilityCategoryKey.tradition_magic;
    tradZibilja.ap = 100f;
    tradZibilja.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradSchelme.requireOneOfCultures = List.of(CultureKey.norbarden);
    returnValue.add(tradZibilja);

    return returnValue;
  }

}

/**
 * LEGACY:
 * <p>
 * private static void summarizeTextCsv(String path, String file)
 * {
 * try (Stream<Path> paths = Files.walk(Paths.get(path)))
 * {
 * List<TextWithMetaInfo> concatList = new ArrayList<>();
 * paths.filter(Files::isRegularFile)
 * .filter(p -> !p.endsWith(file))
 * .map(p -> CsvHandler.readBeanFromPath(TextWithMetaInfo.class, p))
 * .forEach(l -> concatList.addAll(l));
 * File fOut = new File(path, file);
 * CsvHandler.writeBeanToUrl(fOut, concatList);
 * }
 * catch (IOException e)
 * {
 * LOGGER.error(e.getMessage(), e);
 * }
 * }
 * <p>
 * private static void summarizeMsCsv(String path, String file)
 * {
 * try (Stream<Path> paths = Files.walk(Paths.get(path)))
 * {
 * List<MysticalSkillRaw> concatList = new ArrayList<>();
 * paths.filter(Files::isRegularFile)
 * .filter(p -> !p.endsWith(file))
 * .map(p -> CsvHandler.readBeanFromPath(MysticalSkillRaw.class, p))
 * .forEach(l -> concatList.addAll(l));
 * File fOut = new File(path, file);
 * CsvHandler.writeBeanToUrl(fOut, concatList);
 * }
 * catch (IOException e)
 * {
 * LOGGER.error(e.getMessage(), e);
 * }
 * }
 * <p>
 * private static void summarizeMsJson(String path, String file)
 * {
 * try (Stream<Path> paths = Files.walk(Paths.get(path)))
 * {
 * List<MysticalSkill> mysticalSkills = new ArrayList<>();
 * paths.filter(Files::isRegularFile)
 * .filter(p -> !p.endsWith(file))
 * .map(p -> {
 * ObjectMapper objectMapper = initObjectMapper();
 * String json = readFromInputStream(new File(p.toString()));
 * try
 * {
 * return objectMapper.readValue(json, new TypeReference<List<MysticalSkill>>()
 * {
 * });
 * }
 * catch (JsonProcessingException e)
 * {
 * LOGGER.error("JSON processing Error in %s", path, e);
 * }
 * return new ArrayList<MysticalSkill>();
 * })
 * .forEach(l -> mysticalSkills.addAll(l));
 * ObjectMapper mapper = initObjectMapper();
 * String jsonResult = mapper
 * .writerWithDefaultPrettyPrinter()
 * .writeValueAsString(mysticalSkills);
 * <p>
 * try (BufferedWriter writer = generateBufferedWriter(path + file))
 * {
 * writer.write(jsonResult);
 * writer.flush();
 * }
 * }
 * catch (IOException e)
 * {
 * LOGGER.error(e.getMessage(), e);
 * }
 * }
 */

