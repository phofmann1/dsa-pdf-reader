package de.pho.dsapdfreader.config;

import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.DsaPdfReaderMain;
import de.pho.dsapdfreader.tools.csv.CsvHandler;

public class ConfigurationInitializer
{
    private ConfigurationInitializer() {
    }

    public static final Logger LOGGER = LogManager.getLogger();

    public static List<TopicConfiguration> readTopicConfigurations() {
        URL url = DsaPdfReaderMain.class.getClassLoader().getResource("topic-conf.csv");
        return CsvHandler.readBeanFromUrl(TopicConfiguration.class, url);
    }
}
