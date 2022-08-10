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
import de.pho.dsapdfreader.dsaconverter.DsaConverterApplyChangesToMysticalSkills;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMeritsAndFlaws;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMsyticalSkillCommonness;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillActivity;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillMedium;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillSmall;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.DsaConverterStrategy;
import de.pho.dsapdfreader.exporter.LoadToMysticalSkill;
import de.pho.dsapdfreader.exporter.model.MysticalSkill;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.pdf.PdfReader;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;
import de.pho.dsapdfreader.tools.csv.CsvHandler;

public class DsaPdfReaderMain
{

    private static final String STRATEGY_PACKAGE = DsaConverterStrategy.class.getPackageName() + ".";
    private static final String DOCUMENT = "<DOCUMENT>";
    private static final String PATH_BASE = "C:\\develop\\project\\dsa-pdf-reader\\export\\";
    private static final String PATH_PDF_2_TEXT = PATH_BASE + "01 - pdf2text\\";
    private static final String FILE_PDF_2_TEXT = PATH_PDF_2_TEXT + DOCUMENT + "_txt.csv";
    private static final String PATH_TEXT_2_STRATEGY = PATH_BASE + "02 - applyStrategies\\";
    private static final String FILE_TEXT_2_STRATEGY = PATH_TEXT_2_STRATEGY + DOCUMENT + "_str.csv";
    private static final String PATH_STRATEGY_2_RAW = PATH_BASE + "03 - raw\\";
    private static final String FILE_STRATEGY_2_RAW = PATH_STRATEGY_2_RAW + DOCUMENT + "_raw.csv";
    private static final String PATH_RAW_2_JSON = PATH_BASE + "04 - json\\";
    private static final String FILE_RAW_2_JSON = PATH_RAW_2_JSON + DOCUMENT + ".json";
    private static final String SEPARATOR = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

    private static List<TopicConfiguration> configs = null;
    private static final Logger LOGGER = LogManager.getLogger();

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
        isToStrategy = false;
        isToRaws = true;
        isToJson = false;
        isSummarize = true;

        if (isToText) convertToText();
        if (isToText && isSummarize) summarizeTextCsv(PATH_PDF_2_TEXT, "all_txt.csv");
        if (isToStrategy) convertToStrategy();
        if (isToStrategy && isSummarize) summarizeTextCsv(PATH_TEXT_2_STRATEGY, "all_str.csv");
        if (isToRaws) convertToRaws();
        if (isToRaws && isSummarize) summarizeMsCsv(PATH_STRATEGY_2_RAW, "all_raw.csv");
        if (isToJson) convertToJson();
        if (isToJson && isSummarize) summarizeMsJson(PATH_RAW_2_JSON, "all.json");

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
        } catch (IOException e)
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
        } catch (IOException e)
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
                    } catch (JsonProcessingException e)
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
        } catch (IOException e)
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
        } catch (IOException e)
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
                LOGGER.info("Config PDF import verarbeiten: %s (%s)", conf.publication, conf.topic);

                List<TextWithMetaInfo> pdfResults;
                try
                {
                    pdfResults = extractPdfResults(conf);

                    LOGGER.debug("export Texts");
                    final List<TextWithMetaInfo> texts = pdfResults.stream()
                        .filter(t -> t.onPage == conf.startPage && (t.onLine > conf.startAfterLine || conf.startAfterLine == 0)
                            || t.onPage > conf.startPage && t.onPage < conf.endPage
                            || t.onPage == conf.endPage && (t.onLine <= conf.endAfterLine || conf.endAfterLine == 0)
                        )
                        .sorted((a, b) -> a.sortIndex() < b.sortIndex() ? -1 : 1)
                        .collect(Collectors.toList());

                    File fOut = new File(FILE_PDF_2_TEXT.replace(DOCUMENT, conf.publication + "_" + conf.topic));
                    CsvHandler.writeBeanToUrl(fOut, texts);
                } catch (IOException | NullPointerException e)
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

                    File fOut = new File(FILE_TEXT_2_STRATEGY.replace(DOCUMENT, conf.publication + "_" + conf.topic));
                    CsvHandler.writeBeanToUrl(fOut, texts);
                } catch (NullPointerException e)
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
                        msg = String.format("# of MysticalSkills of Type (%s): %s", conf.topic, results.size());
                        LOGGER.info(msg);
                        CsvHandler.writeBeanToUrl(fOut, results);
                    }
                } catch (NullPointerException e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
                LOGGER.debug(SEPARATOR);
            });
    }

    private static String generateFileName(String filePattern, TopicConfiguration conf)
    {
        return filePattern.replace(DOCUMENT, conf.publication + "_" + conf.topic);
    }


    private static void convertToJson()
    {
        configs.stream()
            .filter(conf -> conf != null && conf.active && conf.topic != TopicEnum.MYSTICAL_SKILL_VARIANTS && conf.topic != TopicEnum.MYSTICAL_SKILL_COMMONNESS)
            .forEach(conf -> {
                String msg = String.format("Config zu JSON verarbeiten: %s (%s)", conf.publication, conf.topic);
                LOGGER.info(msg);
                try
                {

                    File fIn = new File(generateFileName(FILE_STRATEGY_2_RAW, conf));

                    List<MysticalSkillRaw> rawMysticalSkills = CsvHandler.readBeanFromFile(MysticalSkillRaw.class, fIn);
                    List<MysticalSkill> mysticalSkills = rawMysticalSkills.stream().map(msm -> LoadToMysticalSkill.migrate(msm)).collect(Collectors.toList());

                    ObjectMapper mapper = new ObjectMapper();
                    String jsonResult = mapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(mysticalSkills);

                    BufferedWriter writer = new BufferedWriter(new FileWriter(generateFileName(FILE_RAW_2_JSON, conf)));
                    writer.write(jsonResult);

                    writer.close();

                } catch (IOException e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
            });
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
                    } catch (InstantiationException
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
        } catch (JAXBException e)
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
            case MERITS -> results = new DsaConverterMeritsAndFlaws().convertTextWithMetaInfo(texts, conf);
            case BLESSINGS, TRICKS -> results = new DsaConverterMysticalSkillSmall().convertTextWithMetaInfo(texts, conf);
            case SPELLS, LITURGIES, RITUALS, CEREMONIES -> results = new DsaConverterMysticalSkillMedium().convertTextWithMetaInfo(texts, conf);
            case CURSES, ELFENSONGS, MELODIES, DANCES -> results = new DsaConverterMysticalSkillActivity().convertTextWithMetaInfo(texts, conf);
            case MYSTICAL_SKILL_VARIANTS ->
            {
                List<MysticalSkillRaw> msrs = new DsaConverterApplyChangesToMysticalSkills().convertTextWithMetaInfo(texts, conf);
                applyToBasis(msrs, conf);
                results = msrs;
            }
            case MYSTICAL_SKILL_COMMONNESS ->
            {
                Map<String, List<String>> commonness = new DsaConverterMsyticalSkillCommonness().convertTextWithMetaInfo(texts);
                applyToMysticalSkills(commonness);
            }
            default -> LOGGER.error("Unexpected value: %s", conf.topic);
        }
        return results;
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
        LOGGER.info("Apply to BASIS: " + conf.publication + " (" + conf.topic + ")");
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
        File fIn = new File(FILE_STRATEGY_2_RAW.replace(DOCUMENT, Publication.BASIS + "_" + topic));
        List<MysticalSkillRaw> spells = CsvHandler.readBeanFromFile(MysticalSkillRaw.class, fIn);
        List<MysticalSkillRaw> results = spells.stream().map(msr -> {
            Optional<MysticalSkillRaw> applicablveVariatnMsr = variants.stream().filter(v -> v.name.equals(msr.name)).findFirst();
            if (applicablveVariatnMsr.isPresent())
            {
                msr.variantsText = applicablveVariatnMsr.get().variantsText;
                msr.variant1 = applicablveVariatnMsr.get().variant1;
                msr.variant2 = applicablveVariatnMsr.get().variant2;
                msr.variant3 = applicablveVariatnMsr.get().variant3;
                msr.variant4 = applicablveVariatnMsr.get().variant4;
                msr.variant5 = applicablveVariatnMsr.get().variant5;
            } else
            {
                LOGGER.error("Für das Topic (%s)  (%s) wurden keine Varianten gefunden.", topic, msr.name);
            }
            return msr;
        }).collect(Collectors.toList());

        CsvHandler.writeBeanToUrl(fIn, results);
    }

    private static void logAnalysisForPublication(String publication, List<TextWithMetaInfo> rawList)
    {
        String fileName = FILE_PDF_2_TEXT.replace(DOCUMENT, publication);
        File output = new File(fileName);
        CsvHandler.writeBeanToUrl(output, rawList);
    }

}

