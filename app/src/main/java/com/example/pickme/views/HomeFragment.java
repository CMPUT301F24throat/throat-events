package com.example.pickme.views;


import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Notification;
import com.example.pickme.models.User;
import com.example.pickme.repositories.NotificationRepository;
import com.example.pickme.utils.NotificationHelper;
import com.example.pickme.utils.NotificationList;
import com.example.pickme.utils.UserNotification;
import com.example.pickme.views.adapters.NotificationAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private ImageButton homeProfileButton;
    private NotificationRepository notificationRepository = new NotificationRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channelID";
            String channelName = "My Channel";
            String channelDescription = "Channel for app notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CircleImageView homeProfileButton = view.findViewById(R.id.homeProfileButton);

        User user = User.getInstance();
        if (user != null) {
            String profilePictureUrl = user.getProfilePictureUrl();
            Glide.with(this)
                    .load(user.getProfilePictureUrl())
                    .into(homeProfileButton);

            homeProfileButton.setVisibility(View.VISIBLE);

            homeProfileButton.setOnClickListener(v -> {
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_userProfileFragment);
            });

            //notification setup
            askNotificationPermission();

            new NotificationHelper().cleanNotifications(() -> {

                NotificationAdapter notificationAdapter = new NotificationAdapter(getContext(), NotificationList.getInstance());

                ListView notifList = view.findViewById(R.id.notifList);
                notifList.setAdapter(notificationAdapter);

                notificationRepository.addSnapshotListener(getContext());
                notificationRepository.attachAdapter(notificationAdapter);

//                NotificationList.getInstance().clear();
                if(NotificationList.getInstance().isEmpty()){
                    for(UserNotification userNotification : user.getUserNotifications()){
                        new NotificationRepository().getNotificationById(userNotification.getNotificationID(), documentSnapshot -> {
                            Notification notification = documentSnapshot.toObject(Notification.class);
                            notification.markRead(userNotification.isRead());

                            NotificationList.getInstance().add(notification);
                            notificationAdapter.notifyDataSetChanged();
                        });
                    }
                }

            });


        } else {
            // Handle the case where the user is null
            homeProfileButton.setVisibility(View.GONE);
        }

    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                User.getInstance().setNotificationEnabled(isGranted);
            });

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                User.getInstance().setNotificationEnabled(true);
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(requireContext())
                        .setMessage("Please enable notifications to receive updates on events")
                        .setTitle("Allow Notifications")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        })
                        .setNegativeButton("No Thanks", (dialog, which) -> {
                            User.getInstance().setNotificationEnabled(false);
                            // No action needed
                        }).show();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}