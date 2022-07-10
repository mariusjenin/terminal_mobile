package terminal.mobilite.activity.operational;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.ConnectedActivity;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.ToolbarActivity;
import terminal.mobilite.controller.FutureTripsController;
import terminal.mobilite.manager.LocalizationManager;
import terminal.mobilite.view.ImgAnimationManager;
import terminal.mobilite.manager.MapManager;

/**
 * Activité de l'écran d'accueil
 */
public class HomeActivity extends ToolbarActivity implements ConnectedActivity, OnMapReadyCallback {
    private SupportMapFragment mapFragment;

    private RecyclerView recycl_future_trips;
    private TextView no_future_trip;
    private ConstraintLayout const_layout_future_trips_home;
    private ConstraintLayout const_layout_see_itineraries_home;

    /**
     * Creation of the HomeActivity (setting up of the layout and recuperation of all the fields)
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        middleware.verify_and_redirect(this);

        //Put the right layout
        this.setContentView(R.layout.home_activity);

        mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map_home);

        const_layout_future_trips_home = findViewById(R.id.const_layout_future_trips_home);
        const_layout_see_itineraries_home = findViewById(R.id.const_layout_see_itineraries_home);
        recycl_future_trips = findViewById(R.id.future_trips_recycler_home);
        no_future_trip = findViewById(R.id.no_future_trip_home);
        Button future_trips_button = findViewById(R.id.future_trips_button);
        Button itineraries_button = findViewById(R.id.btn_see_itineraries);

        future_trips_button.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FutureTripsActivity.class);
            HomeActivity.this.startActivity(intent);
        });

        itineraries_button.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ItinerariesActivity.class);
            HomeActivity.this.startActivity(intent);
        });
    }

    /**
     * Reinitialisation des trajets à venir lorsqu'on revient sur l'application et actualisation de la carte
     */
    @Override
    protected void onResume() {
        super.onResume();

        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        initialize_future_trips();
    }

    /**
     * On effectue la requête pour récupérer les trajets à venir avec le controller FutureTripsController
     */
    private void initialize_future_trips() {
        FutureTripsController.getInstance().get_future_trips(this, recycl_future_trips, no_future_trip);
    }


    /**
     * A chaque actualisation de la carte on recharge l'affichage
     * @param googleMap
     */
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style));
        LocalizationManager gpsTracker = new LocalizationManager(this);
        if (gpsTracker.canGetLocation()) {
            LatLng defaultLocation = gpsTracker.getLatLng();
            MapManager mcm = new MapManager(googleMap);

            recycl_future_trips.getViewTreeObserver()
                    .addOnGlobalLayoutListener(
                            () -> const_layout_see_itineraries_home.post(() -> const_layout_future_trips_home.post(() -> {
                                int height_total = const_layout_future_trips_home.getHeight() + const_layout_see_itineraries_home.getHeight();
                                googleMap.setPadding(0, 0, 0, height_total);
                                mcm.cameraDefaultPosition(defaultLocation);

                                mcm.removeAllMarker();
                                Marker m = mcm.addMarkerAndGet(defaultLocation, ImgAnimationManager.getInstance().getRoundMarkerBitmap(ImgAnimationManager.MARKER_POSITION_SIZE, ContextCompat.getColor(TerminalApp.getContext(), R.color.vibrantBlue)), getString(R.string.title_marker_position_home), getString(R.string.descr_marker_position_home), 0);
                                m.showInfoWindow();
                            })));
        } else {
            gpsTracker.showSettingsAlert();
        }
    }
}
