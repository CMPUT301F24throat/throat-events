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

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment handles the create/send screen for notifications
 *
 * @version 1.1
 */
public class CreateNotificationFragment extends Fragment {
    private ImageButton backArrow;
    private Button sendButton;
    private EditText message;
    private Spinner recipientsSpinner;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState){
        return layoutInflater.inflate(R.layout.notif_create_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // get all the views
        backArrow = view.findViewById(R.id.back_arrow);
        sendButton = view.findViewById(R.id.sendButton);
        message = view.findViewById(R.id.messageEditText);
        recipientsSpinner = view.findViewById(R.id.dropdown_menu);

        // go back a screen when the back arrow is clicked
        backArrow.setOnClickListener( (v) -> getActivity().getOnBackPressedDispatcher().onBackPressed());

        // populate the spinner with its options
        ArrayList<String> recipientOptions = new ArrayList<>();
        for(Notification.SendLevel level : Notification.SendLevel.values()){
            recipientOptions.add(level.toString());
        }

        ArrayAdapter<String> recipientsAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, recipientOptions);

        recipientsSpinner.setAdapter(recipientsAdapter);

        // clicking the send button will first make sure the values entered are valid, then will
        // create the Notification and send it
        sendButton.setOnClickListener(view1 -> {
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
                            view1.getContext(),
                            "Notification Sent",
                            Toast.LENGTH_SHORT).show();

                    new NotificationHelper().sendNotification(notification);
                });

                getActivity().getOnBackPressedDispatcher().onBackPressed(); //return to prev screen
            });

        });
    }

    /**
     * Creates the list of people to send the notification to based on the sendLevel of the notification
     *
     * @param notification the notification to update the list of
     * @param task the task to run after this process is done
     */
    private void createSendList(Notification notification, Runnable task){
        switch(notification.getLevel()){
            case All:
                UserRepository.getInstance().getAllUsers((querySnapshot, e) -> {
                    if (e != null) {
                        Log.w("CreateNotificationFragment", "Listen failed.", e);
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                        for(DocumentSnapshot doc : docs){
                            Log.i("DOC", "DOC ID: " + doc.getId());
                            notification.getSendTo().add(doc.getId());
                        }

                        task.run();
                    }
                });

                //TODO: add more cases to send notifs to correct people based on selection
        }
    }
}

/*
 * Sources:
 * ChatGPT: "how do I make a button behave like the android back button"
 */