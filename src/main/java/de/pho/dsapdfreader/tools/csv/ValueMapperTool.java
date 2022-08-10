package de.pho.dsapdfreader.tools.csv;

public class ValueMapperTool
{
    private ValueMapperTool()
    {
    }

    public static String mapStringToEnumName(String text)
    {
        return text
            .trim()
            .toUpperCase()
            .replaceAll(" ", "_")
            .replaceAll("Ä", "AE")
            .replaceAll("Ö", "OE")
            .replaceAll("Ü", "UE")
            .replaceAll("__", "_")
            .replaceAll("ß", "SS")
            .replaceAll("&", "UND")
            .replaceAll("!", "")
            .replaceAll("\\(", "")
            .replaceAll("\\)", "");
    }
}
