package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pickme.R;
import com.example.pickme.models.Notification;
import com.example.pickme.models.User;
import com.example.pickme.repositories.NotificationRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This fragment handles the create/send screen for notifications
 *
 * @author Omar-Kattan-1
 * @version 1.1
 */
public class CreateNotificationFragment extends Fragment {

    private FirebaseFirestore db;

    private ImageButton backArrow;
    private Button sendButton;
    private EditText message;
    private Spinner recipientsSpinner;

    private User user;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState){
        return layoutInflater.inflate(R.layout.notif_create_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstaneState){
        super.onViewCreated(view, savedInstaneState);

        backArrow = view.findViewById(R.id.back_arrow);
        sendButton = view.findViewById(R.id.sendButton);
        message = view.findViewById(R.id.messageEditText);
        recipientsSpinner = view.findViewById(R.id.dropdown_menu);

        backArrow.setOnClickListener( (v) -> getActivity().getOnBackPressedDispatcher().onBackPressed());

        ArrayList<String> recipientOptions = new ArrayList<>();
        for(Notification.SendLevel level : Notification.SendLevel.values()){
            recipientOptions.add(level.toString());
        }

        ArrayAdapter<String> recipientsAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, recipientOptions);

        recipientsSpinner.setAdapter(recipientsAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notification notification = new Notification();

                if(!notification.setMessage(message.getText().toString())){
                    message.setError("Message must be between 1 and 300 characters long");
                    return;
                }

                notification.setSentFrom(User.getInstance().getUserId());
                notification.setLevel(Notification.SendLevel.valueOf((String)recipientsSpinner.getSelectedItem()));
                notification.setDateTimeNow();

                NotificationRepository repo = new NotificationRepository();

                repo.addNotification(notification, task ->
                        Toast.makeText(
                        view.getContext(),
                        "Notification Sent",
                        Toast.LENGTH_SHORT).show());

                getActivity().getOnBackPressedDispatcher().onBackPressed(); //return to prev screen
            }
        });
    }
}

/*
 * Sources:
 * ChatGPT: "how do I make a button behave like the android back button"
 */