package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import de.pho.dsapdfreader.exporter.model.enums.EffectCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EffectVectorKey;

public class HerbEffect {
    public String beschreibung;
    public EffectVectorKey vectorKey;
    public List<EffectCategoryKey> categories;
}


