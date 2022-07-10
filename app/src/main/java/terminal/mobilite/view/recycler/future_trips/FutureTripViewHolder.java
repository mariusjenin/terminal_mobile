package terminal.mobilite.view.recycler.future_trips;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.TerminalActivity;
import terminal.mobilite.activity.operational.TripDriverActivity;
import terminal.mobilite.activity.operational.TripPassengerActivity;
import terminal.mobilite.manager.ConnectionManager;
import terminal.mobilite.model.future_trips.FutureTrip;
import terminal.mobilite.model.user.Utilisateur;

/**
 * ViewHolder pour RecyclerView de trajets à venir
 */
public class FutureTripViewHolder extends RecyclerView.ViewHolder {
    private final TextView title;
    private final TextView date;
    private final TextView stop_1;
    private final TextView h_stop_1;
    private final TextView stop_2;
    private final TextView h_stop_2;
    private final ConstraintLayout const_layout_item_future_trip;

    public FutureTripViewHolder(View itemView) {
        super(itemView);
        const_layout_item_future_trip = itemView.findViewById(R.id.const_layout_item_future_trip);
        LinearLayout lin_layout = itemView.findViewById(R.id.layout_item_future_trip_home);
        LinearLayout lin_lay_1 = (LinearLayout) lin_layout.getChildAt(0);
        LinearLayout lin_lay_2 = (LinearLayout) lin_layout.getChildAt(1);
        LinearLayout lin_lay_3 = (LinearLayout) lin_layout.getChildAt(2);
        title = (TextView) lin_lay_1.getChildAt(0);
        date = (TextView) lin_lay_1.getChildAt(1);
        stop_1 = (TextView) lin_lay_2.getChildAt(0);
        h_stop_1 = (TextView) lin_lay_2.getChildAt(1);
        stop_2 = (TextView) lin_lay_3.getChildAt(0);
        h_stop_2 = (TextView) lin_lay_3.getChildAt(1);
    }

    public void updateWithModel(FutureTrip ft){
        title.setText(ft.getTitre());
        date.setText(ft.getDateDepart());
        stop_1.setText(ft.getArretDepart());
        h_stop_1.setText(ft.getHeureDepart());
        stop_2.setText(ft.getArretFin());
        h_stop_2.setText(ft.getHeureFin());
        const_layout_item_future_trip.setOnClickListener(v -> {
            String type_user = ConnectionManager.getInstance().getUtilisateur().getType();
            TerminalActivity act = TerminalApp.getCurrentAct();
            if(type_user.equals(Utilisateur.PASSENGER_TYPE)){
                Intent intent = new Intent(act, TripPassengerActivity.class);
                Bundle b = new Bundle();
                b.putString("id", ft.getIdReserv());
                intent.putExtras(b);
                act.startActivity(intent);
            } else if(type_user.equals(Utilisateur.DRIVER_TYPE)){
                Intent intent = new Intent(act, TripDriverActivity.class);
                Bundle b = new Bundle();
                b.putString("id", ft.getIdTrajet());
                intent.putExtras(b);
                act.startActivity(intent);
            }
        });
    }
}
