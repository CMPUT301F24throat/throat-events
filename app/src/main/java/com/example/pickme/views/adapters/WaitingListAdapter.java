package com.example.pickme.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.UserRepository;

import java.util.List;

/**
 * Adapter class for displaying waiting list entrants in a RecyclerView.
 */
public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.ViewHolder> {

    private final List<WaitingListEntrant> waitingListEntrants;
    Context context;

    /**
     * Constructor for WaitingListAdapter.
     *
     * @param waitingListEntrants the list of waiting list entrants to display
     * @param context the context in which the adapter is used
     */
    public WaitingListAdapter(List<WaitingListEntrant> waitingListEntrants, Context context) {
        this.waitingListEntrants = waitingListEntrants;
        this.context = context;
    }

    /**
     * Called when the RecyclerView needs a new ViewHolder.
     *
     * @param parent the parent ViewGroup
     * @param viewType the view type of the new View
     * @return a new ViewHolder instance
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waitinglist_entrant_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called to bind data to the ViewHolder.
     *
     * @param holder the ViewHolder to bind data to
     * @param position the position of the item in the data set
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaitingListEntrant entrant = waitingListEntrants.get(position);

        UserRepository.getInstance().getUserByDeviceId(entrant.getEntrantId(), task -> {
            String entrantName;

            if(!task.isSuccessful() || task.getResult() == null){
                entrantName = "Error";
            }
            else{
                String lastName = task.getResult().getLastName();

                entrantName = task.getResult().getFirstName() + (lastName != null ? " " + lastName : "");
            }

            holder.entrantNameTextView.setText((position + 1) + ". " + entrantName); // Adding position number
        });

        holder.entrantStatusTextView.setText(entrant.getStatus().toString());

        int colorID;
        EntrantStatus status = entrant.getStatus();

        if(status == EntrantStatus.WAITING){
            colorID = R.color.text3;
        }
        else if(status == EntrantStatus.ACCEPTED || status == EntrantStatus.SELECTED){
            colorID = R.color.green1;
        }
        else{
            colorID = R.color.red1;
        }

        holder.entrantStatusTextView.setTextColor(ContextCompat.getColor(this.context, colorID));
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return the total number of items
     */
    @Override
    public int getItemCount() {
        return waitingListEntrants.size();
    }

    /**
     * ViewHolder class to hold references to the views for each item in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView entrantNameTextView;
        public TextView entrantStatusTextView;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView the view of the item
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.entrantName);
            entrantStatusTextView = itemView.findViewById(R.id.entrantStatus);
        }
    }
}