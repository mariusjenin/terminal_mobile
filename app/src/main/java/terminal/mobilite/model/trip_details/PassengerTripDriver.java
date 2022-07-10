package terminal.mobilite.model.trip_details;


/**
 * Passager d'un trajet en dehors des arrêts (Utilisé pour le parsing de JSON)
 */
public class PassengerTripDriver {
    private final int coords_pos;
    private final int nb_min;
    private final int start_stop_pos;
    private final int end_stop_pos;
    private final String start_stop_name;
    private final String end_stop_name;
    private final TripDetails trip_details;

    public PassengerTripDriver(int cp, int nm, int ssp, int esp, String ss, String es, TripDetails td) {
        coords_pos = cp;
        nb_min = nm;
        start_stop_pos = ssp;
        end_stop_pos = esp;
        start_stop_name = ss;
        end_stop_name = es;
        trip_details = td;
    }

    public TripDetails getTripDetails() {
        return trip_details;
    }

    public int getStartStopPos() {
        return start_stop_pos;
    }

    public int getCoordsPos() {
        return coords_pos;
    }

    public String getStartStopName() {
        return start_stop_name;
    }

    public String getEndStopName() {
        return end_stop_name;
    }

    public int getEndStopPos() {
        return end_stop_pos;
    }
}
