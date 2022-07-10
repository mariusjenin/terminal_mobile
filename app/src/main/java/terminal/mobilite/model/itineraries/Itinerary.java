package terminal.mobilite.model.itineraries;

import androidx.annotation.NonNull;

import java.util.ArrayList;
/**
 * Itineraire (Utilisé pour le parsing de JSON)
 */
public class Itinerary implements Comparable<Itinerary> {
    private final String id_itineraire;
    private final String nom_itineraire;
    private final ArrayList<StopInItineray> stops;
    private final ArrayList<TripInItineray> trips;

    public Itinerary(String id_i, String nom_i, ArrayList<StopInItineray> s, ArrayList<TripInItineray> t) {
        id_itineraire = id_i;
        nom_itineraire = nom_i;
        stops = s;
        trips = t;
    }

    public ArrayList<TripInItineray> getTripOfRepetition(String repetition){
        ArrayList<TripInItineray> trips_repetition = new ArrayList<>();
        for (TripInItineray trip: trips) {
            if(repetition.equals(trip.getRepetition())){
                trips_repetition.add(trip);
            }
        }
        return trips_repetition;
    }

    public boolean hasTrips(){
        return trips.size()>0;
    }

    @Override
    public int compareTo(Itinerary i) {
        return id_itineraire.compareTo(i.id_itineraire);
    }

    public String getId_itineraire() {
        return id_itineraire;
    }

    public String getNom() {
        return nom_itineraire;
    }

    public ArrayList<StopInItineray> getStops() {
        return stops;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ id_itineraire : "+id_itineraire+
                ", nom_itineraire : "+nom_itineraire+
                ", stops : "+stops+
                ", trips : "+trips+"}";
    }
}

