package de.pho.dsapdfreader.dsaconverter.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import de.pho.dsapdfreader.exporter.model.MysticalSkillVariant;

public class MysticalSkillVariantConvert extends AbstractBeanField
{
    public static final String MSV_SEPARATOR = "|";
    private static final String MSV_SEPARATOR_REGEX = "\\" + MSV_SEPARATOR;
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected MysticalSkillVariant convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException
    {
        MysticalSkillVariant returnValue = null;
        if (s != null && !s.isEmpty())
        {
            String[] values = s.split(MSV_SEPARATOR_REGEX);
            if (values.length == 5)
            {
                returnValue = new MysticalSkillVariant();
                returnValue.name = values[0];
                returnValue.minLevel = Integer.valueOf(values[1]);
                returnValue.ap = Integer.valueOf(values[2]);
                returnValue.description = values[3];
                returnValue.requiredVariantName = (values[4] != null && !values[4].isEmpty() && !values[4].equalsIgnoreCase("null")) ? values[4] : null;
            } else
            {
                LOGGER.error("Not a valid Variant string: " + s);
            }
        }
        return returnValue;
    }
}