package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import de.pho.dsapdfreader.exporter.model.enums.EntityDomainKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractorEntityDomain extends Extractor {
    private static final List<EntityDomainKey> ALL_ELEMENTS = List.of(EntityDomainKey.luft, EntityDomainKey.erz, EntityDomainKey.wasser, EntityDomainKey.feuer, EntityDomainKey.eis, EntityDomainKey.humus);
    private static final List<EntityDomainKey> ALL_DOMAINS = List.of(EntityDomainKey.licht, EntityDomainKey.finsternis, EntityDomainKey.berg,EntityDomainKey.wasser_feen, EntityDomainKey.wald);

    public static List<EntityDomainKey> retrieveDemonic(String name) {
        List<EntityDomainKey> returnValue = new ArrayList<>();
        try {
            returnValue.add(EntityDomainKey.valueOf(extractKeyTextFromTextWithUmlauts(name.replace("allgemein", "dämonisch")).toLowerCase()));
        } catch (IllegalArgumentException e) {
            // String msg = String.format("%s entity domain key could not be interpreted.", name);
            //LOGGER.error(msg);
            System.out.println(extractKeyTextFromTextWithUmlauts(name).toLowerCase() + ",");
        }
        return returnValue;
    }

    public static List<EntityDomainKey> retrieveElement(String preconditions) {
        List<EntityDomainKey> returnValue = new ArrayList<>();
        if(preconditions.contains("alle Elemente")) return ALL_ELEMENTS;
        Matcher m = Pattern.compile("(?<=(Element |Elemente)).*(?=Elementarpakt)").matcher(preconditions);
        boolean isNotPresent = preconditions.contains("bis auf");
        if (m.find()) {
            returnValue = Arrays.stream(m.group().replace("bis auf", "")
                            .replace(" und ", ", ")
                            .replace(" oder ", ", ")
                            .toLowerCase()
                            .split(","))
                    .filter(e ->  !e.isBlank())
                    .map(e -> EntityDomainKey.valueOf(extractKeyTextFromTextWithUmlauts(e).toLowerCase()))
                    .toList();
            if(isNotPresent) {
                List<EntityDomainKey> finalReturnValue = returnValue;
                returnValue = ALL_ELEMENTS.stream().filter(e -> !finalReturnValue.contains(e)).toList();
            }
        }
        return returnValue;
    }

    public static List<EntityDomainKey> retrieveFairy(String preconditions) {
        List<EntityDomainKey> returnValue = new ArrayList<>();
        if(preconditions.contains("Domäne beliebig")) return ALL_DOMAINS;
        Matcher m = Pattern.compile("(?<=(Domäne ))[A-ü ,]*?(?=(, kein| AP|$))").matcher(preconditions);
        if (m.find()) {
            returnValue = Arrays.stream(m.group().replace("bis auf", "")
                            .replace(" und ", ", ")
                            .replace(" oder ", ", ")
                            .replace("Wasser", "Wasser Feen")
                            .toLowerCase()
                            .split(","))
                    .map(e -> EntityDomainKey.valueOf(extractKeyTextFromTextWithUmlauts(e).toLowerCase()))
                    .toList();
        }
        return returnValue;
    }
}
