package terminal.mobilite.activity.operational;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.ConnectedActivity;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.TripActivity;
import terminal.mobilite.controller.ReservationController;
import terminal.mobilite.controller.TripController;
import terminal.mobilite.manager.DateManager;
import terminal.mobilite.manager.LocalizationManager;
import terminal.mobilite.model.trip_details.StopInTrip;
import terminal.mobilite.model.trip_details.TripDetails;
import terminal.mobilite.view.ImgAnimationManager;
import terminal.mobilite.manager.MapManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Activité de l'écran affichant un trajet pour un passager
 */
public class TripPassengerActivity extends TripActivity implements ConnectedActivity, OnMapReadyCallback {

    private ConstraintLayout const_layout_right_notch_trip_passenger;
    private ConstraintLayout const_layout_details_trip_passenger;
    private ImageView img_1_notch_trip_passenger;
    private ImageView img_2_notch_trip_passenger;
    private TextView msg_notch_trip_passenger;
    private TextView title_trip_passenger;
    private TextView msg_trip_passenger;
    private Button cancel_trip_passenger;

    private Marker position_marker;

    /**
     * Creation of the TripPassengerActivity (setting up of the layout and recuperation of all the fields)
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        middleware.verify_and_redirect(this);

        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");

        //Put the right layout
        this.setContentView(R.layout.trip_passenger_activity);

        mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map_trip_passenger);

        const_layout_right_notch_trip_passenger = findViewById(R.id.const_layout_right_notch_trip_passenger);
        const_layout_details_trip_passenger = findViewById(R.id.const_layout_details_trip_passenger);
        img_1_notch_trip_passenger = findViewById(R.id.img_1_notch_trip_passenger);
        img_2_notch_trip_passenger = findViewById(R.id.img_2_notch_trip_passenger);
        title_trip_passenger = findViewById(R.id.title_trip_passenger);
        msg_trip_passenger = findViewById(R.id.msg_trip_passenger);
        msg_notch_trip_passenger = findViewById(R.id.msg_notch_trip_passenger);
        cancel_trip_passenger = findViewById(R.id.cancel_trip_passenger);

        title_trip_passenger.setText(R.string.title_msg_passenger_trip);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = ImgAnimationManager.getInstance().getCarBitmap(this);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        img_1_notch_trip_passenger.setImageBitmap(rotatedBitmap);
        img_2_notch_trip_passenger.setImageBitmap(ImgAnimationManager.getInstance().getRoundMarkerBitmap(ImgAnimationManager.MARKER_POSITION_SIZE, ContextCompat.getColor(TerminalApp.getContext(), R.color.vibrantBlue)));


        this.trip_details = new TripDetails();


        cancel_trip_passenger.setOnClickListener(v -> ReservationController.getInstance().post_cancel_reservation(this,trip_details));

        handler = new Handler();
        runnable = () -> {
            TripController.getInstance().get_trip_reserved_ongoing(this, trip_details);
            if (trip_details.isInProcess() && TerminalApp.isForeground()) {
                handler.postDelayed(runnable, DELAY_ACTUALIZE_IN_PROGRESS);
            } else {
                handler.postDelayed(runnable, DELAY_ACTUALIZE_NOT_IN_PROGRESS);
            }
        };

        initialize(id);
    }

    /**
     * On met en route les requêtes vers le serveur qui s'effectuent régulièrement
     */
    @Override
    public void initializeCallback() {
        handler.post(runnable);
    }

    /**
     * On supprime le marker du conducteur pour reeffectuer son initialisation
     * à la prochaine requête vers le serveur
     */
    @Override
    protected void onResume() {
        super.onResume();
        driver_marker = null;
    }

    /**
     * Appel de cette fonction pour changer l'affichage après avoir effectuer une requête vers le serveur
     */
    @Override
    public void tripOngoingCallback() {
        if (trip_details.isInProcess()) {
            Context c = TerminalApp.getContext();
            int time = trip_details.getTimeFromDriverToPassengers(trip_details.getStartStopPos(),trip_details.getEndStopPos(),trip_details.getTripPos()[0]);
            String text;
            if(time == -1){
                const_layout_right_notch_trip_passenger.setVisibility(View.GONE);
            } else{
                const_layout_right_notch_trip_passenger.setVisibility(View.VISIBLE);
                if(time >= 3000){
                    text = c.getString(R.string.about)+" "+ DateManager.getInstance().format(time)+" "+c.getString(R.string.from_you);
                } else {
                    text = c.getString(R.string.to_your_position);
                }
                msg_notch_trip_passenger.setText(text);
            }

            MapManager mcm = new MapManager(gMap);

            int fromIndex = trip_details.getPrevPosNearCoordsDriver();
            int toIndex = trip_details.getPosNearCoordsDriver();
            boolean inverse = false;
            if (fromIndex > toIndex) {
                inverse = true;
                int tmpIndex = toIndex;
                toIndex = fromIndex;
                fromIndex = tmpIndex;
            }
            ArrayList<LatLng> coords = trip_details.getCoords();
            ArrayList<LatLng> cabLatLngList = new ArrayList<>(coords.subList(fromIndex, toIndex + 1));
            if (inverse) {
                Collections.reverse(cabLatLngList);
            }

            if (driver_marker == null) {
                driver_marker = mcm.addMarkerAndGet(cabLatLngList.get(0), ImgAnimationManager.getInstance().getCarBitmap(this), getString(R.string.title_marker_position_home), getString(R.string.descr_marker_position_home), 10);
                if (coords.size() > fromIndex + 1) {
                    driver_marker.setRotation(ImgAnimationManager.getInstance().getRotation(coords.get(fromIndex), coords.get(fromIndex + 1)));
                } else if (0 <= fromIndex - 1) {
                    driver_marker.setRotation(ImgAnimationManager.getInstance().getRotation(coords.get(fromIndex - 1), coords.get(fromIndex)));
                }
            }

            mcm.drawCab(driver_marker, cabLatLngList, DELAY_ACTUALIZE_IN_PROGRESS);
        } else {
            const_layout_right_notch_trip_passenger.setVisibility(View.GONE);
        }
    }

    /**
     * A chaque actualisation de la carte on recharge l'affichage
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style));
        LocalizationManager gpsTracker = new LocalizationManager(this);
        if (gpsTracker.canGetLocation()) {
            gMap.clear();


            const_layout_right_notch_trip_passenger.post(() -> const_layout_details_trip_passenger.post(() -> {
                if (msg_trip_passenger.getText().equals("")) {
                    TextWatcher tw = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            msg_trip_passenger.post(() -> {
                                gMap.setPadding(0, const_layout_right_notch_trip_passenger.getHeight(), 0, const_layout_details_trip_passenger.getHeight());
                                initializeDisplay();
                            });
                            msg_trip_passenger.removeTextChangedListener(this);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    };
                    msg_trip_passenger.addTextChangedListener(tw);
                } else {
                    msg_trip_passenger.post(() -> {
                        gMap.setPadding(0, const_layout_right_notch_trip_passenger.getHeight(), 0, const_layout_details_trip_passenger.getHeight());
                        initializeDisplay();
                    });
                }
            }));

            initializeDisplay();

        } else {
            gpsTracker.showSettingsAlert();
        }
    }


    /**
     * On initialise l'affichage
     */
    @Override
    public void initializeDisplay() {
        LocalizationManager gpsTracker = new LocalizationManager(this);
        MapManager mcm = new MapManager(gMap);
        if (gpsTracker.canGetLocation()) {
            LatLng defaultLocation = gpsTracker.getLatLng();
            position_marker = mcm.addMarkerAndGet(defaultLocation, ImgAnimationManager.getInstance().getRoundMarkerBitmap(ImgAnimationManager.MARKER_POSITION_SIZE, ContextCompat.getColor(TerminalApp.getContext(), R.color.vibrantBlue)), getString(R.string.title_marker_position_home), getString(R.string.descr_marker_position_home), 1);
            position_marker.showInfoWindow();

            if (trip_details_ready) {
                msg_trip_passenger.setText(trip_details.generateString());

                ArrayList<LatLng> latLngList = trip_details.getCoords();

                //Polyline Itinerary
                PolylineOptions polylineOptionsItinerary = new PolylineOptions();
                polylineOptionsItinerary.color(ContextCompat.getColor(TerminalApp.getContext(), R.color.grey));
                polylineOptionsItinerary.width(10F);
                polylineOptionsItinerary.addAll(latLngList);
                polyline_itinerary = gMap.addPolyline(polylineOptionsItinerary);

                //Polyline Trip
                PolylineOptions polylineOptionsTrip = new PolylineOptions();
                polylineOptionsTrip.color(ContextCompat.getColor(TerminalApp.getContext(), R.color.vibrantBlue));
                polylineOptionsTrip.width(15F);

                int[] tripPos = trip_details.getTripPos();
                polylineOptionsTrip.addAll(new ArrayList<>(latLngList.subList(tripPos[0], tripPos[1])));
                gMap.addPolyline(polylineOptionsTrip);


                mcm.animateCameraAroundBounds(latLngList);
//        MARKER Tous les arrêts
                TreeMap<String, StopInTrip> stops_itinerary = trip_details.getStopsItinerary();
                int start_stop_pos = trip_details.getStartStopPos();
                int end_stop_pos = trip_details.getEndStopPos();

                int colorBegin = ContextCompat.getColor(TerminalApp.getContext(), R.color.green);
                String msgBegin = getString(R.string.from_here_position_reservation);
                int colorEnd = ContextCompat.getColor(TerminalApp.getContext(), R.color.red);
                String msgEnd = getString(R.string.to_here_position_reservation);
                long sizeStandard = ImgAnimationManager.MARKER_ITINERARY_SIZE;
                long sizeBig = ImgAnimationManager.MARKER_ITINERARY_SIZE_BIGGER;
                int id_in_geo = trip_details.getIdInGeo();
                if (id_in_geo != -1) {
                    mcm.addMarkerAndGet(latLngList.get(id_in_geo), ImgAnimationManager.getInstance().getRoundMarkerBitmap(sizeBig, colorBegin), getString(R.string.title_marker_position_home), msgBegin, 2);
                }
                for (String key : stops_itinerary.keySet()) {
                    StopInTrip trip_stop = stops_itinerary.get(key);
                    assert trip_stop != null;
                    LatLng l = trip_stop.getCoords();
                    int color = ContextCompat.getColor(TerminalApp.getContext(), R.color.black);
                    String msg = "";
                    long size = sizeStandard;
                    int zIndex = 0;
                    boolean affichSpe = false;
                    //START
                    if (id_in_geo == -1 && Objects.requireNonNull(stops_itinerary.get(start_stop_pos + "")).getId().equals(trip_stop.getId())) {
                        color = colorBegin;
                        msg = msgBegin;
                        affichSpe = true;
                    } else
                        //END
                        if (Objects.requireNonNull(stops_itinerary.get(end_stop_pos + "")).getId().equals(trip_stop.getId())) {
                            color = colorEnd;
                            msg = msgEnd;
                            affichSpe = true;
                        }
                    //START OR END
                    if (affichSpe) {
                        size = sizeBig;
                        zIndex = 2;
                    }
                    mcm.addMarkerAndGet(l, ImgAnimationManager.getInstance().getRoundMarkerBitmap(size, color), trip_stop.getName(), msg, zIndex);
                }
            } else {

                mcm.cameraDefaultPosition(defaultLocation);
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }
}
