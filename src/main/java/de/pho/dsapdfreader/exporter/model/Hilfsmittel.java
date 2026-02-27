package de.pho.dsapdfreader.exporter.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.pho.dsapdfreader.dsaconverter.strategies.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentCategoryKey;
import de.pho.dsapdfreader.exporter.model.enums.EquipmentKey;
import de.pho.dsapdfreader.exporter.model.enums.HilfsmittelKey;
import de.pho.dsapdfreader.exporter.model.enums.Publication;
import de.pho.dsapdfreader.exporter.model.sammelobjekt.HilfsmittelSO;
import de.pho.dsapdfreader.uid.UidCategory;
import de.pho.dsapdfreader.uid.UidCategorySub;

public class Hilfsmittel implements EquipmentI {

  public String uid;
  public EquipmentKey key;
  public HilfsmittelKey hilfsmittelKey;

  public String name;

  public EquipmentCategoryKey equipmentCategoryKey = EquipmentCategoryKey.pflanzliche_hilfsmittel;

  public List<Publication> publications;

  public Hilfsmittel() {
  }

  public Hilfsmittel(HilfsmittelSO eso) {
    this.key = Extractor.extractEnumKey(UidCategorySub.hilfsmittel.prefix + eso.name, EquipmentKey.class);
    this.hilfsmittelKey = eso.key;
    this.uid = UidCategory.ausruestung_hilfsmittel.prefix + Extractor.extractKeyTextFromText(eso.name).toLowerCase();
    this.name = eso.name;

  }

  @Override
  @JsonIgnore
  public int getKeyValue() {
    return this.key.toValue();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  @JsonIgnore
  public String getRules() {
    return null;
  }

  @Override
  public String getAdvantage() {
    return null;
  }

  @Override
  public String getDisadvantage() {
    return null;
  }

  @Override
  public String getRemark() {
    return null;
  }
}
