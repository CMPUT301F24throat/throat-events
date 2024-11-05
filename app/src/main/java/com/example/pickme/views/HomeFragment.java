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

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button imageTest = view.findViewById(R.id.btn_ImageTesting);
        imageTest.setOnClickListener(view1 -> {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_imageTestFragment);
        });

        Button eventTest = view.findViewById(R.id.btn_EventTesting);
        eventTest.setOnClickListener(view2 -> {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_eventTestFragment);
        });

        Button notifTest = view.findViewById(R.id.notifTest);
        notifTest.setOnClickListener(view3 -> {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_createNotificationFragment);
        });
    }
}