package terminal.mobilite.activity.operational;

import android.os.Bundle;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import terminal.mobilite.activity.ConnectedActivity;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.ToolbarActivity;
import terminal.mobilite.controller.ItinerariesController;

/**
 * Activité de l'écran des itinéraires
 */
public class ItinerariesActivity extends ToolbarActivity implements ConnectedActivity {
    private RecyclerView recycl_itineraries;
    private TextView no_itineraries;

    /**
     * Creation of the ItinerariesActivity (setting up of the layout and recuperation of all the fields)
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        middleware.verify_and_redirect(this);

        //Put the right layout
        this.setContentView(R.layout.itineraries_activity);

        recycl_itineraries = findViewById(R.id.itineraries_recycler);
        no_itineraries = findViewById(R.id.no_itineraries);

    }

    /**
     * Reinitialisation des itinéraires lorsqu'on revient sur l'application
     */
    @Override
    protected void onResume() {
        super.onResume();
        ItinerariesController.getInstance().get_simple_itienaries(this,recycl_itineraries,no_itineraries);
    }
}
