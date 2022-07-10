package terminal.mobilite.activity;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import terminal.mobilite.controller.TripController;
import terminal.mobilite.model.trip_details.TripDetails;

/**
 * Activité affichant un trajet
 */
public abstract class TripActivity extends ToolbarActivity implements ConnectedActivity, OnMapReadyCallback {
    protected final static int DELAY_ACTUALIZE_IN_PROGRESS = 2000;
    protected final static int DELAY_ACTUALIZE_NOT_IN_PROGRESS = 10000;
    protected SupportMapFragment mapFragment;
    protected GoogleMap gMap;

    protected TripDetails trip_details;
    protected boolean trip_details_ready;

    protected Polyline polyline_itinerary;

    protected Marker driver_marker;

    protected Handler handler;
    protected Runnable runnable;

    public abstract void initializeDisplay();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trip_details_ready = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    public void initialize(String id_trip_or_reserv){
        trip_details.setIdTripOrReserv(id_trip_or_reserv);
        TripController.getInstance().get_trip_details(this,trip_details);
    }

    public abstract void initializeCallback();

    public abstract void tripOngoingCallback();

    public void setTripDetailsReady(){
        trip_details_ready = true;
    }

    @Override
    public void finish() {
        super.finish();
        if(handler != null && runnable != null){
            handler.removeCallbacks(runnable);
        }
    }
}