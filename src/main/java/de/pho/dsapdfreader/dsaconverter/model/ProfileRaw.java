package de.pho.dsapdfreader.dsaconverter.model;

import de.pho.dsapdfreader.config.TopicEnum;

public class ProfileRaw implements DsaObjectI
{

  public String name;
  public TopicEnum topic;
  public String publication;
  public String description;
  public String Name;
  public String Größe;
  public String Gewicht;
  public String MU;
  public String KL;
  public String IN;
  public String CH;
  public String FF;
  public String GE;
  public String KO;
  public String KK;
  public String LeP;
  public String AsP;
  public String KaP;
  public String INI;
  public String AW;
  public String SK;
  public String ZK;
  public String GS;
  public String AT;
  public String PA;
  public String TP;
  public String RW;
  public String FK;
  public String LZ;
  public String RS_BE;
  public String Aktionen;
  public String Vorteile_Nachteile;
  public String Sonderfertigkeiten;
  public String Talente;
  public String Anzahl;
  public String Größenkategorie;
  public String Typus;
  public String Beute;
  public String Kampfverhalten;
  public String Flucht;
  public String Schmerz_1_bei;
  public String Sonderregeln;
  public String Erfahren;
  public String Kompetent;
  public String weaponName;
  public String information;
  public String qs1;
  public String qs2;
  public String qs3;
  public String VW;
  public String verbreitung;
  public String lebensweise;

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public void setName(String name)
  {
    this.name = name;
  }

  @Override
  public void setTopic(TopicEnum topic)
  {
    this.topic = topic;
  }

  @Override
  public void setPublication(String publication)
  {
    this.publication = publication;
  }
}
