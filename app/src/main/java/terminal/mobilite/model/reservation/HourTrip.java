package terminal.mobilite.model.reservation;

import androidx.annotation.NonNull;

/**
 * Heure possible de départ d'une reservation (Utilisé pour le parsing de JSON)
 */
public class HourTrip {
    private final String id;
    private final String hour_start;

    public HourTrip(String i, String hs){
        id=i;
        hour_start=hs;
    }


    @NonNull
    @Override
    public String toString() {
        return "(id : "+id+", hour_start : "+hour_start+")";
    }

    public String toItemString(){
        return hour_start;
    }

    public String getId() {
        return id;
    }

    public String getHourStart() {
        return hour_start;
    }
}
