package de.pho.dsapdfreader.book;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Snapshot-Tests fuer den seitenweisen Markdown-Output des BookInterpreters.
 *
 * <p>Pro Buch ein Verzeichnis unter {@code src/test/resources/snapshots/<buch>/}
 * mit erwarteten {@code seite_NNN.md}. Der Test vergleicht jede Snapshot-Datei
 * gegen die aktuell unter {@code export/markdown/text/...} produzierte Datei.
 *
 * <p>Aktualisierung des Snapshots nach bewusster Aenderung:
 * <pre>-Dsnapshot.update=true</pre>
 *
 * <p>Wichtig: Aktualisierung ueberschreibt blind. Vorher Diff anschauen.
 */
class BookSnapshotTest {

    private static final Path EXPECTED_BASE = Path.of("src/test/resources/snapshots");

    /** publication-id -> Pfad zum aktuellen Markdown-Output */
    private static final java.util.Map<String, Path> ACTUAL_DIRS = java.util.Map.of(
            "schwertes",
            Path.of("export/markdown/text/01 - Regeln/Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln")
    );

    private static final boolean UPDATE = Boolean.parseBoolean(System.getProperty("snapshot.update", "false"));

    @TestFactory
    List<DynamicTest> snapshots() throws IOException {
        List<DynamicTest> all = new ArrayList<>();
        for (var entry : ACTUAL_DIRS.entrySet()) {
            String book = entry.getKey();
            Path expectedDir = EXPECTED_BASE.resolve(book);
            Path actualDir = entry.getValue();
            if (!Files.isDirectory(expectedDir)) {
                all.add(DynamicTest.dynamicTest(book + "/<no-snapshots>",
                        () -> { throw new AssertionError("Snapshot-Verzeichnis fehlt: " + expectedDir.toAbsolutePath()); }));
                continue;
            }
            try (Stream<Path> stream = Files.list(expectedDir)) {
                stream.filter(p -> p.getFileName().toString().matches("seite_\\d{3}\\.md"))
                        .sorted()
                        .forEach(expected -> all.add(DynamicTest.dynamicTest(
                                book + "/" + expected.getFileName().toString(),
                                () -> compareOrUpdate(expected, actualDir.resolve(expected.getFileName().toString())))));
            }
        }
        if (all.isEmpty()) {
            all.add(DynamicTest.dynamicTest("no-snapshots-configured",
                    () -> { throw new AssertionError("Keine Snapshots gefunden."); }));
        }
        return all;
    }

    private void compareOrUpdate(Path expected, Path actual) throws IOException {
        if (!Files.exists(actual)) {
            throw new AssertionError("Actual-Datei fehlt: " + actual.toAbsolutePath()
                    + " — Pipeline laufen lassen und erneut testen.");
        }
        String actualText = Files.readString(actual, StandardCharsets.UTF_8);
        if (UPDATE) {
            Files.writeString(expected, actualText, StandardCharsets.UTF_8);
            return;
        }
        String expectedText = Files.readString(expected, StandardCharsets.UTF_8);
        if (!expectedText.equals(actualText)) {
            throw new AssertionError(snapshotDiffMessage(expected, expectedText, actualText));
        }
    }

    private String snapshotDiffMessage(Path expected, String expectedText, String actualText) {
        int firstDiff = -1;
        int min = Math.min(expectedText.length(), actualText.length());
        for (int i = 0; i < min; i++) {
            if (expectedText.charAt(i) != actualText.charAt(i)) { firstDiff = i; break; }
        }
        if (firstDiff < 0) firstDiff = min;
        int from = Math.max(0, firstDiff - 40);
        int toExp = Math.min(expectedText.length(), firstDiff + 80);
        int toAct = Math.min(actualText.length(), firstDiff + 80);
        return "Snapshot-Drift in " + expected.getFileName() + " ab Offset " + firstDiff + "\n"
                + "expected: ..." + expectedText.substring(from, toExp).replace("\n", "\\n") + "...\n"
                + "actual  : ..." + actualText.substring(from, toAct).replace("\n", "\\n") + "...\n"
                + "Mit -Dsnapshot.update=true bestaetigen.";
    }
}
