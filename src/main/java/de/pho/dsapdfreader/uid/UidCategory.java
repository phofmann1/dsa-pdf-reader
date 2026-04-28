package de.pho.dsapdfreader.uid;

import java.util.Arrays;
import java.util.stream.Stream;

public enum UidCategory {


  //-----------------------------------
  //MAINCATEGORIES
  //-----------------------------------
  attribut("att"),
  ausruestung("ausr"),
  ausruestung_typ(ausruestung.id, "typ"),
  ausruestung_gift(ausruestung, UidCategorySub.gift),
  ausruestung_elixier(ausruestung, UidCategorySub.elixier),
  ausruestung_hilfsmittel(ausruestung, UidCategorySub.hilfsmittel),
  ausruestung_zutatalchimie(ausruestung, UidCategorySub.zutatalchimie),
  ausruestung_droge(ausruestung, UidCategorySub.droge),

  ausruestung_waffe(ausruestung, UidCategorySub.waffe),
  ausruestung_ruestung(ausruestung, UidCategorySub.ruestung),

  rezept("rezept"),

  sonderfertigkeit("sf"),

  magie("magie"),
  magie_trick(magie, UidCategorySub.trick),
  magie_zauber(magie, UidCategorySub.zauber),
  magie_zauber_variante(magie, UidCategorySub.zauber, UidCategorySub.variante),
  magie_ritual(magie, UidCategorySub.ritual),
  magie_ritual_variante(magie, UidCategorySub.ritual, UidCategorySub.variante),
  magie_handlung(magie, UidCategorySub.handlung),

  weihe("weihe"),
  weihe_trick(weihe, UidCategorySub.segnung),
  weihe_liturgie(weihe, UidCategorySub.liturgie),
  weihe_liturgie_variante(weihe, UidCategorySub.liturgie, UidCategorySub.variante),
  weihe_zeremonie(weihe, UidCategorySub.zeremonie),
  weihe_zeremonie_variante(weihe, UidCategorySub.zeremonie, UidCategorySub.variante),
  weihe_predigt(weihe, UidCategorySub.predigt),
  weihe_vision(weihe, UidCategorySub.vision),

  talent("talent"),
  talent_anwendung(talent, UidCategorySub.anwendungsgebiet),
  talent_einsatz(talent, UidCategorySub.einsatzmoeglichkeit),

  profile("profil"),
  profile_kulturschaffend(profile, UidCategorySub.kulturschaffend),
  profile_tier(profile, UidCategorySub.tier),
  profile_pflanzen(profile, UidCategorySub.pflanzen),
  profile_fee(profile, UidCategorySub.fee),
  profile_chimaere(profile, UidCategorySub.chimaere),
  profile_drache(profile, UidCategorySub.drache),
  profile_daimonide(profile, UidCategorySub.daimonide),
  profile_geist(profile, UidCategorySub.geist),
  profile_hirnlose(profile, UidCategorySub.hirnlose),
  profile_vampir(profile, UidCategorySub.vampir),
  profile_nicht_lebende_untot_beseelte(profile, UidCategorySub.nicht_lebende_untot_beseelte),
  profile_daemon(profile, UidCategorySub.daemon),
  profile_elementar(profile, UidCategorySub.elementar),
  profile_unelementar(profile, UidCategorySub.unelementar),
  profile_golem(profile, UidCategorySub.golem);
  public final String prefix;
  private final String id;

  UidCategory(String... ids) {
    this.id = compose(ids);
    this.prefix = this.id + "_";
  }

  UidCategory(UidCategory main, UidCategorySub... subs) {
    this.id = compose(
        Stream.concat(
            Stream.of(main.id),
            Arrays.stream(subs).map(UidCategorySub::externalId)
        ).toArray(String[]::new)
    );
    this.prefix = this.id + "_";
  }

  private static String compose(String... ids) {
    return String.join("_", ids);
  }

  public String externalId() {
    return this.id;
  }
}
