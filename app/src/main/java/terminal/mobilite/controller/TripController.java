package terminal.mobilite.controller;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.TripActivity;
import terminal.mobilite.manager.ConnectionManager;
import terminal.mobilite.manager.InternetConnectionManager;
import terminal.mobilite.manager.LocalizationManager;
import terminal.mobilite.manager.HttpRequest;
import terminal.mobilite.model.reservation.Segment;
import terminal.mobilite.model.trip_details.PassengerTripDriver;
import terminal.mobilite.model.trip_details.StopInTrip;
import terminal.mobilite.model.trip_details.TripDetails;
import terminal.mobilite.model.trip_details.TripDetailsDriver;
import terminal.mobilite.model.user.Utilisateur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

public class TripController extends Controller {
    private static TripController INSTANCE;

    private TripController() {
    }

    public static TripController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TripController();
        }
        return INSTANCE;
    }

    @SuppressLint("StaticFieldLeak")
    public void get_trip_details(TripActivity act, TripDetails td) {

        act.showProgress();
        if (InternetConnectionManager.checkConnection(act)) {
            new AsyncTask<Void, String, String>() {

                /**
                 * Launching the GET request to the server (trip_details)
                 * @param voids no parameters
                 * @return the response of the request (normally json)
                 */
                @Override
                protected String doInBackground(Void... voids) {
                    String result;


                    //params
                    HashMap<String, Object> params = new HashMap<>();
                    HashMap<String, Object> header = new HashMap<>();

                    String token = ConnectionManager.getInstance().getUtilisateur().getToken();
                    header.put("token", token);

                    //URL
                    String url_request;
                    if (ConnectionManager.getInstance().getUtilisateur().getType().equals(Utilisateur.DRIVER_TYPE)) {
                        url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/trip-driven-details/" + td.getIdTripOrReserv();
                    } else {
                        url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/trip-reserved-details/" + td.getIdTripOrReserv();
                    }

                    //Doing the request
                    HttpRequest req = new HttpRequest(url_request, params, header);
                    result = req.get();

                    return result;
                }

                /**
                 * Processing of the response of the GET request (trip_details)
                 * @param res response of the GET request
                 */
                @Override
                protected void onPostExecute(String res) {
                    super.onPostExecute(res);
                    response_str = act.getString(R.string.error_recuperation_data_on_server);
                    try {
                        JSONObject json_results = TripController.this.parse(res);

                        if (json_results != null &&
                                json_results.containsKey("itinerary_name") &&
                                json_results.containsKey("itinerary_coords") &&
                                json_results.containsKey("itinerary_stops")) {
                            String itinerary_name = (String) json_results.get("itinerary_name");
                            JSONObject itinerary_geo_json = (JSONObject) json_results.get("itinerary_coords");
                            JSONObject itinerary_stops_json = (JSONObject) json_results.get("itinerary_stops");

                            ArrayList<LatLng> coords = null;
                            ArrayList<Segment> segment_list = new ArrayList<>();

                            TreeMap<String, StopInTrip> stops_itinerary = new TreeMap<>();

                            assert itinerary_stops_json != null;
                            for (Object index_stops_itinerary : itinerary_stops_json.keySet()) {
                                JSONObject stop_itinerary_json = (JSONObject) itinerary_stops_json.get(index_stops_itinerary);
                                if (stop_itinerary_json != null &&
                                        stop_itinerary_json.containsKey("id_stop") &&
                                        stop_itinerary_json.containsKey("name_stop") &&
                                        stop_itinerary_json.containsKey("num_stop") &&
                                        stop_itinerary_json.containsKey("time") &&
                                        stop_itinerary_json.containsKey("latitude") &&
                                        stop_itinerary_json.containsKey("longitude")) {
                                    stops_itinerary.put(
                                            (String) stop_itinerary_json.get("num_stop"),
                                            new StopInTrip(
                                                    (String) stop_itinerary_json.get("id_stop"),
                                                    (String) stop_itinerary_json.get("name_stop"),
                                                    (String) stop_itinerary_json.get("num_stop"),
                                                    new LatLng(
                                                            Double.parseDouble((String) Objects.requireNonNull(stop_itinerary_json.get("latitude"))),
                                                            Double.parseDouble((String) Objects.requireNonNull(stop_itinerary_json.get("longitude")))
                                                    ),
                                                    (String) stop_itinerary_json.get("time")
                                            ));
                                }
                            }
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
                                    coords = new ArrayList<>(itinerary_geo.values());


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


                                }
                            }

                            if (json_results.containsKey("passenger_coords")) {
                                ArrayList<PassengerTripDriver> passengers_list = new ArrayList<>();
                                JSONObject passengers_json = (JSONObject) json_results.get("passenger_coords");
                                assert passengers_json != null;
                                for (Object index_passengers : passengers_json.keySet()) {
                                    JSONObject passenger_json = (JSONObject) passengers_json.get(index_passengers);
                                    if (passenger_json != null &&
                                            passenger_json.containsKey("id_in_geo") &&
                                            passenger_json.containsKey("start_stop_name") &&
                                            passenger_json.containsKey("end_stop_name") &&
                                            passenger_json.containsKey("start_stop_pos") &&
                                            passenger_json.containsKey("end_stop_pos") &&
                                            passenger_json.containsKey("time")) {
                                        passengers_list.add(new PassengerTripDriver(
                                                Integer.parseInt((String) Objects.requireNonNull(passenger_json.get("id_in_geo"))),
                                                Integer.parseInt((String) Objects.requireNonNull(passenger_json.get("time"))),
                                                Integer.parseInt((String) Objects.requireNonNull(passenger_json.get("start_stop_pos"))),
                                                Integer.parseInt((String) Objects.requireNonNull(passenger_json.get("end_stop_pos"))),
                                                (String) Objects.requireNonNull(passenger_json.get("start_stop_name")),
                                                (String) Objects.requireNonNull(passenger_json.get("end_stop_name")),
                                                td
                                        ));
                                    }
                                }
                                assert td instanceof TripDetailsDriver;
                                ((TripDetailsDriver) td).setPassengers(passengers_list);
                            }

                            if (json_results.containsKey("trip")) {
                                JSONObject trip_json = (JSONObject) json_results.get("trip");
                                assert trip_json != null;
                                if (trip_json.containsKey("start_stop") &&
                                        trip_json.containsKey("end_stop") &&
                                        trip_json.containsKey("date") &&
                                        trip_json.containsKey("time_trip")) {
                                    if (trip_json.containsKey("id_in_geo")) {
                                        td.setIdInGeo(Integer.parseInt((String) Objects.requireNonNull(trip_json.get("id_in_geo"))));
                                    }
                                    td.setStartStopPos(Integer.parseInt((String) Objects.requireNonNull(trip_json.get("start_stop"))));
                                    td.setEndStopPos(Integer.parseInt((String) Objects.requireNonNull(trip_json.get("end_stop"))));
                                    td.setDate((String) Objects.requireNonNull(trip_json.get("date")));
                                    td.setDuration(Integer.parseInt((String) Objects.requireNonNull(trip_json.get("time_trip"))));
                                }
                            } else {
                                td.setStartStopPos(0);
                                td.setEndStopPos(stops_itinerary.size() - 1);
                            }

                            if (coords != null && segment_list.size() > 0 && stops_itinerary.size() > 0) {
                                td.setNameItinerary(itinerary_name);
                                td.setCoords(coords);
                                td.setSegmentList(segment_list);
                                td.setStopsItinerary(stops_itinerary);
                                act.setTripDetailsReady();
                                act.initializeCallback();
                                act.initializeDisplay();
                            } else {
                                throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION);
                            }
                        }

                    } catch (ParseException e) {
                        act.finish();
                        Toast.makeText(act, response_str, Toast.LENGTH_SHORT).show();
                    }
                    act.hideProgress();
                }

            }.execute();
        } else {
            Toast.makeText(act, act.getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show();
            act.hideProgress();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void post_trip_driven_ongoing(TripActivity act, TripDetails td) {

        if (InternetConnectionManager.checkConnection(act)) {
            new AsyncTask<Void, String, String>() {

                /**
                 * Launching the POST request to the server (trip_driven_ongoing)
                 * @param voids no parameters
                 * @return the response of the request (normally json)
                 */
                @Override
                protected String doInBackground(Void... voids) {
                    String result;


                    //params
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("id_trip", td.getIdTripOrReserv());

                    LocalizationManager gpsTracker = TerminalApp.getGpsTracker();
                    if (gpsTracker.canGetLocation()) {
                        Location location = gpsTracker.getLocation();
                        float lat = (float) location.getLatitude();
                        float lon = (float) location.getLongitude();
                        params.put("latitude", lat);
                        params.put("longitude", lon);
                        td.setCoordsDriver(new LatLng(lat, lon));
                    } else {
                        gpsTracker.showSettingsAlert();
                    }

                    HashMap<String, Object> header = new HashMap<>();

                    String token = ConnectionManager.getInstance().getUtilisateur().getToken();
                    header.put("token", token);

                    //URL
                    String url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/trip-driven-ongoing";

                    //Doing the request
                    HttpRequest req = new HttpRequest(url_request, params, header);
                    result = req.post();

                    return result;
                }

                /**
                 * Processing of the response of the POST request (trip_driven_ongoing)
                 * @param res response of the POST request
                 */
                @Override
                protected void onPostExecute(String res) {
                    super.onPostExecute(res);
                    response_str = act.getString(R.string.error_recuperation_data_on_server);
                    try {
                        JSONObject json_results = TripController.this.parse(res);
                        if (json_results != null &&
                                json_results.containsKey("in_process")) {
                            boolean in_process = Integer.parseInt((String) Objects.requireNonNull(json_results.get("in_process"))) > 0;
                            td.setInProcess(in_process);
                            act.tripOngoingCallback();
                        }

                    } catch (ParseException e) {
                        act.finish();
                        Toast.makeText(act, response_str, Toast.LENGTH_SHORT).show();
                    }
                }

            }.execute();
        } else {
            Toast.makeText(act, act.getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void get_trip_reserved_ongoing(TripActivity act, TripDetails td) {

        if (InternetConnectionManager.checkConnection(act)) {
            new AsyncTask<Void, String, String>() {

                /**
                 * Launching the GET request to the server (trip_reserved_ongoing)
                 * @param voids no parameters
                 * @return the response of the request (normally json)
                 */
                @Override
                protected String doInBackground(Void... voids) {
                    String result;

                    //params
                    HashMap<String, Object> params = new HashMap<>();
                    HashMap<String, Object> header = new HashMap<>();

                    String token = ConnectionManager.getInstance().getUtilisateur().getToken();
                    header.put("token", token);

                    //URL
                    String url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/trip-reserved-ongoing/" + td.getIdTripOrReserv();

                    //Doing the request
                    HttpRequest req = new HttpRequest(url_request, params, header);
                    result = req.get();

                    return result;
                }

                /**
                 * Processing of the response of the GET request (trip_reserved_ongoing)
                 * @param res response of the GET request
                 */
                @Override
                protected void onPostExecute(String res) {
                    super.onPostExecute(res);
                    response_str = act.getString(R.string.error_recuperation_data_on_server);
                    try {
                        JSONObject json_results = TripController.this.parse(res);

                        if (json_results != null &&
                                json_results.containsKey("in_process")) {
                            boolean in_process = Integer.parseInt((String) Objects.requireNonNull(json_results.get("in_process"))) > 0;

                            if (json_results.containsKey("coords")) {
                                JSONObject coords_json = (JSONObject) json_results.get("coords");
                                if (coords_json != null &&
                                        coords_json.containsKey("latitude") &&
                                        coords_json.containsKey("longitude")) {
                                    String latitude_str = (String) coords_json.get("latitude");
                                    String longitude_str = (String) coords_json.get("longitude");
                                    assert latitude_str != null;
                                    assert longitude_str != null;
                                    td.setCoordsDriver(new LatLng(Float.parseFloat(latitude_str), Float.parseFloat(longitude_str)));
                                }
                            }
                            td.setInProcess(in_process);
                            act.tripOngoingCallback();
                        }

                    } catch (ParseException e) {
                        act.finish();
                        Toast.makeText(act, response_str, Toast.LENGTH_SHORT).show();
                    }
                }

            }.execute();
        } else {
            Toast.makeText(act, act.getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }
}
