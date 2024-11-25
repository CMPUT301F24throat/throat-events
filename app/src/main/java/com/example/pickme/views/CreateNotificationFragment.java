package com.example.pickme.views;

import android.os.Bundle;
import android.util.Log;
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
import com.example.pickme.repositories.UserRepository;
import com.example.pickme.utils.NotificationHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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
        return layoutInflater.inflate(R.layout.notif_create_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

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
                notification.setEventID(getArguments().getString("EventID"));

                createSendList(notification, () -> {

                    NotificationRepository repo = NotificationRepository.getInstance();

                    repo.addNotification(notification, task ->{
                            Toast.makeText(
                            view.getContext(),
                            "Notification Sent",
                            Toast.LENGTH_SHORT).show();

                            new NotificationHelper().sendNotification(notification);
                        });

                    getActivity().getOnBackPressedDispatcher().onBackPressed(); //return to prev screen
                });

            }
        });
    }

    private void createSendList(Notification notification, Runnable task){
        UserRepository userRepository = new UserRepository();

        switch(notification.getLevel()){
            case All:
                userRepository.getAllUsers(query -> {
                    List<DocumentSnapshot> docs = query.getResult().getDocuments();
                    for(DocumentSnapshot doc : docs){
                        Log.i("DOC", "DOC ID: " + doc.getId());
                        notification.getSendTo().add(doc.getId());
                    }

                    task.run();
                });
        }
    }
}

/*
 * Sources:
 * ChatGPT: "how do I make a button behave like the android back button"
 */