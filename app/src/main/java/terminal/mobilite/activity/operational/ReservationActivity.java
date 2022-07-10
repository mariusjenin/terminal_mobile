package terminal.mobilite.activity.operational;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.ConnectedActivity;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.ToolbarActivity;
import terminal.mobilite.controller.ReservationController;
import terminal.mobilite.manager.LocalizationManager;
import terminal.mobilite.model.reservation.*;
import terminal.mobilite.model.trip.ItineraryStop;
import terminal.mobilite.view.ImgAnimationManager;
import terminal.mobilite.manager.MapManager;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Activité de l'écran de réservation d'un trajet
 */
public class ReservationActivity extends ToolbarActivity implements ConnectedActivity, OnMapReadyCallback {
    private SupportMapFragment mapFragment;
    private GoogleMap gMap;

    private TextView msg_reservation;
    private Button date_button;
    private Spinner start_stops_spinner;
    private Spinner nb_place_spinner;
    private Spinner hour_trip_spinner;
    private Spinner end_stops_spinner;
    private TextView msg_under_map;
    private Button submit;
    private ScrollView scroll_view;

    private ReservationTrip reserv;

    private ConstraintLayout const_layout_date_part;
    private ConstraintLayout const_layout_start_stops_part;
    private ConstraintLayout const_layout_hour_trip_part;
    private ConstraintLayout const_layout_end_stops_part;

    private ConstraintLayout const_layout_map_my_position;

    private boolean flag_spinner_start_stops;
    private boolean flag_spinner_hours_trip;
    private boolean flag_spinner_nb_places;
    private boolean flag_spinner_end_stops;

    /**
     * Creation of the ReservationActivity (setting up of the layout and recuperation of all the fields)
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        middleware.verify_and_redirect(this);

        //Get the id of the itinerary

        Bundle extras = getIntent().getExtras();
        String id_itinerary = extras.getString("id_itinerary");
        String name_itinerary = extras.getString("name_itinerary");

        //Put the right layout
        this.setContentView(R.layout.reservation_activity);

        //initialize reserv
        reserv = new ReservationTrip(id_itinerary, name_itinerary);

        //Initialize flags
        flag_spinner_start_stops = false;
        flag_spinner_hours_trip = false;
        flag_spinner_nb_places = false;
        flag_spinner_end_stops = false;

        //get all elements
        mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map_reservation);
        TextView reservation_title = findViewById(R.id.reservation_title);
        msg_reservation = findViewById(R.id.msg_reservation);
        date_button = findViewById(R.id.date_button_reservation);
        start_stops_spinner = findViewById(R.id.start_stops_spinner_reservation);
        nb_place_spinner = findViewById(R.id.nb_place_spinner_reservation);
        hour_trip_spinner = findViewById(R.id.hour_trip_spinner_reservation);
        end_stops_spinner = findViewById(R.id.end_stops_spinner_reservation);
        SwitchCompat switch_my_position = findViewById(R.id.switch_my_position_reservation);
        SwitchCompat switch_travel_alone = findViewById(R.id.switch_travel_alone_reservation);
        msg_under_map = findViewById(R.id.msg_under_map_reservation);
        scroll_view = findViewById(R.id.scrollview_reservation);
        submit = findViewById(R.id.submit_reservation);

        //Constraint layout of Parts
        const_layout_date_part = findViewById(R.id.const_layout_date_part_reservation);
        const_layout_start_stops_part = findViewById(R.id.const_layout_start_stops_part_reservation);
        const_layout_hour_trip_part = findViewById(R.id.const_layout_hour_trip_part_reservation);
        const_layout_end_stops_part = findViewById(R.id.const_layout_end_stops_part_reservation);

        //Other Constraint Layouts
        const_layout_map_my_position = findViewById(R.id.const_layout_map_my_position_reservation);
        ConstraintLayout const_layout_start_stops = findViewById(R.id.const_layout_start_stops_reservation);
        ConstraintLayout const_layout_nb_place = findViewById(R.id.const_layout_nb_place_reservation);
        ConstraintLayout const_layout_hour_trip = findViewById(R.id.const_layout_hour_trip_reservation);
        ConstraintLayout const_layout_end_stops = findViewById(R.id.const_layout_end_stops_reservation);


        switch_my_position.setChecked(reserv.isToMyPosition());
        switch_travel_alone.setChecked(reserv.isTravelAlone());

        switch_my_position.setOnClickListener(v -> {
            reserv.setToMyPosition(!reserv.isToMyPosition());
            ReservationController.getInstance().post_reservation_ongoing(ReservationActivity.this, reserv);
        });
        switch_travel_alone.setOnClickListener(v -> {
            reserv.setTravelAlone(!reserv.isTravelAlone());
            ReservationController.getInstance().post_reservation_ongoing(ReservationActivity.this, reserv);
        });

        const_layout_start_stops.setOnClickListener(v -> start_stops_spinner.performClick());
        const_layout_nb_place.setOnClickListener(v -> nb_place_spinner.performClick());
        const_layout_hour_trip.setOnClickListener(v -> hour_trip_spinner.performClick());
        const_layout_end_stops.setOnClickListener(v -> end_stops_spinner.performClick());

        //all the stuff for the date picker
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            reserv.setYear(year);
            reserv.setMonth(monthOfYear);
            reserv.setDay(dayOfMonth);
            ReservationController.getInstance().post_reservation_ongoing(ReservationActivity.this, reserv);
        };

        date_button.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    dateSetListener,
                    reserv.getYear(),
                    reserv.getMonth(),
                    reserv.getDay()
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        start_stops_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flag_spinner_start_stops) {
                    reserv.setStartStopPos(position);
                    ReservationController.getInstance().post_reservation_ongoing(ReservationActivity.this, reserv);
                } else {
                    flag_spinner_start_stops = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        hour_trip_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flag_spinner_hours_trip) {
                    reserv.setHourTripPos(position);
                    ReservationController.getInstance().post_reservation_ongoing(ReservationActivity.this, reserv);
                } else {
                    flag_spinner_hours_trip = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        nb_place_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flag_spinner_nb_places) {
                    reserv.setNbPlacesPos(position);
                    ReservationController.getInstance().post_reservation_ongoing(ReservationActivity.this, reserv);
                } else {
                    flag_spinner_nb_places = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        end_stops_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flag_spinner_end_stops) {
                    reserv.setEndStopPos(position);
                    ReservationController.getInstance().post_reservation_ongoing(ReservationActivity.this, reserv);
                } else {
                    flag_spinner_end_stops = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        submit.setOnClickListener(v -> ReservationController.getInstance().post_reserve(this, reserv));

        reservation_title.setText(reserv.getNameItinerary());

        ReservationController.getInstance().post_reservation_ongoing(this, reserv);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    /**
     * Donne de nouvelles valeurs aux spinners
     */
    public void setSpinnersValue() {
        //Spinner Start Stops
        ArrayList<String> start_stops_name_list = new ArrayList<>();
        for (StartStop ss : reserv.getStartStopList()) {
            start_stops_name_list.add(ss.toItemString());
        }
        ArrayAdapter<String> adapter_start_stops = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, start_stops_name_list);
        adapter_start_stops.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        start_stops_spinner.setAdapter(adapter_start_stops);
        flag_spinner_start_stops = false;
        start_stops_spinner.setSelection(reserv.getStartStopPos());

        //Spinner nb Place
        ArrayAdapter<String> adapter_nb_place = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reserv.getNbPlacesList());
        adapter_nb_place.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nb_place_spinner.setAdapter(adapter_nb_place);
        flag_spinner_nb_places = false;
        nb_place_spinner.setSelection(reserv.getNbPlacesPos());


        //Spinner Hour trip
        ArrayList<String> hour_trip_list = new ArrayList<>();
        for (HourTrip ht : reserv.getHourTripList()) {
            if (reserv.isToMyPosition() && !reserv.notChangedIdInGeo()) {
                hour_trip_list.add(reserv.getTimeStart(ht.toItemString()));
            } else {
                hour_trip_list.add(ht.toItemString());
            }
        }
        ArrayAdapter<String> adapter_hour_trip = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hour_trip_list);
        adapter_hour_trip.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hour_trip_spinner.setAdapter(adapter_hour_trip);
        flag_spinner_hours_trip = false;
        hour_trip_spinner.setSelection(reserv.getHourTripPos());


        //Spinner End Stops
        ArrayList<String> end_stops_list = new ArrayList<>();
        for (EndStop es : reserv.getEndStopList()) {
            end_stops_list.add(es.toItemString());
        }
        ArrayAdapter<String> adapter_end_stops = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, end_stops_list);
        adapter_end_stops.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        end_stops_spinner.setAdapter(adapter_end_stops);
        flag_spinner_end_stops = false;
        end_stops_spinner.setSelection(reserv.getEndStopPos());


    }

    /**
     * Actualise l'affichage des champs en fonction des valeurs selectionnées
     */
    public void actualizeView() {
        boolean enabled = reserv.getEnabled();
        submit.setEnabled(enabled);
        if (enabled) {
            msg_reservation.setVisibility(View.VISIBLE);
            msg_reservation.setText(reserv.getMsgReservation());
        } else {
            msg_reservation.setVisibility(View.GONE);
        }
        date_button.setText(reserv.getDateForDisplay());
        if (reserv.isToMyPosition()) {
            if (!enabled) {
                msg_under_map.setVisibility(TextView.GONE);
            } else {
                int dist = reserv.getDistFromPosition();

                msg_under_map.setVisibility(TextView.VISIBLE);
                String msg = TerminalApp.getContext().getString(R.string.you_are_at)+" " +
                        dist + TerminalApp.getContext().getString(R.string.meter_of_the_itinerary);
                msg_under_map.setText(msg);
            }

            const_layout_map_my_position.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                    this.getResources().getDisplayMetrics()));

            const_layout_date_part.setVisibility(View.GONE);

            reserv.initDate();
            const_layout_start_stops_part.setVisibility(View.GONE);
            const_layout_hour_trip_part.setVisibility(View.VISIBLE);

        } else {
            msg_under_map.setVisibility(TextView.GONE);
            const_layout_map_my_position.setElevation(0);

            const_layout_date_part.setVisibility(View.VISIBLE);

            const_layout_hour_trip_part.setVisibility(View.VISIBLE);

            int start_stops_visible = reserv.notChangedStartStopPos() ? View.GONE : View.VISIBLE;
            int hours_trip_visible = reserv.notChangedHourTripPos() ? View.GONE : View.VISIBLE;
            int end_stops_visible = reserv.notChangedEndStopPos() ? View.GONE : View.VISIBLE;
            const_layout_start_stops_part.setVisibility(start_stops_visible);
            const_layout_hour_trip_part.setVisibility(hours_trip_visible);
            const_layout_end_stops_part.setVisibility(end_stops_visible);
        }
    }

    /**
     * A chaque actualisation de la carte on recharge l'affichage
     * @param googleMap
     */
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style));
        ImageView transparent_for_map_reservation = findViewById(R.id.transparent_for_map_reservation);
        transparent_for_map_reservation.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:

                case MotionEvent.ACTION_MOVE:
                    scroll_view.requestDisallowInterceptTouchEvent(true);
                    return false;

                case MotionEvent.ACTION_UP:
                    scroll_view.requestDisallowInterceptTouchEvent(false);
                    return true;

                default:
                    return true;
            }
        });


        LocalizationManager gpsTracker = TerminalApp.getGpsTracker();
        if (gpsTracker.canGetLocation()) {
            LatLng defaultLocation = gpsTracker.getLatLng();
            MapManager mcm = new MapManager(gMap);
            mcm.removeAllMarker();
            mcm.cameraDefaultPosition(defaultLocation);

            Marker m = mcm.addMarkerAndGet(defaultLocation, ImgAnimationManager.getInstance().getRoundMarkerBitmap(ImgAnimationManager.MARKER_POSITION_SIZE, ContextCompat.getColor(TerminalApp.getContext(), R.color.vibrantBlue)), getString(R.string.title_marker_position_home), getString(R.string.descr_marker_position_home), 0);
            m.showInfoWindow();
        } else {
            gpsTracker.showSettingsAlert();
        }

    }

    /**
     * Actualise l'affichage de la carte, de l'itinéraire et du trajet selectionné
     */
    public void actualizeMap() {
        gMap.clear();


        ArrayList<LatLng> latLngList = reserv.getCoords();

        //Polyline Itinerary
        PolylineOptions polylineOptionsItinerary = new PolylineOptions();
        polylineOptionsItinerary.color(ContextCompat.getColor(TerminalApp.getContext(), R.color.grey));
        polylineOptionsItinerary.width(10F);
        polylineOptionsItinerary.addAll(latLngList);
        gMap.addPolyline(polylineOptionsItinerary);

        Log.e("",(!(reserv.notChangedStartStopPos() && reserv.notChangedIdInGeo()) && !reserv.notChangedEndStopPos())?"true":"false");
        if (!(reserv.notChangedStartStopPos() && reserv.notChangedIdInGeo()) && !reserv.notChangedEndStopPos()) {
            //Polyline Trip
            PolylineOptions polylineOptionsTrip = new PolylineOptions();
            polylineOptionsTrip.color(ContextCompat.getColor(TerminalApp.getContext(), R.color.vibrantBlue));
            polylineOptionsTrip.width(15F);
            int[] tripPos = reserv.getTripPos();
            polylineOptionsTrip.addAll(new ArrayList<>(latLngList.subList(tripPos[0], tripPos[1])));
            gMap.addPolyline(polylineOptionsTrip);
        }


        MapManager mcm = new MapManager(gMap);
        if(latLngList.size()>0){
            mcm.animateCameraAroundBounds(latLngList);
        }

        LocalizationManager gpsTracker = TerminalApp.getGpsTracker();
        if (gpsTracker.canGetLocation()) {
            LatLng defaultLocation = gpsTracker.getLatLng();
            mcm.addMarkerAndGet(defaultLocation, ImgAnimationManager.getInstance().getRoundMarkerBitmap(ImgAnimationManager.MARKER_POSITION_SIZE, ContextCompat.getColor(TerminalApp.getContext(), R.color.vibrantBlue)), getString(R.string.title_marker_position_home), getString(R.string.descr_marker_position_home), 1);
        } else {
            gpsTracker.showSettingsAlert();
        }
//        MARKER Tous les arrêts
        TreeMap<String, ItineraryStop> stops_itinerary = reserv.getStopsItinerary();
        ArrayList<StartStop> start_stops = reserv.getStartStopList();
        ArrayList<EndStop> end_stops = reserv.getEndStopList();
        int start_stop_pos;
        int end_stop_pos;
        String ss_beginning_id;
        String ss_ending_id;
        if (!reserv.notChangedStartStopPos() && !reserv.notChangedEndStopPos()) {
            start_stop_pos = reserv.getStartStopPos();
            end_stop_pos = reserv.getEndStopPos();
            ss_beginning_id = start_stops.get(start_stop_pos).getId();
            ss_ending_id = end_stops.get(end_stop_pos).getId();
        } else {
            ss_beginning_id = Objects.requireNonNull(stops_itinerary.firstEntry()).getValue().getId();
            ss_ending_id = Objects.requireNonNull(stops_itinerary.lastEntry()).getValue().getId();
        }
        int colorBegin = ContextCompat.getColor(TerminalApp.getContext(), R.color.green);
        String msgBegin = getString(R.string.from_here_position_reservation);
        int colorEnd = ContextCompat.getColor(TerminalApp.getContext(), R.color.red);
        String msgEnd = getString(R.string.to_here_position_reservation);
        long sizeStandard = ImgAnimationManager.MARKER_ITINERARY_SIZE;
        long sizeBig = ImgAnimationManager.MARKER_ITINERARY_SIZE_BIGGER;
        if (!reserv.notChangedIdInGeo()) {
            mcm.addMarkerAndGet(latLngList.get(reserv.getIdInGeo()), ImgAnimationManager.getInstance().getRoundMarkerBitmap(sizeBig, colorBegin), getString(R.string.title_marker_position_home), msgBegin, 10);
        }
        for (String key : stops_itinerary.keySet()) {
            ItineraryStop itin_stop = stops_itinerary.get(key);
            assert itin_stop != null;
            LatLng l = itin_stop.getCoords();
            int color = ContextCompat.getColor(TerminalApp.getContext(), R.color.black);
            String msg = "";
            long size = sizeStandard;
            int zIndex = 0;
            boolean affichSpe = false;
            //START
            if (reserv.notChangedIdInGeo() && ss_beginning_id.equals(itin_stop.getId())) {
                color = colorBegin;
                msg = msgBegin;
                affichSpe = true;
            } else
                //END
                if (ss_ending_id.equals(itin_stop.getId())) {
                    color = colorEnd;
                    msg = msgEnd;
                    affichSpe = true;
                }
            //START OR END
            if (affichSpe) {
                size = sizeBig;
                zIndex = 2;
            }
            mcm.addMarkerAndGet(l, ImgAnimationManager.getInstance().getRoundMarkerBitmap(size, color), itin_stop.getName(), msg, zIndex);
        }
    }

    public void resetFlagsSpinner() {
        flag_spinner_start_stops = false;
        flag_spinner_hours_trip = false;
        flag_spinner_nb_places = false;
        flag_spinner_end_stops = false;
    }
}
