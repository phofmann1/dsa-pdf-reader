package de.pho.dsapdfreader;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.pho.dsapdfreader.markdown.RawPageData;
import de.pho.dsapdfreader.markdown.TextInterpreter;

import java.nio.file.Path;

public class RenderOnePage {
    public static void main(String[] args) throws Exception {
        int pn = Integer.parseInt(args[0]);
        Path p = Path.of(String.format(
                "export/markdown/raw/01 - Regeln/Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln/page_%03d.json",
                pn));
        RawPageData page = new ObjectMapper().readValue(p.toFile(), RawPageData.class);
        TextInterpreter ti = new TextInterpreter();
        ti.setEmitBoxesInline(false);
        System.out.println(ti.interpretPage(page));
    }
}
