package com.example.pickme.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.pickme.utils.NotificationList;
import com.example.pickme.utils.UserNotification;
import com.example.pickme.views.adapters.NotifRecAdapter;

public class InboxFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        NotificationList notifications = NotificationList.getInstance();
//        notifications.clear();

//        ArrayList<Notification> notifications = new ArrayList<Notification>();

        NotifRecAdapter notificationAdapter = new NotifRecAdapter(getContext(), notifications);
        RecyclerView recyclerView = view.findViewById(R.id.notifRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(notificationAdapter);

        ItemTouchHelper itemTouchHelper = getItemTouchHelper(recyclerView, notifications, notificationAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

//        ListView listView = view.findViewById(R.id.notificationsList);

//        listView.setAdapter(notificationAdapter);

//        User user = User.getInstance();

//        NotificationRepository notificationRepository = new NotificationRepository();
//        notificationRepository.addSnapshotListener();
//        notificationRepository.attachAdapter(notificationAdapter);
//
//        for(UserNotification userNotification : user.getUserNotifications()){
//            Log.i("NOTIF", "NotifID: " + userNotification.getNotificationID());
//            notificationRepository.getNotificationById(userNotification.getNotificationID(), documentSnapshot -> {
//                Log.i("NOTIF", "DOCID: " + documentSnapshot.getId());
//
//                Notification notification = documentSnapshot.toObject(Notification.class);
//                if(userNotification.isRead())
//                    notification.markRead();
//
//                notifications.add(notification);
//
//                notificationAdapter.notifyDataSetChanged();
//            });
//        }

//        Notification notif1 = new Notification();
//        notif1.setEventID("RFcDhogc3mHnI0uleHnt");
//        notif1.setMessage("Test notif 1");
//
//        Notification notif2 = new Notification();
//        notif2.setEventID("RFcDhogc3mHnI0uleHnt");
//        notif2.setMessage("a super duper duper duper duper duper duper duper duper duper duper long message just to see what happens");
//        notif2.markRead();
//
//        notifications.add(notif1);
//        notifications.add(notif2);



    }

    @NonNull
    private static ItemTouchHelper getItemTouchHelper(RecyclerView recyclerView, NotificationList notifications, NotifRecAdapter notificationAdapter) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // No drag-and-drop support in this example
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition(); // Get the swiped item's position
                if (direction == ItemTouchHelper.LEFT) {
                    Notification notification = notifications.get(position);
                    notifications.remove(notification);

                    User user = User.getInstance();
                    for(UserNotification userNotification : user.getUserNotifications()){
                        if(userNotification.getNotificationID().equals(notification.getNotificationId())){
                            user.getUserNotifications().remove(userNotification);
                            break;
                        }
                    }
//                    Toast.makeText(recyclerView.getContext(), "Swiped Left: " + notifications.get(position), Toast.LENGTH_SHORT).show();

                } else if (direction == ItemTouchHelper.RIGHT) {
                    Notification notification = notifications.get(position);
                    notification.markRead(!notification.isRead());

                    User user = User.getInstance();
                    for(UserNotification userNotification : user.getUserNotifications()){
                        if(userNotification.getNotificationID().equals(notification.getNotificationId())){
                            userNotification.setRead(notification.isRead());
                            break;
                        }
                    }
//                    Toast.makeText(recyclerView.getContext(), "Swiped Right: " + notifications.get(position), Toast.LENGTH_SHORT).show();
                }

                notificationAdapter.notifyDataSetChanged();

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
                    paint.setColor(ContextCompat.getColor(recyclerView.getContext(), R.color.highlight2));
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
                    paint.setColor(ContextCompat.getColor(recyclerView.getContext(), R.color.highlight1));
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