package de.pho.dsapdfreader;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.pho.dsapdfreader.markdown.RawPageData;
import de.pho.dsapdfreader.markdown.TextInterpreter;

/**
 * Regenerates all text files from raw JSON data.
 * Does NOT need PDF files - reads only from export/markdown/raw/
 */
public class RegenerateText
{
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String RAW_BASE = "export/markdown/raw";
    private static final String TEXT_BASE = "export/markdown/text";

    public static void main(String[] args) throws Exception
    {
        Path rawBase = Paths.get(RAW_BASE);
        Path textBase = Paths.get(TEXT_BASE);
        TextInterpreter interpreter = new TextInterpreter();

        int totalDirs = 0;
        int totalPages = 0;

        // Walk raw directories to find all book directories (containing page_*.json)
        try (Stream<Path> walk = Files.walk(rawBase))
        {
            List<Path> rawDirs = walk
                .filter(Files::isDirectory)
                .filter(dir -> {
                    try (Stream<Path> files = Files.list(dir))
                    {
                        return files.anyMatch(f -> f.getFileName().toString().matches("page_\\d+\\.json"));
                    }
                    catch (IOException e)
                    {
                        return false;
                    }
                })
                .sorted()
                .toList();

            for (Path rawDir : rawDirs)
            {
                Path relPath = rawBase.relativize(rawDir);
                Path textDir = textBase.resolve(relPath);
                Files.createDirectories(textDir);

                // Source path for comments
                String sourcePath = relPath.toString().replace("/", "\\");

                List<Path> rawFiles;
                try (Stream<Path> files = Files.list(rawDir))
                {
                    rawFiles = files
                        .filter(p -> p.getFileName().toString().matches("page_\\d+\\.json"))
                        .sorted()
                        .toList();
                }

                StringBuilder fullText = new StringBuilder();
                fullText.append("# Quelle: ").append(sourcePath).append("\n\n");

                for (Path rawFile : rawFiles)
                {
                    RawPageData page = MAPPER.readValue(rawFile.toFile(), RawPageData.class);
                    String markdown = interpreter.interpretPage(page);

                    Path pageFile = textDir.resolve(String.format("seite_%03d.md", page.pageNumber));
                    try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(pageFile, StandardCharsets.UTF_8)))
                    {
                        pw.println("# Seite " + page.pageNumber);
                        pw.println("<!-- Quelle: " + sourcePath + ".pdf -->");
                        pw.println();
                        pw.print(markdown);
                    }

                    fullText.append("\n---\n# Seite ").append(page.pageNumber).append("\n\n");
                    fullText.append(markdown);
                    totalPages++;
                }

                // Volltext
                Path fullFile = textDir.resolve("_volltext.md");
                Files.writeString(fullFile, fullText.toString(), StandardCharsets.UTF_8);

                totalDirs++;
                if (totalDirs % 50 == 0)
                {
                    System.out.println("  ... " + totalDirs + " books, " + totalPages + " pages ...");
                }
            }
        }

        System.out.println("Done: " + totalDirs + " books, " + totalPages + " pages regenerated.");
    }
}
