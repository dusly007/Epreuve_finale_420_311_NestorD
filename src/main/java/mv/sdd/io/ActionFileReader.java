package mv.sdd.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Lecture du fichier d'actions
public class ActionFileReader {
    public static List<Action> readActions(String filePath) throws IOException {
        List<Action> actions = new ArrayList<>();

        // TODO : Ajouter le code qui permet de lire et parser un fichier d'actions

        //ouverture fichier en lecture
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            //lecture par ligne
            while ((line = br.readLine()) != null) {
                //enlever espaces de début fin
                line = line.trim();

                //ignorer lignes vides ou commentaires('#')
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                //parser transforme ligne -> Action
                Action action = ActionParser.parseLigne(line);

                // Ajouter dans liste actions
                actions.add(action);
            }
        } catch (IOException e) {
            //erreurs de lecture
            throw new IOException("Erreur lors de la lecture du fichier d'actions : " + e.getMessage(), e);
        }

        //liste actions lues
        return actions;


    }
}
