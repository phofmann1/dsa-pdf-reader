package de.pho.dsapdfreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.ConfigurationInitializer;
import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.generated.topicstrategymapping.TopicStrategies;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillMedium;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillSmall;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.DsaConverterStrategy;
import de.pho.dsapdfreader.exporter.LoadToMysticalSkill;
import de.pho.dsapdfreader.exporter.model.MysticalSkill;
import de.pho.dsapdfreader.pdf.PdfReader;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;
import de.pho.dsapdfreader.tools.csv.CsvHandler;

public class DsaPdfReaderMain
{

    private static final String STRATEGY_PACKAGE = DsaConverterStrategy.class.getPackageName() + ".";
    private static final String PATH_BASE = "C:\\develop\\project\\dsa-pdf-reader\\target\\export\\";
    private static final String FILE_RAW_MYSTICAL_SKILLS = PATH_BASE + "raw_mystical_skills.csv";
    private static final String FILE_ANALYSE_PDF_TEXT = PATH_BASE + "<publication>_analyse.csv";

    private static List<TopicConfiguration> configs = null;
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args)
    {
        LOGGER.debug("----------------------------------");
        LOGGER.debug("start");
        Instant start = Instant.now();

        boolean isParse = Arrays.asList(args).contains("parse");
        boolean isConvertRaws = Arrays.asList(args).contains("convertRaw");

        isParse = isParse || !(isParse || isConvertRaws);
        isConvertRaws = isConvertRaws || !(isParse || isConvertRaws);

        if (isParse) parsePdfs();
        if (isConvertRaws) convertRaws();

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);

        LOGGER.debug(timeElapsed.toString()
            .substring(2)
            .replaceAll("(\\d[HMS])(?!$)", "$1 ")
            .toLowerCase());

        LOGGER.debug("----------------------------------");
        LOGGER.debug("finish");
        LOGGER.debug("----------------------------------");

    }

    private static void convertRaws()
    {
        File rawMysticalSkillsFile = new File(FILE_RAW_MYSTICAL_SKILLS);

        if (rawMysticalSkillsFile.exists() && !rawMysticalSkillsFile.isDirectory())
        {
            List<MysticalSkillRaw> rawMysticalSkills = CsvHandler.readBeanFromFile(MysticalSkillRaw.class, rawMysticalSkillsFile);
            List<MysticalSkill> mysticalSkills = rawMysticalSkills.stream().map(msm -> LoadToMysticalSkill.migrate(msm)).collect(Collectors.toList());
        }

    }

    private static String readFile(String file) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try
        {
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally
        {
            reader.close();
        }
    }

    private static void parsePdfs()
    {
        LOGGER.debug("init config");
        initConfig();
        LOGGER.debug("----------------------------------");

        List results = new ArrayList();
        configs.stream()
            .filter(c -> c != null && c.active)
            .forEach(conf -> {
                LOGGER.debug("Config verarbeiten: " + conf.publication + " (" + conf.topic + ")");

                try
                {

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
                    File f = new File(path + "/" + conf.pdfName);
                    Map<Integer, List<TextWithMetaInfo>> resultListForPage = PdfReader.convertToText(f, conf);

                    LOGGER.debug("applyStrategies");
                    resultListForPage = applyStrategies(resultListForPage, conf);

                    List<TextWithMetaInfo> resultList = resultListForPage.values().stream()
                        .map(e -> e.toArray(new TextWithMetaInfo[e.size()]))
                        .flatMap(Stream::of)
                        .collect(Collectors.toList());

                    LOGGER.debug("Log analysis fo publication <" + conf.publication + ">");
                    logAnalysisForPublication(conf.publication, resultList);

                    //cleanup list from "-"
                    resultList = resultList.stream().filter(t -> !t.text.trim().equals("-")).collect(Collectors.toList());

                    LOGGER.debug("parse results to raw");
                    results.addAll(parseResult(resultList, conf));

                } catch (IOException | NullPointerException e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
                LOGGER.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            });


        LOGGER.debug("write results to File by type");
        writeCsvFilesByType(results);
    }

    private static void writeCsvFilesByType(List<Object> results)
    {
        List<MysticalSkillRaw> rawMysticalSkills = results.stream()
            .filter(MysticalSkillRaw.class::isInstance)
            .map(MysticalSkillRaw.class::cast)
            .collect(Collectors.toList());

        File output = new File(FILE_RAW_MYSTICAL_SKILLS);
        CsvHandler.writeBeanToUrl(output, rawMysticalSkills);
    }

    private static Map<Integer, List<TextWithMetaInfo>> applyStrategies(Map<Integer, List<TextWithMetaInfo>> resultListForPage, TopicConfiguration conf)
    {
        if (conf.strategyMapping != null && !conf.strategyMapping.isEmpty())
        {
            TopicStrategies topicMappings = unmarshall(conf.strategyMapping);
            if (topicMappings != null)
            {
                Map<Integer, List<TextWithMetaInfo>> returnValue = resultListForPage;

                for (TopicStrategies.Strategy s : topicMappings.getStrategy())

                {
                    try
                    {
                        DsaConverterStrategy currentStrategy = (DsaConverterStrategy) Class.forName(STRATEGY_PACKAGE + s.getStrategyClass().trim())
                            .getDeclaredConstructor()
                            .newInstance();
                        returnValue = currentStrategy.applyStrategy(returnValue, s.getParams().getParameter(), s.getName());
                    } catch (InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException
                        | NoSuchMethodException
                        | ClassNotFoundException e)
                    {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                return returnValue;
            }
        }

        return resultListForPage;
    }

    private static TopicStrategies unmarshall(String fileName)
    {
        LOGGER.debug("initialize Strategy mapping: " + fileName);
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
        if (conf.topic == null)
        {
            LOGGER.error("Das Topic in der Konfiguration konnte nicht interpretiert werden oder ist nicht gesetzt.");
        }

        List results = null;
        switch (conf.topic)
        {
            case BLESSINGS, TRICKS -> results = new DsaConverterMysticalSkillSmall().convertTextWithMetaInfo(texts, conf);
            case SPELLS, LITURGIES, RITUALS, CEREMONIES -> results = new DsaConverterMysticalSkillMedium().convertTextWithMetaInfo(texts, conf);
            default -> LOGGER.error("Unexpected value: " + conf.topic);
        }
        return results;
    }

    private static void logAnalysisForPublication(String publication, List<TextWithMetaInfo> rawList)
    {
        String fileName = FILE_ANALYSE_PDF_TEXT.replaceAll("<publication>", publication);
        File output = new File(fileName);
        CsvHandler.writeBeanToUrl(output, rawList);
    }

}

