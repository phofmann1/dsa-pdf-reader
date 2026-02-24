package de.pho.dsapdfreader.exporter;

public class SaSpecialRequirementsToggle {
    public boolean isAuthor = false;
    public boolean isHealingSpec = false;
    public boolean isGebieterDesAspekts = false;
    public boolean isDemonicBinding = false;
    public boolean isDemonicTrueName = false;
    public boolean isElementalBinding = false;
    public boolean isElementalTrueName = false;
    public boolean isFairyBinding = false;
    public boolean isDauerhafteGolems = false;

    public SaSpecialRequirementsToggle(String name, String publication) {
        this.isAuthor = name.equals("Schriftstellerei");
        this.isHealingSpec = name.equals("Heilungsspezialgebiet (Anwendungsgebiet)");
        this.isGebieterDesAspekts = name.equals("Gebieter des (Aspekts)");
        this.isDemonicBinding = name.equals("Bindung (Dämonen)");
        this.isDemonicTrueName = name.equals("Wahrer Name (spezieller Dämon)");
        this.isElementalBinding = name.equals("Bindung") && publication.equals("Aventurisches_Elementarium");
        this.isElementalTrueName = name.equals("Wahrer Name") && publication.equals("Aventurisches_Elementarium");
        this.isFairyBinding = name.equals("Bindung (Feen)");
        this.isDauerhafteGolems = name.equals("Dauerhafte Golems");
    }

    public boolean isBaseRequirement() {
        return !this.isAuthor
                && !this.isHealingSpec
                && !this.isGebieterDesAspekts
                && !this.isDemonicBinding
                && !this.isDemonicTrueName
                && !this.isElementalBinding
                && !this.isElementalTrueName
                && !this.isFairyBinding;
    }
}
