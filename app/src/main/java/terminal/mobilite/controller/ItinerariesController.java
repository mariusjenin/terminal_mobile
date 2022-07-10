package terminal.mobilite.controller;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.TerminalActivity;
import terminal.mobilite.manager.HttpRequest;
import terminal.mobilite.manager.InternetConnectionManager;
import terminal.mobilite.model.itineraries.TimeStopInItineray;
import terminal.mobilite.model.itineraries.Itinerary;
import terminal.mobilite.model.itineraries.StopInItineray;
import terminal.mobilite.model.itineraries.TripInItineray;
import terminal.mobilite.view.recycler.itineraries.ItineraryAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ItinerariesController extends Controller {
    private static ItinerariesController INSTANCE;

    private ItinerariesController() {
    }

    public static ItinerariesController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ItinerariesController();
        }
        return INSTANCE;
    }

    /**
     * get the itineraries and put the result in recyclerview
     *
     * @param act                Activity
     * @param recycl_itineraries RecyclerView
     * @param no_itinerary       TextView
     */
    @SuppressLint("StaticFieldLeak")
    public void get_simple_itienaries(TerminalActivity act, RecyclerView recycl_itineraries, TextView no_itinerary) {
        act.showProgress();
        if (InternetConnectionManager.checkConnection(act)) {
            new AsyncTask<Void, String, String>() {

                /**
                 * Launching the GET request to the server (itineraries)
                 * @param voids no parameters
                 * @return the response of the request (normally json)
                 */
                @Override
                protected String doInBackground(Void... voids) {
                    String result;

                    //params and header empty
                    HashMap<String, Object> params = new HashMap<>();
                    HashMap<String, Object> header = new HashMap<>();

                    //URL
                    String url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/itineraries";

                    //Doing the request
                    HttpRequest req = new HttpRequest(url_request, params, header);
                    result = req.get();

                    return result;
                }

                /**
                 * Processing of the response of the GET request (itineraries)
                 * @param res response of the GET request
                 */
                @Override
                protected void onPostExecute(String res) {
                    super.onPostExecute(res);
                    response_str = act.getString(R.string.error_recuperation_data_on_server);
                    try {
                        JSONObject json_results = ItinerariesController.this.parse(res);

                        ArrayList<Itinerary> itineraries = new ArrayList<>();

                        for (Object index : json_results.keySet()) {
                            JSONObject itineraries_json = (JSONObject) json_results.get(index);
                            if (itineraries_json != null &&
                                    itineraries_json.containsKey("id_itinerary") &&
                                    itineraries_json.containsKey("name_itinerary") &&
                                    itineraries_json.containsKey("stops") &&
                                    itineraries_json.containsKey("trips")) {
                                String id_itineraire = (String) itineraries_json.get("id_itinerary");
                                String nom_itineraire = (String) itineraries_json.get("name_itinerary");
                                JSONObject stops_json = (JSONObject) itineraries_json.get("stops");
                                JSONObject trips_json = (JSONObject) itineraries_json.get("trips");

                                ArrayList<StopInItineray> arr_stops = new ArrayList<>();
                                ArrayList<TripInItineray> arr_trips = new ArrayList<>();

                                assert stops_json != null;
                                for (Object index_stops : stops_json.keySet()) {
                                    JSONObject stop_json = (JSONObject) stops_json.get(index_stops);
                                    assert stop_json != null;
                                    StopInItineray stop = new StopInItineray(
                                            (String) stop_json.get("id_arret"),
                                            (String) stop_json.get("nom_arret")
                                    );
                                    arr_stops.add(stop);
                                }

                                assert trips_json != null;
                                for (Object index_trips : trips_json.keySet()) {
                                    JSONObject trip_json = (JSONObject) trips_json.get(index_trips);

                                    ArrayList<TimeStopInItineray> arr_times_stops = new ArrayList<>();

                                    assert trip_json != null;
                                    JSONObject times_stops_json = (JSONObject) trip_json.get("time_passage");
                                    assert times_stops_json != null;
                                    for (Object index_times_stops : times_stops_json.keySet()) {
                                        JSONObject time_stop_json = (JSONObject) times_stops_json.get(index_times_stops);
                                        assert time_stop_json != null;
                                        TimeStopInItineray time_stop = new TimeStopInItineray(
                                                (String) time_stop_json.get("num_stop"),
                                                (String) time_stop_json.get("time")
                                        );
                                        arr_times_stops.add(time_stop);
                                    }

                                    TripInItineray trip = new TripInItineray(
                                            (String) trip_json.get("repetition"),
                                            (String) trip_json.get("date_depart"),
                                            arr_times_stops

                                    );
                                    arr_trips.add(trip);
                                }

                                Itinerary it = new Itinerary(id_itineraire, nom_itineraire, arr_stops, arr_trips);

                                itineraries.add(it);
                            }

                        }
                        if (itineraries.isEmpty()) {
                            no_itinerary.setVisibility(View.VISIBLE);
                        }

                        LinearLayoutManager layoutManager = new LinearLayoutManager(act);
                        recycl_itineraries.setLayoutManager(layoutManager);

                        // specify an adapter (see also next example)
                        ItineraryAdapter ia = new ItineraryAdapter(itineraries);
                        recycl_itineraries.setAdapter(ia);

                    } catch (ParseException e) {
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
}
