package terminal.mobilite.model.itineraries;

import androidx.annotation.NonNull;

/**
 * horaire d'un arrêt d'un itineraire dans un trajet (Utilisé pour le parsing de JSON)
 */
public class TimeStopInItineray {
    private final String num_stop;
    private final String time;

    public TimeStopInItineray(String ns, String t) {
        num_stop = ns;
        time = t;
    }

    public String getTime() {
        return time;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ num_stop : "+num_stop+", time : "+time+"}";
    }
}
