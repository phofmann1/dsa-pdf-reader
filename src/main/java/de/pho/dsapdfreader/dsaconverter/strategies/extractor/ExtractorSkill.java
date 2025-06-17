package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.SkillApplication;
import de.pho.dsapdfreader.exporter.model.SkillUsage;
import de.pho.dsapdfreader.exporter.model.enums.SkillApplicationKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;

public class ExtractorSkill extends Extractor {

  public static final Pattern PAT_HAS_NEW_SKILL_APPLICATION = Pattern.compile("(eröffnet|erwirbt|ist eine|erhält|wird eine|bekommen).*Einsatzmöglichkeit");
  public static final Pattern PAT_EXTRACT_NEW_SKILL_APPLICATION = Pattern.compile("(?<=(Talent <i>|it von <i>|it für <i>|alents <i>|be auf <i>))[A-zÄ-üß ()&.]*(<\\/i> <i>)?[A-zÄ-üß ()&]*(?=<\\/i>)");
  //test/resources/testdata - regex/KodexSchwert - abilities - new skill usages.txt
  public static final Pattern PAT_HAS_NEW_SKILL_USAGE = Pattern.compile("neues? (<i>)?Anwendungsgebiet|erhält .* das Anwendungsgebiet <i>");
  public static final Pattern PAT_EXTRACT_NEW_SKILL_USAGE = Pattern.compile("(?<=Anwendungsgebiet <i>)[A-zÄ-üß ()&]*(<\\/i> <i>)?[A-zÄ-üß ()&]*(?=<\\/i>)"); //die Trennung durch <i> ist z.B. im Anwendungsgebiet Instrumente bauen zu sehen
  public static final Pattern PAT_EXTRACT_SKILL = Pattern.compile("(?<=<i>)[A-zÄ-üß &-]*(?=<\\/i>)");

  private ExtractorSkill() {
  }

  public static SkillKey retrieveSkillKey(String name) {
    return extractSkillKeyFromText(name);
  }

  public static List<SkillKey> retrieveSkillKeysForMysticalSkillRaw(MysticalSkillRaw msr) {
    List<SkillKey> returnValue = new ArrayList<>();
    if (msr.talentKey != null) {
      if (msr.talentKey.contains("Musizieren")) returnValue.add(SkillKey.musizieren);
      if (msr.talentKey.contains("Singen")) returnValue.add(SkillKey.singen);
    }
    return returnValue;
  }

  public static SkillUsageKey retrieveSkillUsageKey(String usage) {
    try {
      return SkillUsageKey.fromString(usage);

    }
    catch (IllegalArgumentException e) {
      //System.out.println("US: " + usage + ",");
      LOGGER.error(e.getMessage(), e);
    }
    return null;
  }

  private static SkillKey extractSkillKeyFromText(String name) {
    SkillKey returnValue;
    String skillKeyString = extractKeyTextFromTextWithUmlauts(name).toLowerCase();

    try {
      returnValue = SkillKey.valueOf(skillKeyString);
    }
    catch (IllegalArgumentException e) {
      returnValue = null;
      //System.out.println(skillKeyString + " --> ");
      LOGGER.error("Invalid Skill name: " + skillKeyString);
    }
    return returnValue;
  }

  public static SkillApplication retrieveSkillApplication(String rules, String name) {
    SkillApplication returnValue = null;

    if (PAT_HAS_NEW_SKILL_APPLICATION.matcher(rules).find()) {
      returnValue = new SkillApplication();
      String keyString = Extractor.extractKeyTextFromTextWithUmlauts(name.replaceAll("\\(.*\\)", "")).toLowerCase();
      try {
        returnValue.key = SkillApplicationKey.valueOf(keyString);
      }
      catch (IllegalArgumentException e) {
        LOGGER.error("Invalid SkillApplicationKey name: " + keyString);
      }

      returnValue.name = name;
      returnValue.skillKey = extractSkillKeyForNewApplication(rules);

      returnValue.skillUsages = extractSkillUsages(returnValue.key);
    }

    return returnValue;
  }

  public static boolean isSkillKey(String skillName) {
    String enumName = extractKeyTextFromTextWithUmlauts(skillName).toLowerCase();

    try {
      SkillKey.valueOf(enumName);
      return true;
    }
    catch (IllegalArgumentException e) {
      return false;
    }
  }


  private static List<SkillUsageKey> extractSkillUsages(SkillApplicationKey skillApplicationKey) {
    if (skillApplicationKey == null) return new ArrayList<>();
    return switch (skillApplicationKey) {
      case abrichter -> List.of(SkillUsageKey.wildtiere);
      case anführer -> List.of(SkillUsageKey.aufschwatzen, SkillUsageKey.manipulieren, SkillUsageKey.schmeicheln);
      case anpeitscher, drohgebärden -> List.of(SkillUsageKey.drohung);
      case bildhauerei -> List.of(SkillUsageKey.steinmetzarbeiten);
      case iglubau -> List.of(SkillUsageKey.lageraufbau);
      case konditor -> List.of(SkillUsageKey.backen);
      case menschenstimmen_imitieren -> List.of(SkillUsageKey.personen_imitieren);
      case rosstäuscher -> List.of(SkillUsageKey.feilschen);
      case schmerzen_unterdrücken -> List.of(SkillUsageKey.handlungsfähigkeit_bewahren);
      case viehzucht -> List.of(SkillUsageKey.domestizierte_tiere);
      case scholar_der_akademie_schwert_und_stab_zu_gareth, scholar_der_schule_der_hellsicht_zu_thorwal ->
          List.of(SkillUsageKey.bedrohungen_standhalten);
      case tapferkeit_der_unsterblichen -> List.of(SkillUsageKey.öffentliche_rede);
      case scholar_der_akademie_der_hohen_magie_zu_punin, analytiker, drachenkampf_taktik, ermutigender_gesang, erweiterte_drachenkampf_taktik, falschspielen, faszinierender_gesang, tierstimmen_imitieren, ungeheuer_taktik, unterminieren, radscha_anhänger, einschüchternde_zurechtweisung, ackerbau, abrollen, majestätsstimme, verführerische_gestalt, harmonie_der_seele,wohlklang_der_seele, gesunder_geist_gesunder_körper, ermutigende_darstellung, fallentarnung,fallen_wieder_scharfmachen ->
          new ArrayList<>();
    };
  }

  private static SkillKey extractSkillKeyForNewApplication(String rules) {
    Matcher m = PAT_EXTRACT_NEW_SKILL_APPLICATION.matcher(rules.replace("<i>Pflanzen</i> <i>-</i> <i>kunde (Nutzpflanzen)</i>", "<i>Pflanzenkunde (Nutzpflanzen)</i>"));
    String forSkill = "";
    SkillKey returnValue = null;
    if (m.find()) {
      forSkill = m.group()
          .replaceAll("\\(.*\\)?", "")
          .replaceAll("<\\/?i>", "");
      try {
        returnValue = SkillKey.valueOf(Extractor.extractKeyTextFromTextWithUmlauts(forSkill).toLowerCase());
      }
      catch (IllegalArgumentException e) {
        LOGGER.error("Skill with the name (" + forSkill + ") unknown");
      }
    }
    return returnValue;
  }

  public static SkillUsage retrieveSkillUsage(String rules) {
    SkillUsage returnValue = null;
    rules = rules
        .replace("<i>Anwendungsgebiet Gil</i> <i>-</i> <i>denrecht</i>", "Anwendungsgebiet <i>Gildenrecht</i>")
        .replace("Ölgemälde malen", "<i>Ölgemälde malen</i>")
        .replace("Prunkkleider herstellen", "<i>Prunkkleider herstellen</i>");
    Matcher m = PAT_HAS_NEW_SKILL_USAGE.matcher(rules);

    if (m.find()) {
      returnValue = new SkillUsage();
      String skillUsageText = null;
      m = PAT_EXTRACT_NEW_SKILL_USAGE.matcher(rules);
      if (m.find()) {
        skillUsageText = m.group();
        returnValue.name = skillUsageText
            .replace("<i>", "")
            .replace("</i>", "")
            .replace("Magiespür", "Magiegespür");
        returnValue.key = retrieveSkillUsageKey(Extractor.extractKeyTextFromTextWithUmlauts(returnValue.name).toLowerCase());
      }

      returnValue.skillKeys = etractSkillKeys(rules.replace("<i>" + skillUsageText + "</i>", ""));

      if (returnValue.key == SkillUsageKey.berserker_beruhigen) {
        returnValue.skillKeys = List.of(SkillKey.überreden);
      }
    }

    return returnValue;
  }

  private static List<SkillKey> etractSkillKeys(String rules) {
    List<SkillKey> returnValue = new ArrayList<>();
    Matcher m = PAT_EXTRACT_SKILL.matcher(rules
        .replace("Brennend", "")
        .replace("<i>-</i>", "")
        .replace("<i>tung</i>", ""));

    while (m.find()) {
      String skillString = m.group();
      skillString = (skillString.equalsIgnoreCase("Holz-") || skillString.equalsIgnoreCase("Holz"))
          ? "Holzbearbeitung"
          : skillString;
      skillString = (skillString.equalsIgnoreCase("Metall-") || skillString.equalsIgnoreCase("Metall"))
          ? "Metallbearbeitung"
          : skillString;
      skillString = (skillString.equalsIgnoreCase("Steinbearbei"))
          ? "Steinbearbeitung"
          : skillString;
      skillString = (skillString.equalsIgnoreCase("Malen & Zeichen"))
          ? "Malen & Zeichnen"
          : skillString;

      //Fehlerkorrektur Kryptographie (da ist viel kursiv und wird falsch erkannt
      skillString = skillString.replace("Kryptographie", "")
          .replace("Einfache", "")
          .replace("Optionale Regel", "")
          .replace("Primitive", "");

      if (!skillString.isEmpty()) {
        Optional<SkillKey> sko = SkillKey.fromString(skillString);
        if (sko.isPresent()) {
          returnValue.add(sko.get());
        }
        else {
          //LOGGER.error("Kein gültiger ENUM SkillKey: " + skillString);
        }
      }

    }

    return returnValue;
  }
}
