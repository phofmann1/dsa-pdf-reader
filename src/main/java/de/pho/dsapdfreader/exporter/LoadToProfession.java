package de.pho.dsapdfreader.exporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.pho.dsapdfreader.dsaconverter.model.ProfessionRaw;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorBoonKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCombatSkillKeys;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCultureKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorGenderKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorMysticalSkillKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorObjectRitual;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorProfessionKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSkillKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSpecialAbility;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSpecieKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorTradtion;
import de.pho.dsapdfreader.exporter.model.AttributeValuePair;
import de.pho.dsapdfreader.exporter.model.Profession;
import de.pho.dsapdfreader.exporter.model.RequirementBoon;
import de.pho.dsapdfreader.exporter.model.RequirementSpecialAbility;
import de.pho.dsapdfreader.exporter.model.ValueChange;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.CultureKey;
import de.pho.dsapdfreader.exporter.model.enums.GenderKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.ObjectRitualKey;
import de.pho.dsapdfreader.exporter.model.enums.ProfessionKey;
import de.pho.dsapdfreader.exporter.model.enums.ProfessionTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;
import de.pho.dsapdfreader.exporter.model.enums.TerrainTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.ValueChangeKey;
import de.pho.dsapdfreader.exporter.model.enums.ValueChangeType;
import de.pho.dsapdfreader.tools.roman.RomanNumberHelper;


public class LoadToProfession {

  public static List<MysticalSkillKey> MAGE_STANDARD_SPELLS_LIST = List.of(
      MysticalSkillKey.spell_aeolito,
      MysticalSkillKey.spell_analys_arkanstruktur,
      MysticalSkillKey.spell_armatrutz,
      MysticalSkillKey.spell_attributo_klugheit,
      MysticalSkillKey.spell_attributo_koerperkraft,
      MysticalSkillKey.spell_auris_illusionis,
      MysticalSkillKey.spell_balsam_salabunde,
      MysticalSkillKey.spell_bannbaladin,
      MysticalSkillKey.spell_blick_in_die_gedanken,
      MysticalSkillKey.spell_blitz_dich_find,
      MysticalSkillKey.spell_claudibus,
      MysticalSkillKey.spell_corpofesso,
      MysticalSkillKey.spell_cryptographo,
      MysticalSkillKey.spell_disruptivo,
      MysticalSkillKey.spell_duplicatus,
      MysticalSkillKey.spell_eisenrost,
      MysticalSkillKey.ritual_elementarer_diener,
      MysticalSkillKey.spell_flim_flam,
      MysticalSkillKey.spell_foramen,
      MysticalSkillKey.spell_fulminictus,
      MysticalSkillKey.spell_gardianum,
      MysticalSkillKey.spell_horriphobus,
      MysticalSkillKey.spell_ignifaxius,
      MysticalSkillKey.spell_invocatio_minima,
      MysticalSkillKey.ritual_invocatio_minor,
      MysticalSkillKey.spell_manifesto,
      MysticalSkillKey.spell_manus_miracula,
      MysticalSkillKey.spell_menetekel,
      MysticalSkillKey.spell_motoricus,
      MysticalSkillKey.spell_objectovoco,
      MysticalSkillKey.spell_oculus_illusionis,
      MysticalSkillKey.spell_odem_arcanum,
      MysticalSkillKey.spell_paralysis,
      MysticalSkillKey.spell_penetrizzel,
      MysticalSkillKey.spell_physiostabilis,
      MysticalSkillKey.spell_plumbumbarum,
      MysticalSkillKey.spell_psychostabilis,
      MysticalSkillKey.spell_respondami,
      MysticalSkillKey.spell_salander,
      MysticalSkillKey.spell_sapefacta,
      MysticalSkillKey.spell_sensibar,
      MysticalSkillKey.spell_silentium,
      MysticalSkillKey.spell_somnigravis,
      MysticalSkillKey.spell_transversalis,
      MysticalSkillKey.spell_visibili
  );

  private LoadToProfession() {
  }

  public static Stream<Profession> migrate(ProfessionRaw raw) {
    List<Profession> returnValue = new ArrayList<>();
    boolean isMagical = raw.professionType == ProfessionTypeKey.magical;
    Profession profession = new Profession();
    profession.name = raw.name
        .replace("Sarviniodella-Monte-Schwertgesellin", "Sarvinio-della-Monte-Schwertgesellin")
        .replace("Scanlailni-Uinin-Schwertgeselle", "Scanlail-ni-Uinin-Schwertgeselle")
        .replace("Nipakaukocan", "Nipakau-kocan")
        .replace("Rurund-Gror-Priesterin", "Rur-und-Gror-Priesterin");
    profession.key = ExtractorProfessionKey.retrieve(profession.name);
    profession.apValue = Integer.parseInt(raw.ap.substring(0, raw.ap.indexOf(' ')));
    profession.professionType = raw.professionType;

    if (!raw.preconditions.startsWith("keine")) {
      raw.preconditions = raw.preconditions
          .replace(", weiblich", ", Geschlecht: weiblich")
          .replace("Reiten 10 (für Berittener Kampf), ", "")
          .replace(", Ahnenblut Wolfsblut", ", Vorteil: Ahnenblut Wolfsblut")
          .replace(", Prinzipientreue I", ", Nachteil Prinzipientreue I")
          .replace(", Verpflichtungen II", ", Nachteil Verpflichtungen II")
          .replace(", Zauberer", ", Vorteil: Zauberer")
          .replace("Sonderfertigkeit Sichelangriff", "Sonderfertigkeit Befehl Sichelangriff")
          .replace("<br>", ", ")
          .replace("  ", " ");
      profession.requiredSpecieKeys = extractRequiredSpecieKeys(raw.preconditions);
      profession.requiredCultureKeys = extractRequiredCultureKeys(raw.preconditions);
      profession.requiredGender = extractRequiredGenderKey(raw.preconditions);
      profession.requiredAttributes = extractRequiredAttributes(raw.preconditions);
      profession.requiredBoons = extractRequiredBoons(raw.preconditions);
      if (profession.key == ProfessionKey.schelmin || profession.key == ProfessionKey.zauberalchimistin) { //Wird von RegEx nicht erkannt
        profession.requiredBoons.add(new RequirementBoon(BoonKey.zauberer, true));
      }
      profession.requiredAbilities = extractRequiredAbilities(raw.preconditions);
    }
    profession.minLanguageAp = extractMinLanguageAp(raw.specialAbilities);
    profession.only4languages = extractIsOnly4Language(raw.specialAbilities);

    //ABILITIES
    profession.terrainKnowledgeOptions = extractTerrainKnowledgeOptions(raw.specialAbilities);
    profession.specializationOptions = extractSpecialisations(raw.specialAbilities);
    profession.specialAbilities = extractSpecialAbilities(cleanSAs(raw.specialAbilities));
    //COMBAT SKILLS
    profession.combatSkillChanges = extractCombatskillChanges(raw.skillsCombat);
    profession.combatSkillOptions = extractCombatskillOptions(raw.skillsCombat);
    //SKILLS
    profession.skillChanges = extractSkillChanges(raw.skills);
    profession.ap4Skills = extractAp4Skills(raw.skills);
    profession.ap4SkillsSet = extractAp4SkillsSet(raw.skills);
    //MYSTICAL SKILLS
    // from special abilities
    profession.objectRituals = extractObjectRituals(cleanSAs(raw.specialAbilities));
    profession.curseAp = extractCurseAp(raw.specialAbilities);
    profession.mysticalSkillChanges = extractMysticalSkillChanges(raw.specialAbilities.replaceAll("(Sprachen für insgesamt |Sprachen und Schriften für insgesamt )\\d*,?\\s?Abenteuerpunkte", ""), isMagical);
    //from mystical skills
    String msString = ((raw.skillsMagic != null ? raw.skillsMagic : "") + " " + (raw.skillsCleric != null ? raw.skillsCleric : "")).trim();
    profession.noOfTricks = extractNoOfTricks(msString);
    profession.trickOptions = extractTrickOptions(msString);
    if (profession.mysticalSkillChanges == null) profession.mysticalSkillChanges = new ArrayList<>();
    profession.mysticalSkillChanges.addAll(extractMysticalSkillChanges(msString, isMagical));

    profession.boonsRecomended = extractBoonsRecommended(raw.meritsRecommended + ", " + raw.flawsRecommended, profession.name);
    profession.boonsUnrecomended = extractBoonsRecommended(raw.meritsInappropriate + ", " + raw.flawsInappropriate, profession.name);
    /*
    public TraditionGuidelineKey traditionGuidLineKey;
    public List<ElementKey> elementsAttuned;
    public List<ElementKey> elementsAttunedSelectOne;
    */

    //VARIANTS
    returnValue.add(profession);
    if (profession.key != ProfessionKey.wildniskundiger) {
      List<Profession> variants = generateVariants(raw.variants.replaceAll("\\*\\).*$", ""), profession, isMagical);
      returnValue.addAll(variants);
    }
    return returnValue.stream();
  }

  private static List<BoonKey> extractBoonsRecommended(String recommended, String name) {
    String cleanedString = recommended
        .replaceAll("\\*\\).*", "")
        .replace("Schlechte Eigenschaft (Neugier) Verpflichtungen II", "Schlechte Eigenschaft (Neugier), Verpflichtungen II")
        .replaceAll("(, )?alle (magischen )?(und )?(geweihten )?(Vorteile|Nachteile)(, )?", ", ")
        .replaceAll("(-|–)I+\\b", "")
        .replaceAll(" I+\\b", "")
        .replace("Adlig", "Adel")
        .replace("Beidhändigkeit", "Beidhändig")
        .replace("Behäbig Hitzeempfindlich", "Behäbig, Hitzeempfindlich")
        .replace("Eigeschränkter", "Eingeschränkter")
        .replace("Fettleibigkeit", "Fettleibig")
        .replace("Hitzeresistent", "Hitzeresistenz")
        .replace("Kein Vertrautentier", "Kein Vertrauter")
        .replace("Körperliche Besonderheit", "körperliche_auffälligkeit")
        .replace("Hexensträhne", "körperliche_auffälligkeit")
        .replaceAll("Persönlichkeitsschwäche(?=,| |$)", "Persönlichkeitsschwächen")
        .replaceAll("\\bPrinzipientreu\\b", "Prinzipientreue")
        .replace("Schlechte Eigenschaften", "Schlechte Eigenschaft")
        .replace("Immunität (", "Immunität gegen (")
        .replace("(Lebensenergie)", "Lebensenergie")
        .replace("(Astralenergie)", "Astralenergie")
        .replace("(Gift)", "Gift")
        .replace("(Krankheit)", "Krankheit")
        .replace("(Blutiger Rotz, Brabaker Schweiß)", "Krankheit")
        .replaceAll("\\(.*?\\)", "")
        .replaceAll("Angst vor.*?(,|$)", "Angst vor x,")
        .replaceAll("Begabung.*?(?=,|$)", "Begabung")
        .replaceAll("Unfähig.*?(?=,|$)", "Unfähig")
        .replaceAll("(, )?\\b(K|k)eine\\b(?! Flugsalbe),?", "")
        .replaceAll("(, )?\\b(–|-)\\b,?", "")
        .replaceAll(",+", ",");
    List<BoonKey> returnValue = recommended.equals("keine")
        ? new ArrayList<>()
        : Arrays.stream(cleanedString.split(","))
        .filter(str -> str != null && !str.trim().isEmpty() && !str.trim().equals("–"))
        .map(str -> {
          BoonKey r = ExtractorBoonKey.retrieve(str.trim());
          if (r == null) System.out.println(str.trim());
          return r;
        }).collect(Collectors.toList());
    if (returnValue.contains(null))
      System.out.println(name + ": " + cleanedString + "\r\n\t" + returnValue);
    return returnValue.stream().filter(k -> k != null).collect(Collectors.toList());
  }

  private static List<Profession> generateVariants(String variants, Profession profession, boolean isMagical) {
    List<Profession> returnValue = new ArrayList<>();
    Matcher m = Pattern.compile("<i>.*?<\\/i>( und <i>.*?<\\/i>)?.*?(?=<i>|$)").matcher(variants);
    while (m.find()) {
      try {
        String variantString = m.group();
        //System.out.println(variantString);
        Matcher varMatcher = Pattern.compile("^<i>.*<\\/i>").matcher(variantString);
        Profession variant = deepCopy(profession);
        if (varMatcher.find()) {
          String nameString = varMatcher.group();
          variant.name = nameString.replaceAll("<i>", "")
              .replaceAll("</i>", "")
              .trim();
          variant.key = ExtractorProfessionKey.retrieve(variant.name);
          if (variant.key != null) {
            variantString = variantString.replace(nameString, "").trim();
          }
        }

        varMatcher = Pattern.compile("(?<=^\\(|^)\\d+(?= AP\\):)").matcher(variantString);
        if (varMatcher.find()) {
          variant.apValue = Integer.parseInt(varMatcher.group());
        }

        variantString = variantString.replaceAll("\\(\\d+ AP\\)\\*?", "").trim();

        varMatcher = Pattern.compile("(?<=Sprachen und Schriften für insgesamt )\\d+(?= Abenteuerpunkte(, | statt |<br>|$))").matcher(variantString);
        if (varMatcher.find()) {
          variant.minLanguageAp = Integer.parseInt(varMatcher.group());
          variantString = variantString.replaceAll("Sprachen und Schriften für insgesamt \\d+ Abenteuerpunkte(, | statt |<br>|$)", "");
        }

        varMatcher = Pattern.compile("(?<=Kultur ).*(?= wählen)").matcher(variantString);
        if (varMatcher.find()) {
          List<CultureKey> reqCultureKeys = List.of(varMatcher.group().split(",| oder ")).stream()
              .map(cstr -> ExtractorCultureKey.retrieve(cstr.trim()))
              .collect(Collectors.toList());
          variant.requiredCultureKeys = reqCultureKeys;
          variantString = variantString.replaceAll("[A-ü ]*Kultur .* wählen", "");
        }

        varMatcher = Pattern.compile("(?<=Empfohlener Vorteil )[A-ü]+(?=,|$)").matcher(variantString);
        if (varMatcher.find()) {
          variant.boonsRecomended.add(ExtractorBoonKey.retrieve(varMatcher.group()));
          variantString = variantString.replaceAll("Empfohlener Vorteil [A-ü]+(?=,|$)", "");
        }

        varMatcher = Pattern.compile("[A-ü ]+ (weiblich|männlich) [A-ü ]+").matcher(variantString);
        if (varMatcher.find()) {
          String genderString = varMatcher.group();
          variant.requiredGender = genderString.contains(" weiblich ") ? GenderKey.weiblich : GenderKey.männlich;
          variantString = variantString.replace(genderString, "");
        }

        varMatcher = Pattern.compile("(keine |zusätzliche )?Voraussetzung(en)?:?[A-ü (),\\d-]+(?=<br>|$|)").matcher(variantString);
        if (varMatcher.find()) {
          String preRequisitString = varMatcher.group().replace("folgende Änderungen der Kampftechniken", "");
          if (preRequisitString.startsWith("keine")) {

          }
          else {
            variant.requiredAttributes.addAll(extractRequiredAttributes(preRequisitString));
            variant.requiredBoons.addAll(extractRequiredBoons(preRequisitString));
            variant.requiredAbilities.addAll(extractRequiredAbilities(preRequisitString));
          }
          variantString = variantString.replace(preRequisitString, "");
        }

        varMatcher = Pattern.compile("(keine |nur )?Fertigkeitsspezialisierung [A-ü & -]*(,|<br>|$)").matcher(variantString);
        while (varMatcher.find()) {
          String specString = varMatcher.group();
          boolean removeSpec = specString.replaceAll("$SF:? ", "").startsWith("keine ");
          boolean removeOtherSpecs = specString.startsWith("nur ");
          boolean replaceSpec = specString.contains("statt ");
          String nameNewSpec = "";
          if (removeSpec) {
            List<SkillKey> removeSpecs = Arrays.stream(cleanupSpecString(specString)
                    .split(" oder |,"))
                .map(name -> ExtractorSkillKey.retrieveSkillKey(name.trim()))
                .collect(Collectors.toList());

            variant.specializationOptions = variant.specializationOptions.stream()
                .filter(vc -> vc.type != ValueChangeType.specialize || removeSpecs.contains(vc.skillKey))
                .map(vc -> {
                  if (vc.type == ValueChangeType.specialize) {
                    if (vc.skillKeysOneOf != null) {
                      vc.skillKeysOneOf = vc.skillKeysOneOf.stream().filter(sk -> !removeSpecs.contains(sk)).collect(Collectors.toList());
                    }
                  }
                  return vc;
                }).filter(vc -> vc.type != ValueChangeType.specialize || vc.skillKey != null || vc.skillKeysOneOf.size() > 0)
                .collect(Collectors.toList());
          }
          else if (removeOtherSpecs) {
            variant.specializationOptions = variant.specializationOptions.stream().filter(vc -> vc.type != ValueChangeType.specialize).collect(Collectors.toList());
            ValueChange specVc = new ValueChange();
            specVc.key = ValueChangeKey.skill;
            specVc.type = ValueChangeType.specialize;
            specVc.skillKey = ExtractorSkillKey.retrieveSkillKey(cleanupSpecString(specString));
            variant.specializationOptions.add(specVc);
          }
          else if (replaceSpec) {
            List<SkillKey> replacementList = List.of(cleanupSpecString(specString).split(" (statt|anstatt) ")).stream()
                .map(str -> ExtractorSkillKey.retrieveSkillKey(str.trim()))
                .collect(Collectors.toList());

            variant.specializationOptions = variant.specializationOptions.stream().map(so -> {
              if (so.skillKey == replacementList.get(1)) {
                so.skillKey = replacementList.get(0);
              }
              if (so.skillKeysOneOf != null && so.skillKeysOneOf.contains(replacementList.get(1))) {
                so.skillKeysOneOf = so.skillKeysOneOf.stream().filter(sk -> sk != replacementList.get(1)).collect(Collectors.toList());
                so.skillKeysOneOf.add(replacementList.get(0));
              }
              return so;
            }).collect(Collectors.toList());
          }
          else {
            nameNewSpec = cleanupSpecString(specString);
            ValueChange specVc = new ValueChange();
            specVc.key = ValueChangeKey.skill;
            specVc.type = ValueChangeType.specialize;
            specVc.skillKey = ExtractorSkillKey.retrieveSkillKey(nameNewSpec);
            variant.specializationOptions.add(specVc);
          }
          variantString = variantString.replace(specString, "");
        }

        Matcher msMinApMatcher = Pattern.compile("(?<=weitere Liturgien im Wert von )\\d+(?= AP)").matcher(variantString);
        if (msMinApMatcher.find()) {
          variant.minMysticalSkillAp = Integer.valueOf(msMinApMatcher.group());
          variantString = variantString.replaceAll("weitere Liturgien im Wert von \\d+ AP", "");
        }

        varMatcher = Pattern.compile("(?<=:).*").matcher(variantString);
        if (varMatcher.find()) {
          String scString = varMatcher.group().trim().replace("Wogenform 5, statt Steinwand", "Wogenform 5 statt Steinwand");
          boolean replaceAllMysticalSkills = Pattern.compile("Zauber.*statt der angegebenen").matcher(scString).find();
          if (replaceAllMysticalSkills) {
            variant.mysticalSkillChanges = new ArrayList<>();
            scString = scString.replace("statt der angegebenen", "").trim();
          }
          Matcher skillChangeMatcher = Pattern.compile("[A-ü\\d \\-&\\(\\)\\*]*\\d+( statt .*?)?(?=,|<br>|$)").matcher(scString);

          while (skillChangeMatcher.find()) {
            String skillChangeString = skillChangeMatcher.group();
            String skillName = skillChangeString.replaceAll("\\d+.*", "")
                .replace("Lebensmittelverarbeitung", "Lebensmittelbearbeitung")
                .replace("Armbürste", "Armbrüste")
                .replaceAll("bei der Auswahl nur Schwerter.*(?=,|$)", "")
                .replaceAll("keine weitere Kampftechnik.*(?=,|$)", "")
                .trim();
            Matcher replacedSkillMatcher = Pattern.compile("(?<=\\d statt )[A-ü ()]+").matcher(skillChangeString);
            String skillReplacedName = replacedSkillMatcher.find() ? replacedSkillMatcher.group() : null;
            if (!skillName.isEmpty()) {
              boolean isSkillKey = ExtractorSkillKey.isSkillKey(skillName);
              boolean isCombatSkillKey = ExtractorCombatSkillKeys.isCombatSkillKey(skillName);
              boolean isMysticalSkillKey = ExtractorMysticalSkillKey.isMysticalSkillKey(skillName, isMagical);

              Matcher vm = Pattern.compile("\\d+").matcher(skillChangeString);
              final int firstValue = (vm.find())
                  ? Integer.valueOf(vm.group())
                  : 0;

              if (isSkillKey) {
                variant.skillChanges = variant.skillChanges.stream().map(sc -> {
                      if (skillReplacedName == null && (sc.skillKey == ExtractorSkillKey.retrieveSkillKey(skillName) && sc.type == ValueChangeType.value)) {
                        sc.change = firstValue;
                      }
                      else if (skillReplacedName != null && (sc.skillKey == ExtractorSkillKey.retrieveSkillKey(skillReplacedName.trim()) && sc.type == ValueChangeType.value)) {
                        sc.change = firstValue;
                        sc.skillKey = ExtractorSkillKey.retrieveSkillKey(skillName.trim());
                      }
                      return sc;
                    }).filter(sc -> sc.type != ValueChangeType.value || sc.change > 0)
                    .collect(Collectors.toList());
              }

              if (isCombatSkillKey) {
                variant.combatSkillChanges = variant.combatSkillChanges.stream().map(sc -> {
                      if (sc.combatSkillKey == ExtractorCombatSkillKeys.retrieveFromName(skillName) && sc.type == ValueChangeType.value) {
                        sc.change = firstValue;
                      }
                      else if (skillReplacedName != null && (sc.combatSkillKey == ExtractorCombatSkillKeys.retrieveFromName(skillReplacedName.trim()) && sc.type == ValueChangeType.value)) {
                        sc.change = firstValue;
                        sc.combatSkillKey = ExtractorCombatSkillKeys.retrieveFromName(skillName.trim());
                        System.out.println(variant.name + " --> " + skillName + " <4> " + skillReplacedName);
                      }
                      return sc;
                    }).filter(sc -> sc.type != ValueChangeType.value || sc.change > 0)
                    .collect(Collectors.toList());
              }


              if (isMysticalSkillKey && !replaceAllMysticalSkills) {
                variant.mysticalSkillChanges = variant.mysticalSkillChanges.stream().map(sc -> {
                      if (sc.mysticalSkillKeys.get(0) == ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(skillName, isMagical) && sc.type == ValueChangeType.value) {
                        sc.change = firstValue;
                      }
                      else if (skillReplacedName != null && (sc.mysticalSkillKeys.get(0) == ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(skillReplacedName.trim(), isMagical) && sc.type == ValueChangeType.value)) {
                        sc.change = firstValue;
                        sc.mysticalSkillKeys = List.of(ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(skillName.trim(), isMagical));

                        if (skillName.contains("(")) {
                          String tradition = skillName.replaceAll("(Attributo.*)|Aufnahme.*|[^()\\r\\n]+(?![^()]*\\))", "").replace("(", "").replace(")", "").trim();
                          if (!tradition.isEmpty()) {
                            sc.traditionKey = ExtractorTradtion.extractTraditionKeyFromText(tradition);
                          }
                        }

                      }
                      return sc;
                    }).filter(sc -> sc.type != ValueChangeType.value || sc.change > 0)
                    .collect(Collectors.toList());
              }

              if (isMysticalSkillKey && replaceAllMysticalSkills) {
                ValueChange msc = new ValueChange();
                msc.key = ValueChangeKey.skill;
                msc.type = ValueChangeType.value;
                msc.change = firstValue;
                msc.mysticalSkillKeys = List.of(ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(skillName, isMagical));
                variant.mysticalSkillChanges.add(msc);
              }
            }
            variantString = variantString
                .replace(skillChangeString, "")
                .replace("Wogenform 5, statt Steinwand", "")
                .trim();


          }


          if (variantString.contains("eine der folgenden Kampftechniken 12") && variant.key == ProfessionKey.kopfgeldjäger_in || variant.key == ProfessionKey.geheimagent_in || variant.key == ProfessionKey.meuchelmörder_in) {
            ValueChange vc = new ValueChange();
            vc.key = ValueChangeKey.skill;
            vc.type = ValueChangeType.value;
            vc.change = 12;
            vc.combatSkillKeysOneOf = List.of(CombatSkillKey.fechtwaffen, CombatSkillKey.hiebwaffen, CombatSkillKey.schwerter);
            variant.combatSkillOptions.add(vc);
            variantString = variantString
                .replace("eine der folgenden Kampftechniken 12: Fechtwaffen, Hiebwaffen, Schwerter", "")
                .replace("Hiebwaffen oder Schwerter", "");
          }

          if (variant.key == ProfessionKey.bogenschütze_in) {
            ValueChange vc = new ValueChange();
            vc.key = ValueChangeKey.skill;
            vc.type = ValueChangeType.value;
            vc.change = 12;
            vc.combatSkillKey = CombatSkillKey.bögen;
            variant.combatSkillChanges.add(vc);
            variant.combatSkillOptions = variant.combatSkillOptions.stream().filter(cso -> cso.change != 12).collect(Collectors.toList());
            variantString = variantString.replace("Bögen 12 (als eine der ausgewählten Kampftechniken)", "");
          }

          if (variant.key == ProfessionKey.khunchom) {
            ValueChange vc1 = new ValueChange();
            vc1.key = ValueChangeKey.skill;
            vc1.type = ValueChangeType.value;
            vc1.change = 12;
            vc1.combatSkillKey = CombatSkillKey.schwerter;
            variant.combatSkillChanges.add(vc1);

            ValueChange vc2 = new ValueChange();
            vc2.key = ValueChangeKey.skill;
            vc2.type = ValueChangeType.value;
            vc2.change = 12;
            vc2.combatSkillKey = CombatSkillKey.zweihandschwerter;
            variant.combatSkillChanges.add(vc2);
            variant.combatSkillOptions = new ArrayList<>();
            variantString = variantString.replace("Veränderung der Kampftechniken:", "");
          }

          if (variant.key == ProfessionKey.rashdul) {
            ValueChange vc1 = new ValueChange();
            vc1.key = ValueChangeKey.skill;
            vc1.type = ValueChangeType.value;
            vc1.change = 12;
            vc1.combatSkillKeysOneOf = List.of(CombatSkillKey.schwerter, CombatSkillKey.stangenwaffen, CombatSkillKey.lanzen);

            ValueChange vc2 = new ValueChange();
            vc2.key = ValueChangeKey.skill;
            vc2.type = ValueChangeType.value;
            vc2.change = 12;
            vc2.combatSkillKeysOneOf = List.of(CombatSkillKey.schwerter, CombatSkillKey.stangenwaffen, CombatSkillKey.lanzen);
            variant.combatSkillOptions = List.of(vc1, vc2);
            variantString = variantString.replace("Veränderung der Kampftechniken:, sowie Lanzen", "");
          }

          if (variant.key == ProfessionKey.thalusa) {
            ValueChange vc1 = new ValueChange();
            vc1.key = ValueChangeKey.skill;
            vc1.type = ValueChangeType.value;
            vc1.change = 12;
            vc1.combatSkillKey = CombatSkillKey.schwerter;
            variant.combatSkillChanges.add(vc1);

            ValueChange vc2 = new ValueChange();
            vc2.key = ValueChangeKey.skill;
            vc2.type = ValueChangeType.value;
            vc2.change = 12;
            vc2.combatSkillKey = CombatSkillKey.stangenwaffen;
            variant.combatSkillChanges.add(vc2);
            variant.combatSkillOptions = new ArrayList<>();
            variantString = variantString.replace("Veränderung der Kampftechniken:", "");
          }

          variantString = variantString
              .replaceAll(" ?: ?", "")
              .replaceAll("<br>", ",")
              .replaceAll(",+", ",")
              .replace("folgende Änderungen der Kampftechniken", "")
              .replace("Zauber", "")
              .replace("Liturgien", "")
              .replace("Die Zwölf Segnungen", "")
              .replace("statt der angegebenen", "")
              .replace("SF( |,)", "")
              .replace(",.", ",")
              .replace("keine Änderungen", "")
              .trim();

          varMatcher = Pattern.compile("[A-ü]+[A-ü -]*").matcher(variantString.replace("<br>", ""));
          while (varMatcher.find()) {
            String abilityString = varMatcher.group();
            List<SpecialAbilityKey> abilityKeys = extractSpecialAbilities(abilityString.replaceAll(" statt .*", "")
                .replace("SF ", "")
                .replace("Sonderfertigkeiten", "")
                .replaceAll("Belastungsgewöhnung$", "Belastungsgewöhnung I")
                .replaceAll("keine? ", ""));
            List<SpecialAbilityKey> replacedAbilityKeys = abilityString.contains(" statt ")
                ? extractSpecialAbilities(abilityString.replaceAll(".* statt ", "")
                .replace("Sonderfertigkeiten", "")
                .replace("Scanlail-Stil", "scanlail_ni_uinin_stil")
                .replaceAll("keine? ", ""))
                : null;
            boolean isRemove = abilityString.contains("kein ") || abilityString.contains("keine ");

            if (isRemove) {
              variant.specialAbilities = variant.specialAbilities.stream().filter(sak -> !abilityKeys.contains(sak)).collect(Collectors.toList());
            }
            else {
              if (replacedAbilityKeys != null) {
                variant.specialAbilities = variant.specialAbilities.stream().filter(sak -> !replacedAbilityKeys.contains(sak)).collect(Collectors.toList());
              }
              variant.specialAbilities.addAll(abilityKeys);
            }
            if (abilityKeys.size() > 0) {
              variantString = variantString.replace(abilityString, "");
            }
          }


          if (true && !variantString.replace(",", "").trim().isEmpty()) {
            System.out.print(profession.name + " (" + variant.name + ")" + " --> ");
            System.out.println(variantString);
          }

        }
        returnValue.add(variant);
      }
      catch (IOException | ClassNotFoundException e) {
        System.out.println(e);
      }
    }
    return returnValue;
  }

  private static int extractNoOfTricks(String msString) {
    Matcher m = Pattern.compile("(Ein|ein(en)?|zwei) Zaubertricks? aus folgender Liste: [A-ü ,&]*(<br>)?").matcher(msString);
    if (m.find()) {
      String result = m.group();
      return result.startsWith("zwei") ? 2 : 1;
    }
    return 0;
  }


  private static List<MysticalSkillKey> extractTrickOptions(String msString) {
    Matcher m = Pattern.compile("(?<= Zaubertrick aus folgender Liste: )[A-ü ,&]*(?=<br>)").matcher(msString);
    if (m.find()) {
      return List.of(m.group().split(","))
          .stream().map(t -> ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(t.trim(), true))
          .collect(Collectors.toList());
    }
    return null;
  }

  private static List<SkillKey> extractAp4SkillsSet(String skillsText) {
    Matcher m = Pattern.compile("(?<=\\d AP).*").matcher(skillsText);
    if (m.find()) {
      String result = m.group();
      return result.contains("Handwerkstalente")
          ? List.of(SkillKey.alchimie, SkillKey.boote_und_schiffe, SkillKey.fahrzeuge, SkillKey.handel, SkillKey.heilkunde_gift, SkillKey.heilkunde_krankheiten, SkillKey.heilkunde_seele, SkillKey.heilkunde_wunden, SkillKey.holzbearbeitung, SkillKey.lebensmittelbearbeitung, SkillKey.lederbearbeitung, SkillKey.malen_und_zeichnen, SkillKey.metallbearbeitung, SkillKey.musizieren, SkillKey.schlösserknacken, SkillKey.steinbearbeitung, SkillKey.stoffbearbeitung)
          : Arrays.stream(result
              .replaceAll(" \\(.*\\)", "")
              .replaceAll(".*?:", "")
              .split(","))
          .filter(s -> s != null && !s.isEmpty())
          .map(s -> ExtractorSkillKey.retrieveSkillKey(s.trim()))
          .collect(Collectors.toList());
    }
    else {
      return null;
    }
  }

  private static int extractAp4Skills(String skillsText) {
    Matcher m = Pattern.compile("\\d+ AP.*$").matcher(skillsText);
    if (m.find()) {
      String result = m.group();
      return Integer.valueOf(result.replaceAll("\\D+", ""));
    }
    else {
      return 0;
    }
  }

  private static List<TerrainTypeKey> extractTerrainKnowledgeOptions(String specialAbilities) {
    Matcher m = Pattern.compile("(?<=Geländekunde )([A-ü]+|\\([A-ü \\-,]*\\))").matcher(specialAbilities);
    if (m.find()) {
      List<String> terrainStrings = List.of(m.group()
          .replace("(", "")
          .replace(")", "")
          .replace("kundig", "")
          .replace("-", "")
          .replace("Gebirgs", "gebirge")
          .replace("Höhlen", "höhle")
          .replace("Meeres", "meer")
          .replace("Steppen", "steppe")
          .replace("Wüsten", "wüste")
          .split(",|oder"));
      return terrainStrings.stream().map(ts -> TerrainTypeKey.valueOf(ts.trim().replace(" ", "_").toLowerCase())).collect(Collectors.toList());

    }
    return new ArrayList<>();
  }

  private static int extractCurseAp(String specialAbilities) {
    Matcher m = Pattern.compile("(?<=Flüche für insgesamt )\\d+(?= [A-ü]+(,|$))").matcher(specialAbilities);
    if (m.find()) return Integer.valueOf(m.group());
    else return 0;
  }

  private static String cleanSAs(String saTxt) {
    return saTxt.replaceAll("(Sprachen für insgesamt |Sprachen und Schriften für insgesamt )[0-9]*,?\\s?Abenteuerpunkte", "") // remove Sprach AP
        .replaceAll("[A-ü ]*Fertigkeitsspezialisierung [A-ü &-()]*(, )?", "") // remove Spezialisierungen
        .replaceAll("Geländekunde [A-ü]+|Geländekunde \\([A-ü \\-,]*\\)", "") // remove Geländekunden
        .replaceAll("Flüche für insgesamt \\d+ [A-ü]+(,|$)", "") // remove Fluch AP
        .replaceAll("[A-ü ]* \\d(,|$)", "") // remove magische Handlungen
        .replace("Ishannahal’Kira-Stil", "Ishannah'al'Kira Stil")
        .replace("Berittener Kampf. Finte I", "Berittener Kampf, Finte I")
        .replace("Sarviniodella-Monte", "Sarvinio-della-Monte")
        .replace("Scanlailni-Uinin", "Scanlail-ni-Uinin")
        .replace("Tulamid-Stil", "Tulamidischer Balayan-Stil")
        .replace("Scholar Akademie der Geistreisen zu Belhanka", "Scholar der Akademie der Geistreisen zu Belhanka")
        .replace("Scholar der Dunklen Halle zu Brabak", "Scholar der dunklen Halle der Geister zu Brabak")
        .replace("Rashduler Dämonologen", "Scholar der Rashduler Dämonologen")
        .replace("Scholar der Schule des magischen Wissens zu Methumis", "Scholar der Akademie des magischen Wissens zu Methumis")
        .replace("Bindung des Schwertes wurde für ein Bannschwert der Kampftechnik Dolche erworben", "Bindung des Bannschwerts Dolche")
        .replace("Bindung des Bannschwertes", "Bindung des Bannschwerts Dolche")
        .trim();
  }

  private static int extractMinLanguageAp(String specialAbilities) {
    int returnValue = 0;
    Matcher m = Pattern.compile("(?<=Sprachen für insgesamt |Sprachen und Schriften für insgesamt )[0-9]*(?=\\s?Abenteuerpunkte)").matcher(specialAbilities);
    if (m.find()) returnValue = Integer.valueOf(m.group());
    return returnValue;
  }

  private static boolean extractIsOnly4Language(String specialAbilities) {
    return specialAbilities.contains("Sprachen für insgesamt");
  }

  private static List<ValueChange> extractMysticalSkillChanges(String msChangeString, boolean isMagical) {
    List<ValueChange> returnValue = new ArrayList<>();
    String t;
    String msName = null;
    Integer msValue = null;
    Matcher m = Pattern.compile("[A-ü ()]* \\d+(?=(,|$))").matcher(msChangeString);
    while (m.find()) {
      t = m.group().trim();
      msName = t.replaceAll("\\d+", "").trim();
      msValue = Integer.valueOf(t.replace(msName, "").trim());
      ValueChange vc = new ValueChange();
      vc.key = ValueChangeKey.skill;
      vc.type = ValueChangeType.value;
      vc.change = msValue;
      vc.mysticalSkillKeys = List.of(ExtractorMysticalSkillKey.extractMysticalSkillKeyFromText(msName, isMagical));

      if (msName.contains("(")) {
        String tradition = msName.replaceAll("(Attributo.*)|Aufnahme.*|[^()\\r\\n]+(?![^()]*\\))", "").replace("(", "").replace(")", "").trim();
        if (!tradition.isEmpty()) {
          vc.traditionKey = ExtractorTradtion.extractTraditionKeyFromText(tradition);
        }
      }
      returnValue.add(vc);
    }
    return returnValue;
  }

  private static List<ValueChange> extractSpecialisations(String saText) {
    List<ValueChange> returnValue = new ArrayList<>();
    Matcher m = Pattern.compile("(?<=Fertigkeitsspezialisierung )[A-ü &-()]*").matcher(saText);
    while (m.find()) {
      List<String> skillNames = List.of(m.group().split(" oder "));
      ValueChange vc = new ValueChange();
      vc.key = ValueChangeKey.skill;
      vc.type = ValueChangeType.specialize;
      if (skillNames.size() == 1) {
        String skillName = skillNames.get(0).replaceAll("(?<=[A-ü]) \\([A-ü &-]*\\)", "");
        String usageName = skillNames.get(0).replace(skillName, "");
        if (!usageName.isEmpty()) {
          SkillUsageKey suk = ExtractorSkillKey.retrieveSkillUsageKey(usageName.replace("(", "").replace(")", "").trim());
          vc.usageKeys = List.of(suk);
        }
        vc.skillKey = ExtractorSkillKey.retrieveSkillKey(skillName);
      }
      else {
        vc.skillKeysOneOf = skillNames.stream().map(sName -> ExtractorSkillKey.retrieveSkillKey(sName.trim())).collect(Collectors.toList());
      }
      returnValue.add(vc);
    }
    return returnValue;
  }

  private static List<ValueChange> extractCombatskillChanges(String combatSkillChanges) {
    List<ValueChange> returnValue = new ArrayList<>();
    Matcher m = Pattern.compile("[A-ü]+[A-ü &-]*\\d+").matcher(
        combatSkillChanges
            .replace("Schleuder ", "Schleudern ")
            .replaceAll(", ((eine|zwei) der (nach)?folgenden Kampftechniken (auf )?\\d+(, eine weitere \\d+)?: .*$|eine Kampftechnik aus folgender Liste \\d+:.*$|[A-ü]* oder [A-ü]* \\d+)", "") //remove optional selections
            .trim());
    while (m.find()) {
      String skillText = m.group().trim();
      String skillName = skillText.replaceAll("\\d*", "")
          .trim();
      Integer skillValue = Integer.valueOf(skillText.replace(skillName, "").trim());

      ValueChange vc = new ValueChange();
      vc.key = ValueChangeKey.skill;
      vc.type = ValueChangeType.value;
      vc.change = skillValue;
      vc.combatSkillKey = ExtractorCombatSkillKeys.retrieveFromName(skillName);

      returnValue.add(vc);
    }
    return returnValue;
  }

  private static List<ValueChange> extractCombatskillOptions(String combatSkillChanges) {
    List<ValueChange> returnValue = new ArrayList<>();
    String result = null;
    String currentText = combatSkillChanges.replace("Hiebwaffen Kettenwaffen", "Hiebwaffen, Kettenwaffen");

    // * eine aus der Liste auf 10,
    // * A oder B,
    final String REG_ONE_OF_LIST_INCL_OR = "([A-ü]* oder [A-ü]* \\d+)|(eine der (nach)?folgenden Kampftechniken (auf )?\\d+: .*$)|(eine Kampftechnik aus folgender Liste \\d+:.*$)";
    Matcher m = Pattern.compile(REG_ONE_OF_LIST_INCL_OR).matcher(currentText);
    if (m.find()) {
      result = m.group();
      ValueChange vc = new ValueChange();
      vc.key = ValueChangeKey.combatSkill;
      vc.type = ValueChangeType.value;
      vc.change = Integer.valueOf(result.replaceAll("\\D*", ""));
      vc.combatSkillKeysOneOf = List.of(
              result.replaceAll(".*:", "").replaceAll("\\d*", "").split("(,|oder)")
          ).stream()
          .map(sn -> ExtractorCombatSkillKeys.retrieveFromName(sn.trim())).collect(Collectors.toList());
      returnValue.add(vc);
      currentText.replaceAll(REG_ONE_OF_LIST_INCL_OR, "");
    }
    // * zwei aus der Liste auf 10,
    final String REG_TWO_OF_LIST = "zwei der (nach)?folgenden Kampftechniken (auf )?\\d+: .*$";
    m = Pattern.compile(REG_TWO_OF_LIST).matcher(currentText);
    if (m.find()) {
      result = m.group();
      ValueChange vc = new ValueChange();
      vc.key = ValueChangeKey.combatSkill;
      vc.type = ValueChangeType.value;
      vc.change = Integer.valueOf(result.replaceAll("\\D*", ""));
      vc.combatSkillKeysOneOf = List.of(
              result.replaceAll(".*:", "").replaceAll("\\d*", "").split("(,|oder)")
          ).stream()
          .map(sn -> ExtractorCombatSkillKeys.retrieveFromName(sn.trim())).collect(Collectors.toList());

      returnValue.add(vc);
      returnValue.add(vc); //add second
      currentText.replaceAll(REG_TWO_OF_LIST, "");
    }
    // * eine aus der Liste auf 10, eine weiter auf 8)
    final String REG_ONE_AND_ANOTHER = "eine der (nach)?folgenden Kampftechniken (auf )?\\d+, eine weitere \\d+: .*$";
    m = Pattern.compile(REG_ONE_AND_ANOTHER).matcher(currentText);
    if (m.find()) {
      result = m.group();
      List<Integer> values = Arrays.stream(result.split("\\D+"))
          .filter(s -> s != null && !s.isEmpty())
          .map(s -> Integer.valueOf(s))
          .toList();

      ValueChange vc1 = new ValueChange();
      vc1.key = ValueChangeKey.combatSkill;
      vc1.type = ValueChangeType.value;
      vc1.change = values.get(0);
      vc1.combatSkillKeysOneOf = List.of(
              result.replaceAll(".*:", "").replaceAll("\\d*", "").split("(,|oder)")
          ).stream()
          .map(sn -> ExtractorCombatSkillKeys.retrieveFromName(sn.trim())).collect(Collectors.toList());

      returnValue.add(vc1);
      ValueChange vc2 = new ValueChange();
      vc2.key = vc1.key;
      vc2.type = vc1.type;
      vc2.change = values.get(1);
      vc2.combatSkillKeysOneOf = vc1.combatSkillKeysOneOf;

      returnValue.add(vc2); //add second
      currentText.replaceAll(REG_TWO_OF_LIST, "");
    }
    return returnValue;
  }

  private static List<ValueChange> extractSkillChanges(String skillChanges) {
    List<ValueChange> returnValue = new ArrayList<>();
    Matcher m = Pattern.compile("[A-ü]+[A-ü &-]*\\d").matcher(skillChanges.trim());
    while (m.find()) {
      String skillText = m.group()
          .replace("Körper ", "")
          .replace("Gesellschaft ", "")
          .replace("Natur ", "")
          .replace("Wissen ", "")
          .replace("Handwerk ", "")
          .replaceAll("Gassenwisse ", "Gassenwissen ")
          .replaceAll("Brett- & Kartenspiel ", "Brett- und Glücksspiel ")
          .trim();
      String skillName = skillText.replaceAll("\\d*", "")
          .trim();
      Integer skillValue = Integer.valueOf(skillText.replace(skillName, "").trim());

      ValueChange vc = new ValueChange();
      vc.key = ValueChangeKey.skill;
      vc.type = ValueChangeType.value;
      vc.change = skillValue;
      vc.skillKey = ExtractorSkillKey.retrieveSkillKey(skillName);
      returnValue.add(vc);
    }
    return returnValue;
  }

  private static List<SpecialAbilityKey> extractSpecialAbilities(String specialAbilityNames) {
    return Arrays.stream(specialAbilityNames.split(",|(?<! Verständigung| Licht| Schwert) und "))
        .filter(saName -> !saName.isEmpty() && !isObjectRitual(saName))
        .map(ExtractorSpecialAbility::retrieve)
        .filter(k -> k != null)
        .collect(Collectors.toList());
  }

  private static boolean isObjectRitual(String saName) {
    return ExtractorObjectRitual.extractOrKeyFromName(saName) != null;
  }

  private static List<ObjectRitualKey> extractObjectRituals(String specialAbilityNames) {
    return Arrays.stream(specialAbilityNames.split(",|(?<! Verständigung| Licht| Schwert) und "))
        .filter(saName -> !saName.isEmpty() && !isSpecialAbility(saName.trim()))
        .map(saName -> saName.trim())
        .map(ExtractorObjectRitual::extractOrKeyFromName)
        .filter(ork -> ork != null)
        .collect(Collectors.toList());
  }

  private static boolean isSpecialAbility(String saName) {
    return ExtractorSpecialAbility.retrieve(saName) != null;
  }

  public static List<SpecieKey> extractRequiredSpecieKeys(String preconditions) {
    List<SpecieKey> returnValue = new ArrayList<>();
    //(?<=Spezies |Spezies: ).*?(?=,|$|\(| Vorteil)
    Pattern p = Pattern.compile("(?<=Spezies |Spezies: ).*?(?=,|$|\\(| Vorteil)");
    Matcher m = p.matcher(preconditions);
    while (m.find()) {
      String specieString = m.group();
      List<String> species = List.of(specieString.split("oder"));
      returnValue.addAll(species.stream().map(str -> ExtractorSpecieKey.retrieve(str.trim())).collect(Collectors.toList()));
    }
    return returnValue;
  }

  public static List<CultureKey> extractRequiredCultureKeys(String preconditions) {
    List<CultureKey> returnValue = new ArrayList<>();
    //(?<=Kultur: |Kultur )[A-ü -]*(?=,|$|\()
    Pattern p = Pattern.compile("(?<=Kultur: |Kultur )[A-ü -]*(?=,|$|\\()");
    Matcher m = p.matcher(preconditions);
    while (m.find()) {
      String specieString = m.group();
      List<String> cultures = List.of(specieString.split("oder"));
      returnValue.addAll(cultures.stream().map(str -> ExtractorCultureKey.retrieve(str.trim())).collect(Collectors.toList()));
    }
    return returnValue;
  }

  public static GenderKey extractRequiredGenderKey(String preconditions) {

    GenderKey returnValue = null;
    Pattern p = Pattern.compile("(?<=Geschlecht: |Geschlecht )[A-ü -]*(?=,|$|\\()");
    Matcher m = p.matcher(preconditions);
    while (m.find()) {
      String genderString = m.group();
      returnValue = ExtractorGenderKey.retrieve(genderString);
    }
    return returnValue;
  }

  private static List<AttributeValuePair> extractRequiredAttributes(String preconditions) {
    List<AttributeValuePair> returnValue = new ArrayList<>();
    Pattern p = Pattern.compile("//MU \\d\\d|KL \\d\\d|IN \\d\\d|CH \\d\\d|FF \\d\\d|GE \\d\\d|KO \\d\\d|KK \\d\\d");
    Matcher m = p.matcher(preconditions);
    while (m.find()) {
      String[] reqStringSplit = m.group().split(" ");
      returnValue.add(new AttributeValuePair(AttributeShort.valueOf(reqStringSplit[0]), Integer.valueOf(reqStringSplit[1])));
    }
    return returnValue;
  }


  public static List<RequirementBoon> extractRequiredBoons(String preconditions) {
    List<RequirementBoon> returnValue = new ArrayList<>();
    Pattern p = Pattern.compile("(?<=Nachteil\\s)(.*?)(?=, Sonderfertigkeit|, Vorteil|, Nachteil|, Spezies|, Geschlecht|$)|(?<=(Vorteil|Ahnenblut)\\s)(.*?)(?=, Sonderfertigkeit|, Nachteil|, Spezies|, Geschlecht|$)");
    Matcher m = p.matcher(preconditions);
    while (m.find()) {
      String meritOrFlawString = m.group();
      List<String> boonStrings = List.of(meritOrFlawString.split(",(?![^(]*\\))"));
      returnValue.addAll(boonStrings.stream().map(bs -> {
        String boonCleaned = bs.replaceAll("\\([^(]*?AP\\)", ""); //remove the AP String
        String boonWithLevel = bs.replaceAll("\\([^(]*?\\)", "")
            .replace("Ahnenblut-Vorteil", "") // Korrigiere Ahnenblut-Vorteil
            .trim(); //extract the Name
        String boonSuffix = boonCleaned.replace(boonWithLevel, "").replace("(", "").replace(")", "").trim(); //extract Text in Parantheses
        String boonName = boonWithLevel.replaceAll("\\b([IVXLCDM]+)\\b$", "")
            .trim(); //extract Name without level
        String boonLevel = boonWithLevel.replace(boonName, "").trim();
        boonName = boonName.replaceAll("Verpflichtung$", "Verpflichtungen");
        return new RequirementBoon(ExtractorBoonKey.retrieve(boonName), true, boonSuffix, RomanNumberHelper.romanToInt(boonLevel));
      }).collect(Collectors.toList()));
    }
    return returnValue;
  }

  public static List<RequirementSpecialAbility> extractRequiredAbilities(String preconditions) {
    List<RequirementSpecialAbility> returnValue = new ArrayList<>();
    Pattern p = Pattern.compile("(?<=Sonderfertigkeit\\s)(.*?)(?=, Sonderfertigkeit|, Nachteil|, Vorteil|, Spezies|, Kultur|, Geschlecht|$)");
    Matcher m = p.matcher(preconditions
        .replace("Tradition (eine Zwölfgötterkirche)", ""));
    while (m.find()) {
      String abilityString = m.group();
      String abilityCleaned = m.group().replaceAll("\\([^(]*?AP\\)", "").trim(); //remove the AP String
      String abilityName = abilityString.replaceAll("\\([^(]*?\\)", "").trim(); //remove the AP String
      String abilitySuffix = abilityCleaned.replace(abilityName, "").replace("(", "").replace(")", "").trim()
          .replaceAll("Schelm$", "Schelme")
          .replaceAll("Namenloser$", "Namenloser Kult")
          .replaceAll("Rondra$", "Rondrakirche")
          .replaceAll("Tairachkult$", "Tairachschamane")
          .trim(); //extract Text in Parantheses

      returnValue.add(new RequirementSpecialAbility(ExtractorSpecialAbility.retrieve(abilityName + (abilityName.equalsIgnoreCase("tradition") ? " " + abilitySuffix : "")), abilitySuffix));
    }
    return returnValue;
  }

  public static <T extends Serializable> T deepCopy(T object) throws IOException, ClassNotFoundException {
    // Write the object to a byte array
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
    outputStrm.writeObject(object);
    outputStrm.flush();
    outputStrm.close();

    // Read the object from the byte array
    ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
    return (T) objInputStream.readObject();
  }

  private static String cleanupSpecString(String text) {
    return text.replace("Fertigkeitsspezialisierung ", "")
        .replace("keine ", "")
        .replace("nur ", "")
        .replace(",", "")
        .replace("<br>", "")
        .trim();
  }
}
