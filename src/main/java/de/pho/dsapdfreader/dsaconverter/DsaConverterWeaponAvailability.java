package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorCultureKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorRegionKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorSpecieKey;
import de.pho.dsapdfreader.dsaconverter.strategies.extractor.ExtractorWeaponKey;
import de.pho.dsapdfreader.exporter.model.AvailabilityWeapon;
import de.pho.dsapdfreader.exporter.model.enums.CultureKey;
import de.pho.dsapdfreader.exporter.model.enums.ProfessionKey;
import de.pho.dsapdfreader.exporter.model.enums.RegionKey;
import de.pho.dsapdfreader.exporter.model.enums.SpecieKey;
import de.pho.dsapdfreader.exporter.model.enums.TraditionKey;
import de.pho.dsapdfreader.exporter.model.enums.WeaponKey;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterWeaponAvailability {
  public static List<AvailabilityWeapon> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf) {
    List<AvailabilityWeapon> returnValue = new ArrayList<>();

    StringBuilder rb = new StringBuilder();
    AtomicReference<WeaponKey> currentWeapon = new AtomicReference<>();
    resultList
        .forEach(t -> {

              String cleanText = t.text
                  .replace("NahkampfwaffenWaffeRegion", "")
                  .replace("WaffenRegion", "")
                  .replace("Fernkampfwaffen", "")
                  .replace("WaffenRegion", "")
                  .replace("Anhang", "")
                  .replace("(fast)", "")
                  .replace("Nipauka-Axt", "Geisteraxt (Nipakau Nigoma)")
                  .replace("Orkkriegshammer (Gruufhai)", "Orkhammer (Gruufhai)")
                  .replace("Opferdolch des Namenlosen (Basiliskenzunge)", "Opferdolch des Namenlosen")
                  .replace("Tulamidisches Kurzschwert (Scimshar)", "Scimshar (Tulamidisches Kurzschwert)")
                  .replace("Wolfsmesser", "Wolfsmesser (Zarzil)")
                  .replace("Zyklopäisches Kurzschwert (Parazonium)", "Parazonium (Zyklopäisches Kurzschwert)")
                  .replace("Balläster", "Balläster (Armbrust)")
                  .replace("Elfenbogen", "Elfenbogen (Yara)")
                  .trim();
              if (cleanText != null && !cleanText.isEmpty()) {
                if (t.isBold) {

                  if (currentWeapon.get() != null) {
                    returnValue.addAll(_handleAvailabilities(rb.toString(), currentWeapon.get()));
                  }
                  if (cleanText.equals("Flöte") || cleanText.equals("Laute") || cleanText.equals("Feuerspeien")) {
                    currentWeapon.set(null);
                  }
                  if (cleanText.equals("Magierstab")) {
                    currentWeapon.set(WeaponKey.magierstab_lang);
                  }
                  else {
                    currentWeapon.set(ExtractorWeaponKey.retrieve(cleanText.replace(" & Lasso", "")));
                  }
                  rb.setLength(0);
                }
                else {
                  rb.append(cleanText);
                }


              }
              else {
                rb.append(cleanText.replace("überall,", "überall"));
              }
            }
        );
    return returnValue;
  }

  private static List<AvailabilityWeapon> _handleAvailabilities(String availabilityText, WeaponKey weaponKey) {
    AvailabilityWeapon nAvailability = new AvailabilityWeapon(weaponKey);
    List<AvailabilityWeapon> returnValue = new ArrayList<>(List.of(nAvailability));

    List<String> availabilities = Arrays.asList(availabilityText.replace("überall, wo", "überall wo").split(",|(?<=[a-z)])(?=[A-Z])|(?<=[a-z)])(?=alle)|(?<=[a-z])(?=überall)")).stream().map(s ->
        s.replace("-", "")
            .replace("NamenloseGeweihte", "Namenlose-Geweihte")
            .replaceAll("Salamandersteine.*", "Salamandersteine")
            .trim()
    ).collect(Collectors.toList());
    Collections.sort(availabilities);

    availabilities.forEach(r -> {
      if (r != null && !r.isEmpty()) {
        boolean isRegion = _handleAvailabilityByRegion(nAvailability, r);
        boolean isSpecie = _handleAvailabilityBySpecie(nAvailability, r);
        boolean isCulture = _handleAvailabilityByCulture(nAvailability, r);
        boolean isTradition = _handleAvailabilityByTradition(nAvailability, r);
        boolean isProfession = _handleAvailabilityForProfession(nAvailability, r);
        _handleAvailabilityForCondition(nAvailability, r);

        if (!isRegion && !isSpecie && !isCulture && !isTradition && !isProfession) {
          System.out.println(weaponKey + " -> " + r);
        }
      }
    });

    if (nAvailability.weaponKey == WeaponKey.magierstab_lang) {
      returnValue.add(new AvailabilityWeapon(WeaponKey.magierstab_mittel, nAvailability));
      returnValue.add(new AvailabilityWeapon(WeaponKey.magierstab_kurz, nAvailability));
    }

    return returnValue;
  }

  private static void _handleAvailabilityForCondition(AvailabilityWeapon nAvailability, String r) {
    if (r.contains("(jeweils bei Barbaren)")) nAvailability.availableByCondition = "bei Barbaren";
    if (r.contains("Amazonen")) nAvailability.availableByCondition = "Amazonen";
  }

  private static boolean _handleAvailabilityForProfession(AvailabilityWeapon nAvailability, String r) {
    if (r.contains("Amazonen")) {
      nAvailability.availableGenerationByProfessions = List.of(
          ProfessionKey.amazone,
          ProfessionKey.bogenschützin,
          ProfessionKey.kundschafterin,
          ProfessionKey.rondrageweihte_amazone);
    }
    else if (r.contains("Gaukler")) {
      nAvailability.availableGenerationByProfessions = List.of(
          ProfessionKey.gauklerin,
          ProfessionKey.akrobat_in,
          ProfessionKey.dompteur_in,
          ProfessionKey.messerwerfer_in,
          ProfessionKey.possenreißer_in,
          ProfessionKey.wahrsager_in);
    }
    else if (r.contains("Sonnenlegion")) {
      nAvailability.availableGenerationByProfessions = List.of(
          ProfessionKey.sonnenlegionärin);

    }
    else if (r.contains("Avesgeweihte")) {
      nAvailability.availableGenerationByProfessions = List.of(ProfessionKey.avesgeweihte);
    }
    else if (r.contains("Efferdgeweihte")) {
      nAvailability.availableGenerationByProfessions = List.of(ProfessionKey.efferdgeweihte);
    }
    else if (r.contains("Firungeweihte")) {
      nAvailability.availableGenerationByProfessions = List.of(ProfessionKey.firungeweihter);
    }
    else if (r.contains("Ingerimmgeweihte")) {
      nAvailability.availableGenerationByProfessions = List.of(ProfessionKey.ingerimmgeweihte);
    }
    else if (r.contains("Korgeweihte")) {
      nAvailability.availableGenerationByProfessions = List.of(ProfessionKey.korgeweihter);
    }
    else if (r.contains("Marbopriester")) {
      nAvailability.availableGenerationByProfessions = List.of(ProfessionKey.marbopriester);
    }
    else if (r.contains("Namenlose")) {
      nAvailability.availableGenerationByProfessions = List.of(ProfessionKey.namenloser_geweihter);
    }
    else if (r.contains("Rondrageweihte")) {
      nAvailability.availableGenerationByProfessions = List.of(ProfessionKey.rondrageweihte_amazone, ProfessionKey.rondrageweihte, ProfessionKey.rondrageweihter_ardarit);
    }
    else if (r.contains("Praiosgeweihte")) {
      nAvailability.availableGenerationByProfessions = List.of(ProfessionKey.praiosgeweihte, ProfessionKey.praiosgeweihter_bannstrahler);
    }
    else if (r.contains("Swafnirgeweihte")) {
      nAvailability.availableGenerationByProfessions = List.of(ProfessionKey.swafnirgeweihte);
    }
    else if (r.contains("Levthanpriester")) {
      nAvailability.availableGenerationByProfessions = List.of(ProfessionKey.levthanpriester);
    }
    else {
      return false;
    }

    return true;
  }

  private static boolean _handleAvailabilityByTradition(AvailabilityWeapon nAvailability, String r) {
    nAvailability.availableByTradition = switch (r) {
      case "alle Regionen mit Magiern", "Gildenmagier" -> TraditionKey.gildenmagier;
      case "Scharlatane" -> TraditionKey.scharlatane;
      default -> null;
    };

    return nAvailability.availableByTradition != null;
  }

  private static boolean _handleAvailabilityByCulture(AvailabilityWeapon nAvailability, String r) {
    if (r.contains("bei Nivesen")) {
      nAvailability.availableByCultures.add(CultureKey.nivesen);
    }
    else if (r.contains("Waldmenschen und Utulu")) {
      nAvailability.availableByCultures.add(CultureKey.waldmenschen);
      nAvailability.availableByCultures.add(CultureKey.utulu);
    }
    else {
      CultureKey cultureKey = ExtractorCultureKey.retrieveOptional(r);
      if (cultureKey != null) {
        nAvailability.availableByCultures = List.of(cultureKey);
      }
      else {
        return false;
      }
    }
    return true;
  }

  private static boolean _handleAvailabilityBySpecie(AvailabilityWeapon nAvailability, String r) {
    SpecieKey key = ExtractorSpecieKey.retrieveOptional(r
        .replace("Zwerge", "Zwerg")
        .replace("Elfen", "Elf")
        .replace("Goblins", "Goblin")
        .replace("Orks", "Ork"));
    if (key != null) {
      nAvailability.availableBySpecie = key;
    }
    else {
      return false;
    }
    return true;
  }

  private static boolean _handleAvailabilityByRegion(AvailabilityWeapon nAvailability, String r) {
    if (r.equals("alle")) {
      nAvailability.availableByRegions = List.of(RegionKey.values());
    }
    else if (r.contains("Küstenregionen")) {
      nAvailability.availableByRegions = List.of(RegionKey.albernia,
          RegionKey.al_anfanisches_imperium,
          RegionKey.aranien,
          RegionKey.bornland,
          RegionKey.hoher_norden,
          RegionKey.horasreich,
          RegionKey.maraskan,
          RegionKey.nostria,
          RegionKey.schattenlande,
          RegionKey.svellttal,
          RegionKey.südmeer_und_waldinseln,
          RegionKey.thorwal,
          RegionKey.tiefer_süden,
          RegionKey.tobrien,
          RegionKey.tulamidenlande,
          RegionKey.weiden,
          RegionKey.windhag,
          RegionKey.zyklopeninseln);
    }
    else if (r.equals("wie Turnierschwert")) {
      nAvailability.availableByRegions = List.of(
          RegionKey.albernia,
          RegionKey.almada,
          RegionKey.garetien,
          RegionKey.kosch,
          RegionKey.nordmarken,
          RegionKey.rommilyser_mark,
          RegionKey.tobrien,
          RegionKey.weiden,
          RegionKey.windhag,
          RegionKey.andergast,
          RegionKey.bornland,
          RegionKey.horasreich,
          RegionKey.nostria,
          RegionKey.schattenlande);
    }
    else {
      RegionKey key = ExtractorRegionKey.retrieveOptional(r.replace("(jeweils bei Barbaren)", "").trim());
      if (key != null) {
        nAvailability.availableByRegions.add(key);
      }
      else {
        return false;
      }
    }
    return true;
  }
}
