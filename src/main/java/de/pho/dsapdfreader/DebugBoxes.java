package de.pho.dsapdfreader;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.pho.dsapdfreader.book.BoxExtractor;
import de.pho.dsapdfreader.markdown.RawPageData;
import java.nio.file.Path;

public class DebugBoxes {
    public static void main(String[] args) throws Exception {
        Path p = Path.of(String.format(
                "export/markdown/raw/01 - Regeln/Archiv der Damonen (178)/Archiv der Damonen - 01 - Regeln/page_%03d.json",
                Integer.parseInt(args[0])));
        RawPageData page = new ObjectMapper().readValue(p.toFile(), RawPageData.class);
        BoxExtractor be = new BoxExtractor();
        var boxes = be.identifyBoxes(page);
        System.out.println("identifyBoxes: " + boxes.size());
        for (var b : boxes) System.out.printf("  before split: y=%.1f x=%.1f w=%.1f h=%.1f%n", b.y, b.x, b.width, b.height);
        var sr = be.split(page);
        System.out.println("split.boxes: " + sr.boxes.size());
        for (var b : sr.boxes) System.out.printf("  after split: y=%.1f x=%.1f w=%.1f h=%.1f chars=%d%n", b.y, b.x, b.width, b.height, b.chars.size());
    }
}
