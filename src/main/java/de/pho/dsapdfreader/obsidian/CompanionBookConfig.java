package de.pho.dsapdfreader.obsidian;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Zuordnung von Kompendien/Ergaenzungsbaenden zum Hauptwerk.
 * Liefert den Vault-Ordnernamen und ein Image-Prefix fuer Kollisionsvermeidung.
 */
public class CompanionBookConfig
{
    private static final Map<String, String[]> MAPPINGS = new LinkedHashMap<>();

    static
    {
        // Format: bookDirName-Prefix -> { vaultFolder, imagePrefix }
        map("Kompendium der Winterwacht", "Die Winterwacht", "komp_");
        map("Kompendium des Wolfsfrosts", "Der Wolfsfrost", "komp_");
        map("Kompendium der Echsensumpfe", "Die Echsensümpfe", "komp_");
        map("Die Streitenden Konigreiche - Beilst", "Die Streitenden Königreiche", "beilstatt_");
        map("Die Streitenden Konigreiche - Nostri", "Die Streitenden Königreiche", "nostria_");
        map("Die Sonnenkuste - Glaube Macht", "Die Sonnenküste", "glaube_");
        map("Die dampfenden Dschungel - Glaube", "Die Dampfenden Dschungel", "glaube_");
        map("Havena - Die grosse Flut", "Havena", "flut_");
        map("Glaube Macht & Heldenmut Havena", "Havena", "glaube_");
        map("Kompendium der Goldenen Kaiserzeit", "Kompendium der Goldenen Kaiserzeit", "");
    }

    private static void map(String prefix, String vaultFolder, String imagePrefix)
    {
        MAPPINGS.put(prefix, new String[]{vaultFolder, imagePrefix});
    }

    /**
     * Prueft ob ein Buchverzeichnis ein Kompendium/Addon ist.
     * @return Vault-Ordnername des Hauptwerks, oder null wenn eigenstaendig
     */
    public static String getVaultFolder(String bookDirName)
    {
        for (Map.Entry<String, String[]> entry : MAPPINGS.entrySet())
        {
            if (bookDirName.startsWith(entry.getKey()))
            {
                return entry.getValue()[0];
            }
        }
        return null;
    }

    /**
     * Gibt das Image-Prefix fuer Kollisionsvermeidung zurueck.
     */
    public static String getImagePrefix(String bookDirName)
    {
        for (Map.Entry<String, String[]> entry : MAPPINGS.entrySet())
        {
            if (bookDirName.startsWith(entry.getKey()))
            {
                return entry.getValue()[1];
            }
        }
        return "";
    }

    /**
     * Erzeugt einen huebschen Vault-Ordnernamen mit Umlauten aus dem Verzeichnisnamen.
     */
    public static String displayName(String bookDirName)
    {
        // Nummer in Klammern entfernen
        String name = bookDirName.replaceAll("\\s*\\(\\d+\\)\\s*$", "").trim();

        // Bekannte Umlaut-Ersetzungen
        name = name.replace("Wustenreich", "Wüstenreich");
        name = name.replace("Sonnenkuste", "Sonnenküste");
        name = name.replace("Siebenwindkuste", "Siebenwindküste");
        name = name.replace("Konigreiche", "Königreiche");
        name = name.replace("Echsensumpfe", "Echsensümpfe");
        name = name.replace("Flusslande", "Flusslande"); // kein Umlaut
        name = name.replace("Dampfenden", "Dampfenden");
        name = name.replace("dampfenden", "Dampfenden");
        name = name.replace("Goetter", "Götter");
        name = name.replace("grosse", "große");
        name = name.replace("Dämonen", "Dämonen");

        return name;
    }
}
