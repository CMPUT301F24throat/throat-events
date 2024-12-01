package com.example.pickme.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserProfilesAdapter extends RecyclerView.Adapter<UserProfilesAdapter.EventViewHolder> {

    private final List<User> usersList;
    private List<User> filteredList; // For search filtering
    private final Context context;
    private final OnItemClickListener onItemClickListener;

    public UserProfilesAdapter(Context context, List<User> usersList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.usersList = usersList;
        this.filteredList = new ArrayList<>(usersList); // Initialize with all users
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onDeleteClick(User user, int position);
        void onFacilityClick(User user, int position);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_profile_item_view, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        User user = filteredList.get(position);
        holder.firstNameTitle.setText(user.getFirstName());
        holder.lastNameTitle.setText(user.getLastName());
        holder.idTitle.setText(String.valueOf(position + 1));
        holder.delete.setOnClickListener(v -> onItemClickListener.onDeleteClick(user, position));
        holder.facilityDetails.setOnClickListener(v -> onItemClickListener.onFacilityClick(user, position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView lastNameTitle;
        TextView firstNameTitle;
        TextView idTitle;
        ImageView delete;
        ImageView facilityDetails;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            firstNameTitle = itemView.findViewById(R.id.firstNameTitle);
            lastNameTitle = itemView.findViewById(R.id.lastNameTitle);
            idTitle = itemView.findViewById(R.id.idTitle);
            facilityDetails = itemView.findViewById(R.id.facilityDetails);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    // Method to filter the list
    public void filter(String query) {
        if (TextUtils.isEmpty(query)) {
            filteredList = new ArrayList<>(usersList); // Reset to full list if query is empty
        } else {
            List<User> filtered = new ArrayList<>();
            for (User user : usersList) {
                if (user.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                        user.getLastName().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(user);
                }
            }
            filteredList = filtered;
        }
        notifyDataSetChanged(); // Notify RecyclerView about data changes
    }
}
