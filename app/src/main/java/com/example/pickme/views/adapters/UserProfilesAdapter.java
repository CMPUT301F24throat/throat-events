package com.example.pickme.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.User;

import java.util.List;

public class UserProfilesAdapter extends RecyclerView.Adapter<UserProfilesAdapter.EventViewHolder> {

    private final List<User> usersList;
    private final Context context;
    private final OnItemClickListener onItemClickListener;

    public UserProfilesAdapter(Context context, List<User> usersList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.usersList = usersList;
        this.onItemClickListener = onItemClickListener;
    }

    // Define an interface for click listener
    public interface OnItemClickListener {
        void onDeleteClick(User user, int position);
        void onFacilityClick(User user, int position);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout
        View view = LayoutInflater.from(context).inflate(R.layout.user_profile_item_view, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Bind data to the view
        User user = usersList.get(position);
        holder.firstNameTitle.setText(user.getFirstName());
        holder.idTitle.setText(position+1);
        holder.lastNameTitle.setText(position+1);
        // Set the click listener for the item
        holder.delete.setOnClickListener(v -> onItemClickListener.onDeleteClick(user, position));
        holder.facilityDetails.setOnClickListener(v -> onItemClickListener.onFacilityClick(user, position));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    // ViewHolder class
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView lastNameTitle;
        TextView firstNameTitle;
        TextView idTitle;
        ImageView delete;
        ImageView facilityDetails;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Bind the TextView
            firstNameTitle = itemView.findViewById(R.id.firstNameTitle);
            lastNameTitle = itemView.findViewById(R.id.lastNameTitle);
            idTitle = itemView.findViewById(R.id.idTitle);
            facilityDetails = itemView.findViewById(R.id.facilityDetails);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}