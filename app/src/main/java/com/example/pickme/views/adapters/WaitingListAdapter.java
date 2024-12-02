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

public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.ViewHolder> {

    private final List<WaitingListEntrant> waitingListEntrants;
    Context context;

    public WaitingListAdapter(List<WaitingListEntrant> waitingListEntrants, Context context) {
        this.waitingListEntrants = waitingListEntrants;
        this.context = context;
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

    @Override
    public int getItemCount() {
        return waitingListEntrants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView entrantNameTextView;
        public TextView entrantStatusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.entrantName);
            entrantStatusTextView = itemView.findViewById(R.id.entrantStatus);
        }
    }
}