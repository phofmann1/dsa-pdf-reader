package de.pho.dsapdfreader.tools.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class CsvHandler
{
    private CsvHandler()
    {
    }
    private static final char SEPARATOR = ';';
    private static final Logger LOGGER = LogManager.getLogger();

    public static <T> List<T> readBeanFromFile(Class<T> clazz, File f)
    {
        return readBeanFromPath(clazz, f.toPath());
    }

    public static <T> List<T> readBeanFromPath(Class<T> clazz, Path p)
    {
        try (Reader reader = Files.newBufferedReader(p, StandardCharsets.UTF_8))
        {
            return readBeanFromReader(clazz, reader);
        } catch (IOException e)
        {
            if (p != null) LOGGER.error(p);
            LOGGER.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }


    public static <T> List<T> readBeanFromUrl(Class<T> clazz, URL url)
    {
        try (Reader reader = Files.newBufferedReader(Path.of(url.toURI()), StandardCharsets.UTF_8))
        {

            return readBeanFromReader(clazz, reader);
        } catch (IOException | URISyntaxException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public static <T> List<T> readBeanFromReader(Class<T> clazz, Reader reader)
    {
        List<T> returnValue;
        try
        {
            CsvToBean<T> cb = new CsvToBeanBuilder(reader)
                .withType(clazz)
                .withSeparator(SEPARATOR)
                .build();
            returnValue = cb.parse();
            reader.close();
            return returnValue;
        } catch (IOException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public static void writeBeanToUrl(File output, List list)
    {
        try
        {

            // Ensure the parent directory exists File
            File parentDir = output.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            Writer writer = new FileWriter(output, StandardCharsets.UTF_8);
            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer)
                .withSeparator(SEPARATOR)
                .withApplyQuotesToAll(false)
                .build();

            sbc.write(list);
            writer.flush();
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e)
        {
            LOGGER.error(e.getMessage(), e);
        }

    }
}
