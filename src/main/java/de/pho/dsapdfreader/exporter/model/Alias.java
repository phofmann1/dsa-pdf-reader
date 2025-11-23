package de.pho.dsapdfreader.exporter.model;

import java.util.ArrayList;
import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.LanguageKey;

public class Alias {
  public String name;
  public List<LanguageKey> languageKeys = new ArrayList<>();

  public Alias(String name) {
    this.name = name;
  }

  public Alias(String name, LanguageKey languageKey) {
    this.name = name;
    if (languageKey != null) this.languageKeys.add(languageKey);
  }
}
