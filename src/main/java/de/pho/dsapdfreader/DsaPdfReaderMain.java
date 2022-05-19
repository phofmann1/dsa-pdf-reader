package de.pho.dsapdfreader;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import de.pho.dsapdfreader.config.ConfigurationInitializer;
import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.dsa.DsaConverter;
import de.pho.dsapdfreader.dsa.model.Trick;
import de.pho.dsapdfreader.pdf.PdfReader;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaPdfReaderMain
{

    private static List<TopicConfiguration> configs = null;
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Logger LOGGER_FILE = LogManager.getLogger("fileLogger");
    public static final Logger LOGGER_ANALYSE = LogManager.getLogger("analyseLogger");

    public static void main(String[] args)
    {
        LOGGER.debug("----------------------------------");
        LOGGER.debug("start");
        Instant start = Instant.now();

        LOGGER.debug("init config");
        initConfig();
        LOGGER_ANALYSE.info("publication;isBold;isItalic;size;font;text");
        LOGGER_FILE.info("Publication;name;duration;range;targetCategory;feature;description");
        configs.stream()
            .filter(c -> c != null)
            .forEach(conf -> {
                LOGGER.debug("----------------------------------");
                LOGGER.info("Config verarbeiten: " + conf.getPublication());
                LOGGER.debug("reading file: " + conf.getPdfName());
                try
                {
                    File f = new File(DsaPdfReaderMain.class.getClassLoader().getResource(conf.getPdfName()).getFile());

                    List<TextWithMetaInfo> resultList = PdfReader.convertToText(f, conf.getFromPage(), conf.getUntilPage(), conf.getPublication());


                     parseResult(resultList, conf);


                } catch (IOException e)
                {
                    LOGGER.error(e);
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
        configs  = ConfigurationInitializer.readTopicConfigurations();
    }

    private static List parseResult(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
    {
        switch (conf.getTopic())
        {
            case TRICKS -> {
                logTrickList(DsaConverter.convertTextWithMetaInfoToTrick(resultList, conf), conf.getPublication());
            }
            case SPELLS -> {
                //return parseResultToSpells(result);
            }
            case RITUALS -> {
            }
        }
        return new ArrayList();
    }

    private static void logTrickList(List<Trick> trickList, String publication)
    {
        trickList.stream()
            .filter(t -> t.name != null && t.targetCategory != null && t.feature != null && t.duration != null && t.range != null && t.description != null)
            .filter(t -> !t.name.isEmpty() && !t.targetCategory.isEmpty() && !t.feature.isEmpty() && !t.duration.isEmpty() && !t.range.isEmpty() && !t.description.isEmpty())
            .forEach(t -> {
                String msg = publication + ";"
                    + t.name + ";" +
                    t.duration + ";" +
                    t.range + ";" +
                    t.targetCategory + ";" +
                    t.feature + ";" +
                    t.description;
                LOGGER_FILE.info(msg);

            });

    }


    private static List<Trick> parseResultToSpells(String result)
    {
        LOGGER.debug("parseResultToSpell");
        return null;
    }

}

