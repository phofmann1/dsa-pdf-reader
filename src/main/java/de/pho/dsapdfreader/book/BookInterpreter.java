package de.pho.dsapdfreader.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.pho.dsapdfreader.markdown.RawPageData;
import de.pho.dsapdfreader.markdown.TextInterpreter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Buchweite Pipeline (Iteration 1 — Markdown-basiert).
 *
 * <p>Liest alle {@code page_NNN.json} eines Buches aus dem RAW-Verzeichnis,
 * konvertiert pro Page via {@link TextInterpreter} zu Markdown, und konsolidiert
 * sie zu zwei buchweiten Output-Dateien:
 *
 * <ul>
 *   <li>{@code <Buchtitel>.md} — vollstaendiger Fliesstext mit Seitenmarkern,
 *       Heading-Wort-Reflow, ohne Spalten-Marker</li>
 *   <li>{@code _structure.json} — Strukturindex mit Hierarchie, atomaren
 *       Bloecken (ueber Spalten-/Seiten-Grenzen aggregiert), Feldern</li>
 * </ul>
 *
 * <p>Iteration 1 deckt das markdown-basierte Modell ab. Boxen, Bilder, Tabellen
 * werden in Iteration 2 ueber {@code RawPageData.rects}/{@code .images}/Tabellen-
 * Erkennung dazugeholt — das aktuelle Output enthaelt sie noch nicht.
 *
 * <p>Siehe {@code docs/book-structure-model.md} fuer das vollstaendige Modell.
 */
public class BookInterpreter {

    private static final String GENERATOR_VERSION = "v1.0-mvp";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    /** "## Heading" oder "# Heading" oder "### Heading" oder "#### Heading" am Zeilenanfang */
    private static final Pattern P_HEADING = Pattern.compile("^(#{1,4})\\s+(.+?)\\s*$");
    /** "<!-- Spalte 1 -->" / "<!-- Spalte 2 -->" / "<!-- Vollbreite -->" */
    private static final Pattern P_COLUMN_MARKER = Pattern.compile("^<!--\\s*(Spalte\\s*\\d+|Vollbreite)\\s*-->\\s*$");
    /** "# Seite N" — Page-Header, der von RegenerateText geschrieben wird */
    private static final Pattern P_PAGE_HEADER = Pattern.compile("^#\\s+Seite\\s+(\\d+)\\s*$");
    /** "<!-- Quelle: ... -->" — Source-Marker */
    private static final Pattern P_SOURCE_MARKER = Pattern.compile("^<!--\\s*Quelle:.*-->\\s*$");
    /** "**Name** (passiv)" — fett-Block-Anfang mit Type-Marker */
    private static final Pattern P_BLOCK_BOLD_TYPE = Pattern.compile("^\\*\\*([^*]+?)\\*\\*\\s*\\(([a-zA-ZäöüÄÖÜ\\s]+?)\\)");
    /** "**Feld:**" — fett mit Doppelpunkt am Zeilenanfang */
    private static final Pattern P_FIELD_MARKER = Pattern.compile("^\\*\\*([^*:]+?):\\*\\*\\s*(.*)$");
    /** Inline-Field-Capture innerhalb eines Block-Bodies: alle "**Feld:** Wert" */
    private static final Pattern P_FIELDS_IN_BODY = Pattern.compile("\\*\\*([^*:]+?):\\*\\*\\s*(.*?)(?=\\*\\*[^*:]+?:\\*\\*|$)", Pattern.DOTALL);
    /** Embed-Marker fuer Boxen/Bilder/Tabellen, die Bloecke NICHT zerschneiden */
    private static final Pattern P_EMBED = Pattern.compile("^!?\\[\\[[^]]+]]\\s*$");
    /** raw page_NNN.json Dateien */
    private static final Pattern P_RAW_PAGE = Pattern.compile("page_(\\d+)\\.json$");

    public static class Args {
        public Path rawBookDir;     // raw input: enthaelt page_NNN.json
        public Path textBookDir;    // output: enthaelt <Buchtitel>.md, _structure.json, _assets/
        public String publicationCode;  // z. B. "kodex_des_schwertes"
        public String title;            // z. B. "Kodex des Schwertes"
        public Integer dsaVersion;      // optional, sonst null
    }

    /** Hauptpipeline. */
    public BookStructure run(Args args) throws IOException {
        // Schritt 1: alle Raw-Pages lesen
        List<RawPageData> pages = readRawPages(args.rawBookDir);

        // Schritt 2: pro Page Boxen splitten
        BoxExtractor boxer = new BoxExtractor();
        TextInterpreter ti = new TextInterpreter();
        List<String> pageMarkdowns = new ArrayList<>(pages.size());
        // Sammle alle Boxen pro Page als (page, idx, markdown)
        List<PageBox> allBoxes = new ArrayList<>();

        for (RawPageData p : pages) {
            BoxExtractor.SplitResult split = boxer.split(p);
            // Hauptlauftext rendern (ohne Box-Chars)
            String mainMd = ti.interpretPage(split.mainPage);
            // pro Box: eigene Page bauen und rendern
            List<String> boxEmbeds = new ArrayList<>();
            for (BoxExtractor.BoxRegion box : split.boxes) {
                RawPageData boxPage = new RawPageData();
                boxPage.pageNumber = p.pageNumber;
                boxPage.pageWidth = p.pageWidth;
                boxPage.pageHeight = p.pageHeight;
                boxPage.chars = box.chars;
                boxPage.images = new ArrayList<>();
                boxPage.rects = new ArrayList<>();
                String boxMd = ti.interpretPage(boxPage).trim();
                if (boxMd.isEmpty()) continue;
                String boxId = String.format("box_p%03d_%03d", p.pageNumber, box.boxIndex + 1);
                allBoxes.add(new PageBox(p.pageNumber, boxId, boxMd, box));
                boxEmbeds.add("![[" + boxId + "]]");
            }
            // Box-Embeds am Page-Ende anhaengen — der Block-Aggregator zerschneidet
            // dadurch keine Bloecke (Embeds gelten als unsichtbar)
            if (!boxEmbeds.isEmpty()) {
                StringBuilder sb = new StringBuilder(mainMd);
                if (!mainMd.endsWith("\n")) sb.append('\n');
                for (String e : boxEmbeds) sb.append(e).append('\n');
                mainMd = sb.toString();
            }
            pageMarkdowns.add(mainMd);
        }

        // Schritt 3: konsolidierter Fliesstext (mit Page-Markern, ohne Spalten-Marker, mit Heading-Reflow)
        String flowText = consolidateMarkdown(pageMarkdowns, pages);

        // Schritt 4: Strukturindex aufbauen (Hierarchie + Bloecke + Felder)
        BookStructure structure = buildStructure(flowText, args);

        // Schritt 5: Box-Assets in _structure.json eintragen
        for (PageBox pb : allBoxes) {
            BookStructure.BoxAsset ba = new BookStructure.BoxAsset();
            ba.path = "_assets/boxes/" + pb.boxId + ".md";
            ba.page = pb.page;
            ba.boxType = "unclassified";
            structure.assets.boxes.put(pb.boxId, ba);
            BookStructure.PageEntry pe = structure.pageIndex.computeIfAbsent(
                    String.valueOf(pb.page), k -> new BookStructure.PageEntry());
            pe.boxes.add(pb.boxId);
        }

        // Schritt 6: Output schreiben (inkl. Box-Files)
        writeOutputs(args, flowText, structure, allBoxes);
        return structure;
    }

    private record PageBox(int page, String boxId, String markdown, BoxExtractor.BoxRegion region) {}

    // -------------------------------------------------------------------------
    // Schritt 1: Raw-Pages lesen
    // -------------------------------------------------------------------------

    private List<RawPageData> readRawPages(Path rawBookDir) throws IOException {
        List<Path> pageFiles;
        try (Stream<Path> walk = Files.list(rawBookDir)) {
            pageFiles = walk
                    .filter(p -> P_RAW_PAGE.matcher(p.getFileName().toString()).find())
                    .sorted()
                    .toList();
        }
        List<RawPageData> result = new ArrayList<>(pageFiles.size());
        for (Path pf : pageFiles) {
            result.add(MAPPER.readValue(pf.toFile(), RawPageData.class));
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // Schritt 3: Markdown konsolidieren
    //   - Page-Marker einfuegen (<!-- Seite N -->)
    //   - Spalten-/Vollbreite-Marker entfernen (Lesereihenfolge ist linear)
    //   - Heading-Wort-Reflow ("Pro" + "ben" -> "Proben")
    // -------------------------------------------------------------------------

    String consolidateMarkdown(List<String> pageMarkdowns, List<RawPageData> pages) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pageMarkdowns.size(); i++) {
            int pageNum = pages.get(i).pageNumber;
            sb.append("<!-- Seite ").append(pageNum).append(" -->\n\n");
            for (String line : pageMarkdowns.get(i).split("\n", -1)) {
                if (P_COLUMN_MARKER.matcher(line.trim()).matches()) continue;
                if (P_PAGE_HEADER.matcher(line.trim()).matches()) continue;
                if (P_SOURCE_MARKER.matcher(line.trim()).matches()) continue;
                sb.append(line).append('\n');
            }
            sb.append('\n');
        }
        return reflowSplitHeadings(sb.toString());
    }

    /**
     * Wenn zwei aufeinanderfolgende Heading-Zeilen derselben Stufe direkt untereinander
     * stehen ohne dazwischenliegenden Body-Text und das erste mit Bindestrich endet
     * ODER zusammen ein typisch deutsches Wort ergeben, werden sie zusammengefuehrt.
     *
     * Beispiele die gemerged werden:
     *   "## Pro" + "## ben"   -> "## Proben"
     *   "## Heil" + "## ung"  -> "## Heilung"
     *   "## Gi" + "## fte"    -> "## Gifte"
     *
     * Heuristik: wenn das erste Heading <= 6 Zeichen hat und kein Wort ist,
     * mit dem zweiten zusammenfuehren. Konservativ — falsche Merges sind teurer
     * als verpasste Merges.
     */
    String reflowSplitHeadings(String md) {
        String[] lines = md.split("\n", -1);
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < lines.length) {
            String line = lines[i];
            Matcher m1 = P_HEADING.matcher(line);
            if (m1.matches() && i + 1 < lines.length) {
                // Suche das naechste Heading derselben Stufe direkt darunter
                int j = i + 1;
                // Leere Zeilen ueberspringen, aber nichts anderes zwischendrin
                while (j < lines.length && lines[j].trim().isEmpty()) j++;
                if (j < lines.length) {
                    Matcher m2 = P_HEADING.matcher(lines[j]);
                    if (m2.matches() && m1.group(1).equals(m2.group(1))) {
                        String left = m1.group(2).trim();
                        String right = m2.group(2).trim();
                        if (shouldMergeHeadings(left, right)) {
                            String merged = mergeHeadingTexts(left, right);
                            out.append(m1.group(1)).append(' ').append(merged).append('\n');
                            i = j + 1;
                            continue;
                        }
                    }
                }
            }
            out.append(line).append('\n');
            i++;
        }
        // letzten Newline kuerzen
        String result = out.toString();
        if (result.endsWith("\n\n")) result = result.substring(0, result.length() - 1);
        return result;
    }

    /**
     * Heuristik: zwei aufeinander folgende Headings derselben Stufe gehoeren zusammen, wenn:
     *  - linkes Heading endet mit Bindestrich (Silbentrennung), oder
     *  - rechtes Heading faengt mit Kleinbuchstaben an (typisch Print-Layout-Artefakt
     *    von ueber zwei Zeilen umbrochenen Worten), oder
     *  - linkes Heading endet mit einem einzelnen Buchstaben gefolgt von rechts mit lowercase.
     * Ausnahme: linkes Heading endet bereits mit Satzzeichen.
     */
    private boolean shouldMergeHeadings(String left, String right) {
        if (left.isEmpty() || right.isEmpty()) return false;
        if (left.endsWith("-")) return true;
        if (left.endsWith(".") || left.endsWith(":") || left.endsWith("!") || left.endsWith("?")) return false;
        char firstCh = right.charAt(0);
        // rechtes Heading beginnt mit lowercase -> sehr starkes Indiz fuer Wort-Mitte
        if (Character.isLetter(firstCh) && Character.isLowerCase(firstCh)) return true;
        // linkes endet mit einzelnem Großbuchstaben (z. B. "Kodex des S")
        String lastToken = left.substring(left.lastIndexOf(' ') + 1);
        char lastCh = left.charAt(left.length() - 1);
        return lastToken.length() == 1 && Character.isUpperCase(lastCh);
    }

    /**
     * Heuristik: Heading-Text ist tatsaechlich ein Tabellen-Header.
     * Beispiele: "I II III IV", "Wurf Wahrschein- Prozent", "Material Effekte Herstellung-Probe".
     */
    boolean looksLikeTableHeader(String title) {
        if (title == null || title.isEmpty()) return false;
        // Roemische Ziffern als ganze Spalten-Header ("I II III IV", "I. II.")
        if (title.matches("^[IVX]{1,4}(\\s+[IVX]{1,4})+\\s*\\.?$")) return true;
        // viele kurze Tokens (typisch Tabellen): >= 4 Wörter, durchschnittl. < 8 Buchstaben pro Token
        String[] tokens = title.split("\\s+");
        if (tokens.length >= 4) {
            int totalChars = 0;
            for (String t : tokens) totalChars += t.length();
            double avg = (double) totalChars / tokens.length;
            if (avg < 8.5) return true;
        }
        // Bindestrich-Wort + viele Tokens (klassischer Tabellen-Header)
        if (tokens.length >= 3 && title.contains("-") && title.length() <= 80
                && !title.endsWith(".") && !title.endsWith(":")) return true;
        return false;
    }

    /** Mergt zwei Heading-Texte. Bindestrich am Ende des linken faellt weg. */
    private String mergeHeadingTexts(String left, String right) {
        if (left.endsWith("-")) return left.substring(0, left.length() - 1) + right;
        // wenn rechtes mit lowercase oder linkes endet mit einzelnem Buchstaben:
        // direkt anschmiegen ohne Leerzeichen (Wort-Trennung)
        String lastToken = left.substring(left.lastIndexOf(' ') + 1);
        char firstCh = right.charAt(0);
        if (lastToken.length() == 1 || Character.isLowerCase(firstCh)) return left + right;
        return left + " " + right;
    }

    // -------------------------------------------------------------------------
    // Schritt 4: Strukturindex aufbauen
    //   - Heading-Hierarchie (H1..H4)
    //   - atomare Bloecke (`### Foobar`-Headings ODER `**Name** (passiv)`-Pattern)
    //   - Felder pro Block (Bold mit Doppelpunkt + Lauftext)
    //   - Bloecke werden ueber Page/Spalten-Grenzen aggregiert
    // -------------------------------------------------------------------------

    BookStructure buildStructure(String flowText, Args args) {
        BookStructure s = new BookStructure();
        s.publication = args.publicationCode;
        s.title = args.title;
        s.dsaVersion = args.dsaVersion;
        s.generatedAt = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        s.generatorVersion = GENERATOR_VERSION;

        // Lese den konsolidierten Markdown linear, fuehre einen Hierarchie-Stack
        // Pro Heading: Body bis zum naechsten Heading sammeln, dann entscheiden,
        // ob es Hierarchie-Knoten oder atomarer Block wird (basierend auf Pflichtfeldern).
        String[] lines = flowText.split("\n", -1);
        List<HierarchyContext> stack = new ArrayList<>();
        BookStructure.Block currentBlock = null;
        StringBuilder currentBlockBody = null;
        PendingHeading pendingHeading = null;
        StringBuilder pendingHeadingBody = null;
        int currentPage = -1;
        int blockCounter = 0;
        int hierarchyCounter = 0;
        int pageHwm = 0;
        int charOffset = 0;
        Pattern P_PAGE_LINE = Pattern.compile("^<!--\\s*Seite\\s+(\\d+)\\s*-->\\s*$");

        for (String rawLine : lines) {
            String line = rawLine.trim();
            int lineLen = rawLine.length() + 1;

            Matcher mPage = P_PAGE_LINE.matcher(line);
            if (mPage.matches()) {
                currentPage = Integer.parseInt(mPage.group(1));
                pageHwm = Math.max(pageHwm, currentPage);
                if (pendingHeading != null) pendingHeadingBody.append(rawLine).append('\n');
                if (currentBlock != null) currentBlockBody.append(rawLine).append('\n');
                charOffset += lineLen;
                continue;
            }
            Matcher mH = P_HEADING.matcher(line);
            if (mH.matches()) {
                int level = mH.group(1).length();
                String title = mH.group(2).trim();
                // Tabellen-Header (z. B. "I II III IV", "Material Effekte Herstellung-Probe")
                // ignorieren — sie sind keine Headings, sondern Pseudo-Bold aus Tabellen-Layout.
                if (looksLikeTableHeader(title)) {
                    if (pendingHeading != null) pendingHeadingBody.append(rawLine).append('\n');
                    if (currentBlock != null) currentBlockBody.append(rawLine).append('\n');
                    charOffset += lineLen;
                    continue;
                }
                if (currentBlock != null) {
                    flushBlockFields(currentBlock, currentBlockBody.toString());
                    blockCounter += attachWithStageExpansion(currentBlock, stack, s, blockCounter);
                    currentBlock = null;
                    currentBlockBody = null;
                }
                if (pendingHeading != null) {
                    blockCounter += finalizePendingHeading(pendingHeading, pendingHeadingBody.toString(),
                            stack, s, blockCounter);
                    pendingHeading = null;
                    pendingHeadingBody = null;
                }
                pendingHeading = new PendingHeading();
                pendingHeading.id = String.format("h%04d", ++hierarchyCounter);
                pendingHeading.level = level;
                pendingHeading.title = title;
                pendingHeading.page = currentPage;
                pendingHeading.anchorOffset = charOffset;
                pendingHeadingBody = new StringBuilder(rawLine).append('\n');
                charOffset += lineLen;
                continue;
            }
            Matcher mBlock = P_BLOCK_BOLD_TYPE.matcher(line);
            if (mBlock.lookingAt()) {
                if (pendingHeading != null) {
                    blockCounter += finalizePendingHeading(pendingHeading, pendingHeadingBody.toString(),
                            stack, s, blockCounter);
                    pendingHeading = null;
                    pendingHeadingBody = null;
                }
                if (currentBlock != null) {
                    flushBlockFields(currentBlock, currentBlockBody.toString());
                    blockCounter += attachWithStageExpansion(currentBlock, stack, s, blockCounter);
                }
                currentBlock = new BookStructure.Block();
                currentBlock.kind = "unclassified";
                currentBlock.name = mBlock.group(1).trim();
                currentBlock.page = currentPage;
                currentBlock.anchorOffset = charOffset;
                currentBlockBody = new StringBuilder(rawLine).append('\n');
                charOffset += lineLen;
                continue;
            }
            if (currentBlock != null) currentBlockBody.append(rawLine).append('\n');
            else if (pendingHeading != null) pendingHeadingBody.append(rawLine).append('\n');
            charOffset += lineLen;
        }
        if (currentBlock != null) {
            flushBlockFields(currentBlock, currentBlockBody.toString());
            blockCounter += attachWithStageExpansion(currentBlock, stack, s, blockCounter);
        }
        if (pendingHeading != null) {
            blockCounter += finalizePendingHeading(pendingHeading, pendingHeadingBody.toString(),
                    stack, s, blockCounter);
        }
        s.pages = pageHwm;
        return s;
    }

    /** Pending-Heading: gerade gestartetes Heading mit Body-Sammlung. */
    private static class PendingHeading {
        String id;
        int level;
        String title;
        int page;
        int anchorOffset;
    }

    /**
     * Entscheidet ueber Block-vs-Hierarchie. Returnt &gt;= 1, wenn als Block(s)
     * konsumiert wurde (und der Block-Counter um diese Anzahl erhoeht werden muss).
     * Returnt 0, wenn als Hierarchie-Knoten konsumiert.
     */
    private int finalizePendingHeading(PendingHeading ph, String body,
                                       List<HierarchyContext> stack, BookStructure s,
                                       int blockCounterBefore) {
        Map<String, String> probeFields = new LinkedHashMap<>();
        Matcher m = P_FIELDS_IN_BODY.matcher(body);
        while (m.find()) {
            String key = m.group(1).trim().toLowerCase();
            if (key.equals("voraussetzungen")) key = "voraussetzung";
            if (key.equals("kampftechniken")) key = "kampftechnik";
            probeFields.put(key, m.group(2).trim().replaceAll("\\s+", " "));
        }
        boolean hasVor = probeFields.containsKey("voraussetzung");
        boolean hasAp = probeFields.containsKey("ap-wert") || probeFields.containsKey("kosten");
        if (hasVor && hasAp) {
            BookStructure.Block prototype = new BookStructure.Block();
            prototype.kind = "ability";
            prototype.name = ph.title;
            prototype.page = ph.page;
            prototype.anchorOffset = ph.anchorOffset;
            prototype.fields = probeFields;
            List<BookStructure.Block> stages = expandByStages(prototype, blockCounterBefore);
            for (BookStructure.Block stage : stages) {
                attachBlockToTop(stack, s.hierarchy, stage);
                indexBlockOnPage(s, stage);
            }
            return stages.size();
        }
        BookStructure.HierarchyNode node = new BookStructure.HierarchyNode();
        node.id = ph.id;
        node.level = ph.level;
        node.title = ph.title;
        node.pageRange = new int[]{ph.page > 0 ? ph.page : 1, ph.page > 0 ? ph.page : 1};
        while (!stack.isEmpty() && stack.get(stack.size() - 1).level >= ph.level) {
            stack.remove(stack.size() - 1);
        }
        if (stack.isEmpty()) s.hierarchy.add(node);
        else stack.get(stack.size() - 1).node.children.add(node);
        stack.add(new HierarchyContext(ph.level, node));
        return 0;
    }

    private static class HierarchyContext {
        int level;
        BookStructure.HierarchyNode node;
        HierarchyContext(int level, BookStructure.HierarchyNode node) {
            this.level = level;
            this.node = node;
        }
    }

    /**
     * Attached einen Bold-Type-Block an den aktuellen Hierarchie-Knoten — mit
     * Stufen-Splitting wenn der Name eine Range enthaelt (z. B. "Foobar I-IV").
     * Returnt die Anzahl der erzeugten Bloecke.
     */
    private int attachWithStageExpansion(BookStructure.Block prototype,
                                         List<HierarchyContext> stack, BookStructure s,
                                         int blockCounterBefore) {
        List<BookStructure.Block> stages = expandByStages(prototype, blockCounterBefore);
        for (BookStructure.Block stage : stages) {
            attachBlockToTop(stack, s.hierarchy, stage);
            indexBlockOnPage(s, stage);
        }
        return stages.size();
    }

    /**
     * Wenn der Block-Name eine Stufen-Range enthaelt (z. B. "Beidhaendiger Kampf I-II"),
     * werden mehrere Stufen-Bloecke erzeugt — sonst genau ein Block.
     */
    private List<BookStructure.Block> expandByStages(BookStructure.Block prototype, int blockCounterBefore) {
        int[] stages = parseStageRange(prototype.name);
        if (stages.length == 0) {
            // keine Range: ein einziger Block (Stufen-Suffix bleibt im Namen wie "Foobar I")
            prototype.id = String.format("b%05d", blockCounterBefore + 1);
            return List.of(prototype);
        }
        String baseName = stripStageFromName(prototype.name);
        List<BookStructure.Block> result = new ArrayList<>();
        for (int i = 0; i < stages.length; i++) {
            BookStructure.Block stage = new BookStructure.Block();
            stage.id = String.format("b%05d", blockCounterBefore + 1 + i);
            stage.kind = prototype.kind;
            stage.name = baseName + " " + toRoman(stages[i]);
            stage.page = prototype.page;
            stage.anchorOffset = prototype.anchorOffset;
            stage.fields = new LinkedHashMap<>(prototype.fields);
            result.add(stage);
        }
        return result;
    }

    /** Erkennt Stufen-Ranges am Ende des Namens: "I-II", "I-IV", "I/II", aber NICHT einzelne "I" / "III". */
    private int[] parseStageRange(String name) {
        Matcher m = Pattern.compile("\\s+([IVX]{1,5})\\s*[-/]\\s*([IVX]{1,5})\\s*$").matcher(name);
        if (!m.find()) return new int[0];
        int start = romanToInt(m.group(1));
        int end = romanToInt(m.group(2));
        if (start <= 0 || end <= start || end > 10) return new int[0];
        int[] r = new int[end - start + 1];
        for (int i = 0; i < r.length; i++) r[i] = start + i;
        return r;
    }

    /** Entfernt eine Stufen-Range vom Namen-Ende ("Foobar I-IV" -> "Foobar"). */
    private String stripStageFromName(String name) {
        Matcher m = Pattern.compile("\\s+[IVX]{1,5}\\s*[-/]\\s*[IVX]{1,5}\\s*$").matcher(name);
        return m.find() ? name.substring(0, m.start()).trim() : name;
    }

    private int romanToInt(String s) {
        if (s == null || s.isEmpty()) return 0;
        s = s.toUpperCase();
        int total = 0, prev = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            int v = switch (s.charAt(i)) {
                case 'I' -> 1;
                case 'V' -> 5;
                case 'X' -> 10;
                default -> -1;
            };
            if (v < 0) return 0;
            if (v < prev) total -= v; else { total += v; prev = v; }
        }
        return total;
    }

    private String toRoman(int n) {
        return switch (n) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(n);
        };
    }

    /** Pflichtfelder aus dem Block-Body extrahieren. */
    private void flushBlockFields(BookStructure.Block block, String body) {
        Matcher m = P_FIELDS_IN_BODY.matcher(body);
        while (m.find()) {
            String key = m.group(1).trim().toLowerCase();
            String value = m.group(2).trim().replaceAll("\\s+", " ");
            // Plural normalisieren
            if (key.equals("voraussetzungen")) key = "voraussetzung";
            if (key.equals("kampftechniken")) key = "kampftechnik";
            // Bekannte Feld-Whitelist + alles andere als raw-payload
            block.fields.put(key, value);
        }
        // Klassifikation: hat Voraussetzung + (AP-Wert | Kosten) -> ability
        if (block.fields.containsKey("voraussetzung")
                && (block.fields.containsKey("ap-wert") || block.fields.containsKey("kosten"))) {
            block.kind = "ability";
        }
    }

    private void attachBlockToTop(List<HierarchyContext> stack,
                                  List<BookStructure.HierarchyNode> rootHierarchy,
                                  BookStructure.Block block) {
        if (stack.isEmpty()) {
            // kein Heading bis hierhin — zur Wurzel haengen ueber pseudo-root
            BookStructure.HierarchyNode pseudoRoot;
            if (rootHierarchy.isEmpty() || !"_root".equals(rootHierarchy.get(0).id)) {
                pseudoRoot = new BookStructure.HierarchyNode();
                pseudoRoot.id = "_root";
                pseudoRoot.level = 0;
                pseudoRoot.title = "(ohne Heading)";
                rootHierarchy.add(0, pseudoRoot);
            } else {
                pseudoRoot = rootHierarchy.get(0);
            }
            pseudoRoot.blocks.add(block);
        } else {
            stack.get(stack.size() - 1).node.blocks.add(block);
        }
    }

    private void indexBlockOnPage(BookStructure s, BookStructure.Block block) {
        if (block.page == null) return;
        BookStructure.PageEntry e = s.pageIndex.computeIfAbsent(
                String.valueOf(block.page), k -> new BookStructure.PageEntry());
        e.blocks.add(block.id);
    }

    // -------------------------------------------------------------------------
    // Schritt 5: Outputs schreiben
    // -------------------------------------------------------------------------

    private void writeOutputs(Args args, String flowText, BookStructure structure,
                              List<PageBox> boxes) throws IOException {
        Files.createDirectories(args.textBookDir);
        Files.createDirectories(args.textBookDir.resolve("_assets/images"));
        Files.createDirectories(args.textBookDir.resolve("_assets/tables"));
        Path boxesDir = args.textBookDir.resolve("_assets/boxes");
        Files.createDirectories(boxesDir);

        Path mdFile = args.textBookDir.resolve(args.title + ".md");
        Files.writeString(mdFile, flowText, StandardCharsets.UTF_8);

        Path structFile = args.textBookDir.resolve("_structure.json");
        Map<String, Object> wrapper = toWrapper(structure);
        Files.writeString(structFile, MAPPER.writeValueAsString(wrapper), StandardCharsets.UTF_8);

        // Box-Files schreiben
        for (PageBox pb : boxes) {
            Path boxFile = boxesDir.resolve(pb.boxId + ".md");
            String header = "---\n"
                    + "id: " + pb.boxId + "\n"
                    + "page: " + pb.page + "\n"
                    + "---\n\n";
            Files.writeString(boxFile, header + pb.markdown + "\n", StandardCharsets.UTF_8);
        }
    }

    /** Wandelt das Object-Modell in eine Map mit `$schema`-Schluessel oben. */
    private Map<String, Object> toWrapper(BookStructure s) {
        Map<String, Object> w = new LinkedHashMap<>();
        w.put("$schema", "https://dsa-data.local/schema/book-structure-v1.json");
        Map<String, Object> body = MAPPER.convertValue(s, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        w.putAll(body);
        return w;
    }
}
