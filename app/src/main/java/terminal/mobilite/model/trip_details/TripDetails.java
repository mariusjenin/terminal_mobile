package terminal.mobilite.model.trip_details;

import android.content.Context;
import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.R;
import terminal.mobilite.manager.LocalizationManager;
import terminal.mobilite.model.reservation.Segment;
import terminal.mobilite.model.trip.TripParsed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Trajet détaillé pour un passager (Utilisé pour le parsing de JSON)
 */
public class TripDetails extends TripParsed {
    protected TreeMap<String, StopInTrip> stops_itinerary;
    protected String id_trip_or_reserv;
    protected int duration;
    protected String date;
    protected LatLng coords_driver;
    protected int pos_near_coords_driver;
    protected int prev_pos_near_coords_driver;
    protected boolean in_process;
    protected boolean coords_driver_defined;


    public TripDetails() {
        super();
        stops_itinerary = new TreeMap<>();
        in_process = true;
        coords_driver_defined = false;
        pos_near_coords_driver = 0;
        prev_pos_near_coords_driver = 0;
        LocalizationManager gpsTracker = TerminalApp.getGpsTracker();
        if (gpsTracker.canGetLocation()) {
            Location location = gpsTracker.getLocation();
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            coords_driver = new LatLng(lat, lon);
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    public void setStopsItinerary(TreeMap<String, StopInTrip> stops_itinerary) {
        this.stops_itinerary = stops_itinerary;
    }

    public void setCoordsDriver(LatLng c_d) {
        this.coords_driver = c_d;
        if (coords_driver_defined) {
            prev_pos_near_coords_driver = pos_near_coords_driver;
            pos_near_coords_driver = LocalizationManager.nearestLatLng(coords, coords_driver);
        } else {
            coords_driver_defined = true;
            pos_near_coords_driver = LocalizationManager.nearestLatLng(coords, coords_driver);
            prev_pos_near_coords_driver = pos_near_coords_driver;
        }
    }

    public int getPosNearCoordsDriver() {
        return pos_near_coords_driver;
    }

    public int getPrevPosNearCoordsDriver() {
        return prev_pos_near_coords_driver;
    }

    public void setInProcess(boolean in_process) {
        this.in_process = in_process;
    }

    public boolean isInProcess() {
        return in_process;
    }

    public LatLng getCoordsDriver() {
        return coords_driver;
    }

    public void setIdTripOrReserv(String id_trip_or_reserv) {
        this.id_trip_or_reserv = id_trip_or_reserv;
    }

    public String getIdTripOrReserv() {
        return id_trip_or_reserv;
    }

    public TreeMap<String, StopInTrip> getStopsItinerary() {
        return stops_itinerary;
    }

    public int[] getTripPos() {
        int num_segment_end = Integer.parseInt(Objects.requireNonNull(stops_itinerary.get(end_stop_pos + "")).getNum()) - 1;
        int end_pos = segment_list.get(num_segment_end).getEndStopPos() + 1;

        int start_pos;
        if (id_in_geo == -1) {
            int num_segment_start = Integer.parseInt(Objects.requireNonNull(stops_itinerary.get(start_stop_pos + "")).getNum());
            start_pos = segment_list.get(num_segment_start).getStartStopPos();
        } else {
            start_pos = id_in_geo;
        }
        return new int[]{start_pos, end_pos};
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String toString() {
        return
                "(id : " + id_trip_or_reserv +
                        ", name : " + name_itinerary +
                        ", start_stop_pos : " + start_stop_pos +
                        ", end_stop_pos : " + end_stop_pos +
                        ", stops_itinerary : " + stops_itinerary.toString() +
                        ", segment_list : " + segment_list.toString() +
                        ", coords : " + coords.toString() +
                        ")";
    }

    public String generateString() {
        Context c = TerminalApp.getContext();
        StopInTrip start_stop = Objects.requireNonNull(stops_itinerary.get(start_stop_pos + ""));
        StopInTrip end_stop = Objects.requireNonNull(stops_itinerary.get(end_stop_pos + ""));
        String time = "";
        int hour = duration / 60;
        int minute = duration % 60;
        if (hour > 0) {
            if (hour > 1) {
                time += hour + " " + c.getString(R.string.hour_plural);
            } else {
                time += hour + " " + c.getString(R.string.hour_singular);
            }
        }
        if (hour > 0 && minute > 0) {
            time += " ";
        }
        if (minute > 0) {
            if (minute > 1) {
                time += minute + " " + c.getString(R.string.min_plural);
            } else {
                time += minute + " " + c.getString(R.string.min_singular);
            }
        }
        String start_location;
        if (id_in_geo != -1) {
            start_location = c.getString(R.string.your_position);
        } else {
            start_location = start_stop.getName();
        }
        return c.getString(R.string.your_trip_of) + " " +
                date + " " +
                c.getString(R.string.will_last) + " " +
                time + " " +
                c.getString(R.string.from) + " " +
                start_location + " " +
                c.getString(R.string.at) + " " +
                start_stop.getTime() + " " +
                c.getString(R.string.up_to) + " " +
                end_stop.getName() + " " +
                c.getString(R.string.at) + " " +
                end_stop.getTime();
    }

    public String generateTitleString() {
        StopInTrip start_stop = Objects.requireNonNull(stops_itinerary.get(start_stop_pos + ""));
        StopInTrip end_stop = Objects.requireNonNull(stops_itinerary.get(end_stop_pos + ""));
        return name_itinerary + " - " + date + " - " + start_stop.getTime() + "/" + end_stop.getTime();
    }

    public int getTimeFromDriverToPassengers(int ssp, int esp, int cp) {
        if (cp < pos_near_coords_driver) return -1;
        try {

            TreeMap<String, StopInTrip> stops_itinerary = getStopsItinerary();
            SimpleDateFormat parser = new SimpleDateFormat(TerminalApp.getContext().getString(R.string.hour_format));

            Segment segment_ptd = getSegmentList().get(ssp);

            StopInTrip start_stop_ptd = stops_itinerary.get(ssp + "");
            StopInTrip end_stop_ptd = stops_itinerary.get(esp + "");

            String timeStartStop_ptd = Objects.requireNonNull(start_stop_ptd).getTime();
            String timeEndStop_ptd = Objects.requireNonNull(end_stop_ptd).getTime();

            Date firstDate_ptd = parser.parse(timeStartStop_ptd);
            Date secondDate_ptd = parser.parse(timeEndStop_ptd);
            assert secondDate_ptd != null;
            assert firstDate_ptd != null;
            long diffInMillies_ptd = Math.abs(secondDate_ptd.getTime() - firstDate_ptd.getTime());

            int from_ptd = segment_ptd.getStartStopPos();
            int to_ptd = segment_ptd.getEndStopPos();
            ArrayList<LatLng> latLngList_ptd = new ArrayList<>(getCoords().subList(from_ptd, to_ptd));

            int size_ptd = latLngList_ptd.size();
            int distAmount_ptd = 0;
            int dist_ptd = 0;
            LatLng prevLatLng = latLngList_ptd.get(0);
            for (int i = 0; i < size_ptd; i++) {
                LatLng currentLatLng = latLngList_ptd.get(i);
                distAmount_ptd += LocalizationManager.haversine(prevLatLng, currentLatLng);
                prevLatLng = currentLatLng;
                if (cp == i + from_ptd) {
                    dist_ptd = distAmount_ptd;
                }
            }
            if (distAmount_ptd == 0) distAmount_ptd = 1;

            int time_ptd = (int) ((dist_ptd * diffInMillies_ptd) / distAmount_ptd);


            Segment segment_driver = segment_list.get(0);

            int pos_start_stop_driver = 0;
            for (int i = 0; i < segment_list.size(); i++) {
                Segment s_driver = segment_list.get(i);
                if (s_driver.getStartStopPos() <= pos_near_coords_driver && s_driver.getEndStopPos() >= pos_near_coords_driver) {
                    segment_driver = s_driver;
                    pos_start_stop_driver = i;
                    break;
                }
            }

            StopInTrip start_stop_driver = stops_itinerary.get(pos_start_stop_driver + "");
            StopInTrip end_stop_driver = stops_itinerary.get(pos_start_stop_driver + 1 + "");

            String timeStartStop_driver = Objects.requireNonNull(start_stop_driver).getTime();
            String timeEndStop_driver = Objects.requireNonNull(end_stop_driver).getTime();

            Date firstDate_driver = parser.parse(timeStartStop_driver);
            Date secondDate_driver = parser.parse(timeEndStop_driver);
            assert secondDate_driver != null;
            assert firstDate_driver != null;
            long diffInMillies_driver = Math.abs(secondDate_driver.getTime() - firstDate_driver.getTime());


            int from_driver = segment_driver.getStartStopPos();
            int to_driver = segment_driver.getEndStopPos();
            ArrayList<LatLng> latLngList_driver = new ArrayList<>(getCoords().subList(from_driver, to_driver));

            int size_driver = latLngList_driver.size();
            int distAmount_driver = 0;
            int dist_driver = 0;
            LatLng prevLatLng_driver = latLngList_driver.get(size_driver - 1);
            for (int i = size_driver - 1; i >= 0; i--) {
                LatLng currentLatLng_driver = latLngList_driver.get(i);
                distAmount_driver += LocalizationManager.haversine(prevLatLng_driver, currentLatLng_driver);
                prevLatLng_driver = currentLatLng_driver;
                if (pos_near_coords_driver == i + from_driver) {
                    dist_driver = distAmount_driver;
                }
            }
            if (distAmount_driver == 0) distAmount_driver = 1;

            int time_driver = (int) ((dist_driver * diffInMillies_driver) / distAmount_driver);

            if (segment_ptd == segment_driver) {
                return (int) (time_ptd + time_driver - diffInMillies_driver);
            } else {
                return time_ptd + time_driver;
            }
        } catch (ParseException e) {
            //Impossible normally
        }
        return 0;
    }
}
