package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.EffectCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EffectVectorKey;

import java.util.List;

public class HerbEffect {
    public String beschreibung;
    public EffectVectorKey vectorKey;
    public List<EffectCategoryKey> categories;
}


