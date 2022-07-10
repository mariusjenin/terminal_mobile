package terminal.mobilite.model.reservation;

import androidx.annotation.NonNull;
import terminal.mobilite.model.trip.Stop;

/**
 * Dernier arrêt possible d'une réservation (Utilisé pour le parsing de JSON)
 */
public class EndStop extends Stop {
    private final String hour_end;
    private final String free_places;

    public EndStop(String i, String n, String he, String fp,String nu) {
        super(i,n,nu);
        hour_end = he;
        free_places = fp;
    }


    @NonNull
    @Override
    public String toString() {
        return "(id : "+id
                +", name : "+name
                +", hour_end : "+hour_end
                +", free_places : "+free_places+")";
    }


    public String toItemString(){
        return name+" - "+hour_end;
    }

    public String getId() {
        return id;
    }

    public String getNum() {
        return num;
    }

    public String getHourEnd() {
        return hour_end;
    }
}
