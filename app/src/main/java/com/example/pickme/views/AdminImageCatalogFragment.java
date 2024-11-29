package com.example.pickme.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pickme.R;
import com.example.pickme.repositories.ImageRepository;

public class AdminImageCatalogFragment extends Fragment {

    private ImageView backButton;
    private ImageRepository ir = new ImageRepository();
    private GridView gallery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_image_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton = view.findViewById(R.id.imageCatalogBack);
        backButton.setOnClickListener(v -> Navigation
                .findNavController(view)
                .navigate(R.id.action_adminImageCatalogFragment_to_adminToolsFragment));

        gallery = view.findViewById(R.id.imageCatalogGridView);

        ir.getAllImages(this.getContext(), gallery);

        gallery.setOnItemClickListener((adapterView, v, i, l) -> {
            Log.d("TEST", "onViewCreated: " + adapterView.getItemAtPosition(i)); ;
        });
    }
}