package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ValueChangeType
{
  value, // absolute Änderung des Wertes
  max, // Änderung des Maximalwertes
  fp, // Änderung der FP bei einem Probenerfolg
  qs, // Änderung der QS bei einem Probenerfolg
  regeneration, // Änderung der Regeneration bei einer Energie
  difficulty, // Änderung der Schwierigkeit eines Tests
  min, //Änderung des Minimalwertes (ACHTUNG: In den veralteten Professionen wurde das zuerst mit value abgebildet!)
  gifted, //Änderung des Verhaltens als Begabter, NUR bei Talenten
  checkAttributeBonus, //Modifikation eines Attributs bei eine Probe um den valueChange
  traditionChangeForFeature,  //Modifikation der Tradition zur eigenen
  newSkillUsage, //Neues Anwendungsgebiet für Skill (Fuchssinn)
  impedeAdversary, //ValueChange wird auf die Probe des gegenübers angewendet (Allerweltsname)
  inebt,
  specialize, //Bei Charaktergenerierung
  newMysticalCategory;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}
