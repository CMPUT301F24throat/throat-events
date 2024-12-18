package com.example.pickme.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme.R;
import com.example.pickme.models.Notification;
import com.example.pickme.models.User;
import com.example.pickme.repositories.NotificationRepository;
import com.example.pickme.repositories.UserRepository;
import com.example.pickme.utils.NotificationList;
import com.example.pickme.utils.UserNotification;
import com.example.pickme.views.adapters.NotifRecAdapter;

/**
 * Fragment representing the inbox view where notifications are displayed.
 */
public class InboxFragment extends Fragment {
    NotificationRepository notificationRepository = NotificationRepository.getInstance();
    private TextView noNotifsText;

    /**
     * Inflates the fragment's view.
     *
     * @param inflater LayoutInflater to inflate the view.
     * @param container ViewGroup that contains the fragment's UI.
     * @param savedInstanceState Bundle containing the saved state.
     * @return The inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    /**
     * Called when the fragment's view has been created.
     *
     * @param view The fragment's view.
     * @param savedInstanceState Bundle containing the saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Get the singleton instance of NotificationList
        NotificationList notifications = NotificationList.getInstance();
        noNotifsText = view.findViewById(R.id.noNotifsText);

        noNotifsText.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);

        // Set up the RecyclerView with the notification adapter
        NotifRecAdapter notificationAdapter = new NotifRecAdapter(getContext(), notifications);
        RecyclerView recyclerView = view.findViewById(R.id.notifRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(notificationAdapter);
        notificationRepository.attachRecAdapter(notificationAdapter);

        // Attach ItemTouchHelper to handle swipe actions
        ItemTouchHelper itemTouchHelper = getItemTouchHelper(recyclerView, notifications, notificationAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Creates and returns an ItemTouchHelper for handling swipe actions on the RecyclerView.
     *
     * @param recyclerView The RecyclerView to attach the ItemTouchHelper to.
     * @param notifications The list of notifications.
     * @param notificationAdapter The adapter for the notifications.
     * @return The configured ItemTouchHelper.
     */
    @NonNull
    private static ItemTouchHelper getItemTouchHelper(RecyclerView recyclerView, NotificationList notifications, NotifRecAdapter notificationAdapter) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // No drag-and-drop support
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition(); // Get the swiped item's position

                Notification notification = notifications.get(position);
                User user = User.getInstance();
                if (direction == ItemTouchHelper.LEFT) {
                    // Handle swipe to the left (delete notification)
                    notifications.remove(notification);
                    notificationAdapter.notifyItemRemoved(position);

                    for(UserNotification userNotification : user.getUserNotifications()){
                        if(userNotification.getNotificationID().equals(notification.getNotificationId())){
                            user.getUserNotifications().remove(userNotification);
                            break;
                        }
                    }

                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Handle swipe to the right (mark notification as read/unread)
                    notification.markRead(!notification.isRead());
                    notificationAdapter.notifyItemChanged(position);

                    for(UserNotification userNotification : user.getUserNotifications()){
                        if(userNotification.getNotificationID().equals(notification.getNotificationId())){
                            userNotification.setRead(notification.isRead());
                            break;
                        }
                    }
                }

                // Update the user in the repository
                UserRepository.getInstance().updateUser(user, task -> {});
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                // Custom swipe drawing if needed (optional)
                // Get the item's view
                View itemView = viewHolder.itemView;

                // Define colors for swipe directions
                Paint paint = new Paint();
                if (dX > 0) {
                    // Swiping to the right
                    paint.setColor(ContextCompat.getColor(recyclerView.getContext(), R.color.green1));
                    canvas.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), paint);

                    // Draw a custom icon (optional)
                    Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_eye);
                    if (icon != null) {
                        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        int iconLeft = itemView.getLeft() + iconMargin;
                        int iconRight = iconLeft + icon.getIntrinsicWidth();
                        int iconTop = itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(canvas);
                    }
                } else if (dX < 0) {
                    // Swiping to the left
                    paint.setColor(ContextCompat.getColor(recyclerView.getContext(), R.color.red1));
                    canvas.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);

                    // Draw a delete icon (optional)
                    Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_delete);
                    if (icon != null) {
                        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        int iconRight = itemView.getRight() - iconMargin;
                        int iconLeft = iconRight - icon.getIntrinsicWidth();
                        int iconTop = itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(canvas);
                    }
                }
            }
        };

        // Attach the ItemTouchHelper to the RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        return itemTouchHelper;
    }

}

/*
  Coding Sources
  <p>
  ChatGPT:
  - How do I draw something when I swipe a recyclerView
 */