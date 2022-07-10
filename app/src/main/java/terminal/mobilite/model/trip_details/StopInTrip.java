package terminal.mobilite.model.trip_details;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import terminal.mobilite.model.trip.Stop;

/**
 * Arrêt d'un trajet et horaire à cet arrêt (Utilisé pour le parsing de JSON)
 */
public class StopInTrip extends Stop {
    private final LatLng coords;
    private final String time;

    public StopInTrip(String i, String n, String nu,LatLng c, String t) {
        super(i, n, nu);
        coords=c;
        time=t;
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

    public LatLng getCoords() {
        return coords;
    }

    public String getTime() {
        return time;
    }
}
