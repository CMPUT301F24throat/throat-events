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

/**
 * Adapter class for displaying a list of user profiles in a RecyclerView.
 * Allows interaction with each item through click listeners.
 */
public class UserProfilesAdapter extends RecyclerView.Adapter<UserProfilesAdapter.EventViewHolder> {

    private List<User> usersList;
    private List<User> filteredList;
    private final Context context;
    private final OnItemClickListener onItemClickListener;

    /**
     * Constructor to initialize the adapter with a list of users and click listener.
     *
     * @param context The context for inflating the views.
     * @param usersList The list of users to display.
     * @param onItemClickListener Listener for item click events.
     */
    public UserProfilesAdapter(Context context, List<User> usersList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.usersList = usersList;
        this.filteredList = new ArrayList<>(usersList); // Initialize with all users
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Interface to handle item click events such as deleting and viewing facility details.
     */
    public interface OnItemClickListener {
        /**
         * Called when the delete button is clicked for a user.
         *
         * @param user The user being deleted.
         * @param position The position of the user in the list.
         */
        void onDeleteClick(User user, int position);
        /**
         * Called when the facility details button is clicked for a user.
         *
         * @param user The user whose facility details are being viewed.
         * @param position The position of the user in the list.
         */
        void onFacilityClick(User user, int position);
    }

    /**
     * Creates a new ViewHolder for user profile items.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type for the new view.
     * @return A new EventViewHolder object.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_profile_item_view, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds the data to the ViewHolder for a specific position in the filtered list.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item within the filtered list.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        User user = filteredList.get(position);
        holder.idTitle.setText(user.getDeviceId());
        holder.fullNameTitle.setText(user.fullName(user.getFirstName(), user.getLastName()));
        holder.deleteButton.setOnClickListener(v -> onItemClickListener.onDeleteClick(user, position));
        holder.facilityDetails.setOnClickListener(v -> onItemClickListener.onFacilityClick(user, position));
    }

    /**
     * Returns the total number of items in the filtered list.
     *
     * @return The size of the filtered list.
     */
    @Override
    public int getItemCount() {
        return filteredList.size();
    }


    /**
     * ViewHolder class for holding references to views in the user profile item layout.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTitle;
        TextView idTitle;
        ImageView deleteButton;
        ImageView facilityDetails;

        /**
         * Constructor for initializing the view references for the item.
         *
         * @param itemView The root view for the user profile item layout.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTitle = itemView.findViewById(R.id.fullNameTitle);
            idTitle = itemView.findViewById(R.id.idTitle);
            facilityDetails = itemView.findViewById(R.id.facilityDetails);
            deleteButton = itemView.findViewById(R.id.deleteIcon);
        }
    }

    /**
     * Updates the list of users in the adapter and refreshes the displayed data.
     * This method replaces the current list with the provided one and clears any previous filtering.
     *
     * @param arrayList The new list of users to be displayed in the RecyclerView.
     */
    public void updateList(List<User> arrayList) {
        this.usersList = new ArrayList<>(arrayList);
        filteredList.clear();
        filteredList.addAll(arrayList);
        notifyDataSetChanged();
    }

    /**
     * Filters the list of users based on a search query and updates the displayed list.
     *
     * @param query The search query to filter the users by.
     */
    public void filter(String query) {
        if (TextUtils.isEmpty(query)) {
            filteredList = new ArrayList<>(usersList); // Reset to full list if query is empty
        } else {
            List<User> filtered = new ArrayList<>();
            query = query.toLowerCase();  // Convert query to lowercase for case-insensitive comparison

            for (User user : usersList) {
                if (user.getFirstName().toLowerCase().contains(query)
                        || user.getLastName().toLowerCase().contains(query)
                        || (user.getFirstName().toLowerCase() + " " + user.getLastName().toLowerCase()).contains(query)) {
                    filtered.add(user);
                }
            }
            filteredList = filtered;
        }
        notifyDataSetChanged();
    }

}
