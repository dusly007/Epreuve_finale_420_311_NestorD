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


        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            //par ligne
            while ((line = br.readLine()) != null) {
                //enlever espaces
                line = line.trim();

                //ignorer lignes vides ou '#'
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }


                Action action = ActionParser.parseLigne(line);


                actions.add(action);
            }
        } catch (IOException e) {
            //erreurs
            throw new IOException("Erreur lors de la lecture du fichier d'actions : " + e.getMessage(), e);
        }


        return actions;


    }
}
