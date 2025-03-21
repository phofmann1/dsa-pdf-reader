package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BoonVariantKey
{
  biss_winzig,
  biss_klein,
  biss_mittel,
  biss_groß,
  biss_riesig,
  hass_auf_x_häufig,
  hass_auf_x_gelegentlich,
  hass_auf_x_selten,
  herausragender_sinn_sicht,
  herausragender_sinn_gehör,
  herausragender_sinn_geruch_und_geschmack,
  herausragender_sinn_tastsinn,
  angst_vor_x_tierart,
  angst_vor_x_blut,
  angst_vor_x_dunkelheit,
  angst_vor_x_höhe,
  angst_vor_x_dem_meer,
  angst_vor_x_engen_räumen,
  angst_vor_x_toten_und_untoten,
  eingeschränkter_sinn_sicht,
  eingeschränkter_sinn_gehör,
  eingeschränkter_sinn_geruch_und_geschmack,
  eingeschränkter_sinn_tastsinn,
  persönlichkeitsschwäche_arroganz,
  persönlichkeitsschwäche_eitelkeit,
  persönlichkeitsschwäche_neid,
  persönlichkeitsschwäche_streitsucht,
  persönlichkeitsschwäche_unheimlich,
  persönlichkeitsschwäche_verwöhnt,
  persönlichkeitsschwäche_vorurteile,
  persönlichkeitsschwäche_weltfremd,
  prinzipientreue_i_,
  prinzipientreue_ii_,
  prinzipientreue_iii_,
  schlechte_angewohnheit_barfüßer,
  schlechte_angewohnheit_dritte_person,
  schlechte_angewohnheit_duzer,
  schlechte_angewohnheit_erster,
  schlechte_angewohnheit_heulsuse,
  schlechte_angewohnheit_hypochonder,
  schlechte_angewohnheit_junge,
  schlechte_angewohnheit_langschläfer,
  schlechte_angewohnheit_links_rechts_schwäche,
  schlechte_angewohnheit_mein_kind,
  schlechte_angewohnheit_nägelkauer,
  schlechte_angewohnheit_nase_ohrenbohrer,
  schlechte_angewohnheit_nervös,
  schlechte_angewohnheit_putzfimmel,
  schlechte_angewohnheit_redet_wie_ein_wasserfall,
  schlechte_angewohnheit_schlechte_tischmanieren,
  schlechte_angewohnheit_selbstgespräche,
  schlechte_angewohnheit_unordentlich,
  schlechte_angewohnheit_wir,
  schlechte_eigenschaften_aberglaube,
  schlechte_eigenschaften_autoritätsgläubig,
  schlechte_eigenschaften_geiz,
  schlechte_eigenschaften_goldgier,
  schlechte_eigenschaften_jähzorn,
  schlechte_eigenschaften_kleptomanie,
  schlechte_eigenschaften_naiv,
  schlechte_eigenschaften_neugier,
  schlechte_eigenschaften_rachsucht,
  schlechte_eigenschaften_spielsucht,
  schlechte_eigenschaften_verschwendungssucht,
  stigma_albino,
  stigma_grüne_haare,
  stigma_brandmale,
  stigma_katzenhafte_augen,
  stigma_schlangenschuppen,
  verstümmelung_einarmig,
  verstümmelung_einäugig,
  verstümmelung_einbeinig,
  verstümmelung_einhändig,
  verstümmelung_einohrig,
  schlechte_angewohnheit_raucher,
  stigma_kein_schatten,
  stigma_kein_spiegelbild;

  @JsonValue
  public int toValue() {
    return ordinal();
  }

}
