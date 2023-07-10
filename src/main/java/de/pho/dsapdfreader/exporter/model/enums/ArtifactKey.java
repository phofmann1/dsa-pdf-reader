package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ArtifactKey
{
  alchimistenschale,
  angroschanhänger,
  animistenwaffe,
  avesstab,
  bannschwert,
  buch_der_schlange,
  druidendolch,
  druidensichel,
  efferdbart,
  einhornstirnband,
  ferkinaknochenkeule,
  firunsmesser,
  fjarningerknochenkeule,
  gänsebeutel,
  gildenmagische_magierkugel,
  gjalskerknochenkeule,
  grüne_handschuhe,
  hexenkessel,
  ifirnsmantel,
  ingerimmshammer,
  korspieß,
  lebensring,
  magierstab,
  marbodolch,
  mohaknochenkeule,
  mondamulett,
  narrenkappe,
  nivesenknochenkeule,
  opferdolch_des_namenlosen,
  prisma,
  rabenschnabel,
  rondrakamm,
  roter_schleier,
  scharlatanische_zauberkugel,
  schelmenspielzeug,
  sippenchronik,
  sonnenzepter,
  trollzackerknochenkeule,
  walschild,
  widderkeule,
  zauberinstrument,
  zauberkleidung,
  zauberstecken,
  krallenkette,
  seeleninstrument,
  zauberschale,
  goblinkeule,
  schweinetrommel,
  hauerkette,
  fluggerät,
  schuppenbeutel,
  trinkhorn,
  kristallomantische_kristallkugel,
  echsenhaube;

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
