package de.pho.dsapdfreader.exporter.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Publication
{
    ALMANACH,
    BASIS,
    BOTE,
    EISERNE_FLAMMEN,
    KATAKOMBEN_UND_RUINEN,
    KOMPENDIUM_1,
    KOMPENDIUM_2,
    MAGIE_1,
    MAGIE_2,
    MAGIE_3,
    SIEBENWINDKUESTE,
    STREITENDE_KOENIGREICHE,
    UNBEKANNT,
    RUESTKAMMER_1,
    BUENDNIS_DER_WACHT,
    DER_WEISSE_SEE,
    SCHIRM,
    NAMEN,
    HAUSREGEL,
    GLAUBE_MACHT_UND_HELDENMUT,
    GOETTER_1,
    GOETTER_2,
    KLINGEN_DER_NACHT,
    BESTIARIUM_1,
    BESTIARIUM_2,
    KNEIPEN_UND_TAVERNEN,
    FLUSSLANDE,
    GEFANGEN_IN_DER_GRUFT_DER_KOENIGIN,
    RUESTKAMMER_2,
    DORNENREICH,
    DER_DUNKLE_MHANADI,
    HAVENA,
    DIE_SILBERNE_WEHR,
    HERBARIUM_1,
    HERBARIUM_2;

    @JsonValue
    public int toValue()
    {
        return ordinal();
    }

}