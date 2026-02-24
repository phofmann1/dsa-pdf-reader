package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Triplet;

import de.pho.dsapdfreader.exporter.model.RequirementAttribute;
import de.pho.dsapdfreader.exporter.model.RequirementBoon;
import de.pho.dsapdfreader.exporter.model.RequirementCombatSkill;
import de.pho.dsapdfreader.exporter.model.RequirementMysticalSkill;
import de.pho.dsapdfreader.exporter.model.RequirementSkill;
import de.pho.dsapdfreader.exporter.model.RequirementSkillSum;
import de.pho.dsapdfreader.exporter.model.RequirementSpecialAbility;
import de.pho.dsapdfreader.exporter.model.RequirementsAttribute;
import de.pho.dsapdfreader.exporter.model.RequirementsCombatSkill;
import de.pho.dsapdfreader.exporter.model.RequirementsSkill;
import de.pho.dsapdfreader.exporter.model.RequirementsSpecialAbility;
import de.pho.dsapdfreader.exporter.model.SpecialAbilityAdvancedSelection;
import de.pho.dsapdfreader.exporter.model.SpecialAbilityOption;
import de.pho.dsapdfreader.exporter.model.ValueChange;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.BoonKey;
import de.pho.dsapdfreader.exporter.model.enums.BoonVariantKey;
import de.pho.dsapdfreader.exporter.model.enums.CombatSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.LogicalOperatorKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillFeature;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillModification;
import de.pho.dsapdfreader.exporter.model.enums.SkillKey;
import de.pho.dsapdfreader.exporter.model.enums.SkillUsageKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;
import de.pho.dsapdfreader.exporter.model.enums.TargetCategory;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;
import de.pho.dsapdfreader.exporter.model.enums.UsageRestrictionKey;
import de.pho.dsapdfreader.exporter.model.enums.ValueChangeKey;
import de.pho.dsapdfreader.exporter.model.enums.ValueChangeType;

public class ExtractorSpecialAbility extends Extractor {

    public static final Pattern PAT_TALENT_MULTISELECT = Pattern.compile("bis zu drei .*alente aussuchen");
    private static final Pattern PAT_EXTRACT_SPECIE = Pattern.compile("(?<=Spezies )\\w*");
    private static final Pattern PAT_EXTRACT_TRADITION = Pattern.compile("(?<!(keine )Sonderfertigkeit Tradition \\()(?<=Tradition \\()[^\\)]*");

    private static final Pattern PAT_EXTRACT_NONE_OF_BOONS = Pattern.compile("(?<=kein Nachteil )[\\w Ä-ü\\(\\).\\/]*");
    private static final Pattern PAT_EXTRACT_ATTRIBUTES = Pattern.compile("(([MUKLINCHFGEO]{2}|Leiteigenschaft( der Tradition)?) \\d{2}( oder )?){1,2}");

    private static final Pattern PAT_EXTRACT_SKILL_REQ = Pattern.compile("([A-ZÄÖÜ]?[a-ü& -])+(?= \\d\\d?)|(?<=Zauber )[A-Z][A-Z \\-]+(?= ?\\d?\\d?)");
    private static final Pattern PAT_EXTRACT_MYSTICAL_SKILL_REQ = Pattern.compile("(?<=Zauber )[A-Z -]{3,}\\d?\\d?");

    private static final Pattern PAT_VC_QS_PLUS = Pattern.compile("(\\+| um) ?\\d QS");
    private static final Pattern PAT_VC_FP_PLUS = Pattern.compile("(?<=\\+| um) ?\\d(?= FP)");


    private static final SpecialAbilityKey[] NEBENFACH = {
            SpecialAbilityKey.begnadeter_objektzauberer,
            SpecialAbilityKey.bewanderter_heilzauberer,
            SpecialAbilityKey.brillanter_telekinetiker,
            SpecialAbilityKey.erfahrener_antimagier,
            SpecialAbilityKey.hervorragender_illusionist,
            SpecialAbilityKey.kundiger_sphärologe,
            SpecialAbilityKey.matrixzauberei,
            SpecialAbilityKey.unübertroffener_verwandler,
            SpecialAbilityKey.vollkommener_beherrscher,
            SpecialAbilityKey.vortrefflicher_hellseher,
    };
    private static final String GESUNDER_GEIST_REPLACEMENT = "Gesunder GeistXXX gesunder Körper";

    public static SpecialAbilityTypeKey retrieveType(String description) {
        if (description.contains("(passiv)")) return SpecialAbilityTypeKey.passive;
        if (description.contains("(Basismanöver)")) return SpecialAbilityTypeKey.basic;
        if (description.contains("(Spezialmanöver)")) return SpecialAbilityTypeKey.special;
        if (description.contains("(aktiv)")) return SpecialAbilityTypeKey.active;
        return null;
    }


    public static SpecialAbilityKey retrieve(String name) {
        SpecialAbilityKey returnValue = null;
        try {
            returnValue = extractSpecialAbilityKeyFromText(name);
            if (returnValue == null)
                throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            String msg = String.format("%s ability key could not be interpreted.", name);
            LOGGER.error(msg);
        }
        return returnValue;
    }

    public static SpecialAbilityKey retrieveNoLog(String name) {
        SpecialAbilityKey returnValue = null;
        try {
            returnValue = extractSpecialAbilityKeyFromText(name);
            if (returnValue == null)
                throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            //Nothing to report, this is the noLogMethod for isAbilityKey Checks
        }
        return returnValue;
    }

    private static SpecialAbilityKey extractSpecialAbilityKeyFromText(String name) throws IllegalArgumentException {
        SpecialAbilityKey returnValue = null;
        String keyString = extractKeyTextFromTextWithUmlauts(name.replace("ß", "xxx"))
                .toLowerCase()
                .replace("xxx", "ß")
                .replace("vertrauenswürdigswürdig", "vertrauenswürdig")
                .replace("sf_", "")
                .replace("je_nach_material", "")
                .replace("monate_in_entsprechender_umgebung_gelebt", "")
                .replace("alle_anderen_haben_keine_voraussetzungen", "")
                .replace("tradition_zibiljas", "tradition_zibilja")
                .replace("kampf_reflexe_i", "kampfreflexe_i")
                .replace("wesenszug_olochtai", "")
                .replace("wesenszug_zholochai", "")
                .replace("Gebieter der Reise", "gebieter_in_der_reise")
                .replace("Verkünder des Schickals", "verkünder_des_schicksals")
                .replace("scholar_des_seminars_der_elfischen_scholar_des_seminars_der_elfischen_", "scholar_des_seminars_der_elfischen_")
                .replaceAll("^ausfall$", "ausfall_i");

        keyString = keyString.trim();

        if (!keyString.isEmpty()) {
            try {
                returnValue = SpecialAbilityKey.valueOf(keyString.toLowerCase());
            } catch (IllegalArgumentException e) {
            }
        }

        return returnValue;
    }

    public static int retrieveMultiselect(String rules) {
        boolean hasThreeMultiselectTalents = PAT_TALENT_MULTISELECT.matcher(rules).find();
        boolean hasTwoSpells = rules.contains("Maximal können zwei Zauber mit dieser Sonderfertigkeit ausgestattet werden");
        int noOfSpellsFor2 = hasTwoSpells ? 2 : 1;
        return hasThreeMultiselectTalents ? 3 : noOfSpellsFor2;
    }

    public static SpecialAbilityAdvancedSelection retrieveAdvancedAbilities(String advancedAbilities, List<CombatSkillKey> csk) {
        SpecialAbilityAdvancedSelection returnValue = null;
        if (advancedAbilities != null && !advancedAbilities.isEmpty()) {
            returnValue = new SpecialAbilityAdvancedSelection();

            List<String> optionEntries = retrieveAdvancedAbilitiesOptionEntries(advancedAbilities);


            for (String o : optionEntries) {
                int selectionCount = o.startsWith("Drei") ? 3 : (o.startsWith("Zwei") ? 2 : 1);
                if (o.contains("Nebenfach")) {
                    SpecialAbilityOption sao = new SpecialAbilityOption();
                    sao.nOf = selectionCount;
                    sao.options = Arrays.stream(NEBENFACH).toList();
                    returnValue.advancedAbilityKeyOptions.add(sao);
                } else {
                    List<SpecialAbilityKey> options = retrieveAdvancedAbilitiyKeys(o.trim()
                            .replaceAll("[Dd]rei.*: ", "")
                            .replaceAll("[Zz]wei.*: ", "")
                            .replaceAll("[Ee]ine.*: ", ""), csk);
                    if (options != null && !options.isEmpty()) {
                        SpecialAbilityOption sao = new SpecialAbilityOption();
                        sao.nOf = selectionCount;
                        sao.options = options;
                        returnValue.advancedAbilityKeyOptions.add(sao);
                    }
                }
                advancedAbilities = advancedAbilities.replace(o, "").trim();
            }
            returnValue.advancedAbilityKeys = retrieveAdvancedAbilitiyKeys(advancedAbilities, csk);
        }
        return returnValue;
    }


    private static List<String> retrieveAdvancedAbilitiesOptionEntries(String advancedAbilities) {
        List<String> returnValue = new ArrayList<>();
        Matcher m = Pattern.compile("eine weitere erweiterte.*|eine erweiterte.*|Zwei erweiterte.*<br>|Drei [Ee]rweiterte.*").matcher(advancedAbilities);
        while (m.find()) {
            returnValue.add(m.group());
        }
        return returnValue;
    }

    public static List<SpecialAbilityKey> retrieveAdvancedAbilitiyKeys(String listOfAbilities, List<CombatSkillKey> csks) {
        String cleanList = cleanListOfAbilities(listOfAbilities);

        String[] splitAdvancedAbilities = cleanList.split(",|<br>");

        Stream<SpecialAbilityKey> r = (Arrays.stream(splitAdvancedAbilities)
                .filter(aaName -> !aaName.contains("oder"))
                .filter(aaName -> aaName != null && !aaName.trim().isEmpty())
                .map(aaName -> {
                    List result = new ArrayList();
                    String name = aaName
                            .replace(GESUNDER_GEIST_REPLACEMENT, "Gesunder Geist, gesunder Körper")
                            .replace("Vertrauen", "Vertrauenswürdig")
                            .replace("Gebieter der Magie", "Gebieter in der Magie")
                            .replace("Gebieter des Wissens", "Gebieter in des Wissens")
                            .replace("Gebieter der Reise", "Gebieter in der Reise")
                            .replace("Übertragung der Astralkraft", "Übertragung der Astralkräfte")
                            .replace("Übertragung der Lebenskräfte", "Übertragung der Lebenskraft")
                            .replace("Gebieter/in der Flammen", "Gebieter/in der Flamme")
                            .replace("Fluchmeisterin", "Langer Fluch")
                            .replaceAll("(?<=Fachwissen )\\(.*\\)", "")
                            .trim();
                    if (name.startsWith("Gegnerische Zauberpraxis (")) {
                        result.add(SpecialAbilityKey.gegnerische_zauberpraxis_eis);
                        result.add(SpecialAbilityKey.gegnerische_zauberpraxis_luft);
                        result.add(SpecialAbilityKey.gegnerische_zauberpraxis_wasser);
                    } else if (name.startsWith("Nachladespezialist")) {
                        if (csks != null) {
                            for (CombatSkillKey csk : csks
                            ) {
                                result.add(switch (csk) {
                                    case armbrüste -> SpecialAbilityKey.nachladespezialist_armbrüste;
                                    case blasrohre -> SpecialAbilityKey.nachladespezialist_blasrohre;
                                    case bögen -> SpecialAbilityKey.nachladespezialist_bögen;
                                    case diskusse -> SpecialAbilityKey.nachladespezialist_diskus;
                                    case schleudern -> SpecialAbilityKey.nachladespezialist_schleudern;
                                    case wurfwaffen -> SpecialAbilityKey.nachladespezialist_wurfwaffen;
                                    default -> null;
                                });
                            }
                            result.removeIf(Objects::isNull);
                        } else {
                            result.add(SpecialAbilityKey.nachladespezialist_armbrüste);
                            result.add(SpecialAbilityKey.nachladespezialist_blasrohre);
                            result.add(SpecialAbilityKey.nachladespezialist_bögen);
                            result.add(SpecialAbilityKey.nachladespezialist_diskus);
                            result.add(SpecialAbilityKey.nachladespezialist_schleudern);
                            result.add(SpecialAbilityKey.nachladespezialist_wurfwaffen);
                        }
                    } else {
                        result.add(ExtractorSpecialAbility.retrieve(name));
                    }
                    return result;
                })
                .filter(Objects::nonNull)
                .flatMap(Collection::stream));
        return r.collect(Collectors.toList());
    }

    private static String cleanListOfAbilities(String listOfAbilities) {
        Matcher m = Pattern.compile("(?<=\\().*,.*(?=\\))").matcher(listOfAbilities);
        String foundVal = null;
        while (m.find()) {
            foundVal = m.group();
            listOfAbilities = listOfAbilities
                    .replace("(" + foundVal + ")", "(" + foundVal.replace(",", ";") + ")")
                    .replace("und", ";");
        }

        return listOfAbilities.trim()
                .replace("Gesunder Geist, Gesunder Körper", GESUNDER_GEIST_REPLACEMENT)
                .replace("Gesunder Geist, gesunder Körper", GESUNDER_GEIST_REPLACEMENT)
                .replace("Kälte Kältegewöhnung", "Kälte, Kältegewöhnung")
                .replace("Wissenstausch", "Wissensaustausch")
                .replace("Dä mo nenmeisterin", "Dämonenmeisterin")
                .replaceAll("(?<=[a-z])\u00AD(?=[a-z])", "");
    }


    public static SpecieKey retrieveRequiredSpecie(String preconditions) {
        Matcher m = PAT_EXTRACT_SPECIE.matcher(preconditions);
        if (m.find()) {
            return switch (m.group()) {
                case "Zwerg" -> SpecieKey.zwerg;
                case "Elf" -> SpecieKey.elf;
                default -> null;
            };
        } else {
            return null;
        }
    }

    public static List<TraditionKey> retrieveRequiredTradition(String preconditions, String name) {

        Matcher m = PAT_EXTRACT_TRADITION.matcher(preconditions);
        if (m.find()) {
            String traditionsText = m.group();
            List<String> traditionTextList = Arrays.stream(traditionsText.split(",|oder")).toList();
            return traditionTextList.stream().map(tt -> extractTraditionKeyFromText(tt.replace("\u00AD", ""))).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public static List<RequirementBoon> retrieveRequiredOneOfBoons(SpecialAbilityCategoryKey category) {
        List<RequirementBoon> returnValue = new ArrayList<>();
        switch (category) {
            case cleric, cleric_advanced, cleric_stile -> returnValue.add(new RequirementBoon(BoonKey.geweihter, true));
            case magic, magic_advanced, magic_stile, magic_signs ->
                    returnValue.add(new RequirementBoon(BoonKey.zauberer, true));
            case vision -> returnValue = List.of(
                    new RequirementBoon(BoonKey.geweihter, true),
                    new RequirementBoon(BoonKey.visionär, true));
            case sermon -> returnValue = List.of(
                    new RequirementBoon(BoonKey.geweihter, true),
                    new RequirementBoon(BoonKey.prediger, true));
            default -> returnValue = new ArrayList<>();
        }
        return returnValue;


    }

    public static List<RequirementBoon> retrieveRequiredNoneOfBoons(String preconditions, SpecialAbilityKey key) {
        List<RequirementBoon> returnValue = new ArrayList<>();
        if (key == SpecialAbilityKey.vertrautenbindung) {
            returnValue.add(new RequirementBoon(BoonKey.kein_vertrauter, false));
        } else if (key == SpecialAbilityKey.flugsalbe) {
            returnValue.add(new RequirementBoon(BoonKey.keine_flugsalbe, false));
        } else {
            Matcher m = PAT_EXTRACT_NONE_OF_BOONS.matcher(preconditions);
            if (m.find()) {
                String boonsText = m.group();
                List<String> boonTextList = List.of(boonsText.split(",|oder|\\/"));
                returnValue.addAll(boonTextList.stream().map(bt -> switch (bt.trim()) {
                            case "Blind" -> new RequirementBoon(BoonKey.blind, false);
                            case "Angst vor ..." -> new RequirementBoon(BoonKey.angst_vor_x, false);
                            case "Angst vor Blut" ->
                                    new RequirementBoon(BoonKey.angst_vor_x, false, BoonVariantKey.angst_vor_x_blut);
                            case "Behäbig" -> new RequirementBoon(BoonKey.behäbig, false);
                            case "Unfrei" -> new RequirementBoon(BoonKey.unfrei, false);
                            case "Eingeschränkter Sinn (Tastsinn) (je nach Form des Leggaleg)" ->
                                    new RequirementBoon(BoonKey.eingeschränkter_sinn, false, BoonVariantKey.eingeschränkter_sinn_tastsinn);
                            case "Verstümmelt (Einäugig)" ->
                                    new RequirementBoon(BoonKey.verstümmelt, false, BoonVariantKey.verstümmelung_einäugig);
                            default -> null;
                        }).filter(Objects::nonNull)
                        .collect(Collectors.toList()));
            }
        }
        return returnValue;
    }

    public static RequirementsAttribute retrieveRequirementAttribute(Map<String, String> preconditionMap, int levels, int currentLevel, SpecialAbilityCategoryKey sack, boolean isUseSamePrecondition, String name) {
        RequirementsAttribute returnValue = null;

        String requirementsString = ExtractorRequirements.extractRequirementsStringForLevel(preconditionMap, levels, currentLevel);
        final String leiteigenschaftText = retrieveLeAttributeShort(sack);

        Matcher m = PAT_EXTRACT_ATTRIBUTES.matcher(requirementsString);
        if (m.find()) {

            m = PAT_EXTRACT_ATTRIBUTES.matcher(requirementsString);
            returnValue = new RequirementsAttribute();
            while (m.find()) {
                String attributeText = m.group();
                returnValue.logicalOpperator = LogicalOperatorKey.and;
                if (attributeText.contains("oder")) {
                    RequirementsAttribute ras = new RequirementsAttribute();
                    ras.logicalOpperator = LogicalOperatorKey.or;
                    List<String> attributeTextList = List.of(attributeText.split(" oder "));
                    ras.requirements.addAll(attributeTextList.stream().map(at -> extractAttributeRequirement(at, leiteigenschaftText)).collect(Collectors.toList()));
                    returnValue.childs = ras;
                } else {
                    returnValue.requirements.add(extractAttributeRequirement(attributeText, leiteigenschaftText));
                }
            }
            if (returnValue.childs == null && returnValue.requirements.isEmpty()) returnValue = null;
        }
        return returnValue;
    }

    private static String retrieveLeAttributeShort(SpecialAbilityCategoryKey sack) {
        String nonClericString = (
                sack == SpecialAbilityCategoryKey.mixed //Arkane Schmieden, hier sind ausschliesslich magische SFs
                        || sack == SpecialAbilityCategoryKey.magic || sack == SpecialAbilityCategoryKey.magic_advanced || sack == SpecialAbilityCategoryKey.magic_stile || sack == SpecialAbilityCategoryKey.magic_signs)
                ? "LE_MAGIC"
                : "";
        return (sack == SpecialAbilityCategoryKey.cleric || sack == SpecialAbilityCategoryKey.cleric_advanced || sack == SpecialAbilityCategoryKey.cleric_stile || sack == SpecialAbilityCategoryKey.sermon || sack == SpecialAbilityCategoryKey.vision)
                ? "LE_CLERIC"
                : nonClericString;
    }


    public static Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> retrieveRequirementsSkill(Map<String, String> preconditionMap, int levels, int currentLevel, String name, boolean isUseSamePrecondition) {
        Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> returnValue = new Quartet<>(null, null, null, null);
        String preconditions = ExtractorRequirements.extractRequirementsStringForLevel(preconditionMap, levels, currentLevel);
        String requirementsString = preconditions.trim()
                .replace("Leiteigenschaft der Tradition", "")
                .replace("Leiteigenschaft", "")
                .replace(" mindestens 2 Schicksalspunkte", "")
                .replace("Sozialer Stand ", "")
                .replace("Zeichen", "Zeichnen")
                .replace("Heilkunde Gifte", "Heilkunde Gift")
                .replace("ARCANOVI 12", "Zauber ARCANOVI 12");
        Matcher m = PAT_EXTRACT_SKILL_REQ.matcher(requirementsString);
        if (m.find()) {
            Quintet<Boolean, RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> handledReqExceptions =
                    handleRequirementSkillExceptions(preconditions, name, currentLevel);

            returnValue = new Quartet<>(handledReqExceptions.getValue1(), handledReqExceptions.getValue2(), handledReqExceptions.getValue3(), handledReqExceptions.getValue4());
            if (Boolean.FALSE.equals(handledReqExceptions.getValue0())) {
                returnValue = handleRequirementSkillRegulars(returnValue, requirementsString, name);

                m = PAT_EXTRACT_MYSTICAL_SKILL_REQ.matcher(requirementsString);
                if (m.find()) {
                    returnValue = handleRequirementSkillMystical(returnValue, m.group().replace("-", ""), name);
                }
            }

        }


        return returnValue;
    }

    private static Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> handleRequirementSkillMystical(Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> returnValue, String requirementString, String name) {
        RequirementMysticalSkill rms = new RequirementMysticalSkill();
        String skillName = requirementString.replaceAll("[0-9]", "")
                .replace("CHIMAE-ROFORM", "CHIMAEROFORM").trim();
        String skillValueText = requirementString.replaceAll("[^0-9]", "");
        int skillValue = skillValueText.isEmpty() ? 0 : Integer.parseInt(skillValueText);
        switch (skillName) {
            case "CHIMAEROFORM" -> rms.key = MysticalSkillKey.ritual_chimaeroform;
            case "ARCANOVI" -> rms.key = MysticalSkillKey.ritual_arcanovi;
            case "PENTAGRAMMA" -> rms.key = MysticalSkillKey.spell_pentagramma;
            case "HEXAGRAMMA" -> rms.key = MysticalSkillKey.spell_hexagramma;
            case "HEPTAGRAMMA" -> rms.key = MysticalSkillKey.spell_heptagramma;
            case "TRAUMGESTALT" -> rms.key = MysticalSkillKey.ritual_traumgestalt;
            case "STEIN WANDLE" -> rms.key = MysticalSkillKey.ritual_stein_wandle;
            case "STAUB WANDLE" -> rms.key = MysticalSkillKey.ritual_staub_wandle;
            case "TOTES HANDLE FW","TOTES HANDLE" -> rms.key = MysticalSkillKey.ritual_totes_handle;
            default ->
                    LOGGER.error("SwitchCase Missing for Requirement (MS):" + name + ": " + skillName + " ->> " + skillValue);
        }
        rms.minValue = skillValue;
        return returnValue.setAt3(rms);

    }

    private static Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> handleRequirementSkillRegulars(Quartet<RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> q, String requirementsString, String name) {
        RequirementsSkill rss = q.getValue0();
        RequirementSkillSum rsss = q.getValue1();
        RequirementsCombatSkill rscs = q.getValue2();
        RequirementMysticalSkill rms = q.getValue3();

        Matcher m = PAT_EXTRACT_SKILL_REQ.matcher(requirementsString
                .replace("Fernkampftechnikwert 10", "")
                .replace("Leiteigenschaft", "")
                .replace("TOTES HAND-LE", "TOTES HANDLE")
                .replace("mindestens 12 Monate in einem Gjalsker Haerad gelebt", "")
        );
        while (m.find()) {
            String skillText = m.group().trim();
            if (!skillText.isEmpty()) {
                Matcher vm = Pattern.compile("(?<=" + skillText + " )" + "\\d\\d?").matcher(requirementsString);
                if (vm.find()) {
                    String valueText = vm.group();
                    Optional<SkillKey> sko = SkillKey.fromString(skillText.replace("oder ", "").trim());
                    Optional<MysticalSkillKey> msko = Optional.empty();
                    Optional<CombatSkillKey> csko = Optional.empty();
                    if (sko.isPresent()) {
                        rss = updateRequirementsSkill(rss, sko.get(), valueText);
                    }

                    if (!sko.isPresent()) {
                        msko = MysticalSkillKey.fromString(skillText);
                        if (msko.isPresent()) {
                            rms = generateRequirementMysticalSkill(msko.get(), valueText);
                        }
                    }

                    if (!sko.isPresent() && !msko.isPresent()) {
                        csko = CombatSkillKey.fromString(skillText);
                        if (csko.isPresent()) {
                            rscs = updateRequirementsCombatSkill(rscs, csko.get(), valueText);
                        }
                    }

                    if (!sko.isPresent() && !msko.isPresent() && !csko.isPresent()) {
                        System.out.println(name + ": " + requirementsString);
                        //throw new IllegalArgumentException("No enum MysticalSkillKey constant with name " + skillText + " for ability " + name);
                    }

                }
            }
        }

        return new Quartet<>(rss, rsss, rscs, rms);
    }

    private static Quintet<Boolean, RequirementsSkill, RequirementSkillSum, RequirementsCombatSkill, RequirementMysticalSkill> handleRequirementSkillExceptions(String preconditions, String name, int currentLevel) {
        RequirementsSkill rss = null;
        RequirementSkillSum rsss = null;
        RequirementsCombatSkill rscs = null;
        RequirementMysticalSkill rms = null;

        boolean isHandled = false;
        if (preconditions.contains("FW von Holzbearbeitung und Metallbearbeitung muss zusammen 12 ergeben")) {
            rsss = new RequirementSkillSum();
            rsss.skillKey.add(SkillKey.holzbearbeitung);
            rsss.skillKey.add(SkillKey.metallbearbeitung);
            rsss.minSum = 12;
            isHandled = true;
        } else if (name.equals("Geländekunde")) {
            isHandled = true;
        } else if (preconditions.contains("je nach verwendeten Material")) {
            rss = updateRequirementsSkill(rss, SkillKey.holzbearbeitung, "4");
            updateRequirementsSkill(rss, SkillKey.lederbearbeitung, "4");
            updateRequirementsSkill(rss, SkillKey.metallbearbeitung, "4");
            updateRequirementsSkill(rss, SkillKey.steinbearbeitung, "4");
            rss.logicalOpperator = LogicalOperatorKey.or;
            isHandled = true;
        } else if (preconditions.contains("je nach verwendeten Rüstungsmaterial")) {
            rss = updateRequirementsSkill(rss, SkillKey.holzbearbeitung, "8");
            updateRequirementsSkill(rss, SkillKey.lederbearbeitung, "8");
            updateRequirementsSkill(rss, SkillKey.metallbearbeitung, "8");
            updateRequirementsSkill(rss, SkillKey.stoffbearbeitung, "8");
            rss.logicalOpperator = LogicalOperatorKey.or;
            isHandled = true;
        } else if (preconditions.contains("je nach verwendeten Waffenmaterial")) {
            rss = updateRequirementsSkill(rss, SkillKey.holzbearbeitung, "8");
            updateRequirementsSkill(rss, SkillKey.metallbearbeitung, "8");
            updateRequirementsSkill(rss, SkillKey.steinbearbeitung, "8");
            rss.logicalOpperator = LogicalOperatorKey.or;
            isHandled = true;
        } else if (preconditions.contains("je nach verwendeter Giftart")) {
            Optional<SkillKey> sko = SkillKey.fromString(name.replace("Giftverstärkung ", ""));
            if (sko.isPresent()) {
                rss = updateRequirementsSkill(rss, sko.get(), "8");
                isHandled = true;
            }
        } else if (preconditions.contains("Sonderfertigkeit Ermutigender Gesang, passender Talentstil")
                || preconditions.contains("Sonderfertigkeit Faszinierender Gesang, passender Talentstil")) {
            rss = updateRequirementsSkill(rss, SkillKey.musizieren, "12");
            updateRequirementsSkill(rss, SkillKey.singen, "12");
            rss.logicalOpperator = LogicalOperatorKey.or;
            isHandled = true;
        } else if (preconditions.contains("alle genannten Talente")) {
            rss = updateRequirementsSkill(rss, SkillKey.körperbeherrschung, "4");
            updateRequirementsSkill(rss, SkillKey.kraftakt, "4");
            isHandled = true;
        } else if (preconditions.contains("Fernkampftechnikwert")) {
            for (CombatSkillKey csk : ExtractorCombatSkillKeys.CSS_ALL_RANGED) {
                rscs = updateRequirementsCombatSkill(rscs, csk, "10");
            }
            if (rscs != null) rscs.logicalOperator = LogicalOperatorKey.or;
        } else if (preconditions.contains("FW des adaptierten Zaubers")
                || preconditions.contains("3 Zauber des Merkmals auf 10")
                || preconditions.contains("3 Liturgien und Zeremonien des Aspekts auf 10")) {
            LOGGER.debug("handle in code");
            isHandled = true;
        } else if (preconditions.contains("je nach Fachbereich")) {
            LOGGER.debug("handled in LoadToSpecialAbility");
            isHandled = true;
        } else if (name.startsWith("Zeremonialgegenstände herstellen")) {
            rms = new RequirementMysticalSkill();
            rms.key = MysticalSkillKey.ceremony_objektweihe;
            rms.minValue = 0;
        } else if (preconditions.contains("Herstellungstalent des Objekts")) {
            rss = updateRequirementsSkill(rss, SkillKey.holzbearbeitung, "10");
            updateRequirementsSkill(rss, SkillKey.lederbearbeitung, "10");
            updateRequirementsSkill(rss, SkillKey.malen_und_zeichnen, "10");
            updateRequirementsSkill(rss, SkillKey.metallbearbeitung, "10");
            updateRequirementsSkill(rss, SkillKey.steinbearbeitung, "10");
            updateRequirementsSkill(rss, SkillKey.stoffbearbeitung, "10");
            rss.logicalOpperator = LogicalOperatorKey.or;
            isHandled = true;
        } else if (preconditions.contains("Handwerkstalent des jeweiligen Artefaktmaterials")) {
            rss = updateRequirementsSkill(rss, SkillKey.holzbearbeitung, "10");
            updateRequirementsSkill(rss, SkillKey.lederbearbeitung, "10");
            updateRequirementsSkill(rss, SkillKey.metallbearbeitung, "10");
            updateRequirementsSkill(rss, SkillKey.steinbearbeitung, "10");
            updateRequirementsSkill(rss, SkillKey.stoffbearbeitung, "10");
            rss.logicalOpperator = LogicalOperatorKey.or;
            isHandled = true;
        } else if (preconditions.contains("passende INVOCATIO-Zauber FW ")
                || preconditions.contains("passender INVOCATIO-Zauber FW ")) {
            isHandled = true;
        }

        return new Quintet<>(
                isHandled,
                rss,
                rsss,
                rscs,
                rms
        );
    }

    private static RequirementsCombatSkill updateRequirementsCombatSkill(RequirementsCombatSkill rscs, CombatSkillKey combatSkillKey, String minValue) {
        if (rscs == null) {
            rscs = new RequirementsCombatSkill();
        }
        RequirementCombatSkill rcs = new RequirementCombatSkill();
        rcs.key = combatSkillKey;
        rcs.minValue = Integer.valueOf(minValue);

        rscs.requirements.add(rcs);
        return rscs;
    }

    private static RequirementMysticalSkill generateRequirementMysticalSkill(MysticalSkillKey mysticalSkillKey, String minValue) {
        RequirementMysticalSkill rms = new RequirementMysticalSkill();
        rms.key = mysticalSkillKey;
        rms.minValue = Integer.valueOf(minValue);
        return rms;
    }

    private static RequirementsSkill updateRequirementsSkill(RequirementsSkill rss, SkillKey skillKey, String minValue) {
        RequirementSkill rs = new RequirementSkill();
        rs.skillKey = skillKey;
        rs.minValue = Integer.valueOf(minValue);
        if (rss == null) {
            rss = new RequirementsSkill();
            rss.logicalOpperator = LogicalOperatorKey.and;
        }
        rss.requirements.add(rs);
        return rss;
    }

    public static Map<String, String> generatePreconditionMap(String precondition) {
        String cleanedPrec = precondition
                .replace("Stufe II/III/IV/V: Sonderfertigkeit Exzellenter Entschwörer Stufe I/ II/II/IV", "")
                .replace("Präziser Schuss I", "Präziser Schuss/Wurf I")
                .replace("Präziser Schuss II", "Präziser Schuss/Wurf II");
        return ExtractorRequirements.extractLevelRequirementMap(cleanedPrec);
    }

    private static RequirementAttribute extractAttributeRequirement(String attributeText, String leiteigenschaftString) {
        RequirementAttribute returnValue = new RequirementAttribute();
        String t = attributeText
                .replace("Leiteigenschaft der Tradition", leiteigenschaftString)
                .replace("Leiteigenschaft", leiteigenschaftString)
                .replaceAll("\\d", "").trim();
        returnValue.attribute = AttributeShort.valueOf(t);
        returnValue.minValue = Integer.valueOf(attributeText.replaceAll("[MUKLINCHFGEO]{2}|Leiteigenschaft( der Tradition)?", "").trim());
        return returnValue;
    }

    public static Triplet<Boolean, Boolean, Boolean> retrieveAllowedWeapons(String combatSkillsText) {
        boolean isParryOnly = combatSkillsText.startsWith("alle Parierwaffen");
        boolean isElfWeaponOnly = combatSkillsText.startsWith("alle Elfenwaffen") || combatSkillsText.startsWith("alle (solange Elfenwaffen)");
        boolean isDwarfWeaponOnly = combatSkillsText.startsWith("alle (solange Zwergenwaffen)");
        return new Triplet<>(isParryOnly, isElfWeaponOnly, isDwarfWeaponOnly);
    }

    public static RequirementsSpecialAbility retrieveRequirementsAbility(Map<String, String> preconditionMap, String name, int levels, int currentLevel, boolean isUseSamePrecondition) {
        RequirementsSpecialAbility returnValue = null;
        String requirementsString = ExtractorRequirements.extractRequirementsStringForLevel(preconditionMap, levels, currentLevel).replace("\u00AD", "-");

        Matcher m = Pattern.compile("[A-ü &/()-]{3,}(?=$|,|(<br>))").matcher(requirementsString);
        while (m.find()) {
            String text = m.group().trim();

            Pair<Boolean, RequirementsSpecialAbility> p = handleRequirementAbilitiesExceptions(text, name);
            if (!p.getValue0() && !checkInvalidSpecialAbilityTexts(text.trim())) {
                List<String> reqTexts = List.of(text.split(" und | oder "));
                returnValue = returnValue == null ? new RequirementsSpecialAbility() : returnValue;
                returnValue.logicalOpperator = LogicalOperatorKey.and;
                List<RequirementSpecialAbility> reqSa = reqTexts.stream().map(reqText -> {
                            SpecialAbilityKey sak = extractSpecialAbilityKeyFromText(reqText
                                    .replace("Sonderfertigkeiten", "")
                                    .replace("Sonderfertigkeit", "")
                                    .replace("Reversalis I des gleichen Zaubers", "Reversalis I")
                                    .replace("Kampftechnik keine", "")
                                    .replaceAll("Kernschuss$", "Kernschuss I")
                                    .replaceAll("Präziser Schuss II$", "Präziser Schuss/Wurf II")
                                    .replace("Finte I/II/III (je nach Antäuschen-Stufe)", "Finte I")
                                    .replaceAll("Wirbelangriff$", "Wirbelangriff I")
                                    .replace("Ottagalder I", "Blutmagie (Ottagalder I)")
                                    .replace("Blutmagie (Ottagalder I)I", "Blutmagie (Ottagalder II)")
                                    .trim());
                            return generateRequirementSpecialAbility(sak, null);
                        })
                        .filter(sa -> sa.abilityKey != null)
                        .collect(Collectors.toList());
                if (text.contains(" oder ")) {
                    if (reqSa != null && reqSa.size() > 0) {
                        RequirementsSpecialAbility childs = new RequirementsSpecialAbility();
                        childs.requirements = reqSa;
                        childs.logicalOpperator = LogicalOperatorKey.or;
                        returnValue.childs = childs;
                    }
                } else {
                    returnValue.requirements.addAll(reqSa);
                }
            }
            if (p.getValue0()) {
                returnValue = p.getValue1();
            }
        }

        return (returnValue != null && (returnValue.childs != null || returnValue.requirements != null && returnValue.requirements.size() > 0))
                ? returnValue
                : null;
    }

    private static Pair<Boolean, RequirementsSpecialAbility> handleRequirementAbilitiesExceptions(String text, String name) {
        RequirementsSpecialAbility reqs = null;
        Boolean isHandled = Boolean.FALSE;
        if (text.contains("Geländekunde")) {
            isHandled = Boolean.TRUE;
            reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
            reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.geländekunde, text.replace("Geländekunde", "").trim()));
        } else if (text.contains("Ortskenntnis")) {
            isHandled = Boolean.TRUE;
            reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
            reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.ortskenntnis, text.replace("Geländekunde", "").trim()));
        } else if (name.equals("Nachladespezialist Armbrüste")) {
            isHandled = Boolean.TRUE;
            reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
            reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_armbrüste, null));
        } else if (name.equals("Nachladespezialist Blasrohre")) {
            isHandled = Boolean.TRUE;
            reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
            reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_blasrohre, null));
        } else if (name.equals("Nachladespezialist Bögen")) {
            isHandled = Boolean.TRUE;
            reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
            reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_bögen, null));
        } else if (name.equals("Nachladespezialist Diskus")) {
            isHandled = Boolean.TRUE;
            reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
            reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_diskus, null));
        } else if (name.equals("Nachladespezialist Schleudern")) {
            isHandled = Boolean.TRUE;
            reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
            reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_schleudern, null));
        } else if (name.equals("Nachladespezialist Wurfwaffen")) {
            isHandled = Boolean.TRUE;
            reqs = reqs == null ? new RequirementsSpecialAbility() : reqs;
            reqs.requirements.add(generateRequirementSpecialAbility(SpecialAbilityKey.schnellladen_wurfwaffen, null));
        }
        return new Pair<>(isHandled, reqs);
    }

    private static RequirementSpecialAbility generateRequirementSpecialAbility(SpecialAbilityKey saKey, String variantText) {
        RequirementSpecialAbility returnValue = new RequirementSpecialAbility();
        returnValue.abilityKey = saKey;
        returnValue.variant = variantText;
        return returnValue;
    }

    private static boolean checkInvalidSpecialAbilityTexts(String text) {
        return text.contains("Vorteil")
                || text.contains("Nachteil")
                || text.contains("Tradition")
                || text.contains("Schrift")
                || text.contains("Sprache")
                || text.contains("Kampfstil")
                || text.contains("Zauberstil")
                || text.contains("Schicksalspunkte")
                || text.contains("Einweisung durch eine Dornrose")
                || text.equals("keine")
                || text.equals("Objektweihe")
                || text.equals("Geweihter")
                // COPIES from sout not matching the regEx. Has to be changed, if regEx changes:
                || text.equals("der ihn bei der Ausübung der Talente behindert")
                || text.equals("Mitglied einer Meschpoche")
                || text.equals("an dem die Ware verkauft werden soll")
                || text.startsWith("Eingeschränkter Sinn ")
                || text.equals("die beigebrachte Fähigkeit muss der Lehrer selbst beherrschen")
                || text.equals("Monate lang an diesem Ort gelebt oder Weg dutzendfach bereist haben")
                || text.equals("Held muss mindestens eine Schrift und die gesprochene Sprache beherrschen")
                || text.startsWith("für")
                || text.startsWith("Kampftechnik ")
                || text.startsWith("Voraussetzungen für ")
                || text.equals("nur gegen GK mittel und klein")
                || text.startsWith("Spezie")
                || text.startsWith("passende")
                || text.equals("Elfen")
                || text.startsWith("Zauber")
                || text.startsWith("oder Steinbearbeitung")
                || text.startsWith("oder höher")
                || text.contains("Daimonidenkonstrukteur")
                || text.contains("Lieblings")
                || text.contains("Gildenmagier")
                || text.contains("Hexen)")
                || text.contains("Kenntnisse des jeweiligen Zaubers")
                || text.contains("Geoden")
                || text.contains("Goblinzauberinnen")
                || text.contains("Zibilja)")
                || text.isEmpty();
    }


    public static List<ValueChange> retrieveValueChanges(String rules, SpecialAbilityKey saKey) {
        List<ValueChange> returnValue = new ArrayList<>();
        ValueChange qsValueChange = generateQsValueChanges(rules, saKey);
        if (qsValueChange != null && qsValueChange.isValid()) returnValue.add(qsValueChange);
        if (qsValueChange != null && !qsValueChange.isValid())
            LOGGER.error("VALUECHANGES (QS) invalid for: " + saKey.name() + " -> " + rules);

        if (qsValueChange == null) {
            ValueChange fpValueChangeSkill = generateFpValueChanges(rules, saKey);
            if (fpValueChangeSkill != null && fpValueChangeSkill.isValid()) returnValue.add(fpValueChangeSkill);

            ValueChange fpValueChangeFeature = generateFeatureValueChanges(rules, saKey);
            if (fpValueChangeFeature != null && fpValueChangeFeature.isValid()) returnValue.add(fpValueChangeFeature);

            boolean fpvcNone = fpValueChangeSkill == null && fpValueChangeFeature == null;
            boolean fpvcSkillIsValid = fpValueChangeSkill != null && fpValueChangeSkill.isValid();
            boolean fpvcFeatuIsValid = fpValueChangeFeature != null && fpValueChangeFeature.isValid();
            //if (!fpvcNone && !fpvcSkillIsValid && !fpvcFeatuIsValid) LOGGER.error("VALUECHANGES (FP) invalid for: " + saKey.name() + " -> " + rules);
        }


        // +begabung
        // +CheckParts
        // +Energy
        // traditionChange
        return returnValue;
    }


    private static ValueChange generateQsValueChanges(String rules, SpecialAbilityKey saKey) {
        ValueChange resultValueChange = handleCompleteSpecialValueChangesQs(saKey);
        if (resultValueChange != null && resultValueChange.isValid()) {
            return resultValueChange;
        }
        resultValueChange = handlePlusQsValueChanges(rules);
        handleAdditionalSpecialValueChangesQs(resultValueChange, saKey);
        if (resultValueChange != null && !resultValueChange.isValid())
            System.out.println("VALUECHANGES;qs;" + saKey.name() + ";" + rules);
        return resultValueChange;
    }


    private static ValueChange generateFpValueChanges(String rules, SpecialAbilityKey saKey) {
        ValueChange resultValueChange = handleCompleteSpecialValueChangesFp(saKey);
        if (resultValueChange != null && resultValueChange.isValid()) {
            return resultValueChange;
        }
        resultValueChange = handlePlusFpValueChangesSkill(rules);
        handleAdditionalSpecialValueChangesFpSkill(resultValueChange, saKey);
        return resultValueChange;
    }

    private static ValueChange generateFeatureValueChanges(String rules, SpecialAbilityKey saKey) {
        ValueChange resultValueChange = handleCompleteSpecialValueChangesFeature(saKey);
        if (resultValueChange != null && resultValueChange.isValid()) {
            return resultValueChange;
        }
        resultValueChange = handlePlusFpValueChangesFeature(rules);
        handleAdditionalSpecialValueChangesFpMysticalSkill(resultValueChange, saKey);
        return resultValueChange;
    }


    private static ValueChange handlePlusQsValueChanges(String rules) {
        ValueChange returnValue = null;
        Matcher m = PAT_VC_QS_PLUS.matcher(rules);

        if (m.find()) {
            returnValue = new ValueChange();
            returnValue.key = ValueChangeKey.skill;
            returnValue.type = ValueChangeType.qs;
            returnValue.valueChange = 1;

            returnValue.usageKeys = new ArrayList<>();
            Matcher mSkillUsages = Pattern.compile("(?<=\\()[A-ü<>\\/ ,&]*(?=\\))").matcher(rules);
            while (mSkillUsages.find()) {
                String skillUsagesString = mSkillUsages.group()
                        .replaceAll("<i>", "")
                        .replaceAll("<\\/i>", "")
                        .replace("oder nach Meisterentscheid ein anderes Talent im Zusammenhang mit Kunst oder Erschaffung von Kunst", "");
                String[] skillUsages = skillUsagesString.split(",| und | oder ");
                returnValue.usageKeys.addAll(List.of(skillUsages).stream()
                        .filter(sus -> !sus.isEmpty())
                        .map(sus -> {
                            try {
                                return SkillUsageKey.fromString(sus
                                        .replace("Heben & Stemmen", "Stemmen & Heben")
                                        .replace("Bedrohung standhalten", "Bedrohungen standhalten")
                                        .replace("Provozieren", "Provokation")
                                        .trim());
                            } catch (IllegalArgumentException e) {
                                //System.out.println(rules+";"+sus);
                            }
                            return null;
                        }).collect(Collectors.toList()));
            }
        }

        return returnValue;
    }


    private static ValueChange handlePlusFpValueChangesSkill(String rules) {
        ValueChange returnValue = null;
        Matcher m = PAT_VC_FP_PLUS.matcher(rules);

        if (m.find()) {
            returnValue = new ValueChange();
            returnValue.key = ValueChangeKey.skill;
            returnValue.type = ValueChangeType.fp;
            returnValue.valueChange = Integer.valueOf(m.group().trim());

            returnValue.usageKeys = new ArrayList<>();
            Matcher mSkillUsages = Pattern.compile("(?<=\\()[A-ü<>\\/ ,&]*(?=\\))").matcher(
                    rules.replaceAll("Tradition \\([A-ü]*\\)", "") // Traditionen werden sonst als Skill erkannt
            );
            while (mSkillUsages.find()) {
                String skillUsagesString = mSkillUsages.group()
                        .replaceAll("<i>", "")
                        .replaceAll("<\\/i>", "")
                        .replace("oder nach Meisterentscheid ein anderes Talent im Zusammenhang mit Kunst oder Erschaffung von Kunst", "");
                String[] skillUsages = skillUsagesString.split(",| und | oder ");
                returnValue.usageKeys.addAll(List.of(skillUsages).stream()
                        .filter(sus -> !sus.isEmpty())
                        .map(sus -> {
                            try {
                                return SkillUsageKey.fromString(sus
                                        .replace("Kostümieren", "Kostümierung")
                                        .trim());
                            } catch (IllegalArgumentException e) {
                                //System.out.println(sus + ";" + rules);
                                LOGGER.error("(" + sus + ") ist kein gültiger SkillUsageKey. Regeln: (" + rules + ")");
                            }
                            return null;
                        }).collect(Collectors.toList()));
            }
        }

        return returnValue;
    }

    private static ValueChange handlePlusFpValueChangesFeature(String rules) {
        ValueChange returnValue = null;
        Matcher m = PAT_VC_FP_PLUS.matcher(rules);

        if (m.find()) {
            returnValue = new ValueChange();
            returnValue.key = ValueChangeKey.skill;
            returnValue.type = ValueChangeType.fp;
            returnValue.valueChange = Integer.valueOf(m.group().trim());


            returnValue.usageKeys = new ArrayList<>();
            Matcher mSkillUsages = Pattern.compile("(?<=Merkmals <i>)[A-ü]*(?=<\\/i>)").matcher(rules);
            while (mSkillUsages.find()) {
                String featureString = mSkillUsages.group();
                returnValue.featureKey = MysticalSkillFeature.fromString(featureString);
            }
        }

        return returnValue;
    }

    private static ValueChange handleCompleteSpecialValueChangesQs(SpecialAbilityKey saKey) {
        if (saKey == null) return null;
        ValueChange result = new ValueChange();
        result.key = ValueChangeKey.skill;
        result.valueChange = 1;
        switch (saKey) {
            case holzkenntnis:
                result.type = ValueChangeType.qs;
                result.usageKeys = List.of(
                        SkillUsageKey.schlagen_und_schneiden, SkillUsageKey.tischlerarbeiten, SkillUsageKey.zimmermannsarbeiten,//Holzbearbeitung
                        SkillUsageKey.giftpflanzen, SkillUsageKey.heilpflanzen, SkillUsageKey.nutzpflanzen//Pflanzenkunde
                );
                result.usageRestrictionKey = UsageRestrictionKey.baum;
                return result;
            case stechender_blick:
                result.type = ValueChangeType.qs;
                result.usageKeys = List.of(SkillUsageKey.drohung, SkillUsageKey.folter, SkillUsageKey.provokation, SkillUsageKey.verhör);
                result.usageRestrictionKey = UsageRestrictionKey.einzelne_person;
                return result;
            case erfolgreicher_sammler:
                result.type = ValueChangeType.qs;
                result.usageKeys = List.of(SkillUsageKey.nutzpflanzen);
                result.usageRestrictionKey = UsageRestrictionKey.nahrungsbeschaffung;
                return result;
            case kunstbegabt:
                result.type = ValueChangeType.qs;
                result.usageKeys = List.of(
                        SkillUsageKey.malen, SkillUsageKey.ritzen, SkillUsageKey.zeichnen, // malen
                        SkillUsageKey.blasinstrumente, SkillUsageKey.saiteninstrumente, SkillUsageKey.trommeln, // musizieren
                        SkillUsageKey.bardenballade, SkillUsageKey.choral, SkillUsageKey.chorgesang, SkillUsageKey.sprechgesang, // singen
                        SkillUsageKey.dorftanz, SkillUsageKey.exotischer_tanz, SkillUsageKey.hoftanz, SkillUsageKey.kulttanz // tanzen
                );
                return result;
            case fachwissen:
            case hauptsegnung_i:
            case hauptsegnung_ii:
            case hauptsegnung_iii:
            case meistertrick_i:
            case meistertrick_ii:
            case meistertrick_iii:
                result.type = ValueChangeType.qs;
                result.useParentSelection = true;
                return result;
            default:
                return null;
        }
    }

    private static ValueChange handleCompleteSpecialValueChangesFp(SpecialAbilityKey saKey) {
        if (saKey == null) return null;
        ValueChange result = new ValueChange();
        result.key = ValueChangeKey.skill;
        switch (saKey) {
            case vertrauenswürdig:
                result.type = ValueChangeType.fp;
                result.usageKeys = List.of(
                        SkillUsageKey.diskussionsführung, SkillUsageKey.einzelgespräch, SkillUsageKey.öffentliche_rede,//Bekehren & Überzeugen
                        SkillUsageKey.anbändeln, SkillUsageKey.aufhübschen, SkillUsageKey.liebeskünste,//Betören
                        SkillUsageKey.benehmen, SkillUsageKey.klatsch_und_tratsch, SkillUsageKey.leichte_unterhaltung, SkillUsageKey.mode,//Etikette
                        SkillUsageKey.beschatten, SkillUsageKey.informationssuche, SkillUsageKey.ortseinschätzung,//Gassenwissen
                        SkillUsageKey.bühnenschauspiel, SkillUsageKey.kostümierung, SkillUsageKey.personen_imitieren,//Verkleiden
                        SkillUsageKey.aufschwatzen, SkillUsageKey.betteln, SkillUsageKey.herausreden, SkillUsageKey.manipulieren, SkillUsageKey.schmeicheln//Überreden
                );
                result.valueChange = 1;
                result.usageRestrictionKey = UsageRestrictionKey.probenart_vergleich;
                return result;
            default:
                return null;
        }
    }

    private static ValueChange handleCompleteSpecialValueChangesFeature(SpecialAbilityKey saKey) {
        if (saKey == null) return null;
        ValueChange result = new ValueChange();
        result.key = ValueChangeKey.skill;
        result.type = ValueChangeType.fp;

        switch (saKey) {

            case salonlöwinnen:
                result.mysticalSkillKeys = List.of(
                        MysticalSkillKey.spell_bannbaladin, MysticalSkillKey.spell_levthans_feuer, MysticalSkillKey.spell_seidenzunge
                );
                result.valueChange = 1;
                break;

            default:
                return null;
        }
        return result;
    }

    private static ValueChange handleCompleteSpecialValueChangesTradition(SpecialAbilityKey saKey) {
        if (saKey == null) return null;
        ValueChange result = new ValueChange();
        result.key = ValueChangeKey.skill;
        result.type = ValueChangeType.traditionChangeForFeature;
        switch (saKey) {
            case quacksalber:
                result.featureKey = MysticalSkillFeature.healing;
                result.traditionKey = TraditionKey.scharlatane;
                return result;
            default:
                return null;
        }
    }


    private static void handleAdditionalSpecialValueChangesQs(ValueChange returnValue, SpecialAbilityKey saKey) {
        if (saKey != null && returnValue != null) {

            switch (saKey) {
                case geisterfreundin:
                    returnValue.targetCategory = TargetCategory.GHOST;
                    break;
            }
        }
    }


    private static void handleAdditionalSpecialValueChangesFpSkill(ValueChange returnValue, SpecialAbilityKey saKey) {
        if (saKey != null && returnValue != null) {

            switch (saKey) {
                case weg_des_schmieds:
                    returnValue.usageKeys.addAll(List.of(
                            SkillUsageKey.feinschmiedearbeiten, SkillUsageKey.grobschmiedearbeiten, SkillUsageKey.metallguss, SkillUsageKey.verhütten, SkillUsageKey.waffenherstellung // Metallbearbeitung
                    ));
                    break;
                case weg_des_taschendiebes:
                    returnValue.usageKeys.addAll(List.of(
                            SkillUsageKey.ablenkungen, SkillUsageKey.person_bestehlen, SkillUsageKey.gegenstand_entwenden, SkillUsageKey.zustecken// Taschendiebstahl
                    ));
                    break;
                case bedrohliche_aura:
                    returnValue.usageKeys.addAll(List.of(
                            SkillUsageKey.drohung, SkillUsageKey.folter, SkillUsageKey.provokation, SkillUsageKey.verhör// Einschüchtern
                    ));
                    break;
                case handwerkskunst:
                case kind_der_natur:
                case körperliches_geschick:
                case soziale_kompetenz:
                case universalgenie:
                case lieblingszauber:
                case lieblingsliturgie:
                    returnValue.useParentSelection = true;
                    break;
                case transporteur:
                    returnValue.usageRestrictionKey = UsageRestrictionKey.transport;
                    returnValue.usageKeys.addAll(List.of(
                            SkillUsageKey.kampfmanöver, SkillUsageKey.langstrecke, SkillUsageKey.verfolgungsjagden, SkillUsageKey.wettfahren, // Boote & Schiffe, Fahrzeuge, Fliegen
                            SkillUsageKey.drücken_und_verbiegen, SkillUsageKey.eintreten_und_zertrümmern, SkillUsageKey.stemmen_und_heben, SkillUsageKey.ziehen_und_zerren // Kraftakt
                    ));
                    break;

                case scholar_der_akademie_der_magischen_rüstung_zu_gareth:
                    returnValue.mysticalSkillKeys = List.of(
                            MysticalSkillKey.spell_ablativum, MysticalSkillKey.spell_armatrutz, MysticalSkillKey.spell_daemonenschild, MysticalSkillKey.spell_gardianum, MysticalSkillKey.spell_schimmernder_schild,
                            MysticalSkillKey.spell_fortifex
                    );
                    break;
                case scholar_der_halle_der_antimagie_zu_kuslik:
                    returnValue.mysticalSkillKeys = List.of(
                            MysticalSkillKey.spell_daemonenbann,
                            MysticalSkillKey.spell_einflussbann,
                            MysticalSkillKey.spell_elementarbann,
                            MysticalSkillKey.spell_heilungsbann,
                            MysticalSkillKey.spell_hellsichtbann,
                            MysticalSkillKey.spell_illusionsbann,
                            MysticalSkillKey.spell_objektbann,
                            MysticalSkillKey.spell_sphaerenbann,
                            MysticalSkillKey.spell_telekinesebann,
                            MysticalSkillKey.spell_temporalbann,
                            MysticalSkillKey.spell_verwandlungsbann
                    );
                    break;
                case austreibung:
                case scholar_der_schule_der_austreibung_zu_perricum:
                    returnValue.mysticalSkillKeys = List.of(
                            MysticalSkillKey.spell_heptagramma,
                            MysticalSkillKey.spell_hexagramma,
                            MysticalSkillKey.spell_oktagramma,
                            MysticalSkillKey.spell_pentagramma
                    );
                    break;
                case scholar_der_akademie_des_magischen_wissens_zu_methumis:
                case scholar_der_akademie_der_hohen_magie_zu_punin:
                    returnValue.usageKeys = List.of(
                            SkillUsageKey.artefakte, SkillUsageKey.magische_wesen, SkillUsageKey.rituale, SkillUsageKey.zaubersprüche// Magiekunde
                    );
                    break;
                case scholar_des_hesindius_lichtblick:
                    returnValue.mysticalSkillModificationType = MysticalSkillModification.zauberdauer_erhöhen;
                    break;
                case scholar_des_demirion_ophenos:
                case küstenwächter:
                case erzwungene_liturgie:
                    returnValue.mysticalSkillModificationType = MysticalSkillModification.erzwingen;
                    break;
                case quacksalber:
                    returnValue.usageKeys = List.of(
                            SkillUsageKey.alchimistische_gifte, SkillUsageKey.elixiere, SkillUsageKey.profane_alchimie// Aclhimie
                    );
                    break;
                case graue_katzen:
                    returnValue.mysticalSkillKeys = List.of(
                            MysticalSkillKey.spell_blindheit, MysticalSkillKey.spell_chamaelioni, MysticalSkillKey.spell_harmlose_gestalt, MysticalSkillKey.spell_ignorantia, MysticalSkillKey.spell_spurlos, MysticalSkillKey.spell_visibili
                    );
                    break;
                case waldhexen:
                    returnValue.usageRestrictionKey = UsageRestrictionKey.im_wald;
                    break;
                case wildkatzen:
                    returnValue.mysticalSkillKeys = List.of(
                            MysticalSkillKey.spell_aeolito, MysticalSkillKey.spell_aquafaxius, MysticalSkillKey.spell_aquasphaero, MysticalSkillKey.spell_archofaxius, MysticalSkillKey.spell_archosphaero,
                            MysticalSkillKey.spell_blitzball,
                            MysticalSkillKey.spell_corpofrigo,
                            MysticalSkillKey.spell_eulenruf,
                            MysticalSkillKey.spell_fledermausruf, MysticalSkillKey.spell_frigifaxius, MysticalSkillKey.spell_frigisphaero, MysticalSkillKey.spell_fulminictus,
                            MysticalSkillKey.spell_hexengalle, MysticalSkillKey.spell_hexenkrallen, MysticalSkillKey.spell_humofaxius, MysticalSkillKey.spell_humosphaero,
                            MysticalSkillKey.spell_ignifaxius, MysticalSkillKey.spell_ignisphaero, MysticalSkillKey.spell_incendio, MysticalSkillKey.spell_invinculo,
                            MysticalSkillKey.spell_katzenruf, MysticalSkillKey.spell_kraehenruf, MysticalSkillKey.spell_kulminatio,
                            MysticalSkillKey.spell_orcanofaxius, MysticalSkillKey.spell_orcanosphaero,
                            MysticalSkillKey.spell_pandaemonium, MysticalSkillKey.spell_radau,
                            MysticalSkillKey.spell_zorn_der_elemente,
                            MysticalSkillKey.ritual_custodosigil
                    );
                    break;
                case kristallmeister:
                    returnValue.usageRestrictionKey = UsageRestrictionKey.kristall_verwendet;
                    break;
                case matrixzauberei:
                case apricarier:
                case hausgänse:
                case seher_der_seele:
                case pastori:
                case waldläufer:
                case anhänger_des_roten_gottes:
                case hüter_der_esse:
                    returnValue.usageRestrictionKey = UsageRestrictionKey.unmodifiziert;
                    break;
                case freidenker:
                    returnValue.usageRestrictionKey = UsageRestrictionKey.modifiziert;
                    break;
                case kristallkraft:
                    returnValue.usageRestrictionKey = UsageRestrictionKey.kristall_geopfert;
                    break;
                case meereswächter:
                    returnValue.usageRestrictionKey = UsageRestrictionKey.fischfang_oder_seefahrt;
                case propheten_des_todes:
                    returnValue.targetCategory = TargetCategory.UNDEAD;
            }
        }
    }

    private static void handleAdditionalSpecialValueChangesFpMysticalSkill(ValueChange returnValue, SpecialAbilityKey saKey) {
        if (saKey != null && returnValue != null) {

            switch (saKey) {
            }
        }
    }
}
