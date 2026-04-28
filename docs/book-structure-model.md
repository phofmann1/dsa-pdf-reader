# Buchstruktur-Modell

> Stand 2026-04-26. Driver: das Schwesterprojekt **dsa-data** baut Importer
> auf den Markdown-Output dieses Projekts auf. Erste Ableitung (Sonderfertigkeiten)
> hat 73 % der ng-dsa-Quote erreicht — die Luecke kommt durch fehlende
> Block-Atomaritaet und unvollstaendige Hierarchie. Diese Doku beschreibt das
> **logische Modell**, an dem sich der TextInterpreter und der Vault-Generator
> ausrichten muessen.

## Kernsatz

Ein Buch hat eine **logische Struktur**, die unabhaengig vom Print-Layout ist.
Spalten, Seiten, Spaltenwechsel sind reine **Lesereihenfolge** — sie
**duerfen niemals** logische Bloecke zerschneiden, Hierarchie ueberlagern oder
Boxen mit Lauftext vermischen.

## Logische Struktur

```
Buch
└── Kapitel                                       (z. B. "Kapitel 7: Sonderfertigkeiten")
    └── Section                                   (z. B. "Allgemeine Sonderfertigkeiten")
        └── Sub-Section / Sub-Sub-Section         (z. B. "Beschreibung der Allgemeinen ...")
            └── Atomarer Block                    (z. B. eine Sonderfertigkeit "Abrichter")
                ├── Felder                        (Voraussetzungen, AP-Wert, Regel, ...)
                └── Optional: Inline-Boxen        (Beispiele, Anmerkungen)
            └── Atomarer Block (naechster)
            └── Atomarer Block (...)
            └── Box (nebenstaendig)               (z. B. "Beispiel-Kasten")
                └── eigene Sub-Hierarchie moeglich
```

### Vier Konzept-Ebenen

1. **Hierarchie** (Buch → Kapitel → Section → Sub-Section)
   - Ergibt sich aus Heading-Stufen (H1 / H2 / H3 / H4)
   - Jeder atomare Block traegt seinen vollstaendigen Hierarchie-Pfad als Kontext
   - Beispiel: `Buch:Kodex_des_Schwertes > Kapitel:Sonderfertigkeiten > Section:Allgemeine > Block:Abrichter`

2. **Atomare Bloecke** (das Konzept, das ein Importer extrahieren will)
   - Eine Sonderfertigkeit, ein Zauber, eine Liturgie, ein Talent — alles als ATOMARE Einheit
   - Ein atomarer Block enthaelt seinen Beschreibungstext UND alle zugehoerigen Felder
   - Block-Anfang: typischerweise ein Heading oder ein fett-gestarteter Name
   - Block-Ende: der naechste Block-Anfang gleicher oder hoeherer Hierarchie-Stufe

3. **Felder** (Untereinheit eines Blocks)
   - Markiert durch **Fett mit Doppelpunkt + folgender Lauftext** (`**Voraussetzungen:** Tierkunde 8`)
   - Felder gehoeren immer zum **uebergeordneten** Block — sie sind keine eigenen Bloecke
   - Wenn Felder durch Spalten-/Seiten-Brueche geographisch vom Block-Anfang getrennt sind:
     der Block hat sie trotzdem einzuschliessen.

4. **Boxen** (nebenstaendige Sub-Inhalte)
   - Im PDF erkennbar an gefuelltem Hintergrund-Rechteck (`RawRect` mit fillColor)
   - Boxen unterbrechen den Lesefluss visuell, gehoeren aber inhaltlich zur
     uebergeordneten Section/Sub-Section, NICHT zum atomaren Block, in dessen Naehe sie stehen
   - Boxen koennen eigene Hierarchie haben (Box-internes Heading, Felder, ...)
   - Markdown-Output muss Boxen klar als separate Einheit ausgeben — sie duerfen nicht
     in den Lauftext anderer Bloecke einfliessen

## Print-Layout — was es **nicht** sein darf

| Layout-Element | Aktuell im Markdown | Soll-Verhalten |
|---|---|---|
| Spaltenwechsel `<!-- Spalte 1 --> ... <!-- Spalte 2 -->` | trennt Bloecke | reine Lesereihenfolge — Block-Aggregation MUSS sie ignorieren |
| Vollbreite-Marker `<!-- Vollbreite -->` | erscheint mitten im Block | reine Layout-Info — nicht Block-Trennung |
| Seitenwechsel (Datei-Wechsel `seite_NNN.md`) | trennt Bloecke implizit | wenn Block ueber Seiten geht: zusammenfuehren |
| Tabellen | werden als Heading-/BOLD_BARE-Pseudo-Bloecke erkannt | als TABELLE markieren, nicht als Heading |
| Box-Hintergruende | werden als Lauftext im Hauptfluss aufgenommen | als BOX-Block separat ausgeben |

## Konkrete Probleme im Bestand

Beobachtet im Markdown-Output von **Kodex des Schwertes** (Stand 2026-04-26):

### P1: Headings werden gespalten

Wort wird zu zwei Headings, weil das PDF-Layout die Schrift ueber zwei Zeilen
umbricht:

```
## Pro
## ben          ← sollte: ## Proben

## Heil
## ung          ← sollte: ## Heilung

## Gi
## fte          ← sollte: ## Gifte
```

Ursache vermutlich: `collectHeading` in TextInterpreter klassifiziert pro
Zeile, ohne mehrzeilige Headings (typisch bei grossen H2/H3) zu rekonstruieren.

### P2: Tabellen-Zeilen werden zu Headings

```
## I II III IV          ← Spaltenkopf einer Komplexitaets-Tabelle

**Wurf Wahrschein- Prozent**     ← BOLD_BARE-Pseudoblock fuer Tabelle
**Material Effekte Herstellung-Probe Bruch- Anmerkung**
```

Ursache: Tabellen-Header haben oft Bold und vergleichbare Schriftgroesse wie
H3 — `collectHeading` klassifiziert sie ohne Tabellen-Kontext.

### P3: Bloecke werden durch Spalten-/Vollbreite-Wechsel zerschnitten

Konkretes Beispiel aus seite_293.md:

```
**Berittene Lanzenformation** (passiv) Wer als Reiter ausgebildet wird, einen konzentrierten
<!-- Spalte 2 -->
**Berittener Flugkampf** (passiv) Helden, die sich auf den Kampf in der Luft ...
```

Block "Berittene Lanzenformation" wird in Spalte 1 angefangen, hat **keine
Pflichtfelder mehr in Spalte 1**, und Spalte 2 startet mit dem **naechsten** Block —
die Felder von "Lanzenformation" sind verloren oder im naechsten Spalten-Schwung
weiter unten.

Anderes Beispiel:

```
**Berittener Schütze** (passiv)
<!-- Vollbreite -->
<!-- Spalte 1 -->
**Voraussetzungen:** Lanzenangriff
```

Pflichtfeld `Voraussetzungen:` gehoert zu "Berittener Schuetze" (drei Marker
hoeher), wird aber durch zwei Layout-Wechsel davon getrennt. Aktueller
Block-Aggregator sieht das nicht zusammen.

### P4: Felder werden als Bloecke missinterpretiert

Bekannte Felder am Zeilenstart wie `**Voraussetzungen:**`, `**AP-Wert:**`,
`**Anwendungsgebiete:**`, `**Mut (MU):**` werden derzeit als eigene Bloecke
extrahiert. Sie sind aber **Felder eines uebergeordneten Blocks** (oder
Talent-Beschreibungs-Marker innerhalb eines Talent-Sub-Sections).

Konsequenz im Importer: 1140 false-positive-Bloecke, die Klassifikator-
Confidence verzerren.

### P5: Boxen erscheinen als Lauftext

Beispiel-Boxen, Anmerkung-Kaesten, "Achtung"-Hinweise sind im PDF visuell durch
Hintergrund-Rechteck erkennbar (`RawRect` mit fillColor). Im Markdown sind sie
ohne Trennung in den Lauftext eingefuegt — sie unterbrechen die Lesbarkeit der
eigentlichen Bloecke.

### P6: Hierarchie-Pfad fehlt im Block-Output

Bloecke werden geschrieben **ohne den Hierarchie-Kontext**, in dem sie stehen.
Konsumenten wie der dsa-data-Importer muessen den Pfad ueber State-Tracking
selbst rekonstruieren — fragil, weil eine fehlerhaft erkannte Heading die
Section-Zuordnung aller folgenden Bloecke kaputt macht.

## Soll-Anforderungen an den TextInterpreter

Ableitend aus den Problemen, was der Markdown-Output liefern muss:

### A1: Heading-Zusammenfuehrung mehrzeiliger Worte

Wenn zwei aufeinanderfolgende Headings derselben Stufe **direkt
untereinander** stehen (gleicher X-Bereich, kleiner Y-Abstand) und einer der
beiden mit Bindestrich endet ODER zusammen ein bekanntes deutsches Wort
ergeben: zusammenfuehren.

### A2: Block-Aggregation ueber Spalten/Seiten hinweg

Spalten-Wechsel-Marker (`<!-- Spalte X -->`) sind reine Lesereihenfolge. Der
Block-Aggregator (Phase 6 im aktuellen TextInterpreter) muss:
- Spalten-Wechsel als nicht-trennend behandeln
- Seiten-Wechsel als nicht-trennend behandeln, **wenn** der Block am
  Seitenende offen ist (kein Pflichtfeld-Abschluss)
- Vollbreite-Wechsel als nicht-trennend behandeln, wenn der vorhergehende und
  folgende Inhalt zum selben Block gehoeren

Praktisch: nach der Spalten-/Seiten-Aufloesung ist der Spalten-Marker
informativ, aber alle Bloecke haben ihre vollstaendigen Felder zusammen.

### A3: Tabellen sauber als Tabellen ausgeben

Bloecke, die als Tabelle erkannt werden (Spalten-Histogramm + GAP_THRESHOLD-
Regelmass), werden als Markdown-Tabelle (`| col | col |`) ausgegeben —
**nicht** als Heading, **nicht** als BOLD_BARE-Pseudoblock.

### A4: Box-Erkennung ueber `RawRect`

`RawRect` mit `fillColor != null` und plausibler Groesse (Min-Hoehe ~40 px,
Min-Breite ~100 px, max-Breite < 90 % Seitenbreite) markiert eine **Box**.
Zeichen, deren Position in der Box liegt, werden als separate **BOX-
Block-Sequenz** ausgegeben:

```
<!-- BOX:start type=example border=none bg=#fff5d8 -->
…Lauftext der Box…
<!-- BOX:end -->
```

Der Box-Inhalt darf NICHT in den umgebenden Block-Lauftext einfliessen.

### A5: Felder erkennen, nicht als Bloecke ausgeben

`**Name:**` mit Doppelpunkt und folgendem Lauftext am Zeilenanfang ist ein
**Feld**. Der Markdown-Output bleibt unveraendert (Markdown ist menschlesbar),
aber im **Sidecar-Format** (`seite_NNN.blocks.json` aus
`markdown-extractor-improvements.md`) werden Felder explizit als Felder
markiert, nicht als Bloecke.

Konkret: das Sidecar-JSON pro Block bekommt eine `fields`-Map; die rohen
Markdown-Marker wandern dort hinein.

### A6: Hierarchie-Pfad pro Block im Sidecar

Jeder Block im Sidecar bekommt seinen vollstaendigen Hierarchie-Pfad:

```json
{
  "type": "BLOCK",
  "name": "Abrichter",
  "hierarchy": [
    {"level": 1, "title": "Kodex des Schwertes"},
    {"level": 2, "title": "Sonderfertigkeiten"},
    {"level": 3, "title": "Allgemeine Sonderfertigkeiten"}
  ],
  "fields": {"voraussetzung": "Tierkunde 8", "ap-wert": "5 Abenteuerpunkte"},
  ...
}
```

Konsumenten wie dsa-data brauchen dann keinen State-Tracking-Code.

## Auswirkungen auf den TextInterpreter

Der bestehende `TextInterpreter.interpretPage()` arbeitet **rein page-lokal**
und wird per `RegenerateText.main()` pro Seite einzeln aufgerufen. Damit sind
seitenuebergreifende Block-Aggregationen (A2) **strukturell unmoeglich** in der
aktuellen Architektur.

Vorschlag fuer die Umsetzung:

```
Heutiger Pfad:
  RegenerateText
    ├─ pro Seite: interpretPage(page) → Markdown
    └─ schreibt seite_NNN.md

Neuer Pfad:
  BookInterpreter
    ├─ liest ALLE seiten eines Buches in RawPageData
    ├─ Phase A: pro Seite klassifizieren (chars, tables, boxes, headings)
    ├─ Phase B: book-level Heading-Hierarchie aufbauen
    ├─ Phase C: book-level atomare Bloecke aggregieren (ueber Spalten/Seiten)
    ├─ Phase D: pro Seite Markdown ausgeben (rueckwaertskompatibel)
    └─ Phase E: pro Seite Sidecar-JSON ausgeben (NEU)
                + book-level Strukturindex (_structure.json) (NEU)
```

`_structure.json` ist die neue Wahrheitsquelle: vollstaendige logische Struktur
des Buches, von Konsumenten wie dsa-data direkt nutzbar — ohne Markdown-
Pattern-Matching.

## Roll-out (Iterationen)

| Iter | Inhalt | Prio | Aufwand |
|---|---|---|---|
| 1 | A1 Heading-Wort-Reflow | hoch | klein |
| 2 | A2 Spalten/Seiten-Reflow + book-level BookInterpreter | **fundamental** | gross |
| 3 | A6 Hierarchie-Pfad + Strukturindex `_structure.json` | hoch | mittel |
| 4 | A4 Box-Erkennung ueber RawRect | hoch | mittel |
| 5 | A3 Tabellen sauber als Tabellen | mittel | mittel |
| 6 | A5 Felder explizit im Sidecar | gering (folgt aus 2+3) | klein |

Iteration 2 ist der grosse Brocken — sie aendert die Architektur. Davor lohnt
es sich, den existierenden TextInterpreter als **Baseline** stabil zu halten
(rueckwaertskompatibel) und das book-level-Verhalten als zusaetzliche Schicht
einzubauen.

## Erfolgsmessung

Pro Buch nach jeder Iteration:
- **dsa-data Importer-Quote** gegen ng-dsa-Benchmark
- **Anzahl `<!-- Spalte X -->`-Marker** im Markdown-Output, die Bloecke zerschneiden (Soll: 0)
- **Anzahl gespaltener Headings** (Soll: 0, bisher ~5-10 pro grossem Buch)
- **Anzahl falsch klassifizierter Tabellen-Zeilen als Heading** (Soll: 0)

### Quoten nach Iteration 5 (Stufen-Splitting)

| Buch | Bloecke (vor → nach Splitting) |
|---|---|
| Kodex des Schwertes | 458 → **496** (+38 Stufen-Bloecke) |
| Kodex der Magie | 468 → **520** (+52) |
| Kodex des Goetterwirkens | 423 → **425** (+2) |

Bloecke wie `Auf Distanz halten I-II` werden in `Auf Distanz halten I` +
`Auf Distanz halten II` aufgesplittet — separate Instanzen mit (vorerst noch
gemeinsamen) Feldern. Stufen-spezifisches Feld-Parsing ("Stufe I: GE 13;
Stufe II: GE 15" → pro Stufe eigene Voraussetzung) folgt in einer
spaeteren Iteration.

Compare-Tool-Quote gegen ng-dsa bleibt unveraendert (Tool kollabiert
Stufen-Suffixe), aber die DB enthaelt nun pro Stufe einen eigenen `Block` —
Voraussetzung fuer korrekte Charaktererzeugung.

### Quoten nach Iteration 4 (Box-Extraktion via RawRect)

| Buch | vor Box-Extraktion | nach Box-Extraktion | Boxen erkannt |
|---|---|---|---|
| Kodex des Schwertes | 69 % | **72 %** | 39 |
| Kodex der Magie | 33 % | 33 % | 56 |
| Kodex des Goetterwirkens | 55 % | **58 %** | 30 |

Boxen werden korrekt erkannt und in `_assets/boxes/box_pNNN_xxx.md` gespeichert
(Markdown mit Frontmatter). Der Hauptlauftext erhaelt am Page-Ende
`![[box_pNNN_xxx]]`-Embeds. Aber: das sind hauptsaechlich
**Tabellen-Hintergruende** mit gefuelltem Rechteck. Die "echten" Beispiel-Boxen
mit hellem oder transparentem Hintergrund werden vom aktuellen
`isNearWhite`-Filter ausgeschlossen.

**Vorbereitung Iteration 5**: Box-Erkennung verbessern fuer
- helle Beige-Hintergruende (Beispiel-Boxen)
- transparente Border-Boxen (manchmal nur durch RawImage oder dichteres
  RawRect-Stack erkennbar)

### Quoten BookInterpreter MVP (2026-04-27, mit Name-Diff gegen ng-dsa)

Vergleich ueber `dsa-data/scripts/compare_ng_dsa_quote.py` — listet
fehlende und zusaetzliche SF-Namen pro Buch in `dsa-data/build/ng_dsa_diff/`.

| Buch | unsere Bloecke | ng-dsa SFs | fehlend | zusaetzlich | echte Quote |
|---|---|---|---|---|---|
| Kodex des Schwertes | 449 (424 norm) | 572 (526 norm) | 159 | 57 | **69 %** |
| Kodex der Magie | 455 (420 norm) | 477 (387 norm) | 259 | 292 | **33 %** |
| Kodex des Goetterwirkens | 459 (437 norm) | 239 (193 norm) | 86 | 330 | **55 %** |
| Wege der Helden (DSA 4) | – | TBD | – | – | on hold (4-spaltig, andere Designregeln) |

Normalisierung: Stufen-Suffixe ("I", "II", ...) und Klammer-Zusaetze werden
fuer den Vergleich entfernt; Vergleich case-insensitive.

**Beobachtungen:**

- **Schwertes 69 %**: 159 echte fehlende SFs (Akrobat, Anführer, Berserkerangriff,
  Ardariten-Stil, ...). Vermutlich durch Box-/Vollbreite-Zerschneiden verloren.
  Box-Extraktion (Iteration 4) sollte das deutlich heben.
- **Magie 33 %, aber 292 zusaetzlich**: starkes Naming-Mismatch. Sehr viele
  Stufen-Variationen ("Belkelels Ekstase III", "Brünstigkeit erzeugen IV") —
  ng-dsa fuehrt jede Stufe als eigene SF, wir aggregieren. Stufen-Aufloesung
  noetig in `_structure.json`.
- **Goetterwirken 55 %, aber 330 zusaetzlich**: wir liefern viele Bloecke, die
  ng-dsa als **Liturgien** fuehrt (eigene Tabelle), nicht als Abilities.
  Beispiele aus unseren extras: "Bebende Erde", "Beruhigende Worte",
  "Bollwerk des Schutzes". Klassifikator-Verbesserung noetig: Liturgien haben
  Probe + KaP + Liturgiezeit als Pflichtfelder.

**Implikationen fuer Iterationen 4+:**

1. **Box-Extraktion (Iteration 4)** — trifft primaer Schwertes (geschaetzt +20-25 %).
2. **Klassifikator-Verbesserung** (Liturgy/Spell vs Ability) — reduziert
   Goetterwirken-Extras, gibt eigenen Output-Typ.
3. **Stufen-Erkennung** — Sub-Bloecke in `_structure.json` fuer Magie-SFs mit
   I/II/III/IV-Aufteilung.

Ziel: >85 % echte Quote pro Buch nach Iterationen 4-6.

## Finales Output-Format (Spezifikation)

Pro Buch ein Verzeichnis. Die alten `seite_NNN.md`-Dateien werden ausgemustert.

```
export/markdown/text/<Buchpfad>/<Buchordner>/
├─ Kodex des Schwertes.md          ← Fliesstext, vollstaendig, mit Seitenmarkern + Box-Embeds
├─ _structure.json                 ← Strukturindex (siehe Schema unten)
└─ _assets/
   ├─ images/                      ← bereinigte Bilder
   │  └─ img_p042_001.png
   ├─ tables/                      ← Tabellen als JSON
   │  └─ tbl_p050_001.json         ← {caption, columns, rows}
   └─ boxes/                       ← Boxen als Markdown mit Frontmatter
      └─ box_p250_001.md
```

### `<Buchtitel>.md` — der Fliesstext

- Vollstaendiger Lauftext des Buches in Lesereihenfolge
- **Seitenmarker** als HTML-Kommentare: `<!-- Seite 249 -->`
- **Hierarchie** als Markdown-Headings (`#`, `##`, `###`, `####`)
- **Boxen** sind als Embed-Verweis eingebettet, der **nicht** als Block-Trenner gilt:
  ```
  ## Abrichter
  Der Held hat sich angeeignet, wie man wilde Tiere ausbildet.
  ![[box_p250_001]]
  **Regel:** Durch diese Sonderfertigkeit erhaelt der Abenteurer ...
  **Voraussetzungen:** Tierkunde 8
  **AP-Wert:** 5 Abenteuerpunkte
  ```
  → Block "Abrichter" ist **eine** atomare Einheit, auch wenn die Box-Referenz mittendrin steht.
- **Bilder** als Markdown-Embed: `![[img_p042_001]]` — gleiche Block-Regel
- **Tabellen** als Markdown-Tabelle inline ODER als Embed `![[tbl_p050_001]]` (siehe unten)
- Keine Spalten-Marker mehr (`<!-- Spalte X -->` entfaellt — Lauftext ist linear in Lesereihenfolge)

### `_structure.json` — JSON-Schema

```json
{
  "$schema": "https://dsa-data.local/schema/book-structure-v1.json",
  "publication": "kodex_des_schwertes",
  "title": "Kodex des Schwertes",
  "dsa_version": 5,
  "pages": 396,
  "generated_at": "2026-04-26T20:00:00Z",
  "generator_version": "v1.0",

  "hierarchy": [
    {
      "id": "h0001",
      "level": 1,
      "title": "Kapitel 7: Sonderfertigkeiten",
      "page_range": [248, 296],
      "children": [
        {
          "id": "h0042",
          "level": 2,
          "title": "Allgemeine Sonderfertigkeiten",
          "page_range": [249, 290],
          "blocks": [
            {
              "id": "b0123",
              "kind": "ability",
              "name": "Abrichter",
              "page": 249,
              "anchor_offset": 4521,
              "fields": {
                "voraussetzung": "Tierkunde 8, kein Nachteil Unfaehig (Tierkunde)",
                "ap-wert": "5 Abenteuerpunkte",
                "regel": "Durch diese Sonderfertigkeit erhaelt der Abenteurer ..."
              },
              "boxes":  ["box_p249_001"],
              "images": [],
              "tables": []
            }
          ],
          "boxes":  ["box_p250_002"],
          "images": ["img_p250_001"],
          "tables": ["tbl_p250_001"]
        }
      ]
    }
  ],

  "page_index": {
    "249": {
      "blocks": ["b0123"],
      "boxes":  ["box_p249_001"],
      "images": [],
      "tables": []
    }
  },

  "assets": {
    "images": {
      "img_p042_001": {
        "path": "_assets/images/img_p042_001.png",
        "page": 42,
        "anchor_hierarchy_id": "h0007",
        "size_px": [800, 600],
        "checksum": "sha256:..."
      }
    },
    "tables": {
      "tbl_p050_001": {
        "path": "_assets/tables/tbl_p050_001.json",
        "page": 50,
        "anchor_hierarchy_id": "h0011",
        "caption": "Eigenschafts-Modifikatoren"
      }
    },
    "boxes": {
      "box_p250_001": {
        "path": "_assets/boxes/box_p250_001.md",
        "page": 250,
        "anchor_hierarchy_id": "h0042",
        "anchor_block_id": "b0123",
        "box_type": "example"
      }
    }
  }
}
```

**Konventionen:**
- `id`-Felder sind **stabil** ueber Re-Generationen (z. B. `b<sha256-prefix>` oder fortlaufend
  pro Buch). Konsumenten dürfen sie als Verweis cachen.
- `kind` (auf Block-Ebene): `ability` | `spell` | `liturgy` | `boon` | `talent` | `unclassified`.
  Klassifikation kann initial `unclassified` sein und durch nachgelagerte Pipelines
  verfeinert werden.
- `fields` (auf Block-Ebene): Map kanonischer Feld-Namen → Roh-Lauftext-Wert. Field-Erkennung
  erfolgt im `BookInterpreter` (Pattern: Bold + Doppelpunkt + Lauftext).
- `anchor_offset`: Zeichen-Offset im `<Buchtitel>.md` — fuer praezise Lokalisierung.
- `anchor_hierarchy_id` / `anchor_block_id`: Asset-Verankerung. Pflicht-Anker ist
  `page`; Hierarchie-/Block-Anker sind Best-Effort.

### `_assets/tables/<id>.json` — Tabellen

```json
{
  "id": "tbl_p050_001",
  "page": 50,
  "caption": "Eigenschafts-Modifikatoren",
  "columns": ["Eigenschaft", "Mod"],
  "rows": [
    ["MU", "+1"],
    ["KL", "0"]
  ]
}
```

Im Fliesstext als Markdown-Tabelle gerendert ODER (bei grossen Tabellen) als Embed
`![[tbl_p050_001]]`. Schwellenwert (z. B. > 8 Zeilen) konfigurierbar.

### `_assets/boxes/<id>.md` — Boxen

Markdown mit Frontmatter:

```markdown
---
id: box_p250_001
page: 250
box_type: example
anchor_hierarchy_id: h0042
anchor_block_id: b0123
---

# Beispiel: Abrichten

Geron, ein erfahrener Tierkenner, möchte einen Wildhund abrichten...
```

Box-internes Heading ist erlaubt — Boxen koennen eigene Sub-Hierarchie haben.

### `_assets/images/<id>.png` — Bilder

Reine PNG-Datei, Metadaten leben in `_structure.json` unter `assets.images`. Bereits
existierende Bild-Bereinigungs-Logik (Dedup ueber Checksum) bleibt erhalten.

## Block-Aggregations-Regel

> **Eine Box-/Bild-/Tabellen-Embed-Referenz im Fliesstext beendet niemals einen Block.**

Konkret: der `BookInterpreter` muss bei der Block-Aggregation `![[xxx]]`-Marker als
**unsichtbar** behandeln — Lauftext davor und danach gehoert weiterhin zum selben
atomaren Block. Block-Trenner sind nur:
- Heading (`#`/`##`/`###`/`####`)
- Naechster Block-Anfang (z. B. naechster fett-gestarteter SF-Name)
- Doppelte Leerzeile **ohne** dazwischenliegendes Embed

## Migrations-Plan

1. **Stufe 1**: `BookInterpreter` produziert das neue Format **zusaetzlich** zu `seite_NNN.md`
   (Co-Existenz). Konsumenten werden umgestellt.
2. **Stufe 2**: alte `seite_NNN.md` und `_volltext.md` werden geloescht. `_headings.md`
   bleibt vorerst fuer Debug.
3. **Stufe 3**: `_headings.md` ebenfalls weg, weil `_structure.json` es ersetzt.

User-Entscheidung 2026-04-26: `seite_NNN.md` und `_volltext.md` sind ueberfaellig und
duerfen weg, sobald das neue Format steht.

## Verweise

- `markdown-extractor-improvements.md` — konkrete Code-Erweiterungspunkte
- `../../dsa-data/docs/concepts/import.md` — Importer-Pipeline und Benchmark
