package de.pho.dsapdfreader.tools.csv;

public class DsaStringCleanupTool
{
    private DsaStringCleanupTool()
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

    public static String cleanupString(String text)
    {
        return text
            .replaceAll("\\h", " ") // replace non breaking white space with white space
            .replaceAll("(?<=[a-zßöäü])-(?=[a-zßöäü])", "") // replace "-" between lower chars with ""
            .replaceAll("(?<=[a-zßöäü]) -(?=[a-zßöäü])", "") // replace " -" between lower chars with ""
            .replace("> -", ">-") // case: "<i>Sinnesschärfe</i> -Probe"
            .replaceAll("^ :|^:", "") // case: ": Sinnesschärfe-Probe"
            .replaceAll("\\s\\s", " ") // replace any two blanks with one
            .replace(" –", "")
            .trim();
    }
}
