package com.example.pickme.views.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.User;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.UserRepository;

import java.util.List;

/**
 * Adapter class for displaying entrants in a RecyclerView.
 */
public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {

    private List<WaitingListEntrant> entrants;

    /**
     * Constructor for EntrantAdapter.
     *
     * @param entrants The list of entrants to display.
     */
    public EntrantAdapter(List<WaitingListEntrant> entrants) {
        this.entrants = entrants;
    }

    /**
     * Creates and returns a new EntrantViewHolder.
     *
     * @param parent The parent view group.
     * @param viewType The view type of the new view.
     * @return A new EntrantViewHolder.
     */
    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrant, parent, false);
        return new EntrantViewHolder(view);
    }

    /**
     * Binds the data to the ViewHolder.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        WaitingListEntrant entrant = entrants.get(position);
        UserRepository userRepository = UserRepository.getInstance();
        userRepository.getUserByDeviceId(entrant.getEntrantId(), task -> {
            if (task.isSuccessful()) {
                User user = task.getResult();
                String name = user.getFirstName();
                if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                    name += " " + user.getLastName();
                }
                holder.entrantName.setText((position + 1) + ". " + name);
            }
        });
    }

    /**
     * Returns the total number of items in the data set.
     *
     * @return The total number of items.
     */
    @Override
    public int getItemCount() {
        return entrants.size();
    }

    /**
     * Updates the list of entrants and notifies the adapter.
     *
     * @param newEntrants The new list of entrants.
     */
    public void updateEntrants(List<WaitingListEntrant> newEntrants) {
        this.entrants = newEntrants;

        for(WaitingListEntrant entrant : entrants){
            Log.i("LOTTERY", "overview showing ID: " + entrant.getEntrantId());
        }
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for entrant items.
     */
    static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantName;

        /**
         * Constructor for EntrantViewHolder.
         *
         * @param itemView The view of the item.
         */
        EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantName = itemView.findViewById(R.id.entrantName);
        }
    }
}