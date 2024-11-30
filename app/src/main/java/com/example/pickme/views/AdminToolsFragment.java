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

public class AdminToolsFragment extends Fragment {

    private Button adminGalleryButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_tools, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        adminGalleryButton = view.findViewById(R.id.adminGalleryButton);
        adminGalleryButton.setOnClickListener(v -> Navigation
                .findNavController(view)
                .navigate(R.id.action_adminToolsFragment_to_adminGalleryFragment));

    }
}
