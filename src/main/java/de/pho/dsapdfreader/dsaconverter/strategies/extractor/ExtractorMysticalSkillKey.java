package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillCategory;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillKey;
import de.pho.dsapdfreader.exporter.model.enums.MysticalSkillVariantKey;

public class ExtractorMysticalSkillKey extends Extractor {
  public static MysticalSkillKey retrieveMysticalSkillKey(String publication, String name, MysticalSkillCategory category) {
    MysticalSkillKey returnValue = null;
    try {
      returnValue = extractMysticalSkillKeyFromText(category, name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e) {
      String msg = String.format("%s key for (%s - %s) could not be interpreted.", getPrefix(publication, name), category, name);
      //LOGGER.error(msg);
    }
    return returnValue;
  }

  public static MysticalSkillVariantKey extractMysticalSkillVariantKeyFromText(MysticalSkillKey msk, String variantText) {
    MysticalSkillVariantKey returnValue;
    if (msk == null) {
      return null;
    }
    String skillKeyString = msk
        + "_"
        + extractKeyTextFromText(variantText).toLowerCase();
    skillKeyString = skillKeyString.trim();

    try {
      returnValue = MysticalSkillVariantKey.valueOf(skillKeyString);
    }
    catch (IllegalArgumentException e) {
      System.out.println(skillKeyString+',');
      //LOGGER.error("Invalid MysticalSkillVariantKey: {}", skillKeyString, e);
      returnValue = null;
    }
    return returnValue;
  }

  public static String extractKeyTextFromText(String txt) {
    return txt == null ? "" : (txt.toUpperCase()
        .replace("Ä", "AE")
        .replace("Ö", "OE")
        .replace("Ü", "UE")
        .replace("ß", "SS")
        .replace("&", "UND")
        .replace("!", "")
        .replace("(", "")
        .replace(")", "")
        .replace("[", "")
        .replace("]", "")
        .replace("/", " ")
        .replace("?", "")
        .replace("’", " ")
        .replace(",", " ")
        .replace(" ..", "")
        .replace(".", "")
        .replaceAll("\s+", " ")
        .replace("-", "_")
    ).trim()
        .replace(" ", "_")
        .replace("__", "_");
  }


  public static MysticalSkillKey extractMysticalSkillKeyFromText(MysticalSkillCategory category, String name) {
    MysticalSkillKey returnValue;
    String skillKeyString = (extractKeyTextFromText(category.name())
        + "_"
        + extractKeyTextFromText(name.replace("Achtung (Exkommunikation)", "Ächtung (Exkommunikation)"))).replaceAll("_+", "_").toLowerCase();
    try {
      returnValue = MysticalSkillKey.valueOf(skillKeyString);
    }
    catch (IllegalArgumentException e) {
      returnValue = null;
    }
    return returnValue;
  }

  public static MysticalSkillKey extractMysticalSkillKeyFromText(String name, boolean isMagical) {
    MysticalSkillKey returnValue = null;
    String enumEnd = extractKeyTextFromText(name
        .trim()
        .replaceAll("(?<!(Attributo |Aufnahme ))\\(.*?\\)", "")
        .replaceAll("Analys$", "Analys Arkanstruktur")
        .replaceAll("Balsam$", "Balsam Salabunde")
        .replaceAll("Blitz$", "Blitz dich find")
        .replaceAll("Grußwort$", "Grußworte")
        .replaceAll("Odem$", "Odem Arcanum")
        .replace("Entgiftungsrune", "entgiftungsrune_eidurbanruna")
        .replace("Würzen", "Würze")
        .replace("Felsenrune", "felsenrune_bjoergruna")
        .replace("Feuerschutztrune", "feuerschutzrune_eldurvernruna")
        .replace("Finsterrune", "finsterrune_warteruna")
        .replace("Friedensrune", "friedensrune_fjoeterlundruna")
        .replace("Furchtrune", "furchtrune_vargruna")
        .replace("Lebensrune", "lebensrune_livruna")
        .replace("Nebelbannrune", "nebelbannrune_thokebanruna")
        .replace("Orkanstimmenrune", "orkanstimmenrune_hringjavindruna")
        .replace("Ottarune", "ottarune_ottaruna")
        .replace("Pfeilrune", "pfeilrune_boltruna")
        .replace("Rauschrune", "rauschrune_hugibaniruna")
        .replace("Rüstrune", "ruestrune_verndunruna")
        .replace("Salzwasserrune", "salzwasserrune_sjoevannruna")
        .replace("Schicksalsrune", "schicksalsrune_wyrdruna")
        .replace("Schutzrune vor Stürmen", "schutzrune_vor_stuermen_vagakoruna")
        .replace("Schutzrune vor Alfen", "schutzrune_vor_alfen_alfibanruna")
        .replace("Schutzrune vor Daimonide und Chimären", "schutzrune_vor_daimonide_und_chimaeren_skepnabanruna")
        .replace("Schutzrune vor Dämonen", "schutzrune_vor_daemonen_vondurbanruna")
        .replace("Schutzrune vor Elementare", "schutzrune_vor_elementare_verabanruna")
        .replace("Schutzrune vor Geister", "schutzrune_vor_geister_vandrendabanruna")
        .replace("Schutzrune vor Hranngargezücht", "schutzrune_vor_hranngargezuecht_fylgjaruna")
        .replace("Schutzrune vor Untote", "schutzrune_vor_untote_draugerbanruna")
        .replace("Schutzrune vor Zauberei", "schutzrune_vor_zauberei_galderbanruna")
        .replace("Stärkerune", "staerkerune_styrkurruna")
        .replace("Waffenrune", "waffenrune_aescruna")
    ).replaceAll("_+", "_")
        .trim()
        .toLowerCase();
    try {
      List<MysticalSkillKey> possibleResults = Arrays.stream(MysticalSkillKey.values()).filter(msk -> isMagical && (
          msk.name().equals("curse_" + enumEnd) ||
              msk.name().equals("dance_" + enumEnd) ||
              msk.name().equals("elfensong_" + enumEnd) ||
              msk.name().equals("geode_" + enumEnd) ||
              msk.name().equals("jest_" + enumEnd) ||
              msk.name().equals("melody_" + enumEnd) ||
              msk.name().equals("power_" + enumEnd) ||
              msk.name().equals("ritual_" + enumEnd) ||
              msk.name().equals("ritualofdominion_" + enumEnd) ||
              msk.name().equals("spell_" + enumEnd) ||
              msk.name().equals("trick_" + enumEnd) ||
              msk.name().equals("zibilja_" + enumEnd) ||
              msk.name().equals("goblinritual_" + enumEnd) ||
              msk.name().equals("bansign_" + enumEnd) ||
              msk.name().equals("rune_" + enumEnd)
      ) || !isMagical && (
          msk.name().equals("blessing_" + enumEnd) ||
              msk.name().equals("ceremony_" + enumEnd) ||
              msk.name().equals("liturgy_" + enumEnd)
      )).collect(Collectors.toList());
      if (possibleResults.size() == 1)
        returnValue = possibleResults.get(0);
      else if (possibleResults.size() > 1) {
        LOGGER.error("TOOO many results for MysticalSkillName (" + name + ")");
        possibleResults.forEach(r -> LOGGER.debug(r.name()));
      }
    }
    catch (IllegalArgumentException e) {
      returnValue = null;
      //LOGGER.error("Invalid MysticalSkillKey: " + enumEnd);
    }
    return returnValue;
  }


  public static boolean isMysticalSkillKey(String skillName, boolean isMagical) {
    try {
      MysticalSkillKey result = extractMysticalSkillKeyFromText(skillName, isMagical);
      return result != null;
    }
    catch (IllegalArgumentException e) {
      return false;
    }
  }
}
