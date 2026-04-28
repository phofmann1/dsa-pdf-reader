package de.pho.dsapdfreader.book.analysis.structured;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Externe Mapping-Konfig fuer Naming-Diskrepanzen zwischen PDF und ng-dsa.
 * <p>
 * Manchmal weicht der PDF-Heading-Name vom ng-dsa-Namen ab — z.B.:
 * <ul>
 *   <li>{@code Heilungsspezialgebiet} (PDF) → {@code Heilungsspezialist} (ng-dsa)
 *       mit Variant-Format {@code "{master} ({variant})"}</li>
 *   <li>{@code Schnellladen} (nach Klammer-Strip) → {@code Schnellladen} (ng-dsa)
 *       mit Variant-Format {@code "{master} {variant}"}</li>
 * </ul>
 * Format der JSON-Datei (eine pro Buch):
 * <pre>
 * {
 *   "Heilungsspezialgebiet": {
 *     "ngDsaMaster": "Heilungsspezialist",
 *     "variantFormat": "{master} ({variant})"
 *   },
 *   "Schnellladen": {
 *     "ngDsaMaster": "Schnellladen",
 *     "variantFormat": "{master} {variant}"
 *   }
 * }
 * </pre>
 * Wenn fuer einen Master-Namen kein Eintrag vorhanden ist, wird der
 * PDF-Master-Name unveraendert verwendet.
 */
public class NameMapping {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Map<String, Entry> entries;
    /** Block-Namen die explizit aus dem ability-Output ausgeschlossen werden sollen. */
    private final java.util.Set<String> ignoreNames = new java.util.HashSet<>();

    public NameMapping(Map<String, Entry> entries) {
        this.entries = entries == null ? new LinkedHashMap<>() : entries;
    }

    public static NameMapping empty() {
        return new NameMapping(new LinkedHashMap<>());
    }

    @SuppressWarnings("unchecked")
    public static NameMapping load(Path file) throws IOException {
        if (file == null || !Files.isRegularFile(file)) return empty();
        Map<String, Object> raw = MAPPER.readValue(file.toFile(),
                MAPPER.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class));
        // Spezial-Key "_ignore": Liste von Block-Namen die als ability ignoriert werden
        java.util.List<String> ignoreList = (java.util.List<String>) raw.remove("_ignore");
        Map<String, Entry> entries = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : raw.entrySet()) {
            entries.put(e.getKey(), MAPPER.convertValue(e.getValue(), Entry.class));
        }
        NameMapping m = new NameMapping(entries);
        if (ignoreList != null) m.ignoreNames.addAll(ignoreList);
        return m;
    }

    public Entry findFor(String pdfMaster) {
        if (pdfMaster == null) return null;
        return entries.get(pdfMaster);
    }

    public boolean isIgnored(String blockName) {
        return blockName != null && ignoreNames.contains(blockName);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Entry {
        public String ngDsaMaster;
        public String variantFormat;
        /** Explizite Variant-Liste — ueberschreibt die automatische Erkennung. */
        public java.util.List<String> explicitVariants;
        /** Map fuer Variant-Renamings (PDF-Schreibweise → ng-dsa-Schreibweise). */
        public java.util.Map<String, String> variantRename;

        public Entry() {}
        public Entry(String ngDsaMaster, String variantFormat) {
            this.ngDsaMaster = ngDsaMaster;
            this.variantFormat = variantFormat;
        }

        public String ngDsaMaster() { return ngDsaMaster; }
        public String variantFormat() { return variantFormat; }
        public java.util.List<String> explicitVariants() { return explicitVariants; }
        public java.util.Map<String, String> variantRename() { return variantRename; }
    }
}
