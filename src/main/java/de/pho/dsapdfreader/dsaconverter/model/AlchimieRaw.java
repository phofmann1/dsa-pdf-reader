package de.pho.dsapdfreader.dsaconverter.model;

import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.exporter.model.enums.AlchimieTypeKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;

public class AlchimieRaw implements DsaObjectI{
    public String name;
    public TopicEnum topic;
    public String publication;
    public String typicalIngredients;
    public String cost;
    public String labor;
    public String brewingDifficulty;
    public String requirements;
    public String apValue;
    public String qualityLevels;
    public String description;
    public String alternativeNamen;
    public String stufe;
    public String art;
    public String widerstand;
    public String wirkungKurz;
    public String beginn;
    public String dauer;
    public String wertPreis;
    public String einnahme;
    public String nebenwirkung;
    public String legalitaet;
    public String sucht;
    public String besonderheiten;
    public String hyperpotenteWirkung;
    public String ueberdosierung;
    public AlchimieTypeKey type;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setTopic(TopicEnum topic) {
        this.topic = topic;
    }

    @Override
    public void setPublication(String publication) {
        this.publication = publication;
    }
}
