package terminal.mobilite.model.trip;

import com.google.android.gms.maps.model.LatLng;
import terminal.mobilite.model.reservation.Segment;

import java.util.ArrayList;

/**
 * Trajet (Utilisé pour le parsing de JSON)
 */
public abstract class TripParsed {
    protected ArrayList<LatLng> coords;
    protected ArrayList<Segment> segment_list;
    protected String name_itinerary;
    protected int start_stop_pos;
    protected int end_stop_pos;
    protected int id_in_geo;

    public TripParsed() {
        name_itinerary = "";
        start_stop_pos = -1;
        end_stop_pos = -1;
        id_in_geo = -1;
        segment_list = new ArrayList<>();
        coords = new ArrayList<>();
    }


    public boolean notChangedStartStopPos() {
        return start_stop_pos == -1;
    }
    public boolean notChangedEndStopPos() {
        return end_stop_pos == -1;
    }
    public boolean notChangedIdInGeo() {
        return id_in_geo == -1;
    }

    public ArrayList<LatLng> getCoords() {
        return coords;
    }

    public void setCoords(ArrayList<LatLng> coords) {
        this.coords = coords;
    }

    public ArrayList<Segment> getSegmentList() {
        return segment_list;
    }

    public void setSegmentList(ArrayList<Segment> segment_list) {
        this.segment_list = segment_list;
    }

    public String getNameItinerary() {
        return name_itinerary;
    }

    public void setNameItinerary(String name_itinerary) {
        this.name_itinerary = name_itinerary;
    }

    public int getStartStopPos() {
        return start_stop_pos;
    }

    public void setStartStopPos(int start_stop_pos) {
        this.start_stop_pos = start_stop_pos;
    }

    public int getEndStopPos() {
        return end_stop_pos;
    }

    public void setEndStopPos(int end_stop_pos) {
        this.end_stop_pos = end_stop_pos;
    }

    public int getIdInGeo() {
        return id_in_geo;
    }

    public void setIdInGeo(int id_in_geo) {
        this.id_in_geo = id_in_geo;
    }
}
