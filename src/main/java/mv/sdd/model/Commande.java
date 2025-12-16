package mv.sdd.model;

import mv.sdd.utils.Constantes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Commande {
    private int id;
    private static int nbCmd = 0;
    private final Client client;
    private EtatCommande etat = EtatCommande.EN_ATTENTE;
    private int tempsRestant; // en minutes simulées
    // TODO : ajouter l'attribut plats et son getter avec le bon type et le choix de la SdD adéquat
    // private final <Votre structure de choix adéquat> plats
    private final List<MenuPlat> plats = new ArrayList<>();

    // TODO : Ajout du ou des constructeur(s) nécessaires ou compléter au besoin
    public Commande(Client client) {
        this.id = ++nbCmd;
        this.client = client;
        // À compléter

        this.tempsRestant = 0;
    }

    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public int getTempsRestant() {
        return tempsRestant;
    }

    public void setEtat(EtatCommande etat) {
        this.etat = etat;
    }

    public List<MenuPlat> getPlats() {
        return new ArrayList<>(plats);
    }

    // TODO : Ajoutez la méthode ajouterPlat
    public void ajouterPlat(MenuPlat plat) {
        plats.add(plat);
        if (etat == EtatCommande.EN_ATTENTE) {
            tempsRestant = calculerTempsPreparationTotal();
        }
    }

    // TODO : Ajoutez la méthode demarrerPreparation
    public void demarrerPreparation() {
        etat = EtatCommande.EN_PREPARATION;
        tempsRestant = calculerTempsPreparationTotal();
    }


    // TODO : Ajoutez la méthode decrementerTempsRestant
    public void decrementerTempsRestant(int minutes) {
        if (etat == EtatCommande.EN_PREPARATION) {
            tempsRestant = Math.max(0, tempsRestant - minutes);
            if (tempsRestant == 0) {
                etat = EtatCommande.PRETE;
            }
        }
    }


    // TODO : Ajoutez la méthode estTermineeParTemps
    public boolean estTermineeParTemps() {
        return etat == EtatCommande.PRETE;
    }

    // TODO : Ajoutez la méthode calculerTempsPreparationTotal
    public int calculerTempsPreparationTotal() {
        int total = 0;
        for (MenuPlat plat : plats) {
            total += Constantes.MENU.get(plat).getTempsPreparation();
        }
        return total;
    }


    // TODO : Ajoutez la méthode calculerMontant
    public double calculerMontant() {
        double total = 0;
        for (MenuPlat plat : plats) {
            total += Constantes.MENU.get(plat).getPrix();
        }
        return total;
    }

}
