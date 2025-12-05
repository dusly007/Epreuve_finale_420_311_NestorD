package mv.sdd.model;

import java.util.ArrayList;
import java.util.List;

public class Commande {
    private int id;
    private static int nbCmd = 0;
    private final Client client;
    private EtatCommande etat = EtatCommande.EN_ATTENTE;
    private int tempsRestant; // en minutes simulées
    // TODO : ajouter l'attribut plats et son getter avec le bon type et le choix de la SdD adéquat
    // private final <Votre structure de choix adéquat> plats
    private final List<MenuPlat> plats = new ArrayList<>();


    public List<MenuPlat> getPlats() {
        return plats;
    }

    // TODO : Ajout du ou des constructeur(s) nécessaires ou compléter au besoin
    public Commande(Client client, MenuPlat plat) {
        id = ++nbCmd;
        this.client = client;
        // À compléter
        this.plats.add(plat); // Ajoute le premier plat à la commande
        this.tempsRestant = plat.getTempsPreparation();
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

    // TODO : Ajoutez la méthode ajouterPlat
    public void ajouterPlat(MenuPlat plat) {
        this.plats.add(plat);
        this.tempsRestant += plat.getTempsPreparation();

    }

    // TODO : Ajoutez la méthode demarrerPreparation
    public void demarrerPreparation() {
        if (this.etat == EtatCommande.EN_ATTENTE) {
            this.etat = EtatCommande.EN_PREPARATION; //je change l'état
            System.out.println("Commande #" + id + " en préparation.");
        }
    }

    // TODO : Ajoutez la méthode decrementerTempsRestant
    public void decrementerTempsRestant() {
        if (this.tempsRestant > 0) {
            this.tempsRestant--;
        }
    }


    // TODO : Ajoutez la méthode estTermineeParTemps
    public boolean estTermineeParTemps() {
        return this.tempsRestant <= 0;  //si = 0 ou moins -> commande terminée
    }

    // TODO : Ajoutez la méthode calculerTempsPreparationTotal
    public int calculerTempsPreparationTotal() {
        int total = 0;
        for (MenuPlat plat : plats) {
            total += plat.getTempsPreparation();
        }
        return total;
    }


    // TODO : Ajoutez la méthode calculerMontant
    public double calculerMontant() {
        double montantTotal = 0.0;
        for (MenuPlat plat : plats) {
            // Supposons que chaque plat a une méthode getPrix()
            montantTotal += plat.getPrix();
        }
        return montantTotal;
    }

}
