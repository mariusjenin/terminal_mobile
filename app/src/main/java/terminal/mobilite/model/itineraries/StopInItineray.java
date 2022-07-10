package terminal.mobilite.model.itineraries;

import androidx.annotation.NonNull;

/**
 * Arrêt d'un itineraire (Utilisé pour le parsing de JSON pour l'affichage des planning)
 */
public class StopInItineray {
    private final String id_arret;
    private final String nom_arret;

    public StopInItineray(String id, String na) {
        id_arret = id;
        nom_arret = na;
    }

    public String getNom_arret() {
        return nom_arret;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ id_arret : "+id_arret+", nom_arret : "+nom_arret+"}";
    }
}
