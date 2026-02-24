package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.AlchimieTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.AttributeShort;
import de.pho.dsapdfreader.exporter.model.enums.LaborKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;

import java.util.List;

public abstract class AlchimieA {
    public String name;
    public List<Alias> alternativeNamen;
    public List<Publication> publications ;
    public Berufsgeheimnis berufsgeheimnis;
    public List<String> typicalIngredients;
    public Price kostenIngredienzien;
    public LaborKey labor;
    public Integer brewingDifficulty;
    public List<String> requirements;
    public String description;
    public Price preis;
    public String besonderheiten;
    public QSEntry hyperpotenteWirkung;
}