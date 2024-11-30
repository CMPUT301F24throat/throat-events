package com.example.pickme.views;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Image;
import com.example.pickme.repositories.ImageRepository;

public class AdminGalleryFragment extends Fragment {

    private final ImageRepository ir = ImageRepository.getInstance();
    private GridView gallery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backButton = view.findViewById(R.id.imageCatalogBack);
        backButton.setOnClickListener(v -> Navigation
                .findNavController(view)
                .navigate(R.id.action_adminGalleryFragment_to_adminToolsFragment));

        gallery = view.findViewById(R.id.imageCatalogGridView);

        ir.getAllImages(view.getContext(), gallery);

        gallery.setOnItemClickListener((adapterView, view1, i, l) -> {
            createDeleteDialog(adapterView, view1, i);
        });
    }

    public void createDeleteDialog(AdapterView<?> adapterView, View view, int i) {
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        Image img = (Image) adapterView.getItemAtPosition(i);

        Log.d("TESTING", "createDeleteDialog: " + img.getUploaderId());

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        View dialogView = inflater.inflate(R.layout.dialog_gallery_view, (ViewGroup)view, false);
        builder.setView(dialogView);

        final AlertDialog deleteDialog = builder.create();

        setDialogInfo(dialogView, img);

        ImageView iv = dialogView.findViewById(R.id.dialogGalleryImageView);
        Glide
                .with(view.getContext())
                .load(img.getImageUrl())
                .error(R.drawable.ic_disabled)
                .into(iv);

        Image temp = new Image(img.getUploaderId(), img.getImageAssociation());

        Button dialog_delete = dialogView.findViewById(R.id.dialogGalleryDeleteButton);
        dialog_delete.setOnClickListener(v -> temp.delete(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(
                        this.getContext(),
                        "Image deleted",
                        Toast.LENGTH_SHORT).show();
                ir.getAllImages(view.getContext(), gallery);
            } else {
                Toast.makeText(
                        this.getContext(),
                        "Error deleting image",
                        Toast.LENGTH_SHORT).show();
            }
            deleteDialog.dismiss();
        }));

        Button dialog_cancel = dialogView.findViewById(R.id.dialogGalleryCancelButton);
        dialog_cancel.setOnClickListener(v -> deleteDialog.dismiss());


        deleteDialog.show();
    }

    private void setDialogInfo(View v, Image i) {
        TextView tv_uploaderId = v.findViewById(R.id.dialogGalleryInfo_uploaderId);
        TextView tv_imageAssociation = v.findViewById(R.id.dialogGalleryInfo_imageAssociation);
        TextView tv_imageType = v.findViewById(R.id.dialogGalleryInfo_imageType);
        TextView tv_updatedAt = v.findViewById(R.id.dialogGalleryInfo_updatedAt);
        TextView tv_createdAt = v.findViewById(R.id.dialogGalleryInfo_createdAt);

        tv_uploaderId.append(i.getUploaderId());
        tv_imageAssociation.append(i.getImageAssociation());
        tv_imageType.append(i.getImageType().toString());
        tv_updatedAt.append(i.getUpdatedAt().toDate().toString());
        tv_createdAt.append(i.getCreatedAt().toDate().toString());
    }
}