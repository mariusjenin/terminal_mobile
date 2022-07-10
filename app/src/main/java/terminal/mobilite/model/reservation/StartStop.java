package terminal.mobilite.model.reservation;

import androidx.annotation.NonNull;
import terminal.mobilite.model.trip.Stop;

/**
 * Premier arrêt possible d'une réservation (Utilisé pour le parsing de JSON)
 */
public class StartStop extends Stop {
    public StartStop(String i, String n, String nu) {
        super(i, n, nu);
    }

    @NonNull
    @Override
    public String toString() {
        return "(id : "+id+", name : "+name+")";
    }

    public String getId() {
        return id;
    }


    public String getNum() {
        return num;
    }
}
