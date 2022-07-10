package terminal.mobilite.view.recycler.itineraries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import terminal.mobilite.activity.R;
import terminal.mobilite.model.itineraries.Itinerary;

import java.util.ArrayList;

/**
 * Adapter pour RecyclerView d'itineraires
 */
public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryViewHolder> {
    protected final ArrayList<Itinerary> list;

    public ItineraryAdapter(ArrayList<Itinerary> l){
        this.list=l;
    }

    @Override
    public ItineraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_itineraries,parent,false);
        return new ItineraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItineraryViewHolder holder, int position) {
        holder.updateWithModel(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
