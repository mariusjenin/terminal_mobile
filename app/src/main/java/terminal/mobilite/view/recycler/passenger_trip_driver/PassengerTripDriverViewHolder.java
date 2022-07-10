package terminal.mobilite.view.recycler.passenger_trip_driver;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.R;
import terminal.mobilite.manager.DateManager;
import terminal.mobilite.model.trip_details.PassengerTripDriver;
import terminal.mobilite.view.ImgAnimationManager;

/**
 * ViewHolder pour RecyclerView d'utilisateurs en dehors des arrêts
 */
public class PassengerTripDriverViewHolder extends RecyclerView.ViewHolder {
    private final ImageView arrow_1_item_passenger_trip_driver;
    private final ImageView objective_img_item_passenger_trip_driver;
    private final ImageView arrow_2_item_passenger_trip_driver;
    private final TextView start_stop_item_trip_driver;
    private final TextView end_stop_item_trip_driver;
    private final TextView time_item_passenger_trip_driver;

    public PassengerTripDriverViewHolder(View itemView) {
        super(itemView);
        start_stop_item_trip_driver = itemView.findViewById(R.id.start_stop_item_trip_driver);
        end_stop_item_trip_driver = itemView.findViewById(R.id.end_stop_item_trip_driver);
        time_item_passenger_trip_driver = itemView.findViewById(R.id.time_item_passenger_trip_driver);
        arrow_1_item_passenger_trip_driver = itemView.findViewById(R.id.arrow_1_item_passenger_trip_driver);
        objective_img_item_passenger_trip_driver = itemView.findViewById(R.id.objective_img_item_passenger_trip_driver);
        arrow_2_item_passenger_trip_driver = itemView.findViewById(R.id.arrow_2_item_passenger_trip_driver);
        arrow_1_item_passenger_trip_driver.setImageBitmap(ImgAnimationManager.getInstance().getArrowBitmap(TerminalApp.getContext()));
        objective_img_item_passenger_trip_driver.setImageBitmap(ImgAnimationManager.getInstance().getRoundMarkerBitmap(ImgAnimationManager.MARKER_POSITION_SIZE, ContextCompat.getColor(itemView.getContext(), R.color.violet)));
        arrow_2_item_passenger_trip_driver.setImageBitmap(ImgAnimationManager.getInstance().getArrowBitmap(TerminalApp.getContext()));
    }

    public void updateWithModel(PassengerTripDriver ptd) {
        start_stop_item_trip_driver.setText(ptd.getStartStopName());
        end_stop_item_trip_driver.setText(ptd.getEndStopName());
        int time = ptd.getTripDetails().getTimeFromDriverToPassengers(ptd.getStartStopPos(), ptd.getEndStopPos(), ptd.getCoordsPos());
        String text = "";
        if(time != 0){
            text += "≈ ";
        }
        text += DateManager.getInstance().format(time);
        time_item_passenger_trip_driver.setText(text);
    }
}
