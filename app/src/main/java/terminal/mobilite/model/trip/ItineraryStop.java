package terminal.mobilite.model.trip;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import terminal.mobilite.model.trip.Stop;

/**
 * Arrêt d'un itineraire (Utilisé pour le parsing de JSON pour l'affichage des planning)
 */
public class ItineraryStop extends Stop {
    private final LatLng coords;

    public ItineraryStop(String i, String n, String nu,LatLng c) {
        super(i, n, nu);
        coords=c;
    }

    @NonNull
    @Override
    public String toString() {
        return "(id : "+id+", name : "+name+", num : "+num+")";
    }

    public String toItemString(){
        return name;
    }

    public String getId() {
        return id;
    }


    public LatLng getCoords() {
        return coords;
    }
}
