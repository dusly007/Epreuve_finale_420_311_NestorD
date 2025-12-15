package mv.sdd.model;

import mv.sdd.utils.Constantes;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Stats {
    private Horloge horloge;
    private int totalClients = 0;
    private int nbServis = 0;
    private int nbFaches = 0;
    private double chiffreAffaires = 0;
    // TODO : remplacer Object par le bon type et initilaliser l'attribut avec la bonne valeur
    //  et ajuster les getters et les setters
    private final EnumMap<MenuPlat, Integer> ventesParPlat = new EnumMap<>(MenuPlat.class);



    // TODO: au besoin ajuster le constructeur et/ou ajouter d'autres
    public Stats(Horloge horloge) {
        this.horloge = horloge;
        // TODO : compléter le code manquant
        for (MenuPlat plat: MenuPlat.values()) {
            ventesParPlat.put(plat, 0);
        }
    }

    public void incrementerTotalClients() {
        totalClients++;
    }

    public void incrementerNbServis() {
        nbServis++;
    }

    public void incrementerNbFaches() {
        nbFaches++;
    }

    public void incrementerChiffreAffaires(double montant) {
        this.chiffreAffaires += montant;
    }

    public static String statsPlatLine(MenuPlat codePlat, int quantite) {
        return "\n" + "\t\t" + codePlat + " : " + quantite;
    }

    // TODO : ajouter incrementerVentesParPlat(MenuPlat codePlat) et autres méthodes au besoin
    public void incrementerVentesParPlat(MenuPlat codePlat) {
        ventesParPlat.put(codePlat, ventesParPlat.get(codePlat) + 1);
    }

    @Override
    public String toString() {
        String chaine = String.format(
                Constantes.STATS_GENERAL,
                horloge.getTempsSimule(),
                totalClients,
                nbServis,
                nbFaches,
                chiffreAffaires
        );
        for (MenuPlat plat : MenuPlat.values()) {
            chaine += statsPlatLine(plat, ventesParPlat.get(plat));
        }
        return chaine;
    }
}
