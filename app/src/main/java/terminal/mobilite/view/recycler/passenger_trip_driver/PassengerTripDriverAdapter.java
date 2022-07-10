package terminal.mobilite.view.recycler.passenger_trip_driver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import terminal.mobilite.activity.R;
import terminal.mobilite.model.trip_details.PassengerTripDriver;

import java.util.ArrayList;

/**
 * Adapter pour RecyclerView d'utilisateurs en dehors des arrêts
 */
public class PassengerTripDriverAdapter extends RecyclerView.Adapter<PassengerTripDriverViewHolder> {
    protected final ArrayList<PassengerTripDriver> list;

    public PassengerTripDriverAdapter(ArrayList<PassengerTripDriver> l){
        this.list=l;
    }

    @Override
    public PassengerTripDriverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_passenger_trip_driver,parent,false);
        return new PassengerTripDriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PassengerTripDriverViewHolder holder, int position) {
        holder.updateWithModel(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
