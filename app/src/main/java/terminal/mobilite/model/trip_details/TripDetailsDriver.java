package terminal.mobilite.model.trip_details;

import java.util.ArrayList;

/**
 * Trajet détaillé pour un conducteur (Utilisé pour le parsing de JSON)
 */
public class TripDetailsDriver extends TripDetails {
    private ArrayList<PassengerTripDriver> passengers;

    public TripDetailsDriver(){
        super();
        passengers = new ArrayList<>();
    }

    public ArrayList<PassengerTripDriver> getPassengersFuture() {
        ArrayList<PassengerTripDriver> res = new ArrayList<>();
        for (PassengerTripDriver p : passengers) {
            if (pos_near_coords_driver <= p.getCoordsPos()) {
                res.add(p);
            }
        }
        return res;
    }

    public void setPassengers(ArrayList<PassengerTripDriver> passengers) {
        this.passengers = passengers;
    }
}
