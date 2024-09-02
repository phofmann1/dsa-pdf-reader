package de.pho.dsapdfreader.exporter.model;

import de.pho.dsapdfreader.exporter.model.enums.WeaponKey;

public class AvailabilityWeapon extends Availability {

  public AvailabilityWeapon() {
  } //for csv uses

  public AvailabilityWeapon(WeaponKey weaponKey) {
    super.init(weaponKey);
  }

  public AvailabilityWeapon(WeaponKey weaponKey, AvailabilityWeapon template) {
    this.init(weaponKey, template);
  }
}
