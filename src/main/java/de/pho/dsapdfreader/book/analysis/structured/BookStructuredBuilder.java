package de.pho.dsapdfreader.book.analysis.structured;

import de.pho.dsapdfreader.book.analysis.BookFontClusterAnalyzer;
import de.pho.dsapdfreader.book.analysis.BookFontClusters;
import de.pho.dsapdfreader.book.analysis.FontStyleKey;
import de.pho.dsapdfreader.markdown.RawPageData;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Buchweite Pipeline:
 * <ol>
 *   <li>Pass 1: Cluster-Analyse ueber alle Seiten ({@link BookFontClusterAnalyzer})</li>
 *   <li>Pass 2: pro Seite Linien klassifizieren ({@link StructuredPageBuilder})</li>
 *   <li>Pass 3: Hierarchie aus Heading-Tiers aggregieren — <b>seitenuebergreifend</b>.
 *       Heading-Knoten mit den Pflichtfeldern {@code voraussetzung} + {@code ap-wert}/{@code kosten}
 *       im Body werden zu atomaren {@link StructuredBlock}s promoviert (Sonderfertigkeit etc.).</li>
 * </ol>
 */
public class BookStructuredBuilder {

    public static final String GENERATOR_VERSION = "structured-v1.1";

    /** Field-Marker am Zeilenanfang: "Wuchtschlag:" / "AP-Wert:" / ... */
    private static final Pattern P_FIELD_PREFIX = Pattern.compile(
            "^([A-ZÄÖÜ][a-zA-ZäöüÄÖÜß\\-]{1,40}?):\\s*(.*)$");

    /** Stufen-Range am Ende eines Heading-Namens: "Wuchtschlag I-III", "Heilkraft I/II/III", "Auf Distanz halten I-II". */
    private static final Pattern P_STAGE_RANGE = Pattern.compile(
            "\\s+([IVX]{1,5})(?:\\s*[-/]\\s*([IVX]{1,5}))+\\s*$");
    private static final Pattern P_TRAILING_STAGE = Pattern.compile(
            "\\s+[IVX]{1,5}(?:\\s*[-/]\\s*[IVX]{1,5})+\\s*$");

    /** Heading-Name ist Muell (Page-Number, Tabellen-Header, Klammer-Untertitel). */
    private static boolean isJunkHeading(String title) {
        if (title == null) return true;
        String t = title.trim();
        if (t.isEmpty()) return true;
        // Klammer-Untertitel: "(Anwendungsgebiet)", "(Kampftechnik)"
        if (t.startsWith("(")) return true;
        // Reine Ziffern oder Page-Marker: "155", "250 Kapitel 7: Sonderfertigkeiten"
        if (t.matches("\\d+(\\s+Kapitel\\s+\\d+.*)?")) return true;
        // Kapitel-Header die mit Ziffer + "Kapitel" anfangen: "9 Sozialer Stand 87"
        if (t.matches("^\\d+\\s+.*\\s+\\d+$")) return true;
        // Tabellen-Header-Heuristik: SEHR viele kurze Tokens. Schwelle hoch
        // angesetzt, damit echte SF-Namen mit kurzen Woertern wie "Guter Gardist,
        // boeser Gardist" oder "Meister der improvisierten Waffen" nicht
        // faelschlich gefiltert werden.
        String stripped = t
                .replaceAll("\\s*\\([a-zA-ZäöüÄÖÜ\\s]+\\)\\s*$", "")            // (passiv)
                .replaceAll("\\s+[IVX]{1,5}(\\s*[-/]\\s*[IVX]{1,5})*\\s*$", "") // I-II / I/II/III
                .trim();
        if (stripped.isEmpty()) return true;
        String[] tokens = stripped.split("\\s+");
        if (tokens.length >= 6) {
            int chars = 0;
            for (String tok : tokens) chars += tok.length();
            if ((double) chars / tokens.length < 7.0) return true;
        }
        return false;
    }

    public BookStructured build(String publication, String title, Integer dsaVersion,
                                List<RawPageData> pages) {
        return build(publication, title, dsaVersion, pages, NameMapping.empty());
    }

    public BookStructured build(String publication, String title, Integer dsaVersion,
                                List<RawPageData> pages, NameMapping mapping) {
        // Pass 1: Cluster
        BookFontClusters clusters = new BookFontClusterAnalyzer().analyze(pages);

        // Pass 2: Linien pro Seite (Reading-Order pro Seite intern)
        StructuredPageBuilder pageBuilder = new StructuredPageBuilder();
        Map<Integer, List<StructuredLine>> linesByPage = new LinkedHashMap<>();
        List<StructuredLine> allLines = new ArrayList<>();
        int readingIndex = 0;
        for (RawPageData p : pages) {
            List<StructuredLine> lines = pageBuilder.build(p, clusters);
            // Heading-Reflow: zwei aufeinanderfolgende Heading-Linien gleichen
            // Tiers in derselben Spalte mit kleinem y-Abstand sind ein Wort-
            // umbruch (z.B. "Machtvoller" + "Wirbelangriff" → "Machtvoller
            // Wirbelangriff", "Erweiterte Drachenkampf-" + "Taktik" → "Erweiterte
            // Drachenkampf-Taktik").
            lines = reflowHeadings(lines);
            for (StructuredLine l : lines) {
                l.readingOrder = readingIndex++;
                allLines.add(l);
            }
            linesByPage.put(p.pageNumber, lines);
        }

        // Pass 3: Hierarchie + Block-Promotion
        BookStructured result = new BookStructured();
        result.publication = publication;
        result.title = title;
        result.dsaVersion = dsaVersion;
        result.pages = pages.size();
        result.generatedAt = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        result.generatorVersion = GENERATOR_VERSION;

        for (FontStyleKey k : clusters.orderedKeys()) {
            result.clusters.add(BookStructured.ClusterEntry.from(clusters.tierFor(k), k, clusters));
        }

        aggregateHierarchy(allLines, result);

        // Pass 3.5: Hierarchy-Fallback — leere Tier-5-Knoten in SF-Sections
        // sind oft SFs, deren Pflichtfelder durch PDF-Layout-Effekte (Boxen,
        // Spalten-Wraps) nicht erkannt wurden. Soft-promote zu Block.
        promoteIsolatedHierarchyNodes(result);

        // Pass 4: Komposit-Bloecke expandieren (Klammer-Variants, Body-Pattern,
        // AP-Wert-Listen). Mit Name-Mapping fuer ng-dsa-Diskrepanzen.
        expandComposites(result, mapping);

        // Pass 5: Orphan-Block-Renaming. Wenn ein Block einen "verwaisten" Namen
        // traegt (z.B. "I" als Stufen-Markierung oder "Erweiterte X" als
        // miss-erkannter Field-Marker), und der unmittelbare Hierarchy-Parent
        // einen plausiblen Ability-Namen traegt, wird der Block umbenannt.
        renameOrphanBlocks(result.hierarchy, mapping);

        for (Map.Entry<Integer, List<StructuredLine>> e : linesByPage.entrySet()) {
            result.linesByPage.put(String.valueOf(e.getKey()), e.getValue());
        }

        return result;
    }

    /**
     * Befoerdert isolierte Tier-X-Hierarchy-Knoten zu Bloecken, wenn ihr
     * Vorfahre ein SF-Container ist ("Sonderfertigkeiten" im Titel) und der
     * Knoten selbst keine Kinder/Bloecke traegt. Damit kommen Eintraege wie
     * "Abrichter" oder "Olochtai-Stil" rein, deren Pflichtfelder durch
     * Layout-Eigenheiten nicht erkennbar waren.
     */
    private static void promoteIsolatedHierarchyNodes(BookStructured book) {
        Counter blockSeq = new Counter();
        // Letzte vorhandene Block-ID-Nummer fortsetzen
        for (StructuredBlock b : book.blocks) {
            if (b.id != null && b.id.startsWith("blk_")) {
                try {
                    int n = Integer.parseInt(b.id.substring(4));
                    if (n >= blockSeq.v) blockSeq.v = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        promoteRec(book.hierarchy, false, book, blockSeq);
    }

    private static final Pattern P_SF_SECTION = Pattern.compile(
            "(?i)sonderfertigkeit|kampfstil|magiestil|liturgiestil|talentstil|themengruppe");

    private static void promoteRec(List<StructuredHierarchyNode> nodes, boolean inSfSection,
                                    BookStructured book, Counter blockSeq) {
        List<StructuredHierarchyNode> stillNodes = new ArrayList<>();
        for (StructuredHierarchyNode n : nodes) {
            boolean isOwnSectionKeyword = n.title != null && P_SF_SECTION.matcher(n.title).find();
            boolean nowInSf = inSfSection || isOwnSectionKeyword;
            // Erst rekursiv in Kinder gehen
            promoteRec(n.children, nowInSf, book, blockSeq);
            // Dann pruefen, ob wir n promoten sollen.
            // Wir erlauben Hierarchy-Children (z.B. Sub-Untertitel) — aber KEINE
            // echten Block-Children, denn dann ist n eine Section.
            // Auch: rekursiv pruefen, ob irgendein Nachkomme einen Block hat.
            boolean hasNoBlockDescendants = n.blocks.isEmpty() && !hasAnyBlockDescendant(n.children);
            // Lockerer Filter: ein SF-typisches Field MUSS erkannt sein (Regel,
            // AP-Wert oder Voraussetzung). Damit fallen TOC-Marker ohne Body
            // durch, aber echte SFs mit unvollstaendigen Feldern bleiben drin.
            // Gegen False-Positives wie "Passive, Basis- und Spezialmanoever"
            // hilft die buch-spezifische ignore-Liste im Mapping.
            boolean hasAnySfField = n.ownFieldKeys.contains("ap-wert")
                    || n.ownFieldKeys.contains("kosten")
                    || n.ownFieldKeys.contains("voraussetzung")
                    || n.ownFieldKeys.contains("regel");
            boolean shouldPromote = inSfSection && hasNoBlockDescendants
                    && hasAnySfField
                    && !isOwnSectionKeyword
                    && !isJunkHeading(n.title)
                    && isPlausibleAbilityName(n.title);
            if (shouldPromote) {
                // Stage-Splitting auch im Fallback ("Beistand der Goetter I/II" → "I" + "II")
                int[] stages = parseStageRange(n.title);
                String[] names = stages.length > 0
                        ? expandStageNames(n.title, stages)
                        : new String[]{n.title};
                for (String name : names) {
                    StructuredBlock blk = new StructuredBlock();
                    blk.id = String.format("blk_%05d", blockSeq.next());
                    blk.kind = "ability_inferred";
                    blk.tier = n.tier;
                    blk.name = name;
                    blk.startPage = n.startPage;
                    blk.endPage = n.endPage;
                    book.blocks.add(blk);
                }
            } else {
                stillNodes.add(n);
            }
        }
        nodes.clear();
        nodes.addAll(stillNodes);
    }

    private static boolean hasAnyBlockDescendant(List<StructuredHierarchyNode> nodes) {
        for (StructuredHierarchyNode n : nodes) {
            if (!n.blocks.isEmpty()) return true;
            if (hasAnyBlockDescendant(n.children)) return true;
        }
        return false;
    }

    /** Plausibilitaets-Check fuer einen Ability-Namen (verhindert Page-Header etc.). */
    private static boolean isPlausibleAbilityName(String title) {
        if (title == null || title.isBlank()) return false;
        String t = title.trim();
        // Anhang/Glossar-Marker
        if (t.toLowerCase().contains("anhang")) return false;
        if (t.toLowerCase().contains("glossar")) return false;
        // Generische Container-Begriffe
        if (t.equalsIgnoreCase("Nahkampfwaffen") || t.equalsIgnoreCase("Fernkampfwaffen")) return false;
        // Anfang mit Ziffer (Page-Marker)
        if (t.matches("^\\d+\\s.*")) return false;
        // Sehr kurze Namen (1-2 Zeichen) sind Reflow-Reste
        if (t.length() < 3) return false;
        return true;
    }

    /**
     * Klassifiziert die "Art" eines Blocks anhand der erkannten Pflichtfelder.
     * Liturgien und Zauber sind eigene Entity-Typen — sie sollten nicht in den
     * ability-Datensatz fliessen.
     */
    private static String inferBlockKind(Map<String, String> fields) {
        // Liturgie: liturgiezeit ODER aspekt (Geweihten-Aspekt) — beides ist im
        // jeweiligen Buch der primaere Discriminator. Im Kodex des Goetterwirkens
        // wird "Aspekt:" konsistent als Liturgie-Marker verwendet.
        if (fields.containsKey("liturgiezeit") || fields.containsKey("aspekt")) return "liturgy";
        if (fields.containsKey("probe") && fields.containsKey("wirkung")
                && (fields.containsKey("reichweite") || fields.containsKey("wirkungsdauer"))) {
            return "spell";
        }
        return "ability";
    }

    /**
     * Verwaiste Block-Namen (z.B. "I", "Erweiterte X") durch den
     * Heading-Parent-Namen ersetzen. Tritt auf, wenn ein Field-Marker oder
     * ein Stufe-Token faelschlich als eigenes Heading interpretiert wurde.
     */
    private static void renameOrphanBlocks(List<StructuredHierarchyNode> nodes, NameMapping mapping) {
        for (StructuredHierarchyNode n : nodes) {
            for (StructuredBlock b : n.blocks) {
                if (!isOrphanBlockName(b.name)) continue;
                String parentName = n.title == null ? "" : n.title;
                // Operational-Marker am Parent-Namen abstreifen
                while (true) {
                    String stripped = CompositeExpander.stripOperational(parentName);
                    if (stripped.equals(parentName)) break;
                    parentName = stripped;
                }
                if (parentName.isEmpty() || isOrphanBlockName(parentName)) continue;
                // Mapping anwenden, falls vorhanden
                NameMapping.Entry me = mapping == null ? null : mapping.findFor(parentName);
                if (me != null && me.ngDsaMaster() != null) {
                    parentName = me.ngDsaMaster();
                }
                b.name = parentName;
            }
            renameOrphanBlocks(n.children, mapping);
        }
    }

    private static boolean isOrphanBlockName(String name) {
        if (name == null || name.isBlank()) return true;
        String t = name.trim();
        if (t.matches("^[IVX]{1,5}$")) return true;          // Stufen-only
        if (t.startsWith("Erweiterte ")) return true;        // Field-Marker
        if (t.startsWith("Voraussetzung")) return true;      // Field-Marker
        if (t.length() <= 2) return true;                    // "I", "BK", ...
        return false;
    }

    private static void expandComposites(BookStructured book, NameMapping mapping) {
        CompositeExpander expander = new CompositeExpander();
        List<StructuredBlock> expanded = new ArrayList<>(book.blocks.size());
        for (StructuredBlock b : book.blocks) {
            expanded.addAll(expander.expand(b, mapping));
        }
        // Ignore-Liste anwenden: Block-Namen die in der Mapping-Datei als
        // _ignore gelistet sind, aus den ability-Output entfernen.
        if (mapping != null) {
            expanded.removeIf(b -> mapping.isIgnored(b.name));
        }
        book.blocks = expanded;
        // Auch die Hierarchie-Knoten-Bloecke aktualisieren (referenzieren dieselben Block-Instanzen
        // — die wurden in-place per Name-Update auch geaendert; bei Expansion entstehen neue Blocks,
        // die jedoch nur in book.blocks landen. Hierarchy-children sehen den Master umbenannt).
    }

    /**
     * Aggregiert Linien zu einem Hierarchie-Baum mit Block-Promotion.
     * <p>
     * Ablauf:
     * <ul>
     *   <li>Heading-Tiers (1+) bilden Hierarchie. Stack-basiert: ein Heading
     *       schliesst alle offenen Knoten gleichen oder hoeheren Tiers.</li>
     *   <li>Body-Linien (tier 0 oder unbekannt) werden dem obersten offenen
     *       Knoten als Body-Inhalt zugeordnet. Felder werden im Knoten
     *       gesammelt.</li>
     *   <li>Beim Schliessen eines Knotens: hat er {@code voraussetzung} +
     *       ({@code ap-wert} | {@code kosten})? Dann wird er zu einem Block
     *       promoviert; sonst bleibt er Hierarchie-Knoten.</li>
     * </ul>
     */
    private void aggregateHierarchy(List<StructuredLine> lines, BookStructured book) {
        Counter blockSeq = new Counter();
        Counter hierSeq = new Counter();
        List<Pending> stack = new ArrayList<>();

        for (StructuredLine line : lines) {
            if (line.tier <= 0) {
                // body — gehoert zum obersten offenen Knoten
                if (!stack.isEmpty()) {
                    Pending top = stack.get(stack.size() - 1);
                    top.bodyLines.add(line);
                    top.endPage = Math.max(top.endPage, line.page);
                }
                continue;
            }

            // Muell-Headings (Page-Numbers, Kapitel-Marker, Klammer-Untertitel,
            // Tabellen-Header) als Body-Linien behandeln — nicht eigenen Knoten oeffnen.
            if (isJunkHeading(line.text)) {
                if (!stack.isEmpty()) {
                    Pending top = stack.get(stack.size() - 1);
                    top.bodyLines.add(line);
                    top.endPage = Math.max(top.endPage, line.page);
                }
                continue;
            }

            // Heading-Tier — Stack so weit poppen, bis top.tier < line.tier
            while (!stack.isEmpty() && stack.get(stack.size() - 1).tier >= line.tier) {
                Pending closed = stack.remove(stack.size() - 1);
                finalize(closed, stack, book, blockSeq, hierSeq);
            }
            Pending p = new Pending();
            p.tier = line.tier;
            p.title = line.text;
            p.startPage = line.page;
            p.endPage = line.page;
            p.headingLine = line;
            stack.add(p);
        }
        while (!stack.isEmpty()) {
            Pending closed = stack.remove(stack.size() - 1);
            finalize(closed, stack, book, blockSeq, hierSeq);
        }
    }

    /** Schliesst einen Pending-Knoten — promoviert zu Block oder zu Hierarchy-Knoten. */
    private void finalize(Pending p, List<Pending> parentStack, BookStructured book,
                          Counter blockSeq, Counter hierSeq) {
        // Felder aus den Body-Linien mehrzeilig erfassen (vor der Promotion-Entscheidung)
        captureFieldsFromPending(p);

        // Generischer "ist promotbar"-Check: ein Block muss DREI Klassen von
        // Pflichtfeldern haben:
        //   1. Kosten     — ap-wert / kosten
        //   2. Outcome    — regel / wirkung (was die SF/Paktgeschenk konkret tut)
        //   3. Context    — voraussetzung / verbreitung / kreis / kampftechnik / probe
        // Damit fallen Section-Header durch, die zwar voraussetzung+ap-wert aus
        // dem allgemeinen Erklaerungstext einsammeln, aber keinen Regel-/Wirkung-
        // Marker tragen ("Tricks", "Passive, Basis- und Spezialmanoever", ...).
        boolean hasAp = p.fields.containsKey("ap-wert") || p.fields.containsKey("kosten");
        // Outcome: regel/wirkung (klassische SFs/Paktgeschenke), aspekte/pantheon
        // (Tradition-SFs haben statt Regel die Aspekte-Liste).
        boolean hasOutcome = p.fields.containsKey("regel")
                || p.fields.containsKey("wirkung")
                || p.fields.containsKey("aspekte")
                || p.fields.containsKey("pantheon");
        boolean hasContext = p.fields.containsKey("voraussetzung")
                || p.fields.containsKey("verbreitung")
                || p.fields.containsKey("kreis")
                || p.fields.containsKey("kampftechnik")
                || p.fields.containsKey("probe");
        boolean hasVor = hasAp && hasOutcome && hasContext;
        // kind-Klassifikation anhand der Pflichtfeld-Kombination:
        //   liturgiezeit → Liturgie (NICHT als ability)
        //   probe + wirkung + (reichweite/wirkungsdauer) → Zauber (NICHT als ability)
        //   verbreitung + kreis → Paktgeschenk (zaehlt als ability fuer Magie-Buecher)
        //   voraussetzung + ap-wert → klassische SF
        String inferredKind = inferBlockKind(p.fields);

        // Section-Header-Schutz: ein Heading mit echten Child-Bloecken (= Sub-SFs)
        // ist eine Section, keine Sonderfertigkeit. Pflichtfelder im Body kommen
        // dann ueblicherweise vom ersten Child-Block durch Reading-Order-Effekte.
        // Hierarchie-Children allein sind kein Section-Indikator — ein SF darf
        // sub-Hierarchien tragen (z.B. Variants oder Stufen-Sub-Headings).
        boolean hasChildBlocks = !p.blocks.isEmpty();

        if (hasVor && hasAp && !hasChildBlocks) {
            // Operational Type-Marker (passiv/aktiv/...) abtrennen — Variant-Marker bleiben
            // erhalten (werden vom CompositeExpander interpretiert).
            String cleanTitle = p.title;
            while (true) {
                String stripped = CompositeExpander.stripOperational(cleanTitle);
                if (stripped.equals(cleanTitle)) break;
                cleanTitle = stripped;
            }
            String typeMarker = p.headingLine != null ? p.headingLine.typeMarker : null;

            // Stufen-Range im Namen ("Wuchtschlag I-III", "Heilkraft I/II/III") → mehrere Bloecke
            int[] stages = parseStageRange(cleanTitle);
            String[] names = stages.length > 0
                    ? expandStageNames(cleanTitle, stages)
                    : new String[]{cleanTitle};

            for (String name : names) {
                StructuredBlock blk = new StructuredBlock();
                blk.id = String.format("blk_%05d", blockSeq.next());
                blk.kind = inferredKind;
                blk.tier = p.tier;
                blk.name = name;
                blk.typeMarker = typeMarker;
                blk.startPage = p.startPage;
                blk.endPage = p.endPage;
                blk.fields = new LinkedHashMap<>(p.fields);
                blk.bodyText = p.bodyText;
                blk.lines.add(p.headingLine);
                blk.lines.addAll(p.bodyLines);
                blk.lines.addAll(collectAllLines(p.children, p.blocks));
                book.blocks.add(blk);
                if (!parentStack.isEmpty()) {
                    parentStack.get(parentStack.size() - 1).blocks.add(blk);
                    blk.parentHierarchyId = parentStack.get(parentStack.size() - 1).id();
                    parentStack.get(parentStack.size() - 1).endPage = Math.max(
                            parentStack.get(parentStack.size() - 1).endPage, p.endPage);
                }
            }
        } else {
            StructuredHierarchyNode node = new StructuredHierarchyNode();
            node.id = "h" + String.format("%04d", hierSeq.next());
            p.assignedId = node.id;
            node.tier = p.tier;
            node.title = p.title;
            node.startPage = p.startPage;
            node.endPage = p.endPage;
            node.bodyLineCount = p.bodyLines == null ? 0 : p.bodyLines.size();
            if (p.fields != null) node.ownFieldKeys.addAll(p.fields.keySet());
            node.children.addAll(p.children);
            node.blocks.addAll(p.blocks);
            for (StructuredBlock b : p.blocks) b.parentHierarchyId = node.id;
            if (parentStack.isEmpty()) {
                book.hierarchy.add(node);
            } else {
                parentStack.get(parentStack.size() - 1).children.add(node);
                parentStack.get(parentStack.size() - 1).endPage = Math.max(
                        parentStack.get(parentStack.size() - 1).endPage, p.endPage);
            }
        }
    }

    private static List<StructuredLine> collectAllLines(List<StructuredHierarchyNode> children,
                                                        List<StructuredBlock> blocks) {
        List<StructuredLine> out = new ArrayList<>();
        for (StructuredBlock b : blocks) out.addAll(b.lines);
        for (StructuredHierarchyNode c : children) collectFromNode(c, out);
        return out;
    }

    private static void collectFromNode(StructuredHierarchyNode n, List<StructuredLine> out) {
        for (StructuredBlock b : n.blocks) out.addAll(b.lines);
        for (StructuredHierarchyNode c : n.children) collectFromNode(c, out);
    }

    /**
     * Mehrzeilige Field-Erfassung auf einem Pending-Knoten: mergt
     * aufeinanderfolgende Body-Linien zu einem Feld-Wert, bis ein neues
     * Field-Marker kommt. Setzt zusaetzlich {@code bodyText} fuer Pattern-Suche.
     */
    private static void captureFieldsFromPending(Pending p) {
        Map<String, String> fields = new LinkedHashMap<>();
        StringBuilder bodyText = new StringBuilder();
        String currentKey = null;
        StringBuilder currentVal = null;

        for (StructuredLine l : p.bodyLines) {
            if (l == null || l.text == null) continue;
            String text = l.text;
            if (bodyText.length() > 0) bodyText.append(' ');
            bodyText.append(text);

            Matcher m = P_FIELD_PREFIX.matcher(text);
            if (m.matches()) {
                if (currentKey != null) {
                    fields.merge(currentKey, currentVal.toString().trim(), (old, neu) -> old);
                }
                currentKey = normalizeFieldKey(m.group(1));
                currentVal = new StringBuilder(m.group(2).trim());
            } else if (currentKey != null) {
                if (currentVal.length() > 0) currentVal.append(' ');
                currentVal.append(text.trim());
            }
        }
        if (currentKey != null) {
            fields.merge(currentKey, currentVal.toString().trim(), (old, neu) -> old);
        }
        p.fields = fields;
        p.bodyText = bodyText.toString();
    }

    private static String normalizeFieldKey(String raw) {
        String r = raw.toLowerCase().trim();
        if (r.equals("voraussetzungen")) return "voraussetzung";
        if (r.equals("kampftechniken")) return "kampftechnik";
        if (r.equals("ap-wert")) return "ap-wert";
        return r;
    }

    /**
     * Zwischenstand eines Hierarchie-Knotens, waehrend Body und Kinder gesammelt werden.
     */
    private static class Pending {
        int tier;
        String title;
        int startPage;
        int endPage;
        StructuredLine headingLine;
        List<StructuredLine> bodyLines = new ArrayList<>();
        Map<String, String> fields = new LinkedHashMap<>();
        String bodyText;
        List<StructuredHierarchyNode> children = new ArrayList<>();
        List<StructuredBlock> blocks = new ArrayList<>();
        String assignedId;

        String id() { return assignedId; }
    }

    private static class Counter {
        int v = 0;
        int next() { return ++v; }
    }

    /** Entfernt einen Type-Marker am Ende: "Wuchtschlag (passiv)" → "Wuchtschlag". */
    private static String stripTrailingTypeMarker(String title) {
        if (title == null) return null;
        return title.replaceAll("\\s*\\([a-zA-ZäöüÄÖÜ][a-zA-ZäöüÄÖÜ\\s]{1,30}\\)\\s*$", "").trim();
    }

    /**
     * Mergt aufeinanderfolgende Heading-Linien gleichen Formats, die durch
     * Zeilenumbruch in einer mehrzeiligen Ueberschrift entstanden sind.
     * <p>
     * Regeln:
     * <ul>
     *   <li>Naechste Linie hat denselben Stil-Cluster (= dasselbe "Format"
     *       laut User-Definition) → mergen.</li>
     *   <li>Kleine Annotations-Linien dazwischen (tier &lt; 0, &lt;= 4 Zeichen,
     *       wie "BK"/"WK"-Marker) werden uebersprungen, ohne den Merge zu brechen.</li>
     *   <li>Bindestrich-Behandlung am Ende der ersten Zeile:
     *       <ul>
     *         <li>folgt ein Grossbuchstabe → echter Bindestrich zwischen Wortverbund,
     *             beibehalten + Leerzeichen ("Drachenkampf-" + "Taktik" →
     *             "Drachenkampf- Taktik")</li>
     *         <li>folgt ein Kleinbuchstabe → Silbentrennung, Bindestrich entfernen
     *             ("Heil-" + "kraft" → "Heilkraft")</li>
     *       </ul></li>
     * </ul>
     */
    private static List<StructuredLine> reflowHeadings(List<StructuredLine> lines) {
        if (lines.size() < 2) return lines;
        List<StructuredLine> result = new ArrayList<>(lines.size());
        int i = 0;
        while (i < lines.size()) {
            StructuredLine cur = lines.get(i);
            int j = i + 1;
            StringBuilder merged = new StringBuilder(cur.text);
            int lastConsumed = i;
            float lastMergeY = cur.y;
            // Skip-Buffer: kurze Annotations-Linien zwischen Heading-Halves duerfen ueberlebt werden
            while (j < lines.size()) {
                StructuredLine next = lines.get(j);
                // Nur Headings gleichen Cluster-Tiers (= gleiches Format) sind merge-Kandidaten.
                if (cur.tier <= 0) break;

                boolean isShortAnnotation = next.tier <= 0
                        && next.text != null && next.text.trim().length() <= 4;
                if (isShortAnnotation) {
                    // ueberspringen, aber Merge-Schleife weiterlaufen lassen
                    j++;
                    continue;
                }
                if (next.tier != cur.tier) break;
                // y-diff-Check: nur eng aufeinander folgende Linien mergen (typische Zeilenhoehe).
                // Ohne das wuerden zwei eigenstaendige aufeinanderfolgende Headings (Sub-Section +
                // SF wie "Belkelel-Paktgeschenke" + "Belkelels Ekstase I-IV") faelschlich gemergt.
                float yDiff = next.y - lastMergeY;
                float threshold = (cur.style != null ? cur.style.sizeBin() : 13f) * 1.5f;
                if (yDiff <= 0 || yDiff > threshold) break;
                // Merge!
                String left = merged.toString();
                String right = next.text;
                if (left.endsWith("-") && !right.isEmpty()) {
                    char first = right.charAt(0);
                    if (Character.isUpperCase(first)) {
                        // Wortverbund: Bindestrich behalten, Leerzeichen einfuegen
                        merged.setLength(0);
                        merged.append(left).append(' ').append(right);
                    } else {
                        // Silbentrennung: Bindestrich entfernen, kein Leerzeichen
                        merged.setLength(0);
                        merged.append(left, 0, left.length() - 1).append(right);
                    }
                } else {
                    merged.append(' ').append(right);
                }
                lastMergeY = next.y;
                lastConsumed = j;
                j++;
            }
            if (lastConsumed > i) {
                cur.text = merged.toString();
                cur.typeMarker = extractTypeMarkerLocal(cur.text);
                // Nicht-konsumierte Skip-Linien (kurze Annotations zwischen zwei Heading-Halves)
                // wandern auch in den Output, damit nichts verloren geht.
                result.add(cur);
                for (int k = i + 1; k <= lastConsumed; k++) {
                    StructuredLine inBetween = lines.get(k);
                    if (inBetween.tier <= 0
                            && inBetween.text != null
                            && inBetween.text.trim().length() <= 4) {
                        result.add(inBetween);
                    }
                }
                i = lastConsumed + 1;
            } else {
                result.add(cur);
                i++;
            }
        }
        return result;
    }

    private static final Pattern P_TYPE_MARKER_LOCAL = Pattern.compile(
            "\\(([a-zA-ZäöüÄÖÜ][a-zA-ZäöüÄÖÜ\\s]{1,30})\\)\\s*$");

    private static String extractTypeMarkerLocal(String text) {
        if (text == null) return null;
        Matcher m = P_TYPE_MARKER_LOCAL.matcher(text);
        return m.find() ? m.group(1).trim() : null;
    }

    // -------------------------------------------------------------------------
    // Stufen-Splitting: "Wuchtschlag I-III" / "Heilkraft I/II/III" / "I-II"
    // -------------------------------------------------------------------------

    /** Liefert die Stufen-Sequenz, falls der Name auf eine Range endet, sonst leeres Array. */
    static int[] parseStageRange(String name) {
        if (name == null) return new int[0];
        Matcher m = P_TRAILING_STAGE.matcher(name);
        if (!m.find()) return new int[0];
        String suffix = m.group().trim();
        // Tokens auf '-' und '/' splitten und alle als roemische Ziffern interpretieren
        String[] tokens = suffix.split("\\s*[-/]\\s*");
        int[] stages = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            stages[i] = romanToInt(tokens[i]);
            if (stages[i] <= 0 || stages[i] > 10) return new int[0];
        }
        // Beispiele:
        //   "I-III" → [1, 3] interpretiert als Range 1..3 (3 Bloecke)
        //   "I/II/III" → [1, 2, 3] interpretiert als Aufzaehlung (3 Bloecke)
        //   "I-II" → [1, 2] (2 Bloecke)
        if (stages.length == 2) {
            // bei genau zwei Tokens UND Bindestrich-Trenner = Range
            // bei Slash-Trenner = Aufzaehlung
            // suffix.contains("-") indiziert Range
            if (suffix.contains("-") && stages[0] < stages[1]) {
                int[] expanded = new int[stages[1] - stages[0] + 1];
                for (int i = 0; i < expanded.length; i++) expanded[i] = stages[0] + i;
                return expanded;
            }
        }
        return stages;
    }

    /** Erzeugt die Namen "Wuchtschlag I", "Wuchtschlag II", ... fuer eine Stufen-Sequenz. */
    static String[] expandStageNames(String fullName, int[] stages) {
        String base = P_TRAILING_STAGE.matcher(fullName).replaceAll("").trim();
        String[] result = new String[stages.length];
        for (int i = 0; i < stages.length; i++) {
            result[i] = base + " " + intToRoman(stages[i]);
        }
        return result;
    }

    private static int romanToInt(String s) {
        int total = 0, prev = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            int v = switch (s.charAt(i)) {
                case 'I', 'i' -> 1;
                case 'V', 'v' -> 5;
                case 'X', 'x' -> 10;
                default -> -1;
            };
            if (v < 0) return -1;
            if (v < prev) total -= v;
            else { total += v; prev = v; }
        }
        return total;
    }

    private static String intToRoman(int n) {
        return switch (n) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV";
            case 5 -> "V"; case 6 -> "VI"; case 7 -> "VII"; case 8 -> "VIII";
            case 9 -> "IX"; case 10 -> "X";
            default -> String.valueOf(n);
        };
    }
}
