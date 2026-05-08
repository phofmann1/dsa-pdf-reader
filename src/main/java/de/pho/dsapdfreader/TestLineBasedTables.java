package de.pho.dsapdfreader;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.pho.dsapdfreader.markdown.LineBasedTableDetector;
import de.pho.dsapdfreader.markdown.RawPageData;

import java.nio.file.Path;
import java.util.List;

/** One-shot tester: laedt eine Seite und gibt erkannte line-basierte Tabellen aus. */
public class TestLineBasedTables {
    public static void main(String[] args) throws Exception {
        ObjectMapper m = new ObjectMapper();
        for (int pn : new int[]{26, 38}) {
            Path p = Path.of(String.format(
                    "export/markdown/raw/01 - Regeln/Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln/page_%03d.json",
                    pn));
            RawPageData page = m.readValue(p.toFile(), RawPageData.class);
            List<LineBasedTableDetector.LineBasedTable> tables = LineBasedTableDetector.detect(page);
            System.out.println("=== Page " + pn + ": " + tables.size() + " tables ===");
            for (int t = 0; t < tables.size(); t++) {
                LineBasedTableDetector.LineBasedTable tbl = tables.get(t);
                System.out.println(String.format("Table %d: y=%.0f..%.0f x=%.0f..%.0f cols=%d rows=%d",
                        t, tbl.yTop, tbl.yBottom, tbl.xLeft, tbl.xRight, tbl.colCount(), tbl.rowCount()));
                for (List<String> row : tbl.cells) System.out.println("  | " + String.join(" | ", row) + " |");
            }
        }
    }
}
