package de.pho.dsapdfreader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Exportiert die Volltext-Markdowns aus {@code export/markdown/text/.../result/_volltext.md}
 * in einen Obsidian-Vault unter {@code D:/Daten/Obsidian/Aventurien}.
 *
 * <h2>Konventionen</h2>
 * <ul>
 *   <li>Default-Kategorien: Praefixe {@code 00}-{@code 04}.</li>
 *   <li>Knoten mit Kindern → Folder + Folder-Note ({@code &lt;name&gt;/&lt;name&gt;.md}).</li>
 *   <li>Knoten ohne Kinder → einzelne {@code .md}-Datei.</li>
 *   <li>Title steht im Filename — KEIN zusaetzliches {@code # Title} im Body.</li>
 *   <li>Folder-Notes erhalten am Ende ein {@code ## Inhalt}-Block mit Wikilinks
 *       zu allen direkten Kindern.</li>
 *   <li>Sortierung via Frontmatter pro Folder-Note: {@code sorting-spec} mit
 *       expliziter Children-Reihenfolge (Plugin "Custom File Explorer Sorting").
 *       Leaf-Notes haben zusaetzlich {@code sort-index} fuer Fallback-Sortierung.</li>
 * </ul>
 *
 * <h2>Pipeline-Bug-Schutz</h2>
 * <ul>
 *   <li>Vorlauf-Skip (Impressum/Inhaltsverzeichnis/Vorwort) bis zum ersten echten Heading.</li>
 *   <li>Falsch-Heading-Filter: H1 mit Italic-Marker, H1 nach H4+ ausser
 *       "KAPITEL N:" oder ALL-CAPS (echte Buch-Kapitel), Heading > 80 Zeichen.</li>
 *   <li>Banner-Heading-Promotion: Display-Banner wie "Land &amp; Leute *...*"
 *       werden zu virtuellen H1-Bannern, die nachfolgende H2 als Children haben.</li>
 * </ul>
 */
public class VaultExporter {

    private static final Path SOURCE = Paths.get("export/markdown/text");
    private static final Path TARGET = Paths.get("D:/Daten/Obsidian/Aventurien");

    private static final Pattern HEADING = Pattern.compile("^(#{1,6})\\s+(.+?)\\s*$");
    /**
     * Vorlauf-Headings die im Vault verworfen werden. Enthaelt neben den
     * Standard-Bezeichnern auch typische Vorwort-Sub-Sections der
     * Regionalspielhilfen (im Dornenreich/Sonnenkueste/etc. als H3 statt H4
     * gerendert).
     */
    private static final Pattern SKIP_HEADING = Pattern.compile(
            "^(impressum|inhaltsverzeichnis|inhalt|vorwort|geleitwort|prolog|kolophon|"
            + "danksagung|widmung|"
            + "eine kurze gebrauchsanleitung|gebrauchsanleitung|gebrauchshinweise|"
            + "hinweise zum buch|hinweise zur lekt(ü|ue)re|"
            + "wie ist (der|das) band aufgebaut\\??|"
            + "wie ist (der|das) (band|buch) (aufgebaut|gegliedert)\\??|"
            + "wo fange ich (an)?\\??|"
            + "wo lese ich (weiter)?\\??|"
            + "was wei[ßs]+ mein held\\??|"
            + "wie kann ich (einfach )?(los)?spielen\\??"
            + ")\\s*$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern CATEGORY_PREFIX = Pattern.compile("^(\\d+)([a-z]?)\\s*-\\s*(.+)$");
    private static final Pattern BOOK_DIR = Pattern.compile("^(.+?)\\s*\\(\\d+\\)\\s*$");

    private static final Pattern BANNER = Pattern.compile(
            "^(?<title>[A-ZÄÖÜ][^*]{2,40}?)\\s+\\*[a-zäöüß]");

    /** "KAPITEL 1: GRUNDREGELN" — echtes Buch-Kapitel auch nach tiefen Sub-Headings. */
    private static final Pattern CHAPTER_HEADING = Pattern.compile(
            "(?i)^(kapitel|teil|abschnitt|anhang|index|epilog)\\b.*");

    private static final int MAX_FILENAME_LEN = 80;
    private static final int MIN_LINK_LEN = 5;

    private static final Set<String> DEFAULT_CATEGORY_PREFIXES = Set.of(
            "00", "01", "02", "03", "04");

    private static final Set<String> STOP_WORDS = Set.of(
            "Aber","Alle","Auch","Auf","Aus","Bei","Beim","Bis",
            "Das","Dem","Den","Der","Des","Die","Diese","Doch","Dort","Durch",
            "Ein","Eine","Eines","Einer","Einen","Es",
            "Fuer","Gegen","Hat","Hier","Ich","Ihm","Ihr","Ihre","Ihrer","Ist",
            "Mehr","Mit","Nach","Nicht","Noch","Nun","Nur","Ohne",
            "Sehr","Sein","Seine","Seinen","Seiner","Sie","Sind","Solche","Soll","Sondern",
            "Ueber","Um","Und","Uns","Unser","Vom","Von","Vor",
            "Wann","War","Was","Weg","Wegen","Weil","Welche","Wenn","Wer","Werden",
            "Wie","Wieder","Wird","Wo","Worauf","Zu","Zum","Zur","Zwei");

    public static void main(String[] args) throws Exception {
        List<String> filters = new ArrayList<>();
        for (String a : args) if (a != null && !a.isBlank()) filters.add(a.toLowerCase());

        Files.createDirectories(TARGET);

        Map<String, List<BookSpec>> booksByCategory = new LinkedHashMap<>();
        try (Stream<Path> walk = Files.walk(SOURCE)) {
            for (Path innerDir : walk
                    .filter(Files::isDirectory)
                    .filter(d -> Files.exists(d.resolve("result/_volltext.md")))
                    .sorted()
                    .toList())
            {
                Path rel = SOURCE.relativize(innerDir);
                if (rel.getNameCount() < 3) continue;
                String category = rel.getName(0).toString();
                String bookDirName = rel.getName(1).toString();

                if (filters.isEmpty()) {
                    String prefix = categoryPrefix(category);
                    if (prefix == null || !DEFAULT_CATEGORY_PREFIXES.contains(prefix)) continue;
                } else {
                    String pathLower = innerDir.toString().toLowerCase();
                    boolean match = false;
                    for (String f : filters) if (pathLower.contains(f)) { match = true; break; }
                    if (!match) continue;
                }

                String bookName = stripBookKey(bookDirName);
                Path volltext = innerDir.resolve("result/_volltext.md");
                booksByCategory.computeIfAbsent(category, k -> new ArrayList<>())
                        .add(new BookSpec(bookName, volltext, category));
            }
        }

        List<CategorySpec> categories = new ArrayList<>();
        for (var e : booksByCategory.entrySet()) categories.add(new CategorySpec(e.getKey(), e.getValue()));
        categories.sort(Comparator
                .comparingInt((CategorySpec c) -> c.sortPrimary)
                .thenComparing(c -> c.sortSecondary)
                .thenComparing(c -> c.cleanName));

        // Phase 2: Parse alle Volltexte zu Section-Trees in Memory
        for (CategorySpec cat : categories) {
            for (BookSpec book : cat.books) {
                String content = Files.readString(book.volltextPath, StandardCharsets.UTF_8);
                book.tree = parseSections(content);
            }
        }

        // Phase 3: Per-Buch Note-Index aufbauen (fuer Wikilinks)
        for (CategorySpec cat : categories) {
            cat.cleanCategoryName = cat.cleanName;
            cat.books.sort(Comparator.comparing((BookSpec b) -> b.bookName.toLowerCase()));
            for (BookSpec book : cat.books) {
                String catSafe = sanitize(cat.cleanCategoryName);
                String bookSafe = sanitize(book.bookName);
                Path bookRel = Paths.get(catSafe, bookSafe);
                book.vaultRelDir = bookRel;
                book.noteIndex = new HashMap<>();
                indexNote(book.noteIndex, bookRel.resolve(bookSafe).toString().replace('\\', '/'),
                        book.bookName);
                indexSectionsForLinks(book.tree.children, bookRel, book.noteIndex);
            }
        }

        // Alte sortspec.md im Root entfernen — wir nutzen jetzt Folder-Note-Frontmatter.
        Files.deleteIfExists(TARGET.resolve("sortspec.md"));

        int totalBooks = 0, totalNotes = 0;
        List<String> categoryFolderNames = new ArrayList<>();
        for (int ci = 0; ci < categories.size(); ci++) {
            CategorySpec cat = categories.get(ci);
            String catSafe = sanitize(cat.cleanCategoryName);
            categoryFolderNames.add(catSafe);
            Path catDir = TARGET.resolve(catSafe);
            cleanDirectory(catDir);
            Files.createDirectories(catDir);

            // Kinder der Kategorie (Buecher) — Reihenfolge fuer sortspec
            List<String> bookFolderNames = new ArrayList<>();
            for (BookSpec book : cat.books) bookFolderNames.add(sanitize(book.bookName));
            writeFolderNote(catDir, cat.cleanCategoryName, ci, "", null, bookFolderNames);

            for (int bi = 0; bi < cat.books.size(); bi++) {
                BookSpec book = cat.books.get(bi);
                int notes = exportBook(book, catDir, bi, book.noteIndex);
                totalBooks++;
                totalNotes += notes;
            }
        }

        // Einstellungen-Folder am Ende mit Vault-System-Doku.
        writeSettingsFolder(categoryFolderNames);

        System.out.println("Done: " + totalBooks + " Buecher, " + totalNotes + " Notes nach " + TARGET);
    }

    // ---------- Indexing ---------------------------------------------------

    static void indexSectionsForLinks(List<Section> sections, Path parentRel,
                                       Map<String, String> index) {
        Set<String> usedNames = new HashSet<>();
        for (Section s : sections) {
            String safe = uniqueName(sanitize(s.title), usedNames);
            usedNames.add(safe.toLowerCase());
            Path noteRel;
            if (s.children.isEmpty()) {
                noteRel = parentRel.resolve(safe);
            } else {
                Path subDir = parentRel.resolve(safe);
                noteRel = subDir.resolve(safe);
                indexSectionsForLinks(s.children, subDir, index);
            }
            indexNote(index, noteRel.toString().replace('\\', '/'), s.title);
        }
    }

    static void indexNote(Map<String, String> index, String relPath, String title) {
        String key = title.toLowerCase().strip();
        if (key.length() < MIN_LINK_LEN) return;
        if (STOP_WORDS.contains(title.strip())) return;
        index.putIfAbsent(key, relPath);
    }

    // ---------- Book Export ------------------------------------------------

    static int exportBook(BookSpec book, Path catDir, int sortIdx,
                           Map<String, String> noteIndex) throws IOException {
        Section root = book.tree;
        Path bookDir = catDir.resolve(sanitize(book.bookName));
        Files.createDirectories(bookDir);

        // Children-Reihenfolge fuer sortspec
        List<String> childNames = childOrderNames(root.children);
        writeFolderNote(bookDir, book.bookName, sortIdx, root.body.strip(), noteIndex, childNames);

        Counter c = new Counter();
        c.count = 1;
        writeSections(root.children, bookDir, c, noteIndex);
        return c.count;
    }

    static List<String> childOrderNames(List<Section> sections) {
        List<String> names = new ArrayList<>();
        Set<String> used = new HashSet<>();
        for (Section s : sections) {
            String safe = uniqueName(sanitize(s.title), used);
            used.add(safe.toLowerCase());
            names.add(safe);
        }
        return names;
    }

    static void writeSections(List<Section> sections, Path parentDir, Counter counter,
                               Map<String, String> noteIndex) throws IOException {
        Set<String> usedNames = new HashSet<>();
        for (int i = 0; i < sections.size(); i++) {
            Section s = sections.get(i);
            String safe = uniqueName(sanitize(s.title), usedNames);
            usedNames.add(safe.toLowerCase());
            if (s.children.isEmpty()) {
                Path file = parentDir.resolve(safe + ".md");
                writeNote(file, s.title, i, s.body.strip(), noteIndex, null);
                counter.count++;
            } else {
                Path subDir = parentDir.resolve(safe);
                Files.createDirectories(subDir);
                List<String> childNames = childOrderNames(s.children);
                writeFolderNote(subDir, s.title, i, s.body.strip(), noteIndex, childNames);
                counter.count++;
                writeSections(s.children, subDir, counter, noteIndex);
            }
        }
    }

    // ---------- Markdown-Parsing ------------------------------------------

    static Section parseSections(String content) {
        String[] lines = content.split("\\r?\\n");
        int contentStart = findContentStartLine(lines);

        Section root = new Section(0, "");
        Deque<Section> stack = new ArrayDeque<>();
        stack.push(root);

        StringBuilder body = new StringBuilder();
        int lastHeadingLevel = 0;
        for (int i = contentStart; i < lines.length; i++) {
            String line = lines[i];
            Matcher mh = HEADING.matcher(line);
            if (mh.matches() && isRealHeading(line, mh, lastHeadingLevel)) {
                int level = mh.group(1).length();
                String title = stripFormatting(mh.group(2)).trim();
                if (title.isEmpty()) continue;

                appendBody(stack, body);
                while (stack.peek().level >= level) stack.pop();
                Section ns = new Section(level, title);
                stack.peek().children.add(ns);
                stack.push(ns);
                lastHeadingLevel = level;
            } else {
                Matcher mb = BANNER.matcher(line);
                if (mb.find() && mb.start() == 0) {
                    String candidate = mb.group("title").strip();
                    if (validateBannerTitle(candidate)) {
                        appendBody(stack, body);
                        while (stack.peek().level >= 1) stack.pop();
                        Section banner = new Section(1, candidate);
                        stack.peek().children.add(banner);
                        stack.push(banner);
                        lastHeadingLevel = 1;
                        body.append(line).append("\n");
                        continue;
                    }
                }
                body.append(line).append("\n");
            }
        }
        appendBody(stack, body);
        return root;
    }

    private static void appendBody(Deque<Section> stack, StringBuilder body) {
        if (body.length() == 0) return;
        stack.peek().body += body.toString();
        body.setLength(0);
    }

    static int findContentStartLine(String[] lines) {
        int lastSkipIdx = -1;
        int lastSkipLevel = 0;
        for (int i = 0; i < lines.length; i++) {
            Matcher m = HEADING.matcher(lines[i]);
            if (!m.matches()) continue;
            int level = m.group(1).length();
            String title = stripFormatting(m.group(2)).trim();
            if (level == 1 && title.toLowerCase().startsWith("quelle:")) continue;
            if (SKIP_HEADING.matcher(title).matches()) {
                lastSkipIdx = i;
                lastSkipLevel = level;
            }
        }
        int searchFrom = (lastSkipIdx >= 0) ? lastSkipIdx + 1 : 0;
        int targetLevel = (lastSkipLevel > 0) ? lastSkipLevel : 6;
        for (int i = searchFrom; i < lines.length; i++) {
            Matcher m = HEADING.matcher(lines[i]);
            if (!m.matches()) continue;
            int level = m.group(1).length();
            String title = stripFormatting(m.group(2)).trim();
            if (level == 1 && title.toLowerCase().startsWith("quelle:")) continue;
            if (SKIP_HEADING.matcher(title).matches()) continue;
            if (!isRealHeading(lines[i], m, 0)) continue;
            if (level > targetLevel) continue;
            return i;
        }
        return lines.length;
    }

    static boolean validateBannerTitle(String title) {
        if (title == null) return false;
        String[] words = title.strip().split("\\s+");
        if (words.length < 2 || words.length > 5) return false;
        if (!startsWithUpper(words[0])) return false;
        if (!startsWithUpper(words[words.length - 1])) return false;
        Set<String> connectors = Set.of("und", "&", "im", "in", "an", "am",
                "der", "des", "von", "zu", "auf", "mit");
        for (String w : words) {
            if (connectors.contains(w.toLowerCase())) continue;
            if (!startsWithUpper(w)) return false;
            if (!w.matches("[\\p{L}&\\-]+")) return false;
        }
        return true;
    }

    private static boolean startsWithUpper(String s) {
        if (s == null || s.isEmpty()) return false;
        return Character.isUpperCase(s.charAt(0));
    }

    /**
     * Filtert Pipeline-Bug-Headings.
     * H1 wird nach H4+ normalerweise als Bug verworfen — Ausnahme: echte
     * Buch-Kapitel mit "KAPITEL N:"-Praefix oder ALL-CAPS-Title werden
     * trotzdem akzeptiert.
     */
    static boolean isRealHeading(String line, Matcher m, int lastHeadingLevel) {
        int level = m.group(1).length();
        String raw = m.group(2).trim();
        if (level == 1 && raw.contains("*")) return false;
        String stripped = stripFormatting(raw);
        if (stripped.length() > 80) return false;
        if (level == 1 && lastHeadingLevel >= 3) {
            // Echtes Buch-Kapitel oder Anhang → akzeptieren
            if (CHAPTER_HEADING.matcher(stripped).matches()) return true;
            if (isAllCaps(stripped)) return true;
            return false;
        }
        return true;
    }

    private static boolean isAllCaps(String s) {
        boolean hasLetter = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
                if (Character.isLowerCase(c)) return false;
            }
        }
        return hasLetter;
    }

    private static String stripFormatting(String s) {
        return s.replaceAll("\\*\\*", "")
                .replaceAll("(?<![\\w])\\*(?![\\s\\*])", "")
                .replaceAll("(?<![\\s\\*])\\*(?![\\w])", "")
                .replaceAll("__", "")
                .trim();
    }

    // ---------- File Writing ----------------------------------------------

    /**
     * Schreibt eine Note (Leaf oder Folder-Note).
     * Title ist der Filename — wird NICHT als {@code # Title} im Body
     * wiederholt. Wenn {@code childOrder != null}, wird Frontmatter um
     * {@code sorting-spec} (explizite Children-Reihenfolge) erweitert und
     * am Ende des Body ein {@code ## Inhalt}-TOC angehaengt.
     */
    static void writeNote(Path file, String title, int sortIdx, String body,
                          Map<String, String> noteIndex, List<String> childOrder) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("---\n");
        sb.append("sort-index: ").append(sortIdx).append('\n');
        if (childOrder != null && !childOrder.isEmpty()) {
            // Custom File Explorer Sorting: explizite Children-Reihenfolge fuer
            // den aktuellen Folder. Items ausserhalb der Liste kommen alphabetisch
            // danach (Plugin-Default).
            sb.append("sorting-spec: |\n");
            sb.append("  target-folder: \"./\"\n");
            for (String name : childOrder) {
                sb.append("  ").append(name).append('\n');
            }
        }
        sb.append("---\n");
        if (body != null && !body.isEmpty()) {
            String linked = applyWikilinks(body, noteIndex, file);
            sb.append(linked).append('\n');
        }
        if (childOrder != null && !childOrder.isEmpty()) {
            sb.append("\n## Inhalt\n");
            String parentRel = TARGET.relativize(file.getParent()).toString().replace('\\', '/');
            for (String name : childOrder) {
                String linkPath = parentRel.isEmpty() ? name : (parentRel + "/" + name);
                sb.append("- [[").append(linkPath).append('|').append(name).append("]]\n");
            }
        }
        Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
    }

    static void writeFolderNote(Path folder, String title, int sortIdx, String body,
                                Map<String, String> noteIndex, List<String> childOrder) throws IOException {
        Path file = folder.resolve(folder.getFileName().toString() + ".md");
        writeNote(file, title, sortIdx, body, noteIndex, childOrder);
    }

    /**
     * Erzeugt einen Einstellungen-Folder am Ende des Vaults
     * mit hohem sort-index (sortiert nach allen Kategorien).
     * Enthaelt eine README mit Vault-Konventionen.
     */
    static void writeSettingsFolder(List<String> categoryFolders) throws IOException {
        Path dir = TARGET.resolve("_Einstellungen");
        cleanDirectory(dir);
        Files.createDirectories(dir);
        Path readme = dir.resolve("_Einstellungen.md");
        String body = ""
                + "Technischer Ordner — vom VaultExporter erzeugt und gepflegt.\n\n"
                + "## Konventionen\n\n"
                + "- **Folder-Note** pro Knoten mit Kindern: `<folder>/<folder>.md` (Plugin: Folder Notes).\n"
                + "- **Sortierung** ueber Frontmatter `sorting-spec` in jeder Folder-Note (Plugin: Custom File Explorer Sorting).\n"
                + "  Reihenfolge folgt der Lese-Reihenfolge im Buch.\n"
                + "- **Wikilinks** intra-book: Note-Namen werden im Body zu `[[Pfad|Display]]`.\n"
                + "- **Buch-Hierarchie**: Kategorie → Buch → Kapitel-Banner → Sub-Sections.\n\n"
                + "## Top-Level-Reihenfolge\n\n";
        StringBuilder sb = new StringBuilder();
        sb.append("---\n");
        sb.append("sort-index: 9999\n");
        sb.append("---\n");
        sb.append(body);
        for (String cat : categoryFolders) sb.append("- [[").append(cat).append("]]\n");
        Files.writeString(readme, sb.toString(), StandardCharsets.UTF_8);
    }

    // ---------- Wikilinks --------------------------------------------------

    static String applyWikilinks(String body, Map<String, String> noteIndex, Path currentFile) {
        if (noteIndex == null || noteIndex.isEmpty() || body.isBlank()) return body;
        String selfRel = TARGET.relativize(currentFile).toString().replace('\\', '/');
        if (selfRel.endsWith(".md")) selfRel = selfRel.substring(0, selfRel.length() - 3);

        List<String> placeholders = new ArrayList<>();
        Pattern protectPat = Pattern.compile(
                "(`[^`]+`|\\[\\[[^\\]]+\\]\\]|\\[[^\\]]+\\]\\([^)]+\\))");
        StringBuffer maskBuf = new StringBuffer();
        Matcher pm = protectPat.matcher(body);
        while (pm.find()) {
            placeholders.add(pm.group(1));
            pm.appendReplacement(maskBuf,
                    Matcher.quoteReplacement(" PH" + (placeholders.size() - 1) + " "));
        }
        pm.appendTail(maskBuf);
        String working = maskBuf.toString();

        List<Map.Entry<String, String>> entries = new ArrayList<>(noteIndex.entrySet());
        entries.sort((a, b) -> Integer.compare(b.getKey().length(), a.getKey().length()));
        if (entries.isEmpty()) return body;
        StringBuilder alt = new StringBuilder();
        for (var e : entries) {
            if (alt.length() > 0) alt.append('|');
            alt.append(Pattern.quote(e.getKey()));
        }
        Pattern multi = Pattern.compile(
                "(?<![\\p{L}\\p{Nd}])(" + alt + ")(?![\\p{L}\\p{Nd}])",
                Pattern.CASE_INSENSITIVE);

        StringBuffer out = new StringBuffer();
        Matcher mm = multi.matcher(working);
        while (mm.find()) {
            String matched = mm.group(1);
            String relPath = noteIndex.get(matched.toLowerCase());
            if (relPath == null || relPath.equalsIgnoreCase(selfRel)) {
                mm.appendReplacement(out, Matcher.quoteReplacement(matched));
                continue;
            }
            String replacement = "[[" + relPath + "|" + matched + "]]";
            mm.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        mm.appendTail(out);
        working = out.toString();

        for (int i = 0; i < placeholders.size(); i++) {
            working = working.replace(" PH" + i + " ", placeholders.get(i));
        }
        return working;
    }

    // ---------- Naming Helpers --------------------------------------------

    static String stripBookKey(String dirName) {
        Matcher m = BOOK_DIR.matcher(dirName);
        if (m.matches()) return m.group(1).trim();
        return dirName;
    }

    static String categoryPrefix(String categoryDir) {
        Matcher m = CATEGORY_PREFIX.matcher(categoryDir);
        if (m.matches()) {
            String num = m.group(1);
            return String.format("%02d", Integer.parseInt(num));
        }
        return null;
    }

    static String sanitize(String s) {
        String cleaned = s.replaceAll("[\\\\/:*?\"<>|]", " ")
                .replaceAll("\\s+", " ")
                .strip();
        if (cleaned.isEmpty()) cleaned = "Section";
        if (cleaned.length() > MAX_FILENAME_LEN) {
            cleaned = cleaned.substring(0, MAX_FILENAME_LEN).strip();
        }
        while (cleaned.endsWith(".") || cleaned.endsWith(" ")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }
        if (cleaned.isEmpty()) cleaned = "Section";
        return cleaned;
    }

    static String uniqueName(String base, Set<String> used) {
        String key = base.toLowerCase();
        if (!used.contains(key)) return base;
        int n = 2;
        while (true) {
            String candidate = base + " (" + n + ")";
            if (!used.contains(candidate.toLowerCase())) return candidate;
            n++;
        }
    }

    static void cleanDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                    .filter(p -> !p.equals(dir))
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); }
                        catch (IOException e) { throw new RuntimeException(e); }
                    });
        }
    }

    // ---------- Models ----------------------------------------------------

    static class Section {
        final int level;
        final String title;
        String body = "";
        final List<Section> children = new ArrayList<>();
        Section(int level, String title) { this.level = level; this.title = title; }
    }

    static class BookSpec {
        final String bookName;
        final Path volltextPath;
        final String rawCategory;
        Section tree;
        Path vaultRelDir;
        Map<String, String> noteIndex;
        BookSpec(String bookName, Path volltextPath, String rawCategory) {
            this.bookName = bookName;
            this.volltextPath = volltextPath;
            this.rawCategory = rawCategory;
        }
    }

    static class CategorySpec {
        final String rawName;
        final String cleanName;
        final int sortPrimary;
        final String sortSecondary;
        final List<BookSpec> books;
        String cleanCategoryName;
        CategorySpec(String rawName, List<BookSpec> books) {
            this.rawName = rawName;
            this.books = books;
            Matcher m = CATEGORY_PREFIX.matcher(rawName);
            if (m.matches()) {
                this.sortPrimary = Integer.parseInt(m.group(1));
                this.sortSecondary = m.group(2);
                this.cleanName = m.group(3).trim();
            } else {
                this.sortPrimary = Integer.MAX_VALUE;
                this.sortSecondary = "";
                this.cleanName = rawName;
            }
            this.cleanCategoryName = this.cleanName;
        }
    }

    static class Counter { int count; }
}
