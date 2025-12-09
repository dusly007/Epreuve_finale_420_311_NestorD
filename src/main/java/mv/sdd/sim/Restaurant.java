package mv.sdd.sim;

import mv.sdd.io.Action;
import mv.sdd.model.*;
import mv.sdd.sim.thread.Cuisinier;
import mv.sdd.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private final Logger logger;
    // TODO : Ajouter les attributs nécessaires ainsi que les getters et les setters
    private final List<Client> clients = new ArrayList<>();
    private final List<Commande> commandesEnFile = new ArrayList<>();
    private final List<Commande> commandesEnPreparation = new ArrayList<>();
    private Horloge horloge;
    private Stats stats;
    private int dureeMax;

    // TODO : Ajouter le(s) constructeur(s)
    public Restaurant(Logger logger) {
        this.logger = logger;
    }

    // TODO : implémenter les méthodes suivantes
    // Méthode appelée depuis App pour chaque action
    public void executerAction(Action action){
        switch (action.getType()) {
            case DEMARRER_SERVICE:
                demarrerService(action.getParam1(), action.getParam2());
                break;
            case AVANCER_TEMPS:
                avancerTemps(action.getParam1());
                break;
            case AJOUTER_CLIENT:
                ajouterClient(action.getParam1(), action.getParam3(), action.getParam2());
                break;
            case PASSER_COMMANDE:
                passerCommande(action.getParam1(), action.getParam3());
                break;
            case AFFICHER_ETAT:
                afficherEtat();
                break;
            case AFFICHER_STATS:
                afficherStatistiques();
                break;
            case QUITTER:
                arreterService();
                break;
            default:
                throw new IllegalArgumentException("Action inconnue : " + action.getType());
        }
    }

    public void demarrerService(int dureeMax, int nbCuisiniers) {
        // Votre code ici.
        this.horloge = new Horloge();
        this.stats = new Stats(horloge);
        logger.logLine("Service démarré avec une durée de " + dureeMax + " minutes et " + nbCuisiniers + " cuisinier(s).");

        //un thread pour chaque cuisinier
        for (int i = 0; i < nbCuisiniers; i++) {
            Thread cuisinierThread = new Thread(String.valueOf(new Cuisinier(this, horloge)));  // Création du thread Cuisinier
            cuisinierThread.start();
        }
        this.dureeMax = dureeMax;
    }

    public void avancerTemps(int minutes) {
        // Votre code ici.
        horloge.avancerTempsSimule(minutes);  // Avancer l'horloge
        logger.logLine("[⏱ t=" + horloge.getTempsSimule() + "] Temps avancé de " + minutes + " minute(s).");
        diminuerPatienceClients(minutes);  // Diminuer la patience des clients
        tick();  // Vérifier les commandes en préparation
    }

    public void arreterService(){
        // Votre code ici.
        logger.logLine("[❌ t=" + horloge.getTempsSimule() + "] Service terminé.");
    }

    // TODO : Déclarer et implémenter les méthodes suivantes
    // tick() avancer commande
    private void tick() {
        for (Commande commande : commandesEnPreparation) {
            commande.decrementerTempsRestant();  // réduit temps
            if (commande.estTermineeParTemps()) {
                marquerCommandeTerminee(commande);
            }
        }
    }

    // afficherEtat()
    public void afficherEtat() {
        logger.logLine("[t=" + horloge.getTempsSimule() + "] 👥 " + clients.size() +
                " 😋 " + clients.stream().filter(c -> c.getEtat() == EtatClient.SERVI).count() +
                " 😡 " + clients.stream().filter(c -> c.getEtat() == EtatClient.PARTI_FACHE).count() +
                " 📥 " + commandesEnFile.size() +
                " 🍳 " + commandesEnPreparation.size());

        for (Client client : clients) {
            logger.logLine("#" + client.getId() + " " + client.getNom() + " " +
                    client.getEtat() + " (pat=" + client.getPatience() + ", " + client.getCommande().getPlats() + ")");
        }
    }

    // afficherStatistiques()
    public void afficherStatistiques() {
        logger.logLine("[📈 t=" + horloge.getTempsSimule() + "] " + stats.toString());
    }

    // Client ajouterClient(int id, String nom, int patienceInitiale)
    public void ajouterClient(int id, String nom, int patienceInitiale) {
        Client client = new Client(id, nom, patienceInitiale);
        clients.add(client);  // Ajoute le client à la liste
        logger.logLine("[🚪 t=" + horloge.getTempsSimule() + "] Client #" + id + " \"" + nom + "\" (pat=" + patienceInitiale + ")");
    }

    public Plat creerPlat(MenuPlat menuPlat) {
        switch (menuPlat) {
            case PIZZA:
                return new Plat(MenuPlat.PIZZA, "Pizza", 10, 8.5);
            case BURGER:
                return new Plat(MenuPlat.BURGER, "Burger", 8, 5.0);
            case FRITES:
                return new Plat(MenuPlat.FRITES, "Frites", 5, 2.5);
            default:
                throw new IllegalArgumentException("Plat inconnu : " + menuPlat);
        }
    }

    public void passerCommande(int idClient, String codePlat) {
        Client client = trouverClientParId(idClient);
        if (client == null) {
            logger.logLine("[🚪 t=" + horloge.getTempsSimule() + "] Client #" + idClient + " introuvable.");
            return;
        }

        MenuPlat menuPlat;
        try {
            menuPlat = MenuPlat.valueOf(codePlat.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.logLine("[🚪 t=" + horloge.getTempsSimule() + "] Plat inconnu : " + codePlat);
            return;
        }

        Plat plat = creerPlat(menuPlat);
        Commande commande = new Commande(client, plat);  // Crée une commande avec le plat
        commandesEnFile.add(commande);
        logger.logLine("[📥 t=" + horloge.getTempsSimule() + "] Cmd #" + commande.getId() + " (" + client.getNom() + ") → " + plat.getNom());
    }


    // retirerProchaineCommande(): Commande
    // marquerCommandeTerminee(Commande commande)
    private void marquerCommandeTerminee(Commande commande) {
        commandesEnPreparation.remove(commande);
        commande.setEtat(EtatCommande.LIVREE);
        stats.incrementerNbServis();
        stats.incrementerChiffreAffaires(commande.calculerMontant());
        logger.logLine("[✅ t=" + horloge.getTempsSimule() + "] Cmd #" + commande.getId() + " livrée.");
    }

    // Client creerClient(String nom, int patienceInitiale)
    public Client creerClient(String nom, int patienceInitiale) {
        Client client = new Client(clients.size() + 1, nom, patienceInitiale);
        clients.add(client);
        return client;
    }

    // Commande creerCommandePourClient(Client client)
    public Commande creerCommandePourClient(Client client) {
        Plat plat = new Plat(MenuPlat.PIZZA, "Pizza", 10, 8.5);  // Par défaut, créer une pizza
        Commande commande = new Commande(client, plat);
        commandesEnFile.add(commande);
        return commande;
    }

    // TODO : implémenter d'autres sous-méthodes qui seront appelées par les méthodes principales
    // Trouver un client par son ID
    private Client trouverClientParId(int id) {
        return clients.stream().filter(client -> client.getId() == id).findFirst().orElse(null);
    }

    //  pour améliorer la lisibilité des méthodes en les découpant au besoin (éviter les trés longues méthodes)
    //  exemple : on peut avoir une méthode diminuerPatienceClients()
    //  qui permet de diminuer la patience des clients (appelée par tick())
    private void diminuerPatienceClients(int minutes) {
        for (Client client : clients) {
            client.diminuerPatience(minutes);
            if (client.getPatience() <= 0) {
                client.setEtat(EtatClient.PARTI_FACHE);
            }
        }
    }
}
