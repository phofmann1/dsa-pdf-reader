package de.pho.dsapdfreader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.time.Duration;
import java.time.Instant;
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
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillMedium;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillSmall;
import de.pho.dsapdfreader.dsaconverter.strategies.DsaConverterStrategy;
import de.pho.dsapdfreader.pdf.PdfReader;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaPdfReaderMain
{

    private static final String STRATEGY_PACKAGE = DsaConverterStrategy.class.getPackageName() + ".";
    private static List<TopicConfiguration> configs = null;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Logger LOGGER_ANALYSE = LogManager.getLogger("analyseLogger");
    private static final Logger LOGGER_FILE_MS_SMALL = LogManager.getLogger("exportLoggerMsSmall");
    private static final Logger LOGGER_FILE_MS_MEDIUM = LogManager.getLogger("exportLoggerMsMedium");

    public static void main(String[] args)
    {
        LOGGER.debug("----------------------------------");
        LOGGER.debug("start");
        Instant start = Instant.now();

        LOGGER.debug("init config");
        initConfig();
        LOGGER_FILE_MS_SMALL.info("publication;name;topic;duration;range;targetCategory;feature;description");
        LOGGER_FILE_MS_MEDIUM.info("publication;name;check;topic;castingDuration;duration;range;targetCategory;cost;commonness;feature;remarks;advancementCategory;description;effect;QS 1;QS 2;QS 3;QS 4;QS 5;QS 6;additionl infos");

        configs.stream()
            .filter(c -> c != null && c.active)
            .forEach(conf -> {
                LOGGER.debug("----------------------------------");
                LOGGER.debug("Config verarbeiten: " + conf);

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
                    LOGGER.debug("File einlesen: " + path + "/" + conf.pdfName);
                    File f = new File(path + "/" + conf.pdfName);
                    Map<Integer, List<TextWithMetaInfo>> resultListForPage = PdfReader.convertToText(f, conf);

                    LOGGER.debug("applyStrategies");

                    resultListForPage = applyStrategies(resultListForPage, conf);

                    logAnalysisForPage(conf.publication, resultListForPage);


                    LOGGER.debug("flatten map to list");
                    List<TextWithMetaInfo> resultList = resultListForPage.values().stream()
                        .map(e -> e.toArray(new TextWithMetaInfo[e.size()]))
                        .flatMap(Stream::of)
                        .collect(Collectors.toList());

                    LOGGER.debug("remove lines with only - from list");
                    resultList = resultList.stream().filter(t -> !t.text.trim().equals("-")).collect(Collectors.toList());

                    parseResult(resultList, conf);

                } catch (IOException | NullPointerException e)
                {
                    LOGGER.error(e.getMessage(), e);
                }

                LOGGER.debug("----------------------------------");
            });
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

    private static void parseResult(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
    {
        if (conf.topic == null)
        {
            LOGGER.error("Das Topic in der Konfiguration konnte nicht interpretiert werden oder ist nicht gesetzt.");
        }
        switch (conf.topic)
        {
            case BLESSINGS, TRICKS -> logMsSmallList(new DsaConverterMysticalSkillSmall().convertTextWithMetaInfo(resultList, conf), conf.publication);
            case SPELLS, LITURGIES, RITUALS, CEREMONIES -> logMsMediumList(new DsaConverterMysticalSkillMedium().convertTextWithMetaInfo(resultList, conf), conf.publication);
            default -> LOGGER.error("Unexpected value: " + conf.topic);
        }
    }

    private static void logMsMediumList(List<MysticalSkillMedium> msList, String publication)
    {
        msList.forEach(t -> {
            String msg = publication + ";" +
                t.name + ";" +
                t.check + ";" +
                t.topic + ";" +
                t.castingDuration + ";" +
                t.duration + ";" +
                t.range + ";" +
                t.targetCategory + ";" +
                t.cost + ";" +
                t.commonness + ";" +
                t.feature + ";" +
                t.remarks + ";" +
                t.advancementCategory + ";" +
                t.description + ";" +
                t.effect + ";" +
                (t.qs1 == null ? "" : t.qs1) + ";" +
                (t.qs2 == null ? "" : t.qs2) + ";" +
                (t.qs3 == null ? "" : t.qs3) + ";" +
                (t.qs4 == null ? "" : t.qs4) + ";" +
                (t.qs5 == null ? "" : t.qs5) + ";" +
                (t.qs6 == null ? "" : t.qs6) + ";" +
                (t.furtherInformation == null ? "" : t.furtherInformation) +
                "";
            LOGGER_FILE_MS_MEDIUM.info(msg);
        });
    }

    private static void logMsSmallList(List<MysticalSkillSmall> msList, String publication)
    {
        msList.forEach(t -> {
            String msg = publication + ";" +
                t.name + ";" +
                t.topic + ";" +
                t.duration + ";" +
                t.range + ";" +
                t.targetCategory + ";" +
                t.feature + ";" +
                t.description;
            LOGGER_FILE_MS_SMALL.info(msg);

        });

    }

    private static void logAnalysisForPage(String publication, Map<Integer, List<TextWithMetaInfo>> resultTextPerPage)
    {
        LOGGER_ANALYSE.info("publication;page;isBold;isItalic;size;font;text");
        resultTextPerPage.forEach((k, v) -> logAnalysis(publication, v, k.intValue()));
    }


    private static void logAnalysis(String publication, List<TextWithMetaInfo> resultTexts, int... page)
    {
        String pageString = page == null ? "" : page[0] + ";";
        if (page == null)
            LOGGER_ANALYSE.info("publication;isBold;isItalic;size;font;text");

        resultTexts.forEach(t -> LOGGER_ANALYSE.info(
            publication + ";"
                + pageString
                + convertBooleanForExcel(t.isBold) + ";"
                + convertBooleanForExcel(t.isItalic) + ";"
                + t.size + ";"
                + t.font + ";"
                + t.text));
    }


    private static String convertBooleanForExcel(boolean b)
    {
        return b ? "WAHR" : "FALSCH";
    }


}

