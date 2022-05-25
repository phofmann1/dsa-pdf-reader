package de.pho.dsapdfreader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.ConfigurationInitializer;
import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillMedium;
import de.pho.dsapdfreader.dsaconverter.DsaConverterMysticalSkillSmall;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillMedium;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillSmall;
import de.pho.dsapdfreader.pdf.PdfReader;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaPdfReaderMain
{

    private static List<TopicConfiguration> configs = null;
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Logger LOGGER_FILE_MS_SMALL = LogManager.getLogger("exportLoggerMsSmall");
    private static final Logger LOGGER_FILE_MS_MEDIUM = LogManager.getLogger("exportLoggerMsMedium");
    public static final Logger LOGGER_ANALYSE = LogManager.getLogger("analyseLogger");

    public static void main(String[] args)
    {
        LOGGER.debug("----------------------------------");
        LOGGER.debug("start");
        Instant start = Instant.now();

        LOGGER.debug("init config");
        initConfig();
        LOGGER_FILE_MS_SMALL.info("publication;name;topic;duration;range;targetCategory;feature;description");
        LOGGER_FILE_MS_MEDIUM.info("publication;name;check;topic;castingDuration;duration;range;targetCategory;cost;feature;remarks;advancementCategory;description;effect");

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

                    List<TextWithMetaInfo> resultList = PdfReader.convertToText(f, conf.fromPage, conf.untilPage, conf.publication);
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

    private static void initConfig()
    {
        configs = ConfigurationInitializer.readTopicConfigurations();
    }

    private static void parseResult(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
    {
        if (conf.topic == null)
        {
            LOGGER.error("Das Topic in der Konfiguration konnte nicht interpretiert werden");
        }
        switch (conf.topic)
        {
            case BLESSINGS, TRICKS -> logMsSmallList(new DsaConverterMysticalSkillSmall().convertTextWithMetaInfo(resultList, conf), conf.publication);
            case SPELLS, LITURGIES -> logMsMediumList(new DsaConverterMysticalSkillMedium().convertTextWithMetaInfo(resultList, conf), conf.publication);
            case RITUALS, CEREMONIES -> {
            }
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
                (t.qs6 == null ? "" : t.qs6);

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

}

