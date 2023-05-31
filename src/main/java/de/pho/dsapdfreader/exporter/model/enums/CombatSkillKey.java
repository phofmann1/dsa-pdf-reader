package de.pho.dsapdfreader.exporter.model.enums;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CombatSkillKey
{
  dolche,
  fächer,
  fechtwaffen,
  hiebwaffen,
  kettenwaffen,
  lanzen,
  peitschen,
  raufen,
  schilde,
  schwerter,
  spießwaffen,
  stangenwaffen,
  zweihandhiebwaffen,
  zweihandschwerter,
  armbrüste,
  blasrohre,
  bögen,
  diskusse,
  feuerspeien,
  schleudern,
  wurfwaffen;

  public static Optional<CombatSkillKey> fromString(String name)
  {

    String cleanName = name.toLowerCase();
    return Arrays.stream(CombatSkillKey.values()).filter(msv -> msv.name().equalsIgnoreCase(cleanName)).findFirst();
  }

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }

  public static CombatSkillKey parse(String text)
  {
    return switch (text)
        {
          case "Dolche" -> dolche;
          case "Fächer" -> fächer;
          case "Fechtwaffen" -> fechtwaffen;
          case "Hiebwaffen" -> hiebwaffen;
          case "Kettenwaffen" -> kettenwaffen;
          case "Lanzen" -> lanzen;
          case "Peitschen" -> peitschen;
          case "Raufen" -> raufen;
          case "Schilde" -> schilde;
          case "Schwerter" -> schwerter;
          case "Spießwaffen" -> spießwaffen;
          case "Stangenwaffen" -> stangenwaffen;
          case "Zweihandhiebwaffen" -> zweihandhiebwaffen;
          case "Zweihandschwerter" -> zweihandschwerter;
          case "Armbrüste" -> armbrüste;
          case "Blasrohre" -> blasrohre;
          case "Bögen" -> bögen;
          case "Diskusse" -> diskusse;
          case "Feuerspucken" -> feuerspeien;
          case "Schleudern" -> schleudern;
          case "Wurfwaffen" -> wurfwaffen;
          default -> throw new IllegalArgumentException("Argument " + text + " unknown in Enum " + CombatSkillKey.class.getSimpleName());
        };
    }
}
