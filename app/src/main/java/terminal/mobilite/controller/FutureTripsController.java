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
import terminal.mobilite.manager.ConnectionManager;
import terminal.mobilite.manager.HttpRequest;
import terminal.mobilite.manager.InternetConnectionManager;
import terminal.mobilite.model.future_trips.FutureTrip;
import terminal.mobilite.model.user.Utilisateur;
import terminal.mobilite.view.recycler.future_trips.FutureTripAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class FutureTripsController extends Controller {
    private static FutureTripsController INSTANCE;

    private FutureTripsController() {
    }

    public static FutureTripsController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FutureTripsController();
        }
        return INSTANCE;
    }

    /**
     * get the future_trips of the user and put the result in the recycler view
     *
     * @param act                 Activity
     * @param recycl_future_trips RecyclerView
     * @param no_future_trip      TextView
     */
    @SuppressLint("StaticFieldLeak")
    public void get_future_trips(TerminalActivity act, RecyclerView recycl_future_trips, TextView no_future_trip) {
        act.showProgress();

        if (InternetConnectionManager.checkConnection(act)) {
            new AsyncTask<Void, String, String>() {

                /**
                 * Launching the GET request to the server (future_trips)
                 * @param voids no parameters
                 * @return the response of the request (normally json)
                 */
                @Override
                protected String doInBackground(Void... voids) {
                    String result;

                    //params and header empty
                    HashMap<String, Object> params = new HashMap<>();
                    HashMap<String, Object> header = new HashMap<>();

                    String token = ConnectionManager.getInstance().getUtilisateur().getToken();

                    header.put("token", token);

                    //URL
                    String url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/future-trips";

                    //Doing the request
                    HttpRequest req = new HttpRequest(url_request, params, header);
                    result = req.get();

                    return result;
                }

                /**
                 * Processing of the response of the GET request (future_trips)
                 * @param res response of the GET request
                 */
                @Override
                protected void onPostExecute(String res) {
                    super.onPostExecute(res);
                    response_str = act.getString(R.string.error_recuperation_data_on_server);
                    try {
                        JSONObject json_results = FutureTripsController.this.parse(res);

                        ArrayList<FutureTrip> future_trips = new ArrayList<>();

                        for (Object index : json_results.keySet()) {
                            JSONObject future_trip_json = (JSONObject) json_results.get(index);
                            if (future_trip_json != null &&
                                    future_trip_json.containsKey("id_trajet") &&
                                    future_trip_json.containsKey("titre") &&
                                    future_trip_json.containsKey("date_depart") &&
                                    future_trip_json.containsKey("arret_depart") &&
                                    future_trip_json.containsKey("heure_depart") &&
                                    future_trip_json.containsKey("arret_fin") &&
                                    future_trip_json.containsKey("heure_fin")) {
                                String id_trajet = (String) future_trip_json.get("id_trajet");
                                String titre = (String) future_trip_json.get("titre");
                                String date_depart = (String) future_trip_json.get("date_depart");
                                String arret_depart = (String) future_trip_json.get("arret_depart");
                                String heure_depart = (String) future_trip_json.get("heure_depart");
                                String arret_fin = (String) future_trip_json.get("arret_fin");
                                String heure_fin = (String) future_trip_json.get("heure_fin");
                                FutureTrip future_trip;
                                if (future_trip_json.containsKey("id_reservation") && ConnectionManager.getInstance().getUtilisateur().getType().equals(Utilisateur.PASSENGER_TYPE)) {
                                    String id_reservation = (String) future_trip_json.get("id_reservation");
                                    future_trip = new FutureTrip(id_reservation, id_trajet, titre, date_depart, arret_depart, arret_fin, heure_depart, heure_fin);
                                } else {
                                    future_trip = new FutureTrip(id_trajet, titre, date_depart, arret_depart, arret_fin, heure_depart, heure_fin);
                                }

                                future_trips.add(future_trip);
                            }

                        }
                        if (future_trips.isEmpty()) {
                            no_future_trip.setVisibility(View.VISIBLE);
                        }

                        LinearLayoutManager layoutManager = new LinearLayoutManager(act);
                        recycl_future_trips.setLayoutManager(layoutManager);

                        // specify an adapter (see also next example)
                        FutureTripAdapter ca = new FutureTripAdapter(future_trips);
                        recycl_future_trips.setAdapter(ca);

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
