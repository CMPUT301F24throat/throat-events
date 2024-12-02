package com.example.pickme.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.User;

import java.util.List;

/**
 * Adapter for displaying winner information in a RecyclerView.
 */
public class WinnerAdapter extends RecyclerView.Adapter<WinnerAdapter.WinnerViewHolder> {

    private List<User> winnerList;

    /**
     * Constructor for WinnerAdapter.
     *
     * @param winnerList The list of winners to display.
     */
    public WinnerAdapter(List<User> winnerList) {
        this.winnerList = winnerList;
    }

    /**
     * Called when the RecyclerView needs a new ViewHolder.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new WinnerViewHolder instance.
     */
    @NonNull
    @Override
    public WinnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_winner, parent, false);
        return new WinnerViewHolder(view);
    }

    /**
     * Called to bind data to the ViewHolder.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull WinnerViewHolder holder, int position) {
        User user = winnerList.get(position);
        String userName = user.getFirstName();
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            userName += " " + user.getLastName();
        }
        holder.userName.setText(userName);
        holder.winnerNumber.setText(String.valueOf(position + 1));
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return The total number of items.
     */
    @Override
    public int getItemCount() {
        return winnerList.size();
    }

    /**
     * ViewHolder class for winner items.
     */
    public static class WinnerViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView winnerNumber;

        /**
         * Constructor for WinnerViewHolder.
         *
         * @param itemView The view of the winner item.
         */
        public WinnerViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            winnerNumber = itemView.findViewById(R.id.winnerNumber);
        }
    }
}