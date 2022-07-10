package terminal.mobilite.activity.operational;

import android.os.Bundle;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import terminal.mobilite.activity.ConnectedActivity;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.ToolbarActivity;
import terminal.mobilite.controller.FutureTripsController;

/**
 * Activité de l'écran affichant les trajets à venir
 */
public class FutureTripsActivity extends ToolbarActivity implements ConnectedActivity {
    private RecyclerView recycl_future_trips;
    private TextView no_future_trip;

    /**
     * Creation of the activity (setting up of the layout and recuperation of all the fields)
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        middleware.verify_and_redirect(this);

        //Put the right layout
        this.setContentView(R.layout.future_trips_activity);

        recycl_future_trips = findViewById(R.id.future_trips_recycler_future_trips);
        no_future_trip = findViewById(R.id.no_future_trip_future_trips);

    }

    /**
     * Reinitialisation des trajets à venir lorsqu'on revient sur l'application
     */
    @Override
    protected void onResume() {
        super.onResume();

        initialize_future_trips();
    }

    /**
     * On effectue la requête pour récupérer les trajets à venir avec le controller FutureTripsController
     */
    private void initialize_future_trips() {
        FutureTripsController.getInstance().get_future_trips(this, recycl_future_trips, no_future_trip);
    }
}
