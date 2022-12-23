package de.pho.dsapdfreader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.pho.dsapdfreader.config.ConfigurationInitializer;
import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.TopicStrategies;
import de.pho.dsapdfreader.dsaconverter.DsaConverterArmor;
import de.pho.dsapdfreader.dsaconverter.DsaConverterBoon;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMeleeWeapon;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMsyticalSkillCommonness;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMsyticalSkillElements;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMsyticalSkillIncantations;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkill;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillActivity;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillGrimorium;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillGrimoriumTricks;
import de.pho.dsapdfreader.dsaconverter.DsaConverterProfession;
import de.pho.dsapdfreader.dsaconverter.DsaConverterRangedWeapon;
import de.pho.dsapdfreader.dsaconverter.DsaConverterSpecialAbility;
import de.pho.dsapdfreader.dsaconverter.DsaConverterSpecialAbilityClericBase;
import de.pho.dsapdfreader.dsaconverter.model.ArmorRaw;
import de.pho.dsapdfreader.dsaconverter.model.MeleeWeaponRaw;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.dsaconverter.model.RangedWeaponRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.DsaConverterStrategy;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillKey;
import de.pho.dsapdfreader.exporter.LoadToArmor;
import de.pho.dsapdfreader.exporter.LoadToMeleeWeapon;
import de.pho.dsapdfreader.exporter.LoadToMysticalSkill;
import de.pho.dsapdfreader.exporter.model.Armor;
import de.pho.dsapdfreader.exporter.model.MeleeWeapon;
import de.pho.dsapdfreader.exporter.model.MysticalSkill;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.pdf.PdfReader;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;
import de.pho.dsapdfreader.tools.csv.CsvHandler;

public class DsaPdfReaderMain
{

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

  public static void main(String[] args)
  {
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
    isSummarize = true;

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


  private static void summarizeTextCsv(String path, String file)
  {
    try (Stream<Path> paths = Files.walk(Paths.get(path)))
    {
      List<TextWithMetaInfo> concatList = new ArrayList<>();
      paths.filter(Files::isRegularFile)
          .filter(p -> !p.endsWith(file))
          .map(p -> CsvHandler.readBeanFromPath(TextWithMetaInfo.class, p))
          .forEach(l -> concatList.addAll(l));
      File fOut = new File(path, file);
      CsvHandler.writeBeanToUrl(fOut, concatList);
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private static void summarizeMsCsv(String path, String file)
  {
    try (Stream<Path> paths = Files.walk(Paths.get(path)))
    {
      List<MysticalSkillRaw> concatList = new ArrayList<>();
      paths.filter(Files::isRegularFile)
          .filter(p -> !p.endsWith(file))
          .map(p -> CsvHandler.readBeanFromPath(MysticalSkillRaw.class, p))
          .forEach(l -> concatList.addAll(l));
      File fOut = new File(path, file);
      CsvHandler.writeBeanToUrl(fOut, concatList);
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private static void summarizeMsJson(String path, String file)
  {
    try (Stream<Path> paths = Files.walk(Paths.get(path)))
    {
      List<MysticalSkill> mysticalSkills = new ArrayList<>();
      paths.filter(Files::isRegularFile)
          .filter(p -> !p.endsWith(file))
          .map(p -> {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = readFromInputStream(new File(p.toString()));
            try
            {
              return objectMapper.readValue(json, new TypeReference<List<MysticalSkill>>()
              {
              });
            }
            catch (JsonProcessingException e)
            {
              LOGGER.error("JSON processing Error in %s", path, e);
            }
            return new ArrayList<MysticalSkill>();
          })
          .forEach(l -> mysticalSkills.addAll(l));
      ObjectMapper mapper = new ObjectMapper();
      String jsonResult = mapper
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(mysticalSkills);

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(path + file)))
      {
        writer.write(jsonResult);
        writer.flush();
      }
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private static String readFromInputStream(File file)
  {
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br
             = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
    {
      String line;
      while ((line = br.readLine()) != null)
      {
        resultStringBuilder.append(line).append("\n");
      }
    }
    catch (IOException e)
    {
      LOGGER.error("Error reading File (%s)", file.getAbsolutePath());
    }
    return resultStringBuilder.toString();
  }


  private static void convertToText()
  {
    configs.stream()
        .filter(c -> c != null && c.active)
        .forEach(conf -> {
          String msg = String.format("Config PDF import verarbeiten: %s (%s)", conf.publication, conf.topic);
          LOGGER.info(msg);

          List<TextWithMetaInfo> pdfResults;
          try
          {
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

            File fOut = new File(String.format(FILE_PDF_2_TEXT, conf.publication + "_" + conf.topic));
            CsvHandler.writeBeanToUrl(fOut, texts);
          }
          catch (IOException | NullPointerException e)
          {
            LOGGER.error(e.getMessage(), e);
          }
          LOGGER.debug(SEPARATOR);
        });
  }

  private static void convertToStrategy()
  {
    configs.stream()
        .filter(c -> c != null && c.active)
        .forEach(conf -> {
          String msg = String.format("Config Strategie verarbeiten: %s (%s)", conf.publication, conf.topic);
          LOGGER.info(msg);
          LOGGER.debug(generateFileName(FILE_PDF_2_TEXT, conf));
          File fIn = new File(generateFileName(FILE_PDF_2_TEXT, conf));

          List<TextWithMetaInfo> texts;
          try
          {
            texts = CsvHandler.readBeanFromFile(TextWithMetaInfo.class, fIn);

            texts = applyStrategies(texts, conf);

            File fOut = new File(String.format(FILE_TEXT_2_STRATEGY, conf.publication + "_" + conf.topic));
            CsvHandler.writeBeanToUrl(fOut, texts);
          }
          catch (NullPointerException e)
          {
            LOGGER.error(e.getMessage(), e);
          }
          LOGGER.debug(SEPARATOR);
        });
  }


  private static void convertToRaws()
  {
    configs.stream()
        .filter(conf -> conf != null && conf.active)
        .forEach(conf -> {
          String msg = String.format("Config zu raw-objects verarbeiten: %s (%s)", conf.publication, conf.topic);
          LOGGER.info(msg);

          File fIn = new File(generateFileName(FILE_TEXT_2_STRATEGY, conf));

          List<TextWithMetaInfo> texts;
          try
          {
            texts = CsvHandler.readBeanFromFile(TextWithMetaInfo.class, fIn);

            List results = parseResult(texts, conf);

            if (results != null)
            {
              File fOut = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
              msg = String.format("# of Entries of Type (%s): %s", conf.topic, results.size());
              LOGGER.info(msg);
              CsvHandler.writeBeanToUrl(fOut, results);
            }
          }
          catch (NullPointerException e)
          {
            LOGGER.error(e.getMessage(), e);
          }
          LOGGER.debug(SEPARATOR);
        });
  }

  private static String generateFileName(String filePattern, TopicConfiguration conf)
  {
    return String.format(filePattern, conf.publication + "_" + conf.topic);
  }

  private static String generateFileNameTypedDirectory(String filePattern, TopicConfiguration conf, String type)
  {
    return String.format(filePattern, type, conf.publication + "_" + conf.topic);
  }


  private static void convertToJson()
  {
    configs.stream()
        .filter(conf -> conf != null && conf.active)
        .forEach(DsaPdfReaderMain::parseToJson);
  }

  private static void summarizeRawByType()
  {
    Map<String, List<MysticalSkillRaw>> allByTypeMap = new HashMap<>();
    List<MeleeWeaponRaw> meleeWeaponRawList = new ArrayList<>();
    List<RangedWeaponRaw> rangedWeaponRawList = new ArrayList<>();
    List<ArmorRaw> armorRawList = new ArrayList<>();

    configs.stream()
        .filter(conf -> conf != null && conf.active)
        .forEach(conf -> {
          String msg = String.format("Config zu JSON verarbeiten: %s (%s)", conf.publication, conf.topic);
          LOGGER.info(msg);
          switch (conf.topic)
          {
          case BLESSINGS, TRICKS, SPELLS, LITURGIES, RITUALS, CEREMONIES, SPELLS_GRIMORIUM, RITUALS_GRIMORIUM, TRICKS_GRIMORIUM, CURSES, ELFENSONGS, MELODIES, DANCES ->
          {
            List<MysticalSkillRaw> mysticalSkillRawList = CsvHandler.readBeanFromFile(MysticalSkillRaw.class, new File(generateFileName(FILE_STRATEGY_2_RAW, conf)));
            if (allByTypeMap.containsKey(MysticalSkill.TYPE)) allByTypeMap.get(MysticalSkill.TYPE).addAll(mysticalSkillRawList);
            else allByTypeMap.put(MysticalSkill.TYPE, mysticalSkillRawList);
          }
          case INCANTATIONS_RIME_SPELLS_GRIMORIUM, INCANTATIONS_RIME_RITUALS_GRIMORIUM, INCANTATIONS_ZHAYAD_SPELLS_GRIMORIUM, INCANTATIONS_ZHAYAD_RITUALS_GRIMORIUM, ELEMENTS_SPELLS_GRIMORIUM, ELEMENTS_RITUALS_GRIMORIUM ->
          {
            break;
          }
          case NAHKAMPFWAFFEN ->
          {
            List<MeleeWeaponRaw> rawList = CsvHandler.readBeanFromFile(MeleeWeaponRaw.class, new File(generateFileName(FILE_STRATEGY_2_RAW, conf)));
            meleeWeaponRawList.addAll(rawList);
          }
          case FERNKAMPFWAFFEN ->
          {
            List<RangedWeaponRaw> rawList = CsvHandler.readBeanFromFile(RangedWeaponRaw.class, new File(generateFileName(FILE_STRATEGY_2_RAW, conf)));
            rangedWeaponRawList.addAll(rawList);
          }
          case RÜSTUNGEN ->
          {
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


  private static void parseToJson(TopicConfiguration conf)
  {
    String msg = String.format("Config zu JSON verarbeiten: %s (%s)", conf.publication, conf.topic);
    LOGGER.info(msg);
    switch (conf.topic)
    {
    case BLESSINGS, TRICKS, SPELLS, LITURGIES, RITUALS, CEREMONIES, SPELLS_GRIMORIUM, RITUALS_GRIMORIUM, TRICKS_GRIMORIUM, CURSES, ELFENSONGS, MELODIES, DANCES ->
    {
      try
      {
        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));

        List<MysticalSkillRaw> rawMysticalSkills = CsvHandler.readBeanFromFile(MysticalSkillRaw.class, fIn);
        List<MysticalSkill> mysticalSkills = rawMysticalSkills.stream().map(LoadToMysticalSkill::migrate).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(mysticalSkills);

        BufferedWriter writer = new BufferedWriter(new FileWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf, "mysticalskills")));
        writer.write(jsonResult);
        writer.close();

        if (conf.topic == TopicEnum.RITUALS
            || conf.topic == TopicEnum.RITUALS_GRIMORIUM
            || conf.topic == TopicEnum.SPELLS
            || conf.topic == TopicEnum.SPELLS_GRIMORIUM
        )
        {
          writer = new BufferedWriter(new FileWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf, "mysticalskills_descriptions")));
          writer.write(generateDescriptionString(rawMysticalSkills));
          writer.close();


          writer = new BufferedWriter(new FileWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf, "mysticalskills_effects")));
          writer.write(generateEffectString(rawMysticalSkills));
          writer.close();

          writer = new BufferedWriter(new FileWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf, "mysticalskills_variant_descriptions")));
          writer.write(generateVariantDescriptionString(rawMysticalSkills));
          writer.close();
        }
      }
      catch (JsonProcessingException e)
      {
        throw new RuntimeException(e);
      }
      catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    }
    case NAHKAMPFWAFFEN ->
    {
      try
      {

        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<MeleeWeaponRaw> raws = CsvHandler.readBeanFromFile(MeleeWeaponRaw.class, fIn);

        List<MeleeWeapon> meleeWeapons = raws.stream().map(LoadToMeleeWeapon::migrate).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(meleeWeapons);

        BufferedWriter writer = new BufferedWriter(new FileWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf, "equipment")));
        writer.write(jsonResult);
        writer.close();
      }
      catch (JsonProcessingException e)
      {
        throw new RuntimeException(e);
      }
      catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    }
    case FERNKAMPFWAFFEN ->
    {

    }
    case RÜSTUNGEN ->
    {
      try
      {
        File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));
        List<ArmorRaw> raws = CsvHandler.readBeanFromFile(ArmorRaw.class, fIn);

        List<Armor> pures = raws.stream().map(LoadToArmor::migrate).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(pures);

        BufferedWriter writer = new BufferedWriter(new FileWriter(generateFileNameTypedDirectory(FILE_RAW_2_JSON, conf, "equipment")));
        writer.write(jsonResult);
        writer.close();
      }
      catch (JsonProcessingException e)
      {
        throw new RuntimeException(e);
      }
      catch (IOException e)
      {
        throw new RuntimeException(e);
      }

    }
    case INCANTATIONS_RIME_SPELLS_GRIMORIUM, INCANTATIONS_RIME_RITUALS_GRIMORIUM, INCANTATIONS_ZHAYAD_SPELLS_GRIMORIUM, INCANTATIONS_ZHAYAD_RITUALS_GRIMORIUM, ELEMENTS_SPELLS_GRIMORIUM, ELEMENTS_RITUALS_GRIMORIUM ->
    {
      break;
    }
    default -> LOGGER.error(String.format("Unexpected value (parseToJson): %s", conf.topic));
    }
  }

  private static String generateEffectString(List<MysticalSkillRaw> rawMysticalSkills)
  {
    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream().forEach(msr -> {
      returnValue.append(ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr, Extractor.retrieveCategory(msr.topic)).toValue()
          + " {"
          + msr.effect
          + "}"
          + "\r\n");
      if (msr.description == null || msr.description.isEmpty())
      {
        LOGGER.error("Mystical Skill (" + msr.description + ") has no description!");
      }
    });
    return returnValue.toString();

  }

  private static String generateVariantDescriptionString(List<MysticalSkillRaw> rawMysticalSkills)
  {

    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream()
        .forEach(msr -> {
          returnValue.append("{" + msr.variant1.key.ordinal() + "} " + msr.variant1.description + "\r\n");
          returnValue.append("{" + msr.variant2.key.ordinal() + "} " + msr.variant2.description + "\r\n");
          returnValue.append("{" + msr.variant3.key.ordinal() + "} " + msr.variant3.description + "\r\n");
          returnValue.append("{" + msr.variant4.key.ordinal() + "} " + msr.variant4.description + "\r\n");
          returnValue.append("{" + msr.variant5.key.ordinal() + "} " + msr.variant5.description + "\r\n");
        });
    return returnValue.toString();
  }

  private static String generateDescriptionString(List<MysticalSkillRaw> rawMysticalSkills)
  {
    StringBuilder returnValue = new StringBuilder();
    rawMysticalSkills.stream().forEach(msr -> {
      returnValue.append(
          ExtractorMysticalSkillKey.retrieveMysticalSkillKey(msr, Extractor.retrieveCategory(msr.topic)).toValue()
              + " {"
              + msr.description
              + "} "
              + "\r\n");
      if (msr.description == null || msr.description.isEmpty())
      {
        LOGGER.error("Mystical Skill (" + msr.description + ") has no description!");
      }
    });
    return returnValue.toString();
  }

  private static List<TextWithMetaInfo> extractPdfResults(TopicConfiguration conf) throws IOException
  {
    List<TextWithMetaInfo> returnValue = new ArrayList<>();

    String path = conf.path;
    if (path == null)
    {
      URL urlPdfLibInClassPath = DsaPdfReaderMain.class.getClassLoader().getResource("./pdf_lib");
      if (urlPdfLibInClassPath == null)
      {
        throw new InvalidPathException("./pdf_lib", "der Pfad für die PDF-Bibliothek konnte nicht gefunden werden");
      }
      path = urlPdfLibInClassPath.getFile();
    }
    File fIn = new File(path + "/" + conf.pdfName);
    returnValue.addAll(PdfReader.convertToText(fIn, conf));
    return returnValue;

  }

  private static List<TextWithMetaInfo> applyStrategies(List<TextWithMetaInfo> texts, TopicConfiguration conf)
  {
    if (conf.strategyMapping != null && !conf.strategyMapping.isEmpty())
    {
      TopicStrategies topicMappings = unmarshall(conf.strategyMapping);
      if (topicMappings != null)
      {
        List<TopicStrategies.Strategy> applicableStrategies = topicMappings.getStrategy().stream()
            .filter(s -> s.getParams().getParameter().stream()
                .filter(p -> p.getKey().equals("applyToPage"))
                .filter(p -> (Integer.valueOf(p.getValue()) >= conf.startPage) && (Integer.valueOf(p.getValue()) <= conf.endPage))
                .count() > 0
            ).collect(Collectors.toList());
        for (TopicStrategies.Strategy s : applicableStrategies)

        {
          try
          {
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
                 | ClassNotFoundException e)
          {
            LOGGER.error(e.getMessage(), e);
          }
        }
      }
    }
    return texts;
  }

  private static TopicStrategies unmarshall(String fileName)
  {
    LOGGER.debug("initialize Strategy mapping: %s", fileName);
    URL url = DsaPdfReaderMain.class.getResource("/" + fileName);
    File file = new File(url.getFile());
    JAXBContext jaxbContext;
    TopicStrategies returnValue = null;
    try
    {
      jaxbContext = JAXBContext.newInstance(TopicStrategies.class);

      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      returnValue = (TopicStrategies) jaxbUnmarshaller.unmarshal(file);
    }
    catch (JAXBException e)
    {
      e.printStackTrace();
    }

    return returnValue;
  }

  private static void initConfig()
  {
    configs = ConfigurationInitializer.readTopicConfigurations();
  }

  private static List parseResult(List<TextWithMetaInfo> texts, TopicConfiguration conf)
  {
    List results = null;
    switch (conf.topic)
    {
    case MERITS, FLAWS -> results = new DsaConverterBoon().convertTextWithMetaInfo(texts, conf);
    case SPECIAL_ABILITY_MUNDANE, SPECIAL_ABILITY_FATE, SPECIAL_ABILITY_COMBAT, SPECIAL_ABILITY_MAGIC, SPECIAL_ABILITY_CLERIC, SPECIAL_ABILITY_SERMONS, SPECIAL_ABILITY_VISIONS ->
        results = new DsaConverterSpecialAbility().convertTextWithMetaInfo(texts, conf);
    case SPECIAL_ABILITY_CLERIC_BASE -> results = new DsaConverterSpecialAbilityClericBase().convertTextWithMetaInfo(texts, conf);
    case BLESSINGS, TRICKS, SPELLS, LITURGIES, RITUALS, CEREMONIES -> results = new DsaConverterMysticalSkill().convertTextWithMetaInfo(texts, conf);
    case SPELLS_GRIMORIUM, RITUALS_GRIMORIUM -> results = new DsaConverterMysticalSkillGrimorium().convertTextWithMetaInfo(texts, conf);
    case TRICKS_GRIMORIUM -> results = new DsaConverterMysticalSkillGrimoriumTricks().convertTextWithMetaInfo(texts, conf);
    case CURSES, ELFENSONGS, MELODIES, DANCES, ANIMIST -> results = new DsaConverterMysticalSkillActivity().convertTextWithMetaInfo(texts, conf);
    case TRADITIONS_GRIMORIUM -> System.out.println("IMPLEMENTIER MICH");
    case INCANTATIONS_RIME_SPELLS_GRIMORIUM, INCANTATIONS_ZHAYAD_SPELLS_GRIMORIUM ->
    {
      Map<String, String> incantations = new DsaConverterMsyticalSkillIncantations().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(incantations, TopicEnum.SPELLS, conf.topic, conf.publication);
    }
    case INCANTATIONS_RIME_RITUALS_GRIMORIUM, INCANTATIONS_ZHAYAD_RITUALS_GRIMORIUM ->
    {
      Map<String, String> incantations = new DsaConverterMsyticalSkillIncantations().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(incantations, TopicEnum.RITUALS, conf.topic, conf.publication);
    }
    case ELEMENTS_SPELLS_GRIMORIUM ->
    {
      Map<String, String> spellsToElements = new DsaConverterMsyticalSkillElements().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(spellsToElements, TopicEnum.SPELLS, conf.topic, conf.publication);
    }
    case ELEMENTS_RITUALS_GRIMORIUM ->
    {
      Map<String, String> spellsToElements = new DsaConverterMsyticalSkillElements().convertTextWithMetaInfo(texts);
      applyContentMapToMysticalSkills(spellsToElements, TopicEnum.RITUALS, conf.topic, conf.publication);
    }
    case MYSTICAL_SKILL_VARIANTS ->
    {
      List<MysticalSkillRaw> msrs = new DsaConverterMysticalSkill().convertTextWithMetaInfo(texts, conf);
      applyToBasis(msrs, conf);
      results = msrs;
    }
    case MYSTICAL_SKILL_COMMONNESS ->
    {
      Map<String, List<String>> commonness = new DsaConverterMsyticalSkillCommonness().convertTextWithMetaInfo(texts);
      applyToMysticalSkills(commonness);
    }
    case PROFESSIONS -> results = new DsaConverterProfession().convertTextWithMetaInfo(texts, conf);
    case NAHKAMPFWAFFEN -> results = new DsaConverterMeleeWeapon().convertTextWithMetaInfo(texts, conf);
    case FERNKAMPFWAFFEN -> results = new DsaConverterRangedWeapon().convertTextWithMetaInfo(texts, conf);
    case RÜSTUNGEN -> results = new DsaConverterArmor().convertTextWithMetaInfo(texts, conf);
    default -> LOGGER.error(String.format("Unexpected value (parseResult): %s", conf.topic));
    }
    return results;
  }

  private static void applyContentMapToMysticalSkills(Map<String, String> contentMap, TopicEnum topicApplyTo, TopicEnum topicFrom, String publication)
  {
    try (Stream<Path> paths = Files.walk(Paths.get(PATH_STRATEGY_2_RAW)))
    {
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
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private static List applyNewContentToMsr(List<MysticalSkillRaw> valueList, Map<String, String> contentMap, TopicEnum topic)
  {
    return valueList.stream().map(msr -> {
      Optional<String> incantationKeyOptional = contentMap.keySet().stream().filter(c ->
          c.toLowerCase().contains(msr.name.toLowerCase())
              || msr.name.toLowerCase().contains(c.toLowerCase())).findFirst();
      return applyContentToMysticalSkillRaw(msr, contentMap, incantationKeyOptional, topic);
    }).collect(Collectors.toList());
  }

  private static MysticalSkillRaw applyContentToMysticalSkillRaw(MysticalSkillRaw msr, Map<String, String> contentMap, Optional<String> keyOptional, TopicEnum topicFrom)
  {
    if (keyOptional.isPresent())
    {
      if (topicFrom == TopicEnum.INCANTATIONS_RIME_SPELLS_GRIMORIUM || topicFrom == TopicEnum.INCANTATIONS_RIME_RITUALS_GRIMORIUM)
        msr.rime = contentMap.get(keyOptional.get());
      if (topicFrom == TopicEnum.INCANTATIONS_ZHAYAD_SPELLS_GRIMORIUM || topicFrom == TopicEnum.INCANTATIONS_ZHAYAD_RITUALS_GRIMORIUM)
        msr.zhayad = contentMap.get(keyOptional.get());
      if (topicFrom == TopicEnum.ELEMENTS_SPELLS_GRIMORIUM || topicFrom == TopicEnum.ELEMENTS_RITUALS_GRIMORIUM)
        msr.elements = contentMap.get(keyOptional.get());
    }
    return msr;
  }

  private static void applyToMysticalSkills(Map<String, List<String>> commonnessList)
  {
    try (Stream<Path> paths = Files.walk(Paths.get(PATH_STRATEGY_2_RAW)))
    {
      HashMap<Path, List<MysticalSkillRaw>> resultMap = new HashMap();
      paths.filter(Files::isRegularFile)
          .filter(p -> !p.endsWith("all_raw.csv"))
          .forEach(p -> resultMap.put(p, CsvHandler.readBeanFromPath(MysticalSkillRaw.class, p)));

      resultMap.forEach((k, v) -> {
        List<MysticalSkillRaw> newMsList = v.stream().map(msr -> {
          if (commonnessList.containsKey(msr.name))
          {
            for (String commonness : commonnessList.get(msr.name))
            {
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
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private static void applyToBasis(List<MysticalSkillRaw> variants, TopicConfiguration conf)
  {
    String msg = String.format("Apply to BASIS: %s (%s)", conf.publication, conf.topic);
    LOGGER.info(msg);
    if (conf.publication.equals("Magie_1"))
    {
      applyVariantsToBasisMysticalSkills(variants, TopicEnum.SPELLS);
      applyVariantsToBasisMysticalSkills(variants, TopicEnum.RITUALS);
    }
    if (conf.publication.equals("Götter_1"))
    {
      applyVariantsToBasisMysticalSkills(variants, TopicEnum.LITURGIES);
      applyVariantsToBasisMysticalSkills(variants, TopicEnum.CEREMONIES);
    }
  }

  private static void applyVariantsToBasisMysticalSkills(List<MysticalSkillRaw> variants, TopicEnum topic)
  {
    File fIn = new File(String.format(FILE_STRATEGY_2_RAW, Publication.BASIS + "_" + topic));
    List<MysticalSkillRaw> spells = CsvHandler.readBeanFromFile(MysticalSkillRaw.class, fIn);
    List<MysticalSkillRaw> results = spells.stream().map(msr -> {
      Optional<MysticalSkillRaw> applicableVariantMsr = variants.stream().filter(v -> v.name.equals(msr.name)).findFirst();
      if (applicableVariantMsr.isPresent())
      {
        msr.variantsText = applicableVariantMsr.get().variantsText;
        msr.variant1 = applicableVariantMsr.get().variant1;
        msr.variant2 = applicableVariantMsr.get().variant2;
        msr.variant3 = applicableVariantMsr.get().variant3;
        msr.variant4 = applicableVariantMsr.get().variant4;
        msr.variant5 = applicableVariantMsr.get().variant5;
      }
      else
      {
        String msg = String.format("Für das Topic (%s)  (%s) wurden keine Varianten gefunden.", topic, msr.name);
        LOGGER.error(msg);
      }
      return msr;
    }).collect(Collectors.toList());

    CsvHandler.writeBeanToUrl(fIn, results);
  }
}

