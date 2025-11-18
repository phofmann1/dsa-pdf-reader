package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import de.pho.dsapdfreader.exporter.model.enums.Publication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.SpecialAbilityRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsSpecialAbility;
import de.pho.dsapdfreader.exporter.model.enums.SpecialAbilityCategoryKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterSpecialAbilityKodex extends DsaConverter<SpecialAbilityRaw, ConverterAtomicFlagsSpecialAbility> {

    private static final String KEY_RULES_I = "Regel";
    private static final String KEY_RULES_II = "Wirkung";
    private static final String KEY_PRECONDITIONS = "Voraussetzungen";
    private static final String KEY_PRECONDITIONS_II = "Voraussetzung";
    private static final String KEY_PRECONDITIONS_III = "Voraussetzungen:";
    private static final String KEY_AP_VALUE = "AP-Wert";
    private static final String KEY_ADVANCED_ABILITIES_I = "Erweiterte Kampfsonderfertigkeiten";
    private static final String KEY_ADVANCED_ABILITIES_II = "Erweiterte Talentsonderfertigkeiten";
    private static final String KEY_ADVANCED_ABILITIES_III = "Erweiterte Zaubersonderfertigkeiten";
    private static final String KEY_ADVANCED_ABILITIES_IV = "Erweiterte Zauberstilsonderfertigkeiten";
    private static final String KEY_ADVANCED_ABILITIES_V = "Erweiterte Liturgiesonderfertigkeiten";
    private static final String KEY_COMBAT_SKILLS = "Kampftechniken";
    private static final String KEY_VERBREITUNG = "Verbreitung";
    private static final String KEY_KREIS_DER_VERDAMMNIS = "Kreis";


    private static final String KEY_DIFFICULTY = "Erschwernis";
    protected static final String[] KEYS = {
            KEY_RULES_I,
            KEY_RULES_II,
            KEY_PRECONDITIONS,
            KEY_PRECONDITIONS_II,
            KEY_PRECONDITIONS_III,
            KEY_AP_VALUE,
            KEY_ADVANCED_ABILITIES_I,
            KEY_ADVANCED_ABILITIES_II,
            KEY_ADVANCED_ABILITIES_III,
            KEY_ADVANCED_ABILITIES_IV,
            KEY_ADVANCED_ABILITIES_V,
            KEY_DIFFICULTY,
            KEY_COMBAT_SKILLS,
            KEY_VERBREITUNG,
            KEY_KREIS_DER_VERDAMMNIS
    };
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<String> HIDDEN_TOPIC_HEADLINES = List.of(
        "Prügel-Sonderfertigkeiten",
        "Vision-Sonderfertigkeiten",
        "Predigt-Sonderfertigkeiten"
    );

    ConverterAtomicFlagsSpecialAbility flags;

    public List<SpecialAbilityRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf) {
        List<SpecialAbilityRaw> returnValue = new ArrayList<>();
        String msg = String.format("parse  result to %s", getClassName());
        LOGGER.debug(msg);

        AtomicReference<SpecialAbilityCategoryKey> abilityCategory = new AtomicReference<>();
        if(conf.publication.equals("Archiv_der_Ausrüstung")) abilityCategory.set(SpecialAbilityCategoryKey.common);
        AtomicReference<String> abilityName = new AtomicReference<>("");
        resultList
                .forEach(t -> {

                    Optional<String> hiddenTopic = findHiddenTopic(t, conf); //Paktgeschenke, Kampftrinker und Prügel SF

                    String cleanText = t.text
                            .replace("Leggaleg", "Leg-ga-leg")
                            .trim();

                    if (hiddenTopic.isPresent()) {
                        cleanText = cleanText.replace(hiddenTopic.get(), "");
                    }

                    boolean isTopic = t.size == 1800 || hiddenTopic.isPresent();
                    // validate the flags for conf
                    boolean isFirstValue = validateIsFirstValue(t, conf) && !abilityName.get().equals(cleanText) && !cleanText.isEmpty();
                    boolean isFirstValueSkipped = isFirstValue && isNumeric(t.text); // gets skipped, when the firstValue is a number (Page Number in some documents)
                    boolean isDataKey = validateIsDataKey(t, cleanText, conf);
                    boolean isDataValue = validateIsDataValue(t, cleanText, conf);
                    boolean isIgnore = isFirstValue && cleanText.endsWith("Strömungen"); // Strömungs Vorwort in Kodex des Götterwirkens
                    handleWasNoKeyStrings(getFlags(), t); // used in MysticalSkill for QS flags, they act differently, because they are also part of the effect


                    if (isTopic)
                        abilityCategory.set(extractTopic((hiddenTopic.isPresent() ? hiddenTopic.get() : t.text), conf));


                    if (abilityCategory.get() != null) {
                        if (!isIgnore) {
                            finishPredecessorAndStartNew(isFirstValue, isFirstValueSkipped, returnValue, conf, cleanText);
                            if (isFirstValue) {
                                abilityName.set(cleanText);
                                last(returnValue).abilityCategory = abilityCategory.get();
                                last(returnValue).name = abilityName.get();
                            }
                            // handle keys
                            if (isDataKey) {
                                applyFlagsForKey(t.text);
                            }

                            // handle values
                            if (isDataValue) {
                                applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
                                applySpecialAbilitiesFlagsForNoKeyStrings(getFlags(), t);
                            }
                            getFlags().getFirstFlag().set(isFirstValue && !isFirstValueSkipped);
                        }
                    }
                });
        concludePredecessor(last(returnValue)); //finish the last entry in list
        return returnValue;
    }

    private Optional<String> findHiddenTopic(TextWithMetaInfo t, TopicConfiguration conf) {
        Optional<String> topic = Optional.empty();

        if (conf.nameSize == t.size) {
            if (HIDDEN_TOPIC_HEADLINES.contains(t.text)) {
                topic = Optional.of(t.text);
            } else {
                List<String> res = List.of(t.text.replaceAll("([a-z])([A-Z])", "$1|$2").split("\\|"));
                if (res.size() > 1) {
                    topic = Optional.of(res.get(0));
                }

            }

        }
        return topic;
    }

    private void applySpecialAbilitiesFlagsForNoKeyStrings(ConverterAtomicFlagsSpecialAbility flags, TextWithMetaInfo t) {
        if (flags.wasName.get() && !t.isBold) {
            flags.wasName.set(false);
            flags.wasDescription.set(true);
        }
    }

    private SpecialAbilityCategoryKey extractTopic(String text, TopicConfiguration conf) {
        if(text.equals("Neue Sonderfertigkeiten") && conf.publication.equals(Publication.Archiv_der_Dämonen.name())) { //Hier muss ein Split abhängig von der config gemacht werden.
            return SpecialAbilityCategoryKey.magic;
        }
        return switch (text) {
            case "Allgemeine Sonderfertigkeiten", "Allgemeine Sonderfertigkeit", "Neue Sonderfertigkeiten", "Neue SonderfertigkeitenFallen-Regeln",
                 "Neue Sonderfertigkeiten für Kulturschaffende" -> SpecialAbilityCategoryKey.common;
            case "Schicksalspunkte-Sonderfertigkeiten" -> SpecialAbilityCategoryKey.fate;
            case "Talentstilsonderfertigkeiten" -> SpecialAbilityCategoryKey.skill_stile;
            case "Erweiterte Talentsonderfertigkeiten" -> SpecialAbilityCategoryKey.skill_advanced;
            case "Kampfsonderfertigkeiten" -> SpecialAbilityCategoryKey.combat;
            case "Kampfstilsonderfertigkeiten" -> SpecialAbilityCategoryKey.combat_stile;
            case "Kampfstile fremder Völker" -> SpecialAbilityCategoryKey.combat_stile_alien;
            case "Waffenlose Kampfstile" -> SpecialAbilityCategoryKey.combat_unarmed_stile;
            case "Erweiterte Kampfsonderfertigkeiten" -> SpecialAbilityCategoryKey.combat_advanced;
            case "Befehlssonderfertigkeiten" -> SpecialAbilityCategoryKey.order;
            case "Neue magische Sonderfertigkeiten","Allgemeine magische Sonderfertigkeiten", "Sonderfertigkeiten für Chimärologen" -> SpecialAbilityCategoryKey.magic;
            case "Erweiterte Zauberstilsonderfertigkeiten", "Erweiterte Zaubersonderfertigkeiten" ->
                    SpecialAbilityCategoryKey.magic_advanced;
            case "Prügel-Sonderfertigkeiten" -> SpecialAbilityCategoryKey.brawl;
            case "ZauberstilsonderfertigkeitenGildenmagische Zauberstilsonderfertigkeiten (Akademie)",
                 "Gildenmagische Zauberstilsonderfertigkeiten (Lehrmeister)",
                 "Gildenmagische Zauberstilsonderfertigkeiten (Qabalya)",
                 "Druidische Zauberstilsonderfertigkeiten",
                 "Elfische ZauberstilsonderfertigkeitenGeodische Zauberstilsonderfertigkeiten",
                 "Goblinische Zauberstilsonderfertigkeiten",
                 "Hexische Zauberstilsonderfertigkeiten",
                 "Kristallomantische Zauberstilsonderfertigkeiten",
                 "Scharlatanische Zauberstilsonderfertigkeiten",
                 "Zauberstilsonderfertigkeiten" -> SpecialAbilityCategoryKey.magic_stile;
            case "Allgemeine karmale Sonderfertigkeiten" -> SpecialAbilityCategoryKey.cleric;
            case "Praios-Strömungen",
                 "Rondra-Strömungen",
                 "Efferd-Strömungen",
                 "Efferd-StrömungenRondra-Strömungen",
                 "Travia-Strömungen",
                 "Boron-Strömungen",
                 "Hesinde-Strömungen",
                 "Firun-Strömungen",
                 "Tsa-Strömungen",
                 "Phex-Strömungen",
                 "Tsa-StrömungenPhex-Strömungen",
                 "Peraine-Strömungen",
                 "Ingerimm-Strömungen",
                 "Rahja-Strömungen",
                 "Aves-Strömungen",
                 "Ifirn-Strömungen",
                 "Kor-Strömungen",
                 "Kor-StrömungenIfirn-Strömungen",
                 "Nandus-Strömungen",
                 "Swafnir-Strömungen",
                 "Levthan-Strömungen",
                 "Marbo-Strömungen",
                 "Schamanen-Strömungen",
                 "Angrosch-Strömungen",
                 "Namenlose Strömungen",
                 "Schamanen- Strömungen",
                 "Liturgiestilsonderfertigkeiten" -> SpecialAbilityCategoryKey.cleric_stile;
            case "Erweiterte Liturgiesonderfertigkeiten" -> SpecialAbilityCategoryKey.cleric_advanced;
            case "Predigt-Sonderfertigkeiten" -> SpecialAbilityCategoryKey.sermon;
            case "Vision-Sonderfertigkeiten" -> SpecialAbilityCategoryKey.vision;
            case "Sikaryan-Raub-Sonderfertigkeiten" -> SpecialAbilityCategoryKey.sikaryan_deprivation;
            case "Vampirische Gaben" -> SpecialAbilityCategoryKey.vampire;
            case "Lykanthropische Gaben" -> SpecialAbilityCategoryKey.werebeeing;
            case "Allgemeine Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_common;
            case "Blakharaz-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_blakharaz;
            case "Belhalhar-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_belhalhar;
            case "Charyptoroth-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_charyptoroth;
            case "Lolgramoth-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_lolgramoth;
            case "Thargunitoth-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_thargunitoth;
            case "Amazeroth-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_amazeroth;
            case "Nagrach-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_nagrach;
            case "Asfaloth-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_asfaloth;
            case "Tasfarelel-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_tasfarelel;
            case "Mishkara-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_mishkara;
            case "Agrimoth-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_agrimoth;
            case "Belkelel-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_belkelel;
            case "Aphasmayra-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_aphasmayra;
            case "Aphestadil-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_aphestadil;
            case "Heskatet-Paktgeschenke" -> SpecialAbilityCategoryKey.pact_demonic_heskatet;
            case "Elementarpakte" -> SpecialAbilityCategoryKey.pact_elemental;
            case "Feenpakte" -> SpecialAbilityCategoryKey.pact_fairy;
            case "Die Tricks der Vertrauten" -> SpecialAbilityCategoryKey.familiar;
            case "Zauberzeichen" -> SpecialAbilityCategoryKey.magic_signs;
            case "Sonderfertigkeiten" -> SpecialAbilityCategoryKey.mixed;

            default -> null;
        };
    }


    @Override
    protected String[] getKeys() {
        return KEYS;
    }

    @Override
    protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
        return Arrays.stream(this.getKeys()).anyMatch(k -> k.equalsIgnoreCase(t.text));
    }

    @Override
    protected ConverterAtomicFlagsSpecialAbility getFlags() {
        if (flags == null) {
            this.flags = new ConverterAtomicFlagsSpecialAbility();
        }
        return flags;
    }

    @Override
    protected String getClassName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    protected void handleFirstValue(List<SpecialAbilityRaw> returnValue, TopicConfiguration conf, String cleanText) {

        if (!this.getFlags().getFirstFlag().get()) {
            SpecialAbilityRaw newEntry = new SpecialAbilityRaw();
            this.getFlags().initDataFlags();
            newEntry.setTopic(conf.topic);
            newEntry.setPublication(conf.publication);
            returnValue.add(newEntry);
        }

        this.getFlags().wasName.set(true);
    }

    @Override
    protected void applyFlagsForKey(String key) {
        this.getFlags().wasName.set(false);
        this.getFlags().wasDescription.set(false);
        this.getFlags().wasRules.set(
                key.trim().equals(KEY_RULES_I)
                        || key.trim().equals(KEY_RULES_II)
        );
        this.getFlags().wasPrecondition.set(key.trim().equals(KEY_PRECONDITIONS) || key.trim().equals(KEY_PRECONDITIONS_II)|| key.trim().equals(KEY_PRECONDITIONS_III));
        this.getFlags().wasApValue.set(key.trim().equals(KEY_AP_VALUE));
        this.getFlags().wasAdvancedCombatAbility.set(
                key.trim().equals(KEY_ADVANCED_ABILITIES_I)
                        || key.trim().equals(KEY_ADVANCED_ABILITIES_II)
                        || key.trim().equals(KEY_ADVANCED_ABILITIES_III)
                        || key.trim().equals(KEY_ADVANCED_ABILITIES_IV)
                        || key.trim().equals(KEY_ADVANCED_ABILITIES_V));
        this.getFlags().wasDifficulty.set(key.trim().equals(KEY_DIFFICULTY));
        this.getFlags().wasCombatSkills.set(key.trim().equals(KEY_COMBAT_SKILLS));
        this.getFlags().wasVerbreitung.set(key.trim().equals(KEY_VERBREITUNG));
        this.getFlags().wasKreisDerVerdammnis.set(key.trim().equals(KEY_KREIS_DER_VERDAMMNIS));
    }

    @Override
    protected void applyDataValue(SpecialAbilityRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic) {
        if (currentDataObject != null) {
            if (this.getFlags().wasName.get() && isBold) {
                currentDataObject.name = concatForDataValue(currentDataObject.name, cleanText);
            } else if ((this.getFlags().wasName.get() || this.getFlags().wasDescription.get()))
                currentDataObject.description = concatForDataValueWithMarkup(currentDataObject.description, cleanText, isBold, isItalic);

            //WITH MARKUP ist bei den Regeln notwendig um später Anwendungsgebiete oder ähnliches herauszufinden:
            if (this.getFlags().wasRules.get())
                currentDataObject.rules = concatForDataValueWithMarkup(currentDataObject.rules, cleanText, isBold, isItalic);

            if (this.getFlags().wasPrecondition.get())
                currentDataObject.preconditions = concatForDataValue(currentDataObject.preconditions, cleanText);
            if (this.getFlags().wasApValue.get())
                currentDataObject.ap = concatForDataValue(currentDataObject.ap, cleanText);
            if (this.getFlags().wasAdvancedCombatAbility.get())
                currentDataObject.advancedAbilities = concatForDataValue(currentDataObject.advancedAbilities, cleanText);
            if (this.getFlags().wasDifficulty.get())
                currentDataObject.difficulty = concatForDataValue(currentDataObject.difficulty, cleanText);
            if (this.getFlags().wasCombatSkills.get())
                currentDataObject.combatSkills = concatForDataValue(currentDataObject.combatSkills, cleanText);
            if (this.getFlags().wasVerbreitung.get())
                currentDataObject.verbreitung = concatForDataValue(currentDataObject.verbreitung, cleanText);
            if (this.getFlags().wasKreisDerVerdammnis.get())
                currentDataObject.kreisDerVerdammnis = concatForDataValue(currentDataObject.kreisDerVerdammnis, cleanText);
        }
    }

    @Override
    protected void concludePredecessor(SpecialAbilityRaw lastEntry) {
        if (lastEntry != null && (lastEntry.rules == null || lastEntry.rules.isEmpty())) {
            lastEntry.rules = lastEntry.description;
            lastEntry.description = null;
        }
    }
}
