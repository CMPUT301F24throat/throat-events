package com.example.pickme.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.databinding.EventEventdetailsBinding;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
import com.example.pickme.utils.ImageQuery;

public class EventDetailsActivity extends AppCompatActivity {

    private EventEventdetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = EventEventdetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        Event event = (Event) intent.getSerializableExtra("selectedEvent");

        Image image = new Image("1234567890", "123456789");
        image.download(new ImageQuery() {
            @Override
            public void onSuccess(Image image) {
                Glide.with(binding.getRoot()).load(image.getImageUrl()).into(binding.eventFlyer);
            }

            @Override
            public void onEmpty() {

            }
        });


        // Set the data to the UI elements
        if (event != null) {
            binding.title.setText(event.getEventTitle());
            binding.description.setText(event.getEventDescription());
            binding.date.setText(event.getEventDate());
            binding.winners.setText(event.getMaxWinners()+" Winners");
            binding.entrants.setText(event.getEventDate()+" Entrants");
            // Load the poster image using an image loading library (e.g., Glide)
            Image image1 = new Image("123456789", "1234567890");
            image1.download(new ImageQuery() {
                @Override
                public void onSuccess(Image image) {
                    Glide.with(binding.getRoot()).load(image.getImageUrl()).into(binding.eventFlyer);
                }

                @Override
                public void onEmpty() {

                }
            });
        }
    }
}