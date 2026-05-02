package de.pho.dsapdfreader.book.analysis.structured;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Expandiert Komposit-Sonderfertigkeiten in N Sub-Bloecke.
 * <p>
 * In DSA-Buechern stehen viele SFs als ein Master-Block, dessen Klammer-Suffix
 * eine Variant-Kategorie kennzeichnet (z.B. {@code Schnellladen (Kampftechnik)}).
 * Die konkreten Varianten kommen aus:
 * <ol>
 *   <li>einem fields-Eintrag mit dem Kategorie-Namen
 *       (z.B. {@code kampftechnik: "Armbrueste, Blasrohre, Boegen"})</li>
 *   <li>dem AP-Wert-Feld, das eine Liste mit unterschiedlichen Kosten pro
 *       Variant enthaelt (z.B. {@code "Armbrueste: 5 AP; Boegen: 20 AP"})</li>
 *   <li>einem Body-Text-Pattern wie {@code "Folgende Anwendungsgebiete koennen
 *       erworben werden: Amputieren, Chirurgie, ..."} oder {@code "im Talent
 *       A, B oder C"}</li>
 * </ol>
 * Erkennt die Variant-Liste in dieser Reihenfolge (1 → 2 → 3) und erzeugt fuer
 * jede Variant einen eigenen Block. Wenn nichts gefunden: Original-Block
 * bleibt unveraendert.
 */
public class CompositeExpander {

    /** Operational Type-Marker — werden vor der Komposit-Erkennung gestrippt. */
    private static final Set<String> OPERATIONAL_TYPES = Set.of(
            "passiv", "aktiv", "basis",
            "spezialmanöver", "spezialmanover",
            "basismanöver", "basismanover",
            "manöver", "manover",
            // Klassen-Marker fuer SFs/Zauber-Disambiguierung
            // ng-dsa nutzt diese Suffixe nicht — wir strippen sie.
            "zauber", "liturgie", "zeremonie", "ritual");

    /**
     * Klammer-Inhalt, der eine Variant-Kategorie markiert. Mapping zur
     * Field-Bezeichnung in {@link StructuredBlock#fields}.
     */
    private static final Map<String, String> VARIANT_CATEGORIES = Map.ofEntries(
            Map.entry("kampftechnik",      "kampftechnik"),
            Map.entry("anwendungsgebiet",  "anwendungsgebiet"),
            Map.entry("talent",            "talent"),
            Map.entry("talente",           "talent"),
            Map.entry("sphäre",            "sphäre"),
            Map.entry("sphare",            "sphäre"),
            Map.entry("tradition",         "tradition"),
            Map.entry("gattung",           "gattung"),
            Map.entry("wesenheit",         "wesenheit"),
            Map.entry("spezies",           "spezies"),
            Map.entry("einzelne spezies",  "spezies"),
            Map.entry("giftart",           "giftart"),
            Map.entry("schrift",           "schrift"),
            Map.entry("element",           "element")
    );

    /** Pattern: "Folgende ... koennen erworben werden: A, B, C, D" */
    private static final Pattern P_BODY_LIST_FOLGENDE = Pattern.compile(
            "(?:Folgende|folgende)\\s+\\w+\\s+(?:koennen|können|kann)\\s+(?:erworben|gewählt|gewaehlt)\\s+werden\\s*:?\\s*([^.]+)\\.",
            Pattern.CASE_INSENSITIVE);

    /** Pattern: "verschiedene X gewaehlt werden: A, B, C, D" */
    private static final Pattern P_BODY_LIST_VERSCHIEDENE = Pattern.compile(
            "verschiedene\\s+\\w+\\s+(?:gewählt|gewaehlt|gewahlt)\\s+werden\\s*:?\\s*([^.]+)\\.",
            Pattern.CASE_INSENSITIVE);

    /** Pattern: "im Talent A, B oder C" / "im Talent A, B, C oder D" */
    private static final Pattern P_BODY_LIST_TALENT = Pattern.compile(
            "im\\s+Talent\\s+([A-ZÄÖÜ][a-zA-Zäöüß]+(?:,\\s*[A-ZÄÖÜ][a-zA-Zäöüß]+)*(?:\\s+oder\\s+[A-ZÄÖÜ][a-zA-Zäöüß]+)?)",
            Pattern.CASE_INSENSITIVE);

    /** Pattern fuer trailing Klammer-Marker in Heading-Namen. */
    private static final Pattern P_TRAILING_PAREN = Pattern.compile(
            "\\s*\\(([a-zA-ZäöüÄÖÜß][a-zA-ZäöüÄÖÜß\\s]{0,30})\\)\\s*$");

    /** Pattern: "VariantName: ... " (oder "VariantName N AP") in einer AP-Wert-/Voraussetzung-Liste. */
    private static final Pattern P_AP_VARIANT_TOKEN = Pattern.compile(
            "([A-ZÄÖÜ][a-zA-ZäöüÄÖÜß\\-]+(?:\\s+[a-zA-ZäöüÄÖÜß][a-zA-ZäöüÄÖÜß\\-]+)?)\\s*:");

    /** Pattern: "X und Y:" → beide als Variants registrieren ("Amputieren und Verbrennungen: 8 AP"). */
    private static final Pattern P_AP_AND_PAIR = Pattern.compile(
            "([A-ZÄÖÜ][a-zA-ZäöüÄÖÜß\\-]+)\\s+und\\s+([A-ZÄÖÜ][a-zA-ZäöüÄÖÜß\\-]+)\\s*:");

    /**
     * Versucht, einen Block zu expandieren. Liefert eine Liste der resultierenden
     * Bloecke (entweder die Sub-Bloecke oder eine Singleton-Liste mit dem Original).
     */
    public List<StructuredBlock> expand(StructuredBlock master, NameMapping mapping) {
        // 1. Operational Type-Marker iterativ wegstrippen ("(passiv)", "(aktiv)" am Ende)
        String name = master.name == null ? "" : master.name;
        while (true) {
            String stripped = stripOperational(name);
            if (stripped.equals(name)) break;
            name = stripped;
        }

        // 2. Variant-Kategorie aus Klammer-Suffix erkennen
        String variantCategory = null;
        Matcher m = P_TRAILING_PAREN.matcher(name);
        if (m.find()) {
            String inner = m.group(1).trim().toLowerCase(Locale.ROOT);
            if (VARIANT_CATEGORIES.containsKey(inner)) {
                variantCategory = VARIANT_CATEGORIES.get(inner);
                name = name.substring(0, m.start()).trim();
            }
        }

        // 3. Variants ermitteln — Reihenfolge: Field, AP-Wert, Body-Pattern.
        // AP-Wert-Variants werden auch ohne Klammer-Marker versucht (z.B. "Nachladespezialist"
        // hat keine Klammer, aber AP-Wert listet "Armbrueste: 10 AP; Boegen: 20 AP; ...").
        List<String> variants = null;
        if (variantCategory != null) {
            variants = variantsFromField(master, variantCategory);
        }
        if (variants == null || variants.size() < 2) {
            variants = variantsFromApWert(master);
        }
        if (variants == null || variants.size() < 2) {
            variants = variantsFromBodyPattern(master);
        }

        // 4. Mapping anwenden
        NameMapping.Entry mapped = mapping == null ? null : mapping.findFor(name);
        String masterDisplay = mapped != null && mapped.ngDsaMaster() != null
                ? mapped.ngDsaMaster() : name;
        String variantFormat = mapped != null && mapped.variantFormat() != null
                ? mapped.variantFormat()
                : "{master} {variant}";

        // Mapping kann explicitVariants liefern (ueberschreibt detektierte Variants —
        // z.B. fuer Nachladespezialist mit "alle Fernkampftechniken").
        if (mapped != null && mapped.explicitVariants() != null && !mapped.explicitVariants().isEmpty()) {
            variants = new ArrayList<>(mapped.explicitVariants());
        }

        // Variant-Renaming (Plural-/Schreibweisen-Anpassung an ng-dsa)
        if (variants != null && mapped != null && mapped.variantRename() != null) {
            List<String> renamed = new ArrayList<>(variants.size());
            for (String v : variants) {
                renamed.add(mapped.variantRename().getOrDefault(v, v));
            }
            variants = renamed;
        }

        // 5. Ergebnis
        if (variants != null && variants.size() >= 2) {
            List<StructuredBlock> result = new ArrayList<>();
            for (String v : variants) {
                StructuredBlock sub = copyOf(master);
                sub.name = variantFormat.replace("{master}", masterDisplay).replace("{variant}", v);
                result.add(sub);
            }
            return result;
        }

        // Kein Komposit: Master-Name normalisieren (operational stripped, mapping ggf.)
        master.name = masterDisplay;
        return List.of(master);
    }

    /** Strippt einen einzelnen operational Type-Marker am Ende ("(passiv)" etc.). */
    static String stripOperational(String name) {
        if (name == null) return "";
        Matcher m = P_TRAILING_PAREN.matcher(name);
        if (!m.find()) return name;
        String inner = m.group(1).trim().toLowerCase(Locale.ROOT);
        // Nur wenn es ein bekannter operational marker ist — variant marker bleiben dran
        if (OPERATIONAL_TYPES.contains(inner)) {
            return name.substring(0, m.start()).trim();
        }
        return name;
    }

    /** Liest Variants aus einem Field-Wert (Komma-getrennt). */
    private static List<String> variantsFromField(StructuredBlock b, String fieldKey) {
        if (b.fields == null) return null;
        String value = b.fields.get(fieldKey);
        if (value == null) return null;
        // Comma + " und " als Trenner. "alle Fernkampftechniken" → kein Composite.
        if (value.toLowerCase(Locale.ROOT).startsWith("alle ")) return null;
        return parseCommaList(value);
    }

    /** Liest Variants aus dem AP-Wert-Feld (z.B. "Armbrueste: 5 AP; Boegen: 20 AP"). */
    private static List<String> variantsFromApWert(StructuredBlock b) {
        if (b.fields == null) return null;
        String value = b.fields.get("ap-wert");
        if (value == null) value = b.fields.get("kosten");
        if (value == null) return null;
        // Wenn AP-Wert ein einzelner Pauschal-Wert ist (z.B. "5 Abenteuerpunkte"
        // oder "12 Abenteuerpunkte pro Giftart"), gibt's keine Variant-Liste.
        if (value.matches("^\\d+\\s*Abenteuerpunkt\\w*.*$")) return null;
        // Bindestrich-Trennungen aus Zeilenumbruch reparieren ("Kno- chenbrueche" → "Knochenbrueche")
        value = value.replaceAll("(\\w)-\\s+(\\w)", "$1$2");
        // Erst "X und Y:"-Paare extrahieren — beide bekommen denselben Wert
        Set<String> result = new LinkedHashSet<>();
        Matcher andMm = P_AP_AND_PAIR.matcher(value);
        while (andMm.find()) {
            String left = andMm.group(1).trim();
            String right = andMm.group(2).trim();
            if (!left.equalsIgnoreCase("Stufe") && !left.equalsIgnoreCase("alle anderen")) result.add(left);
            if (!right.equalsIgnoreCase("Stufe") && !right.equalsIgnoreCase("alle anderen")) result.add(right);
        }
        // Dann einzelne "Word:"-Tokens
        Matcher mm = P_AP_VARIANT_TOKEN.matcher(value);
        while (mm.find()) {
            String token = mm.group(1).trim();
            // Nicht jeden Doppelpunkt — manche sind Nachsaetze
            if (token.equalsIgnoreCase("Stufe") || token.startsWith("Stufe ")) continue;
            if (token.equalsIgnoreCase("alle anderen")) continue;
            // Mehrwort-Tokens wie "Wurfwaffen und alle anderen Fernkampftechniken"
            // sind Catch-all-Phrasen, kein einzelner Variant
            if (token.toLowerCase(Locale.ROOT).contains(" und alle anderen")) continue;
            // Erstes Wort vor " und " als eigene Variant nehmen
            if (token.toLowerCase(Locale.ROOT).contains(" und ")) {
                int p = token.toLowerCase(Locale.ROOT).indexOf(" und ");
                String first = token.substring(0, p).trim();
                if (first.length() > 1) result.add(first);
                continue;
            }
            result.add(token);
        }
        return result.size() >= 2 ? new ArrayList<>(result) : null;
    }

    /** Erkennt Body-Patterns wie "Folgende X koennen erworben werden: A, B, C". */
    private static List<String> variantsFromBodyPattern(StructuredBlock b) {
        if (b.bodyText == null || b.bodyText.isEmpty()) return null;
        String body = b.bodyText;

        for (Pattern p : List.of(P_BODY_LIST_FOLGENDE, P_BODY_LIST_VERSCHIEDENE, P_BODY_LIST_TALENT)) {
            Matcher mm = p.matcher(body);
            if (mm.find()) {
                List<String> list = parseCommaList(mm.group(1));
                if (list != null && list.size() >= 2) return list;
            }
        }
        return null;
    }

    /** Parst eine kommagetrennte Liste, unterstuetzt " und " / " oder " als letzten Trenner. */
    static List<String> parseCommaList(String text) {
        if (text == null) return null;
        // Nachgestelltes "..." abschneiden
        String t = text.trim();
        // " und " / " oder " durch Komma ersetzen
        t = t.replaceAll("\\s+(?:und|oder)\\s+", ", ");
        // Bindestrich-Trennungen (Zeilenumbruch-Artefakt) reparieren: "Dis- kusse" → "Diskusse"
        t = t.replaceAll("(\\w)-\\s+(\\w)", "$1$2");

        List<String> result = new ArrayList<>();
        for (String token : t.split(",")) {
            String tok = token.trim();
            // Leading "im X" oder "der" entfernen
            tok = tok.replaceAll("^(im|der|die|das|den)\\s+\\w+\\s+", "");
            tok = tok.replaceAll("[.;]+$", "").trim();
            if (tok.isEmpty()) continue;
            // Capitalized first letter (sonst: Junk)
            if (!Character.isUpperCase(tok.charAt(0))) continue;
            // Sehr lange Tokens (>= 50 chars) sind eher Saetze als Variants
            if (tok.length() > 50) continue;
            result.add(tok);
        }
        return result;
    }

    /** Flache Kopie eines Blocks fuer Variant-Sub-Bloecke (lines bleibt geteilt — read-only Nutzung). */
    private static StructuredBlock copyOf(StructuredBlock src) {
        StructuredBlock dst = new StructuredBlock();
        dst.id = src.id;
        dst.kind = src.kind;
        dst.tier = src.tier;
        dst.name = src.name;
        dst.typeMarker = src.typeMarker;
        dst.startPage = src.startPage;
        dst.endPage = src.endPage;
        dst.parentHierarchyId = src.parentHierarchyId;
        dst.fields = src.fields;
        dst.lines = src.lines;
        dst.bodyText = src.bodyText;
        return dst;
    }
}
