package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

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

        ImageButton homeProfileButton = getActivity().findViewById(R.id.homeProfileButton);

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
    }
}