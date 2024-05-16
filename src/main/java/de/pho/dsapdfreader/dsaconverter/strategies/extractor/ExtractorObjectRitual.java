package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.pho.dsapdfreader.exporter.model.enums.ArtifactKey;
import de.pho.dsapdfreader.exporter.model.enums.ObjectRitualKey;

public class ExtractorObjectRitual extends Extractor {
  public static ObjectRitualKey extractOrKeyFromName(String name) throws IllegalArgumentException {
    ObjectRitualKey returnValue = null;
    try {
      returnValue = extractOrKeyFromText(name);
      if (returnValue == null)
        throw new IllegalArgumentException();
    }
    catch (IllegalArgumentException e) {
    }
    return returnValue;
  }

  private static ObjectRitualKey extractOrKeyFromText(String name) throws IllegalArgumentException {
    ObjectRitualKey returnValue = null;
    AtomicReference<String> enumEnd = new AtomicReference<>(extractKeyTextFromTextWithUmlauts(name.replace("ß", "xxx"))
        .toLowerCase()
        .replace("xxx", "ß")
        .trim());
    if (!enumEnd.get().isEmpty()) {
      List<ObjectRitualKey> possibleResults = Arrays.stream(ObjectRitualKey.values()).filter(ork -> ork.name().equals(enumEnd.get()) ||
          (Arrays.stream(ArtifactKey.values()).anyMatch(ak -> ork.name().equals(mapArtifactKey2Prefix(ak) + enumEnd.get()))
          )).collect(Collectors.toList());

      if (possibleResults.size() == 1)
        returnValue = possibleResults.get(0);
      else if (possibleResults.size() == 0) {

      }
      else if (possibleResults.size() > 1) {
        LOGGER.error("TOOO many results for MysticalSkillName (" + name + ")");
        possibleResults.forEach(r -> LOGGER.debug("\t" + r.name()));
      }
    }
    return returnValue;
  }


  public static List<ObjectRitualKey> retrieveRequirementsObjectRitual(Map<String, String> preconditionMap, int levels, int currentLevel, ObjectRitualKey currentOrk, ArtifactKey artifactKey) {
    List<ObjectRitualKey> returnValue = new ArrayList<>();
    String requirementsString = ExtractorRequirements.extractRequirementsStringForLevel(preconditionMap, levels, currentLevel).replace("\u00AD", "-");

    Matcher m = Pattern.compile("[A-ü &/()-]{3,}(?=$|,|(<br>))").matcher(requirementsString);
    while (m.find()) {
      String text = m.group().replace(" für ", "")
          .replace("Sonderfertigkeit", "").trim();
      if (isNoValidObjectRitualKey(text)) {
        text = text.contains("Merkmalsfokus") ? "Merkmalsfokus" : text;

        String orKeyString = mapArtifactKey2Prefix(artifactKey) + extractKeyTextFromTextWithUmlauts(text
            .replace("ß", "xxx")
        )
            .toLowerCase()
            .replace("xxx", "ss");
        try {
          ObjectRitualKey ork = ExtractorObjectRitual.extractOrKeyFromName(orKeyString);
          if (ork != null && !text.trim().startsWith("keine ")) returnValue.add(ork);

        }
        catch (IllegalArgumentException e) {
          LOGGER.error("INVALID Requirement for ObjectRitual (" + text + ")");
        }
      }
    }

    if (currentLevel > 0) {
      returnValue.add(ObjectRitualKey.values()[currentOrk.ordinal() - 1]);
    }
    return returnValue;
  }

  public static List<ObjectRitualKey> retrieveRequirementsNoneObjectRitual(Map<String, String> preconditionMap, int levels, int currentLevel, ObjectRitualKey currentOrk, ArtifactKey artifactKey) {
    List<ObjectRitualKey> returnValue = new ArrayList<>();
    String requirementsString = ExtractorRequirements.extractRequirementsStringForLevel(preconditionMap, levels, currentLevel).replace("\u00AD", "-");

    Matcher m = Pattern.compile("(?<=keine )[A-ü &/()-]{3,}(?=$|,|(<br>))").matcher(requirementsString);
    while (m.find()) {
      String text = m.group().replace(" für ", "")
          .replace("Sonderfertigkeit", "").trim();
      if (isNoValidObjectRitualKey(text)) {
        text = text.contains("Merkmalsfokus") ? "Merkmalsfokus" : text;

        String orKeyString = mapArtifactKey2Prefix(artifactKey) + extractKeyTextFromTextWithUmlauts(text
            .replace("ß", "xxx")
        )
            .toLowerCase()
            .replace("xxx", "ss");
        try {
          ObjectRitualKey ork = ExtractorObjectRitual.extractOrKeyFromName(orKeyString);
          if (ork != null && !text.trim().startsWith("keine ")) returnValue.add(ork);

        }
        catch (IllegalArgumentException e) {
          LOGGER.error("INVALID Requirement for ObjectRitual (" + text + ")");
        }
      }
    }

    if (currentLevel > 0) {
      returnValue.add(ObjectRitualKey.values()[currentOrk.ordinal() - 1]);
    }
    return returnValue;
  }

  private static boolean isNoValidObjectRitualKey(String text) {
    return !text.contains("keine")
        && !text.contains("bei höheren Stufen")
        && !text.contains("Stufe niedrigere Stufe der SF")
        && !text.contains("Merkmal ")
        && !text.contains("Merkmalskenntnis")
        && !text.contains("Schalenverzauberung")
        && !text.contains("Punkte Volumenerweiterung")
        && !text.isEmpty();
  }

  public static String mapArtifactKey2Prefix(ArtifactKey ak) {
    return switch (ak) {
      case alchimistenschale -> "alc_";
      case angroschanhänger -> "ang_";
      case animistenwaffe -> "ani_";
      case avesstab -> "ave_";
      case bannschwert -> "ban_";
      case buch_der_schlange -> "buc_";
      case druidendolch -> "dru_";
      case druidensichel -> "drs_";
      case efferdbart -> "eff_";
      case einhornstirnband -> "ein_";
      case ferkinaknochenkeule -> "fer_";
      case firunsmesser -> "fir_";
      case fjarningerknochenkeule -> "fja_";
      case gänsebeutel -> "gän_";
      case gildenmagische_magierkugel -> "gil_";
      case gjalskerknochenkeule -> "gja_";
      case grüne_handschuhe -> "grü_";
      case hexenkessel -> "hex_";
      case ifirnsmantel -> "ifi_";
      case ingerimmshammer -> "ing_";
      case korspieß -> "kor_";
      case lebensring -> "leb_";
      case magierstab -> "mag_";
      case marbodolch -> "mar_";
      case tahayaknochenkeule -> "tah_";
      case mondamulett -> "mon_";
      case narrenkappe -> "nar_";
      case nivesenknochenkeule -> "niv_";
      case opferdolch_des_namenlosen -> "opf_";
      case prisma -> "pri_";
      case rabenschnabel -> "rab_";
      case rondrakamm -> "ron_";
      case roter_schleier -> "rot_";
      case scharlatanische_zauberkugel -> "scz_";
      case schelmenspielzeug -> "sch_";
      case sippenchronik -> "sip_";
      case sonnenzepter -> "son_";
      case trollzackerknochenkeule -> "tro_";
      case walschild -> "wal_";
      case widderkeule -> "wid_";
      case zauberkleidung -> "zau_";
      case krallenkette -> "krk_";
      case seeleninstrument -> "sei_";
      case zauberschale -> "zsc_";
      case goblinkeule -> "gke_";
      case schweinetrommel -> "sct_";
      case hauerkette -> "hak_";
      case fluggerät -> "flg_";
      case schuppenbeutel -> "scb_";
      case trinkhorn -> "tkh_";
      case kristallomantische_kristallkugel -> "kkk_";
      case echsenhaube -> "eha_";
      case schutzkugel -> "scz_";
      case schwertfibel -> "scw_";
      case muschelkette -> "mus_";
      case amulett_des_heiligen_badilak -> "ahb_";
      case das_dunkle_buch -> "dub_";
      case erkenntnisstab -> "erk_";
      case firunsbogen -> "fib_";
      case das_bunte_gewand -> "bug_";
      case der_graue_umhang -> "gra_";
      case saatgutbeutel -> "saa_";
      case laterne_des_ewigen_feuers -> "lat_";
      case schmuck_der_schönen_götting -> "scm_";
      case silberflöte -> "sil_";
      case ifirnsbogen -> "ifb_";
      case mantikorkette -> "man_";
      case trigon_amulett -> "tri_";
      case flukenamulett -> "flu_";
      case füllhorn -> "fül_";
      case stundenglas -> "stu_";
      case muschelhorn -> "muh_";
      case bimssteinkette -> "bim_";
      case vitis -> "vit_";
      case hornissenstachel -> "hor_";
      case geisterfetisch -> "gei_";
      case angroschhammer -> "anh_";
      case eidechsenkleidung -> "eid_";
      case regenbogenstein -> "reg_";
      case schlangenstab -> "scl_";
      case schlangenlederarmband -> "sla_";
      case sturmschwinge -> "sts_";
      case coelestin_metallband -> "coe_";
      case achazknochenkeule -> "ach_";
      case tairachknochenkeule -> "tai_";
      case graveshhammer -> "grh_";
      case kupferroter_schleifstein -> "kup_";
      case geischtslarve -> "ges_";
      case zauberinstrument -> "zai_";
      case zauberstecken -> "zst_";
    };
  }
}
