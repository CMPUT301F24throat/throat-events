package com.example.pickme.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme.R;
import com.example.pickme.models.Notification;
import com.example.pickme.models.User;
import com.example.pickme.repositories.NotificationRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CreateNotificationController extends AppCompatActivity {
    private FirebaseFirestore db;

    ImageButton backArrow;
    Button sendButton;
    EditText message;
    Spinner recipientsSpinner;

    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.notif_create_activity);
        db = FirebaseFirestore.getInstance();

        backArrow = findViewById(R.id.back_arrow);
        sendButton = findViewById(R.id.sendButton);
        message = findViewById(R.id.messageEditText);
        recipientsSpinner = findViewById(R.id.dropdown_menu);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        ArrayList<String> recipientOptions = new ArrayList<String>();
        for(Notification.SendLevel level : Notification.SendLevel.values()){
            recipientOptions.add(level.toString());
        }

        ArrayAdapter<String> recipientAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, recipientOptions);

        recipientsSpinner.setAdapter(recipientAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notification notification = new Notification();

                if(!notification.setMessage(message.getText().toString())){
                    message.setError("Message must be between 1 and 300 characters long");
                    return;
                }

//                db.collection("users")
//                    .limit(1)
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if(task.isSuccessful() && !task.getResult().isEmpty()){
//                            user = task.getResult().getDocuments().get(0).toObject(User.class);
//
//                            notification.setSentFrom(user);
//
//                            notification.setLevel(Notification.SendLevel.valueOf((String)recipientsSpinner.getSelectedItem()));
//
//                            notification.setDateTimeNow();
//
//                            NotificationRepository repo = new NotificationRepository();
//                            OnCompleteListener<Object> listener = new OnCompleteListener<Object>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Object> task) {
//                                    Log.i("MYTAG", "Notification added");
//                                }
//                            };
//
//                            repo.addNotification(notification, listener);
//                        }
//                    });

//                notification.setSentFrom(new User());

                notification.setLevel(Notification.SendLevel.valueOf((String)recipientsSpinner.getSelectedItem()));

                notification.setDateTimeNow();

                NotificationRepository repo = new NotificationRepository();
                OnCompleteListener<Object> listener = new OnCompleteListener<Object>() {
                    @Override
                    public void onComplete(@NonNull Task<Object> task) {
                        Log.i("MYTAG", "Notification added");
                    }
                };

                repo.addNotification(notification, listener);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }
}
