# Markdown-Extractor: Verbesserungs-Roadmap

> Stand: 2026-04-26. Treiber: Schwesterprojekt **dsa-data** baut einen
> Sonderfertigkeit-Importer (V9), der den Markdown-Output dieses Projekts als
> Eingabe nimmt. Daraus ergeben sich konkrete Anforderungen, die hier dokumentiert
> und schrittweise umgesetzt werden.

## Aktueller Zustand

`TextInterpreter` (4845 Zeilen) konvertiert PDF-Seiten zu Markdown. Heading-
Erkennung ueber Schriftgroessen (>25pt → H1, >14pt → H2, >10pt+bold → H3),
Spalten-Histogramm, Tabellen-Erkennung, Ornament-Font-Filter
(`TypeEmbellishmentsOne`, `PaganSymbols`).

### Audit der internen Pipeline

`TextInterpreter` hat **zwei parallele Pipelines**:

| Pipeline | Einstieg | Output | Nutzt ContentBlock? | Aktiv genutzt? |
|---|---|---|---|---|
| Alt (linear) | `interpretPage(page)` Zeile 1652 | nur Markdown-String | nein | **ja** — von `RegenerateText.main()` aufgerufen |
| Neu (strukturiert) | `interpretPageStructured(page)` Zeile 582 + `consolidateBlocks()` Zeile 1175 | Markdown via Block-Tree | ja, mit `ContentBlock.Type` (HEADING, BULLET, TABLE, BODY, BOX_CONTENT) | **nein** — niemand ruft das auf |

→ **Die strukturierte Pipeline existiert, hat sogar bereits ein `BOX_CONTENT`-
Konzept, wird aber nicht verwendet.** Der lineare `interpretPage`-Pfad ist
das, was den aktuellen Markdown-Output produziert — und der hat keinen
Block-Tree, keine Box-Erkennung im Output, keine seitenuebergreifende
Aggregation.

### Phasen der strukturierten Pipeline (laut Header-Doku)

```
Phase 1: Vorbereitung   (classify, footer, boxes, bullets)
Phase 2: Box-Inhalt isolieren
Phase 3: Spaltenaufteilung
Phase 4: Per-Column Klassifikation (bullet, table, heading, body)
Phase 5: Cross-column Tabellen-Merging
Phase 6: Leserichtung
```

**Was vorhanden ist:**
- Phase 1: classifyChars, RawRect-Filtering (overlap-Test bei imageBounds)
- Phase 2: hat als Konzept existiert, aber Boxen werden nicht als eigener
  Output-Typ geschrieben — Box-Inhalte fliessen in den BODY-Stream
- Phase 5: Cross-column Tabellen-Merging existiert (GAP_THRESHOLD-Logik)
- Phase 6: Leserichtung sortiert pro Spalte, fuegt `<!-- Spalte X -->`-Marker
  ein — aber keine Aggregation ueber Spalten hinweg fuer atomare Bloecke

**Was fehlt:**
- **Page-uebergreifende Block-Aggregation** (alle Aufrufe sind page-lokal)
- **Box-Erkennung als separater Output-Block** (das Konzept BOX_CONTENT
  existiert, wird aber nicht genutzt)
- **Heading-Wort-Reflow** bei mehrzeiligen Headings ("Pro" + "ben" → "Proben")
- **Hierarchie-Pfad pro Block** (kein State-Tracking nach Pages)
- **Document-Level-Bodysize-Normalisierung** (DSA 4 vs DSA 5 unterschiedliche
  Schriftgroessen-Skalen)

→ Die Probleme P1–P6 aus `book-structure-model.md` sind ausnahmslos auf diese
Luecken zurueckfuehrbar.

**Stark:** PDF-Layout-Erkennung, Spalten-Splitting, Heading-Hierarchie auf
DSA-5-Buecher gut trainiert.

### Verweis: Strukturmodell

Die fundamentale Anforderung an die Pipeline ist in
[`book-structure-model.md`](book-structure-model.md) beschrieben — eine logische
Buchstruktur (Hierarchie + atomare Bloecke + Boxen + Felder) unabhaengig vom
Print-Layout. Diese Doku hier (markdown-extractor-improvements) ist die
**Implementierungs-Roadmap** dazu.

**Schwaechen** (vom User identifiziert):
1. Nur 3 Heading-Stufen (H1/H2/H3) und eine grobe Erkennung — koennte
   detailgenauer sein.
2. **Verzierungs-Schriftarten** sollten als Schrift erkennbar bleiben (fuer
   Darstellung), aber nicht in die Heading-/Block-Klassifikation einfliessen.
   Aktuell sind nur 2 ornament fonts hardcoded.
3. **GROSS-GESCHRIEBEN** als heading-Indikator wird in der Klassifikation
   nicht beruecksichtigt — nur in der Normalisierung.
4. **Block-Erkennung** ist gelegentlich durcheinander — speziell bei Kaesten
   (`RawRect` mit Hintergrundfarbe), die den Lesefluss unterbrechen und einen
   eigenen Block bilden sollten.
5. **Schriftart-/Schriftgroessen-Information geht im Markdown-Output verloren**
   — der V9-Importer in `dsa-data` kann sie aber gut gebrauchen, um
   Sonderfertigkeit-Bloecke von Lauftext zu unterscheiden.

## Roadmap

Geplante Iterationen, in der Reihenfolge des konkreten Bedarfs durch
`dsa-data`. Pro Iteration ein abgeschlossenes, reviewbares Stueck.

### Iteration 1 — Font-Sidecar + ALL-CAPS

> **Status: aufgeschoben, on-demand.** Der `dsa-data`-V9-Importer startet
> erst einmal ohne diese Erweiterung — die existierende Markdown-Struktur
> (Bold-Markup, Heading-Hierarchie, Pattern-Marker `**Voraussetzungen:**`/
> `**AP-Wert:**`) reicht fuer DSA-5-Sonderfertigkeiten aus. Iteration 1 wird
> erst angefasst, wenn der V9-Importer in der Praxis an einer konkreten Stelle
> nicht weiterkommt (vermutlich bei DSA 4 oder bei Adventure-eingebetteten SFs).

Ziel: `dsa-data`-Importer kann pro Block die Schriftart abfragen und ALL-CAPS-
Bloecke als Heading-Hinweise behandeln, **ohne dass der Markdown-Output an
Lesbarkeit verliert**.

**Aenderungen:**

1. **Sidecar-JSON pro Seite**: neben `seite_NNN.md` wird `seite_NNN.blocks.json`
   geschrieben. Format pro Block:
   ```json
   {
     "page": 12,
     "blocks": [
       {
         "type": "HEADING|BODY|BULLET|TABLE_ROW|...",
         "level": 2,
         "text": "Kampfstilsonderfertigkeiten",
         "y": 134.5,
         "fonts": [
           {"name": "EBGaramond-Bold", "size": 14.0, "bold": true, "italic": false}
         ],
         "allCaps": false,
         "isInBox": false
       },
       ...
     ]
   }
   ```
   - `fonts` listet die im Block vorkommenden Schriften (haeufig nur eine).
   - `allCaps` ist Bool, wenn ueber 80 % der Buchstaben gross.
   - `isInBox` bleibt vorerst bei `false` (kommt in Iteration 3).

2. **ALL-CAPS-Flag fuer Heading-Erkennung**: bestehende `mostlyUppercase`-
   Pruefung in `buildHeadingText` wird ergaenzt um ein zusaetzliches
   `isAllCaps(line)` als **Heading-Hinweis**. Wenn ein Block mit normaler
   Schriftgroesse aber ALL-CAPS daherkommt, wird er als H4 klassifiziert
   (statt BODY).

3. **Markdown-Output bleibt unveraendert**. Sidecar ist optional fuer
   Konsumenten — wer nur das Markdown braucht, nimmt nur das.

**Schalter:** neuer Boolean `RUN_BLOCKS_SIDECAR` in `DsaMarkdownReaderMain`,
default `true` wenn `EXTRACT_TEXT=true`.

### Iteration 2 — Heading-Stufen-Verfeinerung + Decoration-Marker

Ziel: feinere Hierarchie + sauberere Decoration-Behandlung.

**Aenderungen:**

1. **H4 + H5** in `collectHeading()` ergaenzen. Konkret:
   - H4: bold + ALL-CAPS bei Body-Schriftgroesse → starker Block-Anfang
   - H5: bold + endsWithColon → Feld-Marker (`Voraussetzungen:`)
2. **Erweiterte Ornament-Liste** als externe Konfigurationsdatei
   (`config/ornament-fonts.txt`). Aktuelles `ORNAMENT_FONTS = Set.of(...)`
   wird zu Datei eingelesen — User kann ergaenzen ohne Code-Aenderung.
3. **Decoration-Marker im Sidecar**: Bloecke, die in einer ornament-Schrift
   erscheinen, bekommen `"isDecoration": true`. Markdown bleibt entweder
   leer (Default) oder wird in `<span class="ornament">…</span>` gewrappt
   (bestehende Logik ueber `includeOrnaments`).

### Iteration 3 — Sidebar/Kasten-Erkennung

Ziel: Bloecke in Kaesten als zusammenhaengende Einheit ausgeben.

**Aenderungen:**

1. `RawRect` mit `fillColor != null` und plausibler Groesse (nicht volle
   Seite, > 100×40) wird als **Box** erkannt.
2. Zeichen, deren Position in einer Box liegt, werden in einem eigenen
   Block-Typ `BOX` zusammengefasst — vor und nach der Box laeuft der normale
   Lesefluss weiter.
3. Sidecar bekommt `"isInBox": true` und optional `"boxColor": "#fff5d8"`.
4. Ueberschriften innerhalb einer Box werden als eigene Heading-Hierarchie
   gefuehrt (Box-lokales H1).

### Iteration 4 — Document-Level-Bodysize-Normalisierung

Ziel: DSA-4- und DSA-5-Buecher mit unterschiedlichen Schrifts-Skalen
einheitlich verarbeiten.

**Aenderungen:**

1. Erst-Pass durch alle Seiten ermittelt **dokumentweite Body-Schriftgroesse**
   (Median aller Body-Bloecke).
2. Heading-Schwellen werden relativ zur Body-Schrift berechnet (nicht hardcoded
   `>25`, sondern `> 1.7 * bodyFontSize`).
3. DSA-4-Buecher (oft 9pt Body) und DSA-5 (10-11pt) bekommen damit
   konsistente Heading-Klassifikation.

### Iteration 5 — Konfigurations-Externalisierung

Wenn die ersten vier Iterationen genug Erfahrung gebracht haben:

1. Heading-Schwellen, Gap-Toleranzen, Min-Tabellen-Hoehe etc. als
   `application.properties` oder YAML-Konfig.
2. Pro-Buch-Override moeglich (z. B. `Wege_der_Helden.yaml`).

## Konsumenten

| Konsument | Bedarf | Iteration |
|---|---|---|
| `dsa-data` Sonderfertigkeit-Importer (V9) | Font-Metadaten pro Block | 1 |
| `dsa-data` Importer ALL-CAPS als Block-Anfang | ALL-CAPS-Flag | 1 |
| `dsa-data` Importer fuer dezentrale SF (Kodex Magie) | feinere Heading-Stufen | 2 |
| `dsa-data` Importer fuer Adventure-SFs (Klingen der Nacht) | Sidebar-Erkennung | 3 |
| `dsa-data` DSA-4-Buecher (Wege der Helden) | Document-Bodysize-Normalisierung | 4 |
| Obsidian-Vault-Generator | Detailgenauere Headings | 2 |

## Konvention bei Aenderungen

- **Bestehender Markdown-Output bleibt rueckwaerts-kompatibel**. Sidecar
  und neue Flags sind opt-in.
- Pro Iteration mindestens ein Smoke-Test gegen 1-2 bekannte Buecher
  (`Kodex des Schwertes`, `Wege der Helden` ab Iteration 4).
- Aenderungen am `TextInterpreter` werden in dieser Datei nachgepflegt:
  Iteration markieren als "umgesetzt" mit Datum.
