package com.example.pickme.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Event;
import com.example.pickme.models.Image;
import com.example.pickme.models.User;
import com.example.pickme.repositories.ImageRepository;
import com.example.pickme.repositories.UserRepository;
import com.example.pickme.utils.ImageQuery;
import com.google.firebase.auth.FirebaseAuth;

public class ImageTestFragment extends Fragment {

    //region Attributes

    String TAG = "Example_ImageUpload";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    UserRepository ur = new UserRepository();
    User user1 = new User(ur, auth.getUid());
    Event event1 = new Event();
    //endregion

    //region Activity result contracts

    // these are the gallery picker promises/contracts so we can grab the image uri



    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.image_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityResultLauncher<PickVisualMediaRequest> pickPfp =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // callback is invoked after the user selects a media item or closes the photo picker
                    if (uri != null) {

                        Image image = new Image(user1.getUserId(), user1.getUserId());
                        image.upload(uri);

                        Toast.makeText(
                                view.getContext(),
                                "Profile picture uploaded for user: " + user1.getUserId(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                        Toast.makeText(
                                view.getContext(),
                                "No media selected",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        ActivityResultLauncher<PickVisualMediaRequest> pickEP =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // callback is invoked after the user selects a media item or closes the photo picker
                    if (uri != null) {

                        Image image = new Image(user1.getUserId(), event1.getEventId());
                        image.upload(uri);

                        Toast.makeText(
                                view.getContext(),
                                "Profile picture uploaded for user: " + user1.getUserId(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                        Toast.makeText(
                                view.getContext(),
                                "No media selected",
                                Toast.LENGTH_SHORT).show();
                    }
                });


        event1.setEventId("1234_event_test");

        // uploads
        Button uploadPfpButton = view.findViewById(R.id.uploadPfpButton) ;
        uploadPfpButton.setOnClickListener(view1 -> {
            // launch the picker to complete the promise (limited to single image)
            pickPfp.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        Button uploadEPButton = view.findViewById(R.id.uploadEPButton) ;
        uploadEPButton.setOnClickListener(view1 -> {
            pickEP.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // downloads
        ImageView imageView = view.findViewById(R.id.imageView);

        Button generateButton = view.findViewById(R.id.generatePfpButton);
        generateButton.setOnClickListener(view1 -> {
            Image image = new Image(user1.getUserId(), user1.getUserId());
            image.generate();
            Glide.with(view)
                    .load(image.getImageUrl())
                    .into(imageView);
        });


        Button downloadPfpButton = view.findViewById(R.id.downloadPfpButton);
        downloadPfpButton.setOnClickListener(view1 -> {
            // clears the imageview
            imageView.setImageResource(0);

            // download method requires a callback to access asynchronous query data
            Image image = new Image(user1.getUserId(), user1.getUserId());
            image.download(new ImageQuery() {
                @Override
                public void onSuccess(Image image) {
                    Glide.with(view)
                            .load(image.getImageUrl())
                            .into(imageView);
                }

                @Override
                public void onEmpty() {
                    Toast.makeText(
                            view.getContext(),
                            "Cannot find profile picture for user: " + user1.getUserId(),
                            Toast.LENGTH_SHORT).show();
                }
            });

        });

        Button downloadEPButton = view.findViewById(R.id.downloadEPButton);
        downloadEPButton.setOnClickListener(view1 -> {
            ImageRepository ir = new ImageRepository();

            // clears the imageview
            imageView.setImageResource(0);

            Image image = new Image(user1.getUserId(), event1.getEventId());
            image.download(new ImageQuery() {
                @Override
                public void onSuccess(Image image) {
                    Glide.with(view)
                            .load(image.getImageUrl())
                            .into(imageView);
                }

                @Override
                public void onEmpty() {
                }
            });
        });

        // deletes
        Button deletePfpButton = view.findViewById(R.id.deletePfpButton);
        deletePfpButton.setOnClickListener(view1 -> {
            Image image = new Image(user1.getUserId(), user1.getUserId());
            image.delete();
        });

        Button deleteEPButton = view.findViewById(R.id.deleteEPButton);
        deleteEPButton.setOnClickListener(view1 -> {
            Image image = new Image(user1.getUserId(), event1.getEventId());
            image.delete();
        });

        // get all in gallery
        Button getAllButton = view.findViewById(R.id.getAllButton);
        GridView gridView = view.findViewById(R.id.imageGridView);


        getAllButton.setOnClickListener(view1 -> {
            ImageRepository ir = new ImageRepository();
            ir.getAllImages(view1.getContext() ,gridView);
        });

    }
}