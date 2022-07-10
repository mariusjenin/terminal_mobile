package terminal.mobilite.view.recycler.itineraries;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.operational.ReservationActivity;
import terminal.mobilite.manager.ConnectionManager;
import terminal.mobilite.model.itineraries.Itinerary;
import terminal.mobilite.model.itineraries.StopInItineray;
import terminal.mobilite.model.itineraries.TripInItineray;
import terminal.mobilite.model.user.Utilisateur;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ViewHolder pour RecyclerView d'itineraires
 */
public class ItineraryViewHolder extends RecyclerView.ViewHolder {
    private final TextView btn_title_itinerary;
    private final ImageButton btn_reserve_trip_itinerary_item;
    private final TableLayout tablelayout;

    public ItineraryViewHolder(View itemView) {
        super(itemView);
        btn_title_itinerary = itemView.findViewById(R.id.title_itinerary_item);
        btn_reserve_trip_itinerary_item = itemView.findViewById(R.id.reserve_trip_itinerary_item);
        ImageButton btn_toggle_view_expanded = itemView.findViewById(R.id.toggle_view_expanded_itinary);
        tablelayout = itemView.findViewById(R.id.table_itinerary);
        LinearLayout layout_table_itinerary = itemView.findViewById(R.id.layout_table_itinerary);

        tablelayout.setStretchAllColumns(true);

        layout_table_itinerary.setVisibility(LinearLayout.GONE);

        btn_toggle_view_expanded.setOnClickListener(v -> {
            float deg = 0f;
            int visibility = LinearLayout.GONE;
            float elevation = 3f;
            if (!(layout_table_itinerary.getVisibility() == LinearLayout.VISIBLE)) {
                visibility = LinearLayout.VISIBLE;
                deg = 180.05f; //Obligé de mettre .05 car sinon les ombres disparaissent
                elevation = 1f;
            }
            int finalVisibility = visibility;
            float finalElevation = elevation;
            btn_toggle_view_expanded.animate()
                    .withLayer()
                    .setDuration(200L)
                    .rotation(deg)
                    .setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(() -> {
                btn_toggle_view_expanded.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, finalElevation,
                        itemView.getResources().getDisplayMetrics()));
                layout_table_itinerary.setVisibility(finalVisibility);
            });
        });
    }

    public void updateWithModel(Itinerary itinerary) {
        if (ConnectionManager.getInstance().getUtilisateur().getType().equals(Utilisateur.PASSENGER_TYPE)) {
            btn_reserve_trip_itinerary_item.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ReservationActivity.class);
                Bundle b = new Bundle();
                b.putString("id_itinerary", itinerary.getId_itineraire());
                b.putString("name_itinerary", itinerary.getNom());
                intent.putExtras(b);
                v.getContext().startActivity(intent);
            });
        } else {
            btn_reserve_trip_itinerary_item.setVisibility(View.GONE);
        }

        Drawable vertical_divider = ResourcesCompat.getDrawable(tablelayout.getContext().getResources(), R.drawable.vertical_black_divider_linear_layout, null);

        btn_title_itinerary.setText(itinerary.getNom());

        if (itinerary.hasTrips()) {
            ArrayList<StopInItineray> stops = itinerary.getStops();

            TableRow entete = new TableRow(itemView.getContext());
            entete.setDividerDrawable(vertical_divider);
            entete.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

            ArrayList<TripInItineray> trips_unique = itinerary.getTripOfRepetition(TripInItineray.UNIQUE_REPETITION);
            ArrayList<TripInItineray> trips_weekly = itinerary.getTripOfRepetition(TripInItineray.HEBDOMADAIRE_REPETITION);
            ArrayList<TripInItineray> trips_monthly = itinerary.getTripOfRepetition(TripInItineray.MENSUEL_REPETITION);
            ArrayList<TripInItineray> trips_annually = itinerary.getTripOfRepetition(TripInItineray.ANNUEL_REPETITION);
            ArrayList<TripInItineray> trips_work_days = itinerary.getTripOfRepetition(TripInItineray.OUVRES_REPETITION);
            ArrayList<TripInItineray> trips_weekend = itinerary.getTripOfRepetition(TripInItineray.WEEKEND_REPETITION);


            addTextView(1, entete, TerminalApp.getContext().getString(R.string.table_itinerary_stops_title), Color.BLACK, true);

            if (trips_work_days.size() > 0) {
                addTextView(trips_work_days.size(), entete, TerminalApp.getContext().getString(R.string.table_itinerary_workdays_title), Color.BLACK, true);
            }
            if (trips_weekend.size() > 0) {
                addTextView(trips_weekend.size(), entete, TerminalApp.getContext().getString(R.string.table_itinerary_weekend_title), Color.BLACK, true);
            }

            HashMap<String, ArrayList<TripInItineray>> trips = new HashMap<>();

            for (TripInItineray trip : trips_unique) {
                ArrayList<TripInItineray> trips_arr_list;
                String date_depart = trip.getDateDepart();
                if (trips.containsKey(date_depart)) {
                    trips_arr_list = trips.get(date_depart);
                } else {
                    trips_arr_list = new ArrayList<>();
                }
                assert trips_arr_list != null;
                trips_arr_list.add(trip);
                trips.put(date_depart, trips_arr_list);
            }
            for (TripInItineray trip : trips_weekly) {
                ArrayList<TripInItineray> trips_arr_list;
                String date_depart = trip.getDateDepart();
                if (trips.containsKey(date_depart)) {
                    trips_arr_list = trips.get(date_depart);
                } else {
                    trips_arr_list = new ArrayList<>();
                }
                assert trips_arr_list != null;
                trips_arr_list.add(trip);
                trips.put(date_depart, trips_arr_list);
            }
            for (TripInItineray trip : trips_monthly) {
                ArrayList<TripInItineray> trips_arr_list;
                String date_depart = trip.getDateDepart();
                if (trips.containsKey(date_depart)) {
                    trips_arr_list = trips.get(date_depart);
                } else {
                    trips_arr_list = new ArrayList<>();
                }
                assert trips_arr_list != null;
                trips_arr_list.add(trip);
                trips.put(date_depart, trips_arr_list);
            }
            for (TripInItineray trip : trips_annually) {
                ArrayList<TripInItineray> trips_arr_list;
                String date_depart = trip.getDateDepart();
                if (trips.containsKey(date_depart)) {
                    trips_arr_list = trips.get(date_depart);
                } else {
                    trips_arr_list = new ArrayList<>();
                }
                assert trips_arr_list != null;
                trips_arr_list.add(trip);
                trips.put(date_depart, trips_arr_list);
            }

            for (ArrayList<TripInItineray> trips_list : trips.values()) {
                addTextView(trips_list.size(), entete, trips_list.get(0).getDateDepart(), Color.BLACK, true);
            }


            TableRow[] rows = new TableRow[stops.size()];
            for (int i = 0; i < rows.length; i++) {
                rows[i] = new TableRow(itemView.getContext());
                rows[i].setDividerDrawable(vertical_divider);
                rows[i].setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

                addTextView(1, rows[i], stops.get(i).getNom_arret(), Color.BLACK, false);

                for (TripInItineray trip : trips_work_days) {
                    addTextView(1, rows[i], trip.getTimePassage().get(i).getTime(), -1, false);
                }
                for (TripInItineray trip : trips_weekend) {
                    addTextView(1, rows[i], trip.getTimePassage().get(i).getTime(), -1, false);
                }


                for (TripInItineray trip : trips_weekly) {
                    addTextView(1, rows[i], trip.getTimePassage().get(i).getTime(), -1, false);
                }
                for (TripInItineray trip : trips_monthly) {
                    addTextView(1, rows[i], trip.getTimePassage().get(i).getTime(), -1, false);
                }
                for (TripInItineray trip : trips_annually) {
                    addTextView(1, rows[i], trip.getTimePassage().get(i).getTime(), -1, false);
                }
                for (TripInItineray trip : trips_unique) {
                    addTextView(1, rows[i], trip.getTimePassage().get(i).getTime(), -1, false);
                }
            }

            tablelayout.addView(entete);
            for (TableRow row : rows) {
                tablelayout.addView(row);
            }
        } else {
            TableRow no_trips = new TableRow(itemView.getContext());
            addTextView(1, no_trips, TerminalApp.getContext().getString(R.string.no_trips_for_itinerary), Color.GRAY, false);
            tablelayout.addView(no_trips);
        }
    }

    public void addTextView(int span, TableRow t_row, String text, int text_color, boolean bold) {
        TextView tv = new TextView(itemView.getContext());

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        params.span = span;
        tv.setLayoutParams(params);
        tv.setLines(1);

        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        int padding_x_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                itemView.getResources().getDisplayMetrics());
        int padding_y_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                itemView.getResources().getDisplayMetrics());
        tv.setPadding(padding_x_dp, padding_y_dp, padding_x_dp, padding_y_dp);
        if (text_color != -1) {
            tv.setTextColor(text_color);
        }
        if (bold) {
            tv.setTypeface(null, Typeface.BOLD);
        }

        tv.setText(text);
        t_row.addView(tv);
    }
}
