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
            Commande cmd = restaurant.retirerProchaineCommande();
            if (cmd != null && cmd.getTempsRestant() > 0) {
                cmd.demarrerPreparation();
                restaurant.getEnPreparation().add(cmd);
                restaurant.getLogger().logLine(String.format(
                        "[🍳 t=%d] Cmd #%d commence (%d min)",
                        restaurant.getHorloge().getTempsSimule(),
                        cmd.getId(), cmd.getTempsRestant()));
            }
            try {
                Thread.sleep(10);  // Très réactif
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}