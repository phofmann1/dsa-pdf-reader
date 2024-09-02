package de.pho.dsapdfreader.dsaconverter.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import de.pho.dsapdfreader.exporter.model.enums.CultureKey;

public class CsvCustomConvertEnumCultureList extends AbstractBeanField<List<CultureKey>, String> {
    public static final String SEPARATOR = "|";
    private static final String SEPARATOR_REGEX = "\\" + SEPARATOR;
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected List<CultureKey> convert(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null; // Handle null or empty string
        }

        // Convert CSV string back to List<Color>
        return Arrays.stream(value.split(SEPARATOR_REGEX))
            .map(String::trim)
            .map(CultureKey::valueOf)
            .collect(Collectors.toList());
    }

    @Override
    protected String convertToWrite(Object value) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        if (value == null) {
            return ""; // Handle null case
        }

        @SuppressWarnings("unchecked")
        List<CultureKey> colors = (List<CultureKey>) value;

        // Convert List<Color> to a CSV string
        return colors.stream()
            .map(CultureKey::name)
            .collect(Collectors.joining(SEPARATOR));
    }
}
