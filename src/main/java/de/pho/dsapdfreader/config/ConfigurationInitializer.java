package de.pho.dsapdfreader.config;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import de.pho.dsapdfreader.DsaPdfReaderMain;

public class ConfigurationInitializer
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static List<TopicConfiguration> readTopicConfigurations()
    {
        List<TopicConfiguration> returnValue = new ArrayList<>();

        try
        {
            URL url = DsaPdfReaderMain.class.getClassLoader().getResource("topic-conf.csv");

            Reader reader = Files.newBufferedReader(Path.of(url.toURI()));
            CsvToBean cb = new CsvToBeanBuilder(reader)
                .withType(TopicConfiguration.class)
                .withSeparator(';')
                .build();
            returnValue = cb.parse();
            reader.close();
        } catch (IOException | URISyntaxException e)
        {
            LOGGER.error(e);
        }
        return returnValue;
    }
}
