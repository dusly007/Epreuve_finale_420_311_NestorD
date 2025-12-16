package mv.sdd.sim.thread;

import mv.sdd.model.Horloge;
import mv.sdd.sim.Restaurant;
import mv.sdd.model.Commande;

public class Cuisinier implements Runnable {

    private final Restaurant restaurant;

    public Cuisinier(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        while (restaurant.getServiceActif().get()) {
            Commande commande = restaurant.retirerProchaineCommande();

            if (commande != null && commande.getTempsRestant() > 0) {
                demarrerCommande(commande);
            }

            attendre(10); // petite pause
        }
    }

    private void demarrerCommande(Commande commande) {
        commande.demarrerPreparation();
        restaurant.getEnPreparation().add(commande);
        restaurant.getLogger().logLine(
                String.format("[🍳 t=%d] Cmd #%d commence (%d min)",
                        restaurant.getHorloge().getTempsSimule(),
                        commande.getId(),
                        commande.getTempsRestant())
        );
    }

    private void attendre(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}