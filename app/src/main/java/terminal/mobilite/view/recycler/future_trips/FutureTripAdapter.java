package terminal.mobilite.view.recycler.future_trips;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import terminal.mobilite.activity.R;
import terminal.mobilite.model.future_trips.FutureTrip;

import java.util.ArrayList;

/**
 * Adapter pour RecyclerView de trajets à venir
 */
public class FutureTripAdapter extends RecyclerView.Adapter<FutureTripViewHolder> {
    protected final ArrayList<FutureTrip> list;

    public FutureTripAdapter(ArrayList<FutureTrip> l){
        this.list=l;
    }

    @Override
    public FutureTripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_future_trips,parent,false);
        return new FutureTripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FutureTripViewHolder holder, int position) {
        holder.updateWithModel(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
