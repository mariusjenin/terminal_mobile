package terminal.mobilite.model.itineraries;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Trajet d'un itinéraire (Utilisé pour le parsing de JSON)
 */
public class TripInItineray {
    public static final String UNIQUE_REPETITION = "UNIQUE";
    public static final String HEBDOMADAIRE_REPETITION = "HEBDOMADAIRE";
    public static final String MENSUEL_REPETITION = "MENSUEL";
    public static final String ANNUEL_REPETITION = "ANNUEL";
    public static final String OUVRES_REPETITION = "OUVRES";
    public static final String WEEKEND_REPETITION = "WEEKEND";

    private final String repetition;
    private final String date_depart;
    private final ArrayList<TimeStopInItineray> time_passage;

    public TripInItineray(String r, String dp, ArrayList<TimeStopInItineray> tp) {
        repetition = r;
        date_depart = dp;
        time_passage = tp;
    }

    public String getRepetition() {
        return repetition;
    }

    public String getDateDepart() {
        return date_depart;
    }

    public ArrayList<TimeStopInItineray> getTimePassage() {
        return time_passage;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ repetition : "+repetition+", date_depart : "+date_depart+", time_passage : "+time_passage+"}";
    }
}