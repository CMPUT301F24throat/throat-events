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
import com.example.pickme.models.Enums.EntrantStatus;
import com.example.pickme.models.Event;
import com.example.pickme.models.Notification;
import com.example.pickme.models.User;
import com.example.pickme.models.WaitingListEntrant;
import com.example.pickme.repositories.NotificationRepository;
import com.example.pickme.utils.NotificationHelper;
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
    private Event event;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState){
        return layoutInflater.inflate(R.layout.notif_create_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null)
            this.event = (Event) getArguments().getSerializable("Event");

        if(event == null){
            Log.i("NOTIF", "event passed was null");
            Toast.makeText(getContext(), "Cannot send message right now", Toast.LENGTH_SHORT).show();
            getActivity().getOnBackPressedDispatcher().onBackPressed(); //return to prev screen
        }

        // get all the views
        backArrow = view.findViewById(R.id.back_arrow);
        sendButton = view.findViewById(R.id.sendButton);
        message = view.findViewById(R.id.messageEditText);
        recipientsSpinner = view.findViewById(R.id.dropdown_menu);

        // go back a screen when the back arrow is clicked
        backArrow.setOnClickListener( (v) -> getActivity().getOnBackPressedDispatcher().onBackPressed());

        // populate the spinner with its options
        ArrayList<String> recipientOptions = new ArrayList<>();
        for(EntrantStatus status : EntrantStatus.values()){
            recipientOptions.add(status.toString().charAt(0) + status.toString().substring(1).toLowerCase());
        }

        ArrayAdapter<String> recipientsAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, recipientOptions);

        recipientsSpinner.setAdapter(recipientsAdapter);

        // clicking the send button will first make sure the values entered are valid, then will
        // create the Notification and send it
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notification notification = new Notification();

                if(!notification.setMessage(message.getText().toString())){
                    message.setError("Message must be between 1 and 300 characters long");
                    return;
                }

                notification.setSentFrom(User.getInstance().getDeviceId());

                String selection = (String) recipientsSpinner.getSelectedItem();
                notification.setLevel(EntrantStatus.valueOf(selection.toUpperCase()));

                notification.setDateTimeNow();
                notification.setEventID(event.getEventId());

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

    /**
     * Creates the list of people to send the notification to based on the sendLevel of the notification
     *
     * @param notification the notification to update the list of
     * @param task the task to run after this process is done
     */
    private void createSendList(Notification notification, Runnable task){

        for(WaitingListEntrant entrant : event.getWaitingList()){
            if(notification.getLevel() == EntrantStatus.ALL || notification.getLevel() == entrant.getStatus())
                notification.getSendTo().add(entrant.getEntrantId());
        }

        task.run();
    }
}

/*
 * Sources:
 * ChatGPT: "how do I make a button behave like the android back button"
 */