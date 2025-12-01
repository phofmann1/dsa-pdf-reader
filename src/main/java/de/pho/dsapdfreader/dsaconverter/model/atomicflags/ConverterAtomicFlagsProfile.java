package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsProfile implements ConverterAtomicFlagsI
{
  public AtomicBoolean wasName;
  public AtomicBoolean wasDescription;
  public AtomicBoolean wasGröße;
  public AtomicBoolean wasGewicht;
  public AtomicBoolean wasMU;
  public AtomicBoolean wasKL;
  public AtomicBoolean wasIN;
  public AtomicBoolean wasCH;
  public AtomicBoolean wasFF;
  public AtomicBoolean wasGE;
  public AtomicBoolean wasKO;
  public AtomicBoolean wasKK;
  public AtomicBoolean wasLeP;
  public AtomicBoolean wasAsP;
  public AtomicBoolean wasKaP;
  public AtomicBoolean wasINI;
  public AtomicBoolean wasAW;
  public AtomicBoolean wasSK;
  public AtomicBoolean wasZK;
  public AtomicBoolean wasGS;
  public AtomicBoolean wasAT;
  public AtomicBoolean wasPA;
  public AtomicBoolean wasTP;
  public AtomicBoolean wasRW;
  public AtomicBoolean wasFK;
  public AtomicBoolean wasLZ;
  public AtomicBoolean wasRS_BE;
  public AtomicBoolean wasAktionen;
  public AtomicBoolean wasVorteile_Nachteile;
  public AtomicBoolean wasSonderfertigkeiten;
  public AtomicBoolean wasTalente;
  public AtomicBoolean wasAnzahl;
  public AtomicBoolean wasGrößenkategorie;
  public AtomicBoolean wasTypus;
  public AtomicBoolean wasBeute;
  public AtomicBoolean wasKampfverhalten;
  public AtomicBoolean wasFlucht;
  public AtomicBoolean wasSchmerz_1_bei;
  public AtomicBoolean wasSonderregeln;
  public AtomicBoolean wasErfahren;
  public AtomicBoolean wasKompetent;
  public AtomicBoolean wasWeaponName;
  public AtomicBoolean wasInformation;
  public AtomicBoolean wasQs1;
  public AtomicBoolean wasQs2;
  public AtomicBoolean wasQs3;
  public AtomicBoolean wasVW;
  public AtomicBoolean wasVerbreitung;
  public AtomicBoolean wasLebensweise;


  public ConverterAtomicFlagsProfile()
  {
    initDataFlags();
  }

  @Override
  public AtomicBoolean getFirstFlag()
  {
    return wasName;
  }

  @Override
  public void initDataFlags()
  {
    wasName = new AtomicBoolean(false);
    wasDescription = new AtomicBoolean(false);
    wasGröße = new AtomicBoolean(false);
    wasGewicht = new AtomicBoolean(false);
    wasMU = new AtomicBoolean(false);
    wasKL = new AtomicBoolean(false);
    wasIN = new AtomicBoolean(false);
    wasCH = new AtomicBoolean(false);
    wasFF = new AtomicBoolean(false);
    wasGE = new AtomicBoolean(false);
    wasKO = new AtomicBoolean(false);
    wasKK = new AtomicBoolean(false);
    wasLeP = new AtomicBoolean(false);
    wasAsP = new AtomicBoolean(false);
    wasKaP = new AtomicBoolean(false);
    wasINI = new AtomicBoolean(false);
    wasAW = new AtomicBoolean(false);
    wasSK = new AtomicBoolean(false);
    wasZK = new AtomicBoolean(false);
    wasGS = new AtomicBoolean(false);
    wasAT = new AtomicBoolean(false);
    wasPA = new AtomicBoolean(false);
    wasTP = new AtomicBoolean(false);
    wasRW = new AtomicBoolean(false);
    wasFK = new AtomicBoolean(false);
    wasLZ = new AtomicBoolean(false);
    wasRS_BE = new AtomicBoolean(false);
    wasAktionen = new AtomicBoolean(false);
    wasVorteile_Nachteile = new AtomicBoolean(false);
    wasSonderfertigkeiten = new AtomicBoolean(false);
    wasTalente = new AtomicBoolean(false);
    wasAnzahl = new AtomicBoolean(false);
    wasGrößenkategorie = new AtomicBoolean(false);
    wasTypus = new AtomicBoolean(false);
    wasBeute = new AtomicBoolean(false);
    wasKampfverhalten = new AtomicBoolean(false);
    wasFlucht = new AtomicBoolean(false);
    wasSchmerz_1_bei = new AtomicBoolean(false);
    wasSonderregeln = new AtomicBoolean(false);
    wasErfahren = new AtomicBoolean(false);
    wasKompetent = new AtomicBoolean(false);
    wasWeaponName = new AtomicBoolean(false);
    wasInformation = new AtomicBoolean(false);
    wasQs1 = new AtomicBoolean(false);
    wasQs2 = new AtomicBoolean(false);
    wasQs3 = new AtomicBoolean(false);
    wasVW = new AtomicBoolean(false);
    wasVerbreitung = new AtomicBoolean(false);
    wasLebensweise = new AtomicBoolean(false);
  }
}
