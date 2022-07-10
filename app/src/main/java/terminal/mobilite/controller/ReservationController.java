package terminal.mobilite.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.TerminalActivity;
import terminal.mobilite.activity.operational.HomeActivity;
import terminal.mobilite.activity.operational.ReservationActivity;
import terminal.mobilite.manager.ConnectionManager;
import terminal.mobilite.manager.InternetConnectionManager;
import terminal.mobilite.manager.LocalizationManager;
import terminal.mobilite.manager.HttpRequest;
import terminal.mobilite.model.reservation.*;
import terminal.mobilite.model.trip.ItineraryStop;
import terminal.mobilite.model.trip_details.TripDetails;

import java.util.*;

public class ReservationController extends Controller {
    private static ReservationController INSTANCE;

    private ReservationController() {
    }

    public static ReservationController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReservationController();
        }
        return INSTANCE;
    }

    @SuppressLint("StaticFieldLeak")
    public void post_reservation_ongoing(ReservationActivity act, ReservationTrip reserv) {

        act.showProgress();
        if (InternetConnectionManager.checkConnection(act)) {
            new AsyncTask<Void, String, String>() {

                /**
                 * Launching the POST request to the server (reservation-ongoing)
                 * @param voids no parameters
                 * @return the response of the request (normally json)
                 */
                @Override
                protected String doInBackground(Void... voids) {
                    String result;


                    //params and header
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("id_itinerary", reserv.getIdItinerary());
                    if (!reserv.notChangedNbPlacesPos()) {
                        params.put("nb_place", reserv.getNbPlacesList().get(reserv.getNbPlacesPos()));
                    } else {
                        params.put("nb_place", "1");
                    }
                    if (!reserv.notChangedTravelAlone()) {
                        params.put("travel_alone", reserv.isTravelAlone() ? "1" : "0");
                    } else {
                        params.put("travel_alone", "0");
                    }
                    if (!reserv.notChangedTravelAlone()) {
                        params.put("travel_alone", reserv.isTravelAlone() ? "1" : "0");
                    } else {
                        params.put("travel_alone", "0");
                    }
                    params.put("give_coords", reserv.isGiveCoords() ? "1" : "0");

                    params.put("date", reserv.getDateForRequest());

                    if (reserv.isToMyPosition()) {
                        LocalizationManager gpsTracker = TerminalApp.getGpsTracker();
                        if (gpsTracker.canGetLocation()) {
                            Location l = gpsTracker.getLocation();
                            params.put("latitude", l.getLatitude());
                            params.put("longitude", l.getLongitude());
                        } else {
                            gpsTracker.showSettingsAlert();
                        }
                    } else {
                        if (!reserv.notChangedStartStopPos()) {
                            params.put("start_stop", reserv.getStartStopList().get(reserv.getStartStopPos()).getId());
                        }
                    }
                    if (!reserv.notChangedHourTripPos()) {
                        params.put("trip", reserv.getHourTripList().get(reserv.getHourTripPos()).getId());
                    }
                    if (!reserv.notChangedEndStopPos()) {
                        params.put("end_stop", reserv.getEndStopList().get(reserv.getEndStopPos()).getId());
                    }

                    HashMap<String, Object> header = new HashMap<>();

                    //URL
                    String url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/reservation-ongoing";

                    //Doing the request
                    HttpRequest req = new HttpRequest(url_request, params, header);
                    result = req.post();

                    return result;
                }

                /**
                 * Processing of the response of the POST request (reservation-ongoing)
                 * @param res response of the GET request
                 */
                @Override
                protected void onPostExecute(String res) {
                    super.onPostExecute(res);
                    response_str = act.getString(R.string.error_recuperation_data_on_server);
                    try {
                        JSONObject json_results = ReservationController.this.parse(res);

                        if (json_results != null &&
                                json_results.containsKey("fields") &&
                                json_results.containsKey("selected")) {
                            JSONObject fields_json = (JSONObject) json_results.get("fields");
                            JSONObject selected_json = (JSONObject) json_results.get("selected");

                            if (selected_json != null &&
                                    selected_json.containsKey("start_stops") &&
                                    selected_json.containsKey("hour_trip") &&
                                    selected_json.containsKey("end_stop")) {
                                String start_stops_selected = (String) selected_json.get("start_stops");
                                String hour_trip_selected = (String) selected_json.get("hour_trip");
                                String end_stop_selected = (String) selected_json.get("end_stop");

                                if (selected_json.containsKey("id_in_geo")) {
                                    String id_in_geo = (String) selected_json.get("id_in_geo");
                                    assert id_in_geo != null;
                                    reserv.setIdInGeo(Integer.parseInt(id_in_geo));
                                } else {
                                    reserv.setIdInGeo(-1);
                                }
                                assert start_stops_selected != null;
                                reserv.setStartStopPos(Integer.parseInt(start_stops_selected));
                                assert hour_trip_selected != null;
                                reserv.setHourTripPos(Integer.parseInt(hour_trip_selected));
                                assert end_stop_selected != null;
                                reserv.setEndStopPos(Integer.parseInt(end_stop_selected));
                                if (reserv.getNbPlacesPos() == -1) {
                                    reserv.setNbPlacesPos(0);
                                }
                            }

                            if (fields_json != null &&
                                    fields_json.containsKey("start_stops") &&
                                    fields_json.containsKey("hour_trip") &&
                                    fields_json.containsKey("end_stops") &&
                                    fields_json.containsKey("free_places_max") &&
                                    fields_json.containsKey("stops_itinerary")) {
                                JSONObject start_stops_json = (JSONObject) fields_json.get("start_stops");
                                JSONObject hours_trip_json = (JSONObject) fields_json.get("hour_trip");
                                JSONObject end_stops_json = (JSONObject) fields_json.get("end_stops");
                                JSONObject stops_itinerary_json = (JSONObject) fields_json.get("stops_itinerary");
                                String free_places_max = (String) fields_json.get("free_places_max");

                                ArrayList<StartStop> start_stops = new ArrayList<>();
                                TreeMap<String, ItineraryStop> stops_itinerary = new TreeMap<>();
                                ArrayList<HourTrip> hours_trip = new ArrayList<>();
                                ArrayList<EndStop> end_stops = new ArrayList<>();

                                assert stops_itinerary_json != null;
                                for (Object index_stops_itinerary : stops_itinerary_json.keySet()) {
                                    JSONObject stop_itinerary_json = (JSONObject) stops_itinerary_json.get(index_stops_itinerary);
                                    if (stop_itinerary_json != null &&
                                            stop_itinerary_json.containsKey("id_stop") &&
                                            stop_itinerary_json.containsKey("name_stop") &&
                                            stop_itinerary_json.containsKey("num_stop") &&
                                            stop_itinerary_json.containsKey("latitude") &&
                                            stop_itinerary_json.containsKey("longitude")) {
                                        stops_itinerary.put(
                                                (String) stop_itinerary_json.get("num_stop"),
                                                new ItineraryStop(
                                                        (String) stop_itinerary_json.get("id_stop"),
                                                        (String) stop_itinerary_json.get("name_stop"),
                                                        (String) stop_itinerary_json.get("num_stop"),
                                                        new LatLng(
                                                                Double.parseDouble((String) Objects.requireNonNull(stop_itinerary_json.get("latitude"))),
                                                                Double.parseDouble((String) Objects.requireNonNull(stop_itinerary_json.get("longitude")))
                                                        )
                                                ));
                                    }
                                }

                                assert start_stops_json != null;
                                for (Object index_start_stops : start_stops_json.keySet()) {
                                    JSONObject start_stop_json = (JSONObject) start_stops_json.get(index_start_stops);
                                    if (start_stop_json != null &&
                                            start_stop_json.containsKey("id_stop") &&
                                            start_stop_json.containsKey("name_stop") &&
                                            start_stop_json.containsKey("num_stop")) {
                                        start_stops.add(new StartStop(
                                                (String) start_stop_json.get("id_stop"),
                                                (String) start_stop_json.get("name_stop"),
                                                (String) start_stop_json.get("num_stop")
                                        ));
                                    }
                                }

                                assert hours_trip_json != null;
                                for (Object index_hours_trip : hours_trip_json.keySet()) {
                                    JSONObject hour_trip_json = (JSONObject) hours_trip_json.get(index_hours_trip);
                                    if (hour_trip_json != null &&
                                            hour_trip_json.containsKey("id_trip") &&
                                            hour_trip_json.containsKey("hour_start")) {
                                        hours_trip.add(new HourTrip(
                                                (String) hour_trip_json.get("id_trip"),
                                                (String) hour_trip_json.get("hour_start")
                                        ));
                                    }
                                }

                                assert end_stops_json != null;
                                for (Object index_end_stops : end_stops_json.keySet()) {
                                    JSONObject end_stop_json = (JSONObject) end_stops_json.get(index_end_stops);
                                    if (end_stop_json != null &&
                                            end_stop_json.containsKey("id_stop") &&
                                            end_stop_json.containsKey("name_stop") &&
                                            end_stop_json.containsKey("hour_end") &&
                                            end_stop_json.containsKey("free_places") &&
                                            end_stop_json.containsKey("num_stop")) {
                                        end_stops.add(new EndStop(
                                                (String) end_stop_json.get("id_stop"),
                                                (String) end_stop_json.get("name_stop"),
                                                (String) end_stop_json.get("hour_end"),
                                                (String) end_stop_json.get("free_places"),
                                                (String) end_stop_json.get("num_stop")
                                        ));
                                    }
                                }
                                reserv.setStopsItineraryList(stops_itinerary);
                                reserv.setStartStopList(start_stops);
                                reserv.setHourTripList(hours_trip);
                                reserv.setEndStopList(end_stops);
                                ArrayList<String> nbPlacesList = new ArrayList<>();
                                for (int i = 1; i <= Integer.parseInt(Objects.requireNonNull(free_places_max)); i++) {
                                    nbPlacesList.add(i + "");
                                }
                                reserv.setNbPlacesList(nbPlacesList);
                            }
                            if (fields_json != null &&
                                    fields_json.containsKey("itinerary_geo")) {
                                JSONObject itinerary_geo_json = (JSONObject) fields_json.get("itinerary_geo");
                                if (itinerary_geo_json != null &&
                                        itinerary_geo_json.containsKey("response")) {

                                    JSONObject response_geo_json = (JSONObject) itinerary_geo_json.get("response");
                                    if (response_geo_json != null &&
                                            response_geo_json.containsKey("coordinates") &&
                                            response_geo_json.containsKey("segments")) {

                                        JSONObject coordinates_geo_json = (JSONObject) response_geo_json.get("coordinates");
                                        TreeMap<Integer, LatLng> itinerary_geo = new TreeMap<>();
                                        assert coordinates_geo_json != null;
                                        for (Object index_coordinates_response : coordinates_geo_json.keySet()) {
                                            JSONObject coord_json = (JSONObject) coordinates_geo_json.get(index_coordinates_response);
                                            if (coord_json != null &&
                                                    coord_json.containsKey("0") &&
                                                    coord_json.containsKey("1")) {
                                                itinerary_geo.put(Integer.parseInt((String) index_coordinates_response),
                                                        new LatLng(
                                                                Double.parseDouble((String) Objects.requireNonNull(coord_json.get("1"))),
                                                                Double.parseDouble((String) Objects.requireNonNull(coord_json.get("0")))
                                                        )
                                                );
                                            }
                                        }
                                        reserv.setCoords(new ArrayList<>(itinerary_geo.values()));
                                        reserv.setGiveCoords(false);

                                        ArrayList<Segment> segment_list = new ArrayList<>();
                                        JSONObject segments_json = (JSONObject) response_geo_json.get("segments");
                                        assert segments_json != null;
                                        for (Object index_segments_request : segments_json.keySet()) {
                                            JSONObject segment_json = (JSONObject) segments_json.get(index_segments_request);
                                            if (segment_json != null &&
                                                    segment_json.containsKey("start_stop_pos") &&
                                                    segment_json.containsKey("end_stop_pos") &&
                                                    segment_json.containsKey("distance") &&
                                                    segment_json.containsKey("duration")) {
                                                segment_list.add(new Segment(
                                                        Integer.parseInt((String) Objects.requireNonNull(segment_json.get("start_stop_pos"))),
                                                        Integer.parseInt((String) Objects.requireNonNull(segment_json.get("end_stop_pos"))),
                                                        Float.parseFloat((String) Objects.requireNonNull(segment_json.get("distance"))),
                                                        Float.parseFloat((String) Objects.requireNonNull(segment_json.get("duration")))
                                                ));
                                            }
                                        }
                                        reserv.setSegmentList(segment_list);
                                    }
                                }
                            }
                            if (!reserv.isGiveCoords() && !reserv.getStopsItinerary().isEmpty()) {
                                act.actualizeMap();
                            }
                        }

                        reserv.setEnabled(true);
                    } catch (ParseException e) {
                        reserv.setEnabled(false);
                        Toast.makeText(act, response_str, Toast.LENGTH_SHORT).show();
                    }
                    act.resetFlagsSpinner();
                    act.setSpinnersValue();
                    act.actualizeView();
                    act.hideProgress();
                }

            }.execute();
        } else {
            Toast.makeText(act, act.getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show();
            act.hideProgress();
        }
    }

    /**
     * Register a reservation
     *
     * @param act    Activity
     * @param reserv Reservation
     */
    @SuppressLint("StaticFieldLeak")
    public void post_reserve(TerminalActivity act, ReservationTrip reserv) {
        act.showProgress();
        if (InternetConnectionManager.checkConnection(act)) {
            new AsyncTask<Void, String, String>() {

                /**
                 * Launching the POST request to the server (reservation)
                 * @param voids no parameters
                 * @return the response of the request (normally json)
                 */
                @Override
                protected String doInBackground(Void... voids) {
                    String result;


                    HashMap<String, Object> params = new HashMap<>();
                    HashMap<String, Object> header = new HashMap<>();

                    String token = ConnectionManager.getInstance().getUtilisateur().getToken();
                    header.put("token", token);

                    params.put("id_itinerary", reserv.getIdItinerary());
                    params.put("id_trip", reserv.getHourTripList().get(reserv.getHourTripPos()).getId());
                    params.put("date", reserv.getDateForRequest());
                    if (reserv.isToMyPosition()) {
                        LocalizationManager gpsTracker = TerminalApp.getGpsTracker();
                        if (gpsTracker.canGetLocation()) {
                            Location l = gpsTracker.getLocation();
                            params.put("latitude", l.getLatitude());
                            params.put("longitude", l.getLongitude());
                        } else {
                            gpsTracker.showSettingsAlert();
                        }
                    } else {
                        params.put("start_stop", reserv.getStartStopList().get(reserv.getStartStopPos()).getId());
                    }
                    params.put("end_stop", reserv.getEndStopList().get(reserv.getEndStopPos()).getId());
                    params.put("travel_alone", reserv.isTravelAlone() ? "1" : "0");
                    params.put("nb_place", reserv.getNbPlacesList().get(reserv.getNbPlacesPos()));

                    //Take the right url
                    String url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/reservation";

                    //Doing the request
                    HttpRequest req = new HttpRequest(url_request, params, header);
                    result = req.post();

                    return result;
                }

                /**
                 * Processing of the response of the POST request (reservation)
                 * @param res response of the POST request
                 */
                @Override
                protected void onPostExecute(String res) {
                    super.onPostExecute(res);
                    response_str = act.getString(R.string.error_recuperation_data_on_server);

                    //Initialization of the message to print in the toast with the error one
                    try {
                        JSONObject userJson = ReservationController.this.parse(res);

                        if (userJson != null &&
                                userJson.containsKey("id_reservation")) {
                            String id_reservation = (String) userJson.get("id_reservation");

                            //We can change the activity now
                            act.finish();
                            Intent intent = new Intent(act, HomeActivity.class);
                            Bundle b = new Bundle();
                            b.putString("id_reservation", id_reservation);
                            intent.putExtras(b);
                            act.startActivity(intent);
                        }
                    } catch (ParseException e) {
                        //Message of the error already stored in response_str attribute
                    }
                    Toast.makeText(act, response_str, Toast.LENGTH_SHORT).show();
                    act.hideProgress();
                }

            }.execute();
        } else {
            Toast.makeText(act, act.getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show();
            act.hideProgress();
        }
    }

    /**
     * Cancel a reservation
     *
     * @param act Activity
     * @param td  TripDetails
     */
    @SuppressLint("StaticFieldLeak")
    public void post_cancel_reservation(TerminalActivity act, TripDetails td) {
        act.showProgress();
        if (InternetConnectionManager.checkConnection(act)) {
            new AsyncTask<Void, String, String>() {

                /**
                 * Launching the POST request to the server (cancel reservation)
                 * @param voids no parameters
                 * @return the response of the request (normally json)
                 */
                @Override
                protected String doInBackground(Void... voids) {
                    String result;


                    HashMap<String, Object> params = new HashMap<>();
                    HashMap<String, Object> header = new HashMap<>();

                    String token = ConnectionManager.getInstance().getUtilisateur().getToken();
                    header.put("token", token);

                    params.put("id_reservation", td.getIdTripOrReserv());


                    //Take the right url
                    String url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/reservation-cancel";

                    //Doing the request
                    HttpRequest req = new HttpRequest(url_request, params, header);
                    result = req.post();

                    return result;
                }

                /**
                 * Processing of the response of the POST request (cancel reservation)
                 * @param res response of the POST request
                 */
                @Override
                protected void onPostExecute(String res) {
                    super.onPostExecute(res);
                    response_str = act.getString(R.string.error_recuperation_data_on_server);

                    //Initialization of the message to print in the toast with the error one
                    try {
                        ReservationController.this.parse(res);

                        act.finish();

                    } catch (ParseException e) {
                        //Message of the error already stored in response_str attribute
                    }
                    Toast.makeText(act, response_str, Toast.LENGTH_SHORT).show();
                    act.hideProgress();
                }

            }.execute();
        } else {
            Toast.makeText(act, act.getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show();
            act.hideProgress();
        }
    }
}
