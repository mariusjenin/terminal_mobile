package terminal.mobilite.model.reservation;

import android.annotation.SuppressLint;
import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.R;
import terminal.mobilite.manager.LocalizationManager;
import terminal.mobilite.model.trip.TripParsed;
import terminal.mobilite.model.trip.ItineraryStop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Reservation d'un trajet (Utilisé pour le parsing de JSON)
 */
public class ReservationTrip extends TripParsed {

    private TreeMap<String, ItineraryStop> stops_itinerary;
    private final String id_itinerary;
    private int year;
    private int month;
    private int day;
    private boolean to_my_position;
    private boolean travel_alone;
    private int nb_places_pos;
    private int hour_trip_pos;
    private boolean give_coords;
    private boolean enabled;

    //List for Spinner
    private ArrayList<StartStop> startStopList;
    private ArrayList<HourTrip> hourTripList;
    private ArrayList<EndStop> endStopList;
    private ArrayList<String> nbPlacesList;


    public ReservationTrip(String id, String n_it) {
        super();
        id_itinerary = id;
        name_itinerary = n_it;

        give_coords = true;
        enabled = false;
        to_my_position = false;
        travel_alone = false;
        nb_places_pos = -1;
        hour_trip_pos = -1;
        startStopList = new ArrayList<>();
        hourTripList = new ArrayList<>();
        endStopList = new ArrayList<>();
        nbPlacesList = new ArrayList<>();
        initDate();
    }

    public boolean isGiveCoords() {
        return give_coords;
    }

    public void setGiveCoords(boolean b) {
        give_coords = b;
    }

    public String getIdItinerary() {
        return id_itinerary;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isToMyPosition() {
        return to_my_position;
    }

    public void setToMyPosition(boolean to_my_position) {
        this.to_my_position = to_my_position;
    }

    public boolean isTravelAlone() {
        return travel_alone;
    }

    public void setTravelAlone(boolean travel_alone) {
        this.travel_alone = travel_alone;
    }

    public int getNbPlacesPos() {
        return nb_places_pos;
    }

    public void setNbPlacesPos(int nb_places_pos) {
        this.nb_places_pos = nb_places_pos;
    }

    public int getHourTripPos() {
        return hour_trip_pos;
    }

    public void setHourTripPos(int hour_trip_pos) {
        this.hour_trip_pos = hour_trip_pos;
    }

    public void initDate() {
        Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
    }

    public boolean notChangedTravelAlone() {
        return !travel_alone;
    }

    public boolean notChangedNbPlacesPos() {
        return nb_places_pos == -1;
    }

    public boolean notChangedHourTripPos() {
        return hour_trip_pos == -1;
    }


    public void setStartStopList(ArrayList<StartStop> start_stops) {
        startStopList = start_stops;
    }

    public void setHourTripList(ArrayList<HourTrip> hours_trip) {
        hourTripList = hours_trip;
    }

    public void setEndStopList(ArrayList<EndStop> end_stops) {
        endStopList = end_stops;
    }

    public void setNbPlacesList(ArrayList<String> free_places_max) {
        nbPlacesList = free_places_max;
    }

    public ArrayList<StartStop> getStartStopList() {
        return startStopList;
    }

    public TreeMap<String, ItineraryStop> getStopsItinerary() {
        return stops_itinerary;
    }

    public ArrayList<HourTrip> getHourTripList() {
        return hourTripList;
    }

    public ArrayList<EndStop> getEndStopList() {
        return endStopList;
    }

    public ArrayList<String> getNbPlacesList() {
        return nbPlacesList;
    }

    public String getDateForDisplay() {
        StringBuilder day;
        StringBuilder month;
        StringBuilder year;
        if(to_my_position){
            Calendar c = Calendar.getInstance();
            day = new StringBuilder("" + c.get(Calendar.DAY_OF_MONTH));
            month = new StringBuilder("" + (c.get(Calendar.MONTH) + 1));
            year = new StringBuilder("" + c.get(Calendar.YEAR));

        } else {
            day = new StringBuilder("" + getDay());
            month = new StringBuilder("" + (getMonth() + 1));
            year = new StringBuilder("" + getYear());
        }

        for (int i = 0; i < 2 - day.length(); i++) {
            day.insert(0, "0");
        }
        for (int i = 0; i < 2 - month.length(); i++) {
            month.insert(0, "0");
        }
        for (int i = 0; i < 4 - year.length(); i++) {
            year.insert(0, "0");
        }
        return day + "/" + month + "/" + year;
    }

    public String getDateForRequest() {
        StringBuilder day = new StringBuilder("" + getDay());
        for (int i = 0; i < 2 - day.length(); i++) {
            day.insert(0, "0");
        }
        StringBuilder month = new StringBuilder("" + (getMonth() + 1));
        for (int i = 0; i < 2 - month.length(); i++) {
            month.insert(0, "0");
        }
        StringBuilder year = new StringBuilder("" + getYear());
        for (int i = 0; i < 4 - year.length(); i++) {
            year.insert(0, "0");
        }
        return year + "-" + month + "-" + day;
    }

    public void setStopsItineraryList(TreeMap<String, ItineraryStop> s_i) {
        stops_itinerary = s_i;
    }

    public int[] getTripPos() {


        int num_segment_end = Integer.parseInt(endStopList.get(end_stop_pos).getNum()) - 1;
        int end_pos = segment_list.get(num_segment_end).getEndStopPos() + 1;

        int start_pos;
        if (notChangedIdInGeo()) {
            int num_segment_start = Integer.parseInt(startStopList.get(start_stop_pos).getNum());
            start_pos = segment_list.get(num_segment_start).getStartStopPos();
        } else {
            start_pos = id_in_geo;
        }
        return new int[]{start_pos, end_pos};
    }

    @SuppressLint("SimpleDateFormat")
    public String getTimeStart(String time_str) {
        try{
            SimpleDateFormat formatter = new SimpleDateFormat(TerminalApp.getContext().getString(R.string.hour_format), Locale.getDefault());
            Date d1 = formatter.parse(time_str);
            assert d1 != null;
            long time = d1.getTime();
            Segment seg = segment_list.get(0);
            for (Segment s : segment_list) {
                if (s.getStartStopPos() <= id_in_geo && s.getEndStopPos() >= id_in_geo) {
                    seg = s;
                    break;
                }
            }
            LatLng start_stop_coords = Objects.requireNonNull(stops_itinerary.get(start_stop_pos+"")).getCoords();
            int duree = (int) (seg.getDuration() * LocalizationManager.haversine(start_stop_coords, coords.get(id_in_geo)) / seg.getDistance());

            time = time + duree * 1000L;
            d1.setTime(time);
            return formatter.format(d1);
        } catch (ParseException pe){
            //ERREUR ParseException (not possible normally)
            return "";
        }
    }

    public int getDistFromPosition() {
        if (!notChangedIdInGeo()) {
            LocalizationManager gpsTracker = TerminalApp.getGpsTracker();
            if (gpsTracker.canGetLocation()) {
                return LocalizationManager.haversine(gpsTracker.getLatLng(), coords.get(id_in_geo));
            } else {
                gpsTracker.showSettingsAlert();
            }
        }
        return -1;
    }

    public String getMsgReservation() {
        Context c = TerminalApp.getContext();
        EndStop e_s = endStopList.get(end_stop_pos);
        int nbplace = Integer.parseInt(nbPlacesList.get(nb_places_pos));
        String place = nbplace > 1 ? c.getString(R.string.place_plural) : c.getString(R.string.place_singular);
        String shared_trip;
        String start;
        if (travel_alone) {
            shared_trip = c.getString(R.string.not_shared_trip);
        } else {
            shared_trip = c.getString(R.string.shared_trip);
        }

        if (to_my_position) {
            start = TerminalApp.getContext().getString(R.string.your_position);
            if (!notChangedIdInGeo()) {
                start = start + " "+c.getString(R.string.at)+" " + getTimeStart(hourTripList.get(hour_trip_pos).getHourStart());
            }
        } else {
            start = startStopList.get(start_stop_pos).getName() + " "+c.getString(R.string.at)+" " + hourTripList.get(hour_trip_pos).getHourStart();
        }

        return nbplace +
                " " +
                place +
                " "+c.getString(R.string.from)+" " +
                start +
                " "+c.getString(R.string.up_to)+" " +
                e_s.getName() +
                " "+c.getString(R.string.at)+" " +
                e_s.getHourEnd() +
                "\n" +
                shared_trip;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean b) {
        enabled = b;
    }
}
