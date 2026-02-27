package de.pho.dsapdfreader.dsaconverter.model.atomicflags;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConverterAtomicFlagsAlchimie implements ConverterAtomicFlagsI {
        public AtomicBoolean wasName = new AtomicBoolean(false);
        public AtomicBoolean wasDescription = new AtomicBoolean(false);
        public AtomicBoolean wasTypicalIngredients = new AtomicBoolean(false);
  public AtomicBoolean wasZutaten = new AtomicBoolean(false);
  public AtomicBoolean wasCost = new AtomicBoolean(false);
        public AtomicBoolean wasLabor = new AtomicBoolean(false);
        public AtomicBoolean wasBrewingDifficulty = new AtomicBoolean(false);
        public AtomicBoolean wasRequirements = new AtomicBoolean(false);
  public AtomicBoolean wasApValue = new AtomicBoolean(false);
  public AtomicBoolean isSecret = new AtomicBoolean(false);
  public AtomicBoolean wasQualityLevels = new AtomicBoolean(false);
        public AtomicBoolean wasStufe = new AtomicBoolean(false);
        public AtomicBoolean wasArt = new AtomicBoolean(false);
        public AtomicBoolean wasWiderstand = new AtomicBoolean(false);
        public AtomicBoolean wasWirkung = new AtomicBoolean(false);
        public AtomicBoolean wasBeginn = new AtomicBoolean(false);
        public AtomicBoolean wasDauer = new AtomicBoolean(false);
        public AtomicBoolean wasWertPreis = new AtomicBoolean(false);
        public AtomicBoolean wasAlternativeNamen = new AtomicBoolean(false);
        public AtomicBoolean wasEinnahme = new AtomicBoolean(false);
        public AtomicBoolean wasNebenwirkung = new AtomicBoolean(false);
        public AtomicBoolean wasLegalitaet = new AtomicBoolean(false);
        public AtomicBoolean wasSucht = new AtomicBoolean(false);
        public AtomicBoolean wasUeberdosierung = new AtomicBoolean(false);
        public AtomicBoolean wasBesonderheiten = new AtomicBoolean(false);
        public AtomicBoolean wasHyperpotenteWirkung = new AtomicBoolean(false);
    @Override
        public void initDataFlags() {
      wasName.set(false); // Name kommt zuerst
      wasDescription.set(false);
      wasTypicalIngredients.set(false);
      wasZutaten.set(false);
      wasCost.set(false);
      wasLabor.set(false);
      wasBrewingDifficulty.set(false);
      wasRequirements.set(false);
      wasApValue.set(false);
      wasQualityLevels.set(false);
      wasStufe.set(false);
      wasArt.set(false);
      wasWiderstand.set(false);
      wasWirkung.set(false);
      wasBeginn.set(false);
      wasDauer.set(false);
      wasWertPreis.set(false);
      wasAlternativeNamen.set(false);
      wasEinnahme.set(false);
      wasNebenwirkung.set(false);
      wasLegalitaet.set(false);
      wasSucht.set(false);
      wasUeberdosierung.set(false);
      wasBesonderheiten.set(false);
      wasHyperpotenteWirkung.set(false);
      isSecret.set(false);
    }


    public ConverterAtomicFlagsAlchimie() {
        initDataFlags();
    }

    @Override
    public AtomicBoolean getFirstFlag() {
        return wasName;
    }

}
