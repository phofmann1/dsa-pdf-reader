package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.exporter.model.enums.CheckQsEntryKey;

public class QualityEntry {
    public Integer qs; // 1, 2, 3+
    public CheckQsEntryKey key;
    public String description;

    public QualityEntry(CheckQsEntryKey key, Integer qs, String description) {
        this.qs = qs;
        this.key = key;
        this.description = description;
    }

    @Override
    public String toString() {
        return "QS " + qs + ": " + description;
    }
}
