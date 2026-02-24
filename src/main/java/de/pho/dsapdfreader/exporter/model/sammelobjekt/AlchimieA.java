package de.pho.dsapdfreader.exporter.model.sammelobjekt;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.Alias;
import de.pho.dsapdfreader.exporter.model.Berufsgeheimnis;
import de.pho.dsapdfreader.exporter.model.Price;
import de.pho.dsapdfreader.exporter.model.QSEntry;
import de.pho.dsapdfreader.exporter.model.enums.LaborKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;

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