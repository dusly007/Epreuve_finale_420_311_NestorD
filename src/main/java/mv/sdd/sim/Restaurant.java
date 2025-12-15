package mv.sdd.sim;

import mv.sdd.io.Action;
import mv.sdd.model.*;
import mv.sdd.sim.thread.Cuisinier;
import mv.sdd.utils.Constantes;
import mv.sdd.utils.Logger;
import mv.sdd.utils.Formatter;
import mv.sdd.io.ActionType;
import java.util.Comparator;


import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Restaurant {
    private final Logger logger;
    // TODO : Ajouter les attributs nécessaires ainsi que les getters et les setters
    private final Horloge horloge = new Horloge();
    private final Map<Integer, Client> clients = new HashMap<>();
    private final Queue<Commande> fileCommandes = new ConcurrentLinkedQueue<>(); //file attente
    private final List<Commande> enPreparation = Collections.synchronizedList(new ArrayList<>()); //en preparation
    private final Stats stats = new Stats(horloge);
    private Thread threadCuisinier;
    private final AtomicBoolean serviceActif = new AtomicBoolean(false); //apres mes recherche sa rend lecture/ecriture indivisible

    // TODO : Ajouter le(s) constructeur(s)
    public Restaurant(Logger logger) {
        this.logger = logger;
    }

    public AtomicBoolean getServiceActif() { return serviceActif; }
    public Horloge getHorloge() { return horloge; }
    public Queue<Commande> getFileCommandes() { return fileCommandes; }
    public List<Commande> getEnPreparation() { return enPreparation; }
    public Logger getLogger() { return logger; }


    // TODO : implémenter les méthodes suivantes
    // Méthode appelée depuis App pour chaque action
    public synchronized void executerAction(Action action) {
        switch (action.getType()) {
            case DEMARRER_SERVICE -> demarrerService(action.getParam1(), action.getParam2());
            case AJOUTER_CLIENT -> ajouterClient(action.getParam1(), action.getParam3(), action.getParam2());
            case PASSER_COMMANDE -> passerCommande(action.getParam1(), action.getParam3());
            case AVANCER_TEMPS -> avancerTemps(action.getParam1());
            case AFFICHER_ETAT -> afficherEtat();
            case AFFICHER_STATS -> afficherStatistiques();
            case QUITTER -> arreterService();
        }
    }


    public void demarrerService(int dureeMax, int nbCuisiniers) {
        // Votre code ici.
        serviceActif.set(true);
        logger.logLine(String.format("[⏱️] Service = %d min, 👨‍🍳 = %d", dureeMax, nbCuisiniers));
        threadCuisinier = new Thread(new Cuisinier(this), "Cuisinier");
        threadCuisinier.start();
    }

    public void avancerTemps(int minutes) {
        // Votre code ici.
        logger.logLine(String.format("%s%d", Constantes.AVANCER_TEMPS, minutes));
        for (int i = 0; i < minutes; i++) {
            horloge.avancerTempsSimule(1);
            tick();
        }
    }

    public void arreterService(){
        // Votre code ici.
        if (!serviceActif.get()) {
            return;  //si service terminé -> ne fais rien
        }
        serviceActif.set(false);  // Arrête le cuisinier
        logger.logLine(String.format("[⏱️ t=%d] Service terminé.", horloge.getTempsSimule()));

        if (threadCuisinier != null && threadCuisinier.isAlive()) {
            try {
                threadCuisinier.join(1000);  //1s max
            } catch (InterruptedException e) {
                threadCuisinier.interrupt();
            }
        }
        logger.logLine(Constantes.FOOTER_APP);
    }

    // TODO : Déclarer et implémenter les méthodes suivantes
    // tick() avancer commande
    public void tick() {
        // 1. Diminuer la patience des clients avant tout (car chaque minute passe)
        synchronized (clients) {
            for (Client client : clients.values()) {
                if (client.getEtat() == EtatClient.EN_ATTENTE) {
                    client.diminuerPatience(1);
                    if (client.getEtat() == EtatClient.PARTI_FACHE) {
                        stats.incrementerNbFaches();
                        logger.logLine(Formatter.eventClientFache(horloge.getTempsSimule(), client));
                    }
                }
            }
        }

        // 2. Faire progresser les commandes en préparation
        synchronized (enPreparation) {
            for (Iterator<Commande> it = enPreparation.iterator(); it.hasNext();) {
                Commande cmd = it.next();
                cmd.decrementerTempsRestant(1);
                if (cmd.estTermineeParTemps()) {
                    marquerCommandeTerminee(cmd);
                    it.remove();
                }
            }
        }
    }



    // afficherEtat()
    public void afficherEtat() {
        int nbClientsPresents = (int) clients.values().stream()
                .filter(c -> c.getEtat() != EtatClient.PARTI_FACHE).count();
        long nbServis = clients.values().stream().filter(c -> c.getEtat() == EtatClient.SERVI).count();
        long nbFaches = clients.values().stream().filter(c -> c.getEtat() == EtatClient.PARTI_FACHE).count();

        logger.logLine(Formatter.resumeEtat(
                horloge.getTempsSimule(),
                nbClientsPresents,
                (int) nbServis,
                (int) nbFaches,
                fileCommandes.size(),
                enPreparation.size()
        ));

        // Une ligne par client
        synchronized (clients) {
            clients.values().stream()
                    .filter(c -> c.getEtat() != EtatClient.PARTI_FACHE)
                    .sorted(Comparator.comparingInt(Client::getId))
                    .forEach(c -> logger.logLine(Formatter.clientLine(c, c.getCommande())));
        }
    }

    // afficherStatistiques()
    public void afficherStatistiques() {
        logger.logLine(Constantes.HEADER_AFFICHER_STATS);
        logger.logLine(stats.toString());
    }

    // Client ajouterClient(int id, String nom, int patienceInitiale)
    public Client ajouterClient(int id, String nom, int patienceInitiale) {
        Client client = new Client(id, nom, patienceInitiale);
        synchronized (clients) {
            clients.put(id, client);
        }
        stats.incrementerTotalClients();
        logger.logLine(Formatter.eventArriveeClient(horloge.getTempsSimule(), client));
        return client;
    }



    public Commande passerCommande(int idClient, String codePlat) {
        MenuPlat plat = MenuPlat.valueOf(codePlat);
        Client client;
        synchronized (clients) {
            client = clients.get(idClient);
        }
        if (client == null || client.getEtat() != EtatClient.EN_ATTENTE) return null;

        Commande commande = client.getCommande();
        if (commande == null) {
            commande = new Commande(client);
            client.setCommande(commande);
            commande.ajouterPlat(plat);
            fileCommandes.add(commande);
            logger.logLine(Formatter.eventCommandeCree(
                    horloge.getTempsSimule(), commande.getId(), client, plat));
        } else {
            commande.ajouterPlat(plat);
        }
        return commande;
    }
    // retirerProchaineCommande(): Commande
    public Commande retirerProchaineCommande() {
        return fileCommandes.poll();
    }

    // marquerCommandeTerminee(Commande commande)
    public void marquerCommandeTerminee(Commande commande) {
        if (commande.getEtat() == EtatCommande.LIVREE) {
            return;
        }

        commande.setEtat(EtatCommande.LIVREE);
        Client client = commande.getClient();
        client.setEtat(EtatClient.SERVI);
        stats.incrementerNbServis();
        stats.incrementerChiffreAffaires(commande.calculerMontant());
        for (MenuPlat plat : commande.getPlats()) {
            stats.incrementerVentesParPlat(plat);
        }
        logger.logLine(Formatter.eventCommandeTerminee(
                horloge.getTempsSimule(), commande.getId(), client));
    }


    // Client creerClient(String nom, int patienceInitiale)
    public Client creerClient(String nom, int patienceInitiale) {
        int id = (int) clients.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;        Client client = new Client(id, nom, patienceInitiale);
        synchronized (clients) {
            clients.put(id, client);
        }
        return client;
    }

    // Commande creerCommandePourClient(Client client)
    public Commande creerCommandePourClient(Client client) {
        return new Commande(client);
    }


    // TODO : implémenter d'autres sous-méthodes qui seront appelées par les méthodes principales
    // Trouver un client par son ID

    //  pour améliorer la lisibilité des méthodes en les découpant au besoin (éviter les trés longues méthodes)
    //  exemple : on peut avoir une méthode diminuerPatienceClients()
    //  qui permet de diminuer la patience des clients (appelée par tick())


}
