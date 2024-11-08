package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pickme.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private static final String EVENT_ID = "CV33jsHcQ3CW2WbxYBDX";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CircleImageView homeProfileButton = getActivity().findViewById(R.id.homeProfileButton);

        Button imageTest = view.findViewById(R.id.btn_ImageTesting);
        imageTest.setOnClickListener(view1 -> {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_imageTestFragment);
            if (homeProfileButton != null) {
                homeProfileButton.setVisibility(View.GONE);
            }
        });

        Button eventTest = view.findViewById(R.id.btn_EventTesting);
        eventTest.setOnClickListener(view2 -> {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_eventTestFragment);
            if (homeProfileButton != null) {
                homeProfileButton.setVisibility(View.GONE);
            }
        });

        // Button to navigate to QR Code View Fragment
        Button qrViewButton = view.findViewById(R.id.btn_qrTesting);
        qrViewButton.setOnClickListener(v -> {
            // Create a bundle to pass the eventID to QRCodeViewFragment
            Bundle args = new Bundle();
            args.putString("eventID", EVENT_ID);

            // Navigate to QRCodeViewFragment, passing the eventID as an argument
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_QRCodeViewFragment, args);
        });
    }
}