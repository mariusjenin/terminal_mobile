package terminal.mobilite.activity.operational;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.ConnectedActivity;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.TripActivity;
import terminal.mobilite.controller.TripController;
import terminal.mobilite.manager.LocalizationManager;
import terminal.mobilite.model.trip_details.PassengerTripDriver;
import terminal.mobilite.model.trip_details.StopInTrip;
import terminal.mobilite.model.trip_details.TripDetailsDriver;
import terminal.mobilite.view.ImgAnimationManager;
import terminal.mobilite.manager.MapManager;
import terminal.mobilite.view.recycler.passenger_trip_driver.PassengerTripDriverAdapter;

import java.util.*;

/**
 * Activité de l'écran affichant un trajet pour un conducteur
 */
public class TripDriverActivity extends TripActivity implements ConnectedActivity, OnMapReadyCallback {
    private final static int DELAY_ACTUALIZE_IN_PROGRESS = 1000;
    private final static int DELAY_ACTUALIZE_NOT_IN_PROGRESS = 5000;
    private ConstraintLayout const_layout_passenger_trip_driver;
    private RecyclerView recycler_trip_driver;
    private TextView no_passenger_trip_driver;
    private TextView title_trip_driver;
    private ArrayList<Marker> passengers_on_trip;


    /**
     * Creation of the TripDriverActivity (setting up of the layout and recuperation of all the fields)
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
        this.setContentView(R.layout.trip_driver_activity);

        mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map_trip_driver);


        const_layout_passenger_trip_driver = findViewById(R.id.const_layout_passenger_trip_driver);
        recycler_trip_driver = findViewById(R.id.recycler_trip_driver);
        no_passenger_trip_driver = findViewById(R.id.no_passenger_trip_driver);
        title_trip_driver = findViewById(R.id.title_trip_driver);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_trip_driver.setLayoutManager(layoutManager);

        this.trip_details = new TripDetailsDriver();
        this.passengers_on_trip = new ArrayList<>();

        handler = new Handler();
        runnable = () -> {
            TripController.getInstance().post_trip_driven_ongoing(TripDriverActivity.this, trip_details);
            if (trip_details.isInProcess()) {
                handler.postDelayed(runnable, DELAY_ACTUALIZE_IN_PROGRESS);
            } else {
                handler.postDelayed(runnable, DELAY_ACTUALIZE_NOT_IN_PROGRESS);
            }
        };

        initialize(id);
    }

    /**
     * On initialise la liste des passagers et on met en route les requêtes vers le serveur
     * qui s'effectuent régulièrement
     */
    @Override
    public void initializeCallback() {
        actualizePassengersTrip();

        handler.post(runnable);
    }

    /**
     * A chaque actualisation de la carte on recharge l'affichage
     * @param googleMap
     */
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style));
        LocalizationManager gpsTracker = new LocalizationManager(this);
        if (gpsTracker.canGetLocation()) {
            gMap.clear();

            const_layout_passenger_trip_driver.post(() -> {
                if (title_trip_driver.getText().equals("")) {
                    TextWatcher tw = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            title_trip_driver.post(() -> {
                                gMap.setPadding(0, 0, 0, const_layout_passenger_trip_driver.getHeight());
                                initializeDisplay();
                            });
                            title_trip_driver.removeTextChangedListener(this);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    };
                    title_trip_driver.addTextChangedListener(tw);
                } else {
                    title_trip_driver.post(() -> {
                        gMap.setPadding(0, 0, 0, const_layout_passenger_trip_driver.getHeight());
                        initializeDisplay();
                    });
                }
            });

            initializeDisplay();

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    /**
     * Appel de cette fonction pour changer l'affichage après avoir effectuer une requête vers le serveur
     */
    @Override
    public void tripOngoingCallback() {
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
            if(coords.size()>fromIndex+1){
                driver_marker.setRotation(ImgAnimationManager.getInstance().getRotation(coords.get(fromIndex), coords.get(fromIndex+1)));
            } else if(0<=fromIndex-1){
                driver_marker.setRotation(ImgAnimationManager.getInstance().getRotation(coords.get(fromIndex-1), coords.get(fromIndex)));
            }
        }
        actualizePassengersTrip();

        int delay = DELAY_ACTUALIZE_IN_PROGRESS;
        mcm.drawCab(driver_marker,cabLatLngList,delay);
        handler.postDelayed(this::actualizePassengersTrip,delay/2);

    }

    @Override
    public void initializeDisplay() {
        LocalizationManager gpsTracker = new LocalizationManager(this);
        MapManager mcm = new MapManager(gMap);
        gMap.clear();
        if (gpsTracker.canGetLocation()) {

            if (trip_details_ready) {
                title_trip_driver.setText(trip_details.generateTitleString());

                ArrayList<LatLng> latLngList = trip_details.getCoords();

                //Polyline Itinerary
                PolylineOptions polylineOptionsItinerary = new PolylineOptions();
                polylineOptionsItinerary.color(ContextCompat.getColor(TerminalApp.getContext(), R.color.grey));
                polylineOptionsItinerary.width(10F);
                polylineOptionsItinerary.addAll(latLngList);
                polyline_itinerary = gMap.addPolyline(polylineOptionsItinerary);

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
                    if (Objects.requireNonNull(stops_itinerary.get(start_stop_pos + "")).getId().equals(trip_stop.getId())) {
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
                        zIndex = 1;
                    }
                    mcm.addMarkerAndGet(l, ImgAnimationManager.getInstance().getRoundMarkerBitmap(size, color), trip_stop.getName(), msg, zIndex);
                }
                actualizePassengersTrip();
            } else {
                mcm.cameraDefaultPosition(trip_details.getCoordsDriver());
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    /**
     * On récupère les passagers à aller chercher en dehors des arrêts qui sont sur notre chemin
     * et on les affiche dans la liste et sur la carte
     */
    public void actualizePassengersTrip(){

        //RecyclerView
        assert trip_details instanceof TripDetailsDriver;
        ArrayList<PassengerTripDriver> passengers_trip_driver = ((TripDetailsDriver) trip_details).getPassengersFuture();

        if (passengers_trip_driver.isEmpty()) {
            no_passenger_trip_driver.setVisibility(View.VISIBLE);
        }
        // specify an adapter (see also next example)
        PassengerTripDriverAdapter ptda = new PassengerTripDriverAdapter(passengers_trip_driver);
        recycler_trip_driver.setAdapter(ptda);

        //On Map
        MapManager mcm = new MapManager(gMap);
        for(Marker m: passengers_on_trip){
            m.remove();
        }
        ArrayList<LatLng> latLngList = trip_details.getCoords();
        int colorPassengers = ContextCompat.getColor(TerminalApp.getContext(), R.color.violet);
        for (PassengerTripDriver p : passengers_trip_driver) {
            Marker m =  mcm.addMarkerAndGet(
                    latLngList.get(p.getCoordsPos()), ImgAnimationManager.getInstance().getRoundMarkerBitmap(ImgAnimationManager.MARKER_ITINERARY_SIZE, colorPassengers), p.getStartStopName(), "", 10);
            passengers_on_trip.add(m);
        }
    }
}
