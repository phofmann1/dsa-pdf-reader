package de.pho.dsapdfreader.exporter.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ObjectRitualKey
{
  bindung_der_waffe,
  eins_mit_der_waffe,
  einschüchternde_waffe,
  kampfglück,
  kreisende_wurfwaffe,
  leuchtkraft,
  mächtige_waffe_gegen_böse_wesen,
  machtvolle_waffenzauber_i,
  machtvolle_waffenzauber_ii,
  machtvolle_waffenzauber_iii,
  machtvolle_waffenzauber_iv,
  scharfe_klinge,
  volumenerweiterung_der_waffe_i,
  volumenerweiterung_der_waffe_ii,
  volumenerweiterung_der_waffe_iii,
  volumenerweiterung_der_waffe_iv,
  volumenerweiterung_der_waffe_v,
  waffe_gegen_böse_wesen,
  waffen_apport,
  wut_des_odûn,
  bindung_der_krallenkette,
  eindrucksvolle_kette,
  krallenketten_apport,
  machtvolle_krallenkettenzauber_i,
  machtvolle_krallenkettenzauber_ii,
  machtvolle_krallenkettenzauber_iii,
  machtvolle_krallenkettenzauber_iv,
  meute,
  schnelligkeit_i,
  schnelligkeit_ii,
  schutz_des_rudels,
  schutz_vor_übernatürlichen_wesen,
  volumenerweiterung_der_krallenkette_i,
  volumenerweiterung_der_krallenkette_ii,
  volumenerweiterung_der_krallenkette_iii,
  volumenerweiterung_der_krallenkette_iv,
  volumenerweiterung_der_krallenkette_v,
  wunden_lecken,
  bindung_des_dolches,
  blutbaum,
  borkenhaut,
  dolch_apport,
  flug_des_dolches,
  gespür_des_dolches,
  kraft_aus_der_erde,
  machtvolle_dolchrituale_i,
  machtvolle_dolchrituale_ii,
  machtvolle_dolchrituale_iii,
  machtvolle_dolchrituale_iv,
  schutz_des_dolches,
  sicht_im_dunkeln,
  tieropferung,
  volumenerweiterung_des_dolchs_i,
  volumenerweiterung_des_dolchs_ii,
  volumenerweiterung_des_dolchs_iii,
  volumenerweiterung_des_dolchs_iv,
  volumenerweiterung_des_dolchs_v,
  weg_des_dolches,
  wegweiser,
  bannsichel,
  bindung_der_sichel,
  ernte_der_sichel,
  haltbare_ernte,
  kraft_der_sichel,
  kräuterernte,
  machtvolle_sichelrituale_i,
  machtvolle_sichelrituale_ii,
  machtvolle_sichelrituale_iii,
  machtvolle_sichelrituale_iv,
  schnitt_durch_stein,
  sichel_apport,
  sichel_wider_übernatürliches,
  volumenerweiterung_der_sichel_i,
  volumenerweiterung_der_sichel_ii,
  volumenerweiterung_der_sichel_iii,
  volumenerweiterung_der_sichel_iv,
  volumenerweiterung_der_sichel_v,
  zaubermistel,
  bindung_des_rings,
  heilkräfte_der_natur,
  beherrscher_der_flammen,
  eiskristalle,
  frostwelle,
  launen_des_windes,
  macht_über_regen,
  machtvolle_heilkräfte_der_natur,
  machtvolle_ringzauber_i,
  machtvolle_ringzauber_ii,
  machtvolle_ringzauber_iii,
  machtvolle_ringzauber_iv,
  machtvoller_wasserbann,
  machtvoller_wirbelnder_luftschild,
  magnetismus,
  pforte_durch_sumus_leib,
  schutzzone_vor_bösem,
  seelenfeuer,
  volumenerweiterung_des_rings_i,
  volumenerweiterung_des_rings_ii,
  volumenerweiterung_des_rings_iii,
  volumenerweiterung_des_rings_iv,
  volumenerweiterung_des_rings_v,
  wasserbann,
  weg_durch_sumus_leib,
  wirbelnder_luftschild,
  astralentzug,
  astralraub,
  astraltransfer,
  astralverschiebung_volumenerweiterung_i,
  astralverschiebung_volumenerweiterung_ii,
  astralverschiebung_volumenerweiterung_iii,
  astralverschiebung_volumenerweiterung_iv,
  astralverschiebung_volumenerweiterung_v,
  astralverschiebung_volumenerweiterung_vi,
  astralverschiebung_volumenerweiterung_vii,
  bannstab_des_adepten,
  bannstab_des_erzmagus,
  bannstab_des_magus,
  bann_volumenerweiterung_i,
  bann_volumenerweiterung_ii,
  bann_volumenerweiterung_iii,
  bann_volumenerweiterung_iv,
  bann_volumenerweiterung_v,
  bann_volumenerweiterung_vi,
  bann_volumenerweiterung_vii,
  bindung_des_stabes,
  doppeltes_maß,
  druckwelle,
  energiestrahl,
  ewige_flamme,
  flammenschwert,
  gestapelter_zauberspeicher_i,
  gestapelter_zauberspeicher_ii,
  halbes_maß,
  hammer_des_adepten,
  hammer_des_erzmagus,
  hammer_des_magus,
  hammer_volumenerweiterung_i,
  hammer_volumenerweiterung_ii,
  hammer_volumenerweiterung_iii,
  hammer_volumenerweiterung_iv,
  hammer_volumenerweiterung_v,
  hammer_volumenerweiterung_vi,
  hammer_volumenerweiterung_vii,
  heilschlaf,
  kraftfokus,
  machtvolle_stabzauber_i,
  machtvolle_stabzauber_ii,
  machtvolle_stabzauber_iii,
  machtvolle_stabzauber_iv,
  machtvoller_energiestrahl,
  machtvoller_zauberspeicher,
  merkmalsfokus,
  modifikationsfokus,
  schlossöffner,
  seil_des_adepten,
  seil_des_erzmagus,
  seil_des_magus,
  seilpeitsche,
  seilschlange,
  seil_volumenerweiterung_i,
  seil_volumenerweiterung_ii,
  seil_volumenerweiterung_iii,
  seil_volumenerweiterung_iv,
  seil_volumenerweiterung_v,
  seil_volumenerweiterung_vi,
  seil_volumenerweiterung_vii,
  siegelbrecher,
  stab_apport,
  stabbohrer,
  stabexplosion,
  stabfixierung,
  telekineseschlag,
  telekinese_volumenerweiterung_i,
  telekinese_volumenerweiterung_ii,
  telekinese_volumenerweiterung_iii,
  telekinese_volumenerweiterung_iv,
  telekinese_volumenerweiterung_v,
  telekinese_volumenerweiterung_vi,
  telekinese_volumenerweiterung_vii,
  tierwandlung_chamäleon,
  tierwandlung_speikobra,
  tierwandlung_eichhörnchen,
  tierwandlung_biber,
  tierwandlung_taube,
  tierwandlung_rabe,
  torsperre,
  volumenerweiterung_des_zauberstabes_i,
  volumenerweiterung_des_zauberstabes_ii,
  volumenerweiterung_des_zauberstabes_iii,
  volumenerweiterung_des_zauberstabes_iv,
  volumenerweiterung_des_zauberstabes_v,
  zauberspeicher_des_adepten,
  zauberspeicher_des_erzmagus,
  zauberspeicher_des_magus,
  zauberspeicher_volumenerweiterung_i,
  zauberspeicher_volumenerweiterung_ii,
  zauberspeicher_volumenerweiterung_iii,
  zauberspeicher_volumenerweiterung_iv,
  zauberspeicher_volumenerweiterung_v,
  zauberspeicher_volumenerweiterung_vi,
  zauberspeicher_volumenerweiterung_vii,
  bannschwert_des_adepten,
  bannschwert_des_erzmagus,
  bannschwert_des_magus,
  bannschwert_des_schutzsegens,
  bannschwert_apport,
  bindung_des_bannschwerts_dolche,
  bindung_des_bannschwerts_schwerter,
  bindung_des_bannschwerts_zweihandschwerter,
  fokus_der_austreibung,
  klinge_wider_chimären,
  klinge_wider_daimonide,
  klinge_wider_golems,
  klinge_wider_untote,
  klinge_wider_feen,
  klinge_wider_geister,
  klinge_wider_dämonen,
  klinge_wider_elementare,
  machtvolle_bannschwertzauber_i,
  machtvolle_bannschwertzauber_ii,
  machtvolle_bannschwertzauber_iii,
  machtvolle_bannschwertzauber_iv,
  paralysierendes_schwert,
  übernatürlicher_wegweiser,
  verschwinde,
  volumenerweiterung_des_bannschwerts_i,
  volumenerweiterung_des_bannschwerts_ii,
  volumenerweiterung_des_bannschwerts_iii,
  volumenerweiterung_des_bannschwerts_iv,
  volumenerweiterung_des_bannschwerts_v,
  aufbewahrung,
  bindung_der_magierkugel,
  brennglas,
  geschossschutz,
  großes_orbitarium,
  kleines_orbitarium,
  konservierungsgefäß,
  kopierter_kugelzauber,
  kugelbarriere,
  lichtstrahl,
  machtvolle_kugelzauber_i,
  machtvolle_kugelzauber_ii,
  machtvolle_kugelzauber_iii,
  machtvolle_kugelzauber_iv,
  magierkugel_apport,
  miniaturkugel,
  schutz_gegen_untote,
  vergrößerungsglas,
  volumenerweiterung_der_magierkugel_i,
  volumenerweiterung_der_magierkugel_ii,
  volumenerweiterung_der_magierkugel_iii,
  volumenerweiterung_der_magierkugel_iv,
  volumenerweiterung_der_magierkugel_v,
  allegorische_analyse,
  feuer_und_eis,
  kleine_chymische_hochzeit,
  bindung_der_goblinkeule,
  goblinkeulen_apport,
  keulenwurf,
  machtvolle_keulenzauber_i,
  machtvolle_keulenzauber_ii,
  machtvolle_keulenzauber_iii,
  machtvolle_keulenzauber_iv,
  rottenheilung,
  schweinekraft,
  tierfreundschaft,
  volumenerweiterung_der_goblinkeule_i,
  volumenerweiterung_der_goblinkeule_ii,
  volumenerweiterung_der_goblinkeule_iii,
  volumenerweiterung_der_goblinkeule_iv,
  volumenerweiterung_der_goblinkeule_v,
  wirbelsteine,
  bindung_der_schweinetrommel,
  geschrei_der_schweinetrommel,
  goblingelage,
  goblins_die_auf_schweine_starren,
  mutiges_grunzen,
  machtvolle_schweinetrommelzauber_i,
  machtvolle_schweinetrommelzauber_ii,
  machtvolle_schweinetrommelzauber_iii,
  machtvolle_schweinetrommelzauber_iv,
  schweinetanz,
  schweinetrommel_apport,
  volumenerweiterung_der_schweinetrommel_i,
  volumenerweiterung_der_schweinetrommel_ii,
  volumenerweiterung_der_schweinetrommel_iii,
  volumenerweiterung_der_schweinetrommel_iv,
  volumenerweiterung_der_schweinetrommel_v,
  bindung_der_hauerkette,
  fruchtbarkeitsritus,
  goblinnahrung,
  hauerketten_apport,
  machtvolle_hauerkettenzauber_i,
  machtvolle_hauerkettenzauber_ii,
  machtvolle_hauerkettenzauber_iii,
  machtvolle_hauerkettenzauber_iv,
  mahlzeit_der_meute,
  schutz_der_muttersau,
  trüffelsinn,
  volumenerweiterung_der_hauerkette_i,
  volumenerweiterung_der_hauerkette_ii,
  volumenerweiterung_der_hauerkette_iii,
  volumenerweiterung_der_hauerkette_iv,
  volumenerweiterung_der_hauerkette_v,
  angsteinflößendes_geblubber,
  benommenheitstrank,
  bindung_des_hexenkessels,
  brechsuppe,
  giftgebräu,
  heilsüppchen,
  hexenkessel_apport,
  krafttrank,
  machtvolle_kesselzauber_i,
  machtvolle_kesselzauber_ii,
  machtvolle_kesselzauber_iii,
  machtvolle_kesselzauber_iv,
  schönheitssalbe,
  schutzsalbe,
  tarnsalbe,
  tiersuppe,
  volumenerweiterung_des_hexenkessels_i,
  volumenerweiterung_des_hexenkessels_ii,
  volumenerweiterung_des_hexenkessels_iii,
  volumenerweiterung_des_hexenkessels_iv,
  volumenerweiterung_des_hexenkessels_v,
  waschung_des_vertrauten,
  beutel_apport,
  bindung_des_schuppenbeutels,
  ewige_wegzehrung,
  leichter_beutel_i,
  leichter_beutel_ii,
  leichter_beutel_iii,
  machtvolle_beutelzauber_i,
  machtvolle_beutelzauber_ii,
  machtvolle_beutelzauber_iii,
  machtvolle_beutelzauber_iv,
  riesiger_beutel_i,
  riesiger_beutel_ii,
  riesiger_beutel_iii,
  riesiger_beutel_iv,
  riesiger_beutel_v,
  schutz_des_beutels_i,
  schutz_des_beutels_ii,
  suchende_finger,
  unauffindbarer_inhalt,
  unheilvoller_inhalt,
  volumenerweiterung_des_schuppenbeutels_i,
  volumenerweiterung_des_schuppenbeutels_ii,
  volumenerweiterung_des_schuppenbeutels_iii,
  volumenerweiterung_des_schuppenbeutels_iv,
  volumenerweiterung_des_schuppenbeutels_v,
  windbeutel,
  bilderspiel,
  bindung_der_kristallkugel,
  fernbild,
  großer_hohlraum,
  kugel_des_hellsehers,
  kugel_des_zauberschutzes,
  kugel_apport,
  mehrfaches_volumen,
  volumenerweiterung_der_kristallkugel_i,
  volumenerweiterung_der_kristallkugel_ii,
  volumenerweiterung_der_kristallkugel_iii,
  volumenerweiterung_der_kristallkugel_iv,
  volumenerweiterung_der_kristallkugel_v,
  wachendes_auge,
  warnendes_leuchten,
  zauberspeicher_kristallkugel_i,
  zauberspeicher_kristallkugel_ii,
  astralspeicher_haube_i,
  astralspeicher_haube_ii,
  astralspeicher_haube_iii,
  bindung_der_echsenhaube,
  hauben_apport,
  machtvolle_haubenzauber_i,
  machtvolle_haubenzauber_ii,
  machtvolle_haubenzauber_iii,
  machtvolle_haubenzauber_iv,
  meisterschliff_i,
  meisterschliff_ii,
  merkmalsverstärkung,
  schutz_des_geistes_i,
  schutz_des_geistes_ii,
  volumenerweiterung_der_echsenhaube_i,
  volumenerweiterung_der_echsenhaube_ii,
  volumenerweiterung_der_echsenhaube_iii,
  volumenerweiterung_der_echsenhaube_iv,
  volumenerweiterung_der_echsenhaube_v,
  aufnahme,
  bindung_der_zauberkugel,
  illusionsspeicher,
  kommunikation,
  kugel_des_illusionisten,
  magischer_schutzwall,
  prisma,
  störfeld,
  volumenerweiterung_der_zauberkugel_i,
  volumenerweiterung_der_zauberkugel_ii,
  volumenerweiterung_der_zauberkugel_iii,
  volumenerweiterung_der_zauberkugel_iv,
  volumenerweiterung_der_zauberkugel_v,
  zauberkugel_apport,
  funkenregen,
  leuchtfeuer,
  bindung_der_narrenkappe,
  fernkampfmurks,
  geräumige_kappe,
  jemanden_für_sich_sprechen_lassen,
  machtvolle_kappenzauber_i,
  machtvolle_kappenzauber_ii,
  machtvolle_kappenzauber_iii,
  machtvolle_kappenzauber_iv,
  massenverwirrung,
  mir_nach,
  na_los_mir_nach,
  narrenkappen_apport,
  publikumsliebling,
  ungesehen,
  volumenerweiterung_der_narrenkappe_i,
  volumenerweiterung_der_narrenkappe_ii,
  volumenerweiterung_der_narrenkappe_iii,
  volumenerweiterung_der_narrenkappe_iv,
  volumenerweiterung_der_narrenkappe_v,
  beleidigungen,
  beweg_dich,
  bindung_des_schelmenspielzeugs,
  guter_witz,
  kämpfendes_spielzeug,
  machtvolle_spielzeugzauber_i,
  machtvolle_spielzeugzauber_ii,
  machtvolle_spielzeugzauber_iii,
  machtvolle_spielzeugzauber_iv,
  schelmenspielzeug_apport,
  spielbewegung,
  spionierendes_spielzeug,
  trösten,
  volumenerweiterung_des_schelmenspielzeugs_i,
  volumenerweiterung_des_schelmenspielzeugs_ii,
  volumenerweiterung_des_schelmenspielzeugs_iii,
  volumenerweiterung_des_schelmenspielzeugs_iv,
  volumenerweiterung_des_schelmenspielzeugs_v,
  alchimistenschalen_apport,
  bindung_der_alchimistenschale,
  chymische_hochzeit,
  dunkle_schale,
  giftiges_metall,
  hilfreiche_schale,
  konservierende_schale,
  leuchtende_schale,
  luftdichte_schale,
  machtvolle_schalenzauber_i,
  machtvolle_schalenzauber_ii,
  machtvolle_schalenzauber_iii,
  machtvolle_schalenzauber_iv,
  machtvolle_tränke,
  schnelles_brauen,
  schwebende_schale,
  telekinetische_schale,
  volumenerweiterung_der_alchimistenschale_i,
  volumenerweiterung_der_alchimistenschale_ii,
  volumenerweiterung_der_alchimistenschale_iii,
  volumenerweiterung_der_alchimistenschale_iv,
  volumenerweiterung_der_alchimistenschale_v,
  zersetzende_schale,
  bann_des_übernatürlichen,
  bindung_des_zauberinstruments,
  brechender_fels,
  emotionsübernahme,
  fokus_der_aufmerksamkeit,
  hilferuf,
  klangillusion,
  langanhaltender_rhythmus,
  machtvoller_instrumentzauber_i,
  machtvoller_instrumentzauber_ii,
  machtvoller_instrumentzauber_iii,
  machtvoller_instrumentzauber_iv,
  ohrenbetäubender_klang,
  scharfes_instrument,
  stärkung_der_kampfgefährten,
  störgeräusch,
  viele_instrumente,
  volumenerweiterung_des_zauberinstruments_i,
  volumenerweiterung_des_zauberinstruments_ii,
  volumenerweiterung_des_zauberinstruments_iii,
  volumenerweiterung_des_zauberinstruments_iv,
  volumenerweiterung_des_zauberinstruments_v,
  weiter_klang,
  zauberinstrument_apport,
  zerspringendes_glas,
  bindung_des_trinkhorns,
  friedensstifter,
  heldenrausch,
  machtvolle_trinkhornzauber_i,
  machtvolle_trinkhornzauber_ii,
  machtvolle_trinkhornzauber_iii,
  machtvolle_trinkhornzauber_iv,
  nebel_der_erinnerung,
  ögnirs_gelage,
  sumu_nimmt_und_sumu_gibt,
  trinkhorn_apport,
  volumenerweiterung_des_trinkhorns_i,
  volumenerweiterung_des_trinkhorns_ii,
  volumenerweiterung_des_trinkhorns_iii,
  volumenerweiterung_des_trinkhorns_iv,
  volumenerweiterung_des_trinkhorns_v,
  zwistbringer,
  besitzanspruch,
  bindung_der_zauberkleidung,
  diebesgewand,
  faszinierende_kleidung,
  fesselnde_gewandung,
  fokus_der_begierde,
  gefühlsübernahme,
  gewand_der_beeinflussung,
  gewand_der_heilung,
  gewand_der_täuschung,
  langer_tanz,
  machtvolle_gewandzauber_i,
  machtvolle_gewandzauber_ii,
  machtvolle_gewandzauber_iii,
  machtvolle_gewandzauber_iv,
  schmutzabweisend,
  schutzgewand_i,
  schutzgewand_ii,
  verführerische_gewandung,
  volumenerweiterung_der_zauberkleidung_i,
  volumenerweiterung_der_zauberkleidung_ii,
  volumenerweiterung_der_zauberkleidung_iii,
  volumenerweiterung_der_zauberkleidung_iv,
  volumenerweiterung_der_zauberkleidung_v,
  wandelbare_kleidung,
  zauberkleidung_apport,
  bindung_der_chronik,
  chronik_apport,
  eindämmung_des_kampfeswillens,
  hilfe_der_bienen,
  heilungswissen,
  honigkleber,
  machtvolle_chronikzauber_i,
  machtvolle_chronikzauber_ii,
  machtvolle_chronikzauber_iii,
  machtvolle_chronikzauber_iv,
  mokoschas_schutz,
  sicht_auf_heshinjas_werk,
  volumenerweiterung_der_chronik_i,
  volumenerweiterung_der_chronik_ii,
  volumenerweiterung_der_chronik_iii,
  volumenerweiterung_der_chronik_iv,
  volumenerweiterung_der_chronik_v,
  wind_des_schutzes,
  wissen_ist_macht;

  public static Optional<ObjectRitualKey> fromString(String str)
  {
    String cleanName = str.toUpperCase().replace(" ", "_")
        .replace("&", "UND");

    List<String> names = Arrays.stream(ObjectRitualKey.values()).map(v -> v.name() + "_" + cleanName).collect(Collectors.toList());
    return Arrays.stream(ObjectRitualKey.values()).filter(msv -> names.stream().anyMatch(n -> n.equalsIgnoreCase(msv.name()))).findFirst();
  }

  @JsonValue
  public int toValue()
  {
    return ordinal();
  }
}
