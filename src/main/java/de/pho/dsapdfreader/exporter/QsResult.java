package de.pho.dsapdfreader.exporter;

import de.pho.dsapdfreader.exporter.model.enums.CheckQsEntryKey;

public class QsResult {

    public Integer checkResultKey; // 1, 2, 3+
    public CheckQsEntryKey key;
    public String text;

    public QsResult(CheckQsEntryKey key, Integer qs, String description) {
        this.checkResultKey = qs;
        this.key = key;
        this.text = description;
    }

    @Override
    public String toString() {
        return "QS " + checkResultKey + ": " + text;
    }
}
