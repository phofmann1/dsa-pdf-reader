package de.pho.dsapdfreader.dsaconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.ProfileRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsProfile;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterProfile extends DsaConverter<ProfileRaw, ConverterAtomicFlagsProfile>
{

  public static final String KEY_Verbreitung = "Verbreitung";
  public static final String KEY_Lebensweise = "Lebensweise";
  public static final String KEY_Größe = "Größe";
  public static final String KEY_Gewicht = "Gewicht";
  public static final String KEY_MU = "MU";
  public static final String KEY_KL = "KL";
  public static final String KEY_IN = "IN";
  public static final String KEY_CH = "CH";
  public static final String KEY_FF = "FF";
  public static final String KEY_GE = "GE";
  public static final String KEY_KO = "KO";
  public static final String KEY_KK = "KK";
  public static final String KEY_LeP = "LeP";
  public static final String KEY_AsP = "AsP";
  public static final String KEY_KaP = "KaP";
  public static final String KEY_INI = "INI";
  public static final String KEY_AW = "AW";
  public static final String KEY_SK = "SK";
  public static final String KEY_ZK = "ZK";
  public static final String KEY_GS = "GS";
  public static final String KEY_AT = "AT";
  public static final String KEY_PA = "PA";
  public static final String KEY_TP = "TP";
  public static final String KEY_RW = "RW";
  public static final String KEY_FK = "FK";
  public static final String KEY_LZ = "LZ";
  public static final String KEY_VW = "VW";
  public static final String KEY_RS_BE = "RS/BE";
  public static final String KEY_Aktionen = "Aktionen";
  public static final String KEY_Vorteile_Nachteile = "Vorteile/Nachteile";
  public static final String KEY_Vorteile = "Vorteil";
  public static final String KEY_Sonderfertigkeiten = "Sonderfertigkeiten";
  public static final String KEY_Talente = "Talente";
  public static final String KEY_Anzahl = "Anzahl";
  public static final String KEY_Größenkategorie = "Größenkategorie";
  public static final String KEY_Typus = "Typus";
  public static final String KEY_Beute = "Beute";
  public static final String KEY_Kampfverhalten = "Kampfverhalten";
  public static final String KEY_Flucht = "Flucht";
  public static final String KEY_Schmerz_1_bei = "Schmerz +1 bei";
  public static final String KEY_Sonderregeln = "Sonderregeln";
  public static final String KEY_Erfahren = "Erfahren";
  public static final String KEY_Kompetent = "Kompetent";
  public static final String KEY_QS1 = "QS 1";
  public static final String KEY_QS2 = "QS 2";
  public static final String KEY_QS3 = "QS 3+";
  protected static final String[] KEYS = {
      KEY_Größe,
      KEY_Gewicht,
      KEY_MU,
      KEY_KL,
      KEY_IN,
      KEY_CH,
      KEY_FF,
      KEY_GE,
      KEY_KO,
      KEY_KK,
      KEY_LeP,
      KEY_AsP,
      KEY_KaP,
      KEY_INI,
      KEY_AW,
      KEY_SK,
      KEY_ZK,
      KEY_GS,
      KEY_AT,
      KEY_PA,
      KEY_TP,
      KEY_RW,
      KEY_FK,
      KEY_LZ,
      KEY_VW,
      KEY_RS_BE,
      KEY_Aktionen,
      KEY_Vorteile_Nachteile,
      KEY_Vorteile,
      KEY_Sonderfertigkeiten,
      KEY_Talente,
      KEY_Anzahl,
      KEY_Größenkategorie,
      KEY_Typus,
      KEY_Beute,
      KEY_Kampfverhalten,
      KEY_Flucht,
      KEY_Schmerz_1_bei,
      KEY_Sonderregeln,
      KEY_Erfahren,
      KEY_Kompetent,
      KEY_QS1,
      KEY_QS2,
      KEY_QS3,
          KEY_Verbreitung,
          KEY_Lebensweise
  };
  private static final Logger LOGGER = LogManager.getLogger();
  ConverterAtomicFlagsProfile flags;

  public List<ProfileRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> resultList, TopicConfiguration conf)
  {
    List<ProfileRaw> returnValue = new ArrayList<>();
    String msg = String.format("parse  result to %s", getClassName());
    LOGGER.debug(msg);

    AtomicReference<String> profileName = new AtomicReference<>("");
    resultList
        .forEach(t -> {


          String cleanText = t.text
              .trim();
          // validate the flags for conf
          boolean isFirstValue = validateIsFirstValue(t, conf) && !cleanText.isEmpty();
          boolean isStartOfProfile = cleanText.equals(profileName.get() + KEY_Größe);
          if (isStartOfProfile)
          {
            cleanText = KEY_Größe;
          }
          boolean isDataKey = validateIsDataKey(t, cleanText, conf);
          boolean isDataValue = validateIsDataValue(t, cleanText, conf);

          if (isFirstValue)
          {
            finishPredecessorAndStartNew(isFirstValue, false, returnValue, conf, cleanText);
            profileName.set(cleanText);
          }
          // handle keys
          if (isDataKey)
          {
            applyFlagsForKey(cleanText);
            this.applyValuesLikeKeysToKey(last(returnValue), cleanText);
          }

          // handle values
          if (isDataValue)
          {
            applyDataValue(last(returnValue), cleanText, t.isBold, t.isItalic);
          }

        });

    concludePredecessor(last(returnValue)); //finish the last entry in list
    return returnValue;
  }

  private void applyValuesLikeKeysToKey(ProfileRaw currentDataObject, String cleanText)
  {
    if (this.getFlags().wasAT.get() || this.getFlags().wasFK.get() || this.getFlags().wasWeaponName.get())
    {
      currentDataObject.weaponName = concatForDataValue(currentDataObject.weaponName, cleanText.replace(KEY_AT, "").replace(KEY_FK, "").trim(), "|");
      this.getFlags().wasWeaponName.set(false);
    }
    else if (this.getFlags().wasInformation.get())
    {
      currentDataObject.information = concatForDataValue(currentDataObject.information, cleanText);
    }
  }


  @Override
  protected String[] getKeys()
  {
    return KEYS;
  }

  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf)
  {
    return Arrays.stream(this.getKeys()).anyMatch(k -> k.equalsIgnoreCase(cleanText))
        || cleanText.endsWith(KEY_AT)
        || cleanText.endsWith(KEY_FK)
        || t.isBold && (this.getFlags().wasRW.get() || this.getFlags().wasGS.get())
        || t.isBold && (this.getFlags().wasSchmerz_1_bei.get());
  }

  @Override
  protected ConverterAtomicFlagsProfile getFlags()
  {
    if (flags == null)
    {
      this.flags = new ConverterAtomicFlagsProfile();
    }
    return flags;
  }

  @Override
  protected String getClassName()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<ProfileRaw> returnValue, TopicConfiguration conf, String cleanText)
  {

    if (!this.getFlags().getFirstFlag().get())
    {
      ProfileRaw newEntry = new ProfileRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      newEntry.setName(cleanText);
      returnValue.add(newEntry);
    }

    this.getFlags().wasName.set(true);
  }

  @Override
  protected void applyFlagsForKey(String key)
  {
    boolean couldBeWeaponName = this.getFlags().wasGS.get() || this.getFlags().wasRW.get();
    boolean couldBeInformation = this.getFlags().wasSchmerz_1_bei.get();

    this.getFlags().wasName.set(false);
    this.getFlags().wasDescription.set(false);
    this.getFlags().wasGröße.set(key.equals(KEY_Größe));
    this.getFlags().wasGewicht.set(key.equals(KEY_Gewicht));
    this.getFlags().wasMU.set(key.equals(KEY_MU));
    this.getFlags().wasKL.set(key.equals(KEY_KL));
    this.getFlags().wasIN.set(key.equals(KEY_IN));
    this.getFlags().wasCH.set(key.equals(KEY_CH));
    this.getFlags().wasFF.set(key.equals(KEY_FF));
    this.getFlags().wasGE.set(key.equals(KEY_GE));
    this.getFlags().wasKO.set(key.equals(KEY_KO));
    this.getFlags().wasKK.set(key.equals(KEY_KK));
    this.getFlags().wasLeP.set(key.equals(KEY_LeP));
    this.getFlags().wasAsP.set(key.equals(KEY_AsP));
    this.getFlags().wasKaP.set(key.equals(KEY_KaP));
    this.getFlags().wasINI.set(key.equals(KEY_INI));
    this.getFlags().wasAW.set(key.equals(KEY_AW));
    this.getFlags().wasSK.set(key.equals(KEY_SK));
    this.getFlags().wasZK.set(key.equals(KEY_ZK));
    this.getFlags().wasGS.set(key.equals(KEY_GS));
    this.getFlags().wasAT.set(key.endsWith(KEY_AT));
    this.getFlags().wasPA.set(key.equals(KEY_PA));
    this.getFlags().wasTP.set(key.equals(KEY_TP));
    this.getFlags().wasRW.set(key.equals(KEY_RW));
    this.getFlags().wasFK.set(key.endsWith(KEY_FK));
    this.getFlags().wasLZ.set(key.equals(KEY_LZ));
    this.getFlags().wasVW.set(key.equals(KEY_VW));
    this.getFlags().wasRS_BE.set(key.equals(KEY_RS_BE));
    this.getFlags().wasAktionen.set(key.equals(KEY_Aktionen));
    this.getFlags().wasVorteile_Nachteile.set(key.equals(KEY_Vorteile_Nachteile) || key.equals(KEY_Vorteile));
    this.getFlags().wasSonderfertigkeiten.set(key.equals(KEY_Sonderfertigkeiten));
    this.getFlags().wasTalente.set(key.equals(KEY_Talente));
    this.getFlags().wasAnzahl.set(key.equals(KEY_Anzahl));
    this.getFlags().wasGrößenkategorie.set(key.equals(KEY_Größenkategorie));
    this.getFlags().wasTypus.set(key.equals(KEY_Typus));
    this.getFlags().wasBeute.set(key.equals(KEY_Beute));
    this.getFlags().wasKampfverhalten.set(key.equals(KEY_Kampfverhalten));
    this.getFlags().wasFlucht.set(key.equals(KEY_Flucht));
    this.getFlags().wasSchmerz_1_bei.set(key.equals(KEY_Schmerz_1_bei));
    this.getFlags().wasSonderregeln.set(key.equals(KEY_Sonderregeln));
    this.getFlags().wasErfahren.set(key.equals(KEY_Erfahren));
    this.getFlags().wasKompetent.set(key.equals(KEY_Kompetent));
    this.getFlags().wasQs1.set(key.equals(KEY_QS1));
    this.getFlags().wasQs2.set(key.equals(KEY_QS2));
    this.getFlags().wasQs3.set(key.equals(KEY_QS3));
    this.getFlags().wasVerbreitung.set(key.equals(KEY_Verbreitung));
    this.getFlags().wasLebensweise.set(key.equals(KEY_Lebensweise));
    this.getFlags().wasWeaponName.set(couldBeWeaponName && Arrays.stream(KEYS).noneMatch(k -> k.equals(key)));
    this.getFlags().wasInformation.set(couldBeInformation && Arrays.stream(KEYS).noneMatch(k -> k.equals(key)));
  }

  @Override
  protected void applyDataValue(ProfileRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    if (currentDataObject != null)
    {
      if ((this.getFlags().wasName.get() || this.getFlags().wasDescription.get()))
        currentDataObject.description = concatForDataValueWithMarkup(currentDataObject.description, cleanText, isBold, isItalic);
      if (this.getFlags().wasGröße.get()) currentDataObject.Größe = concatForDataValue(currentDataObject.Größe, cleanText);
      if (this.getFlags().wasGewicht.get()) currentDataObject.Gewicht = concatForDataValue(currentDataObject.Gewicht, cleanText);
      if (this.getFlags().wasMU.get()) currentDataObject.MU = concatForDataValue(currentDataObject.MU, cleanText);
      if (this.getFlags().wasKL.get()) currentDataObject.KL = concatForDataValue(currentDataObject.KL, cleanText);
      if (this.getFlags().wasIN.get()) currentDataObject.IN = concatForDataValue(currentDataObject.IN, cleanText);
      if (this.getFlags().wasCH.get()) currentDataObject.CH = concatForDataValue(currentDataObject.CH, cleanText);
      if (this.getFlags().wasFF.get()) currentDataObject.FF = concatForDataValue(currentDataObject.FF, cleanText);
      if (this.getFlags().wasGE.get()) currentDataObject.GE = concatForDataValue(currentDataObject.GE, cleanText);
      if (this.getFlags().wasKO.get()) currentDataObject.KO = concatForDataValue(currentDataObject.KO, cleanText);
      if (this.getFlags().wasKK.get()) currentDataObject.KK = concatForDataValue(currentDataObject.KK, cleanText);
      if (this.getFlags().wasLeP.get()) currentDataObject.LeP = concatForDataValue(currentDataObject.LeP, cleanText);
      if (this.getFlags().wasAsP.get()) currentDataObject.AsP = concatForDataValue(currentDataObject.AsP, cleanText);
      if (this.getFlags().wasKaP.get()) currentDataObject.KaP = concatForDataValue(currentDataObject.KaP, cleanText);
      if (this.getFlags().wasINI.get()) currentDataObject.INI = concatForDataValue(currentDataObject.INI, cleanText);
      if (this.getFlags().wasAW.get()) currentDataObject.AW = concatForDataValue(currentDataObject.AW, cleanText);
      if (this.getFlags().wasSK.get()) currentDataObject.SK = concatForDataValue(currentDataObject.SK, cleanText);
      if (this.getFlags().wasZK.get()) currentDataObject.ZK = concatForDataValue(currentDataObject.ZK, cleanText);
      if (this.getFlags().wasGS.get()) currentDataObject.GS = concatForDataValue(currentDataObject.GS, cleanText);
      if (this.getFlags().wasAT.get()) currentDataObject.AT = concatForDataValue(currentDataObject.AT, cleanText, "|");
      if (this.getFlags().wasPA.get()) currentDataObject.PA = concatForDataValue(currentDataObject.PA, cleanText, "|");
      if (this.getFlags().wasTP.get()) currentDataObject.TP = concatForDataValue(currentDataObject.TP, cleanText, "|");
      if (this.getFlags().wasRW.get()) currentDataObject.RW = concatForDataValue(currentDataObject.RW, cleanText, "|");
      if (this.getFlags().wasFK.get()) currentDataObject.FK = concatForDataValue(currentDataObject.FK, cleanText, "|");
      if (this.getFlags().wasLZ.get()) currentDataObject.LZ = concatForDataValue(currentDataObject.LZ, cleanText, "|");
      if (this.getFlags().wasVW.get()) currentDataObject.VW = concatForDataValue(currentDataObject.VW, cleanText);
      if (this.getFlags().wasRS_BE.get()) currentDataObject.RS_BE = concatForDataValue(currentDataObject.RS_BE, cleanText);
      if (this.getFlags().wasAktionen.get()) currentDataObject.Aktionen = concatForDataValue(currentDataObject.Aktionen, cleanText);
      if (this.getFlags().wasVorteile_Nachteile.get())
        currentDataObject.Vorteile_Nachteile = concatForDataValue(currentDataObject.Vorteile_Nachteile, cleanText);
      if (this.getFlags().wasSonderfertigkeiten.get())
        currentDataObject.Sonderfertigkeiten = concatForDataValue(currentDataObject.Sonderfertigkeiten, cleanText);
      if (this.getFlags().wasTalente.get()) currentDataObject.Talente = concatForDataValue(currentDataObject.Talente, cleanText);
      if (this.getFlags().wasAnzahl.get()) currentDataObject.Anzahl = concatForDataValue(currentDataObject.Anzahl, cleanText);
      if (this.getFlags().wasGrößenkategorie.get())
        currentDataObject.Größenkategorie = concatForDataValue(currentDataObject.Größenkategorie, cleanText);
      if (this.getFlags().wasTypus.get()) currentDataObject.Typus = concatForDataValue(currentDataObject.Typus, cleanText);
      if (this.getFlags().wasBeute.get()) currentDataObject.Beute = concatForDataValue(currentDataObject.Beute, cleanText);
      if (this.getFlags().wasKampfverhalten.get()) currentDataObject.Kampfverhalten = concatForDataValue(currentDataObject.Kampfverhalten, cleanText);
      if (this.getFlags().wasFlucht.get()) currentDataObject.Flucht = concatForDataValue(currentDataObject.Flucht, cleanText);
      if (this.getFlags().wasSchmerz_1_bei.get()) currentDataObject.Schmerz_1_bei = concatForDataValue(currentDataObject.Schmerz_1_bei, cleanText);
      if (this.getFlags().wasSonderregeln.get()) currentDataObject.Sonderregeln = concatForDataValue(currentDataObject.Sonderregeln, cleanText);
      if (this.getFlags().wasErfahren.get()) currentDataObject.Erfahren = concatForDataValue(currentDataObject.Erfahren, cleanText);
      if (this.getFlags().wasKompetent.get()) currentDataObject.Kompetent = concatForDataValue(currentDataObject.Kompetent, cleanText);
      if (this.getFlags().wasQs1.get()) currentDataObject.qs1 = concatForDataValue(currentDataObject.qs1, cleanText);
      if (this.getFlags().wasQs2.get()) currentDataObject.qs2 = concatForDataValue(currentDataObject.qs2, cleanText);
      if (this.getFlags().wasQs3.get()) currentDataObject.qs3 = concatForDataValue(currentDataObject.qs3, cleanText);
      if (this.getFlags().wasVerbreitung.get()) currentDataObject.verbreitung = concatForDataValue(currentDataObject.verbreitung, cleanText);
      if (this.getFlags().wasLebensweise.get()) currentDataObject.lebensweise = concatForDataValue(currentDataObject.lebensweise, cleanText);

    }
  }

  @Override
  protected void concludePredecessor(ProfileRaw lastEntry)
  {
  }
}
