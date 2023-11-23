package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UsageRestrictionKey
{
  baum, einzelne_person, nahrungsbeschaffung, probenart_vergleich, transport, im_wald, kristall_verwendet, unmodifiziert, kristall_geopfert, fischfang_oder_seefahrt, modifiziert;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
