package com.example.pickme.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.WaitingListEntrant;

import java.util.List;

public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.ViewHolder> {

    private final List<WaitingListEntrant> waitingListEntrants;

    public WaitingListAdapter(List<WaitingListEntrant> waitingListEntrants) {
        this.waitingListEntrants = waitingListEntrants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waitinglist_entrant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaitingListEntrant entrant = waitingListEntrants.get(position);
        holder.entrantNameTextView.setText((position + 1) + ". " + entrant.getEntrantId()); // Adding position number
    }

    @Override
    public int getItemCount() {
        return waitingListEntrants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView entrantNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.entrantName);
        }
    }
}