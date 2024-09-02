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
import de.pho.dsapdfreader.dsaconverter.DsaConverterBoon;
import de.pho.dsapdfreader.dsaconverter.DsaConverterClericalObjectRituals;
import de.pho.dsapdfreader.dsaconverter.DsaConverterCurriculum;
import de.pho.dsapdfreader.dsaconverter.DsaConverterEquipment;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMsyticalSkillCommonness;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMsyticalSkillElements;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMsyticalSkillIncantations;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkill;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillActivityAndArtifacts;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillActivity_LEGACY;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillGrimorium;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillGrimoriumTricks;
import de.pho.dsapdfreader.dsaconverter.DsaConverterProfession;
import de.pho.dsapdfreader.dsaconverter.DsaConverterProfile;
import de.pho.dsapdfreader.dsaconverter.DsaConverterSkillKodex;
import de.pho.dsapdfreader.dsaconverter.DsaConverterSpecialAbility;
import de.pho.dsapdfreader.dsaconverter.DsaConverterSpecialAbilityClericBase;
import de.pho.dsapdfreader.dsaconverter.DsaConverterSpecialAbilityKodex;
import de.pho.dsapdfreader.dsaconverter.DsaConverterTraditionsToSpecialAbility;
import de.pho.dsapdfreader.dsaconverter.DsaConverterWeapon;
import de.pho.dsapdfreader.dsaconverter.DsaConverterWeaponAvailability;
import de.pho.dsapdfreader.dsaconverter.DsaConverterWeaponLists;
import de.pho.dsapdfreader.dsaconverter.model.ArmorRaw;
import de.pho.dsapdfreader.dsaconverter.model.BoonRaw;
import de.pho.dsapdfreader.dsaconverter.model.CurriculumRaw;
import de.pho.dsapdfreader.dsaconverter.model.EquipmentRaw;
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
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorBoonKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorObjectRitual;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSpecialAbility;
import de.pho.dsapdfreader.exporter.LoadToArmor;
import de.pho.dsapdfreader.exporter.LoadToBoon;
import de.pho.dsapdfreader.exporter.LoadToEquipment;
import de.pho.dsapdfreader.exporter.LoadToMeleeWeapon;
import de.pho.dsapdfreader.exporter.LoadToMysticalSkill;
import de.pho.dsapdfreader.exporter.LoadToObjectRitual;
import de.pho.dsapdfreader.exporter.LoadToProfession;
import de.pho.dsapdfreader.exporter.LoadToRangedWeapon;
import de.pho.dsapdfreader.exporter.LoadToSkill;
import de.pho.dsapdfreader.exporter.LoadToSpecialAbility;
import de.pho.dsapdfreader.exporter.LoadToTraditionAbility;
import de.pho.dsapdfreader.exporter.model.Armor;
import de.pho.dsapdfreader.exporter.model.AvailabilityWeapon;
import de.pho.dsapdfreader.exporter.model.Boon;
import de.pho.dsapdfreader.exporter.model.Equipment;
import de.pho.dsapdfreader.exporter.model.EquipmentI;
import de.pho.dsapdfreader.exporter.model.MeleeWeapon;
import de.pho.dsapdfreader.exporter.model.MysticalSkill;
import de.pho.dsapdfreader.exporter.model.ObjectRitual;
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

  private static final String STRATEGY_PACKAGE = DsaConverterStrategy.class.getPackageName() + ".";
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
  private static final String SEPARATOR = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
  private static final Logger LOGGER = LogManager.getLogger();
  private static List<TopicConfiguration> configs = null;

  static final CombatSkillKey[] COMBAT_SKILL_KEYS_RANGED = {CombatSkillKey.blasrohre, CombatSkillKey.bögen, CombatSkillKey.armbrüste, CombatSkillKey.diskusse, CombatSkillKey.schleudern, CombatSkillKey.wurfwaffen};
  static final Map<String, String> mapProfession2CurriculumRaw;

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
    LOGGER.info("start");
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

    isToText = false;
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
        .filter(conf -> conf != null && conf.active)
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
          case BLESSINGS, TRICKS, SPELLS, LITURGIES, RITUALS, CEREMONIES, SPELLS_GRIMORIUM, RITUALS_GRIMORIUM, TRICKS_GRIMORIUM, BLESSING_DIVINARIUM, LITURGY_DIVINARIUM, CEREMONY_DIVINARIUM, CURSES, ELFENSONGS, MELODIES, DANCES -> {
            List<MysticalSkillRaw> mysticalSkillRawList = CsvHandler.readBeanFromFile(MysticalSkillRaw.class, new File(generateFileName(FILE_STRATEGY_2_RAW, conf)));
            if (allByTypeMap.containsKey(MysticalSkill.TYPE)) allByTypeMap.get(MysticalSkill.TYPE).addAll(mysticalSkillRawList);
            else allByTypeMap.put(MysticalSkill.TYPE, mysticalSkillRawList);
          }
          case INCANTATIONS_RIME_SPELLS_GRIMORIUM, INCANTATIONS_RIME_RITUALS_GRIMORIUM, INCANTATIONS_ZHAYAD_SPELLS_GRIMORIUM, INCANTATIONS_ZHAYAD_RITUALS_GRIMORIUM, ELEMENTS_SPELLS_GRIMORIUM, ELEMENTS_RITUALS_GRIMORIUM -> {
            break;
          }
          case WAFFENLISTE, WAFFEN -> {

          }
          case RÜSTUNGENLISTE, RÜSTUNGEN -> {
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
    case BLESSINGS, TRICKS, SPELLS, LITURGIES, RITUALS, CEREMONIES, SPELLS_GRIMORIUM, RITUALS_GRIMORIUM, TRICKS_GRIMORIUM, BLESSING_DIVINARIUM, LITURGY_DIVINARIUM, CEREMONY_DIVINARIUM -> {
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

        if (conf.topic == TopicEnum.RITUALS
            || conf.topic == TopicEnum.RITUALS_GRIMORIUM
            || conf.topic == TopicEnum.SPELLS
            || conf.topic == TopicEnum.SPELLS_GRIMORIUM
            || conf.topic == TopicEnum.CEREMONY_DIVINARIUM
            || conf.topic == TopicEnum.LITURGY_DIVINARIUM
            || conf.topic == TopicEnum.TRICKS
            || conf.topic == TopicEnum.TRICKS_GRIMORIUM
            || conf.topic == TopicEnum.BLESSINGS
            || conf.topic == TopicEnum.BLESSING_DIVINARIUM
        ) {

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
        extractAdditionalApplications(raws, conf);

        List<SpecialAbility> corrections = initExporterCorrections(SpecialAbility.class);
        List<SpecialAbility> specialAbilities = raws.stream().flatMap(LoadToSpecialAbility::migrate)
            .map(sa -> {
              LoadToSpecialAbility.applyCorrections(sa, corrections);
              return sa;
            }).collect(Collectors.toList());

        specialAbilities.addAll(generateTraditionsByPublication(conf.publication));

        ObjectMapper mapper = initObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(specialAbilities);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "abilities"));
        writer.write(jsonResult);
        writer.close();

        if (conf.topic == TopicEnum.ABILITIES) {

          Triplet<StringBuilder, StringBuilder, StringBuilder> t = generateSaStringBuilders(raws);
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
        }

      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    case INCANTATIONS_RIME_SPELLS_GRIMORIUM, INCANTATIONS_RIME_RITUALS_GRIMORIUM, INCANTATIONS_ZHAYAD_SPELLS_GRIMORIUM, INCANTATIONS_ZHAYAD_RITUALS_GRIMORIUM, ELEMENTS_SPELLS_GRIMORIUM, ELEMENTS_RITUALS_GRIMORIUM -> {
      break;
    }
    case BOONS -> {
      try {

        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<BoonRaw> raws = CsvHandler.readBeanFromFile(BoonRaw.class, fIn);

        List<Boon> corrections = initExporterCorrections(Boon.class);
        List<Boon> boons = raws.stream().map(LoadToBoon::migrate)
            .map(b -> {
              LoadToBoon.applyCorrections(b, corrections);
              return b;
            })
            .collect(Collectors.toList());

        boons.addAll(corrections.stream().filter(c -> boons.stream().noneMatch(b -> b.key == c.key)).collect(Collectors.toList()));
        List<Boon> finalList = boons.stream().filter(b -> b.name != null).collect(Collectors.toList());

        ObjectMapper mapper = initObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(finalList);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, "boons"));
        writer.write(jsonResult);
        writer.close();


        Quartet<StringBuilder, StringBuilder, StringBuilder, StringBuilder> t = generateBoonStringBuilders(raws);
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
          writer.write(generateNameString(equipmentList.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_remark"));
          writer.write(generateRemarkString(equipmentList.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
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
        if (pures.size() > 0) {
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
    case RÜSTUNGENLISTE, RÜSTUNGEN -> {
      try {
        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<ArmorRaw> raws = CsvHandler.readBeanFromFile(ArmorRaw.class, fIn);

        List<Armor> pures = raws.stream().map(LoadToArmor::migrate).collect(Collectors.toList());

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
          writer.write(generateNameString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_advantage"));
          writer.write(generateAdvantageString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_disadvantage"));
          writer.write(generateDisadvantageString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
          writer.close();

          writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_remark"));
          writer.write(generateRemarkString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
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

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, "object_rituals", "object_rituals"));
        writer.write(jsonResultOrs);
        writer.close();

        Pair<StringBuilder, StringBuilder> pOr = generateOrStringBuilders(raws.stream().filter(r -> r.artifactKey != null).collect(Collectors.toList()));


        String prefix = "object_rituals";
        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_name"));
        writer.write(pOr.getValue0().toString());
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, conf.fileAffix, prefix + "_rules"));
        writer.write(pOr.getValue1().toString());
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

        fIn = new File("C:\\develop\\project\\dsa-pdf-reader\\export\\03 - raw\\Kodex_der_Magie_CURRICULUM_raw.csv");
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
            .map(sa -> {
              LoadToSpecialAbility.applyCorrections(sa, corrections);
              return sa;
            })
            .collect(Collectors.toList());


        ObjectMapper mapperOrs = initObjectMapper();
        String jsonResultOrs = mapperOrs
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(pureSas);

        BufferedWriter writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, conf.publication, "", "special_abilities"));
        writer.write(jsonResultOrs);
        writer.close();

      }
      catch (IOException e) {
        LOGGER.error(e);
      }
    }
    case CURRICULUM -> LOGGER.debug("Nothing to do, used in PROFESSION extraction");
    default -> LOGGER.error(String.format("Unexpected value (parseToJson): %s", conf.topic));
    }
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
    catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
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

  private static void extractAdditionalApplications(List<SpecialAbilityRaw> raws, TopicConfiguration conf) throws IOException {
    List<SkillApplication> result = raws.stream()
        .map(raw -> ExtractorSpecialAbility.retrieveSkillApplication(raw.rules, raw.name))
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
        .map(raw -> ExtractorSpecialAbility.retrieveSkillUsage(raw.rules))
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
        writer.write(generateNameString(meleeWeapons.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_advantage"));
        writer.write(generateAdvantageString(meleeWeapons.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_disadvantage"));
        writer.write(generateDisadvantageString(meleeWeapons.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_remark"));
        writer.write(generateRemarkString(meleeWeapons.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
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
        writer.write(generateNameString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_advantage"));
        writer.write(generateAdvantageString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_disadvantage"));
        writer.write(generateDisadvantageString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
        writer.close();

        writer = generateBufferedWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf.topic, pubPlusWeaponType, conf.fileAffix, prefix + "_remark"));
        writer.write(generateRemarkString(pures.stream().map(mw -> (EquipmentI) mw).collect(Collectors.toList())));
        writer.close();
      }
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String generateRemarkString(List<EquipmentI> es) {
    StringBuilder returnValue = new StringBuilder();
    es.stream().forEach(e -> {
      returnValue.append(
          e.getKeyValue()
              + " {"
              + e.getRemark()
              + "} "
              + "\r\n");
      if (e.getName() == null || e.getName().isEmpty()) {
        LOGGER.error("Equipment (" + e.getName() + ") has no advantage!");
      }
    });
    return returnValue.toString();
  }

  private static String generateAdvantageString(List<EquipmentI> es) {
    StringBuilder returnValue = new StringBuilder();
    es.stream().forEach(e -> {
      returnValue.append(
          e.getKeyValue()
              + " {"
              + e.getAdvantage()
              + "} "
              + "\r\n");
      if (e.getName() == null || e.getName().isEmpty()) {
        LOGGER.error("Equipment (" + e.getName() + ") has no advantage!");
      }
    });
    return returnValue.toString();
  }

  private static String generateDisadvantageString(List<EquipmentI> es) {
    StringBuilder returnValue = new StringBuilder();
    es.stream().forEach(e -> {
      returnValue.append(
          e.getKeyValue()
              + " {"
              + e.getDisadvantage()
              + "} "
              + "\r\n");
      if (e.getName() == null || e.getName().isEmpty()) {
        LOGGER.error("Equipment (" + e.getName() + ") has no disadvantage!");
      }
    });
    return returnValue.toString();
  }

  private static String generateMsEffectString(List<MysticalSkillRaw> rawMysticalSkills) {
    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream().forEach(msr -> {
      returnValue.append(ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr.publication, msr.name, Extractor.retrieveMsCategory(msr.topic)).toValue() + " {" + msr.effect + "}\r\n");
    });
    return returnValue.toString();

  }


  private static String generateMsVariantNameString(List<MysticalSkillRaw> rawMysticalSkills) {

    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream()
        .forEach(msr -> {
          if (msr.variant1 != null && msr.variant1.key != null) returnValue.append(msr.variant1.key.ordinal() + " {" + msr.variant1.name + "}\r\n");
          if (msr.variant2 != null && msr.variant2.key != null) returnValue.append(msr.variant2.key.ordinal() + " {" + msr.variant2.name + "}\r\n");
          if (msr.variant3 != null && msr.variant3.key != null) returnValue.append(msr.variant3.key.ordinal() + " {" + msr.variant3.name + "}\r\n");
          if (msr.variant4 != null && msr.variant4.key != null) returnValue.append(msr.variant4.key.ordinal() + " {" + msr.variant4.name + "}\r\n");
          if (msr.variant5 != null && msr.variant5.key != null) returnValue.append(msr.variant5.key.ordinal() + " {" + msr.variant5.name + "}\r\n");
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


  private static Triplet<StringBuilder, StringBuilder, StringBuilder> generateSaStringBuilders(List<SpecialAbilityRaw> rawMysticalSkills) {
    Triplet<StringBuilder, StringBuilder, StringBuilder> returnValue = new Triplet<>(new StringBuilder(), new StringBuilder(), new StringBuilder());
    rawMysticalSkills.stream().forEach(raw -> {

      int levels = LoadToSpecialAbility.extractLevels(raw.name);
      String baseName = levels > 1
          ? raw.name.split("(?= (I-|I\\/))")[0]
          : raw.name;

      boolean ignoreBrackets = Arrays.stream(LoadToSpecialAbility.BRACKETED_NAMES).filter(bn -> baseName.startsWith(bn)).count() == 0;
      for (int currentLevel = 0; currentLevel < levels; currentLevel++) {
        String name = LoadToSpecialAbility.extractName(baseName, levels, currentLevel, ignoreBrackets);
        SpecialAbilityKey key = ExtractorSpecialAbility.retrieve(name);

        boolean isAuthor = name.equals("Schriftstellerei");
        boolean isHealingSpec = name.equals("Heilungsspezialgebiet");
        boolean isGebieterDesAspekts = raw.name.equals("Gebieter des (Aspekts)");

        List<Pair<SpecialAbilityKey, String>> keyNames = new ArrayList<>();
        if (isAuthor) {
          keyNames.addAll(
              LoadToSpecialAbility.generateScribeList(new SpecialAbility()).stream()
                  .map(sa -> new Pair<>(sa.key, sa.name))
                  .collect(Collectors.toList()));
        }
        else if (isHealingSpec) {
          keyNames.addAll(
              LoadToSpecialAbility.generateHealingSpecList(new SpecialAbility()).stream()
                  .map(sa -> new Pair<>(sa.key, sa.name))
                  .collect(Collectors.toList()));
        }
        else if (isGebieterDesAspekts) {
          keyNames.addAll(
              LoadToSpecialAbility.generateGebieterDesAspektsList(new SpecialAbility(), "").stream()
                  .map(sa -> new Pair<>(sa.key, sa.name))
                  .collect(Collectors.toList()));
        }
        else {
          keyNames.add(new Pair<>(key, name));
        }

        keyNames.forEach(p -> {
          if (p.getValue0() != null) {
            returnValue.getValue0().append(p.getValue0().toValue() + " {" + p.getValue1() + "}\r\n");
            returnValue.getValue1().append(p.getValue0().toValue() + " {" + raw.rules + "} \r\n");
            returnValue.getValue2().append(p.getValue0().toValue() + " {" + raw.description + "} \r\n");
          }
          else {
            LOGGER.error("Special Ability (" + p.getValue1() + ") has no key!");
          }
        });
      }

    });
    return returnValue;
  }

  private static Pair<StringBuilder, StringBuilder> generateOrStringBuilders(List<MysticalActivityObjectRitualRaw> raws) {
    Pair<StringBuilder, StringBuilder> returnValue = new Pair<>(new StringBuilder(), new StringBuilder());
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
            }
            else {
              LOGGER.error("ObjectRitual (" + raw.name + ") has no key!");
            }
          }

        });
    return returnValue;
  }

  private static Quartet<StringBuilder, StringBuilder, StringBuilder, StringBuilder> generateBoonStringBuilders(List<BoonRaw> rawBoon) {
    Quartet<StringBuilder, StringBuilder, StringBuilder, StringBuilder> returnValue = new Quartet<>(new StringBuilder(), new StringBuilder(), new StringBuilder(), new StringBuilder());
    rawBoon.stream().forEach(raw -> {
      int levels = LoadToSpecialAbility.extractLevels(raw.name);
      String name = (levels > 1
          ? raw.name.split("(?= (I-|I\\/))")[0]
          : raw.name)
          .replace("Ahnenblut-Vorteile", "")
          .replace("Feenblut-Vorteile", "");
      BoonKey key = ExtractorBoonKey.retrieve(name);

      Pair<BoonKey, String> p = new Pair<>(key, name);

      if (p.getValue0() != null) {
        returnValue.getValue0().append(p.getValue0().toValue() + " {" + p.getValue1() + "}\r\n");
        returnValue.getValue1().append(p.getValue0().toValue() + " {" + raw.rules + "} \r\n");
        returnValue.getValue2().append(p.getValue0().toValue() + " {" + raw.description + "} \r\n");
        returnValue.getValue3().append(p.getValue0().toValue() + " {" + raw.preconditions + "} \r\n");
      }
      else {
        LOGGER.error("Boon (" + p.getValue1() + ") has no key!");
      }


    });
    return returnValue;
  }

  private static String generateMsName(List<MysticalSkill> rawMysticalSkills) {
    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream().forEach(msr -> {
      returnValue.append(
          ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr.publication.name(), msr.name, msr.category).toValue()
              + " {"
              + msr.name
              + "} "
              + "\r\n");
      if (msr.name == null || msr.name.isEmpty()) {
        LOGGER.error("Mystical Skill (" + msr.name + ") has no name!");
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
      if (msr.description == null || msr.description.isEmpty()) {
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

  private static String generateNameString(List<EquipmentI> euqipments) {
    StringBuilder returnValue = new StringBuilder();
    euqipments.stream().forEach(e -> {
      returnValue.append(
          e.getKeyValue()
              + " {"
              + e.getName()
              + "} "
              + "\r\n");
      if (e.getName() == null || e.getName().isEmpty()) {
        LOGGER.error("Equipment (" + e.getName() + ") has no name!");
      }
    });
    return returnValue.toString();
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
    File fIn = new File(path + "/" + conf.pdfName);
    returnValue.addAll(PdfReader.convertToText(fIn, conf));
    return returnValue;

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
    case BOONS, MERITS, FLAWS -> results = new DsaConverterBoon().convertTextWithMetaInfo(texts, conf);
    case SPECIAL_ABILITY_MUNDANE, SPECIAL_ABILITY_FATE, SPECIAL_ABILITY_COMBAT, SPECIAL_ABILITY_MAGIC, SPECIAL_ABILITY_CLERIC, SPECIAL_ABILITY_SERMONS, SPECIAL_ABILITY_VISIONS ->
        results = new DsaConverterSpecialAbility().convertTextWithMetaInfo(texts, conf);
    case SPECIAL_ABILITY_CLERIC_BASE -> results = new DsaConverterSpecialAbilityClericBase().convertTextWithMetaInfo(texts, conf);
    case BLESSINGS, TRICKS, SPELLS, LITURGIES, RITUALS, CEREMONIES -> results = new DsaConverterMysticalSkill().convertTextWithMetaInfo(texts, conf);
    case SPELLS_GRIMORIUM, RITUALS_GRIMORIUM, LITURGY_DIVINARIUM, CEREMONY_DIVINARIUM ->
        results = new DsaConverterMysticalSkillGrimorium().convertTextWithMetaInfo(texts, conf);
    case TRICKS_GRIMORIUM, BLESSING_DIVINARIUM -> results = new DsaConverterMysticalSkillGrimoriumTricks().convertTextWithMetaInfo(texts, conf);
    case CURSES, ELFENSONGS, MELODIES, DANCES, ANIMIST ->
        results = new DsaConverterMysticalSkillActivity_LEGACY().convertTextWithMetaInfo(texts, conf);
    case TRADITIONS -> results = new DsaConverterTraditionsToSpecialAbility().convertTextWithMetaInfo(texts, conf);
    case INCANTATIONS_RIME_SPELLS_GRIMORIUM, INCANTATIONS_ZHAYAD_SPELLS_GRIMORIUM -> {
      Map<String, String> incantations = new DsaConverterMsyticalSkillIncantations().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(incantations, TopicEnum.SPELLS, conf.topic, conf.publication);
    }
    case INCANTATIONS_RIME_RITUALS_GRIMORIUM, INCANTATIONS_ZHAYAD_RITUALS_GRIMORIUM -> {
      Map<String, String> incantations = new DsaConverterMsyticalSkillIncantations().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(incantations, TopicEnum.RITUALS, conf.topic, conf.publication);
    }
    case ELEMENTS_SPELLS_GRIMORIUM -> {
      Map<String, String> spellsToElements = new DsaConverterMsyticalSkillElements().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(spellsToElements, TopicEnum.SPELLS, conf.topic, conf.publication);
    }
    case ELEMENTS_RITUALS_GRIMORIUM -> {
      Map<String, String> spellsToElements = new DsaConverterMsyticalSkillElements().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(spellsToElements, TopicEnum.RITUALS, conf.topic, conf.publication);
    }
    case MYSTICAL_SKILL_VARIANTS -> {
      List<MysticalSkillRaw> msrs = new DsaConverterMysticalSkill().convertTextWithMetaInfo(texts, conf);
      applyToBasis(msrs, conf);
      results = msrs;
    }
    case MYSTICAL_SKILL_COMMONNESS -> {
      Map<String, List<String>> commonness = new DsaConverterMsyticalSkillCommonness().convertTextWithMetaInfo(texts);
      applyToMysticalSkills(commonness);
    }
    case PROFESSIONS -> results = new DsaConverterProfession().convertTextWithMetaInfo(texts, conf);
    case WAFFEN -> results = new DsaConverterWeapon().convertTextWithMetaInfo(texts, conf);
    case WAFFENLISTE -> results = new DsaConverterWeaponLists().convertTextWithMetaInfo(texts, conf);
    case RÜSTUNGEN -> results = new DsaConverterArmor().convertTextWithMetaInfo(texts, conf);
    case RÜSTUNGENLISTE -> results = new DsaConverterArmorLists().convertTextWithMetaInfo(texts, conf);
    case VERBREITUNG_WAFFEN -> results = new DsaConverterWeaponAvailability().convertTextWithMetaInfo(texts, conf);
    case VERBREITUNG_RÜSTUNG -> results = new DsaConverterArmorAvailability().convertTextWithMetaInfo(texts, conf);
    case AUSRÜSTUNG -> results = new DsaConverterEquipment().convertTextWithMetaInfo(texts, conf);
    case ABILITIES -> results = new DsaConverterSpecialAbilityKodex().convertTextWithMetaInfo(texts, conf);
    case SKILLS -> results = new DsaConverterSkillKodex().convertTextWithMetaInfo(texts, conf);
    case MYSTICAL_SKILL_ACTIVITIES_MAGIC -> results = new DsaConverterMysticalSkillActivityAndArtifacts().convertTextWithMetaInfo(texts, conf);
    case CLERICAL_OBJECT_RITUALS -> results = new DsaConverterClericalObjectRituals().convertTextWithMetaInfo(texts, conf);
    case PROFILE -> results = new DsaConverterProfile().convertTextWithMetaInfo(texts, conf);
    case CURRICULUM -> results = new DsaConverterCurriculum().convertTextWithMetaInfo(texts, conf);
    default -> LOGGER.error(String.format("Unexpected value (parseResult): %s", conf.topic));
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

  private static void applyToBasis(List<MysticalSkillRaw> variants, TopicConfiguration conf) {
    String msg = String.format("Apply to BASIS: %s (%s)", conf.publication, conf.topic);
    LOGGER.info(msg);
    if (conf.publication.equals("Magie_1")) {
      applyVariantsToBasisMysticalSkills(variants, TopicEnum.SPELLS);
      applyVariantsToBasisMysticalSkills(variants, TopicEnum.RITUALS);
    }
    if (conf.publication.equals("Götter_1")) {
      applyVariantsToBasisMysticalSkills(variants, TopicEnum.LITURGIES);
      applyVariantsToBasisMysticalSkills(variants, TopicEnum.CEREMONIES);
    }
  }

  private static void applyVariantsToBasisMysticalSkills(List<MysticalSkillRaw> variants, TopicEnum topic) {
    File fIn = new File(String.format(FILE_STRATEGY_2_RAW, Publication.Basis + "_" + topic));
    List<MysticalSkillRaw> spells = CsvHandler.readBeanFromFile(MysticalSkillRaw.class, fIn);
    List<MysticalSkillRaw> results = spells.stream().map(msr -> {
      Optional<MysticalSkillRaw> applicableVariantMsr = variants.stream().filter(v -> v.name.equals(msr.name)).findFirst();
      if (applicableVariantMsr.isPresent()) {
        msr.variantsText = applicableVariantMsr.get().variantsText;
        msr.variant1 = applicableVariantMsr.get().variant1;
        msr.variant2 = applicableVariantMsr.get().variant2;
        msr.variant3 = applicableVariantMsr.get().variant3;
        msr.variant4 = applicableVariantMsr.get().variant4;
        msr.variant5 = applicableVariantMsr.get().variant5;
      }
      else {
        String msg = String.format("Für das Topic (%s)  (%s) wurden keine Varianten gefunden.", topic, msr.name);
        LOGGER.error(msg);
      }
      return msr;
    }).collect(Collectors.toList());

    CsvHandler.writeBeanToUrl(fIn, results);
  }

  private static List<SpecialAbility> generateClericalTraditions() {
    List<SpecialAbility> returnValue = new ArrayList<>();

    SpecialAbility tradPraios = new SpecialAbility();
    tradPraios.name = "Tradition (Praioskirche)";
    tradPraios.key = SpecialAbilityKey.tradition_praioskirche;
    tradPraios.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradPraios.ap = 130;
    tradPraios.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradPraios);

    SpecialAbility tradRondra = new SpecialAbility();
    tradRondra.name = "Tradition (Rondrakirche)";
    tradRondra.key = SpecialAbilityKey.tradition_rondrakirche;
    tradRondra.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradRondra.ap = 150;
    tradRondra.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradRondra);


    SpecialAbility tradEfferd = new SpecialAbility();
    tradEfferd.name = "Tradition (Efferdkirche)";
    tradEfferd.key = SpecialAbilityKey.tradition_efferdkirche;
    tradEfferd.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradEfferd.ap = 130;
    tradEfferd.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradEfferd);

    SpecialAbility tradTravia = new SpecialAbility();
    tradTravia.name = "Tradition (Traviakirche)";
    tradTravia.key = SpecialAbilityKey.tradition_traviakirche;
    tradTravia.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradTravia.ap = 110;
    tradTravia.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradTravia);

    SpecialAbility tradBoron = new SpecialAbility();
    tradBoron.name = "Tradition (Boronkirche)";
    tradBoron.key = SpecialAbilityKey.tradition_boronkirche;
    tradBoron.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradBoron.ap = 130;
    tradBoron.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradBoron);

    SpecialAbility tradHesinde = new SpecialAbility();
    tradHesinde.name = "Tradition (Hesindekirche)";
    tradHesinde.key = SpecialAbilityKey.tradition_hesindekirche;
    tradHesinde.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradHesinde.ap = 130;
    tradHesinde.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradHesinde);

    SpecialAbility tradFirun = new SpecialAbility();
    tradFirun.name = "Tradition (Firunkirche)";
    tradFirun.key = SpecialAbilityKey.tradition_firunkirche;
    tradFirun.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradFirun.ap = 140;
    tradFirun.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradFirun);

    SpecialAbility tradTsa = new SpecialAbility();
    tradTsa.name = "Tradition (Tsakirche)";
    tradTsa.key = SpecialAbilityKey.tradition_tsakirche;
    tradTsa.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradTsa.ap = 140;
    tradTsa.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradTsa);

    SpecialAbility tradPhex = new SpecialAbility();
    tradPhex.name = "Tradition (Phexkirche)";
    tradPhex.key = SpecialAbilityKey.tradition_phexkirche;
    tradPhex.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradPhex.ap = 150;
    tradPhex.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradPhex);

    SpecialAbility tradPeraine = new SpecialAbility();
    tradPeraine.name = "Tradition (Perainekirche)";
    tradPeraine.key = SpecialAbilityKey.tradition_perainekirche;
    tradPeraine.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradPeraine.ap = 110;
    tradPeraine.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradPeraine);

    SpecialAbility tradIngerimm = new SpecialAbility();
    tradIngerimm.name = "Tradition (Ingerimmkirche)";
    tradIngerimm.key = SpecialAbilityKey.tradition_ingerimmkirche;
    tradIngerimm.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradIngerimm.ap = 125;
    tradIngerimm.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradIngerimm);

    SpecialAbility tradRahja = new SpecialAbility();
    tradRahja.name = "Tradition (Rahjakirche)";
    tradRahja.key = SpecialAbilityKey.tradition_rahjakirche;
    tradRahja.category = SpecialAbilityCategoryKey.tradition_alveran_major;
    tradRahja.ap = 125;
    tradRahja.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradRahja);

    SpecialAbility tradAves = new SpecialAbility();
    tradAves.name = "Tradition (Aveskirche)";
    tradAves.key = SpecialAbilityKey.tradition_aveskirche;
    tradAves.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradAves.ap = 110;
    tradAves.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradAves);

    SpecialAbility tradIfirn = new SpecialAbility();
    tradIfirn.name = "Tradition (Ifirnkirche)";
    tradIfirn.key = SpecialAbilityKey.tradition_ifirnkirche;
    tradIfirn.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradIfirn.ap = 105;
    tradIfirn.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradIfirn);

    SpecialAbility tradKor = new SpecialAbility();
    tradKor.name = "Tradition (Korkirche)";
    tradKor.key = SpecialAbilityKey.tradition_korkirche;
    tradKor.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradKor.ap = 130;
    tradKor.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradKor);

    SpecialAbility tradNandus = new SpecialAbility();
    tradNandus.name = "Tradition (Nanduskirche)";
    tradNandus.key = SpecialAbilityKey.tradition_nanduskirche;
    tradNandus.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradNandus.ap = 130;
    tradNandus.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradNandus);

    SpecialAbility tradSwafnir = new SpecialAbility();
    tradSwafnir.name = "Tradition (Swafnirkirche)";
    tradSwafnir.key = SpecialAbilityKey.tradition_swafnirkirche;
    tradSwafnir.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradSwafnir.ap = 115;
    tradSwafnir.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradSwafnir);

    SpecialAbility Levthan = new SpecialAbility();
    Levthan.name = "Tradition (Levthankult)";
    Levthan.key = SpecialAbilityKey.tradition_levthankult;
    Levthan.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    Levthan.ap = 125;
    Levthan.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(Levthan);

    SpecialAbility tradMarbo = new SpecialAbility();
    tradMarbo.name = "Tradition (Marbokult)";
    tradMarbo.key = SpecialAbilityKey.tradition_marbokult;
    tradMarbo.category = SpecialAbilityCategoryKey.tradition_alveran_minor;
    tradMarbo.ap = 120;
    tradMarbo.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradMarbo);

    SpecialAbility tradNuminorukult = new SpecialAbility();
    tradNuminorukult.name = "Tradition (Numinorukult)";
    tradNuminorukult.key = SpecialAbilityKey.tradition_numinorukult;
    tradNuminorukult.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradNuminorukult.ap = 125;
    tradNuminorukult.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradNuminorukult);

    SpecialAbility tradShinxir = new SpecialAbility();
    tradShinxir.name = "Tradition (Shinxirkult)";
    tradShinxir.key = SpecialAbilityKey.tradition_shinxirkult;
    tradShinxir.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradShinxir.ap = 165;
    tradShinxir.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradShinxir);

    SpecialAbility tradAngrosch = new SpecialAbility();
    tradAngrosch.name = "Tradition (Angroschkirche)";
    tradAngrosch.key = SpecialAbilityKey.tradition_angroschkirche;
    tradAngrosch.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradAngrosch.ap = 125;
    tradAngrosch.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradAngrosch);

    SpecialAbility tradZsahh = new SpecialAbility();
    tradZsahh.name = "Tradition (Zsahh Kult)";
    tradZsahh.key = SpecialAbilityKey.tradition_zsahh_kult;
    tradZsahh.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradZsahh.ap = 140;
    tradZsahh.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradZsahh);

    SpecialAbility tradHSzint = new SpecialAbility();
    tradHSzint.name = "Tradition (H'Szint Kult)";
    tradHSzint.key = SpecialAbilityKey.tradition_h_szint_kult;
    tradHSzint.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradHSzint.ap = 130;
    tradHSzint.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradHSzint);

    SpecialAbility tradChrSsirSsr = new SpecialAbility();
    tradChrSsirSsr.name = "Tradition (Chr'Ssir'Ssr-Kult)";
    tradChrSsirSsr.key = SpecialAbilityKey.tradition_chr_ssir_ssr_kult;
    tradChrSsirSsr.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradChrSsirSsr.ap = 130;
    tradChrSsirSsr.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradChrSsirSsr);

    SpecialAbility tradTairach = new SpecialAbility();
    tradTairach.name = "Tradition (Tairachkult)";
    tradTairach.key = SpecialAbilityKey.tradition_tairachkult;
    tradTairach.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradTairach.ap = 135;
    tradTairach.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradTairach);

    SpecialAbility tradGravesh = new SpecialAbility();
    tradGravesh.name = "Tradition (Graveshkult)";
    tradGravesh.key = SpecialAbilityKey.tradition_graveshkult;
    tradGravesh.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradGravesh.ap = 120;
    tradGravesh.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradGravesh);

    SpecialAbility tradNamenlos = new SpecialAbility();
    tradNamenlos.name = "Tradition (Namenloser Kult)";
    tradNamenlos.key = SpecialAbilityKey.tradition_namenloser_kult;
    tradNamenlos.category = SpecialAbilityCategoryKey.tradition_non_alveran;
    tradNamenlos.ap = 150;
    tradNamenlos.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    returnValue.add(tradNamenlos);

    SpecialAbility tradFerkina = new SpecialAbility();
    tradFerkina.name = "Tradition (Ferkinaschamane)";
    tradFerkina.key = SpecialAbilityKey.tradition_ferkinaschamanen;
    tradFerkina.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradFerkina.ap = 100;
    tradFerkina.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradFerkina.requireOneOfCultures = List.of(CultureKey.ferkinas);
    returnValue.add(tradFerkina);

    SpecialAbility tradFjarninger = new SpecialAbility();
    tradFjarninger.name = "Tradition (Fjarningerschamane)";
    tradFjarninger.key = SpecialAbilityKey.tradition_fjarningerschamanen;
    tradFjarninger.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradFjarninger.ap = 100;
    tradFjarninger.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradFjarninger.requireOneOfCultures = List.of(CultureKey.fjarninger);
    returnValue.add(tradFjarninger);

    SpecialAbility tradGjalsker = new SpecialAbility();
    tradGjalsker.name = "Tradition (Gjalskerschamane)";
    tradGjalsker.key = SpecialAbilityKey.tradition_gjalskerschamanen;
    tradGjalsker.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradGjalsker.ap = 100;
    tradGjalsker.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradGjalsker.requireOneOfCultures = List.of(CultureKey.gjalsker);
    returnValue.add(tradGjalsker);

    SpecialAbility tradNivesen = new SpecialAbility();
    tradNivesen.name = "Tradition (Nivesenschamane)";
    tradNivesen.key = SpecialAbilityKey.tradition_nivesenschamanen;
    tradNivesen.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradNivesen.ap = 100;
    tradNivesen.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradNivesen.requireOneOfCultures = List.of(CultureKey.nivesen);
    returnValue.add(tradNivesen);

    SpecialAbility tradTahaya = new SpecialAbility();
    tradTahaya.name = "Tradition (Tahayaschamanen)";
    tradTahaya.key = SpecialAbilityKey.tradition_tahayaschamanen;
    tradTahaya.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradTahaya.ap = 100;
    tradTahaya.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradTahaya.requireOneOfCultures = List.of(CultureKey.waldmenschen, CultureKey.utulu);
    returnValue.add(tradTahaya);

    SpecialAbility tradTrollzacker = new SpecialAbility();
    tradTrollzacker.name = "Tradition (Trollzackerschamanen)";
    tradTrollzacker.key = SpecialAbilityKey.tradition_trollzackerschamanen;
    tradTrollzacker.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradTrollzacker.ap = 100;
    tradTrollzacker.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.geweihter, Boolean.TRUE));
    tradTrollzacker.requireOneOfCultures = List.of(CultureKey.trollzacker);
    returnValue.add(tradTrollzacker);

    SpecialAbility tradAchazschamanen = new SpecialAbility();
    tradAchazschamanen.name = "Tradition (Achazschamanen)";
    tradAchazschamanen.key = SpecialAbilityKey.tradition_achazschamanen;
    tradAchazschamanen.category = SpecialAbilityCategoryKey.tradition_shaman;
    tradAchazschamanen.ap = 135;
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
    tradAnimisten.ap = 125;
    tradAnimisten.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradAnimisten.requireOneOfCultures = List.of(CultureKey.ferkinas, CultureKey.fjarninger, CultureKey.gjalsker, CultureKey.nivesen, CultureKey.stammesorks, CultureKey.svellttal, CultureKey.waldmenschen, CultureKey.utulu, CultureKey.trollzacker);
    returnValue.add(tradAnimisten);

    SpecialAbility tradBannzeichner = new SpecialAbility();
    tradBannzeichner.name = "Tradition (Bannzeichner)";
    tradBannzeichner.key = SpecialAbilityKey.tradition_ysilische_bannzeichner;
    tradBannzeichner.category = SpecialAbilityCategoryKey.tradition_magic;
    tradBannzeichner.ap = 40;
    tradBannzeichner.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradBannzeichner);

    SpecialAbility tradBorbaradianer = new SpecialAbility();
    tradBorbaradianer.name = "Tradition (Borbaradianer)";
    tradBorbaradianer.key = SpecialAbilityKey.tradition_borbaradianer;
    tradBorbaradianer.category = SpecialAbilityCategoryKey.tradition_magic;
    tradBorbaradianer.ap = 70;
    returnValue.add(tradBorbaradianer);

    SpecialAbility tradDarna = new SpecialAbility();
    tradDarna.name = "Tradition (Darna)";
    tradDarna.key = SpecialAbilityKey.tradition_darna;
    tradDarna.category = SpecialAbilityCategoryKey.tradition_magic;
    tradDarna.ap = 110;
    tradDarna.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradDarna.requireOneOfCultures = List.of(CultureKey.waldmenschen, CultureKey.utulu);
    returnValue.add(tradDarna);

    SpecialAbility tradDruiden = new SpecialAbility();
    tradDruiden.name = "Tradition (Druiden)";
    tradDruiden.key = SpecialAbilityKey.tradition_druiden;
    tradDruiden.category = SpecialAbilityCategoryKey.tradition_magic;
    tradDruiden.ap = 125;
    tradDruiden.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradDruiden.requireNoneOfBoons = List.of(new RequirementBoon(BoonKey.eisenaffine_aura, Boolean.FALSE));
    returnValue.add(tradDruiden);

    SpecialAbility tradElfen = new SpecialAbility();
    tradElfen.name = "Tradition (Elfen)";
    tradElfen.key = SpecialAbilityKey.tradition_elfen;
    tradElfen.category = SpecialAbilityCategoryKey.tradition_magic;
    tradElfen.ap = 125;
    tradElfen.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradElfen);

    SpecialAbility tradGeoden = new SpecialAbility();
    tradGeoden.name = "Tradition (Geoden)";
    tradGeoden.key = SpecialAbilityKey.tradition_geoden;
    tradGeoden.category = SpecialAbilityCategoryKey.tradition_magic;
    tradGeoden.ap = 130;
    tradGeoden.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradGeoden.requireNoneOfBoons = List.of(new RequirementBoon(BoonKey.eisenaffine_aura, Boolean.FALSE));
    tradGeoden.requiredSpecie = SpecieKey.zwerg;
    returnValue.add(tradGeoden);

    SpecialAbility tradBrobimGeoden = new SpecialAbility();
    tradBrobimGeoden.name = "Tradition (Brobim-Geoden)";
    tradBrobimGeoden.key = SpecialAbilityKey.tradition_brobim_geoden;
    tradBrobimGeoden.category = SpecialAbilityCategoryKey.tradition_magic;
    tradBrobimGeoden.ap = 125;
    tradBrobimGeoden.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradBrobimGeoden.requireNoneOfBoons = List.of(new RequirementBoon(BoonKey.eisenaffine_aura, Boolean.FALSE));
    tradBrobimGeoden.requireOneOfCultures = List.of(CultureKey.wildzwerge);
    tradBrobimGeoden.requiredSpecie = SpecieKey.zwerg;
    returnValue.add(tradBrobimGeoden);

    SpecialAbility tradGildenmagier = new SpecialAbility();
    tradGildenmagier.name = "Tradition (Gildenmagier)";
    tradGildenmagier.key = SpecialAbilityKey.tradition_gildenmagier;
    tradGildenmagier.category = SpecialAbilityCategoryKey.tradition_magic;
    tradGildenmagier.ap = 155;
    tradGildenmagier.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradGildenmagier);

    SpecialAbility tradQabalyamagier = new SpecialAbility();
    tradQabalyamagier.name = "Tradition (Qabalyamagier)";
    tradQabalyamagier.key = SpecialAbilityKey.tradition_qabalyamagier;
    tradQabalyamagier.category = SpecialAbilityCategoryKey.tradition_magic;
    tradQabalyamagier.ap = 165;
    tradQabalyamagier.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradQabalyamagier);

    SpecialAbility tradGoblinzauber = new SpecialAbility();
    tradGoblinzauber.name = "Tradition (Goblinzauber)";
    tradGoblinzauber.key = SpecialAbilityKey.tradition_goblinzauberinnen;
    tradGoblinzauber.category = SpecialAbilityCategoryKey.tradition_magic;
    tradGoblinzauber.ap = 100;
    tradGoblinzauber.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradGoblinzauber.requireOneOfCultures = List.of(CultureKey.stammesgoblins);
    returnValue.add(tradGoblinzauber);

    SpecialAbility tradHexen = new SpecialAbility();
    tradHexen.name = "Tradition (Hexen)";
    tradHexen.key = SpecialAbilityKey.tradition_hexen;
    tradHexen.category = SpecialAbilityCategoryKey.tradition_magic;
    tradHexen.ap = 135;
    tradHexen.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradHexen);

    SpecialAbility tradKristallomanten = new SpecialAbility();
    tradKristallomanten.name = "Tradition (Kristallomanten)";
    tradKristallomanten.key = SpecialAbilityKey.tradition_kristallomanten;
    tradKristallomanten.category = SpecialAbilityCategoryKey.tradition_magic;
    tradKristallomanten.ap = 115;
    tradKristallomanten.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradKristallomanten.requiredSpecie = SpecieKey.achaz;
    returnValue.add(tradKristallomanten);

    SpecialAbility tradIntuitiverZauberer = new SpecialAbility();
    tradIntuitiverZauberer.name = "Tradition (Intuitive Zauberer)";
    tradIntuitiverZauberer.key = SpecialAbilityKey.tradition_intuitive_zauberer;
    tradIntuitiverZauberer.category = SpecialAbilityCategoryKey.tradition_magic;
    tradIntuitiverZauberer.ap = 50;
    tradIntuitiverZauberer.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradIntuitiverZauberer);

    SpecialAbility tradMeistertalentierte = new SpecialAbility();
    tradMeistertalentierte.name = "Tradition (Meisterhandwerker)";
    tradMeistertalentierte.key = SpecialAbilityKey.tradition_meistertalentierte;
    tradMeistertalentierte.category = SpecialAbilityCategoryKey.tradition_magic;
    tradMeistertalentierte.ap = 35;
    tradMeistertalentierte.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradMeistertalentierte);

    SpecialAbility tradRunenschöpfer = new SpecialAbility();
    tradRunenschöpfer.name = "Tradition (Runenschöpfer)";
    tradRunenschöpfer.key = SpecialAbilityKey.tradition_runenschöpfer;
    tradRunenschöpfer.category = SpecialAbilityCategoryKey.tradition_magic;
    tradRunenschöpfer.ap = 75;
    tradRunenschöpfer.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradRunenschöpfer);

    SpecialAbility tradScharlatane = new SpecialAbility();
    tradScharlatane.name = "Tradition (Scharlatane)";
    tradScharlatane.key = SpecialAbilityKey.tradition_animisten;
    tradScharlatane.category = SpecialAbilityCategoryKey.tradition_magic;
    tradScharlatane.ap = 125;
    tradScharlatane.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradScharlatane);

    SpecialAbility tradSchelme = new SpecialAbility();
    tradSchelme.name = "Tradition (Schelme)";
    tradSchelme.key = SpecialAbilityKey.tradition_schelme;
    tradSchelme.category = SpecialAbilityCategoryKey.tradition_magic;
    tradSchelme.ap = 125;
    tradSchelme.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradSchelme.requireOneOfCultures = List.of(CultureKey.koboldweltler);
    returnValue.add(tradSchelme);

    SpecialAbility tradZauberalchimisten = new SpecialAbility();
    tradZauberalchimisten.name = "Tradition (Zauberalchimisten)";
    tradZauberalchimisten.key = SpecialAbilityKey.tradition_zauberalchimisten;
    tradZauberalchimisten.category = SpecialAbilityCategoryKey.tradition_magic;
    tradZauberalchimisten.ap = 45;
    tradZauberalchimisten.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    returnValue.add(tradZauberalchimisten);

    SpecialAbility tradZauberbarden = new SpecialAbility();
    tradZauberbarden.name = "Tradition (Zauberbarden)";
    tradZauberbarden.key = SpecialAbilityKey.tradition_zauberbarden;
    tradZauberbarden.category = SpecialAbilityCategoryKey.tradition_magic;
    tradZauberbarden.ap = 80;
    tradZauberbarden.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradSchelme.requireOneOfCultures = List.of(CultureKey.mittelreich, CultureKey.novadi, CultureKey.thorwal, CultureKey.zyklopeninseln);
    returnValue.add(tradZauberbarden);

    SpecialAbility tradZaubertänzer = new SpecialAbility();
    tradZaubertänzer.name = "Tradition (Zaubertänzer)";
    tradZaubertänzer.key = SpecialAbilityKey.tradition_zaubertänzer;
    tradZaubertänzer.category = SpecialAbilityCategoryKey.tradition_magic;
    tradZaubertänzer.ap = 75;
    tradZaubertänzer.requireOneOfBoons = List.of(new RequirementBoon(BoonKey.zauberer, Boolean.TRUE));
    tradSchelme.requireOneOfCultures = List.of(CultureKey.zahori, CultureKey.aranien, CultureKey.novadi, CultureKey.mhanadistan);
    returnValue.add(tradZaubertänzer);

    SpecialAbility tradZibilja = new SpecialAbility();
    tradZibilja.name = "Tradition (Zibilja)";
    tradZibilja.key = SpecialAbilityKey.tradition_zibilja;
    tradZibilja.category = SpecialAbilityCategoryKey.tradition_magic;
    tradZibilja.ap = 100;
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

