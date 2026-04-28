package de.pho.dsapdfreader.book;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Standalone-Runner fuer den {@link BookInterpreter}.
 *
 * <p>Iteriert ueber alle Buch-Verzeichnisse in {@code export/markdown/raw/} und
 * erzeugt fuer jedes ein {@code <Buchtitel>.md} + {@code _structure.json} im
 * entsprechenden Verzeichnis unter {@code export/markdown/text/}.
 *
 * <p>CLI-Argumente (optional):
 * <ul>
 *   <li>{@code --filter=Kodex} — verarbeite nur Buecher, deren Verzeichnisname diesen
 *       Substring enthaelt</li>
 *   <li>{@code --raw=path/to/raw} — alternative RAW-Wurzel (default: export/markdown/raw)</li>
 *   <li>{@code --text=path/to/text} — alternative TEXT-Wurzel (default: export/markdown/text)</li>
 * </ul>
 */
public class BookInterpreterMain {

    private static final String RAW_DEFAULT = "export/markdown/raw";
    private static final String TEXT_DEFAULT = "export/markdown/text";

    /** Erkennt Buch-Verzeichnisnamen wie "Kodex des Schwertes (171)" oder "Regelwerk (152)" */
    private static final Pattern P_BOOK_DIR = Pattern.compile("^(.+?)(?:\\s*\\((\\d+)\\))?$");

    public static void main(String[] args) throws IOException {
        Path rawRoot = Paths.get(RAW_DEFAULT);
        Path textRoot = Paths.get(TEXT_DEFAULT);
        String filter = null;
        for (String a : args) {
            if (a.startsWith("--filter=")) filter = a.substring("--filter=".length());
            else if (a.startsWith("--raw=")) rawRoot = Paths.get(a.substring("--raw=".length()));
            else if (a.startsWith("--text=")) textRoot = Paths.get(a.substring("--text=".length()));
        }

        if (!Files.isDirectory(rawRoot)) {
            System.err.println("RAW-Wurzel nicht gefunden: " + rawRoot.toAbsolutePath());
            System.exit(1);
        }

        BookInterpreter interpreter = new BookInterpreter();
        List<Path> bookDirs = findBookDirectories(rawRoot);
        int processed = 0;
        for (Path rawBookDir : bookDirs) {
            String dirName = rawBookDir.getFileName().toString();
            if (filter != null && !dirName.toLowerCase().contains(filter.toLowerCase())) continue;

            BookInterpreter.Args bargs = new BookInterpreter.Args();
            bargs.rawBookDir = rawBookDir;
            // textRoot/<relativer pfad>
            Path rel = rawRoot.relativize(rawBookDir);
            bargs.textBookDir = textRoot.resolve(rel);
            bargs.title = parseTitle(dirName);
            bargs.publicationCode = slugify(bargs.title);

            System.out.printf("Buch: %s -> %s%n", rawBookDir, bargs.textBookDir);
            try {
                BookStructure result = interpreter.run(bargs);
                int blockCount = countBlocks(result);
                System.out.printf("  fertig: %d Hierarchie-Knoten, %d Bloecke%n",
                        result.hierarchy.size(), blockCount);
                processed++;
            } catch (Exception e) {
                System.err.printf("  FEHLER: %s%n", e.getMessage());
                e.printStackTrace(System.err);
            }
        }
        System.out.printf("Verarbeitet: %d Buecher%n", processed);
    }

    private static List<Path> findBookDirectories(Path rawRoot) throws IOException {
        try (Stream<Path> walk = Files.walk(rawRoot)) {
            return walk
                    .filter(Files::isDirectory)
                    .filter(BookInterpreterMain::hasPageJsons)
                    .sorted()
                    .toList();
        }
    }

    private static boolean hasPageJsons(Path dir) {
        try (Stream<Path> walk = Files.list(dir)) {
            return walk.anyMatch(p -> p.getFileName().toString().matches("page_\\d+\\.json"));
        } catch (IOException e) {
            return false;
        }
    }

    private static String parseTitle(String dirName) {
        Matcher m = P_BOOK_DIR.matcher(dirName);
        if (m.matches()) return m.group(1).trim();
        return dirName;
    }

    private static String slugify(String text) {
        if (text == null) return "";
        String s = text.toLowerCase()
                .replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").replace("ß", "ss");
        s = s.replaceAll("[^a-z0-9_]+", "_").replaceAll("_+", "_").replaceAll("^_|_$", "");
        return s;
    }

    private static int countBlocks(BookStructure s) {
        int[] count = {0};
        countBlocksRec(s.hierarchy, count);
        return count[0];
    }

    private static void countBlocksRec(List<BookStructure.HierarchyNode> nodes, int[] count) {
        for (BookStructure.HierarchyNode n : nodes) {
            count[0] += n.blocks.size();
            countBlocksRec(n.children, count);
        }
    }
}
