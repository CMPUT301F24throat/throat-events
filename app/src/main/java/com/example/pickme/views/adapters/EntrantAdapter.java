package com.example.pickme.views.adapters;

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

public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {

    private List<WaitingListEntrant> entrants;

    public EntrantAdapter(List<WaitingListEntrant> entrants) {
        this.entrants = entrants;
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrant, parent, false);
        return new EntrantViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    public void updateEntrants(List<WaitingListEntrant> newEntrants) {
        this.entrants = newEntrants;
        notifyDataSetChanged();
    }

    static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantName;

        EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantName = itemView.findViewById(R.id.entrantName);
        }
    }
}